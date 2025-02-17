package com.smokeythebandicoot.witcherycompanion.mixins.witchery.entity;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.utils.LootTables;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntitySpectralFamiliar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntitySpectralFamiliar.class)
public abstract class EntitySpectralFamiliarMixin extends EntityOcelot {

    private EntitySpectralFamiliarMixin(World worldIn) {
        super(worldIn);
    }

    @Inject(method = "dropFewItems", remap = false, cancellable = true, at = @At("HEAD"))
    protected void dropFewItems(boolean par1, int par2, CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.LootTweaks.spectralFamiliar_tweakLootTable) {
            ci.cancel();
        }
    }

    @Override
    public ResourceLocation getLootTable() {
        return ModConfig.PatchesConfiguration.LootTweaks.spectralFamiliar_tweakLootTable ? LootTables.SPECTRAL_FAMILIAR : null;
    }

}
