package com.smokeythebandicoot.witcherypatcher.integrations.justenoughresources.base;

import mezz.jei.api.recipe.BlankRecipeWrapper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecipeWrapper extends BlankRecipeWrapper {

    protected String uId;

    public String getUid() {
        return uId;
    }

    @Nonnull
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return new ArrayList<>();
    }

}
