package com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker;

import com.smokeythebandicoot.witcherycompanion.api.AltarApi;
import com.smokeythebandicoot.witcherycompanion.api.PlacedItemApi;
import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlock;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
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

    @ZenMethod
    @ZenDoc(value="Registers a new IBlockState as an Altar Booster of the specified type and Altar boosting increments. Valid boosterTypes: SKULL, CANDLE, CHALICE, EGG")
    public static void registerAltarBooster(IBlockState state, int priority, String boosterType, int rechargeIncrement, int powerIncrement, int rangeIncrement, int enhancementIncrement) {
        AltarApi.registerAltarBooster(
            CraftTweakerMC.getBlockState(state),
            Enum.valueOf(AltarApi.EAltarBoosterType.class, boosterType.toUpperCase()),
            new AltarApi.AltarBoosterFunc(priority,
                (blockstate, tile, info) -> {
                    info.newRechargeScale += rechargeIncrement;
                    info.newPowerScale += powerIncrement;
                    info.newRangeScale += rangeIncrement;
                    info.newEnhancementLevel += enhancementIncrement;
                }
            )
        );
    }

    @ZenMethod
    @ZenDoc(value="Registers all the valid IBlockStates of a block as an Altar Booster of the specified type. Valid boosterTypes: SKULL, CANDLE, CHALICE, EGG")
    public static void registerAltarBooster(IBlock block, int priority, String boosterType, int rechargeIncrement, int powerIncrement, int rangeIncrement, int enhancementIncrement) {
        AltarApi.registerAltarBooster(
            CraftTweakerMC.getBlock(block),
            Enum.valueOf(AltarApi.EAltarBoosterType.class, boosterType.toUpperCase()),
            new AltarApi.AltarBoosterFunc(priority,
                (state, tile, info) -> {
                    info.newRechargeScale += rechargeIncrement;
                    info.newPowerScale += powerIncrement;
                    info.newRangeScale += rangeIncrement;
                    info.newEnhancementLevel += enhancementIncrement;
                }
            )
        );
    }

    @ZenMethod
    @ZenDoc(value="Registers a new IBlockState as an Altar Booster of the specified type, with increments and multipliers for " +
            "recharge rate, max power, range and enhancement level. All increments are summed first, then multipliers are applied. " +
            "Only the booster with the highest priority of each type counts. Valid boosterTypes: SKULL, CANDLE, CHALICE, EGG " +
            "(defaults: skull, torch/candelabra, chalice/full chalice, Infinity Egg x10 recharge and power)")
    public static void registerAltarBooster(IBlockState state, int priority, String boosterType,
                                            int rechargeIncrement, int powerIncrement, int rangeIncrement, int enhancementIncrement,
                                            int rechargeMultiplier, int powerMultiplier, int rangeMultiplier, int enhancementMultiplier) {
        AltarApi.registerAltarBooster(
            CraftTweakerMC.getBlockState(state),
            Enum.valueOf(AltarApi.EAltarBoosterType.class, boosterType.toUpperCase()),
            boosterFunc(priority, rechargeIncrement, powerIncrement, rangeIncrement, enhancementIncrement,
                    rechargeMultiplier, powerMultiplier, rangeMultiplier, enhancementMultiplier)
        );
    }

    @ZenMethod
    @ZenDoc(value="Registers all the valid IBlockStates of a block as an Altar Booster of the specified type, with increments and " +
            "multipliers for recharge rate, max power, range and enhancement level. All increments are summed first, then multipliers " +
            "are applied. Only the booster with the highest priority of each type counts. Valid boosterTypes: SKULL, CANDLE, CHALICE, EGG")
    public static void registerAltarBooster(IBlock block, int priority, String boosterType,
                                            int rechargeIncrement, int powerIncrement, int rangeIncrement, int enhancementIncrement,
                                            int rechargeMultiplier, int powerMultiplier, int rangeMultiplier, int enhancementMultiplier) {
        AltarApi.registerAltarBooster(
            CraftTweakerMC.getBlock(block),
            Enum.valueOf(AltarApi.EAltarBoosterType.class, boosterType.toUpperCase()),
            boosterFunc(priority, rechargeIncrement, powerIncrement, rangeIncrement, enhancementIncrement,
                    rechargeMultiplier, powerMultiplier, rangeMultiplier, enhancementMultiplier)
        );
    }

    private static AltarApi.AltarBoosterFunc boosterFunc(int priority, int rechargeInc, int powerInc, int rangeInc, int enhancementInc,
                                                         int rechargeMul, int powerMul, int rangeMul, int enhancementMul) {
        return new AltarApi.AltarBoosterFunc(priority,
            (state, tile, info) -> {
                info.add(rechargeInc, powerInc, rangeInc, enhancementInc);
                info.multiply(rechargeMul, powerMul, rangeMul, enhancementMul);
            }
        );
    }

    @ZenMethod
    @ZenDoc(value="Unregisters the specified IBlockState as Altar Booster of the specified type. Valid boosterTypes: SKULL, CANDLE, CHALICE, EGG")
    public static void unregisterAltarBooster(IBlockState state, String boosterType) {
        AltarApi.unregisterAltarBooster(
            CraftTweakerMC.getBlockState(state),
            Enum.valueOf(AltarApi.EAltarBoosterType.class, boosterType.toUpperCase())
        );
    }

    @ZenMethod
    @ZenDoc(value="Unregisters all valid IBlockStates of the Block as Altar Booster of the specified type. Valid boosterTypes: SKULL, CANDLE, CHALICE, EGG")
    public static void unregisterAltarBooster(IBlock block, String boosterType) {
        AltarApi.unregisterAltarBooster(
            CraftTweakerMC.getBlock(block),
            Enum.valueOf(AltarApi.EAltarBoosterType.class, boosterType.toUpperCase())
        );
    }


    /// PLACED ITEMS

    @ZenMethod
    @ZenDoc(value="Allows the items to be placed on top of the Altar, like the Arthana. By default only Arthana, Mystic Branch " +
            "and Kobolite Pentacle can be placed. NOTE: only those three have an effect on the Altar, any other placed item is just " +
            "decoration. Matching ignores NBT, and ignores the metadata of items that do not have subtypes (and of damageable " +
            "items); use a wildcard metadata (<item:*>) to match every metadata. Returns true if at least one item was added")
    public static boolean addPlacedItem(IIngredient ingredient) {
        boolean added = false;
        for (ItemStack stack : CraftTweakerMC.getIngredient(ingredient).getMatchingStacks()) {
            added |= PlacedItemApi.addPlacedItem(stack);
        }
        return added;
    }

    @ZenMethod
    @ZenDoc(value="The items can not be placed on top of the Altar anymore. Works on the default ones too (Arthana, Mystic Branch, " +
            "Kobolite Pentacle). Returns true if at least one item was removed")
    public static boolean removePlacedItem(IIngredient ingredient) {
        boolean removed = false;
        for (ItemStack stack : CraftTweakerMC.getIngredient(ingredient).getMatchingStacks()) {
            removed |= PlacedItemApi.removePlacedItem(stack);
        }
        return removed;
    }

    @ZenMethod
    @ZenDoc(value="Removes every item from the list of items that can be placed on top of the Altar, defaults included")
    public static void clearPlacedItems() {
        PlacedItemApi.clearPlacedItems();
    }

    @ZenMethod
    @ZenDoc(value="Makes the items Altar boosters when placed on top of the Altar (and makes them placeable). Increments for " +
            "recharge rate, max power, range and enhancement level are summed with those of every other booster. Each booster counts " +
            "only once per Altar, however many items trigger it. Defaults: Arthana range +1, Mystic Branch enhancement +1. " +
            "Registering an item again replaces its booster")
    public static void registerPlacedItemBooster(IIngredient ingredient, int rechargeIncrement, int powerIncrement, int rangeIncrement, int enhancementIncrement) {
        registerPlacedItemBooster(ingredient, rechargeIncrement, powerIncrement, rangeIncrement, enhancementIncrement, 1, 1, 1, 1);
    }

    @ZenMethod
    @ZenDoc(value="Makes the items Altar boosters when placed on top of the Altar (and makes them placeable), with increments and " +
            "multipliers for recharge rate, max power, range and enhancement level. All increments of all boosters are summed first, " +
            "then multipliers are applied. Each booster counts only once per Altar. Defaults: Arthana range +1, Mystic Branch " +
            "enhancement +1, Kobolite Pentacle recharge x2. Registering an item again replaces its booster")
    public static void registerPlacedItemBooster(IIngredient ingredient,
                                                 int rechargeIncrement, int powerIncrement, int rangeIncrement, int enhancementIncrement,
                                                 int rechargeMultiplier, int powerMultiplier, int rangeMultiplier, int enhancementMultiplier) {
        PlacedItemApi.PlacedItemBooster booster = PlacedItemApi.PlacedItemBooster.of(
                rechargeIncrement, powerIncrement, rangeIncrement, enhancementIncrement,
                rechargeMultiplier, powerMultiplier, rangeMultiplier, enhancementMultiplier);
        for (ItemStack stack : CraftTweakerMC.getIngredient(ingredient).getMatchingStacks()) {
            PlacedItemApi.registerBooster(stack, booster);
        }
    }

    @ZenMethod
    @ZenDoc(value="The items do not boost the Altar anymore when placed (they stay placeable, use removePlacedItem to change that). " +
            "Works on the default ones too. Returns true if at least one item was a booster")
    public static boolean unregisterPlacedItemBooster(IIngredient ingredient) {
        boolean removed = false;
        for (ItemStack stack : CraftTweakerMC.getIngredient(ingredient).getMatchingStacks()) {
            removed |= PlacedItemApi.unregisterBooster(stack);
        }
        return removed;
    }

    @ZenMethod
    @ZenDoc(value="Removes every placed item booster, defaults included: no placed item boosts the Altar anymore")
    public static void clearPlacedItemBoosters() {
        PlacedItemApi.clearBoosters();
    }

    @ZenMethod
    @ZenDoc(value="Returns true if the item can be placed on top of the Altar")
    public static boolean canPlaceItem(IItemStack stack) {
        return PlacedItemApi.canPlace(CraftTweakerMC.getItemStack(stack));
    }

}
