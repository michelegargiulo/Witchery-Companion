package com.smokeythebandicoot.witcherycompanion.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.KilledByPlayer;
import net.minecraft.world.storage.loot.conditions.LootCondition;

import java.util.Random;


public class LootConditionKilledInDimension implements LootCondition {

    private final int targetDimension;

    private final boolean inverted;

    public LootConditionKilledInDimension(int targetDimension, boolean inverted) {
        this.targetDimension = targetDimension;
        this.inverted = inverted;
    }

    @Override
    public boolean testCondition(Random rand, LootContext context) {
        int dimensionId = context.getWorld().provider.getDimension();
        return !inverted && dimensionId == targetDimension ||
                inverted && dimensionId != targetDimension;
    }

    public static class Serializer extends LootCondition.Serializer<LootConditionKilledInDimension> {
        protected Serializer()
        {
            super(new ResourceLocation(WitcheryCompanion.MODID,"killed_in_dimension"), LootConditionKilledInDimension.class);
        }

        public void serialize(JsonObject json, LootConditionKilledInDimension value, JsonSerializationContext context)
        {
            json.addProperty("target_dimension", Integer.valueOf(value.targetDimension));
            json.addProperty("inverted", Boolean.valueOf(value.inverted));
        }

        public LootConditionKilledInDimension deserialize(JsonObject json, JsonDeserializationContext context)
        {
            return new LootConditionKilledInDimension(
                    JsonUtils.getInt(json, "target_dimension", 0),
                    JsonUtils.getBoolean(json, "inverted", false)
            );
        }
    }
}
