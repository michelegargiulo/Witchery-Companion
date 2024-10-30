package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.tconstruct.traits;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig.IntegrationConfigurations.TinkersIntegration.TinkersConstructIntegration.ModifiersConfig;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.modifiers.ModifierArmorDemonrend;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.modifiers.ModifierArmorGarliced;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.util.CreatureUtil;
import slimeknights.tconstruct.library.modifiers.IToolMod;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;

public class TraitToolWolfsbane extends ModifierTrait {

    public TraitToolWolfsbane() {
        super("witchery_silvered", 0xCDCDCD, 1, 32);
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

    // Incompatible with Silvered
    @Override
    public boolean canApplyTogether(IToolMod otherModifier) {
        return super.canApplyTogether(otherModifier) &&
                !(otherModifier instanceof TraitToolSilvered)
                ;
    }
}
