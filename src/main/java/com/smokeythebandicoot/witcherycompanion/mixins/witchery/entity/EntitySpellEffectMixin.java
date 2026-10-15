package com.smokeythebandicoot.witcherycompanion.mixins.witchery.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntitySpellEffect;
import net.msrandom.witchery.infusion.symbol.SymbolEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Mixins:
 * [Bugfix]: Fix NPE crash when 'ignianima' spell effect is disabled (the one thrown by Lord of Torment) and
 *      the player tries to deflect an EntitySpellEffect contain the aforementioned disabled SpellEffect
 */
@Mixin(EntitySpellEffect.class)
public abstract class EntitySpellEffectMixin extends Entity {

    public EntitySpellEffectMixin(World worldIn) {
        super(worldIn);
    }

    @Redirect(
            method = "attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/msrandom/witchery/infusion/symbol/SymbolEffect;getCanDeflect()Z"
            )
    )
    private boolean safeGetCanDeflect(SymbolEffect effect) {
        return ModConfig.PatchesConfiguration.EntityTweaks.lordOfTorment_fixProjectileDeflect && effect == null || effect.getCanDeflect();
    }
}
