package com.smokeythebandicoot.witcherycompanion.mixins.witchery.rite.effect;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.rite.effect.RiteEffectBlight;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RiteEffectBlight.class)
public abstract class RiteEffectBlightMixin {

    @WrapOperation(method = "doRadiusAction", remap = false, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/msrandom/witchery/item/WitcheryPoppetItems;voodooProtectionActivated(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;I)Z"))
    public boolean strengthTweak(EntityPlayer allowLightning, ItemStack i, EntityLivingBase attackingEntity, int strength, Operation<Boolean> original) {
        return original.call(allowLightning, i, attackingEntity, ModConfig.PatchesConfiguration.RitesTweaks.blight_tweakStrength);
    }

}
