package com.smokeythebandicoot.witcherycompanion.mixins.witchery.item;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.msrandom.witchery.init.WitcheryDimensions;
import net.msrandom.witchery.item.ItemIcyNeedle;
import net.msrandom.witchery.util.WitcheryUtils;
import net.msrandom.witchery.world.dimension.spirit.WorldProviderSpiritWorld;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Mixins:
 * [Bugfix] Fix Icy Needle doing nothing if right-clicked in the air
 */
@ParametersAreNonnullByDefault
@Mixin(ItemIcyNeedle.class)
public abstract class ItemIcyNeedleMixin extends Item {

    /** This Mixin adds the missing onItemRightClick method that gets called when the item is used in the air and replicates
     * the same behaviour of when the item is used on a block **/
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {

        if (!ModConfig.PatchesConfiguration.ItemTweaks.icyNeedle_fixRightClickInAir) {
            return super.onItemRightClick(world, player, hand);
        }

        if (!player.capabilities.isCreativeMode) {
            player.getHeldItem(hand).shrink(1);
        }

        if (WitcheryDimensions.SPIRIT_WORLD.isCurrentDimension(world)) {
            WorldProviderSpiritWorld.returnPlayerToOverworld(player);
        } else if (WitcheryUtils.getExtension(player).getSpiritData().isGhost()) {
            WorldProviderSpiritWorld.returnGhostPlayerToSpiritWorld(player);
        } else {
            player.attackEntityFrom(DamageSource.causePlayerDamage(player), 1.0F);
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

}
