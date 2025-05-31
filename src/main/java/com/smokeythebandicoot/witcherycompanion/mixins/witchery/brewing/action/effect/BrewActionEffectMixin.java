package com.smokeythebandicoot.witcherycompanion.mixins.witchery.brewing.action.effect;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.msrandom.witchery.brewing.ModifiersEffect;
import net.msrandom.witchery.brewing.action.effect.BrewActionEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 Mixins:
 [Tweak] Allows each brew to fine-tune its application behaviour, preserving existing behaviour
 */
@Mixin(value = BrewActionEffect.class, remap = false)
public abstract class BrewActionEffectMixin {

    @Shadow
    protected abstract void doApplyToBlock(World world, BlockPos pos, EnumFacing side, int radius, ModifiersEffect modifiers, ItemStack stack);

    @Shadow
    protected abstract void doApplyToEntity(World world, EntityLivingBase targetEntity, ModifiersEffect modifiers, ItemStack stack);

    // Those methods are injected and called against in the forced check
    // Include and Override in Mixins of child classes to alter their values
    protected boolean witchery_Patcher$hasForceApplyToBlocks() {
        return false;
    }

    // Those methods are injected and called against in the forced check
    // Include and Override in Mixins of child classes to alter their values
    protected boolean witchery_Patcher$hasForceApplyToEntities() {
        return false;
    }

    @Inject(method = "applyToBlock", remap = false, at = @At("HEAD"), cancellable = true)
    public void WPflexibleApplyToBlocks(World world, BlockPos pos, EnumFacing side, int radius, ModifiersEffect modifiers, ItemStack stack, CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.BrewsTweaks.common_tweakBrewApplications) {
            if (!modifiers.disableBlockTarget || witchery_Patcher$hasForceApplyToBlocks()) {
                this.doApplyToBlock(world, pos, side, radius, modifiers, stack);
                modifiers.reset();
                ci.cancel();
            }
        }
    }

    @Inject(method = "applyToEntity", remap = false, at = @At("HEAD"), cancellable = true)
    public void WPflexibleApplyToEntities(World world, EntityLivingBase targetEntity, ModifiersEffect modifiers, ItemStack stack, CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.BrewsTweaks.common_tweakBrewApplications) {
            if (!modifiers.disableEntityTarget || witchery_Patcher$hasForceApplyToEntities()) {
                this.doApplyToEntity(world, targetEntity, modifiers, stack);
                modifiers.reset();
                ci.cancel();
            }
        }
    }
}
