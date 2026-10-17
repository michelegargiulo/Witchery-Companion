package com.smokeythebandicoot.witcherycompanion.mixins_early.minecraft.client.gui;

import com.smokeythebandicoot.witcherycompanion.client.DivinationClientState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiOverlayDebug;
import net.minecraft.client.gui.ScaledResolution;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixins:
 * [Tweak] Spectator Divination: the F3 debug screen shows the coordinates of the camera (the target of the divination)
 */
@Mixin(GuiOverlayDebug.class)
public abstract class GuiOverlayDebugMixin {

    @Inject(method = "renderDebugInfo", at = @At("HEAD"), cancellable = true)
    private void witcherycompanion$hideDebugDuringDivination(ScaledResolution scaledResolutionIn, CallbackInfo ci) {
        if (DivinationClientState.shouldHideDebugScreen()) {
            // Closes the screen too, so that it does not reappear when the divination ends
            Minecraft.getMinecraft().gameSettings.showDebugInfo = false;
            ci.cancel();
        }
    }

}
