package com.smokeythebandicoot.witcherycompanion.mixins.witchery.resources;

import com.google.gson.JsonElement;
import com.smokeythebandicoot.witcherycompanion.api.CovenQuestApi;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.resources.ResourceManager;
import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.resources.CovenQuestManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

/**
 * Mixins:
 * [Feature] Custom Coven Quests: they are added to the registry of quests every time W:R reloads it
 */
@Mixin(CovenQuestManager.class)
public abstract class CovenQuestManagerMixin {

    /** apply() creates a new registry of quests every time the data are reloaded, and fills it with the JSON quests. When it is
     * done, the custom quests of the API are added, so that they are saved by id like any other quest and can be found when
     * the player's coven is loaded */
    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/resources/ResourceManager;)V", remap = false, at = @At("RETURN"))
    private void witcherycompanion$addCustomQuests(Map<ResourceLocation, ? extends JsonElement> value, ResourceManager resourceManager, CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.EntityTweaks.covenWitch_tweakQuestSystem) {
            CovenQuestApi.injectCustomQuests(CovenQuestManager.INSTANCE.getRegistry());
        }
    }

}
