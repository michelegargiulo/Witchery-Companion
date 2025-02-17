package com.smokeythebandicoot.witcherycompanion.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.msrandom.witchery.init.WitcheryDimensions;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;


@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class LootConditionKilledInSpiritWorld implements LootCondition {

    private final boolean inverted;

    public LootConditionKilledInSpiritWorld(boolean inverted) {
        this.inverted = inverted;
    }

    @Override
    public boolean testCondition(Random rand, LootContext context) {
        return !inverted && (context.getLootedEntity() == null || WitcheryDimensions.SPIRIT_WORLD.isInDimension(context.getLootedEntity()));
    }

    public static class Serializer extends LootCondition.Serializer<LootConditionKilledInSpiritWorld> {
        public Serializer() {
            super(new ResourceLocation(WitcheryCompanion.MODID,"killed_in_spirit_world"), LootConditionKilledInSpiritWorld.class);
        }

        public void serialize(JsonObject json, LootConditionKilledInSpiritWorld value, JsonSerializationContext context) {
            json.addProperty("inverted", value.inverted);
        }

        public LootConditionKilledInSpiritWorld deserialize(JsonObject json, JsonDeserializationContext context) {
            return new LootConditionKilledInSpiritWorld(
                    JsonUtils.getBoolean(json, "inverted", false)
            );
        }
    }
}
