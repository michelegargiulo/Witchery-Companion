package com.smokeythebandicoot.witcherycompanion.mixins.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.msrandom.witchery.common.ShapeShift;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 Mixins:
 [Bugfix] Fix floating entities when traveling through dimensions due to an incorrect usage of 'sendPlayerAbilities'
    that spawns tracked entities from origin dimension to target dimension
 */
@Mixin(ShapeShift.class)
public class ShapeShiftMixin {

    /** Wraps around the sendPlayerAbilities() call and cancels it */
    @WrapOperation(method = "initCurrentShift(Lnet/minecraft/entity/player/EntityPlayer;)V", remap = false, at = @At(
            value = "INVOKE", remap = false, target = "Lnet/minecraft/entity/player/EntityPlayer;sendPlayerAbilities()V"))
    public void avoidUpdatingPlayerAbilities(EntityPlayer instance, Operation<Void> original) {
        // If config option is true, do nothing
        if (ModConfig.PatchesConfiguration.CommonTweaks.shapeShift_fixFloatingEntities) return;
        original.call(instance);
    }

}
