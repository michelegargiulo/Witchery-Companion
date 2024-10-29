package com.smokeythebandicoot.witcherycompanion.mixins.witchery.transformation;

import com.smokeythebandicoot.witcherycompanion.api.progress.ProgressUtils;
import com.smokeythebandicoot.witcherycompanion.api.progress.WitcheryProgress;
import com.smokeythebandicoot.witcherycompanion.api.progress.WitcheryProgressEvent;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.PatchouliApiIntegration;
import com.smokeythebandicoot.witcherycompanion.utils.ContentUtils;
import com.smokeythebandicoot.witcherycompanion.utils.Mods;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Loader;
import net.msrandom.witchery.extensions.PlayerExtendedData;
import net.msrandom.witchery.transformation.CreatureTrait;
import net.msrandom.witchery.transformation.CreatureTraitType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.patchouli.api.PatchouliAPI;

/**
 * Mixins:
 * [Feature] unlock progress when leveling up creature traits (vampire or werewolf)
 */
@Mixin(CreatureTrait.class)
public abstract class CreatureTraitMixin {

    @Shadow(remap = false)
    private int level;

    @Shadow(remap = false) @Final
    private PlayerExtendedData playerEx;

    @Shadow(remap = false) @Final
    private CreatureTraitType<?> type;


    /** This Mixin injects into the abstract class CreatureTrait, where all creature traits inherit from. As long as child
     * classes call super.setLevel() in their setLevel(), before actually setting their level, the progress should unlock
     * without problems. Mixin is injected at HEAD to check the previous level and fire Events accordingly **/
    @Inject(method = "setLevel", remap = false, at = @At("HEAD"))
    private void setLevelUnlockProgress(int newLevel, CallbackInfo ci) {
        // Retrieve player and data
        int prevLevel = this.level;
        PlayerExtendedData playerEx = this.playerEx;
        if (playerEx == null) return;
        EntityPlayer player = playerEx.getEntity();
        if (player.world.isRemote) return;

        // Set Patchouli Config flags, before launching progress event (that reloads book if Patchouli is installed)
        if (Loader.isModLoaded(Mods.PATCHOULI)) {
            for (int i = 1; i <= 10; i++) {
                String flagId = ProgressUtils.getCreatureTraitSecret(type, i);
                PatchouliAPI.instance.setConfigFlag(flagId, i <= newLevel);
            }
        }

        // Unlock or lock the progress
        if (newLevel > prevLevel) {
            // Fire an event for each unlocked event
            for (int j = prevLevel; j <= newLevel; j++) {
                if (!player.world.isRemote) {
                    // Retrieve secret id for all unlocked levels
                    String secretId = ProgressUtils.getCreatureTraitSecret(type, j);
                    ProgressUtils.unlockProgress(player, secretId,
                            WitcheryProgressEvent.EProgressTriggerActivity.TRAIT_LEVEL_UP.activityTrigger);
                }
            }
        }
        // Case where newLevel == level there's nothing to do
        else if (newLevel < prevLevel) {
            for (int j = newLevel; j <= prevLevel; j++) {
                if (!player.world.isRemote) {
                    // Retrieve secret id for all unlocked levels
                    String secretId = ProgressUtils.getCreatureTraitSecret(type, j);
                    ProgressUtils.lockProgress(player, secretId,
                            WitcheryProgressEvent.EProgressTriggerActivity.TRAIT_LEVEL_UP.activityTrigger);
                }
            }
        }

        // Reload book
        if (Loader.isModLoaded(Mods.PATCHOULI)) {
            PatchouliAPI.instance.reloadBookContents();
        }
    }

}
