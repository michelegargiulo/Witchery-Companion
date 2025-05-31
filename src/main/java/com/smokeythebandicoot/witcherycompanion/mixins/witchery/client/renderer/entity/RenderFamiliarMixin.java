package com.smokeythebandicoot.witcherycompanion.mixins.witchery.client.renderer.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.api.accessors.entities.spectralfamiliar.IEntitySpectralFamiliarAccessor;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPig;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.msrandom.witchery.client.renderer.entity.RenderFamiliar;
import net.msrandom.witchery.entity.EntitySpectralFamiliar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixins:
 * [Integration] When CraftTweaker integration is enabled, fix color change not being applied
 */
@Mixin(RenderFamiliar.class)
public abstract class RenderFamiliarMixin extends RenderLiving<EntitySpectralFamiliar> {

    private RenderFamiliarMixin(RenderManager rendermanagerIn, ModelBase modelbaseIn, float shadowsizeIn) {
        super(rendermanagerIn, modelbaseIn, shadowsizeIn);
    }


    @WrapOperation(method = "doRender(Lnet/msrandom/witchery/entity/EntitySpectralFamiliar;DDDFF)V", remap = false,
            at = @At(value = "INVOKE", remap = false, target = "Lnet/msrandom/witchery/entity/EntitySpectralFamiliar;getItemIDToFind()I"))
    private int fixColorWithCrTCompatEnabled(EntitySpectralFamiliar instance, Operation<Integer> original) {
        if (ModConfig.PatchesConfiguration.EntityTweaks.spectralFamiliar_tweakEnableCrafttweakerCompat) {
            if (instance instanceof IEntitySpectralFamiliarAccessor) {
                IEntitySpectralFamiliarAccessor accessor = (IEntitySpectralFamiliarAccessor) instance;
                return accessor.witcherycompanion$accessor$getSniffedItem().isEmpty() ? -1 : 0;
            }
        }
        return original.call(instance);
    }

}
