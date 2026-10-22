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


/** Api that returns the list of items required to make the specified brew,
 * with the specified properties and modifiers. Very WIP **/
public class BrewBuilder_OLD {

    private EffectBrewAction target;
    private ItemStack targetStack;

    private List<Ingredient> capacityStacks = new ArrayList<>();
    private List<Ingredient> powerStacks = new ArrayList<>();
    private List<Ingredient> durationStacks = new ArrayList<>();
    private List<Ingredient> modifiersStacks = new ArrayList<>();
    private Ingredient dispersalStacks;

    private BrewBuilder_OLD() { }

    public static BrewBuilder_OLD create() {
        return new BrewBuilder_OLD();
    }

    public static BrewBuilder_OLD create(EffectBrewAction action) {
        return (new BrewBuilder_OLD()).forAction(action);
    }

    public BrewBuilder_OLD forAction(EffectBrewAction action) {
        this.target = action;
        this.targetStack = action.getKey().toStack();
        return this;
    }

    public BrewBuilder_OLD forItem(ItemStack stack) {
        BrewAction action = BrewActionManager.INSTANCE.getAction(ItemKey.fromStack(stack));
        if (action instanceof EffectBrewAction) {
            this.target = (EffectBrewAction) action;
            this.targetStack = stack;
        }
        return this;
    }

    public BrewBuilder_OLD withMinimumCapacity() {

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

    public BrewBuilder_OLD withPower(int power) {
        return this;
    }

    public BrewBuilder_OLD withDuration(int duration) {
        return this;
    }

    public BrewBuilder_OLD withModifier() {
        return this;
    }

    public BrewBuilder_OLD withDispersal(Class<? extends Dispersal> dispersalType) {
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
