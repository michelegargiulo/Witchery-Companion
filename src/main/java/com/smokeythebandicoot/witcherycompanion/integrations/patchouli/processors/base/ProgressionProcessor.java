package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.base;

import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.proxy.ClientProxy;

public abstract class ProgressionProcessor {

    public static boolean shouldHide(ISecretInfo info) {
        // If not secret, it means it is written in the manual, so show it
        if (!info.isSecret())
            return false;

        // Otherwise, Check if secrets should always be shown
        ModConfig.IntegrationConfigurations.PatchouliIntegration.EPatchouliSecretPolicy policy = ModConfig.IntegrationConfigurations.PatchouliIntegration.common_showSecretsPolicy;
        if (policy == ModConfig.IntegrationConfigurations.PatchouliIntegration.EPatchouliSecretPolicy.ALWAYS_SHOW)
            return false;

        // If policy is not ALWAYS HIDDEN, then check progress to see if visible
        return policy != ModConfig.IntegrationConfigurations.PatchouliIntegration.EPatchouliSecretPolicy.PROGRESS ||
                !ClientProxy.getLocalWitcheryProgress().hasProgress(info.getSecretKey());
    }

}
