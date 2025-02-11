package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.modifiers;

import c4.conarm.lib.modifiers.ArmorModifierTrait;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.IntegrationConfigurations.TinkersIntegration.ModifiersConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.msrandom.witchery.extensions.PlayerExtendedData;
import net.msrandom.witchery.init.WitcheryCreatureTraits;
import net.msrandom.witchery.util.CreatureUtil;
import net.msrandom.witchery.util.WitcheryUtils;
import slimeknights.tconstruct.library.modifiers.IToolMod;

public class ModifierGarliced extends ArmorModifierTrait {

    public ModifierGarliced() {
        super(WitcheryCompanion.prefix("garliced"), 0xf0efd5);
    }

    @Override
    public float onHurt(ItemStack armor, EntityPlayer player, DamageSource source, float damage, float newDamage, LivingHurtEvent evt) {
        Entity attacker = source.getTrueSource();
        if (CreatureUtil.isVampire(attacker)) {
            damage *= ModifiersConfig.garliced_damageReduction;
        }
        return super.onHurt(armor, player, source, damage, newDamage, evt);
    }

    @Override
    public void onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, boolean isCritical) {
        if (CreatureUtil.isVampire(target)) {
            damage *= ModifiersConfig.garliced_damageBoost;
        }
        super.onHit(tool, player, target, damage, isCritical);
    }

    @Override
    public float onDamaged(ItemStack armor, EntityPlayer player, DamageSource source, float damage, float newDamage, LivingDamageEvent evt) {
        if (source == null || CreatureUtil.isVampire(source.getTrueSource())) {
            return super.onDamaged(armor, player, source, damage, newDamage, evt);
        }
        return 0.0f;
    }

    @Override
    public void onArmorTick(ItemStack tool, World world, EntityPlayer player) {
        super.onArmorTick(tool, world, player);
        if (!player.world.isRemote && player.ticksExisted % 20 == 3) {
            PlayerExtendedData playerEx = WitcheryUtils.getExtension(player);
            if (playerEx.isTransformation(WitcheryCreatureTraits.VAMPIRE)) {
                player.attackEntityFrom(DamageSource.IN_FIRE, 1.0F);
            }
        }
    }

    // Incompatible with Silvered, Demonrend
    @Override
    public boolean canApplyTogether(IToolMod otherModifier) {
        return super.canApplyTogether(otherModifier) &&
                !(otherModifier instanceof ModifierSilvered) &&
                !(otherModifier instanceof ModifierDemonrend) &&
                !(otherModifier instanceof ModifierWolfsbane)
                ;
    }
}
