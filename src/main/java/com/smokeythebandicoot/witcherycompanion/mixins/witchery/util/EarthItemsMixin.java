package com.smokeythebandicoot.witcherycompanion.mixins.witchery.util;

import com.smokeythebandicoot.witcherycompanion.api.OverworldInfusionApi;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.util.EarthItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixins:
 * [Tweak] CraftTweaker compat for Metallic items
 */
@Mixin(EarthItems.class)
public abstract class EarthItemsMixin {

    /** This Mixin injects into isMatch function that checks if an ItemStack is a metal item.
     * Stops the check against the hard-coded list and redirects towards the API **/
    @Inject(method = "isMatch", remap = false, at = @At("HEAD"), cancellable = true)
    private void injectEarthInfusionApiMetalItems(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (ModConfig.PatchesConfiguration.InfusionTweaks.overworldInfusion_tweakEnableCrafttweakerCompat) {
            cir.setReturnValue(OverworldInfusionApi.isMetalItem(stack));
        }
    }

}
