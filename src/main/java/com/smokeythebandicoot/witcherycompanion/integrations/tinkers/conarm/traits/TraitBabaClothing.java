package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.traits;

import c4.conarm.lib.traits.AbstractArmorTrait;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import slimeknights.tconstruct.library.modifiers.IToolMod;


public class TraitBabaClothing extends AbstractArmorTrait {

    public static final String id = WitcheryCompanion.prefix("baba_clothing");

    public TraitBabaClothing() {
        super(id, 0xDD20DD);
    }

    @Override
    public boolean canApplyTogether(IToolMod otherModifier) {
        if (otherModifier instanceof TraitWitchClothing) return false;
        if (otherModifier instanceof TraitNecromancerClothing) return false;
        return true;
    }
}
