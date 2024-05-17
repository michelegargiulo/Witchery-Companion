# Witchery: Companion

A Companion to Witchery: Resurrected fixing bugs, crashes, patching things and adding integrations and
configurability. Witchery: Resurrected is a required dependency, and is the original 1.7.10 Witchery
jar in the resource packs folder.

## Current Features (as of v0.10-alpha):
### Bugfixes:
- **Common**
  - **[Common]** Fix crash when pulling null entity
- **Blocks**
  - **[Altar]** Fix Altar not providing power on world load/reload
  - **[Altar]** Fix Placed Items (Arthana, Pentacle, etc) not dropping anything when breaking Altar blocks below them
  - **[Altar]** Fix client crash when the player joins a world where Placed Items are within view in the first frame
  - **[Coffin]** Fix edge-case crash when placing the Coffin across chunk boundaries
  - **[Cursed Blocks]** Fix crash when using trigger dispersal. They still don't work, but they should not crash the game
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
  - **[Brew of Tidal Hold]** Fix entity suffocation in ghost blocks
- **Entity**
  - **[Coven]** Fix Brew of the Grotesque quest requiring a negative amount of items
  - **[Enchanted Broom]** Fix server freeze when the player in on the broom and it breaks while flying
  - **[Rowan Boat]** Fix missing Rowan Boat entity model texture (not configurable)
  - **[Owl]** Fix Owls being unable to sit
- **Infusions**
  - **[Soul Brews]** Fix losing Soul brews (so the Mystic Branch knowledge) upon death
- **Items**
  - **[Rowan Boat]** Fix Rowan Boat not having texture (not configurable)
  - **[Spectral Stone]** Fix NBT being retained after entity is released, effectively duping it
- **Potions**
  - **[Fortune]** Fix Brew of Fortune having no effect


### Tweaks
- **Brews**
  - **[Common]** Tweak to disable power ceiling. Fixes some 'non bugs' related to the impossibility of applying certain
    brews with power greater than one (healing, damage, etc)
  - **[Common]** Tweak to force all brews to still apply effect to blocks/entities even if the 'ignore' modifiers
    are applied. This is needed for example to make a Brew of Blast, with ignore blocks modifier, to make an explosion when
    hitting a block (but the explosion won't damage terrain). Each brew is then delegated to ignore the effects, depending
    on applied modifiers
  - **[Brew of Erosion]** Tweak to add CraftTweaker integration to what blockstates the brew can mine, destroy, etc
- **Entities**
  - **[Enchanted Broom]** Tweak Enchanted Broom max health
  - **[Lord of Torment]** Tweak to disable mid-fight teleportation to Torment dimension
  - **[Lord of Torment]** Tweak to disable hardcoded loot drops (enchanted books)
  - **[Owl]** Tweak to disable taking items (since flying AI is bugged and might dupe items)
  - **[Owl]** Tweak to change the model slightly when the owl is sitting
  - **[Owl]** Tweak to render children Owls smaller
- **Items**
  - **[Chalk]** Tweak Chalk max stack size to 1, to avoid weird item loss due to stacking problems
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

### Integrations
- **Crafttweaker**
  - **[Altar]** Integration to register or un-register Blocks nearby the altar that can power the Altar
  - **[Witch's Cauldron]** Integration to register or un-register Blocks underneath the cauldron that can act as a heat
    source. Supports fluids
  - **[Brew of Erosion]** Integration to customize what the brew can mine, destroy or ignore, including
    a maximum harvest level

- **Just Enough Resources**
  - **[Entities]** Integration to register many entities and their loot in entity drops category

### Performance
- **Blocks**
  - **[Altar]** Implement caching for the Power Source map

### Future Plans
- Add CraftTweaker integration for main recipe categories (Cauldron, Distillery, Kettle, Oven)
- Add CraftTweaker integration for Brew of Sprouting, to specify valid trees to spawn
- Finish JER Integration
- Squishing all bugs
- Tinkers Construct / Construct Armory integration (Armor traits or tools that can be used in place of Witchery ones)

### Help Wanted
- Owl AI
- Winged Monkey AI
- Brew Bucket item
- Remove WitcheryLoadingPlugin and provide all missing models, textures and so on to remove dependency from OG Witchery JAR
- Groovyscript Integration
