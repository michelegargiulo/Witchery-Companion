package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.modifiers;

import c4.conarm.lib.modifiers.ArmorModifierTrait;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import net.minecraft.entity.EntityLiving;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.IToolMod;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;

/**
 * Emulates: Seeping Shoes
 * Description: Whenever Player has Poison potion effect, it seeps it into the ground, with a Bone-mealing effect
 */
public class ModifierSeeping extends ArmorModifierTrait {

    public ModifierSeeping() {
        super(WitcheryCompanion.prefix("seeping"), 0xddccdd);
        addAspects(new ModifierAspect.DataAspect(this), new ModifierAspect.SingleAspect(this));
    }

    // Incompatible with Homing
    @Override
    public boolean canApplyTogether(IToolMod otherModifier) {
        return super.canApplyTogether(otherModifier) &&
                !(otherModifier instanceof ModifierHoming)
                ;
    }

    @Override
    public boolean canApplyCustom(ItemStack stack) {
        return EntityLiving.getSlotForItemStack(stack) == EntityEquipmentSlot.FEET &&
                super.canApplyCustom(stack);
    }
}
