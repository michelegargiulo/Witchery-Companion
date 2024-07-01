package com.smokeythebandicoot.witcherycompanion.mixins.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.msrandom.witchery.item.ItemCreativeMedallion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemCreativeMedallion.class)
public abstract class ItemCreativeMedallionMixin {

    @WrapOperation(method = "onItemRightClick", remap = true, at = @At(value = "INVOKE",
            remap = true, target = "Lnet/minecraft/entity/player/EntityPlayer;isCreative()Z"))
    private boolean removeCreativeRequirement(EntityPlayer instance, Operation<Boolean> original) {
        return original.call(instance) || ModConfig.PatchesConfiguration.ItemTweaks.creativeMedallion_tweakDisableCreativeRequirement;
    }

}
