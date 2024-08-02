package com.smokeythebandicoot.witcherycompanion.mixins_early.minecraft.tileentity;

import com.smokeythebandicoot.witcherycompanion.api.dispersaltrigger.IProxedCursedTrigger;
import com.smokeythebandicoot.witcherycompanion.patches.triggerdispersal.TileEntityCursedTrigger;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnderChest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(TileEntityEnderChest.class)
public abstract class TileEntityEnderChestMixin extends TileEntity implements IProxedCursedTrigger {
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

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        this.readTriggerFromNBT(tag);
        super.readFromNBT(tag);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag = this.writeTriggerToNBT(tag);
        return super.writeToNBT(tag);
    }
}
