package com.smokeythebandicoot.witcherypatcher.mixins.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.smokeythebandicoot.witcherypatcher.config.ModConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.msrandom.witchery.block.BlockWitchCauldron;
import net.msrandom.witchery.block.entity.TileEntityCauldron;
import net.msrandom.witchery.brewing.action.BrewActionList;
import net.msrandom.witchery.init.WitcheryFluids;
import net.msrandom.witchery.init.WitcheryTileEntities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 Mixins:
 [Bugfix] Fix Bottling level skill not increasing
 */
@Mixin(value = BlockWitchCauldron.class, remap = false)
public abstract class BlockWitchCauldronMixin {


    @Shadow // For all shadow members, body is ignored. Cannot be abstract, since it's static
    private static ItemStack consumeItem(ItemStack stack) {
        return null;
    }

    @Unique
    BrewActionList witchery_Patcher$preservedActions = null;

    // Save Cauldron actions before they get invalidated by the readNBT operation
    @Inject(method = "onBlockActivated", remap = true,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;addStat(Lnet/minecraft/stats/StatBase;)V"))
    public void WPpreserveActionsBeforeEmptyingCauldron(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ, CallbackInfoReturnable<Boolean> cir) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.witchsCauldron_fixBottlingSkillIncrease) {
            TileEntityCauldron cauldron = WitcheryTileEntities.CAULDRON.getAt(world, pos);
            if (cauldron != null) {
                witchery_Patcher$preservedActions = cauldron.getActions();
            }
        }
    }


    // Uses wrap operation to direct the original call with the always-empty brewActionList to a fixed
    // call that uses the preserved brewActionList
    @WrapOperation(method = "onBlockActivated", remap = false,
            at = @At(value = "INVOKE", target = "Lnet/msrandom/witchery/block/BlockWitchCauldron;processSkillChanges(Lnet/minecraft/entity/player/EntityPlayer;Lnet/msrandom/witchery/brewing/action/BrewActionList;)V"))
    public void WPrestorePreservedActions(BlockWitchCauldron instance, EntityPlayer player, BrewActionList actionList, Operation<Void> original) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.witchsCauldron_fixBottlingSkillIncrease
                && witchery_Patcher$preservedActions != null) {
            original.call(instance, player, witchery_Patcher$preservedActions);
        } else {
            original.call(instance, player, actionList);
        }
    }

    /**
     * Injects before the cauldron.drain() call ONLY in the first instance (before the handler.fill() call, that is
     * unique in the method), to cancel the drain() call and return early if the held item is a bucket and the
     * registry name of WitcheryGeneralItems.BREW_BUCKET is null (because the item itself is not yet implemented)
     */
    @Inject(method = "onBlockActivated", remap = false, cancellable = true,
            slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraftforge/fluids/capability/IFluidHandlerItem;fill(Lnet/minecraftforge/fluids/FluidStack;Z)I")),
            at = @At(value = "INVOKE", target = "Lnet/msrandom/witchery/block/entity/TileEntityCauldron;drain(IZ)Lnet/minecraftforge/fluids/FluidStack;"))
    public void WPfixBucketPotionVoiding(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing,
                                         float hitX, float hitY, float hitZ, CallbackInfoReturnable<Boolean> cir,
                                         @Local ItemStack heldStack, @Local FluidStack fluidStackInCauldron) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.witchsCauldron_fixBucketVoidingBrew) {
            if (heldStack.getItem() == Items.BUCKET && fluidStackInCauldron.getFluid() == WitcheryFluids.BREW) {
                cir.setReturnValue(true);
            }
        }
    }

    /*
    @Inject(method = "onBlockActivated", remap = false, cancellable = true,
            slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraftforge/fluids/capability/IFluidHandlerItem;fill(Lnet/minecraftforge/fluids/FluidStack;Z)I")),
            at = @At(value = "INVOKE", target = "Lnet/msrandom/witchery/block/entity/TileEntityCauldron;writeFluid(Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/nbt/NBTTagCompound;"))
    public void WPimplementBucketBrew(
            World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
            EnumFacing facing, float hitX, float hitY, float hitZ, CallbackInfoReturnable<Boolean> cir,
            @Local TileEntityCauldron cauldron, @Local ItemStack heldStack,
            @Local IFluidHandlerItem handler, @Local FluidStack fluidStackInCauldron) {


        if (ModConfig.PatchesConfiguration.BlockTweaks.witchsCauldron_fixBucketVoidingBrew) {
            NBTTagCompound nbtFluid = cauldron.writeFluid(new NBTTagCompound());
            FluidStack drain = cauldron.drain(1000, !player.capabilities.isCreativeMode);
            if (!player.capabilities.isCreativeMode) {
                handler.fill(drain, true);
                if (heldStack.getItem() == Items.BUCKET) {
                    ItemStack filledBucketStack;

                    filledBucketStack = FluidUtil.getFilledBucket(fluidStackInCauldron);
                    player.addStat(StatList.CAULDRON_USED);
                    if (heldStack.getCount() > 1) {
                        if (!player.inventory.addItemStackToInventory(filledBucketStack)) {
                            cir.setReturnValue(false);
                        }

                        player.setHeldItem(hand, consumeItem(heldStack));
                    } else {
                        player.setHeldItem(hand, filledBucketStack);
                    }

                } else {
                    heldStack.setTagCompound(nbtFluid.copy());
                }
            }

            world.playSound(null, player.getPosition(), SoundEvents.ENTITY_PLAYER_SWIM, SoundCategory.BLOCKS, 0.5F, 0.4F / (world.rand.nextFloat() * 0.4F + 0.8F));
            cauldron.notifyBlockUpdate(true);
        }
    }

    @WrapOperation(method = "onBlockActivated",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraftforge/fluids/capability/IFluidHandlerItem;fill(Lnet/minecraftforge/fluids/FluidStack;Z)I"),
                    to = @At("TAIL")),
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fluids/FluidStack;getFluid()Lnet/minecraftforge/fluids/Fluid;"))
    public Fluid WP_simulateNotBucket(FluidStack instance, Operation<Fluid> original) {
        Fluid originalFluid = original.call(instance);
        if (originalFluid == WitcheryFluids.BREW) {
            return WitcheryFluids.BREW_LIQUID;
        }
        return originalFluid;
    }
     */
}

