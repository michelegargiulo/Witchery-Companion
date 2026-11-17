package com.smokeythebandicoot.witcherycompanion.api;

import com.smokeythebandicoot.witcherycompanion.api.accessors.blocks.altar.IAltarDyeAccessor;
import com.smokeythebandicoot.witcherycompanion.api.accessors.blocks.altar.IBlockAltarAccessor;
import net.minecraft.block.Block;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.msrandom.witchery.block.BlockAltar;
import net.msrandom.witchery.init.WitcheryBlocks;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;

/**
 * Dyeable Altar: the cloth of the Altar can be dyed. The dye is stored in the tile entity of every block of the Altar
 * (see {@link IAltarDyeAccessor}) and drawn by tinting the cloth of the Altar models (see AltarColorHandler)
 */
public class AltarDyeApi {

    /** True if the block at pos is an Altar */
    public static boolean isAltar(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() == WitcheryBlocks.ALTAR;
    }

    /** The blocks that get dyed together: all the blocks of the complete Altar, or just pos if it is not part of one */
    public static Set<BlockPos> getAltarBlocks(World world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        if (block instanceof IBlockAltarAccessor) {
            BlockAltar.AltarPatternMatch match = ((IBlockAltarAccessor) block).witcherycompanion$accessor$findParts(world, pos);
            if (match != null && !match.getParts().isEmpty() && match.getParts().containsKey(pos)) {
                return match.getParts().keySet();
            }
        }
        return Collections.singleton(pos);
    }

    /**
     * Dyes the Altar the block at pos belongs to (all its blocks, or only that one if it is not part of a complete
     * Altar). A null color removes the dye. Server side. Returns true if the dye of any block has changed
     */
    public static boolean dyeAltar(World world, BlockPos pos, @Nullable EnumDyeColor color) {
        if (world.isRemote || !isAltar(world, pos)) return false;

        int dyeMeta = color == null ? -1 : color.getMetadata();
        boolean changed = false;
        for (BlockPos part : getAltarBlocks(world, pos)) {
            TileEntity tile = world.getTileEntity(part);
            if (tile instanceof IAltarDyeAccessor) {
                changed |= ((IAltarDyeAccessor) tile).witcherycompanion$accessor$setDyeColor(dyeMeta);
            }
        }
        return changed;
    }

    /** Dye of the Altar block at pos, or null if it is not dyed (or not an Altar). Safe on both sides */
    @Nullable
    public static EnumDyeColor getDye(net.minecraft.world.IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof IAltarDyeAccessor) {
            int dye = ((IAltarDyeAccessor) tile).witcherycompanion$accessor$getDyeColor();
            if (dye >= 0 && dye <= 15) return EnumDyeColor.byMetadata(dye);
        }
        return null;
    }

}
