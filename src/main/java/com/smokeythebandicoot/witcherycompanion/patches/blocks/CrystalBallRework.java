package com.smokeythebandicoot.witcherycompanion.patches.blocks;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.api.player.DivinationData;
import com.smokeythebandicoot.witcherycompanion.api.player.IPlayerExtendedDataAccessor;
import com.smokeythebandicoot.witcherycompanion.utils.DiviningUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.msrandom.witchery.extensions.PlayerExtendedData;
import net.msrandom.witchery.util.WitcheryUtils;

@Mod.EventBusSubscriber(modid = WitcheryCompanion.MODID)
public class CrystalBallRework {

    /** When a player joins the world, terminate divination if they previously left the world while divinating **/
    @SubscribeEvent
    public static void onEntityJoinWorldEvent(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) entity;
            //DiviningUtils.setPlayerFromData(player);
            if (DiviningUtils.isDivining(player)) {
                DiviningUtils.terminateDivination(player);
            }
        }
    }

    /** When a divining player dies, first terminate divination **/
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityLivingDeath(LivingDeathEvent event) {
        Entity entity = event.getEntityLiving();
        if (entity instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) entity;
            if (DiviningUtils.isDivining(player)) {
                DiviningUtils.terminateDivination(player);
            }
        }
    }

    /** The player respawned or changed dimension **/
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerCloneEvent(PlayerEvent.Clone event) {
        Entity entity = event.getEntity();
        if (entity instanceof EntityPlayer && !entity.world.isRemote) {
            EntityPlayer player = (EntityPlayer) entity;
            EntityPlayer original = event.getOriginal();

            if (DiviningUtils.isDivining(original)) {

                // Copy the data over and terminate the divination
                PlayerExtendedData originalExtendedData = WitcheryUtils.getExtension(original);
                IPlayerExtendedDataAccessor originalAccessor = (IPlayerExtendedDataAccessor) originalExtendedData;
                DivinationData originalData = originalAccessor.getDivinationData();

                PlayerExtendedData cloneExtendedData = WitcheryUtils.getExtension(player);
                IPlayerExtendedDataAccessor cloneAccessor = (IPlayerExtendedDataAccessor) cloneExtendedData;

                cloneAccessor.setDivinationData(originalData);
                cloneExtendedData.processSync();

                //DiviningUtils.terminateDivination(player);
            }
        }
    }

}
