package com.smokeythebandicoot.witcherycompanion.utils;


import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.msrandom.witchery.entity.familiar.Familiar;
import net.msrandom.witchery.entity.passive.EntityOwl;
import net.msrandom.witchery.extensions.PlayerExtendedData;
import net.msrandom.witchery.util.WitcheryUtils;

public class FamiliarTest {

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        EntityPlayer player = event.getPlayer();
        PlayerExtendedData playerEx = WitcheryUtils.getExtension((EntityPlayer)player);
        NBTTagCompound familiarData = playerEx.familiar == null ? new NBTTagCompound() : playerEx.familiar.getData();
        Utils.logChat(familiarData);

        if (playerEx.familiar != null) {
            Utils.logChat("§4" + playerEx.familiar.isSummoned());

            for (EntityOwl owl : player.world.getEntities(EntityOwl.class, entityOwl -> true)) {
                if (owl != null && owl.getOwner() == player) {
                    Utils.logChat("§5X:" + owl.posX + "; Y:" + owl.posY + "; Z:" + owl.posZ);
                }

            }
        }
    }

}
