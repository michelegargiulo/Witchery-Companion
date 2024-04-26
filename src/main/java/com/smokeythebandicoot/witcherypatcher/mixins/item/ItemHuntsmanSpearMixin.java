package com.smokeythebandicoot.witcherypatcher.mixins.item;

import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.msrandom.witchery.item.ItemHuntsmanSpear;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemHuntsmanSpear.class, remap = false)
public class ItemHuntsmanSpearMixin {

    @Inject(method = "getItemAttributeModifiers", at = @At("HEAD"), remap = false)
    public void WPaddAttackSpeed(EntityEquipmentSlot slot, CallbackInfoReturnable<Multimap<String, AttributeModifier>> cir) {

    }

}
