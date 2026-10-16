package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.BlockTweaks;
import kotlin.jvm.functions.Function0;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.msrandom.witchery.block.BlockSnowbellCrop;
import net.msrandom.witchery.block.BlockWitchCrop;
import net.msrandom.witchery.init.items.WitcheryIngredientItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

/**
 * Mixins:
 * [Bugfix] Fix Snowbell Flower (W:R addition) not dropping from Snowbell crop
 * [Tweak] Configure Snowbell Flower drop rate (above bugfix must be enabled to work)
 */
@Mixin(BlockSnowbellCrop.class)
public abstract class BlockSnowbellCropMixin extends BlockWitchCrop {

    public BlockSnowbellCropMixin(Function0<Item> crop) {
        super(crop);
    }

    @Inject(method = "getDrops", remap = false, cancellable = false, at = @At("TAIL"))
    private void addSnowbellFlowerDrop(NonNullList<ItemStack> ret, IBlockAccess access, BlockPos pos, IBlockState state, int fortune, CallbackInfo ci) {
        if (this.isMaxAge(state)) {
            Random random = access instanceof World ? ((World)access).rand : new Random();
            if (random.nextFloat() < BlockTweaks.snowbellCrop_tweakSnowbellFlowerDropRate) {
                ret.add(new ItemStack(WitcheryIngredientItems.SNOWBELL_FLOWER));
            }
        }
    }
}
