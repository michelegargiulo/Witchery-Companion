package com.smokeythebandicoot.witcherycompanion.integrations.patchouli;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.api.mutations.MutationRegistry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.Map;

public class MultiblockRegistry {

    private static boolean multiblocks_updated = false;

    public static void reloadMultiblocks() {
        for (Map.Entry<ResourceLocation, MutationRegistry.MutationInfo> entry : MutationRegistry.mutations.entrySet()) {

            ResourceLocation mutation = entry.getKey();
            MutationRegistry.MutationInfo info = entry.getValue();

            // Copy pattern to avoid modifying the registry
            String[][] infoPattern = cloneMatrix(info.pattern);

            // Find center of the multiblock
            String[] bottomLayer = infoPattern[0];
            int midLength = bottomLayer.length / 2;
            String middleOfLayer = bottomLayer[midLength];
            char middleChar = middleOfLayer.charAt(middleOfLayer.length() / 2);

            // Replace center char with '0'
            infoPattern[0][bottomLayer.length / 2] =
                    middleOfLayer.substring(0, midLength) + '0' +
                            middleOfLayer.substring(midLength + 1);

            Object[] charMap = convertForPatchouli(info.state, middleChar);
            PatchouliAPI.instance.registerMultiblock(
                    mutation, PatchouliAPI.instance.makeMultiblock(infoPattern, charMap));
        }
        if (!multiblocks_updated && !MutationRegistry.mutations.isEmpty()) {
            multiblocks_updated = true;
            PatchouliApiIntegration.updateFlag("multiblocks_ready", true);
        }
    }

    private static Object[] convertForPatchouli(Map<Character, IBlockState> map, char middleChar) {
        if (map == null) return new Object[0];
        // Init array with double the size of the map (all keys + all values) + 2 (for any match) + 2 (for center)
        Object[] objects = new Object[map.size() * 2 + 4];
        // Add any matcher
        objects[0] = ' ';
        objects[1] = PatchouliAPI.instance.anyMatcher();
        objects[2] = '0';
        objects[3] = map.containsKey(middleChar) ? map.get(middleChar) : PatchouliAPI.instance.anyMatcher();
        // Start index from 2 and add two elements (key, value) at each iteration
        int index = 4;
        for (Map.Entry<Character, IBlockState> entry : map.entrySet()) {
            objects[index] = entry.getKey();
            objects[index + 1] = entry.getValue();
            index += 2;
        }
        return objects;
    }

    private static String[][] cloneMatrix(String[][] src) {
        int iLen = src.length;
        int jLen = src[0].length;

        String[][] dst = new String[iLen][jLen];
        for (int i = 0; i < iLen; i++) {
            for (int j = 0; j < jLen; j++) {
                dst[i][j] = new String(src[i][j]);
            }
        }
        return dst;
    }

}
