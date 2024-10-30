package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.mixins.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.Integration;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.block.BlockKettle;
import net.msrandom.witchery.init.items.WitcheryEquipmentItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import slimeknights.tconstruct.library.utils.TinkerUtil;

/**
 * [Feature] Conarm integration for Witches Hat (trait)
 * [Feature] Conarm integration for Baba yaga Hat (trait)
 */
@Mixin(BlockKettle.class)
public abstract class BlockKettleMixin extends BlockContainer {

    private BlockKettleMixin(Material materialIn) {
        super(materialIn);
    }

    /** This Mixin targets the first player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() and returns
     * WitcheryEquipmentItems.WITCH_CLOTHING (thus making the condition true) if the player has a witch_hat trait on their helmet **/
    @WrapOperation(method = "onBlockActivated", remap = true, at = @At(value = "INVOKE", remap = false, ordinal = 1,
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
    private Item checkWitchHatTrait(ItemStack instance, Operation<Item> original) {
        if (instance != null && TinkerUtil.hasTrait(instance.getTagCompound(),
                Integration.TRAIT_ARMOR_WITCH_CLOTHING.getIdentifier())) {
            return WitcheryEquipmentItems.WITCH_HAT;
        }
        return original.call(instance);
    }

    /** This Mixin targets the second player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() and returns
     * WitcheryEquipmentItems.BABAS_HAT (thus making the condition true) if the player has a babas_blessing trait on their helmet **/
    @WrapOperation(method = "onBlockActivated", remap = true, at = @At(value = "INVOKE", remap = false, ordinal = 2,
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
    private Item checkBabasHatTrait(ItemStack instance, Operation<Item> original) {
        if (instance != null && TinkerUtil.hasTrait(instance.getTagCompound(),
                Integration.MODIFIER_ARMOR_BABAS_BLESS.getIdentifier())) {
            return WitcheryEquipmentItems.BABAS_HAT;
        }
        return original.call(instance);
    }

    /** This Mixin targets the fifth player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() and returns
     * WitcheryEquipmentItems.BABAS_HAT (thus making the condition true) if the player has a babas_blessing trait on their helmet.
     * This Mixin is the additional check that increases effectiveness of Baba's Hat by another 5% if the player has a toad familiar **/
    @WrapOperation(method = "onBlockActivated", remap = true, at = @At(value = "INVOKE", remap = false, ordinal = 5,
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
    private Item checkBabasHatTraitWithFamiliar(ItemStack instance, Operation<Item> original) {
        if (instance != null && TinkerUtil.hasTrait(instance.getTagCompound(),
                Integration.MODIFIER_ARMOR_BABAS_BLESS.getIdentifier())) {
            return WitcheryEquipmentItems.BABAS_HAT;
        }
        return original.call(instance);
    }
}
