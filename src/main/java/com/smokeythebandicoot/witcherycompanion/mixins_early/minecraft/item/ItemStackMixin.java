package com.smokeythebandicoot.witcherycompanion.mixins_early.minecraft.item;

import com.smokeythebandicoot.witcherycompanion.api.PlacedItemApi;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.BlockTweaks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 Mixins:
 [Integration] CraftTweaker control over the items that can be placed on top of the Altar (Placed Items). Items that
    are not Witchery's own (Arthana, Mystic Branch, Kobolite Pentacle) have no code to place themselves, so they are
    placed from here
 */
@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "onItemUse", remap = true, cancellable = true, at = @At("HEAD"))
    private void witcherycompanion$placeOnAltar(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side,
                                                float hitX, float hitY, float hitZ, CallbackInfoReturnable<EnumActionResult> cir) {
        if (!BlockTweaks.placedItems_tweakEnableCrafttweakerCompat) {
            return;
        }

        EnumActionResult result = PlacedItemApi.tryPlaceOnAltar((ItemStack) (Object) this, player, world, pos, hand, side);
        if (result != null) {
            cir.setReturnValue(result);
        }
    }

}
