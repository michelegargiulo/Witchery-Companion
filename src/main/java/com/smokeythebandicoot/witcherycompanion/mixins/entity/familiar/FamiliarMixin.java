package com.smokeythebandicoot.witcherycompanion.mixins.entity.familiar;

import net.minecraft.entity.*;
import net.minecraft.entity.passive.EntityTameable;
import net.msrandom.witchery.entity.familiar.Familiar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 Mixins:
 [Bugfix] Fix familiar losing owner connection on world relad
 */
@Mixin(Familiar.class)
public interface FamiliarMixin extends IEntityOwnable {

    @Shadow(remap = false)
    Entity getEntity();

    @Shadow(remap = false)
    boolean isFamiliar();

    @Inject(method = "getFamiliarOwner", remap = false, cancellable = true, at = @At("HEAD"))
    default void getFamiliarOwner(EntityLivingBase entityLivingBase, CallbackInfoReturnable<EntityLivingBase> cir) {
        if (this.isFamiliar() && !this.getEntity().world.isRemote) {
            cir.setReturnValue(((EntityTameable) this).getOwner());
        }
        cir.setReturnValue(null);
    }
}
