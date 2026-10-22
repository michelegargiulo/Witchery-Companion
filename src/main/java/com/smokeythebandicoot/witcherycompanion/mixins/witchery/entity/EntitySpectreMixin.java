package com.smokeythebandicoot.witcherycompanion.mixins.witchery.entity;

import com.smokeythebandicoot.witcherycompanion.api.accessors.entities.spectre.IEntitySpectreAccessor;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.utils.LootTables;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntitySpectre;
import net.msrandom.witchery.entity.EntitySummonedUndead;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 Mixins:
 [Tweak] Modify Attributes (multiple tweaks)
 [Tweak] Custom despawn delay when the spectre has no aggro target
 [Tweak] Custom lifespan
 */
@Mixin(EntitySpectre.class)
public abstract class EntitySpectreMixin extends EntitySummonedUndead implements IEntitySpectreAccessor {

    @Shadow(remap = false)
    private int lifetime;

    @Unique
    protected int witcherycompanion$despawnDelay = ModConfig.PatchesConfiguration.EntityTweaks.spectre_tweakDelayTicksBeforeDespawn;

    private EntitySpectreMixin(World world) {
        super(world);
    }

    /** Injects at head to modify entity attributes **/
    @Inject(method = "applyEntityAttributes", remap = false, cancellable = true, at = @At("HEAD"))
    public void WPtweakEntityAttributes(CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.EntityTweaks.spectre_tweakAttributes) {
            super.applyEntityAttributes();
            getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(ModConfig.PatchesConfiguration.EntityTweaks.spectre_tweakFollowRange);
            getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(ModConfig.PatchesConfiguration.EntityTweaks.spectre_tweakMovementSpeed);
            getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(ModConfig.PatchesConfiguration.EntityTweaks.spectre_tweakAttackDamage);
            ci.cancel();
        }
    }

    /** On initial spawn, sets the custom lifetime of the Spectre to the configured value **/
    @Inject(method = "onInitialSpawn", remap = false, at = @At("RETURN"), cancellable = false)
    private void tweakCustomLifetime(DifficultyInstance difficulty, IEntityLivingData livingData, CallbackInfoReturnable<IEntityLivingData> cir) {
        this.lifetime = ModConfig.PatchesConfiguration.EntityTweaks.spectre_tweakMaxLifetime;
    }

    /** This Mixin replaces the updateAITasks logic to include the custom delay despawn **/
    @Inject(method = "updateAITasks", remap = true, cancellable = true, at = @At("HEAD"))
    protected void updateAITasks(CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.EntityTweaks.spectre_tweakDelayBeforeDespawn) {
            super.updateAITasks();
            if (this.world != null && !this.isDead && !this.world.isRemote && (--this.lifetime <= 0 || witcherycompanion$despawnDelay == 0)) {
                this.world.setEntityState(this, (byte)5);
                this.setDead();
            }

            if (this.getAttackTarget() == null || this.getAttackTarget().isDead) {
                witcherycompanion$despawnDelay -= 1;
            } else {
                witcherycompanion$despawnDelay = ModConfig.PatchesConfiguration.EntityTweaks.spectre_tweakDelayTicksBeforeDespawn;
            }

            ci.cancel();
        }
    }

    @Override
    public int witcherycompanion$accessor$getDespawnDelay() {
        return this.witcherycompanion$despawnDelay;
    }

    @Override
    public int witcherycompanion$accessor$getLifetime() {
        return this.lifetime;
    }

    @Override
    public ResourceLocation getLootTable() {
        return ModConfig.PatchesConfiguration.LootTweaks.spectre_tweakLootTable ? LootTables.SPECTRE : null;
    }

}
