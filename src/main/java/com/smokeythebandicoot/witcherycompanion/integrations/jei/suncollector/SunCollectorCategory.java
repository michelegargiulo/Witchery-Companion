package com.smokeythebandicoot.witcherycompanion.integrations.jei.suncollector;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.api.barkbelt.BarkBeltApi;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.integrations.jei.abstractbase.BaseRecipeCategory;
import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.init.WitcheryBlocks;
import net.msrandom.witchery.init.items.WitcheryEquipmentItems;
import net.msrandom.witchery.init.items.WitcheryGeneralItems;
import net.msrandom.witchery.init.items.WitcheryIngredientItems;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SunCollectorCategory extends BaseRecipeCategory<SunCollectorWrapper> {

    public static String UID = WitcheryCompanion.prefix("sun_collector");
    public static ResourceLocation backgroundTexture = new ResourceLocation(WitcheryCompanion.MODID, "textures/gui/sun_collector.png");

    private static final int PAGE_WIDTH = 6;
    private static final int PAGE_HEIGHT = 7;

    public SunCollectorCategory(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(backgroundTexture, 0, 0, 124, 44, 124, 44);
        icon = null;
        localizedName = I18n.format("witcherycompanion.gui.sun_collector.name");
    }

    public static boolean shouldRegister() {
        return ModConfig.IntegrationConfigurations.JeiIntegration.enableJeiSunCollector;
    }

    public static void register(IRecipeCategoryRegistration registry) {

        if (!shouldRegister()) return;

        IJeiHelpers jeiHelpers = registry.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registry.addRecipeCategories(new SunCollectorCategory(guiHelper));
    }

    public static void initialize(IModRegistry registry) {

        if (!shouldRegister()) return;

        try {
            IJeiHelpers jeiHelpers = registry.getJeiHelpers();
            IGuiHelper guiHelper = jeiHelpers.getGuiHelper();



            registry.addRecipes(getRecipes(guiHelper), UID);
            registry.addRecipeCatalyst(new ItemStack(WitcheryBlocks.SUN_COLLECTOR), UID);
        } catch (Throwable t) {
            WitcheryCompanion.logger.error(t);
        }
    }

    public static List<SunCollectorWrapper> getRecipes(IGuiHelper guiHelper) {
        return Collections.singletonList(
                new SunCollectorWrapper(
                        new ItemStack(WitcheryIngredientItems.QUARTZ_SPHERE),
                        new ItemStack(WitcheryGeneralItems.SUN_GRENADE)
                ));
    }

    @Nonnull
    @Override
    public String getUid() {
        return UID;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, SunCollectorWrapper sunCollectorWrapper, @Nonnull IIngredients iIngredients) {

        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        guiItemStacks.init(0, false, 31, 4);
        guiItemStacks.init(1, true, 84, 17);
        guiItemStacks.set(0, sunCollectorWrapper.input);
        guiItemStacks.set(1, sunCollectorWrapper.output);

    }


}
