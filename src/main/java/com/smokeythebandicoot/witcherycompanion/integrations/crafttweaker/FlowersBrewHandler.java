package com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker;

import com.smokeythebandicoot.witcherycompanion.api.FlowersBrewApi;
import com.smokeythebandicoot.witcherycompanion.api.RiteOfMovingEarthApi;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlock;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraftforge.common.BiomeDictionary;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ModOnly(value = "witchery")
@ZenClass("mods.smokeythebandicoot.witcherycompanion.BrewOfFlowers")
@ZenRegister
public class FlowersBrewHandler {

    @ZenMethod
    @ZenDoc(value="Adds a new flower that can spawn in all biomes")
    public static void addFlower(IBlockState flower) {
        FlowersBrewApi.addFlower(CraftTweakerMC.getBlockState(flower), (BiomeDictionary.Type) null);
    }

    @ZenMethod
    @ZenDoc(value="Adds a new flower that can spawn in the specified biome types")
    public static void addFlower(IBlockState flower, String... biomeTypes) {
        FlowersBrewApi.addFlower(CraftTweakerMC.getBlockState(flower), biomeTypes);
    }

    @ZenMethod
    @ZenDoc(value="Completely removes the flower from spawning")
    public static void removeFlower(IBlockState flower) {
        FlowersBrewApi.removeFlower(CraftTweakerMC.getBlockState(flower));
    }

    @ZenMethod
    @ZenDoc(value="Prevents the flower from spawning in the specified biome types. pass null as biome types to refer to the 'any type' list")
    public static void removeFlower(IBlockState flower, String... biomeTypes) {
        FlowersBrewApi.removeFlower(CraftTweakerMC.getBlockState(flower), biomeTypes);
    }

}
