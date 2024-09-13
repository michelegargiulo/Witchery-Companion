package com.smokeythebandicoot.witcherycompanion.mixins.resources;

import com.google.gson.Gson;
import com.smokeythebandicoot.witcherycompanion.api.spiriteffect.SpiritEffectApi;
import net.minecraft.resources.JsonReloadListener;
import net.msrandom.witchery.infusion.spirit.SpiritEffectRecipe;
import net.msrandom.witchery.resources.SpiritEffectManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.SortedSet;

/**
 * Mixins:
 * [Feature] Update SpiritEffectApi for SpiritEffectRecipes when they are updated
 */
@Mixin(SpiritEffectManager.class)
public abstract class SpiritEffectManagerMixin extends JsonReloadListener {

    private SpiritEffectManagerMixin(Gson gson, String folder) {
        super(gson, folder);
    }

    /** This Mixin takes in the effects (from PacketUpdateSpiritEffects) and passes to the API
     * to reload them. The API then provides a central and conveniente point to access them by key **/
    @Inject(method = "deserializeEffects", remap = false, at = @At("TAIL"))
    private void reloadSpiritEffectsRegistry(SortedSet<SpiritEffectRecipe> effects, CallbackInfo ci) {
        SpiritEffectApi.reloadEffects(effects);
    }

}
