package com.smokeythebandicoot.witcherycompanion.mixins.witchery.network.resources;

import com.smokeythebandicoot.witcherycompanion.api.brewing.BrewRegistry;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.PatchouliApiIntegration;
import com.smokeythebandicoot.witcherycompanion.utils.Mods;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Loader;
import net.msrandom.witchery.network.WitcheryNetworkPacket;
import net.msrandom.witchery.network.resources.PacketUpdateBrewActions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixins:
 * [Feature] Updates BrewRegistry (helper class) immediately after BrewActions are reloaded
 */
@Mixin(PacketUpdateBrewActions.class)
public abstract class PacketUpdateBrewActionsMixin implements WitcheryNetworkPacket {

    @Inject(method = "apply", remap = false, at = @At("TAIL"))
    private void reloadBrewRegistryOnApply(EntityPlayer player, CallbackInfo ci) {
        BrewRegistry.reloadRegistries();
        if (Loader.isModLoaded(Mods.PATCHOULI)) {
            PatchouliApiIntegration.brewActionReloader.reloadFlags();
        }
    }

}
