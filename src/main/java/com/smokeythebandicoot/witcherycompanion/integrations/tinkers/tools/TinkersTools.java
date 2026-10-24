package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.tools;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.tools.ToolCore;

/** Tinkers tools that are the equivalent of Witchery items **/
public class TinkersTools {

    public static ToolArthana ARTHANA;
    public static ToolBoline BOLINE;

    public static void registerItems(IForgeRegistry<Item> registry) {
        ARTHANA = register(registry, new ToolArthana(), "arthana");
        BOLINE = register(registry, new ToolBoline(), "boline");
    }

    /** Makes the tools craftable in the Tinkers Crafting Station / Tool Station. Must be called after registering the items **/
    public static void registerCrafting() {
        if (ARTHANA != null) TinkerRegistry.registerToolCrafting(ARTHANA);
        if (BOLINE != null) TinkerRegistry.registerToolCrafting(BOLINE);
    }

    private static <T extends ToolCore> T register(IForgeRegistry<Item> registry, T tool, String name) {
        tool.setTranslationKey(WitcheryCompanion.MODID + "." + name);
        tool.setRegistryName(new ResourceLocation(WitcheryCompanion.MODID, name));
        registry.register(tool);
        return tool;
    }
}
