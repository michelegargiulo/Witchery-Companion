package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.mixins.item;

import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.Integration;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.item.ItemWitchesClothes;
import net.msrandom.witchery.item.traits.Invisible;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.library.utils.TinkerUtil;

/**
 * Mixins:
 * [Feature] Add compat for Witch Clothing tcon traits
 */
@Mixin(ItemWitchesClothes.class)
public abstract class ItemWitchesClothesMixin extends ItemArmor implements Invisible {

    private ItemWitchesClothesMixin(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
        super(materialIn, renderIndexIn, equipmentSlotIn);
    }

    @Inject(method = "isHatWorn", remap = false, cancellable = true, at = @At("HEAD"))
    private void checkTraitHat(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        ItemStack headStack = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        cir.setReturnValue(
                headStack.getItem() instanceof ItemWitchesClothes ||
                        TinkerUtil.hasTrait(
                                headStack.getTagCompound(),
                                Integration.TRAIT_ARMOR_WITCH_CLOTHING.getIdentifier()
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
                                Integration.TRAIT_ARMOR_WITCH_CLOTHING.getIdentifier()
                        ) ||
                        TinkerUtil.hasModifier(
                                chestStack.getTagCompound(),
                                Integration.MODIFIER_ARMOR_NECROMANCER.getIdentifier()
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
                    TinkerUtil.hasTrait(stack.getTagCompound(), Integration.TRAIT_ARMOR_BARKED.getIdentifier())
            ) {
                level += 2;
            }
        }
        cir.setReturnValue(level);
    }

}
