package com.smokeythebandicoot.witcherycompanion.patches.coven;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.api.CovenQuestApi;
import com.smokeythebandicoot.witcherycompanion.api.accessors.entities.covenwitch.ICovenWitchAccessor;
import com.smokeythebandicoot.witcherycompanion.api.covenquest.CovenQuestAcceptedEvent;
import com.smokeythebandicoot.witcherycompanion.api.covenquest.CovenQuestCheckEvent;
import com.smokeythebandicoot.witcherycompanion.api.covenquest.CovenQuestContext;
import com.smokeythebandicoot.witcherycompanion.api.covenquest.CovenQuestHandler;
import com.smokeythebandicoot.witcherycompanion.api.covenquest.CustomCovenQuest;
import com.smokeythebandicoot.witcherycompanion.api.covenquest.CustomCovenQuestDefinition;
import com.smokeythebandicoot.witcherycompanion.api.player.PlayerExtendedDataApi;
import com.smokeythebandicoot.witcherycompanion.api.progress.ProgressUtils;
import com.smokeythebandicoot.witcherycompanion.api.progress.WitcheryProgressEvent;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.msrandom.witchery.entity.passive.coven.CovenQuest;
import net.msrandom.witchery.entity.passive.coven.EntityCovenWitch;
import net.msrandom.witchery.entity.passive.coven.FightPetCovenQuest;
import net.msrandom.witchery.registry.WitcheryRegistry;
import net.msrandom.witchery.resources.CovenQuestManager;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * The part of the interaction between a player and a Coven Witch that W:C replaces (see EntityCovenWitchMixin), server side:
 * <ul>
 *     <li>offering a quest (first right-click): the quest is chosen by CovenQuestApi (levels, weights, requirements)</li>
 *     <li>accepting it (second right-click): same as W:R, and the CovenQuestAcceptedEvent is posted</li>
 *     <li>checking it (next right-clicks): only for custom quests, which are checked by their handler and the
 *     CovenQuestCheckEvent. Quests of W:R keep being checked by W:R</li>
 * </ul>
 * The messages and the order of the operations are the ones of W:R's EntityCovenWitch.processInteract.
 */
public final class CovenQuestInteraction {

    private CovenQuestInteraction() { }

    /**
     * @return true if the interaction was handled here (nothing else must happen), false if W:R has to handle it
     */
    public static boolean handle(EntityCovenWitch witch, ICovenWitchAccessor accessor, EntityPlayer player, EnumHand hand) {
        boolean accepted = accessor.witcherycompanion$accessor$isQuestAccepted();
        boolean offeredToPlayer = player.getUniqueID().equals(accessor.witcherycompanion$accessor$getQuestOfferedTo());

        if (!accepted) {
            if (!offeredToPlayer) {
                offerQuest(witch, accessor, player);
            } else {
                acceptQuest(witch, accessor, player, hand);
            }
            return true;
        }

        CovenQuest quest = accessor.witcherycompanion$accessor$getQuest();
        if (offeredToPlayer && quest instanceof CustomCovenQuest) {
            checkCustomQuest(witch, accessor, player, hand, (CustomCovenQuest) quest);
            return true;
        }
        return false;
    }

    // ---- First right-click: the witch offers a quest

    private static void offerQuest(EntityCovenWitch witch, ICovenWitchAccessor accessor, EntityPlayer player) {
        witch.enablePersistence();
        if (accessor.witcherycompanion$accessor$isCovenFull(player)) {
            accessor.witcherycompanion$accessor$say("full_coven", player);
            return;
        }

        Random random = witch.getRNG();
        ResourceLocation questId = chooseQuest(witch, player, random);
        if (questId == null) {
            accessor.witcherycompanion$accessor$say("not_interested_" + (random.nextInt(3) + 1), player);
            return;
        }

        CovenQuest quest = CovenQuestManager.INSTANCE.getRegistry().get(questId);
        accessor.witcherycompanion$accessor$setQuestOfferedTo(player.getUniqueID());
        accessor.witcherycompanion$accessor$setQuest(quest);
        if (quest == null) return;

        accessor.witcherycompanion$accessor$setQuestItemsNeeded(quest.getItemsNeeded());
        String offerText = quest instanceof CustomCovenQuest ? ((CustomCovenQuest) quest).getDefinition().getOfferText() : null;
        if (offerText != null) {
            sayRaw(witch, accessor, player, offerText, quest.getItemsNeeded());
        } else {
            accessor.witcherycompanion$accessor$talkQuest(questId.getNamespace(), questId.getPath(), player);
        }
    }

