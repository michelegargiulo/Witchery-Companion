package com.smokeythebandicoot.witcherycompanion.client;

import com.google.common.eventbus.EventBus;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import net.minecraft.client.Minecraft;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.include.com.google.common.io.ByteStreams;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@IFMLLoadingPlugin.Name("Witchery Downloader Plugin")
@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
@IFMLLoadingPlugin.SortingIndex(Integer.MIN_VALUE)
public class WitcheryJarDownloader implements IFMLLoadingPlugin, IEarlyMixinLoader {

    private static final String FILE_NAME = "witchery-1.7.10-0.24.1.jar";
    private static final String WITCHERY_1_7_10_DOWNLOAD_URL = "https://mediafilez.forgecdn.net/files/2234/410/" + FILE_NAME;

    private static final Logger LOGGER = LogManager.getLogger();

    private static boolean isValidFile(Path jarFile) throws IOException {
        if (!Files.isRegularFile(jarFile)) {
            return false;
        }

        try (InputStream existingFile = Files.newInputStream(jarFile)) {
            return DigestUtils.md5Hex(existingFile).equals("22096b8e462fa5cca3a6c8054116d4fc");
        }
    }

    private static void downloadJar(Path jarFile) throws IOException {
        if (isValidFile(jarFile)) {
            LOGGER.info("Witchery jar found. Skipping download");
            return;
        }

        LOGGER.info("Could not find Witchery jar. Trying to download...");
        Files.deleteIfExists(jarFile);

        try (
                InputStream download = new URL(WITCHERY_1_7_10_DOWNLOAD_URL).openStream();
                OutputStream output = Files.newOutputStream(jarFile)
        ) {
            ByteStreams.copy(download, output);
        }

        LOGGER.info("Witchery Jar successfully downloaded");
    }

    public static void downloadJar() {

        // This code should not run on the serverside
        if (Launch.minecraftHome == null) return;

        // Retrieve resourcePacks folder
        Path resourcePacksDirectory = Launch.minecraftHome.toPath();
        Path jarFile = resourcePacksDirectory.resolve("resourcepacks/" + FILE_NAME);

        // Try to download the JAR
        try {
            downloadJar(jarFile);
        } catch (IOException exception) {
            LOGGER.warn("Failed to download original Witchery 1.7.10 Jar from {}, got exception {}", WITCHERY_1_7_10_DOWNLOAD_URL, exception);
        }
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return "com.smokeythebandicoot.witcherycompanion.client.WitcheryJarDownloader$WitcheryDownloaderContainer";
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        downloadJar();
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    @Override
    public List<String> getMixinConfigs()
    {
        List<String> configs = new ArrayList<>();
        configs.add("mixins.witcherycompanion_early.json");
        return configs;
    }

    public static class WitcheryDownloaderContainer extends DummyModContainer {

        public WitcheryDownloaderContainer() {
            super(new ModMetadata());
            ModMetadata meta = this.getMetadata();
            meta.modId = "witcherydownloader";
            meta.name = "Witchery: Companion Downloader";
            meta.description = "A simple coremod that downloads the Witchery jar and puts it into the resourcepacks folder";
            meta.version = "1.0";
            meta.logoFile = "";
            meta.authorList.add("SmokeyTheBandicoot");
            meta.authorList.add("Ashley");
        }

        @Override
        public boolean registerBus(EventBus bus, LoadController controller) {
            bus.register(this);
            return true;
        }

    }
}
