package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.mixins.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.entity.EntityGoblin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import slimeknights.tconstruct.tools.tools.Pickaxe;

/**
 * Mixins:
 * [Feature] Goblins use Tic Pickaxes (no other types of TiC tool)
 */
@Mixin(EntityGoblin.class)
public abstract class EntityGoblinMixin {

    /** This Mixin checks if the item given to the Goblin is a TiC Pickaxe. If it is, it tricks Witchery into
     * thinking it is a Vanilla pickaxe by returning the Vanilla Wooden Pick **/
    @WrapOperation(method = "processInteract", remap = false, at = @At(value = "INVOKE", remap = true,
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;", ordinal = 1))
    private Item ticPickaxesCompat(ItemStack instance, Operation<Item> original) {
        Item result = original.call(instance);
        if (result instanceof Pickaxe) {
            return Items.WOODEN_PICKAXE;
        }
        return result;
    }

}