    /** Chooses the quest by level, weight and requirements. In Peaceful, a quest that needs a fight is chosen again, for the
     * number of times set in the config (fights cannot be won in Peaceful) */
    @Nullable
    private static ResourceLocation chooseQuest(EntityCovenWitch witch, EntityPlayer player, Random random) {
        int level = PlayerExtendedDataApi.getCovenSize(player);
        ResourceLocation questId = CovenQuestApi.pickQuest(player, level, random);

        int rerolls = ModConfig.PatchesConfiguration.EntityTweaks.covenWitch_tweakFightQuestsPeacefulRerolls;
        if (witch.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
            WitcheryRegistry<ResourceLocation, CovenQuest> registry = CovenQuestManager.INSTANCE.getRegistry();
            while (rerolls > 0 && questId != null && registry.get(questId) instanceof FightPetCovenQuest) {
                rerolls--;
                questId = CovenQuestApi.pickQuest(player, level, random);
            }
        }
        return questId;
    }

    // ---- Second right-click: the player accepts

    private static void acceptQuest(EntityCovenWitch witch, ICovenWitchAccessor accessor, EntityPlayer player, EnumHand hand) {
        if (accessor.witcherycompanion$accessor$isCovenFull(player)) {
            accessor.witcherycompanion$accessor$setQuestOfferedTo(null);
            accessor.witcherycompanion$accessor$say("full_coven", player);
            return;
        }

        accessor.witcherycompanion$accessor$setQuestAccepted(true);
        CovenQuest quest = accessor.witcherycompanion$accessor$getQuest();
        if (quest != null) {
            quest.activate(witch.world, witch, player);

            ResourceLocation questId = CovenQuestManager.INSTANCE.getRegistry().getKey(quest);
            CovenQuestContext context = new CovenQuestContext(player, witch, questId, player.getHeldItem(hand), hand,
                    PlayerExtendedDataApi.getCovenSize(player), accessor.witcherycompanion$accessor$getQuestItemsNeeded());

            if (quest instanceof CustomCovenQuest) {
                CustomCovenQuestDefinition definition = ((CustomCovenQuest) quest).getDefinition();
                runHandler(definition.getHandler(), context, true);
                if (definition.getAcceptText() != null) {
                    sayRaw(witch, accessor, player, definition.getAcceptText(), context.getItemsNeeded());
                }
            } else if (quest.getStartText() != null && questId != null) {
                accessor.witcherycompanion$accessor$talkQuest(questId.getNamespace(), quest.getStartText(), player);
            }
            MinecraftForge.EVENT_BUS.post(new CovenQuestAcceptedEvent(context));
        }
        accessor.witcherycompanion$accessor$playWitchTalk();
    }

    // ---- Next right-clicks: the player tries to complete a custom quest

