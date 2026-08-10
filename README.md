# Links
- [Official Discord](https://discord.gg/ujY2mV9)

- [Resource Data Pack Loader on CurseForge](https://www.curseforge.com/minecraft/mc-mods/mct-resource-data-pack-loader)
- [Resource Data Pack Loader on Modrinth](https://modrinth.com/mod/mct-resource-data-pack-loader)

# MCT Resource Data Pack Loader
One global folder that overrides what Minecraft and mods provide, defines new content from JSON, and
controls what generates, in every world, on clients and servers.

Minecraft 1.12.2 has no data pack system, and Resource Loader only covers client assets. Advancements,
loot tables and functions are read from a world's own data folder, so overriding one means copying files
into every save, and recipes are read from mod jars only, so there is no way to touch them short of
repacking a jar. Adding a single ore or a single biome means writing a mod. This mod covers all of it
from one folder.

# Usage
Put loose files or a zip in the `rdploader` folder, alongside `mods` and `config`.

```
rdploader/assets/<namespace>/...
rdploader/<packname>.zip
```

Override paths match the layout inside a mod jar, so files can be copied straight across. Content is
defined by adding a JSON file in the matching folder, the path is the identity, so
`assets/mypack/blocks/ruby_ore.json` registers `mypack:ruby_ore`.

See [HOWTO.md](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/1.12.2-1.0-Release/HOWTO.md) for the full folder list, every block and item type, every worldgen shape,
pack priority, resource pack precedence and the commands.

# Overriding what already exists
- Textures, models, blockstates, language files, sounds and anything else in a mod's `assets` folder
- Advancements and loot tables, on dedicated servers as well as singleplayer
- Recipes, replaced or added, and recipe removals by name, namespace or output
- Loot injections, which add a pool to an existing table instead of replacing the whole thing
- Player loot, a loot table rolled when a player dies, dropped alongside what they were carrying or in place of it
- Structure templates (`.nbt`)
- Functions (`.mcfunction`), which vanilla otherwise only reads per world
- Registry renames, so worlds saved before a mod renamed a block or item keep it
- Ore dictionary names, furnace recipes, fuel burn times, creative tabs and sound events

# Defining new content
Blocks in every common shape, basic, ore, falling, slab, stairs, fence, fence gate, wall, pane, door,
trapdoor, ladder, torch, log, leaves, sapling, crop, flower, cane, vine and portal. Items as basic, food, drink, tool,
armor, seed, potion and potion bottle. Also fluids, tool and armor materials, potion effects,
potion types, brewing recipes, villager professions with trades, game rules, biomes, village plots,
entity variants and whole dimensions.

Saplings grow into a tree built from your own log and leaves, or into one of your structure
templates. Leaves take a tint and drop your sapling. Portal blocks link two dimensions, remember who
built them, and survive an explosion when they do. Gates put conditions on portals and on dimensions
themselves, vanilla ones included: an item held or paid, a recipe crafted, an advancement earned, or
a mob slain, once or a counted number of times, per player or for the whole world, with a key laid
at the slayer's feet when the pack wants the unlock to be a thing that can be handed on. A dimension picks its own sky, fog and cloud
colors, and can turn the sky, clouds or weather off entirely.

An entity variant is a new entity built on one that already exists, vanilla or modded, its own
registry name, name, spawn egg, loot table and skin, with its own health, damage, speed, jump,
size, effects, equipment and temper. An aggressive rabbit that swells when it charges, a zombie
that trades, a cow that shrugs off fire: all of it a file, and the entity it was built from is
left exactly as it was. A variant can also be told to ignore the spawn rules it inherited, so an
animal turned hostile will come out of a spawner anywhere rather than only on lit grass.

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
- Block other mods' world generators outright, or by what they make, ores, structures, flora, lakes
  or terrain, which is how mods add what Forge's events never see
- Suppress vanilla structures, or set how far apart they are seeded, which biomes they are allowed
  in, how far from spawn they start, what they spawn and what their mob spawners hold
- Set mob spawn rates and caps per biome
- Swap blocks out of chunks that already exist, so an ore that leaked into a world can be cleaned up
- Block crafting and furnace recipes by mod, with CraftTweaker and GroovyScript additions always
  surviving
- Flatten bedrock, per dimension and per biome, in new chunks or in ones that already exist
- Shape the overworld itself, sea level, lava oceans and the terrain noise, applied as a world is
  created, so worlds that already exist are untouched
- Generate the overworld as a void with a platform, which also happens by itself if every biome is
  blocked
- Adapt the terrain to structures as they generate, in the spirit of how modern versions seat
  theirs, with villages taken further: roads graded to a walkable slope and surfaced to suit the
  ground they cross, buildings seated on the road they front and refused ground that would put them
  on a plinth, lamp posts lit at road height, and hillsides opened rather than buildings buried.
  This one is experimental, reshapes terrain permanently in whatever save it touches, changes
  between builds, and stays off unless a pack or the config asks for it

A world template gathers those settings into one file so a pack ships a whole world shape at once,
and every group answers to a config switch that leaves it to the pack, forces the config's own value,
or turns the group off entirely so no pack can enable it.

# World intro
A pack can put a sequence of pages in front of a player as they enter the world. Each page is text
scrolling over a picture, or text sitting still on one, with a background that can cycle through
several images, a font size of the pack's choosing, and one music track behind the whole run.
Scrolling text can run clear off the top or come to rest with its last line centered before the
page turns itself. The player gets Next Page and Skip All along the bottom, or Continue to World
on the last one, and the world stays paused behind it in singleplayer. It can play once per player
per world, remembered in their save, or on every join.

# Pregeneration
A pack can hand a player a world whose land is already there. `pregenOnNewWorld` makes every chunk
around the spawn the moment a world is created, in the overworld, in a list of dimensions one after
another, in every dimension anything registers, or out to each dimension's world border. A dimension
can instead be made the first time somebody sets foot in it, so worlds nobody visits cost nothing.

While it runs everybody is held: made a spectator, kept in place, shown a pulsing line mid-screen,
the world paused around them, and released into the pack's game mode with a greeting when everything
is done. Progress is announced with an ETA per pass and a total time at the end, in messages a pack
words itself. A finished dimension is written into the world and never made again, unless its files
go missing from the disk, which is noticed and makes that one over, and a remade end brings its
dragon back. A stopped run can resume where it left off, a wedged one stops itself within a minute
rather than hanging anybody, and the engineering that makes it fast, a few hundred chunks a second,
without a single cascading chunk load, is written down in [HOWTO.md](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/1.12.2-1.0-Release/HOWTO.md) for whoever wants it.

# CoFH World
Mods that require CoFH World load without it. Their generation files can be read straight out of
their jars and generated through this mod instead, covering every CoFH generator and distribution
that produces anything. It is off by default, and stands down when the real CoFH World is installed,
which then generates as normal, translating those files into a pack is the supported route, and the
only way to change what they generate.

# Good to know
A pack whose name starts with `RDPLO` always overrides the player's selected resource packs, one
starting with `RDPLN` never does, and anything else follows the `overrideResourcePacks` config.

`/rdpl unused` lists files in your packs that nothing has asked for, which is usually a typo in a
path. `/rdpl biome` and `/rdpl oregen` report what generated and what was blocked.

Grave mods need nothing set up. Player loot is put down as an ordinary death drop before any of them
looks, so a grave collects it along with the inventory, and `dropLoose` on a `player_loot` entry
leaves it on the ground for the killer instead.

The mod's own report goes to `logs/rdpl.log` rather than the main log.

A pack can stay on the server alone, with every player on a plain vanilla client, as long as it
registers nothing, and the `vanillaClients` config switch enforces exactly that, skipping anything
a client would have to know about and naming each skipped file in the log. [HOWTO.md](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/1.12.2-1.0-Release/HOWTO.md)'s
Server-side packs section has the folder split and the steps.

# Requirements
Requires [MixinBooter](https://www.curseforge.com/minecraft/mc-mods/mixinbooter) and
[MCT Blast Plaster](https://www.curseforge.com/minecraft/mc-mods/mct-blast-plaster). Both are hard
dependencies, the game will not start without them.

# Reporting issues
When you are reporting bugs, please attach `logs/latest.log` and `logs/rdpl.log`, plus your mod and
Forge version.

# Help translate the mod
Feel free to translate the mod and put it in a pull request.

# About Modpack and License
Resource Data Pack Loader is licensed under the GNU GENERAL PUBLIC LICENSE Version 3. You may use it
in modpacks, reviews or any other form as long as you abide by the terms.