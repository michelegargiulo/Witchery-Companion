package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockRenderLayer;
import net.msrandom.witchery.block.BlockPerpetualIcePressurePlate;
import org.spongepowered.asm.mixin.Mixin;


@MethodsReturnNonnullByDefault
@Mixin(BlockPerpetualIcePressurePlate.class)
public abstract class BlockPerpetualIcePressurePlateMixin extends BlockPressurePlate {

    public BlockPerpetualIcePressurePlateMixin() {
        super(Material.ICE, Sensitivity.EVERYTHING);
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

