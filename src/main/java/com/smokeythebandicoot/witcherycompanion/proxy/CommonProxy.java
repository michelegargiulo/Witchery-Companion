package com.smokeythebandicoot.witcherycompanion.proxy;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.integrations.justenoughresources.JERIntegration;
import com.smokeythebandicoot.witcherycompanion.integrations.quark.BlockMandrakeCropIntegration;
import com.smokeythebandicoot.witcherycompanion.patches.common.CommonEventsPatch;
import com.smokeythebandicoot.witcherycompanion.patches.infusion.symbol.SymbolEffectPatch;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.msrandom.witchery.WitcheryModContainer;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {

        if (ModConfig.PatchesConfiguration.InfusionTweaks.soulBrews_fixPersistency)
            MinecraftForge.EVENT_BUS.register(SymbolEffectPatch.INSTANCE);

        if (ModConfig.PatchesConfiguration.RitesTweaks.ritePriorIncarnation_fixNbtNotRemoved)
            MinecraftForge.EVENT_BUS.register(CommonEventsPatch.INSTANCE);

        if (ModConfig.IntegrationConfigurations.QuarkIntegration.fixMandrakesRightClickHarvest && Loader.isModLoaded("quark"))
            MinecraftForge.EVENT_BUS.register(new BlockMandrakeCropIntegration());
    }

    public void init(FMLInitializationEvent event) {
        // Reload configs
        ModConfig.ConfigSyncHandler.reloadConfig();

        // Init compats
        if (Loader.isModLoaded("jeresources") &&
                ModConfig.IntegrationConfigurations.JerIntegration.enableJerIntegration) {
            JERIntegration.init();
        }
    }

}
