package com.smokeythebandicoot.witcherycompanion.mixins.witchery.entity.passive;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.EntityTweaks;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.LootTweaks;
import com.smokeythebandicoot.witcherycompanion.utils.LootTables;
import net.minecraft.entity.EntityCreature;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.passive.EntityFairest;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

/**
 Mixins:
 [Bugfix] Fix spawning with no texture on summon and sometimes on mirror spawn
 [Tweak] Introduce loot table
 */
@Mixin(EntityFairest.class)
public abstract class EntityFairestMixin extends EntityCreature {

    @Shadow(remap = false) @Final
    private static String[] FIRST_NAMES_M;

    @Shadow(remap = false) @Final
    private static String[] FIRST_NAMES_F;

    @Shadow(remap = false) @Final
    private static DataParameter<Integer> VARIANT;

    @Shadow(remap = false) @Final
    private static String[] SURNAMES;

    private EntityFairestMixin(World worldIn) {
        super(worldIn);
    }

    /** This Mixin Injects into the head of setType, so that types get remapped 0-3 -> 1-4 (as texture names go from 0
     * to 4) and if outside of range, it still generates a random within valid range */
    @Inject(method = "setType", remap = false, cancellable = true, at = @At("HEAD"))
    private void fixTypeRange(int value, CallbackInfo ci) {
        if (!EntityTweaks.fairest_fixBrokenTextures) return;

        // valid input range is 0-3
        if (value >= 0 && value <= 3){
            value += 1;


        } // If ouside of range, gen a valid random int (1-4)
        else {
            value = (new Random()).nextInt(4) + 1;
        }
        String[] names = value == 4 ? FIRST_NAMES_M : FIRST_NAMES_F;
        this.dataManager.set(VARIANT, value);
        this.setCustomNameTag(names[this.rand.nextInt(names.length)] + ' ' + SURNAMES[this.rand.nextInt(SURNAMES.length)]);
        ci.cancel();
    }

    @WrapOperation(method = "entityInit", remap = true, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/minecraft/network/datasync/EntityDataManager;register(Lnet/minecraft/network/datasync/DataParameter;Ljava/lang/Object;)V"))
    private void fixTypeRange(EntityDataManager instance, DataParameter<Integer> dataParameter, Object o, Operation<Void> original) {
        Integer initParam = EntityTweaks.fairest_fixBrokenTextures ? (new Random()).nextInt(4) + 1 : 0;
        original.call(instance, dataParameter, initParam);
    }

    @Inject(method = "dropFewItems", remap = false, cancellable = true, at = @At("HEAD"))
    private void preventHardcodedLoot(boolean wasRecentlyHit, int lootingModifier, CallbackInfo ci) {
        if (LootTweaks.fairest_tweakLootTable) {
            ci.cancel();
        }
    }

    protected ResourceLocation getLootTable() {
        if (LootTweaks.fairest_tweakLootTable) {
            return LootTables.FAIREST;
        }
        return null;
    }
}
