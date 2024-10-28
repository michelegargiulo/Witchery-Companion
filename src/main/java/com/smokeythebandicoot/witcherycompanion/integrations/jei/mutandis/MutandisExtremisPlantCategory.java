package com.smokeythebandicoot.witcherycompanion.integrations.jei.mutandis;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.api.mutandis.MutandisApi;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.integrations.jei.abstractbase.BaseRecipeCategory;
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
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.init.items.WitcheryIngredientItems;

import javax.annotation.Nonnull;
import java.util.*;

public class MutandisExtremisPlantCategory extends BaseRecipeCategory<MutandisExtremisPlantWrapper> {

    public static String UID = WitcheryCompanion.prefix("mutandis_extremis_plant");
    public static ResourceLocation backgroundTexture = new ResourceLocation(WitcheryCompanion.MODID, "textures/gui/mutandis_plant.png");

    private static final int PAGE_WIDTH = 6;
    private static final int PAGE_HEIGHT = 7;

    public MutandisExtremisPlantCategory(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(backgroundTexture, 0, 0, 124, 144, 124, 144);
        icon = null;
        localizedName = I18n.format("witcherycompanion.gui.mutandis_plant.name");
    }

    public static boolean shouldRegister() {
        return ModConfig.IntegrationConfigurations.JeiIntegration.enableJeiMutandisPlant;
    }

    public static void register(IRecipeCategoryRegistration registry) {

        if (!shouldRegister()) return;

        IJeiHelpers jeiHelpers = registry.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registry.addRecipeCategories(new MutandisExtremisPlantCategory(guiHelper));
    }

    public static void initialize(IModRegistry registry) {

        if (!shouldRegister()) return;

        try {
            IJeiHelpers jeiHelpers = registry.getJeiHelpers();
            IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

            registry.addRecipes(getRecipes(guiHelper), UID);
            registry.addRecipeCatalyst(new ItemStack(WitcheryIngredientItems.MUTANDIS_EXTREMIS), UID);
        } catch (Throwable t) {
            WitcheryCompanion.logger.error(t);
        }
    }

    public static List<MutandisExtremisPlantWrapper> getRecipes(IGuiHelper guiHelper) {

        List<MutandisExtremisPlantWrapper> mutables = new ArrayList<>();
        HashMap<IBlockState, Ingredient> conversions = MutandisApi.getPlantConversions(true);

        int index = 0;
        boolean partialPage = false;
        MutandisExtremisPlantWrapper page = new MutandisExtremisPlantWrapper(guiHelper);
        for (IBlockState state : conversions.keySet()) {

            partialPage = true;
            index++;

            if (conversions.get(state) != null)
                page.addItem(conversions.get(state));
            else
                page.addItem(state);

            if (index == PAGE_WIDTH * PAGE_HEIGHT) {
                mutables.add(page);
                page = new MutandisExtremisPlantWrapper(guiHelper);
                partialPage = false;
                index = 0;
            }
        }

        if (partialPage) {
            mutables.add(page);
        }

        return mutables;

    }

    @Nonnull
    @Override
    public String getUid() {
        return UID;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, MutandisExtremisPlantWrapper mutandisPlantWrapper, IIngredients iIngredients) {

        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        int x = 0, y = 0;
        for (List<ItemStack> stacks: mutandisPlantWrapper.itemsAtPage) {
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


}
