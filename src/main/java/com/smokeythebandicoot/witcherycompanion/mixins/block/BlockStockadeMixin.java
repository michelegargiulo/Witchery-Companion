package com.smokeythebandicoot.witcherycompanion.mixins.block;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.msrandom.witchery.block.BlockStockade;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 Mixins:
 [Workaround] Expands Stockade Bounding Box to avoid player head getting inside of the model
 */
@Mixin(value = BlockStockade.class)
public abstract class BlockStockadeMixin extends Block {

    @Shadow(remap = false)
    protected abstract IBlockState setConnections(IBlockState state, IBlockAccess world, BlockPos pos);

    @Shadow(remap = false) @Final
    private static PropertyInteger CONNECTIONS;

    private static final float H_SMALL = 0.045f;
    private static final float H_LARGE = 1.00f - H_SMALL;
    private static final float V_SHORT = 0.90f;

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

    /*
        WitcheryUtils.getBlockShape(4.8, 0.0, 4.8, 11.2, 14.4, 11.2),
        WitcheryUtils.getBlockShape(0.8, 0.0, 4.8, 15.2, 14.4, 11.2),
        WitcheryUtils.getBlockShape(4.8, 0.0, 0.8, 11.2, 14.4, 15.2),
        WitcheryUtils.getBlockShape(0.8, 0.0, 0.8, 15.2, 14.4, 15.2),
        WitcheryUtils.getBlockShape(4.8, 0.0, 4.8, 11.2, 16.0, 11.2),
        WitcheryUtils.getBlockShape(0.8, 0.0, 4.8, 15.2, 16.0, 11.2),
        WitcheryUtils.getBlockShape(4.8, 0.0, 0.8, 11.2, 16.0, 15.2),
        WitcheryUtils.getBlockShape(0.8, 0.0, 0.8, 15.2, 16.0, 15.2)


            WitcheryUtils.getBlockShape(4.0, 0.0, 4.0, 12.0, 12.0, 12.0),
            WitcheryUtils.getBlockShape(0.0, 0.0, 4.0, 16.0, 12.0, 12.0),
            WitcheryUtils.getBlockShape(4.0, 0.0, 0.0, 12.0, 12.0, 16.0),
            WitcheryUtils.getBlockShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0),
            WitcheryUtils.getBlockShape(4.0, 0.0, 4.0, 12.0, 16.0, 12.0),
            WitcheryUtils.getBlockShape(0.0, 0.0, 4.0, 16.0, 16.0, 12.0),
            WitcheryUtils.getBlockShape(4.0, 0.0, 0.0, 12.0, 16.0, 16.0),
            WitcheryUtils.getBlockShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)
     */

    private BlockStockadeMixin(Material blockMaterialIn, MapColor blockMapColorIn) {
        super(blockMaterialIn, blockMapColorIn);
    }

    @Inject(method = "getBoundingBox", remap = true, at = @At("HEAD"), cancellable = true)
    public void WPfixBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos, CallbackInfoReturnable<AxisAlignedBB> cir) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.stockade_fixBoundingBox) {
            int connections = setConnections(state, world, pos).getValue(CONNECTIONS);
            cir.setReturnValue(witchery_Patcher$SHAPES[connections]);
        }
    }

}
