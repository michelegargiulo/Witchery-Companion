package com.smokeythebandicoot.witcherycompanion.mixins.block.entity;

import com.smokeythebandicoot.witcherycompanion.api.brazier.ITileEntityBrazierAccessor;
import com.smokeythebandicoot.witcherycompanion.api.progress.IWitcheryProgress;
import com.smokeythebandicoot.witcherycompanion.api.progress.ProgressUtils;
import com.smokeythebandicoot.witcherycompanion.api.progress.WitcheryProgressEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.msrandom.witchery.block.entity.TileEntityBrazier;
import net.msrandom.witchery.block.entity.WitcheryTileEntity;
import net.msrandom.witchery.recipe.brazier.BrazierRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.smokeythebandicoot.witcherycompanion.api.progress.CapabilityWitcheryProgress.WITCHERY_PROGRESS_CAPABILITY;

/**
 * Mixins:
 * [Feature] Unlock secret brazier recipes
 */
@Mixin(TileEntityBrazier.class)
public abstract class TileEntityBrazierMixin extends WitcheryTileEntity implements ITileEntityBrazierAccessor {

    @Shadow(remap = false)
    private BrazierRecipe recipe;

    @Unique
    private EntityPlayer witchery_Patcher$recipeOwner;

    /** This Mixin unlocks a brazier recipe in WitcheryProgression if the recipe is secret */
    @Inject(method = "update", remap = false, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/msrandom/witchery/recipe/brazier/BrazierRecipe;onBurnt(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"))
    private void unlockSecretKnowledge(CallbackInfo ci) {
        // Recipe Owner must be set to unlock their progress
        if (witchery_Patcher$recipeOwner == null)
            return;

        // Retrieve progress and unlock a new secret
        IWitcheryProgress progress = witchery_Patcher$recipeOwner.getCapability(WITCHERY_PROGRESS_CAPABILITY, null);
        if (progress != null && this.recipe.getHidden()) {
            ProgressUtils.unlockProgress(witchery_Patcher$recipeOwner, this.recipe.getId().toString(),
                    WitcheryProgressEvent.EProgressTriggerActivity.BRAZIER_RECIPE.activityTrigger);
        }

        // Reset the recipe owner
        this.witchery_Patcher$recipeOwner = null;
    }

    @Override
    public EntityPlayer getRecipeOwner() {
        return this.witchery_Patcher$recipeOwner;
    }

    @Override
    public void setRecipeOwner(EntityPlayer player) {
        this.witchery_Patcher$recipeOwner = player;
    }

}
