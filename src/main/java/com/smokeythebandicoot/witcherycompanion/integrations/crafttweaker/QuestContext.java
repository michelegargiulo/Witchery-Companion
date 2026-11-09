package com.smokeythebandicoot.witcherycompanion.integrations.crafttweaker;

import com.smokeythebandicoot.witcherycompanion.api.covenquest.CovenQuestContext;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IWorld;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * ZenScript view of an interaction between a player and a Coven Witch about a quest. Passed to the functions of
 * CovenQuestBuilder.setOnAccept and setOnCheck. The answer of setOnCheck is given with complete, progress and notFinished
 */
@ModOnly(value = "witchery")
@ZenClass("mods.smokeythebandicoot.witcherycompanion.QuestContext")
@ZenRegister
public class QuestContext {

    private final CovenQuestContext context;

    public QuestContext(CovenQuestContext context) {
        this.context = context;
    }

    @ZenGetter("questId")
    @ZenDoc(value = "Id of the quest, for example witcherycompanion:my_quest")
    public String getQuestId() {
        return String.valueOf(context.getQuestId());
    }

    @ZenGetter("world")
    @ZenDoc(value = "The world of the witch")
    public IWorld getWorld() {
        return CraftTweakerMC.getIWorld(context.getWitch().world);
    }

    @ZenGetter("player")
    @ZenDoc(value = "The player that is talking to the witch")
    public IPlayer getPlayer() {
        return CraftTweakerMC.getIPlayer(context.getPlayer());
    }

    @ZenGetter("witch")
    @ZenDoc(value = "The Coven Witch")
    public IEntity getWitch() {
        return CraftTweakerMC.getIEntity(context.getWitch());
    }

    @ZenGetter("witchUuid")
    @ZenDoc(value = "UUID of the Coven Witch")
    public String getWitchUuid() {
        return context.getWitchId().toString();
    }

    @ZenGetter("stack")
    @ZenDoc(value = "The item that the player holds in the hand used to talk to the witch. Null if the hand is empty")
    public IItemStack getStack() {
        ItemStack stack = context.getStack();
        return stack.isEmpty() ? null : CraftTweakerMC.getIItemStack(stack);
    }

    @ZenGetter("hand")
    @ZenDoc(value = "The hand used to talk to the witch: main_hand or off_hand")
    public String getHand() {
        return context.getHand().name().toLowerCase();
    }

    @ZenGetter("covenSize")
    @ZenDoc(value = "Number of witches in the coven of the player (the level of the quest)")
    public int getCovenSize() {
        return context.getCovenSize();
    }

    @ZenGetter("itemsNeeded")
    @ZenDoc(value = "Items that the witch still needs, before this check. Only meaningful for quests that work with items")
    public int getItemsNeeded() {
        return context.getItemsNeeded();
    }

    @ZenMethod
    @ZenDoc(value = "The quest is completed: the witch joins the coven of the player. Nothing is taken from the player")
    public void complete() {
        context.setComplete();
    }

    @ZenMethod
    @ZenDoc(value = "The quest is completed and 'consumed' items are taken from the item that the player holds")
    public void complete(int consumed) {
        context.setComplete(consumed);
    }

    @ZenMethod
    @ZenDoc(value = "Some of the task was done: 'consumed' items are taken from the item that the player holds, and the witch " +
            "says how many are still needed (use setMessage to say something else)")
    public void progress(int consumed) {
        context.setProgress(consumed);
    }

    @ZenMethod
    @ZenDoc(value = "The task is not finished: nothing is taken, and the witch says so")
    public void notFinished() {
        context.setNotFinished();
    }

    @ZenMethod
    @ZenDoc(value = "Text that the witch says, instead of the one of the quest, for this check. Placeholders: {remaining} " +
            "(items still needed after this check) and {player}")
    public void setMessage(String message) {
        context.setMessage(message);
    }

}
