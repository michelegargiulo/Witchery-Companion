package com.smokeythebandicoot.witcherycompanion.integrations.jei.base;

import mezz.jei.api.recipe.BlankRecipeWrapper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecipeWrapper extends BlankRecipeWrapper {

    protected String uid;

    public String getUid() {
        return uid;
    }

    @Nonnull
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return new ArrayList<>();
    }

}
