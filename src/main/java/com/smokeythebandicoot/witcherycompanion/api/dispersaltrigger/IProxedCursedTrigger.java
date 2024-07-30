package com.smokeythebandicoot.witcherycompanion.api.dispersaltrigger;

import com.smokeythebandicoot.witcherycompanion.patches.triggerdispersal.TileEntityCursedTrigger;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.msrandom.witchery.brewing.ModifiersImpact;
import net.msrandom.witchery.brewing.action.BrewActionList;

public interface IProxedCursedTrigger {

    /** This function is used to proxy the application of the curse effect from the block
     * to the inner TileEntity */
    default boolean absorbBrew(ModifiersImpact modifiers, BrewActionList actions) {
        TileEntityCursedTrigger trigger = getInnerTrigger();
        if (trigger == null) {
            trigger = this.createInnerTrigger();
            trigger.initialize(modifiers, actions);
        } else {
            trigger.updateCurse(modifiers, actions);
            return true;
        }
        return false;
    }

    /** This function proxes the activation from the outside block to the inner trigger */
    default boolean onTrigger(Entity entity) {
        TileEntityCursedTrigger cursedTrigger = this.getInnerTrigger();
        if (cursedTrigger == null || entity == null) return false;
        if (cursedTrigger.applyToEntityAndDestroy(entity)) {
            setInnerTrigger(null);
        }
        return true;
    }


    TileEntityCursedTrigger createInnerTrigger();

    TileEntityCursedTrigger getInnerTrigger();

    void setInnerTrigger(TileEntityCursedTrigger trigger);


    /** This function is required to save the Inner Trigger NBT data, so
     * that the trigger can survive a world reload. It is advised that before
     * calling this function, inside the outside TileEntity's readFromNBT,
     * the inner trigger is instantiated as a new TileEntityCursedTrigger */
    default void readTriggerFromNBT(NBTTagCompound tag) {
        TileEntityCursedTrigger trigger = createInnerTrigger();
        if (tag.hasKey("InnerTrigger") && trigger != null) {
            trigger.readFromNBT(tag.getCompoundTag("InnerTrigger"));
            this.setInnerTrigger(trigger);
        }
    }

    /** This function is required to read the Inner Trigger on world load */
    default NBTTagCompound writeTriggerToNBT(NBTTagCompound nbt) {
        TileEntityCursedTrigger trigger = getInnerTrigger();
        if (trigger != null) {
            NBTTagCompound triggerTag = trigger.writeToNBT(new NBTTagCompound());
            nbt.setTag("InnerTrigger", triggerTag);
        }
        return nbt;
    }

}
