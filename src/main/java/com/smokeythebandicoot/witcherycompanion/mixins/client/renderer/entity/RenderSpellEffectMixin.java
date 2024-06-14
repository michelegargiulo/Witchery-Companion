package com.smokeythebandicoot.witcherycompanion.mixins.client.renderer.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.msrandom.witchery.client.renderer.entity.RenderSpellEffect;
import net.msrandom.witchery.infusion.symbol.ProjectileSymbolEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;

/**
 Mixins:
 [Bugfix] Fixes a NPE caused by trying to retrieve size and color of a spell effect that has been disabled
 */
@Mixin(RenderSpellEffect.class)
public class RenderSpellEffectMixin {

    @WrapOperation(method = "doRender(Lnet/msrandom/witchery/entity/EntitySpellEffect;DDDFF)V", remap = false,
            at = @At(value = "INVOKE", remap = false, target = "Lnet/msrandom/witchery/infusion/symbol/ProjectileSymbolEffect;getColor()I"))
    public int genRandomColorForDisabledSpell(ProjectileSymbolEffect instance, Operation<Integer> original) {
        if (ModConfig.PatchesConfiguration.CommonTweaks.renderSpellEffect_fixCrashOnDisabledSpell && instance == null) {
            return (new Random()).nextInt();
        }
        return original.call(instance);
    }

    @WrapOperation(method = "doRender(Lnet/msrandom/witchery/entity/EntitySpellEffect;DDDFF)V", remap = false,
            at = @At(value = "INVOKE", remap = false, target = "Lnet/msrandom/witchery/infusion/symbol/ProjectileSymbolEffect;getSize()F"))
    public float genStandardSizeForDisabledSpell(ProjectileSymbolEffect instance, Operation<Float> original) {
        if (ModConfig.PatchesConfiguration.CommonTweaks.renderSpellEffect_fixCrashOnDisabledSpell && instance == null) {
            return 1.0f;
        }
        return original.call(instance);
    }

}
