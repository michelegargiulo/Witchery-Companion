package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.api.progress.IWitcheryProgress;
import com.smokeythebandicoot.witcherycompanion.api.progress.ProgressUtils;
import com.smokeythebandicoot.witcherycompanion.api.progress.WitcheryProgressEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.msrandom.witchery.block.entity.TileEntityCircle;
import net.msrandom.witchery.block.entity.WitcheryTileEntity;
import net.msrandom.witchery.rite.RiteHandler;
import net.msrandom.witchery.rite.effect.RiteEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.atomic.AtomicInteger;

import static com.smokeythebandicoot.witcherycompanion.api.progress.CapabilityWitcheryProgress.WITCHERY_PROGRESS_CAPABILITY;

/**
 * Mixins:
 * [Feature] Unlock secret recipes
 */
@Mixin(TileEntityCircle.class)
public abstract class TileEntityCircleMixin extends WitcheryTileEntity {

    @WrapOperation(method = "update", at = @At(value = "INVOKE", ordinal = 1, remap = false,
            target = "Lnet/msrandom/witchery/rite/effect/RiteEffect;run(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;ILjava/util/concurrent/atomic/AtomicInteger;Lnet/msrandom/witchery/block/entity/TileEntityCircle$ActivatedRitual;)Lnet/msrandom/witchery/rite/RiteHandler$Result;"))
    private RiteHandler.Result unlockProgress(RiteEffect instance, World world, BlockPos blockPos, int ticks, AtomicInteger stage, TileEntityCircle.ActivatedRitual ritual, Operation<RiteHandler.Result> original) {

        // Store initial result, it should not be modified
        RiteHandler.Result result = original.call(instance, world, blockPos, ticks, stage, ritual);
        EntityPlayer player = ritual.getInitiatingPlayer(world);

        // If rite is hidden and player is not null, unlock progress for him
        if (player != null && ritual.rite != null && ritual.rite.getHidden()) {
            IWitcheryProgress progress = player.getCapability(WITCHERY_PROGRESS_CAPABILITY, null);
            if (progress == null) {
                WitcheryCompanion.logger.warn("Error while updating Witchery Progress: could not find capability");
            } else {
                ProgressUtils.unlockProgress(player, ritual.rite.getId().toString(),
                        WitcheryProgressEvent.EProgressTriggerActivity.CIRCLE_MAGIC.activityTrigger);
            }
        }

        // Return original result
        return result;
    }


}
