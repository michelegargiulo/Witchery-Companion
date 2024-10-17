package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.traits;

import c4.conarm.lib.traits.AbstractArmorTrait;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import slimeknights.tconstruct.library.modifiers.IToolMod;


public class TraitWitchClothing extends AbstractArmorTrait {

    public static final String id = WitcheryCompanion.prefix("witch_clothing");

    public TraitWitchClothing() {
        super(id, 0xBB11BB);
    }


    @Override
    public boolean canApplyTogether(IToolMod otherModifier) {
        if (otherModifier instanceof TraitNecromancerClothing) return false;
        if (otherModifier instanceof TraitBabaClothing) return false;
        return true;
    }
}
