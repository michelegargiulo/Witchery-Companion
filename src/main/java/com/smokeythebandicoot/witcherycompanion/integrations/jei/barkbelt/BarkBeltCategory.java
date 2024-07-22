package com.smokeythebandicoot.witcherycompanion.integrations.jei.barkbelt;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.api.BarkBeltApi;
import com.smokeythebandicoot.witcherycompanion.api.InfernalImpApi;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.integrations.jei.base.BaseRecipeCategory;
import com.smokeythebandicoot.witcherycompanion.integrations.jei.imp.shinies.ImpShinyWrapper;
import com.smokeythebandicoot.witcherycompanion.utils.LootTables;
import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import jeresources.api.drop.LootDrop;
import jeresources.util.LootTableHelper;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.msrandom.witchery.init.items.WitcheryEquipmentItems;
import net.msrandom.witchery.init.items.WitcheryFumeItems;
import net.msrandom.witchery.init.items.WitcheryIngredientItems;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class BarkBeltCategory extends BaseRecipeCategory<BarkBeltWrapper> {

    public static String UID = WitcheryCompanion.MODID + ":bark_belt";
    public static ResourceLocation backgroundTexture = new ResourceLocation(WitcheryCompanion.MODID, "textures/gui/bark_belt.png");
    public static ResourceLocation iconTexture = new ResourceLocation(WitcheryCompanion.MODID, "textures/gui/bark_belt_icon.png");

    private static final int PAGE_WIDTH = 6;
    private static final int PAGE_HEIGHT = 7;

    public BarkBeltCategory(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(backgroundTexture, 0, 0, 124, 144, 124, 144);
        //icon = guiHelper.createDrawable(iconTexture, 0, 0, 16, 16, 16, 16);
        icon = null;
        localizedName = I18n.format("witcherycompanion.gui.bark_belt.name");
    }

    public static boolean shouldRegister() {
        return ModConfig.IntegrationConfigurations.JeiIntegration.enableJeiBarkBelt;
    }

    public static void register(IRecipeCategoryRegistration registry) {

        if (!shouldRegister()) return;

        IJeiHelpers jeiHelpers = registry.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registry.addRecipeCategories(new BarkBeltCategory(guiHelper));
    }

    public static void initialize(IModRegistry registry) {

        if (!shouldRegister()) return;

        try {
            IJeiHelpers jeiHelpers = registry.getJeiHelpers();
            IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

            registry.addRecipes(getRecipes(guiHelper), UID);
            registry.addRecipeCatalyst(WitcheryEquipmentItems.BARK_BELT, UID);
        } catch (Throwable t) {
            WitcheryCompanion.logger.error(t);
        }
    }

    public static List<BarkBeltWrapper> getRecipes(IGuiHelper guiHelper) {

        List<BarkBeltWrapper> recipes = new ArrayList<>();
        Set<Set<IBlockState>> rechargers = BarkBeltApi.getRechargers();

        int index = 0;
        boolean partialPage = false;
        BarkBeltWrapper page = new BarkBeltWrapper(guiHelper);
        for (Set<IBlockState> states : rechargers) {
            page.addItemsForSlot(states.stream()
                    .map(Utils::blockstateToStack)
                    .collect(Collectors.toSet()));
            partialPage = true;
            index++;
            if (index == PAGE_WIDTH * PAGE_HEIGHT) {
                recipes.add(page);
                page = new BarkBeltWrapper(guiHelper);
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
    public void setRecipe(IRecipeLayout recipeLayout, BarkBeltWrapper barkBeltWrapper, IIngredients iIngredients) {

        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        int x = 0, y = 0;
        for (Set<ItemStack> stacks : barkBeltWrapper.itemsAtPage) {
            int index = y * PAGE_HEIGHT + x;
            guiItemStacks.init(index, true, x * (20) + 2, y * (20) + 2);
            guiItemStacks.set(index, new ArrayList<>(stacks));
            x++;
            if (x == PAGE_WIDTH) {
                x = 0;
                y++;
            }
        }
    }


}
