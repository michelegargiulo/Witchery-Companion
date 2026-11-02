package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.BlockTweaks;
import com.smokeythebandicoot.witcherycompanion.utils.SingleEntryLookupCache;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.msrandom.witchery.block.entity.TileEntityWitchesOven;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Mixins:
 * [Feature] Implement IItemHandler capability
 * [Bugfix] Fix burnable container items (for example, lava buckets) being voided
 * [Tweak] Cache the Furnace recipe lookup (performance)
 */
@Mixin(TileEntityWitchesOven.class)
public abstract class TileEntityWitchesOvenMixin extends TileEntity implements ICapabilityProvider {

    /**
     * SLOTS:
     * 0 -> burned item
     * 1 -> clay jars
     * 2 -> fuel slot
     * 3 -> main output
     * 4 -> secondary output
     * */

    @Shadow(remap = false)
    private NonNullList<ItemStack> items;

    @Unique
    private final IItemHandler witcherycompanion$itemHandlerUp = new SidedInvWrapper((TileEntityWitchesOven)(Object)this, EnumFacing.UP);

    @Unique
    private final IItemHandler witcherycompanion$itemHandlerDown = new SidedInvWrapper((TileEntityWitchesOven)(Object)this, EnumFacing.DOWN);

    @Unique
    private final IItemHandler witcherycompanion$itemHandlerSides = new SidedInvWrapper((TileEntityWitchesOven)(Object)this, EnumFacing.NORTH);

    /** Time-to-live (in ticks) of a cached smelting lookup. After that it is thrown away and looked up again, so that
     * a stale entry is always bounded in time even if the recipe list was modified without changing its size **/
    @Unique
    private static final long witcherycompanion$SMELTING_CACHE_TTL = 100L;

    /** Memo of the last Furnace recipe lookup done by this oven. Value is ItemStack.EMPTY when the input has no recipe **/
    @Unique
    private final SingleEntryLookupCache<Item, ItemStack> witcherycompanion$smeltingCache = new SingleEntryLookupCache<>();


    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return (BlockTweaks.witchesOven_tweakEnableItemHandlerCap && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nullable
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY &&
                BlockTweaks.witchesOven_tweakEnableItemHandlerCap && facing != null) {
            switch (facing) {
                case UP:
                    return (T) witcherycompanion$itemHandlerUp;
                case DOWN:
                    return (T) witcherycompanion$itemHandlerDown;
                default:
                    return (T) witcherycompanion$itemHandlerSides;
            }
        }
        return super.getCapability(capability, facing);
    }

    /** This Mixin saves the result of getcontainerItem BEFORE the ItemStack gets shrunk to 0, as in that case
     * the item is set to AIR, and getContainerItem returns ItemStack.EMPTY. This wouldn't work without the
     * 'fixVoidedLavaBucketPreventOverride' mixin. Without that, Witchery would override the result we set here
     * with an empty ItemStack again. **/
    @WrapOperation(method = "update", remap = false, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;shrink(I)V", remap = true))
    private void fixVoidedLavaBucket(ItemStack instance, int i, Operation<Void> original) {
        if (BlockTweaks.witchesOven_fixVoidingLavaBuckets) {
            ItemStack result = instance.getItem().getContainerItem(instance);
            this.items.set(2, result);
        }
        original.call(instance, i);
    }

    /** This Mixin prevents Witchery from overriding the container item with AIR **/
    @WrapOperation(method = "update", remap = false, at = @At(value = "INVOKE", ordinal = 0,
            target = "Lnet/minecraft/util/NonNullList;set(ILjava/lang/Object;)Ljava/lang/Object;", remap = true))
    private Object fixVoidedLavaBucketPreventOverride(NonNullList<?> instance, int i, Object o, Operation<Object> original) {
        if (!BlockTweaks.witchesOven_fixVoidingLavaBuckets) {
            return original.call(instance, i, o);
        }
        return null;
    }

    /** Fixes the furnace sideness by allowing extracting from the sides BOTH outputs and from the bottom side
     * any item that is not fuel (for example Empty Buckets after Lava in Lava Bucket has been consumed) **/
    @Inject(method = "canExtractItem", remap = false, at = @At("HEAD"), cancellable = true)
    private void fixInventorySideness(int slot, ItemStack stack, EnumFacing side, CallbackInfoReturnable<Boolean> cir) {
        if (BlockTweaks.witchesOven_fixInventorySidedness) {
            if (side == EnumFacing.UP) {
                cir.setReturnValue(false);
            } else if (side == EnumFacing.DOWN) {
                cir.setReturnValue(slot == 2 && !TileEntityFurnace.isItemFuel(stack));
            } else {
                cir.setReturnValue(slot == 3 || slot == 4);
            }
        }
    }

    /** W:R does not cache anything: canSmelt() (called every tick while the oven has fuel and an input, twice on the
     * tick in which fuel is consumed) and smeltItem() ask FurnaceRecipes.getSmeltingResult every time. In Forge 1.12.2
     * that is a linear scan over the whole smelting map (thousands of entries in big modpacks), and when the input has
     * no recipe the whole map is scanned for nothing, on every tick. The result of getSmeltingResult only depends on
     * the Item and the metadata of the stack, so we remember the last lookup of this oven (including 'no recipe').
     * Stacks with NBT are never cached, to stay safe with anything that may make the lookup NBT-sensitive. **/
    @WrapOperation(method = {"canSmelt", "smeltItem"}, remap = false, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/item/crafting/FurnaceRecipes;getSmeltingResult(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;", remap = true))
    private ItemStack tweakCacheSmeltingResult(FurnaceRecipes instance, ItemStack input, Operation<ItemStack> original) {
        if (!BlockTweaks.witchesOven_tweakCacheSmeltingResult || this.world == null || input.isEmpty() || input.hasTagCompound()) {
            return original.call(instance, input);
        }

        Item item = input.getItem();
        int meta = input.getMetadata();
        int fingerprint = instance.getSmeltingList().size();
        long now = this.world.getTotalWorldTime();
        if (this.witcherycompanion$smeltingCache.isHit(item, meta, fingerprint, now, witcherycompanion$SMELTING_CACHE_TTL)) {
            return this.witcherycompanion$smeltingCache.get();
        }

        ItemStack result = original.call(instance, input);
        this.witcherycompanion$smeltingCache.store(item, meta, fingerprint, now, result);
        return result;
    }

}
