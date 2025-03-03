package com.smokeythebandicoot.witcherycompanion.integrations.tinkers;


import c4.conarm.lib.materials.CoreMaterialStats;
import c4.conarm.lib.materials.PlatesMaterialStats;
import c4.conarm.lib.materials.TrimMaterialStats;
import c4.conarm.lib.modifiers.ArmorModifierTrait;
import c4.conarm.lib.utils.RecipeMatchHolder;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.modifiers.*;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.traits.*;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.init.WitcheryBlocks;
import net.msrandom.witchery.init.items.*;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.*;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;


public class Integration {

    /** ========== CONFIG ========== **/
    private static final HashSet<String> earthInfusionMaterialBlacklist = new HashSet<>();

    public static void reloadEarthInfusionDisarmableMaterials() {
        earthInfusionMaterialBlacklist.clear();
        earthInfusionMaterialBlacklist.addAll(Arrays.asList(
                ModConfig.IntegrationConfigurations.TinkersIntegration.EarthInfusionConfig.earthInfusion_blacklistMaterials)
        );
    }

    /** ========== TINKERS CONSTUCT ========== **/
    // Materials
    public static Material KOBOLDITE;
    public static Material IMPREGNATED_LEATHER;
    public static Material NULLIFIED_LEATHER;
    public static Material WOVEN_CRUOR;
    public static Material SILVER;

    // Traits
    public static final TraitWitchVeil TRAIT_WITCH_VEIL = new TraitWitchVeil();
    public static final TraitBrewAffinity TRAIT_BREW_AFFINITY = new TraitBrewAffinity();
    public static final TraitNullifying TRAIT_NULLIFYING = new TraitNullifying();
    public static final TraitBarkable TRAIT_BARKABLE = new TraitBarkable();
    public static final TraitGoblinsFavor TRAIT_GOBLINS_FAVOR = new TraitGoblinsFavor();
    public static final TraitVampiric TRAIT_VAMPIRIC = new TraitVampiric();


    // Modifiers
    public static Modifier MODIFIER_SEEPING;
    public static Modifier MODIFIER_HOMING;
    public static Modifier MODIFIER_BABAS_BLESS;
    public static Modifier MODIFIER_CREEPER_REPELLENT;
    public static Modifier MODIFIER_UNDEAD_REPELLENT;
    public static Modifier MODIFIER_SILVERED;
    public static Modifier MODIFIER_GARLICED;
    public static Modifier MODIFIER_WOLFSBANE;
    public static Modifier MODIFIER_DEMONREND;
    public static Modifier MODIFIER_BARKED;


    public static void ticPreInit() {

        // Similar to IRON, but more durable
        KOBOLDITE = new Material("koboldite", 0x61aadf);
        KOBOLDITE.addItem(WitcheryIngredientItems.KOBOLDITE_INGOT, 1, Material.VALUE_Ingot);
        KOBOLDITE.addItem("ingotKoboldite", 1, Material.VALUE_Ingot);
        KOBOLDITE.setCraftable(false);
        KOBOLDITE.setCastable(true);
        KOBOLDITE.setRepresentativeItem(WitcheryIngredientItems.KOBOLDITE_INGOT);
        KOBOLDITE.addTrait(TRAIT_GOBLINS_FAVOR);
        TinkerRegistry.addMaterialStats(
                KOBOLDITE,
                new HeadMaterialStats(260, 6.2f, 3.7f, 2),
                new HandleMaterialStats(0.86f, 65),
                new ExtraMaterialStats(55),
                new BowMaterialStats(2.2f, 1.6f, 7)
        );
        TinkerRegistry.integrate(KOBOLDITE).preInit();

        // Very weak, but has some traits imbued
        IMPREGNATED_LEATHER = new Material("impregnated_leather", 0x49271a);
        IMPREGNATED_LEATHER.addItem(WitcheryIngredientItems.IMPREGNATED_LEATHER, 1, Material.VALUE_Ingot);
        IMPREGNATED_LEATHER.setCraftable(true);
        IMPREGNATED_LEATHER.setCastable(false);
        IMPREGNATED_LEATHER.setRepresentativeItem(WitcheryIngredientItems.IMPREGNATED_LEATHER);
        /*TinkerRegistry.addMaterialStats(
                IMPREGNATED_LEATHER,
                new HeadMaterialStats(260, 6.2f, 3.7f, 0),
                new HandleMaterialStats(0.86f, 65),
                new ExtraMaterialStats(55),
                new BowMaterialStats(2.2f, 1.6f, 7)
        );*/
        IMPREGNATED_LEATHER.addTrait(TRAIT_WITCH_VEIL);
        IMPREGNATED_LEATHER.addTrait(TRAIT_BREW_AFFINITY);
        IMPREGNATED_LEATHER.addTrait(TRAIT_BARKABLE);
        TinkerRegistry.integrate(IMPREGNATED_LEATHER).preInit();

        // Very durable, but all other stats are very low
        NULLIFIED_LEATHER = new Material("nullified_leather", 0x49271a);
        NULLIFIED_LEATHER.addItem(WitcheryIngredientItems.NULLIFIED_LEATHER, 1, Material.VALUE_Ingot);
        NULLIFIED_LEATHER.setCraftable(true);
        NULLIFIED_LEATHER.setCastable(false);
        NULLIFIED_LEATHER.setRepresentativeItem(WitcheryIngredientItems.NULLIFIED_LEATHER);
        NULLIFIED_LEATHER.addTrait(TRAIT_WITCH_VEIL);
        NULLIFIED_LEATHER.addTrait(TRAIT_BARKABLE);
        /*TinkerRegistry.addMaterialStats(
                NULLIFIED_LEATHER,
                new HeadMaterialStats(340, 5.5f, 2.9f, 0),
                new HandleMaterialStats(0.87f, 75),
                new ExtraMaterialStats(62),
                new BowMaterialStats(2.6f, 1.3f, 4)
        );*/
        TinkerRegistry.integrate(NULLIFIED_LEATHER).preInit();

        // Very durable, but all other stats are very low
        WOVEN_CRUOR = new Material("woven_cruor", 0x393737);
        WOVEN_CRUOR.addItem(WitcheryIngredientItems.DARK_CLOTH, 1, Material.VALUE_Ingot);
        WOVEN_CRUOR.setCraftable(true);
        WOVEN_CRUOR.setCastable(false);
        WOVEN_CRUOR.setRepresentativeItem(WitcheryIngredientItems.DARK_CLOTH);
        WOVEN_CRUOR.addTrait(TRAIT_BARKABLE);
        /*TinkerRegistry.addMaterialStats(
                WOVEN_CRUOR,
                new HeadMaterialStats(360, 5.8f, 3.1f, 0),
                new HandleMaterialStats(0.87f, 76),
                new ExtraMaterialStats(64),
                new BowMaterialStats(2.3f, 1.45f, 5)
        );*/
        TinkerRegistry.integrate(WOVEN_CRUOR).preInit();

        if (TinkerRegistry.getMaterial("silver") == null) {
            SILVER = new Material("silver", 0xdbdadf);
            SILVER.addItem(WitcheryIngredientItems.SILVER_DUST, 1, Material.VALUE_Ingot);
            SILVER.addItem("ingotSilver", 1, Material.VALUE_Ingot);
            SILVER.setCraftable(false);
            SILVER.setCastable(true);
            SILVER.setRepresentativeItem(WitcheryIngredientItems.SILVER_DUST);
            TinkerRegistry.addMaterialStats(
                    KOBOLDITE,
                    new HeadMaterialStats(232, 6.4f, 3.9f, 1),
                    new HandleMaterialStats(0.9f, 68),
                    new ExtraMaterialStats(60),
                    new BowMaterialStats(2.1f, 1.7f, 8)
            );
            TinkerRegistry.integrate(SILVER).preInit();
        }
        else {
            SILVER = TinkerRegistry.getMaterial("silver");
        }

    }

