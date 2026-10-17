package com.smokeythebandicoot.witcherycompanion.mixins.witchery.entity;

import com.smokeythebandicoot.witcherycompanion.patches.crystalball.DivinationManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntitySleepingBody;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixins:
 * [Tweak] Spectator Divination: hurting the body left by a Crystal Ball divination ends it and hurts the player
 */
@Mixin(EntitySleepingBody.class)
public abstract class EntitySleepingBodyMixin extends EntityLiving {

    private EntitySleepingBodyMixin(World worldIn) {
        super(worldIn);
    }

    /** Only bodies spawned by a divination are affected, every other body behaves as in Witchery */
    @Inject(method = "attackEntityFrom", remap = false, cancellable = true, at = @At("HEAD"))
    private void witcherycompanion$onDivinationBodyAttacked(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (DivinationManager.onBodyAttacked((EntitySleepingBody) (Object) this, source, amount)) {
            cir.setReturnValue(true);
        }
    }

}
