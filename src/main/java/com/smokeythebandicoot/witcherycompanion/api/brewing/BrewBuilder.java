package com.smokeythebandicoot.witcherycompanion.api.brewing;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.msrandom.witchery.brewing.Dispersal;
import net.msrandom.witchery.brewing.ItemKey;
import net.msrandom.witchery.brewing.action.BrewAction;
import net.msrandom.witchery.brewing.action.CapacityBrewAction;
import net.msrandom.witchery.brewing.action.EffectBrewAction;
import net.msrandom.witchery.resources.BrewActionManager;

import java.util.ArrayList;
import java.util.List;

public class BrewBuilder {

    private EffectBrewAction target;
    private ItemStack targetStack;

    private List<Ingredient> capacityStacks = new ArrayList<>();
    private List<Ingredient> powerStacks = new ArrayList<>();
    private List<Ingredient> durationStacks = new ArrayList<>();
    private List<Ingredient> modifiersStacks = new ArrayList<>();
    private Ingredient dispersalStacks;

    private BrewBuilder() { }

    public static BrewBuilder create() {
        return new BrewBuilder();
    }

    public static BrewBuilder create(EffectBrewAction action) {
        return (new BrewBuilder()).forAction(action);
    }

    public BrewBuilder forAction(EffectBrewAction action) {
        this.target = action;
        this.targetStack = action.getKey().toStack();
        return this;
    }

    public BrewBuilder forItem(ItemStack stack) {
        BrewAction action = BrewActionManager.INSTANCE.getAction(ItemKey.fromStack(stack));
        if (action instanceof EffectBrewAction) {
            this.target = (EffectBrewAction) action;
            this.targetStack = stack;
        }
        return this;
    }

    public BrewBuilder withMinimumCapacity() {

        this.capacityStacks = new ArrayList<>();

        if (this.target == null) return this;

        // Build the list
        int currentCapacity = 0;
        int currentIndex = 0;

        while (currentCapacity < target.getEffectLevel()) {
            CapacityBrewAction action = BrewRegistry.getCapacity(currentIndex);
            if (action == null) return this;
            currentCapacity += action.getIncrement();
            capacityStacks.add(Ingredient.fromStacks(action.getKey().toStack()));
            currentIndex += 1;
        }

        return this;
    }

    public BrewBuilder withPower(int power) {
        return this;
    }

    public BrewBuilder withDuration(int duration) {
        return this;
    }

    public BrewBuilder withModifier() {
        return this;
    }

    public BrewBuilder withDispersal(Class<? extends Dispersal> dispersalType) {
        this.dispersalStacks = BrewRegistry.getDispersalIngredients(dispersalType);
        return this;
    }

    public List<Ingredient> build() {

        List<Ingredient> ingredients = new ArrayList<>();
        if (this.target == null) return ingredients;

        // Add enough capacity
        if (this.capacityStacks.isEmpty()) {
            withMinimumCapacity();
        }
        ingredients.addAll(this.capacityStacks);

        // Add main ingredient
        ingredients.add(Ingredient.fromStacks(targetStack));

        // Add dispersal
        ingredients.add(this.dispersalStacks);

        return ingredients;
    }


    public boolean hasEnoughCapacity() {
        return false;
    }

    public int getRequiredCapacity() {
        return 0;
    }

    public int getEccessCapacity() {
        return 0;
    }

    public int getPower() {
        return 0;
    }

    public int getDuration() {
        return 0;
    }

    public int getRequiredPower() {
        return 0;
    }

}
