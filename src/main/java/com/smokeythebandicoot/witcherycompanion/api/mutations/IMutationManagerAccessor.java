package com.smokeythebandicoot.witcherycompanion.api.mutations;

import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.mutation.MutationPattern;

import javax.annotation.Nullable;

public interface IMutationManagerAccessor {

    @Nullable MutationPattern getMutation(ResourceLocation id);

}
