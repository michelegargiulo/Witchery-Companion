package com.smokeythebandicoot.witcherycompanion.mixins_early.witchery;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import net.minecraft.command.ICommand;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.msrandom.witchery.WitcheryResurrected;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mixins:
 * [Bugfix] Prevent the `/locate` command to be fully replaced by Witchery, and instead modify the Vanilla one
 */
@Mixin(WitcheryResurrected.class)
public class WitcheryResurrectedMixin {

    @WrapWithCondition(
            method = "serverLoad",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/fml/common/event/FMLServerStartingEvent;registerServerCommand(Lnet/minecraft/command/ICommand;)V"
            ),
            remap = false
    )
    private boolean cancelCustomLocateCommand(FMLServerStartingEvent instance, ICommand command) {
        return !ModConfig.PatchesConfiguration.CommonTweaks.tweak_lessInvasiveLocateCommand;
    }

}
