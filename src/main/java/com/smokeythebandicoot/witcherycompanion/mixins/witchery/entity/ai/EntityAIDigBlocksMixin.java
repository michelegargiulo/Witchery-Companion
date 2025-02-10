package com.smokeythebandicoot.witcherycompanion.mixins.witchery.entity.ai;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.msrandom.witchery.entity.ai.EntityAIDigBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Mixins:
 * [Tweak] Koboldite chance configuration
 * [Tweak] Ore smelting chance configuration
 */
@Mixin(EntityAIDigBlocks.class)
public abstract class EntityAIDigBlocksMixin {

    @ModifyConstant(method = "onHarvestDrops", remap = false, constant = @Constant(doubleValue = 0.01))
    private static double kobolditeInitialChanceNoPick(double constant) {
        return ModConfig.PatchesConfiguration.EntityTweaks.goblin_tweakKobolditeInitialChanceNoPick;
    }

    @ModifyConstant(method = "onHarvestDrops", remap = false, constant = @Constant(doubleValue = 0.02))
    private static double kobolditeInitialChancePick(double constant) {
        return ModConfig.PatchesConfiguration.EntityTweaks.goblin_tweakKobolditeInitialChancePick;
    }

    @ModifyConstant(method = "onHarvestDrops", remap = false, constant = @Constant(doubleValue = 0.05))
    private static double kobolditeAdditionalChanceNoPick(double constant) {
        return ModConfig.PatchesConfiguration.EntityTweaks.goblin_tweakKobolditeAdditionalChanceNoPick;
    }

    @ModifyConstant(method = "onHarvestDrops", remap = false, constant = @Constant(doubleValue = 0.08))
    private static double kobolditeAdditionalChancePick(double constant) {
        return ModConfig.PatchesConfiguration.EntityTweaks.goblin_tweakKobolditeAdditionalChancePick;
    }

    @ModifyConstant(method = "onHarvestDrops", remap = false, constant = @Constant(doubleValue = 0.5))
    private static double smeltingInitialChance(double constant) {
        return ModConfig.PatchesConfiguration.EntityTweaks.goblin_tweakSmeltingInitialChance;
    }

    @ModifyConstant(method = "onHarvestDrops", remap = false, constant = @Constant(doubleValue = 0.25))
    private static double smeltingAdditionalChance(double constant) {
        return ModConfig.PatchesConfiguration.EntityTweaks.goblin_tweakSmeltingAdditionalChance;
    }

}
a