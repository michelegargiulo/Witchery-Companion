package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.modifiers;

import c4.conarm.common.items.armor.Helmet;
import c4.conarm.lib.modifiers.ArmorModifierTrait;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.IToolMod;


/**
 * Marker Trait - Witchery checks if player is wearing a helmet with this trait
 * and if it is, its effects will be applied:
 * - Unlock Baba's bonus when brewing in Kettle
 * **/
public class ModifierArmorBabasBless extends ArmorModifierTrait {

    public static final String id = WitcheryCompanion.prefix("babas_bless");

    public ModifierArmorBabasBless() {
        super(id, 0xdd20dd);
    }

    @Override
    public boolean canApplyCustom(ItemStack stack) {
        return stack.getItem() instanceof Helmet; // Can only be applied to helmets
    }

    @Override
    public boolean canApplyTogether(IToolMod otherModifier) {
        // if (otherModifier instanceof TraitWitchClothing) return false;
        // if (otherModifier instanceof TraitNecromancerClothing) return false;
        return true;
    }

}
