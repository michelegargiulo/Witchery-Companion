package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.modifiers;

import c4.conarm.lib.modifiers.ArmorModifierTrait;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.msrandom.witchery.util.CreatureUtil;
import slimeknights.tconstruct.library.modifiers.IToolMod;

public class ModifierArmorDemonrend extends ArmorModifierTrait {

    public ModifierArmorDemonrend() {
        super(WitcheryCompanion.prefix("demonrend"), 0xdb2100, 1, 1);
    }

    @Override
    public float onHurt(ItemStack armor, EntityPlayer player, DamageSource source, float damage, float newDamage, LivingHurtEvent evt) {

        Entity attacker = source.getTrueSource();
        if (CreatureUtil.isDemonic(attacker)) {
            damage *= 0.5f;
        }
        return super.onHurt(armor, player, source, damage, newDamage, evt);
    }

    // Incompatible with Garliced, Silvered
    @Override
    public boolean canApplyTogether(IToolMod otherModifier) {
        return super.canApplyTogether(otherModifier) &&
                !(otherModifier instanceof ModifierArmorGarliced) &&
                !(otherModifier instanceof ModifierArmorSilvered)
                ;
    }


}
