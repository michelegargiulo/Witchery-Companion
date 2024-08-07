package com.smokeythebandicoot.witcherycompanion.integrations.jei.goblin;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.api.goblintrade.GoblinTradeApi;
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
import net.minecraft.village.MerchantRecipe;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class GoblinTradeCategory extends BaseRecipeCategory<GoblinTradeWrapper> {

    public static String UID = WitcheryCompanion.MODID + ":goblin_trade";
    public static ResourceLocation backgroundTexture = new ResourceLocation(WitcheryCompanion.MODID, "textures/gui/goblin_trade.png");
    public static ResourceLocation iconTexture = new ResourceLocation(WitcheryCompanion.MODID, "textures/gui/goblin_trade_icon.png");

    public GoblinTradeCategory(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(backgroundTexture, 0, 0, 124, 22, 124, 22);
        icon = guiHelper.createDrawable(iconTexture, 0, 0, 16, 16, 16, 16);
        localizedName = I18n.format("witcherycompanion.gui.goblin_trade.name");
    }

    public static boolean shouldRegister() {
        return ModConfig.IntegrationConfigurations.JeiIntegration.enableJeiGoblinTrades;
    }

    public static void register(IRecipeCategoryRegistration registry) {
        if (!shouldRegister()) return;

        IJeiHelpers jeiHelpers = registry.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registry.addRecipeCategories(new GoblinTradeCategory(guiHelper));
    }

    public static void initialize(IModRegistry registry) {
        if (!shouldRegister()) return;

        try {
            IJeiHelpers jeiHelpers = registry.getJeiHelpers();
            IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

            registry.addRecipes(getRecipes(guiHelper), UID);
        } catch (Throwable t) {
            WitcheryCompanion.logger.error(t.getStackTrace());
        }
    }

    public static List<GoblinTradeWrapper> getRecipes(IGuiHelper guiHelper) {

        List<GoblinTradeWrapper> recipes = new ArrayList<>();

        for (int id = 0; id < GoblinTradeApi.getProfessionCount(); id++) {

            GoblinTradeApi.GoblinProfession profession = GoblinTradeApi.getProfessionByID(id);
            if (profession == null) continue;

            for (MerchantRecipe goblinTrade : GoblinTradeApi.getTrades(id)) {
                recipes.add(new GoblinTradeWrapper(guiHelper, goblinTrade, profession.professionName));
            }
        }

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

        guiItemStacks.init(0, true, 21, 2);
        guiItemStacks.init(1, true, 45, 2);
        guiItemStacks.init(2, false, 100, 2);

        guiItemStacks.set(0, inputs.get(0));
        guiItemStacks.set(1, inputs.get(1));
        guiItemStacks.set(2, outputs.get(0));
    }

}
