package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.modifiers;

import c4.conarm.lib.modifiers.ArmorModifierTrait;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.traits.TraitWitchClothing;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.modifiers.IToolMod;

public class ModifierArmorNecromancer extends ArmorModifierTrait {

    public static final String id = WitcheryCompanion.prefix("necromancer_clothing");

    public ModifierArmorNecromancer() {
        super(id, 0x0A0A0A);
    }

    @Override
    public boolean canApplyTogether(IToolMod otherModifier) {
        if (otherModifier instanceof TraitWitchClothing) return false;
        if (otherModifier instanceof ModifierArmorBabasBless) return false;
        return true;
    }
}
