package com.smokeythebandicoot.witcherycompanion.integrations.tinkers;

import com.smokeythebandicoot.witcherycompanion.api.HegemonyApi;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.modifiers.ModifierSlayer;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.modifiers.ModifierWolfsbane;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.modifiers.SlayerType;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.tools.TinkersTools;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.tools.TinkersToolsClient;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.tools.TinkersToolsHandler;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.traits.TraitGoblinsFavor;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.msrandom.witchery.init.WitcheryBlocks;
import net.msrandom.witchery.init.items.WitcheryIngredientItems;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.BowMaterialStats;
import slimeknights.tconstruct.library.materials.ExtraMaterialStats;
import slimeknights.tconstruct.library.materials.HandleMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Tinkers Construct integration. This class (and everything in this package) must NOT reference Construct Armory in
 * any way, as Tinkers Construct can be installed without it. The armor part is in the conarm integration package.
 * Nothing is instantiated in static initializers, so just loading this class has no side effects
 **/
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
    public static Material SILVER;

    // Traits
    public static TraitGoblinsFavor TRAIT_GOBLINS_FAVOR;

    // Modifiers (weapons). The armor ones are in the ConArm integration
    public static Modifier MODIFIER_SILVERED;
    public static Modifier MODIFIER_GARLICED;
    public static Modifier MODIFIER_WOLFSBANE;
    public static Modifier MODIFIER_DEMONREND;

    public static void ticPreInit() {

        // Tinkers tools that are the equivalent of Witchery items (Arthana, Boline)
        MinecraftForge.EVENT_BUS.register(new TinkersToolsHandler());
        if (FMLCommonHandler.instance().getSide().isClient()) {
            MinecraftForge.EVENT_BUS.register(new TinkersToolsClient());
        }

        TRAIT_GOBLINS_FAVOR = new TraitGoblinsFavor();

        // Similar to IRON, but more durable
        KOBOLDITE = new Material("koboldite", 0x61aadf);
        KOBOLDITE.addItem(WitcheryIngredientItems.KOBOLDITE_INGOT, 1, Material.VALUE_Ingot);
        KOBOLDITE.addItem("ingotKoboldite", 1, Material.VALUE_Ingot);
        KOBOLDITE.setCraftable(false);
        KOBOLDITE.setCastable(true);
        KOBOLDITE.setRepresentativeItem(WitcheryIngredientItems.KOBOLDITE_INGOT);
        // Goblin's Favor is granted by the head only: a Koboldite Pickaxe is a pickaxe with a Koboldite head
        KOBOLDITE.addTrait(TRAIT_GOBLINS_FAVOR, MaterialTypes.HEAD);
        TinkerRegistry.addMaterialStats(
                KOBOLDITE,
                new HeadMaterialStats(260, 6.2f, 3.7f, 2),
                new HandleMaterialStats(0.86f, 65),
                new ExtraMaterialStats(55),
                new BowMaterialStats(2.2f, 1.6f, 7)
        );
        TinkerRegistry.integrate(KOBOLDITE).preInit();

        if (TinkerRegistry.getMaterial("silver") == null) {
            SILVER = new Material("silver", 0xdbdadf);
            SILVER.addItem(WitcheryIngredientItems.SILVER_DUST, 1, Material.VALUE_Ingot);
            SILVER.addItem("ingotSilver", 1, Material.VALUE_Ingot);
            SILVER.setCraftable(false);
            SILVER.setCastable(true);
            SILVER.setRepresentativeItem(WitcheryIngredientItems.SILVER_DUST);
            TinkerRegistry.addMaterialStats(
                    SILVER,
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

        // Tools can only be added to the Tool Station once they have been registered
        TinkersTools.registerCrafting();
        if (FMLCommonHandler.instance().getSide().isClient()) {
            TinkersToolsClient.registerToolBuildingInfo();
        }

        // Weapon modifiers
        MODIFIER_SILVERED = new ModifierSlayer(WitcheryCompanion.prefix("silvered"), 0xe1e1e1, 1, 0, SlayerType.SILVERED);
        MODIFIER_SILVERED.addRecipeMatch(new RecipeMatch.Item(new ItemStack(WitcheryIngredientItems.SILVER_DUST), 1));

        MODIFIER_GARLICED = new ModifierSlayer(WitcheryCompanion.prefix("garliced"), 0xf0efd5, 1, 0, SlayerType.GARLICED);
        MODIFIER_GARLICED.addRecipeMatch(new RecipeMatch.Item(new ItemStack(WitcheryBlocks.GARLIC), 1));

        MODIFIER_DEMONREND = new ModifierSlayer(WitcheryCompanion.prefix("demonrend"), 0xdb2100, 1, 1, SlayerType.DEMONREND);
        MODIFIER_DEMONREND.addRecipeMatch(new RecipeMatch.Item(new ItemStack(WitcheryBlocks.DEMON_HEART), 1));

        MODIFIER_WOLFSBANE = new ModifierWolfsbane();
        MODIFIER_WOLFSBANE.addRecipeMatch(new RecipeMatch.Item(new ItemStack(WitcheryIngredientItems.WOLFSBANE), 16));

        // Silver weapons are silvered: the head grants the modifier as a trait
        if (SILVER != null) {
            SILVER.addTrait((ModifierSlayer) MODIFIER_SILVERED, MaterialTypes.HEAD);
        }

        // Tinkers items that can be used in place of Witchery items
        TinkersEquivalents.registerDefaults();
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
