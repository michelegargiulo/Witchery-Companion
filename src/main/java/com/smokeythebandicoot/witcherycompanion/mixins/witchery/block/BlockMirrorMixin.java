package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block;

import com.smokeythebandicoot.witcherycompanion.api.accessors.mirror.IBlockMirrorAccessor;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.msrandom.witchery.block.BlockMirror;
import net.msrandom.witchery.block.entity.TileEntityMirror;
import net.msrandom.witchery.common.IPowerSource;
import net.msrandom.witchery.common.PowerSources;
import net.msrandom.witchery.entity.EntityReflection;
import net.msrandom.witchery.extensions.PlayerExtendedData;
import net.msrandom.witchery.init.WitcheryBlocks;
import net.msrandom.witchery.init.WitcheryDimensions;
import net.msrandom.witchery.init.WitcherySounds;
import net.msrandom.witchery.init.WitcheryTileEntities;
import net.msrandom.witchery.network.PacketParticles;
import net.msrandom.witchery.network.WitcheryNetworkChannel;
import net.msrandom.witchery.util.TeleportationUtil;
import net.msrandom.witchery.util.WitcheryUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

/**
 * Mixins:
 * [Bugfix] Fix Mirror in Mirror not working
 * [Tweak] Tweak power requirements for using Mirror in Mirror
 */
@Mixin(BlockMirror.class)
public abstract class BlockMirrorMixin extends BlockContainer implements IBlockMirrorAccessor {


    @Shadow(remap = false)
    protected abstract boolean isTransportableEntity(Entity entity);

    @Shadow(remap = false)
    public static boolean isBlockTopOfMirror(IBlockState meta) {
        return false;
    }

    @Shadow(remap = false)
    public abstract AxisAlignedBB getServerSelectedBoundingBoxFromPool(World world, BlockPos pos);

    @Shadow(remap = false)
    public static EnumFacing getDirection(IBlockState meta) {
        return null;
    }

    @Shadow(remap = false) @Final
    private boolean unbreakable;

    private BlockMirrorMixin(Material materialIn) {
        super(materialIn);
    }


    @Override
    public boolean isExit() {
        return this.unbreakable;
    }

