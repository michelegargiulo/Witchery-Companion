package com.smokeythebandicoot.witcherycompanion.api.altar;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface IBlockAltarAccessor {

    BlockPos accessor_getCore(IBlockAccess world, BlockPos pos);

}
