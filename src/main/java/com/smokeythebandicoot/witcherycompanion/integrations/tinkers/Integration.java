package com.smokeythebandicoot.witcherycompanion.integrations.tinkers;


import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.modifiers.*;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.traits.TraitArmorBarked;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.traits.TraitArmorWitchClothing;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.traits.TraitArmorWitchVeil;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.tconstruct.modifiers.ModifierToolWolfsbane;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.tconstruct.traits.TraitToolSilvered;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.init.WitcheryBlocks;
import net.msrandom.witchery.init.items.WitcheryEquipmentItems;
import net.msrandom.witchery.init.items.WitcheryGeneralItems;
import net.msrandom.witchery.init.items.WitcheryIngredientItems;

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
    public static final TraitArmorBarked TRAIT_ARMOR_BARKED = new TraitArmorBarked();
    public static final TraitArmorWitchClothing TRAIT_ARMOR_WITCH_CLOTHING = new TraitArmorWitchClothing();
    public static final TraitArmorWitchVeil TRAIT_ARMOR_WITCH_VEIL = new TraitArmorWitchVeil();

    // Modifiers
    public static final ModifierArmorSilvered MODIFIER_ARMOR_SILVERED = new ModifierArmorSilvered();
    public static final ModifierArmorBabasBless MODIFIER_ARMOR_BABAS_BLESS = new ModifierArmorBabasBless();
    public static final ModifierArmorDemonrend MODIFIER_ARMOR_DEMONREND = new ModifierArmorDemonrend();
    public static final ModifierArmorGarliced MODIFIER_ARMOR_GARLICED = new ModifierArmorGarliced();
    public static final ModifierArmorNecromancer MODIFIER_ARMOR_NECROMANCER = new ModifierArmorNecromancer();


    public static void registerTinkers() {
        // TinkerRegistry.addMaterial();
    }

    public static void registerConarm() {
        MODIFIER_ARMOR_BABAS_BLESS.addItem(WitcheryEquipmentItems.BABAS_HAT, 1, 1);
        MODIFIER_ARMOR_DEMONREND.addItem(new ItemStack(WitcheryBlocks.DEMON_HEART), 1, 1);
        MODIFIER_ARMOR_GARLICED.addItem(new ItemStack(WitcheryBlocks.GARLIC), 64, 1);
        MODIFIER_ARMOR_SILVERED.addItem("dustSilver", 64, 1);
        MODIFIER_ARMOR_NECROMANCER.addItem(WitcheryIngredientItems.CREEPER_HEART, 1, 1);

    }
}
