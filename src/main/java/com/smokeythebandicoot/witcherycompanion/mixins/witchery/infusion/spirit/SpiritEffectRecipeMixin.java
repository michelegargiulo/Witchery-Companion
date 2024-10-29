package com.smokeythebandicoot.witcherycompanion.mixins.witchery.infusion.spirit;

import com.smokeythebandicoot.witcherycompanion.api.spiriteffect.ISpiritEffectRecipeAccessor;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.infusion.spirit.InfusedSpiritEffect;
import net.msrandom.witchery.infusion.spirit.SpiritEffectRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixins:
 * [Feature] Expose unique key for recipe identification
 */
@Mixin(SpiritEffectRecipe.class)
public abstract class SpiritEffectRecipeMixin implements ISpiritEffectRecipeAccessor {

    @Unique
    protected ResourceLocation witchery_Patcher$key;

    /**
     * SpiritEffectRecipes have the same key as the InfusedSpiritEffect that they produce, but the key
     * is only used internally to automatically build the Symbology book. Store and expose the key */
    @Inject(method = "<init>", remap = false, at = @At("TAIL"))
    private void storeKey(Object2IntMap<?> ghosts, boolean hidden, InfusedSpiritEffect result, CallbackInfo ci) {
        this.witchery_Patcher$key = InfusedSpiritEffect.REGISTRY.getKey(result);
    }

    @Override
    public ResourceLocation getId() {
        return this.witchery_Patcher$key;
    }
}
