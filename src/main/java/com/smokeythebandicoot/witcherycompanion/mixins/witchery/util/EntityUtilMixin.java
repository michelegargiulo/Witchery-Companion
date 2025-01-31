package com.smokeythebandicoot.witcherycompanion.mixins.witchery.util;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.utils.DiviningUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.msrandom.witchery.util.EntityUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 Mixins:
 [Bugfix] Prevent crashing game when null entity is called in pullTowards() method
 */
@Mixin(value = EntityUtil.class)
public abstract class EntityUtilMixin {

    /** This Mixin performs a null-check before continuing **/
    @Inject(method = "pullTowards", at = @At("HEAD"), remap = false, cancellable = true)
    private static void WPpullTowards(Entity entity, Vec3d target, double dy, double yy, CallbackInfo cbi) {
        if (ModConfig.PatchesConfiguration.CommonTweaks.entityUtils_fixNullPointer) {
            if (entity == null) cbi.cancel();
        }
    }

    /** This Mixin terminates a divination if the player has to die by EntityUtils.instantDeath **/
    @WrapOperation(method = "instantDeath", remap = false, at = @At(value = "INVOKE", ordinal = 1,
            target = "Lnet/minecraft/entity/EntityLivingBase;setHealth(F)V", remap = true))
    private static void instantDeathFixDesync(EntityLivingBase instance, float v, Operation<Void> original) {
        if (instance instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) instance;
            DiviningUtils.terminateDivination(player);
        }
        instance.setDead();
        original.call(instance, v);
    }
}
