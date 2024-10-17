package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.modifiers;

import c4.conarm.common.items.armor.Helmet;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.traits.TraitNecromancerClothing;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.traits.TraitWitchClothing;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.IToolMod;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;


/**
 * Marker Trait - Witchery checks if player is wearing a helmet with this trait
 * and if it is, its effects will be applied:
 * - Unlock Baba's bonus when brewing in Kettle
 * **/
public class ModifierBabasBless extends ModifierTrait {

    public static final String id = WitcheryCompanion.prefix("babas_bless");

    public ModifierBabasBless() {
        super(id, 0xDD20DD);
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
