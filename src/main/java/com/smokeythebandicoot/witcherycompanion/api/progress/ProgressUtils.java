package com.smokeythebandicoot.witcherycompanion.api.progress;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.network.ProgressSync;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nonnull;

import static com.smokeythebandicoot.witcherycompanion.api.progress.CapabilityWitcheryProgress.WITCHERY_PROGRESS_CAPABILITY;

public class ProgressUtils {

    // Defined format for All Brew secret items: MODID:brewing/items/<namespace>:<path>:<meta>
    public static String getBrewActionSecret(@Nonnull ItemStack stack) {
        return WitcheryCompanion.prefix("brewing/items/" + stack.getItem().getRegistryName() + ":" + stack.getMetadata());
    }

    // Defined format for Secret Brazier Recipes: MODID:brazier/recipe/<recipeID>
    public static String getBrazierRecipeSecret(@Nonnull String recipeId) {
        return WitcheryCompanion.prefix("brazier/recipe/" + recipeId);
    }

    // Defined format for Secret Infused Spirit Effects: MODID:brazier/effect/<recipeID>
    public static String getSpiritEffectRecipeSecret(@Nonnull String effectId) {
        return WitcheryCompanion.prefix("conjuring/effect/" + effectId);
    }

    // Defined format for Symbol Effects: MODID:symbology/symbol/<recipeID>
    public static String getSymbolEffectSecret(@Nonnull String symbolId) {
        return WitcheryCompanion.prefix("symbology/symbol/" + symbolId);
    }

    // Defined format for Secret Rite Effects: MODID:circles/rite_effect/<riteID>
    public static String getRiteSecret(@Nonnull String riteId) {
        return WitcheryCompanion.prefix("circles/rite/" + riteId);
    }

    // Defined format for Secret Rite Effects: MODID:circles/rite_effect/<riteEffectID>
    public static String getRiteEffectSecret(@Nonnull String riteEffectId) {
        return WitcheryCompanion.prefix("circles/rite_effect/" + riteEffectId);
    }

    // Defined format for Distillery Recipe: MODID:distillery/recipe/<recipeId>
    public static String getDistilleryRecipeSecret(@Nonnull String recipeId) {
        return WitcheryCompanion.prefix("distillery/recipe/" + recipeId);
    }

    // Defined format for Distillery Recipe: MODID:distillery/recipe/<recipeId>
    public static String getCauldronRecipeSecret(@Nonnull String recipeId) {
        return WitcheryCompanion.prefix("cauldron/recipe/" + recipeId);
    }

    // Defined format for Dimensions: MODID:dimensions/<dimension_name>
    public static String getDimensionSecret(@Nonnull String dimension_name) {
        return WitcheryCompanion.prefix("dimensions/" + dimension_name);
    }

    // Defined format for Dimensions: MODID:kettle/<recipe_id>
    public static String getKettleSecret(@Nonnull String recipe_id) {
        return WitcheryCompanion.prefix("kettle/" + recipe_id);
    }



    /** Helper method that takes a player, a secret key and an activity type and retrieves player capability.
     * Should only be called server-side **/
    public static boolean unlockProgress(EntityPlayer player, String progressKey, String activity) {

        // Event and progress cannot continue if any element is null
        if (progressKey == null || activity == null || player instanceof FakePlayer)
            return false;

        // Retrieve progress
        IWitcheryProgress progress = player.getCapability(WITCHERY_PROGRESS_CAPABILITY, null);
        if (progress == null) {
            WitcheryCompanion.logger.warn("Could not unlock progress: capability is null; player: {}, key: {}, activity: {}",
                    player, progressKey, activity);
            return false;
        }

        // Unlock progress if it's new, otherwise ignore
        if (!progress.hasProgress(progressKey)) {
            progress.unlockProgress(progressKey);
            ProgressSync.serverRequest(player);
            return MinecraftForge.EVENT_BUS.post(new WitcheryProgressUnlockEvent(player, progressKey, activity));
        }

        return false;
    }

}
