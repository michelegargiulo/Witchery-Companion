package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.modifiers;

import c4.conarm.lib.modifiers.ArmorModifierTrait;
import c4.conarm.lib.tinkering.TinkersArmor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.TinkersItem;
import slimeknights.tconstruct.library.utils.TinkerUtil;

/**
 * Base class for the Silvered, Garliced and Demonrend modifiers: they can be applied both to armor and to weapons.
 * On weapons they boost the damage dealt to a kind of creature, on armor they reduce the damage received from it.
 * Armor is also not damaged by attacks of such creatures, and attackers can be set on fire.
 **/
public abstract class ModifierSlayer extends ArmorModifierTrait {

    public ModifierSlayer(String identifier, int color) {
        super(identifier, color);
    }

    public ModifierSlayer(String identifier, int color, int maxLevel, int countPerLevel) {
        super(identifier, color, maxLevel, countPerLevel);
    }

    /** Returns true if the entity is the kind of creature that this modifier is effective against **/
    protected abstract boolean isTarget(Entity entity);

    /** Multiplier of the damage dealt to targets, when applied to weapons **/
    protected abstract float getDamageBoost();

    /** Multiplier of the damage received from targets, when applied to armor **/
    protected abstract float getDamageReduction();

    /** Seconds for which the attackers of armor wearers are set on fire. 0 or less disables it **/
    protected int getAttackerFireSeconds() {
        return 0;
    }

    /** Armor is handled by ConArm (the superclass) and weapons by TiC (only weapons, only once) **/
    @Override
    public boolean canApplyCustom(ItemStack stack) {
        if (stack.getItem() instanceof TinkersArmor) {
            return super.canApplyCustom(stack);
        }
        if (stack.getItem() instanceof TinkersItem && ((TinkersItem) stack.getItem()).hasCategory(Category.WEAPON)) {
            return !TinkerUtil.hasModifier(stack.getTagCompound(), this.getIdentifier());
        }
        return false;
    }

    /** Weapons: damage is changed by returning the new damage (onHit cannot alter it) **/
    @Override
    public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
        if (isTarget(target)) {
            newDamage *= getDamageBoost();
        }
        return super.damage(tool, player, target, damage, newDamage, isCritical);
    }

    /** Armor: reduces the damage received. ConArm passes newDamage from piece to piece, so every piece reduces it
     * (the final damage is anyway never lower than 20% of the original) **/
    @Override
    public float onHurt(ItemStack armor, EntityPlayer player, DamageSource source, float damage, float newDamage, LivingHurtEvent evt) {
        Entity attacker = source.getTrueSource();
        if (isTarget(attacker)) {
            newDamage *= getDamageReduction();
            int fireSeconds = getAttackerFireSeconds();
            if (fireSeconds > 0 && !player.world.isRemote) {
                attacker.setFire(fireSeconds);
            }
        }
        return super.onHurt(armor, player, source, damage, newDamage, evt);
    }

    /** Armor: it is not damaged by attacks from the targets of this modifier **/
    @Override
    public int onArmorDamage(ItemStack armor, DamageSource source, int damage, int newDamage, EntityPlayer player, int slot) {
        if (source != null && isTarget(source.getTrueSource())) {
            return 0;
        }
        return super.onArmorDamage(armor, source, damage, newDamage, player, slot);
    }
}
