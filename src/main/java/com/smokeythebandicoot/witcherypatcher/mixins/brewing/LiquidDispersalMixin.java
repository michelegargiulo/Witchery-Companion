package com.smokeythebandicoot.witcherypatcher.mixins.brewing;

import com.smokeythebandicoot.witcherypatcher.config.ModConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.msrandom.witchery.brewing.LiquidDispersal;
import net.msrandom.witchery.brewing.ModifiersImpact;
import net.msrandom.witchery.brewing.ModifiersRitual;
import net.msrandom.witchery.brewing.RitualStatus;
import net.msrandom.witchery.brewing.action.BrewActionList;
import net.msrandom.witchery.entity.EntityDroplet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 Mixins:
 [Bugfix] Fix Cauldron Rituals with Liquid Disperal not spawning droplets
 */
@Mixin(value = LiquidDispersal.class)
public class LiquidDispersalMixin {

    @Inject(method = "onUpdateRitual", remap = false, at = @At("HEAD"), cancellable = true)
    public void WPritualLiquidDispersalIgnoreHeight(World world, BlockPos pos, BrewActionList actionList, ModifiersRitual modifiers, ModifiersImpact impactModifiers, CallbackInfoReturnable<RitualStatus> cir) {

        if (ModConfig.PatchesConfiguration.BrewsTweaks.common_fixCauldronRitualLiquidDispersalNoEffect) {
            World targetWorld = world.getMinecraftServer().getWorld(modifiers.getDimension());
            int maxQuantity;
            int radius = maxQuantity = 16 + 8 * impactModifiers.extent.get();
            int halfQuantity = maxQuantity / 4;
            int y = pos.getY() + world.rand.nextInt(20);;
            int i = 0;

            for (int quantity = halfQuantity + world.rand.nextInt(halfQuantity); i < quantity; ++i) {
                int x = pos.getX() - radius + world.rand.nextInt(2 * radius);
                int z = pos.getZ() - radius + world.rand.nextInt(2 * radius);

                // This is the problematic line: a check is performed to assure that the droplets are spawned
                // within a circle of radius <radius> centered on the ritual target position, but height is
                // taken into account, but since it is 100 + random (up to 20)
                // it is almost impossible to spawn even a single droplet. This fixes it by setting a relative
                // height where to spawn the particles and ignore height when computing the droplet position
                // Also improves performance (veeeery marginal) by skipping y in the distance computation
                int dx = x - pos.getX();
                int dz = z - pos.getZ();
                if (dx * dx + dz * dz <= radius * radius) {
                    targetWorld.spawnEntity(new EntityDroplet(targetWorld, x, y, z, actionList));
                }
            }

            cir.setReturnValue(RitualStatus.success(modifiers.pulses >= 10 + impactModifiers.lifetime.get() * 5));
        }
    }

}
