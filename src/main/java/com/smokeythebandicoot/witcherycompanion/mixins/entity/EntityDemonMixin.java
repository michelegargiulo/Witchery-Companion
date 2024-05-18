package com.smokeythebandicoot.witcherycompanion.mixins.entity;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.utils.LootTables;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntityDemon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityDemon.class)
public abstract class EntityDemonMixin extends EntityGolem {

    private EntityDemonMixin(World worldIn) {
        super(worldIn);
    }

    @Override
    public ResourceLocation getLootTable() {
        return ModConfig.PatchesConfiguration.LootTweaks.demon_tweakLootTable ? LootTables.DEMON : null;
    }

    @Inject(method = "getDropItem", remap = true, cancellable = true, at = @At("HEAD"))
    public void WPtweakDropItem(CallbackInfoReturnable<Item> cir) {
        if (ModConfig.PatchesConfiguration.LootTweaks.demon_tweakLootTable) {
            cir.setReturnValue(null);
        }
    }

    @Inject(method = "dropFewItems", remap = true, cancellable = true, at = @At("HEAD"))
    public void WPtweakLoot(boolean wasRecentlyHit, int lootingModifier, CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.LootTweaks.demon_tweakLootTable) {
            ci.cancel();
        }
    }
}
