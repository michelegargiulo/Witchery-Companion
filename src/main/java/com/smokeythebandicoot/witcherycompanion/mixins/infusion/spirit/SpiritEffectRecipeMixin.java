package com.smokeythebandicoot.witcherycompanion.mixins.infusion.spirit;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.api.spiriteffect.ISpiritEffectRecipeAccessor;
import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.infusion.spirit.SpiritEffectRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mixins:
 * [Feature] Expose unique key for recipe identification
 */
@Mixin(SpiritEffectRecipe.class)
public abstract class SpiritEffectRecipeMixin implements ISpiritEffectRecipeAccessor {

    @Unique
    protected ResourceLocation witchery_Patcher$key;

    /** SpiritEffectRecipes have the same key as the InfusedSpiritEffect that they produce, but the key
     * is only used internally to automatically build the Symbology book. Store and expose the key */
    @WrapOperation(method = "getDescription", remap = false, at = @At(value = "INVOKE", remap = true, ordinal = 0,
            target = "Lnet/minecraft/util/ResourceLocation;getNamespace()Ljava/lang/String;"))
    private String storeKey(ResourceLocation instance, Operation<String> original) {
        this.witchery_Patcher$key = instance;
        return original.call(instance);
    }

    @Override
    public ResourceLocation getId() {
        return this.witchery_Patcher$key;
    }
}
