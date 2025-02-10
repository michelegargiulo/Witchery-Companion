package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.mixins.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.Integration;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.init.items.WitcheryEquipmentItems;
import net.msrandom.witchery.item.ItemWitchesClothes;
import net.msrandom.witchery.item.traits.Invisible;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.library.utils.TinkerUtil;

/**
 * Mixins:
 * [Feature] Seeping TiC Trait
 */
@Mixin(ItemWitchesClothes.class)
public abstract class ItemWitchesClothesMixin extends ItemArmor implements Invisible {

    private ItemWitchesClothesMixin(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
        super(materialIn, renderIndexIn, equipmentSlotIn);
    }

    /** This Mixin tricks witchery into thinking that TiC boots with Homing trait are Witchery's Ruby Slippers **/
    @WrapOperation(method = "noPlaceLikeHome", remap = false, at = @At(value = "INVOKE", ordinal = 0,
            target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;", remap = false))
    private static Item handleSeepingShoesTrait(ItemStack instance, Operation<Item> original) {
        if (TinkerUtil.hasModifier(instance.getTagCompound(), Integration.MODIFIER_HOMING.identifier)) {
            return WitcheryEquipmentItems.RUBY_SLIPPERS;
        }
        return original.call(instance);
    }

    @Inject(method = "isHatWorn", remap = false, cancellable = true, at = @At("HEAD"))
    private void checkTraitHat(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        ItemStack headStack = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        cir.setReturnValue(
                headStack.getItem() instanceof ItemWitchesClothes ||
                        TinkerUtil.hasTrait(
                                headStack.getTagCompound(),
                                Integration.TRAIT_WITCH_CLOTHING.getIdentifier()
                        )
        );
    }

    @Inject(method = "isRobeWorn", remap = false, cancellable = true, at = @At("HEAD"))
    private void checkTraitRobes(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        ItemStack chestStack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        cir.setReturnValue(
                chestStack.getItem() instanceof ItemWitchesClothes ||
                        TinkerUtil.hasTrait(
                                chestStack.getTagCompound(),
                                Integration.TRAIT_WITCH_CLOTHING.getIdentifier()
                        ) ||
                        TinkerUtil.hasModifier(
                                chestStack.getTagCompound(),
                                Integration.TRAIT_NECROMANCER.getIdentifier()
                        )
        );
    }

    @Inject(method = "isBeltWorn", remap = false, cancellable = true, at = @At("HEAD"))
    private void checkTraitBelt(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        //
    }

    /** This Mixin makes it so that Bark Belt charges increased not only for worn items of ItemWitchesClothes
     * type, but also for armor that has the Barked armor trait **/
    @Inject(method = "getMaxChargeLevel", remap = false, cancellable = true, at = @At("HEAD"))
    private static void maxChargeLevelIncludesTinkersArmor(EntityLivingBase entity, CallbackInfoReturnable<Integer> cir) {
        int level = 0;
        for (EntityEquipmentSlot slot : new EntityEquipmentSlot[] {
                EntityEquipmentSlot.HEAD,
                EntityEquipmentSlot.CHEST,
                EntityEquipmentSlot.LEGS,
                EntityEquipmentSlot.FEET,
        }) {
            ItemStack stack = entity.getItemStackFromSlot(slot);
            if (stack.getItem() instanceof ItemWitchesClothes ||
                    TinkerUtil.hasTrait(stack.getTagCompound(), Integration.TRAIT_BARKED.getIdentifier())
            ) {
                level += 2;
            }
        }
        cir.setReturnValue(level);
    }

}
