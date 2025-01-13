package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockRenderLayer;
import net.msrandom.witchery.block.BlockPerpetualIceDoor;
import net.msrandom.witchery.block.WitcheryBlockDoor;
import org.spongepowered.asm.mixin.Mixin;

@MethodsReturnNonnullByDefault
@Mixin(BlockPerpetualIceDoor.class)
public abstract class BlockPerpetualIceDoorMixin extends WitcheryBlockDoor {

    public BlockPerpetualIceDoorMixin() {
        super(Material.ICE);
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
