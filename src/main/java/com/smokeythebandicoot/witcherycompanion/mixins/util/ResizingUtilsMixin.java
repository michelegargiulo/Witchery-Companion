package com.smokeythebandicoot.witcherycompanion.mixins.util;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.PotionTweaks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.msrandom.witchery.util.EntitySizeInfo;
import net.msrandom.witchery.util.ResizingUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 Mixins:
 [Bugfix] Fix brew of Resizing not working on players
 */
@Mixin(ResizingUtils.class)
public class ResizingUtilsMixin {

    @Inject(method = "setSize", remap = false, cancellable = true, at = @At("HEAD"))
    private static void forceSetSizePlayers(Entity entity, float targetWidth, float targetHeight, CallbackInfo ci) {

        if (!PotionTweaks.resizing_fixEffectOnPlayers ||
            !(entity instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) entity;
        EntitySizeInfo sizeInfo = new EntitySizeInfo(player);
        player.eyeHeight = player.getDefaultEyeHeight() * (targetHeight / sizeInfo.defaultHeight);

        // Since the potion is applied to a player, no need to check
        // for Zombies or EntityAgeable creatures. Can cancel
        ci.cancel();

    }

}
