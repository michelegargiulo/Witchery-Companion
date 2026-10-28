package com.smokeythebandicoot.witcherycompanion.api.customrite;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;

import javax.annotation.Nullable;

/** The two ways in which the NBT of a sacrificed item can be matched against the NBT required by a custom rite */
public final class NbtMatch {

    private NbtMatch() { }

    /**
     * True if the actual NBT contains every entry of the required one. The actual NBT can have more entries. Compounds
     * are compared the same way, and a list matches if each required element is found in the actual list.
     * An empty required tag matches everything, including items without NBT.
     */
    public static boolean contains(NBTTagCompound required, @Nullable NBTTagCompound actual) {
        if (required.isEmpty()) return true;
        return NBTUtil.areNBTEquals(required, actual, true);
    }

    /** True if the actual NBT is identical to the required one. No NBT is the same as an empty tag */
    public static boolean exactly(NBTTagCompound required, @Nullable NBTTagCompound actual) {
        return required.equals(actual == null ? new NBTTagCompound() : actual);
    }

}
