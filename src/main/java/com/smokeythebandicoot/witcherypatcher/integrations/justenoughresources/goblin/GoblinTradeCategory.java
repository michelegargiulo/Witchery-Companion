package com.smokeythebandicoot.witcherypatcher.integrations.justenoughresources.goblin;

import com.smokeythebandicoot.witcherypatcher.WitcheryPatcher;
import com.smokeythebandicoot.witcherypatcher.config.ModConfig;
import com.smokeythebandicoot.witcherypatcher.integrations.justenoughresources.base.BaseRecipeCategory;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class GoblinTradeCategory extends BaseRecipeCategory<GoblinTradeWrapper> {

    public static boolean enabled = true;
    public static String UID;

    public  static void register(IRecipeCategoryRegistration registry) {
        enabled = ModConfig.IntegrationConfigurations.enableJeiGoblinTrades;
        if (!enabled) return;

        IJeiHelpers jeiHelpers = registry.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registry.addRecipeCategories(new GoblinTradeCategory(guiHelper));
    }

    public static void initialize(IModRegistry registry) {
        if (!enabled) return;

        try {
            IJeiHelpers jeiHelpers = registry.getJeiHelpers();
            IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

            registry.addRecipes(getRecipes(guiHelper), UID);
        } catch (Throwable t) {
            WitcheryPatcher.logger.error(t);
        }
    }

    public static List<GoblinTradeWrapper> getRecipes(IGuiHelper guiHelper) {
        List<GoblinTradeWrapper> recipes = new ArrayList<>();
        recipes.add(new GoblinTradeWrapper(guiHelper, testRecipe));
        return recipes;
    }

    @Nonnull
    @Override
    public String getUid() {
        return UID;
    }

    public void setRecipe(IRecipeLayout recipeLayout, GoblinTradeWrapper tradeWrapper, IIngredients ingredients) {
        List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
        List<List<ItemStack>> outputs = ingredients.getOutputs(ItemStack.class);

        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        guiItemStacks.init(0, true, 21, 14);
        guiItemStacks.init(1, true, 45, 14);
        guiItemStacks.init(2, false, 105, 14);

        guiItemStacks.set(0, inputs.get(0));
        guiItemStacks.set(1, inputs.get(1));
        guiItemStacks.set(2, outputs.get(0));
    }

}
