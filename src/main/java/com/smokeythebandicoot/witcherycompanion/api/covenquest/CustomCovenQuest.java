package com.smokeythebandicoot.witcherycompanion.api.covenquest;

import net.minecraft.item.ItemStack;
import net.msrandom.witchery.entity.passive.coven.CovenQuest;

import javax.annotation.Nullable;

/**
 * Runtime object of a custom quest. It is a quest of W:R (it is stored in the registry of the CovenQuestManager, so the
 * player's coven saves it by id like any other quest), but the interaction with the witch is handled by
 * CovenQuestInteraction, which asks the definition what to do.
 */
public class CustomCovenQuest extends CovenQuest {

    private final CustomCovenQuestDefinition definition;

    public CustomCovenQuest(CustomCovenQuestDefinition definition) {
        // No start text: the accept message is sent by CovenQuestInteraction (inline text or none)
        super(null, definition.getItemsNeeded());
        this.definition = definition;
    }

    public CustomCovenQuestDefinition getDefinition() { return definition; }

    /** True if the stack counts for an ITEM quest: it is not empty and the matcher (if any) accepts it */
    public boolean matches(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return definition.getMatcher() == null || definition.getMatcher().test(stack);
    }

    @Override
    public boolean isQuestItem(@Nullable ItemStack stack) {
        return stack != null && definition.getMode() == CustomCovenQuestDefinition.Mode.ITEM && matches(stack);
    }

}
