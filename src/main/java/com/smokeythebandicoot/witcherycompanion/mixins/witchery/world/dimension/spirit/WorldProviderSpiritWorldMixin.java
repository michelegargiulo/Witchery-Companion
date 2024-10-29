package com.smokeythebandicoot.witcherycompanion.mixins.witchery.world.dimension.spirit;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.DimensionTweaks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntityNightmare;
import net.msrandom.witchery.world.dimension.spirit.WorldProviderSpiritWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

/**
 Mixins:
 [Bugfix] Fix endless Nightmare spawning
 [Tweak] Tweak Nightmare spawn cap
 [Tweak] Tweak Nightmare spawning cooldown after last kill
 */
@Mixin(WorldProviderSpiritWorld.class)
public class WorldProviderSpiritWorldMixin {

    @Unique
    private static int witchery_Patcher$countForPlayer = -1;

    /** This method injects before the WitcheryUtils.summonEntity method and cancels its call.
     A WrapOperation might have worked, but I prefer to not depend on Kotlin internals, so I re-wrote
     the part of code responsible for entity spawning with the fix inside of it*/
    @Inject(method = "updatePlayerEffects", remap = false, cancellable = true, at = @At(value = "INVOKE", target = "Lnet/msrandom/witchery/util/WitcheryUtils;summonEntity(Lnet/minecraft/world/World;Lnet/minecraft/entity/EntityType;Lnet/minecraft/util/math/BlockPos;IILkotlin/jvm/functions/Function1;)Lnet/minecraft/entity/Entity;"))
    private static void fixSetVictimID(World world, EntityPlayer player, long counter, CallbackInfo ci) {
        if (DimensionTweaks.spiritWorld_fixNightmareSpawning) {
            witchery_Patcher$spawnNightmare(world, player, 2, 6);
            ci.cancel();
        }
    }

    /** This function mimics Witchery's summonEntity() function, but without Kotlin elements */
    @Unique
    private static EntityNightmare witchery_Patcher$spawnNightmare(World world, EntityPlayer player, int minimumDistance, int maximumDistance) {

        // Compute potential spawn location
        int radius = maximumDistance - minimumDistance;
        int x = world.rand.nextInt(radius * 2 + 1);
        if (x > radius) {
            x += minimumDistance * 2;
        }

        int z = world.rand.nextInt(radius * 2 + 1);
        if (z > radius) {
            z += minimumDistance * 2;
        }

        BlockPos spawnPosition;
        BlockPos position = player.getPosition();
        for(spawnPosition = position.add(x - maximumDistance, 0, z - maximumDistance); !world.isAirBlock(spawnPosition); spawnPosition = spawnPosition.up()) {
            if (spawnPosition.getY() >= position.getY() + 8) {
                break;
            }
        }

        // Find suitable space for spawning
        while(world.isAirBlock(spawnPosition)) {
            if (spawnPosition.getY() <= 0) {
                break;
            }

            spawnPosition = spawnPosition.down();
        }

        int height;
        for(height = 0; world.isAirBlock(spawnPosition.up(height + 1)) && height < 6; ++height) {
        }

        if (height >= 2) {
            // Create a Nightmare Entity and set its location and rotation
            EntityNightmare nightmare = new EntityNightmare(world);
            nightmare.setLocationAndAngles((double)spawnPosition.getX() + 0.5, (double)spawnPosition.getY() + 1.05, (double)spawnPosition.getZ() + 0.5, 0.0F, 0.0F);

            /** This is the part that actually fixes the issue. Nightmare's victim ID was set to its own instead
             of player's UUID, causing never-ending Nightmare spawns */
            nightmare.setVictimId(player.getUniqueID());

            // Actually spawn and return the Entity
            world.spawnEntity(nightmare);
            return nightmare;
        }

        // Return null: could not spawn the entity
        return null;
    }

    /** This mixin resets the counter for the Nightmare spawning cap */
    @Inject(method = "updatePlayerEffects", remap = false, at = @At("HEAD"))
    private static void resetCapCounter(World world, EntityPlayer player, long counter, CallbackInfo ci) {
        witchery_Patcher$countForPlayer = 0;
    }

    /** This mixin counts how many Nightmares have player's VictimID and only returns if there are as much as the cap */
    @WrapOperation(method = "updatePlayerEffects", remap = false, at = @At(value = "INVOKE",
            target = "Ljava/util/UUID;equals(Ljava/lang/Object;)Z", remap = false))
    private static boolean tweakCustomNightmareCap(UUID instance, Object o, Operation<Boolean> original) {
        boolean result = original.call(instance, o);

        // Keep count of how many matches
        if (result) {
            witchery_Patcher$countForPlayer++;
        }

        // If not enough for cap, return false, otherwise return true
        return witchery_Patcher$countForPlayer >= DimensionTweaks.spiritWorld_tweakNightmareSpawnCap;

    }

    /** This mixin changes the cooldown value in code */
    @ModifyConstant(method = "updatePlayerEffects", remap = false, constant = @Constant(longValue = 600L))
    private static long modifyCooldown(long constant) {
        return DimensionTweaks.spiritWorld_tweakNightmareSpawnCooldown;
    }
}
