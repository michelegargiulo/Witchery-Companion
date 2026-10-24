package com.smokeythebandicoot.witcherycompanion.integrations.conarm.mixins.item;

import com.smokeythebandicoot.witcherycompanion.integrations.conarm.ConArmIntegration;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.TinkersUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.item.ItemVampireClothes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixins:
 * [Feature] TiC armor with the Vampiric trait counts as light Vampire Clothes pieces
 */
@Mixin(ItemVampireClothes.class)
public abstract class ItemVampireClothesMixin {

    /** Witchery only counts light pieces when 'light' is true (it returns 0 otherwise), so TiC pieces with the
     * Vampiric trait are added only in that case. Vampire Clothes are skipped, as they are already counted **/
    @Inject(method = "numLightPiecesWorn", remap = false, cancellable = true, at = @At("RETURN"))
    private static void checkVampiricTrait(EntityLivingBase entity, boolean light, CallbackInfoReturnable<Integer> cir) {
        if (!light) return;
        int wornPieces = cir.getReturnValue();
        for (EntityEquipmentSlot slot : new EntityEquipmentSlot[]{
                EntityEquipmentSlot.HEAD,
                EntityEquipmentSlot.CHEST,
                EntityEquipmentSlot.LEGS,
                EntityEquipmentSlot.FEET,
        }) {
            ItemStack stack = entity.getItemStackFromSlot(slot);
            if (stack.getItem() instanceof ItemVampireClothes) continue;
            if (TinkersUtils.hasEffect(stack, ConArmIntegration.TRAIT_VAMPIRIC)) {
                wornPieces++;
            }
        }
        cir.setReturnValue(wornPieces);
    }
}
