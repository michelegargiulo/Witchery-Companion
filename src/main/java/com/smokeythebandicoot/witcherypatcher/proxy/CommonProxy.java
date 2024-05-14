package com.smokeythebandicoot.witcherypatcher.proxy;

import com.smokeythebandicoot.witcherypatcher.WitcheryPatcher;
import com.smokeythebandicoot.witcherypatcher.config.ModConfig;
import com.smokeythebandicoot.witcherypatcher.integrations.justenoughresources.DummyEnchantment;
import com.smokeythebandicoot.witcherypatcher.integrations.justenoughresources.JERIntegration;
import com.smokeythebandicoot.witcherypatcher.patches.infusion.symbol.SymbolEffectPatch;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {

        if (ModConfig.PatchesConfiguration.InfusionTweaks.soulBrews_fixPersistency)
            MinecraftForge.EVENT_BUS.register(SymbolEffectPatch.getInstance());
    }

    public void init(FMLInitializationEvent event) {
        // Reload configs
        ModConfig.ConfigSyncHandler.reloadConfig();

        // Init compats
        if (Loader.isModLoaded("jeresources") &&
                ModConfig.IntegrationConfigurations.enableJerIntegration) {
            JERIntegration.init();
        }
    }

}
