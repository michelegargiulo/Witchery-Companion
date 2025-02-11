package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.mixins.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.Integration;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.common.CommonEvents;
import net.msrandom.witchery.init.items.WitcheryEquipmentItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import slimeknights.tconstruct.library.utils.TinkerUtil;

/**
 * Mixins:
 * [Feature] Witch Clothing trait makes players being ignored by creepers
 * [Feature] Necromancer Robes trait makes players being ignored by undeads
 */
@Mixin(CommonEvents.class)
public abstract class CommonEventsMixin {

    /** Witch Clothing trait makes players being ignored by creepers  **/
    @WrapOperation(method = "onLivingSetAttackTarget", remap = false, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;", ordinal = 0))
    private static Item witchClothingTraitIgnoreCreeper(ItemStack instance, Operation<Item> original) {
        if (TinkerUtil.hasModifier(instance.getTagCompound(), Integration.MODIFIER_CREEPER_REPELLENT.identifier)) {
            return WitcheryEquipmentItems.WITCH_ROBES;
        }
        return original.call(instance);
    }

    /** Necromancer Robes trait makes players being ignored by undeads **/
    @WrapOperation(method = "onLivingSetAttackTarget", remap = false, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;", ordinal = 1))
    private static Item necromancerClothingTraitIgnoreCreeper(ItemStack instance, Operation<Item> original) {
        if (TinkerUtil.hasModifier(instance.getTagCompound(), Integration.MODIFIER_UNDEAD_REPELLENT.identifier)) {
            return WitcheryEquipmentItems.NECROMANCERS_ROBES;
        }
        return original.call(instance);
    }

    /** This Mixin emulates the Baba's hat capability to teleport the player randomly upon receiving some types of damage **/
    @WrapOperation(method = "onLivingHurt", remap = false, at = @At(value = "INVOKE", ordinal = 1,
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
    private static Item babasBlessTeleportOnHurt(ItemStack instance, Operation<Item> original) {
        if (TinkerUtil.hasTrait(instance.getTagCompound(), Integration.TRAIT_BREW_AFFINITY.identifier)) {
            return WitcheryEquipmentItems.BABAS_HAT;
        }
        return original.call(instance);
    }

    /** This Mixin tricks witchery into thinking that TiC boots with Seeping trait are Witchery's Seeping Shoes **/
    @WrapOperation(method = "handleSeepingShoesEffect", remap = false, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;", remap = false))
    private static Item handleSeepingShoesTrait(ItemStack instance, Operation<Item> original) {
        if (TinkerUtil.hasModifier(instance.getTagCompound(), Integration.MODIFIER_SEEPING.identifier)) {
            return WitcheryEquipmentItems.SEEPING_SHOES;
        }
        return original.call(instance);
    }

    @WrapOperation(method = "onLivingUpdate", remap = false, at = @At(value = "INVOKE", remap = false, ordinal = 1,
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
    private static Item checkBarkedTraitOnLegs(ItemStack instance, Operation<Item> original) {
        if (TinkerUtil.hasModifier(instance.getTagCompound(), Integration.MODIFIER_BARKED.identifier)) {
            return WitcheryEquipmentItems.BARK_BELT;
        }
        return original.call(instance);
    }

}
