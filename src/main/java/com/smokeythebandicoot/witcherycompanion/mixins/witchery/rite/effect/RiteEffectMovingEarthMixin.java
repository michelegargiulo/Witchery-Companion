package com.smokeythebandicoot.witcherycompanion.mixins.witchery.rite.effect;

import com.smokeythebandicoot.witcherycompanion.api.RiteOfMovingEarthApi;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.utils.BlockPredicateCircle;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.msrandom.witchery.block.BlockFetish;
import net.msrandom.witchery.block.entity.TileEntityCircle;
import net.msrandom.witchery.network.PacketParticles;
import net.msrandom.witchery.network.WitcheryNetworkChannel;
import net.msrandom.witchery.rite.RiteHandler;
import net.msrandom.witchery.rite.effect.RiteEffectMovingEarth;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 Mixins:
 [Bugfix] Disable moving TileEntities with Rite Of Moving Earth due to dupes, bugs and crashes
 [Tweak] Blacklist for blocks that should not be moved by the Rite of Moving Earth
 */
@Mixin(value = RiteEffectMovingEarth.class)
public abstract class RiteEffectMovingEarthMixin {

    @Final @Shadow(remap = false)
    private int radius;

    @Mutable @Final @Shadow(remap = false)
    private int height;


    @Inject(method = "process", at = @At("HEAD"), remap = false, cancellable = true)
    public void process(World world, BlockPos pos, int ticks, AtomicInteger stage, TileEntityCircle.ActivatedRitual ritual, CallbackInfoReturnable<RiteHandler.Result> cir) {

        if (world.isRemote) {
            cir.setReturnValue(RiteHandler.Result.COMPLETED);
            return;
        }

        if (ModConfig.PatchesConfiguration.RitesTweaks.movingEarth_tweakDisableVoidingBlocks) {
            boolean canMoveTop = witchery_Patcher$theresEmptySpace(world, pos.up(height + 1), radius);
            boolean canMoveCur = witchery_Patcher$theresEmptySpace(world, pos.up(height), radius);

            if (!(canMoveTop || canMoveCur)) {
                // If refund if partial or refund if initial and stage == 0, then refund player, else do nothing
                if ((ModConfig.PatchesConfiguration.RitesTweaks.movingEarth_tweakRefundPolicy == 1) ||
                        (ModConfig.PatchesConfiguration.RitesTweaks.movingEarth_tweakRefundPolicy == 2 && stage.get() == 0))
                    cir.setReturnValue(RiteHandler.Result.ABORTED_REFUND);
                else
                    cir.setReturnValue(RiteHandler.Result.COMPLETED);
                return;
            }
        }

        // Spawn particles and play sounds
        if (stage.incrementAndGet() == 1) {
            world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.5F, 0.4F / (world.rand.nextFloat() * 0.4F + 0.8F));
            WitcheryNetworkChannel.sendToAllAround(new PacketParticles((double) pos.getX() + 0.5, pos.getY(), (double) pos.getZ() + 0.5, 0.5F, 1.0F, EnumParticleTypes.PORTAL), world, pos);
        }

        int radius = this.radius + ritual.covenSize * 2;
        int AIR_SPACE = radius * 2;

        for (BlockPos depth = pos.up(AIR_SPACE); depth.getY() >= pos.getY() - this.height; depth = depth.down()) {
            this.witchery_Patcher$drawFilledCircle(world, depth, radius, depth.getY() == pos.getY() - 1);
        }

        // Update Entities, moving them upwards by 1 block
        AxisAlignedBB bounds = new AxisAlignedBB(pos.add(-radius, 0, -radius), pos.add(radius, AIR_SPACE, radius));
        for (Entity entity : world.getEntitiesWithinAABB(Entity.class, bounds)) {
            if (entity.getDistanceSq(pos) <= (double) (radius * radius)) {
                if (entity instanceof EntityLivingBase) {
                    entity.setPositionAndUpdate(entity.posX, entity.posY + 1.0, entity.posZ);
                } else {
                    entity.noClip = true;
                    entity.setPosition(entity.posX, entity.posY + 1.0, entity.posZ);
                    entity.noClip = false;
                }
            }
        }

