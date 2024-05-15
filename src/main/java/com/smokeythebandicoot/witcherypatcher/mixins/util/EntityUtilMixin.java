package com.smokeythebandicoot.witcherypatcher.mixins.util;

import com.smokeythebandicoot.witcherypatcher.config.ModConfig;
import net.minecraft.entity.Entity;
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
public class EntityUtilMixin {

    @Inject(method = "pullTowards", at = @At("HEAD"), remap = false, cancellable = true)
    private static void WPpullTowards(Entity entity, Vec3d target, double dy, double yy, CallbackInfo cbi) {
        if (ModConfig.PatchesConfiguration.CommonTweaks.entityUtils_fixNullPointer) {
            if (entity == null) cbi.cancel();
        }
    }
}
