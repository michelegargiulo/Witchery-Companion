package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.api.accessors.kettle.IKettleRecipeAccessor;
import com.smokeythebandicoot.witcherycompanion.api.accessors.kettle.ITileEntityKettleAccessor;
import com.smokeythebandicoot.witcherycompanion.api.KettleApi;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.msrandom.witchery.block.entity.TileEntityKettle;
import net.msrandom.witchery.block.entity.WitcheryTileEntity;
import net.msrandom.witchery.entity.familiar.Familiars;
import net.msrandom.witchery.recipe.KettleRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/**
 Mixins:
 [Tweak] Crafttweaker integration for Kettle (heat sources)
 */
@Mixin(TileEntityKettle.class)
public abstract class TileEntityKettleMixin extends WitcheryTileEntity implements ITileEntityKettleAccessor {

    @Shadow(remap = false)
    private boolean isRuined;

    @Shadow(remap = false)
    private NonNullList<ItemStack> items;

    @Shadow(remap = false)
    private KettleRecipe recipe;

    @Unique
    private float witchery_Patcher$currentPowerNeeded = -1.0f;

    /** This Mixin adds CraftTweaker compat for Kettle heat sources */
    @WrapOperation(method = "update", remap = true, at = @At(value = "INVOKE", remap = true,
            target = "Lnet/minecraft/block/state/IBlockState;getMaterial()Lnet/minecraft/block/material/Material;"))
    private Material injectHeatSources(IBlockState instance, Operation<Material> original) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.kettle_tweakCustomHeatSources) {
            return KettleApi.isHeatSource(instance) ? KettleApi.getFireMaterial() : Material.AIR;
        }
        return original.call(instance);
    }

    /** This Mixin stores the power needed for the current recipe */
    @WrapOperation(method = "update", remap = false, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/msrandom/witchery/recipe/KettleRecipe;getPowerRequired()F"))
    private float saveCurrentPowerNeeded(KettleRecipe instance, Operation<Float> original) {
        witchery_Patcher$currentPowerNeeded = original.call(instance);
        return witchery_Patcher$currentPowerNeeded;
    }

    @Override
    public boolean getIsRuined() {
        return isRuined;
    }

    @Override
    public float getCurrentPowerNeeded() {
        return witchery_Patcher$currentPowerNeeded;
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    public String requiredFamiliar() {
        if (this.recipe != null && this.recipe.getFamiliarPower() != null) {
            return this.recipe.getFamiliarPower().getPath();
        }
        // No familiar required or no recipe
        return null;
    }

    @Override
    public boolean satisfyFamiliar(EntityPlayer player) {
        return recipe != null && (recipe.getFamiliarPower() == null || Familiars.hasFamiliarPower(player, recipe.getFamiliarPower()));
    }

    @Override
    public Integer requiredDimension() {
        if ((Object)this.recipe instanceof IKettleRecipeAccessor) {
            IKettleRecipeAccessor accessor = (IKettleRecipeAccessor) (Object) this.recipe;
            return accessor.getDimension();
        }
        return null;
    }

}
