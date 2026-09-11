# MCT Resource Data Pack Loader

One folder that changes what Minecraft and your mods provide, adds new content from
plain JSON, and controls what generates. It applies to every world, in singleplayer
and on dedicated servers. This is the 1.20.1 Forge and 1.21.1 NeoForge line; both
read the same packs.

- [HOWTO.md](HOWTO.md), the full manual (English, Русский, Deutsch)
- [Discord](https://discord.gg/ujY2mV9)
- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/mct-resource-data-pack-loader)
- [Modrinth](https://modrinth.com/mod/mct-resource-data-pack-loader)

# Why it exists

A data pack lives in one world's save, a resource pack is the player's to switch on,
and adding a single ore or a single biome means writing a mod. This mod gives all of
that one folder that applies everywhere, with nothing for players to enable.

# Getting started

Start the game once. The mod creates `rdploader` next to `mods` and `config`, and
writes a `readme.txt` into it that covers the basics.

Files go in by the same path they have inside a jar. To replace the iron ore
texture, the file inside the Minecraft jar is
`assets/minecraft/textures/block/iron_ore.png`, so your version goes at:

```
rdploader/assets/minecraft/textures/block/iron_ore.png
```

That is the whole rule, and `data/` works the same way for loot tables, recipes,
advancements and tags. You can also group files into named zips with a priority
order, and turn any of them off by adding `.disabled` to the name.
[HOWTO.md](HOWTO.md) has the full folder list, every block and item type, every
worldgen shape, pack priority, resource pack precedence and the commands.

# Replacing files

Anything a mod keeps in its `assets` folder: textures, models, blockstates,
language files, sounds, fonts, splash texts, guide books.

Beyond assets, a pack can also replace or add advancements, loot tables, recipes,
tags, functions and structure templates, and they work on dedicated servers too.
Recipe removals delete recipes by name, namespace or output. Loot injections add a
pool to an existing table instead of replacing the whole thing. Player loot gives
players a loot table of their own when they die. Registry renames keep old worlds
working after a mod renames a block. Furnace recipes, fuel burn times, creative tabs
and sound events round it out.

# Changing what already exists

An `overrides/` file changes the properties of a block, item or potion that is
already in the game, vanilla or modded, without replacing any of its files. The
path names the target, so `overrides/minecraft/stone.json` changes
`minecraft:stone`.

Blocks take hardness, blast resistance, light, light opacity, slipperiness, sound,
harvest tool and level, and flammability. Items take stack size, durability and a
container item, and any item can be made edible, with food values and effects, so
wood can be eaten if a pack says so. A potion's effects can be rewritten outright.

These are live: disable the pack and run `/rdplserver reload`, and every value snaps
back to what it was, no restart needed.

# Adding new content

A pack can define blocks in every common shape: basic, ore, falling, slab, stairs,
fence, fence gate, wall, pane, door, trapdoor, banner, ladder, torch, log, leaves,
sapling, crop, flower, cane, vine, portal and container. Items come as basic, food,
drink, tool, armor, seed, potion, potion bottle and pouch. Fluids, tool and armor
materials, potion effects, potion types, brewing recipes, villager professions with
trades, game rules, biomes, village plots, entity variants and whole dimensions are
all files too.

Each key under a file's `variants` is a registry name: `data/mypack/blocks/ore.json`
holding `ruby_ore` registers `mypack:ruby_ore`. Ship a texture and the blockstate,
the models, the loot table and the tags are written for you; a texture can itself
be a JSON pixel map, so a pack need not ship a single PNG. If a real mod already
registers that name, the mod wins.

A few of these go further than a list can show. Saplings grow into trees built
from your own log and leaves, or into your structure templates. Portal blocks link
two dimensions and remember who built them, and a frame a player builds and lights
can open one. Gates lock a portal or a dimension, vanilla ones included, behind an
item held or paid, a recipe crafted, an advancement earned, or a mob slain. An
entity variant is a new entity built on an existing one, with its own name, skin,
stats, equipment, loot and any task the game has, while the original is left
exactly as it was. A container holds an inventory of any size, draws as a chest or
as a block of its own, fills from a loot table on first open, and as an item can be
worn through Curios.

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
- Suppress vanilla structures, or set their spacing, biomes, distance from spawn,
  how they seat into the terrain, and what their spawners hold
- Set mob spawn rates and caps per biome
- Swap blocks out of existing chunks, so an ore that leaked into a world can be
  cleaned up
- Block crafting and furnace recipes by mod, with KubeJS and CraftTweaker
  additions always surviving
- Flatten bedrock, per dimension and per biome
- Shape the overworld itself: its floor and ceiling, sea level, lava oceans and a
  deep world under the vanilla terrain, applied only as a world is created
- Generate the overworld as a void with a platform
- Scale gravity, fall damage and jump strength per dimension, and stack dimensions
  so that falling out of the bottom of one lands in the next

A world template gathers these into one file, so a pack ships a whole world shape
at once. Every group also answers to a config switch that lets the pack decide,
forces the config's value, or turns the group off entirely.

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
round, and rounds are counted into a match.

# Pregeneration

A pack can hand a player a world whose land is already there. `pregenOnNewWorld`
makes every chunk around spawn the moment a world is created, for the overworld,
a list of dimensions, or every dimension registered. A dimension can instead be
made the first time somebody enters it.

While it runs, everybody is held as a spectator with progress on screen, then
released with a greeting when it is done. A finished dimension is written into the
world and never made again, a stopped run resumes where it left off, and a pristine
copy of the finished world can be kept for a later reset.

# Good to know

A pack whose name starts with `RDPLO` always overrides the player's selected
resource packs, one starting with `RDPLN` never does, and anything else follows
the `overrideResourcePacks` config.

`/rdpl unused` lists files in your packs that nothing has asked for, which is
usually a typo in a path. `/rdplserver oregen` reports what was blocked. `/rdpl
which` names the pack serving any file.

A pack can stay on the server alone, with every player on a plain vanilla client,
as long as it registers nothing. The `vanillaClients` config switch enforces
exactly that. [HOWTO.md](HOWTO.md)'s Server-side packs section has the steps.

The game's telemetry and chat reporting are switched off by default; `privacy` in
the config puts them back.

The mod's own report goes to `logs/rdpl.log` rather than the main log.

# Requirements

1.20.1 needs Forge; 1.21.1 needs NeoForge. Blast Plaster and Curios are optional:
the pack keys that drive them do nothing when they are absent.

# Reporting issues

Attach `logs/latest.log` and `logs/rdpl.log`, plus your mod and loader version.

# Help translate the mod

Feel free to translate the mod and put it in a pull request.

# License

Resource Data Pack Loader is licensed under the GNU GENERAL PUBLIC LICENSE
Version 3. You may use it in modpacks, reviews or any other form as long as you
abide by the terms.
