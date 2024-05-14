package com.smokeythebandicoot.witcherypatcher.integrations.justenoughresources;

import com.smokeythebandicoot.witcherypatcher.WitcheryPatcher;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;

public class DummyEnchantment extends Enchantment {

    public DummyEnchantment() {

        super(Rarity.COMMON, EnumEnchantmentType.ALL, new EntityEquipmentSlot[]{ });
        this.setName(WitcheryPatcher.MODID + "_dummy");
    }

    @Override
    public String getName() {
        return "enchantment.witcherypatches.dummy";
    }

}
