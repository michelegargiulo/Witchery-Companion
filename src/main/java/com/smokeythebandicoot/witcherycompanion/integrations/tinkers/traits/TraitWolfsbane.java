package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.traits;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig.IntegrationConfigurations.TinkersIntegration.TinkersConstructIntegration.ModifiersConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.util.CreatureUtil;
import slimeknights.tconstruct.library.modifiers.IToolMod;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.tools.SwordCore;

public class TraitWolfsbane extends ModifierTrait {

    public TraitWolfsbane() {
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
                !(otherModifier instanceof TraitSilvered)
                ;
    }

    @Override
    public boolean canApplyCustom(ItemStack stack) {
        return stack.getItem() instanceof SwordCore; // Can only be applied to swords and similars
    }
}
