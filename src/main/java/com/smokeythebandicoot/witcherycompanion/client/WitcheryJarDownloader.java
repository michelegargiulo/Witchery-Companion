package com.smokeythebandicoot.witcherycompanion.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.include.com.google.common.io.ByteStreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class WitcheryJarDownloader {
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
        if (!isValidFile(jarFile)) {
            Files.deleteIfExists(jarFile);
        }

        try (
                InputStream download = new URL(WITCHERY_1_7_10_DOWNLOAD_URL).openStream();
                OutputStream output = Files.newOutputStream(jarFile)
        ) {
            ByteStreams.copy(download, output);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void downloadJar() {
        Path resourcePacksDirectory = Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks().toPath();
        Path jarFile = resourcePacksDirectory.resolve(FILE_NAME);

        try {
            downloadJar(jarFile);
        } catch (IOException exception) {
            LOGGER.warn("Failed to download original Witchery 1.7.10 Jar from {}, got exception {}", WITCHERY_1_7_10_DOWNLOAD_URL, exception);
        }
    }
}
