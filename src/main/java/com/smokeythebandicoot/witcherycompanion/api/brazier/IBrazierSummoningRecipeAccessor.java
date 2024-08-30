package com.smokeythebandicoot.witcherycompanion.api.brazier;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityType;

public interface IBrazierSummoningRecipeAccessor {

    EntityType<EntityCreature> getSpawnedEntity();

    EntityType<EntityCreature> getExtraSpawnedEntity();

}
