package com.smokeythebandicoot.witcherycompanion.mixins.brewing.action;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.init.Items;
import net.msrandom.witchery.brewing.ItemKey;
import net.msrandom.witchery.brewing.action.BrewActionSerializer;
import net.msrandom.witchery.brewing.action.CapacityBrewAction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 Mixins:
 [Bugfix] Fix Nether Star not removing powerCeiling
 */
@Mixin(CapacityBrewAction.class)
public abstract class CapacityBrewActionMixin {

    @Mutable @Shadow(remap = false) @Final
    private boolean removeCeiling;

    /** This Mixin injects into the Constructor of CapacityBrewAction (only called on load, then stored in registry)
     * and sets the removeCeiling variable for NETHER STAR item. This is due to missing 'remove_ceiling: true' line in
     * data/witchery/brewing/capacity_6.json file */
    @Inject(method = "<init>(Lnet/msrandom/witchery/brewing/ItemKey;Lnet/msrandom/witchery/brewing/action/BrewActionSerializer;IIIZ)V", remap = false, at = @At("TAIL"))
    public void netherStarRemovesCeiling(ItemKey key, BrewActionSerializer<?> serializer, int increment, int ceiling, int cost, boolean removeCeiling, CallbackInfo ci) {
        if (ModConfig.PatchesConfiguration.BrewsTweaks.common_fixNetherStarPowerScaling) {
            if (key.getItem() == Items.NETHER_STAR)
                this.removeCeiling = true;
        }
    }

}
