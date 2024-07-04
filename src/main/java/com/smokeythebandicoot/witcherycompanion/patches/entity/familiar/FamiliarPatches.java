package com.smokeythebandicoot.witcherycompanion.patches.entity.familiar;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.msrandom.witchery.entity.familiar.Familiar;
import net.msrandom.witchery.entity.familiar.FamiliarInstance;
import net.msrandom.witchery.extensions.PlayerExtendedData;
import net.msrandom.witchery.util.WitcheryUtils;

public class FamiliarPatches {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onFamiliarDeath(LivingDeathEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (!(entity instanceof Familiar)) return;
        if (!event.isCancelable() || event.isCanceled()) return;
        Familiar<?> familiar = (Familiar<?>) entity;
        familiar.dismiss();
        Entity owner = familiar.getOwner();
        if (!(owner instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) owner;
        PlayerExtendedData playerEx = WitcheryUtils.getExtension(player);
        FamiliarInstance instance = playerEx.familiar;
        if (instance != null) {
            instance.setSummoned(false);
            instance.setColor(familiar.getColor());
            instance.getData().setString("id", String.valueOf(EntityList.getKey(familiar.getEntity())));
            entity.writeToNBT(instance.getData());
        }
        playerEx.markChanged();
    }

    @SubscribeEvent
    public static void entityJoinWorldEvent(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Familiar)) return;
        if (!event.isCancelable() || event.isCanceled()) return;
        Familiar<?> familiar = (Familiar<?>) entity;
        Entity owner = familiar.getOwner();
        if (!(owner instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) owner;
        familiar.bindTo(player);
        PlayerExtendedData playerEx = WitcheryUtils.getExtension(player);
        FamiliarInstance instance = playerEx.familiar;
        if (instance != null) {
            instance.setSummoned(true);
            instance.setColor(familiar.getColor());
            instance.getData().setString("id", String.valueOf(EntityList.getKey(familiar.getEntity())));
            entity.writeToNBT(instance.getData());
        }
        playerEx.markChanged();
    }

    /*
    @SubscribeEvent
    public void onEntityLeaveWorld() {

    }
     */

}
