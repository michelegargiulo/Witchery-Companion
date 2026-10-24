package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.mixins.entity.ai;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.api.HegemonyApi;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.init.items.WitcheryGeneralItems;
import net.msrandom.witchery.entity.ai.EntityAIDigBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.tools.Pickaxe;

@Mixin(EntityAIDigBlocks.class)
public abstract class EntityAIDigBlocksMixin extends EntityAIBase {

    @WrapOperation(method = "isHoldingKobolditePick", remap = false, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
    private static Item compatTicKoboldite(ItemStack instance, Operation<Item> original) {
        return HegemonyApi.resolveAs(instance, original.call(instance), WitcheryGeneralItems.KOBOLDITE_PICKAXE);
    }

    @WrapOperation(method = "shouldExecute", remap = true, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;", ordinal = 0))
    private Item shouldExecuteWithTicPick(ItemStack instance, Operation<Item> original) {
        Item result = original.call(instance);
        if (HegemonyApi.isEnabled() && result instanceof Pickaxe && !ToolHelper.isBroken(instance)) {
            return Items.WOODEN_PICKAXE;
        }
        return result;
    }

    @WrapOperation(method = "shouldContinueExecuting", remap = true, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
    private Item shouldContinueExecutingWithTicPick(ItemStack instance, Operation<Item> original) {
        Item result = original.call(instance);
        if (HegemonyApi.isEnabled() && result instanceof Pickaxe && !ToolHelper.isBroken(instance)) {
            return Items.WOODEN_PICKAXE;
        }
        return result;
    }

}
