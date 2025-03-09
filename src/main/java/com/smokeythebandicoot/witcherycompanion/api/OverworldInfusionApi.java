package com.smokeythebandicoot.witcherycompanion.api;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.InfusionTweaks;
import com.smokeythebandicoot.witcherycompanion.utils.ComparableItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.msrandom.witchery.init.WitcheryBlocks;
import net.msrandom.witchery.init.items.WitcheryEquipmentItems;
import net.msrandom.witchery.init.items.WitcheryGeneralItems;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public class OverworldInfusionApi {

    // Metallic items
    private static HashSet<ComparableItemStack> earthItems;

    // Blocks and states that can be thrown
    private static HashSet<IBlockState> throwableStates;
    private static HashSet<Block> throwableBlocks;

    // Ore Transformation
    private static HashMap<IBlockState, OreTransformationInfo> oreTransforms;

    // Metal Entity Knockback
    private static HashSet<ResourceLocation> metalEntities;


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

        if (InfusionTweaks.overworldInfusion_tweakAttractExtraItems) {

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

    private static void initThrowables() {
        throwableStates = new HashSet<>();
        throwableBlocks = new HashSet<>();

        throwableBlocks.add(Blocks.DIRT);
        throwableBlocks.add(Blocks.GRASS);
        throwableBlocks.add(Blocks.STONE);
        throwableBlocks.add(Blocks.COBBLESTONE);
        throwableBlocks.add(Blocks.SAND);
        throwableBlocks.add(Blocks.GRAVEL);
        throwableBlocks.add(Blocks.SANDSTONE);
        throwableBlocks.add(Blocks.BRICK_BLOCK);
        throwableBlocks.add(Blocks.MOSSY_COBBLESTONE);
        throwableBlocks.add(Blocks.CLAY);
        throwableBlocks.add(Blocks.SOUL_SAND);
        throwableBlocks.add(Blocks.STONEBRICK);
        throwableBlocks.add(Blocks.MYCELIUM);
        throwableBlocks.add(Blocks.NETHER_BRICK);
        throwableBlocks.add(Blocks.HARDENED_CLAY);
        throwableBlocks.add(Blocks.COAL_BLOCK);
        throwableBlocks.add(Blocks.NETHERRACK);

        throwableBlocks.add(Blocks.STONE_SLAB);
        throwableBlocks.add(Blocks.STONE_SLAB2);            // Missing from Witchery

        throwableBlocks.add(Blocks.STONE_STAIRS);
        throwableBlocks.add(Blocks.STONE_BRICK_STAIRS);
        throwableBlocks.add(Blocks.BRICK_STAIRS);
        throwableBlocks.add(Blocks.SANDSTONE_STAIRS);
        throwableBlocks.add(Blocks.RED_SANDSTONE_STAIRS);   // Missing from Witchery
        throwableBlocks.add(Blocks.NETHER_BRICK_STAIRS);

    }

    private static void initOreTransforms() {
        oreTransforms = new HashMap<>();
        oreTransforms.put(Blocks.IRON_ORE.getDefaultState(), new OreTransformationInfo(new ItemStack(Items.IRON_INGOT)));
        oreTransforms.put(Blocks.GOLD_ORE.getDefaultState(), new OreTransformationInfo(new ItemStack(Items.GOLD_INGOT)));
    }

    private static void initMetalEntities() {
        metalEntities = new HashSet<>();

        if (InfusionTweaks.overworldInfusion_tweakKnockbackMetalEntities) {
            metalEntities.add(EntityList.getKey(EntityIronGolem.class));
        }
    }


    static {
        initEarthItems();
        initThrowables();
        initOreTransforms();
        initMetalEntities();
    }


    /** ========== METAL ITEMS ========== **/

    /** The ItemStack with the specified item (count is ignored) is considered metallic **/
    public static void addMetalItem(ItemStack stack) {
        earthItems.add(new ComparableItemStack(stack));
    }

    /** The ItemStack with the specified item is considered metallic **/
    public static void addMetalItem(Item item, int meta) {
        earthItems.add(new ComparableItemStack(item, meta));
    }

    /** The ItemStack with the specified item (meta 0) is considered metallic **/
    public static void addMetalItem(Item item) {
        addMetalItem(new ItemStack(item, 1, 0));
    }

    /** The ItemStack is not considered metallic **/
    public static void removeMetalItem(ItemStack stack) {
        ItemStack s = stack.copy();
        s.setCount(1);
        earthItems.remove(new ComparableItemStack(stack));
    }

    /** The ItemStack is not considered metallic **/
    public static void removeMetalItem(Item item, int meta) {
        earthItems.remove(new ComparableItemStack(item, meta));
    }

    /** Returns true if the ItemStack is considered made out of metal **/
    public static boolean isMetalItem(ItemStack stack) {
        if (stack == null || stack.isEmpty())
            return false;
        return earthItems.contains(new ComparableItemStack(stack));
    }


    /** ========== THROWABLE BLOCKS ========== **/

    /** The BlockState can be thrown as rock **/
    public static void addThrowable(IBlockState state) {
        throwableStates.add(state);
    }

    /** The Block can be thrown as rock **/
    public static void addThrowable(Block block) {
        throwableBlocks.add(block);
    }

    /** The IBlockState won't be thrown as rock **/
    public static void removeThrowable(IBlockState state) {
        throwableStates.remove(state);
    }

    /** The Block won't be thrown as rock **/
    public static void removeThrowable(Block block) {
        throwableBlocks.remove(block);
    }

    /** Returns true if the specified BlockState is throwable as a rock **/
    public static boolean isThrowable(IBlockState state) {
        if (state == null)
            return false;
        return throwableStates.contains(state) || throwableBlocks.contains(state.getBlock());
    }

    /** Returns true if the specified Block is throwable as a rock **/
    public static boolean isThrowable(Block block) {
        return throwableBlocks.contains(block);
    }


    /** ========== ORE TRANSFORMATIONS ========== **/

    /** The infusion will transform the specified state into leftover, while spitting result as Item Entity **/
    public static void addOreTransformation(IBlockState state, ItemStack resultStack, IBlockState leftover) {
        if (state != null && state.getBlock() != Blocks.AIR && resultStack != null && !resultStack.isEmpty() && leftover != null) {
            oreTransforms.put(state, new OreTransformationInfo(resultStack, leftover));
        }
    }

    /** The infusion will transform the specified state into Stone, while spitting result as Item Entity **/
    public static void addOreTransformation(IBlockState state, ItemStack resultStack) {
        if (state != null && state.getBlock() != Blocks.AIR && resultStack != null && !resultStack.isEmpty()) {
            oreTransforms.put(state, new OreTransformationInfo(resultStack, Blocks.STONE.getDefaultState()));
        }
    }

    /** The infusion will no longer have any effect on the specified state **/
    public static void removeOreTransformation(IBlockState state) {
        if (state != null) {
            oreTransforms.remove(state);
        }
    }

    /** Returns the ore transformation. Can be null **/
    public static OreTransformationInfo getOreTransformation(World world, BlockPos pos, IBlockState ore) {

        // If contained in map, returns the transformation
        if (oreTransforms.containsKey(ore)) {
            return oreTransforms.get(ore);
        }

        // Else, try to retrieve from fallbacks
        ItemStack oreStack = ore.getBlock().getItem(world, pos, ore);
        int[] oreIDs = OreDictionary.getOreIDs(oreStack);
        for (int oreID : oreIDs) {
            String oreName = OreDictionary.getOreName(oreID);
            if (oreName.startsWith("ore")) {
                for (String replacement : InfusionTweaks.overworldInfusion_tweakOreToIngotFallbacks) {
                    String resultName = oreName.replace("ore", replacement);
                    if (OreDictionary.doesOreNameExist(resultName)) {
                        List<ItemStack> resultStacks = OreDictionary.getOres(resultName);
                        if (!resultStacks.isEmpty()) {
                            return new OreTransformationInfo(resultStacks.get(0), Blocks.STONE.getDefaultState());
                        }
                    }
                }
            }
        }

        // Cannot transform
        return null;
    }


    public static class OreTransformationInfo {
        public final ItemStack target;
        public final IBlockState leftOver;

        public OreTransformationInfo(ItemStack target, IBlockState leftOver) {
            this.target = target;
            this.leftOver = leftOver;
        }

        public OreTransformationInfo(ItemStack target) {
            this(target, Blocks.STONE.getDefaultState());
        }
    }


    /** ========== METAL ENTITIES ========== **/

    /** The specified entity can always be knocked back, regardless of worn armor **/
    public static void addMetalEntity(EntityLivingBase base) {
        if (base != null) {
            ResourceLocation entityId = EntityList.getKey(base);
            if (entityId != null) {
                metalEntities.add(entityId);
            }
        }
    }

    /** The specified entity can always be knocked back, regardless of worn armor **/
    public static void addMetalEntity(ResourceLocation entityID) {
        metalEntities.add(entityID);
    }

    /** The specified entity won't be pushed back regardless of armor. Needs metallic armor to be pushed **/
    public static void removeMetalEntity(EntityLivingBase base) {
        if (base != null) {
            ResourceLocation entityId = EntityList.getKey(base);
            if (entityId != null) {
                metalEntities.remove(entityId);
            }
        }
    }

    /** The specified entity won't be pushed back regardless of armor. Needs metallic armor to be pushed **/
    public static void removeMetalEntity(ResourceLocation entityID) {
        metalEntities.remove(entityID);
    }

    /** Returns true if the entity is considered by the infusion to be made out of metal **/
    public static boolean isMetalEntity(EntityLivingBase base) {
        if (base != null) {
            ResourceLocation entityId = EntityList.getKey(base);
            if (entityId != null) {
                return metalEntities.contains(entityId);
            }
        }
        return false;
    }

    /** Returns true if the entity is considered by the infusion to be made out of metal **/
    public static boolean isMetalEntity(ResourceLocation entityID) {
        return metalEntities.contains(entityID);
    }

}