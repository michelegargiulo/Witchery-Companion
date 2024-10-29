package com.smokeythebandicoot.witcherycompanion.mixins.witchery.integration.jei.SpinningWheelRecipeCategory;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.msrandom.witchery.init.data.recipes.WitcheryRecipeTypes;
import net.msrandom.witchery.integration.jei.SpinningWheelRecipeCategory;
import net.msrandom.witchery.recipe.SpinningWheelRecipe;
import net.msrandom.witchery.util.WitcheryUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

/**
 * Mixins:
 * [Bugfix] Fix Spinning Wheel JEI plugin not haivng any recipe
 */
@Mixin(SpinningWheelRecipeCategory.Companion.class)
public abstract class CompanionMixin {

    /** This Mixin solves a bug with the JEI plugin for Spinning Wheel, as during the loading phase the world is null. An
     * unnecessary null-check, returns early if world is null, while getRecipeManager(null) is perfectly valid and returns
     * the recipes. This breaks with Witchery compatibility-mode ON. This method injects on the CollectionsKt.emptyList()
     * call and returns the list populated with the Spinning Wheel recipes instead of an empty list **/
    @WrapOperation(method = "addRecipes", remap = false, at = @At(value = "INVOKE", remap = false,
            target = "Lkotlin/collections/CollectionsKt;emptyList()Ljava/util/List;"))
    public List<?> fixNullWorld(Operation<List<?>> original) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.spinningWheel_fixJeiPlugin) {
            return WitcheryUtils.getRecipeManager(null).getRecipesForType(WitcheryRecipeTypes.SPINNING_WHEEL);
        }
        return original.call();
    }


}
