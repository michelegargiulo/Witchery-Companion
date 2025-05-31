package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block.entity;

import com.smokeythebandicoot.witcherycompanion.api.dispersaltrigger.IProxedCursedTrigger;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.BlockTweaks;
import com.smokeythebandicoot.witcherycompanion.patches.triggerdispersal.TileEntityCursedTrigger;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.msrandom.witchery.block.entity.TileEntityGrassper;
import net.msrandom.witchery.util.BlockUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Mixins:
 * [Feature] Implement compat for Triggered Brew Dispersal
 * [Feature] Implement compat for IItemHandling capability
 * [Bugfix] Inventory not marked dirty after changing items
 */
@Mixin(TileEntityGrassper.class)
public class TileEntityGrassperMixin extends TileEntity implements IProxedCursedTrigger, ICapabilityProvider {

    @Shadow(remap = false)
    private ItemStack item;

    @Unique
    protected TileEntityCursedTrigger witchery_Patcher$innerTrigger = null;

    @Unique
    private final IItemHandler witcherycompanion$itemHandler = new InvWrapper((TileEntityGrassper)(Object)this);


    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return (BlockTweaks.grassper_tweakEnableItemHandlerCap && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nullable
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY &&
                BlockTweaks.grassper_tweakEnableItemHandlerCap && facing != null) {
            return (T) witcherycompanion$itemHandler;
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public TileEntityCursedTrigger getInnerTrigger() {
        // Updates world and pos in case the outer TE has been moved or when the
        // world is first loaded, as in this phase world is still null (after readFromNBT)
        if (witchery_Patcher$innerTrigger != null) {
            witchery_Patcher$innerTrigger.setWorld(this.world);
            witchery_Patcher$innerTrigger.setPos(this.pos);
        }
        return witchery_Patcher$innerTrigger;
    }

    @Override
    public TileEntityCursedTrigger createInnerTrigger() {
        witchery_Patcher$innerTrigger = new TileEntityCursedTrigger();
        witchery_Patcher$innerTrigger.setWorld(this.world);
        witchery_Patcher$innerTrigger.setPos(this.pos);
        return witchery_Patcher$innerTrigger;
    }

    @Override
    public void setInnerTrigger(TileEntityCursedTrigger trigger) {
        witchery_Patcher$innerTrigger = trigger;
    }

    @Inject(method = "readFromNBT", remap = true, at = @At("TAIL"))
    private void injectInnerTriggerRead(NBTTagCompound tag, CallbackInfo ci) {
        this.readTriggerFromNBT(tag);
    }

    @Inject(method = "writeToNBT", remap = true, at = @At("TAIL"))
    private void injectInnerTriggerWrite(NBTTagCompound tag, CallbackInfoReturnable<NBTTagCompound> cir) {
        this.writeTriggerToNBT(tag);
    }

    /** This Mixin calls for a Block update after the Grassper's inventory has been updated. Otherwise
     * a client-server desync happens, where the inv is updated on server but client still thinks that the item
     * is still in the inventory, causing it to render in RenderGrassper class **/
    @Inject(method = "decrStackSize", remap = false, at = @At(value = "HEAD"), cancellable = true)
    private void markDirtyAfterItemChange(int slot, int size, CallbackInfoReturnable<ItemStack> cir) {
        cir.setReturnValue(slot == 0 ? this.item.splitStack(size) : ItemStack.EMPTY);
        BlockUtil.notifyBlockUpdate(this.world, this.getPos());
    }

}
