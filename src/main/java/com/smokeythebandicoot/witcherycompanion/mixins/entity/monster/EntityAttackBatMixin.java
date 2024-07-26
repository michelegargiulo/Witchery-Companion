package com.smokeythebandicoot.witcherycompanion.mixins.entity.monster;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.utils.LootTables;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.msrandom.witchery.entity.monster.EntityAttackBat;
import org.spongepowered.asm.mixin.Mixin;

/**
 Mixins:
 [Tweak] Introduce own loot table
 */
@Mixin(EntityAttackBat.class)
public abstract class EntityAttackBatMixin extends EntityBat {

    private EntityAttackBatMixin(World worldIn) {
        super(worldIn);
    }

    protected ResourceLocation getLootTable() {
        if (ModConfig.PatchesConfiguration.LootTweaks.attackBat_tweakOwnLootTable) {
            return LootTables.ATTACK_BAT;
        }
        return LootTableList.ENTITIES_BAT;
    }

}
