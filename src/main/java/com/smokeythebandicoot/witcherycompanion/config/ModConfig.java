package com.smokeythebandicoot.witcherycompanion.config;

import com.smokeythebandicoot.witcherycompanion.WitcheryCompanion;
import com.smokeythebandicoot.witcherycompanion.integrations.patchouli.PatchouliApiIntegration;
import com.smokeythebandicoot.witcherycompanion.utils.Mods;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;

@Config(modid = WitcheryCompanion.MODID, name = "witchery_patches")
@Mod.EventBusSubscriber(modid = WitcheryCompanion.MODID)
public class ModConfig {

    @Config.Comment("Patches configuration.\n" +
            "Bugfixes fix bugs and crashes, and are enabled by default.\n" +
            "Tweaks alter the behaviour of Witchery, and are disabled by default")
    @Config.Name("Patches Configuration")
    public static PatchesConfiguration mixins;

    @Config.Name("Integration Configuration")
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

        @Config.Comment("Configuration for patches related to Dimensions")
        @Config.Name("Dimension Tweaks")
        public static DimensionTweaks dimensions;

        @Config.Comment("Configuration for patches related to Transformations")
        @Config.Name("Transformation Tweaks")
        public static TransformationTweaks transformations;

        @Config.Comment("Configuration for patches related to World Generation")
        @Config.Name("Worldgen Tweaks")
        public static WorldGenTweaks worldgen;


        public static class BrewsTweaks {

            @Config.Comment("Configuration for patches related to Triggered Dispersal")
            @Config.Name("Triggered Dispersal Tweaks")
            public static TriggeredDispersalTweaks triggeredDispersal;

            @Config.Comment("Needs to be enabled for some Brew Patches to work. Enabled for extended flexibility on when certain" +
                    "brews should be applied")
            @Config.Name("Common - Extend Flexibility")
            public static boolean common_tweakBrewApplications = true;

            @Config.Comment("Disables Strength Ceiling. Fixes some 'non-bugs' related to Potion Brews not scaling their " +
                    "effects despite their power increasing (Harm I deals same damage as Harm III)")
            @Config.Name("Common - Disable Strength Ceiling")
            public static boolean common_tweakDisableStrengthCeiling = true;

            @Config.Comment("If true, completes implementation of the throwing skill, that is used to throw splash brews further. To improve " +
                    "this skill, launch more splash brews. Max distance is controlled by the Tweak Throwing Skill Max Power config")
            @Config.Name("Common - Fix Throwing Skill")
            public static boolean common_fixThrowingSkill = true;

            @Config.Comment("Controls how powerful the throws of a lvl100 throwing skill compared to a lvl1. Defaults to 2.0. " +
                    "Higher values may result in weird behavior, so it's capped at 3.0")
            @Config.RangeDouble(min = 1.0f, max = 3.0f)
            @Config.Name("Common - Tweak Throwing Skill Max Power")
            public static float common_tweakThrowingSkillMaxPower = 2.0f;

            @Config.Comment("If true, fixes Cauldron rituals with Liquid Dispersal not having any effect. Also" +
                    " should improve performance and memory usage by 0.00000001%")
            @Config.Name("Liquid Dispersal - Fix Cauldron Ritual No Effect")
            public static boolean common_fixCauldronRitualLiquidDispersalNoEffect = true;

            @Config.Comment("If true, fixes the brew from breaking blocks even if the 'ignore blocks' modifier has been added")
            @Config.Name("Brew of Blast - Fix Terrain Damage")
            public static boolean blast_fixExplosionBreakingBlocks = true;

            @Config.Comment("Fixes brew of erosion crash while attempting to generate a random int with a negative bound")
            @Config.Name("Brew of Erosion - Fix Random Integer Bound Crash")
            public static boolean erosion_fixRandomIntegerCrash = true;

            @Config.Comment("If true, it adds more blocks to the unbreakable blocks list (Torment floor and portal, Mirror walls, etc.)")
            @Config.Name("Brew of Erosion - Fix Breaking Unbreakable Blocks")
            public static boolean erosion_fixUnbreakables = true;

            @Config.Comment("If true, gives CraftTweaker integration total control about which blocks can be mined or destroyed, " +
                    "enabling a much more in-depth customizability. If set to True, but no script changes it, behaviour is default Witchery")
            @Config.Name("Brew of Erosion - Tweak Effect With Crafttweaker")
            public static boolean erosion_tweakEnableCrafttweaker = true;

            @Config.Comment("Fixes crash if players accidentally drink the potion instead of throwing it")
            @Config.Name("Brew of Frogs Tongue - Fix Pull Null Entity Crash")
            public static boolean frogsTongue_fixPullNullEntity = true;

            @Config.Comment("Fixes entities suffocating while traversing blocks removed by Tidal Hold brew")
            @Config.Name("Brew of Tidal Hold - Fix Entity Suffocation")
            public static boolean tidalHold_fixEntitySuffocation = true;

            @Config.Comment("Fixes some dispersal methods of the brew of raising causing a crash")
            @Config.Name("Brew of Raising - Fix Null Player Name Crash")
            public static boolean raising_fixNullPlayerName = true;

            public static class TriggeredDispersalTweaks {

                @Config.Comment("If true, Companion will replace the entire logic and inner workings of the Triggered " +
                        "Dispersal. This will make this dispersal work in the first place and improves several aspects")
                @Config.Name("Common - Enable Trigger Dispersal Rework")
                public static boolean enable_dispersalRework = true;

                @Config.Comment("If true, Doors will be able to be cursed with Triggered Dispersal brews")
                @Config.Name("Triggered Dispersal - Door")
                public static boolean enable_door = true;

                @Config.Comment("If true, Trap Doors will be able to be cursed with Triggered Dispersal brews")
                @Config.Name("Triggered Dispersal - Trap Door")
                public static boolean enable_trapdoor = true;

                @Config.Comment("If true, Levers will be able to be cursed with Triggered Dispersal brews")
                @Config.Name("Triggered Dispersal - Lever")
                public static boolean enable_lever = true;

                @Config.Comment("If true, Buttons will be able to be cursed with Triggered Dispersal brews")
                @Config.Name("Triggered Dispersal - Button")
                public static boolean enable_button = true;

                @Config.Comment("If true, Pressure Plates will be able to be cursed with Triggered Dispersal brews")
                @Config.Name("Triggered Dispersal - Pressure Plate")
                public static boolean enable_pressurePlate = true;

                @Config.Comment("If true, Fence Gates will be able to be cursed with Triggered Dispersal brews")
                @Config.Name("Triggered Dispersal - Fence Gate")
                public static boolean enable_fenceGate = true;

                @Config.Comment("If true, Tripwires will be able to be cursed with Triggered Dispersal brews")
                @Config.Name("Triggered Dispersal - Tripwire")
                public static boolean enable_tripwireHook = true;

                @Config.Comment("If true, Chests will be able to be cursed with Triggered Dispersal brews")
                @Config.Name("Triggered Dispersal - Chest")
                public static boolean enable_chest = true;

                @Config.Comment("If true, Ender Chests will be able to be cursed with Triggered Dispersal brews")
                @Config.Name("Triggered Dispersal - Ender Chest")
                public static boolean enable_enderChest = true;

                @Config.Comment("If true, Crafting Tables will be able to be cursed with Triggered Dispersal brews")
                @Config.Name("Triggered Dispersal - Crafting Table")
                public static boolean enable_craftingTable = true;

                @Config.Comment("If true, Beds will be able to be cursed with Triggered Dispersal brews")
                @Config.Name("Triggered Dispersal - Bed")
                public static boolean enable_beds = true;

                @Config.Comment("If true, Grasspers will be able to be cursed with Triggered Dispersal brews")
                @Config.Name("Triggered Dispersal - Grassper")
                public static boolean enable_grasspers = true;

            }
        }

        public static class InfusionTweaks {

            @Config.Comment("Fixes weird behaviour when Alohomora is used on Rowan Doors")
            @Config.Name("Alohomora Symbol Effect - Fix Effect On Rowan Doors")
            @Config.RequiresMcRestart
            public static boolean alohomora_fixOnRowanDoors = true;

            @Config.Comment("Fixes doors having different hinge position and facing on transformation")
            @Config.Name("Colloportus Symbol Effect - Fix Preserve Door Properties")
            @Config.RequiresMcRestart
            public static boolean colloportus_fixPreserveDoorProperties = true;

            @Config.Comment("Fix Soul Infusions progress reset when player dies")
            @Config.Name("Soul Brews - Fix Persistency After Death")
            @Config.RequiresMcRestart
            public static boolean soulBrews_fixPersistency = true;

            @Config.Comment("Fixes crashes due to bosses using disabled spells")
            @Config.Name("Symbol Effects - Fix Bosses Using Disabled Spells")
            @Config.RequiresMcRestart
            public static boolean symbolEffects_fixBossesUsingDisabledSpells = true;

            @Config.Comment("Sets the amount of ticks between Sentinel effect activations")
            @Config.Name("Sentinel Effect - Tweak Cooldown")
            public static int infusedSpiritSentinel_tweakCooldown = 600;

            @Config.Comment("Sets the amount of ticks between Twister effect activations")
            @Config.Name("Twister Effect - Tweak Cooldown")
            public static int infusedSpiritTwister_tweakCooldown = 10;

            @Config.Comment("Sets the amount of ticks between Twister effect activations")
            @Config.Name("Infusion Energy Bar - Offset X")
            public static float infusion_tweakEnergyBarOffsetX = 0;

            @Config.Comment("Sets the amount of ticks between Twister effect activations")
            @Config.Name("Infusion Energy Bar - Offset Y")
            public static float infusion_tweakEnergyBarOffsetY = 0;

