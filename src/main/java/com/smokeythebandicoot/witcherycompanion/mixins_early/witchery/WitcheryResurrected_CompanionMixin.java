package com.smokeythebandicoot.witcherycompanion.mixins_early.witchery;

import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.PatchouliApiIntegration;
import com.smokeythebandicoot.witcherycompanion.utils.Mods;
import net.minecraftforge.fml.common.Loader;
import net.msrandom.witchery.WitcheryResurrected;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WitcheryResurrected.Companion.class)
public abstract class WitcheryResurrected_CompanionMixin {

    @Inject(method = "reloadRecipes", remap = false, at = @At("TAIL"))
    private void reloadPatchouliBooksOnRecipesUpdate(CallbackInfo ci) {
        if (Loader.isModLoaded(Mods.PATCHOULI)) {
            PatchouliApiIntegration.cauldronRecipeReloader.reloadFlags();
        }
    }

}
