package com.smokeythebandicoot.witcherycompanion.mixins.minecraft.world.loot.conditions;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.api.HegemonyApi;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.loot.conditions.MatchToolCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mixins:
 * [Tweak] Hegemony: Tinkers items count as the Witchery item they are the equivalent of in loot conditions
 * (e.g. the Arthana increasing the drops of some creatures, both in code and in loot tables)
 */
@Mixin(MatchToolCondition.class)
public abstract class MatchToolConditionMixin {

    @WrapOperation(method = "testCondition", remap = true, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/advancements/critereon/ItemPredicate;test(Lnet/minecraft/item/ItemStack;)Z"))
    private boolean hegemonyToolPredicate(ItemPredicate instance, ItemStack stack, Operation<Boolean> original) {
        boolean matches = original.call(instance, stack);
        if (!matches) {
            for (Item equivalent : HegemonyApi.getEquivalents(stack)) {
                if (original.call(instance, new ItemStack(equivalent))) {
                    return true;
                }
            }
        }
        return matches;
    }
}
