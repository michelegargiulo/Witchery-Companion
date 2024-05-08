package com.smokeythebandicoot.witcherypatcher.config;

import com.smokeythebandicoot.witcherypatcher.WitcheryPatcher;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.HashSet;

@Config(modid = WitcheryPatcher.MODID, name = "witchery_patches")
@Mod.EventBusSubscriber(modid = WitcheryPatcher.MODID)
public class ModConfig {

    @Config.Comment("Patches configuration.\n" +
            "Bugfixes fix bugs and crashes, and are enabled by default.\n" +
            "Tweaks alter the behaviour of Witchery, and are disabled by default")
    @Config.Name("General Configuration")
    public static PatchesConfiguration mixins;

    public static class PatchesConfiguration {

        @Config.Comment("Configuration for common bugs")
        @Config.Name("Common Tweaks")
        public static CommonTweaks common;

        @Config.Comment("Configuration for fixes related to brews and brew effects")
        @Config.Name("Brews Tweaks")
        public static BrewsTweaks brews;

        @Config.Comment("Configuration for fixes related to infusions")
        @Config.Name("Infusion Tweaks")
        public static InfusionTweaks infusions;

        @Config.Comment("Configuration for bugs related to Blocks")
        @Config.Name("Block Tweaks")
        public static BlockTweaks blocks;

        @Config.Comment("Configuration for bugs related to Items")
        @Config.Name("Item Tweaks")
        public static ItemTweaks items;

        @Config.Comment("Configuration for bugs related to Rites")
        @Config.Name("Rites Tweaks")
        public static RitesTweaks rites;

        @Config.Comment("Configuration for bugs related to Potions")
        @Config.Name("Potion Tweaks")
        public static PotionTweaks potions;

        @Config.Comment("Configuration for bugs related to Books")
        @Config.Name("Book Tweaks")
        public static BookTweaks books;

        @Config.Comment("Configuration for bugs related to Entities")
        @Config.Name("Entity Tweaks")
        public static EntityTweaks entities;


        public static class BrewsTweaks {

            @Config.Comment("Needs to be enabled for some Brew Patches to work. Enabled for extended flexibility on when certain" +
                    "brews should be applied")
            @Config.Name("Common - Extend Flexibility")
            public static boolean common_tweakBrewApplications = true;

            @Config.Comment("Disables Strength Ceiling. Fixes some 'non-bugs' related to Potion Brews not scaling their " +
                    "effects despite their power increasing (Harm I deals same damage as Harm III)")
            @Config.Name("Common - Disable Strength Ceiling")
            public static boolean common_tweakDisableStrengthCeiling = true;

            @Config.Comment("If true, fixes Cauldron rituals with Liquid Dispersal not having any effect. Also" +
                    " should improve performance and memory usage by 0.00000001%")
            @Config.Name("Liquid Dispersal - Fix Cauldron Ritual No Effect")
            public static boolean common_fixCauldronRitualLiquidDispersalNoEffect = true;

            @Config.Comment("If true, fixes the brew from breaking blocks even if the 'ignore blocks' modifier has been added")
            @Config.Name("Brew of Blast - Fix Terrain Damage")
            public static boolean brewBlast_fixExplosionBreakingBlocks = true;

            @Config.Comment("Fixes brew of erosion crash while attempting to generate a random int with a negative bound")
            @Config.Name("Brew of Erosion - Fix Random Integer Bound Crash")
            public static boolean erosion_fixRandomIntegerCrash = true;

            @Config.Comment("Fixes crash if players accidentally drink the potion instead of throwing it")
            @Config.Name("Brew of Frogs Tongue - Fix Pull Null Entity Crash")
            public static boolean frogsTongue_fixPullNullEntity = true;

            @Config.Comment("Fixes entities suffocating while traversing blocks removed by Tidal Hold brew")
            @Config.Name("Brew of Tidal Hold - Fix Entity Suffocation")
            public static boolean tidalHold_fixEntitySuffocation = true;

            @Config.Comment("Maximum harvest level that the brew is able to break. Harder blocks will be ignored. Set to -1 to disable (any block can be harvested)")
            @Config.Name("Brew of Erosion - Tweak Maximum Harvest Level")
            public static int erosion_tweakMaximumHL = -1;

            @Config.Comment("If true, Obsidian will be broken and dropped in the world, otherwise it will just be destroyed as other blocks")
            @Config.Name("Brew of Erosion - Tweak Obsidian Drop")
            public static boolean erosion_tweakObsidianDrop = true;

