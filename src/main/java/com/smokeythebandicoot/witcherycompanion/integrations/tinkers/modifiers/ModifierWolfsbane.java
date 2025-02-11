package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.modifiers;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.IntegrationConfigurations.TinkersIntegration.ModifiersConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.util.CreatureUtil;
import slimeknights.tconstruct.library.modifiers.IToolMod;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.tools.SwordCore;

public class ModifierWolfsbane extends ModifierTrait {

    public ModifierWolfsbane() {
        super(WitcheryCompanion.prefix("wolfsbane"), 0xebe62c);
    }

    @Override
    public void onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, boolean isCritical) {
        if (CreatureUtil.isWerewolf(target)) {
            damage *= ModifiersConfig.wolfsbane_werewolfDamageMultiplier;
        } else {
            damage *= ModifiersConfig.wolfsbane_nonWerewolfDamageMultiplier;
        }
        super.onHit(tool, player, target, damage, isCritical);
    }

    @Override
    public boolean canApplyTogether(IToolMod otherModifier) {
        return super.canApplyTogether(otherModifier) &&
                !(otherModifier instanceof ModifierSilvered) &&
                !(otherModifier instanceof ModifierDemonrend) &&
                !(otherModifier instanceof ModifierGarliced)
                ;
    }

    @Override
    public boolean canApplyCustom(ItemStack stack) {
        return stack.getItem() instanceof SwordCore; // Can only be applied to swords and similars
    }
}
