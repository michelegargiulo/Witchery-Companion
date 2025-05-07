package com.smokeythebandicoot.witcherycompanion.mixins.witchery.entity;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.utils.LootTables;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntityFlyingTameable;
import net.msrandom.witchery.entity.EntityWingedMonkey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixins:
 * [Tweak] Introduce own loot table
 */
@Mixin(EntityWingedMonkey.class)
public abstract class EntityWingedMonkeyMixin extends EntityFlyingTameable {

    private EntityWingedMonkeyMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "dropFewItems", remap = false, cancellable = true, at = @At("HEAD"))
    protected void disableDropFewItems(boolean param, int fortune, CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.LootTweaks.wingedMonkey_tweakLootTable) {
            ci.cancel();
        }
    }

    @Override
    public ResourceLocation getLootTable() {
        return ModConfig.PatchesConfiguration.LootTweaks.wingedMonkey_tweakLootTable ? LootTables.WINGED_MONKEY : null;
    }

}
