package com.smokeythebandicoot.witcherycompanion.mixins.witchery.recipe;

import com.smokeythebandicoot.witcherycompanion.api.CauldronApi;
import com.smokeythebandicoot.witcherycompanion.api.accessors.blocks.cauldron.ITileEntityCauldronAccessor;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.BlockTweaks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.msrandom.witchery.block.entity.TileEntityCauldron;
import net.msrandom.witchery.recipe.CauldronRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Mixins:
 * [Bugfix] Fix input matching of Cauldron recipes. Witchery loops over the inputs and, for each one, over the items in
 *    the cauldron, calling Iterator.remove() on every match, without stopping at the first. This throws an
 *    IllegalStateException (crashing the server) when two items satisfy the same input (a recipe that needs the same
 *    item twice, or an OreDict/wildcard ingredient matched by two different items), and counts a single item as many
 *    times as it appears in the recipe (a recipe that needs 2x A is satisfied by 1x A).
 *    It also only looks at brewing ingredients. Here every input is paired with a different stack.
 */
@Mixin(CauldronRecipe.class)
public abstract class CauldronRecipeMixin {

    @Shadow(remap = false) @Final
    private NonNullList<CauldronRecipe.PoweredItem> inputs;

    @Shadow(remap = false) @Final
    private ItemStack result;

    @Inject(method = "craft(Lnet/msrandom/witchery/block/entity/TileEntityCauldron;)Lnet/minecraft/item/ItemStack;",
            remap = false, at = @At("HEAD"), cancellable = true)
    private void witcherycompanion$matchInputsOneToOne(TileEntityCauldron cauldron, CallbackInfoReturnable<ItemStack> cir) {
        if (!BlockTweaks.witchsCauldron_fixCustomRecipeIngredients) return;

        List<ItemStack> pool = ((ITileEntityCauldronAccessor) cauldron).witcherycompanion$accessor$getCraftingPool();
        cir.setReturnValue(CauldronApi.areInputsSatisfied(this.inputs, pool) ? this.result.copy() : ItemStack.EMPTY);
    }

}
