package com.smokeythebandicoot.witcherycompanion.integrations.jei;

import com.smokeythebandicoot.witcherycompanion.integrations.jei.barkbelt.BarkBeltCategory;
import com.smokeythebandicoot.witcherycompanion.integrations.jei.goblin.GoblinTradeCategory;
import com.smokeythebandicoot.witcherycompanion.integrations.jei.imp.gifts.ImpGiftCategory;
import com.smokeythebandicoot.witcherycompanion.integrations.jei.imp.shinies.ImpShinyCategory;
import mezz.jei.api.*;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraftforge.fml.common.Loader;

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
        ImpShinyCategory.register(registry);
        // This category requires Just Enough Resources for Loot de-serialization
        if (Loader.isModLoaded("jeresources"))
            ImpGiftCategory.register(registry);
        BarkBeltCategory.register(registry);
    }

    @Override
    public void register(IModRegistry registry) {

        jeiHelpers = registry.getJeiHelpers();
        guiHelper = jeiHelpers.getGuiHelper();

        GoblinTradeCategory.initialize(registry);
        ImpShinyCategory.initialize(registry);
        // This category requires Just Enough Resources for Loot de-serialization
        if (Loader.isModLoaded("jeresources"))
            ImpGiftCategory.initialize(registry);
        BarkBeltCategory.initialize(registry);

    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        CompanionJEIPlugin.jeiRuntime = jeiRuntime;
    }

}
