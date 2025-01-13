package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block.entity;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.api.progress.ProgressUtils;
import com.smokeythebandicoot.witcherycompanion.api.progress.WitcheryProgressEvent;
import com.smokeythebandicoot.witcherycompanion.api.accessors.worshipstatue.ITileEntityWorshipStatueAccessor;
import net.minecraft.entity.player.EntityPlayer;
import net.msrandom.witchery.block.entity.TileEntityWorshipStatue;
import net.msrandom.witchery.block.entity.WitcheryTileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixins:
 * [Feature] Accessor for worship level
 * [Feature] Progress when players summons goblin Gods
 */
@Mixin(TileEntityWorshipStatue.class)
public abstract class TileEntityWorshipStatueMixin extends WitcheryTileEntity implements ITileEntityWorshipStatueAccessor {

    @Unique
    private int witchery_Patcher$worshipLevel = 0;

    private TileEntityWorshipStatueMixin(int worshipLevel) {
        this.witchery_Patcher$worshipLevel = worshipLevel;
    }

    @Inject(method = "updateWorshippersAndGetLevel", remap = false, at = @At("RETURN"))
    private void retrieveWorshipLevel(CallbackInfoReturnable<Integer> cir) {
        this.witchery_Patcher$worshipLevel = cir.getReturnValue();
    }

    @Override
    public int getWorshipLevel() {
        return this.witchery_Patcher$worshipLevel;
    }

    /** This Mixin unlocks a progress secret for hobgoblins when the player summons Mog and Gulg **/
    @Inject(method = "summonGoblinGods", remap = true, at = @At(value = "INVOKE", remap = true, ordinal = 1,
            target = "Lnet/minecraft/entity/EntityCreature;setAttackTarget(Lnet/minecraft/entity/EntityLivingBase;)V"))
    private void unlockSecretOnGodsSpawn(EntityPlayer player, double detectDistance, int spawnDistance, CallbackInfoReturnable<Boolean> cir) {
        ProgressUtils.unlockProgress(player, WitcheryCompanion.prefix("creatures/hobgoblin_worship"),
                WitcheryProgressEvent.EProgressTriggerActivity.SUMMON_GOBLIN_GODS.activityTrigger);
    }
}