            @Config.Comment("Sets the amount of ticks between Twister effect activations")
            @Config.Name("Infusion Creature Energy Bar - Offset X")
            public static float infusion_tweakCreatureBarOffsetX = 0;

            @Config.Comment("Sets the amount of ticks between Twister effect activations")
            @Config.Name("Infusion Creature Energy Bar - Offset Y")
            public static float infusion_tweakCreatureBarOffsetY = 0;

        }

        public static class CommonTweaks {

            @Config.Comment("Custom Recipes in Mods' data folder is fundamentally broken in W:R. If true, this fixes " +
                    "resource loading and allows Mod Authors to define custom .jsons and override custom ones in " +
                    "resources/data/<modid>/brewing|mutations|... folders, creating custom recipes. This does not " +
                    "touch data folders inside of the World save folder" )
            @Config.Name("Custom Recipes - Fix Resource Loading")
            public static boolean customRecipes_fixResourceLoading = true;

            @Config.Comment("Fix crash when trying to pull a null entity. Overshadows Frogs Tongue brew fix")
            @Config.Name("Entity Utils - Fix Null Pointer On Pull Entity")
            public static boolean entityUtils_fixNullPointer = true;

            @Config.Comment("Fix crash when loot function is applied and a Null Random is passed to it (JER does this)")
            @Config.Name("Loot Utils - Fix NPE on JER Integration")
            public static boolean levelledRandomEnchant_fixCrashNullRandom = true;

            @Config.Comment("Fix crash an Entity (such as Lord of Torment, or Lilith) uses a Spell that has been disabled. " +
                    "As a side effect of enabling this, spell projectiles will have a default size and a random color.")
            @Config.Name("Spell Effect Render - Fix Crash On Disabled Spell Cast")
            public static boolean renderSpellEffect_fixCrashOnDisabledSpell = true;

            @Config.Comment("If true, reduces to a single line an exception logging occuring because a Spell Effect " +
                    "has been disabled in config. As the log spam only happens at load-time, this option requires a MC restart")
            @Config.Name("Spell Effect - Tweak Reduce Logging On Disabled Effects")
            @Config.RequiresMcRestart
            public static boolean spellEffect_tweakMuteLogSpamOnDisable = true;

            @Config.Comment("Fix 'ghost entities' being rendered in world when player changes dimension")
            @Config.Name("Shape Shifting - Fix Floating Entities")
            public static boolean shapeShift_fixFloatingEntities = true;

            @Config.Comment("If true, when player shapeshifts into a form that has more HP, its health percentage is perserved")
            @Config.Name("Shape Shifting - Tweak Preserve HP Ratio on Trasform")
            public static boolean shapeShift_tweakPreserveHpPercentOnTransform = false;

            @Config.Comment("If true, when player transforms back into its normal form, its health percentage is perserved")
            @Config.Name("Shape Shifting - Tweak Preserve HP Ratio on Detransform")
            public static boolean shapeShift_tweakPreserveHpPercentOnDetransform = false;

            @Config.Comment("Fixes an edge-case crash that happens when villagers try to sleep")
            @Config.Name("Villager - Fix Crash On Sleeping")
            public static boolean villagerExtendedData_fixCrashOnSleeping = true;

            @Config.Comment("Disallow Elytra usage when the entity is Resized or Transformed")
            @Config.Name("Elytra - Disallow When Resized Or Transformed")
            public static boolean tweak_disallowElytraWhenTransformedOrResized = true;

        }

        public static class BlockTweaks {

            @Config.Comment("Configuration for patches related to Books")
            @Config.Name("Kettle Tweaks")
            public static KettleTweaks kettleTweaks;

            @Config.Comment("Fix Alder Door rendering when disguised, as the rendering of the door does not reflect its state " +
                    "(for example it renders open while it is closed, etc.)")
            @Config.Name("Alder Door - Fix Disguised Alder Door Rendering")
            public static boolean alderDoor_fixDisguiseRendering = true;

            @Config.Comment("Fix Alder Door reacting to redstone. The Alder door should provide redstone power, but should be " +
                    "inert to it. Pressure plates, levers etc should never open/close the door, only right-clicking it")
            @Config.Name("Alder Door - Fix Redstone Behavior")
            public static boolean alderDoor_fixRedstoneBehavior = true;

            @Config.Comment("Fix Altar blocks requiring to get broken and re-placed to work properly again")
            @Config.Name("Altar - Fix Power Source Persistence")
            public static boolean altar_fixPowerSourcePersistency = true;

            @Config.Comment("If true, implements caching for Altar Power Source map, improving TPS." +
                    "NOTE: Required for CraftTweaker integration for Custom Power Sources")
            @Config.Name("Altar - Tweak Cache Power Source Map")
            public static boolean altar_tweakCachePowerMap = true;

            @Config.Comment("Fixes fetish blocks not dropping when they are harvested due to TileEntity being null " +
                    "when the getDrops method is called")
            @Config.Name("Block Fetish - Fix No Drops on Harvest")
            public static boolean blockFetish_fixNoDropsOnHarvest = true;

            @Config.Comment("If true, makes Bear Traps and Wolf Traps break if there is no block with a solid upper face beneath. " +
                    "Also applies to Wolf Traps")
            @Config.Name("Bear Trap - Tweak Surface Requirement")
            public static boolean bearTrap_tweakNeedsSolidSurface = false;

            @Config.Comment("Fixes fetish blocks resetting their data on world reload or when many blocks are " +
                    "updated at once, including Players bound to them")
            @Config.Name("Block Fetish - Fix Data Loss on World Reload")
            public static boolean blockFetish_fixMissingDataOnWorldReload = true;

            @Config.Comment("If true, fixes a crash involving mods trying to get the Circle Glyph blockstate from the " +
                    "Chalk metadata (for example, FutureMC bees)")
            @Config.Name("Circle Glyph - Fix Out Of Bounds Crash")
            public static boolean circleGlyph_fixOutOfBoundsCrash = true;

            @Config.Comment("Fix an edge case where the coffin would not have a color associated with it, causing a crash.")
            @Config.Name("Coffin - Fix Edge Case Crash")
            public static boolean coffin_fixEdgeCrash = true;

            @Config.Comment("Tightens bounding boxes around the Statue of Broken Curse and Statue of Occluded Summons.")
            @Config.Name("Creative Statues - Tweak Tighten Bounding Boxes")
            public static boolean creativeStatues_tweakTightenBoundingBoxes = true;

            @Config.Comment("Sets the Altar Power required for a Crystal Ball prediciton. Witchery default is 500.")
            @Config.Name("Crystal Ball - Tweak Required Power")
            @Config.RangeInt(min = 1, max = 10000)
            public static int crystalBall_tweakRequiredPower = 500;

            @Config.Comment("Sets the cooldown in ticks between each Crystal Ball usage. Witchery default is 100 (5 seconds).")
            @Config.Name("Crystal Ball - Tweak Cooldown")
            @Config.RangeInt(min = 1, max = 10000)
            public static int crystalBall_tweakCooldown = 100;

            @Config.Comment("Cursed Blocks are not fully implemented. If you enable this, they still won't work, but " +
                    "at least they should not crash the game as often")
            @Config.Name("Cursed Blocks - Fix Null Brew Action List Crash")
            public static boolean cursedBlock_fixNullActionListCrash = true;

            @Config.Comment("Fix crash when one of the coffin pieces is moved by a piston.")
            @Config.Name("Coffin - Fix Crash When Moved By Piston")
            public static boolean coffin_fixPistonMoveCrash = true;

            @Config.Comment("Fix the Garlic Garland having incorrect Bounding Boxes.")
            @Config.Name("Garlic Garland - Fix Bounding Box")
            public static boolean garlicGarland_fixBoundingBox = true;

            @Config.Comment("Fix the Garlic Garland being placed with the wrong facing upon placement.")
            @Config.Name("Garlic Garland - Fix Facing on Placement")
            public static boolean garlicGarland_fixFacingOnPlacement = true;

            @Config.Comment("Fixes a problem with the bounding box of the Statue of Goddess. Being two blocks high, when a " +
                    "player stands on top of it on a server it will be kicked for flying. If this config is true, the bounding " +
                    "box height will be reduced to 1.6, the maximum allowed without being kicked")
            @Config.Name("Statue of Goddess - Fix Flying On Servers")
            public static boolean goddessStatue_fixFlyingOnServers = true;

            @Config.Comment("If true, when the player holds any block and right-clicks the statue, it won't place the block")
            @Config.Name("Statue of Hobgoblin Patron - Fix Block Placing")
            public static boolean hobgoblinPatronStatue_fixBlockPlacing = true;

            @Config.Comment("Fixes a problem with rendering of the statue, where the skin of the bound player is visible " +
                    "without the overlay due to W:R using the original (no longer working) Witchery texture for the statue")
            @Config.Name("Statue of Hobgoblin Patron - Fix Rendering")
            public static boolean hobgoblinPatronStatue_fixRendering = true;

            @Config.Comment("If true, enables Crafttweaker integration for Kettle. Defaults to true, " +
                    "as if enabled and not used does not alter Witchery behaviour")
            @Config.Name("Kettle - Tweak Enable Crafttweaker Integration")
            public static boolean kettle_tweakCustomHeatSources = true;

            @Config.Comment("Fix brews getting thrown immediately upon right-clicking the Kettle with a single empty bottle.")
            @Config.Name("Kettle - Fix Brews Thrown Upon Creation")
            public static boolean kettle_fixThrowBrewsUponCreation = true;

            @Config.Comment("Fix true, it will prevent Mandrake entities from spawning when harvesting non-mature mandrake crops.")
            @Config.Name("Mandrake Crop - Fix Drop Even When Not Mature")
            public static boolean mandrakeCrop_fixMandrakeSpawningNotMature = true;

