package com.smokeythebandicoot.witcherycompanion.mixins.witchery.client.model;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.EntityTweaks;
import net.minecraft.client.model.ModelPig;
import net.minecraft.entity.EntityLivingBase;
import net.msrandom.witchery.client.model.ModelFamiliarPig;
import net.msrandom.witchery.entity.EntitySpectralFamiliar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


/**
 * Mixins:
 * [Bugfix] Fix incorrect model after un-sitting
 */
@Mixin(ModelFamiliarPig.class)
public abstract class ModelFamiliarPigMixin extends ModelPig {

    /** Report correct values (from 1.7.10 Witchery) **/
    @Inject(method = "setLivingAnimations", remap = false, at = @At("HEAD"), cancellable = true)
    private void injectLivingAnimations(EntityLivingBase entity, float par2, float par3, float par4, CallbackInfo ci) {

        if (!EntityTweaks.spectralFamiliar_fixIncorrectModel) {
            return;
        }

        EntitySpectralFamiliar familiar = (EntitySpectralFamiliar)entity;
        this.head.setRotationPoint(0.0f, 12.0f, -6.0f);
        this.body.setRotationPoint(0.0f, 11.0f, 2.0f);
        this.leg1.setRotationPoint(-3.0f, 18.0f, 7.0f);
        this.leg2.setRotationPoint(3.0f, 18.0f, 7.0f);
        this.leg3.setRotationPoint(-3.0f, 18.0f, -5.0f);
        this.leg4.setRotationPoint(3.0f, 18.0f, -5.0f);
        if (familiar.isSitting()) {
            this.body.rotateAngleX = 0.7853982f;
            this.body.rotationPointY += 3.5f;
            this.body.rotationPointZ += 0.0f;
            this.leg2.rotateAngleX = -0.15707964f;
            this.leg1.rotateAngleX = -0.15707964f;
            this.leg2.rotationPointY = 15.8f;
            this.leg1.rotationPointY = 15.8f;
            this.leg2.rotationPointZ = -7.0f;
            this.leg1.rotationPointZ = -7.0f;
            this.leg4.rotateAngleX = -1.5707964f;
            this.leg3.rotateAngleX = -1.5707964f;
            this.leg4.rotationPointY = 21.0f;
            this.leg3.rotationPointY = 21.0f;
            this.leg4.rotationPointZ = 1.0f;
            this.leg3.rotationPointZ = 1.0f;
        }

        ci.cancel();
    }

}
