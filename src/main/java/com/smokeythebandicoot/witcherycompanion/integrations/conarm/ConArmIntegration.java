package com.smokeythebandicoot.witcherycompanion.integrations.conarm;

import c4.conarm.lib.materials.ArmorMaterialType;
import c4.conarm.lib.materials.CoreMaterialStats;
import c4.conarm.lib.materials.PlatesMaterialStats;
import c4.conarm.lib.materials.TrimMaterialStats;
import c4.conarm.lib.utils.RecipeMatchHolder;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.Integration;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.modifiers.SlayerType;
import com.smokeythebandicoot.witcherycompanion.integrations.conarm.modifiers.*;
import com.smokeythebandicoot.witcherycompanion.integrations.conarm.traits.*;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.init.WitcheryBlocks;
import net.msrandom.witchery.init.items.*;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.modifiers.Modifier;

/**
 * Construct Armory integration: armor materials, traits and modifiers. Everything that needs Construct Armory is in
 * this package (and its mixins in mixins.witcherycompanion.conarm.json), as it is only loaded when it is installed.
 * Tinkers Construct is a hard dependency of Construct Armory, so the Tinkers integration can be used from here, but
 * never the other way around. Nothing is instantiated in static initializers
 **/
public class ConArmIntegration {

    // Materials
    public static Material IMPREGNATED_LEATHER;
    public static Material NULLIFIED_LEATHER;
    public static Material WOVEN_CRUOR;

    // Traits
    public static TraitWitchVeil TRAIT_WITCH_VEIL;
    public static TraitBrewAffinity TRAIT_BREW_AFFINITY;
    public static TraitNullifying TRAIT_NULLIFYING;
    public static TraitBarkable TRAIT_BARKABLE;
    public static TraitGoblinsFavor TRAIT_GOBLINS_FAVOR;
    public static TraitVampiric TRAIT_VAMPIRIC;

    // Modifiers
    public static Modifier MODIFIER_SEEPING;
    public static Modifier MODIFIER_HOMING;
    public static Modifier MODIFIER_BABAS_BLESS;
    public static Modifier MODIFIER_CREEPER_REPELLENT;
    public static Modifier MODIFIER_UNDEAD_REPELLENT;
    public static Modifier MODIFIER_SILVERED;
    public static Modifier MODIFIER_GARLICED;
    public static Modifier MODIFIER_DEMONREND;
    public static Modifier MODIFIER_BARKED;
    public static Modifier MODIFIER_MOGS_QUIVER;

