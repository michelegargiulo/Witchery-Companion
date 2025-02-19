package com.smokeythebandicoot.witcherycompanion.api;

import net.minecraft.block.BlockFlower;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class FlowersBrewApi {

    private static HashMap<BiomeDictionary.Type, HashSet<IBlockState>> flowers;
    private static HashMap<Set<BiomeDictionary.Type>, IBlockState[]> cache;

    private static void initMap() {
        flowers = new HashMap<>();
        cache = new HashMap<>();

        // Add yellow flower
        addFlower(Blocks.YELLOW_FLOWER.getDefaultState(), (BiomeDictionary.Type) null);

        for (int i = 0; i <= 8; i++) {
            addFlower(Blocks.RED_FLOWER.getDefaultState().withProperty(
                    Blocks.RED_FLOWER.getTypeProperty(), BlockFlower.EnumFlowerType.getType(BlockFlower.EnumFlowerColor.RED, i - 1)
            ), (BiomeDictionary.Type) null);
        }
    }

    static {
        initMap();
    }

    /** Adds a flower that can spawn from the brew in the specified biome types **/
    public static void addFlower(IBlockState flower, BiomeDictionary.Type type) {
        flowers.computeIfAbsent(type, set -> new HashSet<>()).add(flower);
    }

    /** Adds a flower that can spawn from the brew in the specified biome types **/
    public static void addFlower(IBlockState flower, BiomeDictionary.Type... types) {
        for (BiomeDictionary.Type type : types) {
            addFlower(flower, type);
        }
    }

    /** Adds a flower that can spawn from the brew in the specified biome types **/
    public static void addFlower(IBlockState flower, String... biomeTypes) {
        for (String biomeType : biomeTypes) {
            if (!biomeType.isEmpty()) {
                BiomeDictionary.Type type = BiomeDictionary.Type.getType(biomeType);
                addFlower(flower, type);
            }
        }
    }

    /** Removes a flower completely **/
    public static void removeFlower(IBlockState flower) {
        for (BiomeDictionary.Type type : flowers.keySet()) {
            HashSet<IBlockState> f = flowers.get(type);
            if (f != null) {
                f.remove(flower);
            }
        }
    }

    /** Removes a flower for the specified biome types **/
    public static void removeFlower(IBlockState flower, BiomeDictionary.Type type) {
        if (!flowers.containsKey(type)) {
            return;
        }

        HashSet<IBlockState> biomeFlowers = flowers.get(type);
        if (biomeFlowers == null || biomeFlowers.isEmpty()) {
            return;
        }

        biomeFlowers.remove(flower);
        if (biomeFlowers.isEmpty()) {
            flowers.remove(type);
        }
    }

    /** Removes a flower for the specified biome types **/
    public static void removeFlower(IBlockState flower, BiomeDictionary.Type... types) {
        if (types == null) {
            removeFlower(flower, (BiomeDictionary.Type) null);
            return;
        }
        for (BiomeDictionary.Type type : types) {
            removeFlower(flower, type);
        }
    }

    /** Removes a flower for the specified biome types **/
    public static void removeFlower(IBlockState flower, String... biomeTypes) {
        if (biomeTypes == null) {
            removeFlower(flower, (BiomeDictionary.Type) null);
            return;
        }
        for (String biomeType : biomeTypes) {
            if (!biomeType.isEmpty()) {
                BiomeDictionary.Type type = BiomeDictionary.Type.getType(biomeType);
                removeFlower(flower, type);
            }
        }
    }


    /** Returns a random flower valid for spawning in the biome **/
    @Nullable
    public static IBlockState getRandomFlower(Biome biome, Random random) {
        IBlockState[] flowers = getValidFlowers(biome);
        return flowers == null || flowers.length == 0 ? null : flowers[random.nextInt(flowers.length)];
    }

    @Nullable
    public static IBlockState[] getValidFlowers(Biome biome) {
        HashSet<IBlockState> result = flowers.get(null) == null ? new HashSet<>() : new HashSet<>(flowers.get(null));
        Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes(biome);

        if (cache.containsKey(types)) {
            return cache.get(types);
        }

        for (BiomeDictionary.Type type : types) {
            HashSet<IBlockState> flowersForType = flowers.get(type);
            if (flowersForType != null && !flowersForType.isEmpty()) {
                result.addAll(flowersForType);
            }
        }

        // If still empty there are no flowers to generate
        if (result.isEmpty()) {
            return null;
        }

        // Return a random element of the array
        IBlockState[] flowerArray = result.toArray(new IBlockState[] {});
        cache.put(types, flowerArray);
        return flowerArray;
    }

}
