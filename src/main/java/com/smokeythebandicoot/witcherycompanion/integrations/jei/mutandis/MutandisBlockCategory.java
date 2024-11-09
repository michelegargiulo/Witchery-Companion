package com.smokeythebandicoot.witcherycompanion.integrations.jei.mutandis;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.api.MutandisApi;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.integrations.jei.abstractbase.BaseRecipeCategory;
import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.init.items.WitcheryIngredientItems;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MutandisBlockCategory extends BaseRecipeCategory<MutandisBlockWrapper> {

    public static String UID = WitcheryCompanion.prefix("mutandis_block");
    public static ResourceLocation backgroundTexture = new ResourceLocation(WitcheryCompanion.MODID, "textures/gui/mutandis_block.png");


    public MutandisBlockCategory(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(backgroundTexture, 0, 0, 124, 44, 124, 44);
        icon = null;
        localizedName = I18n.format("witcherycompanion.gui.mutandis_block.name");
    }

    public static boolean shouldRegister() {
        return ModConfig.IntegrationConfigurations.JeiIntegration.enableJeiMutandisBlock;
    }

    public static void register(IRecipeCategoryRegistration registry) {

        if (!shouldRegister()) return;

        IJeiHelpers jeiHelpers = registry.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registry.addRecipeCategories(new MutandisBlockCategory(guiHelper));
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

    public static List<MutandisBlockWrapper> getRecipes(IGuiHelper guiHelper) {
        List<MutandisBlockWrapper> recipes = new ArrayList<>();

        for (Map.Entry<IBlockState, IBlockState> entry : MutandisApi.clayConversion.entrySet()) {
            recipes.add(new MutandisBlockWrapper(
                    Utils.blockstateToStack(entry.getKey()),
                    Utils.blockstateToStack(entry.getValue()),
                    true
            ));
        }

        for (Map.Entry<IBlockState, IBlockState> entry : MutandisApi.grassConversion.entrySet()) {
            recipes.add(new MutandisBlockWrapper(
                    Utils.blockstateToStack(entry.getKey()),
                    Utils.blockstateToStack(entry.getValue()),
                    false
            ));
        }

        return recipes;
    }

    @Nonnull
    @Override
    public String getUid() {
        return UID;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, MutandisBlockWrapper mutandisBlockWrapper, @Nonnull IIngredients iIngredients) {

        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        if (mutandisBlockWrapper.isClayConversion) {
            guiItemStacks.init(0, false, 2, 22);
            guiItemStacks.init(2, false, 2, 2);
            guiItemStacks.init(1, true, 106, 8);
            guiItemStacks.set(0, mutandisBlockWrapper.input);
            guiItemStacks.set(1, mutandisBlockWrapper.output);
            guiItemStacks.set(2, new ItemStack(Items.WATER_BUCKET));
        } else {
            guiItemStacks.init(0, false, 2, 10);
            guiItemStacks.init(1, true, 106, 10);
            guiItemStacks.set(0, mutandisBlockWrapper.input);
            guiItemStacks.set(1, mutandisBlockWrapper.output);
        }

    }


}
