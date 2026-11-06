package com.smokeythebandicoot.witcherycompanion.mixins.witchery.transformation;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.api.VampireBloodApi;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.TransformationTweaks;
import com.smokeythebandicoot.witcherycompanion.utils.blood.BloodProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.util.DamageSource;
import net.msrandom.witchery.transformation.VampireCreatureTrait;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixins:
 * [Tweak] Vampires drink blood from the creatures decided by VampireBloodApi (config and CraftTweaker), instead of
 * from any EntityAnimal. With default settings the behaviour is the same as W:R
 */
@Mixin(VampireCreatureTrait.class)
public abstract class VampireCreatureTraitMixin {

    @Shadow(remap = false)
    public abstract int getBloodPower();

    @Shadow(remap = false)
    public abstract void setBloodPower(int bloodPower);

    @Shadow(remap = false)
    public abstract int getMaxBloodPower();

    /** Profile of the creature that is currently being drunk from. Only set during triggerSelectedPower, and only
     * if the creature is a blood source because of the API. Always reset at the start and at the end **/
    @Unique
    private BloodProfile witcherycompanion$drainProfile;


    @Inject(method = "triggerSelectedPower", remap = false, at = @At("HEAD"))
    private void witcherycompanion$resetProfileAtHead(Entity target, CallbackInfo ci) {
        this.witcherycompanion$drainProfile = null;
    }

    @Inject(method = "triggerSelectedPower", remap = false, at = @At("RETURN"))
    private void witcherycompanion$resetProfileAtReturn(Entity target, CallbackInfo ci) {
        this.witcherycompanion$drainProfile = null;
    }

    /** W:R checks 'target instanceof EntityAnimal' to know if it is a creature that a vampire can drink from, after
     * having excluded Werewolves, Village Guards, Villagers and Players. This replaces that check with the API.
     * The handler receives the checked object and the class, and its return value replaces the instanceof result **/
    @ModifyConstant(method = "triggerSelectedPower", remap = false,
            constant = @Constant(classValue = EntityAnimal.class, ordinal = 0))
    private boolean witcherycompanion$isBloodSource(Object target, Class<?> clazz) {
        if (!TransformationTweaks.vampire_tweakCustomBloodSources) {
            return clazz.isInstance(target);
        }
        this.witcherycompanion$drainProfile = VampireBloodApi.getProfile((Entity) target);
        return this.witcherycompanion$drainProfile != null;
    }

    /** The damage dealt to the creature by each drink (1.0 in W:R) is taken from the profile. A zero damage means the
     * creature is not hurt at all **/
    @WrapOperation(method = "triggerSelectedPower", remap = false, at = @At(value = "INVOKE", remap = true, ordinal = 0,
            target = "Lnet/minecraft/entity/Entity;attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z"))
    private boolean witcherycompanion$applyProfileDamage(Entity instance, DamageSource source, float amount,
                                                         Operation<Boolean> original) {
        BloodProfile profile = this.witcherycompanion$drainProfile;
        if (profile == null) {
            return original.call(instance, source, amount);
        }
        if (profile.damage <= 0.0f) {
            return false;
        }
        return original.call(instance, source, profile.damage);
    }

    /** The blood power restored by each drink, and its cap, are taken from the profile. Same algorithm as W:R **/
    @WrapOperation(method = "triggerSelectedPower", remap = false, at = @At(value = "INVOKE", remap = false, ordinal = 0,
            target = "Lnet/msrandom/witchery/transformation/VampireCreatureTrait;drinkAnimalBlood(I)V"))
    private void witcherycompanion$applyProfileBlood(VampireCreatureTrait instance, int cap, Operation<Void> original) {
        BloodProfile profile = this.witcherycompanion$drainProfile;
        if (profile == null) {
            original.call(instance, cap);
            return;
        }
        this.setBloodPower(profile.drink(this.getBloodPower(), this.getMaxBloodPower()));
    }

}
