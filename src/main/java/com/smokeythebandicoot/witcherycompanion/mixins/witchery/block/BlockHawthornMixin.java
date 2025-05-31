package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block;

import com.smokeythebandicoot.witcherycompanion.api.dispersaltrigger.ICursableTrigger;
import net.minecraft.block.material.Material;
import net.msrandom.witchery.block.BlockHawthornDoor;
import net.msrandom.witchery.block.WitcheryBlockDoor;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockHawthornDoor.class)
public abstract class BlockHawthornMixin extends WitcheryBlockDoor {

    private BlockHawthornMixin(Material material) {
        super(material);
    }

}
