package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.mixins.rite.sacrifice;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.Hegemony;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.msrandom.witchery.rite.sacrifice.ItemRiteSacrifice;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mixins:
 * [Tweak] Hegemony: Tinkers items can be used in rites in place of the Witchery item they are the equivalent of
 */
@Mixin(ItemRiteSacrifice.class)
public abstract class ItemRiteSacrificeMixin {

    /** Rite items are matched with Ingredients, both when checking the requirements and when consuming them. If the
     * Ingredient does not accept a Tinkers item, it is tested again with the Witchery item it is the equivalent of.
     * This also works for rites that modpack makers define, or will define with CraftTweaker. Ingredient.test is a
     * Guava/Java Predicate method, so it is not obfuscated and remap is false **/
    @WrapOperation(method = {"prepareIfMatches", "process"}, remap = false, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/minecraft/item/crafting/Ingredient;test(Ljava/lang/Object;)Z"))
    private boolean hegemonyIngredientMatch(Ingredient instance, Object stack, Operation<Boolean> original) {
        boolean matches = original.call(instance, stack);
        if (!matches && stack instanceof ItemStack) {
            ItemStack itemStack = (ItemStack) stack;
            for (Item equivalent : Hegemony.getEquivalents(itemStack)) {
                if (original.call(instance, new ItemStack(equivalent))) {
                    return true;
                }
            }
        }
        return matches;
    }
}
