package com.smokeythebandicoot.witcherycompanion.mixins.entity.passive;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.utils.LootTables;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.msrandom.witchery.entity.passive.EntityCatFamiliar;
import org.spongepowered.asm.mixin.Mixin;

/**
 Mixins:
 [Tweak] Introduce own loot table
 */
@Mixin(value = EntityCatFamiliar.class)
public abstract class EntityCatFamiliarMixin {

    protected ResourceLocation getLootTable() {
        if (ModConfig.PatchesConfiguration.LootTweaks.familiarCat_tweakOwnLootTable) {
            return LootTables.FAMILIAR_CAT;
        }
        return LootTableList.ENTITIES_OCELOT;
    }

}
