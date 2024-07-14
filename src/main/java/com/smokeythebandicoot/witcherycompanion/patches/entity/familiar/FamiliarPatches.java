package com.smokeythebandicoot.witcherycompanion.patches.entity.familiar;

import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.msrandom.witchery.entity.familiar.Familiar;
import net.msrandom.witchery.entity.familiar.FamiliarInstance;
import net.msrandom.witchery.entity.familiar.Familiars;
import net.msrandom.witchery.entity.passive.EntityOwl;
import net.msrandom.witchery.extensions.PlayerExtendedData;
import net.msrandom.witchery.extensions.WitcheryExtendedData;
import net.msrandom.witchery.util.WitcheryUtils;

import java.util.HashMap;
import java.util.UUID;

public class FamiliarPatches {

    private static FamiliarPatches INSTANCE = null;
    public static FamiliarPatches getInstance() {
        if (INSTANCE == null) INSTANCE = new FamiliarPatches();
        return INSTANCE;
    }
    private FamiliarPatches() {}

    @SubscribeEvent
    public void onBlockBreak_print(BlockEvent.BreakEvent event) {
        EntityPlayer player = event.getPlayer();
        PlayerExtendedData playerEx = WitcheryUtils.getExtension((EntityPlayer)player);

        if (playerEx.familiar != null) {
            Utils.logChat("§4Familiar summoned: " + playerEx.familiar.isSummoned());
            if (Familiars.getBoundFamiliar(player) != null) {
                Familiar familiar = ((Familiar<?>)Familiars.getBoundFamiliar(player).getEntity());
                Utils.logChat("§4Familiar owner BEFORE: " + familiar.getOwner());
                EntityPlayer owner = familiar.getEntity().world.getPlayerEntityByUUID(familiar.getOwnerId());
                if (owner != null) {
                    familiar.bindTo(owner);
                    Utils.logChat("§4Familiar owner AFTER: " + familiar.getOwner());
                } else {
                    Utils.logChat("Owner still null");
                }
            }
            for (EntityOwl owl : player.world.getEntities(EntityOwl.class, entityOwl -> true)) {
                if (owl != null && owl.getOwner() == player) {
                    Utils.logChat("§5X:" + (int)owl.posX + "; §5Y:" + (int)owl.posY + "; §5Z:" + (int)owl.posZ + "; World: " + (event.getWorld().isRemote ? "REMOTE" : "LOCAL"));
                }

            }
        } // ((EntityOwl) familiar).world.getPlayerEntityByUUID(familiar.getOwnerId()) before first logchat
    }

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
                    Utils.logChat("Found familiar!");
                    return;
                }
            }
        }
        Utils.logChat("Player is online, but familiar is not!");
    }

        //if (event.getWorld().isRemote) return;
/*
        Entity entity = event.getEntity();
        if (entity instanceof Familiar) {
            Familiar<?> familiar = (Familiar<?>) entity;
            Entity owner = familiar.getOwner();

            // Owner might not be online! So can't bound now. Save in hashmap and wait for owner to join world
            if (owner == null) {
                UUID ownerUid = familiar.getOwnerId();
                familiarMap.put(ownerUid, familiar);
                Utils.logChat("Familiar is in-world, but player is not online! Caching it...");
            } else if (owner instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) owner;
                familiar.bindTo(player);
                Utils.logChat("Familiar joined world with owner already bound. No need for caching");
            } else {
                Utils.logChat("Familiar owner is not a player! Can't bind");
            }
        } else if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (familiarMap.containsKey(player.getUniqueID())) {
                Familiar<?> familiar = familiarMap.get(player.getUniqueID());
                familiar.bindTo(player);
                Utils.logChat("Previously unbounded familiar UUID " + familiar.getEntity().getUniqueID() + " now bound to " + player.getUniqueID());
                familiarMap.remove(player.getUniqueID());
            }
        }
    }

 */


}
