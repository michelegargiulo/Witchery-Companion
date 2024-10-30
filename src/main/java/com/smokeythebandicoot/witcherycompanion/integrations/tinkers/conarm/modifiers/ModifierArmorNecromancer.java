package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.modifiers;

import c4.conarm.lib.modifiers.ArmorModifierTrait;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.traits.TraitArmorWitchClothing;
import slimeknights.tconstruct.library.modifiers.IToolMod;

public class ModifierArmorNecromancer extends ArmorModifierTrait {

    public ModifierArmorNecromancer() {
        super(WitcheryCompanion.prefix("necromancer_clothing"), 0x0A0A0A);
    }

}
