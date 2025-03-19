package com.smokeythebandicoot.witcherycompanion.mixins.witchery.entity;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.utils.LootTables;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntitySpectre;
import net.msrandom.witchery.entity.EntitySummonedUndead;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 Mixins:
 [Tweak] Modify Attributes (multiple tweaks)
 [Tweak] Custom despawn delay when the spectre has no aggro target
 */
@Mixin(EntitySpectre.class)
public abstract class EntitySpectreMixin extends EntitySummonedUndead {

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

    /** This Mixin replaces the updateAITasks logic to include the custom delay despawn **/
    @Inject(method = "updateAITasks", remap = true, cancellable = true, at = @At("HEAD"))
    protected void updateAITasks(CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.EntityTweaks.spectre_tweakDelayBeforeDespawn) {
            super.updateAITasks();
            if (this.world != null && !this.isDead && !this.world.isRemote && this.lifetime != -1 && (--this.lifetime == 0 || witcherycompanion$despawnDelay == 0)) {
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
    public ResourceLocation getLootTable() {
        return ModConfig.PatchesConfiguration.LootTweaks.spectre_tweakLootTable ? LootTables.SPECTRE : null;
    }

}
