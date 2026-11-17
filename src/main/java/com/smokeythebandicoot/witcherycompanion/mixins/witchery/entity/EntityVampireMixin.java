package com.smokeythebandicoot.witcherycompanion.mixins.witchery.entity;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.EntityTweaks;
import com.smokeythebandicoot.witcherycompanion.patches.entity.vampire.VampireAIPatch;
import com.smokeythebandicoot.witcherycompanion.utils.LootTables;
import net.minecraft.entity.EntityCreature;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntityVampire;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixins:
 * [Tweak] Introduce own loot table
 * [Bugfix] Fix Vampires never attacking: the melee attack AI was missing (see VampireAIPatch)
 */
@Mixin(EntityVampire.class)
public abstract class EntityVampireMixin extends EntityCreature {

    private EntityVampireMixin(World worldIn) {
        super(worldIn);
    }

    @Inject(method = "getDropItem", remap = false, cancellable = true, at = @At("HEAD"))
    protected void dropFewItems(CallbackInfoReturnable<Item> cir) {
        if (ModConfig.PatchesConfiguration.LootTweaks.vampire_tweakLootTable) {
            cir.cancel();
        }
    }

    /** Fixes the AI tasks after W:R has set them up in the constructor */
    @Inject(method = "<init>", remap = false, at = @At("TAIL"))
    private void witcherycompanion$fixMeleeAI(World world, CallbackInfo ci) {
        if (EntityTweaks.vampireMob_fixAI) {
            VampireAIPatch.fixMeleeAI(this);
        }
    }

    @Override
    public ResourceLocation getLootTable() {
        return ModConfig.PatchesConfiguration.LootTweaks.vampire_tweakLootTable ? LootTables.VAMPIRE : null;
    }

}
