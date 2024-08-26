package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.components;

import com.google.gson.annotations.SerializedName;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.CauldronCapacityProcessor;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.CauldronDispersalProcessor;
import net.minecraft.item.crafting.Ingredient;
import net.msrandom.witchery.brewing.action.BrewAction;
import net.msrandom.witchery.brewing.action.EffectBrewAction;
import net.msrandom.witchery.resources.BrewActionManager;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.VariableHolder;

import java.util.*;

public class CauldronBrewComponent implements ICustomComponent {

    // Json variables have to have the @SerializedName annotation
    /** ========== JSON VARIABLES ========== **/

    // The brew ID
    @SerializedName("cauldron_brew")
    @VariableHolder
    public String brewId = null;

    // The dispersal ID. Serialized internally. Defined the final dispersal item to insert. defaults to 'instant'
    // Possible values: 'instant', 'gas', 'liquid', 'triggered'
    @SerializedName("modifier_dispersal")
    @VariableHolder
    public String dispersalId = "instant";

    @SerializedName("augment_strength")
    @VariableHolder
    public int augmentStrength = 0;

    @SerializedName("augment_duration")
    @VariableHolder
    public int augmentDuration = 0;

    @SerializedName("x")
    @VariableHolder
    public int x = 0;

    @SerializedName("y")
    @VariableHolder
    public int y = 0;


    /** ========== NON-JSON VARIABLES ========== **/
    private transient List<Ingredient> stacks = null;
    private static transient Map<String, BrewEffectActionInfo> brewEffects = null;


    /** ========== OVERRIDES ========== **/
    /** Called when this component is built. Take the chance to read variables and set
     * any local positions here. */
    @Override
    public void build(int componentX, int componentY, int pageNum) {

        // Store position variables
        this.x = componentX;
        this.y = componentY;

        // Update effects map in case it's the first time
        if (brewEffects == null || brewEffects.isEmpty()) {
            updateEffectsMap();
        }

        // Start buulding stack list
        stacks = new ArrayList<>();

        if (!brewEffects.containsKey(brewId)) {
            return;
        }

        // Retrieve current brew
        BrewEffectActionInfo currentBrew = brewEffects.get(brewId);

        // First, insert enough capacity ingredients
        stacks.addAll(CauldronCapacityProcessor.getItemsForCapacity(currentBrew.level));

        stacks.add(currentBrew.stack);

        // Then add the effect key
        stacks.add(CauldronDispersalProcessor.getDispersal("instant"));

    }

    /** Called every render tick. No special transformations are applied, so you're responsible
     * for putting everything in the right place. */
    @Override
    public void render(IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
        if (stacks == null) return;
        int curX = this.x;
        int curY = this.y;
        for (Ingredient ingredient : stacks) {
            context.renderIngredient(curX, curY, mouseX, mouseY, ingredient);
            curX += 18; // 16 for the item, 2 for the spacing
        }
    }

    /** Called when this component first enters the screen. Good time to refresh anything that
     * can be dynamic. If you need to add buttons, you can add them here too. */
    public void onDisplayed(IComponentRenderContext context) {
        // NO-OP
    }

    /** Called on mouse click. Note that the click may not be inside your component, so
     * you need to validate the position. */
    public void mouseClicked(IComponentRenderContext context, int mouseX, int mouseY, int mouseButton) {
        // NO-OP
    }

    /** ========== HELPER METHODS ========== **/
    public static void updateEffectsMap() {
        brewEffects = new HashMap<>();
        for (BrewAction action : BrewActionManager.INSTANCE.getActions()) {
            if (action instanceof EffectBrewAction) {
                EffectBrewAction effectBrewAction = (EffectBrewAction) action;
                String effectId = effectBrewAction.getEffect().getKey(false, false);
                int level = effectBrewAction.getEffectLevel();
                Ingredient stack = Ingredient.fromStacks(effectBrewAction.getKey().toStack());
                boolean triggersRitual = effectBrewAction.triggersRitual();
                boolean isSecret = effectBrewAction.getHidden();
                brewEffects.put(effectId, new BrewEffectActionInfo(
                        stack, level, triggersRitual, isSecret
                ));
            }
        }
    }


    /** ========== HELPER CLASS ========== **/
    public static class BrewEffectActionInfo {

        public Ingredient stack;
        public int level;
        public boolean secret;
        public boolean ritual;

        public BrewEffectActionInfo(Ingredient stack, int level, boolean secret, boolean ritual) {
            this.stack = stack;
            this.level = level;
            this.secret = secret;
            this.ritual = ritual;
        }
    }

}
