package com.smokeythebandicoot.witcherycompanion.config;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.msrandom.witchery.integration.JeiIntegration;

import java.util.HashSet;

@Config(modid = WitcheryCompanion.MODID, name = "witchery_patches")
@Mod.EventBusSubscriber(modid = WitcheryCompanion.MODID)
public class ModConfig {

    @Config.Comment("Patches configuration.\n" +
            "Bugfixes fix bugs and crashes, and are enabled by default.\n" +
            "Tweaks alter the behaviour of Witchery, and are disabled by default")
    @Config.Name("General Configuration")
    public static PatchesConfiguration mixins;

    public static IntegrationConfigurations integrations;


    public static class PatchesConfiguration {

        @Config.Comment("Configuration for common bugs")
        @Config.Name("Common Tweaks")
        public static CommonTweaks common;

        @Config.Comment("Configuration for patches related to brews and brew effects")
        @Config.Name("Brews Tweaks")
        public static BrewsTweaks brews;

        @Config.Comment("Configuration for patches related to infusions")
        @Config.Name("Infusion Tweaks")
        public static InfusionTweaks infusions;

        @Config.Comment("Configuration for patches related to Blocks")
        @Config.Name("Block Tweaks")
        public static BlockTweaks blocks;

        @Config.Comment("Configuration for patches related to Items")
        @Config.Name("Item Tweaks")
        public static ItemTweaks items;

        @Config.Comment("Configuration for patches related to Rites")
        @Config.Name("Rites Tweaks")
        public static RitesTweaks rites;

        @Config.Comment("Configuration for patches related to Potions")
        @Config.Name("Potion Tweaks")
        public static PotionTweaks potions;

        @Config.Comment("Configuration for patches related to Books")
        @Config.Name("Book Tweaks")
        public static BookTweaks books;

        @Config.Comment("Configuration for patches related to Entities")
        @Config.Name("Entity Tweaks")
        public static EntityTweaks entities;

        @Config.Comment("Configuration for patches related to Loot")
        @Config.Name("Loot Tweaks")
        public static LootTweaks loot;


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

            @Config.Comment("Fixes some dispersal methods of the brew of raising causing a crash")
            @Config.Name("Brew of Raising - Fix NUll Player Name Crash")
            public static boolean raising_fixNullPlayerName = true;

            @Config.Comment("If true, gives CraftTweaker integration total control about which blocks can be mined or destroyed, " +
                    "enabling a much more in-depth customizability. If set to True, but no script changes it, behaviour is default Witchery")
            @Config.Name("Brew of Erosion - Tweak Effect With Crafttweaker")
            public static boolean erosion_tweakEnableCrafttweaker = true;
        }

        public static class InfusionTweaks {

            @Config.Comment("Fix Soul Infusions progress reset when player dires")
            @Config.Name("Soul Brews - Fix Persistency After Death")
            @Config.RequiresMcRestart
            public static boolean soulBrews_fixPersistency = true;
        }

        public static class CommonTweaks {

            @Config.Comment("Fix crash when trying to pull a null entity. Overshadows Frogs Tongue brew fix")
            @Config.Name("Entity Utils - Fix Null Pointer On Pull Entity")
            public static boolean entityUtils_fixNullPointer = true;

            @Config.Comment("Fix crash when loot function is applied and a Null Random is passed to it (JER does this)")
            @Config.Name("Loot Utils - Fix NPE on JER Integration")
            public static boolean levelledRandomEnchant_fixCrashNullRandom = true;

        }

        public static class BlockTweaks {

            @Config.Comment("Fix Altar blocks requiring to get broken and re-placed to work properly again")
            @Config.Name("Altar - Fix Power Source Persistency")
            public static boolean altar_fixPowerSourcePersistency = true;

            @Config.Comment("Cursed Blocks are not fully implemented. If you enable this, they still won't work, but " +
                    "at least they should not crash the game as often.")
            @Config.Name("Cursed Blocks - Fix Null Brew Action List Crash")
            public static boolean cursedBlock_fixNullActionListCrash = true;

            @Config.Comment("Fix an edge case where the coffin would not have a color associated with it, causing a crash.")
            @Config.Name("Coffin - Fix Edge Case Crash")
            public static boolean coffin_fixEdgeCrash = true;

            @Config.Comment("Fix crash when one of the coffin pieces is moved by a piston.")
            @Config.Name("Coffin - Fix Crash When Moved By Piston")
            public static boolean coffin_fixPistonMoveCrash = true;

