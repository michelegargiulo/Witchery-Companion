package com.smokeythebandicoot.witcherycompanion.integrations.conarm.modifiers;

import c4.conarm.lib.modifiers.ArmorModifierTrait;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import net.minecraft.entity.EntityLiving;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;

/**
 * Mog's Quiver: Witchery's quiver is a chest piece that makes bows not consume arrows (see ItemGoblinClothesMixin).
 * This modifier gives the same to chestplates
 **/
public class ModifierMogsQuiver extends ArmorModifierTrait {

    public ModifierMogsQuiver() {
        super(WitcheryCompanion.prefix("mogs_quiver"), 0x8c6a3d);
        addAspects(new ModifierAspect.DataAspect(this), new ModifierAspect.SingleAspect(this));
    }

    @Override
    public boolean canApplyCustom(ItemStack stack) {
        return EntityLiving.getSlotForItemStack(stack) == EntityEquipmentSlot.CHEST &&
                super.canApplyCustom(stack);
    }
}