            @Config.Comment("Fixes the Mirror in Mirror (MiM) feature.")
            @Config.Name("Mirror - Fix MiM")
            public static boolean mirror_fixMirrorInMirror = true;

            @Config.Comment("Tweaks to modify the required power to use the Mirror in Mirror (MiM) feature (traverse the rooms " +
                    "inside of the Mirror dimension. Witchery default is 3000")
            @Config.Name("Mirror - Tweak MiM Power Requirement")
            @Config.RangeDouble(min = 1.0, max = 10000.0)
            public static float mirror_inMirrorPowerConsumption = 3000;

            @Config.Comment("Fix Perpetual Ice block having the same appearance of Vanilla Ice, making it have the same " +
                    "appearance as other Perpetual Ice blocks (fences, slabs, etc.).")
            @Config.Name("Perpetual Ice - Fix Inconsistent Appearance")
            public static boolean perpetualIce_fixInconsistentAppearance = true;

            @Config.Comment("Fix Perpetual Ice Stairs having translucent cut-out texture, giving it a solid renderLayer instead. " +
                    "Will fix X-Ray and will give the Stairs a consistent look with other Perpetual Ice blocks (fences, slabs, etc.).")
            @Config.Name("Perpetual Ice Stairs - Fix X-Ray")
            public static boolean perpetualIceStairs_fixXRay = true;

            @Config.Comment("Fix Arthana, Pentacle and other items placed on top of the altar not dropping when " +
                    "the altar block below them is broken.")
            @Config.Name("Placed Items - Fix No Drops")
            public static boolean placedItems_fixNoDrops = true;

            @Config.Comment("If true, fixes a crash the player joins a world where an Altar with placed items on top are " +
                    "in its view in the first rendered frame.")
            @Config.Name("Placed Items - Fix Not Initialized Crash")
            public static boolean placedItems_fixNotInitializedCrash = true;

            @Config.Comment("If true, fix popper rendering, displaying them the right way up")
            @Config.Name("Poppet Shelf - Fix Upside-down Poppets")
            public static boolean poppetShelf_fixUpsideDownPoppetRendering = true;

            @Config.Comment("If true, disables the chunkloading behaviour of Poppet Shelves, requiring external " +
                    "chunkloading to be active when a Player is not nearby")
            @Config.Name("Poppet Shelf - Tweak Disable Chunkloading")
            public static boolean poppetShelf_tweakDisableChunkloading = false;

            @Config.Comment("Workaround for Stockade blocks. When player head gets too close, the entire" +
                    "screen is rendered as a stockade side texture. This patch enlarges the stockade bounding" +
                    "box to make player head not get as close.")
            @Config.Name("Stockade - Fix Bounding Box")
            public static boolean stockade_fixBoundingBox = true;

            @Config.Comment("If true, fixes conditions for the portal to stay. If any blocks of the portal frame or portal itself " +
                    "is broken, the portal should be broken. Without this fix, some portal blocks stay lit regardless.")
            @Config.Name("Spirit Portal - Fix Break Condition")
            public static boolean spiritPortal_fixBreakCondition = true;

            @Config.Comment("If true, fixes the Spirit Portal block so that blocks that require support (buttons, levers, etc) " +
                    "cannot be placed on the surface of the portal.")
            @Config.Name("Spirit Portal - Fix Block Face Shape")
            public static boolean spiritPortal_fixBlockFaceShape = true;

            @Config.Comment("If true, fixes JEI Plugin compat for the Spinning Wheel.")
            @Config.Name("Spinning Wheel - Fix JEI Plugin")
            public static boolean spinningWheel_fixJeiPlugin = true;

            @Config.Comment("If true, fix player bottling skill increase, which won't happen otherwise.")
            @Config.Name("Witch's Cauldron - Fix Bottling Skill Increase")
            public static boolean witchsCauldron_fixBottlingSkillIncrease = true;

            @Config.Comment("If true, fix right-clicking on the cauldron with a bucket voiding its contents.")
            @Config.Name("Witch's Cauldron - Fix Bucket Voiding Brew")
            public static boolean witchsCauldron_fixBucketVoidingBrew = true;

            @Config.Comment("If true, fixes a dupe where the same item can be tossed into multiple adjacent cauldrons.")
            @Config.Name("Witch's Cauldron - Fix Multiple Cauldron Dupe")
            public static boolean witchsCauldron_fixMultipleCauldronDupe = true;

            @Config.Comment("If true, fixes an infinite water exploit that works by right-clicking with an empty " +
                    "bucket an empty cauldron, filling the bucket with water.")
            @Config.Name("Witch's Cauldron - Fix Unlimited Water")
            public static boolean witchsCauldron_fixUnlimitedWaterWhenEmpty = true;

            @Config.Comment("If true, enables Crafttweaker integration for Witch's Cauldron. Defaults to true, " +
                    "as if enabled and not used does not alter Witchery behaviour")
            @Config.Name("Witch's Cauldron - Tweak Enable Crafttweaker Integration")
            public static boolean witchsCauldron_tweakCustomHeatSources = true;

            @Config.Comment("If true, disables Forge capability system that other mods might add to fluid containers " +
                    "such as Glass Bottles, as they can create some incompatibilities. This disables such capability " +
                    "only for Witch's Cauldron, causing it to ignore the capability, rather than disabling it")
            @Config.Name("Witch's Cauldron - Tweak Disable Fluid Handler Capability")
            public static boolean witchsCauldron_tweakIgnoreFluidHandlers = false;

            @Config.Comment("If true, sets the glass bottle fluid size to 250 instead of the default 333/334 (depending " +
                    "if the action is filling or draining the cauldron). This makes numbers cleaner, as now 4 bottles " +
                    "make up a bucket. Recommended to set to true")
            @Config.Name("Witch's Cauldron - Tweak Smaller Bottle")
            public static boolean witchsCauldron_tweakSmallerBottle = false;

            @Config.Comment("If true, backports from 0.6 a fix to spawn burning particles at the correct height")
            @Config.Name("Witches Oven - Fix Burning Particles")
            public static boolean witchesOven_fixBurningParticlesHeight = true;

            @Config.Comment("If true, fixes fences connecting to the statue, and buttons and levers cannot be placed " +
                    "anymore on the statue")
            @Config.Name("Wolf Altar - Fix Face Shape")
            public static boolean wolfAltar_fixFaceShape = true;

            @Config.Comment("Fixes a problem with the bounding box of the Wolf Altar. Being two blocks high, when a " +
                    "player stands on top of it on a server it will be kicked for flying. If this config is true, the bounding " +
                    "box height will be reduced to 1.6, the maximum allowed without being kicked.")
            @Config.Name("Wolf Altar - Fix Flying On Servers")
            @Config.RequiresMcRestart
            public static boolean wolfAltar_fixFlyingOnServers = true;

            @Config.Comment("If true, when the Wolf Trap is activated, warns the player about approaching werewolves")
            @Config.Name("Wolf Trap - Tweak Warn Players")
            public static boolean wolfTrap_warnPlayers = false;

            @Config.Comment("If true, the Wolf Trap will drop itself when broken. It is a tweak, as Witchery intentionally " +
                    "makes the wolf trap impossible to relocate once placed")
            @Config.Name("Wolf Trap - Tweak Drop On Break")
            public static boolean wolfTrap_tweakDropOnBreak = false;

            public static class KettleTweaks {

                @Config.Comment("Bonus chance of having increased yield from kettle while wearing Witches Hat " +
                        "or (if enabled) an Construct Armory helmet with 'Witch Touch' Trait")
                @Config.Name("Witches Hat - Tweak Kettle Bonus Chance")
                public static double witchHat_tweakKettleBonus = 0.35;

                @Config.Comment("Bonus chance of having increased yield from kettle while wearing Baba Yaga's Hat " +
                        "or (if enabled) an Construct Armory helmet with 'Baba Yaga's Touch' Trait")
                @Config.Name("Witches Hat - Tweak Bonus Chance")
                public static double babaYagaHat_tweakKettleBonus = 0.25;

                @Config.Comment("Bonus chance of having increased yield from kettle while wearing Baba Yaga's Hat " +
                        "or (if enabled) an Construct Armory helmet with 'Baba Yaga's Touch' Trait")
                @Config.Name("Witches Hat - Tweak Second Bonus Chance")
                public static double babaYagaHat_tweakKettleBonus2 = 0.25;

                @Config.Comment("Bonus chance of having increased yield from kettle while wearing Witches Robes " +
                        "or (if enabled) an Construct Armory chestplate with 'Witch Touch' Trait")
                @Config.Name("Witches Robes - Tweak Kettle Bonus Chance")
                public static double witchRobes_tweakKettleBonus = 0.0;

                @Config.Comment("Bonus chance of having increased yield from kettle while wearing Necromancer's Robes " +
                        "or (if enabled) an Construct Armory helmet with 'Necrotic Touch' Trait")
                @Config.Name("Necromancer Robes - Tweak Kettle Bonus Chance")
                public static double necromancerRobes_tweakKettleBonus = 0.25;

                @Config.Comment("Bonus chance of having increased yield from kettle while having a Toad familiar")
                @Config.Name("Toad Familiar - Tweak Kettle Bonus Chance")
                public static double toadFamiliar_tweakKettleBonus = 0.05;

                @Config.Comment("Bonus chance of having increased yield from kettle while having a Toad familiar AND Baba's Hat")
                @Config.Name("Toad Familiar Baba Hat Enhancement - Tweak Kettle Bonus Chance")
                public static double toadFamiliarAndBabaHat_tweakKettleBonus = 0.05;
            }

        }

        public static class ItemTweaks {

            @Config.Comment("If true, allows CraftTweaker integration with Bark Belt")
            @Config.Name("Bark Belt - Tweak Enable Crafttweaker Integration")
            public static boolean barkBelt_tweakCraftTweakerIntegration = true;

