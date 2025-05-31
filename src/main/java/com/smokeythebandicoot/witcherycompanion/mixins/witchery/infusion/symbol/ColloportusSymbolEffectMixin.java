package com.smokeythebandicoot.witcherycompanion.mixins.witchery.infusion.symbol;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemDoor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.msrandom.witchery.entity.EntitySpellEffect;
import net.msrandom.witchery.infusion.symbol.ColloportusSymbolEffect;
import net.msrandom.witchery.infusion.symbol.ProjectileSymbolEffect;
import net.msrandom.witchery.init.WitcheryBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 Mixins:
 [Bugfix] Preserve Door Hinge position and Facing when transformed
 */
@Mixin(ColloportusSymbolEffect.class)
public abstract class ColloportusSymbolEffectMixin extends ProjectileSymbolEffect {

    @Inject(method = "onCollision", remap = false, cancellable = true, at = @At("HEAD"))
    private void fixCrashOnCollision(World world, EntityLivingBase caster, RayTraceResult hit, EntitySpellEffect spell, CallbackInfo ci) {

        if (!ModConfig.PatchesConfiguration.InfusionTweaks.colloportus_fixPreserveDoorProperties) {
            return;
        }

        if (hit.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos hitPos = hit.getBlockPos();
            IBlockState state = world.getBlockState(hitPos);
            if (state.getBlock() instanceof BlockDoor) {
                BlockPos pos = state.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.LOWER ? hitPos : hitPos.down();
                BlockDoor.EnumHingePosition hingePosition = state.getValue(BlockDoor.HINGE);
                EnumFacing facing = state.getValue(BlockDoor.FACING);

                world.setBlockToAir(pos);
                world.setBlockToAir(pos.up());
                ItemDoor.placeDoor(world, pos, facing, WitcheryBlocks.ROWAN_DOOR, hingePosition == BlockDoor.EnumHingePosition.RIGHT);
            }
        }

        ci.cancel();

    }

}
