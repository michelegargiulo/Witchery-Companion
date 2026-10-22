package com.smokeythebandicoot.witcherycompanion.integrations.baubles.mixins.init.items;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.integrations.baubles.items.FancifulThinkingCharm;
import net.minecraft.item.Item;
import net.msrandom.witchery.init.items.WitcheryIngredientItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mixins:
 * [Integration] Register Fanciful Thinking Charm in a new class
 */
@Mixin(WitcheryIngredientItems.class)
public abstract class WitcheryIngredientItemsMixin {

    @Shadow(remap = false)
    private static <T extends Item> T register(String name, T ingredient) {
        return null;
    }


    @WrapOperation(method = "<clinit>", remap = false, at = @At(value = "INVOKE", ordinal = 23, remap = false,
            target = "Lnet/msrandom/witchery/init/items/WitcheryIngredientItems;register(Ljava/lang/String;)Lnet/minecraft/item/Item;"))
    private static Item fancifulThinkingBauble(String name, Operation<Item> original) {
        return register("fanciful_thinking_charm", new FancifulThinkingCharm());
    }
}