            @Config.Comment("If true, Cane Sword will retain its damage when sheathing/unsheathing")
            @Config.Name("Cane Sword - Fix Preserve Damage on Unsheathing")
            public static boolean caneSword_fixDamageOnSheathe = true;

            @Config.Comment("If true, players will be able to use the Creative Medallion even if not in creative mode")
            @Config.Name("Creative Medallion - Tweak Disable Creative Requirement")
            public static boolean creativeMedallion_tweakDisableCreativeRequirement = false;

            @Config.Comment("If true, Spectral Stones won't dupe the entity they contain")
            @Config.Name("Spectral Stone - Fix Entity Dupe Exploit")
            public static boolean spectralStone_fixEntityReleaseExploit = true;

            @Config.Comment("If true, the Icy Needle can also be used in the air instead of having to be used on a block")
            @Config.Name("Icy Needle - Fix Right-Click in Air")
            public static boolean icyNeedle_fixRightClickInAir = true;

            @Config.Comment("If true, set max stack size of all chalk items to 1, regardless of damage. " +
                    "Workaround for some chalk-stacking related bugs")
            @Config.Name("Chalk - Tweak Unstackable Chalk")
            public static boolean itemChalk_tweakUnstackableChalk = false;

            @Config.Comment("If true, the Brew of Erosion item crafted in the kettle will behave exactly like the " +
                    "Brew of Erosion crafted into the Witch's Cauldron")
            @Config.Name("Brew of Erosion Item - Tweak Emulate Erosion Brew")
            public static boolean itemErosionBrew_tweakEmulateBrewEffects = false;

            @Config.Comment("If true, enables CraftTweaker integration for all types of Mutandis conversions")
            @Config.Name("Mutandis - Tweak Enable CraftTweaker Integration")
            public static boolean mutandis_tweakEnableCraftTweaker = true;

            @Config.Comment("If true, this poppet won't be able to affect players")
            @Config.Name("Poppet Item - Disable PVP")
            public static boolean poppetItem_tweakDisablePvP = false;

            @Config.Comment("If true, this poppet won't be able to affect non-player entities")
            @Config.Name("Poppet Item - Disable PVE")
            public static boolean poppetItem_tweakDisablePvE = false;

            @Config.Comment("If true, a tooltip will be added to all Poppets informing the players about what entities " +
                    "are affected by the poppet")
            @Config.Name("Poppet Item - Disable PVE")
            public static boolean poppetItem_tweakAddTargetRestrictionTooltip = false;

            @Config.Comment("If true, fixes a crash that happens when a Poppet Protection Poppet protects its owner")
            @Config.Name("Poppet Protection Poppet - Fix Crash on Protect")
            public static boolean poppetProtectionPoppet_fixCrashOnProtect = true;

            @Config.Comment("Represents the amount of damage that the poppet takes when it protects its owner from a " +
                    "curse. Default 350 (Vanilla Witchery value). Needs voodooProtectionPoppet_fixCrashOnCurseProtect=true to work")
            @Config.Name("Poppet Protection Poppet - Tweak Damage Taken on Protect")
            public static int poppetProtectionPoppet_tweakDamageTakenOnProtect = 350;

            @Config.Comment("If true, when the Seer Stone is shift-right-clicked Throwing Skills won't be printed, " +
                    "as it is a mechanic not yet implemented in Witchery: Resurrected")
            @Config.Name("Seer Stone - Tweak Unprint Throwing Skills")
            public static boolean seerStone_tweakUnprintThrowingSkill = false;

            @Config.Comment("If true, fixes a crash that happens when a Voodoo Protection Poppet protects its owner")
            @Config.Name("Voodoo Protection Poppet - Fix Crash on Protect")
            public static boolean voodooProtectionPoppet_fixCrashOnCurseProtect = true;

            @Config.Comment("Represents the amount of damage that the poppet takes when it protects its owner from a " +
                    "curse. Default 350 (Vanilla Witchery value). Needs voodooProtectionPoppet_fixCrashOnCurseProtect=true to work")
            @Config.Name("Voodoo Protection Poppet - Tweak Damage Taken on Protect")
            public static int voodooProtectionPoppet_tweakDamageTaken = 350;

        }

        public static class RitesTweaks {

            @Config.Comment("Multiplies the damage taken by a Voodoo Protection Poppet when the owner is protected by " +
                    "this ritual. Default: 6")
            @Config.Name("Rite of Blight - Tweak Strength")
            @Config.RangeInt(min = 1, max = 16)
            public static int blight_tweakStrength = 6;

            @Config.Comment("Multiplies the damage taken by a Voodoo Protection Poppet when the owner is protected by " +
                    "this ritual. Default: 6")
            @Config.Name("Rite of Blindness - Tweak Strength")
            @Config.RangeInt(min = 1, max = 16)
            public static int blindness_tweakStrength = 6;

            @Config.Comment("If true, fixes an edge case when it was not possible to determine the rite's foci's block location")
            @Config.Name("Rite of Broken Earth - Fix NPE on Null Foci Location")
            public static boolean brokenEarth_fixNPEOnNullFociLocation = true;

            @Config.Comment("If true, Rite of Broken Earth will only break things that can be broken with a Brew of " +
                    "Erosion, including (if enabled) CraftTweaker-defined blockstates")
            @Config.Name("Rite of Broken Earth - Tweak Align Breakables With Erosion")
            public static boolean brokenEarth_tweakAlignBreakablesWithErosion = false;

            @Config.Comment("If true, backports from 0.6 a bugfix for the Rite of Binding (Copy Waystone type) where a player " +
                    "tries to duplicate a bound waystone but the result of the rite is a bound waystone with no location data")
            @Config.Name("Rite of Binding (Copy Waystone) - Fix Result")
            public static boolean copyWaystone_fixResult = true;

            @Config.Comment("Multiplies the damage taken by a Voodoo Protection Poppet when the owner is protected by " +
                    "this ritual. Default: 1. Witchery applies a strength of 1, if the levelBuff is zero, 3 otherwise. " +
                    "This factor multiplies the value given by Witchery")
            @Config.Name("Rite Curse Creature - Tweak Strength")
            @Config.RangeInt(min = 1, max = 16)
            public static int curseCreature_tweakStrength = 1;

            @Config.Comment("Multiplies the damage taken by a Voodoo Protection Poppet when the owner is protected by " +
                    "this ritual. Default: 3")
            @Config.Name("Rite Curse of The Wolf - Tweak Strength")
            @Config.RangeInt(min = 1, max = 16)
            public static int curseOfTheWolf_tweakStrength = 3;

            @Config.Comment("If true, the Rite of Moving Earth disables moving TileEntities, preventing crashes, bugs and dupes")
            @Config.Name("Rite of Moving Earth - Fix Crash/Dupes while Moving TileEntities")
            public static boolean movingEarth_tweakDisableMovingTEs = true;

            @Config.Comment("If true, the Rite of Moving Earth won't shift blocks upwards if there are obstructions. This will prevent voiding blocks")
            @Config.Name("Rite of Moving Earth - Fix Destroying Blocks")
            public static boolean movingEarth_tweakDisableVoidingBlocks = true;

            @Config.Comment("Set the Ritual of Moving Earth refund policy. Below, the valid values:\n" +
                    "0: never refound the player (default Witchery Behavior)\n" +
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

            @Config.Comment("If true, when player opens a container the Witchery-added NBT for rite of Prior " +
                    "Incarnation is removed, as it wouldn't be of any use regardless")
            @Config.Name("Rite of Prior Incarnation - Fix NBT Persisting After Pickup")
            @Config.RequiresMcRestart
            public static boolean ritePriorIncarnation_fixNbtNotRemoved = true;
        }

        public static class PotionTweaks {

            @Config.Comment("If true, fixes entities not moving after they have been spawned")
            @Config.Name("Insanity Potion - Fix Ghost Entities")
            public static boolean insanity_fixGhostEntities = true;

            @Config.Comment("Fixes Potion of Fortune not working because of wrong TileEntity check")
            @Config.Name("Fortune Potion - Fix No Effect")
            public static boolean fortunePotion_fixNoEffect = true;

            @Config.Comment("Fixes Potion of Resizing not working on players")
            @Config.Name("Resizing Potion - Fix No Effect On Players")
            public static boolean resizing_fixEffectOnPlayers = true;

            @Config.Comment("If true, enables custom sizes for the Resizing Potion. Values are defined in this config. " +
                    "NOTE 1: Enabling this will already chance Witchery default sizes, as they are determined by a formula." +
                    "NOTE 2: If this value is set to below ~0.42f, the players can x-ray through blocks they are directly " +
                    "touching and looking towards. Setting values to below this threshold is not recommended." +
                    "NOTE 3: If this value is set to ~10.0f or higher, the camera will clip into the player's head")
            @Config.Name("Resizing Potion - Tweak Custom Sizes")
            public static boolean resizing_tweakCustomSizes = false;

            @Config.Comment("Sets the custom scale for the Smallest size of the resizing potion")
            @Config.Name("Resizing Potion - Tweak Size Smallest")
            public static float resizing_tweakCustomSizeSmallest = 0.5f;

            @Config.Comment("Sets the custom scale for the Smaller size of the resizing potion")
            @Config.Name("Resizing Potion - Tweak Size Smaller")
            public static float resizing_tweakCustomSizeSmaller = 0.75f;

            @Config.Comment("Sets the custom scale for the Bigger size of the resizing potion")
            @Config.Name("Resizing Potion - Tweak Size Bigger")
            public static float resizing_tweakCustomSizeBigger = 1.5f;

            @Config.Comment("Sets the custom scale for the Biggest size of the resizing potion")
            @Config.Name("Resizing Potion - Tweak Size Biggest")
            public static float resizing_tweakCustomSizeBiggest = 2.0f;
        }

        public static class EntityTweaks {