            @Config.Comment("Fix true, it will prevent Mandrake entities from spawning when harvesting non-mature mandrake crops.")
            @Config.Name("Mandrake Crop - Fix Drop Even When Not Mature")
            public static boolean mandrakeCrop_fixMandrakeSpawningNotMature = true;

            @Config.Comment("Fix Arthana, Pentacle and other items placed on top of the altar not dropping when " +
                    "the altar block below them is broken.")
            @Config.Name("Placed Items - Fix No Drops")
            public static boolean placedItems_fixNoDrops = true;

            @Config.Comment("If true, fixes a crash the player joins a world where an Altar with placed items on top are " +
                    "in its view in the first rendered frame.")
            @Config.Name("Placed Items - Fix Not Initialized Crash")
            public static boolean placedItems_fixNotInitializedCrash = true;

            @Config.Comment("Workaround for Stockade blocks. When player head gets too close, the entire" +
                    "screen is rendered as a stockade side texture. This patch enlarges the stockade bounding" +
                    "box to make player head not get as close.")
            @Config.Name("Stockade - Fix Bounding Box")
            public static boolean stockade_fixBoundingBox = true;

            @Config.Comment("If true, fix player bottling skill increase, which won't happen otherwise.")
            @Config.Name("Witch's Cauldron - Fix Bottling Skill Increase")
            public static boolean witchsCauldron_fixBottlingSkillIncrease = true;

            @Config.Comment("If true, fix right-clicking on the cauldron with a bucket voiding its contents.")
            @Config.Name("Witch's Cauldron - Fix Bucket Voiding Brew")
            public static boolean witchsCauldron_fixBucketVoidingBrew = true;

            @Config.Comment("If true, fix popper rendering, displaying them the right way up+")
            @Config.Name("Poppet Shelf - Fix Upside-down Poppets")
            public static boolean poppetShelf_fixUpsideDownPoppetRendering = true;
        }

        public static class ItemTweaks {

            @Config.Comment("If true, Spectral Stones won't dupe the entity they contain")
            @Config.Name("Spectral Stone - Fix Entity Dupe Exploit")
            public static boolean spectralStone_fixEntityReleaseExploit = true;

            @Config.Comment("If true, set max stack size of all chalk items to 1, regardless of damage. " +
                    "Workaround for some chalk-stacking related bugs")
            @Config.Name("Chalk - Tweak Unstackable Chalk")
            public static boolean itemChalk_tweakUnstackableChalk = false;

            @Config.Comment("If true, the Brew of Erosion item crafted in the kettle will behave exactly like the " +
                    "Brew of Erosion crafted into the Witch's Cauldron")
            @Config.Name("Brew of Erosion Item - Tweak Emulate Erosion Brew")
            public static boolean itemErosionBrew_tweakEmulateBrewEffects = false;

            @Config.Comment("If true, when the Seer Stone is shift-right-clicked Throwing Skills won't be printed, " +
                    "as it is a mechanic not yet implemented in Witchery: Resurrected")
            @Config.Name("Seer Stone - Tweak Emulate Erosion Brew")
            public static boolean seerStone_tweakUnprintThrowingSkill = false;

        }

        public static class RitesTweaks {

            @Config.Comment("If true, the Rite of Moving Earth disables moving TileEntities, preventing crashes, bugs and dupes")
            @Config.Name("Rite of Moving Earth - Fix Crash/Dupes while Moving TileEntities")
            public static boolean movingEarth_tweakDisableMovingTEs = true;

            @Config.Comment("If true, the Rite of Moving Earth won't shift blocks upwards if there are obstructions. This will prevent voiding blocks")
            @Config.Name("Rite of Moving Earth - Fix Destroying Blocks")
            public static boolean movingEarth_tweakDisableVoidingBlocks = true;

            @Config.Comment("Set the Ritual of Moving Earth refund policy. Below, the valid values:\n" +
                    "0: never refound the player (default Witchery Behaviour)\n" +
                    "1: if the ritual doesn't move the upwards by its full extent, refund the player\n" +
                    "2: refund only if the rite has not moved any block")
            @Config.Name("Rite of Moving Earth - Tweak Rite Refund Policy")
            public static int movingEarth_tweakRefundPolicy = 0;

            @Config.Comment("A list of blockstates that the Rite of Moving earth won't be able to move.\n" +
                    "Can only restrict more blocks, so Altars, Bedrock and some others won't be moved regardless")
            @Config.Name("Rite of Moving Earth - Tweak Block Blacklist")
            public static String[] movingEarth_tweakBlockBlacklist = new String[] { };

            @Config.Comment("If true, smoke particles and sounds will be played for blocks that won't be moved")
            @Config.Name("Rite of Moving Earth - Tweak Show Particles On Failure")
            public static boolean movingEarth_tweakFailIndicators = false;

