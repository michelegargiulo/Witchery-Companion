package com.smokeythebandicoot.witcherypatcher.core;

import com.smokeythebandicoot.witcherypatcher.config.ModConfig;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.ArrayList;
import java.util.List;

public class WPMixinLoader implements ILateMixinLoader {

    @Override
    public List<String> getMixinConfigs() {
        List<String> configs = new ArrayList<>();
        configs.add("mixins.witcherypatches.json");
        return configs;
    }

}
