package com.smokeythebandicoot.witcherycompanion.mixins.witchery.entity;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.utils.LootTables;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntityDeath;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 Mixins:
 [Tweak] Introduce own loot table
 */
@Mixin(EntityDeath.class)
public abstract class EntityDeathMixin extends EntityMob {

    private EntityDeathMixin(World worldIn) {
        super(worldIn);
    }

    @Inject(method = "getDropItem", remap = true, cancellable = true, at = @At("HEAD"))
    public void WPlootTweakDropItem(CallbackInfoReturnable<Item> cir) {
        if (ModConfig.PatchesConfiguration.LootTweaks.death_tweakLootTable) {
            cir.setReturnValue(null);
        }
    }

    @Inject(method = "dropFewItems", remap = true, cancellable = true, at = @At("HEAD"))
    public void WPlootTweakDropFewItems(boolean recentlyHit, int lootingModifier, CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.LootTweaks.death_tweakLootTable) {
            ci.cancel();
        }
    }

    @Override
    public ResourceLocation getLootTable() {
        return ModConfig.PatchesConfiguration.LootTweaks.death_tweakLootTable ? LootTables.DEATH : null;
    }

}
