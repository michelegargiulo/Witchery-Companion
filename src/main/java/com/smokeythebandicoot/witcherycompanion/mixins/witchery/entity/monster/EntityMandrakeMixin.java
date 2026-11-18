package com.smokeythebandicoot.witcherycompanion.mixins.witchery.entity.monster;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.EntityTweaks;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.monster.EntityMandrake;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixins:
 * [Bugfix] Fix the Mandrake scream: the ambient and hurt sounds use the sound the original Witchery used
 */
@Mixin(EntityMandrake.class)
public abstract class EntityMandrakeMixin extends EntityMob {

    private EntityMandrakeMixin(World worldIn) {
        super(worldIn);
    }

    /** The original Witchery used "mob.ghast.scream", which is the sound of the Ghast being hurt. In 1.12 that sound is
     * SoundEvents.ENTITY_GHAST_HURT, while ENTITY_GHAST_SCREAM is a different sound that no vanilla mob uses. W:R picked
     * the wrong one when porting the sounds */
    @Inject(method = "getAmbientSound", remap = false, cancellable = true, at = @At("HEAD"))
    private void witcherycompanion$fixAmbientSound(CallbackInfoReturnable<SoundEvent> cir) {
        if (EntityTweaks.mandrake_fixScreamSound) {
            cir.setReturnValue(SoundEvents.ENTITY_GHAST_HURT);
        }
    }

    @Inject(method = "getHurtSound", remap = false, cancellable = true, at = @At("HEAD"))
    private void witcherycompanion$fixHurtSound(DamageSource source, CallbackInfoReturnable<SoundEvent> cir) {
        if (EntityTweaks.mandrake_fixScreamSound) {
            cir.setReturnValue(SoundEvents.ENTITY_GHAST_HURT);
        }
    }

}
