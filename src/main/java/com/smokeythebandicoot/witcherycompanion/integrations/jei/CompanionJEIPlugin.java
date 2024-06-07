package com.smokeythebandicoot.witcherycompanion.integrations.jei;

import com.smokeythebandicoot.witcherycompanion.integrations.jei.goblin.GoblinTradeCategory;
import com.smokeythebandicoot.witcherycompanion.integrations.jei.imp.gifts.ImpGiftCategory;
import mezz.jei.api.*;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;

@JEIPlugin
public class CompanionJEIPlugin implements IModPlugin {

    public static IJeiHelpers jeiHelpers;
    public static IGuiHelper guiHelper;
    public static IJeiRuntime jeiRuntime;


    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {

        jeiHelpers = registry.getJeiHelpers();
        guiHelper = jeiHelpers.getGuiHelper();

        GoblinTradeCategory.register(registry);
        ImpGiftCategory.register(registry);
    }

    @Override
    public void register(IModRegistry registry) {

        jeiHelpers = registry.getJeiHelpers();
        guiHelper = jeiHelpers.getGuiHelper();

        GoblinTradeCategory.initialize(registry);
        ImpGiftCategory.initialize(registry);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        CompanionJEIPlugin.jeiRuntime = jeiRuntime;
    }

}
