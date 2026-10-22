package com.smokeythebandicoot.witcherycompanion.mixins.witchery.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.utils.LootTables;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntityFlyingMob;
import net.msrandom.witchery.entity.EntityLordOfTorment;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 Mixins:
 [Tweak] Prevents Lord of Torment to teleport players into the Torment Dimension
 [Tweak] Prevents Lord of Torment to generate loot
 */
@Mixin(value = EntityLordOfTorment.class)
public abstract class EntityLordOfTormentMixin extends EntityFlyingMob {

    private EntityLordOfTormentMixin(World world) {
        super(world);
    }

    @Override
    public ResourceLocation getLootTable() {
        return ModConfig.PatchesConfiguration.LootTweaks.lordOfTorment_tweakLootTable ? LootTables.LORD_OF_TORMENT : null;
    }

    @ModifyExpressionValue(method = "attackEntityFrom", remap = false,
            at = @At(value = "INVOKE", target = "Lnet/msrandom/witchery/world/dimension/WitcheryDimension;isInDimension(Lnet/minecraft/entity/Entity;)Z", remap = false))
    private boolean WPdisableTormentTP(boolean original) {
        // Witchery teleports players (and removes the Lord) only when the Lord is NOT in the Torment Dimension, so
        // to disable the teleportation the Lord has to look like it is already there: OR, never AND
        return original
                || ModConfig.PatchesConfiguration.EntityTweaks.lordOfTorment_tweakDisableTeleportation
                || ModConfig.PatchesConfiguration.DimensionTweaks.torment_tweakDisableTeleportation;
    }

    @Inject(method = "dropFewItems", at = @At("HEAD"), cancellable = true, remap = true)
    private void WPdropFewItems(boolean wasRecentlyHit, int lootingModifier, CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.LootTweaks.lordOfTorment_tweakLootTable) {
            ci.cancel();
        }
    }

}
