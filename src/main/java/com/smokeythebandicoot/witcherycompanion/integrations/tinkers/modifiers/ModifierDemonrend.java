package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.modifiers;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.IntegrationConfigurations.TinkersIntegration.ModifiersConfig;
import net.minecraft.entity.Entity;
import net.msrandom.witchery.util.CreatureUtil;
import slimeknights.tconstruct.library.modifiers.IToolMod;

public class ModifierDemonrend extends ModifierSlayer {

    public ModifierDemonrend() {
        super(WitcheryCompanion.prefix("demonrend"), 0xdb2100, 1, 1);
    }

    @Override
    protected boolean isTarget(Entity entity) {
        return CreatureUtil.isDemonic(entity);
    }

    @Override
    protected float getDamageBoost() {
        return ModifiersConfig.demonrend_damageBoost;
    }

    @Override
    protected float getDamageReduction() {
        return ModifiersConfig.demonrend_damageReduction;
    }

    // Incompatible with Garliced, Silvered and Wolfsbane
    @Override
    public boolean canApplyTogether(IToolMod otherModifier) {
        return super.canApplyTogether(otherModifier) &&
                !(otherModifier instanceof ModifierGarliced) &&
                !(otherModifier instanceof ModifierSilvered) &&
                !(otherModifier instanceof ModifierWolfsbane)
                ;
    }

}
