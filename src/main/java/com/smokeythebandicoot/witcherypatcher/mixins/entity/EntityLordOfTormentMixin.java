package com.smokeythebandicoot.witcherypatcher.mixins.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.smokeythebandicoot.witcherypatcher.config.ModConfig;
import com.smokeythebandicoot.witcherypatcher.utils.LootTables;
import com.smokeythebandicoot.witcherypatcher.utils.Utils;
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
 [Tweak] Prevents Lord of Torment to teleport players into the Torment Dimension
 [Tweak] Prevents Lord of Torment to generate loot
 */
@Mixin(value = EntityLordOfTorment.class)
public class EntityLordOfTormentMixin extends EntityFlyingMob {

    private EntityLordOfTormentMixin(World world) {
        super(world);
    }

    @Override
    public ResourceLocation getLootTable() {
        return ModConfig.PatchesConfiguration.LootTweaks.lordOfTorment_tweakLootTable ? LootTables.LORD_OF_TORMENT : null;
    }

    @Final
    @Shadow(remap = false)
    private final Set<UUID> attackers = new HashSet<>();

    /*@Inject(method = "attackEntityFrom", cancellable = true, remap = true,
            at = @At(value = "INVOKE", target = "Lnet/msrandom/witchery/entity/EntityFlyingMob;attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z"))
    private void WPattackEntityFrom(DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        if (ModConfig.PatchesConfiguration.EntityTweaks.lordOfTorment_tweakDisableTeleportation) {
            float damageCap = source instanceof DemonicDamageSource ? 8.0F : 5.0F;
            boolean damaged = super.attackEntityFrom(source, WitcheryUtils.capAround(damage, damageCap));
            cir.setReturnValue(damaged);
        }
    }*/

    @ModifyExpressionValue(method = "attackEntityFrom", remap = false,
            at = @At(value = "INVOKE", target = "Lnet/msrandom/witchery/world/dimension/WitcheryDimension;isInDimension(Lnet/minecraft/entity/Entity;)Z", remap = false))
    private boolean WPdisableTormentTP(boolean original) {
        Utils.logChat("Disabling torment: " + original + " - " + ModConfig.PatchesConfiguration.EntityTweaks.lordOfTorment_tweakDisableTeleportation);
        return original && (ModConfig.PatchesConfiguration.EntityTweaks.lordOfTorment_tweakDisableTeleportation);
    }

    @Inject(method = "dropFewItems", at = @At("HEAD"), cancellable = true, remap = true)
    private void WPdropFewItems(boolean wasRecentlyHit, int lootingModifier, CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.LootTweaks.lordOfTorment_tweakLootTable) {
            ci.cancel();
        }
    }

}
