# MCT Resource Data Pack Loader

One folder that changes what Minecraft and your mods provide, adds new content from
plain JSON, and controls what generates. It applies to every world, in singleplayer
and on dedicated servers.

It runs on Minecraft 1.12.2, 1.20.1 and 1.21.1. The 1.20.1 and 1.21.1 builds read
the same packs, and a pack written for 1.12.2 loads on them, or the other way
around, converted once on first load. Anything below that applies to only some
versions starts with those versions.

- [HOWTO.md](HOWTO.md), the full manual for each version (English, Русский, Deutsch)
- [Discord](https://discord.gg/ujY2mV9)
- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/mct-resource-data-pack-loader)
- [Modrinth](https://modrinth.com/mod/mct-resource-data-pack-loader)

# Why it exists

A data pack lives in one world's save, a resource pack is the player's to switch on,
and adding a single ore or a single biome means writing a mod. This mod gives all of
that one folder that applies everywhere, with nothing for players to enable.

**1.12.2:** there are no data packs at all. Advancements, loot tables and functions
live inside each world's save, so changing one means copying files into every
world, and recipes only load from mod jars, so short of repacking a jar there is no
way to touch them.

# Getting started

Start the game once. The mod creates `rdploader` next to `mods` and `config`, and
writes a `readme.txt` into it that covers the basics.

Files go in by the same path they have inside a jar. To replace the iron ore
texture, take the file's path inside the Minecraft jar and put your version at the
same path under `rdploader`:

```
rdploader/assets/minecraft/textures/blocks/iron_ore.png     (1.12.2)
rdploader/assets/minecraft/textures/block/iron_ore.png      (1.20.1 / 1.21.1)
```

That is the whole rule. You can also group files into named zips with a priority
order, and turn any of them off by adding `.disabled` to the name. A folder in
`rdploader` is never a pack, so zip the contents of a pack, not the folder holding
them.

- **1.12.2:** everything goes under `assets/`, the definition folders for new
  content included.
- **1.20.1 / 1.21.1:** `data/` works the same way for loot tables, recipes,
  advancements and tags, and the definition folders for new content live there.

[HOWTO.md](HOWTO.md) has the full folder list, every block and item type, every
worldgen shape, pack priority, resource pack precedence and the commands.

# Replacing files

Anything a mod keeps in its `assets` folder: textures, models, blockstates,
language files, sounds, fonts, splash texts, guide books.

Beyond assets, a pack can also replace or add advancements, loot tables, recipes,
functions and structure templates, and they work on dedicated servers too. Recipe
removals delete recipes by name, namespace or output. Loot injections add a pool
to an existing table instead of replacing the whole thing. Player loot gives
players a loot table of their own when they die. Registry renames keep old worlds
working after a mod renames a block. Furnace recipes, fuel burn times, creative
tabs and sound events round it out.

- **1.12.2:** ore dictionary names too.
- **1.20.1 / 1.21.1:** tags too.

# Changing what already exists

An `overrides/` file changes the properties of a block, item or potion that is
already in the game, vanilla or modded, without replacing any of its files. The
path names the target, so `overrides/minecraft/stone.json` changes
`minecraft:stone`.

Blocks take hardness, blast resistance, light, light opacity, slipperiness, sound,
harvest tool and level, and flammability. Items take stack size, durability and a
container item, and any item can be made edible, with food values and effects, so
wood can be eaten if a pack says so. A potion's effects can be rewritten outright.

These are live: disable the pack and reload, and every value snaps back to what it
was, no restart needed.

`/rdpl reload` does it on every version; on a dedicated server, `/rdplserver reload`.

# Adding new content

A pack can define blocks in every common shape: basic, ore, falling, slab, stairs,
fence, fence gate, wall, pane, door, trapdoor, banner, ladder, torch, log, leaves,
sapling, crop, flower, cane, vine, portal and container. Items come as basic, food,
drink, tool, armor, seed, potion, potion bottle and pouch. Fluids, tool and armor
materials, potion effects, potion types, brewing recipes, villager professions with
trades, game rules, biomes, village plots, entity variants and whole dimensions are
all files too.

A definition file lists its content under `variants`, and a texture can be a JSON
pixel map, so a pack need not ship a single PNG. If a real mod already registers a
name, the mod wins.

- **1.12.2:** the file's path is the registry name, so
  `assets/mypack/blocks/ruby_ore.json` registers `mypack:ruby_ore`, and each key
  under `variants` is one of its metadata values.
- **1.20.1 / 1.21.1:** each key under `variants` is a registry name of its own, so
  `data/mypack/blocks/ore.json` holding `ruby_ore` registers `mypack:ruby_ore`.
  Ship a texture and the blockstate, the models, the loot table and the tags are
  written for you.

A few of these go further than a list can show. Saplings grow into trees built
from your own log and leaves, or into your structure templates. Portal blocks link
two dimensions and remember who built them, and a frame a player builds and lights
can open one. Gates lock a portal or a dimension, vanilla ones included, behind an
item held or paid, a recipe crafted, an advancement earned, or a mob slain. An
entity variant is a new entity built on an existing one, with its own name, skin,
stats, equipment, loot and any task the game has, while the original is left
exactly as it was. A container holds an inventory of any size, draws as a chest or
as a block of its own, fills from a loot table on first open, and as an item can be
worn.

- **1.12.2:** a pouch is worn through Baubles.
- **1.20.1 / 1.21.1:** a pouch is worn through Curios.

Anything needing a block entity of its own, a screen, an inventory or per-tick
logic still needs a real mod. A machine is out of reach; an ore, a fence, a food, a
fluid or a crate is not.

# Generating it

Worldgen is a shape and a spread. Shapes cover ore blobs, long wandering veins,
seeded noise deposits with rich and poor tiers, plates, geodes, bowls, spires,
nodules, vents, surface decoration, whole trees, vines, multi-chunk belts for stone
regions, per-block fields, and your own `.nbt` templates, alone or composed into
buildings on a grid. Spreads place them evenly, weighted toward a height, fractally,
along the terrain, on cave floors and ceilings, or under water.

Every entry filters by height, attempts, target block, dimension, biome,
temperature, rainfall and distance from spawn, and can generate into chunks that
already exist. Cave regions paint named regions through the underground with their
own covers, spawns and landmarks, and hardness groups make the rock itself vary in
how it mines.

# Cities

A pack can lay a whole city: streets at any width with sidewalks, lines and lamps,
bridges and tunnels where the ground demands them, railways and subways with
stations, sewers under the streets, plazas, and buildings rolled from the pack's
own plots or composed from structure maps. The plan is rolled, or drawn by hand on
a city map, and every block of it answers to a setting a world template can change
per biome.

# Controlling what generates

- Block ore generation by mod or by ore type, in either direction
- Block biomes by mod or by name, with unwanted ones replaced on the finished
  biome map
- Block other mods' world generators outright, or by what they make
- Suppress vanilla structures, or set their spacing, biomes, distance from spawn,
  and what their spawners hold
- Set mob spawn rates and caps per biome
- Swap blocks out of existing chunks, so an ore that leaked into a world can be
  cleaned up
- Block crafting and furnace recipes by mod, with script mod additions always
  surviving. **1.12.2:** CraftTweaker and GroovyScript. **1.20.1 / 1.21.1:**
  KubeJS and CraftTweaker
- Flatten bedrock, per dimension and per biome
- Shape the overworld itself: sea level, lava oceans and terrain noise, applied
  only as a world is created. **1.20.1 / 1.21.1:** its floor and ceiling too, and
  a deep world under the vanilla terrain. **1.12.2:** a deep world needs a rubic
  world, below
- Generate the overworld as a void with a platform
- Scale gravity, fall damage and jump strength per dimension, and stack dimensions
  so that falling out of the bottom of one lands in the next
- Seat structures into the terrain as they generate, with villages and city
  streets graded, surfaced and lit properly. This one changes terrain permanently
  and stays off unless asked for. How the terrain adapts to each vanilla structure
  can be set as well

A world template gathers these into one file, so a pack ships a whole world shape
at once. Every group also answers to a config switch that lets the pack decide,
forces the config's value, or turns the group off entirely.

# 1.12.2 Rubic worlds

**1.12.2 only.** 1.20.1 and 1.21.1 have tall worlds of their own: a dimension's
floor and ceiling are set in the pack.

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
- Any dimension left out, keeping its ordinary world in the same save, so rubic
  and vanilla dimensions mix freely

Inside, storage is 16 by 16 by 16 cubes in their own region files next to the
vanilla ones, loaded and generated and saved on their own, with a light engine
written for that shape. Vanilla's assumption that a world is 256 blocks tall is
patched out wherever it is load bearing: build limits, kill planes, commands,
pathing, portals, beacons, maps, the renderer and the client's own view distance
gain a vertical half. Foreign generators keep running against a normal looking
256 block window, which is why other mods' terrain still works.
[HOWTO.md](HOWTO.md) links the 1.12.2 guide, which covers the settings, the
heights a world may take and the mods it will not run beside.

# World intro

A pack can put a sequence of pages in front of a player entering the world:
scrolling or still text over pictures, backgrounds that cycle, music behind the
run. The player gets Next Page and Skip All, and the world stays paused behind it
in singleplayer. It can play once per player per world or on every join.

# Teams and scoring

A pack can field sides on the vanilla scoreboard that mobs, players or anything
spawning in a corner of the world join as they arrive, and objectives that score
kills and deaths to those sides. A round ends on a score or a clock, the
standings come up as chat or as a card, the map can reset itself for the next
round, and rounds are counted into a match. The Kamikaze demo listed in
[HOWTO.md](HOWTO.md) is that, ready to drop in.

# Pregeneration

A pack can hand a player a world whose land is already there. `pregenOnNewWorld`
makes every chunk around spawn the moment a world is created, for the overworld,
a list of dimensions, or every dimension registered. A dimension can instead be
made the first time somebody enters it.

While it runs, everybody is held as a spectator with progress and an ETA on
screen, then released with a greeting when it is done. A finished dimension is
written into the world and never made again, a stopped run resumes where it left
off, and a pristine copy of the finished world can be kept for a later reset.

# CoFH World

**1.12.2 only.** Mods that require CoFH World load without it. Their generation
files can be read straight out of their jars and generated through this mod,
covering every CoFH generator and distribution that produces anything. Off by
default, and it stands down when the real CoFH World is installed.

**1.20.1 / 1.21.1:** CoFH World and its file format do not exist there, so those
files are translated into a pack instead.

# Good to know

A pack whose name starts with `RDPLO` always overrides the player's selected
resource packs, one starting with `RDPLN` never does, and anything else follows
the `overrideResourcePacks` config.

`/rdpl unused` lists files in your packs that nothing has asked for, which is
usually a typo in a path. `/rdpl biome` lists the biomes that can generate, and
`/rdplserver oregen` reports what was blocked. `/rdpl which` names the pack serving
any file.

Grave mods need nothing set up: player loot is put down as an ordinary death drop
before any of them looks.

A pack can stay on the server alone, with every player on a plain vanilla client,
as long as it registers nothing. The `vanillaClients` config switch enforces
exactly that. [HOWTO.md](HOWTO.md)'s guides have the steps in their Server-side
packs section. **1.12.2:** a rubic world cannot be used with `vanillaClients`.

**1.20.1 / 1.21.1:** the game's telemetry and chat reporting are switched off by
default; `privacy` in the config puts them back.

The mod's own report goes to `logs/rdpl.log` rather than the main log.

# Requirements

[MCT Blast Plaster](https://www.curseforge.com/minecraft/mc-mods/mct-blast-plaster)
is required on every version.

- **1.12.2:** Forge and [MixinBooter](https://www.curseforge.com/minecraft/mc-mods/mixinbooter).
  Baubles is optional.
- **1.20.1:** Forge. Curios is optional.
- **1.21.1:** NeoForge. Curios is optional.

The game will not start without a required mod. An optional one only lights up
the pack keys that drive it; without it they do nothing.

# Reporting issues

Attach `logs/latest.log` and `logs/rdpl.log`, plus your Minecraft, mod and loader
version.

# Help translate the mod

Feel free to translate the mod and put it in a pull request.

# License

Resource Data Pack Loader is licensed under the GNU GENERAL PUBLIC LICENSE
Version 3. You may use it in modpacks, reviews or any other form as long as you
abide by the terms.
