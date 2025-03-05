package com.smokeythebandicoot.witcherycompanion.mixins.witchery.resources;

import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.PatchouliApiIntegration;
import com.smokeythebandicoot.witcherycompanion.utils.Mods;
import net.minecraft.resources.ResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.msrandom.witchery.resources.RiteManager;
import net.msrandom.witchery.rite.Rite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Mixin:
 * [Integration] Patchouli flag reloading for disabled rites
 * [Tweak] Disable Rite of Prior Incarnation
 */
@Mixin(RiteManager.class)
public abstract class RiteManagerMixin {

    /** Injects into while iteration that uses iterator to check if the current element has the
     * Rite of Prior Incarnation key and skips the iteration calling iterator.next() if the rite is disabled **/
    @WrapOperation(method = "apply(Ljava/util/Map;Lnet/minecraft/resources/ResourceManager;)V", remap = false,
            at = @At(value = "INVOKE", target = "Ljava/util/Iterator;next()Ljava/lang/Object;", remap = false,ordinal = 0))
    private Object skipPriorIncarnationWithTMG(Iterator<Map.Entry<ResourceLocation, ? extends JsonElement>> instance, Operation<Map.Entry<ResourceLocation, ?>> original) {
        Object result = original.call(instance);

        if (ModConfig.PatchesConfiguration.RitesTweaks.priorIncarnation_tweakDisableRite) {
            Map.Entry<ResourceLocation, ?> entry = (Map.Entry<ResourceLocation, ?>) result;
            if (entry.getKey().getPath().equals("curse/prior_incarnation")) {

                // Skip the rite and continue to next iteration calling iterator.next()
                return instance.next();
            }
        }
        return result;
    }

    /** Injects at the end of apply() in RiteManager when it finished reloading all the rites to update Patchouli Flags **/
    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/resources/ResourceManager;)V", remap = false, at = @At("RETURN"))
    private void injectRiteFlagReloader(Map<ResourceLocation, ? extends JsonElement> value, ResourceManager resourceManager, CallbackInfo ci) {
        if (Loader.isModLoaded(Mods.PATCHOULI)) {
            PatchouliApiIntegration.riteReloader.reloadFlags();
        }
    }

    /** Injects at the end of apply() in RiteManager when it finished reloading all the rites to update Patchouli Flags **/
    @Inject(method = "updateRites$WitcheryResurrected", remap = false, at = @At("RETURN"))
    private void injectRiteFlagReloaderInUpdate(Map<ResourceLocation, List<Rite>> map, CallbackInfo ci) {
        if (Loader.isModLoaded(Mods.PATCHOULI)) {
            PatchouliApiIntegration.riteReloader.reloadFlags();
        }
    }

}
