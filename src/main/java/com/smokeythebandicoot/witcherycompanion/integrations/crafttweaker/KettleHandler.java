package com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker;

import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.DimensionManager;
import net.msrandom.witchery.recipe.KettleRecipe;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;

@ModOnly(value = "witchery")
@ZenClass(value = "mods.smokeythebandicoot.witcherypatcher.Kettle")
@ZenRegister
public class KettleHandler {

    protected static Integer serial = 0;

    public void addRecipe(IItemStack result, IIngredient[] inputs,
                          @Optional Integer dimension, @Optional Integer hatBonus,
                          @Optional String familiarPower, @Optional Float powerRequired,
                          @Optional Boolean isSpecial) {

        /*
         ID [ResourceLocation]
         result [ItemStack]
         inputs [Ingredient[]]
         hatBonus [int]
         familiarPower [ResourceLocation]
         powerRequired [float]
         dimension [Dimensiontype]
         special [boolean]
         */
        NonNullList<Ingredient> inps = NonNullList.create();
        for (IIngredient ing : inputs) {
            inps.add(CraftTweakerMC.getIngredient(ing));
        }

        KettleRecipe recipe = new KettleRecipe(
            new ResourceLocation(String.valueOf(serial.hashCode())),
                CraftTweakerMC.getItemStack(result),
                inps,
                hatBonus == null ? 0 : hatBonus,
                familiarPower == null ? null : new ResourceLocation(familiarPower),
                powerRequired == null ? 0 : powerRequired,
                dimension == null ? null : DimensionManager.getProviderType(dimension),
                isSpecial != null && isSpecial
        );

        //WitcheryRecipeSerializers.INSTANCE.register();
        //WitcheryRecipes.
    }


}