    private static void checkCustomQuest(EntityCovenWitch witch, ICovenWitchAccessor accessor, EntityPlayer player,
                                         EnumHand hand, CustomCovenQuest quest) {
        if (accessor.witcherycompanion$accessor$isCovenFull(player)) {
            resetQuest(accessor);
            accessor.witcherycompanion$accessor$say("full_coven", player);
            return;
        }

        CustomCovenQuestDefinition definition = quest.getDefinition();
        ItemStack stack = player.getHeldItem(hand);
        boolean progressQuest = definition.getMode() == CustomCovenQuestDefinition.Mode.PROGRESS;
        if (!progressQuest && !quest.matches(stack)) {
            notFinished(witch, accessor, player, definition.getNotFinishedText(), accessor.witcherycompanion$accessor$getQuestItemsNeeded());
            return;
        }

        ResourceLocation questId = CovenQuestManager.INSTANCE.getRegistry().getKey(quest);
        int remaining = accessor.witcherycompanion$accessor$getQuestItemsNeeded();
        CovenQuestContext context = new CovenQuestContext(player, witch, questId, stack, hand,
                PlayerExtendedDataApi.getCovenSize(player), remaining);
        runHandler(definition.getHandler(), context, false);
        MinecraftForge.EVENT_BUS.post(new CovenQuestCheckEvent(context));

        boolean creative = player.capabilities.isCreativeMode;
        CovenQuestContext.Outcome outcome = context.getOutcome();
        int consumed = context.getItemsConsumed();
        if (outcome == null) {
            if (progressQuest) {
                outcome = CovenQuestContext.Outcome.NOT_FINISHED;
            } else {
                // Nobody answered: same as W:R, items are taken until the witch has all of them
                consumed = creative ? remaining : Math.min(remaining, stack.getCount());
                outcome = remaining - consumed <= 0 ? CovenQuestContext.Outcome.COMPLETE : CovenQuestContext.Outcome.PROGRESS;
            }
        }

        if (consumed > 0) {
            if (!creative) {
                consumed = Math.min(consumed, stack.getCount());
                stack.shrink(consumed);
            }
            remaining = Math.max(0, remaining - consumed);
            accessor.witcherycompanion$accessor$setQuestItemsNeeded(remaining);
        }

        switch (outcome) {
            case COMPLETE:
                completeQuest(witch, accessor, player, definition, context, questId);
                break;
            case PROGRESS:
                if (context.getMessage() != null) {
                    sayRaw(witch, accessor, player, context.getMessage(), remaining);
                } else if (definition.getProgressText() != null) {
                    sayRaw(witch, accessor, player, definition.getProgressText(), remaining);
                } else {
                    ITextComponent message = new TextComponentTranslation("entity.witchery.coven_witch.say.items_needed",
                            witch.getCustomNameTag(), remaining);
                    message.setStyle(new Style().setColor(TextFormatting.BLUE));
                    player.sendMessage(message);
                    accessor.witcherycompanion$accessor$playWitchTalk();
                }
                break;
            default:
                notFinished(witch, accessor, player, context.getMessage() != null ? context.getMessage() : definition.getNotFinishedText(), remaining);
                break;
        }
    }

    private static void completeQuest(EntityCovenWitch witch, ICovenWitchAccessor accessor, EntityPlayer player,
                                      CustomCovenQuestDefinition definition, CovenQuestContext context, @Nullable ResourceLocation questId) {
        if (accessor.witcherycompanion$accessor$addToPlayerCoven(player)) {
            String text = context.getMessage() != null ? context.getMessage() : definition.getCompleteText();
            if (text != null) {
                sayRaw(witch, accessor, player, text, 0);
            } else {
                accessor.witcherycompanion$accessor$say("joined_coven", player);
            }
            onJoined(player, questId, accessor.witcherycompanion$accessor$isCovenFull(player), witch);
        } else {
            // The witch was already in the coven of the player: same as W:R, she attacks
            accessor.witcherycompanion$accessor$say("tricked", player);
            witch.setRevengeTarget(player);
            witch.setAttackTarget(player);
            witch.setDrinkingPotion(true);
            resetQuest(accessor);
        }
    }

    private static void notFinished(EntityCovenWitch witch, ICovenWitchAccessor accessor, EntityPlayer player,
                                    @Nullable String text, int remaining) {
        if (text != null) {
            sayRaw(witch, accessor, player, text, remaining);
        } else {
            accessor.witcherycompanion$accessor$say("task_not_finished", player);
        }
    }

    // ---- Helpers