        if (stage.get() < this.height - 1) {
            cir.setReturnValue(RiteHandler.Result.UPKEEP);
        } else {
            cir.setReturnValue(RiteHandler.Result.COMPLETED);
        }

    }

    //TODO: Use @Redirect or @WrapOperation to redirect method calls instead of rewriting code
    @Unique
    protected void witchery_Patcher$drawFilledCircle(World world, BlockPos pos, int radius, boolean topLayer) {
        int x = radius;
        int z = 0;
        int radiusError = 1 - x;

        while(x >= z) {
            this.witchery_Patcher$drawLine(world, pos.add(-x, 0, z), pos.add(x, 0, z), topLayer, radius, pos.getZ());
            this.witchery_Patcher$drawLine(world, pos.add(-z, 0, x), pos.add(z, 0, x), topLayer, radius, pos.getZ());
            this.witchery_Patcher$drawLine(world, pos.add(-x, 0, -z), pos.add(x, 0, -z), topLayer, radius, pos.getZ());
            this.witchery_Patcher$drawLine(world, pos.add(-z, 0, -x), pos.add(z, 0, -x), topLayer, radius, pos.getZ());
            ++z;
            if (radiusError < 0) {
                radiusError += 2 * z + 1;
            } else {
                --x;
                radiusError += 2 * (z - x + 1);
            }
        }

    }

    @Unique
    protected void witchery_Patcher$drawLine(World world, BlockPos x1, BlockPos x2, boolean topLayer, int radius, int midZ) {
        Iterator var7 = BlockPos.getAllInBox(x1, x2).iterator();

        while(true) {
            BlockPos pos;
            IBlockState state;
            Block block;
            TileEntity tileEntity;
            do {
                do {
                    while(true) {
                        boolean edgeZ;
                        do {
                            Block highBlock;
                            Block lowBlock;
                            do {
                                do {
                                    do {
                                        do {
                                            if (!var7.hasNext()) {
                                                return;
                                            }

                                            pos = (BlockPos)var7.next();
                                            state = world.getBlockState(pos);
                                            block = state.getBlock();
                                            highBlock = world.getBlockState(pos.up()).getBlock();
                                            lowBlock = world.getBlockState(pos.down()).getBlock();
                                        } while(block == Blocks.AIR);
                                    } while(witchery_Patcher$isBlockUnmovable(world, block, pos));
                                } while(witchery_Patcher$isBlockUnmovable(world, highBlock, pos.up()));
                            } while(witchery_Patcher$isBlockUnmovable(world, lowBlock, pos.down()));

                            edgeZ = midZ + radius == x1.getZ() || midZ - radius == x2.getZ();
                        } while(!topLayer && (edgeZ || pos.equals(x1) || pos.equals(x2)) && world.rand.nextInt(7) == 0);

                        if (block.hasTileEntity(state)) {
                            tileEntity = world.getTileEntity(pos);
                            break;
                        }

                        world.setBlockState(pos.up(), state, 2);
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
                    }
                } while(tileEntity == null);
            } while(block instanceof BlockFetish && !((BlockFetish)block).isSpectral());

            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
            world.setBlockState(pos.up(), state, 2);
            world.setTileEntity(pos.up(), tileEntity);
            tileEntity.validate();
        }
    }

    @Unique
    private boolean witchery_Patcher$theresEmptySpace(World world, BlockPos c, int r) {
        return (new BlockPredicateCircle() {
            public boolean onBlock(World world, BlockPos pos) {
                IBlockState state = world.getBlockState(pos);
                return state == Blocks.AIR.getDefaultState();
            }
        }).processFilledCircle(world, c, r);
    }

    @Unique
    private boolean witchery_Patcher$isBlockUnmovable(World world, Block block, BlockPos pos) {
        // Exclude moving TileEntities, if enabled. Must Exclude Circles, otherwise ritual would never work
        TileEntity tileEntity = world.getTileEntity(pos);
        if (ModConfig.PatchesConfiguration.RitesTweaks.movingEarth_tweakDisableMovingTEs &&
                world.getTileEntity(pos) != null && (!(tileEntity instanceof TileEntityCircle))) {
            witchery_Patcher$failRitual(world, pos);
            return true;
        }

        IBlockState state = world.getBlockState(pos);
        if (!RiteOfMovingEarthApi.canBeMoved(state)) {
            witchery_Patcher$failRitual(world, pos);
            return true;
        }

        return false;
    }

    @Unique
    private void witchery_Patcher$failRitual(World world, BlockPos pos) {
        if (ModConfig.PatchesConfiguration.RitesTweaks.movingEarth_tweakFailIndicators) {
            world.playSound(null, pos, SoundEvents.BLOCK_SAND_STEP, SoundCategory.PLAYERS, 0.5F, 0.4F / (world.rand.nextFloat() * 0.4F + 0.8F));
            WitcheryNetworkChannel.sendToAllAround(new PacketParticles((double) pos.getX() + 0.5, pos.getY(), (double) pos.getZ() + 0.5, 0.5F, 1.0F, EnumParticleTypes.SMOKE_NORMAL), world, pos);
        }
    }
}
