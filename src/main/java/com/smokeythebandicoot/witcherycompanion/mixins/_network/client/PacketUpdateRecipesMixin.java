package com.smokeythebandicoot.witcherycompanion.mixins._network.client;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.client.PacketUpdateRecipes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PacketUpdateRecipes.class)
public class PacketUpdateRecipesMixin {

    @Inject(method = "apply", remap = false, at = @At("HEAD"), cancellable = true)
    public void disableRecipeSync(EntityPlayer player, CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.CommonTweaks.recipeSync_tweakDisableRecipeSyncing) {
            ci.cancel();
        }
    }

}
