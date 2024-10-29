package com.smokeythebandicoot.witcherycompanion.api.mutandis;

import com.smokeythebandicoot.witcherycompanion.utils.GroupedSet;
import mezz.jei.ingredients.Ingredients;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.BlockVine;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.world.World;
import net.msrandom.witchery.init.WitcheryBlocks;
import net.msrandom.witchery.init.WitcheryDimensions;
import net.msrandom.witchery.init.WitcheryWoodTypes;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@ParametersAreNonnullByDefault
public class MutandisApi {

    public static final HashMap<IBlockState, IBlockState> grassConversion;
    public static final HashMap<IBlockState, IBlockState> clayConversion;
    public static final GroupedSet<IBlockState> mutandis;
    public static final GroupedSet<IBlockState> mutandisExtremis;

    public static final HashMap<IBlockState, Ingredient> representativeItems;
    //public static final HashMap<IBlockState, int> dimensionMap; // Modify IndexedHashSet to contain dimension info

    static {

        // Util hashmap to connect IBlockStates to Ingredients
        representativeItems = new HashMap<>();

        // PRIORITY 1 - this conversion is checked first
        // Clay Conversion: Blocks that get converted into others if water is on top. Conversion happens in patches (+ shape of blocks)
        clayConversion = new HashMap<>();
        clayConversion.put(Blocks.DIRT.getDefaultState(), Blocks.CLAY.getDefaultState());

        // PRIORITY 2 - this conversion is checked second
        // Default conversion: Non-plant blocks that can be converted into other blocks
        grassConversion = new HashMap<>();
        grassConversion.put(Blocks.GRASS.getDefaultState(), Blocks.MYCELIUM.getDefaultState());
        grassConversion.put(Blocks.MYCELIUM.getDefaultState(), Blocks.GRASS.getDefaultState());

        // PRIORITY 3 - this conversion is checked third
        // Mutandis conversion: Plant blocks that can be converted into any other block of the set
        mutandis = new GroupedSet<>(new Random());
        // All vanilla saplings
        mutandis.add(Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.OAK));
        mutandis.add(Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.SPRUCE));
        mutandis.add(Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.BIRCH));
        mutandis.add(Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.JUNGLE));
        mutandis.add(Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.ACACIA));
        mutandis.add(Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.DARK_OAK));
        // Witchery saplings
        if (WitcheryWoodTypes.ROWAN.getSapling() != null)
            mutandis.add(WitcheryWoodTypes.ROWAN.getSapling().getDefaultState());
        if (WitcheryWoodTypes.ALDER.getSapling() != null)
            mutandis.add(WitcheryWoodTypes.ALDER.getSapling().getDefaultState());
        if (WitcheryWoodTypes.HAWTHORN.getSapling() != null)
            mutandis.add(WitcheryWoodTypes.HAWTHORN.getSapling().getDefaultState());
        // Misc vanilla
        mutandis.add(Blocks.TALLGRASS.getDefaultState().withProperty(BlockTallGrass.TYPE, net.minecraft.block.BlockTallGrass.EnumType.GRASS));
        mutandis.add(Blocks.WATERLILY.getDefaultState());
        mutandis.add(Blocks.BROWN_MUSHROOM.getDefaultState());
        mutandis.add(Blocks.RED_MUSHROOM.getDefaultState());
        mutandis.add(Blocks.RED_FLOWER.getDefaultState());
        mutandis.add(Blocks.YELLOW_FLOWER.getDefaultState());
        // Misc Witchery
        mutandis.add(WitcheryBlocks.EMBER_MOSS.getDefaultState());
        mutandis.add(WitcheryBlocks.SPANISH_MOSS.getDefaultState());

        // PRIORITY 3 - same priority as normal mutandis, but with extended set
        // Mutandis Extremis conversion: extends the set of normal Mutandis. Moreover, the Mutandis checks if the BlockStateContainer has
        // some common properties, like BlockCrops.AGE, BlockWitchCrop.AGE4, BlockWitchCrop.AGE5, BlockReed.AGE, BlockStem.AGE or BlockNetherWart.AGE
        // and sets their age to min(age of mutated plant, max age of new crop). Otherwise, property will be ignored
        mutandisExtremis = new GroupedSet<>(new Random());
        mutandisExtremis.add(WitcheryBlocks.BELLADONNA_SEEDS.getDefaultState());
        mutandisExtremis.add(WitcheryBlocks.MANDRAKE_SEEDS.getDefaultState());
        mutandisExtremis.add(WitcheryBlocks.ARTICHOKE_SEEDS.getDefaultState());
        mutandisExtremis.add(Blocks.CACTUS.getDefaultState());
        setMutandisConversion(Blocks.CARROTS.getDefaultState(), true, Ingredient.fromItem(Items.CARROT));
        setMutandisConversion(Blocks.PUMPKIN_STEM.getDefaultState(), true, Ingredient.fromItem(Items.PUMPKIN_SEEDS));
        setMutandisConversion(Blocks.MELON_STEM.getDefaultState(), true, Ingredient.fromItem(Items.MELON_SEEDS));
        setMutandisConversion(Blocks.WHEAT.getDefaultState(), true, Ingredient.fromItem(Items.WHEAT_SEEDS));
        setMutandisConversion(Blocks.POTATOES.getDefaultState(), true, Ingredient.fromItem(Items.POTATO));
        setMutandisConversion(Blocks.REEDS.getDefaultState(), true, Ingredient.fromItem(Items.REEDS));
        setMutandisConversion(Blocks.NETHER_WART.getDefaultState(), true, WitcheryDimensions.SPIRIT_WORLD.getType().getId(), Ingredient.fromItem(Items.NETHER_WART));

        /**
         * mutandisExtremis.add(Blocks.CARROTS.getDefaultState().withProperty(BlockCrops.AGE, Math.min(currentAge, 7)));
         * mutandisExtremis.add(Blocks.POTATOES.getDefaultState().withProperty(BlockCrops.AGE, Math.min(currentAge, 7)));
         * mutandisExtremis.add(Blocks.WHEAT.getDefaultState().withProperty(BlockCrops.AGE, Math.min(currentAge, 7)));
         * mutandisExtremis.add(WitcheryBlocks.BELLADONNA_SEEDS.getDefaultState().withProperty(BlockWitchCrop.AGE4, Math.min(currentAge, 7)));
         * mutandisExtremis.add(WitcheryBlocks.MANDRAKE_SEEDS.getDefaultState().withProperty(BlockWitchCrop.AGE4, Math.min(currentAge, 7)));
         * mutandisExtremis.add(WitcheryBlocks.ARTICHOKE_SEEDS.getDefaultState().withProperty(BlockWitchCrop.AGE4, Math.min(currentAge, 7)));
         * mutandisExtremis.add(Blocks.REEDS.getDefaultState().withProperty(BlockReed.AGE, Math.min(currentAge, 7)));
         * mutandisExtremis.add(Blocks.PUMPKIN_STEM.getDefaultState().withProperty(BlockStem.AGE, Math.min(currentAge, 7)));
         * mutandisExtremis.add(Blocks.MELON_STEM.getDefaultState().withProperty(BlockStem.AGE, Math.min(currentAge, 7)));
         * mutandisExtremis.add(Blocks.NETHER_WART.getDefaultState().withProperty(BlockNetherWart.AGE, Math.min(currentAge, 3)));
         **/
    }



    /** ========== GRASS CONVERSION ========== **/

    public static void addGrassConversion(IBlockState sourceState, IBlockState targetState) {

    }

    public static void removeGrassConversion(IBlockState sourceState) {

    }

    public static boolean isGrassConvertible(IBlockState state) {
        return grassConversion.containsKey(state);
    }

    public static IBlockState getGrassConversion(IBlockState state) {
        return grassConversion.getOrDefault(state, null);
    }



    /** ========== CLAY CONVERSION ========== **/

    public static void addClayConversion(IBlockState sourceState, IBlockState targetState) {
        clayConversion.put(sourceState, targetState);
    }

    public static void removeClayConversion(IBlockState sourceState) {
        clayConversion.remove(sourceState);
    }

    public static boolean isClayConvertible(IBlockState state) {
        return clayConversion.containsKey(state);
    }

    public static IBlockState getClayConversion(IBlockState state) {
        return clayConversion.getOrDefault(state, null);
    }



    /** ========== CLAY CONVERSION ========== **/

    public static void setMutandisConversion(IBlockState sourceState, boolean needsExtremis) {
        IBlockState state = getAgeAgnosticBlockState(sourceState);
        if (needsExtremis) {
            mutandis.remove(state);
            mutandisExtremis.add(state);
        } else {
            mutandis.add(state);
            mutandisExtremis.remove(state);
        }
    }

    public static void setMutandisConversion(IBlockState sourceState, boolean needsExtremis, Ingredient repr) {
        representativeItems.put(sourceState, repr);
        setMutandisConversion(sourceState, needsExtremis);
    }

    public static void setMutandisConversion(IBlockState sourceState, boolean needsExtremis, Integer dimension) {
        IBlockState state = getAgeAgnosticBlockState(sourceState);
        if (needsExtremis) {
            mutandis.remove(state);
            mutandisExtremis.add(state, dimension);
        } else {
            mutandis.add(state, dimension);
            mutandisExtremis.remove(state);
        }
    }

    public static void setMutandisConversion(IBlockState sourceState, boolean needsExtremis, Integer dimension, Ingredient repr) {
        representativeItems.put(sourceState, repr);
        setMutandisConversion(sourceState, needsExtremis, dimension);
    }

    public static void removeMutandisConversion(IBlockState sourceState) {
        IBlockState state = getAgeAgnosticBlockState(sourceState);
        representativeItems.remove(sourceState);
        mutandisExtremis.remove(state);
        mutandis.remove(state);
    }

    public static boolean hasConversion(IBlockState sourceState, boolean extremisAvailable) {
        IBlockState state = getAgeAgnosticBlockState(sourceState);
        return mutandis.contains(state) || (extremisAvailable && mutandisExtremis.contains(state));
    }

    public static IBlockState getConversion(IBlockState sourceState, boolean extremis, int dim) {
        return getConversion(new Random(), sourceState, extremis, dim);
    }

    public static IBlockState getConversion(World world, IBlockState sourceState, boolean extremis) {
        return getConversion(world.rand, sourceState, extremis, world.provider.getDimension());
    }

    public static IBlockState getConversion(Random random, IBlockState sourceState, boolean extremis, Integer dim) {

        // Ignore Age property on blocks
        IBlockState state = getAgeAgnosticBlockState(sourceState);

        // If not extremis, only check the normal list
        if (!extremis) {
            // If list contains it, return a random element
            if (mutandis.contains(state)) {
                return mutandis.getRandom(dim);
            }
            // Otherwise, no conversion :(
            return null;
        }

        // At this point, mutandis is Extremis. If not in the either lists, return
        // It's the same as hasConversion, but avoids unnecessary evaluation/check of boolean extremis
        if (!mutandis.contains(state) && !mutandisExtremis.contains(state)) {
            return null;
        }

        // Instead of creating a unique collection, just select the list randomly, weighted by their respective sizes
        // The result should be a random element selected by uniform distribution across both the sets, as the getRandom()
        // of each list selects the element uniformly as well
        int normalSize = mutandis.size();
        int extremisSize = mutandisExtremis.size();

        // If < normalSize, then take from mutandis
        if ((new Random()).nextInt(normalSize + extremisSize) < normalSize) {
            return mutandis.getRandom(dim);
        } else {
            return mutandisExtremis.getRandom(dim);
        }

    }

    public static HashMap<IBlockState, Ingredient> getPlantConversions(boolean extremis) {
        HashMap<IBlockState, Ingredient> mutables = new HashMap<>();
        for (IBlockState state : mutandis.toSet()) {
            mutables.put(state, representativeItems.getOrDefault(state, null));
        }
        if (extremis) {
            for (IBlockState state : mutandisExtremis.toSet()) {
                mutables.put(state, representativeItems.getOrDefault(state, null));
            }
        }
        return mutables;
    }

    private static IBlockState getAgeAgnosticBlockState(IBlockState state) {
        IProperty<?> age = state.getBlock().getBlockState().getProperty("age");
        if (age instanceof PropertyInteger) {
            return state.withProperty((PropertyInteger)age, 0);
        }
        return state;
    }



}
