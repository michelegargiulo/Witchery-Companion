package com.smokeythebandicoot.witcherypatcher.mixins.item;

import com.smokeythebandicoot.witcherypatcher.config.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.item.ItemChalk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 Mixins:
 [Tweak] Set Item Chalk stack size to 1, to prevent accidental stacking. Should also fix a bug
 */
@Mixin(value = ItemChalk.class, remap = false)
public class ItemChalkMixin extends ItemBlock {

    private ItemChalkMixin(Block block) {
        super(block);
    }

    @Inject(method = "getItemStackLimit", at = @At("HEAD"), remap = false, cancellable = true)
    private void WPgetItemStackLimit(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (ModConfig.PatchesConfiguration.ItemTweaks.itemChalk_tweakUnstackableChalk) {
            cir.setReturnValue(1);
        }
    }

}
