package com.smokeythebandicoot.witcherycompanion.mixins.witchery.brewing.action.effect;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.msrandom.witchery.brewing.ModifiersEffect;
import net.msrandom.witchery.brewing.action.effect.BlastBrewEffect;
import net.msrandom.witchery.brewing.action.effect.BrewActionEffect;
import net.msrandom.witchery.brewing.action.effect.BrewEffectSerializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 Mixins:
 [Bugfix] Fix Blast Brew affecting terrain even with the Nether Brick modifier. This happens because when the
    Nether Brick is added, the "doApplyToBlock" function is not called, but when the entity is hit, the
    resulting explosion is still set to damage terrain
 */
@Mixin(value = BlastBrewEffect.class)
public abstract class BlastBrewEffectMixin extends BrewActionEffect {

    // Method overrides Injected method in BrewActionEffectMixin
    protected boolean witchery_Patcher$hasForceApplyToBlocks() {
        return true;
    }

    // Method overrides Injected method in BrewActionEffectMixin
    protected boolean witchery_Patcher$hasForceApplyToEntities() {
        return true;
    }

    private BlastBrewEffectMixin(BrewEffectSerializer<?> serializer, boolean invertible) {
        super(serializer, invertible);
    }

    // Since hasForceApplyToEntities is true, this method will always be called, regardless
    // of brew's modifiers. If the terrain has to not be damaged, the check must be done here
    @Inject(method = "doApplyToEntity", remap = false, at = @At("HEAD"), cancellable = true)
    public void WPdoApplyToEntity(World world, EntityLivingBase targetEntity, ModifiersEffect modifiers, ItemStack stack, CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.BrewsTweaks.blast_fixExplosionBreakingBlocks) {
            boolean damageTerrain = !modifiers.disableBlockTarget;
            if (modifiers.powerScalingFactor == 1.0 || world.rand.nextDouble() < modifiers.powerScalingFactor * 0.2) {
                world.createExplosion(modifiers.caster, targetEntity.posX, targetEntity.posY, targetEntity.posZ, (float) modifiers.getStrength(), damageTerrain);
            }
            ci.cancel();
        } else {
            // Do not alter the behaviour of default Witchery: this method should not have been
            // called in the first place if modifiers.disableBlockTarget was true, so be sure to not
            // continue to the original method
            if (modifiers.disableEntityTarget) ci.cancel();
        }
    }

    // Since hasForceApplyToBlocks is true, this method will always be called, regardless
    // of brew's modifiers. If the terrain has to not be damaged, the check must be done here
    @Inject(method = "doApplyToBlock", remap = false, at = @At("HEAD"), cancellable = true)
    public void WPdoApplyToEntity(World world, BlockPos pos, EnumFacing side, int radius, ModifiersEffect modifiers, ItemStack stack, CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.BrewsTweaks.blast_fixExplosionBreakingBlocks) {
            boolean damageTerrain = !modifiers.disableBlockTarget;
            pos = pos.offset(side);
            world.createExplosion(modifiers.caster, (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5, (float) modifiers.getStrength(), damageTerrain);
            ci.cancel();
        } else {
            // Do not alter the behaviour of default Witchery: this method should not have been
            // called in the first place if modifiers.disableBlockTarget was true, so be sure to not
            // continue to the original method
            if (modifiers.disableBlockTarget) ci.cancel();
        }
    }


}
