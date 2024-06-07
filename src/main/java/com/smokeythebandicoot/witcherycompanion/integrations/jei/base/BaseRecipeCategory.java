package com.smokeythebandicoot.witcherycompanion.integrations.jei.base;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.msrandom.witchery.WitcheryResurrected;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public abstract class BaseRecipeCategory<T extends IRecipeWrapper> implements IRecipeCategory<T> {

    protected IDrawableStatic background;
    protected IDrawableStatic icon;
    protected String localizedName;

    public BaseRecipeCategory() { }

    /** Child classes must override this method and return true only if all conditions
     * are met in order to register the category. This includes Config options and Dependencies */
    public static boolean shouldRegister() {
        return true;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return localizedName;
    }

    @Nonnull
    @Override
    public String getModName() {
        return WitcheryResurrected.MOD_PREFIX;
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Nullable
    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Nonnull
    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return Collections.emptyList();
    }
}
