package com.smokeythebandicoot.witcherycompanion.integrations.tinkers;


import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.modifiers.ModifierArmorBabasBless;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.modifiers.ModifierArmorDemonrend;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.modifiers.ModifierArmorGarliced;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.traits.TraitArmorWitchVeil;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.traits.TraitArmorSilvered;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.tconstruct.modifiers.ModifierToolWolfsbane;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.tconstruct.traits.TraitToolSilvered;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.init.WitcheryBlocks;
import net.msrandom.witchery.init.items.WitcheryEquipmentItems;
import slimeknights.tconstruct.library.utils.TinkerUtil;

public class Integration {

    public static final Integration INSTANCE;

    private Integration() { }

    static {
        INSTANCE = new Integration();
    }

    /** ========== TINKERS CONSTUCT ========== **/
    // Tools
    public static final TraitToolSilvered TRAIT_TOOL_SILVERED = new TraitToolSilvered();

    // Modifiers
    public static final ModifierToolWolfsbane MODIFIER_TOOL_WOLFSBANE = new ModifierToolWolfsbane();

    /** ========== CONSTRUCT ARMORY ========== **/
    // Traits
    public static final TraitArmorSilvered TRAIT_ARMOR_SILVERED = new TraitArmorSilvered();
    public static final TraitArmorWitchVeil TRAIT_ARMOR_WITCH_VEIL = new TraitArmorWitchVeil();

    // Modifiers
    public static final ModifierArmorBabasBless MODIFIER_ARMOR_BABAS_BLESS = new ModifierArmorBabasBless();
    public static final ModifierArmorDemonrend MODIFIER_ARMOR_DEMONREND = new ModifierArmorDemonrend();
    public static final ModifierArmorGarliced MODIFIER_ARMOR_GARLICED = new ModifierArmorGarliced();


    public static boolean hasTrait(ItemStack stack, String traitName) {
        if (stack == null || stack.getTagCompound() == null || traitName == null) return false;
        return TinkerUtil.hasTrait(stack.getTagCompound(), traitName);
    }

    public static boolean hasTrait(ItemStack stack, ECompanionTrait trait) {
        return hasTrait(stack, trait.getId());
    }


    public static void registerMaterials() {
        // TinkerRegistry.addMaterial();
    }

    public static void registerModifiers() {
        MODIFIER_ARMOR_BABAS_BLESS.addItem(WitcheryEquipmentItems.BABAS_HAT, 1, 1);
        MODIFIER_ARMOR_DEMONREND.addItem(new ItemStack(WitcheryBlocks.DEMON_HEART), 1, 1);
        MODIFIER_ARMOR_GARLICED.addItem(new ItemStack(WitcheryBlocks.GARLIC), 64, 1);

    }
}
