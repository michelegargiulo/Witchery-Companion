package com.smokeythebandicoot.witcherycompanion.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;

public class MutationRegistry {

    public static final HashMap<ResourceLocation, MutationInfo> mutations = new HashMap<>();


    public static class MutationInfo {

        public final String[][] pattern;
        public final HashMap<Character, IBlockState> state;

        public MutationInfo(String[][] pattern, HashMap<Character, IBlockState> state) {
            this.pattern = pattern;
            this.state = state;
        }
    }

}
