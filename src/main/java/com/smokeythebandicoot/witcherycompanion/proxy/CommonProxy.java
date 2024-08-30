package com.smokeythebandicoot.witcherycompanion.proxy;

import com.smokeythebandicoot.witcherycompanion.api.progress.CapabilityWitcheryProgress;
import com.smokeythebandicoot.witcherycompanion.commands.WitcheryProgressCommand;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.integrations.justenoughresources.JERIntegration;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.PatchouliApiIntegration;
import com.smokeythebandicoot.witcherycompanion.integrations.quark.BlockMandrakeCropIntegration;
import com.smokeythebandicoot.witcherycompanion.integrations.thaumcraft.ThaumcraftIntegration;
import com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.TOPPlugin;
import com.smokeythebandicoot.witcherycompanion.network.CompanionNetworkChannel;
import com.smokeythebandicoot.witcherycompanion.patches.common.CommonEventsPatch;
import com.smokeythebandicoot.witcherycompanion.patches.entity.familiar.FamiliarPatches;
import com.smokeythebandicoot.witcherycompanion.patches.infusion.symbol.SymbolEffectPatch;
import com.smokeythebandicoot.witcherycompanion.patches.triggerdispersal.TileEntityCursedTrigger;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {

        registerTileEntities();

        registerCapabilities();

        registerNetworkHandlers();


        if (ModConfig.PatchesConfiguration.InfusionTweaks.soulBrews_fixPersistency)
            MinecraftForge.EVENT_BUS.register(SymbolEffectPatch.INSTANCE);

        if (ModConfig.PatchesConfiguration.RitesTweaks.ritePriorIncarnation_fixNbtNotRemoved)
            MinecraftForge.EVENT_BUS.register(CommonEventsPatch.INSTANCE);

        if (ModConfig.PatchesConfiguration.EntityTweaks.familiarCat_fixOwnerDisconnect ||
            ModConfig.PatchesConfiguration.EntityTweaks.familiarOwl_fixOwnerDisconnect ||
            ModConfig.PatchesConfiguration.EntityTweaks.familiarToad_fixOwnerDisconnect)
            MinecraftForge.EVENT_BUS.register(FamiliarPatches.getInstance());

        if (ModConfig.IntegrationConfigurations.QuarkIntegration.fixMandrakesRightClickHarvest &&
                Loader.isModLoaded("quark"))
            MinecraftForge.EVENT_BUS.register(BlockMandrakeCropIntegration.INSTANCE);

        if (ModConfig.IntegrationConfigurations.ThaumcraftIntegration.enableThaumcraftIntegration &&
                Loader.isModLoaded("thaumcraft"))
            MinecraftForge.EVENT_BUS.register(ThaumcraftIntegration.class);

        if (ModConfig.IntegrationConfigurations.TopIntegration.enableTopIntegration && Loader.isModLoaded("theoneprobe"))
            FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", TOPPlugin.class.getName());

        // Non-configurable, as it is required for Patchouli integration and does nothing if Patchouli is not used
        if (Loader.isModLoaded("patchouli"))
            PatchouliApiIntegration.registerCustomComponents();

    }

    public void init(FMLInitializationEvent event) {
        // Reload configs
        ModConfig.ConfigSyncHandler.reloadConfig();

        // Init compats
        if (Loader.isModLoaded("jeresources") &&
                ModConfig.IntegrationConfigurations.JerIntegration.enableJerIntegration) {
            JERIntegration.init();
        }

        // Does not have a config to disable, as it just registers the flags
        if (Loader.isModLoaded("patchouli")) {
            PatchouliApiIntegration.registerFlags();
            MinecraftForge.EVENT_BUS.register(PatchouliApiIntegration.class);
        }
    }

    public void serverStarting(FMLServerStartingEvent event) {
        registerCommands(event);
    }


    protected void registerTileEntities() {
        GameRegistry.registerTileEntity(TileEntityCursedTrigger.class, TileEntityCursedTrigger.getRegistryName());
    }

    protected void registerCapabilities() {
        CapabilityWitcheryProgress.register();
    }

    protected void registerNetworkHandlers() {
        CompanionNetworkChannel.registerMessages();
    }

    protected void registerCommands(FMLServerStartingEvent event) {
        event.registerServerCommand(new WitcheryProgressCommand());
    }

}
