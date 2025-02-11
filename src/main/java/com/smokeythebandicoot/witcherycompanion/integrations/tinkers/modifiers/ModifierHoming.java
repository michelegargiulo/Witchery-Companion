package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.modifiers;

import c4.conarm.lib.modifiers.ArmorModifierTrait;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import net.minecraft.entity.EntityLiving;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.IToolMod;

/**
 * Emulates: Ruby Slippers
 * Description:
 */
public class ModifierHoming extends ArmorModifierTrait {

    public ModifierHoming() {
        super(WitcheryCompanion.prefix("homing"), 0xdd2010);
    }


    // Incompatible with Seeping
    @Override
    public boolean canApplyTogether(IToolMod otherModifier) {
        return super.canApplyTogether(otherModifier) &&
                !(otherModifier instanceof ModifierSeeping)
                ;
    }

    @Override
    public boolean canApplyCustom(ItemStack stack) {
        return EntityLiving.getSlotForItemStack(stack) == EntityEquipmentSlot.FEET &&
                super.canApplyCustom(stack);
    }
}
