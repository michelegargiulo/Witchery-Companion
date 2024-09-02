package com.smokeythebandicoot.witcherycompanion.mixins.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.api.progress.IWitcheryProgress;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.BlockTweaks;
import com.smokeythebandicoot.witcherycompanion.network.ProgressSync;
import com.smokeythebandicoot.witcherycompanion.api.progress.ProgressUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.msrandom.witchery.block.BlockWitchCauldron;
import net.msrandom.witchery.block.entity.TileEntityCauldron;
import net.msrandom.witchery.brewing.action.BrewAction;
import net.msrandom.witchery.brewing.action.BrewActionList;
import net.msrandom.witchery.init.WitcheryFluids;
import net.msrandom.witchery.init.WitcheryTileEntities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.smokeythebandicoot.witcherycompanion.api.progress.CapabilityWitcheryProgress.WITCHERY_PROGRESS_CAPABILITY;


/**
 Mixins:
 [Bugfix] Fix Bottling level skill not increasing
 [Bugfix] Fix Right-click with Empty Bucket on cauldron filled with brew, just voiding the brew
 [Bugfix] Fix dupe when tossing an item that gets inserted into multiple adjacent cauldrons
 [Integration] Bugfix to ignore forge fluid capability handlers, as Witchery doesn't really interact well with them, voiding the brew
 [Tweak] Tweak to reduce bottle size to 250 (is only applied to water handling, as brews have custom amounts)
 */
@Mixin(value = BlockWitchCauldron.class)
public abstract class BlockWitchCauldronMixin {

    @Unique
    BrewActionList witchery_Patcher$preservedActions = null;

