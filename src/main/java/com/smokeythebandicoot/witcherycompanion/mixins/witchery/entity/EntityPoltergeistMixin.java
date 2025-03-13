package com.smokeythebandicoot.witcherycompanion.mixins.witchery.entity;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.utils.LootTables;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntityPoltergeist;
import net.msrandom.witchery.entity.EntitySummonedUndead;
import org.spongepowered.asm.mixin.Mixin;


@Mixin(EntityPoltergeist.class)
public abstract class EntityPoltergeistMixin extends EntitySummonedUndead {

    private EntityPoltergeistMixin(World world) {
        super(world);
    }

    /** This mixin overrides superclass' dropFewItems, that is hardcoded to drop spectral dust **/
    @Override
    protected void dropFewItems(boolean recentlyHit, int lootingModifier) {
        if (!ModConfig.PatchesConfiguration.LootTweaks.banshee_tweakLootTable) {
            super.dropFewItems(recentlyHit, lootingModifier);
        }
    }

    @Override
    public ResourceLocation getLootTable() {
        return ModConfig.PatchesConfiguration.LootTweaks.poltergeist_tweakLootTable ? LootTables.POLTERGEIST : null;
    }
}
