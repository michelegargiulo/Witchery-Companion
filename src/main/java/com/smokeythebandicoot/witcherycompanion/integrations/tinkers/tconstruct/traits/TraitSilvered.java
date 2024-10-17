package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.tconstruct.traits;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.util.CreatureUtil;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;

public class TraitSilvered extends ModifierTrait {

    public TraitSilvered() {
        super("witchery_silvered", 0xCDCDCD, 1, 32);
    }

    @Override
    public void onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, boolean isCritical) {
        if (CreatureUtil.isWerewolf(target)) {
            damage *= ModConfig.IntegrationConfigurations.TinkersIntegration.ModifiersConfig.silvered_werewolfDamageMultiplier;
        }
        super.onHit(tool, player, target, damage, isCritical);
    }
}
