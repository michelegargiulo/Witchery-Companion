package com.smokeythebandicoot.witcherycompanion.mixins.witchery.rite.sacrifice;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.rite.sacrifice.CopyDataSacrificedItemReturn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixins:
 * [Bugfix] The "witchery:copy_data" item return did not copy the NBT of the sacrificed item. W:R builds the returned item
 *      with new ItemStack(item, 1, data, stack.getTagCompound()), whose last argument is the capability NBT of the stack,
 *      not its tag. The returned item now receives a copy of the tag of the sacrificed item
 */
@Mixin(CopyDataSacrificedItemReturn.class)
public abstract class CopyDataSacrificedItemReturnMixin {

    @Inject(method = "handleItem", remap = false, at = @At("RETURN"))
    private void witcherycompanion$copyTag(ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        if (!ModConfig.PatchesConfiguration.RitesTweaks.copyDataReturn_fixCopyNbt) return;

        ItemStack result = cir.getReturnValue();
        if (result != null && stack.hasTagCompound()) {
            result.setTagCompound(stack.getTagCompound().copy());
        }
    }

}
