package com.smokeythebandicoot.witcherycompanion.integrations.jei.altar;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.api.altar.AltarApi;
import com.smokeythebandicoot.witcherycompanion.api.infernalimp.InfernalImpApi;
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
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.init.WitcheryBlocks;
import net.msrandom.witchery.init.items.WitcheryEquipmentItems;

import javax.annotation.Nonnull;
import java.util.*;

public class AltarCategory extends BaseRecipeCategory<AltarWrapper> {

    public static String UID = WitcheryCompanion.prefix("altar");
    public static ResourceLocation backgroundTexture = new ResourceLocation(WitcheryCompanion.MODID, "textures/gui/altar.png");
    public static ResourceLocation iconTexture = new ResourceLocation(WitcheryCompanion.MODID, "textures/gui/altar_icon.png");

    private static final int PAGE_WIDTH = 6;
    private static final int PAGE_HEIGHT = 7;

    public AltarCategory(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(backgroundTexture, 0, 0, 124, 144, 124, 144);
        localizedName = I18n.format("witcherycompanion.gui.altar.name");
    }

    public static void register(IRecipeCategoryRegistration registry) {
        if (!AltarCategory.shouldRegister()) return;

        IJeiHelpers jeiHelpers = registry.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registry.addRecipeCategories(new AltarCategory(guiHelper));
    }

    public static void initialize(IModRegistry registry) {

        if (!AltarCategory.shouldRegister()) return;

        try {
            IJeiHelpers jeiHelpers = registry.getJeiHelpers();
            IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

            registry.addRecipes(getRecipes(guiHelper), UID);
            registry.addRecipeCatalyst(new ItemStack(WitcheryBlocks.ALTAR), UID);
        } catch (Throwable t) {
            WitcheryCompanion.logger.error(t);
        }
    }

    public static List<AltarWrapper> getRecipes(IGuiHelper guiHelper) {
        List<AltarWrapper> recipes = new ArrayList<>();

        int index = 0;
        boolean partialPage = false;
        AltarWrapper page = new AltarWrapper(guiHelper);
        //for (Ingredient ing : AltarApi.getRechargersRepresentativeItems()) {
        for (Map.Entry<Ingredient, AltarApi.AltarPowerSource> entry : AltarApi.getRechargersRepresentativeItems().entrySet()) {
            List<ItemStack> matching = new ArrayList<>();
            for (ItemStack stack : entry.getKey().getMatchingStacks()) {
                ItemStack withCount = stack.copy();
                withCount.setCount(entry.getValue().getFactor());
                matching.add(withCount);
            }
            page.itemsAtPage.add(matching);
            partialPage = true;
            index++;
            if (index == PAGE_WIDTH * PAGE_HEIGHT) {
                recipes.add(page);
                page = new AltarWrapper(guiHelper);
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
    public void setRecipe(IRecipeLayout recipeLayout, AltarWrapper altarWrapper, IIngredients iIngredients) {

        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        int x = 0, y = 0;
        for (List<ItemStack> stacks: altarWrapper.itemsAtPage) {
            int index = y * PAGE_HEIGHT + x;
            guiItemStacks.init(index, true, x * (20) + 2, y * (20) + 2);
            guiItemStacks.set(index, stacks);
            x++;
            if (x == PAGE_WIDTH) {
                x = 0;
                y++;
            }
        }
    }

    public static boolean shouldRegister() {
        return ModConfig.IntegrationConfigurations.JeiIntegration.enableJeiAltar;
    }
}
