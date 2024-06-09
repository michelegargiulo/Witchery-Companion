package com.smokeythebandicoot.witcherycompanion.integrations.aquaacrobatics;

import net.minecraft.entity.player.EntityPlayer;

import java.lang.reflect.Field;

public class AAIntegration {

    private static Field heightScaleField = null;
    private static boolean heightScaleFieldAttempt = false;
    private static boolean errored = false;

    public static float getPlayerScale(EntityPlayer player) {

        // Attempt to retrieve height scale
        if (heightScaleField == null && !heightScaleFieldAttempt && !errored) {
            try {
                heightScaleField = EntityPlayer.class.getDeclaredField("witchery_Patcher$resizeScaleHeight");
                heightScaleField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                errored = true;
                throw new RuntimeException(e);
            }
            heightScaleFieldAttempt = true;

        }

        if (heightScaleField != null && !errored) {
            try {
                float heightScale = (float)heightScaleField.get(player);
                return heightScale;
            } catch (IllegalAccessException e) {
                errored = true;
                throw new RuntimeException(e);
            }
        }

        return 1.0f;

    }

}
