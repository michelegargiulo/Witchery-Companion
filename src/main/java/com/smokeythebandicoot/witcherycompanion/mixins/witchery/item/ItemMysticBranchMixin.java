package com.smokeythebandicoot.witcherycompanion.mixins.witchery.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.api.PlacedItemApi;
import com.smokeythebandicoot.witcherycompanion.api.progress.ProgressUtils;
import com.smokeythebandicoot.witcherycompanion.api.progress.WitcheryProgressEvent;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.BlockTweaks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.msrandom.witchery.infusion.symbol.SymbolEffect;
import net.msrandom.witchery.item.ItemMysticBranch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixins:
 * [Feature] unlock secret SymbolEffects
 * [Integration] CraftTweaker control over the items that can be placed on top of the Altar (Placed Items). The
 *    Mystic Branch places itself with hardcoded logic: it is skipped when removed from the Placed Items list
 */
@Mixin(ItemMysticBranch.class)
public abstract class ItemMysticBranchMixin extends Item {

    @WrapOperation(method = "onPlayerStoppedUsing", remap = false, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/msrandom/witchery/infusion/symbol/SymbolEffect;perform(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;I)V"))
    private void unlockSecret(SymbolEffect instance, World world, EntityPlayer entityPlayer, int i, Operation<Void> original) {
        // At this point in the function, the effect and the player have already passed null-checks
        int effectId = SymbolEffect.REGISTRY.getId(instance);
        ResourceLocation effectKey = SymbolEffect.REGISTRY.getKey(effectId);

        if (effectKey == null) return;
        ProgressUtils.unlockProgress(entityPlayer, effectKey.path,
                WitcheryProgressEvent.EProgressTriggerActivity.MYSTIC_BRANCH.activityTrigger);

    }

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
