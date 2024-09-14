package com.smokeythebandicoot.witcherycompanion.api.spiriteffect;

import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.PatchouliApiIntegration;
import com.smokeythebandicoot.witcherycompanion.utils.Mods;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.msrandom.witchery.infusion.spirit.SpiritEffectRecipe;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;

public class SpiritEffectApi {

    private static final Map<ResourceLocation, SpiritEffectRecipe> spiritEffectRecipeMap = new HashMap<>();

    public static void reloadEffects(SortedSet<SpiritEffectRecipe> effects) {
        spiritEffectRecipeMap.clear();
        for (SpiritEffectRecipe effectRecipe : effects) {
            if ((Object)effectRecipe instanceof ISpiritEffectRecipeAccessor) {
                ISpiritEffectRecipeAccessor accessor = (ISpiritEffectRecipeAccessor) (Object) effectRecipe;
                ResourceLocation id = accessor.getId();
                spiritEffectRecipeMap.put(id, effectRecipe);
            }
        }
        if (Loader.isModLoaded(Mods.PATCHOULI)) {
            PatchouliApiIntegration.spiritEffectReloader.reloadFlags();
        }
    }

    /** Returns a SpiritEffectRecipe from ID **/
    public static SpiritEffectRecipe getById(ResourceLocation id) {
        return spiritEffectRecipeMap.getOrDefault(id, null);
    }

    /** Helper function that gets the ID for the recipe **/
    public static ResourceLocation getId(SpiritEffectRecipe recipe) {
        if ((Object)recipe instanceof ISpiritEffectRecipeAccessor) {
            ISpiritEffectRecipeAccessor accessor = (ISpiritEffectRecipeAccessor) (Object) recipe;
            return accessor.getId();
        }
        return null;
    }

    public static Iterator<Map.Entry<ResourceLocation, SpiritEffectRecipe>> getIterator() {
        return spiritEffectRecipeMap.entrySet().iterator();
    }

}
