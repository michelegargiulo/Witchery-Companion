package com.smokeythebandicoot.witcherycompanion.integrations.jei;

import com.smokeythebandicoot.witcherycompanion.integrations.jei.barkbelt.BarkBeltCategory;
import com.smokeythebandicoot.witcherycompanion.integrations.jei.goblin.GoblinTradeCategory;
import com.smokeythebandicoot.witcherycompanion.integrations.jei.imp.gifts.ImpGiftCategory;
import com.smokeythebandicoot.witcherycompanion.integrations.jei.imp.shinies.ImpShinyCategory;
import com.smokeythebandicoot.witcherycompanion.integrations.jei.suncollector.SunCollectorCategory;
import com.smokeythebandicoot.witcherycompanion.integrations.jei.wolfaltar.WolfAltarCategory;
import com.smokeythebandicoot.witcherycompanion.utils.Mods;
import mezz.jei.api.*;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nonnull;

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
        BarkBeltCategory.register(registry);
        SunCollectorCategory.register(registry);
        WolfAltarCategory.register(registry);

        // This category requires Just Enough Resources for Loot de-serialization
        if (Loader.isModLoaded(Mods.JER))
            ImpGiftCategory.register(registry);
    }

    @Override
    public void register(IModRegistry registry) {

        jeiHelpers = registry.getJeiHelpers();
        guiHelper = jeiHelpers.getGuiHelper();

        GoblinTradeCategory.initialize(registry);
        ImpShinyCategory.initialize(registry);
        BarkBeltCategory.initialize(registry);
        SunCollectorCategory.initialize(registry);
        WolfAltarCategory.initialize(registry);

        // This category requires Just Enough Resources for Loot de-serialization
        if (Loader.isModLoaded(Mods.JER))
            ImpGiftCategory.initialize(registry);

    }

    @Override
    public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
        CompanionJEIPlugin.jeiRuntime = jeiRuntime;
    }

}
