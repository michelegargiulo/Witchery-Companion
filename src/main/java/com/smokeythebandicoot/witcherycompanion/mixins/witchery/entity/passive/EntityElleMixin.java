package com.smokeythebandicoot.witcherycompanion.mixins.witchery.entity.passive;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.utils.LootTables;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.passive.EntityElle;
import org.spongepowered.asm.mixin.Mixin;

/**
 Mixins:
 [Tweak] Introduce Loot Table
 */
@Mixin(EntityElle.class)
public abstract class EntityElleMixin extends EntityTameable {

    private EntityElleMixin(World worldIn) {
        super(worldIn);
    }

    protected ResourceLocation getLootTable() {
        if (ModConfig.PatchesConfiguration.LootTweaks.elle_tweakLootTable) {
            return LootTables.ELLE;
        }
        return null;
    }
}
