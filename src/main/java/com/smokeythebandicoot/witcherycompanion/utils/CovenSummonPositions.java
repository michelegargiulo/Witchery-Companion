package com.smokeythebandicoot.witcherycompanion.utils;

import net.minecraft.util.math.BlockPos;

/**
 * Extends the position and facing arrays that Witchery: Resurrected uses to place the Coven Witches of a player around
 * a circle (6 entries each) up to {@link CovenCircleLayout#MAX_SLOTS}, so that bigger covens do not overflow them.
 * The first entries are always the ones of Witchery. Arrays are built once, as they do not depend on configs
 */
public final class CovenSummonPositions {

    private static BlockPos[] basePositions;
    private static BlockPos[] positions;

    private static float[] baseFacings;
    private static float[] facings;

    private CovenSummonPositions() {}

    public static synchronized BlockPos[] extendPositions(BlockPos[] original) {
        if (original.length >= CovenCircleLayout.MAX_SLOTS) return original;
        if (positions == null || basePositions != original) {
            BlockPos[] extended = new BlockPos[CovenCircleLayout.MAX_SLOTS];
            System.arraycopy(original, 0, extended, 0, original.length);
            for (int i = original.length; i < extended.length; i++) {
                extended[i] = new BlockPos(CovenCircleLayout.offsetX(i), 0, CovenCircleLayout.offsetZ(i));
            }
            basePositions = original;
            positions = extended;
        }
        return positions;
    }

    public static synchronized float[] extendFacings(float[] original) {
        if (original.length >= CovenCircleLayout.MAX_SLOTS) return original;
        if (facings == null || baseFacings != original) {
            float[] extended = new float[CovenCircleLayout.MAX_SLOTS];
            System.arraycopy(original, 0, extended, 0, original.length);
            for (int i = original.length; i < extended.length; i++) {
                extended[i] = CovenCircleLayout.yaw(i);
            }
            baseFacings = original;
            facings = extended;
        }
        return facings;
    }

}
