package com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker;

import com.smokeythebandicoot.witcherycompanion.api.SpinningWheelApi;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ModOnly(value = "witchery")
@ZenClass("mods.smokeythebandicoot.witcherycompanion.SpinningWheel")
@ZenRegister
public class SpinningWheelHandler {

    @ZenMethod
    @ZenDoc(value="Registers a Spinning Wheel Recipe")
    public static void registerRecipe(IItemStack result, IIngredient fibre, int fibreCount, IIngredient modifier1, IIngredient modifier2, IIngredient modifier3) {

        if (result == null || fibre == null) return;

        SpinningWheelApi.registerRecipe(null,
                CraftTweakerMC.getItemStack(result),
                CraftTweakerMC.getIngredient(fibre),
                fibreCount,
                CraftTweakerMC.getIngredient(modifier1),
                CraftTweakerMC.getIngredient(modifier2),
                CraftTweakerMC.getIngredient(modifier3)
                );
    }

    @ZenMethod
    @ZenDoc(value="Removes a Spinning Wheel Recipe")
    public static void removeRecipe(String resourceLocation) {
        SpinningWheelApi.removeRecipe(new ResourceLocation(resourceLocation));
    }


}
