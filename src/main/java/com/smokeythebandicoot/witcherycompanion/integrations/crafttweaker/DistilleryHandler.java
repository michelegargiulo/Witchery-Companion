package com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker;

import com.smokeythebandicoot.witcherycompanion.api.distillery.DistilleryApi;
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
@ZenClass("mods.smokeythebandicoot.witcherycompanion.Distillery")
@ZenRegister
public class DistilleryHandler {

    @ZenMethod
    @ZenDoc(value="Registers a Distillery Recipe")
    public static void registerRecipe(IIngredient input1, IIngredient input2, int clayJars, IItemStack output1, IItemStack output2, IItemStack output3, IItemStack output4) {

        if (input1 == null || output1 == null) return;

        DistilleryApi.registerRecipe(null,
                CraftTweakerMC.getIngredient(input1),
                CraftTweakerMC.getIngredient(input2),
                clayJars,
                CraftTweakerMC.getItemStack(output1),
                CraftTweakerMC.getItemStack(output2),
                CraftTweakerMC.getItemStack(output3),
                CraftTweakerMC.getItemStack(output4)
                );
    }

    @ZenMethod
    @ZenDoc(value="Removes a Distillery Recipe")
    public static void removeRecipe(String resourceLocation) {
        DistilleryApi.removeRecipe(new ResourceLocation(resourceLocation));
    }


}
