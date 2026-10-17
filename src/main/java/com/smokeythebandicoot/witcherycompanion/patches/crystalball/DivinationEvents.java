package com.smokeythebandicoot.witcherycompanion.patches.crystalball;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Event handlers of the spectator-based Crystal Ball divination.
 * NOTE: these are subscribed regardless of the tweak being enabled: if a player logs in with a leftover recovery tag
 * (server crashed during a divination, then the tweak was disabled), game mode and position still have to be restored.
 * Every handler is a no-op when no divination exists.
 */
public class DivinationEvents {

    public static final DivinationEvents INSTANCE = new DivinationEvents();

    private DivinationEvents() { }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.side != Side.SERVER) return;
        if (DivinationRegistry.isEmpty() || !(event.player instanceof EntityPlayerMP)) return;
        DivinationManager.onPlayerTick((EntityPlayerMP) event.player);
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) DivinationManager.onPlayerLoggedIn((EntityPlayerMP) event.player);
    }

    /** Fired before the data of the player is written to disk */
    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.player instanceof EntityPlayerMP) DivinationManager.onPlayerLoggedOut((EntityPlayerMP) event.player);
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.player instanceof EntityPlayerMP) DivinationManager.finishAfterDeath((EntityPlayerMP) event.player);
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.player instanceof EntityPlayerMP) DivinationManager.onPlayerChangedDimension((EntityPlayerMP) event.player);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayerMP && !DivinationRegistry.isEmpty()) {
            DivinationManager.onPlayerDeath((EntityPlayerMP) event.getEntityLiving());
        }
    }

    /** Bodies left by divinations that no longer exist (server stopped or crashed) are removed when they load */
    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        if (entity.world.isRemote) return;
        if (DivinationManager.isOrphanBody(entity)) {
            event.setCanceled(true);
            // Dead entities are not saved back with their chunk
            entity.setDead();
        }
    }

}
