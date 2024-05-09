package com.smokeythebandicoot.witcherypatcher.mixins.block.entity;

import com.smokeythebandicoot.witcherypatcher.config.ModConfig;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.block.entity.TileEntityPlacedItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TileEntityPlacedItem.class, remap = false)
public class TileEntityPlacedItemMixin {


    @Shadow
    public ItemStack stack;

    @Inject(method = "getStack", remap = false, cancellable = true, at = @At("HEAD"))
    public void WPdontCrashWhenNotInitialized(CallbackInfoReturnable<ItemStack> cir) {
        if (ModConfig.PatchesConfiguration.BlockTweaks.placedItems_fixNotInitializedCrash) {
            cir.setReturnValue(this.stack);
        }
    }
}
