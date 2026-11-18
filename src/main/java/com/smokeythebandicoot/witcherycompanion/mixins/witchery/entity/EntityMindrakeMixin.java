package com.smokeythebandicoot.witcherycompanion.mixins.witchery.entity;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.utils.LootTables;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntityMindrake;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixins:
 * [Tweak] Introduce own loot table
 * [Bugfix] Fix the Mindrake hurt sound: it uses the sound the original Witchery used (see EntityMandrakeMixin)
 */
@Mixin(EntityMindrake.class)
public abstract class EntityMindrakeMixin extends EntityTameable {

    private EntityMindrakeMixin(World worldIn) {
        super(worldIn);
    }

    @Override
    public ResourceLocation getLootTable() {
        return ModConfig.PatchesConfiguration.LootTweaks.mindrake_tweakLootTable ? LootTables.MINDRAKE : null;
    }

    @Inject(method = "dropFewItems", remap = false, cancellable = true, at = @At("HEAD"))
    private void disableDropFewItems(boolean par1, int par2, CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.LootTweaks.mindrake_tweakLootTable) {
            ci.cancel();
        }
    }

    @Inject(method = "getHurtSound", remap = false, cancellable = true, at = @At("HEAD"))
    private void witcherycompanion$fixHurtSound(DamageSource source, CallbackInfoReturnable<SoundEvent> cir) {
        if (ModConfig.PatchesConfiguration.EntityTweaks.mandrake_fixScreamSound) {
            cir.setReturnValue(SoundEvents.ENTITY_GHAST_HURT);
        }
    }
}
