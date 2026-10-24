package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.mixins.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.api.HegemonyApi;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.common.CommonEvents;
import net.msrandom.witchery.init.items.WitcheryGeneralItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixins:
 * [Tweak] Hegemony: the Tinkers Arthana counts as the Witchery Arthana when a creature dies (more drops)
 * [Tweak] Hegemony: a Battlesign with the Huntsman modifier, while blocking, can summon a wolf like the Spear of the Huntsman
 */
@Mixin(CommonEvents.class)
public abstract class CommonEventsMixin {

    /** Arthana in the main hand when a creature dies (onLivingDeath, held item vs Arthana) **/
    @WrapOperation(method = "onLivingDeath", remap = false, at = @At(value = "INVOKE", ordinal = 0,
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
    private static Item hegemonyArthanaMainHand(ItemStack instance, Operation<Item> original) {
        return HegemonyApi.resolveAs(instance, original.call(instance), WitcheryGeneralItems.getArthana());
    }

    /** Arthana in the off hand when a creature dies (onLivingDeath, held item vs Arthana) **/
    @WrapOperation(method = "onLivingDeath", remap = false, at = @At(value = "INVOKE", ordinal = 1,
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
    private static Item hegemonyArthanaOffHand(ItemStack instance, Operation<Item> original) {
        return HegemonyApi.resolveAs(instance, original.call(instance), WitcheryGeneralItems.getArthana());
    }

    /** onLivingHurt checks, with a BiPredicate (lambda$onLivingHurt$5), if the stack is the Spear of the Huntsman being
     * used (blocking) by the player: in that case, 25% chance to summon a wolf against the attacker. The lambda is
     * (captured player, player, stack). This is the same check, with the Battlesign being an equivalent of the spear **/
    @Inject(method = "lambda$onLivingHurt$5", remap = false, cancellable = true, at = @At("RETURN"))
    private static void hegemonyHuntsmanBlocking(EntityPlayer captured, EntityPlayer player, ItemStack stack,
                                                 CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() &&
                HegemonyApi.isEquivalent(stack, WitcheryGeneralItems.HUNTSMANS_SPEAR) &&
                captured.getActiveItemStack() == stack) {
            cir.setReturnValue(true);
        }
    }
}
