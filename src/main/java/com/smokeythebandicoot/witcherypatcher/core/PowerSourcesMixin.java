package com.smokeythebandicoot.witcherypatcher.core;

import com.smokeythebandicoot.witcherypatcher.WitcheryPatcher;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.msrandom.witchery.common.IPowerSource;
import net.msrandom.witchery.common.PowerSources;

import java.lang.reflect.Field;
import java.util.HashSet;

//@Mixin(value = PowerSources.class, remap = false)
@Mod.EventBusSubscriber(modid = WitcheryPatcher.MODID)
public class PowerSourcesMixin {

    private static PowerSourcesMixin instance;

    public static PowerSourcesMixin getInstance() {
        if (instance == null)
            instance = new PowerSourcesMixin();
        return instance;
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        Field powerSourcesField = null;
        try {
            powerSourcesField = PowerSources.class.getDeclaredField("powerSources");
            powerSourcesField.setAccessible(true);
            StringBuilder sb = new StringBuilder();
            HashSet<IPowerSource> powerSources = (HashSet<IPowerSource>)powerSourcesField.get(PowerSources.instance());
            WitcheryPatcher.logger.info("SIZE 12345" + powerSources.size());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            WitcheryPatcher.logger.warn("ERROR 12345");
        }
    }

}
