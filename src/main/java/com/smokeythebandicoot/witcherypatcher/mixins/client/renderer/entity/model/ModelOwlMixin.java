package com.smokeythebandicoot.witcherypatcher.mixins.client.renderer.entity.model;

import com.smokeythebandicoot.witcherypatcher.config.ModConfig;
import com.smokeythebandicoot.witcherypatcher.utils.Utils;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.msrandom.witchery.client.renderer.entity.model.ModelOwl;
import net.msrandom.witchery.entity.passive.EntityOwl;
import net.msrandom.witchery.util.WitcheryUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ModelOwl.class)
public abstract class ModelOwlMixin extends ModelBase {

    @Shadow(remap = false)
    public ModelRenderer body;

    @Shadow(remap = false)
    public ModelRenderer leftLeg;

    @Shadow(remap = false)
    public ModelRenderer rightLeg;

    @Shadow
    public abstract void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale);


    @Inject(method = "render", remap = true, at = @At("HEAD"), cancellable = true)
    public void WPrenderChildAtScale(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.EntityTweaks.owl_tweakRenderChildSmaller && entity instanceof EntityOwl) {

            if (this.isChild) {
                GlStateManager.pushMatrix();
                GlStateManager.scale(0.5F, 0.5F, 0.5F);
                GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
                this.body.render(scale);
                GlStateManager.popMatrix();
                ci.cancel();
            } else {
                this.body.render(scale);
                ci.cancel();
            }
        }
    }

    public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTickTime) {
        if (ModConfig.PatchesConfiguration.EntityTweaks.owl_tweakSitModelChange && entitylivingbaseIn instanceof EntityOwl) {
            EntityOwl entityOwl = (EntityOwl) entitylivingbaseIn;

            float scale = (this.isChild && ModConfig.PatchesConfiguration.EntityTweaks.owl_tweakRenderChildSmaller) ? 0.5f : 1.0f;
            float offset = 0.1f;

            if (entityOwl.isSitting()) {
                this.body.offsetY = offset * scale;
                this.leftLeg.offsetY = -offset * scale;
                this.rightLeg.offsetY = -offset * scale;
            } else {
                this.body.offsetY = 0.0f;
                this.leftLeg.offsetY = 0.0f;
                this.rightLeg.offsetY = 0.0f;
            }
        }
    }

}
