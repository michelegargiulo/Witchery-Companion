package com.smokeythebandicoot.witcherycompanion.integrations.tinkers;


import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.utils.TinkerUtil;

public class Integration {

    public static final Integration INSTANCE;

    private Integration() { }

    static {
        INSTANCE = new Integration();
    }


    public static boolean hasTrait(ItemStack stack, String traitName) {
        if (stack == null || stack.getTagCompound() == null || traitName == null) return false;
        return TinkerUtil.hasTrait(stack.getTagCompound(), traitName);
    }

    public static boolean hasTrait(ItemStack stack, ECompanionTrait trait) {
        return hasTrait(stack, trait.getId());
    }


    public static boolean registerMaterials() {
        TinkerRegistry.addMaterial();
    }
}
