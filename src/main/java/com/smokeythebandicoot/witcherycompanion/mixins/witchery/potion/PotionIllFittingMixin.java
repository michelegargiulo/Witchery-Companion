package com.smokeythebandicoot.witcherycompanion.mixins.witchery.potion;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.PotionTweaks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.msrandom.witchery.potion.PotionIllFitting;
import net.msrandom.witchery.potion.WitcheryPotion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


/**
 * Mixins:
 * [Tweak] potion cannot remove armor cursed with Curse of Binding
 */
@Mixin(PotionIllFitting.class)
public abstract class PotionIllFittingMixin extends WitcheryPotion {

    @Shadow(remap = false)
    public static boolean isTargetAllowed(EntityLivingBase entity) {
        return false;
    }

    @Shadow(remap = false) @Final
    public static EntityEquipmentSlot[] SLOTS;


    private PotionIllFittingMixin(int color) {
        super(color);
    }

    /** Replace the whole performEffect function to check for Curse of Binding **/
    @Inject(method = "performEffect", remap = false, at = @At("HEAD"), cancellable = true)
    private void tweakIllFittingBindingCurse(EntityLivingBase entity, int amplifier, CallbackInfo ci) {
        World world = entity.world;
        if (!world.isRemote && isTargetAllowed(entity)) {
            EntityEquipmentSlot slot = SLOTS[world.rand.nextInt(4) + 2];
            ItemStack armorPiece = entity.getItemStackFromSlot(slot);

            // To remove the equipment, it must not have Binding Curse or effect must:
            if (!armorPiece.isEmpty() && (
                    !EnchantmentHelper.hasBindingCurse(armorPiece) ||                           // Not have Binding Curse, OR
                    amplifier >= PotionTweaks.illFitting_tweakCurseBindingUnequip) ||           // Have a high enough amplifier, OR
                    (entity instanceof EntityPlayer && ((EntityPlayer)entity).isCreative())     // Player is in creative (can remove the armor manually anyway)
            ) {
                entity.setItemStackToSlot(slot, ItemStack.EMPTY);
                entity.entityDropItem(armorPiece, 0.0F).setPickupDelay(5 + 5 * amplifier);
            }
        }
        ci.cancel();
    }

}
