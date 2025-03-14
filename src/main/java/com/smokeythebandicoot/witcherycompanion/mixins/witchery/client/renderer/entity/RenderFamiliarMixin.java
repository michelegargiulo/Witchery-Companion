package com.smokeythebandicoot.witcherycompanion.mixins.witchery.client.renderer.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.api.accessors.entities.spectralfamiliar.IEntitySpectralFamiliarAccessor;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.client.renderer.entity.RenderFamiliar;
import net.msrandom.witchery.entity.EntitySpectralFamiliar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RenderFamiliar.class)
public abstract class RenderFamiliarMixin {

    @WrapOperation(method = "doRender", remap = false, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/msrandom/witchery/entity/EntitySpectralFamiliar;getItemIDToFind()I"))
    private int fixColorWithCrTCompatEnabled(EntitySpectralFamiliar instance, Operation<Integer> original) {
        if (ModConfig.PatchesConfiguration.EntityTweaks.spectralFamiliar_tweakEnableCrafttweakerCompat) {
            if (instance instanceof IEntitySpectralFamiliarAccessor) {
                IEntitySpectralFamiliarAccessor accessor = (IEntitySpectralFamiliarAccessor) instance;
                return accessor.witcherycompanion$accessor$getSniffedItem() == ItemStack.EMPTY ? -1 : 0;
            }
        }
        return original.call(instance);
    }

}
