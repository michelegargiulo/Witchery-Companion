package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.BlockTweaks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.msrandom.witchery.block.BlockSpiritPortal;
import net.msrandom.witchery.init.WitcheryBlocks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Mixins:
 * [Bugfix] Fix Spirit Portal AABB due to missing meta to state and state to meta functions
 */
@ParametersAreNonnullByDefault
@Mixin(BlockSpiritPortal.class)
public abstract class BlockSpiritPortalMixin extends BlockBreakable {

    @Shadow(remap = false) @Final
    private static PropertyEnum<EnumFacing.Axis> AXIS;

    @Shadow(remap = false) @Final
    private Block portalFrameBlock;

    @Unique
    private boolean witchery_Patcher$buildingPortal = false;


    private BlockSpiritPortalMixin(Material materialIn, boolean ignoreSimilarityIn) {
        super(materialIn, ignoreSimilarityIn);
    }


    /** This Mixin fixes the getMetaFromState returning always zero **/
    @Inject(method = "getMetaFromState", remap = true, cancellable = true, at = @At("HEAD"))
    private void fixGetMetaFromState(IBlockState state, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(state.getValue(AXIS) == EnumFacing.Axis.X ? 0 : 1);
    }

    /** This Mixin adds the getStateFromMeta function, that is completely missing **/
    @Override @Nonnull
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(AXIS, meta == 0 ? EnumFacing.Axis.X : EnumFacing.Axis.Z);
    }

    /** Better similarity with Nether Portal **/
    @Inject(method = "getActualState", remap = true, cancellable = true, at = @At("HEAD"))
    public void getActualState(IBlockState state, IBlockAccess world, BlockPos pos, CallbackInfoReturnable<IBlockState> cir) {
        cir.setReturnValue(state);
    }

    /** This Mixin sets the layer to be translucent instead of solid, allowing better transparency and fidelity with the original Witchery **/
    @SideOnly(Side.CLIENT) @Nonnull
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    /** This Mixin should not be required, but it's here for better compat and similarity with the Nether portal **/
    @Override @Nonnull
    public IBlockState withRotation(@Nonnull IBlockState state, Rotation rot) {
        switch (rot) {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:
                switch (state.getValue(AXIS)) {
                    case X:
                        return state.withProperty(AXIS, EnumFacing.Axis.Z);
                    case Z:
                        return state.withProperty(AXIS, EnumFacing.Axis.X);
                    default:
                        return state;
                }
            default:
                return state;
        }
    }

    /** This Mixin fixes the face shape of the spirit portal. Being solid by default, buttons, levers, etc could be placed on it **/
    @Nonnull
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        if (BlockTweaks.spiritPortal_fixBlockFaceShape) {
            return BlockFaceShape.UNDEFINED;
        }
        return BlockFaceShape.SOLID;
    }

    /** Improved checks for break condition **/
    @Inject(method = "neighborChanged", remap = false, cancellable = true, at = @At("HEAD"))
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, CallbackInfo ci) {

        if (!BlockTweaks.spiritPortal_fixBreakCondition) {
            return;
        }

        // Skip checks if we are building the portal
        if (this.witchery_Patcher$buildingPortal) {
            ci.cancel();
            return;
        }

        // Up and Down blocks should either be portal frame or portal block. If not, break portal
        if (witchery_Patcher$shouldPortalBreak(world, pos.up()) || witchery_Patcher$shouldPortalBreak(world, pos.down())) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }

        EnumFacing.Axis axis = state.getValue(AXIS);

        // Depending on the axis of the portal, we either check if west-east or north-south block are either portal frame or portal block. If not, break portal
        if (axis == EnumFacing.Axis.X && (witchery_Patcher$shouldPortalBreak(world, pos.east()) || witchery_Patcher$shouldPortalBreak(world, pos.west()))) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        } else if (axis == EnumFacing.Axis.Z && (witchery_Patcher$shouldPortalBreak(world, pos.south()) || witchery_Patcher$shouldPortalBreak(world, pos.north()))) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }

        // Prevent original code from running
        ci.cancel();

    }

    @Unique
    private boolean witchery_Patcher$shouldPortalBreak(World world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        return block != this && block != this.portalFrameBlock;
    }

    /** Better checks for portal creation **/
    @Inject(method = "tryToCreatePortal", remap = false, at = @At("HEAD"), cancellable = true)
    private void allowPortalBuilding(World world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        this.witchery_Patcher$buildingPortal = true;

        if (world.getBlockState(pos.down()).getBlock() != this.portalFrameBlock) {
            cir.setReturnValue(false);
            this.witchery_Patcher$buildingPortal = false;
            return;
        }

        // CASE 1: EAST - WEST (X Axis), EAST is frame
        if (world.getBlockState(pos.east()).getBlock() == this.portalFrameBlock) {
            if (world.getBlockState(pos.west()).getBlock() == Blocks.AIR) {
                if (witchery_Patcher$isValidAbove(world, pos.up(), pos.west().up())) {
                    IBlockState portalState = WitcheryBlocks.SPIRIT_PORTAL.getDefaultState().withProperty(AXIS, EnumFacing.Axis.X);
                    world.setBlockState(pos, portalState);
                    world.setBlockState(pos.up(), portalState);
                    world.setBlockState(pos.west(), portalState);
                    world.setBlockState(pos.west().up(), portalState);
                    cir.setReturnValue(true);
                    this.witchery_Patcher$buildingPortal = false;
                    return;
                }
            }
            cir.setReturnValue(false);
            this.witchery_Patcher$buildingPortal = false;
            return;
        }

        // CASE 2: EAST - WEST (X Axis), West is frame
        else if (world.getBlockState(pos.west()).getBlock() == this.portalFrameBlock) {
            if (world.getBlockState(pos.east()).getBlock() == Blocks.AIR) {
                if (witchery_Patcher$isValidAbove(world, pos.up(), pos.east().up())) {
                    IBlockState portalState = WitcheryBlocks.SPIRIT_PORTAL.getDefaultState().withProperty(AXIS, EnumFacing.Axis.X);
                    world.setBlockState(pos, portalState);
                    world.setBlockState(pos.up(), portalState);
                    world.setBlockState(pos.east(), portalState);
                    world.setBlockState(pos.east().up(), portalState);
                    cir.setReturnValue(true);
                    this.witchery_Patcher$buildingPortal = false;
                    return;
                }
            }
            cir.setReturnValue(false);
            this.witchery_Patcher$buildingPortal = false;
            return;
        }

        // CASE 3: NORTH - SOUTH (Z Axis), North is frame
        else if (world.getBlockState(pos.north()).getBlock() == this.portalFrameBlock) {
            if (world.getBlockState(pos.south()).getBlock() == Blocks.AIR) {
                if (witchery_Patcher$isValidAbove(world, pos.up(), pos.south().up())) {
                    IBlockState portalState = WitcheryBlocks.SPIRIT_PORTAL.getDefaultState().withProperty(AXIS, EnumFacing.Axis.Z);
                    world.setBlockState(pos, portalState);
                    world.setBlockState(pos.up(), portalState);
                    world.setBlockState(pos.south(), portalState);
                    world.setBlockState(pos.south().up(), portalState);
                    cir.setReturnValue(true);
                    this.witchery_Patcher$buildingPortal = false;
                    return;
                }
            }
            cir.setReturnValue(false);
            this.witchery_Patcher$buildingPortal = false;
            return;
        }

        // CASE 4: EAST - WEST (X Axis), West is frame
        else if (world.getBlockState(pos.south()).getBlock() == this.portalFrameBlock) {
            if (world.getBlockState(pos.north()).getBlock() == Blocks.AIR) {
                if (witchery_Patcher$isValidAbove(world, pos.up(), pos.north().up())) {
                    IBlockState portalState = WitcheryBlocks.SPIRIT_PORTAL.getDefaultState().withProperty(AXIS, EnumFacing.Axis.Z);
                    world.setBlockState(pos, portalState);
                    world.setBlockState(pos.up(), portalState);
                    world.setBlockState(pos.north(), portalState);
                    world.setBlockState(pos.north().up(), portalState);
                    cir.setReturnValue(true);
                    this.witchery_Patcher$buildingPortal = false;
                    return;
                }
            }
            cir.setReturnValue(false);
            this.witchery_Patcher$buildingPortal = false;
            return;
        }

        this.witchery_Patcher$buildingPortal = false;
        cir.setReturnValue(false);
    }

    @Unique
    private boolean witchery_Patcher$isValidAbove(World world, BlockPos pos1, BlockPos pos2) {
        return (
                world.getBlockState(pos1).getBlock() == Blocks.AIR
                && world.getBlockState(pos2).getBlock() == Blocks.AIR
                && world.getBlockState(pos1.up()).getBlock() == this.portalFrameBlock
                && world.getBlockState(pos1.up()).getBlock() == this.portalFrameBlock
        );
    }

}
