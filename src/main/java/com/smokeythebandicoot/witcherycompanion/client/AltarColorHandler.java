package com.smokeythebandicoot.witcherycompanion.client;

import com.smokeythebandicoot.witcherycompanion.api.AltarDyeApi;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.msrandom.witchery.block.BlockAltar;
import net.msrandom.witchery.init.WitcheryBlocks;

import javax.annotation.Nullable;

/**
 * Tints the cloth of the Altar with the color of its dye. Client side only.
 * <p>
 * The cloth faces of the Altar models (witchery:block/altar, altar_connected_lefttop, altar_connected_leftbot, overridden
 * by W:C) have tintindex 0 and a neutral texture, so that this multiplies it by the color. An Altar that is not dyed
 * keeps the colors it has in Witchery: white when it is a single block, red (the color of red wool) when it is
 * connected. The color does not depend on any config: it is whatever the tile entity says
 */
public class AltarColorHandler implements IBlockColor {

    /** Color of the cloth of a single (not connected) Altar in Witchery: white wool */
    public static final int DEFAULT_SINGLE = 0xFFFFFF;

    /** Color of the cloth of a connected Altar in Witchery: red wool */
    public static final int DEFAULT_CONNECTED = EnumDyeColor.RED.getColorValue();

    @SubscribeEvent
    public static void onBlockColors(ColorHandlerEvent.Block event) {
        event.getBlockColors().registerBlockColorHandler(new AltarColorHandler(), WitcheryBlocks.ALTAR);
    }

    @Override
    public int colorMultiplier(IBlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos, int tintIndex) {
        // Only the cloth is tinted. No world: item model and particles, which have no dye
        if (tintIndex != 0 || world == null || pos == null) return -1;

        EnumDyeColor dye = AltarDyeApi.getDye(world, pos);
        if (dye != null) return dye.getColorValue();

        return isSingle(state) ? DEFAULT_SINGLE : DEFAULT_CONNECTED;
    }

    /** True if the state (the actual state, which the renderer passes) is a block that is not connected to an Altar */
    private static boolean isSingle(IBlockState state) {
        for (IProperty<?> property : state.getPropertyKeys()) {
            if ("part".equals(property.getName())) {
                return state.getValue(property) == BlockAltar.Part.NOT_CONNECTED;
            }
        }
        return false;
    }

}
