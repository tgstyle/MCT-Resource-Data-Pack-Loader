# Links
- [Official Discord](https://discord.gg/ujY2mV9)

- [Resource Data Pack Loader on CurseForge](https://www.curseforge.com/minecraft/mc-mods/mct-resource-data-pack-loader)
- [Resource Data Pack Loader on Modrinth](https://modrinth.com/mod/mct-resource-data-pack-loader)

# MCT Resource Data Pack Loader
One global folder that overrides what Minecraft and mods provide, defines new content from JSON, and
controls what generates — in every world, on clients and servers.

Minecraft 1.12.2 has no data pack system, and Resource Loader only covers client assets. Advancements,
loot tables, recipes and functions cannot be overridden without repacking a mod jar or copying files
into every save. Adding a single ore or a single biome means writing a mod. This mod covers all of it
from one folder.

# Usage
Put loose files or a zip in the `rdploader` folder, alongside `mods` and `config`.

```
rdploader/assets/<namespace>/...
rdploader/<packname>.zip
```

Override paths match the layout inside a mod jar, so files can be copied straight across. Content is
defined by adding a JSON file in the matching folder — the path is the identity, so
`assets/mypack/blocks/ruby_ore.json` registers `mypack:ruby_ore`.

See [HOWTO.md](HOWTO.md) for the full folder list, every block and item type, every worldgen shape,
pack priority, resource pack precedence and the commands.

# Overriding what already exists
- Textures, models, blockstates, language files, sounds and anything else in a mod's `assets` folder
- Advancements and loot tables, on dedicated servers as well as singleplayer
- Recipes, replaced or added, and recipe removals by name, namespace or output
- Loot injections, which add a pool to an existing table instead of replacing the whole thing
- Structure templates (`.nbt`)
- Functions (`.mcfunction`), which vanilla otherwise only reads per world
- Registry renames, so worlds saved before a mod renamed a block or item keep it
- Ore dictionary names, furnace recipes, fuel burn times, creative tabs and sound events

# Defining new content
Blocks in every common shape — basic, ore, falling, slab, stairs, fence, wall, pane, door, ladder,
torch, log, leaves, sapling, crop, flower, cane, vine and portal. Items as basic, food, drink, tool,
armour, seed, potion and potion bottle. Also fluids, tool and armour materials, potion effects,
potion types, brewing recipes, villager professions with trades, game rules, biomes, village plots
and whole dimensions.

Saplings grow into a tree built from your own log and leaves, or into one of your structure
templates. Leaves take a tint and drop your sapling. Portal blocks link two dimensions, remember who
built them, and survive an explosion when they do. A dimension picks its own sky, fog and cloud
colours, and can turn the sky, clouds or weather off entirely.

Registration happens at the lowest priority, so a real mod always wins. Anything needing a tile
entity, a GUI, an inventory or per-tick logic still needs a real mod.

# Generating it
Worldgen is a shape and a spread. Shapes cover ore blobs, long wandering veins, flat plates, hollow
geodes, bowls, tapering spires, rough nodules, narrow vents, surface decoration, whole trees, vines,
belts that span several chunks for stone regions, and your own `.nbt` templates. Spreads place them evenly, weighted toward a height, sprawling
fractally, following the terrain, on cave floors and ceilings, or under water.

Every entry is filtered by height, attempt count, target block, dimension, biome, temperature,
rainfall and distance from spawn, and can be generated into chunks that already exist.

# Controlling what generates
- Block ore generation by mod or by ore type, in either direction
- Block biomes by mod or by name, in either direction, with unwanted biomes replaced on the finished
  biome map so oceans, mesas and hill variants are reached too
- Block other mods' world generators outright, or by what they make — ores, structures, flora, lakes
  or terrain — which is how mods add what Forge's events never see
- Suppress vanilla structures, or set how far apart they are seeded, which biomes they are allowed
  in, how far from spawn they start, what they spawn and what their mob spawners hold
- Set mob spawn rates and caps per biome
- Swap blocks out of chunks that already exist, so an ore that leaked into a world can be cleaned up
- Block crafting and furnace recipes by mod, with CraftTweaker and GroovyScript additions always
  surviving
- Flatten bedrock, per dimension and per biome, in new chunks or in ones that already exist
- Shape the overworld itself — sea level, lava oceans and the terrain noise — applied as a world is
  created, so worlds that already exist are untouched
- Generate the overworld as a void with a platform, which also happens by itself if every biome is
  blocked

A world template gathers those settings into one file so a pack ships a whole world shape at once,
and every group answers to a config switch that leaves it to the pack, forces the config's own value,
or turns the group off entirely so no pack can enable it.

# CoFH World
Mods that require CoFH World load without it. Their generation files can be read straight out of
their jars and generated through this mod instead, covering every CoFH generator and distribution
that produces anything. It is off by default, and stands down when the real CoFH World is installed,
which then generates as normal — translating those files into a pack is the supported route, and the
only way to change what they generate.

# Good to know
A pack whose name starts with `RDPLO` always overrides the player's selected resource packs, one
starting with `RDPLN` never does, and anything else follows the `overrideResourcePacks` config.

`/rdpl unused` lists files in your packs that nothing has asked for, which is usually a typo in a
path. `/rdpl biome` and `/rdpl oregen` report what generated and what was blocked.

The mod's own report goes to `logs/rdpl.log` rather than the main log.

# Requirements
Requires [MixinBooter](https://www.curseforge.com/minecraft/mc-mods/mixinbooter).

# Reporting issues
When you are reporting bugs, please attach `logs/latest.log` and `logs/rdpl.log`, plus your mod and
Forge version.

# Help translate the mod
Feel free to translate the mod and put it in a pull request.

# About Modpack and License
Resource Data Pack Loader is licensed under the GNU GENERAL PUBLIC LICENSE Version 3. You may use it
in modpacks, reviews or any other form as long as you abide by the terms.