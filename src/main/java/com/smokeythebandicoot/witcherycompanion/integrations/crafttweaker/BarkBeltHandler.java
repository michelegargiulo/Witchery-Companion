package com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker;


import com.smokeythebandicoot.witcherycompanion.api.BarkBeltApi;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlock;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ModOnly(value = "witchery")
@ZenClass("mods.smokeythebandicoot.witcherycompanion.BarkBelt")
@ZenRegister
public class BarkBeltHandler {

    @ZenMethod
    @ZenDoc(value="Registers a new ingredient as a valid block that recharges Bark Belt. Returns true " +
            "if at least one non-air block has been added")
    public static boolean registerBlock(IIngredient ingredient) {
        boolean added = false;
        for (ItemStack stack : CraftTweakerMC.getIngredient(ingredient).getMatchingStacks()) {
            Block block = Block.getBlockFromItem(stack.getItem());
            if (block == Blocks.AIR) continue;
            net.minecraft.block.state.IBlockState state = block.getStateFromMeta(stack.getMetadata());
            BarkBeltApi.registerBlockstate(state);
            added = true;
        }
        return added;
    }

    @ZenMethod
    @ZenDoc(value="Registers a new Block as a valid block that recharges Bark Belt")
    public static boolean registerBlock(IBlock block) {
        return BarkBeltApi.registerBlock(CraftTweakerMC.getBlock(block));
    }

    @ZenMethod
    @ZenDoc(value="Registers a new Blockstate as a valid block that recharges Bark Belt")
    public static boolean registerBlockstate(IBlockState state) {
        return BarkBeltApi.registerBlockstate(CraftTweakerMC.getBlockState(state));
    }

    @ZenMethod
    @ZenDoc(value="Un-registers a Block as a valid block that recharges Bark Belt")
    public static boolean removeBlock(IBlock block) {
        return BarkBeltApi.removeBlock(CraftTweakerMC.getBlock(block));
    }

    @ZenMethod
    @ZenDoc(value="Un-registers a Blockstate as a valid block that recharges Bark Belt")
    public static boolean removeBlockstate(IBlockState state) {
        return BarkBeltApi.removeBlockstate(CraftTweakerMC.getBlockState(state));
    }

    @ZenMethod
    @ZenDoc(value="Returns true if the block recharges Bark Belt")
    public static boolean canRechargeBarkBelt(IBlock block) {
        return BarkBeltApi.canRechargeBarkBelt(CraftTweakerMC.getBlock(block));
    }

    @ZenMethod
    @ZenDoc(value="Returns true if the blockstate recharges Bark Belt")
    public static boolean canRechargeBarkBelt(IBlockState state) {
        return BarkBeltApi.canRechargeBarkBelt(CraftTweakerMC.getBlockState(state));
    }
}
