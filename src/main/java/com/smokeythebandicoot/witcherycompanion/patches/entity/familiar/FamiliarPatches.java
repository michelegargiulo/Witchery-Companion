package com.smokeythebandicoot.witcherycompanion.patches.entity.familiar;

import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.msrandom.witchery.entity.familiar.Familiar;
import net.msrandom.witchery.entity.familiar.Familiars;

import java.util.UUID;

/**
 Patches:
 [Bugfix] World gets loaded and player gets added for last (inconsistent?). So familiar owner resorts to null on world
 load. This patch runs on EntityJoinWorldEvent and re-binds familiar to its owner, as owner is a player and player's UID
 is consistent across reloads (unique and persistent for each Minecraft player)
 */
public class FamiliarPatches {

    private static FamiliarPatches INSTANCE = null;
    public static FamiliarPatches getInstance() {
        if (INSTANCE == null) INSTANCE = new FamiliarPatches();
        return INSTANCE;
    }
    private FamiliarPatches() {}

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEntityJoinWorldEvent(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote) return;
        Entity entity = event.getEntity();
        if (entity instanceof Familiar<?>) {
            boundFamiliarToPlayer((Familiar<?>) entity);
        } else if (entity instanceof EntityPlayer) {
            boundPlayerToFamiliar((EntityPlayer) entity);
        }
    }

    public static void boundFamiliarToPlayer(Familiar<?> familiar) {
        UUID ownerId = familiar.getOwnerId();
        EntityPlayer player = familiar.getEntity().world.getPlayerEntityByUUID(ownerId);
        if (player == null) {
            Utils.logChat("Familiar joined world but player is not online!");
            return;
        }
        familiar.bindTo(player);
    }

    public static void boundPlayerToFamiliar(EntityPlayer player) {
        Familiar<?> familiar = Familiars.getBoundFamiliar(player);
        if (familiar != null) return;
        for (Entity entity : player.world.getEntities(Entity.class, e -> true)) {
            if (entity instanceof Familiar<?>) {
                familiar = (Familiar<?>) entity;
                UUID ownerId = familiar.getOwnerId();
                if (player.getUniqueID().equals(ownerId)) {
                    familiar.bindTo(player);
                    return;
                }
            }
        }
    }

}
