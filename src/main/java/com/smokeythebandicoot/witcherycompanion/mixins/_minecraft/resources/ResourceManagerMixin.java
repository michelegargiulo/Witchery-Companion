package com.smokeythebandicoot.witcherycompanion.mixins._minecraft.resources;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.config.ModConfig;
import com.smokeythebandicoot.witcherycompanion.utils.Utils;
import net.minecraft.resources.ResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 Mixins:
 [Bugfix] ResourceManager implements a server- and client-side ResourceManager that only contains resources that
    Witchery is interested in. W:R implementation has some bugs concerning the domain (it assumes the 'witchery' modid
    domain under /resources/data instead of the other mods' modid, and only
 */
@Mixin(ResourceManager.class)
public abstract class ResourceManagerMixin {

    @Shadow(remap = false) @Final
    private WorldServer world;

    @Unique
    private TreeSet<ResourceLocation> witchery_Patcher$resourceSet;

    @Unique
    private HashMap<File, FileSystem> witchery_Patcher$fileSystems = new HashMap<>();

    /**
     * This Mixin is used to capture the reference to the 'set' local variable, initialized at the beginning of the
     * method. For simplicity and to avoid inner Kotlin dependencies, this Mixin just creates a new TreeSet and sets it
     * as both the captured value and return value
     */
    @WrapOperation(method = "findResources", remap = false, at = @At(value = "INVOKE", remap = false,
            target = "Lkotlin/collections/SetsKt;sortedSetOf([Ljava/lang/Object;)Ljava/util/TreeSet;"))
    private TreeSet<ResourceLocation> captureResourceSet(Object[] objects, Operation<TreeSet<ResourceLocation>> original) {
        witchery_Patcher$resourceSet = new TreeSet<>();
        witchery_Patcher$fileSystems = new HashMap<>();
        return witchery_Patcher$resourceSet;
    }

    /**
     * This Mixin injects at the second half of the method, after World folder data inspection and returns early,
     * effectively overriding that part of the method, which contains a few bugs
     */
    @Inject(method = "findResources", remap = false, cancellable = true, at = @At(value = "INVOKE", remap = false, shift = At.Shift.BEFORE,
            target = "Lnet/minecraftforge/fml/common/Loader;instance()Lnet/minecraftforge/fml/common/Loader;"))
    public void fixFindResources(String resourceType, Predicate<String> pathPredicate, CallbackInfoReturnable<Collection<ResourceLocation>> cir) {

        if (ModConfig.PatchesConfiguration.CommonTweaks.customRecipes_fixResourceLoading &&
                witchery_Patcher$resourceSet != null &&
                witchery_Patcher$fileSystems != null) {

            for (ModContainer modContainer : Loader.instance().getActiveModList()) {
                File source = modContainer.getSource();
                if (source.exists()) {

                    witchery_Patcher$useSource(source, (path) -> {
                        Path dataPath = path.resolve("data");
                        if (Files.isDirectory(dataPath)) {
                            try {
                                for (Path domain : Files.list(dataPath).collect(Collectors.toList())) {
                                    if (Files.isDirectory(domain)) {
                                        Path rootPath = domain.resolve(resourceType);
                                        if (Files.isDirectory(rootPath)) {
                                            for (Path fPath : Files.walk(rootPath).collect(Collectors.toList())) {
                                                if (!dataPath.endsWith(".mcmeta") && pathPredicate.test(fPath.toString())) {
                                                    String locationPath = witchery_Patcher$trimPath(domain.getFileName().toString(), "/");
                                                    witchery_Patcher$resourceSet.add(new ResourceLocation(locationPath, resourceType + "/" + rootPath.relativize(fPath)));
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (IOException ex) {
                                Utils.logException("An error occurred while finding resources: ", ex);
                            }
                        }
                    });

                }
            }

            cir.setReturnValue(witchery_Patcher$resourceSet);

        }
    }

    @Unique
    private void witchery_Patcher$useSource(File source, Consumer<Path> action) {
        if (source.isDirectory()) {
            action.accept(source.toPath());
        } else {

            // Retrieve the FileSystem
            FileSystem fs = null;

            // If it's already in Map, means that we created it
            if (!witchery_Patcher$fileSystems.containsKey(source)) {
                // Get the URI
                URI uri = URI.create("jar:" + source.toURI() + "!/");
                try {
                    // Try to create the FileSystem
                    fs = FileSystems.newFileSystem(uri, new HashMap<>());
                    witchery_Patcher$fileSystems.put(source, fs);
                } catch (FileSystemAlreadyExistsException e) {
                    // If it already exists, this exception will be thrown. At this point,
                    // a call to FileSystems.getFileSystem should be safe for this URI
                    fs = FileSystems.getFileSystem(uri);
                    // Also insert in Map, to keep it and avoid using try-catch block next time
                    witchery_Patcher$fileSystems.put(source, fs);
                } catch (IOException e) {
                    // Something else went wrong, so log and do nothing. fs will be null
                    Utils.logException("Something went wrong while exploring resources inside jars", e);
                }
            }

            // An error might occur while crating the Filesystem, so check for this
            if (fs != null) {
                action.accept(fs.getPath("."));
                try {
                    fs.close();
                } catch (IOException e) {
                    Utils.logException("Error while closing FileSystem", e);
                }
            }
        }
    }

    @Unique
    private static String witchery_Patcher$trimPath(String string, String suffix) {
        if (string.endsWith("/")) {
            return string.substring(0, string.length() - suffix.length());
        }
        return string;
    }

}