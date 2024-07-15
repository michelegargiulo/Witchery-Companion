package com.smokeythebandicoot.witcherycompanion.mixins.entity.familiar;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.msrandom.witchery.entity.familiar.Familiar;
import net.msrandom.witchery.entity.familiar.Familiars;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Familiars.class)
public abstract class FamiliarsMixin {

    /*
    @Inject(method = "getBoundFamiliar", remap = false, cancellable = true, at = @At("HEAD"))
    private static void WPgetBoundFamiliar(EntityPlayer player, CallbackInfoReturnable<Familiar<?>> cir) {
        if (player == null) {
            cir.setReturnValue(null);
            return;
        }

        for (Entity entity : player.world.getEntities(Entity.class, entity -> entity instanceof Familiar<?>)) {
            Familiar<?> familiar = (Familiar<?>) entity;
            if (familiar.getOwnerId() != null && familiar.getOwnerId().equals(player.getUniqueID())) {
                cir.setReturnValue(familiar);
                return;
            }
        }

        cir.setReturnValue(null);

    }
    */

}
