package com.smokeythebandicoot.witcherycompanion.mixins.entity.passive;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.utils.LootTables;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.passive.EntityDuplicate;
import org.spongepowered.asm.mixin.Mixin;

/**
 Mixins:
 [Tweak] Give loot on death depending on new loot table
 */
@Mixin(EntityDuplicate.class)
public abstract class EntityDuplicateMixin extends EntityCreature {

    private EntityDuplicateMixin(World worldIn) {
        super(worldIn);
    }

    protected ResourceLocation getLootTable() {
        if (ModConfig.PatchesConfiguration.LootTweaks.duplicate_tweakOwnLootTable) {
            return LootTables.DUPLICATE;
        }
        return null;
    }

}
