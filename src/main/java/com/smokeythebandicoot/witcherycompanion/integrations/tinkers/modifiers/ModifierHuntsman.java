package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.modifiers;

import com.google.common.collect.Multimap;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.melee.item.BattleSign;

import java.util.UUID;

/**
 * Huntsman: gives a Battlesign the powers of the Spear of the Huntsman. Witchery's spear does nothing when thrown
 * (the Horned Huntsman throws his), its powers are the ones it has when held:
 * - Full knockback resistance in the main hand (here, as an attribute modifier)
 * - While blocking, a 25% chance when hit to summon an angry Wither wolf that attacks the attacker
 * - Hitting the Nightmare with it shortens the time it stays defended
 * The last two are handled by Hegemony (see TinkersEquivalents), as the Battlesign counts as the Spear of the Huntsman
 **/
public class ModifierHuntsman extends ModifierTrait {

    private static final UUID KNOCKBACK_RESISTANCE_UUID = UUID.fromString("6f0c4f0e-6a5b-4c7a-9b6e-3f2a8f1d7c41");

    public ModifierHuntsman() {
        super(WitcheryCompanion.prefix("huntsman"), 0x7a4b24, 1, 0);
    }

    @Override
    public boolean canApplyCustom(ItemStack stack) {
        return stack.getItem() instanceof BattleSign;
    }

    /** Same as the Spear of the Huntsman: complete knockback resistance while held in the main hand. Broken tools lose it **/
    @Override
    public void getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack, Multimap<String, AttributeModifier> attributeMap) {
        if (slot == EntityEquipmentSlot.MAINHAND && !ToolHelper.isBroken(stack)) {
            attributeMap.put(SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(),
                    new AttributeModifier(KNOCKBACK_RESISTANCE_UUID, "Huntsman knockback resist", 1.0d, 0));
        }
    }
}
