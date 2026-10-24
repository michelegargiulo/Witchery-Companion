package com.smokeythebandicoot.witcherycompanion.integrations.conarm.modifiers;

import c4.conarm.lib.modifiers.ArmorModifierTrait;
import com.smokeythebandicoot.witcherycompanion.integrations.tinkers.modifiers.SlayerType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import slimeknights.tconstruct.library.modifiers.IToolMod;

/**
 * Silvered, Garliced and Demonrend on Construct Armory armor: they reduce the damage received from a kind of creature,
 * the armor is not damaged by their attacks and, for some of them, attackers are set on fire. The weapon version is
 * in the Tinkers integration. Only one of them can be applied
 **/
public class ModifierSlayer extends ArmorModifierTrait {

    private final SlayerType type;

    public ModifierSlayer(String identifier, int color, int maxLevel, int countPerLevel, SlayerType type) {
        super(identifier, color, maxLevel, countPerLevel);
        this.type = type;
    }

    /** ConArm passes newDamage from piece to piece, so every piece reduces it (the final damage is anyway never lower
     * than 20% of the original) **/
    @Override
    public float onHurt(ItemStack armor, EntityPlayer player, DamageSource source, float damage, float newDamage, LivingHurtEvent evt) {
        Entity attacker = source.getTrueSource();
        if (type.isTarget(attacker)) {
            newDamage *= type.getDamageReduction();
            int fireSeconds = type.getAttackerFireSeconds();
            if (fireSeconds > 0 && !player.world.isRemote) {
                attacker.setFire(fireSeconds);
            }
        }
        return super.onHurt(armor, player, source, damage, newDamage, evt);
    }

    /** Armor is not damaged by attacks from the targets of this modifier **/
    @Override
    public int onArmorDamage(ItemStack armor, DamageSource source, int damage, int newDamage, EntityPlayer player, int slot) {
        if (source != null && type.isTarget(source.getTrueSource())) {
            return 0;
        }
        return super.onArmorDamage(armor, source, damage, newDamage, player, slot);
    }

    /** Some materials burn the creatures they are effective against, when they wear them **/
    @Override
    public void onArmorTick(ItemStack tool, World world, EntityPlayer player) {
        super.onArmorTick(tool, world, player);
        int offset = type.getWearerHarmTickOffset();
        if (offset >= 0 && !player.world.isRemote && player.ticksExisted % 20 == offset && type.isWearerHarmed(player)) {
            player.attackEntityFrom(DamageSource.IN_FIRE, 1.0F);
        }
    }

    @Override
    public boolean canApplyTogether(IToolMod otherModifier) {
        return super.canApplyTogether(otherModifier) &&
                (otherModifier == this || !(otherModifier instanceof ModifierSlayer));
    }
}
