package com.smokeythebandicoot.witcherycompanion.integrations.jei.base;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import mezz.jei.api.recipe.BlankRecipeWrapper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecipeWrapper extends BlankRecipeWrapper {

    @Nonnull
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return new ArrayList<>();
    }

}
