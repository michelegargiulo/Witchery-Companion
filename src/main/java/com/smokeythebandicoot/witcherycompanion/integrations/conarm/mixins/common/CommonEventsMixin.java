package com.smokeythebandicoot.witcherycompanion.integrations.conarm.mixins.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.integrations.conarm.ConArmIntegration;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.TinkersUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.common.CommonEvents;
import net.msrandom.witchery.init.items.WitcheryEquipmentItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mixins:
 * [Feature] Creeper Repellent modifier makes players being ignored by creepers
 * [Feature] Undead Repellent modifier makes players being ignored by undeads
 * [Feature] Baba's Bless modifier emulates the random teleport of Baba Yaga's Hat
 * [Feature] Seeping modifier emulates Seeping Shoes
 * [Feature] Barked modifier emulates the Bark Belt (charging and absorbing damage)
 */
@Mixin(CommonEvents.class)
public abstract class CommonEventsMixin {

    /** Creeper Repellent modifier makes players being ignored by creepers (onLivingSetAttackTarget, chest vs Witch Robes) **/
    @WrapOperation(method = "onLivingSetAttackTarget", remap = false, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;", ordinal = 0))
    private static Item creeperRepellentIgnoreCreeper(ItemStack instance, Operation<Item> original) {
        if (TinkersUtils.hasEffect(instance, ConArmIntegration.MODIFIER_CREEPER_REPELLENT)) {
            return WitcheryEquipmentItems.WITCH_ROBES;
        }
        return original.call(instance);
    }

    /** Undead Repellent modifier makes players being ignored by undeads (onLivingSetAttackTarget, chest vs Necromancer Robes) **/
    @WrapOperation(method = "onLivingSetAttackTarget", remap = false, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;", ordinal = 1))
    private static Item undeadRepellentIgnoreUndead(ItemStack instance, Operation<Item> original) {
        if (TinkersUtils.hasEffect(instance, ConArmIntegration.MODIFIER_UNDEAD_REPELLENT)) {
            return WitcheryEquipmentItems.NECROMANCERS_ROBES;
        }
        return original.call(instance);
    }

    /** Baba's Hat (onLivingHurt, head vs Baba's Hat): outside of Torment, 25% chance to spend 5 power, teleport
     * randomly and cancel the damage. Emulated by the Baba's Bless modifier **/
    @WrapOperation(method = "onLivingHurt", remap = false, at = @At(value = "INVOKE", ordinal = 1,
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
    private static Item babasBlessTeleportOnHurt(ItemStack instance, Operation<Item> original) {
        if (TinkersUtils.hasEffect(instance, ConArmIntegration.MODIFIER_BABAS_BLESS)) {
            return WitcheryEquipmentItems.BABAS_HAT;
        }
        return original.call(instance);
    }

    /** Bark Belt damage absorption (onLivingHurt, legs vs Bark Belt). Emulated by the Barked modifier **/
    @WrapOperation(method = "onLivingHurt", remap = false, at = @At(value = "INVOKE", ordinal = 2,
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
    private static Item barkedAbsorbsDamage(ItemStack instance, Operation<Item> original) {
        if (TinkersUtils.hasEffect(instance, ConArmIntegration.MODIFIER_BARKED)) {
            return WitcheryEquipmentItems.BARK_BELT;
        }
        return original.call(instance);
    }

    /** This Mixin tricks witchery into thinking that TiC boots with Seeping modifier are Witchery's Seeping Shoes **/
    @WrapOperation(method = "handleSeepingShoesEffect", remap = false, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
    private static Item handleSeepingModifier(ItemStack instance, Operation<Item> original) {
        if (TinkersUtils.hasEffect(instance, ConArmIntegration.MODIFIER_SEEPING)) {
            return WitcheryEquipmentItems.SEEPING_SHOES;
        }
        return original.call(instance);
    }

    /** Bark Belt charging (onLivingUpdate, legs vs Bark Belt). Emulated by the Barked modifier **/
    @WrapOperation(method = "onLivingUpdate", remap = false, at = @At(value = "INVOKE", ordinal = 1,
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
    private static Item barkedChargesOnLegs(ItemStack instance, Operation<Item> original) {
        if (TinkersUtils.hasEffect(instance, ConArmIntegration.MODIFIER_BARKED)) {
            return WitcheryEquipmentItems.BARK_BELT;
        }
        return original.call(instance);
    }
}
