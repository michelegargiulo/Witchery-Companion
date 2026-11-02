package com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker;

import com.smokeythebandicoot.witcherycompanion.api.SpiritWorldTransferApi;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ModOnly(value = "witchery")
@ZenClass("mods.smokeythebandicoot.witcherycompanion.SpiritWorld")
@ZenRegister
public class SpiritWorldTransferHandler {

    @ZenMethod
    @ZenDoc(value="Items that can be carried from the Overworld into the Spirit World. Default: Icy Needle, Mutandis. " +
            "Matching ignores NBT; use a wildcard metadata (<item:*>) to match every metadata. Returns false if the item was already there")
    public static boolean addEnteringItem(IItemStack stack) {
        return SpiritWorldTransferApi.addEnteringItem(CraftTweakerMC.getItemStack(stack));
    }

    @ZenMethod
    @ZenDoc(value="The item will not be carried into the Spirit World anymore. Returns false if the item was not in the list")
    public static boolean removeEnteringItem(IItemStack stack) {
        return SpiritWorldTransferApi.removeEnteringItem(CraftTweakerMC.getItemStack(stack));
    }

    @ZenMethod
    @ZenDoc(value="Items that can be carried from the Spirit World to the Overworld. Default: Wispy Cotton, Disturbed Cotton, " +
            "Mellifluous Hunger, Flowing Spirit, Subdued Spirit, Icy Needle. Returns false if the item was already there")
    public static boolean addExitingItem(IItemStack stack) {
        return SpiritWorldTransferApi.addExitingItem(CraftTweakerMC.getItemStack(stack));
    }

    @ZenMethod
    @ZenDoc(value="The item will not be carried out of the Spirit World anymore. Returns false if the item was not in the list")
    public static boolean removeExitingItem(IItemStack stack) {
        return SpiritWorldTransferApi.removeExitingItem(CraftTweakerMC.getItemStack(stack));
    }

    @ZenMethod
    @ZenDoc(value="The item will be carried both into and out of the Spirit World (like the Icy Needle)")
    public static void addTransferableItem(IItemStack stack) {
        SpiritWorldTransferApi.addEnteringItem(CraftTweakerMC.getItemStack(stack));
        SpiritWorldTransferApi.addExitingItem(CraftTweakerMC.getItemStack(stack));
    }

    @ZenMethod
    @ZenDoc(value="The item will be carried neither into nor out of the Spirit World")
    public static void removeTransferableItem(IItemStack stack) {
        SpiritWorldTransferApi.removeEnteringItem(CraftTweakerMC.getItemStack(stack));
        SpiritWorldTransferApi.removeExitingItem(CraftTweakerMC.getItemStack(stack));
    }

    @ZenMethod
    @ZenDoc(value="Removes every item from the list of items carried into the Spirit World, defaults included")
    public static void clearEnteringItems() {
        SpiritWorldTransferApi.clearEnteringItems();
    }

    @ZenMethod
    @ZenDoc(value="Removes every item from the list of items carried out of the Spirit World, defaults included")
    public static void clearExitingItems() {
        SpiritWorldTransferApi.clearExitingItems();
    }

    @ZenMethod
    @ZenDoc(value="Returns true if the item is carried from the Overworld into the Spirit World")
    public static boolean canEnter(IItemStack stack) {
        return SpiritWorldTransferApi.canEnter(CraftTweakerMC.getItemStack(stack));
    }

    @ZenMethod
    @ZenDoc(value="Returns true if the item is carried from the Spirit World to the Overworld")
    public static boolean canExit(IItemStack stack) {
        return SpiritWorldTransferApi.canExit(CraftTweakerMC.getItemStack(stack));
    }

}
