package com.smokeythebandicoot.witcherycompanion.integrations.theoneprobe;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig.IntegrationConfigurations.TopIntegration;
import mcjty.theoneprobe.api.ITheOneProbe;

import java.util.function.Function;

public class TOPPlugin implements Function<ITheOneProbe, Void> {

    public static void registerProviders() {

    }

    @Override
    public Void apply(ITheOneProbe probe) {

        if (probe != null) {

            // Altar
            if (TopIntegration.enableAltar)
                probe.registerProvider(AltarProbeInfoProvider.getInstance());
        }
        return null;
    }
}
