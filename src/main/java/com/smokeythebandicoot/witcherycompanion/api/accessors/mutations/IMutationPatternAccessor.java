package com.smokeythebandicoot.witcherycompanion.api.accessors.mutations;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;

import java.util.List;

public interface IMutationPatternAccessor {

    IBlockState[][][] getMatrix();

    List<Class<? extends EntityLiving>> getEntities();

}
