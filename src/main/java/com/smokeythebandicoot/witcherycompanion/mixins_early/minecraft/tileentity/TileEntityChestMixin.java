package com.smokeythebandicoot.witcherycompanion.mixins_early.minecraft.tileentity;

import com.smokeythebandicoot.witcherycompanion.api.dispersaltrigger.IProxedCursedTrigger;
import com.smokeythebandicoot.witcherycompanion.patches.triggerdispersal.TileEntityCursedTrigger;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityLockableLoot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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

    @Unique
    protected TileEntityCursedTrigger witchery_Patcher$innerTrigger = null;

    @Override
    public TileEntityCursedTrigger getInnerTrigger() {
        // Updates world and pos in case the outer TE has been moved or when the
        // world is first loaded, as in this phase world is still null (after readFromNBT)
        if (witchery_Patcher$innerTrigger != null) {
            witchery_Patcher$innerTrigger.setWorld(this.world);
            witchery_Patcher$innerTrigger.setPos(this.pos);
        }
        return witchery_Patcher$innerTrigger;
    }

    @Override
    public TileEntityCursedTrigger createInnerTrigger() {
        witchery_Patcher$innerTrigger = new TileEntityCursedTrigger();
        witchery_Patcher$innerTrigger.setWorld(this.world);
        witchery_Patcher$innerTrigger.setPos(this.pos);
        return witchery_Patcher$innerTrigger;
    }

    @Override
    public void setInnerTrigger(TileEntityCursedTrigger trigger) {
        witchery_Patcher$innerTrigger = trigger;
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
