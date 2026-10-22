package com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker;

import com.smokeythebandicoot.witcherycompanion.api.OverworldInfusionApi;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlock;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.entity.IEntityDefinition;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ModOnly(value = "witchery")
@ZenClass("mods.smokeythebandicoot.witcherycompanion.OverworldInfusion")
@ZenRegister
public class OverworldInfusionHandler {

    @ZenMethod
    @ZenDoc(value="Adds the specified ItemStack as metal item")
    public static void addMetalItem(IItemStack stack) {
        OverworldInfusionApi.addMetalItem(CraftTweakerMC.getItemStack(stack));
    }

    @ZenMethod
    @ZenDoc(value="Removes the specified ItemStack as metal item")
    public static void removeMetalItem(IItemStack stack) {
        OverworldInfusionApi.removeMetalItem(CraftTweakerMC.getItemStack(stack));
    }



    @ZenMethod
    @ZenDoc(value="The specified blockstate will be able to be launched as a rock")
    public static void addThrowable(IBlockState state) {
        OverworldInfusionApi.addThrowable(CraftTweakerMC.getBlockState(state));
    }

    @ZenMethod
    @ZenDoc(value="The specified block will be able to be launched as a rock")
    public static void addThrowable(IBlock block) {
        OverworldInfusionApi.addThrowable(CraftTweakerMC.getBlock(block));
    }

    @ZenMethod
    @ZenDoc(value="The specified blockstate will not be able to be launched as a rock")
    public static void removeThrowable(IBlockState state) {
        OverworldInfusionApi.removeThrowable(CraftTweakerMC.getBlockState(state));
    }

    @ZenMethod
    @ZenDoc(value="The specified block will not be able to be launched as a rock")
    public static void removeThrowable(IBlock block) {
        OverworldInfusionApi.removeThrowable(CraftTweakerMC.getBlock(block));
    }




    @ZenMethod
    @ZenDoc(value="The infusion will transform the specified state into leftover, while spitting result as Item Entity")
    public static void addOreTransformation(IBlockState state, IItemStack result, IBlockState leftover) {
        OverworldInfusionApi.addOreTransformation(
                CraftTweakerMC.getBlockState(state),
                CraftTweakerMC.getItemStack(result),
                CraftTweakerMC.getBlockState(leftover)
        );
    }

    @ZenMethod
    @ZenDoc(value="The infusion will transform the specified state into Stone, while spitting result as Item Entity")
    public static void addOreTransformation(IBlockState state, IItemStack result) {
        OverworldInfusionApi.addOreTransformation(
                CraftTweakerMC.getBlockState(state),
                CraftTweakerMC.getItemStack(result)
        );
    }

    @ZenMethod
    @ZenDoc(value="The infusion will no longer have any effect on the specified state")
    public static void removeOreTransformation(IBlockState state) {
        OverworldInfusionApi.removeOreTransformation(
                CraftTweakerMC.getBlockState(state)
        );
    }



    @ZenMethod
    @ZenDoc(value="The infusion will knockback the specified entity as if it was wearing metal armor")
    public static void addMetalEntity(IEntityDefinition entity) {
        OverworldInfusionApi.addMetalEntity(
                new ResourceLocation(entity.getId())
        );
    }

    @ZenMethod
    @ZenDoc(value="The infusion will not knockback the specified entity as if it was wearing metal armor")
    public static void removeMetalEntity(IEntityDefinition entity) {
        OverworldInfusionApi.removeMetalEntity(
                new ResourceLocation(entity.getId())
        );
    }

}
