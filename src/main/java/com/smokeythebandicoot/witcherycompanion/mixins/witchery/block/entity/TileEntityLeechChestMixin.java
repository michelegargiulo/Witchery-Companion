package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block.entity;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.BlockTweaks;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.msrandom.witchery.block.entity.TileEntityBloodTrap;
import net.msrandom.witchery.block.entity.TileEntityLeechChest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Mixins:
 * [Feature] Implement IItemHandler capability
 */
@Mixin(TileEntityLeechChest.class)
public abstract class TileEntityLeechChestMixin extends TileEntityBloodTrap implements ICapabilityProvider {

    @Unique
    private final IItemHandler witcherycompanion$itemHandler = new InvWrapper((TileEntityLeechChest)(Object)this);


    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return (BlockTweaks.leechChest_tweakEnableItemHandlerCap && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nullable
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY &&
                BlockTweaks.leechChest_tweakEnableItemHandlerCap && facing != null) {
            return (T)witcherycompanion$itemHandler;
        }
        return super.getCapability(capability, facing);
    }

}
