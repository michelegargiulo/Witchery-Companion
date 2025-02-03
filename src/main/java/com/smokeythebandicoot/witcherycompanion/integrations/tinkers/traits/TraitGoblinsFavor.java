package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.traits;

import c4.conarm.common.items.armor.Helmet;
import c4.conarm.lib.modifiers.ArmorModifierTrait;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.IToolMod;
import slimeknights.tconstruct.tools.tools.Pickaxe;


/**
 * Marker Trait - Witchery checks if player is wearing a helmet with this trait
 * and if it is, its effects will be applied:
 * - Unlock Baba's bonus when brewing in Kettle
 * **/
public class TraitGoblinsFavor extends ArmorModifierTrait {

    public static final String id = WitcheryCompanion.prefix("goblins_favor");

    public TraitGoblinsFavor() {
        super(id, 0x61aadf);
    }

    @Override
    public boolean canApplyCustom(ItemStack stack) {
        return stack.getItem() instanceof Pickaxe; // Can only be applied to helmets
    }

    @Override
    public boolean canApplyTogether(IToolMod otherModifier) {
        // if (otherModifier instanceof TraitWitchClothing) return false;
        // if (otherModifier instanceof TraitNecromancerClothing) return false;
        return true;
    }

}
