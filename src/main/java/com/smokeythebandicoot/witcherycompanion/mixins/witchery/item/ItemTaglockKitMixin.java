package com.smokeythebandicoot.witcherycompanion.mixins.witchery.item;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.ItemTweaks;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.item.BloodStorage;
import net.msrandom.witchery.item.ItemTaglockKit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixins:
 * [Tweak] Tweak Max Use Duration
 */
@Mixin(ItemTaglockKit.class)
public abstract class ItemTaglockKitMixin extends Item implements BloodStorage {

    @Unique
    private EntityLivingBase witchery_Patcher$entityToSpectate = null;

    @Inject(method = "getMaxItemUseDuration", remap = false, cancellable = true, at = @At("HEAD"))
    private void tweakMaxUseDuration(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(ItemTweaks.tweakMaxItemUseDuration);
    }

}
