package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.msrandom.witchery.block.BlockStockade;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 Mixins:
 [Workaround] Expands Stockade Bounding Box to avoid player head getting inside of the model
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Mixin(value = BlockStockade.class)
public abstract class BlockStockadeMixin extends Block {

    @Shadow(remap = false)
    protected abstract IBlockState setConnections(IBlockState state, IBlockAccess world, BlockPos pos);

    @Shadow(remap = false) @Final
    private static PropertyInteger CONNECTIONS;

    @Unique private static final float H_SMALL = 0.045f;
    @Unique private static final float H_LARGE = 1.00f - H_SMALL;
    @Unique private static final float V_SHORT = 0.90f;

    @Unique private static final AxisAlignedBB CONN_0 = new AxisAlignedBB(H_SMALL, 0.0, H_SMALL, H_LARGE, V_SHORT, H_LARGE);
    @Unique private static final AxisAlignedBB CONN_1 = new AxisAlignedBB(0.0, 0.0, H_SMALL, 1.0, V_SHORT, H_LARGE);
    @Unique private static final AxisAlignedBB CONN_2 = new AxisAlignedBB(H_SMALL, 0.0, 0.0, H_LARGE, V_SHORT, 1.0);
    @Unique private static final AxisAlignedBB CONN_3 = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, V_SHORT, 1.0);
    @Unique private static final AxisAlignedBB CONN_4 = new AxisAlignedBB(H_SMALL, 0.0, H_SMALL, H_LARGE, 1.0, H_LARGE);
    @Unique private static final AxisAlignedBB CONN_5 = new AxisAlignedBB(0.0, 0.0, H_SMALL, 1.0, 1.0, H_LARGE);
    @Unique private static final AxisAlignedBB CONN_6 = new AxisAlignedBB(H_SMALL, 0.0, 0.0, H_LARGE, 1.0, 1.0);
    @Unique private static final AxisAlignedBB CONN_7 = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);

    @Unique
    private static final AxisAlignedBB[] witchery_Patcher$SHAPES = new AxisAlignedBB[]{
            CONN_0, CONN_1, CONN_2, CONN_3, CONN_4, CONN_5, CONN_6, CONN_7
    };

    private BlockStockadeMixin(Material blockMaterialIn, MapColor blockMapColorIn) {
        super(blockMaterialIn, blockMapColorIn);
    }

    /** Overwrites default Witchery bounding box to avoid player head getting inside of stockade model */
    @Inject(method = "getBoundingBox", remap = true, at = @At("HEAD"), cancellable = true)
    public void WPfixBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos, CallbackInfoReturnable<AxisAlignedBB> cir) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.stockade_fixBoundingBox) {
            int connections = setConnections(state, world, pos).getValue(CONNECTIONS);
            cir.setReturnValue(witchery_Patcher$SHAPES[connections]);
        }
    }

    /** This Mixin sets the render layer to be translucent for Ice stockade **/
    @Override
    public BlockRenderLayer getRenderLayer() {
        if (this.material == Material.ICE) {
            return BlockRenderLayer.TRANSLUCENT;
        }
        return super.getRenderLayer();
    }

    /** This Mixin sets the correct parameters for Ice stockade **/
    @Inject(method = "<init>()V", remap = false, at = @At("TAIL"))
    private void setIceMatrial(CallbackInfo ci) {
        this.setDefaultSlipperiness(0.98F);
        this.setLightOpacity(3);
        this.setHardness(2.0F);
        this.setResistance(5.0F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        if (this.material == Material.ICE && (
                side == EnumFacing.UP && blockAccess.getBlockState(pos.up()).getBlock() == this ||
                side == EnumFacing.DOWN && blockAccess.getBlockState(pos.down()).getBlock() == this)) {
            return false;
        }
        return super.shouldSideBeRendered(blockState, blockAccess, pos, side);
    }

}