            @Config.Ignore
            public static HashSet<IBlockState> movingEarth_stateBlacklist = new HashSet<>();
        }

        public static class PotionTweaks {

            @Config.Comment("Fixes Potion of Fortune not working because of wrong TileEntity check")
            @Config.Name("Fortune Potion - Fix No Effect")
            public static boolean fortunePotion_fixNoEffect = true;
        }

        public static class EntityTweaks {

            @Config.Comment("If true, enables all the Baba Yaga tweaks")
            @Config.Name("Baba Yaga - Tweak Enable Custom Behaviour")
            public static boolean babaYaga_enableTweaks = false;

            @Config.RangeDouble(min = 1.0, max = 256.0)
            @Config.Comment("Sets the max distance within Baba Yaga will give items to their owner")
            @Config.Name("Baba Yaga - Tweak Give Loot Max Distance")
            public static double babaYaga_tweakLivingDropMaxDistance = 64.0;

            @Config.RangeInt(min = 1, max = 10000)
            @Config.Comment("Baba Yaga will give loot to its owner every N ticks. Set here the interval")
            @Config.Name("Baba Yaga - Tweak Give Loot Interval")
            public static int babaYaga_tweakGiveLootTickInterval = 100;

            @Config.RangeInt(min = 1, max = 10000)
            @Config.Comment("Baba Yaga will give loot up until this amount of ticks, then they'll vanish")
            @Config.Name("Baba Yaga - Tweak Give Loot Lifespan")
            public static int babaYaga_tweakMaxGiveTicks = 600;

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

            @Config.Comment("If true, Goblin trades can be customized with CraftTweaker. False by default, because " +
                    "when enabled it completely wipes the Witchery goblin trade tables, and if new trades are not added " +
                    "with CrT, then Goblins won't show any trades")
            @Config.Name("Goblin - Tweak Custom Trades")
            public static boolean goblin_tweakCustomTrades = false;

            @Config.Comment("If true, Lord of Torment won't teleport players to the Torment Dimension")
            @Config.Name("Lord of Torment - Tweak Disable Teleportation to Torment")
            public static boolean lordOfTorment_tweakDisableTeleportation = false;

            @Config.Comment("If true, when interacting with Lilith with an Enchantable item they will act as if with an " +
                    "empty hand. Hint: it's possible to use a resource pack to change the message that lilith says to " +
                    "the player that right-clicks with an enchantable item, to avoid confusing players.")
            @Config.Name("Lilith - Tweak Disable Enchanting")
            public static boolean lilith_tweakDisableEnchanting = false;

            @Config.Comment("If true, allows Owls to sit. (No visual change, but the owl won't follow the owner)")
            @Config.Name("Owl - Fix Sitting Behaviour")
            public static boolean owl_fixSitting = true;

            @Config.Comment("If true, Owls won't take items, except breeding items")
            @Config.Name("Owl - Tweak Disable Taking Items")
            public static boolean owl_tweakDisableTakeItems = true;

            @Config.Comment("If true, Owls' model will change slightly when sitting")
            @Config.Name("Owl - Tweak Sit Model Change")
            public static boolean owl_tweakSitModelChange = false;

            @Config.Comment("If true, Owl children will be smaller than adult counterparts")
            @Config.Name("Owl - Tweak Render Small Children")
            public static boolean owl_tweakRenderChildSmaller = false;
        }

        public static class BookTweaks {

            @Config.Comment("If true, tries to fix the placement of the plant rendering in the Herbology Book")
            @Config.Name("Herbology Book - Fix Plant Rendering")
            public static boolean herbologyBook_fixPlantRendering = true;
        }

        public static class LootTweaks {

            @Config.Comment("Baba Yaga will drop loot according to its Loot Table (witchery:entities/baba_yaga_death)")
            @Config.Name("Baba Yaga - Tweak Drop Loot by Table")
            public static boolean babaYaga_tweakLootTable = false;

            @Config.Comment("Baba Yaga will give loot to its owner based on a Loot Table (witchery:entities/baba_yaga_owner)")
            @Config.Name("Baba Yaga - Tweak Give By Loot Table")
            public static boolean babaYaga_tweakGiveDropLootTable = false;

            @Config.Comment("If true, Coven Witch will drop loot according to its own Loot Table, instead of " +
                    "Vanilla Witch loot table (witchery:entities/coven_witch). WARN: if true, loot added by other " +
                    "mods to vanilla Witch loot table, will not reflect on Coven Witches")
            @Config.Name("Coven Witch - Tweak Give Own Loot Table")
            public static boolean covenWitch_tweakOwnLootTable = false;

