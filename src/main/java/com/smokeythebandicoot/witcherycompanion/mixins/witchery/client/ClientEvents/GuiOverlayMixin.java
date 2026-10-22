package com.smokeythebandicoot.witcherycompanion.mixins.witchery.client.ClientEvents;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.util.ResourceLocation;
import net.msrandom.witchery.client.ClientEvents;
import net.msrandom.witchery.client.RenderInfusionEnergyBar;
import net.msrandom.witchery.infusion.Infusion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mixins:
 * [Tweak] Move Infusion energy bar
 * [Tweak] Move Infusion creature power bar
 * [Tweak] Move Vampire Abilities into keybinds
 */
@Mixin(ClientEvents.GUIOverlay.class)
public abstract class GuiOverlayMixin extends GuiIngame {

    private GuiOverlayMixin(Minecraft mcIn) {
        super(mcIn);
    }

    @WrapOperation(method = "renderHotbar", remap = false, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/msrandom/witchery/client/RenderInfusionEnergyBar;draw(FFDLnet/msrandom/witchery/infusion/Infusion;I)V"))
    private void movableInfusionBars(RenderInfusionEnergyBar instance, float x, float y, double value, Infusion infusion, int iconOffsetId, Operation<Void> original) {
        original.call(instance,
                x + ModConfig.PatchesConfiguration.InfusionTweaks.infusion_tweakEnergyBarOffsetX,
                y + ModConfig.PatchesConfiguration.InfusionTweaks.infusion_tweakEnergyBarOffsetY,
                value, infusion, iconOffsetId);
    }

    @WrapOperation(method = "renderHotbar", remap = false, at = @At(value = "INVOKE", remap = false,
            target = "Lnet/msrandom/witchery/client/RenderInfusionEnergyBar;draw(FFDLnet/msrandom/witchery/infusion/Infusion;Lnet/minecraft/util/ResourceLocation;)V"))
    private void movableCreatureInfusionBars(RenderInfusionEnergyBar instance, float x, float y, double value, Infusion infusion, ResourceLocation icon, Operation<Void> original) {
        original.call(instance,
                x + ModConfig.PatchesConfiguration.InfusionTweaks.infusion_tweakCreatureBarOffsetX,
                y + ModConfig.PatchesConfiguration.InfusionTweaks.infusion_tweakCreatureBarOffsetY,
                value, infusion, icon);
    }

}
