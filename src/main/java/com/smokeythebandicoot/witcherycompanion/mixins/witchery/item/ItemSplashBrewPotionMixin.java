package com.smokeythebandicoot.witcherycompanion.mixins.witchery.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.BrewsTweaks;
import net.minecraft.entity.player.EntityPlayer;
import net.msrandom.witchery.extensions.PlayerExtendedData;
import net.msrandom.witchery.item.ItemBrewBottle;
import net.msrandom.witchery.item.ItemSplashBrewPotion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Mixins:
 * [Feature] Implement Throwing Skill
 */
@Mixin(ItemSplashBrewPotion.class)
public abstract class ItemSplashBrewPotionMixin extends ItemBrewBottle {

    @Unique
    private int throwingSkill = -1;

    /** This Mixin intercepts the call for reading Extended Player Data and reads the Throwing Skill to store it **/
    @WrapOperation(method = "onItemRightClick", remap = true, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/msrandom/witchery/util/WitcheryUtils;getExtension(Lnet/minecraft/entity/player/EntityPlayer;)Lnet/msrandom/witchery/extensions/PlayerExtendedData;"))
    private PlayerExtendedData readPlayerThrowingSkill(EntityPlayer instance, Operation<PlayerExtendedData> original) {
        PlayerExtendedData result = original.call(instance);
        this.throwingSkill = result.getThrowingSkill();
        return result;
    }

    /** This Mixin is the one actually responsible for using the increased throwing skill. Applies a linear formula that
     * depends on the skill level, plus a minimum throwing distance **/
    @ModifyConstant(method = "onItemRightClick", remap = true, constant = @Constant(floatValue = 0.75f))
    private float implementThrowingDistance(float constant) {
        if (BrewsTweaks.common_fixThrowingSkill && this.throwingSkill > -1) {
            float throwingForce = 0.75f + (BrewsTweaks.common_tweakThrowingSkillMaxPower - 1) * 0.0075f * throwingSkill;
            this.throwingSkill = -1;
            return throwingForce;
        }
        return 0.75f;
    }

    /** This Mixin adjusts the pitch to be more horizontal as the speed of the projectile increases, to facilitate aim **/
    @ModifyConstant(method = "onItemRightClick", remap = true, constant = @Constant(floatValue = -20.0f))
    private float adjustPitch(float constant) {
        if (BrewsTweaks.common_fixThrowingSkill && this.throwingSkill > -1) {
            return -20.0f + (throwingSkill * 0.2f);
        }
        return -20.0f;
    }

}
