package com.smokeythebandicoot.witcherycompanion.integrations.jei.imp.gifts;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.api.InfernalImpApi;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.integrations.jei.base.BaseRecipeCategory;
import com.smokeythebandicoot.witcherycompanion.utils.LootTables;
import com.smokeythebandicoot.witcherycompanion.utils.ReflectionHelper;
import jeresources.api.drop.LootDrop;
import jeresources.util.LootTableHelper;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.msrandom.witchery.entity.EntityImp;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImpShinyCategoryk extends BaseRecipeCategory<ImpShinyWrapperk> {

    public static String UID = WitcheryCompanion.MODID + ":imp_shiny";
    public static ResourceLocation backgroundTexture = new ResourceLocation(WitcheryCompanion.MODID, "textures/gui/imp_shiny.png");
    public static ResourceLocation iconTexture = new ResourceLocation(WitcheryCompanion.MODID, "textures/gui/imp_shiny_icon.png");


    public ImpShinyCategoryk(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(backgroundTexture, 0, 0, 124, 22, 124, 22);
        icon = guiHelper.createDrawable(iconTexture, 0, 0, 32, 32, 32, 32);
        localizedName = new TextComponentTranslation("witcherycompanion.gui.imp_shiny.name").getFormattedText();
    }

    public static boolean shouldRegister() {
        return ModConfig.IntegrationConfigurations.JeiIntegration.enableJeiImpShinies;
    }

    public static void register(IRecipeCategoryRegistration registry) {

        IJeiHelpers jeiHelpers = registry.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registry.addRecipeCategories(new ImpShinyCategoryk(guiHelper));
    }

    public static void initialize(IModRegistry registry) {
        try {
            IJeiHelpers jeiHelpers = registry.getJeiHelpers();
            IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

            registry.addRecipes(getRecipes(guiHelper), UID);
        } catch (Throwable t) {
            WitcheryCompanion.logger.error(t);
        }
    }

    public static List<ImpShinyWrapperk> getRecipes(IGuiHelper guiHelper) {
        List<ImpShinyWrapperk> recipes = new ArrayList<>();

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
                    ItemStack[] extraItems = ReflectionHelper.getStaticField(EntityImp.class, "EXTRA_DROPS", false);
                    if (extraItems != null) {
                        lootStacks.addAll(Arrays.asList(extraItems));
                    }
                }
                recipes.add(new ImpShinyWrapperk(guiHelper, lootStacks, index));
            } else {
                recipes.add(new ImpShinyWrapperk(guiHelper, gift, index));
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
    public void setRecipe(IRecipeLayout recipeLayout, ImpShinyWrapperk impGiftWrapper, IIngredients iIngredients) {
        List<ItemStack> gifts = impGiftWrapper.giftsAtLevel;

        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        guiItemStacks.init(0, false, 2, 2);
        guiItemStacks.set(0, gifts);
    }


}
