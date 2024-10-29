package com.smokeythebandicoot.witcherycompanion.integrations.jei.mirror;

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
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.init.WitcheryBlocks;
import net.msrandom.witchery.init.items.WitcheryGeneralItems;
import net.msrandom.witchery.init.items.WitcheryIngredientItems;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class MirrorCategory extends BaseRecipeCategory<MirrorWrapper> {

    public static String UID = WitcheryCompanion.prefix("mirror");
    public static ResourceLocation backgroundTexture = new ResourceLocation(WitcheryCompanion.MODID, "textures/gui/mirror.png");


    public MirrorCategory(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(backgroundTexture, 0, 0, 124, 44, 124, 44);
        icon = null;
        localizedName = I18n.format("witcherycompanion.gui.mirror.name");
    }

    public static boolean shouldRegister() {
        return ModConfig.IntegrationConfigurations.JeiIntegration.enableJeiMirror;
    }

    public static void register(IRecipeCategoryRegistration registry) {

        if (!shouldRegister()) return;

        IJeiHelpers jeiHelpers = registry.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registry.addRecipeCategories(new MirrorCategory(guiHelper));
    }

    public static void initialize(IModRegistry registry) {

        if (!shouldRegister()) return;

        try {
            IJeiHelpers jeiHelpers = registry.getJeiHelpers();
            IGuiHelper guiHelper = jeiHelpers.getGuiHelper();



            registry.addRecipes(getRecipes(guiHelper), UID);
            registry.addRecipeCatalyst(new ItemStack(WitcheryBlocks.MIRROR), UID);
        } catch (Throwable t) {
            WitcheryCompanion.logger.error(t);
        }
    }

    public static List<MirrorWrapper> getRecipes(IGuiHelper guiHelper) {
        return Collections.singletonList(
                new MirrorWrapper(
                        new ItemStack(WitcheryIngredientItems.QUARTZ_SPHERE),
                        new ItemStack(WitcheryGeneralItems.DUP_GRENADE)
                ));
    }

    @Nonnull
    @Override
    public String getUid() {
        return UID;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, MirrorWrapper mirrorWrapper, @Nonnull IIngredients iIngredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        guiItemStacks.init(0, false, 7, 11);
        guiItemStacks.init(1, true, 103, 11);
        guiItemStacks.set(0, mirrorWrapper.input);
        guiItemStacks.set(1, mirrorWrapper.output);

    }


}
