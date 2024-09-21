package com.smokeythebandicoot.witcherycompanion.mixins.mutation;

import com.smokeythebandicoot.witcherycompanion.api.mutations.IMutationPatternAccessor;
import kotlin.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.msrandom.witchery.mutation.BlockReplacement;
import net.msrandom.witchery.mutation.MutationPattern;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.*;


@Mixin(MutationPattern.class)
public abstract class MutationPatternMixin implements IMutationPatternAccessor {

    @Shadow(remap = false) @Final @Mutable
    private Pair<MutationPattern.BlockPredicate, BlockReplacement>[][][] matrix;

    public String[][] stringPattern;
    public Map<Character, IBlockState> charMap;

    @Override
    public IBlockState[][][] getMatrix() {


        int yLength = matrix.length;
        int zLength = matrix[0].length;
        int xLength = matrix[0][0].length;

        stringPattern = new String[yLength][zLength];
        charMap = new HashMap<>();

        for (int y = 0; y < yLength; y++) {
            for (int z = 0; z < zLength; z++) {
                for (int x = 0; x < xLength; x++) {

                    Pair<MutationPattern.BlockPredicate, BlockReplacement> elem = matrix[y][z][x];
                    MutationPattern.BlockPredicate pred = elem.getFirst();

                    if (pred instanceof MutationPattern.MatchingBlockPredicate) {
                        MutationPattern.MatchingBlockPredicate matchingBlockPredicate = (MutationPattern.MatchingBlockPredicate) pred;
                        Block block = matchingBlockPredicate.getBlocks().stream().findFirst().orElse(Blocks.AIR);
                        //Properties prop = matchingBlockPredicate.getProperties().ma
                    }
                }
            }
        }

        for (Pair<MutationPattern.BlockPredicate, BlockReplacement>[][] matrixYZ : matrix) {
            for (Pair<MutationPattern.BlockPredicate, BlockReplacement>[] matrixZ : matrixYZ) {
                for (Pair<MutationPattern.BlockPredicate, BlockReplacement> elem : matrixZ) {

                }
            }
        }

        return null;
    }

    @Override
    public List<Class<? extends EntityLiving>> getEntities() {
        return Collections.emptyList();
    }
}
