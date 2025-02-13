package com.smokeythebandicoot.witcherycompanion.mixins.witchery.entity;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.utils.LootTables;
import net.minecraft.entity.EntityCreature;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntityVillageGuard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityVillageGuard.class)
public abstract class EntityVillageGuardMixin extends EntityCreature {

    private EntityVillageGuardMixin(World worldIn) {
        super(worldIn);
    }

    @Inject(method = "getDropItem", remap = false, cancellable = true, at = @At("HEAD"))
    protected void dropFewItems(CallbackInfoReturnable<Item> cir) {
        if (ModConfig.PatchesConfiguration.LootTweaks.villageGuard_tweakLootTable) {
            cir.setReturnValue(null);
        }
    }

    @Override
    public ResourceLocation getLootTable() {
        return ModConfig.PatchesConfiguration.LootTweaks.villageGuard_tweakLootTable ? LootTables.VILLAGE_GUARD : null;
    }

}
