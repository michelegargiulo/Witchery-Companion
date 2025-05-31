package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.BlockTweaks;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.msrandom.witchery.block.BlockKettle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 Mixins:
 [Bugfix] Fix using brewed potions immediately upon creation
 [Feature] Customizable bonuses
 */
@Mixin(BlockKettle.class)
public abstract class BlockKettleMixin extends BlockContainer {

    private BlockKettleMixin(Material materialIn) {
        super(materialIn);
    }

    /** This Mixin avoids to return false at the end of the method */
    @Inject(method = "onBlockActivated", remap = true, cancellable = true, at = @At(value = "HEAD"))
    private void callSuperOnBlockActivation(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ, CallbackInfoReturnable<Boolean> cir) {
        if (BlockTweaks.kettle_fixThrowBrewsUponCreation) {
            if (world.isRemote) cir.setReturnValue(true);
        }
    }

    /** Modify the hard-coded values for Kettle bonuses **/
    @ModifyConstant(method = "onBlockActivated", remap = false, constant = @Constant(doubleValue = 0.35, ordinal = 0))
    private double customWitchHatBonus(double constant) {
        return BlockTweaks.KettleTweaks.witchHat_tweakKettleBonus;
    }

    @ModifyConstant(method = "onBlockActivated", remap = false, constant = @Constant(doubleValue = 0.25, ordinal = 0))
    private double customBabaHatBonus(double constant) {
        return BlockTweaks.KettleTweaks.babaYagaHat_tweakKettleBonus;
    }

    @ModifyConstant(method = "onBlockActivated", remap = false, constant = @Constant(doubleValue = 0.25, ordinal = 1))
    private double customBabaHatBonus2(double constant) {
        return BlockTweaks.KettleTweaks.babaYagaHat_tweakKettleBonus2;
    }

    @ModifyConstant(method = "onBlockActivated", remap = false, constant = @Constant(doubleValue = 0.35, ordinal = 1))
    private double customWitchesRobesBonus(double constant) {
        return BlockTweaks.KettleTweaks.witchRobes_tweakKettleBonus;
    }

    @ModifyConstant(method = "onBlockActivated", remap = false, constant = @Constant(doubleValue = 0.35, ordinal = 2))
    private double customNecromancerRobesBonus(double constant) {
        return BlockTweaks.KettleTweaks.necromancerRobes_tweakKettleBonus;
    }

    @ModifyConstant(method = "onBlockActivated", remap = false, constant = @Constant(doubleValue = 0.05, ordinal = 0))
    private double customToadFamiliarBonus(double constant) {
        return BlockTweaks.KettleTweaks.toadFamiliar_tweakKettleBonus;
    }

    @ModifyConstant(method = "onBlockActivated", remap = false, constant = @Constant(doubleValue = 0.05, ordinal = 1))
    private double customToadAndBabaHatFamiliarBonus(double constant) {
        return BlockTweaks.KettleTweaks.toadFamiliarAndBabaHat_tweakKettleBonus;
    }
}
