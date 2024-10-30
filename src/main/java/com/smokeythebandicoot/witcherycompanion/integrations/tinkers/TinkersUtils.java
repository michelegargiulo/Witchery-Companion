package com.smokeythebandicoot.witcherycompanion.integrations.tinkers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.init.items.WitcheryEquipmentItems;
import slimeknights.tconstruct.library.utils.TinkerUtil;

public class TinkersUtils {

    public static boolean isTicLoaded = false;
    public static boolean isCoALoaded = false;

    public static boolean isBabaHatWorn(EntityPlayer player) {
        ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        return helmet.getItem() == WitcheryEquipmentItems.WITCH_HAT ||
                isCoALoaded && TinkerUtil.hasModifier(helmet.getTagCompound(), Integration.MODIFIER_ARMOR_BABAS_BLESS.getModifierIdentifier());

    }

}
