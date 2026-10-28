package com.smokeythebandicoot.witcherycompanion.mixins.witchery.rite;

import com.smokeythebandicoot.witcherycompanion.api.CustomRiteApi;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.rite.Rite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixins:
 * [Integration] CraftTweaker Custom Rites: a custom rite can be listed in a section (group) of W:R's books other than the
 *      folder it is loaded from (setSection/insertAfter in the script). RiteManager reads the group of every rite to
 *      build the sections of the books, so it is overridden here
 */
@Mixin(Rite.class)
public abstract class RiteMixin {

    @Shadow(remap = false)
    public abstract ResourceLocation getId();

    @Inject(method = "getGroup", remap = false, at = @At("HEAD"), cancellable = true)
    private void witcherycompanion$overrideGroupOfCustomRites(CallbackInfoReturnable<ResourceLocation> cir) {
        if (!ModConfig.IntegrationConfigurations.CraftTweakerIntegration.enableCustomRites) return;

        ResourceLocation override = CustomRiteApi.getGroupOverride(this.getId());
        if (override != null) cir.setReturnValue(override);
    }

}
