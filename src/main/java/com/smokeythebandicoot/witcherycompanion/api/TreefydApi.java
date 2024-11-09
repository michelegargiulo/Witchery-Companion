package com.smokeythebandicoot.witcherycompanion.api;

import net.minecraft.item.Item;
import net.msrandom.witchery.init.WitcheryBlocks;
import net.msrandom.witchery.init.items.WitcheryIngredientItems;

public class TreefydApi {

    private static Item level1BoostItem = WitcheryIngredientItems.CREEPER_HEART;
    private static Item level2BoostItem = Item.getItemFromBlock(WitcheryBlocks.DEMON_HEART);


    public static Item getLevel1BoostItem() {
        return level1BoostItem;
    }

    public static void setLevel1BoostItem(Item level1BoostItem) {
        TreefydApi.level1BoostItem = level1BoostItem;
    }

    public static Item getLevel2BoostItem() {
        return level2BoostItem;
    }

    public static void setLevel2BoostItem(Item level2BoostItem) {
        TreefydApi.level2BoostItem = level2BoostItem;
    }
}
