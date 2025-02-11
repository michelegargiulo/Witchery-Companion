package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.modifiers;

import c4.conarm.common.items.armor.Chestplate;
import c4.conarm.lib.modifiers.ArmorModifierTrait;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.IToolMod;


public class ModifierUndeadRepellent extends ArmorModifierTrait {

    public static final String id = WitcheryCompanion.prefix("undead_repellent");

    public ModifierUndeadRepellent() {
        super(id, 0x2d9c77);
    }

    @Override
    public boolean canApplyCustom(ItemStack stack) {
        return stack.getItem() instanceof Chestplate;
    }

    @Override
    public boolean canApplyTogether(IToolMod otherModifier) {
        if (otherModifier instanceof ModifierCreeperRepellent) return false;
        return true;
    }

}
