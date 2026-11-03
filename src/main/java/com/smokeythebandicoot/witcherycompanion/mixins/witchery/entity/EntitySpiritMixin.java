package com.smokeythebandicoot.witcherycompanion.mixins.witchery.entity;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.utils.LootTables;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntitySpirit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin:
 * [Tweak] Give own loot table
 * Spirit drops are hardcoded and depend on the spirit type: 0 (natural spawn) drops a Subdued Spirit,
 * 1 (placed with a Subdued Village Spirit) drops a Subdued Village Spirit and 2 (Find Structure ritual) drops nothing.
 * One loot table is provided for each type
 */
@Mixin(EntitySpirit.class)
public abstract class EntitySpiritMixin extends EntityTameable {

    @Shadow(remap = false)
    private int spiritType;

    private EntitySpiritMixin(World world) {
        super(world);
    }

    @Override
    public ResourceLocation getLootTable() {
        if (!ModConfig.PatchesConfiguration.LootTweaks.spirit_tweakLootTable) {
            return null;
        }
        switch (this.spiritType) {
            case 2: return LootTables.SPIRIT_GUIDE;
            case 1: return LootTables.SPIRIT_VILLAGE;
            default: return LootTables.SPIRIT_DEFAULT;
        }
    }

    @Inject(method = "dropFewItems", remap = true, cancellable = true, at = @At("HEAD"))
    public void WPtweakLoot(boolean wasRecentlyHit, int lootingModifier, CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.LootTweaks.spirit_tweakLootTable) {
            ci.cancel();
        }
    }
}
