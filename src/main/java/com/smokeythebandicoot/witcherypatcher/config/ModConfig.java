package com.smokeythebandicoot.witcherypatcher.config;

import com.smokeythebandicoot.witcherypatcher.WitcheryPatcher;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.HashSet;

@Config(modid = WitcheryPatcher.MODID, name = "witchery_patches")
@Mod.EventBusSubscriber(modid = WitcheryPatcher.MODID)
public class ModConfig {

    @Config.Comment("General configuration")
    public static GeneralConfig general;

    @Config.Comment("Patches configuration")
    public static MixinConfig mixins;

    public static class GeneralConfig {

    }

    public static class MixinConfig {

        @Config.Comment("Bugfixes and performance improvements. All enabled by default")
        public static MixinBugfixes bugfixes;

        @Config.Comment("Gameplay changes and augmented customizability. By default, no Witchery behaviour is altered in any way")
        public static GameplayMixins gameplay;

        public static class GameplayMixins {

            @Config.Comment("Alter behaviour of Lord of Torment")
            public static LordOfTormentMixins lordOfTorment;
            @Config.Comment("Alter behaviour of Brew of Erosion")
            public static BrewOfErosionMixins brewOfErosion;

            public static class LordOfTormentMixins {
                @Config.Comment("If true, Lord of Torment won't teleport players to the Torment Dimension")
                public static boolean disableLordOfTormentTeleportation = false;

                @Config.Comment("If true, Lord of Torment won't drop loot. Loot is hardcoded and cannot be changed otherwise")
                public static boolean disableLordOfTormentLoot = false;

            }

            public static class BrewOfErosionMixins {

                @Config.Comment("Maximum harvest level that the brew is able to break. Harder blocks will be ignored. Set to -1 to disable (any block can be harvested)")
                public static int maxBlockHarvestLevel = -1;

                @Config.Comment("If true, Obsidian will be broken and dropped in the world, otherwise it will just be destroyed as other blocks")
                public static boolean dropObsidian = true;

                @Config.Comment("List of blocks to never break, regardless of harvest level. NOTE: this can only LIMIT more blocks than Witchery already restricts. Format is domain:name@meta")
                public static String[] blockBlacklist = new String[] { };

                @Config.Ignore
                public static HashSet<IBlockState> stateBlacklist = new HashSet<>();
            }

        }

        public static class MixinBugfixes {

            @Config.Comment("Configuration for fixes related to brews and brew effects")
            public static BrewsFixes brews;

            @Config.Comment("Configuration for fixes related to infusions")
            public static InfusionFixes infusions;

            @Config.Comment("Configuration for common bugs")
            public static CommonFixes common;

            public static class BrewsFixes {

                @Config.Comment("Fixes brew of erosion crash while attempting to generate a random int with a negative bound")
                public static boolean fixBrewErosion = true;

                @Config.Comment("Fixes crash if players accidentally drink the potion instead of throwing it")
                public static boolean fixFrongsTongueBew = true;

                @Config.Comment("Fixes entities suffocating while traversing blocks removed by Tidal Hold brew")
                public static boolean fixTidalHoldBrew = true;

            }

            public static class InfusionFixes {

                @Config.Comment("Fix Soul Infusions progress reset when player dires")
                public static boolean fixSoulBrewsPersistency = true;
            }

            public static class CommonFixes {

                @Config.Comment("Fix crash when trying to pull a null entity. Overshadows Frogs Tongue brew fix")
                public static boolean fixPullEntityNullPointer = true;

            }
        }
    }


    @Mod.EventBusSubscriber(modid = WitcheryPatcher.MODID)
    public static class ConfigSyncHandler {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if(event.getModID().equals(WitcheryPatcher.MODID)) {
                ConfigManager.sync(WitcheryPatcher.MODID, Config.Type.INSTANCE);
            }
        }

        public static void reloadBrewOfErosionBlacklist() {
            // Clear current configuration
            MixinConfig.GameplayMixins.BrewOfErosionMixins.stateBlacklist = new HashSet<>();

            // Re-add configuration
            for (String entry : MixinConfig.GameplayMixins.BrewOfErosionMixins.blockBlacklist) {
                String[] metaSplit = entry.split("@");
                int meta = 0;
                try {
                    meta = metaSplit.length > 1 ? Integer.parseInt(metaSplit[1]) : 0;
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    WitcheryPatcher.logger.warn("Could not parse blockstate - Invalid meta for entry: " + entry + ". Please fix your configs");
                }

                ResourceLocation rl = new ResourceLocation(metaSplit[0]);
                if (!ForgeRegistries.BLOCKS.containsKey(rl)) {
                    WitcheryPatcher.logger.warn("Could not parse blockstate - Block not found: " + entry + ". Please fix your configs");
                }

                Block block = ForgeRegistries.BLOCKS.getValue(rl);
                MixinConfig.GameplayMixins.BrewOfErosionMixins.stateBlacklist.add(block.getStateFromMeta(meta));
            }
        }
    }
}
