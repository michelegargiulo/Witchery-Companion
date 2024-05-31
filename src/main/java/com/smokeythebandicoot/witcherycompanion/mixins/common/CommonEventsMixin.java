package com.smokeythebandicoot.witcherycompanion.mixins.common;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.item.Item;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.event.LootTableLoadEvent;
import net.msrandom.witchery.common.CommonEvents;
import net.msrandom.witchery.config.WitcheryConfigOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 Mixins:
 [Bugfix] Fixes Crash on Village Worldgen, when Witchery attempts to generate items inside chests or item frames and
   tries to add duplicate loot pools (pools with the same name)
 */
@Mixin(CommonEvents.class)
public abstract class CommonEventsMixin {

    @Inject(method = "loadLoot", remap = false, cancellable = true, at = @At("HEAD"))
    private static void fixAddedLoot(LootTableLoadEvent event, CallbackInfo ci) {

        if (!ModConfig.PatchesConfiguration.WorldGenTweaks.frameWithBook_fixCrashOnVillageGen) return;

        if (event.getName().toString().equals("witchery:chests/bookshop")) {

            for (Item item : WitcheryConfigOptions.townBooks) {
                if (item != null) {
                    String entryName = item.getRegistryName().toString();
                    LootEntry[] entries = new LootEntry[]{
                            new LootEntryItem(item, 1, 0, new LootFunction[0], new LootCondition[0], entryName)
                    };
                    LootTable table = event.getTable();
                    if (table.getPool(entryName) == null)
                        table.addPool(new LootPool(entries, new LootCondition[0], new RandomValueRange(1.0F, 1.0F), new RandomValueRange(0.0F, 0.0F), entryName));
                }
            }

        }

        ci.cancel();

    }

}
