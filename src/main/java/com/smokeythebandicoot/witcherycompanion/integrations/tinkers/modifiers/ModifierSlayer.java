package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.modifiers;

import slimeknights.tconstruct.library.modifiers.IToolMod;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.TinkersItem;

/**
 * Silvered, Garliced and Demonrend on Tinkers weapons: they increase the damage dealt to a kind of creature.
 * Only one of them (or Wolfsbane) can be applied. The armor version is in the ConArm integration
 **/
public class ModifierSlayer extends ModifierTrait {

    private final SlayerType type;

    public ModifierSlayer(String identifier, int color, int maxLevel, int countPerLevel, SlayerType type) {
        super(identifier, color, maxLevel, countPerLevel);
        this.type = type;
    }

    public SlayerType getType() {
        return type;
    }

    @Override
    public boolean canApplyCustom(ItemStack stack) {
        return stack.getItem() instanceof TinkersItem && ((TinkersItem) stack.getItem()).hasCategory(Category.WEAPON);
    }

    /** Damage is changed by returning the new damage (onHit cannot alter it) **/
    @Override
    public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
        if (type.isTarget(target)) {
            newDamage *= type.getDamageBoost();
        }
        return super.damage(tool, player, target, damage, newDamage, isCritical);
    }

    @Override
    public boolean canApplyTogether(IToolMod otherModifier) {
        return super.canApplyTogether(otherModifier) &&
                (otherModifier == this || !(otherModifier instanceof ModifierSlayer)) &&
                !(otherModifier instanceof ModifierWolfsbane);
    }
}