            @Config.Comment("If true, enables all the Baba Yaga tweaks")
            @Config.Name("Baba Yaga - Tweak Enable Custom Behavior")
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

            @Config.Comment("If true, prevents Banshees from attacking other Banshees, making the execution of some " +
                    "rituals easier as they won't kill each other")
            @Config.Name("Banshee - Tweak Ingore Other Banshees")
            public static boolean banshee_tweakDoNotAttackOtherBanshees = false;

            @Config.Comment("If true, fixes the problem where Coven Witches require certain amount of items as a quest, but if" +
                    "the player holds more items than needed, the quest goes into negative item amount requirement")
            @Config.Name("Coven Witch - Fix Negative Request Amount")
            public static boolean covenWitch_fixNegativeRequestAmount = true;

            @Config.Comment("When the game mode is set to Peaceful, some quests cannot be completed. To avoid this, set this number to " +
                    "a value greater than zero, as it represents the number of times the quest can be re-rolled. More attempts means less performance, " +
                    "less attempts means that from time to time a Fight quest can happen in peaceful")
            @Config.Name("Coven Witch - Tweak Fight Quests Peaceful Rerolls")
            @Config.RangeInt(min = 0, max = 10)
            public static int covenWitch_tweakFightQuestsPeacefulRerolls = 0;

            @Config.Comment("Sets the total lifespan (in ticks) of a Duplicate entity (spawned by Duplication Grenades). " +
                    "Default Witchery is 200 ticks (10 seconds)")
            @Config.RangeInt(min = 1, max = 10000000)
            public static int duplicate_tweakTickLifespan = 200;

            @Config.Comment("If true, fixes a freeze when the Broom breaks due to not dismounting passengers")
            @Config.Name("Enchanted Broom - Fix Freeze On Break")
            public static boolean enchantedBroom_fixFreezeOnBreak = true;

            @Config.Comment("If true, makes it so that when a Fairest is spawned, it always has a valid texture")
            @Config.Name("Fairest - Fix Broken Textures")
            public static boolean fairest_fixBrokenTextures = true;

            @Config.Comment("Sets the max number of trades the Goblin can have per level. On initial spawn, and when " +
                    "the player consumes all the Goblin trades, the Goblin will have this number of new merchant recipes")
            @Config.Name("Goblin - Max Trades Per Level")
            public static int goblin_maxTradesPerLevel = 1;

            @Config.Comment("If true, Goblin trades can be customized with CraftTweaker. False by default, because " +
                    "when enabled it completely wipes the Witchery goblin trade tables, and if new trades are not added " +
                    "with CrT, then Goblins won't show any trades")
            @Config.Name("Goblin - Tweak Custom Trades")
            public static boolean goblin_tweakCustomTrades = false;

            @Config.Comment("If true, Goblins no longer have to be in a Village in order to have trades. " +
                    "They spawn, they trade")
            @Config.Name("Goblin - Remove Village Requirement")
            public static boolean goblin_tweakRemoveTradingVillageRequirements = false;

            @Config.Comment("Fix players losing Cat familiars on World reload")
            @Config.Name("Cat Familiar - Fix Owner on World Reload")
            @Config.RequiresMcRestart
            public static boolean familiarCat_fixOwnerDisconnect = true;

            @Config.Comment("Fix players losing Owl familiars on World reload")
            @Config.Name("Owl Familiar - Fix Owner on World Reload")
            @Config.RequiresMcRestart
            public static boolean familiarOwl_fixOwnerDisconnect = true;

            @Config.Comment("Fix players losing Toad familiars on World reload")
            @Config.Name("Toad Familiar - Fix Owner on World Reload")
            @Config.RequiresMcRestart
            public static boolean familiarToad_fixOwnerDisconnect = true;

            @Config.Comment("If true, Infernal Imp won't consume shiny items when it is on cooldown. Otherwise, items " +
                    "given to it will be wasted, as they have no effect")
            @Config.Name("Flame Imp - Tweak Item Consumption On Cooldown")
            public static boolean flameImp_tweakItemConsumptionOnCooldown = true;

            @Config.Comment("If true, Infernal Imp shinies list can be customized with CraftTweaker. Default true, as it " +
                    "does not alter behaviour if not customized. Shinies are also ItemStacks instead of Items, thus " +
                    "they will respect metadata and NBT")
            @Config.Name("Flame Imp - Tweak Custom Shinies")
            public static boolean flameImp_tweakCustomShinies = true;

            @Config.Comment("If true, Infernal Imp gift list can be customized with CraftTweaker. Default true, as it " +
                    "does not alter behaviour if not customized")
            @Config.Name("Flame Imp - Tweak Custom Gifts")
            public static boolean flameImp_tweakCustomGifts = true;

            @Config.Comment("If true, when custom gifts are added by Crafttweaker, if a gift is not set in script it " +
                    "generates a random one from the loot table. If even the loot table generates no gift, behaviour is " +
                    "defined by flameinfernalImp_tweakCustomGiftFallback")
            @Config.Name("Flame Imp - Tweak Custom Extra Items")
            public static boolean flameImp_tweakCustomExtraItems = true;

            @Config.Comment("If true, when custom gifts are added by Crafttweaker, if a gift is not set in script nor " +
                    "in loot table, then fall back to the item that Witchery would have given. If false, the success " +
                    "message will be written in chat, but no items will be given")
            @Config.Name("Flame Imp - Tweak Custom Gifts Fallback")
            public static boolean flameinfernalImp_tweakCustomGiftFallback = true;

            @Config.RangeInt(min = 1, max = 24000)
            @Config.Comment("Sets the minimum amount of time (in ticks) before Infernal Imps will give another gift")
            @Config.Name("Flame Imp - Tweak Gift Delay")
            public static int flameImp_tweakGiftDelayTicks = 3600;

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

            @Config.Comment("If true, Spectre Attributes will be modified according to this config file")
            @Config.Name("Spectre - Tweak Modify Attributes")
            public static boolean spectre_tweakAttributes = false;

            @Config.Comment("If true, Owl children will be smaller than adult counterparts")
            @Config.Name("Spectre - Tweak Attribute Follow range")
            public static float spectre_tweakFollowRange = 40.0f;

            @Config.Comment("If true, Owl children will be smaller than adult counterparts")
            @Config.Name("Spectre - Tweak Attribute Movement Speed")
            public static float spectre_tweakMovementSpeed = 0.4f;

            @Config.Comment("If true, Owl children will be smaller than adult counterparts")
            @Config.Name("Spectre - Tweak Attribute Attack Damage")
            public static float spectre_tweakAttackDamage = 4.0f;

            @Config.Comment("If true, Spectre will have a minimum lifetime, and won't despawn immediately")
            @Config.Name("Spectre - Tweak Delay Before Despawn")
            public static boolean spectre_tweakDelayBeforeDespawn = false;

            @Config.Comment("When the Spectre has no attack target (or it's dead) the Spectre waits this amount " +
                    "of ticks before despawning")
            @Config.Name("Spectre - Tweak Tick Delay Before Despawn")
            public static int spectre_tweakDelayTicksBeforeDespawn = 60;

            @Config.Comment("If true, Treefyd will require to be given a Creeper Heart before they can be given a Demon " +
                    "Heart, which further improves their strength and health")
            @Config.Name("Treefyd - Tweak Overhaul Upgrades")
            public static boolean treefyd_tweakOverhaulUpgrades = false;

            @Config.Comment("Tweaks the speed of Treefyd when it's not boosted. Defaults to 0.25")
            @Config.Name("Treefyd - Tweak Speed Unboosted")
            @Config.RangeDouble()
            @Config.RequiresMcRestart
            public static double treefyd_tweakSpeedUnboosted = 0.25;

            @Config.Comment("Tweaks the health of Treefyd when it's not boosted. Defaults to 50.0")
            @Config.Name("Treefyd - Tweak Health Unboosted")
            @Config.RangeDouble()
            @Config.RequiresMcRestart
            public static double treefyd_tweakHealthUnboosted = 50.0;

            @Config.Comment("Tweaks the damage of Treefyd when it's not boosted. Defaults to 3.0")
            @Config.Name("Treefyd - Tweak Damage Unboosted")
            @Config.RangeDouble(min = 0.1, max = 100.0)
            @Config.RequiresMcRestart
            public static double treefyd_tweakDamageUnboosted = 3.0;

            @Config.Comment("Tweaks the health of Treefyd when given a Creeper Heart. Defaults to 100")
            @Config.Name("Treefyd - Tweak Health Creeper Heart")
            @Config.RangeDouble(min = 1, max = 1000)
            public static double treefyd_tweakHealthWithCreeperHeart = 100.0;

            @Config.Comment("Tweaks the damage of Treefyd when given a Creeper Heart. Defaults to 4.0")
            @Config.Name("Treefyd - Tweak Damage Creeper Heart")
            @Config.RangeDouble(min = 0.1, max = 100.0)
            public static double treefyd_tweakDamageWithCreeperHeart = 4.0;

            @Config.Comment("Tweaks the health of Treefyd when given a Demon Heart. Defaults to 150")
            @Config.Name("Treefyd - Tweak Health Demon Heart")
            @Config.RangeDouble(min = 1, max = 1000)
            public static double treefyd_tweakHealthWithDemonHeart = 150.0;

            @Config.Comment("Tweaks the damage of Treefyd when given a Demon Heart. Defaults to 5.0")
            @Config.Name("Treefyd - Tweak Damage Demon Heart")
            @Config.RangeDouble(min = 0.1, max = 100.0)
            public static double treefyd_tweakDamageWithDemonHeart = 5.0;

            @Config.Comment("If true, removes Witchery: Resurrected Back-ported AI for Villagers")
            @Config.Name("Villager - Tweak Remove Backported AI")
            @Config.RequiresMcRestart
            public static boolean villager_disableBackportedAI = false;
        }

