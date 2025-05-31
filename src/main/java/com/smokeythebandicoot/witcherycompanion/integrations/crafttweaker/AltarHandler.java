package com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker;

import com.smokeythebandicoot.witcherycompanion.api.AltarApi;
import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlock;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import net.minecraft.block.Block;
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
    @ZenDoc(value="Registers a new IBlockState as an Altar Booster of the specified type and Altar boosting increments. Valid boosterTypes: SKULL, CANDLE, CHALICE")
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
    @ZenDoc(value="Registers all the valid IBlockStates of a block as an Altar Booster of the specified type. Valid boosterTypes: SKULL, CANDLE, CHALICE")
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
    @ZenDoc(value="Unregisters the specified IBlockState as Altar Booster of the specified type. Valid boosterTypes: SKULL, CANDLE, CHALICE")
    public static void unregisterAltarBooster(IBlockState state, String boosterType) {
        AltarApi.unregisterAltarBooster(
            CraftTweakerMC.getBlockState(state),
            Enum.valueOf(AltarApi.EAltarBoosterType.class, boosterType.toUpperCase())
        );
    }

    @ZenMethod
    @ZenDoc(value="Unregisters all valid IBlockStates of the Block as Altar Booster of the specified type. Valid boosterTypes: SKULL, CANDLE, CHALICE")
    public static void unregisterAltarBooster(IBlock block, String boosterType) {
        AltarApi.unregisterAltarBooster(
            CraftTweakerMC.getBlock(block),
            Enum.valueOf(AltarApi.EAltarBoosterType.class, boosterType.toUpperCase())
        );
    }

}
