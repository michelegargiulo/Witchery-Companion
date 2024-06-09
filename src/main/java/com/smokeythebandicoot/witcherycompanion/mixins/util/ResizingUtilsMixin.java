package com.smokeythebandicoot.witcherycompanion.mixins.util;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.mixins_early.minecraft.entity.player.EntityPlayerMixin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.msrandom.witchery.util.EntitySizeInfo;
import net.msrandom.witchery.util.ResizingUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

/**
 Mixins:
 [Bugfix] Fix
 */
@Mixin(ResizingUtils.class)
public class ResizingUtilsMixin {

    // Missing @Unique annotation due to successive call with Reflection
    private static Field witchery_Patcher$targetScaleFieldWidth = null;
    private static Field witchery_Patcher$targetScaleFieldHeight = null;

    @Unique
    private static boolean witchery_Patcher$targetScaleHeightAttemp = false;

    @Unique
    private static boolean witchery_Patcher$targetScaleWidthAttemp = false;

    @Unique
    private static boolean witchery_Patcher$errored = false;


    @Inject(method = "setSize", remap = false, at = @At("HEAD"))
    private static void forceSetSizePlayers(Entity entity, float targetWidth, float targetHeight, CallbackInfo ci) {

        // On error, the "errored" flag is set to true. All if statements' execution is prevented, and this Inject
        // will do nothing. This usually means that one of the injected fields has failed
        if (ModConfig.PatchesConfiguration.PotionTweaks.resizing_fixEffectOnPlayers && !witchery_Patcher$errored) {

                // Try retrieve Width field - should only be run once, either successfully or not
                if (witchery_Patcher$targetScaleFieldWidth == null && !witchery_Patcher$targetScaleWidthAttemp && !witchery_Patcher$errored) {
                    try {
                        witchery_Patcher$targetScaleFieldWidth = EntityPlayer.class.getDeclaredField("witchery_Patcher$resizeScaleWidth");
                        witchery_Patcher$targetScaleFieldWidth.setAccessible(true);
                        witchery_Patcher$targetScaleWidthAttemp = true;
                    } catch (NoSuchFieldException e) {
                        witchery_Patcher$errored = true;
                        throw new RuntimeException(e);
                    }
                }

                // Try retrieve Height field - should only be run once, either successfully or not
                if (witchery_Patcher$targetScaleFieldHeight == null && !witchery_Patcher$targetScaleHeightAttemp && !witchery_Patcher$errored) {
                    try {
                        witchery_Patcher$targetScaleFieldHeight = EntityPlayer.class.getDeclaredField("witchery_Patcher$resizeScaleHeight");
                        witchery_Patcher$targetScaleFieldHeight.setAccessible(true);
                        witchery_Patcher$targetScaleHeightAttemp = true;
                    } catch (NoSuchFieldException e) {
                        witchery_Patcher$errored = true;
                        throw new RuntimeException(e);
                    }
                }

                // If success, then apply new resize
                if (!witchery_Patcher$errored && witchery_Patcher$targetScaleFieldWidth != null && witchery_Patcher$targetScaleFieldHeight != null && entity instanceof EntityPlayer) {
                    try {
                        EntityPlayer player = (EntityPlayer) entity;
                        EntitySizeInfo sizeInfo = new EntitySizeInfo(player);
                        witchery_Patcher$targetScaleFieldWidth.set(player, targetWidth / sizeInfo.defaultWidth);
                        witchery_Patcher$targetScaleFieldHeight.set(player, targetHeight / sizeInfo.defaultHeight);
                    } catch (IllegalAccessException e) {
                        witchery_Patcher$errored = true;
                        throw new RuntimeException(e);
                    }
                }
        }
    }

}
