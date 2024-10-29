package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.tconstruct.traits;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.modifiers.ModifierArmorDemonrend;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.modifiers.ModifierArmorGarliced;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.util.CreatureUtil;
import slimeknights.tconstruct.library.modifiers.IToolMod;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;

public class TraitToolSilvered extends ModifierTrait {

    public TraitToolSilvered() {
        super("witchery_silvered", 0xCDCDCD, 1, 32);
    }

    @Override
    public void onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, boolean isCritical) {
        if (CreatureUtil.isWerewolf(target)) {
            damage *= ModConfig.IntegrationConfigurations.TinkersIntegration.ModifiersConfig.silvered_werewolfDamageMultiplier;
        }
        super.onHit(tool, player, target, damage, isCritical);
    }

    // Incompatible with Garliced, Demonrend
    @Override
    public boolean canApplyTogether(IToolMod otherModifier) {
        return super.canApplyTogether(otherModifier) &&
                !(otherModifier instanceof ModifierArmorGarliced) &&
                !(otherModifier instanceof ModifierArmorDemonrend)
                ;
    }
}