            @Config.Comment("If true, Demon will drop loot according to its Loot Table (witchery:entities/demon)")
            @Config.Name("Demon - Tweak Drop Loot by Table")
            public static boolean demon_tweakLootTable = false;

            @Config.Comment("If true, Ent will drop loot according to its Loot Table (witchery:entities/ent)")
            @Config.Name("Ent - Tweak Drop Loot by Table")
            public static boolean ent_tweakLootTable = false;

            @Config.Comment("If true, Cat Familiar will drop loot according to its own Loot Table, instead of " +
                    "Vanilla Ocelot loot table (witchery:entities/coven_witch). WARN: if true, loot added by other " +
                    "mods to vanilla Ocelot loot table, will not reflect on Cat familiars")
            @Config.Name("Cat Familiar - Tweak Give Own Loot Table")
            public static boolean familiarCat_tweakOwnLootTable = false;

            @Config.Comment("If true, Death will drop loot according to its Loot Table (witchery:entities/death)")
            @Config.Name("Death - Tweak Drop Loot by Table")
            public static boolean death_tweakLootTable = false;

            @Config.Comment("If true, Goblin Gulg will drop loot according to its Loot Table (witchery:entities/goblin_gulg)")
            @Config.Name("Gulg - Tweak Drop Loot by Table")
            public static boolean goblinGulg_tweakLootTable = false;

            @Config.Comment("If true, Goblin Mog will drop loot according to its Loot Table (witchery:entities/goblin_mog)")
            @Config.Name("Mog - Tweak Drop Loot by Table")
            public static boolean goblinMog_tweakLootTable = true;

            @Config.Comment("If true, Lord of Torment will drop loot according to its Loot Table (witchery:entities/lord_of_torment)")
            @Config.Name("Lord of Torment - Tweak Drop Loot by Table")
            public static boolean lordOfTorment_tweakLootTable = false;
        }
    }

    public static class IntegrationConfigurations {

        @Config.Comment("Configuration related to Just Enough Resources integration")
        @Config.Name("JER Integration - Configuration")
        public static JerIntegration jerIntegrationConfig;

        @Config.Comment("Configuration related to Just Enough Items integration")
        @Config.Name("JEI Integration - Configuration")
        public static JeiIntegration jeiIntegrationConfig;

        public static class JerIntegration {

            @Config.Comment("Master switch for all JER integrations")
            @Config.Name("JER Integration - Enabled")
            public static boolean enableJerIntegration = true;
        }

        public static class JeiIntegration {

            @Config.Comment("Master switch for all JEI integrations")
            @Config.Name("JEI Integration - Enabled")
            public static boolean enableJeiIntegration = true;

            @Config.Comment("If true, enabled Goblin Trade JEI Integration")
            @Config.Name("JEI Integration - Enable Goblin Trades")
            public static boolean enableJeiGoblinTrades = true;
        }
    }

    @Mod.EventBusSubscriber(modid = WitcheryCompanion.MODID)
    public static class ConfigSyncHandler {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if(event.getModID().equals(WitcheryCompanion.MODID)) {
                reloadConfig();
            }
        }

        public static void reloadConfig() {
            reloadRiteOfMovingEarthBlacklist();
            ConfigManager.sync(WitcheryCompanion.MODID, Config.Type.INSTANCE);
        }

        private static void reloadRiteOfMovingEarthBlacklist() {
            // Clear current configuration
            PatchesConfiguration.RitesTweaks.movingEarth_stateBlacklist = new HashSet<>();

            // Re-add configuration
            for (String entry : PatchesConfiguration.RitesTweaks.movingEarth_tweakBlockBlacklist) {
                String[] metaSplit = entry.split("@");
                int meta = 0;
                try {
                    meta = metaSplit.length > 1 ? Integer.parseInt(metaSplit[1]) : 0;
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    WitcheryCompanion.logger.warn("Could not parse blockstate - Invalid meta for entry: " + entry + ". Please fix your configs");
                }

                ResourceLocation rl = new ResourceLocation(metaSplit[0]);
                if (!ForgeRegistries.BLOCKS.containsKey(rl)) {
                    WitcheryCompanion.logger.warn("Could not parse blockstate - Block not found: " + entry + ". Please fix your configs");
                }

                Block block = ForgeRegistries.BLOCKS.getValue(rl);
                PatchesConfiguration.RitesTweaks.movingEarth_stateBlacklist.add(block.getStateFromMeta(meta));
            }
        }


    }
}
