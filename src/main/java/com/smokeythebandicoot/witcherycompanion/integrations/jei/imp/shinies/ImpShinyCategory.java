package com.smokeythebandicoot.witcherycompanion.integrations.jei.imp.shinies;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.api.InfernalImpApi;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.integrations.jei.abstractbase.BaseRecipeCategory;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ImpShinyCategory extends BaseRecipeCategory<ImpShinyWrapper> {

    public static String UID = WitcheryCompanion.MODID + ":imp_shiny";
    public static ResourceLocation backgroundTexture = new ResourceLocation(WitcheryCompanion.MODID, "textures/gui/imp_shiny.png");
    public static ResourceLocation iconTexture = new ResourceLocation(WitcheryCompanion.MODID, "textures/gui/imp_shiny_icon.png");

    private static final int PAGE_WIDTH = 6;
    private static final int PAGE_HEIGHT = 7;

    public ImpShinyCategory(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(backgroundTexture, 0, 0, 124, 144, 124, 144);
        icon = guiHelper.createDrawable(iconTexture, 0, 0, 16, 16, 16, 16);
        localizedName = I18n.format("witcherycompanion.gui.imp_shiny.name");
    }

    public static void register(IRecipeCategoryRegistration registry) {
        if (!ImpShinyCategory.shouldRegister()) return;

        IJeiHelpers jeiHelpers = registry.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registry.addRecipeCategories(new ImpShinyCategory(guiHelper));
    }

    public static void initialize(IModRegistry registry) {

        if (!ImpShinyCategory.shouldRegister()) return;

        try {
            IJeiHelpers jeiHelpers = registry.getJeiHelpers();
            IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

            registry.addRecipes(getRecipes(guiHelper), UID);
        } catch (Throwable t) {
            WitcheryCompanion.logger.error(t);
        }
    }

    public static List<ImpShinyWrapper> getRecipes(IGuiHelper guiHelper) {
        List<ImpShinyWrapper> recipes = new ArrayList<>();

        HashMap<ItemStack, Integer> allShinies = InfernalImpApi.getShinies();

        int index = 0;
        boolean partialPage = false;
        ImpShinyWrapper page = new ImpShinyWrapper(guiHelper);
        for (ItemStack stack : allShinies.keySet()) {
            page.shiniesPage.put(stack, allShinies.get(stack));
            partialPage = true;
            index++;
            if (index == PAGE_WIDTH * PAGE_HEIGHT) {
                recipes.add(page);
                page = new ImpShinyWrapper(guiHelper);
                partialPage = false;
                index = 0;
            }
        }

        if (partialPage) {
            recipes.add(page);
        }
        return recipes;
    }

    @Nonnull
    @Override
    public String getUid() {
        return UID;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, ImpShinyWrapper impGiftWrapper, IIngredients iIngredients) {

        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        int x = 0, y = 0;
        for (ItemStack stack : impGiftWrapper.shiniesPage.keySet()) {
            int boost = impGiftWrapper.shiniesPage.get(stack);
            int index = y * PAGE_HEIGHT + x;
            guiItemStacks.init(index, true, x * (20) + 2, y * (20) + 2);
            String nameWithBoost = stack.getDisplayName() + " §6(shiny value: §e" + boost + "§6)";
            // The .copy() is necessary to avoid re-adding the affection boost each
            // time the JEI GUI is re-drawn
            guiItemStacks.set(index, stack.copy().setStackDisplayName(nameWithBoost));
            x++;
            if (x == PAGE_WIDTH) {
                x = 0;
                y++;
            }
        }
    }

    public static boolean shouldRegister() {
        return ModConfig.IntegrationConfigurations.JeiIntegration.enableJeiImpShinies;
    }
}
