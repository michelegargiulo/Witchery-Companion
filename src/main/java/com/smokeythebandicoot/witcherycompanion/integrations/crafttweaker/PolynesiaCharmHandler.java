package com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker;

import com.smokeythebandicoot.witcherycompanion.api.PolynesiaCharmApi;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntityLiving;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;


@ModOnly(value = "witchery")
@ZenClass("mods.smokeythebandicoot.witcherycompanion.PolynesiaCharm")
@ZenRegister
public class PolynesiaCharmHandler {

    private static Class<? extends EntityLiving> getEntityClass(IEntityLiving entity) {
        Entity e = CraftTweakerMC.getEntity(entity);
        if (entity instanceof EntityLiving) {
            return ((EntityLiving) e).getClass();
        }
        return null;
    }

    @ZenMethod
    @ZenDoc(value="Adds a Fallback trade for an animal type")
    public static void addFallbackTrade(IEntityLiving entity, IItemStack good, int range, boolean isPrecious) {
        Class<? extends EntityLiving> entityClass = getEntityClass(entity);
        if (entityClass != null) {
            PolynesiaCharmApi.addFallbackTrade(
                    entityClass, CraftTweakerMC.getItemStack(good), range, isPrecious
            );
        }
    }

    @ZenMethod
    @ZenDoc(value="Adds a Fallback trade for an animal type")
    public static void addFallbackTrade(IEntityLiving entity, IItemStack good, int range) {
        addFallbackTrade(entity, good, range, false);
    }

    @ZenMethod
    @ZenDoc(value="Adds a Fallback trade for an animal type")
    public static void addFallbackTrade(IEntityLiving entity, IItemStack good) {
        addFallbackTrade(entity, good, 1, false);
    }

    @ZenMethod
    @ZenDoc(value="")
    public static void removeFallbackTrade(IEntityLiving entity, IItemStack good) {
        Class<? extends EntityLiving> entityClass = getEntityClass(entity);
        if (entityClass != null) {
            PolynesiaCharmApi.removeFallbackTrade(
                    entityClass, CraftTweakerMC.getItemStack(good)
            );
        }
    }

    @ZenMethod
    @ZenDoc(value="")
    public static void addGood(IEntityLiving entity, IItemStack good, int range, double chance, boolean isPrecious) {
        Class<? extends EntityLiving> entityClass = getEntityClass(entity);
        if (entityClass != null) {
            PolynesiaCharmApi.addGood(
                    entityClass, CraftTweakerMC.getItemStack(good), range, chance, isPrecious
            );
        }
    }

    @ZenMethod
    @ZenDoc(value="")
    public static void addGood(IEntityLiving entity, IItemStack good, int range, double chance) {
        addGood(entity, good, range, chance, false);
    }

    @ZenMethod
    @ZenDoc(value="")
    public static void addGood(IEntityLiving entity, IItemStack good, int range) {
        addGood(entity, good, range, 1.0, false);
    }

    @ZenMethod
    @ZenDoc(value="")
    public static void addGood(IEntityLiving entity, IItemStack good) {
        addGood(entity, good, 1, 1.0, false);
    }

    @ZenMethod
    @ZenDoc(value="")
    public static void removeGood(IEntityLiving entity, IItemStack good) {
        Class<? extends EntityLiving> entityClass = getEntityClass(entity);
        if (entityClass != null) {
            PolynesiaCharmApi.removeGood(
                    entityClass, CraftTweakerMC.getItemStack(good)
            );
        }
    }

    @ZenMethod
    @ZenDoc(value="")
    public static void addCurrency(IEntityLiving entity, IItemStack currency, int range) {
        Class<? extends EntityLiving> entityClass = getEntityClass(entity);
        if (entityClass != null) {
            PolynesiaCharmApi.addCurrency(
                    entityClass, CraftTweakerMC.getItemStack(currency), range
            );
        }
    }

    @ZenMethod
    @ZenDoc(value="")
    public static void addCurrency(IEntityLiving entity, IItemStack currency) {
        Class<? extends EntityLiving> entityClass = getEntityClass(entity);
        if (entityClass != null) {
            PolynesiaCharmApi.addCurrency(
                    entityClass, CraftTweakerMC.getItemStack(currency)
            );
        }
    }

    @ZenMethod
    @ZenDoc(value="")
    public static void removeCurrency(IEntityLiving entity, IItemStack currency) {
        Class<? extends EntityLiving> entityClass = getEntityClass(entity);
        if (entityClass != null) {
            PolynesiaCharmApi.removeCurrency(
                    entityClass, CraftTweakerMC.getItemStack(currency)
            );
        }
    }

}
