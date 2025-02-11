package com.smokeythebandicoot.witcherycompanion.mixins.witchery.entity;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.utils.LootTables;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntityLostSoul;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityLostSoul.class)
public abstract class EntityLostSoulMixin extends EntityMob {

    private EntityLostSoulMixin(World worldIn) {
        super(worldIn);
    }

    @Override
    public ResourceLocation getLootTable() {
        return ModConfig.PatchesConfiguration.LootTweaks.lostSoul_tweakLootTable ? LootTables.LOST_SOUL : null;
    }
}
