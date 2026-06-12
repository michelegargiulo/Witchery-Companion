package com.smokeythebandicoot.witcherycompanion.mixins.witchery.integration.jei;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.resources.ResourceManager;
import net.msrandom.witchery.WitcheryResurrected;
import net.msrandom.witchery.integration.jei.JeiWitcheryPlugin;
import net.msrandom.witchery.util.WitcheryUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixins:
 * [Bugfix] Fix JEI force reloading on world load or dimension change
 */
@Mixin(JeiWitcheryPlugin.class)
public class JeiWitcheryPluginMixin {

    /**
     * Witchery normally registers its recipes during world loading. So, even if the JEI plugin was registered
     * normally (and not with hacky Reflection), the recipes would not be available when JEI loads. As a workaround,
     * we forcefully register the recipes as if Witchery was in its "Compatibility Mode" where it registers client
     * side recipes instead of waiting for the PacketUpdateRecipes to arrive during world load or dimension change.
     */
    @Inject(method = "registerCategories", at = @At("HEAD"), remap = false)
    private void injectRecipes(IRecipeCategoryRegistration registry, CallbackInfo ci) {
        if(ModConfig.IntegrationConfigurations.JeiIntegration.fixJeiForceReloading) {
            WitcheryUtils.getRECIPE_MANAGER().reload(new ResourceManager(null));
            WitcheryResurrected.Companion.reloadRecipes();
        }
    }

}
