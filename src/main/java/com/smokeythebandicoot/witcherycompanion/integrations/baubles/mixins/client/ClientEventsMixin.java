package com.smokeythebandicoot.witcherycompanion.integrations.baubles.mixins.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.integrations.baubles.client.BaublesButtonHelper;
import net.minecraft.client.gui.GuiButton;
import net.msrandom.witchery.client.ClientEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

/**
 * Mixins:
 * [Tweak] Always show the Baubles button in the inventory screen, also in the Spirit World
 */
@Mixin(ClientEvents.class)
public abstract class ClientEventsMixin {

    /** While the player is in the Spirit World (and is not in creative) Witchery empties, every tick, the button list of
     * the inventory screens. That removes the recipe book button, but also the one that Baubles adds to open its
     * inventory. With the tweak enabled, only Witchery's intended removal of the other buttons is kept **/
    @WrapOperation(method = "onClientTick", remap = false, at = @At(value = "INVOKE", remap = false,
            target = "Ljava/util/List;clear()V"))
    private static void keepBaublesButtonInSpiritWorld(List<GuiButton> buttons, Operation<Void> original) {
        if (ModConfig.IntegrationConfigurations.BaublesIntegration.tweakShowBaublesButtonInSpiritWorld) {
            BaublesButtonHelper.removeAllButBaublesButtons(buttons);
        } else {
            original.call(buttons);
        }
    }

}
