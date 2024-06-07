package com.smokeythebandicoot.witcherycompanion.integrations.jei.imp.gifts;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.integrations.jei.base.BaseRecipeCategory;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ImpGiftCategory extends BaseRecipeCategory<ImpGiftWrapper> {

    public static String UID = "witchery.imp_gift";
    public static ResourceLocation backgroundTexture = new ResourceLocation(WitcheryCompanion.MODID, "textures/gui/imp_gift.png");

    public ImpGiftCategory(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(backgroundTexture, 0, 0, 124, 22, 124, 22);
        localizedName = new TextComponentTranslation("witcherycompanion.gui.imp_gift.name").getFormattedText();
    }

    public static void register(IRecipeCategoryRegistration registry) {
        if (!ImpGiftCategory.shouldRegister()) return;

        IJeiHelpers jeiHelpers = registry.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registry.addRecipeCategories(new ImpGiftCategory(guiHelper));
    }

    public static void initialize(IModRegistry registry) {
        if (!ImpGiftCategory.shouldRegister()) return;

        try {
            IJeiHelpers jeiHelpers = registry.getJeiHelpers();
            IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

            registry.addRecipes(getRecipes(guiHelper), UID);
        } catch (Throwable t) {
            WitcheryCompanion.logger.error(t);
        }
    }

    public static List<ImpGiftWrapper> getRecipes(IGuiHelper guiHelper) {
        List<ImpGiftWrapper> recipes = new ArrayList<>();

        // There is only one recipe to add, composed of all the possible gifts that the Imp gives
        recipes.add(new ImpGiftWrapper(guiHelper));

        return recipes;
    }

    @Nonnull
    @Override
    public String getUid() {
        return UID;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, ImpGiftWrapper impGiftWrapper, IIngredients iIngredients) {

        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        // If no set gifts are set, return just one slot with all the possible gifts
        if (impGiftWrapper.lastGiftIndex < 0 && !impGiftWrapper.randomGifts.isEmpty()) {
            guiItemStacks.init(0, true, 3, 3);
            guiItemStacks.set(0, impGiftWrapper.randomGifts);
            return;
        }

        for (int i = 0; i <= impGiftWrapper.lastGiftIndex; i++) {
            guiItemStacks.init(i, true, 3 + i * 20, 20);
            if (impGiftWrapper.fixedGifts.containsKey(i)) {
                guiItemStacks.set(i, impGiftWrapper.fixedGifts.get(i));
            } else {
                guiItemStacks.set(i, impGiftWrapper.randomGifts);
            }
        }
    }

    public static boolean shouldRegister() {
        return ModConfig.IntegrationConfigurations.JeiIntegration.enableJeiImpGifts &&
                Loader.isModLoaded("jeresources");
    }
}
