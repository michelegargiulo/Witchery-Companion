package com.smokeythebandicoot.witcherycompanion.integrations.jei.imp.gifts;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.integrations.api.InfernalImpApi;
import com.smokeythebandicoot.witcherycompanion.integrations.jei.base.BaseRecipeWrapper;
import com.smokeythebandicoot.witcherycompanion.utils.LootTables;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.msrandom.witchery.entity.EntityImp;
import net.msrandom.witchery.init.items.WitcheryFumeItems;
import net.msrandom.witchery.init.items.WitcheryIngredientItems;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class ImpGiftWrapper extends BaseRecipeWrapper {

    protected HashMap<Integer, ItemStack> fixedGifts;
    protected List<ItemStack> randomGifts;
    protected int highestIndex = -1;
    protected static List<ItemStack> lootGenCache = null;

    public ImpGiftWrapper(IGuiHelper guiHelper) {
        this(guiHelper, "witchery.imp_gift");
    }

    public ImpGiftWrapper(IGuiHelper guiHelper, String uidIn) {
        this.uid = uidIn;
        fixedGifts = new HashMap<>();
        randomGifts = new ArrayList<>();

        // Retrieve fixed gifts from Api
        highestIndex = InfernalImpApi.getLastGiftIndex();
        for (int i = 0; i < highestIndex; i++) {

        }

        // Retrieve random gifts from Loot Table
        if (ModConfig.PatchesConfiguration.EntityTweaks.flameImp_tweakCustomExtraItems) {
            if (lootGenCache == null) {
                //lootGenCache = LootTableUtils.genLoot(LootTables.IMP_GIFT);
            }
            randomGifts = lootGenCache;

        } else {
            // Those are the same defined by Witchery in EntityImp
            randomGifts.addAll(Arrays.asList(
                    new ItemStack(WitcheryIngredientItems.BAT_WOOL, 5),
                    new ItemStack(WitcheryIngredientItems.DOG_TONGUE, 5),
                    new ItemStack(WitcheryIngredientItems.FROG_TOE, 2),
                    new ItemStack(WitcheryIngredientItems.OWLETS_WING, 2),
                    new ItemStack(WitcheryIngredientItems.ENT_TWIG, 1),
                    new ItemStack(WitcheryFumeItems.DEMONIC_BLOOD, 2),
                    new ItemStack(WitcheryIngredientItems.CREEPER_HEART, 2)));
        }


        /*inputs.add(recipe.getItemToBuy().copy());
        if (recipe.hasSecondItemToBuy()) {
            inputs.add(recipe.getSecondItemToBuy().copy());
        }

        outputs.add(recipe.getItemToSell().copy());*/
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM, new ArrayList<>());
        //ingredients.setOutputs(ItemStack.class, outputs);
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

    }
}
