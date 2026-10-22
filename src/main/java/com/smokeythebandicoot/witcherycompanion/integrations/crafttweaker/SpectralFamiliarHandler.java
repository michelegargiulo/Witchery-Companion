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
    @ZenDoc(value="Adds a sniffable item that will trigger a search for any of the specified blockstates")
    public static void addOreToSniff(IItemStack sniff, IBlockState... states) {
        Set<net.minecraft.block.state.IBlockState> ores = new HashSet<>();
        for (IBlockState state : states) {
            ores.add(CraftTweakerMC.getBlockState(state));
        }
        SpectralFamiliarApi.addOre(CraftTweakerMC.getItemStack(sniff), ores);
    }

    @ZenMethod
    @ZenDoc(value="Removes a block from all groups containing that block")
    public static void removeOreToSniff(IBlockState state) {
        SpectralFamiliarApi.removeOre(CraftTweakerMC.getBlockState(state));
    }

    @ZenMethod
    @ZenDoc(value="Removes a blocks from the set of blocks that the familiar looks for when sniffing the item")
    public static void removeOreToSniff(IItemStack sniffable, IBlockState state) {
        SpectralFamiliarApi.removeOre(
                CraftTweakerMC.getItemStack(sniffable),
                CraftTweakerMC.getBlockState(state)
        );
    }

    @ZenMethod
    @ZenDoc(value="Removes a sniffable and all the associated blocks")
    public static void removeSniffable(IItemStack sniffable) {
        SpectralFamiliarApi.removeSniffable(CraftTweakerMC.getItemStack(sniffable));
    }

}
