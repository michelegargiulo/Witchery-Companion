package com.smokeythebandicoot.witcherycompanion.mixins.brewing.action.effect;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.msrandom.witchery.brewing.ModifiersEffect;
import net.msrandom.witchery.brewing.action.effect.BrewActionEffect;
import net.msrandom.witchery.brewing.action.effect.BrewEffectSerializer;
import net.msrandom.witchery.brewing.action.effect.FrogsTongueBrewEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 Mixins:
 [Bugfix] Fixes brew of frogs tongue crashing null entity being passed if player drinks the brew
 */
@Mixin(value = FrogsTongueBrewEffect.class)
public abstract class FrogsTongueBrewEffectMixin extends BrewActionEffect {
    private FrogsTongueBrewEffectMixin(BrewEffectSerializer<?> serializer, boolean invertible) {
        super(serializer, invertible);
    }

    @Inject(method = "doApplyToEntity", at = @At("HEAD"), remap = false, cancellable = true)
    private void WPdoApplyToEntity(World world, EntityLivingBase targetEntity, ModifiersEffect modifiers, ItemStack actionStack, CallbackInfo cbi) {
        if (ModConfig.PatchesConfiguration.BrewsTweaks.frogsTongue_fixPullNullEntity) {
            if (targetEntity == null) {
                cbi.cancel();
            }
        }

    }
}
