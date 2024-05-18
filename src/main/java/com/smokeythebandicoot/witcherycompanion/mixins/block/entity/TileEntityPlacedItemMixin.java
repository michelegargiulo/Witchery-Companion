package com.smokeythebandicoot.witcherycompanion.mixins.block.entity;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.block.entity.TileEntityPlacedItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 Mixins:
 [Bugfix] Fix rendering crash when Placed Item are within players view within the first frame after world loading
 */
@Mixin(value = TileEntityPlacedItem.class)
public abstract class TileEntityPlacedItemMixin {


    @Shadow(remap = false)
    public ItemStack stack;

    @Inject(method = "getStack", remap = false, cancellable = true, at = @At("HEAD"))
    public void WPdontCrashWhenNotInitialized(CallbackInfoReturnable<ItemStack> cir) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.placedItems_fixNotInitializedCrash) {
            cir.setReturnValue(this.stack);
        }
    }
}
