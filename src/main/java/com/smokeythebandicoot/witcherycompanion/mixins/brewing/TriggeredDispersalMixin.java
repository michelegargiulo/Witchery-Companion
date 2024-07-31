package com.smokeythebandicoot.witcherycompanion.mixins.brewing;

import com.smokeythebandicoot.witcherycompanion.api.dispersaltrigger.IProxedCursedTrigger;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.api.dispersaltrigger.ICursableTrigger;
import com.smokeythebandicoot.witcherycompanion.patches.triggerdispersal.TileEntityCursedTrigger;
import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.msrandom.witchery.brewing.ModifiersImpact;
import net.msrandom.witchery.brewing.TriggeredDispersal;
import net.msrandom.witchery.brewing.action.BrewActionList;
import net.msrandom.witchery.util.WitcheryUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TriggeredDispersal.class)
public abstract class TriggeredDispersalMixin {

    @Inject(method = "onImpactSplashPotion", remap = false, cancellable = true, at = @At("HEAD"))
    public void onImpactSplashPotion(World world, BrewActionList actionList, RayTraceResult mop, ModifiersImpact modifiers, CallbackInfo ci) {
        if (!ModConfig.PatchesConfiguration.BrewsTweaks.TriggeredDispersalTweaks.enable_dispersalRework) {
            return;
        }

        // Retrieve BlockPos and BlockState
        // TODO: look around for triggerable blocks. Look for impact block, then the mop.sideHit, then blocks around a + shape
        BlockPos impactPos = witchery_Patcher$getImpactCoords(mop, modifiers);
        IBlockState state = world.getBlockState(impactPos);
        Block block = state.getBlock();

        // Check if block is 'Triggerable'. If not, nothing we can do
        if (!(block instanceof ICursableTrigger)) {
            ci.cancel();
            return;
        }

        // For Blocks that occupy multible block spaces (doors, beds, etc)
        ICursableTrigger triggeredBlock = (ICursableTrigger)block;
        BlockPos effectivePos = triggeredBlock.getEffectivePos(world, impactPos);

        // Check if the effective position is valid. If not, it means that the target block
        // is not in a valid state to receive the trigger (for example, un-connected tripwires). Return
        if (effectivePos == null) {
            ci.cancel();
            return;
        }

        // Check if the block has already a TileEntity associated and is not a TileEntityCursedBlock
        TileEntity tileEntity = world.getTileEntity(effectivePos);
        TileEntityCursedTrigger cursedTileEntity = null;

        // No TileEntity: create one and assign to this block
        if (tileEntity == null) {
            cursedTileEntity = new TileEntityCursedTrigger();
            cursedTileEntity.setPos(effectivePos);
            world.setTileEntity(effectivePos, cursedTileEntity);
            cursedTileEntity.initialize(modifiers, actionList);
            //witchery_Patcher$spawnParticles(world, cursedTileEntity.getPos(), 25);

        // TileEntityCursedBlock already exists: call initialize to overwrite effect
        } else if (tileEntity instanceof TileEntityCursedTrigger) {
            cursedTileEntity = (TileEntityCursedTrigger) tileEntity;
            cursedTileEntity.updateCurse(modifiers, actionList);
            //witchery_Patcher$spawnParticles(world, cursedTileEntity.getPos(), 25);

        // The Block already implements a different TileEntity, but the TE implements ICursableTrigger
        // (usually this is implemented by the Block class)
        } else if (tileEntity instanceof IProxedCursedTrigger) {
            IProxedCursedTrigger proxedTrigger = (IProxedCursedTrigger) tileEntity;
            proxedTrigger.absorbBrew(modifiers, actionList);
            //witchery_Patcher$spawnParticles(world, tileEntity.getPos(), 25);
        }

        // Else: nothing we can do (then why ICursableTrigger has been implemented the
        // block class, but IProxedCursableTrigger was not implemented on the TE?)

        // TileEntity that is not TileEntityCursedTrigger already exists
        if (cursedTileEntity == null) {
            ci.cancel();
            return;
        }

        triggeredBlock.spawnParticles(world, impactPos, effectivePos);
        ci.cancel();
    }



    @Unique
    private BlockPos witchery_Patcher$getImpactCoords(RayTraceResult mop, ModifiersImpact modifiers) {
        if (mop != null) {
            switch (mop.typeOfHit) {
                case BLOCK:
                    //return mop.getBlockPos().offset(mop.sideHit);
                    return mop.getBlockPos();
                case ENTITY:
                    return mop.entityHit.getPosition();
                default:
                    return new BlockPos(modifiers.impactPosition);
            }
        }
        return new BlockPos(modifiers.impactPosition);

    }

    /*
    @Unique
    private void witchery_Patcher$spawnParticles(World world, BlockPos pos, int count) {
        if (world == null || pos == null) return;
        for (int x = 0; x < count; x++) {
            world.spawnParticle(EnumParticleTypes.PORTAL, false,
                    pos.getX() + world.rand.nextGaussian() * 0.5,
                    pos.getY() + world.rand.nextGaussian() * 0.5,
                    pos.getZ() + world.rand.nextGaussian() * 0.5,
                    1.0f + world.rand.nextGaussian() * 0.5,
                    1.0f + world.rand.nextGaussian() * 0.5,
                    1.0f + world.rand.nextGaussian() * 0.5
                    );
        }
    }
    */
}
