package com.smokeythebandicoot.witcherycompanion.integrations.conarm.mixins.item;

import com.smokeythebandicoot.witcherycompanion.integrations.conarm.ConArmIntegration;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.TinkersUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.item.ItemGoblinClothes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixins:
 * [Feature] Mog's Quiver modifier on chestplates counts as wearing Mog's Quiver (bows do not consume arrows)
 */
@Mixin(ItemGoblinClothes.class)
public abstract class ItemGoblinClothesMixin {

    /** Witchery checks the quiver in the chest slot (armor slot 2), when nocking and loosing arrows **/
    @Inject(method = "isQuiverWorn", remap = false, cancellable = true, at = @At("RETURN"))
    private static void mogsQuiverModifier(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {
            ItemStack chest = player.inventory.armorInventory.get(2);
            if (TinkersUtils.hasEffect(chest, ConArmIntegration.MODIFIER_MOGS_QUIVER)) {
                cir.setReturnValue(true);
            }
        }
    }
}