    /** The witch forgets the quest, as if the player had never talked to her */
    public static void resetQuest(ICovenWitchAccessor accessor) {
        accessor.witcherycompanion$accessor$setQuestAccepted(false);
        accessor.witcherycompanion$accessor$setQuestOfferedTo(null);
        accessor.witcherycompanion$accessor$setQuest(null);
    }

    /** Says a text that is not in the lang files, in the same way as W:R's witch: "&lt;name&gt; text" in blue */
    private static void sayRaw(EntityCovenWitch witch, ICovenWitchAccessor accessor, EntityPlayer player, String text, int remaining) {
        String formatted = text.replace("{remaining}", String.valueOf(remaining)).replace("{player}", player.getName());
        ITextComponent message = new TextComponentString("<" + witch.getCustomNameTag() + "> " + formatted);
        message.setStyle(new Style().setColor(TextFormatting.BLUE));
        player.sendMessage(message);
        accessor.witcherycompanion$accessor$playWitchTalk();
    }

    /** Runs the handler of a custom quest. A broken script must not crash the server: the error is logged and the quest is
     * left as if the handler had not answered */
    private static void runHandler(@Nullable CovenQuestHandler handler, CovenQuestContext context, boolean accept) {
        if (handler == null) return;
        try {
            if (accept) handler.onAccept(context); else handler.check(context);
        } catch (RuntimeException e) {
            WitcheryCompanion.logger.error("Error in the handler of the coven quest " + context.getQuestId(), e);
        }
    }

    /** Progress that a player gets when a witch joins the coven: coven size, quest completed, and all the quests when the
     * coven is full (so that no page of the book stays impossible to read, see EntityCovenWitchMixin) */
    public static void unlockJoinProgress(EntityPlayer player, @Nullable ResourceLocation questId, boolean covenFull) {
        ProgressUtils.unlockCovenSizeProgress(player, WitcheryProgressEvent.EProgressTriggerActivity.COVEN_SIZE.activityTrigger);

        if (questId != null) {
            ProgressUtils.unlockProgress(player, ProgressUtils.getCovenQuestSecret(questId),
                    WitcheryProgressEvent.EProgressTriggerActivity.COVEN_QUEST_FULFILLED.activityTrigger);
        }

        if (covenFull) {
            Iterator<Map.Entry<ResourceLocation, CovenQuest>> it = CovenQuestManager.INSTANCE.getRegistry().iterator();
            while (it.hasNext()) {
                ResourceLocation id = it.next().getKey();
                ProgressUtils.unlockProgress(player, ProgressUtils.getCovenQuestSecret(id),
                        WitcheryProgressEvent.EProgressTriggerActivity.COVEN_FULL.activityTrigger);
            }
        }
    }

    /** A witch joined the coven of the player: progress, and the other witches (loaded ones) that offered a quest to the player
     * forget it, so that their next quest is the one of the new coven size */
    public static void onJoined(EntityPlayer player, @Nullable ResourceLocation questId, boolean covenFull, @Nullable EntityCovenWitch joined) {
        unlockJoinProgress(player, questId, covenFull);
        resetOtherQuests(player, joined);
    }

    /** Every other witch that is offering (or waiting for the completion of) a quest to the player forgets it. This also
     * drops the items they still need: a player completes a quest at a time. Only the witches that are loaded are reached */
    public static void resetOtherQuests(EntityPlayer player, @Nullable EntityCovenWitch joined) {
        if (!ModConfig.PatchesConfiguration.EntityTweaks.covenWitch_tweakResetQuestsOnJoin) return;
        MinecraftServer server = player.getServer();
        if (server == null) return;

        UUID playerId = player.getUniqueID();
        for (WorldServer world : server.worlds) {
            for (Entity entity : world.loadedEntityList) {
                if (entity == joined || !(entity instanceof EntityCovenWitch)) continue;
                ICovenWitchAccessor other = (ICovenWitchAccessor) (Object) entity;
                if (playerId.equals(other.witcherycompanion$accessor$getQuestOfferedTo())) {
                    resetQuest(other);
                }
            }
        }
    }

}
