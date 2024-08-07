package com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker.nonrecipes;

import com.smokeythebandicoot.witcherycompanion.api.altar.AltarApi.AltarPowerSource;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlock;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ModOnly(value = "witchery")
@ZenClass("mods.smokeythebandicoot.witcherycompanion.Altar")
@ZenRegister
public class AltarHandler {

    public static HashMap<Block, AltarPowerSource> addingMap = new HashMap<>();

    public static List<Block> removalList = new ArrayList<>();

    @ZenMethod
    @ZenDoc(value="Registers a new custom Power Source")
    public static void registerPowerSource(IBlock iBlock, int factor, int limit) {
        Block block = CraftTweakerMC.getBlock(iBlock);
        AltarPowerSource data = new AltarPowerSource(factor, limit);
        addingMap.put(block, data);
    }

    @ZenMethod
    @ZenDoc(value="Registers a new custom Power Source by oreDict")
    public static void registerPowerSource(IOreDictEntry oreDictEntry, int factor, int limit) {
        List<Block> blocks = getBlocksForOre(oreDictEntry.getName());
        for (Block block : blocks) {
            AltarPowerSource data = new AltarPowerSource(factor, limit);
            addingMap.put(block, data);
        }
    }

    @ZenMethod
    @ZenDoc(value="Unregisters a previously-registered custom Power Source")
    public static void unregisterPowerSource(IBlock iBlock) {
        Block block = CraftTweakerMC.getBlock(iBlock);
        addingMap.remove(block);
    }

    @ZenMethod
    @ZenDoc(value="Unregisters a previously-registered custom Power Source by oreDict")
    public static void unregisterPowerSource(IOreDictEntry oreDictEntry) {
        List<Block> blocks = getBlocksForOre(oreDictEntry.getName());
        for (Block block : blocks) {
            addingMap.remove(block);
        }
    }

    @ZenMethod
    @ZenDoc(value="Unregisters a default Witchery Power Source. Since this is applied at the end, always wins on custom power sources")
    public static void unregisterWitcheryPowerSource(IBlock iBlock) {
        Block block = CraftTweakerMC.getBlock(iBlock);
        removalList.add(block);
    }

    @ZenMethod
    @ZenDoc(value="Unregisters a default Witchery Power Source by oreDict. Since this is applied at the end, always wins on custom power sources")
    public static void unregisterWitcheryPowerSource(IOreDictEntry oreDictEntry) {
        List<Block> blocks = getBlocksForOre(oreDictEntry.getName());
        removalList.addAll(blocks);
    }

    private static List<Block> getBlocksForOre(String name) {
        List<Block> blockList = new ArrayList<>();
        NonNullList<ItemStack> items = OreDictionary.getOres(name);
        for (ItemStack item : items) {
            blockList.add(Block.getBlockFromItem(item.getItem()));
        }
        return blockList;
    }

}