        public static class BookTweaks {

            @Config.Comment("If true, tries to fix the placement of the plant rendering in the Herbology Book")
            @Config.Name("Herbology Book - Fix Plant Rendering")
            public static boolean herbologyBook_fixPlantRendering = true;
        }

        public static class LootTweaks {

            @Config.Comment("Attack Bat will drop loot according to its Loot Table (witchery:entities/attack_bat)")
            @Config.Name("Attack Bat - Tweak Give Own Loot Table")
            public static boolean attackBat_tweakOwnLootTable = false;

            @Config.Comment("Baba Yaga will drop loot according to its Loot Table (witchery:entities/baba_yaga_death)")
            @Config.Name("Baba Yaga - Tweak Drop Loot by Table")
            public static boolean babaYaga_tweakLootTable = false;

            @Config.Comment("Baba Yaga will give loot to its owner based on a Loot Table (witchery:entities/baba_yaga_owner)")
            @Config.Name("Baba Yaga - Tweak Give By Loot Table")
            public static boolean babaYaga_tweakGiveDropLootTable = false;

            @Config.Comment("Banshee will give loot to its owner based on a Loot Table (witchery:entities/banshee)")
            @Config.Name("Banshee - Tweak Give Own Loot Table")
            public static boolean banshee_tweakLootTable = false;

            @Config.Comment("If true, Coven Witch will drop loot according to its own Loot Table, instead of " +
                    "Vanilla Witch loot table (witchery:entities/coven_witch). WARN: if true, loot added by other " +
                    "mods to vanilla Witch loot table, will not reflect on Coven Witches")
            @Config.Name("Coven Witch - Tweak Give Own Loot Table")
            public static boolean covenWitch_tweakOwnLootTable = false;

            @Config.Comment("If true, Demon will drop loot according to its Loot Table (witchery:entities/demon)")
            @Config.Name("Demon - Tweak Drop Loot by Table")
            public static boolean demon_tweakLootTable = false;

            @Config.Comment("If true, Duplicate will drop loot according to its Loot Table (witchery:entities/demon)")
            @Config.Name("Duplicate - Tweak Give Own Loot Table")
            public static boolean duplicate_tweakOwnLootTable = false;

            @Config.Comment("If true, Ent will drop loot according to its Loot Table (witchery:entities/ent)")
            @Config.Name("Ent - Tweak Drop Loot by Table")
            public static boolean ent_tweakLootTable = false;

            @Config.Comment("If true, Elle will drop loot according to its Loot Table (witchery:entities/elle)")
            @Config.Name("Elle - Tweak Drop Loot by Table")
            public static boolean elle_tweakLootTable = false;

            @Config.Comment("If true, Fairest will drop loot according to its Loot Table (witchery:entities/fairest)")
            @Config.Name("Fairest - Tweak Drop Loot by Table")
            public static boolean fairest_tweakLootTable = false;

            @Config.Comment("If true, Cat Familiar will drop loot according to its own Loot Table, instead of " +
                    "Vanilla Ocelot loot table (witchery:entities/coven_witch). WARN: if true, loot added by other " +
                    "mods to vanilla Ocelot loot table, will not reflect on Cat familiars")
            @Config.Name("Cat Familiar - Tweak Give Own Loot Table")
            public static boolean familiarCat_tweakOwnLootTable = false;

            @Config.Comment("If true, Imp will drop loot according to its Loot Table (witchery:entities/imp_death)")
            @Config.Name("Infernal Imp - Tweak Drop Loot By Table")
            public static boolean infernalImp_tweakLootTable = false;

            @Config.Comment("If true, Hobgoblins will drop loot according to its Loot Table (witchery:entities/hobgoblin)")
            @Config.Name("Hobgoblin - Tweak Own Loot By Table")
            public static boolean hobgoblin_tweakLootTable = false;

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

            @Config.Comment("If true, Spectre will drop loot according to its Loot Table (witchery:entities/spectre)")
            @Config.Name("Spectre - Tweak Drop Loot by Table")
            public static boolean spectre_tweakLootTable = false;

            @Config.Comment("If true, Treefyd will drop loot according to its Loot Table (witchery:entities/treefyd)")
            @Config.Name("Treefyd - Tweak Drop Loot by Table")
            public static boolean treefyd_tweakOwnLootTable = false;
        }

        public static class DimensionTweaks {

            @Config.Comment("Fixes endless Nightmares spawning around players in Spirit World")
            @Config.Name("Spirit World - Fix Endless Nightmare Spawning")
            public static boolean spiritWorld_fixNightmareSpawning = true;

            @Config.Comment("Tweaks the maximum amount of Nightmares that can spawn around a player in the Spirit World. " +
                    "The cap is computed per-player, so more players will actually make the cap increase proportionally." +
                    "NOTE: requires spiritWorld_fixNightmareSpawning set to true, otherwise this does nothing")
            @Config.Name("Spirit World - Tweak Nightmare Spawn Cap")
            @Config.RangeInt(min = 0, max = 20)
            public static int spiritWorld_tweakNightmareSpawnCap = 1;

            @Config.Comment("By default, Witchery won't spawn Nightmares if the player has killed one recently. This " +
                    "tweak allows customizing such delay, expressed in ticks. Default value is 600 ticks")
            @Config.Name("Spirit World - Tweak Nightmare Spawn Cooldown")
            @Config.RangeInt(min = 0, max = 6000)
            public static int spiritWorld_tweakNightmareSpawnCooldown = 600;

            @Config.Comment("Sets the dimension ID for the Spirit World dimension")
            @Config.Name("Spirit World - Tweak Dimension ID")
            @Config.RequiresMcRestart
            public static int spiritWorld_tweakID = 11;

            @Config.Comment("Sets the dimension ID for the Torment dimension")
            @Config.Name("Torment - Tweak Dimension ID")
            @Config.RequiresMcRestart
            public static int torment_tweakID = 12;

            @Config.Comment("Sets the dimension ID for the Mirror dimension")
            @Config.Name("Mirror - Tweak Dimension ID")
            @Config.RequiresMcRestart
            public static int mirror_tweakID = 13;


        }

        public static class TransformationTweaks {

            @Config.Comment("If true, fixes default step height for when the player has no active transformations. Default " +
                    "step height is 0.6 for all entities, including players, but Witchery sets it to 0.5. Enable this to set " +
                    "it back to 0.6.")
            @Config.Name("No Form - Fix Default Step Height")
            public static boolean noForm_fixDefaultStepHeight = true;

            @Config.Comment("If true, a level 6 or higher werewolf without a Moon Charm will transform into WOLFMAN form instead " +
                    "of WOLF form. Lesser levels will transform into WOLF form regardless")
            @Config.Name("Werewolf - Tweak Transform to Wolfman")
            public static boolean werewolf_tweakTransformToWolfman = false;

        }

        public static class WorldGenTweaks {

            @Config.Comment("If true, fixes a crash that happens while trying to generate an Item Frame entity " +
                    "with a book inside in the Witchery village piece")
            @Config.Name("Item Frame - Fix Crash While Generating Book In Village")
            public static boolean frameWithBook_fixCrashOnVillageGen = true;

            @Config.Comment("If true, fixes an Out of Memory crash when Cubic Chunks is installed")
            @Config.Name("Chunk Points of Interest - Fix Crash with Cubic Chunks")
            public static boolean chunkPOI_fixCubicChunksIncompat = true;

        }
    }

    public static class IntegrationConfigurations {

        @Config.Comment("Configuration related to Botania integration")
        @Config.Name("Baubles Integration - Configuration")
        public static BaublesIntegration baublesIntegrationConfig;

        @Config.Comment("Configuration related to Just Enough Resources integration")
        @Config.Name("JER Integration - Configuration")
        public static JerIntegration jerIntegrationConfig;

        @Config.Comment("Configuration related to Just Enough Items integration")
        @Config.Name("JEI Integration - Configuration")
        public static JeiIntegration jeiIntegrationConfig;

        @Config.Comment("Configuration related to Morph integration")
        @Config.Name("Morph Integration - Configuration")
        public static MorphIntegration morphIntegrationConfig;

        @Config.Comment("Configuration related to Quark integration")
        @Config.Name("Quark Integration - Configuration")
        public static QuarkIntegration quarkIntegrationConfig;

        @Config.Comment("Configuration related to Patchouli integration")
        @Config.Name("Patchouli Integration - Configuration")
        public static PatchouliIntegration patchouliIntegrationConfig;

        @Config.Comment("Configuration related to The One Probe integration")
        @Config.Name("TOP Integration - Configuration")
        public static TopIntegration TOPIntegrationConfig;

        @Config.Comment("Configuration related to Thaumcraft integration")
        @Config.Name("Thaumcraft Integration - Configuration")
        public static ThaumcraftIntegration ThaumcraftIntegrationConfig;


        public static class BaublesIntegration {

            @Config.Comment("Fixes a crash when Baubles is installed and Botania is not, the player is a Vampire and dies")
            @Config.Name("Botania Integration - Fix Crash On Vampire Death")
            @Config.RequiresMcRestart
            public static boolean fixCrashOnVampireDeath = true;
        }

        public static class JerIntegration {

            @Config.Comment("Master switch for all JER integrations")
            @Config.Name("JER Integration - Enabled")
            public static boolean enableJerIntegration = true;
        }

        public static class JeiIntegration {

            @Config.Comment("If true, enables Altar JEI Integration")
            @Config.Name("JEI Integration - Altar")
            public static boolean enableJeiAltar = true;

            @Config.Comment("If true, enables Bark Belt JEI Integration")
            @Config.Name("JEI Integration - Enable Bark Belt")
            public static boolean enableJeiBarkBelt = true;

