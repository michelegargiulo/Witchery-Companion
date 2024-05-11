package com.smokeythebandicoot.witcherypatcher.mixins.entity;

import com.smokeythebandicoot.witcherypatcher.config.ModConfig;
import com.smokeythebandicoot.witcherypatcher.utils.LootTables;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntityFlyingMob;
import net.msrandom.witchery.entity.EntityLordOfTorment;
import net.msrandom.witchery.util.WitcheryUtils;
import net.msrandom.witchery.util.damage.DemonicDamageSource;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 Mixins:
 [Gameplay] Prevents Lord of Torment to teleport players into the Torment Dimension
 [Gameplay] Prevents Lord of Torment to generate loot
 */
@Mixin(value = EntityLordOfTorment.class, remap = false)
public class EntityLordOfTormentMixin extends EntityFlyingMob {

    private EntityLordOfTormentMixin(World world) {
        super(world);
    }

    @Override
    public ResourceLocation getLootTable() {
        return LootTables.LORD_OF_TORMENT;
    }

    @Final
    @Shadow(remap = false)
    private final Set<UUID> attackers = new HashSet<>();

    @Inject(method = "attackEntityFrom", at = @At("HEAD"), cancellable = true)
    private void WPattackEntityFrom(DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        if (ModConfig.PatchesConfiguration.EntityTweaks.lordOfTorment_tweakDisableTeleportation) {
            if (source.isExplosion()) {
                cir.setReturnValue(false);
            } else {
                if (source.getImmediateSource() != null && source.getImmediateSource() instanceof EntityPlayer) {
                    EntityPlayer attacker = (EntityPlayer) source.getImmediateSource();
                    this.attackers.add(attacker.getUniqueID());
                }

                float damageCap = source instanceof DemonicDamageSource ? 8.0F : 5.0F;
                boolean damaged = super.attackEntityFrom(source, WitcheryUtils.capAround(damage, damageCap));
                cir.setReturnValue(damaged);
            }
        }
    }

    @Inject(method = "dropFewItems", at = @At("HEAD"), cancellable = true)
    private void WPdropFewItems(boolean wasRecentlyHit, int lootingModifier, CallbackInfo ci) {

        if (ModConfig.PatchesConfiguration.LootTweaks.lordOfTorment_tweakLootTable) {
            super.dropFewItems(wasRecentlyHit, lootingModifier);
        }

        if (ModConfig.PatchesConfiguration.EntityTweaks.lordOfTorment_tweakDisableLoot) {
            ci.cancel();
        }
    }

}