    /** **/
    @Inject(method = "onEntityCollision", remap = true, at = @At(value = "HEAD"))
    private void fixMirrorInMirror(World world, BlockPos pos, IBlockState state, Entity entity, CallbackInfo ci) {
        if (!world.isRemote && entity.ticksExisted % 5 == 1 && this.isTransportableEntity(entity)) {
            int hitZoneyShift = 0;
            if (!isBlockTopOfMirror(state)) {
                pos = pos.up();
                if (entity.height <= 1.0F) {
                    hitZoneyShift = -1;
                }

                if (world.getBlockState(pos).getBlock() != this) {
                    return;
                }
            }

            AxisAlignedBB box = this.getServerSelectedBoundingBoxFromPool(world, pos.up(hitZoneyShift));
            double f = (double)entity.width * 0.5;
            double f2 = (double)entity.height;
            AxisAlignedBB entityBox = new AxisAlignedBB(entity.posX - f, entity.posY + (double)entity.height, entity.posZ - f, entity.posX + f, entity.posY + (double)entity.height + f2, entity.posZ + f);
            if (entityBox.intersects(box)) {
                TileEntityMirror tile = WitcheryTileEntities.MIRROR.getAt(world, pos);
                if (tile != null) {
                    EnumFacing side = getDirection(world.getBlockState(pos));
                    int facing = MathHelper.floor((double)(entity.rotationYaw * 4.0F / 360.0F) + 0.5) & 3;
                    int dx = 0;
                    int dz = 0;
                    float shift = 0.7F;
                    float xShift = 0.0F;
                    float zShift = 0.0F;
                    int scale = 1;
                    EnumFacing requiredSide = side.getOpposite();
                    boolean isLiving = entity instanceof EntityLivingBase;
                    if (side == EnumFacing.NORTH) {
                        dz = scale;
                        zShift = -shift;
                        if (isLiving && facing != 0) {
                            return;
                        }
                    } else if (side == EnumFacing.SOUTH) {
                        dz = -scale;
                        zShift = shift;
                        if (isLiving && facing != 2) {
                            return;
                        }
                    } else if (side == EnumFacing.WEST) {
                        dx = scale;
                        xShift = -shift;
                        if (isLiving && facing != 3) {
                            return;
                        }
                    } else if (side == EnumFacing.EAST) {
                        dx = -scale;
                        xShift = shift;
                        if (isLiving && facing != 1) {
                            return;
                        }
                    }

                    boolean inMirrorWorld = WitcheryDimensions.MIRROR.isInDimension(entity);
                    if (!this.unbreakable) {
                        if (inMirrorWorld || tile.demonKilled) {
                            for (int j = 1; j < 32; ++j) {
                                BlockPos np = pos.add(dx * j, 0, dz * j);
                                int nx = np.getX();
                                int ny = np.getY();
                                int nz = np.getZ();
                                IBlockState ns = world.getBlockState(new BlockPos(nx, ny, nz));
                                Block block = ns.getBlock();
                                if (block == this && getDirection(ns) == requiredSide) {
                                    TeleportationUtil.teleportToLocation(0.5 + nx - xShift, ny - 1 + 0.01, 0.5 + nz - zShift, world.provider.getDimension(), entity, true, EnumParticleTypes.WATER_SPLASH, WitcherySounds.BLOCK_MIRROR_SPLASH);
                                    return;
                                }
                            }
                        }
                        if (inMirrorWorld) {
                            for (int j = 1; j < 10; ++j) {
                                BlockPos np = pos.add(dx * j, 0, dz * j);

                                /** Bugfix: world.isAirBlock was checked against 'pos' instead of the new blockpos 'np'
                                 * which caused the check to always be false **/
                                BlockPos newPos = ModConfig.PatchesConfiguration.BlockTweaks.mirror_fixMirrorInMirror ? np : pos;
                                if (world.isAirBlock(newPos) && world.isAirBlock(newPos.down())) {
                                    boolean isPlayerEntryCell = false;
                                    if (entity instanceof EntityPlayer) {
                                        EntityPlayer player = (EntityPlayer) entity;
                                        PlayerExtendedData playerEx = WitcheryUtils.getExtension(player);
                                        isPlayerEntryCell = playerEx.isMirrorWorldEntryPoint(np);
                                    }
                                    int nx = np.getX();
                                    int ny = np.getY();
                                    int nz = np.getZ();
                                    int cx = (nx >> 4 << 4) + 4;
                                    int cy = (ny >> 4 << 4) + 8;
                                    int cz = (nz >> 4 << 4) + 8;
                                    boolean isEntryCell = world.getBlockState(new BlockPos(cx, cy, cz)).getBlock() == WitcheryBlocks.UNBREAKABLE_MIRROR;
                                    if (!isEntryCell || isPlayerEntryCell) {
                                        IPowerSource power = PowerSources.findClosestPowerSource(world, pos);
                                        if (power != null && power.consumePower(ModConfig.PatchesConfiguration.BlockTweaks.mirror_inMirrorPowerConsumption)) {
                                            TeleportationUtil.teleportToLocation(0.5 + nx - xShift, ny + 0.01, 0.5 + nz - zShift, world.provider.getDimension(), entity, true, EnumParticleTypes.WATER_SPLASH, WitcherySounds.BLOCK_MIRROR_SPLASH);
                                        }
                                    }
                                    return;
                                }
                            }
                        } else if (tile.demonKilled) {
                            for (int dy = 2; dy < 16; ++dy) {
                                BlockPos np = pos.add(0, dy, 0);
                                IBlockState meta = world.getBlockState(np);
                                Block block = meta.getBlock();
                                if (block == this) {
                                    if (getDirection(meta) == side) {
                                        if (isBlockTopOfMirror(meta)) {
                                            np = np.down();
                                        }
                                        int nx = np.getX(), ny = np.getY(), nz = np.getZ();
                                        TeleportationUtil.teleportToLocation(0.5 + nx + xShift, ny + 0.01, 0.5 + nz + zShift, world.provider.getDimension(), entity, true, EnumParticleTypes.WATER_SPLASH, WitcherySounds.BLOCK_MIRROR_SPLASH);
                                        if (entity instanceof EntityPlayerMP) {
                                            EntityPlayerMP player2 = (EntityPlayerMP) entity;
                                            double yaw = player2.rotationYaw + 180.0f;
                                            float rev = (float) yaw % 360.0f;
                                            SPacketPlayerPosLook packet = new SPacketPlayerPosLook(player2.posX, player2.posY, player2.posZ, rev, player2.rotationPitch, EnumSet.noneOf(SPacketPlayerPosLook.EnumFlags.class), player2.connection.teleportId);
                                            player2.connection.sendPacket(packet);
                                        }
                                        return;
                                    }
                                }
                            }
                            for (int dy = 2; dy < 16; ++dy) {
                                BlockPos np = pos.add(0, -dy, 0);
                                IBlockState meta = world.getBlockState(np);
                                Block block = meta.getBlock();
                                if (block == this) {
                                    if (getDirection(meta) == side) {
                                        if (isBlockTopOfMirror(meta)) {
                                            np = np.down();
                                        }
                                        TeleportationUtil.teleportToLocation(0.5 + np.getX() + xShift, np.getY() + 0.01, 0.5 + np.getZ() + zShift, world.provider.getDimension(), entity, true, EnumParticleTypes.WATER_SPLASH, WitcherySounds.BLOCK_MIRROR_SPLASH);
                                        if (entity instanceof EntityPlayerMP) {
                                            EntityPlayerMP player2 = (EntityPlayerMP) entity;
                                            double yaw = player2.rotationYaw + 180.0f;
                                            float rev = (float) yaw % 360.0f;
                                            SPacketPlayerPosLook packet = new SPacketPlayerPosLook(player2.posX, player2.posY, player2.posZ, rev, player2.rotationPitch, EnumSet.noneOf(SPacketPlayerPosLook.EnumFlags.class), player2.connection.teleportId);
                                            player2.connection.sendPacket(packet);
                                        }
                                        return;
                                    }
                                }
                            }
                        }
                    }

                    if (entity instanceof EntityPlayer) {
                        EntityPlayer player3 = (EntityPlayer)entity;
                        PlayerExtendedData playerEx2 = WitcheryUtils.getExtension(player3);
                        if (!inMirrorWorld || playerEx2.isMirrorWorldEntryPoint(pos)) {
                            BlockPos dimCoords = tile.getDimCoords();
                            if (dimCoords != null) {
                                float dimX = (float)dimCoords.getX() + 0.5F;
                                float dimY = (float)dimCoords.getY() + 0.01F;
                                float dimZ = (float)dimCoords.getZ() + 0.5F;
                                int targetDim = inMirrorWorld ? tile.dim : WitcheryDimensions.MIRROR.getType().getId();
                                World otherWorld = player3.getServer().getWorld(targetDim);
                                float face = 0.0F;
                                IBlockState state2 = otherWorld.getBlockState(dimCoords);
                                Block block2 = state2.getBlock();
                                if (block2 instanceof BlockMirror) {
                                    EnumFacing mside = getDirection(state2);
                                    float distance = 1.0F;
                                    face = mside.getHorizontalAngle();
                                    if (mside == EnumFacing.NORTH) {
                                        dimZ -= distance;
                                    } else if (mside == EnumFacing.SOUTH) {
                                        dimZ += distance;
                                    } else if (mside == EnumFacing.WEST) {
                                        dimX -= distance;
                                    } else if (mside == EnumFacing.EAST) {
                                        dimX += distance;
                                    }

                                    player3.rotationYaw = face;
                                    TileEntityMirror otherTile = (TileEntityMirror)WitcheryTileEntities.MIRROR.getAt(world, pos);
                                    if (otherTile != null) {
                                        if (otherTile.onCooldown()) {
                                            return;
                                        }

                                        otherTile.addCooldown(60);
                                    }
                                }

                                entity.playSound(WitcherySounds.BLOCK_MIRROR_SPLASH, 0.5F, 0.4F / (entity.world.rand.nextFloat() * 0.4F + 0.8F));
                                WitcheryNetworkChannel.sendToAllTracking(new PacketParticles(entity.posX, entity.posY, entity.posZ, 0.5F, 2.0F, EnumParticleTypes.WATER_SPLASH), entity);
                                double targetX;
                                double targetY;
                                if (!WitcheryDimensions.MIRROR.isInDimension(entity)) {
                                    if (!tile.demonKilled) {
                                        targetX = 7.0;
                                        targetY = 6.0;
                                        float cellMidX = (float)(dimCoords.getX() + 4);
                                        float cellMidY = (float)dimCoords.getY();
                                        float cellMidZ = (float)dimCoords.getZ();
                                        AxisAlignedBB bounds = new AxisAlignedBB((double)cellMidX - targetX, (double)cellMidY - targetY, (double)cellMidZ - targetX, (double)cellMidX + targetX, (double)cellMidY + targetY, (double)cellMidZ + targetX);
                                        List<EntityReflection> EntityReflection = otherWorld.getEntitiesWithinAABB(EntityReflection.class, bounds);
                                        if (EntityReflection.isEmpty()) {
                                            EntityReflection reflection = new EntityReflection(otherWorld);
                                            reflection.setPositionAndRotation(0.5 + (double)cellMidX, 1.1 + (double)cellMidY, 0.5 + (double)cellMidZ, 0.0F, 0.0F);
                                            reflection.enablePersistence();
                                            reflection.world.spawnEntity(reflection);
                                        }
                                    }

                                    playerEx2.setMirrorWorldEntryPoint(dimCoords);
                                    player3.setPositionAndRotation((double)dimX, (double)(dimY - 1.0F), (double)dimZ, face, player3.rotationPitch);
                                    TeleportationUtil.travelToDimension(player3, WitcheryDimensions.MIRROR.getType().getId());
                                    player3.setPositionAndUpdate((double)dimX, (double)(dimY - 1.0F), (double)dimZ);
                                } else if (tile.isConnected) {
                                    player3.setPositionAndRotation((double)dimX, (double)(dimY - 1.0F), (double)dimZ, face, player3.rotationPitch);
                                    TeleportationUtil.travelToDimension(player3, tile.dim);
                                    player3.setPositionAndUpdate((double)dimX, (double)(dimY - 1.0F), (double)dimZ);
                                } else {
                                    targetX = (double)dimX;
                                    targetY = (double)(dimY - 1.0F);
                                    double targetZ = (double)dimZ;
                                    targetDim = tile.dim;
                                    MinecraftServer server = world.getMinecraftServer();
                                    WorldServer[] var43 = server.worlds;
                                    int var44 = var43.length;
                                    int var45 = 0;

                                    while(true) {
                                        if (var45 >= var44) {
                                            player3.setPositionAndRotation(targetX, targetY, targetZ, face, player3.rotationPitch);
                                            TeleportationUtil.travelToDimension(player3, targetDim);
                                            player3.setPositionAndUpdate(targetX, targetY, targetZ);
                                            break;
                                        }

                                        WorldServer worldServer = var43[var45];
                                        Iterator var47 = worldServer.playerEntities.iterator();

                                        while(true) {
                                            while(var47.hasNext()) {
                                                EntityPlayer otherPlayer = (EntityPlayer)var47.next();
                                                Iterator var49 = otherPlayer.inventory.mainInventory.iterator();

                                                while(var49.hasNext()) {
                                                    ItemStack stack = (ItemStack)var49.next();
                                                    if (Block.getBlockFromItem(stack.getItem()) == this) {
                                                        boolean isMirror = tile.isTargetedBy(stack);
                                                        if (isMirror) {
                                                            if (!WitcheryDimensions.MIRROR.isInDimension(otherPlayer)) {
                                                                targetX = otherPlayer.posX;
                                                                targetY = otherPlayer.posY;
                                                                targetZ = otherPlayer.posZ;
                                                                targetDim = otherPlayer.dimension;
                                                            }
                                                            break;
                                                        }
                                                    }
                                                }
                                            }

                                            ++var45;
                                            break;
                                        }
                                    }
                                }

                                entity.playSound(WitcherySounds.BLOCK_MIRROR_SPLASH, 0.5F, 0.4F / (entity.world.rand.nextFloat() * 0.4F + 0.8F));
                                WitcheryNetworkChannel.sendToAllTracking(new PacketParticles(entity.posX, entity.posY, entity.posZ, 0.5F, 2.0F, EnumParticleTypes.WATER_SPLASH), entity);
                            }
                        }
                    }
                }
            }
        }
    }
}
