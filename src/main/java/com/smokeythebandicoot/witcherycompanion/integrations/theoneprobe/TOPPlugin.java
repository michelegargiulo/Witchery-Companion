package com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig.IntegrationConfigurations.TopIntegration;
import com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.providers.block.*;
import com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.providers.entity.HobgoblinProbeInfoProvider;
import com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe.providers.entity.ImpProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;

import java.util.function.Function;

public class TOPPlugin implements Function<ITheOneProbe, Void> {

    public static void registerProviders() {

    }

    @Override
    public Void apply(ITheOneProbe probe) {

        if (probe != null) {

            // Altar
            if (TopIntegration.enableTopIntegration) {

                // ---------- BLOCKS ---------- //
                // Altar
                probe.registerProvider(AltarProbeInfoProvider.getInstance());

                // Cauldron
                probe.registerProvider(CauldronProbeInfoProvider.getInstance());

                // Crystal Ball
                probe.registerProvider(CrystalBallProbeInfoProvider.getInstance());

                // Grassper
                probe.registerProvider(GrassperProbeInfoProvider.getInstance());

                // Kettle
                probe.registerProvider(KettleProbeInfoProvider.getInstance());

                // Sun Collector
                probe.registerProvider(SunCollectorProbeInfoProvider.getInstance());

                // ---------- ENTITIES ---------- //
                // Hobgoblin
                probe.registerEntityProvider(HobgoblinProbeInfoProvider.getInstance());

                // Flame Imp
                probe.registerEntityProvider(ImpProbeInfoProvider.getInstance());
            }
        }
        return null;
    }
}
