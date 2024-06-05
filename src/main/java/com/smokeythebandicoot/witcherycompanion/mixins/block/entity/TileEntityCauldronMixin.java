package com.smokeythebandicoot.witcherycompanion.mixins.block.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.integrations.api.CauldronApi;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.msrandom.witchery.block.entity.TileEntityCauldron;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 Mixins:
 [Integration] Crafttweaker integration to add new Heat Sources to the Witch's Cauldron
 */
@Mixin(value = TileEntityCauldron.class)
public abstract class TileEntityCauldronMixin {


    /** Since Witchery has hardcoded check to if the block below is FIRE, we will use this
     * as the "true" return value for the method. Anything else is considered "false"
     */
    @WrapOperation(method = "update", remap = true,
        at = @At(value = "INVOKE", target = "Lnet/minecraft/block/state/IBlockState;getBlock()Lnet/minecraft/block/Block;", remap = true))
    public Block WPallowOtherHeatSources(IBlockState instance, Operation<Block> original) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.witchsCauldron_tweakCustomHeatSources) {
            return CauldronApi.isHeatSource(instance) ? CauldronApi.getFireBlock() : Blocks.AIR;
        }
        return original.call(instance);
    }

}
