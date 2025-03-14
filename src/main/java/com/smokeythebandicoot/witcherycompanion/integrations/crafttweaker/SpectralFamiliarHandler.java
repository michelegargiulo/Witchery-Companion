package com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker;

import com.smokeythebandicoot.witcherycompanion.api.SpectralFamiliarApi;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import scala.tools.cmd.Spec;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


@ModOnly(value = "witchery")
@ZenClass("mods.smokeythebandicoot.witcherycompanion.SpectralFamiliar")
@ZenRegister
public class SpectralFamiliarHandler {

    @ZenMethod
    @ZenDoc(value="Registers a new custom Power Source")
    public static void addOreToSniff(IItemStack sniff, IBlockState... states) {
        Set<net.minecraft.block.state.IBlockState> ores = new HashSet<>();
        for (IBlockState state : states) {
            ores.add(CraftTweakerMC.getBlockState(state));
        }
        SpectralFamiliarApi.addOre(CraftTweakerMC.getItemStack(sniff), ores);
    }

    @ZenMethod
    @ZenDoc(value="Registers a new custom Power Source")
    public static void removeOreToSniff(IBlockState state) {
        SpectralFamiliarApi.removeOre(CraftTweakerMC.getBlockState(state));
    }



}
