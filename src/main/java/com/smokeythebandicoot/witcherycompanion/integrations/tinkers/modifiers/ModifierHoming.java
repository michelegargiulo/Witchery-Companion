package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.modifiers;

import c4.conarm.lib.modifiers.ArmorModifierTrait;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.traits.TraitDemonrend;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.traits.TraitSilvered;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.msrandom.witchery.util.CreatureUtil;
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
