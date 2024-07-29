package com.smokeythebandicoot.witcherycompanion.integrations.jei.abstractbase;

import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecipeWrapper implements IRecipeWrapper {

    @Nonnull
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return new ArrayList<>();
    }

}
