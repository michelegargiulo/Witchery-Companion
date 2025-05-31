package com.smokeythebandicoot.witcherycompanion.mixins.witchery.entity;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.utils.LootTables;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntityWitchHunter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityWitchHunter.class)
public abstract class EntityWitchHunterMixin extends EntityCreature {

    private EntityWitchHunterMixin(World world) {
        super(world);
    }

    @Inject(method = "dropFewItems", remap = false, cancellable = true, at = @At("HEAD"))
    protected void disableDropFewItems(boolean param, int fortune, CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.LootTweaks.witchHunter_tweakLootTable) {
            ci.cancel();
        }
    }

    @Override
    public ResourceLocation getLootTable() {
        return ModConfig.PatchesConfiguration.LootTweaks.witchHunter_tweakLootTable ? LootTables.WITCH_HUNTER : null;
    }

}
