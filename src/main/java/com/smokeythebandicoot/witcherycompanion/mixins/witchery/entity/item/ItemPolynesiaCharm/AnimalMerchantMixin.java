package com.smokeythebandicoot.witcherycompanion.mixins.witchery.entity.item.ItemPolynesiaCharm;

import net.minecraft.entity.IMerchant;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = "net.msrandom.witchery.item.ItemPolynesiaCharm$AnimalMerchant")
public abstract class AnimalMerchantMixin implements IMerchant {

}
