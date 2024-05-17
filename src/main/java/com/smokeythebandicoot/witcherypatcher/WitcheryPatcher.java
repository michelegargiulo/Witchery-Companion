package com.smokeythebandicoot.witcherypatcher;

import com.smokeythebandicoot.witcherypatcher.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.ArrayList;
import java.util.List;


@Mod(
        modid = WitcheryPatcher.MODID,
        version = WitcheryPatcher.MODVERSION,
        name = WitcheryPatcher.MODNAME,
        dependencies = WitcheryPatcher.MODDEPS,
        useMetadata = true)
public class WitcheryPatcher implements ILateMixinLoader {

    public static final String MODID = "witcherypatcher";
    public static final String MODNAME = "Witchery Companion";
    public static final String MODVERSION = "1.0";
    public static final String MODDESCRIPTION = "A mod to fix bugs and performance issues in Witchery:Resurrected";
    public static final String MODAUTHOR = "SmokeyTheBandicoot";
    public static final String MODCREDITS = "";
    public static final String MODURL = "";
    public static final String MODLOGO = "assets/witcherypatcher/logo.png";
    public static final String MODDEPS = "required-after:witchery";

    public static Logger logger;

    @Instance(value = MODID)
    public static WitcheryPatcher instance;

    @SidedProxy(clientSide = "com.smokeythebandicoot.witcherypatcher.proxy.ClientProxy",
                serverSide = "com.smokeythebandicoot.witcherypatcher.proxy.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        // Init logger
        logger = event.getModLog();

        // Set mod info
        event.getModMetadata().autogenerated = false;
        event.getModMetadata().name = WitcheryPatcher.MODID;
        event.getModMetadata().credits = WitcheryPatcher.MODCREDITS;
        event.getModMetadata().authorList.clear();
        event.getModMetadata().authorList.add(WitcheryPatcher.MODAUTHOR);
        event.getModMetadata().description = WitcheryPatcher.MODDESCRIPTION;
        event.getModMetadata().url = WitcheryPatcher.MODURL;
        event.getModMetadata().logoFile = WitcheryPatcher.MODLOGO;

        // preInit
        proxy.preInit(event);
    }

    @EventHandler
    public void onInit(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Override
    public List<String> getMixinConfigs() {
        List<String> configs = new ArrayList<>();
        configs.add("mixins.witcherypatches.json");
        return configs;
    }
}