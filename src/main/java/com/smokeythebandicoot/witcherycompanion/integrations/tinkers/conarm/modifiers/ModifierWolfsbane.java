package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.modifiers;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.IntegrationConfigurations.TinkersIntegration.ModifiersConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.util.CreatureUtil;
import slimeknights.tconstruct.library.modifiers.IToolMod;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.tools.SwordCore;



/**
 * New Trait - Deals more damage to werewolves, both villagers and players
 * Does not provide any additional damage bonus to any non-werewolf entities
 * **/
public class ModifierWolfsbane extends ModifierTrait {

    public static final String id = WitcheryCompanion.prefix("wolfsbane");

    public ModifierWolfsbane() {
        super(id, 0xDD20DD);
    }

    @Override
    public boolean canApplyCustom(ItemStack stack) {
        return stack.getItem() instanceof SwordCore; // Can only be applied to swords and similars
    }

    @Override
    public boolean canApplyTogether(IToolMod otherModifier) {
        // if (otherModifier instanceof TraitWitchClothing) return false;
        // if (otherModifier instanceof TraitNecromancerClothing) return false;
        return true;
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
}
