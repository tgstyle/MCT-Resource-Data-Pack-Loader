# MCT Resource Data Pack Loader

One folder that changes what Minecraft and your mods provide, adds new content from
plain JSON, and controls what generates. It applies to every world, in singleplayer
and on dedicated servers.

- [HOWTO.md](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/1.12.2-1.0-Release/HOWTO.md), the full manual (English, Русский, Deutsch)
- [Discord](https://discord.gg/ujY2mV9)
- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/mct-resource-data-pack-loader)
- [Modrinth](https://modrinth.com/mod/mct-resource-data-pack-loader)

# Why it exists

Minecraft 1.12.2 has no data packs. Advancements, loot tables and functions live
inside each world's save, so changing one means copying files into every world.
Recipes only load from mod jars, so short of repacking a jar there is no way to
touch them. Adding a single ore or a single biome means writing a mod.

This mod gives all of that one folder.

# Getting started

Start the game once. The mod creates `rdploader` next to `mods` and `config`, and
writes a `readme.txt` into it that covers the basics.

Files go in by the same path they have inside a jar. To replace the iron ore
texture, the file inside the Minecraft jar is
`assets/minecraft/textures/blocks/iron_ore.png`, so your version goes at:

```
rdploader/assets/minecraft/textures/blocks/iron_ore.png
```

That is the whole rule. You can also group files into named packs, as folders or
zips, with a priority order, and turn any of them off by adding `.disabled` to the
name. [HOWTO.md](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/1.12.2-1.0-Release/HOWTO.md)
has the full folder list, every block and item type, every worldgen shape, pack
priority, resource pack precedence and the commands.

# Replacing files

Anything a mod keeps in its `assets` folder: textures, models, blockstates,
language files, sounds, fonts, splash texts, guide books.

Beyond assets, a pack can also replace or add advancements, loot tables, recipes,
functions and structure templates, and they work on dedicated servers too. Recipe
removals delete recipes by name, namespace or output. Loot injections add a pool
to an existing table instead of replacing the whole thing. Player loot gives
players a loot table of their own when they die. Registry renames keep old worlds
working after a mod renames a block. Ore dictionary names, furnace recipes, fuel
burn times, creative tabs and sound events round it out.

# Changing what already exists

An `overrides/` file changes the properties of a block, item or potion that is
already in the game, vanilla or modded, without replacing any of its files. The
path names the target, so `overrides/minecraft/stone.json` changes
`minecraft:stone`.

Blocks take hardness, blast resistance, light, light opacity, slipperiness, sound,
harvest tool and level, and flammability. Items take stack size, durability and a
container item, and any item can be made edible, with food values and effects, so
wood can be eaten if a pack says so. A potion's effects can be rewritten outright.

These are live: disable the pack and run `/rdpl reload`, and every value snaps
back to what it was, no restart needed.

# Adding new content

A pack can define blocks in every common shape: basic, ore, falling, slab, stairs,
fence, fence gate, wall, pane, door, trapdoor, banner, ladder, torch, log, leaves,
sapling, crop, flower, cane, vine and portal. Items come as basic, food, drink,
tool, armor, seed, potion and potion bottle. Fluids, tool and armor materials,
potion effects, potion types, brewing recipes, villager professions with trades,
game rules, biomes, village plots, entity variants and whole dimensions are all
files too.

The file's path is its name: `assets/mypack/blocks/ruby_ore.json` registers
`mypack:ruby_ore`. There is no name field to get wrong, and if a real mod already
registers that name, the mod wins.

A few of these go further than a list can show. Saplings grow into trees built
from your own log and leaves, or into your structure templates. Portal blocks link
two dimensions and remember who built them. Gates lock a portal or a dimension,
vanilla ones included, behind an item held or paid, a recipe crafted, an
advancement earned, or a mob slain. An entity variant is a new entity built on an
existing one, with its own name, skin, stats, equipment and loot, while the
original is left exactly as it was.

Anything needing a tile entity, a GUI, an inventory or per-tick logic still needs
a real mod. A machine is out of reach; an ore, a fence, a food or a fluid is not.

# Generating it

Worldgen is a shape and a spread. Shapes cover ore blobs, long wandering veins,
plates, geodes, bowls, spires, nodules, vents, surface decoration, whole trees,
vines, multi-chunk belts for stone regions, and your own `.nbt` templates. Spreads
place them evenly, weighted toward a height, fractally, along the terrain, on cave
floors and ceilings, or under water.

Every entry filters by height, attempts, target block, dimension, biome,
temperature, rainfall and distance from spawn, and can generate into chunks that
already exist.

# Controlling what generates

- Block ore generation by mod or by ore type, in either direction
- Block biomes by mod or by name, with unwanted ones replaced on the finished
  biome map, so oceans, mesas and hill variants are reached too
- Block other mods' world generators outright, or by what they make
- Suppress vanilla structures, or set their spacing, biomes, distance from spawn,
  and what their spawners hold
- Set mob spawn rates and caps per biome
- Swap blocks out of existing chunks, so an ore that leaked into a world can be
  cleaned up
- Block crafting and furnace recipes by mod, with CraftTweaker and GroovyScript
  additions always surviving
- Flatten bedrock, per dimension and per biome
- Shape the overworld itself: sea level, lava oceans and terrain noise, applied
  only as a world is created
- Generate the overworld as a void with a platform
- Seat structures into the terrain as they generate, with villages graded,
  surfaced and lit properly. This one is experimental, changes terrain
  permanently, and stays off unless asked for

A world template gathers these into one file, so a pack ships a whole world shape
at once. Every group also answers to a config switch that lets the pack decide,
forces the config's value, or turns the group off entirely.

# Rubic worlds

A pack can ask for a world built out of cubes instead of 256 block columns, and
the world grows in both directions: a floor far below zero, a ceiling far above
255, terrain and caves and ores through all of it. From the outside it is an
ordinary world. You dig, build, light, spawn and travel the same way, and the
whole vanilla generation window keeps its usual shape and sits inside the taller
world, so terrain, structures and mods land where they always did.

What a pack gets from it:

- A world height of its own, in whole cubes, set once when the world is created
- A deep world under the vanilla window, with modern style noise caves, aquifers
  and banded ore veins, and a stone of the pack's choosing
- Cave regions, the pack answer to cave biomes: named regions painted through the
  underground in three dimensions, each with its own floors, ceilings, water
  level, mobs and structures
- Dimensions stacked on each other, so falling out of the bottom of one world
  carries you into the next one down and climbing out the top brings you back
- Any dimension left out, keeping its ordinary world in the same save, so rubic
  and vanilla dimensions mix freely

Inside, storage is 16 by 16 by 16 cubes in their own region files next to the
vanilla ones, loaded and generated and saved on their own, with a light engine
written for that shape. Vanilla's assumption that a world is 256 blocks tall is
patched out wherever it is load bearing: build limits, kill planes, commands,
pathing, portals, beacons, maps, the renderer and the client's own view distance
gain a vertical half. Foreign generators keep running against a normal looking
256 block window, which is why other mods' terrain still works.
[HOWTO.md](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/1.12.2-1.0-Release/HOWTO.md)
covers the settings, the heights a world may take and the mods it will not run
beside.

# World intro

A pack can put a sequence of pages in front of a player entering the world:
scrolling or still text over pictures, backgrounds that cycle, music behind the
run. The player gets Next Page and Skip All, and the world stays paused behind it
in singleplayer. It can play once per player per world or on every join.

# Pregeneration

A pack can hand a player a world whose land is already there. `pregenOnNewWorld`
makes every chunk around spawn the moment a world is created, for the overworld,
a list of dimensions, or every dimension registered. A dimension can instead be
made the first time somebody enters it.

While it runs, everybody is held as a spectator with progress and an ETA on
screen, then released with a greeting when it is done. A finished dimension is
written into the world and never made again. A stopped run resumes where it left
off. It makes a few hundred chunks a second without a single cascading chunk
load, and the engineering behind that is written down in
[HOWTO.md](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/1.12.2-1.0-Release/HOWTO.md).

# CoFH World

Mods that require CoFH World load without it. Their generation files can be read
straight out of their jars and generated through this mod, covering every CoFH
generator and distribution that produces anything. Off by default, and it stands
down when the real CoFH World is installed.

# Good to know

A pack whose name starts with `RDPLO` always overrides the player's selected
resource packs, one starting with `RDPLN` never does, and anything else follows
the `overrideResourcePacks` config.

`/rdpl unused` lists files in your packs that nothing has asked for, which is
usually a typo in a path. `/rdpl biome` and `/rdpl oregen` report what generated
and what was blocked. `/rdpl which` names the pack serving any file.

Grave mods need nothing set up: player loot is put down as an ordinary death drop
before any of them looks.

A pack can stay on the server alone, with every player on a plain vanilla client,
as long as it registers nothing. The `vanillaClients` config switch enforces
exactly that.
[HOWTO.md](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/1.12.2-1.0-Release/HOWTO.md)'s
Server-side packs section has the steps.

The mod's own report goes to `logs/rdpl.log` rather than the main log.

# Requirements

Requires [MixinBooter](https://www.curseforge.com/minecraft/mc-mods/mixinbooter)
and [MCT Blast Plaster](https://www.curseforge.com/minecraft/mc-mods/mct-blast-plaster).
Both are hard dependencies; the game will not start without them.

# Reporting issues

Attach `logs/latest.log` and `logs/rdpl.log`, plus your mod and Forge version.

# Help translate the mod

Feel free to translate the mod and put it in a pull request.

# License

Resource Data Pack Loader is licensed under the GNU GENERAL PUBLIC LICENSE
Version 3. You may use it in modpacks, reviews or any other form as long as you
abide by the terms.