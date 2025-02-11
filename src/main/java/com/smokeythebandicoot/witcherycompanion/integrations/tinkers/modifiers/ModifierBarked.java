package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.modifiers;

import c4.conarm.common.items.armor.Leggings;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.IntegrationConfigurations.TinkersIntegration.ModifiersConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.msrandom.witchery.util.CreatureUtil;
import slimeknights.tconstruct.library.modifiers.IToolMod;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.tools.SwordCore;

public class ModifierBarked extends ModifierTrait {

    public ModifierBarked() {
        super(WitcheryCompanion.prefix("barked"), 0xa86666);
    }

    @Override
    public boolean canApplyCustom(ItemStack stack) {
        return stack.getItem() instanceof Leggings;
    }
}
