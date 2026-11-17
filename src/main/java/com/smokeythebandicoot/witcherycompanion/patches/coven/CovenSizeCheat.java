package com.smokeythebandicoot.witcherycompanion.patches.coven;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.api.progress.CapabilityWitcheryProgress;
import com.smokeythebandicoot.witcherycompanion.api.progress.IWitcheryProgress;
import com.smokeythebandicoot.witcherycompanion.api.progress.ProgressUtils;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.network.ProgressSync;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.msrandom.witchery.common.CovenWitchData;
import net.msrandom.witchery.entity.passive.coven.EntityCovenWitch;
import net.msrandom.witchery.extensions.PlayerExtendedData;
import net.msrandom.witchery.init.items.WitcheryGeneralItems;
import net.msrandom.witchery.util.WitcheryUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Server side of the Coven tab of the Creative Medallion: sets the number of witches in the coven of a player, ignoring the
 * requirements of the quests (no familiar needed, no quest to complete). Meant for testing.
 * <ul>
 *     <li>Witches that are added are new members, with a name and a skin chosen like a witch that spawns, and no quest</li>
 *     <li>When the size goes down, the last members of the coven are removed</li>
 *     <li>The coven size progress is updated: the keys of the sizes reached are unlocked (and the ones above the new
 *     size are locked, so that pages that depend on the size can be tested again). Reaching the full size also unlocks the
 *     quests, as completing the last quest does. Nothing else is locked again</li>
 *     <li>The other witches that were offering a quest to the player forget it, as when a witch joins</li>
 * </ul>
 */
public final class CovenSizeCheat {

    /** Request that only asks for the current size, without changing it */
    public static final int QUERY = -1;

    private CovenSizeCheat() { }

    /**
     * @param requestedSize the size to set, or QUERY
     * @return the size of the coven of the player after the request
     */
    public static int handle(EntityPlayer player, EnumHand hand, int requestedSize) {
        PlayerExtendedData data = WitcheryUtils.getExtension(player);
        if (data == null) return 0;

        if (requestedSize >= 0 && canUse(player, hand)) {
            setSize(player, data, Math.min(requestedSize, getMaxSize()));
        }
        return data.coven.size();
    }

    public static int getMaxSize() {
        return ModConfig.PatchesConfiguration.EntityTweaks.covenWitch_tweakMaxCovenSize;
    }

    /** Same conditions under which W:R lets a player use the medallion: they hold it, and are in creative (unless the
     * config says otherwise) */
    private static boolean canUse(EntityPlayer player, EnumHand hand) {
        if (!ModConfig.PatchesConfiguration.ItemTweaks.creativeMedallion_tweakCovenSizeTab) return false;
        if (player.getHeldItem(hand).getItem() != WitcheryGeneralItems.CREATIVE_MEDALLION) return false;
        return player.isCreative() || ModConfig.PatchesConfiguration.ItemTweaks.creativeMedallion_tweakDisableCreativeRequirement;
    }

    private static void setSize(EntityPlayer player, PlayerExtendedData data, int target) {
        Set<CovenWitchData> coven = data.coven;
        int before = coven.size();

        if (target > before) {
            for (int i = before; i < target; i++) {
                coven.add(createMember(player, i));
            }
        } else if (target < before) {
            List<CovenWitchData> members = new ArrayList<>(coven);
            for (int i = members.size() - 1; i >= target; i--) {
                coven.remove(members.get(i));
            }
        }
        updateProgress(player, before, target);
        CovenQuestInteraction.resetOtherQuests(player, null);
    }

    /** Locks the size keys above the new size and unlocks the ones up to it */
    private static void updateProgress(EntityPlayer player, int before, int target) {
        IWitcheryProgress progress = CapabilityWitcheryProgress.getWitcheryProgress(player);
        if (progress != null) {
            int highest = Math.max(before, getMaxSize());
            for (int size = target + 1; size <= highest; size++) {
                progress.lockProgress(ProgressUtils.getCovenSizeSecret(size));
            }
        }
        // Unlocks sizes 1..target (and all the quests, if the coven is full), and syncs every secret it unlocks
        CovenQuestInteraction.unlockJoinProgress(player, null, target >= getMaxSize());
        ProgressSync.serverRequest(player);
    }

    /** A new member: name and skin chosen by a fresh witch, like the ones of a coven that was recruited */
    private static CovenWitchData createMember(EntityPlayer player, int index) {
        String name = "Witch " + (index + 1);
        int variant = 0;
        try {
            EntityCovenWitch witch = new EntityCovenWitch(player.world);
            witch.onInitialSpawn(player.world.getDifficultyForLocation(player.getPosition()), null);
            if (!witch.getCustomNameTag().isEmpty()) name = witch.getCustomNameTag();
            variant = witch.getVariant();
        } catch (RuntimeException e) {
            WitcheryCompanion.logger.warn("Could not generate a name for a new coven member, using " + name, e);
        }
        return new CovenWitchData(name, variant, UUID.randomUUID(), null);
    }

}
