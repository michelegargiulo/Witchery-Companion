package com.smokeythebandicoot.witcherycompanion.integrations.patchouli.beans.base;

import com.google.gson.annotations.Expose;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig.IntegrationConfigurations.PatchouliIntegration;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.ProcessorUtils;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.processors.base.ISecretInfo;
import com.smokeythebandicoot.witcherycompanion.proxy.ClientProxy;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;


public abstract class AbstractDTO implements ISecretInfo {

    // Common keys for all DTOs
    public boolean isSecret = false;
    public String secretText = "";
    public String secretTooltip = "";


    public AbstractDTO() { }

    @Override
    public boolean isSecret() {
        return this.isSecret;
    }

}
