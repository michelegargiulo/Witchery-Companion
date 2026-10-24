package com.smokeythebandicoot.witcherycompanion.integrations.tinkers;

import com.smokeythebandicoot.witcherycompanion.api.HegemonyApi;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.tools.ToolArthana;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.tools.ToolBoline;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.init.items.WitcheryGeneralItems;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tinkering.TinkersItem;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.tools.Pickaxe;

import java.util.List;

/** Registers the Tinkers items that are the equivalent of Witchery items into {@link HegemonyApi} **/
public class TinkersEquivalents {

    public static void registerDefaults() {
        HegemonyApi.register(WitcheryGeneralItems.getArthana(), stack ->
                stack.getItem() instanceof ToolArthana && !ToolHelper.isBroken(stack));

        HegemonyApi.register(WitcheryGeneralItems.BOLINE, stack ->
                stack.getItem() instanceof ToolBoline && !ToolHelper.isBroken(stack));

        // Any Tinkers tool with a Silver head, as a sword is a wooden stick with two silver ingots
        HegemonyApi.register(WitcheryGeneralItems.SILVER_SWORD, stack ->
                isHeadMadeOf(stack, "silver") && !ToolHelper.isBroken(stack));

        // Pickaxes with the Goblin's Favor trait (the Koboldite material grants it)
        HegemonyApi.register(WitcheryGeneralItems.KOBOLDITE_PICKAXE, stack ->
                stack.getItem() instanceof Pickaxe &&
                        TinkersUtils.hasEffect(stack, Integration.TRAIT_GOBLINS_FAVOR));
    }

    /** Returns true if the stack is a Tinkers tool whose head is made of the material with the given identifier **/
    public static boolean isHeadMadeOf(ItemStack stack, String materialIdentifier) {
        if (!(stack.getItem() instanceof TinkersItem) || stack.getTagCompound() == null) return false;
        List<PartMaterialType> parts = ((TinkersItem) stack.getItem()).getRequiredComponents();
        List<Material> materials = TinkerUtil.getMaterialsFromTagList(TagUtil.getBaseMaterialsTagList(stack));
        for (int i = 0; i < parts.size() && i < materials.size(); i++) {
            if (parts.get(i).usesStat(MaterialTypes.HEAD)) {
                return materialIdentifier.equals(materials.get(i).identifier);
            }
        }
        return false;
    }
}
