package com.smokeythebandicoot.witcherycompanion.mixins.util;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.PotionTweaks;
import com.smokeythebandicoot.witcherycompanion.utils.ReflectionHelper;
import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.msrandom.witchery.util.EntitySizeInfo;
import net.msrandom.witchery.util.ResizingUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 Mixins:
 [Bugfix] Fix brew of Resizing not working on players
 */
@Mixin(ResizingUtils.class)
public class ResizingUtilsMixin {

    @Unique
    private static boolean witchery_Patcher$errored = false;


    @Inject(method = "setSize", remap = false, cancellable = true, at = @At("HEAD"))
    private static void forceSetSizePlayers(Entity entity, float targetWidth, float targetHeight, CallbackInfo ci) {

        if (!PotionTweaks.resizing_fixEffectOnPlayers ||
            witchery_Patcher$errored ||
            !(entity instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) entity;
        Float witchery_Patcher$targetScaleWidth = ReflectionHelper.<Float>getField(player, "witchery_Patcher$resizeScaleWidth", false);
        Float witchery_Patcher$targetScaleHeight = ReflectionHelper.<Float>getField(player, "witchery_Patcher$resizeScaleHeight", false);

        // If an error occurred, then avoid repeating the process
        if (witchery_Patcher$targetScaleWidth == null || witchery_Patcher$targetScaleHeight == null) {
            witchery_Patcher$errored = true;
        }

        // If field retrieval was successful, proceed
        EntitySizeInfo sizeInfo = new EntitySizeInfo(player);
        ReflectionHelper.setField(player, "witchery_Patcher$resizeScaleWidth", false, targetWidth / sizeInfo.defaultWidth);
        ReflectionHelper.setField(player, "witchery_Patcher$resizeScaleHeight", false, targetHeight / sizeInfo.defaultHeight);

        Utils.logChat("TARGET" + targetHeight);
        Utils.logChat("DEFAULT" + sizeInfo.defaultHeight);
        player.eyeHeight = player.getDefaultEyeHeight() * (targetHeight / sizeInfo.defaultHeight);

        // Since the potion is applied to a player, no need to check
        // for Zombies or EntityAgeable creatures. Can cancel
        ci.cancel();

    }

}
