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

public class ModifierArmorSilvered extends ArmorModifierTrait {

    public ModifierArmorSilvered() {
        super(WitcheryCompanion.prefix("silvered"), 0xe1e1e1);
    }

    @Override
    public float onHurt(ItemStack armor, EntityPlayer player, DamageSource source, float damage, float newDamage, LivingHurtEvent evt) {

        Entity attacker = source.getTrueSource();
        if (CreatureUtil.isWerewolf(attacker)) {
            damage *= 0.5f;
        }
        return super.onHurt(armor, player, source, damage, newDamage, evt);
    }

    // Incompatible with Silvered, Demonrend
    @Override
    public boolean canApplyTogether(IToolMod otherModifier) {
        return super.canApplyTogether(otherModifier) &&
                !(otherModifier instanceof ModifierArmorGarliced) &&
                !(otherModifier instanceof ModifierArmorDemonrend)
                ;
    }
}
