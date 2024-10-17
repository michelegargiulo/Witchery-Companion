package com.smokeythebandicoot.witcherycompanion.mixins.witchery.client.renderer.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.msrandom.witchery.client.renderer.entity.RenderSpellEffect;
import net.msrandom.witchery.infusion.symbol.ProjectileSymbolEffect;
import net.msrandom.witchery.registry.WitcheryIdentityRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Random;

/**
 Mixins:
 [Bugfix] Fixes a NPE caused by trying to retrieve size and color of a spell effect that has been disabled
 [Bugfix] Fixes a ClassCast exception caused by some EntitySpellEffect entities having effects that are instance of
 SymbolEffect (such as LeonardSymbolEffect) but not ProjectileSymbolEffect
 */
@Mixin(RenderSpellEffect.class)
public abstract class RenderSpellEffectMixin {

    /** This mixin checks the class of the effect in question. If it is not a ProjectileSymbolEffect returns null,
     causing the other two mixins to generate a random color and size */
    @WrapOperation(method = "doRender(Lnet/msrandom/witchery/entity/EntitySpellEffect;DDDFF)V", remap = false,
    at = @At(value = "INVOKE", target = "Lnet/msrandom/witchery/registry/WitcheryIdentityRegistry;get(I)Ljava/lang/Object;", remap = false))
    private Object returnNullIfNotProjectileInstance(WitcheryIdentityRegistry<?, ?> instance, int id, Operation<?> original){
        Object result = original.call(instance, id);
        if (!(result instanceof ProjectileSymbolEffect) && ModConfig.PatchesConfiguration.CommonTweaks.renderSpellEffect_fixCrashOnDisabledSpell) {
            return null;
        }
        return result;
    }

    @WrapOperation(method = "doRender(Lnet/msrandom/witchery/entity/EntitySpellEffect;DDDFF)V", remap = false,
            at = @At(value = "INVOKE", remap = false, target = "Lnet/msrandom/witchery/infusion/symbol/ProjectileSymbolEffect;getColor()I"))
    private int genRandomColorForDisabledSpell(ProjectileSymbolEffect instance, Operation<Integer> original) {
        if (ModConfig.PatchesConfiguration.CommonTweaks.renderSpellEffect_fixCrashOnDisabledSpell && instance == null) {
            return (new Random()).nextInt();
        }
        return original.call(instance);
    }

    @WrapOperation(method = "doRender(Lnet/msrandom/witchery/entity/EntitySpellEffect;DDDFF)V", remap = false,
            at = @At(value = "INVOKE", remap = false, target = "Lnet/msrandom/witchery/infusion/symbol/ProjectileSymbolEffect;getSize()F"))
    private float genStandardSizeForDisabledSpell(ProjectileSymbolEffect instance, Operation<Float> original) {
        if (ModConfig.PatchesConfiguration.CommonTweaks.renderSpellEffect_fixCrashOnDisabledSpell && instance == null) {
            return 1.0f;
        }
        return original.call(instance);
    }

}
