package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.traits;

import c4.conarm.lib.traits.AbstractArmorTrait;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.modifiers.ModifierArmorBabasBless;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.modifiers.ModifierArmorNecromancer;
import slimeknights.tconstruct.library.modifiers.IToolMod;


public class TraitWitchClothing extends AbstractArmorTrait {

    public static final String id = WitcheryCompanion.prefix("witch_clothing");

    public TraitWitchClothing() {
        super(id, 0xBB11BB);
    }


    @Override
    public boolean canApplyTogether(IToolMod otherModifier) {
        if (otherModifier instanceof ModifierArmorNecromancer) return false;
        if (otherModifier instanceof ModifierArmorBabasBless) return false;
        return true;
    }
}