    /** Must be called after the Tinkers integration pre-init, as it adds armor stats to its materials **/
    public static void preInit() {

        TRAIT_WITCH_VEIL = new TraitWitchVeil();
        TRAIT_BREW_AFFINITY = new TraitBrewAffinity();
        TRAIT_NULLIFYING = new TraitNullifying();
        TRAIT_BARKABLE = new TraitBarkable();
        TRAIT_GOBLINS_FAVOR = new TraitGoblinsFavor();
        TRAIT_VAMPIRIC = new TraitVampiric();

        // Koboldite: armor stats and the armor version of Goblin's Favor
        if (Integration.KOBOLDITE != null) {
            addArmorTrait(Integration.KOBOLDITE, TRAIT_GOBLINS_FAVOR);
            TinkerRegistry.addMaterialStats(
                    Integration.KOBOLDITE,
                    new CoreMaterialStats(16, 12),
                    new TrimMaterialStats(7.0f),
                    new PlatesMaterialStats(1.2f, 6.0f, 1.4f)
            );
        }

        // Very weak, but has some traits imbued
        IMPREGNATED_LEATHER = new Material("impregnated_leather", 0x49271a);
        IMPREGNATED_LEATHER.addItem(WitcheryIngredientItems.IMPREGNATED_LEATHER, 1, Material.VALUE_Ingot);
        IMPREGNATED_LEATHER.setCraftable(true);
        IMPREGNATED_LEATHER.setCastable(false);
        IMPREGNATED_LEATHER.setRepresentativeItem(WitcheryIngredientItems.IMPREGNATED_LEATHER);
        IMPREGNATED_LEATHER.addTrait(TRAIT_WITCH_VEIL);
        IMPREGNATED_LEATHER.addTrait(TRAIT_BREW_AFFINITY);
        IMPREGNATED_LEATHER.addTrait(TRAIT_BARKABLE);
        TinkerRegistry.addMaterialStats(
                IMPREGNATED_LEATHER,
                new CoreMaterialStats(16, 12),
                new TrimMaterialStats(7.0f),
                new PlatesMaterialStats(1.2f, 6.0f, 1.4f)
        );
        TinkerRegistry.integrate(IMPREGNATED_LEATHER).preInit();

        // Very durable, but all other stats are very low
        NULLIFIED_LEATHER = new Material("nullified_leather", 0x49271a);
        NULLIFIED_LEATHER.addItem(WitcheryIngredientItems.NULLIFIED_LEATHER, 1, Material.VALUE_Ingot);
        NULLIFIED_LEATHER.setCraftable(true);
        NULLIFIED_LEATHER.setCastable(false);
        NULLIFIED_LEATHER.setRepresentativeItem(WitcheryIngredientItems.NULLIFIED_LEATHER);
        NULLIFIED_LEATHER.addTrait(TRAIT_WITCH_VEIL);
        NULLIFIED_LEATHER.addTrait(TRAIT_BARKABLE);
        TinkerRegistry.addMaterialStats(
                NULLIFIED_LEATHER,
                new CoreMaterialStats(18, 13),
                new TrimMaterialStats(7.2f),
                new PlatesMaterialStats(1.3f, 6.4f, 1.4f)
        );
        TinkerRegistry.integrate(NULLIFIED_LEATHER).preInit();

        // Very durable, but all other stats are very low
        WOVEN_CRUOR = new Material("woven_cruor", 0x393737);
        WOVEN_CRUOR.addItem(WitcheryIngredientItems.DARK_CLOTH, 1, Material.VALUE_Ingot);
        WOVEN_CRUOR.setCraftable(true);
        WOVEN_CRUOR.setCastable(false);
        WOVEN_CRUOR.setRepresentativeItem(WitcheryIngredientItems.DARK_CLOTH);
        WOVEN_CRUOR.addTrait(TRAIT_BARKABLE);
        TinkerRegistry.addMaterialStats(
                WOVEN_CRUOR,
                new CoreMaterialStats(20, 10),
                new TrimMaterialStats(8.0f),
                new PlatesMaterialStats(1.3f, 6.5f, 1.0f)
        );
        TinkerRegistry.integrate(WOVEN_CRUOR).preInit();
    }

