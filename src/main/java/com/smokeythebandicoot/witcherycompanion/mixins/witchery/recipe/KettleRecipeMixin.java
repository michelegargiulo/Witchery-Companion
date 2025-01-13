package com.smokeythebandicoot.witcherycompanion.mixins.witchery.recipe;

import com.smokeythebandicoot.witcherycompanion.api.accessors.kettle.IKettleRecipeAccessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.msrandom.witchery.block.entity.TileEntityKettle;
import net.msrandom.witchery.recipe.KettleRecipe;
import net.msrandom.witchery.recipe.WitcheryRecipe;
import net.msrandom.witchery.recipe.WitcheryRecipeSerializer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;


/**
 * Mixins:
 * [Feature] Implement accessor to access required dimension
 */
@Mixin(KettleRecipe.class)
public abstract class KettleRecipeMixin extends WitcheryRecipe<TileEntityKettle> implements IKettleRecipeAccessor {

    @Shadow(remap = false) @Final
    private DimensionType dimension;

    private KettleRecipeMixin(ResourceLocation id, WitcheryRecipeSerializer<?> serializer) {
        super(id, serializer);
    }

    @Override
    public Integer getDimension() {
        if (this.dimension == null) return null;
        return this.dimension.getId();
    }
}
