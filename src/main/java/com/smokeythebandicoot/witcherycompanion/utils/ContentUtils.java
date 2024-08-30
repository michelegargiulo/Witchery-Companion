package com.smokeythebandicoot.witcherycompanion.utils;

import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.infusion.symbol.SymbolEffect;

public class ContentUtils {

    public static boolean isSymbolEffectEnabled(String effectId) {
        return SymbolEffect.REGISTRY.get(new ResourceLocation("witchery:" + effectId)) != null;
    }

}
