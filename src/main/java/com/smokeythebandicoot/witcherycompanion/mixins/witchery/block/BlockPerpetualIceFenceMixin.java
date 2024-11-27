package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockFence;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockRenderLayer;
import net.msrandom.witchery.block.BlockPerpetualIceFence;
import org.spongepowered.asm.mixin.Mixin;


@MethodsReturnNonnullByDefault
@Mixin(BlockPerpetualIceFence.class)
public abstract class BlockPerpetualIceFenceMixin extends BlockFence {

    private BlockPerpetualIceFenceMixin(Material materialIn, MapColor mapColorIn) {
        super(materialIn, mapColorIn);
        this.setDefaultSlipperiness(0.98F);
        this.setLightOpacity(3);
        this.setHardness(2.0F);
        this.setResistance(5.0F);
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }


}
