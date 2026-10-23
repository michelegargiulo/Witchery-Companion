package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.modifiers;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.IntegrationConfigurations.TinkersIntegration.ModifiersConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.msrandom.witchery.extensions.PlayerExtendedData;
import net.msrandom.witchery.init.WitcheryCreatureTraits;
import net.msrandom.witchery.util.CreatureUtil;
import net.msrandom.witchery.util.WitcheryUtils;
import slimeknights.tconstruct.library.modifiers.IToolMod;

public class ModifierGarliced extends ModifierSlayer {

    public ModifierGarliced() {
        super(WitcheryCompanion.prefix("garliced"), 0xf0efd5);
    }

    @Override
    protected boolean isTarget(Entity entity) {
        return CreatureUtil.isVampire(entity);
    }

    @Override
    protected float getDamageBoost() {
        return ModifiersConfig.garliced_damageBoost;
    }

    @Override
    protected float getDamageReduction() {
        return ModifiersConfig.garliced_damageReduction;
    }

    @Override
    protected int getAttackerFireSeconds() {
        return ModifiersConfig.garliced_attackerFireSeconds;
    }

    /** Garlic burns vampires wearing it **/
    @Override
    public void onArmorTick(ItemStack tool, World world, EntityPlayer player) {
        super.onArmorTick(tool, world, player);
        if (!player.world.isRemote && player.ticksExisted % 20 == 3) {
            PlayerExtendedData playerEx = WitcheryUtils.getExtension(player);
            if (playerEx.isTransformation(WitcheryCreatureTraits.VAMPIRE)) {
                player.attackEntityFrom(net.minecraft.util.DamageSource.IN_FIRE, 1.0F);
            }
        }
    }

    // Incompatible with Silvered, Demonrend and Wolfsbane
    @Override
    public boolean canApplyTogether(IToolMod otherModifier) {
        return super.canApplyTogether(otherModifier) &&
                !(otherModifier instanceof ModifierSilvered) &&
                !(otherModifier instanceof ModifierDemonrend) &&
                !(otherModifier instanceof ModifierWolfsbane)
                ;
    }

}
