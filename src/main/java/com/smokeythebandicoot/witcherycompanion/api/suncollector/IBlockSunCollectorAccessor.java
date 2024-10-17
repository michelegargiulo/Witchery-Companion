package com.smokeythebandicoot.witcherycompanion.api.suncollector;

import net.minecraft.block.state.IBlockState;

public interface IBlockSunCollectorAccessor {
    int getPower(IBlockState state);
}
