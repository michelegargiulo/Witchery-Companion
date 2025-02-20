package com.smokeythebandicoot.witcherycompanion.mixins.witchery.util;

import com.smokeythebandicoot.witcherycompanion.api.EarthInfusionApi;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.util.EarthItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EarthItems.class)
public abstract class EarthItemsMixin {

    @Inject(method = "isMatch", remap = false, at = @At("HEAD"), cancellable = true)
    private void injectEarthInfusionApi(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (ModConfig.PatchesConfiguration.InfusionTweaks.earthInfusion_tweakEnableCrafttweakerCompat) {
            cir.setReturnValue(EarthInfusionApi.isMetalItem(stack));
        }
    }

}
