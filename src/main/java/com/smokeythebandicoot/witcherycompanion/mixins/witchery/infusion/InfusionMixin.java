package com.smokeythebandicoot.witcherycompanion.mixins.witchery.infusion;

import com.smokeythebandicoot.witcherycompanion.api.progress.ProgressUtils;
import com.smokeythebandicoot.witcherycompanion.api.progress.WitcheryProgressEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.msrandom.witchery.infusion.Infusion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixins:
 * [Feature] Unlock infusion secrets
 */
@Mixin(Infusion.class)
public abstract class InfusionMixin {

    /** This Mixin unlocks progress when a Player has been infused, only for the infusion they obtained **/
    @Inject(method = "infuse", remap = false, cancellable = false, at = @At(value = "INVOKE", remap = false, shift = At.Shift.AFTER,
            target = "Lnet/msrandom/witchery/infusion/Infusion;syncPlayer(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;)V"))
    private void unlockInfusionSecrets(EntityPlayer player, int charges, CallbackInfo ci) {
        ProgressUtils.unlockProgress(player, ProgressUtils.getInfusionSecret((Infusion)(Object)this),
                WitcheryProgressEvent.EProgressTriggerActivity.INFUSION_OBTAINED.activityTrigger);
    }

}
