package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors;

import com.smokeythebandicoot.witcherycompanion.api.brewing.BrewBuilder;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.base.BrewActionProcessor;
import net.minecraft.item.crafting.Ingredient;
import net.msrandom.witchery.brewing.InstantDispersal;
import net.msrandom.witchery.brewing.action.EffectBrewAction;
import vazkii.patchouli.api.IVariableProvider;

import java.util.ArrayList;
import java.util.List;

public class CauldronBrewProcessor extends BrewActionProcessor {

    protected int effectLevel = 0;
    protected List<Ingredient> ingredients;

    @Override
    public void setup(IVariableProvider<String> provider) {
        super.setup(provider);
        if (this.currentAction instanceof EffectBrewAction) {
            EffectBrewAction action = (EffectBrewAction) currentAction;
            this.effectLevel = action.getEffectLevel();

            this.ingredients = BrewBuilder.create().forAction(action)
                    .withMinimumCapacity().withDispersal(InstantDispersal.class).build();
        }
    }

    @Override
    public String process(String key) {
        switch (key) {
            case "effect_level":
                return String.valueOf(effectLevel);
            case "ingredients":
                return ProcessorUtils.serializeIngredientList(this.ingredients);
        }
        return super.process(key);
    }

    @Override
    protected void obfuscateFields() {
        this.effectLevel = -1;
        obfuscateIngredientList(this.ingredients);
    }

    @Override
    protected void hideFields() {
        this.effectLevel = -1;
        this.ingredients = new ArrayList<>();
    }

}