            @Config.Comment("If true, enables Goblin Trade JEI Integration")
            @Config.Name("JEI Integration - Enable Goblin Trades")
            @Config.RequiresMcRestart
            public static boolean enableJeiGoblinTrades = true;

            @Config.Comment("If true, enables Imp Gifts JEI Integration")
            @Config.Name("JEI Integration - Enable Imp Gifts")
            @Config.RequiresMcRestart
            public static boolean enableJeiImpGifts = true;

            @Config.Comment("If true, enables Imp Shinies JEI Integration")
            @Config.Name("JEI Integration - Enable Imp Shinies")
            public static boolean enableJeiImpShinies = true;

            @Config.Comment("If true, enables Mirror JEI Integration")
            @Config.Name("JEI Integration - Enable Mirror Integration")
            public static boolean enableJeiMirror = true;

            @Config.Comment("If true, enables Mutandis (on Blocks) JEI Integration")
            @Config.Name("JEI Integration - Enable Mutandis-on-Block Integration")
            public static boolean enableJeiMutandisBlock = true;

            @Config.Comment("If true, enables Mutandis (on Plants) JEI Integration")
            @Config.Name("JEI Integration - Enable Mutandis-on-Plant Integration")
            public static boolean enableJeiMutandisPlant = true;

            @Config.Comment("If true, enables Sun Collector JEI Integration")
            @Config.Name("JEI Integration - Enable Sun Collector")
            public static boolean enableJeiSunCollector = true;

        }

        public static class QuarkIntegration {

            @Config.Comment("If true, fixes Mandrakes not spawning when Quark right-click harvest feature is enabled")
            @Config.Name("Quark Integration - Fix Mandrakes Right-click Harvest")
            public static boolean fixMandrakesRightClickHarvest = true;
        }

        public static class MorphIntegration {

            @Config.Comment("If true, fixes an entity size desync between client and server when a morphed player " +
                    "changes dimension")
            @Config.Name("Morph Integration - Fix Entity Size On Dimension Change")
            public static boolean fixSizeDesyncOnDimChange = true;
        }

        public static class TopIntegration {

            @Config.Comment("Master switch for all TOP integrations")
            @Config.Name("TOP Integration - Enabled")
            public static boolean enableTopIntegration = true;

            @Config.Comment("Integration")
            @Config.Name("TOP Integration - Altar")
            public static EProbeElementIntegrationConfig altar = EProbeElementIntegrationConfig.DEFAULT;

            @Config.Comment("If true, enables TOP integration for Witch's Cauldron")
            @Config.Name("TOP Integration - Witch's Cauldron")
            public static EProbeElementIntegrationConfig cauldron = EProbeElementIntegrationConfig.DEFAULT;

            @Config.Comment("If true, enables TOP integration for Crystal Ball")
            @Config.Name("TOP Integration - Crystal Ball")
            public static EProbeElementIntegrationConfig crystalBall = EProbeElementIntegrationConfig.DEFAULT;

            @Config.Comment("If true, enables TOP integration for Grassper")
            @Config.Name("TOP Integration - Grassper")
            public static EProbeElementIntegrationConfig grassper = EProbeElementIntegrationConfig.DEFAULT;

            @Config.Comment("If true, enables TOP integration for Kettle")
            @Config.Name("TOP Integration - Kettle")
            public static EProbeElementIntegrationConfig kettle = EProbeElementIntegrationConfig.DEFAULT;

            @Config.Comment("If true, enables TOP integration for Statue of Hobgoblin Patron")
            @Config.Name("TOP Integration - Statue of Hobgoblin Patron")
            public static EProbeElementIntegrationConfig statueOfHobgoblinPatron = EProbeElementIntegrationConfig.DEFAULT;

            @Config.Comment("If true, enables TOP integration for Sun Collector")
            @Config.Name("TOP Integration - Sun Collector")
            public static EProbeElementIntegrationConfig sunCollector = EProbeElementIntegrationConfig.DEFAULT;

            @Config.Comment("If true, enables TOP integration for Hobgoblin")
            @Config.Name("TOP Integration - Hobgoblin")
            public static EProbeElementIntegrationConfig goblin = EProbeElementIntegrationConfig.DEFAULT;

            @Config.Comment("If true, enables TOP integration for Flame Imp")
            @Config.Name("TOP Integration - Flame Imp")
            public static EProbeElementIntegrationConfig imp = EProbeElementIntegrationConfig.DEFAULT;

            @Config.Comment("If true, enables TOP integration for Treefyd")
            @Config.Name("TOP Integration - Treefyd")
            public static EProbeElementIntegrationConfig treefyd = EProbeElementIntegrationConfig.DEFAULT;

            public enum EProbeElementIntegrationConfig {
                // Only enable basic information
                BASIC_ONLY,

                // Basic information will always be available, extended info only with extended probe
                DEFAULT,

                // All information will always be available
                ALL_BASIC,

                // Integration is disabled
                DISABLED,
            }

        }

        public static class ThaumcraftIntegration {

            @Config.Comment("Master switch for all Thaumcraft integrations")
            @Config.Name("Thaumcraft Integration - Enabled")
            public static boolean enableThaumcraftIntegration = true;

        }

        public static class PatchouliIntegration {

            public static Flags flags;

            public static class Flags {

                @Config.Comment("If true, shows a page in the Altar section detailing the effects of the Infinity Egg (Creative Item)")
                @Config.Name("Altar - Show Infinity Booster")
                public static boolean altar_enableInfinity = false;

                @Config.Comment("If true, shows more info for Bottling Skill and Expertise")
                @Config.Name("Brewing - Expertise Extension")
                public static boolean brewing_enableExpertiseExtension = false;

                @Config.Comment("If true, shows ritual details in the Brew book")
                @Config.Name("Brewing - Ritual Details Extension")
                public static boolean brewing_enableRitualsExtension = false;

                @Config.Comment("If true, shows which Capacity ingredients also remove ceiling, allowing for more powerful potions")
                @Config.Name("Brewing - Reveal Remove Ceiling")
                public static boolean brewing_revealRemoveCeiling = false; // Not a flag, used by CauldronCapacityProcessor

                @Config.Comment("If true, shows how much power a Cauldron recipe requires")
                @Config.Name("Brewing - Show Required Power")
                public static boolean brewing_showRequiredPower = false;

                @Config.Comment("If true, shows more information about Instant, Liquid and Gas dispersals, as Witchery does for Triggered dispersal")
                @Config.Name("Brewing - Dispersal Details Extension")
                public static boolean brewing_extendedDispersal = false;

                @Config.Comment("If true, shows an additional page for Inferno Brew Effect detailing how to summon a Demon")
                @Config.Name("Brew Effects - Inferno Details Extension")
                public static boolean brewEffects_infernoExtendedDetails = false;

                @Config.Comment("If true, the Blight effect will show in the Brew Effects List")
                @Config.Name("Brew Effects - Show Blight")
                public static boolean brewEffects_showBlight = false;

                @Config.Comment("If true, the Floating effect will show in the Brew Effects List")
                @Config.Name("Brew Effects - Show Floating")
                public static boolean brewEffects_showFloating = false;

                @Config.Comment("If true, the Healing effect will show in the Brew Effects List")
                @Config.Name("Brew Effects - Show Healing")
                public static boolean brewEffects_showHealing = false;

                @Config.Comment("If true, the Lava Hold effect will show in the Brew Effects List")
                @Config.Name("Brew Effects - Show Lava Hold")
                public static boolean brewEffects_showLavaHold = false;

                @Config.Comment("If true, the Decanting effect will show in the Brew Effects List")
                @Config.Name("Brew Effects - Show Decanting")
                public static boolean brewEffects_showDecanting = false;

                @Config.Comment("If true, the Raise Land effect will show in the Brew Effects List")
                @Config.Name("Brew Effects - Show Raise Land")
                public static boolean brewEffects_showRaiseLand = false;

                @Config.Comment("If true, the Repell Attacker effect will show in the Brew Effects List")
                @Config.Name("Brew Effects - Show Repell Attacker")
                public static boolean brewEffects_showRepellAttacker = false;

                @Config.Comment("If true, the Weaken Vampires effect will show in the Brew Effects List")
                @Config.Name("Brew Effects - Show Weaken Vampires")
                public static boolean brewEffects_showWeakenVampires = false;

                @Config.Comment("If true, shows an additional page for the Rite of Shifting Seasons listing the Foci")
                @Config.Name("Circle Magic - Show Shifting Sesons Foci")
                public static boolean circleMagic_showShiftingSeasonsFoci = false;

                @Config.Comment("If true, adds a few more pages about how Brazier works")
                @Config.Name("Conjuring - Enable Extended Intro")
                public static boolean conjuring_enableExtendedIntro = false;

                @Config.Comment("Some Brazier Summoning Recipes can summon an extra entity. Set this to true to show this info in the manual")
                @Config.Name("Conjuring - Show Extra Entity")
                public static boolean conjuring_showExtraEntity = false;

                @Config.Comment("If true, adds a few pages explaining what blocks are ideal to be bound to spectral entities and their recipes")
                @Config.Name("Fetish - Details Extension")
                public static boolean conjuring_enableFetishExtension = false;

                @Config.Comment("If true, adds some pages that vaguely explain the predictions that the Crystal Ball might foresee")
                @Config.Name("Constructs - Show Crystal Ball Predictions")
                public static boolean constructs_revealCrystalBallPredictions = false;

                @Config.Comment("If true, adds a new entry that explains Fume Funnels and their role in upgrading the Witches' Oven")
                @Config.Name("Fumes - Explain Funnels")
                public static boolean fumes_enableFunnels = false;

                @Config.Comment("If true, adds an entry to the Observations chapter, adding new lore to the Werewolf transformation in a similar way " +
                        "to how Witchery does for Vampirism (in Observations of an Immortal). Content is original.")
                @Config.Name("Observations - Add Observations of a Lycanthrope")
                public static boolean observations_enableLycanthropyLore = false;

