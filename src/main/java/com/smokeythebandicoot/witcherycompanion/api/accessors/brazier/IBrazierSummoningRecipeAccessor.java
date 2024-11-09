package com.smokeythebandicoot.witcherycompanion.api.accessors.brazier;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityType;

public interface IBrazierSummoningRecipeAccessor {

    EntityType<EntityCreature> getSpawnedEntity();

    EntityType<EntityCreature> getExtraSpawnedEntity();

}
