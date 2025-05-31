package com.smokeythebandicoot.witcherycompanion.mixins.witchery.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.utils.LootTables;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntityLeonard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityLeonard.class)
public abstract class EntityLeonardMixin extends EntityMob {

    private EntityLeonardMixin(World worldIn) {
        super(worldIn);
    }

    @Override
    public ResourceLocation getLootTable() {
        return ModConfig.PatchesConfiguration.LootTweaks.leonard_tweakLootTable ? LootTables.LEONARD : null;
    }

    @Inject(method = "dropFewItems", at = @At("HEAD"), cancellable = true, remap = true)
    private void WPdropFewItems(boolean wasRecentlyHit, int lootingModifier, CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.LootTweaks.leonard_tweakLootTable) {
            ci.cancel();
        }
    }

}