            @Config.Comment("List of blocks to never break, regardless of harvest level. NOTE: this can only LIMIT more blocks than Witchery already restricts. Format is domain:name@meta")
            @Config.Name("Brew of Erosion - Tweak BlockState Blacklist")
            public static String[] erosion_tweakBlockBlacklist = new String[] { };

            @Config.Ignore
            public static HashSet<IBlockState> stateBlacklist = new HashSet<>();
        }

        public static class InfusionTweaks {

            @Config.Comment("Fix Soul Infusions progress reset when player dires")
            @Config.Name("Soul Brews - Fix Persistency After Death")
            public static boolean soulBrews_fixPersistency = true;
        }

        public static class CommonTweaks {

            @Config.Comment("Fix crash when trying to pull a null entity. Overshadows Frogs Tongue brew fix")
            @Config.Name("Entity Utils - Fix Null Pointer On Pull Entity")
            public static boolean entityUtils_fixNullPointer = true;

        }

        public static class BlockTweaks {

            @Config.Comment("Fix Altar blocks requiring to get broken and re-placed to work properly again.\n" +
                    "NOTE: it still does not fix all edge cases. Players might still have to interact with the altar" +
                    "before crafting. Right-click should suffice for most cases")
            @Config.Name("Altar - Fix Power Source Persistency")
            public static boolean altar_fixPowerSourcePersistency = true;

            @Config.Comment("Fix Arthana, Pentacle and other items placed on top of the altar not dropping when " +
                    "the altar block below them is broken.")
            @Config.Name("Placed Items - Fix No Drops")
            public static boolean placedItems_fixNoDrops = true;

            @Config.Comment("Workaround for Stockade blocks. When player head gets too close, the entire" +
                    "screen is rendered as a stockade side texture. This patch enlarges the stockade bounding" +
                    "box to make player head not get as close.")
            @Config.Name("Stockade - Fix Bounding Box")
            public static boolean stockade_fixBoundingBox = true;

            @Config.Comment("Fix an edge case where the coffin would not have a color associated with it, causing a crash.")
            @Config.Name("Coffin - Fix Edge Case Crash")
            public static boolean coffin_fixEdgeCrash = true;

            @Config.Comment("Cursed Blocks are not fully implemented. If you enable this, they still won't work, but " +
                    "at least they should not crash the game as often.")
            @Config.Name("Cursed Blocks - Fix Null Brew Action List Crash")
            public static boolean cursedBlock_fixNullActionListCrash = true;

            @Config.Comment("If true, fix player bottling skill increase, which won't happen otherwise.")
            @Config.Name("Witch's Cauldron - Fix Bottling Skill Increase")
            public static boolean witchsCauldron_fixBottlingSkillIncrease = true;

            @Config.Comment("If true, fix right-clicking on the cauldron with a bucket voiding its contents.")
            @Config.Name("Witch's Cauldron - Fix Bucket Voiding Brew")
            public static boolean witchsCauldron_fixBucketVoidingBrew = true;
        }

        public static class ItemTweaks {

            @Config.Comment("If true, Spectral Stones won't dupe the entity they contain")
            @Config.Name("Spectral Stone - Fix Entity Dupe Exploit")
            public static boolean spectralStone_fixEntityReleaseExploit = true;

            @Config.Comment("If true, set maax stack size of all chalk items to 1, regardless of damage")
            @Config.Name("Chalk - Tweak Unstackable Chalk")
            public static boolean itemChalk_tweakUnstackableChalk = false;
        }

        public static class RitesTweaks {

            @Config.Comment("If true, the Rite of Moving Earth disables moving TileEntities, preventing crashes, bugs and dupes")
            @Config.Name("Rite of Moving Earth - Fix Crash/Dupes while Moving TileEntities")
            public static boolean movingEarth_disableMovingTEs = true;

            @Config.Comment("If true, the Rite of Moving Earth won't shift blocks upwards if there are obstructions. This will prevent voiding blocks")
            @Config.Name("Rite of Moving Earth - Fix Destroying Blocks")
            public static boolean movingEarth_disableVoidingBlocks = true;

            @Config.Comment("Set the Ritual of Moving Earth refund policy. Below, the valid values:\n" +
                    "0: never refound the player (default Witchery Behaviour)\n" +
                    "1: if the ritual doesn't move the upwards by its full extent, refund the player\n" +
                    "2: refund only if the rite has not moved any block")
            @Config.Name("Rite of Moving Earth - Tweak Rite Refund Policy")
            public static int movingEarth_refundPolicy = 0;

