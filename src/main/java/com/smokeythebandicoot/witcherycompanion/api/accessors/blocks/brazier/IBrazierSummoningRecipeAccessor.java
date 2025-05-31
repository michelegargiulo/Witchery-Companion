package com.smokeythebandicoot.witcherycompanion.api.accessors.blocks.brazier;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityType;

public interface IBrazierSummoningRecipeAccessor {

    EntityType<EntityCreature> witcherycompanion$accessor$getSpawnedEntity();

    EntityType<EntityCreature> witcherycompanion$accessor$getExtraSpawnedEntity();

}
