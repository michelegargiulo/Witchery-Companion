package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockPlanks;
import net.minecraft.util.BlockRenderLayer;
import net.msrandom.witchery.block.BlockPerpetualIceGate;
import org.spongepowered.asm.mixin.Mixin;

@MethodsReturnNonnullByDefault
@Mixin(BlockPerpetualIceGate.class)
public abstract class BlockPerpetualIceGateMixin extends BlockFenceGate {

    public BlockPerpetualIceGateMixin() {
        super(BlockPlanks.EnumType.OAK);
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
