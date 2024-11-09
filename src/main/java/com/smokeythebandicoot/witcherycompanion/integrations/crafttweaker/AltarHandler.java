package com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker;

import com.smokeythebandicoot.witcherycompanion.api.AltarApi;
import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlock;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import net.minecraft.block.Block;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.List;

@ModOnly(value = "witchery")
@ZenClass("mods.smokeythebandicoot.witcherycompanion.Altar")
@ZenRegister
public class AltarHandler {

    @ZenMethod
    @ZenDoc(value="Registers a new custom Power Source")
    public static void registerPowerSource(IBlock iBlock, int factor, int limit) {
        AltarApi.registerBlock(CraftTweakerMC.getBlock(iBlock), factor, limit);
    }

    @ZenMethod
    @ZenDoc(value="Registers a new custom Power Source")
    public static void registerPowerSource(IBlock iBlock, int factor, int limit, IIngredient representativeItem) {
        AltarApi.registerBlock(CraftTweakerMC.getBlock(iBlock), factor, limit, CraftTweakerMC.getIngredient(representativeItem));
    }

    @ZenMethod
    @ZenDoc(value="Registers a new custom Power Source by oreDict")
    public static void registerPowerSource(IOreDictEntry oreDictEntry, int factor, int limit) {
        List<Block> blocks = Utils.getBlocksForOre(oreDictEntry.getName());
        for (Block block : blocks) {
            AltarApi.registerBlock(block, factor, limit);
        }
    }

    @ZenMethod
    @ZenDoc(value="Registers a new custom Power Source by oreDict")
    public static void registerPowerSource(IOreDictEntry oreDictEntry, int factor, int limit, IIngredient representativeItem) {
        List<Block> blocks = Utils.getBlocksForOre(oreDictEntry.getName());
        for (Block block : blocks) {
            AltarApi.registerBlock(block, factor, limit, CraftTweakerMC.getIngredient(representativeItem));
        }
    }

    @ZenMethod
    @ZenDoc(value="Unregisters a previously-registered custom Power Source")
    public static void unregisterPowerSource(IBlock iBlock) {
        AltarApi.removeBlock(CraftTweakerMC.getBlock(iBlock));
    }

    @ZenMethod
    @ZenDoc(value="Unregisters a previously-registered custom Power Source by oreDict")
    public static void unregisterPowerSource(IOreDictEntry oreDictEntry) {
        List<Block> blocks = Utils.getBlocksForOre(oreDictEntry.getName());
        for (Block block : blocks) {
            AltarApi.removeBlock(block);
        }
    }


}
