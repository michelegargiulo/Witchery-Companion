package com.smokeythebandicoot.witcherycompanion.mixins.block;

import com.smokeythebandicoot.witcherycompanion.api.dispersaltrigger.ICursableTrigger;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.BrewsTweaks.TriggeredDispersalTweaks;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.msrandom.witchery.block.WitcheryBlockDoor;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.Nonnull;

/**
 Mixins:
 [Feature] Add Triggered Dispersal compat
 */
@Mixin(WitcheryBlockDoor.class)
public abstract class WitcheryBlockDoorMixin extends BlockDoor implements ICursableTrigger {

    private WitcheryBlockDoorMixin(Material materialIn) {
        super(materialIn);
    }

    @Override
    public boolean hasTileEntity(@Nonnull IBlockState state) {
        return true;
    }

    @Override
    public boolean isTriggerEnabled() {
        return TriggeredDispersalTweaks.enable_dispersalRework &&
                TriggeredDispersalTweaks.enable_door;
    }
}
