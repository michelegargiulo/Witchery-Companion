package com.smokeythebandicoot.witcherycompanion.integrations.tinkers;


import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.modifiers.ModifierArmorBabasBless;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.modifiers.ModifierArmorNecromancer;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.traits.TraitWitchClothing;

public enum ECompanionTrait {
    BABA_CLOTHING(ModifierArmorBabasBless.id),
    WITCH_CLOTHING(TraitWitchClothing.id),
    NECROMANCER_CLOTHING(ModifierArmorNecromancer.id),
    ;

    final String id;

    ECompanionTrait(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

}
