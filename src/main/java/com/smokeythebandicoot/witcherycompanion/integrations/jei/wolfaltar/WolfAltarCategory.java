package com.smokeythebandicoot.witcherycompanion.integrations.jei.wolfaltar;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.integrations.jei.abstractbase.BaseRecipeCategory;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.init.WitcheryBlocks;
import net.msrandom.witchery.init.items.WitcheryGeneralItems;
import net.msrandom.witchery.init.items.WitcheryIngredientItems;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class WolfAltarCategory extends BaseRecipeCategory<WolfAltarWrapper> {

    public static String UID = WitcheryCompanion.prefix("wolf_altar");
    public static ResourceLocation backgroundTexture = new ResourceLocation(WitcheryCompanion.MODID, "textures/gui/wolf_altar.png");

    private static final int PAGE_WIDTH = 6;
    private static final int PAGE_HEIGHT = 7;

    public WolfAltarCategory(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(backgroundTexture, 0, 0, 124, 44, 124, 44);
        icon = null;
        localizedName = I18n.format("witcherycompanion.gui.wolf_altar.name");
    }

    public static boolean shouldRegister() {
        return ModConfig.IntegrationConfigurations.JeiIntegration.enableJeiSunCollector;
    }

    public static void register(IRecipeCategoryRegistration registry) {

        if (!shouldRegister()) return;

        IJeiHelpers jeiHelpers = registry.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registry.addRecipeCategories(new WolfAltarCategory(guiHelper));
    }

    public static void initialize(IModRegistry registry) {

        if (!shouldRegister()) return;

        try {
            IJeiHelpers jeiHelpers = registry.getJeiHelpers();
            IGuiHelper guiHelper = jeiHelpers.getGuiHelper();



            registry.addRecipes(getRecipes(guiHelper), UID);
            registry.addRecipeCatalyst(new ItemStack(WitcheryBlocks.WOLF_ALTAR), UID);
        } catch (Throwable t) {
            WitcheryCompanion.logger.error(t);
        }
    }

    public static List<WolfAltarWrapper> getRecipes(IGuiHelper guiHelper) {
        return Collections.singletonList(
                new WolfAltarWrapper(
                        new ItemStack(Items.GOLD_INGOT, 3),
                        new ItemStack(WitcheryGeneralItems.MOON_CHARM),
                        2
                ));
    }

    @Nonnull
    @Override
    public String getUid() {
        return UID;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, WolfAltarWrapper wolfAltarWrapper, @Nonnull IIngredients iIngredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        guiItemStacks.init(0, false, 7, 11);
        guiItemStacks.init(1, true, 103, 11);
        guiItemStacks.set(0, wolfAltarWrapper.input);
        guiItemStacks.set(1, wolfAltarWrapper.output);
    }


}
