package com.smokeythebandicoot.witcherycompanion.mixins.entity;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.EntityTweaks;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntityBanshee;
import net.msrandom.witchery.entity.EntitySummonedUndead;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 Mixins:
 [Tweak] Tweak to ignore other Banshees
 */
@Mixin(EntityBanshee.class)
public abstract class EntityBansheeMixin extends EntitySummonedUndead {

    private EntityBansheeMixin(World world) {
        super(world);
    }

    /** This Mixin clears existing AI tasks and replaces the attack targets AIs with just one that attacks everything
     except other Banshees */
    @Inject(method = "<init>", remap = false, at = @At(value = "TAIL"))
    public void friendlyToOtherBanshees(World world, CallbackInfo ci) {
        if (EntityTweaks.banshee_tweakDoNotAttackOtherBanshees) {

            // Clear existing tasks
            this.targetTasks.taskEntries.clear();

            // Add back the "revenge" task
            this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));

            // Add the LivingBase target, but ignore other banshees
            this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(
                    this, EntityLivingBase.class, 0, false, false,
                    entityLivingBase -> !(entityLivingBase instanceof EntityBanshee)));
        }
    }

}
