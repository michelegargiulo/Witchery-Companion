package com.smokeythebandicoot.witcherycompanion.mixins.witchery.item;

import com.smokeythebandicoot.witcherycompanion.api.PlacedItemApi;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.BlockTweaks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.msrandom.witchery.item.ItemKobolditePentacle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 Mixins:
 [Integration] CraftTweaker control over the items that can be placed on top of the Altar (Placed Items). This item
    places itself with hardcoded logic: it is skipped when the item has been removed from the Placed Items list
 */
@Mixin(ItemKobolditePentacle.class)
public abstract class ItemKobolditePentacleMixin {

    /** Witchery's logic is: if the player aims at the top of an Altar with air above, place the item, otherwise use
     * the default item logic, which does nothing (PASS). When the item is not placeable anymore, the first branch is skipped */
    @Inject(method = "onItemUse", remap = true, cancellable = true, at = @At("HEAD"))
    private void witcherycompanion$skipPlacementIfNotAllowed(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing,
                                                             float hitX, float hitY, float hitZ, CallbackInfoReturnable<EnumActionResult> cir) {
        if (BlockTweaks.placedItems_tweakEnableCrafttweakerCompat &&
                PlacedItemApi.shouldBlockNativePlacement(player, world, pos, hand, facing)) {
            cir.setReturnValue(EnumActionResult.PASS);
        }
    }

}
