package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block.entity;

import com.smokeythebandicoot.witcherycompanion.api.dispersaltrigger.IProxedCursedTrigger;
import com.smokeythebandicoot.witcherycompanion.patches.triggerdispersal.TileEntityCursedTrigger;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.msrandom.witchery.block.entity.TileEntityBloodTrap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixins:
 * [Feature] Compat for Cursed Trigger
 */
@Mixin(TileEntityBloodTrap.class)
public abstract class TileEntityBloodTrapMixin extends TileEntity implements IProxedCursedTrigger {

    @Unique
    protected TileEntityCursedTrigger witcherycompanion$innerTrigger = null;

    @Override
    public TileEntityCursedTrigger getInnerTrigger() {
        // Updates world and pos in case the outer TE has been moved or when the
        // world is first loaded, as in this phase world is still null (after readFromNBT)
        if (witcherycompanion$innerTrigger != null) {
            witcherycompanion$innerTrigger.setWorld(this.world);
            witcherycompanion$innerTrigger.setPos(this.pos);
        }
        return witcherycompanion$innerTrigger;
    }

    @Override
    public TileEntityCursedTrigger createInnerTrigger() {
        witcherycompanion$innerTrigger = new TileEntityCursedTrigger();
        witcherycompanion$innerTrigger.setWorld(this.world);
        witcherycompanion$innerTrigger.setPos(this.pos);
        return witcherycompanion$innerTrigger;
    }

    @Override
    public void setInnerTrigger(TileEntityCursedTrigger trigger) {
        witcherycompanion$innerTrigger = trigger;
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
