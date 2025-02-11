package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.mixins.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.Integration;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.msrandom.witchery.extensions.PlayerExtendedData;
import net.msrandom.witchery.init.WitcheryCreatureTraits;
import net.msrandom.witchery.init.items.WitcheryEquipmentItems;
import net.msrandom.witchery.item.ItemHunterClothes;
import net.msrandom.witchery.util.WitcheryUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.library.utils.TinkerUtil;

@Mixin(ItemHunterClothes.class)
public abstract class ItemHunterClothesMixin {

    @WrapOperation(method = "isFullSetWorn", remap = false, at = @At(value = "INVOKE", remap = false, ordinal = 0,
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
    private static Item checkNullifyedTrait(ItemStack instance, Operation<Item> original) {
        if (TinkerUtil.hasTrait(instance.getTagCompound(), Integration.TRAIT_NULLIFYING.identifier)) {
            return WitcheryEquipmentItems.HUNTER_HAT;
        }
        return original.call(instance);
    }

    @WrapOperation(method = "isFullSetWorn", remap = false, at = @At(value = "INVOKE", remap = false, ordinal = 1,
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
    private static Item checkSilveredModifier(ItemStack instance, Operation<Item> original) {
        if (TinkerUtil.hasModifier(instance.getTagCompound(), Integration.MODIFIER_SILVERED.identifier)) {
            return WitcheryEquipmentItems.HUNTER_HAT_SILVERED;
        }
        return original.call(instance);
    }

    /*@Inject(method = "onArmorTick", remap = false, at = @At("TAIL"))
    private void checkSilveredForDamage(World world, EntityPlayer player, ItemStack stack, CallbackInfo ci) {
        if (!world.isRemote && player.ticksExisted % 20 == 3) {
            PlayerExtendedData playerEx = WitcheryUtils.getExtension(player);
            if (
                    (TinkerUtil.hasModifier(stack.getTagCompound(), Integration.MODIFIER_GARLICED.identifier) &&
                            playerEx.isTransformation(WitcheryCreatureTraits.VAMPIRE)) ||
                            (TinkerUtil.hasModifier(stack.getTagCompound(), Integration.MODIFIER_SILVERED.identifier) &&
                                    playerEx.isTransformation(WitcheryCreatureTraits.WEREWOLF))
            ) {
                player.attackEntityFrom(DamageSource.IN_FIRE, 1.0F);
            }
        }
    }*/

}
