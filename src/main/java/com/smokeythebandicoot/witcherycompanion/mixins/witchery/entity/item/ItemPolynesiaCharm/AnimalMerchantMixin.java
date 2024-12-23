package com.smokeythebandicoot.witcherycompanion.mixins.witchery.entity.item.ItemPolynesiaCharm;

import com.smokeythebandicoot.witcherycompanion.api.PolynesiaCharmApi;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IMerchant;
import net.minecraft.village.MerchantRecipeList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixins:
 * [Feature] CraftTweaker integration for Animal Trades
 */
@Mixin(targets = "net.msrandom.witchery.item.ItemPolynesiaCharm$AnimalMerchant")
public abstract class AnimalMerchantMixin implements IMerchant {

    /** This Mixin injects at the HEAD of populate list, that generates trades for the specific instance of the EntityLiving
     * Instead of populating the list, it delegates this task to the API, that holds the rules to generate it **/
    @Inject(method = "populateList", remap = false, cancellable = true, at = @At("HEAD"))
    private static void injectCraftTweakerCompat(EntityLiving animal, MerchantRecipeList finalList, CallbackInfo ci) {

        if (ModConfig.IntegrationConfigurations.CraftTweakerIntegration.enablePolynesiaCharm) {
            finalList.addAll(PolynesiaCharmApi.generateTradesFor(animal));
            ci.cancel();
        }

    }

}
