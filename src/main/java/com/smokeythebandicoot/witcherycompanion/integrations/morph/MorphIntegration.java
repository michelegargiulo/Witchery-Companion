package com.smokeythebandicoot.witcherycompanion.integrations.morph;

import me.ichun.mods.morph.api.MorphApi;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

/**
 Integration:
 [Bugfix] Fix EntitySize returning to the default player size on serverside when player changes dimension
 */
public class MorphIntegration {

    public static MorphIntegration INSTANCE = new MorphIntegration();

    private MorphIntegration() { }

    public void handleMorphOnShapeShift(EntityPlayer player) {

        // Set Eye height
        if (player == null) return;
        EntityLivingBase morphEntity = MorphApi.getApiImpl().getMorphEntity(player.world, player.getName(), Side.SERVER);
        if (morphEntity == null) return;
        player.eyeHeight = morphEntity.getEyeHeight();

        // Set flying state
        if (!player.capabilities.allowFlying && player.capabilities.isFlying) {
            player.capabilities.isFlying = false;
        } else if (player.capabilities.allowFlying) {
            player.capabilities.isFlying = true;
        }

    }

}
