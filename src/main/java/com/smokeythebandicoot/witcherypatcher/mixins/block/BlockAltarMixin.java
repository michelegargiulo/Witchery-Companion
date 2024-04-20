package com.smokeythebandicoot.witcherypatcher.mixins.block;

import com.smokeythebandicoot.witcherypatcher.WitcheryPatcher;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.msrandom.witchery.block.BlockAltar;
import net.msrandom.witchery.block.entity.TileEntityAltar;
import net.msrandom.witchery.common.PowerSources;
import net.msrandom.witchery.init.WitcheryTileEntities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(value = BlockAltar.class, remap = false)
public abstract class BlockAltarMixin extends BlockContainer {

    @Shadow
    protected abstract BlockPos getCore(IBlockAccess world, BlockPos pos);

    private BlockAltarMixin(Material materialIn) {
        super(materialIn);
    }

    @Override
    public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
        super.eventReceived(state, worldIn, pos, id, param);

        // Call getCore on block
        BlockPos corePos = getCore(worldIn, pos);

        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof TileEntityAltar) {
            TileEntityAltar tileEntityAltar = (TileEntityAltar) tileentity;
            if (corePos == pos) {
                PowerSources.instance().registerPowerSource(tileEntityAltar);
                tileEntityAltar.updatePower();
            }
            return tileentity.receiveClientEvent(id, param);
        }
        return false;

    }

}
