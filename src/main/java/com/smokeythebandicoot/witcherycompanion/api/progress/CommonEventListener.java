package com.smokeythebandicoot.witcherycompanion.api.progress;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.msrandom.witchery.init.WitcheryDimensions;

@Mod.EventBusSubscriber(modid = WitcheryCompanion.MODID)
public class CommonEventListener {

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        World world = event.getWorld();

        if (world.isRemote || !(entity instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) entity;

        if (WitcheryDimensions.SPIRIT_WORLD.isInDimension(player)) {
            ProgressUtils.unlockProgress(player,
                    ProgressUtils.getDimensionSecret("spirit_world"),
                    WitcheryProgressEvent.EProgressTriggerActivity.DIMENSION_VISIT.activityTrigger);
        } else if (WitcheryDimensions.TORMENT.isInDimension(player)) {
            ProgressUtils.unlockProgress(player,
                    ProgressUtils.getDimensionSecret("torment"),
                    WitcheryProgressEvent.EProgressTriggerActivity.DIMENSION_VISIT.activityTrigger);
        } else if (WitcheryDimensions.MIRROR.isInDimension(player)) {
            ProgressUtils.unlockProgress(player,
                    ProgressUtils.getDimensionSecret("mirror"),
                    WitcheryProgressEvent.EProgressTriggerActivity.DIMENSION_VISIT.activityTrigger);
        }
    }

}
