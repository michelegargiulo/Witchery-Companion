package com.smokeythebandicoot.witcherycompanion.utils;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockPredicateCircle {

    public BlockPredicateCircle() {
    }

    public abstract boolean onBlock(World var1, BlockPos var2);

    // Processes the filled circle
    public boolean processFilledCircle(World world, BlockPos pos, int radius) {
        if (radius == 1) {
            return this.drawPixel(world, pos);
        } else {
            --radius;
            int x = radius;
            int z = 0;
            int radiusError = 1 - x;

            // Loops over the circle in a line-by-line basis
            while(x >= z) {
                if(
                        !drawLine(world, pos.add(-x, 0, z), pos.add(x, 0, z))
                        || !drawLine(world, pos.add(-z, 0, x), pos.add(z, 0, x))
                        || !drawLine(world, pos.add(-x, 0, -z), pos.add(x, 0, -z))
                        || !drawLine(world, pos.add(-z, 0, -x), pos.add(z, 0, -x)))
                    return false;
                ++z;
                if (radiusError < 0) {
                    radiusError += 2 * z + 1;
                } else {
                    --x;
                    radiusError += 2 * (z - x + 1);
                }
            }
            return true;
        }
    }

    // Performs the effect on a line
    private boolean drawLine(World world, BlockPos x1, BlockPos x2) {
        for (BlockPos pos : BlockPos.getAllInBox(x1, x2)) {
            if (!drawPixel(world, pos)) {
                return false;
            }
        }
        return true;
    }

    // Performs the effect on a pixel/block
    private boolean drawPixel(World world, BlockPos pos) {
        return this.onBlock(world, pos);
    }
}
