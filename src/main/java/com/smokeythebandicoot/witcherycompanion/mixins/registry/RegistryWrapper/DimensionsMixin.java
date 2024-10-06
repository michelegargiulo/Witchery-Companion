package com.smokeythebandicoot.witcherycompanion.mixins.registry.RegistryWrapper;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.msrandom.witchery.registry.RegistryWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashMap;

/**
 Mixins:
 [Bugfix] Fix Dimension ID configuration not being applied
 */
@Mixin(RegistryWrapper.Dimensions.class)
public abstract class DimensionsMixin {

    @Unique
    private final static HashMap<String, Integer> witchery_Patches$dimensionOverrides = new HashMap<>();

    static {
        witchery_Patches$dimensionOverrides.put("spirit_world", ModConfig.PatchesConfiguration.DimensionTweaks.spiritWorld_tweakID);
        witchery_Patches$dimensionOverrides.put("torment", ModConfig.PatchesConfiguration.DimensionTweaks.torment_tweakID);
        witchery_Patches$dimensionOverrides.put("mirror", ModConfig.PatchesConfiguration.DimensionTweaks.mirror_tweakID);
    }

    @SuppressWarnings("UnnecessaryUnboxing")
    @WrapOperation(method = "register$WitcheryResurrected", remap = false, at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/DimensionManager;registerDimension(ILnet/minecraft/world/DimensionType;)V", remap = false))
    public void redirectDimensionManagerRegister(int id, DimensionType dimensionType, Operation<Void> original) {
        original.call(witchery_Patches$dimensionOverrides.getOrDefault(dimensionType.getName(), id).intValue(), dimensionType);
    }

    @SuppressWarnings("UnnecessaryUnboxing")
    @WrapOperation(method = "register$WitcheryResurrected", remap = false, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/minecraft/world/DimensionType;register(Ljava/lang/String;Ljava/lang/String;ILjava/lang/Class;Z)Lnet/minecraft/world/DimensionType;"))
    public DimensionType redirectDimensionTypeRegister(String name, String suffix, int id, Class<? extends WorldProvider> provider, boolean keepLoaded, Operation<DimensionType> original) {
        return original.call(name, suffix, witchery_Patches$dimensionOverrides.getOrDefault(name, id).intValue(), provider, keepLoaded);
    }



}
