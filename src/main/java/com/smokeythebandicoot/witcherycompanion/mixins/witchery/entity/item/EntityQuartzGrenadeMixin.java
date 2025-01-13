package com.smokeythebandicoot.witcherycompanion.mixins.witchery.entity.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.item.EntityQuartzGrenade;
import net.msrandom.witchery.entity.passive.EntityDuplicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mixins:
 * [Tweak] Configurable EntityDuplicate lifespan
 */
@Mixin(EntityQuartzGrenade.class)
public abstract class EntityQuartzGrenadeMixin extends EntityThrowable {

    private EntityQuartzGrenadeMixin(World worldIn) {
        super(worldIn);
    }

    @WrapOperation(method = "onImpact", remap = false, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/msrandom/witchery/entity/passive/EntityDuplicate;setTicksToLive(I)V"))
    private void customDuplicateLifespan(EntityDuplicate instance, int ticks, Operation<Void> original) {
        original.call(instance, ModConfig.PatchesConfiguration.EntityTweaks.duplicate_tweakTickLifespan);
    }


}
