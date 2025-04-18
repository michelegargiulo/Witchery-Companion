package com.smokeythebandicoot.witcherycompanion.mixins.witchery.block.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.msrandom.witchery.block.entity.TileEntityWitchesOven;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Mixins:
 * [Feature] Implement IItemHandler capability
 * [Bugfix] Fix burnable container items (for example, lava buckets) being voided
 */
@Mixin(TileEntityWitchesOven.class)
public abstract class TileEntityWitchesOvenMixin extends TileEntity implements ICapabilityProvider {

    @Shadow(remap = false)
    private NonNullList<ItemStack> items;

    /** This Mixin saves the result of getcontainerItem BEFORE the ItemStack gets shrunk to 0, as in that case
     * the item is set to AIR, and getContainerItem returns ItemStack.EMPTY. This wouldn't work without the
     * 'fixVoidedLavaBucketPreventOverride' mixin. Without that, Witchery would override the result we set here
     * with ItemStack.EMPTY again. **/
    @WrapOperation(method = "update", remap = false, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;shrink(I)V", remap = true))
    private void fixVoidedLavaBucket(ItemStack instance, int i, Operation<Void> original) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.witchesOven_fixVoidingLavaBuckets) {
            ItemStack result = instance.getItem().getContainerItem(instance);
            this.items.set(2, result);
        }
        original.call(instance, i);
    }

    /** This Mixin prevents Witchery from overriding the container item with AIR **/
    @WrapOperation(method = "update", remap = false, at = @At(value = "INVOKE", ordinal = 0,
            target = "Lnet/minecraft/util/NonNullList;set(ILjava/lang/Object;)Ljava/lang/Object;", remap = true))
    private Object fixVoidedLavaBucketPreventOverride(NonNullList instance, int i, Object o, Operation<Object> original) {
        if (!ModConfig.PatchesConfiguration.BlockTweaks.witchesOven_fixVoidingLavaBuckets) {
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

}
