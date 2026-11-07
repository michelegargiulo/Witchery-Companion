package com.smokeythebandicoot.witcherycompanion.api.accessors.spiritworld;

import net.minecraft.nbt.NBTTagList;

/** Accessor for the stacks of extra slots that are stored together with W:R's SpiritWorldData.PlayerData */
public interface ISpiritPlayerDataAccessor {

    /** Entries are compounds with Provider (string), Slot (int) and Stack (compound). Never null, live list */
    NBTTagList witcherycompanion$accessor$getExtraSlots();

    void witcherycompanion$accessor$setExtraSlots(NBTTagList list);

}
