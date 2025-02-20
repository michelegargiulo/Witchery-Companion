package com.smokeythebandicoot.witcherycompanion.api;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.InfusionTweaks;
import com.smokeythebandicoot.witcherycompanion.utils.Mods;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.msrandom.witchery.init.WitcheryBlocks;
import net.msrandom.witchery.init.items.WitcheryEquipmentItems;
import net.msrandom.witchery.init.items.WitcheryGeneralItems;

import java.util.HashSet;


public class EarthInfusionApi {

    private static HashSet<ItemStack> earthItems;

    private static void initEarthItems() {
        earthItems = new HashSet<>();

        // Swords
        addMetalItem(Items.IRON_SWORD);
        addMetalItem(Items.GOLDEN_SWORD);

        // Tools
        addMetalItem(Items.IRON_PICKAXE);
        addMetalItem(Items.GOLDEN_PICKAXE);
        addMetalItem(Items.IRON_AXE);
        addMetalItem(Items.GOLDEN_AXE);
        addMetalItem(Items.IRON_SHOVEL);
        addMetalItem(Items.GOLDEN_SHOVEL);
        addMetalItem(Items.IRON_HOE);
        addMetalItem(Items.GOLDEN_HOE);

        // Chainmail, Iron and Gold armor
        addMetalItem(Items.CHAINMAIL_BOOTS);
        addMetalItem(Items.CHAINMAIL_LEGGINGS);
        addMetalItem(Items.CHAINMAIL_CHESTPLATE);
        addMetalItem(Items.CHAINMAIL_HELMET);
        addMetalItem(Items.IRON_BOOTS);
        addMetalItem(Items.IRON_LEGGINGS);
        addMetalItem(Items.IRON_CHESTPLATE);
        addMetalItem(Items.IRON_HELMET);
        addMetalItem(Items.GOLDEN_BOOTS);
        addMetalItem(Items.GOLDEN_LEGGINGS);
        addMetalItem(Items.GOLDEN_CHESTPLATE);
        addMetalItem(Items.GOLDEN_HELMET);

        if (InfusionTweaks.earthInfusion_tweakAttractExtraItems) {

            // Other items
            addMetalItem(Items.SHEARS);
            addMetalItem(Items.SHIELD);
            addMetalItem(Items.BUCKET);
            addMetalItem(Items.MILK_BUCKET);
            addMetalItem(Items.WATER_BUCKET);
            addMetalItem(Items.LAVA_BUCKET);
            addMetalItem(Items.FLINT_AND_STEEL);
            addMetalItem(Items.COMPASS);
            addMetalItem(Items.CLOCK);
            addMetalItem(Items.IRON_HORSE_ARMOR);
            addMetalItem(Items.GOLDEN_HORSE_ARMOR);

            // Minecarts (intentionally missing Command Block minecart)
            addMetalItem(Items.MINECART);
            addMetalItem(Items.CHEST_MINECART);
            addMetalItem(Items.TNT_MINECART);
            addMetalItem(Items.HOPPER_MINECART);
            addMetalItem(Items.FURNACE_MINECART);

            // Blocks, ingots and nuggets
            addMetalItem(new ItemStack(Blocks.IRON_BLOCK));
            addMetalItem(new ItemStack(Blocks.GOLD_BLOCK));
            addMetalItem(Items.IRON_INGOT);
            addMetalItem(Items.IRON_NUGGET);
            addMetalItem(Items.GOLD_INGOT);
            addMetalItem(Items.GOLD_NUGGET);

            // Other blocks
            addMetalItem(new ItemStack(Blocks.IRON_BARS));
            addMetalItem(new ItemStack(Blocks.IRON_DOOR));
            addMetalItem(new ItemStack(Blocks.IRON_TRAPDOOR));
            addMetalItem(new ItemStack(Blocks.ANVIL));
            addMetalItem(new ItemStack(Blocks.ANVIL, 1, 1));
            addMetalItem(new ItemStack(Blocks.ANVIL, 1, 2));
            addMetalItem(new ItemStack(Blocks.HOPPER));
            addMetalItem(Items.CAULDRON);

            // Rails
            addMetalItem(new ItemStack(Blocks.RAIL));
            addMetalItem(new ItemStack(Blocks.GOLDEN_RAIL));
            addMetalItem(new ItemStack(Blocks.ACTIVATOR_RAIL));
            addMetalItem(new ItemStack(Blocks.DETECTOR_RAIL));

            // Witchery Items
            addMetalItem(WitcheryGeneralItems.BOLINE);
            addMetalItem(WitcheryGeneralItems.HUNTSMANS_SPEAR);
            addMetalItem(WitcheryGeneralItems.CROSSBOW_PISTOL);
            addMetalItem(WitcheryGeneralItems.DRAWN_CANE_SWORD);
            addMetalItem(WitcheryGeneralItems.SHEATHED_CANE_SWORD);
            addMetalItem(WitcheryGeneralItems.CIRCLE_TALISMAN);
            addMetalItem(WitcheryGeneralItems.MOON_CHARM);
            addMetalItem(WitcheryGeneralItems.DEPLOYED_DEATH_HAND);
            addMetalItem(WitcheryEquipmentItems.VAMPIRE_HELMET);
            addMetalItem(WitcheryEquipmentItems.VAMPIRE_CHESTPLATE);
            addMetalItem(WitcheryEquipmentItems.VAMPIRE_LEGGINGS);
            addMetalItem(WitcheryEquipmentItems.VAMPIRE_BOOTS);
            addMetalItem(WitcheryGeneralItems.getArthana());

            // Witchery Blocks
            addMetalItem(new ItemStack(WitcheryBlocks.KETTLE));
            addMetalItem(new ItemStack(WitcheryBlocks.BRAZIER));
            addMetalItem(new ItemStack(WitcheryBlocks.WITCHES_OVEN));
            addMetalItem(new ItemStack(WitcheryBlocks.FUME_FUNNEL));
            addMetalItem(new ItemStack(WitcheryBlocks.FILTERED_FUME_FUNNEL));
            addMetalItem(new ItemStack(WitcheryBlocks.DISTILLERY));
            addMetalItem(new ItemStack(WitcheryBlocks.CAULDRON));
            addMetalItem(new ItemStack(WitcheryBlocks.SILVER_VAT));
            addMetalItem(new ItemStack(WitcheryBlocks.BEARTRAP));
            addMetalItem(new ItemStack(WitcheryBlocks.SUN_COLLECTOR));
            addMetalItem(new ItemStack(WitcheryBlocks.BLOOD_CRUCIBLE));
        }
    }

    public static void addMetalItem(ItemStack stack) {
        ItemStack s = stack.copy();
        s.setCount(1);
        earthItems.add(s);
    }

    public static void addMetalItem(Item item, int meta) {
        earthItems.add(new ItemStack(item, 1, meta));
    }

    public static void addMetalItem(Item item) {
        addMetalItem(new ItemStack(item, 1, 0));
    }

    public static void removeMetalItem(ItemStack stack) {
        ItemStack s = stack.copy();
        s.setCount(1);
        earthItems.remove(stack);
    }

    public static void removeMetalItem(Item item, int meta) {
        earthItems.remove(new ItemStack(item, 1, meta));
    }

    public static boolean isMetalItem(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return false;
        return earthItems.contains(stack);
    }

}