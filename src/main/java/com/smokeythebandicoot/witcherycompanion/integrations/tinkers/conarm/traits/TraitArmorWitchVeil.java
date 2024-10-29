package com.smokeythebandicoot.witcherycompanion.integrations.tinkers.conarm.traits;

import c4.conarm.lib.traits.AbstractArmorTrait;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class TraitArmorWitchVeil extends AbstractArmorTrait {

    public TraitArmorWitchVeil() {
        super(WitcheryCompanion.prefix("witchs_veil"), 0xe36ea8);
    }

    @Override
    public boolean disableRendering(ItemStack armor, EntityLivingBase entityLivingBase) {
        if (entityLivingBase.isInvisible()) {
            return true;
        }
        return super.disableRendering(armor, entityLivingBase);
    }
}
