package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.mixins.block;

import c4.conarm.common.ConstructsRegistry;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.Integration;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.ECompanionTrait;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.block.BlockKettle;
import net.msrandom.witchery.init.items.WitcheryEquipmentItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

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
     * WitcheryEquipmentItems.WITCH_CLOTHING (thus making the condition true) if the player has a witch_hat trait on their armor **/
    @WrapOperation(method = "onBlockActivated", remap = true, at = @At(value = "INVOKE", remap = false, ordinal = 1,
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
    private Item checkWitchHatTrait(ItemStack instance, Operation<Item> original) {
        if (Integration.hasTrait(instance, ECompanionTrait.WITCH_CLOTHING) &&
                (instance.getItem() == ConstructsRegistry.helmet)) {
            return WitcheryEquipmentItems.WITCH_HAT;
        }
        return original.call(instance);
    }
}