    public static void init() {

        MODIFIER_SEEPING = new ModifierSeeping();
        RecipeMatchHolder.addRecipeMatch(MODIFIER_SEEPING,
                new RecipeMatch.ItemCombination(1,
                        new ItemStack(WitcheryIngredientItems.GOLDEN_THREAD),
                        new ItemStack(WitcheryGeneralItems.WITCH_HAND),
                        new ItemStack(WitcheryBrewItems.REDSTONE_SOUP),
                        new ItemStack(Items.MILK_BUCKET)
                ));

        MODIFIER_HOMING = new ModifierHoming();
        RecipeMatchHolder.addRecipeMatch(MODIFIER_HOMING,
                new RecipeMatch.ItemCombination(1,
                        new ItemStack(WitcheryIngredientItems.ATTUNED_STONE),
                        new ItemStack(WitcheryIngredientItems.ATTUNED_STONE),
                        new ItemStack(WitcheryIngredientItems.GOLDEN_THREAD),
                        new ItemStack(WitcheryFumeItems.DEMONIC_BLOOD),
                        new ItemStack(WitcheryFumeItems.DEMONIC_BLOOD)
                )
        );

        MODIFIER_BABAS_BLESS = new ModifierBabasBless();
        RecipeMatchHolder.addRecipeMatch(MODIFIER_BABAS_BLESS,
                new RecipeMatch.Item(
                        new ItemStack(WitcheryEquipmentItems.BABAS_HAT), 1
                ));

        MODIFIER_CREEPER_REPELLENT = new ModifierCreeperRepellent();
        RecipeMatchHolder.addRecipeMatch(MODIFIER_CREEPER_REPELLENT,
                new RecipeMatch.Item(
                        new ItemStack(WitcheryIngredientItems.CREEPER_HEART), 1
                ));

        MODIFIER_UNDEAD_REPELLENT = new ModifierUndeadRepellent();
        RecipeMatchHolder.addRecipeMatch(MODIFIER_UNDEAD_REPELLENT,
                new RecipeMatch.Item(
                        new ItemStack(WitcheryIngredientItems.NECRO_STONE), 1
                ));

        MODIFIER_SILVERED = new ModifierSlayer(WitcheryCompanion.prefix("silvered"), 0xe1e1e1, 1, 0, SlayerType.SILVERED);
        RecipeMatchHolder.addRecipeMatch(MODIFIER_SILVERED,
                new RecipeMatch.Item(
                        new ItemStack(WitcheryIngredientItems.SILVER_DUST), 1
                ));

        MODIFIER_GARLICED = new ModifierSlayer(WitcheryCompanion.prefix("garliced"), 0xf0efd5, 1, 0, SlayerType.GARLICED);
        RecipeMatchHolder.addRecipeMatch(MODIFIER_GARLICED,
                new RecipeMatch.Item(
                        new ItemStack(WitcheryBlocks.GARLIC), 1
                ));

        MODIFIER_DEMONREND = new ModifierSlayer(WitcheryCompanion.prefix("demonrend"), 0xdb2100, 1, 1, SlayerType.DEMONREND);
        RecipeMatchHolder.addRecipeMatch(MODIFIER_DEMONREND,
                new RecipeMatch.Item(
                        new ItemStack(WitcheryBlocks.DEMON_HEART), 1
                ));

        MODIFIER_BARKED = new ModifierBarked();
        RecipeMatchHolder.addRecipeMatch(MODIFIER_BARKED,
                new RecipeMatch.ItemCombination(1,
                        new ItemStack(Items.EMERALD),
                        new ItemStack(WitcheryBrewItems.FLOWING_SPIRIT_BREW),
                        new ItemStack(WitcheryBrewItems.FLOWING_SPIRIT_BREW),
                        new ItemStack(WitcheryIngredientItems.ENT_TWIG),
                        new ItemStack(WitcheryIngredientItems.CREEPER_HEART)
                ));

        // Mog's Quiver can only be obtained from Mog, so the modifier consumes it
        MODIFIER_MOGS_QUIVER = new ModifierMogsQuiver();
        RecipeMatchHolder.addRecipeMatch(MODIFIER_MOGS_QUIVER,
                new RecipeMatch.Item(
                        new ItemStack(WitcheryEquipmentItems.MOGS_QUIVER), 1
                ));

        // Silver armor is silvered: the material grants the modifier as a trait
        if (Integration.SILVER != null && MODIFIER_SILVERED instanceof ModifierSlayer) {
            addArmorTrait(Integration.SILVER, (ModifierSlayer) MODIFIER_SILVERED);
        }
    }

    /** Adds a trait to the armor parts of a material only (the tool parts have their own traits) **/
    private static void addArmorTrait(Material material, slimeknights.tconstruct.library.traits.ITrait trait) {
        material.addTrait(trait, ArmorMaterialType.CORE);
        material.addTrait(trait, ArmorMaterialType.PLATES);
        material.addTrait(trait, ArmorMaterialType.TRIM);
    }
}
