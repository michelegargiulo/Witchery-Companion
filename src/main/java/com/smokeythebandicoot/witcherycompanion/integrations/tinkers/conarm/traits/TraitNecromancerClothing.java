package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.traits;

import c4.conarm.lib.traits.AbstractArmorTrait;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import slimeknights.tconstruct.library.modifiers.IToolMod;

public class TraitNecromancerClothing extends AbstractArmorTrait {

    public static final String id = WitcheryCompanion.prefix("necromancer_clothing");

    public TraitNecromancerClothing() {
        super(id, 0x0A0A0A);
    }

    @Override
    public boolean canApplyTogether(IToolMod otherModifier) {
        if (otherModifier instanceof TraitWitchClothing) return false;
        if (otherModifier instanceof TraitBabaClothing) return false;
        return true;
    }
}