    public static void ticInit() {

    }

    public static void conarmPreInit() {

        TinkerRegistry.addMaterialStats(
                KOBOLDITE,
                new CoreMaterialStats(16, 12), //TODO: check if ConArm is not installed
                new TrimMaterialStats(7.0f),
                new PlatesMaterialStats(1.2f, 6.0f, 1.4f)
        );

        TinkerRegistry.addMaterialStats(
                IMPREGNATED_LEATHER,
                new CoreMaterialStats(16, 12),
                new TrimMaterialStats(7.0f),
                new PlatesMaterialStats(1.2f, 6.0f, 1.4f)
        );

        TinkerRegistry.addMaterialStats(
                NULLIFIED_LEATHER,
                new CoreMaterialStats(18, 13),
                new TrimMaterialStats(7.2f),
                new PlatesMaterialStats(1.3f, 6.4f, 1.4f)
        );

        TinkerRegistry.addMaterialStats(
                WOVEN_CRUOR,
                new CoreMaterialStats(20, 10),
                new TrimMaterialStats(8.0f),
                new PlatesMaterialStats(1.3f, 6.5f, 1.0f)
        );

    }

    public static void conarmInit() {
        MODIFIER_SEEPING  = new ModifierSeeping();
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

        MODIFIER_SILVERED = new ModifierSilvered();
        RecipeMatchHolder.addRecipeMatch(MODIFIER_SILVERED,
                new RecipeMatch.Item(
                        new ItemStack(WitcheryIngredientItems.SILVER_DUST), 1
                ));

        MODIFIER_GARLICED = new ModifierGarliced();
        RecipeMatchHolder.addRecipeMatch(MODIFIER_GARLICED,
                new RecipeMatch.Item(
                        new ItemStack(WitcheryBlocks.GARLIC), 1
                ));

        MODIFIER_WOLFSBANE = new ModifierWolfsbane();
        RecipeMatchHolder.addRecipeMatch(MODIFIER_WOLFSBANE,
                new RecipeMatch.Item(
                        new ItemStack(WitcheryIngredientItems.WOLFSBANE), 16
                ));

        MODIFIER_DEMONREND = new ModifierDemonrend();
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

        // Add Silvered Modifier
        Material silver = TinkerRegistry.getMaterial("silver");
        if (silver != null) {
            silver.addTrait((ArmorModifierTrait)MODIFIER_SILVERED);
        }
    }


    /** Utils **/

    /** Returns true if any of the Materials that compose the Tinker armor/tool/weapon etc is made of a castable material (so metallic) **/
    public static boolean isMetalMaterial(ItemStack stack) {
        List<Material> materials = TinkerUtil.getMaterialsFromTagList(TagUtil.getBaseMaterialsTagList(stack));
        for (Material mat : materials) {
            if (mat.isCastable() && canMaterialBeDisarmed(mat.getIdentifier())) {
                return true;
            }
        }
        return false;
    }

    private static boolean canMaterialBeDisarmed(String materialId) {
        // Either is whitelist and contains, or is blacklist and does not contain in
        return ModConfig.IntegrationConfigurations.TinkersIntegration.EarthInfusionConfig.earthInfusion_earthInfusionIsWhitelist == earthInfusionMaterialBlacklist.contains(materialId);
    }

}
