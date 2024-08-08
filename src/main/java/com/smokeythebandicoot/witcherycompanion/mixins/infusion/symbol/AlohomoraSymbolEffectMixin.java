package com.smokeythebandicoot.witcherycompanion.mixins.infusion.symbol;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntitySpellEffect;
import net.msrandom.witchery.infusion.symbol.AlohomoraSymbolEffect;
import net.msrandom.witchery.infusion.symbol.ProjectileSymbolEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 Mixins:
 [Bugfix] Fix weird behaviour when Alohomora is used on certain doors
 */
@Mixin(AlohomoraSymbolEffect.class)
public abstract class AlohomoraSymbolEffectMixin extends ProjectileSymbolEffect {

    /** This Mixin forces Alohomora to use the toggleDoor function of BlockDoor to open/close it
     * instead of setting blockstates directly, as it is more compatible */
    @Inject(method = "onCollision", remap = false, cancellable = true, at = @At("HEAD"))
    public void onCollision(World world, EntityLivingBase caster, RayTraceResult hit, EntitySpellEffect spell, CallbackInfo ci) {

        if (!ModConfig.PatchesConfiguration.InfusionTweaks.alohomora_fixOnRowanDoors) {
            return;
        }

        if (hit.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos pos = hit.getBlockPos();
            final IBlockState state = world.getBlockState(pos);
            if (state.getBlock() instanceof BlockDoor) {
                // For some reason getting the Upper half always returns OPEN = false, so retrieve bottom block
                final BlockPos lowerPos = state.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.UPPER ? pos.down() : pos;
                final IBlockState lowerState = world.getBlockState(lowerPos);
                boolean isOpen = lowerState.getValue(BlockDoor.OPEN);
                BlockDoor door = (BlockDoor)lowerState.getBlock();
                door.toggleDoor(world, pos, !isOpen);
            }
        }

        ci.cancel();
    }
}
