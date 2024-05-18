package com.smokeythebandicoot.witcherycompanion.integrations.justenoughresources;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;

public class DummyEnchantment extends Enchantment {

    public DummyEnchantment() {

        super(Rarity.COMMON, EnumEnchantmentType.ALL, new EntityEquipmentSlot[]{ });
        this.setName(WitcheryCompanion.MODID + "_dummy");
    }

    @Override
    public String getName() {
        return "enchantment.witcherypatches.dummy";
    }

}
