package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.modifiers;

import c4.conarm.common.items.armor.Chestplate;
import c4.conarm.lib.modifiers.ArmorModifierTrait;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.IToolMod;


public class ModifierCreeperRepellent extends ArmorModifierTrait {

    public static final String id = WitcheryCompanion.prefix("creeper_repellent");

    public ModifierCreeperRepellent() {
        super(id, 0x2e9c5e);
    }

    @Override
    public boolean canApplyCustom(ItemStack stack) {
        return stack.getItem() instanceof Chestplate;
    }

    @Override
    public boolean canApplyTogether(IToolMod otherModifier) {
        if (otherModifier instanceof ModifierUndeadRepellent) return false;
        return true;
    }

}
