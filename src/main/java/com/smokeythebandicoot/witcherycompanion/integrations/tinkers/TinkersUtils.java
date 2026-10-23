package com.smokeythebandicoot.witcherycompanion.integrations.tinkers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class TinkersUtils {

    /**
     * Returns true if the stack has the given Modifier, whether it was applied as a Modifier by the player
     * (TinkerData.Modifiers) or it comes from a Material/Modifier trait (Traits). Broken Tinkers items
     * (tools or armor) lose every effect, so they never count. Safe to call with empty stacks and null modifiers
     * (e.g. when the integration has not been initialized)
     **/
    public static boolean hasEffect(ItemStack stack, Modifier modifier) {
        if (modifier == null || stack == null || stack.isEmpty()) return false;
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) return false;
        String id = modifier.getIdentifier();
        if (!TinkerUtil.hasModifier(tag, id) && !TinkerUtil.hasTrait(tag, id)) return false;
        return !ToolHelper.isBroken(stack);
    }
}
