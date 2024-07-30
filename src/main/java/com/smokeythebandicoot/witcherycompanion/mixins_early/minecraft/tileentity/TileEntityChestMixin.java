package com.smokeythebandicoot.witcherycompanion.mixins_early.minecraft.tileentity;

import com.smokeythebandicoot.witcherycompanion.api.dispersaltrigger.IProxedCursedTrigger;
import com.smokeythebandicoot.witcherycompanion.patches.triggerdispersal.TileEntityCursedTrigger;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityLockableLoot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 Mixins:
 [Feature] Add Triggered Dispersal compat
 */
@Mixin(TileEntityChest.class)
public abstract class TileEntityChestMixin extends TileEntityLockableLoot implements IProxedCursedTrigger {

    protected TileEntityCursedTrigger innerTrigger = null;

    @Override
    public TileEntityCursedTrigger getInnerTrigger() {
        // Updates world and pos in case the outer TE has been moved or when the
        // world is first loaded, as in this phase world is still null (after readFromNBT)
        if (innerTrigger != null) {
            innerTrigger.setWorld(this.world);
            innerTrigger.setPos(this.pos);
        }
        return innerTrigger;
    }

    @Override
    public TileEntityCursedTrigger createInnerTrigger() {
        innerTrigger = new TileEntityCursedTrigger();
        innerTrigger.setWorld(this.world);
        innerTrigger.setPos(this.pos);
        return innerTrigger;
    }

    @Override
    public void setInnerTrigger(TileEntityCursedTrigger trigger) {
        innerTrigger = trigger;
    }

    @Inject(method = "readFromNBT", remap = true, at = @At("TAIL"))
    private void injectInnerTriggerRead(NBTTagCompound tag, CallbackInfo ci) {
        this.readTriggerFromNBT(tag);
    }

    @Inject(method = "writeToNBT", remap = true, at = @At("TAIL"))
    private void injectInnerTriggerWrite(NBTTagCompound tag, CallbackInfoReturnable<NBTTagCompound> cir) {
        this.writeTriggerToNBT(tag);
    }
}
