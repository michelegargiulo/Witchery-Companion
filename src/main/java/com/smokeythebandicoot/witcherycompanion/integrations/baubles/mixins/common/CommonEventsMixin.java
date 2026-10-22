package com.smokeythebandicoot.witcherycompanion.integrations.baubles.mixins.common;

import baubles.api.BaublesApi;
import net.minecraft.entity.player.EntityPlayer;
import net.msrandom.witchery.common.CommonEvents;
import net.msrandom.witchery.init.items.WitcheryGeneralItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixins:
 * [Integration] Moon Charm is a CHARM bauble
 */
@Mixin(CommonEvents.class)
public abstract class CommonEventsMixin {

    /** This Mixin injects at HEAD and checks if the Bauble is equipped. If it is, return true, as it is enough.
     * Otherwise, avoid the call cancellation and let the rest of the method handle its presence in inventory and hands **/
    @Inject(method = "hasMoonCharm", remap = false, cancellable = true, at = @At("HEAD"))
    private static void hasMoonCharmAlsoChecksBaubles(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        if (BaublesApi.isBaubleEquipped(player, WitcheryGeneralItems.MOON_CHARM) > -1) {
            cir.setReturnValue(true);
        }
    }

}
