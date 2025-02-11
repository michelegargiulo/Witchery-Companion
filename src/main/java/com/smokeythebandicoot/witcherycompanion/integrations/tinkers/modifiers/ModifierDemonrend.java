package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.modifiers;

import c4.conarm.lib.modifiers.ArmorModifierTrait;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.IntegrationConfigurations.TinkersIntegration.ModifiersConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.msrandom.witchery.util.CreatureUtil;
import slimeknights.tconstruct.library.modifiers.IToolMod;

public class ModifierDemonrend extends ArmorModifierTrait {

    public ModifierDemonrend() {
        super(WitcheryCompanion.prefix("demonrend"), 0xdb2100, 1, 1);
    }

    @Override
    public float onHurt(ItemStack armor, EntityPlayer player, DamageSource source, float damage, float newDamage, LivingHurtEvent evt) {
        Entity attacker = source.getTrueSource();
        if (CreatureUtil.isDemonic(attacker)) {
            damage *= ModifiersConfig.demonrend_damageReduction;
        }
        return super.onHurt(armor, player, source, damage, newDamage, evt);
    }

    @Override
    public float onDamaged(ItemStack armor, EntityPlayer player, DamageSource source, float damage, float newDamage, LivingDamageEvent evt) {
        if (source == null || CreatureUtil.isDemonic(source.getTrueSource())) {
            return super.onDamaged(armor, player, source, damage, newDamage, evt);
        }
        return 0.0f;
    }

    @Override
    public void onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, boolean isCritical) {
        if (CreatureUtil.isDemonic(target)) {
            damage *= ModifiersConfig.demonrend_damageBoost;
        }
        super.onHit(tool, player, target, damage, isCritical);
    }

    // Incompatible with Garliced, Silvered
    @Override
    public boolean canApplyTogether(IToolMod otherModifier) {
        return super.canApplyTogether(otherModifier) &&
                !(otherModifier instanceof ModifierGarliced) &&
                !(otherModifier instanceof ModifierSilvered) &&
                !(otherModifier instanceof ModifierWolfsbane)
                ;
    }


}
