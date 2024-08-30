package com.smokeythebandicoot.witcherycompanion.integrations.jei.imp.gifts;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.api.infernalimp.InfernalImpApi;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.integrations.jei.abstractbase.BaseRecipeCategory;
import com.smokeythebandicoot.witcherycompanion.utils.LootTables;
import jeresources.api.drop.LootDrop;
import jeresources.util.LootTableHelper;
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
import net.minecraftforge.fml.common.Loader;
import net.msrandom.witchery.init.items.WitcheryFumeItems;
import net.msrandom.witchery.init.items.WitcheryIngredientItems;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImpGiftCategory extends BaseRecipeCategory<ImpGiftWrapper> {

    public static String UID = WitcheryCompanion.prefix("imp_gift");
    public static ResourceLocation backgroundTexture = new ResourceLocation(WitcheryCompanion.MODID, "textures/gui/imp_gift.png");
    public static ResourceLocation iconTexture = new ResourceLocation(WitcheryCompanion.MODID, "textures/gui/imp_gift_icon.png");


    public ImpGiftCategory(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(backgroundTexture, 0, 0, 124, 22, 124, 22);
        icon = guiHelper.createDrawable(iconTexture, 0, 0, 16, 16, 16, 16);
        localizedName = I18n.format("witcherycompanion.gui.imp_gift.name");
    }

    public static boolean shouldRegister() {
        return ModConfig.IntegrationConfigurations.JeiIntegration.enableJeiImpGifts && Loader.isModLoaded("jeresources");
    }

    public static void register(IRecipeCategoryRegistration registry) {

        if (!shouldRegister()) return;

        IJeiHelpers jeiHelpers = registry.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registry.addRecipeCategories(new ImpGiftCategory(guiHelper));
    }

    public static void initialize(IModRegistry registry) {

        if (!shouldRegister()) return;

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

        for (int index = 0; index <= InfernalImpApi.getLastGiftIndex(); index++) {
            ItemStack gift = InfernalImpApi.getGift(index);
            if (gift == null) {

                // Random gift. Check if Extra Drops override is enabled, so return hardcoded items
                // or generate loot based on loot table depending on current configuration
                List<ItemStack> lootStacks = new ArrayList<>();

                // Case 1: Stacks are decided by loot table
                if (ModConfig.PatchesConfiguration.EntityTweaks.flameImp_tweakCustomExtraItems) {
                    List<LootDrop> lootDrops = LootTableHelper.toDrops(LootTableHelper.getManager().getLootTableFromLocation(LootTables.IMP_GIFT));
                    for (LootDrop drop : lootDrops) {
                        lootStacks.add(drop.item);
                    }

                // Case 2: Stacks are hard-coded by Witchery
                } else {
                    // Since they're hard-coded, makes no sense to use reflection
                    lootStacks.addAll(Arrays.asList(
                            new ItemStack(WitcheryIngredientItems.BAT_WOOL, 5),
                            new ItemStack(WitcheryIngredientItems.DOG_TONGUE, 5),
                            new ItemStack(WitcheryIngredientItems.FROG_TOE, 2),
                            new ItemStack(WitcheryIngredientItems.OWLETS_WING, 2),
                            new ItemStack(WitcheryIngredientItems.ENT_TWIG, 1),
                            new ItemStack(WitcheryFumeItems.DEMONIC_BLOOD, 2),
                            new ItemStack(WitcheryIngredientItems.CREEPER_HEART, 2)));
                }
                recipes.add(new ImpGiftWrapper(guiHelper, lootStacks, index));
            } else {
                recipes.add(new ImpGiftWrapper(guiHelper, gift, index));
            }
        }

        return recipes;
    }

    @Nonnull
    @Override
    public String getUid() {
        return UID;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, ImpGiftWrapper impGiftWrapper, IIngredients iIngredients) {
        List<ItemStack> gifts = impGiftWrapper.giftsAtLevel;

        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        guiItemStacks.init(0, false, 2, 2);
        guiItemStacks.set(0, gifts);
    }


}
