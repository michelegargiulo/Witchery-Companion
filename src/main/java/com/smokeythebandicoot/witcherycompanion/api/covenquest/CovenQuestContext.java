package com.smokeythebandicoot.witcherycompanion.api.covenquest;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.entity.passive.coven.EntityCovenWitch;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * What happens in one interaction between a player and a Coven Witch about a quest: who, which quest, with what item.
 * When a quest is checked, the handler (or the event listeners) fill the answer with {@link #setComplete()},
 * {@link #setProgress(int)} or {@link #setNotFinished()}, optionally with {@link #setMessage(String)}.
 * If nobody answers, a quest that works with items behaves like the quests of W:R (the needed items are taken from the
 * stack, one by one, until the witch has all of them) and any other quest is not finished.
 */
public class CovenQuestContext {

    /** What the witch does after checking the quest */
    public enum Outcome {
        /** The task is not finished: nothing happens (the witch says so) */
        NOT_FINISHED,
        /** Some of the task was done: items are taken, the witch says how many are still needed */
        PROGRESS,
        /** The quest is completed: the witch joins the coven of the player */
        COMPLETE
    }

    private final EntityPlayer player;
    private final EntityCovenWitch witch;
    private final ResourceLocation questId;
    private final ItemStack stack;
    private final EnumHand hand;
    private final int covenSize;
    private final int itemsNeeded;

    private Outcome outcome;
    private int itemsConsumed;
    private String message;

    public CovenQuestContext(EntityPlayer player, EntityCovenWitch witch, ResourceLocation questId, ItemStack stack,
                             EnumHand hand, int covenSize, int itemsNeeded) {
        this.player = player;
        this.witch = witch;
        this.questId = questId;
        this.stack = stack;
        this.hand = hand;
        this.covenSize = covenSize;
        this.itemsNeeded = itemsNeeded;
    }

    public EntityPlayer getPlayer() { return player; }
    public EntityCovenWitch getWitch() { return witch; }
    public UUID getWitchId() { return witch.getUniqueID(); }
    public ResourceLocation getQuestId() { return questId; }

    /** Item that the player holds in the hand used to interact with the witch (can be empty). Should not be modified: the
     * items are taken with {@link #setProgress(int)} and {@link #setComplete(int)} */
    public ItemStack getStack() { return stack; }
    public EnumHand getHand() { return hand; }

    /** Number of witches in the coven of the player (the "level" of the quest) */
    public int getCovenSize() { return covenSize; }

    /** Items that the witch still needs, before this check. Only meaningful for quests that work with items */
    public int getItemsNeeded() { return itemsNeeded; }

    /** The task is not finished */
    public void setNotFinished() {
        this.outcome = Outcome.NOT_FINISHED;
        this.itemsConsumed = 0;
    }

    /** Some of the task was done, and "consumed" items are taken from the stack that the player holds */
    public void setProgress(int consumed) {
        this.outcome = Outcome.PROGRESS;
        this.itemsConsumed = Math.max(0, consumed);
    }

    /** The quest is completed, nothing is taken from the player */
    public void setComplete() {
        setComplete(0);
    }

    /** The quest is completed, and "consumed" items are taken from the stack that the player holds */
    public void setComplete(int consumed) {
        this.outcome = Outcome.COMPLETE;
        this.itemsConsumed = Math.max(0, consumed);
    }

    /** Text that the witch says instead of the one of the quest, for this check. Placeholders: {remaining} (items still
     * needed after this check) and {player} */
    public void setMessage(@Nullable String message) { this.message = message; }

    /** The answer, or null if nobody answered yet */
    @Nullable public Outcome getOutcome() { return outcome; }
    public int getItemsConsumed() { return itemsConsumed; }
    @Nullable public String getMessage() { return message; }

}
