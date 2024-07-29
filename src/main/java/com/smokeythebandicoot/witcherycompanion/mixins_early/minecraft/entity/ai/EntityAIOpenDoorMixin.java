package com.smokeythebandicoot.witcherycompanion.mixins_early.minecraft.entity.ai;

import com.smokeythebandicoot.witcherycompanion.api.dispersaltrigger.ICursableTrigger;
import com.smokeythebandicoot.witcherycompanion.patches.triggerdispersal.TileEntityCursedTrigger;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIDoorInteract;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 Mixins:
 [Feature] Adds ICurseableTrigger triggers for when entities open/close doors
 */
@Mixin(EntityAIOpenDoor.class)
public abstract class EntityAIOpenDoorMixin extends EntityAIDoorInteract {

    @Shadow(remap = true)
    boolean closeDoor;

    private EntityAIOpenDoorMixin(EntityLiving entityIn) {
        super(entityIn);
    }

    /** This Mixin performs a trigger when the entity toggles the door */
    @Inject(method = "startExecuting", remap = true, at = @At("TAIL"))
    private void triggerAtStartExecuting(CallbackInfo ci) {
        witchery_Patcher$performTrigger();
    }

    /** This Mixin performs a trigger when the entity task is reset and the door is closed */
    @Inject(method = "resetTask", remap = true, at = @At("TAIL"))
    private void triggerAtResetTask(CallbackInfo ci) {
        if (this.closeDoor) {
            witchery_Patcher$performTrigger();
        }
    }

    /** This Mixin is actually responsible for triggering the effect and removing the TE when it runs out of curses */
    @Unique
    private void witchery_Patcher$performTrigger() {
        if (this.doorBlock instanceof ICursableTrigger) {
            ICursableTrigger cursedDoor = (ICursableTrigger) this.doorBlock;
            if (this.entity == null || this.entity.world == null) return;
            TileEntity tile = this.entity.world.getTileEntity(this.doorPosition);
            if (tile instanceof TileEntityCursedTrigger) {
                TileEntityCursedTrigger trigger = (TileEntityCursedTrigger)tile;
                if (cursedDoor.doApplyTrigger(this.entity, trigger)) {
                    this.entity.world.removeTileEntity(this.doorPosition);
                }
            }
        }
    }
}