    /** Save Cauldron actions before they get invalidated by the readNBT operation */
    @Inject(method = "onBlockActivated", remap = true,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;addStat(Lnet/minecraft/stats/StatBase;)V", remap = true))
    public void WPpreserveActionsBeforeEmptyingCauldron(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ, CallbackInfoReturnable<Boolean> cir) {
        if (BlockTweaks.witchsCauldron_fixBottlingSkillIncrease) {
            TileEntityCauldron cauldron = WitcheryTileEntities.CAULDRON.getAt(world, pos);
            if (cauldron != null) {
                witchery_Patcher$preservedActions = cauldron.getActions();
            }
        }
    }


    /** Uses wrap operation to direct the original call with the always-empty brewActionList to a fixed
     call that uses the preserved brewActionList */
    @WrapOperation(method = "onBlockActivated", remap = true, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/msrandom/witchery/block/BlockWitchCauldron;processSkillChanges(Lnet/minecraft/entity/player/EntityPlayer;Lnet/msrandom/witchery/brewing/action/BrewActionList;)V"))
    public void WPrestorePreservedActions(BlockWitchCauldron instance, EntityPlayer player, BrewActionList actionList, Operation<Void> original) {
        if (BlockTweaks.witchsCauldron_fixBottlingSkillIncrease
                && witchery_Patcher$preservedActions != null) {
            original.call(instance, player, witchery_Patcher$preservedActions);
        } else {
            original.call(instance, player, actionList);
        }
    }

    /**
     * Injects before the cauldron.drain() call ONLY in the first instance (before the handler.fill() call, that is
     * unique in the method), to cancel the drain() call and return early if the held item is a bucket and the
     * registry name of WitcheryGeneralItems.BREW_BUCKET is null (because the item itself is not yet implemented).
     * @Slice is used to only inject in the first cauldron.drain() call, that is from @At("HEAD") to @At("INVOKE", handler.fill())
     */
    @Inject(method = "onBlockActivated", cancellable = true,
            slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraftforge/fluids/capability/IFluidHandlerItem;fill(Lnet/minecraftforge/fluids/FluidStack;Z)I", remap = false)),
            at = @At(value = "INVOKE", target = "Lnet/msrandom/witchery/block/entity/TileEntityCauldron;drain(IZ)Lnet/minecraftforge/fluids/FluidStack;", remap = false))
    public void WPfixBucketPotionVoiding(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing,
                                         float hitX, float hitY, float hitZ, CallbackInfoReturnable<Boolean> cir) {
        if (BlockTweaks.witchsCauldron_fixBucketVoidingBrew) {

            TileEntityCauldron cauldron = WitcheryTileEntities.CAULDRON.getAt(world, pos);
            ItemStack heldStack = player.getHeldItem(hand);

            // This is a local, but there are too many of this type to capture. More readable and maintainable
            // to capture the TileEntityCauldron local and derive the contained fluid stack within
            FluidStack fluidStackInCauldron = cauldron.getTankProperties()[0].getContents();

            // If fluidStack in cauldron != null (Cauldron is filled with something
            // And held item is a Bucket
            // And the fluid in cauldron is a brew, then cancel the action returning early
            if (heldStack.getItem() == Items.BUCKET &&
                    fluidStackInCauldron != null &&
                    fluidStackInCauldron.getFluid() == WitcheryFluids.BREW) {

                cir.setReturnValue(true);
            }
        }
    }

    /** Returns early if the entity is dead, meaning one cauldron already has setDead() the entity, so that others do
     not accept the item, despite still technically existing (dead items are removed the next tick) */
    @Inject(method = "onEntityCollision", remap = true, at = @At("HEAD"), cancellable = true)
    public void fixItemMultipleCauldrons(World world, BlockPos pos, IBlockState state, Entity entity, CallbackInfo ci) {
        if (!BlockTweaks.witchsCauldron_fixMultipleCauldronDupe) return;
        if (entity.isDead) {
            ci.cancel();
        }
    }

    /** This Mixin makes so that all Forge-registered items that have the capability of containing fluids are ignored by
     the cauldron, Witchery Brew fluid cannot be successfully contained by them */
    @WrapOperation(method = "onBlockActivated", remap = true, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/minecraftforge/fluids/FluidUtil;getFluidHandler(Lnet/minecraft/item/ItemStack;)Lnet/minecraftforge/fluids/capability/IFluidHandlerItem;"))
    public IFluidHandlerItem ignoreFluidHandlers(ItemStack itemStack, Operation<IFluidHandlerItem> original) {
        if (BlockTweaks.witchsCauldron_tweakIgnoreFluidHandlers && itemStack.getItem() != Items.BUCKET)
            return null;
        else return original.call(itemStack);
    }

    @Inject(method = "onBlockActivated", remap = true, at = @At(value = "INVOKE", remap = false, ordinal = 1, shift = At.Shift.BEFORE,
            target = "Lnet/msrandom/witchery/block/entity/TileEntityCauldron;drain(IZ)Lnet/minecraftforge/fluids/FluidStack;"))
    private void unlockSecrets(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ, CallbackInfoReturnable<Boolean> cir) {
        TileEntityCauldron cauldron = WitcheryTileEntities.CAULDRON.getAt(world, pos);
        if (cauldron == null) return; // Should not happen
        BrewActionList actionList = cauldron.getActions();
        for (BrewAction action : actionList.actions) {
            if (action.getHidden()) {
                ItemStack stack = action.getKey().toStack();
                IWitcheryProgress progress = player.getCapability(WITCHERY_PROGRESS_CAPABILITY, null);
                if (progress != null) {
                    progress.unlockProgress(ProgressUtils.getBrewActionSecret(stack));
                    ProgressSync.serverRequest(player);
                }
            }
        }
    }


    @ModifyConstant(method = "onBlockActivated", remap = true, constant = @Constant(intValue = 334))
    public int smallerWaterBottleOnActivatedFill(int constant) {
        return BlockTweaks.witchsCauldron_tweakSmallerBottle ? 250 : constant;
    }

    @ModifyConstant(method = "onBlockActivated", remap = true, constant = @Constant(intValue = 333))
    public int smallerWaterBottleOnActivatedDrain(int constant) {
        return BlockTweaks.witchsCauldron_tweakSmallerBottle ? 250 : constant;
    }

    @ModifyConstant(method = "fillBottleFromCauldron", remap = false, constant = @Constant(intValue = 333))
    public int smallerWaterBottleOnFillBottle(int constant) {
        return BlockTweaks.witchsCauldron_tweakSmallerBottle ? 250 : constant;
    }

}

