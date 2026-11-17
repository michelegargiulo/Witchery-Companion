package com.smokeythebandicoot.witcherycompanion.mixins.witchery.item;

import com.smokeythebandicoot.witcherycompanion.api.PlacedItemApi;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.BlockTweaks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.msrandom.witchery.item.ItemArthana;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 Mixins:
 [Bugfix] Placing the Arthana on the Altar no longer opens the Altar GUI too (the client returned PASS, so the right
    click was repeated with the empty off hand)
 [Integration] CraftTweaker control over the items that can be placed on top of the Altar (Placed Items). This item
    places itself with hardcoded logic: it is skipped when the item has been removed from the Placed Items list
 */
@Mixin(ItemArthana.class)
public abstract class ItemArthanaMixin {

    /** Witchery's logic is: if the player aims at the top of an Altar with air above, place the item, otherwise use
     * the default item logic, which does nothing (PASS). When the item is not placeable anymore, the first branch is skipped.
     * When it is placeable, on the client Witchery returns PASS after placing: this lets the right click go on with the
     * other hand, and an empty hand opens the Altar GUI. The client returns SUCCESS instead */
    @Inject(method = "onItemUse", remap = true, cancellable = true, at = @At("HEAD"))
    private void witcherycompanion$onItemUseHead(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing,
                                                 float hitX, float hitY, float hitZ, CallbackInfoReturnable<EnumActionResult> cir) {
        if (BlockTweaks.placedItems_tweakEnableCrafttweakerCompat &&
                PlacedItemApi.shouldBlockNativePlacement(player, world, pos, hand, facing)) {
            cir.setReturnValue(EnumActionResult.PASS);
            return;
        }

        if (BlockTweaks.placedItems_fixOpensAltarGui) {
            EnumActionResult result = PlacedItemApi.placeNativeOnClient(player, world, pos, hand, facing);
            if (result != null) {
                cir.setReturnValue(result);
            }
        }
    }

}
