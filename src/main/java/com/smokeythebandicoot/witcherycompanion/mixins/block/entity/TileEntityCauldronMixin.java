package com.smokeythebandicoot.witcherycompanion.mixins.block.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.api.cauldron.ITileEntityCauldronAccessor;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.BlockTweaks;
import com.smokeythebandicoot.witcherycompanion.api.cauldron.CauldronApi;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.msrandom.witchery.block.entity.TileEntityCauldron;
import net.msrandom.witchery.brewing.CauldronBrewData;
import net.msrandom.witchery.init.WitcheryFluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.atomic.AtomicInteger;

/**
 Mixins:
 [Integration] Crafttweaker integration for Cauldron (heat sources)
 [Bugfix] Fix exploit when right-clicking an empty bucket on an empty cauldron, producing a water bucket
 */
@Mixin(value = TileEntityCauldron.class)
public abstract class TileEntityCauldronMixin implements ITileEntityCauldronAccessor {

    @Shadow(remap = false)
    private CauldronBrewData brewData;

    @Shadow(remap = false)
    public abstract int getLiquidQuantity();

    @Shadow(remap = false)
    protected abstract float getNeededPower(int currentPower, AtomicInteger risk);

    @Unique
    private float witchery_Patcher$currentPowerNeeded = -1.0f;

    /** Since Witchery has hardcoded check to if the block below is FIRE, we will use this
     * as the "true" return value for the method. Anything else is considered "false" */
    @WrapOperation(method = "update", remap = true,
        at = @At(value = "INVOKE", target = "Lnet/minecraft/block/state/IBlockState;getBlock()Lnet/minecraft/block/Block;", remap = true))
    public Block WPallowOtherHeatSources(IBlockState instance, Operation<Block> original) {
        if (BlockTweaks.witchsCauldron_tweakCustomHeatSources) {
            return CauldronApi.isHeatSource(instance) ? CauldronApi.getFireBlock() : Blocks.AIR;
        }
        return original.call(instance);
    }

    /** This Mixin fixes an exploit that allows players to get unlimited water buckets by right-clicking the cauldron
     with an empty bucket, due to the TankProperties always reporting the *potential* fluid capacity instead of the
     *actual* fluid inside. The Witchery.BREW always reports max capacity because the caludron should never accept more
     brew, because other fluids might have different NBTs */
    @Inject(method = "getTankProperties", remap = false, cancellable = true, at = @At("HEAD"))
    public void fixUnlimitedWaterExploit(CallbackInfoReturnable<IFluidTankProperties[]> cir) {
        if (BlockTweaks.witchsCauldron_fixUnlimitedWaterWhenEmpty) {
            FluidStack fluidStack = null;
            if (this.brewData.getActions().size() > 0) {
                fluidStack = new FluidStack(WitcheryFluids.BREW, 3000);
            } else if (this.getLiquidQuantity() > 0) {
                fluidStack = new FluidStack(FluidRegistry.WATER, this.getLiquidQuantity());
            }
            cir.setReturnValue(new IFluidTankProperties[]{new FluidTankProperties(fluidStack, 3000)});
        }
    }

    @WrapOperation(method = "update", remap = true, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/msrandom/witchery/block/entity/TileEntityCauldron;getNeededPower(ILjava/util/concurrent/atomic/AtomicInteger;)F"))
    private float saveCurrentPowerNeeded(TileEntityCauldron instance, int currentPower, AtomicInteger risk, Operation<Float> original) {
        witchery_Patcher$currentPowerNeeded = original.call(instance, currentPower, risk);
        return witchery_Patcher$currentPowerNeeded;
    }

    /** Accessor for 'getNeededPower'. Used by The One Probe */
    @Override
    public float accessor_getNeededPower() {
        return witchery_Patcher$currentPowerNeeded;
    }

}
