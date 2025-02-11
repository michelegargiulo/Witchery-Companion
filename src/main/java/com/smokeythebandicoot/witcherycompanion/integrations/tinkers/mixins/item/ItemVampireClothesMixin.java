package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.mixins.item;

import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.Integration;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.item.ItemVampireClothes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.library.utils.TinkerUtil;

@Mixin(ItemVampireClothes.class)
public abstract class ItemVampireClothesMixin {

    /** This Mixins is used to consider TiC armor with the Fireblooded trait as light pieces **/
    @Inject(method = "numLightPiecesWorn", remap = false, at = @At("RETURN"))
    private static void checkFirebloodedTrait(EntityLivingBase entity, boolean light, CallbackInfoReturnable<Integer> cir) {
        int wornPieces = cir.getReturnValue();
        for (EntityEquipmentSlot slot : new EntityEquipmentSlot[]{
            EntityEquipmentSlot.HEAD,
            EntityEquipmentSlot.CHEST,
            EntityEquipmentSlot.LEGS,
            EntityEquipmentSlot.FEET,
        }) {
            ItemStack stack = entity.getItemStackFromSlot(slot);
            if (TinkerUtil.hasTrait(stack.getTagCompound(), Integration.TRAIT_VAMPIRIC.identifier)) {
                wornPieces++;
            }
        }
        cir.setReturnValue(wornPieces);
    }

}