            @Config.Comment("A list of blockstates that the Rite of Moving earth won't be able to move.\n" +
                    "Can only restrict more blocks, so Altars, Bedrock and some others won't be moved regardless")
            @Config.Name("Rite of Moving Earth - Tweak Block Blacklist")
            public static String[] movingEarth_blockBlacklist = new String[] { };

            @Config.Comment("If true, smoke particles and sounds will be played for blocks that won't be moved")
            @Config.Name("Rite of Moving Earth - Tweak Show Particles On Failure")
            public static boolean movingEarth_failIndicators = false;

            @Config.Ignore
            public static HashSet<IBlockState> movingEarth_stateBlacklist = new HashSet<>();
        }

        public static class PotionTweaks {

            @Config.Comment("Fixes Potion of Fortune not working because of wrong TileEntity check")
            @Config.Name("Fortune Potion - Fix No Effect")
            public static boolean fortunePotion_fixNoEffect = true;
        }

        public static class EntityTweaks {

            @Config.Comment("If true, fixes the problem where Coven Witches require certain amount of items as a quest, but if" +
                    "the player holds more items than needed, the quest goes into negative item amount requirement")
            @Config.Name("Coven Witch - Fix Negative Request Amount")
            public static boolean covenWitch_fixNegativeRequestAmount = true;

            @Config.Comment("If true, fixes a freeze when the Broom breaks due to not dismounting passengers")
            @Config.Name("Enchanted Broom - Fix Freeze On Break")
            public static boolean enchantedBroom_fixFreezeOnBreak = true;

            @Config.Comment("Sets the maximum amount of damage that the broom can take before breaking")
            @Config.Name("Enchanted Broom - Tweak Max Health")
            public static float enchantedBroom_tweakMaxHealth = 40.0f;

            @Config.Comment("If true, Lord of Torment won't teleport players to the Torment Dimension")
            @Config.Name("Lord of Torment - Tweak Disable Teleportation to Torment")
            public static boolean lordOfTorment_tweakDisableTeleportation = false;

            @Config.Comment("If true, Lord of Torment won't drop loot. Loot is hardcoded and cannot be changed otherwise")
            @Config.Name("Lord of Torment - Tweak Disable Hardcoded Loot")
            public static boolean lordOfTorment_tweakDisableLoot = false;

            @Config.Comment("If true, allows Owls to sit. (No visual change, but the owl won't follow the owner)")
            @Config.Name("Owl - Fix Sitting Behaviour")
            public static boolean owl_fixSitting = true;

            @Config.Comment("If true, Owls won't take items, except breeding items")
            @Config.Name("Owl - Tweak Disable Taking Items")
            public static boolean owl_tweakDisableTakeItems = true;

            @Config.Comment("If true, Owls' model will change slightly when sitting")
            @Config.Name("Owl - Tweak Sit Model Change")
            public static boolean owl_tweakSitModelChange = false;

            @Config.Comment("If true, Owls will look down when sitting, as if they're resting")
            @Config.Name("Owl - Tweak Render Small Children")
            public static boolean owl_tweakRenderChildSmaller = false;
        }

        public static class BookTweaks {

            @Config.Comment("If true, tries to fix the placement of the plant rendering in the Herbology Book")
            @Config.Name("Herbology Book - Fix Plant Rendering")
            public static boolean herbologyBook_fixPlantRendering = true;
        }
    }


    @Mod.EventBusSubscriber(modid = WitcheryPatcher.MODID)
    public static class ConfigSyncHandler {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if(event.getModID().equals(WitcheryPatcher.MODID)) {
                reloadConfig();
            }
        }

        public static void reloadConfig() {
            reloadBrewOfErosionBlacklist();
            reloadRiteOfMovingEarthBlacklist();
            ConfigManager.sync(WitcheryPatcher.MODID, Config.Type.INSTANCE);
        }

        private static void reloadBrewOfErosionBlacklist() {
            // Clear current configuration
            PatchesConfiguration.BrewsTweaks.stateBlacklist = new HashSet<>();

            // Re-add configuration
            for (String entry : PatchesConfiguration.BrewsTweaks.erosion_tweakBlockBlacklist) {
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
                PatchesConfiguration.BrewsTweaks.stateBlacklist.add(block.getStateFromMeta(meta));
            }
        }

        private static void reloadRiteOfMovingEarthBlacklist() {
            // Clear current configuration
            PatchesConfiguration.RitesTweaks.movingEarth_stateBlacklist = new HashSet<>();

            // Re-add configuration
            for (String entry : PatchesConfiguration.RitesTweaks.movingEarth_blockBlacklist) {
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
                PatchesConfiguration.RitesTweaks.movingEarth_stateBlacklist.add(block.getStateFromMeta(meta));
            }
        }


    }
}