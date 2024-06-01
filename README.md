# Witchery: Companion

A Companion to Witchery: Resurrected fixing bugs, crashes, patching things and adding integrations and
configurability. Witchery: Resurrected is a required dependency, and is the original 1.7.10 Witchery
jar in the resource packs folder.

## Current Features (as of v0.21-beta):
### Bugfixes:
- **Common**
  - **[Common]** Fix crash when pulling null entity
  - **[Loot]** Fix crash with Levelled random enchant
- **Blocks**
  - **[Altar]** Fix Altar not providing power on world load/reload
  - **[Altar]** Fix Placed Items (Arthana, Pentacle, etc) not dropping anything when breaking Altar blocks below them
  - **[Altar]** Fix client crash when the player joins a world where Placed Items are within view in the first frame
  - **[Coffin]** Fix edge-case crash when placing the Coffin across chunk boundaries
  - **[Coffin]** Fix crash when piston moves the top block of the coffin
  - **[Cursed Blocks]** Fix crash when using trigger dispersal. They still don't work, but they should not crash the game
  - **[Fetish]** Fix fetish blocks not having drops (Scarecrows, Trent Effiges, Vines)
  - **[Mandrake Crop]** Fix spawning mandrakes even when not fully mature
  - **[Poppet Shelf]** Fix upside-down poppet rendering
  - **[Stockade]** Fix weird rendering when player head is too close to the stockade
  - **[Witch's Cauldron]** Fix bottling skill being impossible to increase
  - **[Witch's Cauldron]** Fix right-clicking with an Empty Bucket voiding the brew inside
- **Books**
  - **[Herbology Book]** Fix plants being rendered above text
- **Brews**
  - **[Common]** Fix Liquid Dispersal having no effect
  - **[Brew of Blast]** Fix breaking terrain even if the 'ignore blocks' modifier was added
  - **[Brew of Erosion]** Fix random integer crash
  - **[Brew of Frog's Tongue]** Fix crash when the brew pulls a null entity
  - **[Brew of Raising]** Fix crash on some dispersal methods (by @MsRandom)
  - **[Brew of Tidal Hold]** Fix entity suffocation in ghost blocks
- **Entity**
  - **[Coven Witch]** Fix Brew of the Grotesque quest requiring a negative amount of items
  - **[Enchanted Broom]** Fix server freeze when the player in on the broom and it breaks while flying
  - **[Rowan Boat]** Fix missing Rowan Boat entity model texture (not configurable)
  - **[Owl]** Fix Owls being unable to sit
  - **[Voodoo Protection Poppet]** Fix crash when trying to protect its owner from a curse
  - **[Poppet Protection Poppet]** Fix crash when trying to protect its owner from a poppet
- **Infusions**
  - **[Soul Brews]** Fix losing Soul brews (so the Mystic Branch knowledge) upon death
- **Items**
  - **[Rowan Boat]** Fix Rowan Boat not having texture (not configurable)
  - **[Spectral Stone]** Fix NBT being retained after entity is released, effectively duping it
- **Potions**
  - **[Fortune]** Fix Brew of Fortune having no effect
- **Rites**
  - **[Rite of Prior Incarnation]** Fix "WitcheryPriIncUsr" NBT data persisting after item pickup
- **World**
  - **[Apothecary] Fix crash when generating book in chests and item frames when Tinkers' Construct or Thaumcraft are 
  installed due to duplicate loot pool entries

### Tweaks
- **Brews**
  - **[Common]** Tweak to disable power ceiling. Fixes some 'non bugs' related to the impossibility of applying certain
    brews with power greater than one (healing, damage, etc)
  - **[Common]** Tweak to force all brews to still apply effect to blocks/entities even if the 'ignore' modifiers
    are applied. This is needed for example to make a Brew of Blast, with ignore blocks modifier, to make an explosion when
    hitting a block (but the explosion won't damage terrain). Each brew is then delegated to ignore the effects, depending
    on applied modifiers
- **Entities**
  - **[Baba Yaga]** Tweak to modify gifts given to owners frequency, max distance and max items
  - **[Baba Yaga]** Tweak to give her own loot table instead of hard-coded loot, both on death and for gifted items
  - **[Cat Familiar]** Separate their loot table from Vanilla ocelots loot table
  - **[Coven Witch]** Separate their loot table from Vanilla witches loot table
  - **[Demon]** Tweak to give them their own loot table instead of hard-coded loot
  - **[Enchanted Broom]** Tweak Enchanted Broom max health
  - **[Lilith]** Tweak to disable enchanting items given to her
  - **[Lord of Torment]** Tweak to disable mid-fight teleportation to Torment dimension
  - **[Lord of Torment]** Tweak to disable hardcoded loot drops (enchanted books)
  - **[Owl]** Tweak to disable taking items (since flying AI is bugged and might dupe items)
  - **[Owl]** Tweak to change the model slightly when the owl is sitting
  - **[Owl]** Tweak to render children Owls smaller
  - **[Spectre]** Tweak to add its own loot table
  - **[Spectre]** Tweak to wait a minimum delay before despawning
  - **[Spectre]** Tweak to modify spectre attributes (Follow range, Movement speed, Attack damage)
- **Items**
  - **[Chalk]** Tweak Chalk max stack size to 1, to avoid weird item loss due to stacking problems
  - **[Brew of Erosion]** Tweak behaviour to align with the Erosion Brew
  - **[Seer Stone]** Tweak to not print throwing skill (unimplemented feature by W:R)
  - **[Voodoo Protection Poppet]** Tweak amount of damage taken to protect its owner from a curse
  - **[Poppet Protection Poppet]** Tweak amount of damage taken to protect its owner from a poppet
- **Rites**
  - **[Moving Earth]** Tweak to disable moving TileEntity (for dupes)
  - **[Moving Earth]** Tweak to disable voiding blocks above the ritual. This will make the ritual stop (fail) when
    there is not enough space above to move the terrain upwards
  - **[Moving Earth]** Tweak refund policy to modify the cases in which the ritual refunds the player
    - **0**: Never refound - even if the ritual fails to move terrain, even by 1 block, it won't refund the player
    - **1**: Refund if partial - if the ritual does not move the blocks all the way up, it will refund the player
    - **2**: Refund on fail - only refund the player when the ritual could not move any block
  - **[Moving Earth]** Tweak to configure a blacklist of blockstates that the ritual should not move
  - **[Moving Earth]** Tweak to configure indicators of failure (smoke paticles and sounds) will be generated near the
    unmovable block
- **Dimensions**
  - **[Spirit World]** Tweak to modify default dimension ID to 11 (configurable)
  - **[Torment]** Tweak to modify default dimension ID to 12 (configurable)
  - **[Mirror World]** Tweak to modify default dimension ID to 13 (configurable)

### Integrations
- **Crafttweaker**
  - **[Altar]** Integration to register or un-register Blocks nearby the altar that can power the Altar
  - **[Witch's Cauldron]** Integration to register or un-register Blocks underneath the cauldron that can act as a heat
    source. Supports fluids
  - **[Brew of Erosion]** Integration to customize what the brew can mine, destroy or ignore, including
    a maximum harvest level
  - **[Hobgoblin trades]** Integration to customize trades and various aspects related to them

- **Just Enough Resources**
  - **[Entities]** Integration to register many entities and their loot in entity drops category

### Performance
- **Blocks**
  - **[Altar]** Implement caching for the Power Source map
  - **[JEI Integration]** ~~Option to skip recipe syncing for user-made recipes to save world loading time~~.
    Causes problems. Set compatibility_mode = true in Witchery general config

### Future Plans
- Add CraftTweaker integration for main recipe categories (Cauldron, Distillery, Kettle, Oven)
- Add CraftTweaker integration for Brew of Sprouting, to specify valid trees to spawn
- Finish JER Integration
- Squishing all bugs
- Tinkers Construct / Construct Armory integration (Armor traits or tools that can be used in place of Witchery ones)
    for modpacks where Tinkers is the only way to make tools and armors
  - Death robes
  - Vampire clothing
  - Hunter clothing
  - Withes robes
- JEI Handlers for as many things as possible (Altar Power providers, Goblin trades, Baba Yaga drops, etc)
- More Tweaks
  - Modify Rite/Curse strength, to fine-tune how much damage poppets take for the given Rite/Curse
  - Modify entity attributes, such as Max health, Damage, Speed, etc

### Help Wanted
- Owl AI for transporting items
- Winged Monkey AI
- Brew Bucket item does not exist yet. Needs implementation. Will make possible to move brews between cauldrons
- ~~Remove WitcheryLoadingPlugin and provide all missing models, textures and so on to remove dependency from OG 
  Witchery JAR~~ Implemented a Loading Plugin that checks the file and downloads it automatically
- Groovyscript Integration
