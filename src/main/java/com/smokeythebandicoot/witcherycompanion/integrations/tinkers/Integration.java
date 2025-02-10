package com.smokeythebandicoot.witcherycompanion.integrations.tinkers;


import c4.conarm.lib.materials.CoreMaterialStats;
import c4.conarm.lib.materials.PlatesMaterialStats;
import c4.conarm.lib.materials.TrimMaterialStats;
import c4.conarm.lib.utils.RecipeMatchHolder;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.modifiers.ModifierHoming;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.modifiers.ModifierSeeping;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.traits.*;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.init.items.WitcheryBrewItems;
import net.msrandom.witchery.init.items.WitcheryFumeItems;
import net.msrandom.witchery.init.items.WitcheryGeneralItems;
import net.msrandom.witchery.init.items.WitcheryIngredientItems;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.*;
import slimeknights.tconstruct.library.modifiers.Modifier;



public class Integration {

    public static final Integration INSTANCE;

    private Integration() { }

    static {
        INSTANCE = new Integration();
    }

    /** ========== TINKERS CONSTUCT ========== **/
    // Materials
    public static Material KOBOLDITE;
    public static Material IMPREGNATED_LEATHER;
    public static Material NULLIFIED_LEATHER;
    public static Material WOVEN_CRUOR;
    public static Material SILVER;

    // Traits
    public static final TraitBabasBless TRAIT_BABAS_BLESS = new TraitBabasBless();
    public static final TraitBarked TRAIT_BARKED = new TraitBarked();
    public static final TraitDemonrend TRAIT_DEMONREND = new TraitDemonrend();
    public static final TraitGarliced TRAIT_GARLICED = new TraitGarliced();
    public static final TraitGoblinsFavor TRAIT_GOBLINS_FAVOR = new TraitGoblinsFavor();
    public static final TraitNecromancer TRAIT_NECROMANCER = new TraitNecromancer();
    public static final TraitSilvered TRAIT_SILVERED = new TraitSilvered();
    public static final TraitWitchClothing TRAIT_WITCH_CLOTHING = new TraitWitchClothing();
    public static final TraitWitchVeil TRAIT_WITCH_VEIL = new TraitWitchVeil();
    public static final TraitWolfsbane TRAIT_WOLFSBANE = new TraitWolfsbane();

    // Modifiers
    public static Modifier MODIFIER_SEEPING;
    public static Modifier MODIFIER_HOMING;


    public static void ticPreInit() {
        KOBOLDITE = new Material("koboldite", 0x61aadf);
        KOBOLDITE.addItem(WitcheryIngredientItems.KOBOLDITE_INGOT, 1, Material.VALUE_Ingot);
        KOBOLDITE.addItem("ingotKoboldite", 1, Material.VALUE_Ingot);
        KOBOLDITE.setCraftable(false);
        KOBOLDITE.setCastable(true);
        KOBOLDITE.setRepresentativeItem(WitcheryIngredientItems.KOBOLDITE_INGOT);
        KOBOLDITE.addTrait(TRAIT_GOBLINS_FAVOR);
        KOBOLDITE.setRenderInfo(0x61aadf);
        TinkerRegistry.addMaterialStats(
                KOBOLDITE,
                new HeadMaterialStats(260, 6.2f, 3.7f, 2),
                new HandleMaterialStats(0.86f, 65),
                new ExtraMaterialStats(55),
                new BowMaterialStats(2.2f, 1.6f, 7)
        );

        IMPREGNATED_LEATHER = new Material("impregnated_leather", 0x49271a);
        IMPREGNATED_LEATHER.addItem(WitcheryIngredientItems.IMPREGNATED_LEATHER, 1, Material.VALUE_Ingot);
        IMPREGNATED_LEATHER.setCraftable(true);
        IMPREGNATED_LEATHER.setCastable(false);
        IMPREGNATED_LEATHER.setRepresentativeItem(WitcheryIngredientItems.IMPREGNATED_LEATHER);
        TinkerRegistry.addMaterialStats(
                IMPREGNATED_LEATHER,
                new HeadMaterialStats(260, 6.2f, 3.7f, 2),
                new HandleMaterialStats(0.86f, 65),
                new ExtraMaterialStats(55),
                new BowMaterialStats(2.2f, 1.6f, 7)
        );

        //TinkerRegistry.addTrait(TRAIT_BARKED);
        //TinkerRegistry.addTrait(TRAIT_WITCH_CLOTHING);
        //TinkerRegistry.addTrait(TRAIT_WITCH_VEIL);

        TinkerRegistry.integrate(KOBOLDITE).preInit();
        TinkerRegistry.integrate(IMPREGNATED_LEATHER).preInit();

    }

    public static void ticInit() {

    }

    public static void conarmPreInit() {

        //TRAIT_BABAS_BLESS.addItem(WitcheryEquipmentItems.BABAS_HAT, 1, 1);
        //TRAIT_DEMONREND.addItem(new ItemStack(WitcheryBlocks.DEMON_HEART), 1, 1);
        //TRAIT_GARLICED.addItem(new ItemStack(WitcheryBlocks.GARLIC), 64, 1);
        //TRAIT_SILVERED.addItem("dustSilver", 64, 1);
        //TRAIT_NECROMANCER.addItem(WitcheryIngredientItems.CREEPER_HEART, 1, 1);

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
    }

}
