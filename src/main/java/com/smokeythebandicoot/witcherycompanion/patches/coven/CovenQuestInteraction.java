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
import net.minecraft.nbt.NBTTagCompound;
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
 *     <li>checking it (next right-clicks): for every quest (the ones of W:R too). The quest is checked like W:R does, and
 *     the CovenQuestCheckEvent is posted (and the handler is run, for custom quests), so that listeners can change what
 *     happens</li>
 * </ul>
 * The messages and the order of the operations are the ones of W:R's EntityCovenWitch.processInteract.
 * The witch also remembers the level (coven size) at which she chose the quest, in her Forge entity data: if the coven of the
 * player has a different size when they talk again (a witch joined while she was not loaded), she forgets the quest.
 */
public final class CovenQuestInteraction {

    /** Key of the NBT of W:R's fight quest drops with the UUID of the witch that sent the pet */
    private static final String QUEST_OWNER_KEY = "WitcheryQuestOwnerID";

    private CovenQuestInteraction() { }

    /** Key, in the Forge data of the witch, of the coven size at which she chose the quest she is offering */
    private static final String LEVEL_KEY = WitcheryCompanion.prefix("coven_quest_level");

    /**
     * @return true if the interaction was handled here (nothing else must happen), false if W:R has to handle it
     */
    public static boolean handle(EntityCovenWitch witch, ICovenWitchAccessor accessor, EntityPlayer player, EnumHand hand) {
        boolean accepted = accessor.witcherycompanion$accessor$isQuestAccepted();
        boolean offeredToPlayer = player.getUniqueID().equals(accessor.witcherycompanion$accessor$getQuestOfferedTo());

        if (offeredToPlayer && isStale(witch, player)) {
            // The coven has changed size since the quest was chosen: it is as if the player met the witch for the first time
            resetQuest(witch, accessor);
            accepted = false;
            offeredToPlayer = false;
        }

        if (!accepted) {
            if (!offeredToPlayer) {
                offerQuest(witch, accessor, player);
            } else {
                acceptQuest(witch, accessor, player, hand);
            }
            return true;
        }

        if (offeredToPlayer) {
            checkQuest(witch, accessor, player, hand);
            return true;
        }
        return false;
    }

    /** True if the witch chose her quest when the coven of the player had a different size. Quests that were offered before
     * the witch started to remember the level are never stale */
    private static boolean isStale(EntityCovenWitch witch, EntityPlayer player) {
        if (!ModConfig.PatchesConfiguration.EntityTweaks.covenWitch_tweakResetQuestsOnJoin) return false;
        NBTTagCompound data = witch.getEntityData();
        return data.hasKey(LEVEL_KEY) && data.getInteger(LEVEL_KEY) != PlayerExtendedDataApi.getCovenSize(player);
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
        witch.getEntityData().setInteger(LEVEL_KEY, PlayerExtendedDataApi.getCovenSize(player));
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

    // ---- Next right-clicks: the player tries to complete the quest

    private static void checkQuest(EntityCovenWitch witch, ICovenWitchAccessor accessor, EntityPlayer player, EnumHand hand) {
        if (accessor.witcherycompanion$accessor$isCovenFull(player)) {
            resetQuest(witch, accessor);
            accessor.witcherycompanion$accessor$say("full_coven", player);
            return;
        }

        CovenQuest quest = accessor.witcherycompanion$accessor$getQuest();
        CustomCovenQuestDefinition definition = quest instanceof CustomCovenQuest ? ((CustomCovenQuest) quest).getDefinition() : null;
        ResourceLocation questId = quest == null ? null : CovenQuestManager.INSTANCE.getRegistry().getKey(quest);
        int remaining = accessor.witcherycompanion$accessor$getQuestItemsNeeded();
        if (questId == null) {
            // W:R would crash here, a witch cannot check a quest she does not have
            accessor.witcherycompanion$accessor$say("task_not_finished", player);
            return;
        }

        ItemStack stack = player.getHeldItem(hand);
        boolean progressQuest = definition != null && definition.getMode() == CustomCovenQuestDefinition.Mode.PROGRESS;
        boolean relevant = progressQuest || (definition != null ? ((CustomCovenQuest) quest).matches(stack) : isQuestItemForWitch(stack, witch, quest));
        if (!relevant) {
            notFinished(witch, accessor, player, definition == null ? null : definition.getNotFinishedText(), remaining);
            return;
        }

        CovenQuestContext context = new CovenQuestContext(player, witch, questId, stack, hand,
                PlayerExtendedDataApi.getCovenSize(player), remaining);
        if (definition != null) runHandler(definition.getHandler(), context, false);
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

        String progressText = definition == null ? null : definition.getProgressText();
        switch (outcome) {
            case COMPLETE:
                completeQuest(witch, accessor, player, definition, context, questId);
                break;
            case PROGRESS:
                if (context.getMessage() != null) {
                    sayRaw(witch, accessor, player, context.getMessage(), remaining);
                } else if (progressText != null) {
                    sayRaw(witch, accessor, player, progressText, remaining);
                } else {
                    ITextComponent message = new TextComponentTranslation("entity.witchery.coven_witch.say.items_needed",
                            witch.getCustomNameTag(), remaining);
                    message.setStyle(new Style().setColor(TextFormatting.BLUE));
                    player.sendMessage(message);
                    accessor.witcherycompanion$accessor$playWitchTalk();
                }
                break;
            default:
                notFinished(witch, accessor, player, context.getMessage() != null ? context.getMessage()
                        : (definition == null ? null : definition.getNotFinishedText()), remaining);
                break;
        }
    }

    /** Same check as W:R's EntityCovenWitch.isQuestItemForEntity: the quest accepts the item, or the item is a drop of the pet
     * that the witch sent (fight quests) */
    private static boolean isQuestItemForWitch(ItemStack stack, EntityCovenWitch witch, CovenQuest quest) {
        if (stack.isEmpty()) return false;
        if (quest.isQuestItem(stack)) return true;
        NBTTagCompound tag = stack.getTagCompound();
        return tag != null && tag.hasUniqueId(QUEST_OWNER_KEY) && witch.getPersistentID().equals(tag.getUniqueId(QUEST_OWNER_KEY));
    }

    private static void completeQuest(EntityCovenWitch witch, ICovenWitchAccessor accessor, EntityPlayer player,
                                      @Nullable CustomCovenQuestDefinition definition, CovenQuestContext context, @Nullable ResourceLocation questId) {
        if (accessor.witcherycompanion$accessor$addToPlayerCoven(player)) {
            String text = context.getMessage() != null ? context.getMessage() : (definition == null ? null : definition.getCompleteText());
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
            resetQuest(witch, accessor);
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
    public static void resetQuest(EntityCovenWitch witch, ICovenWitchAccessor accessor) {
        accessor.witcherycompanion$accessor$setQuestAccepted(false);
        accessor.witcherycompanion$accessor$setQuestOfferedTo(null);
        accessor.witcherycompanion$accessor$setQuest(null);
        witch.getEntityData().removeTag(LEVEL_KEY);
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
                    resetQuest((EntityCovenWitch) entity, other);
                }
            }
        }
    }

}
