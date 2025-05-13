package com.smokeythebandicoot.witcherycompanion.proxy;

import com.smokeythebandicoot.witcherycompanion.api.progress.IWitcheryProgress;
import com.smokeythebandicoot.witcherycompanion.api.progress.WitcheryProgress;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.PatchouliApiIntegration;
import com.smokeythebandicoot.witcherycompanion.patches.transformation.VampireKeybindsHandler;
import com.smokeythebandicoot.witcherycompanion.utils.Mods;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.msrandom.witchery.transformation.VampireCreatureTrait;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nonnull;

public class ClientProxy extends CommonProxy {

    private static IWitcheryProgress localWitcheryProgress;

    public static void updateLocalWitcheryProgress(IWitcheryProgress progress) {
        boolean newProgress = false;
        if (localWitcheryProgress == null) {
            localWitcheryProgress = new WitcheryProgress();
            newProgress = true;
        }
        if (progress != null && !progress.getUnlockedProgress().equals(localWitcheryProgress.getUnlockedProgress())) {
            localWitcheryProgress.setUnlockedProgress(progress.getUnlockedProgress());
            newProgress = true;
        }

        if (newProgress && Loader.isModLoaded(Mods.PATCHOULI)) {
            PatchouliApiIntegration.reloadBook();
        }
    }


    @Nonnull
    public static IWitcheryProgress getLocalWitcheryProgress() {
        if (localWitcheryProgress == null) {
            localWitcheryProgress = new WitcheryProgress();
        }
        return localWitcheryProgress;
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        // Non-configurable, as it is required for Patchouli integration and does nothing if Patchouli is not used
        if (Loader.isModLoaded(Mods.PATCHOULI)) {
            PatchouliApiIntegration.registerCustomComponents();
            PatchouliApiIntegration.registerCustomMacros();
        }
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        if (ModConfig.PatchesConfiguration.TransformationTweaks.vampire_tweakOverhaulAbilitiesKeybind) {

            for (EVampireKeybind keybind : EVampireKeybind.values()) {
                KeyBinding binding = new KeyBinding(keybind.desc, keybind.keycode, "key.witcherycompanion.category");
                ClientRegistry.registerKeyBinding(binding);
                keybind.setBinding(binding);
            }

            MinecraftForge.EVENT_BUS.register(VampireKeybindsHandler.INSTANCE);
        }
    }

    public enum EVampireKeybind {
        NONE("key.witchery.vampire.power.none.desc", Keyboard.KEY_MINUS, VampireCreatureTrait.Power.NONE),
        DRINK("key.witchery.vampire.power.drink.desc", Keyboard.KEY_STOP, VampireCreatureTrait.Power.DRINK),
        MESMERIZE("key.witchery.vampire.power.mesmerize.desc", Keyboard.KEY_COMMA, VampireCreatureTrait.Power.MESMERIZE),
        RUN("key.witchery.vampire.power.run.desc", Keyboard.KEY_M, VampireCreatureTrait.Power.SPEED),
        BAT("key.witchery.vampire.power.bat.desc", Keyboard.KEY_N, VampireCreatureTrait.Power.BAT),
        ULTIMATE("key.witchery.vampire.power.ultimate.desc", Keyboard.KEY_B, VampireCreatureTrait.Power.ULTIMATE),
        ;

        final String desc;
        final int keycode;
        VampireCreatureTrait.Power power;

        KeyBinding binding = null;

        EVampireKeybind(String desc, int keycode,VampireCreatureTrait.Power power) {
            this.desc = desc;
            this.keycode = keycode;
            this.power = power;
        }

        public int getKeycode() {
            return keycode;
        }

        public VampireCreatureTrait.Power getPower() {
            return power;
        }

        public void setBinding(KeyBinding binding) {
            this.binding = binding;
        }

        public KeyBinding getBinding() {
            return this.binding;
        }

        public boolean trySelectAbility(VampireCreatureTrait transformation, int level) {
            if (
                    transformation != null &&                                              // Transformation obj must be valid
                    this.binding.isPressed() &&                                            // Keybinding has to be pressed
                    transformation.getSelectedPower() != this.power &&                     // This power should not be already selected
                    VampireCreatureTrait.Power.getMax(level).ordinal() >= this.ordinal()   // This power should be selectable by the current ability level
            ) {
                transformation.setSelectedPower(this.power);
                return true;
            }
            return false;
        }
    }

}
