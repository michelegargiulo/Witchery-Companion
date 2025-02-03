package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.traits;

import c4.conarm.lib.modifiers.ArmorModifierTrait;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.msrandom.witchery.util.CreatureUtil;
import slimeknights.tconstruct.library.modifiers.IToolMod;

public class TraitSilvered extends ArmorModifierTrait {

    public TraitSilvered() {
        super(WitcheryCompanion.prefix("silvered"), 0xe1e1e1, 1, 8);
    }

    @Override
    public float onHurt(ItemStack armor, EntityPlayer player, DamageSource source, float damage, float newDamage, LivingHurtEvent evt) {

        Entity attacker = source.getTrueSource();
        if (CreatureUtil.isWerewolf(attacker)) {
            damage *= 0.5f;
        }
        return super.onHurt(armor, player, source, damage, newDamage, evt);
    }

    @Override
    public void onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, boolean isCritical) {
        if (CreatureUtil.isWerewolf(target)) {
            damage *= ModConfig.IntegrationConfigurations.TinkersIntegration.TinkersConstructIntegration.ModifiersConfig.silvered_werewolfDamageMultiplier;
        }
        super.onHit(tool, player, target, damage, isCritical);
    }

    // Incompatible with Silvered, Demonrend
    @Override
    public boolean canApplyTogether(IToolMod otherModifier) {
        return super.canApplyTogether(otherModifier) &&
                !(otherModifier instanceof TraitGarliced) &&
                !(otherModifier instanceof TraitDemonrend) &&
                !(otherModifier instanceof TraitWolfsbane)
                ;
    }
}
