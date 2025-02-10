package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.mixins.entity.ai;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.Integration;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.entity.ai.EntityAIDigBlocks;
import net.msrandom.witchery.init.items.WitcheryGeneralItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.tools.tools.Pickaxe;

@Mixin(EntityAIDigBlocks.class)
public abstract class EntityAIDigBlocksMixin extends EntityAIBase {

    @WrapOperation(method = "isHoldingKobolditePick", remap = false, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
    private static Item compatTicKoboldite(ItemStack instance, Operation<Item> original) {
        if (instance != null && TinkerUtil.hasTrait(instance.getTagCompound(), Integration.TRAIT_GOBLINS_FAVOR.identifier)) {
            return WitcheryGeneralItems.KOBOLDITE_PICKAXE;
        }
        return original.call(instance);
    }

    @WrapOperation(method = "shouldExecute", remap = false, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;", ordinal = 0))
    private Item shouldExecuteWithTicPick(ItemStack instance, Operation<Item> original) {
        Item result = original.call(instance);
        if (result instanceof Pickaxe) {
            return Items.WOODEN_PICKAXE;
        }
        return result;
    }

    @WrapOperation(method = "shouldContinueExecuting", remap = false, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
    private Item shouldContinueExecutingWithTicPick(ItemStack instance, Operation<Item> original) {
        Item result = original.call(instance);
        if (result instanceof Pickaxe) {
            return Items.WOODEN_PICKAXE;
        }
        return result;
    }

}
