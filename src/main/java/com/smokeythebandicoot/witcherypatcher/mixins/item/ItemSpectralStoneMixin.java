package com.smokeythebandicoot.witcherypatcher.mixins.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherypatcher.config.ModConfig;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.item.ItemSpectralStone;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 [Bugfix] Fix Spectral Stones exploit that trapped entities can be released indefinitely
 */
@Mixin(value = ItemSpectralStone.class)
public class ItemSpectralStoneMixin {

    @WrapOperation(method = "onPlayerStoppedUsing", remap = true,
    at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;splitStack(I)Lnet/minecraft/item/ItemStack;", remap = true))
    public ItemStack WPremoveNBTAfterUse(ItemStack instance, int i, Operation<ItemStack> original) {
        // Weird workaround to remove NBT from the item before or after splitting it. Operation is done
        // on the ItemStack, so Inject at INVOKE is not feasible. So just remove NBT from itemstack next to
        // the ItemStack splitting. Original is always called
        ItemStack newStack = original.call(instance, i);
        if (ModConfig.PatchesConfiguration.ItemTweaks.spectralStone_fixEntityReleaseExploit)
            newStack.setTagCompound(null);
        return newStack;
    }

}
