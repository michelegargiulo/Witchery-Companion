package com.smokeythebandicoot.witcherycompanion.mixins.witchery.integration;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.utils.Mods;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Loader;
import net.msrandom.witchery.integration.WitcheryIntegration;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;


/**
 * Mixins:
 * [Bugfix] Fix crash when baubles is installed and botania is not, and player is a Vampire and they die
 */
@Mixin(net.msrandom.witchery.integration.BaublesIntegration.class)
public abstract class BaublesIntegrationMixin extends WitcheryIntegration {

    private BaublesIntegrationMixin(String modId) {
        super(modId);
    }

    @Unique
    private static final Set<Item> BANNED_ITEMS = new HashSet<>();

    /** This Mixin injects just before the references to Botania.ModItems and returns an empty set if Botania is not installed **/
    @Inject(method = "canVampireBeKilled", remap = false, cancellable = true, at = @At("HEAD"))
    private void doNotReferenceBotaniaItemsIfNotInstalled(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        if (ModConfig.IntegrationConfigurations.BaublesIntegration.fixCrashOnVampireDeath) {
            if (Loader.isModLoaded(Mods.BOTANIA)) {
                // Add botania things
            }

            // if other mod loaded, add other things
            cir.setReturnValue(false);
        }
    }

}
