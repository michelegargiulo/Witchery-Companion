package com.smokeythebandicoot.witcherycompanion.patches.block;

import com.smokeythebandicoot.witcherycompanion.api.AltarDyeApi;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.PatchesConfiguration.BlockTweaks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.DyeUtils;

import java.util.Optional;

/**
 * Dyeable Altar: shift + right click on an Altar with a dye in hand (vanilla dyes and everything registered as
 * "dyeXxx" in the ore dictionary) dyes the cloth of the whole Altar and uses one dye (not in creative).
 * <p>
 * The right click is handled with the Forge event instead of the Altar or the dye item, because with shift pressed the
 * game never asks the block to activate and the dye items would just do nothing. The event is cancelled on both sides,
 * so that the client swings the arm and does not go on to the other hand; only the server dyes the Altar
 */
public class AltarDyePatch {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!BlockTweaks.altar_tweakDyeableCloth) return;

        EntityPlayer player = event.getEntityPlayer();
        if (!player.isSneaking()) return;

        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) return;

        World world = event.getWorld();
        BlockPos pos = event.getPos();
        if (!AltarDyeApi.isAltar(world, pos)) return;

        Optional<EnumDyeColor> color = DyeUtils.colorFromStack(stack);
        if (!color.isPresent()) return;

        event.setCanceled(true);
        event.setCancellationResult(EnumActionResult.SUCCESS);

        if (world.isRemote) return;

        // Nothing is used if the Altar already has this color
        if (AltarDyeApi.dyeAltar(world, pos, color.get()) && !player.capabilities.isCreativeMode) {
            stack.shrink(1);
        }
    }

}
