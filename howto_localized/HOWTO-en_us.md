# Resource Data Pack Loader

**One folder that overrides anything Minecraft or a mod provides, defines new content from JSON, and controls what generates, in every world, on clients and servers, with nothing for players to switch on.**

One working example. Drop it straight into `rdploader` and look at how each file is written.

- [RDPLRubyExample.zip](../example/RDPLRubyExample.zip) uses every kind of file this version of the loader reads, all of it named for ruby: every block and item type, the things it changes about vanilla, the world it generates, the dimension under that world, and the screens the player sees on the way in. It ships no images at all: every texture is a pixel map drawn in JSON.

This guide is for the 1.20.1 and 1.21.1 builds. They read the same packs; the few places where the two differ are marked **1.20.1** and **1.21.1**.

---

## Contents

**Getting started**
- [What it is](#what-it-is)
- [Where files go](#where-files-go)
- [Reading the tables](#reading-the-tables)
- [The one rule](#the-one-rule)
- [Organizing packs](#organizing-packs)
- [Resource packs: who wins](#resource-packs-who-wins)
- [Mod API](#mod-api)
- [Packs written for 1.12.2](#packs-written-for-1122)
- [Server-side packs](#server-side-packs)

**Overriding**
- [What you can override](#what-you-can-override)
- [Property overrides](#property-overrides)
- [Registry renames](#registry-renames)
- [Player loot](#player-loot)

**Defining new content**
- [How definitions work](#how-definitions-work)
- [Blocks](#blocks)
- [Models, blockstates and textures](#models-blockstates-and-textures)
- [Making vanilla treat your block properly](#making-vanilla-treat-your-block-properly)
- [Items](#items)
- [Fluids](#fluids)
- [Materials, tabs, sounds, tags](#materials-tabs-sounds-tags)
- [Furnace recipes and fuels](#furnace-recipes-and-fuels)
- [Potions, potion types and brewing](#potions-potion-types-and-brewing)
- [Exposures](#exposures)
- [Villagers and trades](#villagers-and-trades)
- [Entity variants](#entity-variants)
- [Village plots](#village-plots)
- [Biomes](#biomes)
- [Dimensions](#dimensions)
- [Containers](#containers)
- [Portals and gates](#portals-and-gates)
- [World templates](#world-templates)
- [The deep world](#the-deep-world)
- [Cave regions](#cave-regions)
- [World intro](#world-intro)
- [Game rules](#game-rules)
- [Teams](#teams)
- [Scoring](#scoring)
- [Hardness groups](#hardness-groups)

**Generating it**
- [Worldgen entries](#worldgen-entries)
- [Shapes](#shapes)
- [Structure maps](#structure-maps)
- [City layout maps](#city-layout-maps)
- [Spreads](#spreads)
- [Retrogen](#retrogen)
- [Pregeneration](#pregeneration)

**Control**
- [The control layer](#the-control-layer)
- [What each group does](#what-each-group-does)
- [Blast Plaster integration](#blast-plaster-integration)

**Reference**
- [Value lists](#value-lists)
- [Folder list](#folder-list)
- [Commands](#commands)
- [Good to know](#good-to-know)
- [When something doesn't work](#when-something-doesnt-work)
- [Bonus: vanilla tweaks](#bonus-vanilla-tweaks)

---

# Getting started

## What it is

Resource Data Pack Loader (RDPL) reads a single folder, `rdploader`, and does three jobs:

- **Overrides.** A file in the folder replaces the one the game or a mod would have loaded. No toggle, no per-world setup, nothing for players to enable.
- **New content.** JSON definitions register blocks, items, fluids, biomes, dimensions, potions and villagers. No Java, no jar.
- **Control.** Block ore, biome, structure or recipe generation, flatten bedrock, set spawn rates, void the overworld, set world defaults.

## Where files go

A pack has two roots, the same two a vanilla pack has. `assets/` holds what the client draws and hears: models, blockstates, textures, language files, sounds and the intro's texts. `data/` holds everything else: every definition this mod reads, and the vanilla data files a pack replaces. Every path in this guide is written from the namespace onward, so `<namespace>/blocks/*.json` is `data/mypack/blocks/ruby_ore.json` on disk for a pack whose namespace is `mypack`, and `<namespace>/models/` is `assets/mypack/models/`. Each section repeats its own path under its header.

Under `data/`:

| Path | What it holds |
| --- | --- |
| `<namespace>/blocks/*.json` | Block definitions. [Blocks](#blocks) |
| `<namespace>/items/*.json` | Item definitions. [Items](#items) |
| `<namespace>/fluids/*.json` | Fluids, with a block and a bucket. [Fluids](#fluids) |
| `<namespace>/materials/*.json` | Tool and armor materials. [Materials, tabs, sounds, tags](#materials-tabs-sounds-tags) |
| `<namespace>/tabs/*.json` | Creative tabs. [Materials, tabs, sounds, tags](#materials-tabs-sounds-tags) |
| `<namespace>/sounds/*.json` | Sound events. [Materials, tabs, sounds, tags](#materials-tabs-sounds-tags) |
| `<namespace>/biomes/*.json` | Biome definitions. [Biomes](#biomes) |
| `<namespace>/worldgen/*.json` | What generates, and where. [Worldgen entries](#worldgen-entries) |
| `<namespace>/caveregions/*.json` | Named regions painted over the underground. [Cave regions](#cave-regions) |
| `<namespace>/dimensions/*.json` | Dimension definitions. [Dimensions](#dimensions) |
| `<namespace>/worldtemplates/*.json` | A whole world's settings in one file. [World templates](#world-templates) |
| `<namespace>/worldintro/*.json` | Pages shown when a player enters the world. [World intro](#world-intro) |
| `<namespace>/gates/*.json` | Conditions on portals and dimensions. [Portals and gates](#portals-and-gates) |
| `<namespace>/gamerules/*.json` | Game rules for new worlds. [Game rules](#game-rules) |
| `<namespace>/teams/*.json` | Sides on the vanilla scoreboard, and what joins them. [Teams](#teams) |
| `<namespace>/scoring/*.json` | Objectives, points and how a match ends. [Scoring](#scoring) |
| `<namespace>/entities/*.json` | Entity variants built on entities that already exist. [Entity variants](#entity-variants) |
| `<namespace>/hardness/*.json` | Mining time and blast multipliers for groups of blocks. [Hardness groups](#hardness-groups) |
| `<namespace>/exposures/*.json` | Hazards that expose players near or carrying named blocks and items. [Exposures](#exposures) |
| `<namespace>/overrides/<target>/<name>.json` | Properties of existing blocks, items and potion types, changed in place. [Property overrides](#property-overrides) |
| `<namespace>/villages/*.json` | Plots a city or village can build. [Village plots](#village-plots) |
| `<namespace>/pathintersects/*.json` | Designs painted where streets meet. [Village plots](#village-plots) |
| `<namespace>/structuremaps/*.json` | Templates composed into one large building on a grid. [Structure maps](#structure-maps) |
| `<namespace>/citymaps/*.json` | A drawn street plan a city is laid out from instead of rolling one. [City layout maps](#city-layout-maps) |
| `<namespace>/portalframes/*.json` | Frames a player can build and light. [Portals and gates](#portals-and-gates) |
| `<namespace>/blastplaster/*.json` | What Blast Plaster does after an explosion, per dimension. [Blast Plaster integration](#blast-plaster-integration) |
| `<namespace>/structures/*.nbt` | Templates, for saplings, `imprint`, structure maps and mod overrides. [What you can override](#what-you-can-override) |
| `<namespace>/recipes/*.json` | Crafting recipes, added or replaced. [What you can override](#what-you-can-override) |
| `<namespace>/recipe_removals/*.json` | Recipes deleted by name, namespace or output. [What you can override](#what-you-can-override) |
| `<namespace>/furnace/*.json` | Furnace recipes added and removed. [Furnace recipes and fuels](#furnace-recipes-and-fuels) |
| `<namespace>/fuels/*.json` | Burn times. [Furnace recipes and fuels](#furnace-recipes-and-fuels) |
| `<namespace>/brewing/*.json` | Brewing stand recipes. [Potions, potion types and brewing](#potions-potion-types-and-brewing) |
| `<namespace>/potions/*.json` | Potion effects. [Potions, potion types and brewing](#potions-potion-types-and-brewing) |
| `<namespace>/potion_types/*.json` | Bottled potions built from those effects. [Potions, potion types and brewing](#potions-potion-types-and-brewing) |
| `<namespace>/villagers/*.json` | Villager professions. [Villagers and trades](#villagers-and-trades) |
| `<namespace>/trades/*.json` | What professions buy and sell. [Villagers and trades](#villagers-and-trades) |
| `<namespace>/loot_tables/*.json` | Loot tables, replaced. [What you can override](#what-you-can-override) |
| `<namespace>/loot_injections/*.json` | A pool added to a table that already exists. [What you can override](#what-you-can-override) |
| `<namespace>/player_loot/*.json` | A loot table rolled when a player dies. [Player loot](#player-loot) |
| `<namespace>/advancements/*.json` | Advancements. [What you can override](#what-you-can-override) |
| `<namespace>/functions/*.mcfunction` | Function files. [What you can override](#what-you-can-override) |
| `<namespace>/tags/<kind>/*.json` | Tags, the game's own format. [Materials, tabs, sounds, tags](#materials-tabs-sounds-tags) |
| `<namespace>/registry_remap/*.json` | Old names mapped to new ones. [Registry renames](#registry-renames) |

Under `assets/`:

| Path | What it holds |
| --- | --- |
| `<namespace>/models/`, `<namespace>/blockstates/`, `<namespace>/textures/`, `<namespace>/lang/` | The usual asset folders. [Models, blockstates and textures](#models-blockstates-and-textures) |
| `<namespace>/sounds.json` | The sound index the game reads, beside the `sounds/` definitions under `data/` |
| `<namespace>/texts/*.txt` | Plain text files, used by the world intro. [World intro](#world-intro) |

**1.21.1** names the vanilla data folders in the singular: `loot_table/`, `recipe/`, `advancement/`, `function/`, `structure/`, `tags/item/`, `tags/block/`. A pack may use either spelling there; the plural names above are read as their singular twins, so one pack serves both builds.

## Reading the tables

Every file is standard JSON. A representative worldgen entry:

```json
{
  "blocks": [
    { "block": "minecraft:magenta_wool", "weight": 80 },
    { "block": "mypack:ruby_ore", "weight": 20 }
  ],
  "size": { "min": 4, "max": 12 },
  "attempts": 12,
  "maxTemperature": 0.5,
  "sparse": true,
  "replace": ["minecraft:stone", "minecraft:andesite"],
  "dimensions": ["minecraft:overworld", "minecraft:the_nether"]
}
```

The key tables in this document state whether a key is required, what it holds, and the default when omitted. Unrecognized values are logged and replaced with the default; they do not crash the game. Value types used throughout:

| When a table says | You write |
| --- | --- |
| int | `8` |
| int, ticks | `100` (20 ticks = 1 second) |
| int or range | `8`, or `{ "min": 4, "max": 12 }` to roll between them |
| 0 to 15, 1 to 100 and such | an int inside those bounds |
| float | `0.5` |
| boolean | `true` or `false` |
| string | `"words in quotes"` |
| block name, item name | `"minecraft:stone"`. A block state is the name with `properties` beside it: `{ "block": "minecraft:oak_log", "properties": { "axis": "x" } }` |
| `namespace:name` | `"mypack:ruby_ore"` |
| biome name, sound name, tab name | the same quoted `namespace:name` form |
| dimension id | `"minecraft:overworld"`, `"minecraft:the_nether"`, `"minecraft:the_end"` or a pack's own `"mypack:verdant"`. The 1.12.2 numbers `0`, `-1` and `1` are still taken as those three |
| hex color | six hex digits, `"A0C8FF"`, `#` optional |
| texture path | `"mypack:block/ruby_ore"` |
| list of ints | `[4, 12]` |
| list of block names | `["minecraft:stone", "minecraft:andesite"]` |
| list of biome names | `["minecraft:windswept_hills", "mypack:ruby_hills"]` |
| list of biome types | `["mountain", "forest"]`, the type words listed under [Value lists](#value-lists), each standing for a biome tag |
| list of mod ids or pack namespaces | `["quark", "mypack"]` |
| list of objects | `[{ "potion": "minecraft:strength", "amplifier": 1 }]`, keys per that object's own table |
| object | `{ "type": "cluster" }`, keys per its own table |
| object of role to biome, of variant name to variant | keys are the first thing, values the second: `{ "ocean": "mypack:ruby_ocean" }` |

Most definitions also accept `requires`, a list of mod ids or pack namespaces that must be present or the file is skipped.

## The one rule

Open the jar, find the file you want to change, and copy its path from `assets` or `data` onwards:

```
assets/minecraft/textures/block/iron_ore.png                 (in the Minecraft jar)
rdploader/assets/minecraft/textures/block/iron_ore.png       (your override)

data/minecraft/loot_tables/blocks/iron_ore.json              (in the Minecraft jar)
rdploader/data/minecraft/loot_tables/blocks/iron_ore.json    (your override)
```

The path after `assets` or `data` is always identical to the path inside the jar. Nothing is renamed or moved.

## Organizing packs

Loose files work under `rdploader/assets/<namespace>/` and `rdploader/data/<namespace>/`. Grouping works too, as a zip. A folder in `rdploader` is never a pack: it is skipped with a warning in the log, so zip a pack up before it goes there. When zipping, select the contents and zip those, not the folder holding them: a zip whose top level is one folder wrapping `assets` or `data` is skipped, and the log says so.

```
rdploader/assets/minecraft/textures/block/iron_ore.png
rdploader/MyTextures.zip
```

**Priority.** When two packs contain the same file, prefix the names with `RDPL` and a number; higher numbers load later and win:

```
rdploader/RDPL0 BaseTextures.zip
rdploader/RDPL1 SeasonalTextures.zip
rdploader/RDPL9 ModFixes.zip
```

Case-insensitive; a space, dash or underscore after the number is optional; the prefix is hidden from the display name. An unprefixed pack loads first and loses to any numbered pack. Priority also orders worldgen entries, which matters when one pack lays down blocks another replaces.

**Disable a pack** by appending `.disabled` to its name.

A `pack.mcmeta` at the root of the zip is welcome but not needed: the mod presents every pack to the game under one entry of its own, with the pack format the game expects, so a pack never goes stale on a format number. Put a `pack.png` beside it to give the folder's entry an icon.

## Resource packs: who wins

By default RDPL files sit above the resource packs a player selects, so a resource pack cannot override them. Add `O` or `N` after the `RDPL` prefix to decide per pack:

```
rdploader/RDPLO Branding        always wins; resource packs cannot touch it
rdploader/RDPLN BaseTextures    a resource pack can override it
rdploader/RDPL1O Seasonal       priority and override combined
```

Packs without a letter follow the `overrideResourcePacks` config option. `/rdpl list` marks the packs that override. The letter must end the prefix (followed by a space, dash, underscore, or nothing), so `RDPLOverhaul` is a pack named `Overhaul`, not an `O` flag.

The same tiers cover data packs. A pack marked `N` sits below the data packs a world carries in its own `datapacks` folder, and one marked `O` sits above them.

## Mod API

A mod can ship RDPL content inside its own jar, so it needs no separate pack. Put a folder named `rdploader` at the root of the jar and lay it out exactly like a pack:

```
thatmod.jar
  META-INF/mods.toml                (1.21.1: META-INF/neoforge.mods.toml)
  rdploader/data/thatmod/blocks/ruby_ore.json
  rdploader/assets/thatmod/textures/block/ruby_ore.png
```

What a mod ships is a default, not an override. It loads below every pack in the pack folder, so anything a pack author writes wins over it, and a mod may only supply files under a namespace it declares in its own mods file. Files under any other namespace are ignored with a warning, and so is a nested `rdploader` folder inside a namespace, so a mod cannot quietly redefine another mod's content or a pack author's.

Every mod that ships one gets an entry in `rdploader/config/mods.json` the first time it is seen:

```json
{
  "thatmod": {
    "enabled": true,
    "priority": -1
  }
}
```

| Field | Values | Default | What it does |
| --- | --- | --- | --- |
| `enabled` | `true` or `false` | `true` | Turns that mod's content off, the way `.disabled` turns off a pack |
| `priority` | `-1` or a number | `-1` | `-1` holds the mod under every pack; any other number puts it in the ordinary [priority](#organizing-packs) order beside the numbered packs |

A mod pack never joins the resource pack override tier whatever `overrideResourcePacks` says, since only a pack author can ask for that with the `O` letter. The log marks mod packs and lists packs lowest first, so nothing loads unseen.

## Packs written for 1.12.2

A pack made for the 1.12.2 line loads as it is. The loader recognizes one by its `pack.mcmeta` format, by definition folders under `assets/` with no `data/` beside them, or by a `.lang` file, and carries it forward: a zip is written out as a pack of this version under its own name, with everything below already done, and the 1.12.2 zip it came from is kept beside it as `<name>_converted.zip.disabled`, so nothing is lost and the new pack is yours to finish and edit. Loose files under `rdploader/assets` are not rewritten; they are read through the same port every time the folder is scanned.

- Definition folders move from `assets/<namespace>/` to `data/<namespace>/`, and the vanilla data folders with them: recipes, loot tables, loot injections, advancements, functions and structures.
- `textures/blocks/` and `textures/items/` are served as `textures/block/` and `textures/item/`, in models, in pixel maps and in the files themselves. An item model at `models/item/<file>/<variant>.json` is served as `models/item/<variant>.json`.
- Every id with metadata, `minecraft:wool:14` or `minecraft:dye:4`, is run through the game's own data fixers, the same code that upgrades a 1.12.2 world, so it comes out as the block or item it became: `minecraft:red_wool`, `minecraft:lapis_lazuli`. A block state that survived the flattening as a property, `minecraft:log:1` to `minecraft:oak_log` with `axis=y`, comes out as a `properties` object. The pack's own ids resolve through its own definitions: `mypack:materials:5` becomes the variant whose `meta` was 5, and `mypack:ruby_ore` the file's first variant, since each variant is a block of its own here. Entity and biome names are fixed the same way, and dimension numbers become ids.
- `variants` keep their keys; `meta` is dropped and `oreDict` becomes `tags` through the ore dictionary's mapping onto the convention tags. An `oredict/*.json` file becomes one item tag file per name it adds to. A bare `creativeTab` takes the pack's namespace.
- A `.lang` file is served as the `.json` the game reads, with `tile.mypack:file.variant.name` as `block.mypack.variant`, `item.` the same way, `itemGroup.x` as `itemGroup.mypack.x`, `fluid.x` as both fluid keys, and everything else as written.
- A 1.12.2 blockstate is not served at all. Its textures are read instead and served under the names the generator looks for, `textures/block/<variant>.png` with `_top` and `_bottom` where the blockstate had `end`, `top` or `bottom`, so the blockstate and the models are generated for each variant as they would be for a pack written here.
- Recipes lose their `data` and gain flattened ids, `forge:ore_shaped` becomes `minecraft:crafting_shaped` with `ore` ingredients as `tag`, loot tables lose `set_data` the same way, and an advancement's `item` with `data` becomes `items`.
- Functions are served as written, since a 1.12.2 command line is not something a port can rewrite, and the log says so. `block_drops` has no twin and is left out; a loot table does that here.

The log carries one summary line per ported pack and a line for each file it moved, left out or could not carry, and every key this version no longer reads is still named by the parser that meets it. The port is a best effort, not a finished pack: open the written zip, read those lines, and finish by hand what it names, starting with the functions and any texture it could not find a name for.

## Server-side packs

A pack can live on the server alone, with players on plain vanilla clients, under one constraint: **nothing in it may register anything**. The mod accepts any remote; the pack decides. A vanilla client plays with the registries it shipped with, so a pack that adds to them must be on both sides.

| Server alone is enough | Needs the pack on the client too |
| --- | --- |
| `worldgen`, `worldtemplates`, `gamerules`, `structures`, `structuremaps`, `citymaps`, `villages`, `pathintersects`, `caveregions`, `biomes`, `dimensions` | `blocks`, `items`, `fluids`, `materials`, `containers` |
| `recipes`, `recipe_removals`, `furnace`, `fuels`, `brewing`, `tags` | `potions`, `potion_types`, `sounds`, `tabs` |
| `loot_tables`, `loot_injections`, `player_loot`, `advancements`, `functions` | `entities`, `villagers`, `portalframes` |
| `gates`, `trades`, `registry_remap`, `teams`, `scoring`, `hardness`, `exposures`, `blastplaster` | `models`, `blockstates`, `textures`, `lang`, `worldintro`, `overrides` (client folders: with no client, leave them out) |
| the whole control layer, settings, and pregeneration | |

The right-hand column is a hard stop: blocks, items, entity types, sounds and potion effects a vanilla client does not have cannot be described to it, and a dimension's own portal is one of the pack's blocks. The left-hand column works because everything there either runs entirely server-side, reaches the client as data pack entries vanilla already reads (biomes, cave regions, dimension types, the damage types exposures make), or reaches it through packets vanilla already speaks (server-filled crafting result slot, ordinary advancement packets, status-message gate refusals, and a pregeneration hold made of vanilla game mode/title/teleport packets).

Setup:

1. Enable `vanillaClients` in the config (`content` category, needs a restart). It enforces the right-hand column: those folders are skipped at load and each skipped file is named in the log, so a slipped block file becomes a log line instead of a refused connection.
2. Keep definitions out of the right-hand folders anyway; skipped files are dead weight. Where the pack references items (a gate's `hold`, `killedDrops`, recipe outputs, trades), name only items vanilla or the server's other both-sided mods provide. A biome that names the pack's own ground blocks keeps the base biome's ground, and a dimension opened by its own portal needs that portal block, so send players there by command.
3. Entity variants are entity types of their own on this version, so they belong to the right-hand column: with `vanillaClients` on they are skipped, their spawns with them, and the log names them.
4. Install on the server as usual, with Blast Plaster, which the mod requires and which registers nothing either. Nothing goes on players' machines; `/rdpl` will not exist for them.
5. Test with one clean vanilla client join of the same version. Failures are loud: the connection is refused at the door, not quietly broken later.
6. Two accepted cosmetic gaps: server-added recipes craft but do not appear in the recipe book, and the hold while land is made is a plain spectator hold with the progress on the action bar, without the fog and the logo the mod's own client draws.

# Overriding

## What you can override

- **Anything in a mod's assets folder**, textures, models, blockstates, language files, sounds, fonts, splash texts, guide books, manuals
- **Advancements, loot tables, tags and functions**, server side, so they work on dedicated servers too
- **Recipes**, replace a mod's recipe or add your own
- **Structure templates**, the `.nbt` files mods use for generated buildings, under `<namespace>/structures/`
- **Registry renames**, keep old worlds working when a mod renames a block or item
- **Recipe removals**, delete a crafting recipe by name, namespace or output
- **Loot injections**, add a pool to a loot table instead of replacing the whole thing
- **Player loot**, roll a loot table when a player dies, on top of what they were carrying or instead of it
- **Properties of existing blocks, items and potions**, hardness, light, stack sizes, food on anything, a potion's effects, see [Property overrides](#property-overrides)
- **Furnace recipes, fuel burn times, creative tabs and sound events**

What a block drops is its loot table on this version, so there is no separate block-drops file: to change what stone drops, ship `data/minecraft/loot_tables/blocks/stone.json`, and to add to it without replacing it, a loot injection.

RDPL is good for replacing one or two recipes, and recipes for your own content should be added in the pack alongside it. For full recipe control across a modpack, KubeJS and CraftTweaker are the better options, and a file here still replaces the original completely, so to change one ingredient or drop one loot entry, use those.

### Pack options

Every key an option file accepts:

```json
{
  "hide": false,
  "enableTestingContent": true,
  "enableLoserBlocks": {
    "default": false,
    "hide": true,
    "description": "Registers the loser blocks"
  }
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| an option name | yes | boolean, or an object | | `true` or `false` is the option's default. An object carries the three keys below |
| `hide` at the top level | no | boolean | `false` | Keeps this pack's options out of the options screen and out of the generated file entirely, while they still gate content at their defaults |
| `default` | no | boolean | `false` | The option's value until the user changes it |
| `hide` inside an option | no | boolean | `false` | Hides that one option, so it cannot be flipped and stays at its default |
| `description` | no | string | none | Shown under the option's name in the options screen |

A pack can carry a `config` folder beside its `assets` and `data`, holding JSON files of true/false options with their defaults:

    PackA.zip/config/options.json
    { "enableTestingContent": true, "enableLoserBlocks": false }

A file with `"hide": true` at the top level keeps that pack's options out of the options screen and the generated file entirely, while the options still gate content at their defaults. Two things want that: content that is not ready to ship, and template packs, where the options are machinery holding the definitions together rather than a choice anyone should be making. Remove the key to publish them. The same works per option: `"hide": true` inside an option's object hides just that one, so a finished pack can carry a switch for unfinished content, or a template gate, without either showing up:

    { "enablePackB": { "default": false, "hide": true } }

Since a hidden option cannot be flipped, one hidden with its default true is effectively forced on, for content that must stay wired through the option machinery but is not a choice.

An option can also be an object carrying a description, shown under its name in the options screen:

    { "enableTestingContent": { "default": true, "description": "Registers the test blocks and items" } }

On launch the pack's option files become one real config file the user owns, named after the pack, `rdploader/config/PackA.json`, created with the pack's defaults and merged on pack updates so new options arrive without touching what the user already set. Changes apply on the next game start, and the Pack Options screen on the title screen is where a player flips them. Options belong to named packs only, that is zips, since the generated file is named after the pack; loose files under `rdploader/assets` and `rdploader/data` have no pack name and carry no options, so zip loose content into a named pack if it needs a switch.

Any definition's `requires` list can then name an option with a `config:` entry: `"requires": ["config:enableTestingContent"]` registers that content only while the option is true, exactly as a missing mod would skip it. A bare name checks every pack's file and every pack defining it must agree; `"config:PackA:enableTestingContent"` names one pack. An option no pack defines counts as false and is warned about once.

An option that gates something a world was made with is remembered by that world. Change it and open the world again, and the world is backed up first, to the game's own `backups` folder, exactly as the Edit World screen does; the first player into the overworld is told which options changed and that the copy was made.

A `file:` entry gates on a file or folder existing under the game folder, for coupling content to something outside RDPL's own packs, such as another mod's resource pack: `"requires": ["file:config/StarMaker/resources/0_jackspace2_celestialpack.zip"]` registers the content only while that exact file is installed. The path is relative to the game folder, always with forward slashes, and may not contain `..`.

### Inheriting definitions

A block or item definition can start from another in the same kind with `"inherits"`, naming any variant's registry name, then override whatever differs:

    { "inherits": "mypack:ruby_ore",
      "variants": { "sapphire_ore": { "hardness": 4.0 } } }

The child copies every stat of the parent's file and the named variant, file order never matters, chains resolve parent-first, and a circle or a missing parent is logged and leaves the child as written. Fields the child writes replace the inherited value; nested variant properties override one by one, but lists such as `requires` replace whole, so write the full list wanted. Blocks inherit only from blocks and items only from items.

### Block and item templates

A parent can be a pure template that never enters the game, since inheritance reads the definition files themselves, not what registered. Gate the template behind a hidden option that is forced off, and it registers nothing while its stats stay inheritable:

`config/options.json`

```json
{
  "templates": { "default": false, "hide": true, "description": "Never on, parents only" }
}
```

`data/jacksmod/blocks/ore_template.json`

```json
{
  "type": "ore",
  "material": "rock",
  "soundType": "stone",
  "harvestTool": "pickaxe",
  "harvestToolLevel": 2,
  "creativeTab": "jacksmod:tab",
  "expDrop": { "min": 2, "max": 5 },
  "requires": ["config:templates"],
  "variants": {
    "ore_template": { "hardness": 3.0, "resistance": 5.0 }
  }
}
```

`data/jacksmod/blocks/jacks_ore.json`

```json
{
  "inherits": "jacksmod:ore_template",
  "requires": [],
  "variants": {
    "jacks_ore": { "hardness": 4.0 }
  }
}
```

The template never registers, while `jacks_ore` registers with the template's material, sound, tool, tab, exp drops and resistance, overriding only hardness. The child must write its own `requires`, here cleared to an empty list, because it inherits the parent's otherwise and would vanish with it.

### Structures at exact places

Vanilla structures pin to exact spots with `structureAt` in the `terrain` settings, as `structure=x,z` entries, one per line: `"structureAt": ["villages=1000,-500"]`. **The x and z are block coordinates, not chunk coordinates**, and the structure generates in the chunk that holds that block. One entry per wanted instance. Its spacing, separation, minimum spawn distance and flat-ground checks all stand aside, so the spot is the pack's responsibility, and two pins closer than a chunk apart put two structures in the same chunk. The structure seats to the ground at its chunk by the usual rules once founded.

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `structureAt` | list of `structure=x,z` | none | Pins a vanilla structure to an exact spot, one entry per wanted instance. The x and z are block coordinates, and the structure generates in the chunk that holds that block; its spacing, separation, minimum spawn distance and flat-ground checks all stand aside |

An `imprint` entry pins the same way with `"at": [x, z]` in its shape, placing exactly once at those coordinates on the surface when that chunk generates, instead of by chance. It composes with `locateAs`, so a pinned structure can also be found.

### Finding placed structures

An `imprint` entry with `"locateAs": "Crypt"` registers every structure it places under that name, and `/rdplserver locate Crypt` then points at the nearest one, with the name offered in tab completion; `/rdplserver goto Crypt` carries you there. Only structures that have already generated can be found, since pack structures are placed by chance as chunks are made rather than on a grid the game could predict. The names live in the world's save, so they survive restarts and work on servers. A name registered this way can also be given its own permission with `gotoPlaceLevels`, so a pack decides who may be carried to its own structures separately from the vanilla ones.

## Property overrides

`<namespace>/overrides/<target>/<name>.json`

The path names the target: everything after `overrides/` is the namespace and name of the block, item or potion type being changed.

Everything else in this chapter replaces a file or adds one. An override does neither: it changes the properties of a block, item or potion type that already exists, vanilla or modded, without touching any of its files. The path names the target, so `overrides/minecraft/stone.json` changes `minecraft:stone`, and `overrides/tconstruct/<name>.json` changes that mod's block the same way.

Every key, shown at once. A real file writes only the ones it needs.

```json
{
  "requires": ["tconstruct"],
  "hardness": 0.1,
  "resistance": 3.0,
  "slipperiness": 0.98,
  "light": 10,
  "lightOpacity": 0,
  "soundType": "glass",
  "harvestTool": "pickaxe",
  "harvestToolLevel": 2,
  "flammability": 5,
  "fireSpread": 5,
  "maxStackSize": 16,
  "maxDamage": 250,
  "containerItem": "minecraft:bucket",
  "food": {
    "heal": 4,
    "saturation": 0.3,
    "alwaysEdible": true,
    "effects": [
      { "potion": "minecraft:speed", "duration": 200, "amplifier": 1, "ambient": false, "showParticles": true }
    ]
  },
  "effects": [
    { "potion": "minecraft:levitation", "duration": 200, "amplifier": 0, "ambient": false, "showParticles": true }
  ]
}
```

Every key is optional and a file changes only what it names, so a file at `overrides/minecraft/stone.json` holding `hardness`, `light` and `soundType` alone makes stone mine almost instantly, glow, and sound like glass. One file carries block, item and potion keys together. These apply when the target is a block:

| Key | Value | What it does |
| --- | --- | --- |
| `hardness` | float | Mining time, the same figure a block definition takes |
| `resistance` | float | Blast resistance |
| `slipperiness` | float | `0.6` is ordinary ground, `0.98` is ice |
| `light` | `0` to `15` | Light given off |
| `lightOpacity` | `0` to `15` | How much light the block stops |
| `soundType` | one of the sound types | Step, place and break sounds |
| `harvestTool` | tool class | What mines it, written into the tool tags; `harvestToolLevel`, default `0`, sets the tier |
| `flammability` | int | How readily it burns away; `fireSpread`, default `5`, how readily fire reaches it |

And these when the target is an item:

| Key | Value | What it does |
| --- | --- | --- |
| `maxStackSize` | `1` to `64` | Stack size |
| `maxDamage` | int | Durability |
| `containerItem` | item name | Left behind in the crafting grid, the way a bucket is |
| `food` | object | Makes the item edible, see below |

A name that is both a block and an item, and every placeable block's item is, takes both groups from one file:

```json
{
  "hardness": 0.2,
  "food": {
    "heal": 4,
    "saturation": 0.3,
    "alwaysEdible": true,
    "effects": [
      { "potion": "minecraft:speed", "duration": 200, "amplifier": 1 }
    ]
  }
}
```

At `overrides/minecraft/oak_planks.json` that makes planks break about as fast as dirt and lets them be eaten. `food` takes `heal` (`1`), `saturation` (`0.6`), `alwaysEdible` (`false`; `true` allows eating on a full hunger bar) and `effects`, whose entries are written exactly like a potion type's. An item that is already food takes new `heal`, `saturation` and `alwaysEdible`; `effects` on one of those is not supported, and the log says so. When the edible item places a block, aim at the sky to eat, since aiming at a block places it: that is vanilla's use order, not a bug.

`effects` at the top level of the file rewrites a potion type's effect list outright:

```json
{
  "effects": [
    { "potion": "minecraft:levitation", "duration": 200, "amplifier": 0 }
  ]
}
```

At `overrides/minecraft/swiftness.json` the Potion of Swiftness now grants Levitation. Each entry takes `potion` (required), `duration` (`3600`), `amplifier` (`0`), `ambient` (`false`) and `showParticles` (`true`), the same as in `potion_types/`, and the list may not be empty.

A target another mod owns should carry that mod in `requires`, so the file is skipped quietly when the mod is not installed instead of being reported as a missing target:

```json
{
  "requires": ["tconstruct"],
  "hardness": 1.0
}
```

Overrides are live. The original values are remembered before the first change, so disabling the pack and running `/rdplserver reload` snaps everything back to what it was, no restart needed; the same happens on every world entry. One file per target: when two packs override the same thing, the later pack's file replaces the earlier one whole, and the log says so.

Two limits worth knowing. A block or item whose own code computes a property ignores the field behind it, so the override applies but changes nothing; vanilla only does this for stairs' blast resistance, but mods are free to do it anywhere. And made-edible items only work on items with no right-click behavior of their own: an item that already does something when used keeps doing that.

Overrides need the pack on the client as well as the server, since mining speed, light and eating all happen on the player's screen, so they are not for server-side packs. `overrides` in the `content` config category turns the folder off entirely.

## Registry renames

`<namespace>/registry_remap/*.json`

The file name is yours to choose, only the folder is read, and several files stack.

When a mod renames one of its blocks or items, worlds saved before the rename lose them. Drop a file here to map the old name to the new one:

```json
{
  "registry": "minecraft:item",
  "mapping": { "oldmod:old_name": "newmod:new_name" }
}
```

The registry is the one the entry belongs to, named as the game names it: `minecraft:item`, `minecraft:block`, `minecraft:entity_type` and so on. Renames chain, so mapping A to B and later B to C sends A straight to C.

## Player loot

`<namespace>/player_loot/*.json`

The file name is yours to choose, only the folder is read, and several files stack.

The game gives players no loot table of their own: death drops only the inventory, and there is no table name a pack could override. RDPL adds one, rolled when a player dies:

```json
{
  "table": "mypack:entities/player",
  "mode": "add",
  "rollOnKeepInventory": false,
  "dropLoose": false
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `table` | yes | table name | | The loot table rolled when a player dies |
| `mode` | no | `add` or `replace` | `add` | Whether the table's items join the inventory or take its place |
| `rollOnKeepInventory` | no | boolean | `false` | Whether the table is rolled at all on a death that kept the inventory |
| `dropLoose` | no | boolean | `false` | Whether the items are put on the ground directly instead of joining the death drops |

`add` drops the table's items alongside the inventory, which suits kill bounties. `replace` discards the inventory and drops only what the table rolls.

With `rollOnKeepInventory` off, deaths under `keepInventory` (and spectator deaths, which always keep the inventory) roll nothing. Turning it on keeps deaths costly on keep-inventory worlds.

Multiple files stack, each evaluated on its own terms. If any applicable entry is `replace`, the inventory is cleared once before rolling, so an `add` entry alongside it still lands.

The table is an ordinary loot table looked up by name: it can live in the pack at `loot_tables/entities/player.json`, be any vanilla or mod table, and be reached by `loot_injections`. Loot context: the dying player is the looted entity, the killer (if any) is the killing player, and the damage source is set, so `killed_by_player`, `entity_properties`, `random_chance_with_looting` and the rest all behave normally.

One loot function is RDPL's own, usable in any table with a looted entity: `rdpl:killed_name` names the dropped item after the victim. `format` shapes the display name (`%s` is the victim, default just the name), and `tag` instead writes the plain name into an NBT string key for items that read it themselves.

```json
{ "item": "mypack:human_skull", "weight": 1,
  "functions": [ { "function": "rdpl:killed_name", "format": "%s's Skull" } ] }
```

**Grave mods.** Rolled items join the ordinary death drops before any grave mod reads them, so they end up in the grave with everything else (`replace` puts the table's contents in the grave instead of the inventory). No setup required.

`dropLoose` bypasses the drop list entirely: the items are placed in the world directly, so grave mods never see them; the inventory goes in the grave, the table's items lie on the ground for the killer. Use it for spoils that belong to the killer rather than the victim's grave. Without a grave mod it changes little. Caveat: the items exist before anything downstream could cancel the drops, so entries that must not survive a canceled death should leave it off.

Set `playerLoot` in the `data` config category to `false` to turn the folder off entirely.

---

# Defining new content

## How definitions work

Alongside the folders that override files, there are folders that describe new things. A definition file groups one or more things of a kind under `variants`, and each key inside `variants` is a registry name: `data/mypack/blocks/ore.json` holding a variant called `ruby_ore` registers `mypack:ruby_ore`. The file's own name is a grouping and nothing more; one file can hold one block or a dozen that share their settings.

Registration happens at the lowest priority the loader offers, so if a real mod registers the same name, the mod wins and your file is ignored. Nothing here can replace a mod.

**Where the line is.** Anything needing a block entity of its own, a screen, an inventory or per-tick logic of its own needs a real mod, with one exception: the [container](#containers) type, which carries an inventory and a screen of its own. Everything short of that is fair game.

### Your namespace is your mod

The namespace you choose is, for every practical purpose, a mod id. Nothing is loaded as a mod and it never appears in the mod list, but everything that reads a mod id reads yours:

- Registry names are `mypack:ruby_ore`, exactly as a mod's would be, and they are written into every saved world that contains them.
- The ore, biome and recipe whitelists in the config match it, so `oreWhitelist = mypack` keeps your ore and blocks everyone else's.
- `/rdpl which`, `/rdplserver oregen` and the reports all group by it.
- JEI, tags and other mods' lookups see it the same way.

So pick one name at the start and never change it. Renaming a namespace orphans everything already placed in a world, the same as a mod changing its id, that is what `registry_remap` exists to repair.

This works both ways: `requires` accepts a pack namespace as readily as an installed mod id, so one pack can depend on another and be skipped when it isn't installed.

A mod or a pack named in `requires` that is not installed skips the definition: one line goes to `logs/rdpl.log` naming what was missing, and the game carries on. If a block you expected is not in the creative tab, that log line is the first place to look.

`requires` takes bare ids only. There is no version range syntax, so it can say a mod must be present but not which version.

The mod's own id, `resourcedatapackloader`, is reserved. Defining content under it is ignored and logged, because it would claim ownership of things this mod registers. Overriding this mod's own assets is still fine, only registering content there is not.

Every table below follows the conventions in [Reading the tables](#reading-the-tables).

Most definitions also accept `requires`, a list of mod ids or pack namespaces that must be present or the file is skipped.

## Blocks

`<namespace>/blocks/*.json`

Each key inside `variants` is a block, registered under the pack's namespace: a file holding `ruby_ore` and `deep_ruby_ore` registers `mypack:ruby_ore` and `mypack:deep_ruby_ore`, sharing every setting the file writes outside `variants`. The file's own name is only a grouping.

Every key, shown at once. A real file writes only the ones it needs. A key marked for one type is read only by that type.

```json
{
  "inherits": "mypack:ore_template",
  "type": "ore",
  "material": "rock",
  "soundType": "stone",
  "mapColor": "red",
  "harvestTool": "pickaxe",
  "harvestToolLevel": 2,
  "silkHarvest": true,
  "opensWith": "mypack:ruby_key",
  "openSound": "block.chest.open",
  "expDrop": { "min": 3, "max": 7 },
  "creativeTab": "mypack:tab",
  "renderLayer": "solid",
  "opaque": true,
  "fullCube": true,
  "slipperiness": 0.6,
  "flammability": 0,
  "fireSpread": 0,
  "explosionResistanceDivisor": 1.0,
  "modelBlock": "minecraft:stone",
  "itemModel": "state",
  "tint": "biome",
  "plantTypes": ["plains", "crop"],
  "behavesAs": ["till", "path"],
  "bounds": [0.0, 0.0, 0.0, 1.0, 1.0, 1.0],
  "requires": ["mypack"],
  "particle": "colored",
  "particleColor": "C0304A",
  "smoke": true,
  "leafSapling": "mypack:ruby_sapling",
  "leafSaplingChance": 5,
  "seed": "mypack:ruby_seed",
  "produce": "mypack:ruby_fruit",
  "maxAge": 7,
  "growth": { "stages": 8, "growth": 10 },
  "sapling": { "log": "mypack:ruby_log", "leaves": "mypack:ruby_leaves" },
  "portal": { "dimension": "mypack:ruby_world" },
  "variants": {
    "ruby_ore": {
      "hardness": 3.0,
      "resistance": 5.0,
      "light": 0,
      "harvestLevel": 2,
      "rarity": "rare",
      "maxSize": 64,
      "tags": ["forge:ores/ruby", "forge:ores"],
      "drops": [
        { "block": "mypack:ruby", "amount": { "min": 1, "max": 2 }, "bonusChance": [1, 2] }
      ]
    },
    "deep_ruby_ore": {
      "hardness": 4.5,
      "resistance": 8.0,
      "light": 3
    }
  }
}
```

### Types

| Type | What you get |
| --- | --- |
| `basic` | A plain block. Used when `type` is missing |
| `ore` | Drops something other than itself, with fortune and silk touch |
| `falling` | Falls like sand or gravel |
| `slab` | Bottom, top and double, and two of them merge in hand |
| `stairs` | Corners and slopes handled for you |
| `fence` | Connects to its neighbors, and to fences from other mods |
| `pane` | Connects like glass panes |
| `wall` | Connects like cobblestone walls, with the post shape |
| `door` | Two blocks tall, opens by hand and answers to redstone |
| `trapdoor` | A hinged flap on the top or bottom of a block, opened by hand or by redstone |
| `fence_gate` | A gate in a fence line, opened by hand or by redstone, and lowered where it meets a wall |
| `banner` | A banner on a post or against a wall, sixteen standing rotations, carrying your own design |
| `ladder` | Climbable, placed against a wall |
| `torch` | Wall and floor placement, with a particle |
| `log` | Rotates to the face you place it against |
| `leaves` | Decays, shears, tints and drops a sapling |
| `sapling` | Grows into a tree or into one of your structures |
| `crop` | Grows through stages, drops a seed and a produce item |
| `flower` | A one-block plant standing on soil |
| `cane` | Grows upward in a column, like reeds or cactus |
| `vine` | Climbs and hangs on the sides of blocks |
| `portal` | Sends whatever walks in to another dimension |
| `container` | Holds an inventory a player can open, of any size, and can fill itself from a loot table the first time it is opened. Draws as an ordinary block or as a chest, whichever the pack asks for |

### File keys

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `variants` | yes | object of variant name to variant | | One block per entry. The key is its registry name, and names its blockstate, its models, its textures and its lang key |
| `type` | no | one of the types above | `basic` | Which shape the block takes |
| `material` | no | one of the [block materials](#value-lists) | `rock` | Mining behavior, pistons, fire and liquids |
| `soundType` | no | one of the [sound types](#value-lists) | from the material | Footsteps, breaking and placing |
| `mapColor` | no | one of the [map colors](#value-lists) | from the material | How it looks on a map |
| `harvestTool` | no | `pickaxe`, `axe`, `shovel`, `hoe` | `pickaxe` | Which tool harvests it, written into the game's `mineable` tags for you |
| `harvestToolLevel` | no | 0 to 4 | `0` | 0 wood, 1 stone, 2 iron, 3 diamond, 4 netherite, written into the `needs_*_tool` tags for you |
| `silkHarvest` | no | boolean | `true` | Whether silk touch returns the block itself |
| `opensWith` | no | item id | none | Makes the block a lockbox: breaking it drops the block itself, and right-clicking with the named item consumes one, plays the block's break sound, pays out the variant's `drops` list and removes the block. Any other click shows the action-bar line `block.<pack>.<block>.locked` from the lang files |
| `openSound` | no | sound name | the break sound | What a lockbox plays when opened instead of its break sound |
| `expDrop` | no | object with `min` and `max` | none | Experience dropped when broken without silk touch |
| `creativeTab` | no | tab name | none | The tab it appears in |
| `renderLayer` | no | `solid`, `cutout`, `cutout_mipped`, `translucent` | to suit the type | How it is drawn |
| `opaque` | no | boolean | `true` | Whether it blocks sight and light entirely |
| `fullCube` | no | boolean | same as `opaque` | Whether it fills its whole space |
| `slipperiness` | no | float | `0.6` | Ice is `0.98` |
| `flammability` | no | int | `0` | How readily fire consumes it |
| `fireSpread` | no | int | `0` | How readily fire spreads from it |
| `explosionResistanceDivisor` | no | float | `1.0` | Divides each variant's `resistance` against explosions |
| `modelBlock` | no | block name | `minecraft:stone` | Block whose model is borrowed when yours ships no texture and no model of its own |
| `itemModel` | no | `state`, `item` | `state` | `state` draws the item as the placed block, `item` looks for your own `models/item/<name>.json` |
| `tint` | no | `biome`, `none`, or a hex color | none | Needs a `tintindex` in the model to show |
| `plantTypes` | no | list of [plant types](#value-lists) | none | What can be planted on it |
| `behavesAs` | no | list of `till`, `path` | none | Vanilla behaviors to take on |
| `bounds` | no | list of six numbers, 0 to 1 | full block | The collision box, as `[x1, y1, z1, x2, y2, z2]` |
| `requires` | no | list of mod ids or pack namespaces | none | The file is skipped unless all are present |
| `particle` | torch only | `none`, `flame`, `colored` | `flame` | The particle above a torch |
| `particleColor` | torch only | hex color | `FFFFFF` | Used when `particle` is `colored` |
| `smoke` | torch only | boolean | `true` | Whether it smokes |
| `leafSapling` | leaves only | block name | none | The sapling they drop |
| `leafSaplingChance` | leaves only | int | `5` | One in N leaves drops one |
| `seed` | crop only | item name | none | The item that plants it |
| `produce` | crop only | item name | none | What harvesting yields |
| `maxAge` | crop only | int | `7` | How many growth stages |
| `growth` | plants only | object | none | See [Growth](#growth) |
| `sapling` | sapling only | object | none | See [Saplings](#saplings) |
| `portal` | portal only | object | none | See [Portals and gates](#portals-and-gates) |
| `container` | container only | object | none | See [Containers](#containers) |

### Variant keys

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `hardness` | no | float | `1.0` | How long it takes to break. Obsidian is `50`, `-1` is unbreakable |
| `resistance` | no | float | `5.0` | Blast resistance |
| `light` | no | 0 to 15 | `0` | Light emitted |
| `harvestLevel` | no | 0 to 4 | the file's value | Overrides the tool tier for this variant |
| `rarity` | no | `common`, `uncommon`, `rare`, `epic` | `common` | Name color in the tooltip |
| `maxSize` | no | 1 to 64 | `64` | Stack size |
| `tags` | no | list of tag ids | none | Block and item tags this variant is written into, such as `forge:ores/ruby` on 1.20.1 or `c:ores/ruby` on 1.21.1. The tag files are generated for you |
| `drops` | no | list of drops | drops itself | What breaking it yields |

**Names are permanent.** A variant's key is written into every saved world that contains it. Renaming one later turns placed blocks into air, unless a [registry rename](#registry-renames) maps the old name to the new. A file may hold as many variants as it likes; each is a block of its own, and a `meta` key from a 1.12.2 pack is ignored with a note in the log.

### Drops

```json
{
  "drops": [
    { "block": "mypack:ruby", "amount": { "min": 1, "max": 3 }, "chance": 100, "guaranteed": true, "bonusChance": [1, 2, 3] },
    { "block": "minecraft:coal", "amount": 1, "chance": 25 },
    { "block": "minecraft:diamond", "weight": 1 },
    { "block": "minecraft:emerald", "weight": 4 },
    { "entity": "minecraft:silverfish", "amount": { "min": 1, "max": 2 }, "chance": 15 }
  ]
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `block` | one of the two | block or item name | | What is dropped |
| `entity` | one of the two | entity name | | An entity let out when the block breaks, instead of an item |
| `amount` | no | int or range | `1` | How many |
| `chance` | no | 0 to 100 | `100`, or `0` when `guaranteed` is off | How often the drop happens at all |
| `weight` | no | int | `0` | Above zero, the entry joins a pool that yields exactly one drop. See below |
| `bonusChance` | no | list of ints | none | Extra drops per fortune level, one entry per level |
| `guaranteed` | no | boolean | `true` | Legacy shorthand for `chance`. On is `100`, off is `0` |

Every entry with no `weight` is decided on its own, so a block with three of them can drop all three, or none. Give entries a `weight` and they stop being independent: they form one pool, exactly one of which is chosen each time the block breaks, the odds in proportion to the weights. Above, diamond and emerald share a pool at one to four, so one of the two always comes out and it is emerald four times in five, while the ruby and the coal are decided separately and the silverfish is its own thing again. Items and entities pool separately, so a weighted item and a weighted entity do not compete.

An entry naming an `entity` lets one out where the block stood, facing a random way, and a mob is given its usual spawn treatment for the local difficulty, so it arrives with the equipment and the effects it would have had. `amount` decides how many, `chance` how often, `weight` puts it in the entity pool. It happens as the block breaks, however it broke, so an explosion or a piston sets them loose the same as a pickaxe does. `bonusChance` and fortune mean nothing to an entity and are ignored.

A drop naming both a `block` and an `entity` uses the entity and says so in the log.

The drops are written into a generated loot table, `loot_tables/blocks/<name>.json` under the pack's namespace, unless the pack ships one of its own at that path, in which case the pack's file is what the block uses and `drops` is not read.

### Growth

For `crop`, `flower`, `cane` and `vine`.

```json
{
  "growth": {
    "stages": 8,
    "growth": 10,
    "spread": 1,
    "maxHeight": 3,
    "soil": ["minecraft:sand", "minecraft:red_sand"],
    "drop": "mypack:reed",
    "dropCount": 1,
    "needsSky": false,
    "needsWater": true,
    "waterRange": 2,
    "damage": false,
    "damageAmount": 1.0,
    "breaksNeighbors": false
  }
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `stages` | no | int | `16` | Growth stages before it is done |
| `growth` | no | int | | One in N chance per random tick to advance |
| `spread` | no | int | `0` | How far it spreads to neighboring blocks |
| `maxHeight` | no | int | `3` | Cane only. How tall the column grows |
| `soil` | no | list of block names | the type's usual | What it stands on |
| `drop` | no | item name | none | What it drops when broken |
| `dropCount` | no | int | `1` | How many |
| `needsSky` | no | boolean | `false` | Only grows where the sky is visible |
| `needsWater` | no | boolean | `false` | Only grows near water |
| `waterRange` | no | int | `1` | How far that water may be |
| `damage` | no | boolean | `false` | Hurts whatever touches it |
| `damageAmount` | no | float, half hearts | `1.0` | How much it hurts |
| `breaksNeighbors` | no | boolean | `false` | Breaks blocks placed beside it, like cactus |

### Saplings

Every key, shown at once. A real file writes only the ones it needs.

```json
{
  "sapling": {
    "soil": ["minecraft:grass_block", "minecraft:dirt"],
    "stages": 3,
    "chance": 5,
    "light": 9,
    "log": "mypack:ruby_log",
    "leaves": "mypack:ruby_leaves",
    "height": 5,
    "vines": false,
    "structure": "mypack:ruby_tree"
  }
}
```

A `structure` replaces the generated tree with one of your templates, which is the way to build something a generator cannot, and nothing else in the block needs writing. Name several under `structures` instead and the sapling picks one every time it grows, so a wood is not the same tree over and over:

```json
{
  "sapling": { "structure": "mypack:ruby_tree" }
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `soil` | no | list of block names | none | What it will grow on |
| `stages` | no | int | `2` | Growth stages before it becomes a tree |
| `chance` | no | int | `7` | One in N per random tick |
| `light` | no | 0 to 15 | `9` | Light level needed |
| `log` | no | block name | `minecraft:oak_log` | Trunk block |
| `leaves` | no | block name | `minecraft:oak_leaves` | Leaf block |
| `height` | no | int | `4` | Trunk height |
| `vines` | no | boolean | `false` | Hang vines from the leaves |
| `structure` | no | `namespace:name` | none | Grow into this template instead of a generated tree |
| `structures` | no | list | none | Several templates to grow into, one chosen each time it grows. Each entry is `{ "structure": "namespace:name", "weight": 3 }`, or a bare name for equal odds. Overrides `structure` |

## Models, blockstates and textures

Defining a block or item registers it. What it *looks* like is a set of asset files in the same folders and the same format the game uses, under your own namespace, and on this version most of them are written for you.

```
assets/mypack/textures/block/ruby_ore.png
assets/mypack/textures/item/ruby.png
assets/mypack/lang/en_us.json
```

**Ship a texture and the rest is generated.** For every block whose blockstate the pack does not ship, the mod writes the blockstate and the models the type needs, pointing at `textures/block/<name>.png` where `<name>` is the variant's key, and for every item that has no `models/item/<name>.json`, an item model pointing at `textures/item/<name>.png`. A block with neither a texture nor a model of its own borrows the look of `modelBlock`, stone by default, so nothing ever renders as the purple and black square. Ship a `blockstates/<name>.json` of your own and the mod generates nothing for that block and uses yours; the same for `models/item/<name>.json`.

| Type | Texture files it looks for | Generated from |
| --- | --- | --- |
| `basic`, `ore`, `falling`, `flower`, `sapling`, `cane`, `leaves`, `container` without a chest | `<name>` | `cube_all`, `cross` or `leaves` |
| `log` | `<name>` for the side, `<name>_top` for the ends | `cube_column` |
| `slab` | `<name>` | `slab`, `slab_top` and a `cube_all` double |
| `stairs` | `<name>` | `stairs`, `inner_stairs`, `outer_stairs`, all forty states written out |
| `fence` | `<name>` | `fence_post` and `fence_side` as a multipart, and `fence_inventory` for the hand |
| `wall` | `<name>` | the wall post and side templates as a multipart, and `wall_inventory` for the hand |
| `pane` | `<name>` for the pane, `<name>_top` for the edge | the five glass pane templates as a multipart |
| `door` | `<name>_top` and `<name>_bottom`, or `<name>` for both | the eight door models and their thirty-two states |
| `trapdoor` | `<name>` | the three orientable trapdoor models |
| `fence_gate` | `<name>` | the four gate models, closed and open, in a wall and out |
| `ladder`, `vine`, `torch` | `<name>` | the game's own template for each |
| `crop` | `<name>_stage0` up to `<name>_stage<maxAge>`, or `<name>` for all | one `crop` model per stage, `age=0` to `7` mapped onto them |
| `portal` | `<name>`, or the nether portal's | three portal slabs, one per axis |
| `banner` | its own sheet, see [Banners](#banners) | the game's banner model |
| `container` with `chestModel` | the chest sheet named in `chestModel` | the mod's `pack_chest` model |

Every texture is looked for under `textures/block/`, and the name is the variant's key, so a block registered as `ruby_ore` wants `textures/block/ruby_ore.png` and nothing else needs writing. A block whose item is drawn flat, a door, a ladder, a torch, a sapling, a flower, a cane, a vine or a pane, takes `textures/item/<name>.png` for the hand when it exists and its block texture when it does not.

**Items** take `textures/item/<name>.png` and a generated `item/generated` model, or `item/handheld` for a tool. Ship `models/item/<name>.json` to draw it any other way.

**Fluids** need no model at all; one is generated from the `still` and `flow` textures.

**A block with several variants is several blocks.** Each key under `variants` is registered on its own, so each has its own blockstate, its own models and its own textures, named after the key. There is no shared blockstate carrying a `blocks` property, and nothing in a blockstate needs to say which variant it is: `blockstates/ruby_ore.json` is ruby ore's, and `blockstates/deep_ruby_ore.json` is the deep one's.

### Writing your own

Everything generated can be replaced. A blockstate the pack ships is used as it is, in the game's own format: the vanilla `variants` keyed on the block's properties, or `multipart`. The properties are the game's own for each type: `axis` on a log, `type` on a slab, `facing`, `half` and `shape` on stairs, `facing`, `half`, `hinge` and `open` on a door, `facing`, `half` and `open` on a trapdoor, `facing`, `in_wall` and `open` on a gate, `age` on a crop and a cane, `stage` on a sapling, `north`, `east`, `south`, `west` on a fence or a pane with `up` added on a wall and a vine, `rotation` on a standing banner and `facing` on a wall one, `axis` on a portal. A `basic`, `ore`, `falling`, `leaves`, `flower` or `container` block has one state, keyed `""`.

Point the models at the parents that take textures, not at the finished vanilla ones: `cube_all` takes an `all`; `cube_column` an `end` and a `side`; `cross` a `cross`; the stairs parents `bottom`, `top` and `side`; `fence_post` and `fence_side` a `texture`; `template_wall_post` and `template_wall_side` a `wall`; the glass pane templates a `pane` and an `edge`; the door parents a `top` and a `bottom`; `template_orientable_trapdoor_*` and `template_fence_gate*` a `texture`; `template_torch` a `torch`; `crop` a `crop`; `vine` and `ladder` their own name. A model naming a finished vanilla model such as `oak_door_bottom_left` inherits vanilla's textures with it, whatever the blockstate says.

### Banners

A banner is the one type where the shape of the block and the shape of the model part ways, so it is worth setting out in full.

**It is two blocks in the game's eyes and one in yours.** One definition gives you the standing banner under your own name and the hanging one beside it, the way the game pairs every banner; the item places whichever fits, standing when you click the top of a block and wall when you click a side.

**The model is nearly two blocks tall.** A banner occupies one block for placement and collision, but it is drawn far outside it. The generated blockstate uses the game's banner model, drawn by the banner renderer from the sheet at `textures/entity/banner/<name>.png`, so give it a sheet laid out the way the vanilla banner's is. Its item wants a model of its own, with a `display` block that brings the scale down so it fits its slot.

**There are no colors or patterns on it.** The design is the texture, the same way a door's look is its texture, and one definition is one banner. Dyeing it and stacking patterns on it is not something a pack can reach.

**It takes the `material` you give it.** A stone banner is mined with a pickaxe like the stone it says it is.

### Textures written as pixel maps

A texture can be a JSON file instead of a PNG. Put it where the PNG would have gone with `.json` on the end of the whole name, so `textures/block/panel.png.json` answers every request for `textures/block/panel.png`. Nothing else changes: models point at `mypack:block/panel` as they always did, and the atlas, mipmaps and an animation `.mcmeta` all work, because what the game receives is still a PNG. The example pack ships not one PNG; every texture in it is a map.

```json
{
  "extends": "mypack:textures/block/panel_template",
  "size": "16x16",
  "palette": { "s": "#EDE9E2", "d": "#C6C1B5", "e": "#9E988C", "p": "#F6F4EF" },
  "tint": { "from": "#626669", "to": "#DBDFE2" },
  "notes": {
    "s": "the flat surface",
    "d": "shadow inside the border",
    "e": "the outer edge",
    "p": "the raised panel"
  },
  "rows": [
    "eeeeeeeeeeeeeeee",
    "edddddddddddddde",
    "edssssssssssssde",
    "edspppppppppssde",
    "edspppppppppssde",
    "edspppppppppssde",
    "edspppppppppssde",
    "edssssssssssssde",
    "edssssssssssssde",
    "edspppppppppssde",
    "edspppppppppssde",
    "edspppppppppssde",
    "edspppppppppssde",
    "edssssssssssssde",
    "edddddddddddddde",
    "eeeeeeeeeeeeeeee"
  ]
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `size` | yes, or inherited | `widthxheight` | | How many pixels across and down |
| `rows` | yes, or inherited | list of text | | One string per row of pixels, one character per pixel, from the top down |
| `palette` | yes, or inherited | object | | A character to a color, `#RRGGBB` or `#AARRGGBB` |
| `extends` | no | another pixel map | | The map this one starts from |
| `tint` | no | object with `from` and `to` | | Recolors everything inherited along a ramp between two colors |
| `notes` | no | object | | A character to a line saying what it is for, inherited and never drawn |

**There is no name to declare.** The file's own path is its name, exactly as a PNG's is, so a map at `assets/mypack/textures/block/panel.png.json` is `mypack:block/panel` in a model and a map at `assets/mypack/textures/item/gem.png.json` is `mypack:item/gem` in an item model. Nothing points at a pixel map specially; a block or an item names its texture the way it always did and never learns which of the two it got. That also means the block and item folders stay apart, as they do for PNGs: `textures/block/gem.png.json` and `textures/item/gem.png.json` are two different textures and are cached as two different files.

**Any size you like**, up to 4096 a side, and the two sides need not match. `16x16` is an ordinary block face, `16x32` is the sort of tall strip a door half or an animation wants. The size is checked rather than guessed: give one row per line of pixels and one character per pixel across, or the map is refused and the log names the row and what it found. A character with no color in the palette is left clear, so `.` or a space is a hole.

**Templates are the point of it.** `extends` names another pixel map, as `namespace:path` or a bare path in the same pack, and the file that extends it inherits its `size`, its `rows` and its `palette`. Anything it names itself wins, and it need not name everything, so a whole variant can be a handful of colors:

```json
{
  "extends": "mypack:textures/block/panel.png",
  "palette": { "s": "#AA7EB1", "d": "#8B6292", "e": "#6B4A72", "p": "#C5A1CB" }
}
```

That is a complete second texture: the same shape in purpur, and if the shape is ever redrawn in the template every variant follows. A variant may instead give its own `rows` and keep the template's palette, which is the other way round, the same colors in a different pattern. Inheritance runs up to eight deep, a loop is caught and reported, and a map naming a template nothing provides is reported rather than drawn blank.

**Which of two textures is the template** is settled by which one holds more distinctions, not by which was drawn first. A variant gives each character one color, so every pixel the template calls the same character comes out the same color in the variant. An ore drawn on stone therefore cannot inherit the stone's `rows`: the stone calls the speck positions plain stone, and nothing a variant can write splits one character into two. Turn it round and it works. Let the ore be the template, so the stone tones and the ore tones each hold characters of their own, and a second ore is four colors:

```json
{
  "extends": "mypack:textures/block/ore_template",
  "palette": { "4": "#768291", "5": "#5E6977", "6": "#66717F", "7": "#848F9D" }
}
```

A variant that really does want a different pattern gives its own `rows`, as above, and then inherits only the palette. That is worth doing when the colors are the point and the shape is incidental; when the shape is the point, put the shape in the template and let the variants name colors.

**A template need not be a texture at all.** A map is only served to the game when its path ends in `.png`, so a template at `textures/block/ore_template.json` is invisible to the game and exists purely to be extended, while one at `textures/block/ore_template.png.json` would also answer requests for `ore_template.png`. Name a shared shape without the `.png` and nothing can ask for it by accident.

**A template can be a real image instead of a map.** Point `extends` at a PNG that any pack or the game itself provides and the palette changes meaning: keys become the colors already in that image, values the colors to put in their place. Nothing is traced and no `rows` are written, so a pack can recolor a vanilla or mod texture where it stands:

```json
{
  "extends": "minecraft:textures/block/coal_ore.png",
  "palette": {
    "#3F3F3F": "#C4353F",
    "#343434": "#8E2029",
    "#373737": "#A32A33",
    "#454545": "#DE5F68"
  }
}
```

That is a ruby ore in vanilla's own stone: the four speck tones are swapped and every other pixel is left as it was. A color the image does not contain simply never matches, and the size comes from the image unless you name one, which must then agree.

`extends` prefers a pixel map: it looks for the map at that path first and only falls back to the image when no pack provides one. A name that is neither is reported rather than drawn blank. Building on an image is client-side work, since it is the game's own resources being read, so a dedicated server never does it.

**A template can be tinted instead of repainted.** `tint` names two colors and recolors everything the map inherits along the ramp between them. Each inherited color's brightness is its place on that ramp, so black lands on `from`, white lands on `to`, and every tone between is mixed in proportion. Transparency is left alone. That makes a grayscale template plus two colors a complete variant:

```json
{
  "extends": "mypack:textures/item/materials/ingot.png",
  "tint": { "from": "#626669", "to": "#DBDFE2" }
}
```

`from` may be left out, in which case it is black and the tint becomes an ordinary multiply, the same shape as a `tintindex` at render time. The difference is that this one is drawn into the PNG once and cached, so it costs nothing per frame and reaches a texture nothing tints, but it also cannot follow a biome the way `grass` or `foliage` can.

The template stays an ordinary map: open it, look at it, and it draws as the gray it is. Both colors take `#RRGGBB`, `#AARRGGBB` or a leading `0x`, and a value that is neither leaves the map undrawn rather than drawing it in the wrong color. A tint is inherited like everything else and the first one down the chain wins, so a variant's own tint beats the one it extends. It works on an image template too, where it runs after the palette's color swaps.

**A tint is a ramp between two colors**, so it only suits a texture whose tones sit on one. A shape with two unrelated regions, an ore's stone against its specks, is not that, and wants its palette written out instead.

**Knowing what a template's characters mean** is the awkward part of extending one, which is what the `notes` block above is for: a character to a short line, inherited the same way the palette is and never drawn. Label a template's characters and whoever extends it knows which to override.

`/rdpl pixelmap <namespace:path>` then reports what a map actually came out as, which is the reliable way to write a variant without opening every file up the chain:

```
oretest:textures/block/ruby_ore.png is 16x16
  built from oretest:textures/block/ruby_ore.png.json
  built from oretest:textures/block/gem_ore.png.json
  rows come from oretest:textures/block/gem_ore.png.json
  1  #C4353F  17 pixel(s)  set by ruby_ore.png.json  ore body, most of every lump
  2  #8E2029   8 pixel(s)  set by ruby_ore.png.json  ore shadow, the darkest tone
  a  #747474  86 pixel(s)  set by gem_ore.png.json   stone, the commonest tone
```

Every character is listed with its color, how many pixels it covers, which file in the chain set it and what that file says it is for. The path may be given the short way, `mypack:block/panel`, or in full. A character showing 0 pixels is one the palette names and the rows never use, which is usually a typo in a row.

**Drawn images are kept on disk** in `rdploader/pixelmap-cache`, under a folder per namespace and named after the texture with a hash of its source on the end. The hash covers the whole chain, the map itself and every template above it, so editing a template changes the stamp of every variant that inherits from it and they are all redrawn. When a map is redrawn its older files are swept away.

The folder is also gone over every time the packs are scanned, and any image whose map no pack provides any more is deleted, along with any folder left empty. Rename a texture, drop a pack, delete a map, and its cached image goes with it rather than sitting there for good. Deleting the whole folder costs nothing but the time to draw them again, and it is skipped when packs are scanned, so it is never mistaken for a pack.

A PNG always wins. If both `panel.png` and `panel.png.json` exist, the PNG is served and the map is never drawn, so a generated texture can be replaced by a painted one later without changing anything that points at it.

**Nobody has to write these files by hand.** The repository ships scripts for the whole round trip in [`pixelmap/`](../pixelmap): `png_to_pixelmap.py` turns one PNG into a map, `convert_pack.py` does it for every texture a pack holds, and `verify_pack.py` draws a converted pack's maps and compares them to the PNGs they came from, so a conversion can be trusted before the originals are put aside.

### Traps worth knowing

**A model naming a finished vanilla model inherits vanilla's textures too.** `torch`, `ladder`, `oak_door_bottom_left` and `wheat_stage0` all carry their own textures, so a model pointing at one gets vanilla's look no matter what you put beside it. Parent models such as `cube_all`, `cross` and `crop` take their textures from the model that names them and behave, and so do the door, trapdoor and gate templates.

**Names come from the language file.** A block or item shows a raw key until `lang/en_us.json` gives it one, and the keys are the game's own: `block.mypack.ruby_ore` for a block and the item that places it, `item.mypack.ruby` for an item, `itemGroup.mypack.tab` for a creative tab, `fluid_type.mypack.molten_ruby` and `fluid.mypack.molten_ruby` for a fluid, `effect.mypack.ruby_sight` for a potion effect, `entity.mypack.angry_cow` for an entity variant, `biome.mypack.ruby_forest` for a biome. One name each; nothing on this version wants a key written twice.

## Making vanilla treat your block properly

Vanilla checks for its own blocks by identity in a dozen places, so a pack block that should obviously work often doesn't. Two keys cover it.

```json
{
  "material": "ground",
  "plantTypes": ["plains", "crop"],
  "behavesAs": ["till", "path"],
  "variants": { "ruby_grass": { "hardness": 0.6 } }
}
```

**`plantTypes`** lists the plant types your block supports, so saplings, crops and flowers can be planted on it: `plains`, `desert`, `beach`, `cave`, `water`, `nether` and `crop`. **1.21.1** has no plant types at all; there a plant's own soil list decides, and the key is read and ignored.

**`behavesAs`** makes vanilla treat your block like one of its own:

| Value | What it does |
| --- | --- |
| `till` | A hoe turns it into farmland, or into whatever `hoeTillsInto` in the config names |
| `path` | A shovel turns it into a dirt path, or into whatever `shovelPathBecomes` names |

## Items

`<namespace>/items/*.json`

Each key inside `variants` is an item, registered under the pack's namespace, so a file holding `ruby_apple` and `dried_ruby_apple` registers `mypack:ruby_apple` and `mypack:dried_ruby_apple`; the file's own name is only a grouping. Each one's model is generated from `textures/item/<name>.png` unless the pack ships `models/item/<name>.json`.

Every key, shown at once. A real file writes only the ones it needs. A key marked for one type is read only by that type.

```json
{
  "inherits": "mypack:food_template",
  "type": "food",
  "creativeTab": "mypack:tab",
  "material": "mypack:ruby",
  "toolClass": "pickaxe",
  "slot": "head",
  "eat": true,
  "alwaysEdible": false,
  "useDuration": 32,
  "attackSpeed": -2.4,
  "cooldown": 40,
  "container": "minecraft:glass_bottle",
  "crop": "mypack:ruby_crop",
  "soil": "minecraft:farmland",
  "potionTypes": ["mypack:ruby_tonic"],
  "requires": ["mypack"],
  "variants": {
    "ruby_apple": {
      "maxSize": 64,
      "rarity": "rare",
      "healAmount": 6,
      "saturation": 0.8,
      "tags": ["forge:foods"],
      "potion": "minecraft:speed,600,1"
    },
    "dried_ruby_apple": { "healAmount": 3, "saturation": 0.4 }
  }
}
```

| Type | What you get |
| --- | --- |
| `basic` | A plain item. Used when `type` is missing |
| `food` | Eaten, with hunger and saturation |
| `drink` | Drunk rather than eaten, returning an empty container |
| `tool` | Pickaxe, axe, shovel, hoe or sword from a material |
| `armor` | Helmet, chestplate, leggings or boots from a material |
| `seed` | Plants one of your crops |
| `potion` | Applies your potion effects when used |
| `potion_bottle` | Holds your potion types, and shows them in a creative tab |
| `container` | A pouch: an inventory carried in the hand, or worn, see [Containers](#containers) |

A `potion_bottle` lists what it can hold with `potionTypes`, an array of potion type names such as `["mypack:ruby_tonic"]`. One with an empty list registers nothing, and the log says so.

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `variants` | yes | object of variant name to variant | | One item per entry. The key is its registry name, and names its model, its texture and its lang key |
| `type` | no | one of the types above | `basic` | Which type the item takes |
| `creativeTab` | no | tab name | none | The tab it appears in |
| `material` | tool, armor | material name | none | Which of your materials it is made from |
| `toolClass` | tool | `pickaxe`, `axe`, `shovel`, `hoe`, `sword` | none | Which tool it is |
| `slot` | armor | `head`, `chest`, `legs`, `feet` | none | Where it is worn. `helmet`, `chestplate`, `leggings` and `boots` also work |
| `eat` | food | boolean | `false` | Uses the eating animation |
| `alwaysEdible` | food | boolean | `false` | Can be eaten on a full hunger bar |
| `useDuration` | no | int, ticks | `32` | How long using it takes |
| `attackSpeed` | no | float | to suit the tool class | For `tool`, the attack speed attribute, the way a sword is `-2.4` |
| `cooldown` | no | int, ticks | `0` | For `food`, `drink` and `potion`, how long the item refuses re-use after being consumed |
| `container` | drink | item name | none | What is left behind, such as a bottle. On a `container` item this key is the pouch's own settings instead, see [Containers](#containers) |
| `crop` | seed | block name | none | The crop it plants |
| `soil` | seed | block name | `minecraft:farmland` | What it can be planted on |
| `requires` | no | list of mod ids or pack namespaces | none | The file is skipped unless all are present |

Variant keys:

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `maxSize` | no | 1 to 64 | `64` | Stack size |
| `rarity` | no | `common`, `uncommon`, `rare`, `epic` | `common` | Name color in the tooltip |
| `healAmount` | food | int, half drumsticks | `0` | Hunger restored |
| `saturation` | food | float | `0.0` | Saturation restored |
| `tags` | no | list of tag ids | none | Item tags this variant is written into; the tag files are generated for you |
| `potion` | food, drink | `potion,duration,amplifier` | none | An effect applied when the variant is eaten or drunk. A fourth part, `true`, makes it ambient. A beneficial effect is named in the tooltip |

## Fluids

`<namespace>/fluids/*.json`

The file's path is the fluid's registry name unless `name` overrides it.

```json
{
  "name": "molten_ruby",
  "still": "mypack:block/molten_ruby_still",
  "flow": "mypack:block/molten_ruby_flow",
  "color": "C0304A",
  "bucket": true,
  "luminosity": 12,
  "density": 2000,
  "temperature": 1500,
  "viscosity": 4000,
  "gaseous": false,
  "creativeTab": "mypack:tab",
  "requires": ["mypack"],
  "block": {
    "material": "lava",
    "flammability": 0,
    "fireSpread": 0,
    "quantaPerBlock": 8,
    "potions": ["minecraft:wither,200,0"]
  }
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `name` | no | string | the file name | The fluid's registry name |
| `still` | no | texture path | vanilla water still | Texture for the still fluid |
| `flow` | no | texture path | vanilla water flowing | Texture for the flowing fluid |
| `color` | no | hex color | none | Tint applied to those textures |
| `bucket` | no | boolean | `true` | Register a bucket for it |
| `luminosity` | no | 0 to 15 | `0` | Light emitted |
| `density` | no | int | `1000` | Negative floats upward, like a gas |
| `temperature` | no | int, kelvin | `300` | Water is 300, lava 1300 |
| `viscosity` | no | int | `1000` | How slowly it flows. Water is 1000, lava 6000 |
| `gaseous` | no | boolean | `false` | Treated as a gas |
| `creativeTab` | no | tab name | none | The tab the bucket appears in |
| `block` | no | object | | The fluid block. `material` (`water`), `flammability` (`0`), `fireSpread` (`0`), `quantaPerBlock` (`0`), `potions` (none, a list of effects given to whatever stands in it, each written `potion,duration,amplifier` with an optional fourth part `true` for an ambient one) |
| `requires` | no | list of mod ids or pack namespaces | none | The file is skipped unless all are present |

## Materials, tabs, sounds, tags

`<namespace>/materials/*.json`

The file's path is the material's name, which a tool or armor item then names in `material`.

```json
{
  "harvestLevel": 3,
  "durability": 1200,
  "efficiency": 9.0,
  "damage": 3.5,
  "enchantability": 18,
  "repairItem": "mypack:ruby",
  "reduction": [3, 6, 8, 3],
  "toughness": 2.0,
  "equipSound": "item.armor.equip_diamond",
  "armorTexture": "mypack:ruby"
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `harvestLevel` | no | 0 to 4 | `1` | Tool tier. 0 wood, 1 stone, 2 iron, 3 diamond, 4 netherite |
| `durability` | no | int | `250` | Uses before it breaks |
| `efficiency` | no | float | `6.0` | Mining speed. Diamond is 8 |
| `damage` | no | float | `2.0` | Attack damage bonus |
| `enchantability` | no | int | `14` | How good enchantments are. Gold is 22 |
| `repairItem` | no | item name | none | What repairs it in an anvil |
| `reduction` | no | list of four ints | | Armor points, in the order feet, legs, chest, head |
| `toughness` | no | float | `0.0` | Armor toughness, as diamond has |
| `equipSound` | no | sound name | `item.armor.equip_iron` | Sound when armor is put on |
| `armorTexture` | no | texture prefix | the file name | The worn armor texture, read from `textures/models/armor/<name>_layer_1.png` and `_layer_2.png` under that namespace |

`<namespace>/tabs/*.json`

The file's path is the tab's name unless `label` overrides it, and blocks and items name it in `creativeTab` as `<namespace>:<label>`.

```json
{ "label": "rubypack", "icon": "mypack:ruby" }
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `label` | no | string | the file name | The tab's id: blocks and items name it in `creativeTab`, and the shown name comes from `itemGroup.<namespace>.<label>` in the lang files |
| `icon` | no | item name | none | The item shown on the tab |

`<namespace>/sounds/*.json`

The file name is yours to choose, only the folder is read, and several files stack.

The vanilla `sounds.json` format, so a pack can ship its own audio. A file here registers the sound events; the client still reads the audio through the pack's own `assets/<namespace>/sounds.json`, so ship both, the index under `assets` and the events under `data`.

`<namespace>/tags/<kind>/*.json`

Tags are the game's own format in the game's own folder, and a pack ships them as it would in a data pack: `tags/items/ores/ruby.json` (1.21.1: `tags/item/`) holding `{ "values": ["mypack:ruby_ore"] }` puts the ore in `mypack:ores/ruby`, and a file under `data/forge/tags/items/ores/ruby.json` (1.21.1: `data/c/...`) adds to the shared convention tag every mod reads. A pack's own blocks and items name theirs in the variant's `tags` instead, and the files are written for you; a `harvestTool` and `harvestToolLevel` write the `mineable` and `needs_*_tool` tags the same way.

The 1.12.2 ore dictionary is what tags replaced. Its names map onto the convention tags: `oreRuby` is `forge:ores/ruby` on 1.20.1 and `c:ores/ruby` on 1.21.1, `ingotCopper` is `ingots/copper`, `gemRuby` is `gems/ruby`, `dustX` is `dusts/x`, `nuggetX` is `nuggets/x`, `blockX` is `storage_blocks/x`, and `logWood`, `plankWood` and `stickWood` are the game's own `minecraft:logs`, `minecraft:planks` and the convention `rods/wooden`. There is no file that removes an item from a tag; a data pack tag with `"replace": true` rewrites the whole tag instead.

## Furnace recipes and fuels

`<namespace>/furnace/*.json`

The file name is yours to choose, only the folder is read, and several files stack.

Adds and removes smelting recipes.

```json
{
  "remove": [
    "minecraft:iron_ingot",
    { "input": "minecraft:gold_ore" },
    { "input": "minecraft:iron_ore", "result": "minecraft:iron_ingot" }
  ],
  "add": [
    { "input": "mypack:ruby_ore", "output": "mypack:ruby", "count": 2, "experience": 1.0 }
  ]
}
```

Entries under `add`:

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `input` | yes | item name | none | What goes in |
| `output` | yes | item name | none | What comes out |
| `count` | no | int | `1` | How many come out |
| `experience` | no | number | `0.0` | Experience per smelt. Iron ore gives 0.7 |

Entries under `remove` are either a bare item name, which removes every recipe producing it, or an object naming `input`, `result`, or both to narrow it down. A removal naming neither is skipped and the log says so.

`<namespace>/fuels/*.json`

The file name is yours to choose, only the folder is read, and several files stack.

```json
{
  "fuels": [
    { "item": "mypack:ruby_coal", "burnTime": 2400 },
    { "tag": "forge:gems/ruby", "burnTime": 800 }
  ]
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `item` | one of the two | item name | none | The item that burns |
| `tag` | one of the two | tag id | none | Everything in that tag burns |
| `burnTime` | yes | int, ticks | `0` | Coal is 1600, a plank 300 |

## Potions, potion types and brewing

`<namespace>/potions/*.json`

The file's path is the effect's registry name, so `mypack/potions/ruby_sight.json` registers `mypack:ruby_sight`, which a potion type then names.

```json
{
  "name": "effect.mypack.ruby_sight",
  "color": "C0304A",
  "badEffect": false,
  "beneficial": true,
  "instant": false,
  "effectiveness": 0.5,
  "icon": { "x": 0, "y": 0 },
  "iconTexture": "mypack:textures/gui/effects.png",
  "attributes": [
    { "attribute": "minecraft:generic.movement_speed", "uuid": "91AEAA56-376B-4498-935B-2F7F68070635", "amount": 0.2, "operation": 2 }
  ]
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `name` | no | translation key | `effect.<namespace>.<name>` | What the player sees |
| `color` | no | hex color | `FFFFFF` | Particle color |
| `badEffect` | no | boolean | `false` | Counts as harmful, so a fermented spider eye inverts it |
| `beneficial` | no | boolean | `false` | Shown as a good effect |
| `instant` | no | boolean | `false` | Applies once instead of over time |
| `effectiveness` | no | float | `0.5` | How much mob AI values it |
| `icon` | no | object with `x` and `y` | `0`, `0` | Where the icon sits in the sheet |
| `iconTexture` | no | texture path | vanilla sheet | Your own icon sheet |
| `attributes` | no | list of objects | none | `attribute` (the game's id, such as `minecraft:generic.movement_speed`), `uuid`, `amount` (`0.0`), `operation` (`0`) |

`<namespace>/potion_types/*.json`

The file's path is the potion type's registry name, which a `potion_bottle` item then names in `potionTypes`.

```json
{
  "baseName": "ruby_sight",
  "effects": [
    { "potion": "mypack:ruby_sight", "duration": 3600, "amplifier": 0, "ambient": false, "showParticles": true }
  ]
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `baseName` | no | string | the namespace and name | The name the bottle is built from |
| `effects` | yes | list of objects | | See below |

Each effect takes `potion` (required), `duration` (`3600`), `amplifier` (`0`), `ambient` (`false`) and `showParticles` (`true`).

`<namespace>/brewing/*.json`

The file name is yours to choose, only the folder is read, and several files stack.

```json
{
  "brewing": [
    { "input": "minecraft:potion", "ingredient": "mypack:ruby", "output": "mypack:ruby_potion", "requires": ["mypack"] },
    { "from": "minecraft:awkward", "ingredient": "mypack:ruby", "to": "mypack:ruby_tonic" }
  ]
}
```

Each entry is either `input`, `ingredient` and `output`, which brews one item into another, or `from`, `ingredient` and `to`, which turns one potion type into another. `ingredient` is required either way, and an entry also takes `requires`, so one recipe can be skipped without the file being.

## Exposures

`<namespace>/exposures/*.json`

The file's path is the hazard's name, and its death message comes from the lang key `death.attack.rdpl.<file name>`.

A pack-defined hazard: named blocks and items expose players standing near them or carrying them, in levels, each level applying effects and periodic damage. One file defines one hazard; several run side by side.

```json
{
  "blocks": [ "mypack:nuclear_waste=2", "mypack:uranium_ore" ],
  "items": [ "mypack:nuclear_waste" ],
  "immunity": "mypack:antirad",
  "scanInterval": 20,
  "range": 10,
  "sourcesForNextLevel": 4,
  "skipsCreative": true,
  "levels": [
    { "effect": "mypack:radiation_1", "damage": 4.0, "damageInterval": 160,
      "effects": [ { "potion": "minecraft:nausea", "duration": 0, "amplifier": 0, "ambient": false, "showParticles": false },
                   { "potion": "minecraft:hunger" } ] },
    { "effect": "mypack:radiation_2", "damage": 8.0, "damageInterval": 120,
      "effects": [ { "potion": "minecraft:nausea", "amplifier": 1 }, { "potion": "minecraft:hunger", "amplifier": 1 } ] }
  ]
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `blocks` | one of the two | list of `block` or `block=level` | | Blocks that expose a player standing near them. No level means 1 |
| `items` | one of the two | list of `item` or `item=level` | | Items that expose a player carrying or wearing them |
| `levels` | yes | list of levels | | The severity ladder, first entry is level 1. A player gets the highest level any source reaches |
| `immunity` | no | potion name | none | An effect whose bearer is not exposed at all |
| `scanInterval` | no | ticks | `20` | How often surroundings and inventory are checked |
| `range` | no | blocks | `10` | How far a block's exposure reaches, as a sphere |
| `sourcesForNextLevel` | no | int | `0` | This many nearby sources of one level push it one level further. `0` turns that off |
| `skipsCreative` | no | boolean | `true` | Creative and spectator players are left alone |

Each level:

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `effect` | yes | potion name | | The effect that marks the level on the player. Its presence drives the damage, so it should be one the pack defines for this |
| `damage` | no | half-hearts | `0` | Damage dealt every `damageInterval` ticks while the level holds. It ignores armor |
| `damageInterval` | no | ticks | `160` | How often that damage lands |
| `effects` | no | list of effects | none | Extra effects applied alongside, the same shape potion types use. Without a `duration` they follow the scan window |

The level effects last slightly past the next scan, so walking away lets them lapse on their own. Death by exposure damage reads its message from `death.attack.rdpl.<file name>`, which the pack's lang files supply.

## Villagers and trades

`<namespace>/villagers/*.json`

The file's path is the profession's registry name, so `mypack/villagers/jeweller.json` registers `mypack:jeweller`, which a trade then names in `profession`.

```json
{
  "jobSite": "mypack:gem_bench",
  "workSound": "minecraft:entity.villager.work_toolsmith"
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `jobSite` | no | block name | none | The block a villager claims to take up this profession, the way a smithing table makes a toolsmith |
| `workSound` | no | sound name | none | What it plays while working at that block |

Careers are a 1.12.2 idea the game no longer has: a profession is one trade set, so a pack that had two careers ships two villager files. How the villager looks is an ordinary texture, shipped at `assets/<namespace>/textures/entity/villager/profession/<name>.png` and `textures/entity/zombie_villager/profession/<name>.png`, exactly where the game keeps its own.

`<namespace>/trades/*.json`

The file name is yours to choose, only the folder is read, and several files stack.

```json
{
  "trades": [
    {
      "profession": "mypack:jeweller",
      "level": 1,
      "maxUses": 12,
      "xp": 2,
      "buy": { "item": "minecraft:emerald", "min": 2, "max": 4 },
      "sell": { "item": "mypack:ruby", "min": 1 }
    }
  ]
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `profession` | yes | profession name | | Whose trade this is |
| `level` | no | int | `1` | Which trade tier it appears at, 1 to 5 |
| `maxUses` | no | int | `12` | Times it can be used before locking |
| `xp` | no | int | `2` | Experience the villager earns per trade toward its next level |

A stack is `item` with `min` (`1`) and `max` (`min`), so a fixed price is just `min`.

## Entity variants

`<namespace>/entities/*.json`

The file's path is the variant's registry name, so `mypack/entities/angry_cow.json` registers `mypack:angry_cow`, which is what `becomes`, a spawn egg and a world save all refer to.

A file here makes a new entity out of one that already exists. It is a real entity in its own right, its own registry name, its own name in the world, its own spawn egg, and a loot table of its own if you give it one, built on another entity's behavior rather than replacing it. Nothing about the entity it copies changes.

Every key, shown at once. A real file writes only the ones it needs.

```json
{
  "entity": "minecraft:cow",
  "name": "Angry Cow",
  "showName": false,
  "texture": "mypack:textures/entity/angry_cow.png",
  "lootTable": "mypack:entities/angry_cow",
  "profession": "mypack:jeweller",
  "baby": 0.05,
  "becomes": [
    { "variant": "mypack:angry_cow", "weight": 95 },
    { "variant": "mypack:little_angry_cow", "weight": 5 }
  ],
  "sounds": { "ambient": "entity.cow.ambient", "hurt": "entity.cow.hurt", "death": "entity.cow.death" },
  "soundVolume": 1.0,
  "soundPitch": 1.0,
  "immuneTo": ["fall", "drown", "explosion", "magic", "cactus", "lava", "wither", "starve", "in_wall"],
  "jumpMultiplier": 1.0,
  "fallDamage": 1.0,
  "maxFallHeight": 3,
  "breathesUnderwater": false,
  "swims": false,
  "amphibious": false,
  "waterSlowdown": 0.8,
  "absorption": 0,
  "experience": 3,
  "creatureAttribute": "undefined",
  "effects": [ { "potion": "minecraft:strength", "amplifier": 1 } ],
  "despawns": true,
  "despawnAfter": 600,
  "noAI": false,
  "leftHanded": false,
  "fireproof": false,
  "invulnerable": false,
  "glowing": false,
  "invisible": false,
  "dropChance": 0.085,
  "scale": 1.0,
  "angryScale": 1.2,
  "leashable": true,
  "steerable": false,
  "width": 0.9,
  "height": 1.4,
  "pathPriorities": { "WATER": 0.0, "LAVA": -1.0, "DANGER_FIRE": 8.0, "DOOR_WOOD_CLOSED": 0.0 },
  "egg": { "primary": "AABBCC", "secondary": "112233" },
  "attributes": {
    "maxHealth": 20,
    "movementSpeed": 0.32,
    "attackDamage": 4,
    "knockbackResistance": 0.0,
    "followRange": 32,
    "armor": 4
  },
  "hostile": true,
  "targets": ["minecraft:player"],
  "passive": false,
  "persistent": false,
  "silent": false,
  "picksUpLoot": false,
  "hideArmor": false,
  "hideHeld": false,
  "tint": "C0304A",
  "tintParts": ["body", "armor", "held"],
  "ignoresSpawnRules": false,
  "throws": true,
  "throwAmmo": 8,
  "throwReload": 3,
  "throwRetreat": 3,
  "throwPower": 1.0,
  "throwArc": 0.35,
  "explodes": false,
  "explosionPower": 3.0,
  "explosionFuse": 30,
  "explosionFire": false,
  "equipment": {
    "mainhand": "minecraft:tnt",
    "offhand": "minecraft:shield",
    "head": "minecraft:iron_helmet",
    "chest": "minecraft:iron_chestplate",
    "legs": "minecraft:iron_leggings",
    "feet": "minecraft:iron_boots"
  },
  "spawns": [
    { "creatureType": "creature", "weight": 4, "min": 1, "max": 2 }
  ],
  "biomes": ["minecraft:plains"],
  "biomeTypes": ["plains"],
  "trackingRange": 80,
  "trackVelocity": true,
  "trackingFrequency": 3,
  "requires": ["mypack"]
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `entity` | yes | `namespace:name` | none | The entity to build on. Any mod's, as long as it takes a plain world constructor |
| `name` | no | string | none | The name it carries in the world, in death messages and on its egg |
| `showName` | no | boolean | `false` | Show the name without looking at it |
| `texture` | no | `namespace:textures/entity/<file>.png` | none | A skin of its own, laid out the same way the entity it copies is |
| `lootTable` | no | `namespace:entities/<name>` | the base's | What it drops. Without this it drops whatever the entity it copies drops |
| `profession` | no | `namespace:name` | random | For a villager, the trade it practices |
| `baby` | no | boolean or 0.0 to 1.0 | `false` | How often one spawns young, and it stays that way. `true` is always, a number is that share of them |
| `becomes` | no | list | none | Other variants this one may turn into as it spawns, by weight. See below |
| `sounds` | no | object | the base's | `ambient`, `hurt` and `death`, each a registered sound event |
| `soundVolume` | no | number | `1.0` | How loud those sounds are |
| `soundPitch` | no | number | `1.0` | How high they play. Under 1 is deeper, over 1 is squeakier |
| `immuneTo` | no | list of damage types | none | Damage it shrugs off, by the game's damage type names: `fall`, `drown`, `explosion`, `magic`, `cactus`, `lava`, `wither`, `starve`, `in_wall`, `freeze` and the rest |
| `jumpMultiplier` | no | float | `1.0` | How much higher it jumps than the entity it copies |
| `fallDamage` | no | float | `1.0` | Multiplies the damage a fall does. `0` takes fall damage away |
| `maxFallHeight` | no | int | the base's | How far it will drop while pathing |
| `breathesUnderwater` | no | boolean | `false` | Never drowns, and sinks to walk the bottom rather than swimming for the surface. It still finds its way about on the ground, so deep water it cannot walk out of will hold it |
| `swims` | no | boolean | `false` | Moves through water the way a squid or a guardian does, and never drowns. It finds its way through water rather than over ground, so it belongs in water and is stranded out of it |
| `amphibious` | no | boolean | `false` | Walks on land and swims properly in water, changing how it finds its way as it enters and leaves the water. It never drowns. Whatever it was chasing is forgotten at the water's edge, so it hesitates for a moment each time it crosses |
| `waterSlowdown` | no | float | `0.8` | How much water slows it. Higher is faster |
| `absorption` | no | float | `0` | Extra hearts on top of its health |
| `experience` | no | int | the base's | How much experience it drops |
| `creatureAttribute` | no | `undefined`, `undead`, `arthropod` or `illager` | the base's | What it counts as, so Smite and healing potions treat it accordingly |
| `effects` | no | list of objects | none | Effects it always has: `{ "potion": "minecraft:strength", "amplifier": 1 }` |
| `despawns` | no | boolean | `true` | Off, it stays even when it would normally be cleared away |
| `despawnAfter` | no | int, seconds | none | It goes quietly once it has been in the world this long, however far away anyone is |
| `noAI` | no | boolean | `false` | Stands where it is put and does nothing |
| `leftHanded` | no | boolean | `false` | Holds its weapon in the other hand |
| `fireproof` | no | boolean | `false` | Never catches fire at all, so it is never hurt by fire or lava and never burns in daylight |
| `invulnerable` | no | boolean | `false` | Takes no damage from anything but the void and creative |
| `glowing` | no | boolean | `false` | Outlined through walls |
| `invisible` | no | boolean | `false` | Not drawn, though its gear still is |
| `dropChance` | no | 0 to 1 | `0` | How likely each piece of equipment is to drop |
| `scale` | no | float | `1.0` | How big it is drawn, and how big its hitbox is |
| `angryScale` | no | float | `scale` | The size it swells to while it has something to attack, and for three seconds after it loses one |
| `leashable` | no | boolean | `false` | Can be led on a lead, even if the entity it copies never could |
| `steerable` | no | boolean | `false` | Can be steered while ridden |
| `width` | no | float | the base's | Its hitbox across, before `scale` is applied |
| `height` | no | float | the base's | Its hitbox up, before `scale` is applied |
| `pathPriorities` | no | object | none | What it will walk through, as `WATER`, `LAVA`, `DANGER_FIRE`, `DOOR_WOOD_CLOSED` and the rest of the game's path types, each a number where a negative means never |
| `egg` | no | boolean or object | `true` | A spawn egg, colored like the egg of the entity it copies. `{ "primary": "AABBCC", "secondary": "112233" }` picks your own colors, `false` leaves the egg out |
| `attributes` | no | object | none | `maxHealth`, `movementSpeed`, `attackDamage`, `knockbackResistance`, `followRange`, `armor`. An attribute the entity does not normally have is given to it |
| `hostile` | no | boolean | `false` | Attacks what it can reach, and fights back when hurt. A hostile variant counts as a monster to the game whatever its base, so the monster cap holds it and peaceful clears it, and it drops the animal tasks its base came with, breeding, being tempted, following a parent, an owner or its own kind, sitting |
| `targets` | no | list of entity names | the player | What it goes looking for while hostile. `minecraft:player` is understood even though the player is not a registered entity |
| `passive` | no | boolean | `false` | Stops it attacking anything, however it normally behaves |
| `persistent` | no | boolean | `false` | Never despawns |
| `silent` | no | boolean | `false` | Makes no sound |
| `picksUpLoot` | no | boolean | `false` | Picks up what it walks over |
| `hideArmor` | no | boolean | `false` | Wears its armor without it being drawn |
| `hideHeld` | no | boolean | `false` | The same for whatever it is holding |
| `tint` | no | hex color | none | Colors the entity as it is drawn |
| `tintParts` | no | list of `body`, `armor`, `held` | `["body"]` | Which parts the tint reaches |
| `ignoresSpawnRules` | no | boolean | `false` | Spawns wherever it is put, ignoring the rules it inherited |
| `throws` | no | boolean | `false` | Throws what it holds at its target from a distance, and if that is TNT it lights it and backs off. Needs `hostile` |
| `throwAmmo` | no | int | none | How many it has to throw. Left out, it never runs short |
| `throwReload` | no | int, seconds | `explosionFuse` | How long its hand stays empty before it draws another |
| `throwRetreat` | no | int, seconds | `explosionFuse` | How long it keeps away after a throw before turning back |
| `throwPower` | no | float | `1.0` | How hard it throws. Doubling it roughly doubles the reach |
| `throwArc` | no | float | `0.35` | How high it lobs. Higher hangs longer, near zero is a flat hurl, below zero throws downward |
| `explodes` | no | boolean | `false` | Blows itself up next to its target, like a creeper. Needs `hostile` |
| `explosionPower` | no | number | `3.0` | How big the blast is. A creeper is 3, TNT is 4 |
| `explosionFuse` | no | int, ticks | `30` | How long it hisses before going off |
| `explosionFire` | no | boolean | `false` | Leaves fires behind |
| `charges` | no | boolean | `false` | Rushes its target from a distance and hits with a heavy knockback on contact, the way a ravager does, then rests before the next run. Needs `hostile` |
| `pounces` | no | boolean | `false` | Crouches, then leaps onto its target in an arc and strikes on landing, the way a fox does. Needs `hostile` |
| `sniffs` | no | int, blocks | `0` | Hears players moving within that many blocks, walls or not, and walks to where it heard them; a sneaking or standing player is not heard, and one it then sees becomes its target. `0` does not listen. Needs `hostile` |
| `fleesWhenHurt` | no | 0.0 to 1.0 | `0` | Breaks off and runs from whoever it is fighting while its health is under that fraction, and comes back once above it. `0` never flees. Needs `hostile` |
| `sleepsByDay` | no | boolean | `false` | Finds shade by day and stands still there until night or until something attacks it |
| `home` | no | int, blocks | `0` | Keeps to that many blocks around the spot it first stood on, wandering inside it and walking back when it strays. `0` roams freely |
| `patrols` | no | boolean | `false` | Walks the land in long legs with others of its kind following a leader, the way a pillager patrol does. A group that spawns together picks one leader; the rest keep within a few blocks of it, and when the leader takes a target they all do. A follower that loses its leader takes the lead itself. Needs `hostile` |
| `swoops` | no | boolean | `false` | Circles above its target and dives through it, striking on the pass, the way a phantom does. The variant is given a flying helper, so it flies while it hunts and settles to the ground when idle; it needs a base that is a creature, a parrot for one, and a bat is not. Needs `hostile` |
| `gusts` | no | boolean | `false` | Winds up and lets loose a blast of wind at its target from a distance, throwing everything near the target back and up, the way a breeze's wind charge does. Needs `hostile` |
| `gustPower` | no | float | `1.5` | How hard a gust throws. A hit from a mob is 0.4, a strong knockback enchantment about 1 |
| `threatLeast` | no | int | `0` | The lowest threat band a player or other carrier within 128 blocks must stand in before the variant spawns naturally. `0` spawns as usual |
| `threatHostile` | no | int | `0` | The lowest threat band a player must stand in before the variant goes after them on its own. Below it the variant is docile toward that player, though it still fights back when hit. `0` attacks as usual |
| `equipment` | no | object | none | `mainhand`, `offhand`, `head`, `chest`, `legs`, `feet`, each an item name |
| `spawns` | no | list of objects | none | `creatureType`, `weight`, `min` and `max`, the same shape a biome uses |
| `biomes` | no | list of biome names | every biome | Where those spawns are added |
| `biomeTypes` | no | list of biome types | none | The same, by type word |
| `trackingRange` | no | int | `80` | How far away the client is told about it |
| `trackVelocity` | no | boolean | `true` | Send its speed as well as its position. Off saves traffic on things that barely move |
| `trackingFrequency` | no | int | `3` | How often, in ticks |
| `requires` | no | list of mod ids or pack namespaces | none | The variant is left out unless all are present |
| `tasks` | no | list | none | Any task the game has, added to the variant by name at a priority of your choosing, or taken away from what its base came with. The list below |

**A creature with a shelf life.** `despawnAfter` counts in seconds from the moment a creature first enters the world and takes it away quietly when the time is up: no death, no drops, no sound, exactly as if it had wandered off and been cleared. The clock is written into the creature itself, so it keeps running across a save and reload rather than starting over each time a chunk comes back.

It is its own thing, not a nudge to the rules `despawns` and `persistent` govern. Those two decide whether the game may clear a creature away for being far from anybody; this one is a promise that it goes at a set time regardless. A creature can be `persistent` and still have a shelf life, which is what you want for something summoned for a fight or an event that should not outlive it.

The clock runs on world time, so it pauses when nobody is playing and it does not count the minutes a chunk spent unloaded.

`scale` changes both the model and the hitbox on both sides, so what you see is what you can hit. A creature that changes its own size, an animal growing up or a zombie that is a child, is scaled around whatever size it has chosen, so the two do not fight. `angryScale` swells it while it has a target and returns it to `scale` when it loses one. Since the client is never told what a creature is hunting, the sprinting flag carries that news across, it is set on a variant that uses `angryScale` and on nothing else, so a mod reading sprinting on your variants will see it change. Growing inside a low ceiling is possible, the same way a slime growing is, so keep the difference modest.

A variant drops whatever the entity it copies drops, because the loot table is fixed in that entity's own code rather than looked up by name. `lootTable` points it at a table of your own, which you then supply at `loot_tables/entities/<name>.json` like any other.

A `texture` is bound in place of the one the entity would normally use, whatever renderer it inherits, so it works for modded entities as well as vanilla ones. It has to match the model it is drawn on, since the model is the base entity's, a skin, not a new shape. Layers keep their own textures, so armor still looks like armor on a reskinned zombie.

Armor is only ever drawn on an entity whose renderer has an armor layer, which means the humanoid mobs and villagers. A variant of a cow or a spider can carry armor and gets its protection, but nothing draws it, so `armor` under `attributes` is usually the tidier way to make such a creature tough. `hideArmor` is for the other case: a humanoid that should keep the armor in its slots, for the protection or for a mod that reads them, without it being seen.

`hostile` also takes away the behavior that made the creature run: an animal that avoided players or panicked when hurt does neither once it is hostile, since otherwise it would flee the thing it is meant to be attacking. It needs an entity that walks the ground, since it uses the same attack behavior vanilla gives its own mobs. A flying or swimming base is logged and left alone. `passive` works more widely, but only reaches behavior built the way vanilla builds it, a mod whose hostility is written into its own tick or damage code is not something a pack can talk out of.

A variant is a class of its own, so a world that contains one depends on the pack that made it, the same way it depends on a mod. Take the file away and the creatures in that world go with it.

**Throwing instead of charging.** `explodes` sends a creature in to blow itself up. `throws` is the other temperament: it keeps its distance, throws whatever is in its main hand at what it is fighting, and if that happens to be TNT it lights it, throws it, and backs away while it burns.

```json
{
  "hostile": true,
  "throws": true,
  "explosionFuse": 50,
  "equipment": { "mainhand": "minecraft:tnt" }
}
```

Throwing empties its hand, because it threw the thing. It then keeps away for `throwRetreat`, draws another after `throwReload`, and turns back to its target: a loop of lob, fall back, reload, close in. Give it a `throwAmmo` and that loop ends when the count runs out, its hand staying empty for good and its ordinary attack taking over. Leave `throwAmmo` out and it never runs short.

The count is written into the creature, so it does not refill because a chunk was unloaded and loaded again. Anything that is not TNT flies as an item and lands, which makes a sapper flinging rocks or rotten flesh as easy as one flinging explosives.

`explosionFuse` remains the fuse on the thrown TNT, and stands in for either timer you leave out, so a variant written before these keys behaves exactly as it did.

How the throw itself flies is `throwPower` and `throwArc`. The first is a multiplier on the shove, and since the shove already grows with distance, raising it lengthens the reach without changing how long the throw hangs in the air. The second is the lift, and it changes the shape: high and it lobs over a wall and takes its time, near zero and it is hurled flat and lands almost at once, below zero and it is thrown down at something beneath. Both leave the fuse alone, so a lobbed charge and a flat one go off the same number of seconds after leaving the hand, which is what decides whether one bursts overhead or lands first and waits. How far it will throw from is its `followRange`, and it closes as usual once you are nearer than three blocks, so it is dangerous at range and ordinary in your face.

**Any task the game has.** The keys above are RDPL's own behaviors. `tasks` reaches past them to every task vanilla itself uses, on any base: an entry is an object naming the `task` and its `priority`, plus whatever that task reads; a name after a `-` drops every task of that kind the base came with. Priorities run 0 first, and vanilla keeps its own between 1 and 8, so a task at 0 wins over everything the base does and one at 9 only runs when nothing else wants to.

```json
{
  "entity": "minecraft:cow",
  "tasks": [
    "-wander",
    { "task": "avoidEntity", "priority": 3, "entity": "minecraft:player", "distance": 8, "speed": 1.0, "nearSpeed": 1.4 },
    { "task": "watchClosest", "priority": 6, "entity": "minecraft:wolf", "distance": 12 },
    { "task": "wanderAvoidWater", "priority": 7, "speed": 0.8 }
  ]
}
```

The list is applied after `hostile`, `passive` and the behaviors above have done their work, so it has the last word. Tasks that move the body lock each other out: one only runs when nothing ahead of it in priority is moving the creature, and the attack a monster comes with sits at 2, so a leap or a flee on a zombie needs priority 1 or it never gets a turn; the spider and the wolf keep their leap ahead of their attack for the same reason. A task the base already runs is added a second time rather than replaced; drop the old one first. Some tasks only make sense on a base that has what they drive: a bow fight needs a base that shoots, sitting needs a base that can be tamed, and trading needs a villager. Ask for one on a base that cannot carry it and the log says which base it needs, and the variant does without it. A villager on this version runs on the game's brain rather than on tasks, so the villager rows below reach only what the brain leaves to tasks.

| Key | Type | Default | What it does |
| --- | --- | --- | --- |
| `priority` | int | required | Where it sits among the base's tasks. Lower runs first |
| `speed` | number | the task's usual | How fast it moves while the task runs, as a multiplier on its walking speed |
| `nearSpeed` | number | `1.2` | `avoidEntity`: the multiplier once the thing it avoids is close |
| `distance` | number, blocks | the task's usual | How far it looks, follows, shoots or keeps away |
| `near` | number, blocks | the task's usual | `follow`, `followOwner`, `followOwnerFlying`: how close it comes before it stops |
| `chance` | number | the task's usual | `wander`: one roll in that many ticks; `wanderAvoidWater`: the odds, 0 to 1, of leaving cover; `watchClosest`, `watchClosest2`: the odds, 0 to 1, of looking each tick |
| `leap` | number | `0.4` | `leapAtTarget`: how high the leap goes |
| `cooldown` | int, ticks | `20` | `attackRanged`, `attackRangedBow`: ticks between shots |
| `entity` | entity name | none | Which entity the task looks for, avoids, watches or breeds with. `minecraft:player` is understood |
| `items` | list of item names | none | `tempt`: what a player holds out |
| `sight` | boolean | `true` | `nearestAttackableTarget`, `targetNonTamed`: only what it can see |
| `nearby` | boolean | `false` | `nearestAttackableTarget`: only what is within its own follow range |
| `help` | boolean | `false` | `hurtByTarget`: others of its kind nearby join in |
| `memory` | boolean | `false` | `attackMelee`, `zombieAttack`: keeps after a target it lost sight of |
| `close` | boolean | `false` | `openDoor`: closes the door behind it |
| `nocturnal` | boolean | `false` | `moveThroughVillage`: only at night |
| `scared` | boolean | `false` | `tempt`: a player moving too fast breaks the spell |

The `List` column says where the task lives. `tasks` is what the creature does; `targets` is how it picks what to go after, and a target task without a matching attack does nothing on its own.

| Task | Needs | List | Reads | What it does |
| --- | --- | --- | --- | --- |
| `attackMelee` | a walking creature | `tasks` | `speed`, `memory` | Walks up to its target and hits it |
| `attackRanged` | a base that shoots | `tasks` | `speed`, `cooldown`, `distance` | Keeps its distance and shoots whatever its base shoots |
| `attackRangedBow` | a monster that shoots | `tasks` | `speed`, `cooldown`, `distance` | The skeleton's bow fight: strafes, draws and looses |
| `avoidEntity` | a walking creature | `tasks` | `entity`, `distance`, `speed`, `nearSpeed` | Runs from the named entity when it comes within `distance` |
| `beg` | a wolf | `tasks` | `distance` | Begs from a player holding out food |
| `breakDoor` | any base | `tasks` |  | Breaks the wooden doors in its way, on hard difficulty |
| `creeperSwell` | a creeper | `tasks` |  | Hisses and goes off next to its target |
| `defendVillage` | an iron golem | `targets` |  | Goes after whoever attacked a villager |
| `eatGrass` | any base | `tasks` |  | Eats grass, the way a sheep does |
| `findEntityNearest` | any base | `targets` | `entity` | Targets the nearest of the named entity, the way a slime or ghast targets |
| `findEntityNearestPlayer` | any base | `targets` |  | Targets the nearest player it can reach |
| `fleeSun` | a walking creature | `tasks` | `speed` | Looks for shade when the sun is on it |
| `follow` | any base | `tasks` | `speed`, `near`, `distance` | Follows others of its own kind |
| `followGolem` | a villager | `tasks` |  | Follows an iron golem holding out a poppy |
| `followOwner` | a tameable base | `tasks` | `speed`, `near`, `distance` | Follows its owner, and teleports after them when far behind |
| `followOwnerFlying` | a tameable base | `tasks` | `speed`, `near`, `distance` | The same, flying |
| `followParent` | an animal | `tasks` | `speed` | A child keeps close to a grown one of its kind |
| `harvestFarmland` | a villager | `tasks` | `speed` | Harvests ripe crops and replants them |
| `hurtByTarget` | a walking creature | `targets` | `help` | Fights back at whatever hit it |
| `landOnOwnersShoulder` | a parrot | `tasks` |  | Rides on its owner's shoulder |
| `leapAtTarget` | any base | `tasks` | `leap` | Leaps at its target from close by |
| `llamaFollowCaravan` | a llama | `tasks` | `speed` | Falls in behind a led llama |
| `lookAtTradePlayer` | a villager | `tasks` |  | Faces the player it is trading with |
| `lookAtVillager` | an iron golem | `tasks` |  | Looks at villagers |
| `lookIdle` | any base | `tasks` |  | Looks about now and then |
| `mate` | an animal | `tasks` | `speed`, `entity` | Breeds when in love, with its own kind or the `entity` named |
| `moveIndoors` | a walking creature | `tasks` |  | Goes inside a village house at nightfall |
| `moveThroughVillage` | a walking creature | `tasks` | `speed`, `nocturnal` | Walks the village paths from door to door |
| `moveTowardsRestriction` | a walking creature | `tasks` | `speed` | Walks back toward its home spot when it strays |
| `moveTowardsTarget` | a walking creature | `tasks` | `speed`, `distance` | Closes in on a target that is far off |
| `nearestAttackableTarget` | a walking creature | `targets` | `entity`, `sight`, `nearby` | Targets the nearest of the named entity |
| `ocelotAttack` | any base | `tasks` |  | The cat's stalk and pounce |
| `ocelotSit` | an ocelot | `tasks` | `speed` | Sits on chests, beds and lit furnaces |
| `openDoor` | any base | `tasks` | `close` | Opens the wooden doors it walks through |
| `ownerHurtByTarget` | a tameable base | `targets` |  | Goes after whatever hit its owner |
| `ownerHurtTarget` | a tameable base | `targets` |  | Goes after whatever its owner hit |
| `panic` | a walking creature | `tasks` | `speed` | Runs when hurt or on fire |
| `play` | a villager | `tasks` | `speed` | Children play tag with each other |
| `restrictOpenDoor` | a walking creature | `tasks` |  | Stays inside the village doors at night |
| `restrictSun` | a walking creature | `tasks` |  | Keeps to the shade by day |
| `runAroundLikeCrazy` | a horse, donkey, mule or llama | `tasks` | `speed` | Bucks a rider it does not trust yet |
| `sit` | a tameable base | `tasks` |  | Sits when told to |
| `skeletonRiders` | a skeleton horse | `tasks` |  | Calls in skeleton riders when a player comes near, the trap horse |
| `swimming` | any base | `tasks` |  | Keeps its head above water |
| `targetNonTamed` | a tameable base | `targets` | `entity`, `sight` | Targets the named entity while it is not yet tamed |
| `tempt` | a walking creature | `tasks` | `items`, `speed`, `scared` | Follows a player holding out one of the `items` |
| `tradePlayer` | a villager | `tasks` |  | Stands still while trading |
| `villagerInteract` | a villager | `tasks` |  | Chats with other villagers |
| `villagerMate` | a villager | `tasks` |  | Breeds when the village has room |
| `wander` | a walking creature | `tasks` | `speed`, `chance` | Wanders about |
| `wanderAvoidWater` | a walking creature | `tasks` | `speed`, `chance` | Wanders, keeping out of the water |
| `wanderAvoidWaterFlying` | a walking creature | `tasks` | `speed` | Wanders in the air and perches in trees |
| `watchClosest` | any base | `tasks` | `entity`, `distance`, `chance` | Looks at the nearest of the named entity, the player if none is named |
| `watchClosest2` | any base | `tasks` | `entity`, `distance`, `chance` | The same, kept up while another task runs |
| `zombieAttack` | a zombie | `tasks` | `speed`, `memory` | The zombie's attack, arms raised |

**One egg or spawner giving a mix.** A variant is a class of its own, so on its own it always spawns exactly what it says. `becomes` is how a pack breaks that: a list of variants this one may turn into as it spawns, each with a weight, decided per creature.

```json
{
  "becomes": [
    { "variant": "mypack:walker", "weight": 95 },
    { "variant": "mypack:little_walker", "weight": 5 }
  ]
}
```

Naming itself is how it stays as it is, and the weights are the odds. Put that on `mypack:walker` and one egg, one spawner and one spawn entry give mostly walkers with the occasional little one, the way a zombie egg gives you the odd baby. It happens as the creature enters the world, so it holds for eggs, spawners, `/summon` and natural spawning alike, and the creature that arrives is a real one of the chosen variant with everything that variant says. A variant reached this way does not turn again, so two variants may name each other without spinning.

**Where `baby` fits.** The game has no baby zombie of its own: there is one zombie that rolls whether it is a child as it spawns. `baby` says how often, so `"baby": 0.05` is the vanilla habit and `"baby": true` is always. Between them these are two ways at the same thing, and which to reach for depends on the difference you want: `baby` alone gives one variant that is sometimes young, `becomes` gives several variants that differ in whatever you like, and a mix of both is fine.

## Village plots

`<namespace>/villages/*.json`

The file's path is the plot's name, which `villagePieces` can then name to keep or drop it.

A file here adds a piece the pack's cities and villages can build. Two kinds, chosen with `type`.

Every key, shown at once. A real file writes only the ones it needs. A key marked for one type is read only by that type.

```json
{
  "type": "farm",
  "weight": 3,
  "leastCount": 1,
  "mostCount": 4,
  "width": 7,
  "height": 4,
  "depth": 9,
  "apron": 2,
  "crops": ["simplecorn:corn", "minecraft:wheat"],
  "edge": "minecraft:oak_log",
  "soil": "minecraft:farmland",
  "water": true,
  "rowWidth": 2,
  "structure": "mypack:blacksmith_shed",
  "integrity": 100,
  "lootTable": "minecraft:chests/village/village_toolsmith",
  "villagers": 2,
  "villagerEntity": "mypack:jeweller",
  "villagerX": 1,
  "villagerY": 1,
  "villagerZ": 1,
  "ground": "minecraft:dirt",
  "requires": ["mypack"]
}
```

A `farm` is a field described rather than coded: a plot of the size you ask for, edged with a block, filled with rows of soil separated by water channels, planted with a crop picked per block from your list.

```json
{
  "type": "farm",
  "weight": 3,
  "width": 7,
  "depth": 9,
  "crops": ["simplecorn:corn"],
  "edge": "minecraft:oak_log",
  "water": true,
  "rowWidth": 2
}
```

A `template` places one of your `.nbt` structures instead, turned to face the street.

```json
{
  "type": "template",
  "weight": 2,
  "width": 9,
  "height": 6,
  "depth": 9,
  "structure": "mypack:blacksmith_shed"
}
```

A `template` whose `structure` names one of your [structure maps](#structure-maps) places the whole composite as the plot. The plot's size then comes from the map, its footprint and stacked layers times the cell, so `width`, `height`, `depth` and `integrity` are not read. Layers before the map's `ground` dig down as basements, and weighted palette cells still roll per building, so two towers from the same map can differ.

```json
{
  "type": "template",
  "weight": 2,
  "structure": "mypack:castle",
  "villagers": 4
}
```

| Key | Used by | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `type` | all | `farm` or `template` | `farm` | Which kind of plot |
| `weight` | all | int | `3` | How often this plot is picked against the pack's others |
| `leastCount` | all | int | `1` | Fewest per village, before village size is added |
| `mostCount` | all | int | `4` | Most per village, before village size is added |
| `width` | all | int | `7` | Size across the street |
| `height` | all | int | `4` | Height cleared above the ground |
| `depth` | all | int | `9` | Size away from the street |
| `apron` | all | int | `2` | How far the ground may be off the street's level under the plot before it is refused or slid along its street: that many blocks of fill under it, or of cut into a rise above it, and no more than that between its highest and lowest corner. A wide plot in hills needs more. Set it high and the plot terraces straight into a slope, which in the wrong place eats a mountain |
| `crops` | farm | list of block names | wheat | Planted one per block, at a random growth stage |
| `edge` | farm | block name | `minecraft:oak_log` | The frame around the plot |
| `soil` | farm | block name | `minecraft:farmland` | What the rows are made of |
| `water` | farm | boolean | `true` | Put a water channel between the rows |
| `rowWidth` | farm | int | `2` | How wide each row of soil is |
| `structure` | template | `namespace:name` | none | The template to place, or one of your structure maps, which then sets the plot's size |
| `integrity` | template | 1 to 100 | `100` | Percentage of the template's blocks that appear |
| `lootTable` | template | `namespace:path` | none | The loot table every chest inside the placed template is filled from the first time it is opened. A plot that names a structure map is left alone |
| `villagers` | all | int | `0` | How many people the plot spawns |
| `villagerEntity` | all | `namespace:name` | a villager | Who lives there, such as an entity variant of your own |
| `villagerX` | all | int | `1` | Where they appear, across the plot |
| `villagerY` | all | int | `1` | Where they appear, above the floor |
| `villagerZ` | all | int | `1` | Where they appear, into the plot |
| `ground` | all | block name | `minecraft:dirt` | What is packed underneath on a slope |
| `requires` | all | list of mod ids or pack namespaces | none | The plot is left out unless all are present |

Plots are what the pack's own cities build along their streets; the game's own villages are not changed. `weight` decides which of your plots is chosen once a street asks for one, and `villagePieces` in the `villages` settings names the plots a template keeps, as `mypack:smithy`. How the streets themselves are laid, dressed, bridged, tunneled and railed is the `village*` settings under [What each group does](#what-each-group-does).

## Biomes

`<namespace>/biomes/*.json`

The file's path is the biome's registry name, so `mypack/biomes/ruby_forest.json` registers `mypack:ruby_forest`. `name` is only what the player is shown, and `biome.mypack.ruby_forest` in the lang files says it in every language.

Every key, shown at once. A real file writes only the ones it needs.

```json
{
  "name": "Ruby Forest",
  "types": ["forest", "dense", "wet"],
  "temperature": 0.7,
  "rainfall": 0.8,
  "rain": true,
  "snow": false,
  "topBlock": "mypack:ruby_grass",
  "fillerBlock": "minecraft:dirt",
  "stoneBlock": "mypack:ruby_stone",
  "baseBiome": "minecraft:forest",
  "waterColor": "8040A0",
  "grassColor": "6BA33C",
  "foliageColor": "4E8B2A",
  "decoration": {
    "trees": 10,
    "extratreechance": 10,
    "flowers": 4,
    "grass": 5,
    "deadbush": 0,
    "mushrooms": 1,
    "bigmushrooms": 0,
    "reeds": 10,
    "cacti": 0,
    "sand": 3,
    "gravel": 1,
    "clay": 1,
    "waterlily": 0,
    "falls": 1
  },
  "spawns": [
    { "entity": "minecraft:sheep", "type": "creature", "weight": 12, "min": 2, "max": 4 }
  ],
  "keepDefaultSpawns": false,
  "spawnChance": 0.1,
  "spawnRates": { "surfaceDay": 0.0, "surfaceNight": 0.5, "undergroundDay": 2.0, "undergroundNight": 2.0 },
  "placement": {
    "climate": "warm",
    "weight": 8,
    "villages": true,
    "strongholds": false,
    "playerSpawn": true
  },
  "villageType": "oak",
  "minHeight": 100,
  "maxHeight": 156,
  "replaces": ["minecraft:plains", "minecraft:forest"],
  "requires": ["mypack"]
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `name` | no | string | the file name | Name shown to the player |
| `temperature` | no | float | `0.5` | Below 0.15 snows, above 1.0 is desert-hot |
| `rainfall` | no | float, 0 to 1 | `0.5` | How wet it is |
| `rain` | no | boolean | `true` | Whether weather happens at all |
| `snow` | no | boolean | `false` | Whether rain falls as snow |
| `topBlock` | no | block name | grass | The surface block |
| `fillerBlock` | no | block name | dirt | Just below the surface |
| `stoneBlock` | no | block name | stone | The bulk of the ground |
| `types` | no | list of biome types | none | Writes the biome into the tags those type words stand for, such as `forest`, `cold`, `wet` or `nether`, so other mods find it |
| `waterColor` | no | hex color | `FFFFFF` | Water tint |
| `grassColor` | no | hex color | from the climate | Grass tint, in place of the color temperature and rainfall would give |
| `foliageColor` | no | hex color | from the climate | Leaf tint, the same way |
| `baseBiome` | no | biome name | none | An existing biome to copy settings from |
| `decoration` | no | object | the base biome's | Per-chunk counts, changing what the base biome already places. The names it reads are `trees`, `flowers`, `grass`, `deadbush`, `mushrooms`, `bigmushrooms`, `reeds`, `cacti`, `sand`, `gravel`, `clay` and `waterlily`, plus `falls`, where above zero means lakes and springs generate, and `extratreechance`, a percentage chance of one tree more. A count on a kind the base biome does not place adds nothing; write a worldgen entry for that. Any other name is logged and ignored |
| `spawns` | no | list of objects | vanilla list | See below |
| `keepDefaultSpawns` | no | boolean | `false` | Keep vanilla's list alongside yours |
| `spawnChance` | no | float, below 1 | `0.1` | How likely another herd is placed as the land is first made. The game keeps rolling for as long as it succeeds, so 1 never stops and fills the world until it runs out of room. Anything at or above 0.99 is refused and 0.99 used |
| `spawnRates` | no | object of `surfaceDay`, `surfaceNight`, `undergroundDay`, `undergroundNight` to a multiplier | none | How often hostile mobs spawn here, in place of the global settings. See below |
| `placement` | no | object | none | Where it generates. See below |
| `villageType` | no | `oak`, `sandstone`, `acacia` or `spruce` | none | What a village standing here is built from: the plains, desert, savanna or taiga village. Empty builds the plains one, as it would without the key |
| `minHeight` | no | int | none | Lowest y this biome takes over as a 3D biome. Setting either height turns the biome into a band: the column keeps its own biome outside it, and inside it every 4 by 4 by 4 cell of the world reports this one |
| `maxHeight` | no | int | none | Highest y of that band |
| `replaces` | no | list of biome names | every biome | Restricts the band to columns whose own biome is named here, so an alpine band can sit over mountains and nothing else |
| `requires` | no | list of mod ids or pack namespaces | none | The file is skipped unless all are present |

A spawn entry takes `entity` (required), `type` (`creature`, one of `monster`, `creature`, `ambient` or `water`), `weight` (`10`), `min` (`1`) and `max` (`min`).

`spawnRates` is about hostile mobs only, and nothing else. It takes four keys and no others: `surfaceDay` and `surfaceNight` for where the sky can be seen, `undergroundDay` and `undergroundNight` for where it cannot. Each is a multiplier on how often a hostile mob is allowed to appear, `1` is the ordinary rate, `0` stops them entirely, below 1 turns some attempts down, and above 1 lets through attempts the game would otherwise have refused, so `2` is twice as many. A key left out means the biome does not decide, and the global setting for that time and place is used instead. Anything else written here is not a key and is ignored, so a rate named after a creature type does nothing at all.

`placement`:

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `climate` | no | `icy`, `cool`, `medium`, `warm` or `desert` | none | Which climate band it joins, the same five the overworld's own biomes are dealt out by. Left out, the biome is registered but never placed unless a template's `roles` or a dimension's `biome` asks for it |
| `weight` | no | int | `10` | How often it is chosen against its neighbors in that band |
| `villages` | no | boolean | `false` | Villages may generate |
| `strongholds` | no | boolean | `false` | Strongholds may generate |
| `playerSpawn` | no | boolean | `false` | The world spawn may be placed here |

A biome is a data pack entry on this version, written for you under `worldgen/biome/`, and the terrain under it is the noise settings' rather than the biome's, which is why there is no `baseHeight` or `heightVariation`: the shape of the land comes from where the climate places the biome, as it does for the game's own. A 1.12.2 `id` is read and ignored.

## Dimensions

`<namespace>/dimensions/*.json`

The file's path is the dimension's id, so `mypack/dimensions/verdant.json` is `mypack:verdant`, which is what a portal, a gate, a game rule file and `/execute in` all name. There is no numeric id on this version, and a 1.12.2 `id` or `suffix` is read and ignored.

Every key, shown at once. A real file writes only the ones it needs.

```json
{
  "requires": ["mypack"],
  "terrain": {
    "type": "overworld",
    "minHeight": -64,
    "maxHeight": 320,
    "generatorOptions": { "seaLevel": 63, "useLavaOceans": false },
    "structures": false
  },
  "biomes": {
    "source": "single",
    "biome": "mypack:ruby_forest"
  },
  "sky": {
    "hasSkyLight": true,
    "surfaceWorld": true,
    "respawn": true,
    "respawnDimension": "minecraft:overworld",
    "spawning": true,
    "nether": false,
    "beds": true,
    "waterVaporizes": false,
    "cloudHeight": 160,
    "cloudColor": "5B3E6A",
    "groundLevel": 63,
    "movementFactor": 4.0,
    "fogColor": "20102A",
    "showFog": false,
    "skyColor": "3B1E4A",
    "fixedTime": 18000,
    "sunriseColors": true,
    "ambientLight": 0.1,
    "starBrightness": 0.8,
    "renderSky": true,
    "renderClouds": true,
    "renderWeather": true
  },
  "gameRules": { "doMobSpawning": "false" }
}
```

**Top level**

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `gameRules` | no | object | none | Rules that apply only here |
| `portal` | no | object | none | A frame that opens this dimension. See [Opening a dimension with a frame](#opening-a-dimension-with-a-frame) |
| `requires` | no | list of mod ids or pack namespaces | none | The file is skipped unless all are present |

**`terrain`**

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `type` | no | `overworld`, `flat`, `void`, `nether`, `end` | `overworld` | Which of the game's generators builds it, with its noise settings copied and changed by the keys below |
| `minHeight` | no | int, a multiple of 16 | the type's own | The dimension's floor. Lower than the type's own makes a deep world under the terrain, see [The deep world](#the-deep-world) |
| `maxHeight` | no | int, a multiple of 16 | the type's own | The block above its top |
| `generatorOptions` | no | object, or a list | none | For `overworld` and the others an object of `seaLevel` and `useLavaOceans`. For `flat` the layers, bottom up, as `"minecraft:bedrock"`, `"59*minecraft:stone"`, `"3*minecraft:dirt"`, `"minecraft:grass_block"`, which is also the default ground |
| `structures` | no | boolean | `true` | Whether vanilla structures generate |

**`biomes`**

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `source` | no | `inherit`, `single` | `inherit` | `inherit` uses the overworld's own biome map, `single` uses one biome everywhere |
| `biome` | when `single` | biome name | `minecraft:plains` | Which biome that is |

**`sky`**

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `hasSkyLight` | no | boolean | `true` | Whether daylight reaches it |
| `surfaceWorld` | no | boolean | `true` | Whether maps and compasses behave as in the overworld |
| `respawn` | no | boolean | `true` | Whether players respawn here |
| `respawnDimension` | no | dimension id | none | Where they respawn instead |
| `spawning` | no | boolean | `true` | Whether mobs spawn |
| `nether` | no | boolean | `false` | Treated as the nether for portals and ceilings |
| `beds` | no | boolean | `true` | Off, beds explode |
| `waterVaporizes` | no | boolean | `false` | Water evaporates |
| `cloudHeight` | no | int | `128` | Where clouds sit |
| `cloudColor` | no | hex color | none | Cloud tint |
| `groundLevel` | no | int | `63` | Sea level, used for the horizon, for spawn searches and for where a gate arrival or a fall over the void lands |
| `movementFactor` | no | float | `1.0` | Distance ratio to the overworld. The nether uses 8 |
| `fogColor` | no | hex color | none | Fog tint |
| `showFog` | no | boolean | `false` | Thick fog, as in the nether |
| `skyColor` | no | hex color | none | Sky tint |
| `fixedTime` | no | int, ticks | none | Locks the time of day |
| `sunriseColors` | no | boolean | `true` | Whether sunrise and sunset are tinted |
| `ambientLight` | no | float, 0 to 1 | `0.0` | Minimum light everywhere |
| `starBrightness` | no | float, 0 to 1 | none | How bright the stars are |
| `renderSky` | no | boolean | `true` | Off, nothing draws the sky, sun, moon or stars, leaving the fog color |
| `renderClouds` | no | boolean | `true` | Off, no clouds are drawn |
| `renderWeather` | no | boolean | `true` | Off, no rain or snow is drawn |

Colors and the three render switches are all that is offered. Drawing something of your own up there, a painted dome, your own sun and moon, still needs Java.

A dimension is a data pack entry on this version: the dimension type and the noise settings are written for you under the pack's namespace, so a vanilla client is told about it as it joins and travels there like any other. The dimension keeps its own save folder under the world, named after its id, and is loaded while somebody is in it, or while a `forceload` holds a chunk.

## Containers

`<namespace>/blocks/*.json`, `<namespace>/items/*.json`

```json
{
  "type": "container",
  "material": "wood",
  "creativeTab": "mypack:tab",
  "container": {
    "rows": 6,
    "columns": 9,
    "lootTable": "minecraft:chests/simple_dungeon",
    "chestModel": true
  },
  "variants": { "crate": { "hardness": 2.5 } }
}
```

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `rows` | int | `3` | How many rows of slots, 1 to 9 |
| `columns` | int | `9` | How many slots in a row, 1 to 12 |
| `lootTable` | text | empty | A loot table rolled into the block the first time a player opens it, exactly as a dungeon chest fills. Empty leaves it starting empty |
| `chestModel` | boolean or text | `false` | Draws as a chest with a lid that opens, instead of as an ordinary block from your own texture. `true` uses the vanilla chest artwork; a texture name such as `mypack:entity/chest/strongbox` uses your own chest sheet instead, for the placed block and for the item alike. A chest-model block also defaults `opaque` to `false`, the way a vanilla chest is, so light is not cut off at the block and the chest is not drawn dark |
| `guiTexture` | text | empty | Your own background image for the screen. Empty draws one from the vanilla chest screen at whatever size the rows and columns need |
| `guiWidth` | int | none | How wide that image is, required with `guiTexture` |
| `guiHeight` | int | none | How tall that image is, required with `guiTexture` |
| `curioSlot` | text | empty | An item only: the Curios slot it can be worn in, `back`, `belt`, `body`, `charm`, `head`, `necklace`, `ring` or any slot another mod adds. A backpack usually takes `back`. Ignored, with everything else about the item still working, when Curios is not installed. The 1.12.2 key `bauble` is read as this one, its Baubles names mapped onto the nearest Curios slot, and the log says what it became |

**Nine rows by twelve is the ceiling**, the most a screen can carry. A pack asking for more is cut to it with an error line saying so. One warning about the tallest: a nine-row screen is 276 pixels, and a 1080 display at GUI scale `auto` gives 270, so the top and bottom clip by three pixels each; scale 3 shows it whole.

**The screen is drawn, not shipped.** A container of nine columns or fewer and six rows or fewer uses the vanilla chest screen as it stands, so it looks exactly like a chest of that size. Anything larger is assembled from the same image at draw time, the top edge, a row of slots repeated to fit, and the bottom with the player's own inventory, so a pack can ask for sizes no vanilla screen covers without shipping an image of its own. `guiTexture` overrides all of that where a pack wants its own look, and then `guiWidth` and `guiHeight` must say how big it is or the drawn one is used and an error line says so.

**What the block does.** It keeps its contents through a save and a reload, drops them when broken, answers a comparator by how full it is, and can be renamed in an anvil like a chest. `chestModel` also gives it the chest's opening sound and the lid animation; left off, the block draws from its own texture like any other block, so a crate, a barrel or a cabinet all work.

**Coloring a chest.** The chest sheet is an ordinary texture, so a pixel map can recolor the vanilla one without drawing a pixel: `extends` it and give it a `tint`, then name that map in `chestModel`.

```json
{
  "extends": "minecraft:textures/entity/chest/normal",
  "tint": {
    "from": "#241A12",
    "to": "#D8BC80"
  }
}
```

**A container item is a pouch**, an item of type `container` carrying the same `container` block with `rows` and `columns`; open it with a right-click, and it keeps its contents as it changes hands. Give it `curioSlot` and, where Curios is installed, it goes in that slot and a key opens it without taking it off, `V` by default, rebindable under Resource Data Pack Loader in the controls. Pressing it again, with a worn container already open, moves to the next one you are wearing and wraps around, so several worn at once are all reachable. The key only appears when Curios is there, and everything else about the item, the right-click and its inventory, works whether it is or not. The pouch's wearability is written into the Curios tag for that slot for you.

**The loot table fills on first open**, not when the block is placed, which is what makes it useful in a structure: whoever opens it first gets the roll. The same table can be used by `lootTable` on an imprint shape or a village plot, so a pack can place these through worldgen and stock them the same way.

## Portals and gates

`<namespace>/blocks/*.json`

A portal is an ordinary block definition, so the same path rule applies and each variant is a portal block.

A `portal` block carries a `portal` section:

```json
{
  "type": "portal",
  "material": "portal",
  "portal": {
    "dimension": "mypack:ruby_world",
    "returnDimension": "minecraft:overworld",
    "gate": "mypack:ruby_gate",
    "cooldown": 60,
    "platform": true,
    "platformBlock": "mypack:ruby_block",
    "sound": "block.portal.travel",
    "owned": true
  },
  "variants": { "ruby_portal": { "hardness": -1, "light": 11 } }
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `dimension` | yes | dimension id | | Where it sends you |
| `returnDimension` | no | dimension id | `minecraft:overworld` | Where it sends you back to |
| `gate` | no | gate name | none | A gate that must be open to pass |
| `cooldown` | no | int, ticks | `60` | Before the same player can use it again |
| `platform` | no | boolean | `true` | Build a landing platform on arrival |
| `platformBlock` | no | block name | the portal's own frame | What that platform is made of |
| `sound` | no | sound name | none | Played on passing |
| `owned` | no | boolean | `true` | Only whoever built it, and those they allow, may use it. An owned portal is also immune to explosions |
| `walkIn` | no | boolean | `false` | Walking into the block travels, the way a nether portal does. Off, it is used by hand |

### Portal frames

`<namespace>/portalframes/*.json`

The file's path is the frame's registry name, which a dimension then names in `frames`.

A frame is a picture of what a player has to build, and nothing else: it says which blocks make the edge and where the hole is, and says nothing about where the portal leads. That is deliberate, because a dimension claims a frame rather than owning it, and two dimensions may claim the same one.

```json
{
  "name": "Standing Gate",
  "axis": "vertical",
  "legend": { "q": "minecraft:quartz_block", "r": "mypack:ruby_block" },
  "rows": [
    "rqqqqr",
    "q....q",
    "*",
    "rqqqqr"
  ],
  "maxWidth": 6,
  "maxHeight": 9
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `name` | no | string | the file name | The name used in the log |
| `axis` | no | `vertical`, `horizontal` or `both` | `vertical` | Whether it stands up like a nether portal, lies flat like an end portal, or may do either |
| `legend` | yes | object of one character to a block | none | The blocks the rows may use. A block name with states is read the same way as anywhere else |
| `rows` | yes | list of strings | none | The picture, drawn top row first |
| `maxWidth` | no | int | `21` | Widest hole a `*` may stretch to |
| `maxHeight` | no | int | `21` | Tallest hole a `*` may stretch to |

Three characters are not blocks. `.` is the hole the portal stands in, and a frame without one is refused. A space is a cell the frame does not care about, so an L shaped surround is drawn by leaving the corners blank. `*` repeats: a row that is nothing but `*` repeats the row above it as many times as the player built, and a `*` inside a row repeats the character before it the same way. It may repeat no times at all, so the picture read with every `*` struck out is the smallest thing that will light, and the maxima below are the largest. A picture with no `*` in it is exact, and the player must build that and nothing else.

A vertical frame is found on either horizontal axis and either way round, so it does not matter which way the builder faced. A horizontal one is found in all four turns.

**How big it may be is the pack's to say.** `maxWidth` and `maxHeight` are the largest hole a `*` will stretch to, and anything smaller down to the floor is accepted, so a pack decides whether its gate tops out at vanilla's 21 or at 4. The floor is a player: a standing frame is refused unless its hole can be at least 1 across and 2 up, a flat one at least 1 by 1, and a picture that can never reach that is refused at load with a line in the log rather than being a frame nobody can walk through.

**A frame costs more to look for the more it can stretch.** Both a row `*` and a column `*` means every combination up to the two maxima is tried, so a frame that stretches both ways to 21 is 441 pictures. The search gives up rather than hanging, and says so in the log, which is the sign to lower a maximum or drop one of the stretches.

**Nothing stops a frame being obsidian lit by flint and steel, but it takes precedence.** A frame is looked for before the item does its own work, so such a frame opens the pack's dimension where a nether portal would have stood. Pick another block or another igniter to leave vanilla's portal alone.

### Opening a dimension with a frame

`<namespace>/dimensions/*.json`

A dimension opens through a frame by carrying a `portal` section. The frame and what lights it, together, are what choose the dimension, so one frame shape can lead to several places depending on what it was lit with.

```json
{
  "portal": {
    "frames": ["mypack:standing_gate"],
    "ignitedBy": "minecraft:flint_and_steel",
    "color": "#C77DFF",
    "return": "built",
    "gate": "mypack:ruby_gate",
    "cooldown": 60,
    "platform": true,
    "sound": "block.portal.travel"
  }
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `frames` | yes | list of frame names | none | The frames that open this dimension |
| `ignitedBy` | no | item name | `minecraft:flint_and_steel` | What a player holds to light one |
| `color` | no | hex color | white | The color the portal is drawn in |
| `return` | no | `built`, `player` or `none` | `built` | Whether a way back is provided, built by the player, or not at all |
| `gate` | no | gate name | none | A gate that must be open to pass |
| `cooldown` | no | int, ticks | `60` | Before the same player can pass again |
| `platform` | no | boolean | `true` | Build a landing platform on arrival |
| `platformBlock` | no | block name | stone | What that platform is made of |
| `sound` | no | sound name | none | Played on passing |
| `owned` | no | boolean | `false` | Only whoever lit it, and those they allow, may use it |

The block that stands in the hole is not written by the pack. A dimension with a `portal` section is given one of its own, drawn in the game's own portal texture under `color`, walked into rather than used by hand, and unbreakable. The color multiplies the texture, the way a `tintindex` does, so `#C77DFF` keeps the nether's violet and `#4CFFB0` turns it poisonous. For a portal that is not the vanilla texture at all, write an ordinary `portal` block of your own with its own texture, drawn as a [pixel map](#textures-written-as-pixel-maps) if you like, where `tint` can ramp between two colors.

`return` decides what happens on the other side. `built` puts up the same frame, at the size the player built, and lights it, which is the way vanilla behaves. `player` builds nothing but lets the same frame be lit over there, so the way home has to be found and made. `none` refuses to light the frame in that dimension at all, and the trip is one way.

**One frame, several dimensions.** The pair of a frame and the item that lights it is what picks the dimension, so the same `standing_gate` lit with flint and steel and lit with a pack's own igniter opens two different places, each with its own color. Two dimensions claiming the same frame *and* the same item is a mistake in the pack: the second one is refused and says so in the log rather than one of them quietly winning.

Breaking any block of the frame puts the portal out, as it does in vanilla.

`<namespace>/gates/*.json`

The file's path is the gate's registry name, which a portal then names in `gate`.

Every key, shown at once. A real file writes only the ones it needs.

```json
{
  "dimension": "mypack:ruby_world",
  "name": "The Ruby Gate",
  "scope": "player",
  "open": false,
  "unlock": {
    "hold": "mypack:ruby_key",
    "consume": "mypack:ruby",
    "consumeCount": 4,
    "craft": "mypack:ruby_pickaxe",
    "advancement": "mypack:story/ruby",
    "killed": "minecraft:wither",
    "killedCount": 2,
    "killedDrops": "mypack:ruby_key"
  },
  "unlockedMessage": "%dim% is now open",
  "blockedMessage": "You need %item% to enter %dim%",
  "safeReturn": true,
  "portalBlocks": ["mypack:ruby_portal"],
  "requires": ["mypack"]
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `dimension` | yes | dimension id | | The dimension it guards |
| `name` | no | string | the file name | Shown to the player |
| `scope` | no | `player`, `global` | `player` | One player at a time, or the whole world at once |
| `open` | no | boolean | `false` | Whether it starts open |
| `unlock` | no | object | | What opens it. See below |
| `unlockedMessage` | no | string | `%dim% is now open` | Shown when it opens |
| `blockedMessage` | no | string | `You need %item% to enter %dim%` | Shown when it refuses |
| `safeReturn` | no | boolean | `false` | A blocked return still lands somewhere safe rather than refusing |
| `requires` | no | list of mod ids or pack namespaces | none | The gate is skipped unless all are present |
| `portalBlocks` | no | list of block names | every portal | Limits the gate to these portal blocks, so one dimension can have a guarded door and an open one |

`unlock` takes `hold` (an item that must be held), `consume` with `consumeCount` (`1`), `craft` (an item that must have been crafted), `advancement`, and `killed` (an entity name, the gate opens for whoever slays one, so a boss can hold the key to a world) with `killedCount` (`1`) when one is not enough, tallied per player or for the whole world as the scope says. Adding `killedDrops` (an item name) makes the counted kills lay that item at the slayer's feet instead of opening the gate, and starts the counting over, so a key can be earned again and handed to somebody who never fought for it; gate on `hold` or `consume` of the same item to make it the key. `%item%`, `%mob%` and `%dim%` are filled in for you. A key a mob drops needs nothing special here: give the mob the drop and gate on `hold` or `consume`.

Gates guard the game's own dimensions too: a gate whose `dimension` is `minecraft:the_nether` stands in front of every nether portal.

## World templates

`<namespace>/worldtemplates/*.json`

The file's path is the template's name, which the `worldTemplate` config option can name to pick it outright.

Gathers a world's shape into one file, so a pack ships a whole world at once rather than asking the player to set a dozen config options.

```json
{
  "name": "Ruby World",
  "default": "void",
  "dimensions": ["minecraft:overworld"],
  "settings": {
    "voidWorld": true,
    "flatBedrock": true,
    "blockBiomes": true
  },
  "structures": {
    "villages": false,
    "mineshafts": false,
    "strongholds": true
  },
  "roles": { "ocean": "mypack:ruby_ocean" }
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `name` | no | string | the file name | Shown in the log and in reports |
| `default` | no | biome name or `void` | `void` | What fills a biome that blocking removed. `fallback` is the same key by another name |
| `roles` | no | object of role to biome | none | Biomes filling particular roles, such as ocean or river |
| `structures` | no | object of [structure name](#value-lists) to boolean | none | Vanilla structures switched on or off |
| `settings` | no | object | none | Config values the template sets |
| `dimensions` | no | list of dimension ids | every dimension | Which dimensions it applies to |
| `requires` | no | list of mod ids or pack namespaces | none | The template is skipped unless all are present |

`settings` uses the same key names as the config, so there is no translation table to learn.

Which template is active is decided by the `worldTemplate` config option. Left at `auto`, the highest priority pack that ships one wins, the same order everything else follows. Naming a template there picks it outright.

**One biome can build differently.** A `biomes` object inside `settings` holds village settings of its own for a named biome, so a desert village lays sandstone streets where a plains one lays concrete without either being a separate pack. Name a biome by its id, `minecraft:desert`, by one of the type words this mod maps onto biome tags (`sandy`, `snowy`, `desert`, `forest`, `jungle`, `mountain`, `ocean`, `swamp`, `hot`, `cold` and the rest), or by a tag written out, `#minecraft:is_forest`; an exact id is looked at before the types, so a general rule can be overridden for one biome. Everything not named inside a section falls back to the plain setting above it.

```json
{
  "settings": {
    "villagePathBlock": "minecraft:black_concrete",
    "villageSubwayTunnelBlock": "minecraft:stone_bricks",
    "biomes": {
      "sandy": {
        "villagePathBlock": "minecraft:cut_sandstone",
        "villageSubwayTunnelBlock": "minecraft:sandstone"
      },
      "minecraft:snowy_plains": {
        "villageSubwayTunnelBlock": "minecraft:packed_ice"
      }
    }
  }
}
```

Every block setting a road, a bridge, a railway, a subway, a station or a sewer takes answers to this, and the weighted-mix syntax works inside a section as it does outside. The biome is read as a piece is built, and the blocks are taken again wherever the ground changes biome, so a road or a railway crossing out of a desert changes material at the border itself. A log line on world load says how many sections a pack shipped and names them, and with debug on each biome says which section it took, or that it took none and what it would have answered to.

## The deep world

The overworld can be taller or deeper than the game makes it, and the room that opens under the terrain is filled with generation of its own. Four `terrain` keys do it, in a world template's `settings` block like the rest; a pack dimension does the same with `minHeight` and `maxHeight` in its own `terrain`.

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "worldMinHeight": -320,
    "worldMaxHeight": 320,
    "deepStone": "mypack:slate",
    "noiseCaves": "deep"
  }
}
```

| Key | Value | Default | What it does |
| --- | --- | --- | --- |
| `worldMinHeight` | int, a multiple of 16, down to -2032 | `-64` | The lowest block of the overworld. The game's own bottom is -64; lower makes a deep world under the vanilla terrain, solid stone until the worldgen layer carves it or `noiseCaves` carries the game's caves down. Only applied through the generated preset, so a world made before the pack keeps its height |
| `worldMaxHeight` | int, a multiple of 16, up to 2032 and at most 4064 above the floor | `320` | The block above the overworld's top. The game's own top is 320; higher leaves open sky above the vanilla terrain |
| `deepStone` | block name | none | The block the world below the vanilla terrain is made of when the floor goes under -64, such as a pack's own deepslate. It blends into deepslate across the eight layers under -64, the way deepslate blends into stone. Empty keeps stone |
| `noiseCaves` | `off`, `deep` or `world` | `off` | Where the game's caves, tunnels, noodles and aquifers carry on when the floor goes under -64: `off` keeps the world under the vanilla terrain solid deep stone for the worldgen layer to carve, `deep` carries them down to the floor with the lava lakes moved to its bottom ten layers, `world` means the same on this version because the vanilla terrain has them already |

The deep world is where a pack's own worldgen entries, cave regions and hardness groups do their work: `minHeight` and `maxHeight` on an entry reach as far down as the floor goes. The 1.12.2 sky keys, `deepRavines`, `oreVeins`, `terrainOffset` and the rubic world itself have no twin here, since this engine's own generation already reaches from the floor to the ceiling.

## Cave regions

`<namespace>/caveregions/*.json`

The file's path is the region's name, which a worldgen entry then names in `caveRegions`. A bare name there takes that entry's own namespace.

Paints named regions over the underground, the pack counterpart of the game's cave biomes. The underground is divided into rounded cells, `caveRegionCells` blocks wide and `caveRegionCellsY` tall, both `terrain` keys, and each cell rolls one region, or none, by weight. Everything a region does comes deterministically from the seed, so chunks agree with each other without ever writing across a border.

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `caveRegionCells` | int, blocks | `128` | How wide a region cell is |
| `caveRegionCellsY` | int, blocks | `64` | How tall a region cell is |
| `caveRegionPlainWeight` | int | `4` | The weight of plain, region-less underground in each cell's roll. Higher leaves more of the underground without any region: with a single region of weight 1, about a fifth of the cells get it |

Every key, shown at once. A real file writes only the ones it needs.

```json
{
  "weight": 3,
  "minHeight": -56,
  "maxHeight": 16,
  "dimensions": ["minecraft:overworld"],
  "biome": "minecraft:mushroom_fields",
  "floorCover": "minecraft:mycelium",
  "floorChance": 0.8,
  "ceilingCover": "minecraft:brown_mushroom_block",
  "ceilingChance": 0.3,
  "coverReplace": ["minecraft:stone", "mypack:slate"],
  "keepDefaultSpawns": false,
  "spawns": [
    { "entity": "minecraft:mooshroom", "type": "creature", "weight": 12, "min": 2, "max": 4 }
  ],
  "structures": [
    { "structure": "mypack:cave_shrine", "weight": 3 },
    "mypack:cave_well"
  ],
  "structureChance": 0.5,
  "structureLoot": "minecraft:chests/simple_dungeon"
}
```

| Key | Value | Default | What it does |
| --- | --- | --- | --- |
| `weight` | int | `1` | Share of cells this region wins. `0` switches it off |
| `minHeight` | int | the world floor | Bottom of the band the region exists in |
| `maxHeight` | int | `48` | Top of that band. A cell whose center sits outside the band never picks the region |
| `dimensions` | list of dimension ids | all | Which dimensions the region appears in |
| `floorCover` | block | none | Replaces the top block of cave floors inside the region |
| `floorChance` | 0.0 to 1.0 | `1.0` | How much of the floor gets covered |
| `ceilingCover` | block | none | Replaces cave ceiling blocks inside the region |
| `ceilingChance` | 0.0 to 1.0 | `1.0` | How much of the ceiling |
| `coverReplace` | list of blocks | anything stone-like | What the covers may replace |
| `spawns` | list | none | Mobs that spawn inside the region, the same entries a biome's `spawns` takes: `entity`, `type` (monster, creature, ambient or water), `weight`, `min` and `max` for the group size. A spot that can see the sky is left to the biome, like the covers are |
| `keepDefaultSpawns` | boolean | `false` | Keep the biome's own spawn list alongside the region's. Off, the region's list replaces it entirely inside the region |
| `structures` | list | none | A structure placed once per region cell, at the cell's heart, snapped to a cave floor, the way the game gives a cave biome its landmark. Entries are `namespace:name` templates, or `{ "structure": "...", "weight": 3 }` to choose between several |
| `structureChance` | 0.0 to 1.0 | `1.0` | The chance each cell of the region actually gets its structure |
| `structureLoot` | `namespace:path` | none | The loot table every chest inside a placed structure is filled from the first time it is opened |
| `biome` | biome name | none | The biome the region reports inside its volume, written as a 3D biome. Gives the region its own foliage, grass and water colors, music and ambient sounds, and lets vanilla spawn weighting read it. The surface above is untouched, since only the cells the region occupies are written |
| `requires` | list of mod ids or pack namespaces | none | The region is skipped unless all are present |

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "caveRegionCells": 128,
    "caveRegionCellsY": 64,
    "caveRegionPlainWeight": 4
  }
}
```

How much of the underground stays plain is the `caveRegionPlainWeight` `terrain` key, default `4`: with a single region of weight 1, about a fifth of the cells get the region. Covers apply under a roof, so a region reaching above ground never shows on the surface. Covers work in every cave, whichever generator carved it. The 1.12.2 `waterLevel` has no twin: aquifers are the noise settings' on this engine and cannot be pinned per region.

Features tie in through two keys on ordinary [worldgen entries](#worldgen-entries). `caveRegions` lists the regions an entry may generate in, checked at the placed position, so mushrooms, crystals or anything else appear only inside their region. `snap` first moves each attempt vertically to the nearest cave surface: `floor` for things that stand, `ceiling` for things that hang. A dripstone-like region needs no new shapes:

```json
{
  "block": "mypack:stone_spike",
  "attempts": { "min": 4, "max": 8 },
  "minHeight": -60,
  "maxHeight": 40,
  "caveRegions": ["dripstone"],
  "snap": "ceiling",
  "replace": ["minecraft:air"],
  "shape": { "type": "spire", "radius": 1, "height": { "min": 2, "max": 6 }, "taper": "needle", "hanging": true }
}
```

The `replace` of `minecraft:air` matters: what a placed shape writes over is checked against `replace`, whose default is stone, so anything built into open cave space needs air listed. The same entry with `"snap": "floor"` and no `hanging` grows the stalagmites to match. The region filter works with every placed shape; `belt` and `field` place by their own rules and ignore it.

## World intro

`<namespace>/worldintro/*.json`

The file name is yours to choose, only the folder is read. Every intro a pack ships runs, in pack order.

Shows a run of pages when a player enters the world, before they take control. Scrolling text over a picture, a title card, a slideshow, or all three in a row.

```json
{
  "once": true,
  "music": "minecraft:music.credits",
  "requires": ["mypack"],
  "pages": [
    {
      "background": "mypack:textures/gui/sunrise.png",
      "text": "mypack:texts/opening.txt",
      "mode": "scroll",
      "time": 14.0,
      "direction": "up",
      "textScale": 3.0,
      "settle": true
    },
    {
      "backgrounds": [
        "mypack:textures/gui/logo_a.png",
        "mypack:textures/gui/logo_b.png"
      ],
      "interval": 4.0,
      "text": "mypack:texts/title.txt",
      "mode": "static",
      "textScale": 2.0
    }
  ]
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `pages` | yes | list of pages | none | Shown in order. A file with no pages is refused with an error |
| `once` | no | boolean | `false` | Play once per player per world instead of on every join |
| `music` | no | sound event name | none | One track for the whole run, started with the first page |
| `requires` | no | list of mod ids or pack namespaces | none | The intro is skipped unless all are present |

Each entry in `pages`:

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `mode` | no | `scroll` or `static` | `scroll` | Text that moves, or text that sits still until the player moves on |
| `text` | no | path to a `.txt` file | none | The words. Leave it out for a page that is just pictures |
| `background` | no | texture path | the tiled dirt background | One background |
| `backgrounds` | no | list of texture paths | none | Several, cycled. Adds to `background` if you give both |
| `interval` | no | seconds | `5.0` | How long each background is held, when there is more than one |
| `time` | no | seconds | worked out from the text | How long a scrolling page takes, start to finish |
| `direction` | no | `up` or `down` | `up` | Which way scrolling text travels |
| `textScale` | no | number | `1.0` | Multiplies the font size |
| `settle` | no | boolean | `false` | Finish with the last line centered rather than running clear off the screen |

Text files go in `assets/<namespace>/texts/*.txt`. Plain text, one paragraph to a line, and blank lines are kept as blank lines. `PLAYERNAME` is swapped for the player's name, the same substitution the vanilla end poem uses.

`time` sets how long the page lasts, so the same page takes the same time whether it holds one line or twenty. Tune the reading speed by how much you put on the page. Leave `time` out and the page runs at the same speed as the vanilla credits, where more text simply takes longer.

A scrolling page moves to the next one when its time is up. The last page never advances on its own, it waits. Along the bottom are **Next Page** and **Skip All**, or a single **Continue to World** on the last page. Escape does the same as Skip All. Static pages center every line. Scrolling pages keep to a fixed column, the way the credits do.

In singleplayer the world pauses behind the intro, so nothing creeps up on the player while they read. The one exception is land still being made when the intro opens: then the making carries on behind the pages, and the player stays held as a spectator until they continue to the world, even if the run finishes first. On a server the world keeps running, and a vanilla client never sees the intro at all and joins as normal. The welcome greeting waits until the pages are closed, so it is not lost behind them.

`once` is remembered in the player's saved data and survives death. `/rdplserver intro` clears it for whoever runs it, so the intro plays again the next time they join. It does not replay on the spot, which keeps it from being a way back into the entry sequence in the middle of a game.

Backgrounds are stretched to fill the window, so a 16:9 image suits a 16:9 window and a square one looks squashed. Crop the picture to shape rather than relying on the fit. `music` takes any registered sound event, vanilla or one your own pack adds through `sounds`. It does not loop, so a short track finishes and leaves quiet behind it.

If more than one pack ships an intro, their pages run end to end in pack order rather than one winning. Gate them with `requires` if you only want one.

## Game rules

`<namespace>/gamerules/*.json`

The file name is yours to choose, only the folder is read, and several files stack.

```json
{
  "minecraft:overworld": {
    "doFireTick": "false",
    "keepInventory": "true",
    "randomTickSpeed": "3"
  },
  "minecraft:the_nether": {
    "doFireTick": "true"
  }
}
```

Each key is the id of the world the rules belong to, `minecraft:overworld`, `minecraft:the_nether`, `minecraft:the_end`, a pack's own or whatever a mod uses; the 1.12.2 numbers `0`, `-1` and `1` are still taken as the vanilla three. Values are strings, as they are in the `/gamerule` command, so `"false"` rather than `false`. These are applied to new worlds. A dimension file carries the same rules in a `gameRules` block instead, which only ever applies to that world.

## Teams

`<namespace>/teams/*.json`

The file name is yours to choose, only the folder is read, and several files stack. Each file is one side.

A side is a real team on the game's own scoreboard, so `/team list` sees it, it keeps its members through a save and a reload, and a client without this mod shows the colors and the nameplates the same as any vanilla team. Membership is by name, so anything with a name or a UUID can be on a side: a player, a zombie, a villager, an armor stand.

```json
{
  "name": "red",
  "displayName": "Red Team",
  "color": "red",
  "friendlyFire": false,
  "joinable": false,
  "entities": ["mypack:zombie_a", "mypack:sapper_a"]
}
```

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `name` | text | the file name | The team's name on the scoreboard, 1 to 16 characters. This is what `/team` and the other files use |
| `displayName` | text | the name | What players are shown instead of the name |
| `color` | text | `white` | One of the sixteen text colors. It tints the nameplate and is what the per-team sidebar slots key off |
| `prefix` | text | empty | Put in front of a member's name, after the color |
| `suffix` | text | empty | Put after a member's name |
| `friendlyFire` | boolean | `false` | Whether members can hurt each other. Also the default of `mobFriendlyFire` |
| `mobFriendlyFire` | boolean | `friendlyFire` | Whether a side's mobs can hurt their own side with explosions and thrown TNT, which the game alone never stops. Off spares the side; on leaves it as the game has it |
| `seeFriendlyInvisibles` | boolean | `true` | Whether members see each other while invisible |
| `nameTags` | text | `always` | `always`, `never`, `hideForOtherTeams` or `hideForOwnTeam` |
| `deathMessages` | text | `always` | The same four words, for who is told when a member dies |
| `collision` | text | `always` | `always`, `never`, `pushOtherTeams` or `pushOwnTeam` |
| `entities` | list | empty | Entity ids whose every spawn joins this side, such as `minecraft:zombie` or one of your own |
| `players` | list | empty | Player names that join this side as they log in |
| `spawnBox` | list | none | Six whole numbers, x y z to x y z. Anything spawning inside joins, and the corners may be given either way round |
| `joinable` | boolean | `true` | Whether a player may join with `/rdplserver team join`. Set it false for a side that is only for mobs |
| `balance` | boolean | `false` | Whether `/rdplserver team join` with no name may put a player here. Among the sides that allow it, the one with the fewest players is chosen |
| `scoreboard` | boolean | `true` | Whether the side stands as a team on the game's scoreboard. Off fields no team at all: its mobs wear the side's color in their name instead, nothing keeps them from fighting each other, and no points land on it, since scoring goes by the team |
| `lead` | text | `none` | How the side's lead is chosen: `none`, `topScore` for whoever is highest on the objective `leadOn` names, `appointed` for the player `leadIs` names, `vote` for whoever the members vote for, or `claim` for whoever claims it first. A lead is a label and a color and nothing more: it grants no power, so a lead who logs out breaks nothing |
| `leadOn` | text | empty | With `topScore`, the objective the members are ranked by. It is worked out afresh every time it is read, so it follows the score |
| `leadIs` | text | empty | With `appointed`, the player who leads |

Three ways to join, and a side may use all of them. `entities` names entity ids, and anything of that type joins as it spawns, which is how a pack gives mobs sides without touching the mobs. `spawnBox` claims a corner of the world, and anything spawning inside joins, which suits an arena where both sides use the same mob. `players` names players outright. Beyond those, a player can join with `/rdplserver team join <name>` unless the side sets `joinable` to false, and leave with `/rdplserver team leave`.

A side is only fielded where a pack asks for one: with no `teams` folder anywhere the mod adds no team, listens for nothing, and does not offer the command. A server operator who edits a file can run `/rdplserver reload` to field the change into the running world without restarting.

## Scoring

`<namespace>/scoring/*.json`

The file name is yours to choose, only the folder is read, and several files stack. Each file is one objective.

An objective is a real objective on the game's own scoreboard, so `/scoreboard players list` reads it and it keeps its scores through a save. `criterion` is what the game counts by itself: `dummy` for a score only this pack moves, or `deathCount`, `playerKillCount`, `totalKillCount`, `health`, `air`, `armor`, `food`, `level`, `xp`, `trigger`, or any statistic written the way `/scoreboard` takes it, such as `minecraft.custom:minecraft.jump`.

```json
{
  "name": "kaboom",
  "displayName": "Kills",
  "criterion": "dummy",
  "display": "sidebar",
  "teamTotals": true,
  "points": {
    "kill": { "mypack:zombie_a": 1, "mypack:zombie_b": 1 },
    "death": -1
  },
  "ends": {
    "afterMinutes": 10
  },
  "results": {
    "card": true,
    "title": "Final standings",
    "icon": "minecraft:tnt",
    "seconds": 15
  }
}
```

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `name` | text | the file name | The objective's name on the scoreboard, 1 to 16 characters |
| `displayName` | text | the name | What players are shown instead of the name |
| `criterion` | text | `dummy` | What the game counts by itself. An unknown one is refused with a line saying so |
| `display` | text | empty | `sidebar`, `list`, `belowName` (`below_name` is taken too) or `sidebar.team.<color>`. Empty shows it nowhere; there is no scoreboard screen to open |
| `render` | text | the criterion's own | `integer` or `hearts` |
| `teamTotals` | boolean | `true` | Points land on a row named after the member's team |
| `individuals` | boolean | `false` | Points also land on a row for the member itself |
| `carries` | boolean | `false` | The objective survives a map reset instead of being wiped with it. A match tally of round wins is one |
| `awardsTo` | text | empty | Another objective this one hands a point to when it ends, to the side that led. Level standings hand out nothing |
| `points.kill` | object | empty | Entity id to points, credited to the killer's side. `minecraft:player` scores a player kill |
| `points.death` | int | `0` | Points whenever a member dies, however it died. May be negative |
| `points.ownKill` | int | `0` | Points for a kill of the killer's own side, in place of the `kill` value. 0 scores nothing for it; a negative number is a penalty |
| `ends.atScore` | int | `0` | The match ends the moment a side reaches this. 0 never ends on score |
| `ends.afterMinutes` | int | `0` | The match ends after this many minutes. 0 never ends on time |
| `ends.afterRounds` | int | `0` | For an objective another one `awardsTo`: the match ends once this many rounds have been awarded in all, whoever took them. 0 never ends on rounds |
| `ends.resets` | boolean | `false` | Ending the round resets the map, as `resetSays` and the other reset settings under [World templates](#world-templates) describe, then a new round opens |
| `ends.intermissionSeconds` | int | `10` | How long the standings stand between the end and the reset |
| `ends.intermissionSays` | text | `Round cooldown {seconds}` | Shown on the action bar every second of the intermission after a round ends, with `{seconds}` counting down to the reset. Empty shows nothing |
| `ends.startsSays` | text | `Round starting in {seconds}` | Shown on the action bar through the five-second count that opens the next round after the reset, with `{seconds}` counting down. Empty shows nothing |
| `ends.locksTeams` | boolean | `true` | Joining a side while a round is running waits until the round is over, so nobody drops into a scored round partway |
| `results.card` | boolean | `false` | Show the standings as a card rather than as chat |
| `results.title` | text | the name and `results` | The card's heading |
| `results.icon` | text | empty | An item drawn on the card, e.g. `minecraft:tnt` |
| `results.image` | text | empty | An image drawn on the card instead of an item |
| `results.background` | text | a dark slate | The card's background color |
| `results.seconds` | int | `8` | How long the card stands, at least one second |

`points` is what this mod adds on top of what the game counts, fed into the same objective so `/scoreboard` still reads it. `kill` is worth so many points per entity id killed, credited to the killer's side; `death` is worth so many whenever a member of a side dies, and may be negative. With `teamTotals` the points land on a row named after the team, which is what lets the sidebar show four sides rather than a row for every mob. `individuals` adds a row per member as well, and is off by default because a row per mob UUID reads as noise.

`ends` finishes the match, either the moment a side reaches `atScore` or once `afterMinutes` have passed. The standings are then shown, ranked by the game itself: as chat, or as a card if `results` asks for one. A player without this mod is told the same standings as chat lines, so nobody is left without a result. With `resets`, that end is a round's: the standings stand for `intermissionSeconds` while a cooldown counts down on the action bar, the map resets to the welcome, and the next round opens after a five-second count. `awardsTo` hands the round to the side that led, on an objective that `carries` across the reset. A carried objective can end on its own -- `atScore` for a best-of, `afterRounds` for a fixed count -- and its standing is cleared at the reset after, so a fresh match opens.

## Hardness groups

`<namespace>/hardness/*.json`

The file's path names the group in the log and nothing else reads it, so several files stack.

Gives a group of blocks a mining time multiplier, rolled per block position. The block itself is never changed: nothing is registered, nothing is written into the world, and a world opened without the pack is ordinary vanilla.

```json
{
  "blocks": ["minecraft:stone"],
  "except": [{ "block": "minecraft:oak_log", "properties": { "axis": "y" } }],
  "miningTime": { "min": 1.0, "max": 20.0 },
  "blastResistance": { "min": 1.0, "max": 4.0 },
  "buckets": 10,
  "minHeight": -64,
  "maxHeight": 319,
  "field": { "type": "speckle", "spread": 0.15 },
  "requires": ["mypack"]
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `blocks` | yes | list of block names or objects | | The group. Same forms as a worldgen `replace` |
| `except` | no | list of block names or objects | none | Taken back out of the group, whatever `blocks` says |
| `miningTime` | no | number, or object with `min` and `max` | `1.0` | How many times longer the block takes to break |
| `blastResistance` | no | number, or object with `min` and `max` | `1.0` | Multiplies the block's blast resistance |
| `buckets` | no | 1 to 256 | `10` | How many steps the range is divided into |
| `minHeight` | no | int | the world floor | Below this the roll is the hardest step |
| `maxHeight` | no | int | the world top | Above this the roll is the hardest step |
| `field` | no | object | see below | The shape the roll clumps into |
| `requires` | no | list of mod ids or pack namespaces | none | The file is skipped unless all are present |

A single number gives every block in the group the same multiplier, and nothing is rolled. A `min` and `max` roll per position: `max` where the field is empty, `min` at the middle of a clump, and the steps between decided by `buckets`.

### The field

The roll is not made for each block entirely on its own, or hard and soft would be pure static with no shape to them. `field` decides what shape it takes, and `type` picks between two ways of getting there.

```json
{
  "field": { "type": "speckle" }
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `type` | no | `speckle` or `seeded` | `speckle` | Which of the two below is used |

#### speckle

Every block draws its own step, and a block one face away can pass a weaker step on to it. That gives dense, fine-grained specks, most of them a single block, with the odd larger patch where they meet.

```json
{
  "field": {
    "type": "speckle",
    "chances": [30, 30, 20, 20, 10, 10, 10, 10, 50],
    "spread": 0.15
  }
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `chances` | no | list of ints, per thousand | `[30, 30, 20, 20, 10, 10, 10, 10, 50]` | How often a block starts at each step, softest last. Anything left over is the hardest step |
| `spread` | no | 0.0 to 1.0 | `0.15` | How often a step carries to the block next to it, one step weaker or three |

The list is read softest-last, so the final entry is the softest step and the first is one above hardest. With the numbers above about seven blocks in ten are the hardest step and the rest are scattered through it.

#### seeded

Seeds sit on a lattice worked out from the world and the position, and a block's step comes from how close it is to the nearest one. That gives fewer, larger, rounder patches that run into one another, and it can grow arms.

```json
{
  "field": {
    "type": "seeded",
    "cell": 8,
    "seeds": 1,
    "reach": 3.0,
    "arms": 0,
    "armReach": 0.0
  }
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `cell` | no | int, blocks | `8` | How far apart the seeds are |
| `seeds` | no | 1 to 4 | `1` | Seeds in each cell |
| `reach` | no | float, blocks | `3.0` | How far a seed's influence carries |
| `arms` | no | 0 to 6 | `0` | Arms radiating from each seed |
| `armReach` | no | float, blocks | `0.0` | How far the arms carry |

With `arms` left out the patches are round. Giving a seed arms turns it into a knot with tendrils, and arms from neighboring knots reach toward each other, which is a vein rather than a blob. Keep `reach` above half of `cell` or the patches cannot touch and you get separate balls with nothing between them.

### Showing it

The multiplier is invisible on its own. To let a player see which blocks are tough, give the block a blockstate with one variant per bucket, all of equal weight, listed hardest first:

```json
{
  "variants": {
    "": [
      { "model": "mypack:block/stone_step0", "weight": 1 },
      { "model": "mypack:block/stone_step1", "weight": 1 }
    ]
  }
}
```

Minecraft already picks a variant from a block's position, and a hardness group hands it the bucket instead, so the texture and the multiplier always agree. The example pack does exactly this for its ruby stone.

Three things have to be right, and none of them announce themselves when they are wrong.

**Exactly `buckets` entries, all weighing the same.** The bucket is used as a place in the list, so a list of a different length, or one where the weights differ, quietly points at the wrong texture.

**A model name with `block/` in front.** A blockstate on this version names the model file in full, so `"model": "mypack:block/stone_step0"` reads `models/block/stone_step0.json`; a bare `mypack:stone_step0` looks for `models/stone_step0.json`, which is not there, and the entry is dropped without a word.

**The same key the game asks for.** A block with one state is keyed `""`, and vanilla stone is one of those. A block with properties is keyed by all of them, so an override for a log wants `axis=x`, `axis=y` and `axis=z` each with its own list.

Turn on `worldgenDebug` and every hardness group is checked against its baked model when a world is entered, naming the blockstate, how many variants survived, what texture each one ended up with, and which packs the game merged to get there. That is the quickest way to find any of the three above, and it also warns when overriding a shared blockstate has changed a state the group never named.

### What it does not reach

Only a player's own mining is changed. Machines that break blocks read the block's hardness directly and are not affected. Blocks a player places are rolled the same as any other, since the roll belongs to the place rather than to the block, and a block carried elsewhere takes on whatever its new place says.

# Generating it

## Worldgen entries

`<namespace>/worldgen/*.json`

The file's path names the entry, and the `belt`, `field` and `vein` shapes seed their noise from it, so renaming a file moves what it generates.

Describes something that generates. Every entry is a **shape** placed by a **spread**, filtered by where it is allowed.

```json
{
  "block": "mypack:ruby_ore",
  "blocks": [
    { "block": "mypack:ruby_ore", "weight": 80 },
    { "block": "minecraft:magenta_wool", "weight": 20 }
  ],
  "size": 8,
  "attempts": 12,
  "replace": ["minecraft:stone"],
  "adjacent": ["minecraft:air"],
  "minHeight": 8,
  "maxHeight": 48,
  "dimensions": ["minecraft:overworld"],
  "dimensionsAreBlacklist": false,
  "biomes": ["minecraft:windswept_hills"],
  "biomeTypes": ["mountain"],
  "biomesAreBlacklist": false,
  "minTemperature": -100.0,
  "maxTemperature": 100.0,
  "minRainfall": -100.0,
  "maxRainfall": 100.0,
  "minDistanceFromSpawn": 0,
  "sparse": false,
  "retrogen": false,
  "retrogenKey": "ruby_v1",
  "caveRegions": ["dripstone"],
  "snap": "floor",
  "snapDepth": 0,
  "indicators": ["mypack:iron_rock=3", "minecraft:gravel=1", "empty=4"],
  "indicatorCount": { "min": 1, "max": 3 },
  "indicatorSpread": 2,
  "then": ["mypack:quartz_halo=2", "empty=1", { "name": "mypack:side_branch", "weight": 1, "spread": 4, "depth": 0 }],
  "thenCount": 1,
  "thenSpread": 6,
  "thenDepth": { "min": -8, "max": -2 },
  "prospectAs": "Ruby",
  "requires": ["quark"],
  "shape": { "type": "cluster" },
  "spread": { "type": "even" }
}
```

Only `block` is required; everything else may be left out and takes its default. `blocks` replaces `block` when one is not enough and has its own example below.

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `block` | yes | block name | | What is placed |
| `blocks` | no | list of objects | none | A weighted list, used instead of one block. See below |
| `size` | no | int or range | `8` | How many blocks one attempt places, or how large a shape with a radius is |
| `attempts` | no | int or range | `8` | How many times per chunk it tries |
| `replace` | no | list of block names or objects | `["minecraft:stone"]` | What it may replace. See below |
| `adjacent` | no | list of block names or objects | none | Only place where one of these is among the 26 blocks touching the spot. Same forms as `replace` |
| `minHeight` | no | int | `0` | Lowest y it will place at |
| `maxHeight` | no | int | `64` | Highest y it will place at |
| `dimensions` | no | list of dimension ids | every dimension | Which dimensions it runs in |
| `dimensionsAreBlacklist` | no | boolean | `false` | Turn that list into the ones to avoid |
| `biomes` | no | list of biome names | every biome | Which biomes it runs in |
| `biomeTypes` | no | list of biome types | none | Biomes by type word, such as `forest` or `nether` |
| `biomesAreBlacklist` | no | boolean | `false` | Turn those lists into the ones to avoid |
| `minTemperature` | no | float | `-100.0` | Coldest biome it will generate in |
| `maxTemperature` | no | float | `100.0` | Warmest biome it will generate in |
| `minRainfall` | no | float | `-100.0` | Driest biome it will generate in |
| `maxRainfall` | no | float | `100.0` | Wettest biome it will generate in |
| `minDistanceFromSpawn` | no | int, blocks | `0` | How far from world spawn before it starts |
| `sparse` | no | boolean | `false` | Scatters the blocks instead of packing them together |
| `retrogen` | no | boolean | `false` | Also generate into chunks that already exist |
| `retrogenKey` | no | string | the config's key | Overrides the retrogen key for this entry alone |
| `requires` | no | list of mod ids or pack namespaces | none | The entry is skipped unless all are present |
| `shape` | no | object | `{ "type": "cluster" }` | The form it takes. See [Shapes](#shapes) |
| `spread` | no | object | `{ "type": "even" }` | Where it is put. See [Spreads](#spreads) |
| `caveRegions` | no | list of region names | none | Only generate inside these [cave regions](#cave-regions) |
| `snap` | no | `floor` or `ceiling` | none | Move each attempt vertically to the nearest cave floor or ceiling first |
| `snapDepth` | no | int | `0` | How far past the surface `snap` then moves, down from a floor and up from a ceiling. `0` stays in the open space against the surface, `1` is the surface block itself, `2` the one behind it. What it may overwrite is still governed by `replace`, so this is how a pack bands a block just under the ground rather than on top of it |
| `indicators` | no | list of `block=weight` | none | Blocks left scattered on the surface over a vein that generated, so a player can tell what lies under the ground; pick them to match the vein's contents. `empty=weight` leaves a spot bare |
| `indicatorCount` | no | int or range | `1` | How many surface spots each generated vein gets |
| `indicatorSpread` | no | int, blocks | `0` | How far past the vein's footprint an indicator may land |
| `then` | no | list of `name=weight` or objects | none | Worldgen entries that grow out of this one right after it generates, attached to it: the follower's origin is set just outside this vein's edge, in the direction `thenSpread` and `thenDepth` give, so the two touch. An entry is `name=weight`, or an object with `name`, `weight` and its own `spread` and `depth` (int or range) that override the vein's for that follower alone, so one list can send a diamond tip down and a branch sideways. A bare name is read in this pack's namespace, `empty=weight` queues nothing. A follower keeps its own shape, blocks, size and `replace` but skips its own attempts, chance, height band and biome gates, and may carry `then` itself, as deep as the pack wants; an entry that already generated in the same chain stops it |
| `thenCount` | no | int or range | `1` | How many different followers are picked from that list per generated vein, each entry at most once, so a count equal to the list's length grows every one of them |
| `thenSpread` | no | int, blocks | the shape's radius | How far sideways the direction a follower grows in may lean, rolled from minus this to plus this |
| `thenDepth` | no | int or range | `0` | How far down (negative) or up the direction leans. `0` with no sideways lean hangs the follower straight down |
| `prospectAs` | no | string | the file name | How a prospecting item names this entry in its reading, e.g. `Hematite` |

### Weighted blocks

`blocks` replaces `block` when one entry is not enough. Weights are relative, so 80 and 20 is four to one.

```json
{
  "blocks": [
    { "block": "minecraft:magenta_wool", "weight": 80 },
    { "block": "minecraft:oak_log", "weight": 20, "properties": { "axis": "x" } }
  ]
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `block` | yes | block name | | What is placed |
| `weight` | no | int | `1` | How often this one is chosen against the others |
| `properties` | no | object of property to value | none | Block state properties by name, for a state other than the block's default |

`block` is still required at the top level of the file even when `blocks` is used; the first entry is a good value to put there.

### Replace targets

`replace` is a list, and each entry takes one of two forms.

```json
{
  "replace": [
    "minecraft:stone",
    { "block": "minecraft:oak_log", "properties": { "axis": "y" } }
  ]
}
```

| Form | Example | What it matches |
| --- | --- | --- |
| Name | `"minecraft:stone"` | Every state of that block |
| Object | `{ "block": "minecraft:oak_log", "properties": { "axis": "y" } }` | Only that state |

A 1.12.2 name with metadata on the end, `minecraft:stone:3`, matches every state of the block and says so in the log, since the blocks that carried metadata are separate blocks now: write `minecraft:diorite`. Use `"minecraft:air"` to generate in open space.

### Adjacent blocks

`adjacent` takes the same forms as `replace` and adds a second condition on top of it: the spot is only used when at least one of the 26 blocks touching it, faces, edges and corners, matches the list. Left out, nothing is checked.

```json
{
  "block": "mypack:sulfur_ore",
  "replace": ["minecraft:sandstone"],
  "adjacent": ["minecraft:air"]
}
```

That places sulfur in sandstone only where it is already open to a cave or the surface, and leaves buried sandstone alone. Neighbors in chunks that do not exist yet are treated as not matching rather than being read, so the check never causes a chunk to generate.

Every shape honors it, since it is part of deciding whether a single block may be taken. A `geode` names its crust and filling separately, and those two are placed without the check.

An entry naming only blocks that are not registered is skipped with an error rather than generating everywhere.

### Follower entries

An entry in a worldgen entry's `then` list is a name with a weight, or an object when that follower needs a direction of its own.

```json
{
  "then": [
    "mypack:quartz_halo=2",
    "empty=1",
    { "name": "mypack:side_branch", "weight": 1, "spread": 4, "depth": 0 }
  ]
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `name` | yes | entry name | | The worldgen entry that grows out of this one. A bare name is read in this pack's namespace |
| `weight` | no | int | `1` | How often this follower is picked against the others in the list |
| `spread` | no | int, blocks | the entry's `thenSpread` | How far sideways this follower's direction may lean, for this entry alone |
| `depth` | no | int or range | the entry's `thenDepth` | How far down, negative, or up this follower's direction leans, for this entry alone |

`name=weight` is the short form of an object with only those two, and `empty=weight` queues nothing. Because `spread` and `depth` are per entry, one list can send a diamond tip straight down and a branch sideways off the same vein.

## Shapes

A `shape` block with a `type`. Keys not listed for a type are ignored by it.

Every key, shown at once. A real file writes only the ones it needs. A key marked for one type is read only by that type.

```json
{
  "shape": {
    "type": "geode",
    "radius": 6,
    "height": 8,
    "width": 12,
    "plane": "circle",
    "slim": false,
    "hanging": false,
    "taper": "needle",
    "outline": "minecraft:obsidian",
    "fill": "minecraft:glowstone",
    "surface": ["minecraft:grass_block"],
    "seeSky": true,
    "checkStay": true,
    "stackHeight": 1,
    "scatterX": 8,
    "scatterY": 4,
    "scatterZ": 8,
    "log": "mypack:ruby_log",
    "leaves": "mypack:ruby_leaves",
    "vines": false,
    "structure": "mypack:crypt",
    "structures": [
      { "structure": "mypack:crypt", "weight": 3 },
      "mypack:shrine"
    ],
    "integrity": 100,
    "lootTable": "minecraft:chests/simple_dungeon",
    "turns": ["none", { "turn": "half", "weight": 2 }],
    "mirrors": ["none", { "mirror": "leftright", "weight": 2 }],
    "at": [1000, -500],
    "locateAs": "Crypt",
    "field": { "type": "speckle", "spread": 0.15 },
    "threshold": 0.5,
    "fade": 0,
    "pattern": "banded",
    "density": 0.8,
    "rich": "mypack:rich_ruby_ore",
    "poor": "mypack:poor_ruby_ore",
    "rarity": 400,
    "rarityIsPerChunk": false
  }
}
```

```json
{
  "shape": { "type": "tree", "log": "mypack:ruby_log", "leaves": "mypack:ruby_leaves", "height": { "min": 4, "max": 7 }, "surface": ["minecraft:grass_block"] }
}
```

A `tree` with no `log` or `leaves` generates nothing, and says so in the log. Naming a `structure`, or several under `structures`, plants that template at each spot instead of growing one, and then no `log` or `leaves` is needed; a templated tree reads `turns`, `mirrors`, `integrity`, `lootTable` and `locateAs` exactly as an `imprint` does.

| Type | What it makes |
| --- | --- |
| `cluster` | The default blob, an ore vein. Uses `size` |
| `largevein` | A long wandering vein with branches. Uses `size` |
| `plate` | A flat disc |
| `geode` | A hollow pocket with a crust |
| `decoration` | Surface scatter, such as flowers or mushrooms. Uses `size` |
| `tree` | A whole tree |
| `vines` | Vines on what is already there. Uses `size` |
| `basin` | A bowl that deepens toward the middle |
| `spire` | A tapering column |
| `nodule` | A rough ball |
| `vent` | A narrow column that stops when it hits something |
| `imprint` | One of your `.nbt` templates. One that fits inside a chunk is nudged so it lands whole in the chunk being built rather than reaching into a neighbor that has not been made yet, whichever way it is turned; one larger than a chunk is placed only where the ground around it already exists |
| `belt` | A cluster spanning several chunks, for stone regions |
| `field` | Veins worked out for every block at once, sharing their shape with hardness groups |
| `vein` | A deposit worked out as a seeded noise field around an origin: every chunk writes its own slice of every vein whose 24-block reach touches it, so nothing cascades, and `/rdplserver vein` can tell where a vein will be before the land is made. Uses `size`, `attempts`, `rarity` and the height band; `pattern` picks the look |

| Key | Used by | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `type` | all | one of the shapes above | `cluster` | Which shape |
| `radius` | plate, geode, basin, spire, nodule, vent | int or range | `6` | How wide it is |
| `height` | plate, geode, basin, spire, vent, tree | int or range | `1`, `8` for geode, `5` for tree | How tall or thick it is |
| `width` | geode | int or range | `12` | The overall span of the pocket |
| `plane` | plate, basin, spire, vent | `circle`, `square` | `circle` | Its footprint |
| `slim` | plate, largevein, nodule | boolean | `false` | Plate: one layer thinner. Largevein: single block branches. Nodule: hollow shell |
| `hanging` | spire, vent | boolean | `false` | Grow downward from a ceiling instead of up from a floor |
| `taper` | spire | `straight`, `bell`, `needle` | `straight` | How the width falls away toward the tip. `straight` narrows evenly, `bell` keeps its width low down then drops, `needle` thins at once into a long point |
| `outline` | geode | block name | none | The crust block |
| `fill` | geode | block name | none | What fills the middle. Left out, the middle is hollow |
| `surface` | decoration, tree | list of block names | none | What it will sit on |
| `seeSky` | decoration | boolean | `true` | Only place where the sky is visible |
| `checkStay` | decoration | boolean | `true` | Only place where the block would survive |
| `stackHeight` | decoration | int or range | `1` | How many to stack on top of each other |
| `scatterX` | decoration, tree | int | `8` | How far it wanders sideways |
| `scatterY` | decoration, tree | int | `4` | How far it wanders vertically |
| `scatterZ` | decoration, tree | int | `8` | How far it wanders sideways |
| `log` | tree | block name | none | The trunk block |
| `leaves` | tree | block name | none | The leaf block |
| `vines` | tree | boolean | `false` | Hang vines from the leaves |
| `structure` | imprint, tree | `namespace:name` | none | The template to place |
| `integrity` | imprint, tree | 1 to 100 | `100` | Percentage of the template's blocks that actually appear |
| `lootTable` | imprint, tree | `namespace:path` | none | The loot table every chest inside the placed template is filled from the first time it is opened, and any other container that takes one, a shulker box or a mod's crate among them. Covers `structure` and every entry of `structures`; each chest rolls its own seed |
| `structures` | imprint, tree | list | none | Several templates to choose between, one placed each time. Each entry is `{ "structure": "namespace:name", "weight": 3 }`, or a bare name for equal odds. Overrides `structure` |
| `turns` | imprint, tree | list | any | Which way round it may be placed: `none`, `quarter`, `half`, `threequarter`. Entries may carry a `weight`. Left out, all four are equally likely |
| `mirrors` | imprint, tree | list | none | Flip it as well: `none`, `leftright`, `frontback`, with optional `weight`. An entry naming its own weight is written `{ "mirror": "leftright", "weight": 2 }`, and a `turns` entry the same with `turn` |
| `at` | imprint | two ints, x and z | none | Place exactly once at those block coordinates on the surface, when that chunk generates, instead of by chance. See [Structures at exact places](#structures-at-exact-places) |
| `locateAs` | imprint, tree | string | none | Register every structure this entry places under that name, so `/rdplserver locate <name>` finds the nearest. See [Finding placed structures](#finding-placed-structures) |
| `field` | field | object | `{ "type": "speckle" }` | How the field is worked out. Same keys as a hardness group's `field`, described under [The field](#the-field): `speckle` with `chances` and `spread`, or `seeded` with `cell`, `seeds`, `reach`, `arms` and `armReach` |
| `threshold` | field, vein | 0.0 to 1.0 | `0.5` (`0.4` for vein) | How strong the field must be at a block before it is placed. Lower fills more |
| `fade` | field | int | `0` | Speckle out the top of the band instead of ending it flat: over the top this many blocks of the height range, each block's odds of placing thin out step by step, the same look the engine gives `deepStone` where it meets the world above |
| `rarity` | any | int | none (`400` for belt) | One placement per this many chunks. On a belt this spaces the belts out; on any other shape it gates the whole entry so only one chunk in this many rolls its `attempts` at all. `field` ignores it |
| `rarityIsPerChunk` | any | boolean | `false` | Turn `rarity` into how many placements each chunk gets instead |
| `pattern` | vein | `default`, `banded` or `tube` | `default` | The deposit's look: a warped blob, layers stacked every few blocks, or hollow tubes winding through the rock |
| `density` | vein | 0.0 to 1.0 | `1.0` | The share of qualifying blocks that are actually placed, a per-block coin |
| `rich` | vein | block name | none | Placed in the top fifth of the field's range above `threshold`, the heart of the deposit, instead of the entry's blocks |
| `poor` | vein | block name | none | Placed in the bottom two fifths of that range, the fringe, instead of the entry's blocks; the middle is the entry's own blocks. Either tier left out places the entry's blocks there |

A `field` vein is the one shape you describe rather than pick. It runs the same lattice the hardness groups use, so `seeded` with a few arms gives knots with tendrils reaching toward their neighbors, which is a vein rather than a blob, and `threshold` decides how much of it is solid enough to place:

```json
{
  "shape": {
    "type": "field",
    "threshold": 0.4,
    "field": { "type": "seeded", "cell": 10, "reach": 6.0, "arms": 3, "armReach": 5.0 }
  }
}
```

The keys go in a `field` object of their own, not beside `type`, since `type` on the shape already says `field`.

For a shape no built-in type covers, `imprint` is the way: build it as an `.nbt` template and place that, with `structures` to vary it, `turns` and `mirrors` to turn it about, and `integrity` to dissolve it into something rougher than the file you drew.

### Belts

A `belt` is a ball far bigger than one chunk, used for stone regions rather than ore veins. Its `radius` is the ball's size, and every chunk works out for itself where the balls near it start, from the world seed and the entry's own name, so a belt comes out whole however the chunks are generated and nothing is ever written into a neighboring chunk.

```json
{
  "shape": { "type": "belt", "radius": 32, "rarity": 400 }
}
```

A belt ignores `attempts` and `spread`, since it is placed per chunk rather than per attempt. `minHeight` and `maxHeight` are the band the centers sit in, and the ball reaches `radius` beyond that band. `replace` decides what it eats, `biomes` and the temperature and rainfall limits are checked at the center, so a belt either appears in full or not at all rather than being cut off at a biome edge.

Cost grows with the cube of `radius`, and a low `rarity` multiplies it, so start at the defaults and raise the radius slowly.

### Fields

A `field` places nothing at a point and everything at once. Instead of picking a spot and building a shape around it, it asks a question of every block in the chunk, within `minHeight` and `maxHeight`, and places where the answer is at least `threshold`. The question is the same one hardness groups ask, so the two describe the same veins, and a pack can make a group and an entry that agree.

```json
{
  "block": "mypack:sulfur_ore",
  "replace": ["minecraft:stone"],
  "minHeight": 8,
  "maxHeight": 48,
  "shape": {
    "type": "field",
    "threshold": 0.6,
    "field": { "type": "speckle", "spread": 0.15 }
  }
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `threshold` | no | 0.0 to 1.0 | `0.5` | How strong the field has to be before a block is placed |
| `field` | yes | object | none | The same object a hardness group takes, with the same `speckle` and `seeded` types |

A low `threshold` takes most of the field and gives broad seams, a high one takes only the middle of each clump and gives small scattered pockets. With `speckle` you get many tiny specks, with `seeded` you get rounder patches or, once it has arms, knots with tendrils reaching between them.

Like a belt, a field ignores `attempts` and `spread`, since it is asked per chunk rather than per attempt, and it never writes into a neighboring chunk. It is worked out from the world seed and the entry's own name, so the same seed always gives the same veins, and two entries with different names never line up. `replace`, `adjacent`, `biomes` and the climate limits all apply as usual.

## Structure maps

A structure map composes templates into one named building on a grid, far past the 48 block limit of a single `.nbt` file. Each layer is drawn as rows of single characters, one character to a cell, and stacks one cell height above the layer before it. At most 8 layers of 8 by 8 cells, which at the default cell of 32 is 256 blocks a side.

`<namespace>/structuremaps/*.json`

```json
{
  "cell": 32,
  "ground": 0,
  "spacing": 64,
  "chance": 25,
  "layers": [
    {
      "palette": { "a": "mypack:keep_base", "b": ["mypack:wall=3", "mypack:wall_broken=1"] },
      "map": ["aba",
              "b.b",
              "aba"]
    },
    {
      "palette": { "a": "mypack:keep_top" },
      "map": [".a.",
              "...",
              ".a."]
    }
  ]
}
```

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `cell` | number | `32` | The grid pitch in blocks, up to 48. A template smaller than the cell sits at the cell's corner, so full-size pieces butt together seamlessly |
| `ground` | number | `0` | Which layer floors at the terrain surface. Layers before it dig down, which is how a building gets basements |
| `at` | two numbers | none | Pins one copy at exact block coordinates, the way `structureAt` pins a structure |
| `spacing` | number | `0` | Scatters copies on a grid this many chunks apart, jittered from the world seed. `0` scatters none, so a map with only `at` builds exactly once |
| `chance` | number | `100` | The percent of grid spots that build a copy |
| `dimensions` | list of dimension ids | all | Where the map may build |
| `layers` | list | none | The layers, bottom up, each a `palette` and a `map` |

A palette names templates by registry key from a pack's `<namespace>/structures/`.

| Value | What it does |
| --- | --- |
| `"a": "mypack:keep"` | Every `a` cell of that layer places this template |
| `"a": ["mypack:wall=3", "mypack:broken=1"]` | Each `a` cell rolls the list by weight, from the world seed and the cell's spot, so two copies of the building differ but the same world always builds the same one |
| `.` | An empty cell, nothing placed |

Every copy rolls one of the four facings from the world seed and the whole building turns together, templates included, so walls that meet across cells still meet; a map turns but never mirrors. The ground layer floors at the sampled terrain surface under the building's middle, and the whole map shares that one height. A scattered map is a structure of its own to the game, placed through a structure set written for you, so each chunk builds only its own slice of the grid and a building spanning many chunks arrives without cascading generation, whatever order the chunks load in. A [village plot](#village-plots) of type `template` may also name a map as its `structure`, which makes the composite a city building.

## City layout maps

A city map draws a city's street plan on a grid, one character to a cell, and the city is laid out from the drawing instead of rolling one. Streets, plazas and plots come out as the same pieces a rolled city uses, so every street option, bridge, tunnel, subway, sewer, lamp post and plaza centerpiece applies unchanged. The world template names the map in `villageLayout`.

`<namespace>/citymaps/*.json`

```json
{
  "cell": 48,
  "palette": {
    "#": "street",
    "+": "plaza",
    "a": "alley",
    "T": ["mypack:tower_blue=1", "mypack:tower_gray=1"],
    "B": "mypack:block",
    "s": ["mypack:shop_blue=2", "mypack:shop_gray=1"],
    "g": "grow"
  },
  "map": [
    "sss#BBB#sss",
    "sgs#BgB#sgs",
    "###+###+###",
    "BBB#TTT#BBB",
    "BgB#TgT#BgB",
    "###+###+###",
    "sss#BBB#sss"
  ]
}
```

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `cell` | number | `48` | The grid pitch in blocks, 8 to 128. Streets run down the middle of their cells at the pack's street width and plots are centered in theirs, so a cell wants the widest plot plus room to front the street |
| `palette` | object | none | What each character lays, listed below |
| `map` | list | none | The rows, up to 64 by 64 cells. A row shorter than the widest is open past its end |

| Value | What it does |
| --- | --- |
| `"#": "street"` | A run of street cells along a row or column becomes one street at the pack's width. Where a row run crosses a column run the junction is painted like any other. A lone street cell with no run in either axis is laid as a short stub along the row |
| `"+": "plaza"` | A plaza with its centerpiece. Runs pass through plaza cells, so streets meet at the plaza, and a plaza on a crossing stands its `villageWellStructure` centerpiece in the middle of the crossroads like a roundabout. The first plaza in the file is the city's own center, which pins the map to where the city founds; a map without one is centered there |
| `"a": "alley"` | A narrow run. Buildings front it, but it connects nothing, the alley rule as usual |
| `"T": "mypack:tower"` | A plot cell, laid from that plot definition, centered in the cell and facing the nearest street |
| `"T": ["mypack:a=3", "mypack:b=1"]` | The same, rolled by weight from the world seed and the cell's spot, so the same world always lays the same plot there |
| `"g": "grow"` | Left to the rolled layout, which fills such cells and spreads outward from the map |
| `.` or `open` | Open ground, nothing laid |

Every map rolls one of the four facings from the world seed and turns whole, so a plan reads the same from any side. Streets are laid first, so a plot that would overlap a street or another plot is left open with a line in the log, and a plot name no pack provides leaves its cell open the same way. The map does not change how the pieces dress: the street keys, `villageBlocks`, the lamps and the plaza centerpiece all read as they do for a rolled city.

## Spreads

A `spread` block with a `type`.

Every key, shown at once. A real file writes only the ones it needs. A key marked for one type is read only by that type.

```json
{
  "spread": {
    "type": "centered",
    "center": 32,
    "range": 12,
    "smoothness": 3,
    "veinHeight": 24,
    "veinDiameter": 12,
    "verticalDensity": 16,
    "horizontalDensity": 32,
    "offsetMin": 0,
    "offsetMax": 2,
    "ceiling": false
  }
}
```

| Type | Where it puts things |
| --- | --- |
| `even` | Anywhere between the heights, evenly. The default |
| `centered` | Weighted toward one height, thinning out with distance |
| `sprawl` | Fractal veins spanning a height range |
| `terrain` | Following the surface |
| `cavern` | On cave floors, or roofs |
| `submerged` | Under water or another fluid |

| Key | Used by | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `type` | all | one of the spreads above | `even` | Which spread |
| `center` | centered | int | midpoint of the height range | The height it clusters around |
| `range` | centered | int | half the height range | How far from that height it reaches |
| `smoothness` | centered | 1 to 8 | `2` | How many rolls are averaged. Higher is a tighter band |
| `veinHeight` | sprawl | int | the height range | How tall one vein is |
| `veinDiameter` | sprawl | int | `12` | How wide one vein is |
| `verticalDensity` | sprawl | 1 to 100 | `16` | How solid it is vertically |
| `horizontalDensity` | sprawl | 1 to 100 | `32` | How solid it is horizontally |
| `offsetMin` | terrain | int | `0` | Lowest offset from the surface |
| `offsetMax` | terrain | int | `offsetMin` | Highest offset from the surface |
| `ceiling` | cavern | boolean | `false` | Attach to the cave roof instead of the floor |

## Retrogen

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "retrogen": true,
    "adoptExistingChunks": false
  }
}
```

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `retrogen` | boolean | `false` | Catches chunks saved before an entry existed up on every worldgen entry marked `"retrogen": true`. Off, chunks that already exist are left alone. Chunks are marked as they generate either way, so turning this on later only touches chunks older than the pack |
| `adoptExistingChunks` | boolean | `false` | What happens the first time an old chunk is seen: on, it is stamped as though this pack had already generated it and is never caught up; off, it is caught up like any other. To fill an existing world, set `retrogen` on and this off |

An entry with `"retrogen": true` is generated into chunks that were saved before you added it. Each chunk records what it has had, so nothing is done twice.

The entry flag only marks an entry as eligible. Catching up is switched on by the `retrogen` setting, which a pack can set in its `settings` block or a player can set in the config, and it is off by default. Alongside it, `adoptExistingChunks` decides what happens the first time an old chunk is seen: on, the chunk is stamped as though this pack had already generated it and is never caught up; off, it is caught up like any other. Turning `retrogen` on while `adoptExistingChunks` is also on does nothing, because every old chunk is written off before it can be queued. To fill an existing world, set `retrogen` on and `adoptExistingChunks` off together. `retrogenChunksPerTick` in the config, default `2`, is how many old chunks are caught up each tick.

```json
{
  "block": "mypack:ruby_ore",
  "size": 8,
  "attempts": 12,
  "minHeight": 8,
  "maxHeight": 48,
  "retrogen": true,
  "retrogenKey": "ruby_v1"
}
```

Changing `retrogenKey` in the config makes every chunk eligible again, which adds the new veins on top of the old ones, so density doubles. That is deliberate, and it is why the key is manual.

---

## Pregeneration

Making a world's land ahead of time, so nobody generates chunks while playing: no chunk lag, a known size on disk, and one wait up front instead of a stuttering first hour.

The first 12 chunks around the spawn are always taken in hand, whatever a pack or the config says, because the game makes exactly that much itself before anyone joins. `pregenOnNewWorld` sets how much further to reach, and the command runs one by hand.

`/rdplserver pregen <radius>` makes every chunk within that many chunks of where it is run. `status` says how far along it is and `stop` ends it. The land is asked of the game's own chunk system, `pregenChunksInFlight` chunks at a time in a square spiral from the middle, and comes back lit and finished, so there is no lighting pass to run afterward.

While a run is going everybody is held: made a spectator, kept in place, shown a pulsing line mid-screen with the progress on the action bar, the day held still around them and far-off creatures frozen. The mode each player arrived in is written onto the player as they are held, so a save taken mid-run, a crash, or a rejoin never strands anyone as a spectator; the run's finish gives back exactly the mode it took, or the pack's `worldGameMode` when one is set. A client with the mod sees the view fogged while held and the logo fade in afterward; a vanilla client sees the plain hold. How far each dimension was made is saved in the world, so a finished world never runs again.

In a pack these go in a [world template's](#world-templates) `settings` block, like every other `chunks` key. Every one of them shown, with `pregenBorderLimit` the one absence since the config alone holds it:

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "pregenOnNewWorld": 63,
    "pregenDimensions": ["minecraft:overworld", "minecraft:the_nether"],
    "pregenAllDimensions": false,
    "pregenDimensionsWhenEntered": ["minecraft:the_end"],
    "pregenToBorder": false,
    "pregenResume": true,
    "pregenChunksInFlight": 32,
    "pregenRunningSays": "Building your world, %d%% done",
    "pregenFinishedSays": "Your world is ready",
    "pregenStoppedSays": "World building stopped",
    "pregenSpectatingSays": "Spectating until the world is ready",
    "pregenLogo": "center",
    "pregenBackup": true,
    "pregenBackupSays": "Pack requested world backup",
    "resetSays": "Pack requested map reset",
    "resetSendsTo": "spawn",
    "resetRuns": "",
    "resetClearsEntities": true,
    "resetClearsScores": true,
    "spawnChunkRadius": 2,
    "welcomeSays": ["Welcome to Ruby World!", "minecraft:the_nether=Welcome to the Nether!"],
    "saysCard": true,
    "saysIcon": "minecraft:compass",
    "saysColor": "1E2630",
    "saysImage": "rubyworld:textures/gui/card.png"
  }
}
```

| Key | What it does | Why you would set it |
| --- | --- | --- |
| `pregenOnNewWorld` | Radius in chunks made around the spawn before anybody plays. 12 is the floor and 0 means that floor rather than nothing, since the game makes 12 chunks around the spawn on its own anyway. Raise it to reach further than the game does | Sets how far a pack reaches past the ground the game already makes |
| `pregenDimensions` | Which dimensions are made, by id, in order, each around its own spawn | Add the nether, the end, or your own dimensions |
| `pregenAllDimensions` | Every dimension the server holds instead of a list, overworld first and the rest in id order | Packs with many dimensions. Every mod's dimensions count, so mind the size |
| `pregenDimensionsWhenEntered` | These are made the first time somebody sets foot in them, holding everyone again until done | Dimensions most players never visit; the ones who never go pay nothing |
| `pregenToBorder` | Fill each dimension out to its world border instead of a radius, centered on the border | Bounded worlds |
| `pregenBorderLimit` | How far a border may reach, in chunks either way, before the run is refused. Config only, never a pack key | A guard against a runaway run; raise it only knowing the time and disk it allows |
| `pregenResume` | A stopped or interrupted run picks up where it left off. The run's dimension, center and radius are written into the save when it starts, and the count so far every ten seconds, so a crash, a power cut or a quit mid-run all resume within about ten seconds of where they died on the next load. A run stopped on purpose, by command or by the stall watchdog, stays stopped | Long runs on servers; small runs restart cheaply without it |
| `pregenChunksInFlight` | How many chunks the run asks the game for at once. More keeps the generation threads busier and the server less responsive to whoever is held watching | Turn it up on an empty server, down on one people are playing on |
| `pregenRunningSays`, `pregenFinishedSays`, `pregenStoppedSays` | The messages for each stage. The first may hold `%d` for the percent and, after it, `%s` for the dimension's name, or `%1$d` and `%2$s` to put them in any order. Left at their defaults they speak each player's language | Reword them in your pack's voice, name the dimension when several are made, or silence them |
| `pregenSpectatingSays` | The mid-screen hold line while land is being made. Left at its default it speaks each player's language; empty shows nothing | Keep it under about thirty-five characters or small windows clip it |
| `pregenLogo` | Where the logo stands when pregeneration finishes: `left`, `center` or `right`, above the mid-screen text, shown for a few seconds and then fading out with the fog | It is always shown; an unknown word is read as `center` |
| `pregenBackup` | Copy the world to a pristine backup once pregeneration finishes, while the players are still held. Generation is then paid once: a later reset, or a new world on the same pack and seed, restores the copy instead of generating again, which is far faster than pregenerating twice. The copy is kept outside the save, at `rdpl-pristine/<world>` beside it, so another mod's backups do not sweep it up and it does not appear in a folder they manage | `false` |
| `pregenBackupSays` | The mid-screen line players are shown while that copy is made, with the percentage after it. Empty shows nothing and the copy is made quietly | `Pack requested world backup` |
| `resetSays` | The mid-screen line players are shown while `/rdplserver reset` or a round's end puts the map back. Empty resets quietly | `Pack requested map reset` |
| `resetSendsTo` | Where players are put by a reset: `spawn`, a position as `x,y,z`, or `dimension:x,y,z` to send them into another world, which is how a reset drops everyone in a lobby rather than back in the arena | `spawn` |
| `resetRuns` | A function run after a reset has cleared the map, named `namespace:path`. This is what builds the arena again, since a pack that made its map from a function can simply run it a second time. Empty runs nothing | empty |
| `resetClearsEntities` | Remove every entity that is not a player. Mobs, dropped items and experience all go, which is what leaves the map as it started | `true` |
| `resetClearsScores` | Set every objective the pack keeps back to nothing, so a new match starts from zero. Teams themselves are kept | `true` |
| `spawnChunkRadius` | How far from the spawn point, in chunks, chunks are held loaded whether or not a player is there. The default is `2`, which holds 25 chunks; `-1` leaves the game's own value. On 1.20.1 it sets the spawn ticket the server holds from the moment a world starts, in place of the game's own 11; on 1.21.1 it sets the `spawnChunkRadius` game rule when a world starts. Either way the same number of chunks is held on both | Hold a machine or a farm at spawn running, or turn the spawn chunks off with `0` |
| `welcomeSays` | The green greeting, shown on every login and after pregeneration. A bare entry is the line for everywhere; a `dimension=message` entry overrides it for that dimension and also greets every arrival there, e.g. `"minecraft:the_nether=Welcome to the Nether!"`. An empty message after the `=` mutes that dimension; an empty list shows nothing. Left at its default it speaks each player's language | One bare line names your pack; add dimension lines to theme each world. Keep lines under about thirty-five characters |
| `saysCard` | Shows the lines this mod says, the welcome, the pregeneration progress and the threat lines, as a card in the lower right corner instead of in chat. The card slides in, stays eight seconds and fades, and shows over an open screen too | Turn it on when chat is busy or the lines should read as part of the world rather than as chatter |
| `saysIcon` | An item drawn on the card, e.g. `minecraft:compass`. Empty draws none | Give the card your pack's emblem |
| `saysColor` | The card's background color as hex, e.g. `1E2630`. Empty uses a dark slate | Match your pack's palette |
| `saysImage` | A PNG from the pack's client assets, e.g. `rubyworld:textures/gui/card.png`, stretched over the card as its background and drawn over the color. Empty draws none | Give the card a painted panel; keep the image wide and short, it is stretched to whatever the text needs |

Run it yourself before shipping, at the radius being shipped, start to finish. Chunks grow with the square of the radius, 63 either way is sixteen thousand chunks, 500 is over a million, so your test world's region folder and wall clock are the honest numbers to put in front of players. Do not ship a radius that was never run.

# Control

## The control layer

Everything that stops or changes generation is grouped, and each group has one key in the config's `control` category with three values:

| Value | What it means |
| --- | --- |
| `default` | The pack decides. Config values are the fallback |
| `global` | The config wins. Pack sections are ignored |
| `off` | The group is disabled entirely and no pack can enable it |

The groups are `ores`, `biomes`, `structures`, `spawning`, `bedrock`, `voidWorld`, `recipes`, `terrain`, `replacements`, `villages`, `entities`, `chunks`, `blastPlaster` and `commands`.

Settings resolve **biome section → world template → config**. A world template's `settings` block uses the same key names as the config, so a pack sets them the same way you would:

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "monsterCap": 40,
    "flatBedrock": true,
    "worldGameMode": "creative",
    "oreWhitelist": ["minecraft", "mypack"],
    "pregenOnNewWorld": 63
  }
}
```

With a group's control at `default` these win, at `global` they are ignored, and at `off` the whole group does nothing no matter what any pack says. A key a template names that nothing reads is warned about once in the log, and so is a key in a `biomes` section that is not a village setting.

The config file is `config/resourcedatapackloader-common.toml`. Every key below carries the same name there, under its category, and a list is written as the TOML list the game's config format uses.
## What each group does

Every setting below is read through its group, so the group's `control` key decides whether a pack or the config has the last word. A setting a pack may set appears in a world template's `settings` block under the same name; one marked **config only** is read from the config alone, and a pack writing it is warned and ignored. Defaults are the config's.

### Ores

`control.ores` decides this group. Blocking ore generation by mod and by ore type.

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `blockOres` | boolean | `false` | Stop every mod, and Minecraft itself, from generating ores. Only the mods in oreWhitelist still generate. An ore is a placed feature with ore in its id, which is Minecraft's and most mods' |
| `logBlockedOres` | boolean | `true` | Log the first time each mod and ore type is blocked, so you can see what to whitelist |
| `oreWhitelist` | list | `["minecraft"]` | Mod ids allowed to generate ores while blockOres is on. Ores a pack defines belong to that pack's namespace |
| `prospectItems` | list | empty | Items that prospect for vein shaped worldgen entries when a sneaking player breaks a block with one, as item=entry\|entry[,radius in chunks] or item=*[,radius], e.g. minecraft:compass=iron_vein\|coal_seam or mypack:rod=*,12. The reading names the ore and a compass direction |
| `prospectItemsAreBlacklist` | boolean | `false` | On, the entries named after an item in prospectItems are the ones it does NOT read, and every other vein shaped entry is |
| `prospectDrops` | boolean | `false` | Whether a block broken in prospecting mode drops anything. Off, the sample is destroyed: no drops, no experience |
| `prospectSlow` | int, 1 to 100 | `2` | How many times slower a block breaks in prospecting mode |
| `prospectWear` | int, 2 to 1000 | `2` | How much durability a prospecting break costs the item, at least 2 |
| `oreTypes` | list | empty | Ore types this applies to, whoever generates them and whatever the whitelist says. Known types: COAL, IRON, COPPER, GOLD, REDSTONE, DIAMOND, LAPIS, EMERALD, QUARTZ, DIRT, GRAVEL, DIORITE, GRANITE, ANDESITE, TUFF, CLAY, SILVERFISH, CUSTOM for any other ore |
| `oreTypesAreBlacklist` | boolean | `true` | On, oreTypes are blocked. Off, only oreTypes generate |
| `blockOreDimensions` | list | empty | Dimensions ore blocking applies to, by id such as minecraft:the_nether; read as the overworld, nether and end biome tags. Empty means every dimension |
| `blockOreDimensionsAreBlacklist` | boolean | `false` | Treat blockOreDimensions as the dimensions to leave alone instead |

### Biomes

`control.biomes` decides this group. Blocking biomes by mod and by name, and what replaces them.

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `logBlockedBiomes` | boolean | `true` | Log a per mod count of which biomes were blocked, so you can see what to whitelist |
| `blockBiomes` | boolean | `false` | Stop every biome from generating except the mods in biomeWhitelist. Blocked biomes become the void biome, or what the world template's roles and fallback name. Blocking every biome makes the overworld a void world |
| `biomeWhitelist` | list | `["minecraft"]` | Mod ids whose biomes still generate while blockBiomes is on. A pack biome uses the pack's namespace |
| `biomeNames` | list | empty | Biomes this applies to, whoever owns them and whatever the whitelist says, by id such as minecraft:birch_forest |
| `biomeNamesAreBlacklist` | boolean | `true` | On, biomeNames are blocked. Off, only biomeNames generate |
| `blockBiomeDimensions` | list | `["minecraft:overworld"]` | Dimensions biome blocking applies to, by id. Empty means every dimension whose biomes are placed by climate, the overworld and the nether |
| `blockBiomeDimensionsAreBlacklist` | boolean | `false` | On, biome blocking skips these dimensions. Off, it applies only to them |

### Replacements

`control.replacements` decides this group. Block replacement in chunks that already exist.

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `blockReplacements` | list | empty | Blocks swapped out of chunks as they load, written as block=block with an optional state on either side, such as minecraft:andesite=minecraft:stone or minecraft:oak_log[axis=y]=minecraft:spruce_log[axis=y]. Every chunk is done once, new ones included |
| `blockReplacementDimensions` | list | empty | Dimensions block replacement applies to, by id. Empty means every dimension |
| `blockReplacementDimensionsAreBlacklist` | boolean | `false` | On, block replacement skips these dimensions. Off, it applies only to them |
| `blockReplacementMinHeight` | int, -2032 to 2031 | `-64` | Lowest y block replacement looks at |
| `blockReplacementMaxHeight` | int, -2032 to 2031 | `319` | Highest y block replacement looks at |
| `blockReplacementKey` | text | `0000` | Change this to make every chunk go through block replacement again |
| `logBlockReplacements` | boolean | `true` | Log the first time each replacement is made, and a total when a world catches up |

### Villages and cities

`control.villages` decides this group. The city and village streets a pack lays: their shape, dress, bridges, tunnels, rails, plots and plaza.

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `villagePathBlock` | text | empty | The block city roads are paved with when terrainAdaptation lays them. Empty paves them with dirt path |
| `villagePathExtraWidth` | int, 0 to 16 | `0` | Extra blocks of road width on each side beyond the usual 3, when terrainAdaptation lays the roads. Widens the streets themselves, so the blocks between them stand back from wide roads |
| `villageBlockSizes` | list | empty | How deep the blocks between a city's parallel streets are, one weighted entry per line written size=weight like 32=3, rolled once per district. Empty uses 32 |
| `villageCitySpacing` | int, 0 to 256 | `16` | How far apart city districts are seeded, in districts sized from the plots (twice the largest plot, plus a plaza and a street each side, rounded up to 16 blocks, at least 96): one district in every square of this many carries a city, placed at a spot fixed by the world seed, and at 1 every district is one, a plaza with the well at its center and streets out of it that join the next district's. 0 seeds none |
| `villagePathAlleyBlock` | text | empty | The block alleys are laid with. An alley is a street too narrow for lines and sidewalks. Empty lays alleys with the street block |
| `villagePathAlleyChance` | int, 0 to 100 | `0` | The percent chance a street is laid as an alley rather than at its full width. 0 lays no alleys |
| `villagePathMinimumWidth` | int, 0 to 32 | `0` | The narrowest street allowed. A street that would be laid narrower than this is not laid at all, and the district lays out around the gap. 0 never refuses |
| `villagePathFlatRun` | int, 0 to 64 | `6` | Streets hold each grade for at least this many blocks before stepping, anchored to world coordinates so segments agree across pieces. 0 or 1 lets a street step every block |
| `villagePlotsLeast` | int, 0 to 512 | `0` | The fewest built plots a district settles for. A district that lays out with fewer than this is not laid at all. 0 sets no floor |
| `villagePlotsMost` | int, 0 to 512 | `0` | The most built plots a district may have. Once a district reaches this many, no further plots are laid. 0 sets no ceiling |
| `villagePlotsBackRow` | boolean | `true` | On, a second pass seats a plot directly behind every plot that fronts a street, turned to face it, with the same roll and the same room test, so the inside of a block between two streets is built rather than left bare. Off leaves plots on the street fronts only |
| `villageLayout` | text | empty | A city map laid out instead of planning the district, named like mypack:downtown and read from that pack's citymaps folder. Empty plans the district as usual |
| `villagePathCenterBlock` | text | empty | The block of the center line down the middle of a city street. Empty draws no center line |
| `villagePathCenterDash` | int, 0 to 64 | `0` | Dashes the center line: N blocks of line, then one of street, anchored to world coordinates so segments continue each other. 0 keeps the line solid |
| `villagePathLineBlock` | text | empty | The block of the edge lines between street and sidewalk. Empty draws no edge lines |
| `villagePathSidewalkBlock` | text | empty | The block sidewalks are laid with, level with the street, outside the edge lines. Empty lays no sidewalks |
| `villagePathSidewalkWidth` | int, 0 to 16 | `2` | How many blocks wide each sidewalk is, when villagePathSidewalkBlock is set. A street too narrow to carry its lines and sidewalks is laid bare instead |
| `villagePathLampBlock` | text | `minecraft:oak_fence` | The block a lamp post along a street is built from, stacked villagePathLampHeight tall on the curb. Empty stands no lamp posts |
| `villagePathLampHeight` | int, 1 to 32 | `3` | How many blocks tall the lamp post stands before its head |
| `villagePathLampTopBlock` | text | `minecraft:red_wool` | The head that sits on top of a lamp post. Empty leaves the post bare |
| `villagePathLampSideBlock` | text | `minecraft:torch` | The light hung on each side of a lamp post head. Empty hangs none |
| `villagePathLampStructure` | text | empty | A structure file placed as the lamp instead of stacking the lamp blocks, named like mypack:street_lamp. Its lowest layer sits on the curb. Empty stacks the lamp blocks |
| `villagePathSupportBlock` | text | empty | The block laid one layer under the street surface. Empty lays none |
| `villagePathBridgeBlock` | text | empty | The block a street crosses water with. Empty decks a bridge with the street block |
| `villagePathBridgeSidewalkBlock` | text | empty | The block bridge sidewalks are decked with where a street crosses water. Empty keeps the normal sidewalk block on bridges |
| `villagePathBridgeBarrierBlock` | text | empty | The block bridge barriers are built from, stacked along both edges of the deck. Empty builds no barriers |
| `villagePathBridgeBarrierHeight` | int, 1 to 16 | `1` | How many blocks tall the bridge barriers stand |
| `villagePathBridgeDrop` | int, 0 to 64 | `0` | How far a street's grade must stand clear of the ground before the drop under it is bridged rather than filled solid. 0 keeps streets out of the air, bridging water only |
| `villagePathBridgeFrameBlock` | text | empty | The block an overhead frame over a long bridge is built from: a post up each side of the deck and a beam across the top. Empty builds none |
| `villagePathBridgeFrameTopBlock` | text | empty | The block the beam across the top of that frame is made of. Empty uses villagePathBridgeFrameBlock |
| `villagePathBridgeFrameHeight` | int, 1 to 32 | `4` | How many blocks of clear headroom the frame leaves over the deck. The beam lies one block above that |
| `villagePathBridgeFrameRun` | int, 1 to 256 | `24` | How many rows apart the frames stand when a bridge is long enough for several. They are spread symmetrically about the middle of the bridged run |
| `villagePathBridgeFrameLeast` | int, 1 to 256 | `24` | The shortest bridged run that gets a frame at all, in rows. A shorter bridge is left plain |
| `villagePathTunnelBlock` | text | empty | The block a street is lined with where it bores through a hill instead of cutting it open: the walls either side of the bore and the roof over it. Empty bores no tunnels and lets a street climb the hill |
| `villagePathTunnelDepth` | int, 1 to 128 | `10` | How much ground must stand over the street surface before a stretch is bored as a tunnel rather than climbed. A rise that deep anywhere along it is held level and bored through. Needs villagePathTunnelBlock |
| `villagePathTunnelLightBlock` | text | empty | The block set into a tunnel roof down its center line as a light. Empty lights none |
| `villagePathTunnelLightRun` | int, 1 to 64 | `8` | How many blocks apart the tunnel lights sit, anchored to world coordinates so pieces agree |
| `villageRailLines` | int, 0 to 16 | `0` | How many railway lines run through a district, laid before any street so the town grows around them. 0 lays none |
| `villageRailSpacing` | int, 1 to 256 | `48` | The fewest blocks of clear ground between one railway line's bed and the next of the same district. 1 lays them a block apart, which is how a pack builds a yard of parallel lines |
| `villageRailDirection` | text | `any` | Which way a district's railway lines run: ew for east to west, ns for north to south, any to roll it per district |
| `villageRailWidth` | int, 3 to 32 | `3` | The least the railbed is, in blocks. 3 carries one track down the middle and 5 carries two; a bed asked for more tracks than this fits widens to hold them, and villageRailShoulderWidth is added outside it |
| `villageRailBlock` | text | empty | The track block laid on the bed. Empty lays vanilla rails, which minecarts ride |
| `villageRailTrackSeat` | text | `auto` | Where the track sits: auto seats a rail block on the bed and sets any other block into the bed surface, on always lays it on the bed, in always sets it flush into the bed |
| `villageRailBedBlock` | text | empty | The bed the track lies on. Empty lays gravel |
| `villageRailTieBlock` | text | empty | The sleeper laid across the bed every villageRailTieRun rows. Empty lays oak planks |
| `villageRailTieRun` | int, 1 to 32 | `2` | How many rows apart the sleepers lie |
| `villageRailTracks` | int, 0 to 8 | `0` | How many tracks the one bed carries, side by side, villageRailTrackGap apart. The bed widens to hold them all. 0 lays one track on a bed under five wide and two on a wider one |
| `villageRailTrackGap` | int, 2 to 16 | `2` | How many blocks apart the tracks on a bed sit, center to center. 2, the least allowed, leaves one block of bed between them, which is what keeps them from curving into one another the way touching rails do |
| `villageRailShoulderBlock` | text | empty | The block dressing the outermost columns of the bed, a maintenance path beside the track, the railway counterpart of a street sidewalk. Empty lays none and leaves the bed its full width |
| `villageRailShoulderWidth` | int, 0 to 8 | `1` | How many columns wide that shoulder is on each side, added outside villageRailWidth. Needs villageRailShoulderBlock |
| `villageRailPowerBlock` | text | empty | The powered track set into the line every villageRailPowerRun rows. Empty uses a vanilla powered rail; a block that is not a rail is simply laid there |
| `villageRailPowerBase` | text | empty | What sits under a powered track to feed it. Empty uses a redstone block |
| `villageRailPowerRun` | int, 0 to 256 | `0` | How many rows apart a powered rail over its base is set into the track, so minecarts keep going. 0 powers none |
| `villageRailClimb` | int, 1 to 64 | `8` | How many rows a railway line runs level for each block it climbs or falls. 1 grades it as steep as a street |
| `villageRailTail` | int, 0 to 48 | `48` | How far a railway line runs on past the district at either end. Held to 48, which is as far as a structure start reaches |
| `villageRailSupportBlock` | text | empty | The post block under a trestle, where the line runs over water or a drop. Empty uses oak logs |
| `villageRailDeckBlock` | text | empty | The deck a trestle carries the bed on. Empty uses oak planks |
| `villageRailBarrierBlock` | text | empty | Barriers stood along both edges of a trestle deck. Empty stands none |
| `villageRailBridgeFrameBlock` | text | empty | The block an overhead frame over a long trestle is built from: a post up each side of the deck and a beam across the top. Empty builds none |
| `villageRailBridgeFrameTopBlock` | text | empty | The block the beam across the top of that frame is made of. Empty uses villageRailBridgeFrameBlock |
| `villageRailBridgeFrameHeight` | int, 1 to 32 | `4` | How many blocks of clear headroom the frame leaves over the deck. The beam lies one block above that |
| `villageRailBridgeFrameRun` | int, 1 to 256 | `24` | How many rows apart the frames stand when a trestle is long enough for several. They are spread symmetrically about the middle of the trestle |
| `villageRailBridgeFrameLeast` | int, 1 to 256 | `24` | The shortest trestle that gets a frame at all, in rows. A shorter trestle is left plain |
| `villageRailTunnelBlock` | text | empty | The block a railway line is lined with where it bores through a hill instead of climbing it. Empty bores no tunnels |
| `villageRailTunnelDepth` | int, 1 to 128 | `6` | How much ground must stand over the bed before a stretch is bored as a tunnel rather than climbed. Needs villageRailTunnelBlock |
| `villageRailTunnelLightBlock` | text | empty | The block set into a railway tunnel roof down its center line as a light. Empty lights none |
| `villageRailTunnelLightRun` | int, 1 to 64 | `8` | How many blocks apart those tunnel lights sit, anchored to world coordinates so pieces agree |
| `villageWellStructure` | list | empty | Structure files placed as the plaza centerpiece at the district's middle crossing, one weighted entry per line written name=weight like mypack:plaza_spire=3, rolled once per district. Its lowest layer sits on the plaza floor. Empty places none |
| `villagePathDeadEnds` | list | empty | How a street that dead ends is closed off, as structure names read from a pack, one per line, rolled per end. Empty closes each dead end with a cul-de-sac instead |
| `villagePathPiers` | list | empty | Pier styles for a street that dead ends over water: the bridged tail becomes a pier instead of a bridge to nowhere. The styles are railed, pilings and boardwalk; several entries roll one per pier. Empty leaves such a tail a plain bridge |
| `villagePathPierCargo` | list | empty | Cargo stood along the inside of a pier's rails, as block=weight entries, block=weight,height to stack it, or empty=weight for the share left clear. Every other row rolls the list on each side. Empty leaves piers bare |
| `villagePathPierLoot` | text | `resourcedatapackloader:chests/pier_cargo` | The loot table cargo blocks with an inventory are filled from, rolled the first time one is opened. A pack may replace the built-in table by shipping its own loot table at that name. Empty leaves them empty |
| `villagePathIntersects` | list | empty | Path intersect designs painted where streets cross, by registry key from a pack's pathintersects folder. One entry paints every crossing alike; several roll one per crossing, weighted by each design. Empty paints nothing |
| `villageDecor` | list | empty | Decoration scattered along city streets, as name=weight pairs naming worldgen from a pack, mypack:street_flowers=2. The name empty is the share of spots left bare. Every third block of verge on each side of a street rolls the list. Empty scatters nothing |
| `villageSubwayLines` | int, 0 to 32 | `0` | How many underground railway lines a city digs. 0 digs none and rolls nothing, so the city is laid exactly as it would be without them |
| `villageSubwayDepth` | int, 6 to 192 | `24` | How far under the surface a subway's bed sits. The line is graded from the ground above it, so it follows the land at that depth rather than running level |
| `villageSubwaySpacing` | int, 1 to 512 | `64` | How far apart a city's subway lines are kept from one another |
| `villageSubwayDirection` | text | `any` | Which way subway lines run: x, z, or any to roll per city |
| `villageSubwayWidth` | int, 3 to 33 | `3` | How wide the bed is, before shoulders |
| `villageSubwayBlock` | text | empty | The track block. Empty lays vanilla rail |
| `villageSubwayTrackSeat` | text | `auto` | Whether the track sits on the bed, in it, or auto to let the block decide |
| `villageSubwayBedBlock` | text | empty | The block the bed is made of. Empty uses gravel |
| `villageSubwayTieBlock` | text | empty | The block laid across the bed as sleepers. Empty uses planks |
| `villageSubwayTieRun` | int, 1 to 64 | `2` | How many blocks apart the sleepers sit |
| `villageSubwayTracks` | int, 0 to 16 | `0` | How many parallel tracks the bed carries. 0 takes as many as the width allows |
| `villageSubwayTrackGap` | int, 2 to 16 | `2` | How far apart parallel tracks sit |
| `villageSubwayShoulderBlock` | text | empty | The block either side of the bed. Empty leaves no shoulder |
| `villageSubwayShoulderWidth` | int, 0 to 16 | `1` | How wide that shoulder is |
| `villageSubwayPowerBlock` | text | empty | The powered track block. Empty uses vanilla powered rail |
| `villageSubwayPowerBase` | text | empty | The block set under a powered track to drive it. Empty uses a redstone block |
| `villageSubwayPowerRun` | int, 0 to 256 | `0` | How many blocks apart the powered tracks sit. 0 lays none |
| `villageSubwayTunnelBlock` | text | empty | The block the bore is lined with: the walls either side and the roof over it. Empty digs no subway at all, since a subway is a bore |
| `villageSubwayTunnelLightBlock` | text | empty | The block set into the tunnel roof as a light. Empty lights none |
| `villageSubwayTunnelLightRun` | int, 1 to 128 | `8` | How many blocks apart those lights sit, anchored to world coordinates so pieces agree |
| `villageSubwayClimb` | int, 1 to 128 | `8` | How many blocks a line runs before it may step one block up or down |
| `villageSubwayTail` | int, 0 to 256 | `48` | How far past the city's own pieces a line runs before it stops |
| `villageSubwayStationLength` | int, 0 to 128 | `0` | How many blocks long a subway station chamber is. 0 builds no stations at all |
| `villageSubwayStationRun` | int, 0 to 1024 | `0` | How many blocks apart further stations sit along a line, past the one nearest the plaza. 0 builds only the one at the plaza |
| `villageSubwayPlatformWidth` | int, 0 to 16 | `3` | How far the chamber is opened out either side of the bed to make a platform |
| `villageSubwayPlatformBlock` | text | empty | The block the platform is floored with. Empty floors it with the tunnel lining |
| `villageSubwayStairBlock` | text | empty | The block the steps up to the road side are made of. Empty uses the tunnel lining |
| `villageSubwayRailingBlock` | text | `minecraft:iron_bars` | The block railed around the head of a station's stairs where they open on the street, so nobody walks into the well. Empty leaves the head unrailed |
| `villageSubwayBenchBlock` | text | `minecraft:oak_stairs` | The seat of the benches set on a station's platform and beside its stair head. A stairs block reads as a bench; any block works. Empty leaves the benches out |
| `villageSubwayBenchEndBlock` | text | `minecraft:oak_log` | The arms at each end of a station bench. Empty leaves the seat bare at both ends |
| `villageSubwayBenchLength` | int, 0 to 32 | `5` | How long a station bench is, arms included. 0 leaves the benches out |
| `villageSubwaySurfaces` | int, 0 to 100 | `25` | The chance in a hundred that a subway line climbs to the surface at one end and carries on from there as an ordinary railway, tunnel behind it and open track ahead. The climb takes villageSubwayClimb rows per block, so a deep line spends a long run coming up. 0 keeps every subway buried for its whole length |
| `villageSubwayStation` | text | empty | A structure from a pack's structures folder used as the station itself, in place of the carved stairwell. Lift one out of a world built by hand with #scripts/rdpl-grab-template.py: its solid cells are laid and its air cells are carved, so the shape is the build and not a description of it. Empty carves the stairwell instead |
| `villageSubwayEntrance` | text | empty | A structure from a pack's structures folder set at the head of a station's stairs, so the way in is marked on the street. Empty leaves the stairs coming up bare |
| `villageSubwayStationFoot` | int, 0 to 64 | `4` | How many layers at the foot of a station build are laid once, before the part that repeats. The floor and the doorway out to the platform live here |
| `villageSubwayStationRepeat` | int, 0 to 64 | `12` | How many layers of a station build repeat, so one build serves any depth: the shaft grows by whole copies of this band and the corridor absorbs what is left over. It must be a whole turn of the stairs or the flights will not join. 0 never grows the build |
| `villageSewerBlock` | text | empty | The block a sewer is lined with under a city's streets and alleys: its floor, walls and roof. Empty digs no sewers |
| `villageSewerDepth` | int, 4 to 128 | `8` | How far under a street's own surface the sewer floor sits. The sewer follows the street, so a climbing street carries a climbing sewer. Needs villageSewerBlock |
| `villageSewerHeight` | int, 2 to 32 | `3` | How many blocks of headroom stand over the sewer walkway |
| `villageSewerWidth` | int, 3 to 33 | `5` | How wide a sewer runs, counted across including its two walls. Even numbers are rounded up so the channel keeps the middle |
| `villageSewerWaterBlock` | text | `minecraft:water` | The block filling the channel down the middle of a sewer. Empty leaves the channel dry |
| `villageSewerWalkBlock` | text | empty | The block the walkways either side of the channel are surfaced with. Empty walks on the lining block |
| `villageSewerLightBlock` | text | empty | The block set into a sewer roof over the channel as a light. Empty lights none |
| `villageSewerLightRun` | int, 1 to 128 | `8` | How many blocks apart the sewer lights sit, anchored to world coordinates so pieces agree |
| `villageSewerLadderBlock` | text | empty | The block a manhole shaft is climbed by, set down the shaft from the street to the sewer roof. Empty leaves the shaft open |
| `villageSewerCoverBlock` | text | empty | The block covering a manhole, set flush in an east-west street wherever a street or alley meets it, and on the plaza where that street crosses the sewer loop. Empty leaves the shaft mouth open |
| `villageSewerMossBlock` | text | empty | A second block mixed into the sewer lining here and there, mossy stone among plain for instance. Empty lines the sewer with one block throughout |
| `villageSewerMossChance` | int, 0 to 100 | `25` | The percentage of lining blocks that come out as villageSewerMossBlock. Rolled per block position from the world seed, so a repave lays the same pattern |
| `villageSewerVineBlock` | text | empty | A block hung on the inside of the sewer walls here and there, vines for instance. Empty hangs nothing |
| `villageSewerVineChance` | int, 0 to 100 | `20` | The percentage of wall-side cells that carry villageSewerVineBlock. Rolled per block position from the world seed, so a repave hangs the same pattern |
| `villageSewerWellEntrance` | boolean | `true` | On, a city with sewers gets a loop of sewer under the plaza around the well, the sewers of the streets meeting there running through it, and a manhole on the plaza down onto the loop on each side where an east-west street crosses it, so the sewers are one connected system with an entrance at the town center. Off, the street sewers cross under the well and the plaza has no way down of its own |

### Structures

`control.structures` decides this group. Vanilla structures switched off, their spacing, separation, spawn distance, biomes, spawns, pins and terrain adaptation.

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `structureSpacing` | list | empty | How far apart vanilla structures are seeded, in chunks, as structure=chunks entries: the 1.12.2 names temples, monuments, mansions, mineshafts, strongholds, netherbridges, endcities and villages, or any structure set id such as pillager_outposts. For mineshafts the number is one chunk in that many; for strongholds it is the ring distance |
| `structureSeparation` | list | empty | The closest two of a structure may be, in chunks, as structure=chunks entries; for strongholds it is the ring spread |
| `structureMost` | list | empty | The most of a structure a dimension may hold, as structure=count entries such as villages=100: once that many have been founded no chunk founds another, chunks pinned with structureAt aside. 0 or an absent entry sets no ceiling |
| `structureSpawners` | list | empty | What the mob spawner inside a vanilla structure spawns, as structure=namespace:entity entries, comma separated for a random pick. Only dungeons, mineshafts, netherbridges and strongholds build one; spawners other mods place are left alone |
| `structureMinDistanceFromSpawn` | list | empty | How far from the world spawn a structure starts, in blocks, as structure=blocks entries. Measured from the pack's worldSpawn when one is set, else from the world origin, since placement is decided before any spawn exists |
| `structureBiomes` | list | empty | Where a structure may generate, as structure=biome,biome entries naming biome ids or biome types such as SANDY |
| `structureBiomesAreBlacklist` | list | empty | Direction of the biome lists, written as structure=true or structure=false, one per line. True takes the listed biomes away, false makes them the only ones |
| `structureSpawns` | list | empty | The mobs a structure spawns whatever the biome says, as structure=namespace:entity:weight:least:most entries, comma separated; an empty list after the = spawns nothing |
| `structureAt` | list | empty | Structures pinned to exact spots, as structure=x,z entries in block coordinates, one per wanted instance. A pinned structure generates in that chunk and nowhere else |
| `structureAdaptation` | list | empty | How the terrain adapts to a structure, as structure=mode entries with the modes none, bury, beard_thin, beard_box and encapsulate |
| `terrainAdaptation` | boolean | `false` | Lay RDPL's own city streets, seated into the terrain instead of standing on stilts over every dip, and read the villagePath and villageRail options with them. Changes the terrain, so a world made with it on differs from one made without. This line lays no streets yet, so turning it on only says so |
| `villagePieces` | list | empty | Village plot definitions named here, one per line, as the full id of a villages file such as mypack:smithy. On 1.12.2 these were the vanilla piece names, which this line does not have |
| `villagePiecesAreBlacklist` | boolean | `true` | On, the plots in villagePieces are blocked. Off, only those plots are built |
| `villageBlocks` | list | empty | Blocks village plots are built from, as original=replacement pairs, minecraft:cobblestone=mypack:ruby_brick. A pair may carry a chance out of 100, minecraft:cobblestone=minecraft:mossy_cobblestone,20, weighed where the block is laid. Streets are never ruled. Empty leaves every block as the plot's own structure has it |

### Spawning

`control.spawning` decides this group. Mob spawn caps, hostile spawn rates and the light cap.

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `surfaceDayMonsterRate` | number, 0.0 to 4.0 | `1.0` | How often hostile mobs spawn on the surface during the day, where the sky can be seen. 0 stops them, 1 is vanilla, above 1 forces spawns vanilla would refuse |
| `surfaceNightMonsterRate` | number, 0.0 to 4.0 | `1.0` | The same for the surface at night |
| `undergroundDayMonsterRate` | number, 0.0 to 4.0 | `1.0` | The same for underground during the day, where the sky cannot be seen |
| `undergroundNightMonsterRate` | number, 0.0 to 4.0 | `1.0` | The same for underground at night |
| `monsterCap` | int, -1 to 1000 | `-1` | How many hostile mobs may be loaded at once across the world, before the count is scaled by how many chunks are loaded. Vanilla is 70. -1 leaves it alone |
| `creatureCap` | int, -1 to 1000 | `-1` | The same cap for passive animals. Vanilla is 10. -1 leaves it alone |
| `ambientCap` | int, -1 to 1000 | `-1` | The same cap for ambient mobs such as bats. Vanilla is 15. -1 leaves it alone |
| `waterCreatureCap` | int, -1 to 1000 | `-1` | The same cap for water mobs such as squid. Vanilla is 5. -1 leaves it alone |
| `monsterSpawnLight` | int, -1 to 15 | `-1` | The brightest block light a hostile mob may still spawn in, on top of the vanilla checks. -1 keeps the vanilla rule alone. Spawners are not affected |
| `threatItems` | list | empty | Items that raise a player's threat level, as item=level,count entries with an optional ,each or ,batch at the end, e.g. minecraft:diamond_sword=5,1 or minecraft:diamond=1,16,batch. Each, the default, adds the level for every one held, counting no more than count of them; batch adds the level once for every count held. A count above the item's stack size is cut to the stack size. Every loaded entity holding items is a carrier: a player's main inventory, armor and off hand, a dropped stack, anything with an item inventory such as a chest mule or a chest minecart, and the held items and armor of other mobs. Empty turns the threat level off |
| `threatLevels` | list | empty | Rising scores that open each threat band, e.g. 5, 15, 40 for three bands. A player below the first is in band 0. Empty turns the threat level off |
| `threatMost` | int, -1 to any | `-1` | The highest score a carrier can reach, -1 for no cap |
| `threatSpawnRate` | number, 0.0 to 100.0 | `1.0` | Multiplied into the hostile spawn rate near carriers in the top band, scaled down through the lower bands. 1.0 changes nothing, 2.0 doubles spawns at the top |
| `threatNotice` | number, 0.0 to 256.0 | `0.0` | How many blocks farther hostile mobs notice a carrier in the top band, scaled down through the lower bands. 0 changes nothing |
| `threatSays` | list | empty | Lines said to a player entering a band, as band=message entries |

### Bedrock

`control.bedrock` decides this group. Flat bedrock and its dimension and biome lists.

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `flatBedrock` | boolean | `false` | Replace the jagged bedrock at the bottom of the world with flat layers, through the generated preset, so it shapes new worlds made with it |
| `flatBedrockDimensions` | list | `["minecraft:overworld"]` | Dimensions to flatten bedrock in, by id such as minecraft:the_nether. Leave empty for every dimension |
| `flatBedrockDimensionsAreBlacklist` | boolean | `false` | On, flattening skips these dimensions. Off, it applies only to them |
| `bedrockLayers` | int, 1 to 5 | `1` | How many layers of bedrock to leave at the bottom |
| `flatBedrockBiomes` | list | empty | Biomes to flatten bedrock in, by id such as minecraft:birch_forest. Empty means every biome; elsewhere the bedrock stays as the game makes it |
| `flatBedrockBiomesAreBlacklist` | boolean | `false` | On, flattening skips these biomes. Off, it applies only to them |
| `flatBedrockRoof` | boolean | `false` | Flatten the bedrock ceiling too, where a dimension has one, such as the Nether roof |

### Slow ticking far away

`control.entities` decides this group. The slower pace of entities far from every player.

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `slowDistantEntities` | boolean | `true` | Tick entities far from every player less often. Nothing is ever left unticked, only ticked at a slower pace |
| `slowedKinds` | list | `["items", "experience"]` | Which kinds are given fewer ticks: items, experience, projectiles. Anything that thinks for itself is always given a slower pace instead, without being named here, and machines are never slowed |
| `slowDistance` | int, 64 to 4096 | `192` | How far from the nearest player, in blocks, before a chunk is slowed. The game stops telling a player about most entities beyond 64, so nothing below that |
| `slowRate` | int, 1 to 20 | `4` | One tick in this many is given to a slowed chunk. 1 is no slowing at all, 20 is once a second |
| `neverSlowed` | list | empty | Entities left alone however far away they are, as namespace:name |
| `slowRecheck` | int, 1 to 100 | `20` | How often, in ticks, the distance to the nearest player is worked out again. Every player counts for themselves, so someone alone far away still has their own quiet space around them |

### Land, holds and what the mod says

`control.chunks` decides this group. The welcome lines and the says card, and the rest of the chunks group as it is ported.

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `retrogen` | boolean | `false` | Catch existing chunks up on worldgen entries with \"retrogen\": true. Off, chunks that already exist are left alone. Chunks are marked as they generate either way, so turning this on later only touches chunks older than the pack |
| `adoptExistingChunks` | boolean | `false` | Treat chunks that already exist as if this pack generated them, marking them instead of leaving them for retrogen. Turn this on when replacing a mod that already generated the same ore, so retrogen never doubles it. Worldgen entries added later still retrogen into them |
| `saysCard` | boolean | `false` | Show the lines this mod says, the welcome and later the land-making progress and the threat lines, as a card in the lower right corner instead of in chat. The card slides in, stays eight seconds and fades, and shows over an open screen too |
| `saysIcon` | text | empty | An item drawn on the card, e.g. minecraft:compass. Empty draws none |
| `saysColor` | text | empty | The card's background color as hex, e.g. 1E2630. Empty uses a dark slate |
| `saysImage` | text | empty | A PNG from the pack's client assets stretched over the card as its background, e.g. rubyworld:textures/gui/card.png, drawn over the color. Empty draws none |
| `pregenOnNewWorld` | int, 0 to 8192 | `0` | How far around the spawn, in chunks, a world has its land made before anybody plays it. The game makes 12 chunks around the spawn on its own, so 12 is the floor and 0 means nothing beyond that. Raise it to reach further than the game does |
| `pregenToBorder` | boolean | `false` | Whether a new world has its land made out to its world border instead of a set number of chunks, centered on the border rather than the spawn. A world whose border was never moved in has no border to reach and is passed over |
| `pregenAllDimensions` | boolean | `false` | Make the land of every dimension the server holds, modded ones included, the overworld first and the rest in id order, instead of only those in pregenDimensions. Ones named in pregenDimensionsWhenEntered are still left for their first visitor |
| `pregenResume` | boolean | `false` | Whether a run that was stopped or cut short picks up where it left off next time the world is loaded, rather than starting again |
| `pregenChunksInFlight` | int, 1 to 512 | `32` | How many chunks a land-making run asks the game for at once. More keeps the generation threads busier and the server less responsive to whoever is held watching |
| `pregenBackup` | boolean | `false` | Copy the world to a pristine backup once pregeneration finishes, while the players are still held. The copy is what a reset would restore |
| `resetClearsEntities` | boolean | `true` | Remove every entity that is not a player when the map resets |
| `resetClearsScores` | boolean | `true` | Set every objective the pack keeps back to nothing when the map resets, so a new match starts from zero. Teams themselves are kept |
| `spawnChunkRadius` | int, -1 to 32 | `2` | How far from the spawn point, in chunks, chunks are held loaded whether or not a player is there. The default 2 holds 25 chunks, the number 1.21.1 itself uses, and makes a new world on 1.20.1 ready about five times sooner than the game's own 11 does. On 1.20.1 it sets the spawn ticket the server holds from the moment a world starts; on 1.21.1 it sets the spawnChunkRadius game rule when a world starts. -1 leaves each game its own, 441 chunks on 1.20.1 against 25 on 1.21.1 |
| `welcomeSays` | list | `[WELCOME]` | Welcome lines, shown in green on every login. A bare entry is the line for everywhere; a dimension=message entry overrides it for that dimension and also greets every arrival there, e.g. minecraft:the_nether=Welcome to the Nether!. An empty message after the = mutes that dimension; an empty list shows nothing. Left at this default it speaks each player's language |
| `pregenBorderLimit` | int, 1 to 1875000 | `8192` | The furthest a border may reach, in chunks either way, before making land out to it is refused. This is here to stop a mistake running for weeks, not to be turned up, and a pack cannot set it. A square of 8192 holds 268 million chunks **Config only.** |
| `pregenDimensions` | list | `["minecraft:overworld"]` | Which dimensions a new world has its land made in, by id, in the order given, one after another |
| `pregenDimensionsWhenEntered` | list | empty | Dimensions whose land is made not up front but the first time anybody sets foot in them, to the same reach, holding everybody the same way until it is done. One named here and in pregenDimensions is simply made up front |
| `pregenRunningSays` | text | `World pregeneration running, %d%% done` | The progress message players see while the world generates, where %d is the percentage and a second %s the dimension. Empty tells them nothing. Left at this default it speaks each player's language |
| `pregenFinishedSays` | text | `World pregeneration finished` | The message players see when generation finishes. Empty tells them nothing. Left at this default it speaks each player's language |
| `pregenStoppedSays` | text | `World pregeneration stopped` | The message players see when generation is stopped early. Empty tells them nothing. Left at this default it speaks each player's language |
| `pregenSpectatingSays` | text | `Spectating until the world is ready` | The mid-screen message players see while held in spectator during world generation. Empty shows nothing. Left at this default it speaks each player's language |
| `pregenLogo` | text | `center` | Where the logo stands when pregeneration finishes: left, center or right, above the mid-screen text. It is always shown; an unknown word is read as center |
| `pregenBackupSays` | text | `Pack requested world backup` | The mid-screen message players see while that backup is copied. Empty shows nothing |
| `resetSays` | text | `Pack requested map reset` | The mid-screen line players are shown while /rdpl reset puts the map back. Empty resets quietly |
| `resetSendsTo` | text | `spawn` | Where players are put by a reset: spawn, a position as x,y,z, or dimension:x,y,z to send them into another world |
| `resetRuns` | text | empty | A function run after a reset has cleared the map, named namespace:path. Empty runs nothing |

### Void world

`control.voidWorld` decides this group. Void world generation and its platform.

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `voidWorld` | boolean | `false` | Generate the listed dimensions as empty space with a platform at the spawn point and nothing living, through the generated preset |
| `voidWorldDimensions` | list | `["minecraft:overworld"]` | Which dimensions are made void, by id. Empty means the overworld alone |
| `voidWorldDimensionsAreBlacklist` | boolean | `false` | Treat voidWorldDimensions as the dimensions to leave alone instead |
| `voidPlatformBlock` | text | `minecraft:stone` | The block the void world platform is made of |
| `voidPlatformHeight` | int, -2032 to 2031 | `64` | The y the void world platform sits at |
| `voidPlatformSize` | int, 1 to 255 | `9` | How wide the void world platform is, in blocks. Rounded down to an odd number so it centers on the spawn point |
| `voidWorld` | text | `default` | Void world generation and its platform [default\|global\|off] |

### Terrain

`control.terrain` decides this group. The world's name, seed and game mode at creation, and the rest of the terrain group as it is ported.

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `worldSeed` | text | empty | The seed every new world is made with, whatever was typed when it was made, written the same way it would be typed. Empty leaves the choice alone |
| `worldGameMode` | text | empty | Which way every new world is started, one of survival, hardcore, creative, adventure or spectator. Hardcore is survival where death ends the world, save wide, the same as the choice on the world screen. Empty leaves it as whoever made the world chose |
| `worldName` | text | empty | What a new world is called when the screen for making one opens. Empty leaves it as the game names it |
| `worldType` | text | empty | The world type the shaped world is built on, one of default, largebiomes, amplified or flat; flat is a superflat overworld built from the generatorOptions layers, with the pack's cities on it. The shape below (heights, deep stone, sea level, bedrock, void) is generated as a world preset of its own, listed under World Type on the world screen and chosen there whatever was picked. Empty builds on default |
| `worldTypeExceptions` | list | `["flat", "debug_all_block_states"]` | World types a player picks that the generated preset leaves alone, such as flat or debug_all_block_states. Empty means every choice is replaced |
| `generatorOptions` | text | empty | The overworld's terrain settings as a JSON object, the keys the 1.12.2 customized world type wrote. Read here: seaLevel and useLavaOceans. With worldType flat it is the layers instead, bottom up, as the 1.12.2 superflat text 3;minecraft:bedrock,59*minecraft:stone,4*minecraft:dirt,minecraft:grass_block;1;village or a list of layers, and a village, mineshaft or stronghold named after the biome turns that vanilla structure on. Only applied to a world as it is created. Empty leaves the terrain as the world type makes it |
| `worldMinHeight` | int, -2032 to 2016 | `-64` | The lowest block of the overworld, a multiple of 16 down to -2032. The game's own bottom is -64; lower makes a deep world under the vanilla terrain, solid stone until the worldgen layer carves it or noiseCaves carries the game's caves down. Only applied through the generated preset |
| `worldMaxHeight` | int, -2016 to 2032 | `320` | The block above the overworld's top, a multiple of 16 up to 2032, at most 4064 above worldMinHeight. The game's own top is 320; higher leaves open sky above the vanilla terrain |
| `deepStone` | text | empty | The block the world below the vanilla terrain is made of when worldMinHeight goes under -64, such as a pack's own deepslate. It blends into deepslate across the eight layers under -64 the way deepslate blends into stone. Empty keeps stone |
| `noiseCaves` | text | `off` | Where the game's caves, tunnels, noodles and aquifers carry on when worldMinHeight goes under -64: off keeps the world under the vanilla terrain solid deep stone for the worldgen layer to carve, deep carries them down to the floor with the lava lakes moved to its bottom ten layers, world means the same on this version because the vanilla terrain has them already |
| `worldSpawn` | text | empty | Where every new world spawns, written as x,z or x,y,z. Without a y the ground at that spot is used. Only applied to a world as it is created. Empty leaves the choice to the game |
| `worldBorder` | int, 0 to 60000000 | `0` | How far across, in blocks, the world border stands in every new world. Only applied to a world as it is created. 0 leaves the border where the game puts it |
| `worldTime` | int, -1 to 23999 | `-1` | Lock the overworld's time of day, in ticks, the same figure /time set takes, so 18000 is midnight. The clock stops and never moves. -1 leaves time running |
| `worldDifficulty` | list | empty | Lock the difficulty, one of peaceful, easy, normal or hard, for the whole world. Difficulty is save wide on this version, so an entry written as dimension=difficulty is read for the overworld alone. Empty leaves it as chosen |
| `caveRegionPlainWeight` | int, 0 to 1000 | `4` | The weight of plain, region-less underground against the cave regions' own weights. Higher leaves more of the underground without any region |
| `caveRegionCells` | int, 16 to 4096 | `128` | How wide a cave region cell is in blocks. Cave regions from packs are painted over the underground in cells about this size |
| `caveRegionCellsY` | int, 16 to 4096 | `64` | How tall a cave region cell is in blocks |
| `worldGravity` | list | empty | Scale gravity, as a multiplier of vanilla where 1.0 is unchanged and 0.17 is moon-like, for players and mobs. A bare value covers every dimension, and an entry written as dimension=value covers that dimension alone and wins over the bare one. Empty leaves gravity alone |
| `worldFallDamage` | list | empty | Scale fall damage the same way, 0.5 halving it and 2.0 doubling it |
| `worldJumpStrength` | list | empty | Scale jump strength the same way, 1.5 jumping half again as high |
| `worldTerminalVelocity` | list | empty | Scale the fastest a mob or player falls the same way, 0.5 falling at half vanilla's top speed |
| `cloudHeight` | list | empty | The y clouds are drawn at, as dimension=y entries. A bare number covers every dimension. Empty keeps the game's own cloud height, 192 in the overworld |
| `worldBelow` | list | empty | Stack another dimension under this one: falling out of the bottom of the world carries you into the named dimension, arriving under its ceiling at the same x and z, still falling. Entries are written as dimension=target, such as minecraft:overworld=minecraft:the_nether to hang the nether under the overworld; a bare id covers every dimension. Digging through needs the floor's bedrock left out, which worldSeamBedrock decides. Empty means the floor stays the floor |
| `worldAbove` | list | empty | The same for the ceiling: rising past the top of the world carries you into the named dimension, arriving above its floor. Written the same way as worldBelow |
| `worldSeamEntities` | boolean | `true` | Whether dropped items, mobs and other entities ride the world seams too, or only players. Riders and mounts cross one at a time |
| `worldSeamBedrock` | boolean | `false` | Keep the bedrock at a seam boundary anyway. Off, a dimension whose floor or ceiling carries a worldBelow or worldAbove seam generates no bedrock there, so the way through can be dug. Already generated chunks keep whatever they have |

### Recipes

`control.recipes` decides this group. Recipe and furnace blocking and their whitelists.

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `blockRecipes` | boolean | `false` | Remove every crafting recipe, keeping only the mods in recipeWhitelist. Include your pack's namespace to keep its own recipes |
| `recipeWhitelist` | list | `["minecraft"]` | Mod ids whose crafting recipes survive while blockRecipes is on. Include your pack's namespace to keep its own recipes |
| `blockedRecipeMods` | list | empty | Mod ids whose crafting recipes are removed outright, whoever they belong to and whatever the whitelist says |
| `recipeMatch` | text | `recipe` | What the mod id is read from when blocking crafting recipes. 'recipe' uses the recipe's own name, 'output' uses the item it makes, 'both' blocks if either matches and spares if either is whitelisted |
| `blockFurnaceRecipes` | boolean | `false` | Remove every furnace, blast furnace, smoker and campfire recipe, keeping only the mods in furnaceWhitelist. The mod is read from the item produced |
| `furnaceWhitelist` | list | `["minecraft"]` | Mod ids whose furnace recipes survive while blockFurnaceRecipes is on. Include your pack's namespace to keep its own recipes |
| `blockedFurnaceMods` | list | empty | Mod ids whose furnace recipes are removed outright, whatever the whitelist says |
| `logBlockedRecipes` | boolean | `true` | Log a per mod count of what was blocked, so you can see what to whitelist |
| `furnace` | boolean | `true` | Apply furnace/*.json files, which add and remove furnace smelting recipes **Config only.** |
| `removals` | boolean | `true` | Apply recipe_removals/*.json files, which delete recipes by name, namespace or output **Config only.** |
| `skipMissingItems` | boolean | `true` | Skip recipes that use an item which is not registered, instead of letting them fail. The count is logged once **Config only.** |

### Commands

`control.commands` decides this group. Who may run the mod's own commands: the goto permission levels.

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `gotoLevel` | int, 0 to 4 | `3` | The permission level needed for /rdplserver goto <name>, which carries the sender to the nearest one. 3 is an operator, the level every other part of the command sits at. 2 also lets a command block run it, so a pack can put the jump on a button or a pressure plate without handing anybody the rest of the command. 0 lets any player type it. The other parts of /rdplserver stay at 3 whatever this says |
| `gotoNextLevel` | int, 0 to 4 | `3` | The permission level for /rdplserver goto <name> next, which passes over the one it last carried the sender to and finds another. Same scale as gotoLevel |
| `gotoBackLevel` | int, 0 to 4 | `3` | The permission level for /rdplserver goto <name> back, which returns the sender to the one before. Same scale as gotoLevel |
| `gotoPlaceLevels` | list | empty | Permission levels for single places, as name=level entries, one per line, overriding the three settings above for that place alone and in all three of its forms. The name is what you would type after goto, so a vanilla one such as Village or Mansion, or a name a pack registered for its own structures with locateAs. Same scale: 3 an operator, 2 also a command block, 0 anybody. A pack can then open the way to its own ruins while every vanilla structure stays shut, or the other way about. A name nothing has registered is ignored with a note in the log |

### Worldgen, config only

These `worldgen` keys belong to no group and are the config's alone.

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `worldgenDebug` | boolean | `false` | Write the debug lines other messages refer to into logs/rdpl.log, such as which pack served a file and what each command did. Very verbose |
| `worldTemplate` | text | `auto` | Which world template's settings apply. A pack adds one in worldtemplates/*.json and you name it here as namespace:name. 'auto' picks the template from the highest priority pack. Empty uses none |
| `tellWorldType` | boolean | `true` | Tell a player in chat, as they join a world made with the generated preset, which template shaped it. A pack cannot set this |
| `worldBorderLimit` | int, 1 to 60000000 | `60000000` | The widest border a pack is allowed to ask for through worldBorder. A pack asking for more is refused and the border is left where the game puts it. A pack cannot set this |
| `retrogenKey` | text | `0000` | Change this to make every chunk eligible for retrogen again, for every worldgen entry. New veins are added on top of what is already there |
| `retrogenChunksPerTick` | int, 1 to 64 | `2` | How many already generated chunks to catch up per tick. Higher is faster but stutters more |

### The `packs` category

How pack folders are found and served. Config only.

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `rootDirectory` | text | `rdploader` | Folder packs are loaded from, relative to the .minecraft directory. An absolute path also works. Requires a restart |
| `overrideResourcePacks` | boolean | `true` | Insert the asset pack above the player's selected resource packs and the world's own data packs. A pack named RDPLO... always overrides, RDPLN... never does |
| `warnOnCaseMismatch` | boolean | `true` | Warn when a file only matches because the filesystem is case-insensitive. Such packs break on Linux |
| `logContents` | boolean | `false` | Log every pack found and how many files it provides |
| `traceUnresolvedVariables` | boolean | `false` | Log a stack trace the first time a file with a '#' in its name is requested, naming whatever asked for it |

### The `content` category

Blocks, items, fluids and everything else packs define. Config only.

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `load` | boolean | `true` | Register the blocks, items, fluids, materials and creative tabs that packs define. Requires a restart |
| `vanillaClients` | boolean | `false` | Serve plain vanilla clients: nothing from any pack is registered, no blocks, items, fluids or creative tabs, so a client without the mod can join. Everything that lives on the server alone still applies. Requires a restart |
| `sounds` | boolean | `true` | Register the sound events named by sounds/*.json, so packs can ship their own audio |
| `fuels` | boolean | `true` | Apply fuels/*.json files, which give items a furnace burn time |
| `potions` | boolean | `true` | Register the potion effects and potion types described by potions/*.json and potion_types/*.json in packs. Requires a restart |
| `brewing` | boolean | `true` | Apply brewing/*.json files, which add brewing stand recipes |
| `villagers` | boolean | `true` | Register the villager professions described by villagers/*.json and apply the trades in trades/*.json. Requires a restart |
| `entities` | boolean | `true` | Register the entity variants described by entities/*.json in packs. Requires a restart |
| `overrides` | boolean | `true` | Apply overrides/<namespace>/<name>.json files, which change properties of blocks, items and potion types that already exist, vanilla or modded |
| `hardness` | boolean | `true` | Apply hardness/*.json files, which give a group of blocks a mining time and blast resistance multiplier, rolled per block position |
| `shovelPaths` | boolean | `true` | Let a shovel turn blocks marked behavesAs path into a path, and revert a path while sneaking |
| `shovelPathBecomes` | text | empty | What a shovel turns those blocks into. Empty uses the dirt path |
| `shovelPathReverts` | text | empty | What sneaking with a shovel turns a path back into. Empty uses dirt |
| `hoeTilling` | boolean | `true` | Let a hoe till blocks marked behavesAs till |
| `hoeTillsInto` | text | empty | What a hoe turns those blocks into. Empty uses farmland |
| `caneMaxHeight` | int, 1 to 255 | `3` | How tall vanilla sugar cane grows. Vanilla is 3. Pack defined cane blocks use their own growth section and ignore this |
| `cactusMaxHeight` | int, 1 to 255 | `3` | The same for vanilla cactus |

### The `data` category

Loot and registry names. Config only.

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `lootInjections` | boolean | `true` | Apply loot_injections/*.json files, which add pools to loot tables that already exist instead of replacing the whole table |
| `playerLoot` | boolean | `true` | Apply player_loot/*.json files, which roll a loot table when a player dies and drop what it makes, on top of or instead of the inventory |
| `registryRemaps` | boolean | `true` | Apply registry_remap files, which rename a registry entry so worlds saved before the rename keep their blocks and items instead of losing them |

### The `tweaks` category

Small changes to how vanilla behaves. Config only; see [Bonus: vanilla tweaks](#bonus-vanilla-tweaks).

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `promptLeafDecay` | boolean | `true` | Leaves that lose their tree decay within a second instead of waiting on random ticks |
| `lenientPaths` | boolean | `true` | Paths and tilled ground can be made under a block and stay there when one is placed above |
| `unbreakableSpawners` | boolean | `false` | Mob spawners cannot be mined or blown up. Creative mode still removes them. Requires a restart |
| `experimentalWarning` | boolean | `false` | Show the game's experimental settings warning when a world is made or opened. Off answers it as if you had clicked proceed |
| `privacy` | boolean | `true` | Turn off the game's telemetry and chat reporting: no telemetry event is sent or logged, the client signs no chat message, the server keeps no chat session and does not require one, so no message anybody sends can be reported. A pack cannot set this. Takes effect on the next world or server joined |
| `darkSplash` | boolean | `true` | Draw the loading screen dark with the pack loader's logo in place of the game's: the logo is swapped as the screen is made, and the game's own Monochrome Logo option is turned on when it is still off, which takes effect at the next start. Off leaves the option as it is |

## Blast Plaster integration

`<namespace>/blastplaster/*.json`

The file name is yours to choose, only the folder is read, and several files stack.

Blast Plaster handles post-explosion behavior: healing craters block by block, tree-aware felling, drop control. On its own it reads one global config. Driven from a pack it answers **per dimension**, and the pack ships the decision instead of asking players to edit a config. Without pack files, or without Blast Plaster installed, nothing here does anything, and the folder is skipped with a line in the log.

Keys written at the top of the file apply everywhere; a `dimensions` block overrides them for one dimension by id. Anything a pack never names keeps whatever Blast Plaster's own config says, so a pack sets the handful it cares about and leaves the rest alone.

Every key, shown at once. A real file writes only the ones it needs.

```json
{
  "explosionMode": "EJECT_DROPS",
  "healCreepers": true,
  "healNonPlayerTNT": true,
  "healWither": true,
  "healAll": false,
  "processPlayerIgnitedTNT": false,
  "customEntitiesToHeal": ["icbmclassic:missile"],
  "healFullTrees": true,
  "maxTreeSize": 400,
  "minimumTicksBeforeHeal": 200,
  "randomTickVar": 20,
  "overrideBlocks": false,
  "enableFakeTossedBlocks": true,
  "enableExplosionFlash": true,
  "explosionFlashDuration": 10,
  "explosionFlashLightLevel": 15,
  "explosionFlashParticleCount": 40,
  "explosionFlashPulses": 2,
  "enableExplosionSmoke": true,
  "explosionSmokeDuration": 100,
  "explosionSmokeParticleCount": 30,
  "playerTNTAlwaysDrops": false,
  "playerTNTDropFullBlocks": false,
  "enableDropSuppression": true,
  "dtSpecialDrops": true,
  "preventMobDrops": false,
  "dimensions": {
    "minecraft:the_nether": { "explosionMode": "HEAL", "minimumTicksBeforeHeal": 200 },
    "minecraft:the_end": { "enableExplosionSmoke": false }
  }
}
```

`explosionMode` is the primary switch: `HEAL` restores the crater over time, `EJECT_DROPS` leaves the hole and drops roughly a third of the blocks (vanilla behavior), `VISUAL_TOSS` leaves the hole and drops nothing. When driven by a pack the default is `EJECT_DROPS` (not Blast Plaster's `HEAL`), so an unconfigured install behaves like vanilla.

| Key | Value | What it does |
| --- | --- | --- |
| `explosionMode` | `HEAL`, `EJECT_DROPS`, `VISUAL_TOSS` | What happens after the bang |
| `healCreepers`, `healNonPlayerTNT`, `healWither`, `healAll` | true or false | Which explosions are handled at all |
| `processPlayerIgnitedTNT` | true or false | Whether TNT a player lit is handled with the rest |
| `customEntitiesToHeal` | list of entity names | Explosions from other mods, named as `modid:entity` |
| `healFullTrees` | true or false | A tree clipped by a blast is taken or restored whole, rather than sheared through |
| `maxTreeSize` | number | The most blocks one tree may claim before it is left alone |
| `minimumTicksBeforeHeal`, `randomTickVar` | numbers | How long before mending starts, and how ragged its pace is |
| `overrideBlocks` | true or false | Whether mending overwrites what has since been built in the hole |
| `enableFakeTossedBlocks` | true or false | The debris that flies out of the blast |
| `enableExplosionFlash` | true or false | The bright flash at the moment of the blast |
| `explosionFlashDuration`, `explosionFlashLightLevel`, `explosionFlashParticleCount`, `explosionFlashPulses` | numbers | How long the flash lasts, how bright it burns, how many particles it throws and how many times it pulses |
| `enableExplosionSmoke` | true or false | The column of smoke afterwards |
| `explosionSmokeDuration`, `explosionSmokeParticleCount` | numbers | How long the smoke lingers and how thick it stands |
| `playerTNTAlwaysDrops`, `playerTNTDropFullBlocks` | true or false | What a player's own TNT leaves behind |
| `enableDropSuppression`, `dtSpecialDrops` | true or false | Drops inside a blast, and Dynamic Trees' own drops |
| `preventMobDrops` | true or false | Whether mobs killed by a blast still drop |

**Fully vanilla appearance:** `EJECT_DROPS` plus `healFullTrees`, `enableFakeTossedBlocks`, `enableExplosionFlash`, `enableExplosionSmoke`, `preventMobDrops` and `playerTNTAlwaysDrops` all off. Each key is per-dimension-capable.

**Vanilla clients** see nothing unusual. The flash is the only feature that places a block, so with `vanillaClients` set it is forced off; everything else is particles and items a plain client understands.

Not pack keys: Blast Plaster's debug logging and its log-to-leaves pairing (tree identification must be one answer game-wide). Both stay in Blast Plaster's own config.

---

# Reference

## Value lists

These are the names the parser accepts wherever the tables above say "one of the materials", and so on. Anything unrecognized is logged and replaced with the default.

**Block materials.** `air`, `grass`, `ground`, `wood`, `rock`, `iron`, `anvil`, `water`, `lava`, `leaves`, `plants`, `vine`, `sponge`, `cloth`, `fire`, `sand`, `circuits`, `carpet`, `glass`, `redstone_light`, `tnt`, `coral`, `ice`, `packed_ice`, `snow`, `crafted_snow`, `cactus`, `clay`, `gourd`, `dragon_egg`, `portal`, `cake`, `web`, `piston`, `barrier`, `structure_void`. The game itself no longer has materials; each name stands for the map color, sound, tool and piston behavior that material had.

**Sound types.** `wood`, `ground`, `plant`, `stone`, `metal`, `glass`, `cloth`, `sand`, `snow`, `ladder`, `anvil`, `slime`.

**Map colors.** `air`, `grass`, `sand`, `cloth`, `tnt`, `ice`, `iron`, `foliage`, `snow`, `clay`, `dirt`, `stone`, `water`, `wood`, `quartz`, `adobe`, `magenta`, `light_blue`, `yellow`, `lime`, `pink`, `gray`, `silver`, `cyan`, `purple`, `blue`, `brown`, `green`, `red`, `black`, `gold`, `diamond`, `lapis`, `emerald`, `obsidian`, `netherrack`.

**Render layers.** `solid`, `cutout`, `cutout_mipped`, `translucent`. Left empty, the block picks one to suit its type.

**Rarities.** `common`, `uncommon`, `rare`, `epic`.

**Torch particles.** `none`, `flame`, `colored`. `colored` uses `particleColor`.

**Tool classes.** `pickaxe`, `axe`, `shovel`, `hoe`, `sword`.

**Armor slots.** `head` or `helmet`, `chest` or `chestplate`, `legs` or `leggings`, `feet` or `boots`.

**Tints.** `biome`, `none`, or a six digit hex color. Colors anywhere in a definition are hex, with or without a leading `#`.

**Behaviors** for `behavesAs`. `till`, `path`.

**Plant types** for `plantTypes`. `plains`, `desert`, `beach`, `cave`, `water`, `nether`, `crop`. 1.20.1 only; 1.21.1 reads the key and ignores it.

**Biome types**, the words that stand for a biome tag wherever a table says "list of biome types", in `biomeTypes`, a biome's `types`, a template's `roles` and a `biomes` section: `ocean`, `deepocean`, `beach`, `river`, `mountain`, `mesa`, `hills`, `coniferous`, `jungle`, `forest`, `savanna`, `overworld`, `nether`, `end`, `hot`, `cold`, `sparse`, `dense`, `wet`, `dry`, `spooky`, `dead`, `lush`, `mushroom`, `magical`, `rare`, `plateau`, `modified`, `water`, `desert`, `plains`, `swamp`, `sandy`, `snowy`, `wasteland`, `void`. The vanilla words map onto `minecraft:is_*` tags and the rest onto the convention tags, `forge:is_*` on 1.20.1 and `c:is_*` on 1.21.1. A tag written out, `minecraft:is_forest` or `#minecraft:is_forest`, is taken as it is. Case does not matter, so the 1.12.2 `FOREST` still reads.

**Roles** for a world template's `roles`. Any biome type word above: each names a biome that fills the biomes carrying that tag once blocking has removed them, so `"ocean": "mypack:ruby_ocean"` puts the ruby ocean wherever an ocean was blocked.

**Structures** for a world template's `structures` and for the `structures` group's own lists: the 1.12.2 names `villages`, `mineshafts`, `strongholds`, `temples`, `monuments`, `mansions`, `netherbridges` and `endcities`, or any structure set the game or a mod ships, such as `pillager_outposts`, `ancient_cities`, `trail_ruins`, `shipwrecks`, `ocean_ruins`, `ruined_portals`, `nether_fossils`, `buried_treasures`, `desert_pyramids`, `jungle_temples`, `igloos`, `swamp_huts`, `woodland_mansions`, `ocean_monuments`, `nether_complexes`, `end_cities`. A 1.12.2 name is read as the sets it stood for, so `temples` is the pyramids, the jungle temples, the igloos and the swamp huts together.

**Creature types** for biome spawns and rates. `creature`, `monster`, `ambient`, `water`.

**Ore types** for `oreTypes`. `COAL`, `IRON`, `COPPER`, `GOLD`, `REDSTONE`, `DIAMOND`, `LAPIS`, `EMERALD`, `QUARTZ`, `DIRT`, `GRAVEL`, `DIORITE`, `GRANITE`, `ANDESITE`, `TUFF`, `CLAY`, `SILVERFISH`, `CUSTOM` for any other ore.

**Damage types** for an entity variant's `immuneTo`. The game's own damage type names: `fall`, `drown`, `explosion`, `magic`, `cactus`, `lava`, `wither`, `starve`, `in_wall`, `freeze`, `lightning_bolt`, `fire`, `dragon_breath` and the rest of `minecraft:damage_type/`.

## Folder list

Every folder, with its full path and a link to the section that describes it, is in [Where files go](#where-files-go).

## Commands

`/rdpl` runs on your own machine and needs no permissions, because everything it touches is yours. A reload rescans the folder you own and refreshes your own resources; it reaches no server, so the server's copy is reloaded with `/rdplserver reload` instead. In single player the two are one machine, so `/rdpl reload` is also what re-applies your [property overrides](#property-overrides) and re-fields your teams.

| Command | Level | What it does |
| --- | --- | --- |
| `/rdpl list` | none | Every loaded pack, its priority, and what it contains. Click a pack to look up a file in it |
| `/rdpl which <namespace:path>` | none | Which pack provides a given file, and which packs it shadows |
| `/rdpl reload` | none | Rescan the folder and reload everything, the game's own resource reload included |
| `/rdpl unused` | none | Files in your packs that nothing has asked for yet, usually a typo in a path |
| `/rdpl config unused` | none | Option files in `rdploader/config` that no installed pack defines any more |
| `/rdpl config prune` | none | Delete those files |
| `/rdpl pixelmap <namespace:path>` | none | What a [pixel map](#textures-written-as-pixel-maps) came out as, character by character |
| `/rdpl biome list [all]` | none | Every biome that can generate, and its id; `all` includes the ones nothing can generate |
| `/rdpl biome here` | none | The biome you are standing in |
| `/rdpl locate <name>`, `goto <name>`, `vein <entry> [radius]` | the server's | Linked. Passed word for word to `/rdplserver`, which decides, so see the table below |

**Which server subcommands are linked, and why the rest are not.** `locate`, `goto` and `vein` can only ever mean the server's, since only the server knows the world, so `/rdpl` hands them over and offers their places in tab completion. The rest, `reload`, `list`, `which`, `unused`, `config`, `pixelmap` and `biome`, keep their own meaning of your packs and your client. The server's own permission check decides a linked command, so a client can neither cheat it nor be told a fabricated answer.

On a dedicated server, `/rdplserver` does the same for the server's own copy of the folder. The Level column is the permission level a sender needs: `3` is an operator, `2` also admits command blocks, `0` is any player, and `4` is above operator and reaches nobody.

| Command | Level | What it does |
| --- | --- | --- |
| `/rdplserver reload` | 3 | Rescan the server's folder and reload everything, then field the teams and objectives again |
| `/rdplserver list` | 3 | Every pack the server loaded, its priority, and what it contains |
| `/rdplserver which <namespace:path>` | 3 | Which pack provides a given file, and which packs it shadows |
| `/rdplserver unused` | 3 | Files in the server's packs that nothing has asked for |
| `/rdplserver config unused` | 3 | Option files in `rdploader/config` that no installed pack defines any more |
| `/rdplserver config prune` | 3 | Delete those files |
| `/rdplserver pixelmap <namespace:path>` | 3 | What a pixel map came out as |
| `/rdplserver oregen` | 3 | Running totals of ore generation that was blocked, per mod and type |
| `/rdplserver biome list [all]` | 3 | Every biome that can generate on the server, with its id; `all` includes the ones nothing can generate |
| `/rdplserver biome here` | 3 | The biome you are standing in |
| `/rdplserver dimensions` | 3 | Every dimension, including the ones packs added |
| `/rdplserver gate` | 3 | Every gate, its dimension, its scope and whether it is open |
| `/rdplserver gate check <player>` | 3 | Which gates a player has passed |
| `/rdplserver gate grant <player> <gate>` | 3 | Open a gate for a player |
| `/rdplserver gate revoke <player> <gate>` | 3 | Close one again |
| `/rdplserver pregen <radius>` | 3 | Make every chunk within that many chunks of where it is run. See [Pregeneration](#pregeneration) |
| `/rdplserver pregen status` | 3 | How far along a run is |
| `/rdplserver pregen stop` | 3 | End it |
| `/rdplserver vein <entry> [radius]` | 3 | Where a `vein` shaped worldgen entry has its veins seeded within that many chunks (default 8) of where it is run, nearest first, whether or not those chunks exist yet |
| `/rdplserver locate <name>` | 3 | The nearest structure a pack placed under that `locateAs` name |
| `/rdplserver intro` | 3 | Let the world intro play again on your next join. It only ever clears your own |
| `/rdplserver team`, `team join [name]`, `team leave`, `team vote <player>`, `team claim` | 3 | The sides a pack has fielded and the ways onto and off them, see [Teams](#teams) |
| `/rdplserver reset` | 3 | Put the map back the way a round's end does: everybody is held, the entities swept, the scores wiped, `resetRuns` run, the players put at `resetSendsTo` and released, and a round opens with the starting count, as the reset settings under [Pregeneration](#pregeneration) describe |
| `/rdplserver goto <structure>` | `gotoLevel`, `3` | Take you to the nearest one nobody has been to yet, looking without generating the land on the way |
| `/rdplserver goto <structure> next` | `gotoNextLevel`, `3` | Take you onward to the closest one you have not been taken to this session, whether or not it has been visited before |
| `/rdplserver goto <structure> back` | `gotoBackLevel`, `3` | Take you to the one before it, stepping back through where this session has sent you |

**Opening `goto` up.** Every part of `/rdplserver` needs an operator, level 3, as the command's root does. The three `goto` forms are the one thing a pack decides: each carries a permission level of its own that a pack or the config may lower, separately from the other two and from the rest of the command. A pack that wants `intro`, `team` or `reset` reachable by players puts them on a command block or in a function, which runs at level 2 with `goto` and at level 3 for the rest.

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "gotoLevel": 3,
    "gotoNextLevel": 2,
    "gotoBackLevel": 3,
    "gotoPlaceLevels": ["Crypt=2", "Waystone=0", "Mansion=4"]
  }
}
```

| Setting | What it governs |
| --- | --- |
| `gotoLevel` | `goto <structure>` |
| `gotoNextLevel` | `goto <structure> next` |
| `gotoBackLevel` | `goto <structure> back` |
| `gotoPlaceLevels` | One named place, in all three forms |

The value is the permission level a sender needs. `3` (operator) is the default. `2` also admits command blocks, so a pack can put a jump on a button or pressure plate without exposing the rest of `/rdplserver`. `0` opens it to any player. The three settings are independent: for example, `next` open to command blocks for a city tour while `back` stays operator-only.

`gotoPlaceLevels` overrides the three settings for single places, as `name=level` entries, as in the example above. The name is whatever you would type after `goto`: a vanilla one such as `village` or `mansion`, or a name registered with `locateAs` on an imprint entry. Matching ignores case. A level of `4` is above operator and closes that place to everyone, the way to hide one place while the rest of `goto` is open.

An entry sets one level for all three forms of that place. An unlisted place falls back to the three settings above, and an unregistered name never matches. Tab completion follows the same rules, so after `goto` a sender is offered only the places they may actually be carried to.

These sit in the `commands` group, so `control.commands` in the config decides whether a pack may set them at all, and `off` there keeps everything at operator whatever a pack asks for.

**Day-to-day editing:** F3+T reloads textures, models and language files, and `/reload` the server's data. Use `/rdpl reload` when you *add* or *delete* a file, since that changes what the folder contains.

## Good to know

- KubeJS and CraftTweaker run after RDPL, so their changes still win.
- Recipes, loot tables, advancements and functions are the game's own data files here, so `/reload` picks up an edit and `/rdpl reload` a new file.
- A structure that has already generated stays loaded until you leave the world.
- Filename case matters. If your file's capitalization doesn't match what the game asked for, RDPL still loads it but warns you, because on Linux it wouldn't be found at all.
- Put a `pack.png` in `rdploader` to give the pack an icon.
- The folder can be moved or renamed with the `rootDirectory` option in `config/resourcedatapackloader-common.toml`. An absolute path works too, and it needs a restart.
- A model naming a finished vanilla model inherits vanilla's textures too. Parent models such as `cube_all` and `cross` take their textures from the model that names them and are fine.
- The game's telemetry and chat reporting are off while `privacy` in the `tweaks` category is on, which it is by default: nothing is sent, no chat message is signed, and a server running the mod keeps no chat session for anyone.
- A changed pack option is remembered by the world it changes, and the world is backed up to the game's `backups` folder before it is opened again.

## When something doesn't work

**Check `logs/rdpl.log` first.** Everything RDPL does goes there rather than the main log. Advancements, loot tables, recipes, functions, structures and every piece of content are logged with the pack they came from, and anything malformed is logged with the reason.

**Textures and other assets are different.** They're requested far too often to log individually, so instead `/rdpl unused` lists the files in your packs that nothing has asked for. Run it once the game has finished loading. A file with the right path is always requested, so anything listed is usually a typo, but bear in mind some files only load when they're needed, such as languages other than the one you play in.

**A zip without an `assets` or `data` directory inside it is skipped,** and so is any folder in `rdploader`, and the log says so. A zip whose top level is one folder wrapping them is skipped the same way.

**`/rdpl which minecraft:textures/block/stone.png`** tells you exactly which pack is serving a file and what it's shadowing.

**A pack written for 1.12.2 is read through the forward port.** See [Packs written for 1.12.2](#packs-written-for-1122); the log names every file it moved, left out or could not carry.

## Bonus: vanilla tweaks

Small changes to how vanilla behaves, each switched in the `tweaks` config category.

| Option | Default | What it does |
| --- | --- | --- |
| `promptLeafDecay` | on | Leaves that lose their tree decay within a second instead of waiting on random ticks |
| `lenientPaths` | on | Paths and tilled ground can be made under a block and stay there when one is placed above |
| `unbreakableSpawners` | off | Mob spawners cannot be mined or blown up. Creative mode still removes them. Needs a restart |
| `experimentalWarning` | off | Show the game's experimental settings warning when a world is made or opened. Off answers it as if you had clicked proceed |
| `privacy` | on | Turns off the game's telemetry and chat reporting; see [Good to know](#good-to-know) |
| `darkSplash` | on | Dark loading screen with the pack loader's logo; the game's Monochrome Logo option is turned on for the next start |

Four more sit in the `content` category rather than `tweaks`:

| Option | Default | What it does |
| --- | --- | --- |
| `cactusMaxHeight` | `3` | How tall vanilla cactus grows |
| `caneMaxHeight` | `3` | How tall vanilla sugar cane grows |
| `shovelPaths` | on | A shovel turns blocks marked `behavesAs` path into a path, and sneaking reverts one |
| `hoeTilling` | on | A hoe tills blocks marked `behavesAs` till |

**None of this reaches a pack.** These options only change Minecraft's own cactus, cane, leaves and paths. A block your pack defines with `"type": "cane"` carries its own `growth` section and grows to whatever height you gave it, whatever else is installed.

### Unbreakable spawners

`unbreakableSpawners` gives the mob spawner block bedrock's numbers, an unbreakable hardness and an explosion resistance nothing survives. A player cannot mine one however good the pickaxe, and neither creepers, TNT, nor a pack entity that `explodes` will take one out. Creative mode still removes them, exactly as it still removes bedrock, so a pack author is never locked out of their own build. It requires a restart, since the block's numbers are set as it registers.

**It is the block, not the spawner.** There is no per-spawner switch. The option changes `minecraft:spawner` itself, so it reaches every spawner in the world at once: the vanilla structures that place one, any a mod places, and any your own packs place. A spawner inside one of your `.nbt` templates, placed by an `imprint` entry, is an ordinary spawner block carrying its own block entity, so it is covered the moment the option is on.
