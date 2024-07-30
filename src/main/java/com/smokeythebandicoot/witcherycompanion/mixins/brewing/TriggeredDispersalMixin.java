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
        if (!ModConfig.PatchesConfiguration.BrewsTweaks.common_fixTriggeredDispersal) {
            return;
        }

        // Retrieve BlockPos and BlockState
        // TODO: look around for triggerable blocks. Look for impact block, then the mop.sideHit, then blocks around a + shape
        BlockPos coord = witchery_Patcher$getImpactCoords(mop, modifiers);
        IBlockState state = world.getBlockState(coord);
        Block block = state.getBlock();

        // Check if block is 'Triggerable'. If not, nothing we can do
        if (!(block instanceof ICursableTrigger)) {
            ci.cancel();
            return;
        }

        // For Blocks that occupy multible block spaces (doors, beds, etc)
        ICursableTrigger triggeredBlock = (ICursableTrigger)block;
        coord = triggeredBlock.getEffectivePos(world, coord);

        // Check if the block has already a TileEntity associated and is not a TileEntityCursedBlock
        TileEntity tileEntity = world.getTileEntity(coord);
        TileEntityCursedTrigger cursedTileEntity = null;

        // No TileEntity: create one and assign to this block
        if (tileEntity == null) {
            cursedTileEntity = new TileEntityCursedTrigger();
            cursedTileEntity.setPos(coord);
            world.setTileEntity(coord, cursedTileEntity);
            cursedTileEntity.initialize(modifiers, actionList);
            WitcheryUtils.spawnParticlesAt(modifiers.thrower, EnumParticleTypes.PORTAL, coord.getX(), coord.getY(), coord.getZ(), 1.0, 10);


        // TileEntityCursedBlock already exists: call initialize to overwrite effect
        } else if (tileEntity instanceof TileEntityCursedTrigger) {
            cursedTileEntity = (TileEntityCursedTrigger) tileEntity;
            cursedTileEntity.updateCurse(modifiers, actionList);
            WitcheryUtils.spawnParticlesAt(modifiers.thrower, EnumParticleTypes.PORTAL, coord.getX(), coord.getY(), coord.getZ(), 1.0, 10);

        // The Block already implements a different TileEntity, but the TE implements ICursableTrigger
        // (usually this is implemented by the Block class)
        } else if (tileEntity instanceof IProxedCursedTrigger) {
            IProxedCursedTrigger proxedTrigger = (IProxedCursedTrigger) tileEntity;
            proxedTrigger.absorbBrew(modifiers, actionList);
            //WitcheryUtils.spawnParticlesAt(modifiers.thrower, EnumParticleTypes.PORTAL, coord.getX(), coord.getY(), coord.getZ(), 1.0, 10);
        }

        // Else: nothing we can do (then why ICursableTrigger has been implemented the
        // block class, but IProxedCursableTrigger was not implemented on the TE?)

        // TileEntity that is not TileEntityCursedTrigger already exists
        if (cursedTileEntity == null) {
            ci.cancel();
            return;
        }

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

}
