# MCT Resource Data Pack Loader

**The API that should have been.**

**Not just another resource and data pack loader, though it does that very well.
Now you can take control of the entire sandbox.**

Minecraft is data driven right up to the point where it gets interesting. A data
pack can change a recipe, but it cannot add a block, an item or a fluid, it cannot
reshape the world, and it lives in one world's save. Past that line you write a
mod, once per version and once per loader.

This mod moves the line all the way out. Blocks, items, creatures, dimensions,
cities, rules, rewards and whole game modes are JSON files. One folder applies to
every world, on clients and dedicated servers alike, and a pack written once loads
on Minecraft 1.12.2, 1.20.1, 1.21.1 and 26.x. You are not adding to someone
else's game anymore: you are designing your own.

- [HOWTO.md](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/1.12.2-1.0-Release/HOWTO.md), the full manual for each version (English, Deutsch, Русский, 简体中文, Español, Português (Brasil), Français, Čeština, Italiano, 日本語, 한국어, Português (Portugal), Українська, 繁體中文)
- [Discord](https://discord.gg/ujY2mV9)
- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/mct-resource-data-pack-loader)
- [Modrinth](https://modrinth.com/mod/mct-resource-data-pack-loader)

Anything below that applies to only some versions starts with those versions.

# A new ore, no Java

`rdploader/data/mypack/blocks/ore.json` (1.20.1 / 1.21.1 / 26.x)

```json
{
  "type": "ore",
  "material": "rock",
  "harvestTool": "pickaxe",
  "variants": {
    "ruby_ore": { "hardness": 3.0 }
  }
}
```

That registers `mypack:ruby_ore`. Add a texture and the blockstate, the models, the
loot table and the tags are written for you. On 1.12.2 the same block is
`rdploader/assets/mypack/blocks/ruby_ore.json`.

# Why it exists

- **Content is a file.** Blocks, items, fluids, entity variants and dimensions are
  defined in JSON, where vanilla and the loaders ask for a mod.
- **One pack, every version.** The 1.20.1, 1.21.1 and 26.x builds read the same
  packs, and a pack written for 1.12.2 loads on them, or the other way around,
  converted once on first load.
- **One folder, every world.** A data pack lives in one world's save and a resource
  pack is the player's to switch on. `rdploader` applies everywhere, in
  singleplayer and on dedicated servers, with nothing for players to enable.

**1.12.2:** there are no data packs at all. Advancements, loot tables and functions
live inside each world's save, and recipes only load from mod jars. This mod is the
data pack system that version never had.

# What a pack can do

Everything here runs on the server alone, and players can join on a plain
vanilla client, except what is marked **RDPL client**: that needs the mod and
the pack on each player's client too. See [Vanilla clients welcome](#vanilla-clients-welcome).

## Customize vanilla and every mod you run

Your pack outranks every jar. Nothing installed is off limits, and no jar is
ever touched.

- Replace any recipe, loot table, advancement, function or structure template,
  vanilla's or any mod's
- **1.12.2:** crafting and furnace recipes only; a mod's machine recipes live in
  its own code, where CraftTweaker or GroovyScript are the better fit
- Replace any texture, model, language file or sound (**RDPL client**)
- [Property overrides](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#property-overrides): hardness, light, harvest tool and tier, stack
  size, durability, food values and more on any existing block, item or potion,
  live with `/rdpl reload` (**RDPL client**)
- [Block drops](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#block-drops): add drops and experience to any block without replacing
  its loot table
- [Disabled blocks and items](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#disabled-blocks-and-items): take any block or item out of the game,
  gone from tabs, JEI, loot, trades and every inventory
- [Entity variants](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#entity-variants): new creatures built on any mod's mob, with its AI
  and your own skin, stats, drops and name (**RDPL client**)
- [Mob ranks](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#mob-ranks): a random rank for any mob when it spawns, with its own
  health, damage, effects and loot
- [Language gates](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#language-gates): alternative names players can choose between
  (**RDPL client**)
- [Registry renames](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#registry-renames): old worlds keep loading when a mod renames its
  blocks
- Choose which ores, biomes, structures, spawns and recipes other mods add, and
  reshape the rest

## Remake existing dimensions

The overworld, the Nether, the End and any dimension another mod adds are yours
to rework, not just the ones you create. See [Existing dimensions](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#existing-dimensions).

- A new sky with your own sun, moon, planets and stars, or endless rain
  (**RDPL client**)
- New terrain and structures in every chunk not yet explored
- [Gates](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#gates): lock any dimension, the Nether and the End included, until a
  player earns an advancement, crafts or holds the right item, pays an item
  price, slays a boss or reaches a team score, per player, per team or for the
  whole server
- [Game rules](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#game-rules) per dimension, vanilla or modded
- Structures pinned to exact coordinates, and [retrogen](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#retrogen): new worldgen
  added to chunks already explored
- [Blast Plaster integration](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#blast-plaster-integration): explosions that heal themselves, drop
  their blocks or leave a clean crater, in any dimension and from any mod

## Add content

- [Blocks](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#blocks) in every common shape, ores, crates and [containers](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#containers)
  up to nine rows by twelve, [bells](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#bells), and portal blocks (**RDPL client**)
- [Items](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#items), tools, armor, food, [fluids](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#fluids), creative tabs and
  wearable items (Baubles or Curios) (**RDPL client**)
- [Potions](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#potions-potion-types-and-brewing) and [villager professions](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#villagers-and-trades) (**RDPL client**)
- [Enchantments](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#enchantments) (**RDPL client** on 1.12.2 and 1.20.1)
- Brewing recipes and villager trades
- [Biomes](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#biomes), [cave regions](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#cave-regions) and whole [dimensions](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#dimensions)
  (**RDPL client** on 1.12.2)

## Generate the world

- [Ores, veins, geodes, trees and your own structure templates](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#worldgen-entries),
  filtered by height, biome, dimension and distance from spawn
- [Deep worlds](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#the-deep-world) that reach hundreds of blocks below the vanilla floor,
  raised ceilings and floating sky islands
- Void worlds, your own sea level and terrain shape, gravity, and stacked
  dimensions
- [Structure maps](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#structure-maps) that build far past vanilla's 48 block
  template limit
- Villages and cities: streets, plazas, bridges, railways, subways, sewers and
  lamp posts, rolled at random, drawn by hand from a [city map](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#city-layout-maps), or
  [one endless city](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#continuous-city) that never stops
- [Pregeneration](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#pregeneration) as a world is created, so nobody waits on chunks while
  playing
- **1.12.2:** rubic worlds that reach far above and below the 256 block limit
  (**RDPL client**)

## Control the sandbox

- One [control layer](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#the-control-layer) over ores, biomes, structures, spawning, recipes,
  terrain, villages, entities, commands and server settings: for each group, let
  the pack decide, let the config decide, or switch it off
- Game rules, server properties, game mode and difficulty shipped with the pack
- A [world template](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#world-templates) that carries an entire world design as one file

## Shape how it plays

- [Recipe locks](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#recipe-locks) that open crafting as players earn advancements
- [Anvil recipes](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#anvil-work), [affixes and socketed gems](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#affixes-and-sockets), and a reshaped
  [enchanting table](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#enchanting-table)
- [Vein mining](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#vein-mining), [right-click harvest](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#right-click-harvest),
  [picking up and carrying blocks and mobs](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#carrying-1), and
  [loot dropped by players on death](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#player-loot)
- Rewards for a [varied diet](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#food-variety), [extra hearts](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#max-health-rewards) earned through play,
  and [work orders](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#work-orders)
- [Exposures](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#exposures): environmental hazards with their own death messages
  (**RDPL client** on 1.20.1, 1.21.1 and 26.x)
- [Portways](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#portways): a whole teleport system of named waypoints on their own
  networks, across dimensions if you allow it, with a price per trip in
  experience, items, health or hunger, and locked behind the same gates as a
  dimension (**RDPL client**)
- [Tooltips](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#tooltips) on any item or enchantment (**RDPL client**)

## Build game modes

- A [world intro](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#world-intro) and story pages shown on entry (**RDPL client**)
- [Teams](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#teams), [scoring rounds](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#scoring) and standings
- [Raids](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#raids) of your own design, with your own waves, omens and rewards
- [Event cards](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#cards) triggered by commands, joins, dimensions, biomes,
  structures, advancements, kills, crafts, time and score
- Branching [dialogs](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#dialogs) for your creatures, driven by choices and
  conditions
- [Dice and card decks](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#dice-and-decks) and [board games](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#board-games) played in the world

## What still needs a mod

Anything needing a block entity of its own, a screen of its own or per-tick
machine logic. A machine is currently out of reach; an ore, a fence, a food, a
fluid, a crate, a raid or a whole game mode is not.

[HOWTO.md](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/1.12.2-1.0-Release/HOWTO.md) covers each of these in full.

# Vanilla clients welcome

Much of this runs on the server alone. Install the mod on a dedicated server,
switch on `vanillaClients`, and players join with an untouched vanilla client:
no modpack, nothing to download. [Server-side packs](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#server-side-packs) has the
details.

What that looks like:

- **Cities in vanilla:** an endless city of streets, plazas, subways, sewers and
  towers, built from vanilla blocks
- **Team battles in vanilla:** factions, scoring rounds and standings, with
  results announced to every player
- **An overhauled economy in vanilla:** new recipes, removed recipes, richer
  loot, new trades and reshaped enchanting
- **Progression in vanilla:** the Nether and the End stay shut until a player
  earns an advancement, crafts the key or slays a boss
- **A new world in vanilla:** void worlds, custom ore veins, your own structures
  and a pregenerated map ready on day one
- **1.20.1 / 1.21.1 / 26.x:** new biomes and whole new dimensions, and raids of
  your own design

The limit is what the feature list marks **RDPL client**: anything the client
has to know about to draw it, such as new blocks, items, creatures, sounds,
textures and models. `vanillaClients` skips those files and names each one in
the log, so a stray block file costs a log line, not a refused connection. A
few things look plainer: cards and dialogs arrive as chat, and server recipes
craft but do not show in the recipe book.

# Getting started

Start the game once. The mod creates `rdploader` next to `mods` and `config`, and
writes a `readme.txt` into it that covers the basics.

Files go in by the same path they have inside a jar. To replace the iron ore
texture, take the file's path inside the Minecraft jar and put your version at the
same path under `rdploader`:

```
rdploader/assets/minecraft/textures/blocks/iron_ore.png     (1.12.2)
rdploader/assets/minecraft/textures/block/iron_ore.png      (1.20.1 / 1.21.1 / 26.x)
```

That is the whole rule. You can also group files into named zips with a priority
order, and turn any of them off by adding `.disabled` to the name. A folder in
`rdploader` is never a pack, so zip the contents of a pack, not the folder holding
them.

- **1.12.2:** everything goes under `assets/`, the definition folders for new
  content included.
- **1.20.1 / 1.21.1 / 26.x:** `data/` works the same way for loot tables, recipes,
  advancements and tags, and the definition folders for new content live there.

[HOWTO.md](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/1.12.2-1.0-Release/HOWTO.md) has the full folder list, every block and item type, every
worldgen shape, pack priority, resource pack precedence and the commands.

# Good to know

`/rdpl reload` applies pack changes without a restart; on a dedicated server,
`/rdplserver reload`. `/rdpl which` names the pack serving any file, and
`/rdpl unused` lists files nothing has asked for, which is usually a typo in a path.

A mod can ship RDPL content inside its own jar: a `rdploader` folder at the jar's
root is read like any pack, so the mod needs no separate download. See the
[Mod API](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/26.x-Release/howto_localized/HOWTO-en_us.md#mod-api).

**1.12.2:** mods that require CoFH World load without it.

**1.20.1 / 1.21.1 / 26.x:** the game's telemetry and chat reporting are switched off by
default; `privacy` in the config puts them back.

The mod's own report goes to `logs/rdpl.log` rather than the main log.

# Requirements

[MCT Blast Plaster](https://www.curseforge.com/minecraft/mc-mods/mct-blast-plaster)
is required on every version.

- **1.12.2:** Forge and [MixinBooter](https://www.curseforge.com/minecraft/mc-mods/mixinbooter).
  Baubles is optional.
- **1.20.1:** Forge. Curios is optional.
- **1.21.1:** NeoForge. Curios is optional.
- **26.x:** NeoForge. Curios is optional.

The game will not start without a required mod. An optional one only lights up
the pack keys that drive it; without it they do nothing.

# Reporting issues

Attach `logs/latest.log` and `logs/rdpl.log`, plus your Minecraft, mod and loader
version.

# Help translate the mod

Feel free to translate the mod and put it in a pull request.

# License

Resource Data Pack Loader is © tgstyle, All Rights Reserved. You may include the
unmodified mod in modpacks and show it in videos, streams and reviews. Any other
use, copying, modification or redistribution needs written permission. See
`LICENSE`, `LICENSE_ASSETS.txt` and `LICENSE_THIRD_PARTY.txt`.
