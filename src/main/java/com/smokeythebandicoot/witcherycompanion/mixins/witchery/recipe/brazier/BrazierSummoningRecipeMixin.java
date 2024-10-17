package com.smokeythebandicoot.witcherycompanion.mixins.witchery.recipe.brazier;

import com.smokeythebandicoot.witcherycompanion.api.brazier.IBrazierSummoningRecipeAccessor;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.recipe.WitcheryRecipeSerializer;
import net.msrandom.witchery.recipe.brazier.BrazierRecipe;
import net.msrandom.witchery.recipe.brazier.BrazierSummoningRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Mixins:
 * [Feature] Expose Spawned Entity and Extra Entity
 */
@Mixin(BrazierSummoningRecipe.class)
public abstract class BrazierSummoningRecipeMixin extends BrazierRecipe implements IBrazierSummoningRecipeAccessor {

    @Shadow(remap = false) @Final
    private EntityType<EntityCreature> entityType;

    @Shadow(remap = false) @Final
    private EntityType<EntityCreature> extra;


    private BrazierSummoningRecipeMixin(ResourceLocation id, WitcheryRecipeSerializer<?> serializer, NonNullList<Ingredient> inputs, int burnTime, boolean needsPower, boolean hidden) {
        super(id, serializer, inputs, burnTime, needsPower, hidden);
    }

    @Override
    public EntityType<EntityCreature> getSpawnedEntity() {
        return this.entityType;
    }

    @Override
    public EntityType<EntityCreature> getExtraSpawnedEntity() {
        return this.extra;
    }
}
