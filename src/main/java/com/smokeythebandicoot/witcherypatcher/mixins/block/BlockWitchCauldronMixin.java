package com.smokeythebandicoot.witcherypatcher.mixins.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.smokeythebandicoot.witcherypatcher.config.ModConfig;
import com.smokeythebandicoot.witcherypatcher.utils.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.msrandom.witchery.block.BlockWitchCauldron;
import net.msrandom.witchery.block.entity.TileEntityCauldron;
import net.msrandom.witchery.brewing.action.BrewActionList;
import net.msrandom.witchery.init.WitcheryFluids;
import net.msrandom.witchery.init.WitcheryTileEntities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 Mixins:
 [Bugfix] Fix Bottling level skill not increasing
 [Bugfix] Fix Right-click with Empty Bucket on cauldron filled with brew, just voiding the brew
 */
@Mixin(value = BlockWitchCauldron.class)
public abstract class BlockWitchCauldronMixin {

    @Unique
    BrewActionList witchery_Patcher$preservedActions = null;

    // Save Cauldron actions before they get invalidated by the readNBT operation
    /*@Inject(method = "onBlockActivated", remap = false,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;addStat(Lnet/minecraft/stats/StatBase;)V", remap = false))
    public void WPpreserveActionsBeforeEmptyingCauldron(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ, CallbackInfoReturnable<Boolean> cir) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.witchsCauldron_fixBottlingSkillIncrease) {
            TileEntityCauldron cauldron = WitcheryTileEntities.CAULDRON.getAt(world, pos);
            if (cauldron != null) {
                witchery_Patcher$preservedActions = cauldron.getActions();
            }
        }
    }*/


    // Uses wrap operation to direct the original call with the always-empty brewActionList to a fixed
    // call that uses the preserved brewActionList
    /*@WrapOperation(method = "onBlockActivated", remap = true,
            at = @At(value = "INVOKE", target = "Lnet/msrandom/witchery/block/BlockWitchCauldron;processSkillChanges(Lnet/minecraft/entity/player/EntityPlayer;Lnet/msrandom/witchery/brewing/action/BrewActionList;)V", remap = false))
    public void WPrestorePreservedActions(BlockWitchCauldron instance, EntityPlayer player, BrewActionList actionList, Operation<Void> original) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.witchsCauldron_fixBottlingSkillIncrease
                && witchery_Patcher$preservedActions != null) {
            original.call(instance, player, witchery_Patcher$preservedActions);
        } else {
            original.call(instance, player, actionList);
        }
    }*/

    /**
     * Injects before the cauldron.drain() call ONLY in the first instance (before the handler.fill() call, that is
     * unique in the method), to cancel the drain() call and return early if the held item is a bucket and the
     * registry name of WitcheryGeneralItems.BREW_BUCKET is null (because the item itself is not yet implemented).
     * @Local are used to capture local variables in method (heldStack and cauldron)
     * @Slide is used to only inject in the first cauldron.drain() call, that is from @At("HEAD") to @At("INVOKE", handler.fill())
     */
    @Inject(method = "onBlockActivated", cancellable = true,
            slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraftforge/fluids/capability/IFluidHandlerItem;fill(Lnet/minecraftforge/fluids/FluidStack;Z)I", remap = false)),
            at = @At(value = "INVOKE", target = "Lnet/msrandom/witchery/block/entity/TileEntityCauldron;drain(IZ)Lnet/minecraftforge/fluids/FluidStack;", remap = false))
    public void WPfixBucketPotionVoiding(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing,
                                         float hitX, float hitY, float hitZ, CallbackInfoReturnable<Boolean> cir
                                         //, @Local(ordinal = 0) TileEntityCauldron cauldron, @Local(ordinal = 0) ItemStack heldStack
                                         ) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.witchsCauldron_fixBucketVoidingBrew) {

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

}

