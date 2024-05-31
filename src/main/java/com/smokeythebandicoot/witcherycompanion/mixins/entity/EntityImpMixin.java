package com.smokeythebandicoot.witcherycompanion.mixins.entity;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.utils.LootTables;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntityImp;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityImp.class)
public abstract class EntityImpMixin extends EntityTameable {

    private EntityImpMixin(World worldIn) {
        super(worldIn);
    }

    @Override
    public ResourceLocation getLootTable() {
        return ModConfig.PatchesConfiguration.LootTweaks.infernalImp_tweakLootTable ? LootTables.IMP_DEATH : null;
    }

}
