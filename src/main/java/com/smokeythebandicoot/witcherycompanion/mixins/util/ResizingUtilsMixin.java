package com.smokeythebandicoot.witcherycompanion.mixins.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.msrandom.witchery.util.EntitySizeInfo;
import net.msrandom.witchery.util.ResizingUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(ResizingUtils.class)
public class ResizingUtilsMixin {

    // Missing @Unique annotation due to successive call with Reflection
    private static Field witchery_Patcher$targetScaleFieldWidth = null;
    private static Field witchery_Patcher$targetScaleFieldHeight = null;

    @Unique
    private static boolean witchery_Patcher$targetScaleHeightAttemp = false;

    @Unique
    private static boolean witchery_Patcher$targetScaleWidthAttemp = false;


    @Inject(method = "setSize", remap = false, at = @At("HEAD"))
    private static void forceSetSizePlayers(Entity entity, float targetWidth, float targetHeight, CallbackInfo ci) {
        try {
            // Try retrieve Width field
            if (witchery_Patcher$targetScaleFieldWidth == null && !witchery_Patcher$targetScaleWidthAttemp) {
                witchery_Patcher$targetScaleFieldWidth = EntityPlayer.class.getDeclaredField("witchery_Patcher$resizeScaleWidth");
                witchery_Patcher$targetScaleFieldWidth.setAccessible(true);
                witchery_Patcher$targetScaleWidthAttemp = true;
            }
            // Try retrieve Height field
            if (witchery_Patcher$targetScaleFieldHeight == null && !witchery_Patcher$targetScaleHeightAttemp) {
                witchery_Patcher$targetScaleFieldHeight = EntityPlayer.class.getDeclaredField("witchery_Patcher$resizeScaleHeight");
                witchery_Patcher$targetScaleFieldHeight.setAccessible(true);
                witchery_Patcher$targetScaleHeightAttemp = true;
            }

            // If success, then apply new resize
            if (witchery_Patcher$targetScaleFieldWidth != null && witchery_Patcher$targetScaleFieldHeight != null && entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer)entity;
                EntitySizeInfo sizeInfo = new EntitySizeInfo(player);
                witchery_Patcher$targetScaleFieldWidth.set(player, targetWidth / sizeInfo.defaultWidth);
                witchery_Patcher$targetScaleFieldHeight.set(player, targetHeight / sizeInfo.defaultHeight);
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
