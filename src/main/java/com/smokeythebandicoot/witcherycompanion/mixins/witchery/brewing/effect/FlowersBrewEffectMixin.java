package com.smokeythebandicoot.witcherycompanion.mixins.witchery.brewing.effect;

import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.api.FlowersBrewApi;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.msrandom.witchery.brewing.ModifiersEffect;
import net.msrandom.witchery.brewing.action.effect.BrewActionBlockCircle;
import net.msrandom.witchery.brewing.action.effect.BrewEffectSerializer;
import net.msrandom.witchery.brewing.action.effect.FlowersBrewEffect;
import net.msrandom.witchery.util.BlockUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Mixins:
 * [Tweak] FlowerBrewApi and Crafttweaker integration for custom flower spawning (block and biome)
 */
@Mixin(FlowersBrewEffect.class)
public abstract class FlowersBrewEffectMixin extends BrewActionBlockCircle {

    private FlowersBrewEffectMixin(BrewEffectSerializer<?> serializer, boolean invertible) {
        super(serializer, invertible);
    }

    /** Replaces the function that spawns the flowers with a Custom one, that has custom placement checks for each flower
     * and asks to FlowerBrewApi for the flower to spawn **/
    @Inject(method = "onCircleBlock", remap = false, cancellable = true, at = @At("HEAD"))
    private void injectFlowersBrewApi(World world, BlockPos p, ModifiersEffect modifiers, AtomicInteger counter, CallbackInfo ci) {

        if (ModConfig.PatchesConfiguration.BrewsTweaks.flowers_tweakEnableCrafttweakerCompat) {

            IBlockState[] flowers = FlowersBrewApi.getValidFlowers(world.getBiome(p));
            if (flowers == null || flowers.length == 0) {
                ci.cancel();
                return;
            }

            for (BlockPos pos : BlockPos.getAllInBox(p.down(1), p.up(1))) {
                IBlockState flower = flowers[world.rand.nextInt(flowers.length)];
                if (flower != null && witcherycompanion$canPlaceAt(flower, world, pos, modifiers)) {
                    world.setBlockState(pos, flower);
                }
            }

            ci.cancel();
        }
    }

    /** Internal function for checking if a block can spawn. Prevents invalid blocks from being spawned **/
    @Unique
    private boolean witcherycompanion$canPlaceAt(IBlockState flower, World world, BlockPos pos, ModifiersEffect modifiers) {
        return BlockUtil.isReplaceableBlock(world, pos, modifiers.caster) &&
                !world.getBlockState(pos).getMaterial().isLiquid() &&
                flower.getBlock().canPlaceBlockAt(world, pos) &&
                world.rand.nextInt(8 - modifiers.getStrength()) == 0;
    }

}
