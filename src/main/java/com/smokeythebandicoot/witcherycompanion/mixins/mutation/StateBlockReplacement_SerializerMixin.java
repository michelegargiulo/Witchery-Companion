package com.smokeythebandicoot.witcherycompanion.mixins.mutation;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.block.state.IBlockState;
import net.msrandom.witchery.mutation.StateBlockReplacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashMap;

@Mixin(StateBlockReplacement.Serializer.class)
public class StateBlockReplacement_SerializerMixin {

    @Unique
    private final static HashMap<String, String> witchery_Patcher$invalidToValidRefMap = witchery_Patcher$initInvalidRefMap();

    @Unique
    private static HashMap<String, String> witchery_Patcher$initInvalidRefMap() {
        HashMap<String, String> map = new HashMap<>();

        if (ModConfig.PatchesConfiguration.MutationTweaks.mindrake_fixTransformToAir)
            map.put("witchery:mindrake_seeds", "witchery:mindrake_bulb");

        return map;
    }

    @WrapOperation(method = "read(Lcom/google/gson/JsonObject;)Lnet/msrandom/witchery/mutation/StateBlockReplacement;",
            remap = false, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/msrandom/witchery/util/WitcheryUtils;parseBlockState(Ljava/lang/String;)Lnet/minecraft/block/state/IBlockState;"))
    private IBlockState fixWrongBlockNames(String registryName, Operation<IBlockState> original) {
        if (witchery_Patcher$invalidToValidRefMap.containsKey(registryName)) {
            registryName = witchery_Patcher$invalidToValidRefMap.get(registryName);
        }
        return original.call(registryName);
    }

}