                @Config.Comment("If true, adds an entry to the Lycanthropy section in Observations chapter, with an introduction on lycanthropy in general.")
                @Config.Name("Observations - Add Lycanthropy Intro")
                public static boolean observations_enableLycanthropyIntro = false;

                @Config.Comment("If true, adds an entry to the Observations chapter, documenting the progression of becoming a werewolf. Each page will be " +
                        "unlocked after reaching a higher level in lycanthropy.")
                @Config.Name("Observations - Add Lycanthropy Progress")
                public static boolean observations_enableLycanthropyProgress = false;

                @Config.Comment("If true, adds an entry to the Vampirism section in Observations chapter, with an introduction on vampirism in general.")
                @Config.Name("Observations - Add Vampirism Intro")
                public static boolean observations_enableVampirismIntro = false;

                @Config.Comment("If true, adds an entry to the Observations chapter, documenting the progression of becoming a vampire. Each page will be " +
                        "unlocked after reaching a higher level in vampirism.")
                @Config.Name("Observations - Add Vampirism Progress")
                public static boolean observations_enableVampirismProgress = false;

                @Config.Comment("If true, adds a page regarding the Statue of The Goddess. Being considered an almost-creative item, some pack makers " +
                        "might want to disable its recipe or hide it altogether. Keep this flag to false if it's the case.")
                @Config.Name("Statues - Show Statue of The Goddess")
                public static boolean statues_showGoddess = false;

                @Config.Comment("If true, adds a page regarding the Statue of Broken Curses. Being considered an almost-creative item, some pack makers " +
                        "might want to disable its recipe or hide it altogether. Keep this flag to false if it's the case.")
                @Config.Name("Statues - Show Statue of Broken Curses")
                public static boolean statues_showBrokenCurses = false;

                @Config.Comment("If true, adds a page regarding the Statue of Occluded Summons. Being considered an almost-creative item, some pack makers " +
                        "might want to disable its recipe or hide it altogether. Keep this flag to false if it's the case.")
                @Config.Name("Statues - Show Statue of Broken Curses")
                public static boolean statues_showOccludedSummons = false;

                @Config.Comment("If true, shows more information about how to get into symbology and how it works in general")
                @Config.Name("Symbology - Extended Intro")
                public static boolean symbology_enableExtendedIntro = false;

                @Config.Comment("If true, shows the drawn symbol inside the book pages")
                @Config.Name("Symbology - Stroke Visualization")
                public static boolean symbology_enableStrokeVisualization = false;

                @Config.Comment("If true, shows a small text indicating that the spell is secret")
                @Config.Name("Symbology - Show Secret")
                public static boolean symbology_showSecret = false;

                @Config.Comment("If true, shows a small text indicating that the spell requires knowledge")
                @Config.Name("Symbology - Show Knowledge")
                public static boolean symbology_showKnowledge = false;

            }

            @Config.RequiresWorldRestart
            @Config.Comment("Defines the rules for which secret Witchery content is shown in the Patchouli book.\n" +
                    "ALWAYS_SHOW -> Secret content is always shown in the book.\n" +
                    "PROGRESS (default) -> Secret content is locked behind advancements. Players will discover the " +
                    "secret by themselves, gaining the advancement and permanently revealing the knowledge in the book.\n" +
                    "DISABLED -> Secret content is always hidden.")
            @Config.Name("Patchouli Integration - Secret Policy")
            public static EPatchouliSecretPolicy common_showSecretsPolicy = EPatchouliSecretPolicy.PROGRESS;

            @Config.RequiresWorldRestart
            @Config.Comment("Defines the stretegy used to hide secret elements.\n" +
                    "OBFUSCATE (default) -> Text will be obfuscated (unreadable), Items will show question marks, etc. " +
                    "Players will know that a secret is there, but pages will never be empty.\n" +
                    "INVISIBLE -> Hidden itemelements will not show. If all elements of a page are secret (a recipe page " +
                    "for a secret recipe, for example) the page will simply be white")
            @Config.Name("Patchouli Integration - Obfuscation Strategy")
            public static EPatchouliObfuscationStrategy common_obfuscationStrategy = EPatchouliObfuscationStrategy.OBFUSCATE;

            @Config.RequiresMcRestart
            @Config.Comment("[WIP] If true, WitcheryCompanion will replace the Witchery mechanic of crafting Torn Pages with " +
                    "the Observations of an Immortal book with its own, including it in the Patchouli Guide. Torn Page " +
                    "will be replaced with a new, transparent-to-players item (and compatible with old worlds) that can " +
                    "be right-clicked to add a new page to the Observations section of the Patchouli guide. Can still be " +
                    "combined with Witchery Observations book. If false, this section will be hidden altogether and old " +
                    "mechanic will still work.")
            @Config.Name("Patchouli Integration - Revamp Vampire Book")
            public static boolean common_replaceImmortalsBook = false;

            @Config.Comment("If true, Torn Pages will only have a chance of unlocking new knowledge, as it may contain " +
                    "duplicate pages. Pages will always be unlocked sequentially, but later pages will be increasingly harder " +
                    "to unlock")
            @Config.Name("Patchouli Integration - Harder Immortal Pages")
            public static boolean common_harderImmortalPages = false;

            public enum EPatchouliSecretPolicy {
                ALWAYS_SHOW,
                PROGRESS,
                DISABLED
            }

            public enum EPatchouliObfuscationStrategy {
                OBFUSCATE,
                INVISIBLE
            }

            public static void reloadPatchouliFlags() {
                // This method maps each flag's ID to the corresponding config value
                // PatchouliApiIntegration is responsible for:
                // - prefixing the flag ids with the namespace
                // - checking if Patchouli is loaded before making calls to the API
                // - reloading book contents once only, after all flags are set
                HashMap<String, Boolean> flags = new HashMap<>();

                // Companion Flags
                flags.put("altar/show_infinity", Flags.altar_enableInfinity);
                flags.put("brewing/expertise", Flags.brewing_enableExpertiseExtension);
                flags.put("brewing/extended_dispersal", Flags.brewing_extendedDispersal);
                flags.put("brewing/rituals", Flags.brewing_enableRitualsExtension);
                flags.put("brewing/show_ceiling", Flags.brewing_revealRemoveCeiling);
                flags.put("brewing/show_power", Flags.brewing_showRequiredPower);
                flags.put("brew_effects/inferno_details", Flags.brewEffects_infernoExtendedDetails);
                flags.put("brew_effects/show_blight", Flags.brewEffects_showBlight);
                flags.put("brew_effects/show_floating", Flags.brewEffects_showFloating);
                flags.put("brew_effects/show_healing", Flags.brewEffects_showHealing);
                flags.put("brew_effects/show_lavahold", Flags.brewEffects_showLavaHold);
                flags.put("brew_effects/show_decanting", Flags.brewEffects_showDecanting);
                flags.put("brew_effects/show_raiseland", Flags.brewEffects_showRaiseLand);
                flags.put("brew_effects/show_repellattacker", Flags.brewEffects_showRepellAttacker);
                flags.put("brew_effects/show_weakenvampires", Flags.brewEffects_showWeakenVampires);
                flags.put("circle_magic/shifting_seasons_foci", Flags.circleMagic_showShiftingSeasonsFoci);
                flags.put("conjuring/show_extra", Flags.conjuring_showExtraEntity);
                flags.put("conjuring/extended_intro", Flags.conjuring_enableExtendedIntro);
                flags.put("conjuring/extended_fetish", Flags.conjuring_enableFetishExtension);
                flags.put("crystal_ball/reveal_predictions", Flags.constructs_revealCrystalBallPredictions);
                flags.put("fumes/funnels", Flags.fumes_enableFunnels);
                flags.put("observations/add_werewolf", Flags.observations_enableLycanthropyLore);
                flags.put("observations/show_lycanthropy_intro", Flags.observations_enableLycanthropyIntro);
                flags.put("observations/show_lycanthropy_progress", Flags.observations_enableLycanthropyProgress);
                flags.put("observations/revamp_book", PatchouliIntegration.common_replaceImmortalsBook);
                flags.put("observations/show_vampirism_intro", Flags.observations_enableVampirismIntro);
                flags.put("observations/show_vampirism_progress", Flags.observations_enableVampirismProgress);
                flags.put("statues/show_goddess", Flags.statues_showGoddess);
                flags.put("statues/show_broken_curses", Flags.statues_showBrokenCurses);
                flags.put("statues/show_occluded_summons", Flags.statues_showOccludedSummons);
                flags.put("symbology/extended_intro", Flags.symbology_enableExtendedIntro);
                flags.put("symbology/stroke_visualization", Flags.symbology_enableStrokeVisualization);
                flags.put("symbology/show_secret", Flags.symbology_showSecret);
                flags.put("symbology/show_knowledge", Flags.symbology_showKnowledge);

                PatchouliApiIntegration.updateFlags(flags);
                PatchouliApiIntegration.reloadBook();

            }

        }
    }

    @Mod.EventBusSubscriber(modid = WitcheryCompanion.MODID)
    public static class ConfigSyncHandler {

        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if(event.getModID().equals(WitcheryCompanion.MODID)) {
                ConfigManager.sync(WitcheryCompanion.MODID, Config.Type.INSTANCE);
                reloadConfig();
            }
        }

        public static void reloadConfig() {
            // Flags are always reloaded, but Book Contents won't be triggered
            // Content is reloaded only when PatchouliAPIIntegration 'readyToReload'
            // is true, which is set as such only on PlayerJoinedWorldEvent
            if (Loader.isModLoaded(Mods.PATCHOULI)) {
                IntegrationConfigurations.PatchouliIntegration.reloadPatchouliFlags();
            }

        }

    }
}
