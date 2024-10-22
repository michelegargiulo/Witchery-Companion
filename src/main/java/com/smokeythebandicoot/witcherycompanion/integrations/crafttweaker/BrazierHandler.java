package com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker;

import com.smokeythebandicoot.witcherycompanion.api.brazier.BrazierApi;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntityCreature;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.potions.IPotion;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.registry.RegistryWrappers;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ModOnly(value = "witchery")
@ZenClass("mods.smokeythebandicoot.witcherycompanion.Brazier")
@ZenRegister
public class BrazierHandler {

    @ZenMethod
    @ZenDoc(value="Registers a Summoning Brazier Recipe")
    public static void registerSummoningRecipe(IIngredient input1, IIngredient input2, IIngredient input3, int burnTime, boolean needsPower, IEntityCreature summon, IEntityCreature extra) {

        if (input1 == null || summon == null) return;

        ResourceLocation summonRegistryName = EntityList.getKey(CraftTweakerMC.getEntity(summon));
        EntityType<EntityCreature> summonType = (EntityType<EntityCreature>)RegistryWrappers.ENTITIES.get(summonRegistryName);

        EntityType<EntityCreature> extraType = null;
        if (extra != null) {
            ResourceLocation extraRegistryName = EntityList.getKey(CraftTweakerMC.getEntity(extra));
            extraType = (EntityType<EntityCreature>)RegistryWrappers.ENTITIES.get(extraRegistryName);
        }

        BrazierApi.registerSummoningRecipe(null,
                CraftTweakerMC.getIngredient(input1),
                CraftTweakerMC.getIngredient(input2),
                CraftTweakerMC.getIngredient(input3),
                burnTime,
                needsPower,
                summonType,
                extraType);
    }

    @ZenMethod
    @ZenDoc(value="Registers an Effect Brazier Recipe")
    public static void registerEffectRecipe(IIngredient input1, IIngredient input2, IIngredient input3, int burnTime, boolean needsPower, IPotion potion, int radius) {
        if (input1 == null || potion == null) return;

        BrazierApi.registerEffectRecipe(null,
                CraftTweakerMC.getIngredient(input1),
                CraftTweakerMC.getIngredient(input2),
                CraftTweakerMC.getIngredient(input3),
                burnTime,
                needsPower,
                CraftTweakerMC.getPotion(potion),
                radius);
    }

    @ZenMethod
    @ZenDoc(value="Removes a Brazier Recipe of any type")
    public static void removeRecipe(String resourceLocation) {
        BrazierApi.removeRecipe(new ResourceLocation(resourceLocation));
    }

}
