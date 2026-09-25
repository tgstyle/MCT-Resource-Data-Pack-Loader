# Resource Data Pack Loader

**One folder that overrides anything Minecraft or a mod provides, defines new content from JSON, and controls what generates, in every world, on clients and servers, with nothing for players to switch on.**

One working example. Drop it straight into `rdploader` and look at how each file is written.

- [RDPLExamplePack.zip](../example/RDPLExamplePack.zip) uses nearly every kind of file the loader reads: blocks, items, a fluid, a creative tab, biomes, a world template, a dimension behind a gate, worldgen, a potion and its brewing, a villager and trades, recipes, loot, overrides of vanilla things, a sound, an advancement and a function. Its readme says what to check in game.

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

**How packs work**
- [How definitions work](#how-definitions-work)
- [What you can override](#what-you-can-override)
- [Server-side packs](#server-side-packs)
- [Registry renames](#registry-renames)
- [Mod API](#mod-api)
- [Packs written for 1.12.2](#packs-written-for-1122)

**Blocks and items**
- [Blocks](#blocks)
- [Containers](#containers)
- [Bells](#bells)
- [Models, blockstates and textures](#models-blockstates-and-textures)
- [Making vanilla treat your block properly](#making-vanilla-treat-your-block-properly)
- [Items](#items)
- [Fluids](#fluids)
- [Materials, tabs, sounds, tags](#materials-tabs-sounds-tags)
- [Property overrides](#property-overrides)
- [Hardness groups](#hardness-groups)

**Crafting, loot and trade**
- [Disabled blocks and items](#disabled-blocks-and-items)
- [Furnace recipes and fuels](#furnace-recipes-and-fuels)
- [Potions, potion types and brewing](#potions-potion-types-and-brewing)
- [Anvil work](#anvil-work)
- [Block drops](#block-drops)
- [Player loot](#player-loot)
- [Villagers and trades](#villagers-and-trades)

**Creatures and hazards**
- [Entity variants](#entity-variants)
- [Exposures](#exposures)

**The world**
- [World templates](#world-templates)
- [Game rules](#game-rules)
- [Biomes](#biomes)
- [Dimensions](#dimensions)
- [Portals and gates](#portals-and-gates)
- [The deep world](#the-deep-world)
- [Cave regions](#cave-regions)

**Generating the world**
- [Worldgen entries](#worldgen-entries)
- [Shapes](#shapes)
- [Spreads](#spreads)
- [Structure maps](#structure-maps)
- [Village plots](#village-plots)
- [City layout maps](#city-layout-maps)
- [Retrogen](#retrogen)
- [Pregeneration](#pregeneration)

**Game modes**
- [World intro](#world-intro)
- [Teams](#teams)
- [Scoring](#scoring)
- [Raids](#raids)
- [Cards](#cards)

**Control**
- [The control layer](#the-control-layer)
- [What each group does](#what-each-group-does)

**Other mods**
- [Blast Plaster integration](#blast-plaster-integration)

**Reference**
- [Value lists](#value-lists)
- [Folder list](#folder-list)
- [Commands](#commands)
- [Good to know](#good-to-know)
- [When something doesn't work](#when-something-doesnt-work)
- [Bonus: vanilla tweaks](#bonus-vanilla-tweaks)
- [Keys that did not carry forward](#keys-that-did-not-carry-forward)

---

# Getting started

## What it is

*getting started*

Resource Data Pack Loader (RDPL) reads a single folder, `rdploader`, and does three jobs:

- **Overrides.** A file in the folder replaces the one the game or a mod would have loaded. No toggle, no per-world setup, nothing for players to enable.
- **New content.** JSON definitions register blocks, items, fluids, biomes, dimensions, potions and villagers. No Java, no jar.
- **Control.** Block ore, biome, structure or recipe generation, flatten bedrock, set spawn rates, void the overworld, set world defaults.

## Where files go

*getting started*

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
| `<namespace>/raids/*.json` | Waves that come for a village when a player brings an omen into it. [Raids](#raids) |
| `<namespace>/entities/*.json` | Entity variants built on entities that already exist. [Entity variants](#entity-variants) |
| `<namespace>/hardness/*.json` | Mining time and blast multipliers for groups of blocks. [Hardness groups](#hardness-groups) |
| `<namespace>/anvils/*.json` | Enchantments an anvil puts on a named item, an advancement it earns, and a lock until then. [Anvil work](#anvil-work) |
| `<namespace>/cards/*.json` | On-screen cards shown on a trigger, and the messages this mod says itself. [Cards](#cards) |
| `<namespace>/exposures/*.json` | Hazards that expose players near or carrying named blocks and items. [Exposures](#exposures) |
| `<namespace>/overrides/<target>/<name>.json` | Properties of existing blocks, items and potion types, changed in place. [Property overrides](#property-overrides) |
| `<namespace>/villages/*.json` | Plots a city or village can build. [Village plots](#village-plots) |
| `<namespace>/pathintersects/*.json` | Designs painted where village roads meet. [Village roads](#village-roads) |
| `<namespace>/structuremaps/*.json` | Templates composed into one large building on a grid. [Structure maps](#structure-maps) |
| `<namespace>/citymaps/*.json` | A drawn street plan a city is laid out from instead of rolling one. [City layout maps](#city-layout-maps) |
| `<namespace>/portalframes/*.json` | Frames a player can build and light. [Portal frames](#portal-frames) |
| `<namespace>/blastplaster/*.json` | What Blast Plaster does after an explosion, per dimension. [Blast Plaster integration](#blast-plaster-integration) |
| `<namespace>/structures/*.nbt` | Templates, for saplings, `imprint` and mod overrides. [What you can override](#what-you-can-override) |
| `<namespace>/recipes/*.json` | Crafting recipes, added or replaced. [What you can override](#what-you-can-override) |
| `<namespace>/recipe_removals/*.json` | Recipes deleted by name, namespace or output. [What you can override](#what-you-can-override) |
| `<namespace>/disabled/*.json` | Blocks and items taken out of play. [Disabled blocks and items](#disabled-blocks-and-items) |
| `<namespace>/furnace/*.json` | Furnace recipes added and removed. [Furnace recipes and fuels](#furnace-recipes-and-fuels) |
| `<namespace>/fuels/*.json` | Burn times. [Furnace recipes and fuels](#furnace-recipes-and-fuels) |
| `<namespace>/brewing/*.json` | Brewing stand recipes. [Potions, potion types and brewing](#potions-potion-types-and-brewing) |
| `<namespace>/potions/*.json` | Potion effects. [Potions, potion types and brewing](#potions-potion-types-and-brewing) |
| `<namespace>/potion_types/*.json` | Bottled potions built from those effects. [Potions, potion types and brewing](#potions-potion-types-and-brewing) |
| `<namespace>/villagers/*.json` | Villager professions. [Villagers and trades](#villagers-and-trades) |
| `<namespace>/trades/*.json` | What professions buy and sell. [Villagers and trades](#villagers-and-trades) |
| `<namespace>/loot_tables/*.json` | Loot tables, replaced. [What you can override](#what-you-can-override) |
| `<namespace>/loot_injections/*.json` | A pool added to a table that already exists. [What you can override](#what-you-can-override) |
| `<namespace>/block_drops/*.json` | Extra or replacement drops for blocks a pack does not own. [Block drops](#block-drops) |
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

*getting started*

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

*getting started*

Open the jar, find the file you want to change, and copy its path from `assets` or `data` onwards:

```
assets/minecraft/textures/block/iron_ore.png                 (in the Minecraft jar)
rdploader/assets/minecraft/textures/block/iron_ore.png       (your override)

data/minecraft/loot_tables/blocks/iron_ore.json              (in the Minecraft jar)
rdploader/data/minecraft/loot_tables/blocks/iron_ore.json    (your override)
```

The path after `assets` or `data` is always identical to the path inside the jar. Nothing is renamed or moved.

## Organizing packs

*getting started*

Loose files work under `rdploader/assets/<namespace>/` and `rdploader/data/<namespace>/`. Grouping works too, as a zip. A folder in `rdploader` is never a pack: it is skipped with a warning in the log, so zip a pack up before it goes there. When zipping, select the contents and zip those, not the folder holding them: a zip whose top level is one folder wrapping `assets` or `data` is skipped, and the log says so.

```
rdploader/assets/minecraft/textures/block/iron_ore.png
rdploader/MyTextures.zip
```

**Packs in the wrong folder.** At startup, before reading `rdploader`, RDPL looks through the game's `resourcepacks` folder and the `datapacks` folder of every world (on a dedicated server, the world `level-name` names) and moves every RDPL pack zip it finds into `rdploader`. A zip is an RDPL pack when it holds RDPL definition files, such as `data/<namespace>/blocks/` or, from a 1.12.2 pack, `assets/<namespace>/blocks/`. A plain resource or data pack stays where it is. A zip whose name `rdploader` already holds is left in place, and so is an RDPL pack in a folder; both get a warning. Every move is written to `logs/rdpl.log`. The game drops a moved pack from the resource pack list or the world's data packs on its own, and RDPL loads it from `rdploader` from then on.

**Priority.** When two packs contain the same file, prefix the names with `RDPL` and a number; higher numbers load later and win:

```
rdploader/RDPL0 BaseTextures.zip
rdploader/RDPL1 SeasonalTextures.zip
rdploader/RDPL9 ModFixes.zip
```

Case-insensitive; a space, dash or underscore after the number is optional; the prefix is hidden from the display name. An unprefixed pack loads first and loses to any numbered pack. Priority also orders worldgen entries, which matters when one pack lays down blocks another replaces.

**Disable a pack** by appending `.disabled` to its name.

**One zip for every version.** A zip may carry a `versions/<version>/` folder for each Minecraft version it serves, `versions/1.12.2/`, `versions/1.20.1/` and `versions/1.21.1/`, each laid out like the root of a pack for that version, `pack.mcmeta` included. A file under the running version's folder is read in place of the same path at the root; the root is shared by every version, and another version's folder is never read. Put what every version reads alike at the root and only what differs into a version folder, and one zip loads on all three.

A `pack.mcmeta` at the root of the zip is welcome but not needed: the mod presents every pack to the game under one entry of its own, with the pack format the game expects, so a pack never goes stale on a format number. Put a `pack.png` beside it to give the folder's entry an icon. Without one the entry shows the RDPL icon.

## Resource packs: who wins

*getting started*

By default RDPL files sit above the resource packs a player selects, so a resource pack cannot override them. Add `O` or `N` after the `RDPL` prefix to decide per pack:

```
rdploader/RDPLO Branding        always wins; resource packs cannot touch it
rdploader/RDPLN BaseTextures    a resource pack can override it
rdploader/RDPL1O Seasonal       priority and override combined
```

Packs without a letter follow the `overrideResourcePacks` config option. `/rdpl list` marks the packs that override. The letter must end the prefix (followed by a space, dash, underscore, or nothing), so `RDPLOverhaul` is a pack named `Overhaul`, not an `O` flag.

The same tiers cover data packs. A pack marked `N` sits below the data packs a world carries in its own `datapacks` folder, and one marked `O` sits above them.

---

# How packs work

## How definitions work

*how packs work*

Alongside the folders that override files, there are folders that describe new things. A definition file groups one or more things of a kind under `variants`, and each key inside `variants` is a registry name: `data/mypack/blocks/ore.json` holding a variant called `ruby_ore` registers `mypack:ruby_ore`. The file's own name is a grouping and nothing more; one file can hold one block or a dozen that share their settings.

Registration happens at the lowest priority the loader offers, so if a real mod registers the same name, the mod wins and your file is ignored. Nothing here can replace a mod.

**Where the line is.** Anything needing a block entity of its own, a screen, an inventory or per-tick logic of its own needs a real mod, with one exception: the [container](#containers) type, which carries an inventory and a screen of its own. Everything short of that is fair game.

### Your namespace is your mod

*how definitions work*

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

### Pack options

*how definitions work*

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
| `default` | yes | boolean | | The option's value until the user changes it. An object without a boolean `default` is ignored, with a warning |
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

On launch the pack's option files become one real config file the user owns, named after the pack, `rdploader/config/PackA.json`, created with the pack's defaults and merged on pack updates so new options arrive without touching what the user already set. Changes apply on the next game start, and the Pack Options button on the world selection and create world screens is where a player flips them. Options belong to named packs only, that is zips, since the generated file is named after the pack; loose files under `rdploader/assets` and `rdploader/data` have no pack name and carry no options, so zip loose content into a named pack if it needs a switch.

Any definition's `requires` list can then name an option with a `config:` entry: `"requires": ["config:enableTestingContent"]` registers that content only while the option is true, exactly as a missing mod would skip it. A bare name checks every pack's file and every pack defining it must agree; `"config:PackA:enableTestingContent"` names one pack. An option no pack defines counts as false and is warned about once.

An option that gates something a world was made with is remembered by that world, recorded again every time the world saves. Change it and open the world again, and when the change leaves content the world holds unregistered, the world is backed up first, to the game's own `backups` folder, exactly as the Edit World screen does; if that backup fails, the world is not opened. The first player into the overworld is told which options changed, and that the copy was made when one was.

A `file:` entry gates on a file or folder existing under the game folder, for coupling content to something outside RDPL's own packs, such as another mod's resource pack: `"requires": ["file:config/StarMaker/resources/0_jackspace2_celestialpack.zip"]` registers the content only while that exact file is installed. The path is relative to the game folder, always with forward slashes, and may not contain `..`.

### Inheriting definitions

*how definitions work*

A block or item definition can start from another in the same kind with `"inherits"`, naming any variant's registry name, then override whatever differs:

    { "inherits": "mypack:ruby_ore",
      "variants": { "sapphire_ore": { "hardness": 4.0 } } }

The child copies every stat of the parent's file and the named variant, file order never matters, chains resolve parent-first, and a circle or a missing parent is logged and leaves the child as written. Fields the child writes replace the inherited value; nested variant properties override one by one, but lists such as `requires` replace whole, so write the full list wanted. Blocks inherit only from blocks and items only from items.

### Block and item templates

*how definitions work*

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

## What you can override

*how packs work*

- **Anything in a mod's assets folder**, textures, models, blockstates, language files, sounds, fonts, splash texts, guide books, manuals
- **Advancements, loot tables, tags and functions**, server side, so they work on dedicated servers too
- **Recipes**, replace a mod's recipe or add your own
- **Structure templates**, the `.nbt` files mods use for generated buildings, under `<namespace>/structures/`
- **Registry renames**, keep old worlds working when a mod renames a block or item
- **Recipe removals**, delete a crafting recipe by name, namespace or output
- **Disabled blocks and items**, take any block or item out of play, see [Disabled blocks and items](#disabled-blocks-and-items)
- **Loot injections**, add a pool to a loot table instead of replacing the whole thing
- **Block drops**, add to or replace what any block drops when it is broken, experience included
- **Player loot**, roll a loot table when a player dies, on top of what they were carrying or instead of it
- **Properties of existing blocks, items and potions**, hardness, light, stack sizes, food on anything, a potion's effects, see [Property overrides](#property-overrides)
- **Furnace recipes, fuel burn times, creative tabs and sound events**

What a block drops is its loot table on this version: to change what stone drops, ship `data/minecraft/loot_tables/blocks/stone.json`, and to add to it without replacing it, a loot injection or a [block drops](#block-drops) rule, which can also give experience.

RDPL is good for replacing one or two recipes, and recipes for your own content should be added in the pack alongside it. For full recipe control across a modpack, KubeJS and CraftTweaker are the better options, and a file here still replaces the original completely, so to change one ingredient or drop one loot entry, use those.

## Server-side packs

*how packs work*

A pack can live on the server alone, with players on plain vanilla clients, under one constraint: **nothing in it may register anything**. The mod accepts any remote; the pack decides. A vanilla client plays with the registries it shipped with, so a pack that adds to them must be on both sides.

| Server alone is enough | Needs the pack on the client too |
| --- | --- |
| `worldgen`, `worldtemplates`, `gamerules`, `structures`, `structuremaps`, `citymaps`, `villages`, `pathintersects`, `caveregions`, `biomes`, `dimensions` | `blocks`, `items`, `fluids`, `materials`, `containers` |
| `recipes`, `recipe_removals`, `furnace`, `fuels`, `brewing`, `anvils`, `tags`, `disabled` | `potions`, `potion_types`, `sounds`, `tabs`, `exposures` |
| `loot_tables`, `loot_injections`, `player_loot`, `advancements`, `functions` | `entities`, `villagers`, `portalframes` |
| `gates`, `cards`, `trades`, `registry_remap`, `teams`, `scoring`, `raids`, `hardness`, `blastplaster` | `models`, `blockstates`, `textures`, `lang`, `worldintro`, `overrides` (client folders: with no client, leave them out) |
| the whole control layer, settings, and pregeneration | |

The right-hand column is a hard stop: blocks, items, entity types, sounds and potion effects a vanilla client does not have cannot be described to it, and a dimension's own portal is one of the pack's blocks; exposures load only alongside that content. The left-hand column works because everything there either runs entirely server-side, reaches the client as data pack entries vanilla already reads (biomes, cave regions, dimension types), or reaches it through packets vanilla already speaks (server-filled crafting result slot, ordinary advancement packets, status-message gate refusals, and a pregeneration hold made of vanilla game mode/title/teleport packets).

Setup:

1. Enable `vanillaClients` in the config (`content` category, needs a restart). It enforces the right-hand column: those folders are skipped at load and each skipped file is named in the log, so a slipped block file becomes a log line instead of a refused connection.
2. Keep definitions out of the right-hand folders anyway; skipped files are dead weight. Where the pack references items (a gate's `hold`, `killedDrops`, recipe outputs, trades), name only items vanilla or the server's other both-sided mods provide. A biome that names the pack's own ground blocks keeps the base biome's ground, and a dimension opened by its own portal needs that portal block, so send players there by command.
3. Entity variants are entity types of their own on this version, so they belong to the right-hand column: with `vanillaClients` on they are skipped, their spawns with them, and the log names them.
4. Install on the server as usual, with Blast Plaster, which the mod requires and which registers nothing either. Nothing goes on players' machines; `/rdpl` will not exist for them.
5. Test with one clean vanilla client join of the same version. Failures are loud: the connection is refused at the door, not quietly broken later.
6. Two accepted cosmetic gaps: server-added recipes craft but do not appear in the recipe book, and the hold while land is made is a plain spectator hold with the progress on the action bar, without the fog and the logo the mod's own client draws.

## Registry renames

*how packs work*

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

## Mod API

*how packs work*

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

*how packs work*

A pack made for the 1.12.2 line loads as it is. The loader recognizes one by its `pack.mcmeta` format, by definition folders under `assets/` with no `data/` beside them, or by a `.lang` file, and carries it forward. A zip is converted once, inside itself: every file this version reads differently is written into the zip's `versions/1.20.1/` folder (1.21.1: `versions/1.21.1/`) with everything below already done, and the 1.12.2 files at the root stay as they were, so the same zip still loads on 1.12.2, as [one zip for every version](#organizing-packs) describes. A zip that already has this version's folder is read through it and never converted again, and the root's 1.12.2 files the port replaced, such as definitions under `assets/`, `.lang` files, 1.12.2 blockstates and models, and textures under `textures/blocks/` and `textures/items/`, are not read on this version; only root files the port passes through unchanged, such as sounds, still are. The zip is written to a temporary file first and replaces the original only once it is complete. Loose files under `rdploader/assets` are not rewritten; they are read through the same port every time the folder is scanned.

- Definition folders move from `assets/<namespace>/` to `data/<namespace>/`, and the vanilla data folders with them: recipes, loot tables, loot injections, advancements, functions and structures.
- `textures/blocks/` and `textures/items/` are served as `textures/block/` and `textures/item/`, in models, in pixel maps and in the files themselves. An item model at `models/item/<file>/<variant>.json` is served as `models/item/<variant>.json`.
- Every id with metadata, `minecraft:wool:14` or `minecraft:dye:4`, is run through the game's own data fixers, the same code that upgrades a 1.12.2 world, so it comes out as the block or item it became: `minecraft:red_wool`, `minecraft:lapis_lazuli`. A block state that survived the flattening as a property, `minecraft:log:1` to `minecraft:oak_log` with `axis=y`, comes out as a `properties` object. The pack's own ids resolve through its own definitions: `mypack:materials:5` becomes the variant whose `meta` was 5, and `mypack:ruby_ore` the file's first variant, since each variant is a block of its own here. Entity and biome names are fixed the same way, and dimension numbers become ids.
- `variants` keep their keys; `meta` is dropped and `oreDict` becomes `tags` through the ore dictionary's mapping onto the convention tags. An `oredict/*.json` file becomes one item tag file per name it adds to or removes from: a `-name` removal lands in the tag's `remove` list, and removing `*` replaces the tag. A bare `creativeTab` takes the pack's namespace, and a 1.12.2 vanilla tab label such as `misc` becomes the nearest vanilla tab.
- A `.lang` file is served as the `.json` the game reads, with `tile.mypack:file.variant.name` as `block.mypack.variant`, `item.` the same way, `itemGroup.x` as `itemGroup.mypack.x`, `fluid.x` as both fluid keys, and everything else as written.
- A 1.12.2 blockstate is not served at all. Its textures are read instead and served under the names the generator looks for, `textures/block/<variant>.png` with `_top` and `_bottom` where the blockstate had `end`, `top` or `bottom`, so the blockstate and the models are generated for each variant as they would be for a pack written here.
- Recipes lose their `data` and gain flattened ids, `forge:ore_shaped` becomes `minecraft:crafting_shaped` with `ore` ingredients as `tag`, loot tables lose `set_data` the same way, and an advancement's `item` with `data` becomes `items`. An advancement's `background` moves from `textures/blocks/` to `textures/block/`.
- A recipe's 1.12.2 Forge vocabulary is carried too: a `forge:ore_dict` or `minecraft:item` ingredient type is dropped, a `minecraft:item_nbt` ingredient becomes `forge:nbt` (1.21.1: `neoforge:components`) with its nbt run through the data fixers, `minecraft:item_exists` becomes `forge:item_exists` (1.21.1: every condition takes its `neoforge` name, under `neoforge:conditions`), an item without a namespace takes the recipe's, and `data` 32767 becomes a list of every variant. A `#CONSTANT` from a mod's `_constants.json` cannot come along, and the log names it. Wherever a list names items, `name:*` becomes every variant the item had, and a single value takes the first; that reaches `recipe_removals` outputs and `furnace` removals, which are carried as items too. An anvil's `item` or `with` written as `name:*` becomes a list of every variant, and any of them answers.
- Loot tables renamed since 1.12.2 are renamed wherever a pack names one: a `loot_injections` target, a `player_loot` table and a `loot_table` entry (1.21.1: its `value`), so `minecraft:entities/zombie_pigman` becomes `minecraft:entities/zombified_piglin`. `killed_by_player` with `inverse` becomes an `inverted` condition, `entity_properties` with `on_fire` becomes a `flags` predicate, and `set_attributes` names such as `generic.maxHealth` become `generic.max_health` (1.21.1: the operation takes its new name, and the modifier's `name` becomes its `id`).
- An override of a 1.12.2 block the flattening split, such as `overrides/minecraft/wool.json`, is read as an override of every block it became, all sixteen wools, since 1.12.2 changed every variant at once.
- A game rules file's `gameLoopFunction` becomes the `#minecraft:tick` function tag, written as `data/minecraft/tags/functions/tick.json`, since the game rule is gone. The pack's own dimension numbers are read through its `dimensions` files, so `"id": 7` in `dimensions/verdant.json` makes 7 `mypack:verdant` wherever the pack names it. Both sides of a `villageBlocks` pair are fixed, the chance kept, and a `registry_remap` file may keep 1.12.2's plural `minecraft:blocks` and `minecraft:items`.
- A world template that turns off every structure 1.12.2 had in a dimension turns off the structures only this version has there too: `ancient_cities`, `buried_treasures`, `ocean_ruins`, `pillager_outposts`, `ruined_portals`, `shipwrecks` and `trail_ruins` in the overworld and `nether_fossils` in the Nether. Leave one of the 1.12.2 names on and they are left alone. The generator control keys, `blockWorldGenerators` and its companions, are left out of a converted template with one line in the log, since nothing here reads them. A flat template's `generatorOptions` layers have their block names fixed the same way, so `minecraft:grass` becomes `minecraft:grass_block`.
- Functions are rewritten line by line into this version's command syntax. Ids with data values go through the same data fixers, so `give @p minecraft:wool 1 14` becomes `give @p minecraft:red_wool 1` and `give @p mypack:materials 1 5` gives the variant whose `meta` was 5, and item, entity and block nbt is fixed as a world's would be (1.21.1: an item's nbt becomes its components). `testforblock`, `testfor` and `scoreboard players test` become `execute if`, `execute <entity> <x> <y> <z> [detect ...]` becomes `execute as ... at @s [positioned ...] [if block ...] run`, `effect` takes `give` and `clear`, `blockdata`, `entitydata` and `replaceitem` become `data merge` and `item replace`, and `scoreboard teams` and `scoreboard players tag` become `team` and `tag`. Selectors trade `score_X_min` and `score_X` for `scores`, `r` and `rm` for `distance`, `l` and `lm` for `level`, `m` for `gamemode`, `c` for `limit` and `sort`, and `rx` and `ry` for `x_rotation` and `y_rotation`. Enchantment and effect numbers, particle and sound names, game mode and difficulty numbers, a `weather` duration in seconds and a relative `tp` of another entity are carried too. A line the port cannot carry is kept as written and the log names the file, the line and why; a function holding such a line does not load until it is fixed by hand. `block_drops` is carried as it is, its `meta` folded into the block name or its `properties`.
- A 1.12.2 flat world's floor sat at y 0, and this version lays a flat world from its bottom at y -64, so the port moves heights down with it. Where a world template's `worldType` is `flat` or `superflat`, its `worldSpawn` and `resetSendsTo` move down 64 (to its `worldMinHeight` when it names one), and so do a team's `spawn`, `standIn` `at` and `spawnBox` heights and a score file's `opens.lobby` when every world template the pack ships is flat. A pack dimension with `flat` terrain moves its `groundLevel`, and any position that names that dimension, the same way (by its `minHeight` when it has one). Functions do not say where they run, so when the pack's overworld is flat every absolute y in its functions moves down 64, a selector's `y` included, and the log says so once; `~` and `^` heights are left alone. A world on normal terrain keeps every coordinate, since its surface stays at sea level.

The log carries one summary line per ported pack and a line for each file it moved, left out or could not carry, and every key this version no longer reads is still named by the parser that meets it. The port is a best effort, not a finished pack: open the zip's `versions/` folder, read those lines, and finish by hand what it names, starting with any command line it kept as written and any texture it could not find a name for.

---

# Blocks and items

## Blocks

*blocks and items*

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
  "lightOpacity": 255,
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

*blocks*

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
| `torch` | Wall and floor placement, with a particle. Gives off the variant's `light` as written, so a torch at `0` gives none |
| `bell` | A bell like the one the game's villages have: rings when used on its side, by redstone or when a projectile strikes it, swings in its frame, and makes nearby raiders glow. Its blockstate carries the facing and how it hangs |
| `log` | Rotates to the face you place it against, and carries the `minecraft:logs` tag so tree felling and Blast Plaster treat it as a trunk |
| `leaves` | Decays, shears, tints and drops a sapling, and carries the `minecraft:leaves` tag. Left `opaque`, they draw solid, like fast leaves; set `"opaque": false` to see through them |
| `sapling` | Grows into a tree or into one of your structures |
| `crop` | Grows through stages, drops a seed and a produce item, wheat seeds and wheat for whichever the file leaves out |
| `flower` | A one-block plant standing on soil |
| `cane` | Grows upward in a column, like reeds or cactus |
| `vine` | Climbs and hangs on the sides of blocks. With `growth` it grows down to `maxHeight` and, with `spread`, reaches sideways onto neighboring walls; without it, it stays as placed |
| `portal` | Sends whatever walks in to another dimension |
| `container` | Holds an inventory a player can open, of any size, and can fill itself from a loot table the first time it is opened. Draws as an ordinary block or as a chest, whichever the pack asks for. Without a `container` object it holds three rows of nine |

### File keys

*blocks*

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `variants` | yes | object of variant name to variant | | One block per entry. The key is its registry name, and names its blockstate, its models, its textures and its lang key |
| `type` | no | one of the types above | `basic` | Which shape the block takes |
| `material` | no | one of the [block materials](#value-lists) | `rock` | The block does what that material did on 1.12.2: whether it drops anything when broken by hand, how pistons treat it, whether lava sets it alight, whether flowing liquid washes it away and whether a placed block replaces it. A `log` is always `wood`, `leaves` always `leaves`, a `vine` `vine`, a `torch` or `ladder` `circuits`, a `crop` `plants`, and `stairs` and a `wall` behave as their `modelBlock` |
| `soundType` | no | one of the [sound types](#value-lists) | `stone`; `wood` for a `log`, `plant` for `leaves` and a `crop`, the `modelBlock`'s for `stairs` and a `wall` | Footsteps, breaking and placing |
| `mapColor` | no | one of the [map colors](#value-lists) | from the material | How it looks on a map |
| `harvestTool` | no | `pickaxe`, `axe`, `shovel`, `hoe`, `sword` | `pickaxe` | Which tool harvests it, written into the game's `mineable` tags for you; `sword` goes into `resourcedatapackloader:mineable/sword`, which a pack's own `sword` tools mine. For drops it matters only on a material that needs a tool. As on 1.12.2, a pickaxe also mines `rock`, `iron` and `anvil` at full speed, and an axe `wood`, `plants` and `vine`. Any other name, such as `shears`, is logged and left out |
| `harvestToolLevel` | no | 0 to 4 | `0` | 0 wood, 1 stone, 2 iron, 3 diamond, 4 netherite, written into the `needs_*_tool` tags for you. A `basic`, `ore`, `container`, `portal`, `fence`, `pane`, `log`, `falling`, `slab` or `wall` block ignores it and takes the variant's `harvestLevel`, as on 1.12.2 |
| `silkHarvest` | no | boolean | `true` | Whether silk touch returns the block itself |
| `opensWith` | no | item id | none | Makes the block a lockbox: breaking it drops the block itself, and right-clicking with the named item consumes one, plays the block's break sound, pays out the variant's `drops` list and removes the block. Any other click shows the action-bar line `block.<pack>.<block>.locked` from the lang files |
| `openSound` | no | sound name | the break sound | What a lockbox plays when opened instead of its break sound. A 1.12.2 name still reads, see [sound names](#value-lists) |
| `expDrop` | no | object with `min` and `max` | none | Experience dropped when a player breaks the block, or a pack mob that collects experience digs it; pistons, water and explosions drop none. Silk Touch takes it away only when `silkHarvest` is on |
| `creativeTab` | no | tab name | none | The tab it appears in, see [Creative tabs](#creative-tabs) |
| `renderLayer` | no | `solid`, `cutout`, `cutout_mipped`, `translucent` | to suit the type | How it is drawn |
| `opaque` | no | boolean | `true` | Whether it blocks sight and light entirely |
| `fullCube` | no | boolean | same as `opaque` | Whether it fills its whole space |
| `lightOpacity` | no | 0 to 255 | `255` when opaque, else `0` | How much light it absorbs: 15 or more stops all of it, and `0` lets sunlight straight through. A `slab` keeps the game's own, and a chest-model container lets light through |
| `slipperiness` | no | float | `0.6` | Ice is `0.98` |
| `flammability` | no | int | `0` | How readily fire consumes it |
| `fireSpread` | no | int | `0` | How readily fire spreads from it |
| `explosionResistanceDivisor` | no | float | `1.0` | Divides each variant's `resistance` against explosions |
| `modelBlock` | no | block name | `minecraft:stone` | Block whose model is borrowed when yours ships no texture and no model of its own |
| `itemModel` | no | `state`, `item` | `state` | `state` follows the blockstate, `item` looks for its own file, `models/item/<name>.json` |
| `tint` | no | `biome`, `none`, or a hex color | none | Needs a `tintindex` in the model to show |
| `plantTypes` | no | list of [plant types](#value-lists) | none | What can be planted on it |
| `behavesAs` | no | list of `till`, `path`, `bush`, `animals` | none | Vanilla behaviors to take on |
| `bounds` | no | list of six numbers, 0 to 1 | full block | The collision box, as `[x1, y1, z1, x2, y2, z2]` |
| `requires` | no | list of mod ids or pack namespaces | none | The file is skipped unless all are present |
| `particle` | torch only | `none`, `flame`, `colored` | `flame` | The particle above a torch |
| `particleColor` | torch only | hex color | `FFFFFF` | Used when `particle` is `colored` |
| `smoke` | torch only | boolean | `true` | Whether it smokes |
| `leafSapling` | leaves only | block name | none | The sapling they drop |
| `leafSaplingChance` | leaves only | int | `5` | One in N leaves drops one |
| `seed` | crop only | item name | `minecraft:wheat_seeds` | The item that plants it, and what an unripe crop drops |
| `produce` | crop only | item name | `minecraft:wheat` | What harvesting yields |
| `maxAge` | crop only | int | `7` | How many growth stages |
| `growth` | plants only | object | none | See [Growth](#growth) |
| `sapling` | sapling only | object | none | See [Saplings](#saplings) |
| `portal` | portal only | object | none | See [Portals and gates](#portals-and-gates) |
| `container` | container only | object | none | See [Containers](#containers) |
| `bell` | bell only | object | none | See [Bells](#bells) |

### Variant keys

*blocks*

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `hardness` | no | float | `1.0` | How long it takes to break. Obsidian is `50`, `-1` is unbreakable |
| `resistance` | no | float | `5.0` | Blast resistance as 1.12.2 reads it: the block keeps three fifths of the figure, so `10` gives stone's `6` |
| `light` | no | 0 to 15 | `0` | Light emitted |
| `harvestLevel` | no | 0 to 4 | `0` | The tool tier of a `basic`, `ore`, `container`, `portal`, `fence`, `pane`, `log`, `falling`, `slab` or `wall` block, in place of the file's `harvestToolLevel`. Other types go by the file's value |
| `rarity` | no | `common`, `uncommon`, `rare`, `epic` | `common` | Name color in the tooltip |
| `maxSize` | no | 1 to 64 | `64` | Stack size |
| `tags` | no | list of tag ids | none | Block and item tags this variant is written into, such as `forge:ores/ruby` on 1.20.1 or `c:ores/ruby` on 1.21.1. The tag files are generated for you |
| `drops` | no | list of drops | drops itself | What breaking it yields |
| `portal` | portal only | object | the file's | This variant's own portal in place of the file's, written as in [Portals and gates](#portals-and-gates). The file still needs one of its own |

**Names are permanent.** A variant's key is written into every saved world that contains it. Renaming one later turns placed blocks into air, unless a [registry rename](#registry-renames) maps the old name to the new. A file may hold as many variants as it likes; each is a block of its own, and a `meta` key from a 1.12.2 pack is ignored with a note in the log.

### Drops

*blocks*

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

*blocks*

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
| `stages` | no | int | `16` | Growth stages before it is done. A vine tries to grow on one random tick in this many |
| `growth` | no | int | | One in N chance per random tick to advance |
| `spread` | no | int | `0` | How far it spreads to neighboring blocks. A vine stops reaching sideways once this many vines stand within two blocks of it |
| `maxHeight` | no | int | `3` | Cane and vine. How tall the column grows, or how far a vine hangs down; a vine at `1` neither grows nor spreads |
| `soil` | no | list of block names | the type's usual | What it stands on |
| `drop` | no | item name | none | Cane and vine. What it drops when broken; a flower drops itself |
| `dropCount` | no | int | `1` | Cane and vine. How many |
| `needsSky` | no | boolean | `false` | Only grows where the sky is visible |
| `needsWater` | no | boolean | `false` | Only grows near water |
| `waterRange` | no | int | `1` | How far that water may be |
| `damage` | no | boolean | `false` | Hurts whatever touches it |
| `damageAmount` | no | float, half hearts | `1.0` | How much it hurts |
| `breaksNeighbors` | no | boolean | `false` | Breaks blocks placed beside it, like cactus |

### Saplings

*blocks*

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

## Containers

*blocks and items*

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
| `lootTable` | text | empty | A loot table rolled into the block the first time anything reaches its contents, a player opening it, a hopper, a comparator or breaking it, exactly as a dungeon chest fills. A container a player places never rolls it. Empty leaves it starting empty |
| `chestModel` | boolean or text | `false` | Draws as a chest with a lid that opens, instead of as an ordinary block from your own texture. `true` uses the vanilla chest artwork; a texture name such as `mypack:entity/chest/strongbox` uses your own chest sheet instead, for the placed block and for the item alike. A chest-model block also defaults `opaque` to `false`, the way a vanilla chest is, so light is not cut off at the block and the chest is not drawn dark |
| `guiTexture` | text | empty | Your own background image for the screen. Empty draws one from the vanilla chest screen at whatever size the rows and columns need |
| `guiWidth` | int | none | How wide the screen is, drawn from the top left of that image read as a 256 by 256 sheet, required with `guiTexture` |
| `guiHeight` | int | none | How tall the screen is, required with `guiTexture` |
| `curioSlot` | text | empty | An item only: the Curios slot it can be worn in, `back`, `belt`, `body`, `charm`, `head`, `necklace`, `ring` or any slot another mod adds. A backpack usually takes `back`. Ignored, with everything else about the item still working, when Curios is not installed. The 1.12.2 key `bauble` is read as this one and takes the Baubles names: `amulet` becomes `necklace`, `ring` gives two ring slots, `belt`, `head`, `body` and `charm` keep their names, and `trinket` fits every one of those slots. Any other name leaves the item unworn with an error line |

**Nine rows by twelve is the ceiling**, the most a screen can carry. A pack asking for more is cut to it with an error line saying so. One warning about the tallest: a nine-row screen is 276 pixels, and a 1080 display at GUI scale `auto` gives 270, so the top and bottom clip by three pixels each; scale 3 shows it whole.

**The screen is drawn, not shipped.** A container of nine columns or fewer and six rows or fewer uses the vanilla chest screen as it stands, so it looks exactly like a chest of that size. Anything larger is assembled from the same image at draw time, the top edge, a row of slots repeated to fit, and the bottom with the player's own inventory, so a pack can ask for sizes no vanilla screen covers without shipping an image of its own. `guiTexture` overrides all of that where a pack wants its own look, and then `guiWidth` and `guiHeight` must say how big it is or the drawn one is used and an error line says so.

**What the block does.** It keeps its contents through a save and a reload, drops them when broken, answers a comparator by how full it is, and keeps the rows and columns it was made with, so changing them in the pack later leaves the containers already in a world as they were. A container item or a container block cannot be put inside a container. `chestModel` also gives it the chest's opening sound and the lid animation; left off, the block draws from its own texture like any other block, so a crate, a barrel or a cabinet all work.

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

**A container item is a pouch**, an item of type `container` carrying the same `container` block with `rows` and `columns`; open it with a right-click, and it keeps its contents as it changes hands. Without the `container` object it holds a single row of nine. Give it `curioSlot` and, where Curios is installed, it goes in that slot and a key opens it without taking it off, `V` by default, rebindable under Resource Data Pack Loader in the controls. Pressing it again, with a worn container already open, moves to the next one you are wearing and wraps around, so several worn at once are all reachable. The key only appears when Curios is there, and everything else about the item, the right-click and its inventory, works whether it is or not. The pouch's wearability is written into the Curios tag for that slot for you.

**The loot table fills on first use**, not when the block is placed, which is what makes it useful in a structure: whoever opens it first gets the roll, and a hopper or a comparator reaching it first rolls it just the same. The same table can be used by `lootTable` on an imprint shape or a village plot, so a pack can place these through worldgen and stock them the same way.

## Bells

*blocks*

`<namespace>/blocks/*.json`

```json
{
  "type": "bell",
  "material": "iron",
  "soundType": "metal",
  "renderLayer": "cutout",
  "creativeTab": "decorations",
  "bell": {
    "swing": true,
    "sound": "minecraft:block.note_block.bell",
    "resonateSound": "minecraft:block.note_block.chime"
  },
  "variants": { "village_bell": { "hardness": 5.0, "resistance": 30 } }
}
```

And its blockstate, `assets/mypack/blockstates/village_bell.json`, keyed on `attachment` and `facing`:

```json
{
  "variants": {
    "attachment=floor,facing=north": { "model": "mypack:block/village_bell_floor" },
    "attachment=floor,facing=east": { "model": "mypack:block/village_bell_floor", "y": 90 },
    "attachment=floor,facing=south": { "model": "mypack:block/village_bell_floor", "y": 180 },
    "attachment=floor,facing=west": { "model": "mypack:block/village_bell_floor", "y": 270 },
    "attachment=ceiling,facing=north": { "model": "mypack:block/village_bell_ceiling" },
    "attachment=ceiling,facing=east": { "model": "mypack:block/village_bell_ceiling", "y": 90 },
    "attachment=ceiling,facing=south": { "model": "mypack:block/village_bell_ceiling", "y": 180 },
    "attachment=ceiling,facing=west": { "model": "mypack:block/village_bell_ceiling", "y": 270 },
    "attachment=single_wall,facing=east": { "model": "mypack:block/village_bell_wall" },
    "attachment=single_wall,facing=south": { "model": "mypack:block/village_bell_wall", "y": 90 },
    "attachment=single_wall,facing=west": { "model": "mypack:block/village_bell_wall", "y": 180 },
    "attachment=single_wall,facing=north": { "model": "mypack:block/village_bell_wall", "y": 270 },
    "attachment=double_wall,facing=east": { "model": "mypack:block/village_bell_between_walls" },
    "attachment=double_wall,facing=south": { "model": "mypack:block/village_bell_between_walls", "y": 90 },
    "attachment=double_wall,facing=west": { "model": "mypack:block/village_bell_between_walls", "y": 180 },
    "attachment=double_wall,facing=north": { "model": "mypack:block/village_bell_between_walls", "y": 270 }
  }
}
```

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `swing` | boolean | `true` | Draws the swinging part from a model file of its own and swings it when the bell rings. `false` draws the whole bell from the blockstate's models, with nothing animated |
| `sound` | sound name | `minecraft:block.note_block.bell` | Played when the bell rings. Empty rings silently |
| `resonateSound` | sound name | `minecraft:block.note_block.chime` | Played when the bell resonates because raiders are near. Empty resonates silently |

**It hangs the way the game's own bell does.** Placed on top of a block it stands on the floor, turned the way you face; under a block it hangs from the ceiling; against a wall it hangs from that wall, and between two walls when the far side is solid too. It drops when whatever holds it is gone, and a bell between two walls becomes a single-wall bell when one of them goes. Its hit box follows vanilla's for each of the four, so `bounds` is not read.

**What rings it.** Using the side of the body, below the beam: a floor bell on the two faces its beam runs across, a wall bell on the two faces beside the wall, a ceiling bell on any side. The top, the bottom and anything above the body do nothing. A redstone signal rings it once as it switches on, and an arrow, a snowball or any other projectile rings it when it strikes a side that a hand could. The body swings away from the side it was struck on for two and a half seconds; redstone swings it along the way the bell faces.

**What a ring does.** Villagers within 32 blocks hear it and run home to hide for fifteen seconds, the way the game's own bell sends them. When a raider is within 32 blocks, the bell resonates a quarter second after the ring, and two seconds later every raider within 48 blocks glows for three seconds, with colored particles beside the bell on the side each one stands. A raider is anything a [raid](#raids) sent, and the game's own illagers and witches. A bell of this type is a village bell to every raid: it rings as each wave arrives without being named in the raid's `bell`.

**The models.** A bell is keyed by `attachment` and `facing`, sixteen states in all, and `powered` is left out of the keys. With `swing` on those models draw the frame only, and the swinging part is a model file of its own, `<namespace>:block/<name>_body`: the mod loads it by that path and the renderer draws it, so the name is not yours to choose and it never appears in the blockstate. It is modeled in block space where the body rests, and tilts around the point half a block in and three quarters of a block up, as vanilla's does. With `swing` off there is no body model, and the sixteen frame models draw the bell whole. The item model written for the hand draws the frame and the body together, so the bell shows whole in hand; ship `models/item/<name>.json` to draw it any other way.

**The swing is drawn by the client.** A ring reaches players as a block event, so a dedicated server swings the bell for everyone who has this mod, and a player without it only hears the bell. Sounds, resonance and glowing all happen on the server.

## Models, blockstates and textures

*blocks and items*

Defining a block or item registers it. What it *looks* like is a set of asset files in the same folders and the same format the game uses, under your own namespace, and on this version most of them are written for you.

```
assets/mypack/textures/block/ruby_ore.png
assets/mypack/textures/item/ruby.png
assets/mypack/lang/en_us.json
```

**Ship a texture and the rest is generated.** For every block whose blockstate the pack does not ship, the mod writes the blockstate and the models the type needs, pointing at `textures/block/<name>.png` where `<name>` is the variant's key, and for every item that has no `models/item/<name>.json`, an item model pointing at `textures/item/<name>.png`. A block with neither a texture nor a model of its own borrows the look of `modelBlock`, stone by default, so nothing ever renders as the purple and black square. Ship a `blockstates/<name>.json` of your own and the mod generates nothing for that block and uses yours; the same for `models/item/<name>.json`.

| Type | Texture files it looks for | Generated from |
| --- | --- | --- |
| `basic`, `ore`, `falling` | `<name>`, with `<name>_top` and `<name>_bottom` for the top and bottom faces where the pack ships them | `cube_all`, or `cube_bottom_top` when a top or bottom texture is there |
| `flower`, `sapling`, `cane`, `leaves`, `container` without a chest | `<name>` | `cube_all`, `cross` or `leaves` |
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
| `bell` | `<name>` | the game's own bell frames as `<name>_floor`, `<name>_ceiling`, `<name>_wall` and `<name>_between_walls`, the mod's `pack_bell_body` as `<name>_body`, and a blockstate of sixteen `attachment` and `facing` states over them |
| `crop` | `<name>_stage0` up to `<name>_stage<maxAge>`, or `<name>` for all | one `crop` model per stage, `age=0` to `7` mapped onto them |
| `portal` | `<name>`, or the nether portal's | `cube_all` for a `fullCube` block, as a portal block is on 1.12.2; three portal slabs, one per axis, for one that is not, such as a dimension's frame portal |
| `banner` | its own sheet, see [Banners](#banners) | the game's banner model |
| `container` with `chestModel` | the chest sheet named in `chestModel` | the mod's `pack_chest` model |

Every texture is looked for under `textures/block/`, and the name is the variant's key, so a block registered as `ruby_ore` wants `textures/block/ruby_ore.png` and nothing else needs writing. A block whose item is drawn flat, a door, a ladder, a torch, a sapling, a flower, a cane, a vine or a pane, takes `textures/item/<name>.png` for the hand when it exists and its block texture when it does not.

**Items** take `textures/item/<name>.png` and a generated `item/generated` model, or `item/handheld` for a tool. Ship `models/item/<name>.json` to draw it any other way.

**Fluids** need no model at all; one is generated from the `still` and `flow` textures.

**A block with several variants is several blocks.** Each key under `variants` is registered on its own, so each has its own blockstate, its own models and its own textures, named after the key. There is no shared blockstate carrying a `blocks` property, and nothing in a blockstate needs to say which variant it is: `blockstates/ruby_ore.json` is ruby ore's, and `blockstates/deep_ruby_ore.json` is the deep one's.

### Writing your own

*models, blockstates and textures*

Everything generated can be replaced. A blockstate the pack ships is used as it is, in the game's own format: the vanilla `variants` keyed on the block's properties, or `multipart`. The properties are the game's own for each type: `axis` on a log, `type` on a slab, `facing`, `half` and `shape` on stairs, `facing`, `half`, `hinge` and `open` on a door, `facing`, `half` and `open` on a trapdoor, `facing`, `in_wall` and `open` on a gate, `age` on a crop and a cane, `stage` on a sapling, `north`, `east`, `south`, `west` on a fence or a pane with `up` added on a wall and a vine, `rotation` on a standing banner and `facing` on a wall one, `axis` on a portal, `attachment` and `facing` on a bell with its `powered` left out of the keys. A `basic`, `ore`, `falling`, `leaves`, `flower` or `container` block has one state, keyed `""`.

Point the models at the parents that take textures, not at the finished vanilla ones: `cube_all` takes an `all`; `cube_column` an `end` and a `side`; `cross` a `cross`; the stairs parents `bottom`, `top` and `side`; `fence_post` and `fence_side` a `texture`; `template_wall_post` and `template_wall_side` a `wall`; the glass pane templates a `pane` and an `edge`; the door parents a `top` and a `bottom`; `template_orientable_trapdoor_*` and `template_fence_gate*` a `texture`; `template_torch` a `torch`; `crop` a `crop`; `vine` and `ladder` their own name. A model naming a finished vanilla model such as `oak_door_bottom_left` inherits vanilla's textures with it, whatever the blockstate says.

### Banners

*models, blockstates and textures*

A banner is the one type where the shape of the block and the shape of the model part ways, so it is worth setting out in full.

**It registers two blocks.** One definition gives you the standing banner under your own name and a second block named `<name>_wall` for the hanging one. Both need a blockstate; only the standing one gets an item, and that item decides which of the two it places, standing when you click the top of a block and wall when you click a side. You never place the wall block directly and it needs no item of its own.

**The standing one turns in sixteenths.** Its property is `rotation`, running `0` to `15`, because a banner turns in sixteenths rather than quarters. A blockstate's own `y` only accepts 0, 90, 180 and 270, so each rotation points at a small model of its own that takes your banner model as its parent and turns it with a `transform`:

```json
{
  "variants": {
    "rotation=0": { "model": "mypack:block/my_banner_rotation_0" },
    "rotation=1": { "model": "mypack:block/my_banner_rotation_1" }
  }
}
```

```json
{
  "parent": "mypack:block/my_banner",
  "transform": { "rotation": { "y": -22.5 }, "origin": "center" }
}
```

…and so on to `15`, each one `-22.5` degrees further round. The sign matches the game's own banners, which turn by minus the rotation. Build the model facing south, since that is where a banner placed by a player looking south ends up pointing. The wall block is an ordinary blockstate with the usual four `facing` entries at 0, 90, 180 and 270, since there is nothing fractional about it. A 1.12.2 pack's Forge blockstate is turned into exactly this when the pack is converted.

**The model is nearly two blocks tall.** A banner occupies one block for placement and collision, but it is drawn far outside it, and a model that stops at the top of its own block looks stunted. Vanilla's proportions, in sixteenths of a block, are worth copying exactly:

| Part | From | To |
| --- | --- | --- |
| Post | `0` | `28` |
| Crossbar | `28` | `29.33` |
| Cloth | `2.67` | `29.33` |
| Cloth width | `1.33` | `14.67` |
| Wall cloth | `-13` | `13.67` |

So a standing banner reaches to `29.33`, most of two blocks, and a wall banner hangs thirteen sixteenths *below* the block holding it. Model elements may run from `-16` to `32`, so both fit. The wall form has no post or crossbar, only cloth.

**The cloth is twice as tall as it is wide, and your texture has to be too.** That face is `13.33` by `26.67`. Map a square texture onto it and the design is squeezed to half its height. Block textures cannot themselves be twice as tall as they are wide, since anything non-square is read as an animation, so the way round it is a larger square sheet with the cloth in part of it: a 32×32 file holding the cloth as a 16×32 region, addressed as `"uv": [0, 0, 8, 16]`, with the post and crossbar strips in the space beside it. UV coordinates always run 0 to 16 whatever the file's resolution, so the same numbers work at any size.

**Its item wants a model of its own.** An item that inherits a model this tall will burst out of its slot at the usual block scale, so give `models/item/<name>.json` a `display` block of its own with the scale brought down and the whole thing translated back into the frame.

**Without a blockstate it is drawn from a sheet.** A banner whose pack ships no blockstate for it gets a generated one, and the game's banner renderer draws it in the vanilla banner's shape from the sheet at `textures/entity/banner/<name>.png`, laid out the way the vanilla banner's is. This is an addition of this version; the standing and wall blocks each decide it by their own blockstate.

**There are no colors or patterns on it.** A pack banner carries no layer list the way vanilla banners do. The design is the texture, the same way a door's look is its texture, and one definition is one banner. Dyeing it and stacking patterns on it is not something a pack can reach.

**It takes the `material` you give it.** A stone banner is mined with a pickaxe like the stone it says it is.

### Textures written as pixel maps

*models, blockstates and textures*

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

*models, blockstates and textures*

**A model naming a finished vanilla model inherits vanilla's textures too.** `torch`, `ladder`, `oak_door_bottom_left` and `wheat_stage0` all carry their own textures, so a model pointing at one gets vanilla's look no matter what you put beside it. Parent models such as `cube_all`, `cross` and `crop` take their textures from the model that names them and behave, and so do the door, trapdoor and gate templates.

**Names come from the language file.** A block or item shows a raw key until `lang/en_us.json` gives it one, and the keys are the game's own: `block.mypack.ruby_ore` for a block and the item that places it, `item.mypack.ruby` for an item, `itemGroup.mypack.tab` for a creative tab, `fluid_type.mypack.molten_ruby` and `fluid.mypack.molten_ruby` for a fluid, `effect.mypack.ruby_sight` for a potion effect, `entity.mypack.angry_cow` for an entity variant, `biome.mypack.ruby_forest` for a biome. One name each; nothing on this version wants a key written twice.

## Making vanilla treat your block properly

*blocks and items*

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
| `bush` | Flowers, grass and saplings can be planted on it and stay on it, as on dirt. The same as `plains` in `plantTypes` |
| `animals` | Animals spawn on it in the light, as they do on grass |

## Items

*blocks and items*

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

### Item types

*items*

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

### Item file keys

*items*

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `variants` | yes | object of variant name to variant | | One item per entry. The key is its registry name, and names its model, its texture and its lang key |
| `type` | no | one of the types above | `basic` | Which type the item takes |
| `creativeTab` | no | tab name | none | The tab it appears in, see [Creative tabs](#creative-tabs) |
| `material` | tool, armor | material name | none | Which of your materials it is made from |
| `toolClass` | tool | `pickaxe`, `axe`, `shovel`, `sword` | none | Which tool it is. A `sword` is a 1.12.2 tool rather than a vanilla sword: it hits for 3 plus the material's `damage`, mines the blocks whose `harvestTool` is `sword`, takes mining enchantments, and neither sweeps nor cuts cobwebs |
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

### Item variant keys

*items*

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `maxSize` | no | 1 to 64 | `64` | Stack size |
| `rarity` | no | `common`, `uncommon`, `rare`, `epic` | `common` | Name color in the tooltip |
| `healAmount` | food | int, half drumsticks | `0` | Hunger restored |
| `saturation` | food | float | `0.0` | Saturation restored |
| `tags` | no | list of tag ids | none | Item tags this variant is written into; the tag files are generated for you |
| `potion` | food, drink | `potion,duration,amplifier` | none | An effect applied when the variant is eaten or drunk. A fourth part, `true`, makes it ambient. A beneficial effect is named in green in the tooltip, followed by the amplifier in Roman numerals when it is above 0, and no effect it gives shows particles |

## Fluids

*blocks and items*

`<namespace>/fluids/*.json`

The file's path is the registry name of the fluid's block. `name` names the fluid itself, its bucket and its lang keys, and is the file's path unless the file sets it.

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
| `name` | no | string | the file name | The registry name of the fluid and of its bucket (`<name>_bucket`); the block keeps the file's path |
| `still` | no | texture path | vanilla water still | Texture for the still fluid |
| `flow` | no | texture path | vanilla water flowing | Texture for the flowing fluid |
| `color` | no | hex color | none | Tint applied to those textures. On the default water textures it is multiplied by the blue of 1.12.2's water, so a color picked for 1.12.2 looks the same here |
| `bucket` | no | boolean | `true` | Register a bucket for it |
| `luminosity` | no | 0 to 15 | `0` | Light emitted |
| `density` | no | int | `1000` | Negative floats upward, like a gas |
| `temperature` | no | int, kelvin | `300` | Water is 300, lava 1300 |
| `viscosity` | no | int | `1000` | How slowly it flows: the fluid moves on once every viscosity / 200 ticks. Water is 1000, lava 6000 |
| `gaseous` | no | boolean | `false` | Treated as a gas |
| `creativeTab` | no | tab name | none | The tab the bucket appears in |
| `block` | no | object | | The fluid block. `material` (`water`): `water` can be swum and drowned in, floats boats, puts out burning creatures, keeps farmland wet and boils away when poured in the Nether; `lava` sets whatever stands in it alight, cannot be swum or drowned in and uses the lava bucket sounds; any other material does none of these. `flammability` (`0`) and `fireSpread` (`0`): how readily fire consumes the block and spreads from it. `quantaPerBlock` (`0`, read as 8): how far it runs from a source, one block less than the number as on 1.12.2; fluids here reach 1, 2, 3 or 7 blocks, so 4 and 5 run 3 blocks and 6 and up run 7. `potions` (none, a list of effects given to whatever stands in it, each written `potion,duration,amplifier` with an optional fourth part `true` for an ambient one) |
| `requires` | no | list of mod ids or pack namespaces | none | The file is skipped unless all are present |

## Materials, tabs, sounds, tags

*blocks and items*

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
| `repairItem` | no | item name | none | What repairs a tool made from it in an anvil. Armor made from it is not repaired this way, as on 1.12.2 |
| `reduction` | no | list of four ints | | Armor points, in the order feet, legs, chest, head |
| `toughness` | no | float | `0.0` | Armor toughness, as diamond has |
| `equipSound` | no | sound name | `item.armor.equip_iron` | Sound when armor is put on |
| `armorTexture` | no | texture prefix | the file name | The worn armor texture, read from `textures/models/armor/<name>_layer_1.png` and `_layer_2.png` under that namespace |

### Creative tabs

*materials, tabs, sounds, tags*

`<namespace>/tabs/*.json`

Blocks, items and buckets name their tab in `creativeTab`. A full id, such as `mypack:rubypack` or `minecraft:combat`, is used as written. A bare name is read the way 1.12.2 read a tab label: `buildingBlocks` goes to `minecraft:building_blocks`, `decorations` to `minecraft:functional_blocks`, `redstone` to `minecraft:redstone_blocks`, `transportation` and `tools` to `minecraft:tools_and_utilities`, `misc` and `materials` to `minecraft:ingredients`, `food` and `brewing` to `minecraft:food_and_drinks`, and `combat` to `minecraft:combat`, and any other bare name is the tab `<namespace>:<name>` in the namespace of the file that names it. A tab no file declares is made for you, titled from `itemGroup.<namespace>.<name>` and showing the first item in it. A tab file's path is the tab's name unless `label` overrides it.

```json
{ "label": "rubypack", "icon": "mypack:ruby" }
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `label` | no | string | the file name | The tab's id: blocks and items name it in `creativeTab`, and the shown name comes from `itemGroup.<namespace>.<label>` in the lang files |
| `icon` | no | item name | none | The item shown on the tab |

### Sounds

*materials, tabs, sounds, tags*

`<namespace>/sounds/*.json`

The file name is yours to choose, only the folder is read, and several files stack.

The vanilla `sounds.json` format, so a pack can ship its own audio. A file here registers the sound events; the client still reads the audio through the pack's own `assets/<namespace>/sounds.json`, so ship both, the index under `assets` and the events under `data`.

### Tags

*materials, tabs, sounds, tags*

`<namespace>/tags/<kind>/*.json`

Tags are the game's own format in the game's own folder, and a pack ships them as it would in a data pack: `tags/items/ores/ruby.json` (1.21.1: `tags/item/`) holding `{ "values": ["mypack:ruby_ore"] }` puts the ore in `mypack:ores/ruby`, and a file under `data/forge/tags/items/ores/ruby.json` (1.21.1: `data/c/...`) adds to the shared convention tag every mod reads. A pack's own blocks and items name theirs in the variant's `tags` instead, and the files are written for you; a `harvestTool` and `harvestToolLevel` write the `mineable` and `needs_*_tool` tags the same way.

The 1.12.2 ore dictionary is what tags replaced. Its names map onto the convention tags: `oreRuby` is `forge:ores/ruby` on 1.20.1 and `c:ores/ruby` on 1.21.1, `ingotCopper` is `ingots/copper`, `gemRuby` is `gems/ruby`, `dustX` is `dusts/x`, `nuggetX` is `nuggets/x`, `blockX` is `storage_blocks/x`, and `logWood`, `plankWood` and `stickWood` are the game's own `minecraft:logs`, `minecraft:planks` and the convention `rods/wooden`. A tag file's `"remove": [...]` takes single entries out of a tag, and `"replace": true` with an empty `"values": []` empties it, so a tag from a lower pack or a mod can be cut down or cleared. To take items out of every tag at once, and out of play, use [Disabled blocks and items](#disabled-blocks-and-items).

## Property overrides

*blocks and items*

`<namespace>/overrides/<target>/<name>.json`

The path names the target: everything after `overrides/` is the namespace and name of the block, item or potion type being changed.

Everywhere else, a pack replaces a file or adds one. An override does neither: it changes the properties of a block, item or potion type that already exists, vanilla or modded, without touching any of its files. The path names the target, so `overrides/minecraft/stone.json` changes `minecraft:stone`, and `overrides/tconstruct/<name>.json` changes that mod's block the same way.

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

### Block properties

*property overrides*

Every key is optional and a file changes only what it names, so a file at `overrides/minecraft/stone.json` holding `hardness`, `light` and `soundType` alone makes stone mine almost instantly, glow, and sound like glass. One file carries block, item and potion keys together. These apply when the target is a block:

| Key | Value | What it does |
| --- | --- | --- |
| `hardness` | float | Mining time, the same figure a block definition takes. Without `resistance` it also raises the blast resistance to at least the same figure, as 1.12.2 does |
| `resistance` | float | Blast resistance as 1.12.2 reads it: the block keeps three fifths of the figure, so `10` gives stone's `6` |
| `slipperiness` | float | `0.6` is ordinary ground, `0.98` is ice |
| `light` | `0` to `15` | Light given off |
| `lightOpacity` | `0` to `15` | How much light the block stops |
| `soundType` | one of the sound types | Step, place and break sounds |
| `harvestTool` | `pickaxe`, `axe`, `shovel`, `hoe` or `sword` | What mines it fast, written into the tool tags; `harvestToolLevel`, default `0`, sets the tier: 1 stone, 2 iron, 3 diamond, 4 and above netherite. Whether the block drops without the right tool stays as the block has it |
| `flammability` | int | How readily it burns away; `fireSpread`, default `5`, how readily fire reaches it |

### Item properties

*property overrides*

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

### Potion type effects

*property overrides*

`effects` at the top level of the file rewrites a potion type's effect list outright:

```json
{
  "effects": [
    { "potion": "minecraft:levitation", "duration": 200, "amplifier": 0 }
  ]
}
```

At `overrides/minecraft/swiftness.json` the Potion of Swiftness now grants Levitation. Each entry takes `potion` (required), `duration` (`3600`), `amplifier` (`0`), `ambient` (`false`) and `showParticles` (`true`), the same as in `potion_types/`, and the list may not be empty.

### Other mods, reloads and limits

*property overrides*

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

## Hardness groups

*blocks and items*

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
  "keeps": false,
  "adventure": { "tools": ["minecraft:iron_pickaxe"], "teams": ["red"], "players": [], "entities": ["mypack:digger"] },
  "advancement": "mypack:deep_miner",
  "becomes": { "advancement": "mypack:deep_miner", "block": "mypack:rich_ore" },
  "requires": ["mypack"]
}
```

### Mining and blasting

*hardness groups*

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `blocks` | yes | list of block names or objects | | The group. Same forms as a worldgen `replace` |
| `except` | no | list of block names or objects | none | Taken back out of the group, whatever `blocks` says |
| `miningTime` | no | number, or object with `min` and `max` | `1.0` | How many times longer the block takes to break, for a player and for a `digs` mob alike |
| `blastResistance` | no | number, or object with `min` and `max` | `1.0` | Multiplies the block's blast resistance |
| `buckets` | no | 1 to 256 | `10` | How many steps the range is divided into |
| `minHeight` | no | int | the world's bottom | Below this the roll is the hardest step |
| `maxHeight` | no | int | the world's top | Above this the roll is the hardest step |
| `field` | no | object | see below | The shape the roll clumps into |
| `requires` | no | list of mod ids or pack namespaces | none | The file is skipped unless all are present |

A single number gives every block in the group the same multiplier, and nothing is rolled. A `min` and `max` roll per position: `max` where the field is empty, `min` at the middle of a clump, and the steps between decided by `buckets`.

### Adventure mining and unlocks

*hardness groups*

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `keeps` | no | boolean | `false` | The block stays where it is when it is mined out: the drops, the experience, the tool wear and the break sound all happen and the block is still there to mine again, so the group is an endless seam at whatever pace `miningTime` sets. Creative removes it as ever |
| `adventure` | no | object | none | Who may break the group in adventure mode, where nothing breaks otherwise. `tools` lists the items one of which must be in hand, empty for anything held; `teams`, `players` and `entities` say who, a team by its name, a player by name, a mob by its entity id for the `digs` task, and all three empty means anyone with the tool. Survival and creative are untouched |
| `advancement` | no | `namespace:path` | none | The group counts for a player only once they have that advancement. Two groups may name the same block, one with an advancement and one without, and the unlocked one wins; a player without it gets the plain group, or vanilla if there is none. Mobs hold no advancements, so a gated group never reaches a `digs` task, and blast resistance and the texture roll, which belong to no player, come from the plain group |
| `becomes` | no | object | none | The group's blocks turn into another block, world wide, the moment any player earns `advancement`: `{ "advancement": "mypack:deep_miner", "block": "mypack:rich_ore" }`. Every loaded chunk is swept at once, and a chunk loaded or made later is swept as it comes in, so the old block is gone for good. Give the new block a group of its own to change how it mines |

### The field

*hardness groups*

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

*the field*

Every block draws its own step, and a block one face away can pass a weaker step on to it. That gives dense, fine-grained specks, most of them a single block, with the odd larger patch where they meet. It is the closer of the two to how mining feels in the mod this borrows from.

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

*the field*

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

*hardness groups*

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

*hardness groups*

Only a player's own mining is changed. Machines that break blocks read the block's hardness directly and are not affected. Blocks a player places are rolled the same as any other, since the roll belongs to the place rather than to the block, and a block carried elsewhere takes on whatever its new place says.

---

# Crafting, loot and trade

## Disabled blocks and items

*crafting, loot and trade*

`<namespace>/disabled/*.json`

The file name is yours to choose, only the folder is read, and several files stack.

Takes blocks and items out of play without unregistering them, so worlds keep their ids and deleting the file brings everything back. Vanilla, mod and pack content are treated alike, a pack's own blocks and items included, and a disabled block disables its item just as a disabled item disables its block.

```json
{
  "requires": ["thermal"],
  "names": ["thermal:tin_ore", "thermal:deepslate_tin_ore", "mekanism:salt*"],
  "namespaces": ["bigreactors"],
  "tags": ["forge:ores/tin"]
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `names` | no | list of block and item names | none | What is disabled. A name ending in `*` matches every name that starts with the rest |
| `namespaces` | no | list of mod ids | none | Every block and item of the mod |
| `tags` | no | list of tag names | none | Every item in the item tag and every block in the block tag of that name, and both tags are left empty. A leading `#` is allowed |
| `requires` | no | list of mod ids | none | The file is skipped unless every one is loaded. `config:` and `file:` entries work as they do everywhere else |

A disabled block or item:

- is gone from every creative tab and the search tab, and hidden in JEI
- has no recipe that makes it or uses it: every recipe of every type with it as the result goes, crafting, cooking, stonecutting and smithing alike, and so does every recipe with a slot only it can fill. A slot that also takes something else keeps its recipe, and a tag slot simply loses it with the tag
- is taken off every item and block tag, and is listed in `resourcedatapackloader:disabled` instead
- is stripped from every loot roll, chests, mobs and fishing alike, from block drops and from villager and wandering trader trades, and a dropped stack of it vanishes
- cannot be placed, used, swung or picked up, and the stack in hand is deleted when a player tries
- is deleted wherever a stack of it turns up: from a player's inventory and ender chest at login and every second after, from any container when a player opens it, and from chests and other inventories as their chunk loads
- is removed from the world where it is placed: each block of it turns to air, its block entity with it, as its chunk loads

To swap placed blocks for something else instead of removing them, give them a `blockReplacements` line such as `thermal:tin_ore=minecraft:stone` in the world template, see [Replacements](#replacements). A block the replacement process swaps is left to it. Recipes another mod keeps inside its own machines belong to that mod and are not reached. Hiding from the creative tabs and JEI happens on the client, so a vanilla client still lists the item. `content.disabled` in the config turns the folder off.

To empty a tag while its items stay in play, use a tag file with `"replace": true` and an empty `"values": []` instead, see [Tags](#tags).

## Furnace recipes and fuels

*crafting, loot and trade*

`<namespace>/furnace/*.json`

The file name is yours to choose, only the folder is read, and several files stack.

Adds and removes furnace recipes. A removal also takes out the matching blast furnace, smoker and campfire recipes, since 1.12.2 kept every cooking recipe in the one furnace list; an addition is a furnace recipe only.

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
| `experience` | no | number | `0.0` | Experience per item smelted, one at most: 1.0 or more gives one point for each item taken out, so a `count` of 2 gives two. Iron ore gives 0.7 |

An addition whose input something already smelts is ignored and the log names the recipe in the way, as 1.12.2 does; remove that recipe in the same file or an earlier one to replace it.

Entries under `remove` are either a bare item name, which removes every recipe producing it, or an object naming `input`, `result`, or both to narrow it down. A removal naming neither is skipped and the log says so.

Files apply in load order, each file's removals before its additions. A removal in a later file therefore also takes out an addition an earlier file made, but never reaches an addition that comes after it. An addition counts as a furnace recipe of the mod its output belongs to, so `blockFurnaceRecipes` and `blockedFurnaceMods` block it like any other.

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

*crafting, loot and trade*

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
| `effectiveness` | no | float | `0.5` | Read so a 1.12.2 file loads; neither 1.12.2 nor this version acts on it |
| `attributes` | no | list of objects | none | `attribute` (the game's id, such as `minecraft:generic.movement_speed`), `uuid`, `amount` (`0.0`), `operation` (`0`) |
| `icon` | no | object | none | `x` and `y`, the icon's column and row on the 1.12.2 status sheet, each `0` if left out. Read only without `iconTexture` |
| `iconTexture` | no | texture path | the RDPL icon, or none when `icon` is set | An image a pack ships, such as `mypack:textures/effect/rage.png`, drawn whole as the icon |

The effect's icon is the texture `assets/<namespace>/textures/mob_effect/<name>.png`, 18 by 18 like the game's own, and a pack that ships it there always wins. Otherwise `iconTexture` names an image a pack ships, which is copied there whole; `icon` alone picks a vanilla effect's icon by its place on the 1.12.2 status sheet, `x` across and `y` down from 0, such as `{ "x": 2, "y": 1 }` for jump boost; and an effect that names neither shows the RDPL icon.

### Potion types

*potions, potion types and brewing*

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
| `baseName` | no | string | the namespace and name | Names the potion: the lang key `item.minecraft.potion.effect.<baseName>`, with `splash_potion`, `lingering_potion` or `tipped_arrow` in place of `potion` for the other forms. A `potion_bottle` holding it shows the same name, and a 1.12.2 pack's `potion.effect.<baseName>` keys are converted |
| `effects` | yes | list of objects | | See below |

Each effect takes `potion` (required), `duration` (`3600`), `amplifier` (`0`), `ambient` (`false`) and `showParticles` (`true`).

### Brewing

*potions, potion types and brewing*

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

## Anvil work

*crafting, loot and trade*

`<namespace>/anvils/*.json`

The file name is yours to choose, only the folder is read, and several files stack. Each file is one piece of work.

Put the named item in an anvil's left slot and its `with` item in the right, and the anvil offers the left one back with the enchantments listed, or its `result`, for the levels named; one of each is spent unless a count asks for more, and the rest of either stack stays in the anvil. Taking it out can also earn an advancement, and the item can be held back from use until that advancement is earned: a sword that only swings once it has been worked.

```json
{
  "item": "minecraft:iron_sword",
  "with": "minecraft:wooden_sword",
  "levels": 3,
  "enchantments": { "minecraft:sharpness": 2 },
  "grants": "mypack:sword_rite",
  "locks": true
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `item` | yes | item name, a list of them, or `{ "item", "count" }` | | What goes in the left slot, any item of a list, and how many of it one piece of work takes, one by default; the rest of the stack stays for the next. `{ "item": "minecraft:coal", "count": 8 }` with a diamond `result` is eight coal to one diamond |
| `with` | yes | item name, a list of them, or `{ "item", "count" }` | | What goes in the right slot, any item of a list, and how many of it are spent, one by default: `{ "item": "minecraft:coal", "count": 10 }` asks for a stack of at least ten and takes ten. An anvil never speaks up for a lone item, so every piece of work is a pair |
| `result` | no | item name, or `{ "item", "count" }` | the left item | What comes out instead of the left item, and how many, one by default, keeping the left item's tags, so an unbreakable iron pickaxe and ten coal can come back as an unbreakable diamond one. The enchantments go on whichever comes out |
| `levels` | no | int | `1` | The experience levels the work costs, 1 at the least |
| `enchantments` | no | object of enchantment name to level | none | What the item comes back with. A level it already has at that height or above is left alone, and with nothing to raise the anvil offers nothing, unless `grants` is set |
| `grants` | no | `namespace:path` | none | An advancement earned as the work is taken out. Ship it under `advancements/` with an `impossible` criterion, so nothing else earns it |
| `locks` | no | boolean | `false` | Until the player has `grants`, the item cannot be swung at anything, used, or dug with; they are told what it waits on as it comes into their hand. Putting it in the anvil is still allowed, which is how it gets unlocked |

The anvil's own repairs and combinations are untouched: this only answers when the left holds a named item and the right holds its `with`.

A mob with `collectsExperience` spends its levels here as well. While it holds `item` in its main hand and `with` in its off hand and has `levels` to pay, it walks to an anvil, a chipped anvil or a damaged anvil within 16 blocks across and 4 up or down, and works it once it is within 3 blocks: the levels come off its own as they come off a player's, `with` is used up, the anvil wears as it does under a player, and the work ends up in its main hand. As it walks over a dropped item that some anvil work names under `with`, it picks it up into its off hand. `grants` and `locks` only concern players, so a mob earns nothing from `grants` and no lock holds it back.

## Block drops

*crafting, loot and trade*

`<namespace>/block_drops/*.json`

The file name is yours to choose, only the folder is read, and several files stack.

A block's loot table decides what it drops, but shipping one takes the whole table over, and a loot table cannot give experience. A rule here names a block and what breaking it drops on top of the usual drops, or instead of them, experience included, and leaves the block's own table alone.

```json
{
  "block": "minecraft:stone",
  "replace": false,
  "advancement": "mypack:deep_miner",
  "drops": [
    { "item": "minecraft:diamond", "count": "1-2", "chance": 0.05, "fortune": 1, "silkTouch": "never" },
    { "item": "minecraft:emerald", "silkTouch": "only" },
    { "experience": "2-4", "chance": 0.5 }
  ]
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `block` | yes | block id | | The block the rule watches |
| `properties` | no | object of property to value | none | Only the states with these values, as `{ "axis": "x" }` on a log; without it every state. A 1.12.2 `meta` is not read |
| `replace` | no | boolean | `false` | Whether the usual drops are discarded before these are rolled |
| `advancement` | no | `namespace:path` | none | The rule counts only for a player who has that advancement, so the same block can drop one thing before and another after |
| `drops` | yes | list of drops | | Each rolled on its own when the block is broken |

Each drop:

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `item` | yes, unless `experience` | item id | | What drops |
| `experience` | no | number or `low-high` | | Instead of an item, that much experience as orbs, rolled evenly within the range. `chance` and `silkTouch` apply as for an item |
| `count` | no | number or `low-high` | `1` | How many, rolled evenly within the range |
| `chance` | no | float | `1.0` | The odds the drop happens at all, `0.05` being one break in twenty |
| `fortune` | no | int | `0` | Up to this many extra per level of Fortune on the tool |
| `silkTouch` | no | `either`, `only` or `never` | `either` | Whether the drop needs a Silk Touch harvest, refuses one, or does not care. It is one when a player breaks, with a Silk Touch tool, a block that can be silk harvested the way 1.12.2 decided it: a full block without a block entity, or glass panes, iron bars, cobwebs and ender chests |

Rules see every break that drops the block's loot, as 1.12.2 did: a player's, and explosions, pistons, flowing water, mobs and a pack mob digging through the block, which roll every rule without an `advancement`. Where an explosion thins the block's own drops, each rolled item survives by the same odds; experience is not thinned. Several rules for one block all apply, a `replace` on any of them clearing the usual drops first.

## Player loot

*crafting, loot and trade*

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

## Villagers and trades

*crafting, loot and trade*

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
| `jobSite` | no | block name | none | The block a villager claims to take up this profession, the way a smithing table makes a toolsmith. Without one, no block hands the profession out: as on 1.12.2, a villager that is spawned or bred is given it at random, keeps it, and with no block to work at never restocks |
| `workSound` | no | sound name | none | What it plays while working at that block |

Careers are a 1.12.2 idea the game no longer has: a profession is one trade set, so a pack that had two careers ships two villager files. How the villager looks is an ordinary texture, shipped at `assets/<namespace>/textures/entity/villager/profession/<name>.png` and `textures/entity/zombie_villager/profession/<name>.png`, exactly where the game keeps its own. A trade that names a vanilla 1.12.2 profession together with its `career`, such as `minecraft:smith` with `armor`, goes to the profession that career became, here `minecraft:armorer`. A 1.12.2 file's `texture` and `zombieTexture`, a whole skin a pack ships, are copied to those two paths when the pack has nothing there, and the skin is drawn over the villager's own.

### Trades

*villagers and trades*

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
| `profession` | yes | profession name | | Whose trade this is. A vanilla 1.12.2 name with its `career` is read too, see above |
| `level` | no | int | `1` | Which trade tier it appears at, 1 to 5. A higher level joins level 5, the highest a villager reaches |
| `maxUses` | no | int | `12` | Times it can be used before locking |
| `xp` | no | int | `2` | Experience the villager earns per trade toward its next level |

A stack is `item` with `min` (`1`) and `max` (`min`), so a fixed price is just `min`.

---

# Creatures and hazards

## Entity variants

*creatures and hazards*

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
  "sounds": { "ambient": "entity.cow.ambient", "hurt": "entity.cow.hurt", "death": "entity.cow.death", "target": "mypack:scream", "targetVaries": 3, "explode": "mypack:boom", "throw": "mypack:whoosh" },
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

### Identity

*entity variants*

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `entity` | yes | `namespace:name` | none | The entity to build on. Any mod's, as long as it takes a plain world constructor |
| `name` | no | string | none | The name it carries in the world, in death messages and on its egg |
| `showName` | no | boolean | `false` | Show the name without looking at it |
| `profession` | no | `namespace:name` | random | For a villager, the trade it practices |
| `baby` | no | boolean or 0.0 to 1.0 | `false` | How often one spawns young, and it stays that way. `true` is always, a number is that share of them |
| `becomes` | no | list | none | Other variants this one may turn into as it spawns, by weight. See below |
| `egg` | no | boolean or object | `true` | A spawn egg, colored like the egg of the entity it copies. `{ "primary": "AABBCC", "secondary": "112233" }` picks your own colors, `false` leaves the egg out |
| `keepsBaseBaby` | no | boolean | `false` | Whether the base's own young roll also runs. Without it a zombie-based variant spawns young only as `baby` says, with no child from the zombie's own roll and no chicken jockey |
| `requires` | no | list of mod ids or pack namespaces | none | The variant is left out unless all are present |

A variant is a class of its own, so a world that contains one depends on the pack that made it, the same way it depends on a mod. Take the file away and the creatures in that world go with it.

**One egg or spawner giving a mix.** A variant is a class of its own, so on its own it always spawns exactly what it says. `becomes` is how a pack breaks that: a list of variants this one may turn into as it spawns, each with a weight, decided per creature.

```json
{
  "becomes": [
    { "variant": "mypack:walker", "weight": 95 },
    { "variant": "mypack:little_walker", "weight": 5 }
  ]
}
```

Naming itself is how it stays as it is, and the weights are the odds. Put that on `mypack:walker` and one egg and one spawn entry give mostly walkers with the occasional little one, the way a zombie egg gives you the odd baby. It happens as the creature enters the world, so it holds for eggs, `/summon` and natural spawning alike, and the creature that arrives is a real one of the chosen variant with everything that variant says. A spawner is stricter: it rolls only among the variants on the same base mob and the same team as the variant it is set to, so a zombie spawner gives that team's zombies and their young, and never a creature of another kind or another team, the way a vanilla zombie spawner stays a zombie spawner. A variant reached this way does not turn again, so two variants may name each other without spinning.

**Where `baby` fits.** The game has no baby zombie of its own: there is one zombie that rolls whether it is a child as it spawns. `baby` says how often, so `"baby": 0.05` is the vanilla habit and `"baby": true` is always. A variant does not take the zombie's own roll on top, so no child or chicken jockey turns up that `baby` did not ask for; `keepsBaseBaby` gives that roll back. Between them these are two ways at the same thing, and which to reach for depends on the difference you want: `baby` alone gives one variant that is sometimes young, `becomes` gives several variants that differ in whatever you like, and a mix of both is fine.

### Looks

*entity variants*

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `texture` | no | `namespace:textures/entity/<file>.png` | none | A skin of its own, laid out the same way the entity it copies is |
| `leftHanded` | no | boolean | `false` | Holds its weapon in the other hand |
| `glowing` | no | boolean | `false` | Outlined through walls |
| `invisible` | no | boolean | `false` | Not drawn, though its gear still is |
| `scale` | no | float | `1.0` | How big it is drawn, and how big its hitbox is |
| `angryScale` | no | float | `scale` | The size it swells to while it has something to attack, and for three seconds after it loses one |
| `width` | no | float | the base's | Its hitbox across, before `scale` is applied |
| `height` | no | float | the base's | Its hitbox up, before `scale` is applied |
| `bright` | no | boolean | `false` | Drawn at full light wherever it stands, as if in noon sun, so it is never dimmed by night, shade or a cave |
| `hideArmor` | no | boolean | `false` | Wears its armor without it being drawn |
| `hideHeld` | no | boolean | `false` | The same for whatever it is holding |
| `tint` | no | hex color | none | Colors the entity as it is drawn |
| `tintParts` | no | list of `body`, `armor`, `held` | `["body"]` | Which parts the tint reaches |

`scale` changes both the model and the hitbox on both sides, so what you see is what you can hit. A creature that changes its own size, an animal growing up or a zombie that is a child, is scaled around whatever size it has chosen, so the two do not fight. `angryScale` swells it while it has a target and returns it to `scale` when it loses one. Since the client is never told what a creature is hunting, the sprinting flag carries that news across, it is set on a variant that uses `angryScale` and on nothing else, so a mod reading sprinting on your variants will see it change. Growing inside a low ceiling is possible, the same way a slime growing is, so keep the difference modest.

A `texture` is bound in place of the one the entity would normally use, whatever renderer it inherits, so it works for modded entities as well as vanilla ones. It has to match the model it is drawn on, since the model is the base entity's, a skin, not a new shape. Layers keep their own textures, so armor still looks like armor on a reskinned zombie.

Armor is only ever drawn on an entity whose renderer has an armor layer, which means the humanoid mobs and villagers. A variant of a cow or a spider can carry armor and gets its protection, but nothing draws it, so `armor` under `attributes` is usually the tidier way to make such a creature tough. `hideArmor` is for the other case: a humanoid that should keep the armor in its slots, for the protection or for a mod that reads them, without it being seen.

### Its sounds

*entity variants*

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `sounds` | no | object | the base's | `ambient`, `hurt` and `death`, each a registered sound event, and a 1.12.2 name still reads, see [sound names](#value-lists). Three more it has no base sound for: `target` is played once each time it takes a target, and `explode` is what its blast sounds like, whether it blows itself up with `explodes` or throws TNT with `throws`. `throw` plays as it throws anything with `throws`, in place of the snowball throw, or of the fuse hiss for TNT. `targetVaries` shifts each `target` play up or down by a random amount within that many semitones, so `3` wanders a quarter octave either way; `0` plays it as it is. On this version the game plays its own blast sound as well, because a 1.20.1 client chooses the explosion sound itself; on 1.21.1 `explode` takes its place |
| `soundVolume` | no | number | `1.0` | How loud those sounds are |
| `soundPitch` | no | number | `1.0` | How high they play. Under 1 is deeper, over 1 is squeakier |
| `silent` | no | boolean | `false` | Makes no sound |

### Health, damage and effects

*entity variants*

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `immuneTo` | no | list of damage types | none | Damage it shrugs off, by the names 1.12.2 used, `fall`, `drown`, `explosion`, `explosion.player`, `magic`, `indirectMagic`, `mob`, `player`, `inWall` and the rest, or by a damage type id. See [damage types](#value-lists) |
| `fallDamage` | no | float | `1.0` | Multiplies the damage a fall does. `0` takes fall damage away |
| `absorption` | no | float | `0` | Extra hearts on top of its health |
| `creatureAttribute` | no | `undefined`, `undead`, `arthropod` or `illager` | the base's | What it counts as, so Smite and healing potions treat it accordingly |
| `effects` | no | list of objects | none | Effects it always has: `{ "potion": "minecraft:strength", "amplifier": 1 }` |
| `fireproof` | no | boolean | `false` | Never catches fire at all, so it is never hurt by fire or lava and never burns in daylight |
| `invulnerable` | no | boolean | `false` | Takes no damage from anything but the void and creative |
| `attributes` | no | object | none | `maxHealth`, `movementSpeed`, `attackDamage`, `attackSpeed`, `knockbackResistance`, `followRange`, `armor`. An attribute the entity does not normally have is given to it. `attackSpeed` is blows a second for a melee fighter, `1` as the game has it, so `2` strikes twice as often |
| `hurtResistance` | no | int, ticks | the game's `20` | How long after a hit it cannot be hurt again. Blows faster than half of this are lost, so a fast attacker wants a target with less |
| `ignoresEffects` | no | list of effect names | none | Effects that never take on it, whoever or whatever applies them: a hit, a splash, a beacon, an arrow, `/effect`. `all` refuses every effect, so a variant starts as a blank slate. Its own `effects` are still put on it |

### Movement

*entity variants*

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `jumpMultiplier` | no | float | `1.0` | How much higher it jumps than the entity it copies |
| `maxFallHeight` | no | int | the base's | How far it will drop while pathing |
| `noAI` | no | boolean | `false` | Stands where it is put and does nothing |
| `leashable` | no | boolean | `false` | Can be led on a lead, even if the entity it copies never could |
| `steerable` | no | boolean | `false` | Can be steered while ridden |
| `pathPriorities` | no | object | none | What it will walk through, as `WATER`, `LAVA`, `DANGER_FIRE`, `DOOR_WOOD_CLOSED` and the rest of the game's path types, each a number where a negative means never. The 1.12.2 `DANGER_CACTUS` and `DAMAGE_CACTUS` are read as `DANGER_OTHER` and `DAMAGE_OTHER`, where this version files cactus along with sweet berry bushes |
| `stepHeight` | no | float, blocks | the base's | How high a ledge it walks up without jumping |
| `climbs` | no | boolean | the base's | On, it climbs any wall it walks into, the way a spider does, whatever its base. Off, it climbs nothing, not even a ladder, and a spider stays on the ground |
| `teleports` | no | boolean | `true` | Whether an enderman or a shulker may teleport. Off, it stays where it stands, in daylight and in water too |
| `walks` | no | boolean | `false` | A rabbit walks the way other animals do instead of moving in hops. Only a rabbit reads it |

### Water

*entity variants*

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `breathesUnderwater` | no | boolean | `false` | Never drowns, and sinks to walk the bottom rather than swimming for the surface. It still finds its way about on the ground, so deep water it cannot walk out of will hold it |
| `swims` | no | boolean | `false` | Moves through water the way a squid or a guardian does, and never drowns. It finds its way through water rather than over ground, so it belongs in water and is stranded out of it |
| `amphibious` | no | boolean | `false` | Walks on land and swims properly in water, changing how it finds its way as it enters and leaves the water. It never drowns. Whatever it was chasing is forgotten at the water's edge, so it hesitates for a moment each time it crosses |
| `waterSlowdown` | no | float | `0.8` | How much water slows it. Higher is faster |

### Fighting

*entity variants*

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `hostile` | no | boolean | `false` | Attacks what it can reach, and fights back when hurt. A hostile variant counts as a monster to the game whatever its base, so the monster cap holds it. Peaceful clears it only when its base is a monster; any other base stays, unable to hurt a player there. It drops the animal tasks its base came with, breeding, being tempted, following a parent, an owner or its own kind, sitting |
| `targets` | no | list of entity names | the player | What it goes looking for while hostile. `minecraft:player` is understood even though the player is not a registered entity |
| `attackReach` | no | float, blocks | its size | How far a melee blow reaches. The game reaches twice the width, which is why a scaled-up creature hits from further away; this sets it outright |
| `knockback` | no | float | the base's, `0.4` | How hard its blows shove. `0` shoves not at all |
| `hitEffects` | no | boolean | `true` | Whether it puts on whatever it hits the effect the entity it copies does: a wither skeleton's wither, a cave spider's poison, a husk's hunger. Off, it hits for damage alone |
| `hitFire` | no | boolean | `true` | Whether it sets alight whatever it hits when the entity it copies would: a burning zombie, a blaze's fireball. Off, nothing it does starts a fire on its target |
| `passive` | no | boolean | `false` | Stops it attacking anything, however it normally behaves |
| `threatLeast` | no | int | `0` | The lowest threat band a player or other carrier within 128 blocks must stand in before the variant spawns naturally. `0` spawns as usual |
| `threatHostile` | no | int | `0` | The lowest threat band a player must stand in before the variant goes after them on its own. Below it the variant is docile toward that player, though it still fights back when hit. `0` attacks as usual |

`hostile` also takes away the behavior that made the creature run: an animal that avoided players or panicked when hurt does neither once it is hostile, since otherwise it would flee the thing it is meant to be attacking. It needs an entity that walks the ground, since it uses the same attack behavior vanilla gives its own mobs. A flying or swimming base is logged and left alone. `passive` works more widely, but only reaches behavior built the way vanilla builds it, a mod whose hostility is written into its own tick or damage code is not something a pack can talk out of.

### Gear, drops and experience

*entity variants*

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `lootTable` | no | `namespace:entities/<name>` | the base's | What it drops. Without this it drops whatever the entity it copies drops |
| `experience` | no | int | the base's | How much experience it drops |
| `collectsExperience` | no | boolean | `false` | Gathers experience the way a player does: orbs within eight blocks drift to it and are taken on touch, Mending on its gear is repaired first, and the points build levels on the player's own curve, kept on the mob through a save. What it kills drops its experience as if a player had made the kill, a block its `digs` task breaks drops the block's own experience, and a `block_drops` experience roll lands for it too. On death it drops seven per level up to a hundred, unless `keepInventory` is on. Objectives with the `xp` or `level` criterion carry its total and level on a row named by its UUID, so a function reads them with `execute if score` or a `scores={<objective>=N..}` selector. It spends its levels on anvil work the way a player does, see [Anvil work](#anvil-work) |
| `dropChance` | no | 0 to 1 | `0` | How likely each piece of equipment is to drop |
| `picksUpLoot` | no | boolean | `false` | Picks up what it walks over |
| `equipment` | no | object | none | `mainhand`, `offhand`, `head`, `chest`, `legs`, `feet`, each an item name |

A variant drops whatever the entity it copies drops, because the loot table is fixed in that entity's own code rather than looked up by name. `lootTable` points it at a table of your own, which you then supply at `loot_tables/entities/<name>.json` like any other.

### Special behaviors

*entity variants*

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `digs` | no | boolean | `false` | Digs through whatever stands between it and its target, with the tool in its hand: a shovel through dirt, sand and gravel, a pickaxe through stone, an axe through wood, and only what that tool's material can break, so a wooden pickaxe never opens iron ore and nothing opens obsidian short of diamond. A block takes as long as it would for a player with that tool, drops what it would, and wears the tool. Give it the tool with `equipment`; bare-handed it digs nothing, and it digs nothing where `mobGriefing` is off. It never looks for a way around: with a target it walks straight at it and digs whatever stands in the way, and where the tool cannot open the block it stands and pushes. Needs `hostile`. It takes its targets without needing to see them, since what it digs toward is by nature behind something |
| `throws` | no | boolean | `false` | Throws what it holds at its target from a distance, and if that is TNT it lights it and backs off. Needs `hostile` |
| `throwAmmo` | no | int | none | How many it has to throw. Left out, it never runs short |
| `throwReload` | no | int, seconds | `explosionFuse` | How long its hand stays empty before it draws another |
| `throwRetreat` | no | int, seconds | `explosionFuse` | How long it keeps away after a throw before turning back |
| `throwPower` | no | float | `1.0` | How hard it throws. Doubling it roughly doubles the reach |
| `throwArc` | no | float | `0.35` | How high it lobs. Higher hangs longer, near zero is a flat hurl, below zero throws downward |
| `throwReturns` | no | boolean | `false` | What it throws flies like a trident: it hits for the variant's `attackDamage`, or 8 on a base without one, then flies back into its hand the way Loyalty brings a trident back. It is never used up and is aimed at the target the way a skeleton aims, faster with `throwPower` and with less spread on harder difficulties, and the thrower stands its ground while it flies, so `throwAmmo`, `throwReload`, `throwRetreat` and `throwArc` do not apply to it. TNT is thrown as ever |
| `explodes` | no | boolean | `false` | Blows itself up next to its target, like a creeper. Needs `hostile` |
| `explosionPower` | no | number | `3.0` | How big the blast is. A creeper is 3, TNT is 4. On a creeper or ghast base, writing this or `explosionFuse` also sets the base's own blast without `explodes`: a creeper's blast size and fuse, a ghast's fireball, each in whole numbers, so a ghast given only `explosionFuse` blasts at 3 rather than its own 1 |
| `explosionFuse` | no | int, ticks | `30` | How long it hisses before going off, and a creeper base's own fuse |
| `explosionFire` | no | boolean | `false` | Leaves fires behind |
| `charges` | no | boolean | `false` | Rushes its target from a distance and hits with a heavy knockback on contact, the way a ravager does, then rests before the next run. Needs `hostile` |
| `pounces` | no | boolean | `false` | Crouches, then leaps onto its target in an arc and strikes on landing, the way a fox does. Needs `hostile` |
| `sniffs` | no | int, blocks | `0` | Hears players moving within that many blocks, walls or not, and walks to where it heard them; a sneaking or standing player is not heard, and one it then sees becomes its target. `0` does not listen. Needs `hostile` |
| `fleesWhenHurt` | no | 0.0 to 1.0 | `0` | Breaks off and runs from whoever it is fighting while its health is under that fraction, and comes back once above it. `0` never flees. Needs `hostile` |
| `sleepsByDay` | no | boolean | `false` | Finds shade by day and stands still there until night or until something attacks it. While it rests it lies on its side |
| `home` | no | int, blocks | `0` | Keeps to that many blocks around the spot it first stood on, wandering inside it and walking back when it strays. `0` roams freely |
| `patrols` | no | boolean | `false` | Walks the land in long legs with others of its kind following a leader, the way a pillager patrol does. A group that spawns together picks one leader; the rest keep within a few blocks of it, and when the leader takes a target they all do. A follower that loses its leader takes the lead itself. Needs `hostile` |
| `swoops` | no | boolean | `false` | Circles above its target and dives through it, striking on the pass, the way a phantom does. The variant is given a flying helper, so it flies while it hunts and settles to the ground when idle; it needs a base that is a creature, a parrot for one, and a bat is not. Needs `hostile` |
| `gusts` | no | boolean | `false` | Winds up and lets loose a blast of wind at its target from a distance, throwing everything near the target back and up, the way a breeze's wind charge does. Needs `hostile` |
| `gustPower` | no | float | `1.5` | How hard a gust throws. A hit from a mob is 0.4, a strong knockback enchantment about 1 |

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

### Tasks

*entity variants*

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `tasks` | no | list | none | Any task the game has, added to the variant by name at a priority of your choosing, or taken away from what its base came with. The list below |

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
| `lookAtVillager` | an iron golem | `tasks` |  | Now and then holds a poppy out to a villager and looks at it |
| `lookIdle` | any base | `tasks` |  | Looks about now and then |
| `mate` | an animal | `tasks` | `speed`, `entity` | Breeds when in love, with its own kind or the `entity` named |
| `moveIndoors` | a walking creature | `tasks` |  | Goes inside a village house at nightfall |
| `moveThroughVillage` | a walking creature | `tasks` | `speed`, `nocturnal` | Walks the village paths from door to door |
| `moveTowardsRestriction` | a walking creature | `tasks` | `speed` | Walks back toward its home spot when it strays |
| `moveTowardsTarget` | a walking creature | `tasks` | `speed`, `distance` | Closes in on a target that is far off |
| `nearestAttackableTarget` | a walking creature | `targets` | `entity`, `sight`, `nearby` | Targets the nearest of the named entity |
| `ocelotAttack` | any base | `tasks` |  | The cat's stalk and pounce |
| `ocelotSit` | a cat | `tasks` | `speed` | Sits on chests, beds and lit furnaces. Tamed ocelots became cats, so this takes a cat base |
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

### Spawning and despawning

*entity variants*

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `despawns` | no | boolean | `true` | Off, it stays even when it would normally be cleared away |
| `despawnAfter` | no | int, seconds | none | It goes quietly once it has been in the world this long, however far away anyone is |
| `persistent` | no | boolean | `false` | Never despawns |
| `ignoresSpawnRules` | no | boolean | `false` | Spawns wherever it is put, ignoring the rules it inherited |
| `spawns` | no | list of objects | none | `creatureType`, `weight`, `min` and `max`, the same shape a biome uses. `creatureType` is one of the [creature types](#value-lists), `creature` when left out, and picks the spawn list the entry joins; an entry with a type the game does not know adds nothing |
| `biomes` | no | list of biome names | every biome | Where those spawns are added, by biome id or by the name 1.12.2 showed for a vanilla biome, such as `Extreme Hills`. With neither this nor `biomeTypes`, every biome takes them, the Nether and the End included, and a biome both lists match takes each spawn once |
| `biomeTypes` | no | list of biome types | none | The same, by type word |

**A creature with a shelf life.** `despawnAfter` counts in seconds from the moment a creature first enters the world and takes it away quietly when the time is up: no death, no drops, no sound, exactly as if it had wandered off and been cleared. The clock is written into the creature itself, so it keeps running across a save and reload rather than starting over each time a chunk comes back.

It is its own thing, not a nudge to the rules `despawns` and `persistent` govern. Those two decide whether the game may clear a creature away for being far from anybody; this one is a promise that it goes at a set time regardless. A creature can be `persistent` and still have a shelf life, which is what you want for something summoned for a fight or an event that should not outlive it.

The clock runs on world time, so it pauses when nobody is playing and it does not count the minutes a chunk spent unloaded.

### Network

*entity variants*

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `trackingRange` | no | int | `80` | How far away the client is told about it |
| `trackVelocity` | no | boolean | `true` | Send its speed as well as its position. Off saves traffic on things that barely move |
| `trackingFrequency` | no | int | `3` | How often, in ticks |

## Exposures

*creatures and hazards*

`<namespace>/exposures/*.json`

The file's path is the hazard's name, and its death message comes from the lang key `death.attack.rdpl.<file name>`. Exposures load only while `load` is on and `vanillaClients` is off.

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

### Levels

*exposures*

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `effect` | yes | potion name | | The effect that marks the level on the player. Its presence drives the damage, so it should be one the pack defines for this |
| `damage` | no | half-hearts | `0` | Damage dealt every `damageInterval` ticks while the level holds. It ignores armor |
| `damageInterval` | no | ticks | `160` | How often that damage lands |
| `effects` | no | list of effects | none | Extra effects applied alongside, the same shape potion types use. Without a `duration` they follow the scan window |

The level effects last slightly past the next scan, so walking away lets them lapse on their own. Death by exposure damage reads its message from `death.attack.rdpl.<file name>`, which the pack's lang files supply.

---

# The world

## World templates

*the world*

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
| `default` | no | biome name or `void` | `void` | What fills a biome that blocking removed. `void` leaves the void biome there. A biome that is not registered is logged and the void is used. `fallback` is the same key by another name |
| `roles` | no | object of role to biome | none | Biomes filling particular roles: `ocean`, `river`, `beach`, `mushroom`, `swamp`, `hills`, `mountain`, `jungle`, `forest`, `savanna`, `sandy`, `mesa`, `snowy`, `wasteland`, `plains` and `water`, looked at in that order whatever order the file writes them in, so a blocked biome that is both ocean and snowy takes the ocean role. A role naming `void` or a biome that is not registered falls through to the next. Roles apply only in the template's `dimensions`; elsewhere a blocked biome becomes the void |
| `structures` | no | object of [structure name](#value-lists) to boolean | none | Vanilla structures switched on or off |
| `settings` | no | object | none | Config values the template sets |
| `dimensions` | no | list of dimension ids | every dimension | Which dimensions it applies to |
| `requires` | no | list of mod ids or pack namespaces | none | The template is skipped unless all are present |

`settings` uses the same key names as the config, so there is no translation table to learn.

Which template is active is decided by the `worldTemplate` config option. Left at `auto`, the highest priority pack that ships one wins, the same order everything else follows; when more than one pack ships a template, the log names them all and the one in force, since the rest do nothing, settings and all. Naming a template there picks it outright. Five are built in and can be named that way: `void`, `vanilla` (oceans, rivers, beaches, mushroom fields, swamps and hills kept as the game's own, plains everywhere else), `ocean` (rivers and beaches kept, ocean everywhere else), `plains` and `desert`. `auto` never picks a built-in one.

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

## Game rules

*the world*

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

Each key is the id of the world the rules belong to, `minecraft:overworld`, `minecraft:the_nether`, `minecraft:the_end`, a pack's own or whatever a mod uses; the 1.12.2 numbers `0`, `-1` and `1` are still taken as the vanilla three. Values are strings, as they are in the `/gamerule` command, so `"false"` rather than `false`. These are applied to new worlds. A rule a file leaves out plays the game's own default in that world, not the value the rest of the save uses, and a client with the pack reads the same rules. A dimension file carries the same rules in a `gameRules` block instead, which only ever applies to that world.

## Biomes

*the world*

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

### The biome

*biomes*

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `name` | no | string | the file name | Name shown to the player |
| `types` | no | list of biome types | guessed | Writes the biome into the tags those type words stand for, such as `forest`, `cold`, `wet` or `nether`, so other mods find it. Left out, the types are guessed from the biome the way the game guessed them: `forest` or `jungle` from three trees or more, `plains` otherwise, `hot`, `cold`, `wet` and `dry` from temperature and rainfall, `sparse` or `dense` from the tree count, `snowy` from `snow`, and `sandy`, `mushroom` or `mesa` from sand, mycelium or terracotta ground |
| `baseBiome` | no | biome name | `minecraft:plains` | An existing biome to copy settings from. One that is not a biome the game or a mod ships is logged, and plains is used |
| `requires` | no | list of mod ids or pack namespaces | none | The file is skipped unless all are present |

A biome is a data pack entry on this version, written for you under `worldgen/biome/`, and the terrain under it is the noise settings' rather than the biome's, which is why there is no `baseHeight` or `heightVariation`: the shape of the land comes from where the climate places the biome, as it does for the game's own. A 1.12.2 `id` is read and ignored.

### Climate

*biomes*

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `temperature` | no | float | `0.5` | Below 0.15 snows, above 1.0 is desert-hot |
| `rainfall` | no | float, 0 to 1 | `0.5` | How wet it is |
| `rain` | no | boolean | `true` | Whether weather happens at all |
| `snow` | no | boolean | `false` | Whether rain falls as snow. Snow only falls where the temperature is below 0.15, and this does not change the temperature, so a warmer biome still rains |

### Ground and colors

*biomes*

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `topBlock` | no | block name | grass | The surface block |
| `fillerBlock` | no | block name | dirt | Just below the surface |
| `stoneBlock` | no | block name | stone | The bulk of the ground |
| `waterColor` | no | hex color | `FFFFFF` | Water tint |
| `grassColor` | no | hex color | from the climate | Grass tint, in place of the color temperature and rainfall would give |
| `foliageColor` | no | hex color | from the climate | Leaf tint, the same way |

### Decoration and spawns

*biomes*

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `decoration` | no | object | the base biome's | Per-chunk counts, changing what the base biome already places. The names it reads are `trees`, `flowers`, `grass`, `deadbush`, `mushrooms`, `bigmushrooms`, `reeds`, `cacti`, `sand`, `gravel`, `clay` and `waterlily`, plus the switches `falls` (lakes and springs), `pumpkins`, `desertwells`, `ice` (ice spikes and ice patches), `fossils` and `rocks` (forest rocks), where any value above zero keeps the base biome's own rate and zero or below takes the feature away, and `extratreechance`, a percentage chance of one tree more, where `0` also takes away the extra tree the base biome rolls for. A count on a kind the base biome does not place adds nothing; write a worldgen entry for that. Any other name is logged and ignored |
| `spawns` | no | list of objects | vanilla list | See below |
| `keepDefaultSpawns` | no | boolean | `false` | Keep vanilla's list alongside yours |
| `spawnChance` | no | float, below 1 | `0.1` | How likely another herd is placed as the land is first made. The game keeps rolling for as long as it succeeds, so 1 never stops and fills the world until it runs out of room. Anything at or above 0.99 is refused and 0.99 used |
| `spawnRates` | no | object of `surfaceDay`, `surfaceNight`, `undergroundDay`, `undergroundNight` to a multiplier | none | How often hostile mobs spawn here, in place of the global settings. See below |

A spawn entry takes `entity` (required), `type` (`creature`, one of `monster`, `creature`, `ambient` or `water`, the underscores in a name such as `water_creature` optional), `weight` (`10`), `min` (`1`) and `max` (`min`).

`spawnRates` is about hostile mobs only, and nothing else. It takes four keys and no others: `surfaceDay` and `surfaceNight` for where the sky can be seen, `undergroundDay` and `undergroundNight` for where it cannot. Each is a multiplier on how often a hostile mob is allowed to appear, `1` is the ordinary rate, `0` stops them entirely, below 1 turns some attempts down, and above 1 lets through attempts the game would otherwise have refused, so `2` is twice as many. A key left out means the biome does not decide, and the global setting for that time and place is used instead. Anything else written here is not a key and is ignored, so a rate named after a creature type does nothing at all.

### Where it generates

*biomes*

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `placement` | no | object | none | Where it generates. See below |
| `villageType` | no | `oak`, `sandstone`, `acacia` or `spruce` | none | What a village standing here is built from: the plains, desert, savanna or taiga village. Empty builds the plains one, as it would without the key |

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `climate` | no | `icy`, `cool`, `medium`, `warm` or `desert` | none | Which climate band it joins, the same five the overworld's own biomes are dealt out by. Left out, left at a `weight` of 0 or naming a climate not listed here, the biome is registered but never placed unless a template's `roles`, a dimension's `biome` or a height band asks for it |
| `weight` | no | int | `10` | How often it is chosen against its neighbors in that band |
| `villages` | no | boolean | `false` | Villages may generate |
| `strongholds` | no | boolean | `false` | Strongholds may generate |
| `playerSpawn` | no | boolean | `false` | The world spawn may be placed here |

### Height bands

*biomes*

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `minHeight` | no | int | none | Lowest y this biome takes over as a 3D biome. Setting either height turns the biome into a band: the column keeps its own biome outside it, and inside it every 4 by 4 by 4 cell of the world reports this one |
| `maxHeight` | no | int | none | Highest y of that band |
| `replaces` | no | list of biome names | every biome | Restricts the band to columns whose own biome is named here, so an alpine band can sit over mountains and nothing else. The column's own biome is the one at its surface. Without `minHeight` or `maxHeight` it does nothing |

### Temperature by height

*biomes*

**Temperature by height.** A biome cools as it rises, which is what puts snow on mountain tops and stops rain above a line. Three `terrain` keys move that curve, which matters on a dimension whose ground sits far above or below the height the game assumes. Left unset, the game's own curve stays, so a pack that leaves them alone changes nothing.

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "biomeTemperatureCenterY": 80,
    "biomeTemperatureHeightFactor": -0.00125,
    "biomeTemperatureScaleMaxY": 320
  }
}
```

| Key | Value | Default | What it does |
| --- | --- | --- | --- |
| `biomeTemperatureCenterY` | int | `80` | The height the curve is measured from. At or below it a biome reports its own `temperature` untouched |
| `biomeTemperatureHeightFactor` | float | `-0.00125` | How much the temperature moves per block above that height, the game's own 0.05 across 40 blocks. Negative cools with altitude, positive warms |
| `biomeTemperatureScaleMaxY` | int | none | The height the curve stops at, so a world taller than the game's own does not keep cooling all the way to its ceiling. Unset, the curve runs to the top of the world |

## Dimensions

*the world*

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

### Top level

*dimensions*

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `gameRules` | no | object | none | Rules that apply only here |
| `portal` | no | object | none | A frame that opens this dimension. See [Opening a dimension with a frame](#opening-a-dimension-with-a-frame) |
| `requires` | no | list of mod ids or pack namespaces | none | The file is skipped unless all are present |

A dimension is a data pack entry on this version: the dimension type and the noise settings are written for you under the pack's namespace, so a vanilla client is told about it as it joins and travels there like any other. The dimension keeps its own save folder under the world, named after its id, and is loaded while somebody is in it, or while a `forceload` holds a chunk.

### The `terrain` block

*dimensions*

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `type` | no | `overworld`, `flat`, `void`, `nether`, `end` | `overworld` | Which of the game's generators builds it, with its noise settings copied and changed by the keys below |
| `minHeight` | no | int, a multiple of 16 | the type's own | The dimension's floor. Lower than the type's own makes a deep world under the terrain, see [The deep world](#the-deep-world) |
| `maxHeight` | no | int, a multiple of 16 | the type's own | The block above its top |
| `generatorOptions` | no | object, text or a list | none | For `overworld` and the others an object, or the text of one as 1.12.2 wrote it, of `seaLevel`, `useLavaOceans`, and `useCaves`, `useRavines`, `useDungeons`, `useLavaLakes`, `useStrongholds`, `useVillages`, `useMineShafts`, `useTemples`, `useMonuments` and `useMansions` set to false to leave those out of this dimension. For `flat` the layers, bottom up, as `"minecraft:bedrock"`, `"59*minecraft:stone"`, `"3*minecraft:dirt"`, `"minecraft:grass_block"`, which is also the default ground, or the 1.12.2 superflat text, whose biome number sets the biome and whose `decoration`, `lava_lake` and structure names are read as the overworld's `generatorOptions` reads them |
| `structures` | no | boolean | `true` | Whether vanilla structures generate |

### The `biomes` block

*dimensions*

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `source` | no | `inherit`, `single` | `inherit` | `inherit` uses the overworld's own biome map whatever the terrain type, so a `nether` or `end` dimension gets the overworld's biomes on its own ground; `single` uses one biome everywhere. A `flat` dimension holds one biome either way, the `single` one or its superflat text's |
| `biome` | when `single` | biome name | `minecraft:plains` | Which biome that is |

### The `sky` block

*dimensions*

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `hasSkyLight` | no | boolean | `true` | Whether daylight reaches it |
| `surfaceWorld` | no | boolean | `true` | Whether maps and compasses behave as in the overworld |
| `respawn` | no | boolean | `true` | Whether players respawn here |
| `respawnDimension` | no | dimension id | none | Where they respawn instead |
| `spawning` | no | boolean | `true` | Whether mobs spawn. Off stops every spawn, spawners included, whatever the `spawning` group says |
| `nether` | no | boolean | `false` | Treated as the nether for portals and ceilings |
| `beds` | no | boolean | `true` | Off, beds explode |
| `waterVaporizes` | no | boolean | `false` | Water evaporates |
| `cloudHeight` | no | int | `128` | Where clouds sit. A `cloudHeight` setting naming this dimension, or a bare one, wins over it |
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

## Portals and gates

*the world*

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
| `sound` | no | sound name | none | Played on passing. See [sound names](#value-lists) |
| `owned` | no | boolean | `true` | Only whoever built it, and those they allow, may use it. An owned portal is also immune to explosions |
| `walkIn` | no | boolean | `false` | Walking into the block travels, the way a nether portal does. Off, it is used by hand |

### Portal frames

*portals and gates*

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

*portals and gates*

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
| `sound` | no | sound name | none | Played on passing. See [sound names](#value-lists) |
| `owned` | no | boolean | `false` | Only whoever lit it, and those they allow, may use it |

The block that stands in the hole is not written by the pack. A dimension with a `portal` section is given one of its own, drawn in the game's own portal texture under `color`, walked into rather than used by hand, and unbreakable. The color multiplies the texture, the way a `tintindex` does, so `#C77DFF` keeps the nether's violet and `#4CFFB0` turns it poisonous. For a portal that is not the vanilla texture at all, write an ordinary `portal` block of your own with its own texture, drawn as a [pixel map](#textures-written-as-pixel-maps) if you like, where `tint` can ramp between two colors.

`return` decides what happens on the other side. `built` puts up the same frame, at the size the player built, and lights it, which is the way vanilla behaves. `player` builds nothing but lets the same frame be lit over there, so the way home has to be found and made. `none` refuses to light the frame in that dimension at all, and the trip is one way.

**One frame, several dimensions.** The pair of a frame and the item that lights it is what picks the dimension, so the same `standing_gate` lit with flint and steel and lit with a pack's own igniter opens two different places, each with its own color. Two dimensions claiming the same frame *and* the same item is a mistake in the pack: the second one is refused and says so in the log rather than one of them quietly winning.

Breaking any block of the frame puts the portal out, as it does in vanilla.

### Gates

*portals and gates*

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
| `safeReturn` | no | boolean | `false` | A player turned back is set down somewhere safe in the world they tried to leave: beside their bed or charged respawn anchor there when it still stands, else at that world's spawn |
| `requires` | no | list of mod ids or pack namespaces | none | The gate is skipped unless all are present |
| `portalBlocks` | no | list of block names | every portal | Limits the gate to these portal blocks, so one dimension can have a guarded door and an open one |

`unlock` takes `hold` (an item that must be held), `consume` with `consumeCount` (`1`), `craft` (an item that must have been crafted), `advancement`, and `killed` (an entity name, the gate opens for whoever slays one, so a boss can hold the key to a world) with `killedCount` (`1`) when one is not enough, tallied per player or for the whole world as the scope says. Adding `killedDrops` (an item name) makes the counted kills lay that item at the slayer's feet instead of opening the gate, and starts the counting over, so a key can be earned again and handed to somebody who never fought for it; gate on `hold` or `consume` of the same item to make it the key. `%item%`, `%mob%` and `%dim%` are filled in for you. A key a mob drops needs nothing special here: give the mob the drop and gate on `hold` or `consume`.

Gates guard the game's own dimensions too: a gate whose `dimension` is `minecraft:the_nether` stands in front of every nether portal.

## The deep world

*the world*

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

The deep world is where a pack's own worldgen entries, cave regions and hardness groups do their work: `minHeight` and `maxHeight` on an entry reach as far down as the floor goes. The game's own placed features keep to the vanilla terrain: one whose height counts from the world's bottom, the game's diamonds and lower redstone among them, still counts from -64, and a placement that would fall under -64 is left out, as it is in a world whose floor is -64. The 1.12.2 sky keys, `deepRavines`, `oreVeins`, `terrainOffset` and the rubic world itself have no twin here, since this engine's own generation already reaches from the floor to the ceiling.

## Cave regions

*the world*

`<namespace>/caveregions/*.json`

The file's path is the region's name, which a worldgen entry then names in `caveRegions`. A bare name there takes that entry's own namespace.

Paints named regions over the underground, the pack counterpart of the game's cave biomes. The underground is divided into rounded cells, `caveRegionCells` blocks wide and `caveRegionCellsY` tall, both `terrain` keys, and each cell rolls one region, or none, by weight. Everything a region does comes deterministically from the seed, so chunks agree with each other without ever writing across a border.

### Region files

*cave regions*

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
  "structureLoot": "minecraft:chests/simple_dungeon",
  "ambientSound": "minecraft:block.water.ambient",
  "soundChance": 0.02,
  "particle": "minecraft:dripping_water",
  "particleChance": 0.002
}
```

| Key | Value | Default | What it does |
| --- | --- | --- | --- |
| `weight` | int | `1` | Share of cells this region wins. `0` switches it off |
| `minHeight` | int | the world floor | Bottom of the band the region exists in |
| `maxHeight` | int | `48` | Top of that band. A cell whose center sits outside the band never picks the region |
| `waterLevel` | int | none | Pins the water table inside the region at this height, in place of the aquifers there. It stays below sea level and at least two blocks above the deep lava |
| `dimensions` | list of dimension ids | all | Which dimensions the region appears in, a pack's own included. A region with a `biome` shows it only where biomes are placed by climate: the overworld, the nether and a pack dimension that inherits the overworld's biomes |
| `floorCover` | block | none | Replaces the top block of cave floors inside the region |
| `floorChance` | 0.0 to 1.0 | `1.0` | How much of the floor gets covered |
| `ceilingCover` | block | none | Replaces cave ceiling blocks inside the region |
| `ceilingChance` | 0.0 to 1.0 | `1.0` | How much of the ceiling |
| `coverReplace` | list of blocks | anything stone-like | What the covers may replace |
| `spawns` | list | none | Mobs that spawn inside the region, the same entries a biome's `spawns` takes: `entity`, `type` (monster, creature, ambient or water), `weight` (`8`), `min` (`1`) and `max` (`4`) for the group size. A spot that can see the sky is left to the biome, like the covers are |
| `keepDefaultSpawns` | boolean | `false` | Keep the biome's own spawn list alongside the region's. Off, the region's list replaces it entirely inside the region |
| `structures` | list | none | A structure placed once per region cell, at the cell's heart, snapped to a cave floor, the way the game gives a cave biome its landmark. Entries are `namespace:name` templates, or `{ "structure": "...", "weight": 3 }` to choose between several |
| `structureChance` | 0.0 to 1.0 | `1.0` | The chance each cell of the region actually gets its structure |
| `structureLoot` | `namespace:path` | none | The loot table every chest inside a placed structure is filled from the first time it is opened |
| `biome` | biome name | none | The biome the region reports inside its volume, written as a 3D biome. Gives the region its own foliage, grass and water colors, music and ambient sounds, and lets vanilla spawn weighting read it. The surface above is untouched, since only the cells the region occupies are written. Left out, the region keeps whatever biome surrounds it and still lays its covers, structures and spawns |
| `requires` | list of mod ids or pack namespaces | none | The region is skipped unless all are present |
| `ambientSound` | sound name | none | A sound played now and then to a player standing inside the region, the way the game's biomes add their own cave sounds. Sent by the server to that player alone |
| `soundChance` | 0.0 to 1.0 | `0.0111` | The chance each tick that `ambientSound` plays |
| `particle` | particle name | none | A particle shown around a player inside the region, one of the game's particles that take no settings of their own, such as `minecraft:dripping_water`, `minecraft:happy_villager` or `minecraft:underwater`. A 1.12.2 name such as `dripWater` is converted with its pack. Only air inside the region shows it |
| `particleChance` | 0.0 to 1.0 | `0.00625` | The biomes' own particle density: each tick about 667 spots within 16 blocks are tried, and each shows the particle at this chance |

### Cells

*cave regions*

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `caveRegionCells` | int, blocks | `128` | How wide a region cell is |
| `caveRegionCellsY` | int, blocks | `64` | How tall a region cell is |
| `caveRegionPlainWeight` | int | `4` | The weight of plain, region-less underground in each cell's roll. Higher leaves more of the underground without any region: with a single region of weight 1, about a fifth of the cells get it |

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

How much of the underground stays plain is the `caveRegionPlainWeight` `terrain` key, default `4`: with a single region of weight 1, about a fifth of the cells get the region. Covers apply under a roof, so a region reaching above ground never shows on the surface. Covers work in every cave, whichever generator carved it.

### Features in a region

*cave regions*

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

---

# Generating the world

## Worldgen entries

*generating the world*

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

### What it places

*worldgen entries*

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `block` | yes | block name | | What is placed |
| `blocks` | no | list of objects | none | A weighted list, used instead of one block. See below |
| `size` | no | int or range | `8` | How many blocks one attempt places, or how large a shape with a radius is |
| `attempts` | no | int or range | `8` | How many times per chunk it tries |
| `replace` | no | list of block names or objects | `["minecraft:stone"]` | What it may replace. See below |
| `adjacent` | no | list of block names or objects | none | Only place where one of these is among the 26 blocks touching the spot. Same forms as `replace` |
| `sparse` | no | boolean | `false` | Scatters the blocks instead of packing them together |
| `shape` | no | object | `{ "type": "cluster" }` | The form it takes. See [Shapes](#shapes) |
| `spread` | no | object | `{ "type": "even" }` | Where it is put. See [Spreads](#spreads) |

### Where it may generate

*worldgen entries*

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
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
| `caveRegions` | no | list of region names | none | Only generate inside these [cave regions](#cave-regions) |
| `snap` | no | `floor` or `ceiling` | none | Move each attempt vertically to the nearest cave floor or ceiling first |
| `snapDepth` | no | int | `0` | How far past the surface `snap` then moves, down from a floor and up from a ceiling. `0` stays in the open space against the surface, `1` is the surface block itself, `2` the one behind it. What it may overwrite is still governed by `replace`, so this is how a pack bands a block just under the ground rather than on top of it |

### Surface signs and followers

*worldgen entries*

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `indicators` | no | list of `block=weight` | none | Blocks left scattered on the surface over a vein that generated, so a player can tell what lies under the ground; pick them to match the vein's contents. `empty=weight` leaves a spot bare. An entry with no weight, or a weight below 1, is logged and left out, and none is left on a village's or a city's streets and buildings |
| `indicatorCount` | no | int or range | `1` | How many surface spots each generated vein gets |
| `indicatorSpread` | no | int, blocks | `0` | How far past the vein's footprint an indicator may land |
| `then` | no | list of `name=weight` or objects | none | Worldgen entries that grow out of this one right after it generates, attached to it: the follower's origin is set just outside this vein's edge, in the direction `thenSpread` and `thenDepth` give, so the two touch. An entry is `name=weight`, or an object with `name`, `weight` and its own `spread` and `depth` (int or range) that override the vein's for that follower alone, so one list can send a diamond tip down and a branch sideways. A bare name is read in this pack's namespace, `empty=weight` queues nothing. A follower keeps its own shape, blocks, size and `replace` but skips its own attempts, chance, height band and biome gates, and may carry `then` itself, as deep as the pack wants; an entry that already generated in the same chain stops it |
| `thenCount` | no | int or range | `1` | How many different followers are picked from that list per generated vein, each entry at most once, so a count equal to the list's length grows every one of them |
| `thenSpread` | no | int, blocks | the shape's radius | How far sideways the direction a follower grows in may lean, rolled from minus this to plus this |
| `thenDepth` | no | int or range | `0` | How far down (negative) or up the direction leans. `0` with no sideways lean hangs the follower straight down |
| `prospectAs` | no | string | the file name | How a prospecting item names this entry in its reading, e.g. `Hematite` |

### Retrogen and requirements

*worldgen entries*

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `retrogen` | no | boolean | `false` | Also generate into chunks that already exist |
| `retrogenKey` | no | string | the config's key | Overrides the retrogen key for this entry alone |
| `requires` | no | list of mod ids or pack namespaces | none | The entry is skipped unless all are present |

### Weighted blocks

*worldgen entries*

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

*worldgen entries*

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

*worldgen entries*

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

*worldgen entries*

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

*generating the world*

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
| `vein` | A deposit worked out as a seeded noise field around an origin, the way Immersive Geology does it: every chunk writes its own slice of every vein whose 24-block reach touches it, so nothing cascades, and `/rdplserver vein` can tell where a vein will be before the land is made. Uses `size`, `attempts`, `rarity` and the height band; `pattern` picks the look |
| `spring` | A fluid leaking out of a cave wall: placed where rock stands above, below and on three sides with one side open, and set flowing |

### Size and form

*shapes*

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
| `middle` | geode | block name | none | A shell between the body and `outline`, the calcite of the game's amethyst geode |
| `budding` | geode | block name | none | Swapped in for body blocks that face the hollow middle, as budding amethyst is. Needs `fill` |
| `buddingChance` | geode | 0.0 to 1.0 | `0.083` | How many of those body blocks bud |
| `crystal` | geode | block name | none | Grown into the hollow beside a `budding` block, as an amethyst cluster is |
| `crystalChance` | geode | 0.0 to 1.0 | `0.35` | How many of those spots grow one |
| `crack` | geode | 0.0 to 1.0 | `0` | The chance a geode is cracked open: a tube from the middle out through every layer on one side, filled with `fill`. The game's amethyst geodes use `0.95` |

### Placement

*shapes*

| Key | Used by | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `surface` | decoration, tree | list of block names | none | What it will sit on |
| `seeSky` | decoration | boolean | `true` | Only place where the sky is visible |
| `checkStay` | decoration | boolean | `true` | Only place where the block would survive |
| `stackHeight` | decoration | int or range | `1` | How many to stack on top of each other |
| `scatterX` | decoration, tree | int | `8` | How far it wanders sideways |
| `scatterY` | decoration, tree | int | `4` | How far it wanders vertically |
| `scatterZ` | decoration, tree | int | `8` | How far it wanders sideways |
| `rarity` | any | int | none (`400` for belt) | One placement per this many chunks. On a belt this spaces the belts out; on any other shape it gates the whole entry so only one chunk in this many rolls its `attempts` at all. `field` ignores it |
| `rarityIsPerChunk` | any | boolean | `false` | Turn `rarity` into how many placements each chunk gets instead |

### Trees

*shapes*

| Key | Used by | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `log` | tree | block name | none | The trunk block |
| `leaves` | tree | block name | none | The leaf block |
| `vines` | tree | boolean | `false` | Hang vines from the leaves |

A `tree` with no `log` or `leaves` generates nothing, and says so in the log. Naming a `structure`, or several under `structures`, plants that template at each spot instead of growing one, and then no `log` or `leaves` is needed; a templated tree reads `turns`, `mirrors`, `integrity`, `lootTable` and `locateAs` exactly as an `imprint` does.

### Placing templates

*shapes*

| Key | Used by | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `structure` | imprint, tree | `namespace:name` | none | The template to place |
| `integrity` | imprint, tree | 1 to 100 | `100` | Percentage of the template's blocks that actually appear |
| `lootTable` | imprint, tree | `namespace:path` | none | The loot table every chest inside the placed template is filled from the first time it is opened, and any other container that takes one, a shulker box or a mod's crate among them. Covers `structure` and every entry of `structures`; each chest rolls its own seed |
| `structures` | imprint, tree | list | none | Several templates to choose between, one placed each time. Each entry is `{ "structure": "namespace:name", "weight": 3 }`, or a bare name for equal odds. Overrides `structure` |
| `turns` | imprint, tree | list | any | Which way round it may be placed: `none`, `quarter`, `half`, `threequarter`. Entries may carry a `weight`. Left out, all four are equally likely |
| `mirrors` | imprint, tree | list | none | Flip it as well: `none`, `leftright`, `frontback`, with optional `weight`. An entry naming its own weight is written `{ "mirror": "leftright", "weight": 2 }`, and a `turns` entry the same with `turn` |
| `at` | imprint | two ints, x and z | none | Place exactly once at those block coordinates on the surface, when that chunk generates, instead of by chance. See [Structures at exact places](#structures-at-exact-places) |
| `locateAs` | imprint, tree | string | none | Register every structure this entry places under that name, so `/rdplserver locate <name>` finds the nearest. See [Finding placed structures](#finding-placed-structures) |

For a shape no built-in type covers, `imprint` is the way: build it as an `.nbt` template and place that, with `structures` to vary it, `turns` and `mirrors` to turn it about, and `integrity` to dissolve it into something rougher than the file you drew.

### Structures at exact places

*shapes*

Vanilla structures pin to exact spots with `structureAt` in the `terrain` settings, as `structure=x,z` entries, one per line: `"structureAt": ["villages=1000,-500"]`. **The x and z are block coordinates, not chunk coordinates**, and the structure generates in the chunk that holds that block. With `terrainAdaptation` laying villages as city streets, a pinned village's well stands on that block itself, or as near to it as its district allows when the block lies within a few blocks of the district's edge; other structures, and villages laid without it, start where the game would start them in that chunk. One entry per wanted instance. Its spacing, separation, minimum spawn distance and flat-ground checks all stand aside, so the spot is the pack's responsibility, and two pins closer than a chunk apart put two structures in the same chunk. The structure seats to the ground at its chunk by the usual rules once founded.

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `structureAt` | list of `structure=x,z` | none | Pins a vanilla structure to an exact spot, one entry per wanted instance. The x and z are block coordinates, and the structure generates in the chunk that holds that block; its spacing, separation, minimum spawn distance and flat-ground checks all stand aside |

An `imprint` entry pins the same way with `"at": [x, z]` in its shape, placing exactly once at those coordinates on the surface when that chunk generates, instead of by chance. It composes with `locateAs`, so a pinned structure can also be found.

### Finding placed structures

*shapes*

An `imprint` entry with `"locateAs": "Crypt"` registers every structure it places under that name, and `/rdplserver locate Crypt` then points at the nearest one, with the name offered in tab completion; `/rdplserver goto Crypt` carries you there. Only structures that have already generated can be found, since pack structures are placed by chance as chunks are made rather than on a grid the game could predict. The names live in the world's save, so they survive restarts and work on servers. A name registered this way can also be given its own permission with `gotoPlaceLevels`, so a pack decides who may be carried to its own structures separately from the vanilla ones.

### Field and vein keys

*shapes*

| Key | Used by | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `field` | field | object | `{ "type": "speckle" }` | How the field is worked out. Same keys as a hardness group's `field`, described under [The field](#the-field): `speckle` with `chances` and `spread`, or `seeded` with `cell`, `seeds`, `reach`, `arms` and `armReach` |
| `threshold` | field, vein | 0.0 to 1.0 | `0.5` (`0.4` for vein) | How strong the field must be at a block before it is placed. Lower fills more |
| `fade` | field | int | `0` | Speckle out the top of the band instead of ending it flat: over the top this many blocks of the height range, each block's odds of placing thin out step by step, the same look the engine gives `deepStone` where it meets the world above |
| `pattern` | vein | `default`, `banded` or `tube` | `default` | The deposit's look: a warped blob, layers stacked every few blocks, or hollow tubes winding through the rock |
| `density` | vein | 0.0 to 1.0 | `1.0` | The share of qualifying blocks that are actually placed, a per-block coin |
| `rich` | vein | block name | none | Placed from `richAt` up in the field's range above `threshold`, the heart of the deposit, instead of the entry's blocks |
| `poor` | vein | block name | none | Placed in the bottom two fifths of that range, the fringe, instead of the entry's blocks; the middle is the entry's own blocks. Either tier left out places the entry's blocks there |
| `richAt` | vein | 0.0 to 1.0 | `0.88` | Where the rich tier starts in that range: `0.88` keeps the rich block to the strongest eighth of the deposit, a lower number makes the rich core fatter, `1.0` leaves no rich block at all |
| `poorAt` | vein | 0.0 to 1.0 | `0.4` | Where the entry's own blocks start: below this the `poor` block is placed, so `0.4` gives a fringe of the bottom two fifths and `0.0` leaves no poor fringe. Clamped to `richAt` |

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

### Belts

*shapes*

A `belt` is a ball far bigger than one chunk, used for stone regions rather than ore veins. Its `radius` is the ball's size, and every chunk works out for itself where the balls near it start, from the world seed and the entry's own name, so a belt comes out whole however the chunks are generated and nothing is ever written into a neighboring chunk.

```json
{
  "shape": { "type": "belt", "radius": 32, "rarity": 400 }
}
```

A belt ignores `attempts` and `spread`, since it is placed per chunk rather than per attempt. `minHeight` and `maxHeight` are the band the centers sit in, and the ball reaches `radius` beyond that band. `replace` decides what it eats, `biomes` and the temperature and rainfall limits are checked at the center, so a belt either appears in full or not at all rather than being cut off at a biome edge.

Cost grows with the cube of `radius`, and a low `rarity` multiplies it, so start at the defaults and raise the radius slowly.

### Fields

*shapes*

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

## Spreads

*generating the world*

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

## Structure maps

*generating the world*

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
| `at` | two numbers | none | Pins one copy at exact block coordinates, the way `structureAt` pins a village |
| `spacing` | number | `0` | Scatters copies on a grid this many chunks apart, jittered from the world seed. `0` scatters none, so a map with only `at` builds exactly once |
| `chance` | number | `100` | The percent of grid spots that build a copy |
| `dimensions` | list of dimension ids | all | Where the map may build, a pack's own dimensions included |
| `layers` | list | none | The layers, bottom up, each a `palette` and a `map` |

A palette names templates by registry key from a pack's `<namespace>/structures/`.

| Value | What it does |
| --- | --- |
| `"a": "mypack:keep"` | Every `a` cell of that layer places this template |
| `"a": ["mypack:wall=3", "mypack:broken=1"]` | Each `a` cell rolls the list by weight, from the world seed and the cell's spot, so two copies of the building differ but the same world always builds the same one |
| `.` | An empty cell, nothing placed |

Every copy rolls one of the four facings from the world seed and the whole building turns together, templates included, so walls that meet across cells still meet; a map turns but never mirrors. The ground layer floors at the sampled terrain surface under the building's middle, and the whole map shares that one height. A scattered map is a structure of its own to the game, placed through a structure set written for you, so each chunk builds only its own slice of the grid and a building spanning many chunks arrives without cascading generation, whatever order the chunks load in. A [village plot](#village-plots) of type `template` may also name a map as its `structure`, which makes the composite a city building.

## Village plots

*generating the world*

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

### Every plot

*village plots*

| Key | Used by | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `type` | all | `farm` or `template` | `farm` | Which kind of plot |
| `weight` | all | int | `3` | How often this plot is picked against the pack's others |
| `leastCount` | all | int | `1` | Fewest per district: a district seats plots along its streets up to a cap rolled between the lowest `leastCount` and the highest `mostCount` of the plots it may build, both raised by 16, or by a thirty-second of `villagePlotsLeast` when that is more, while a city grows. Plots behind other plots do not count |
| `mostCount` | all | int | `4` | The top of that roll |
| `width` | all | int | `7` | Size across the street |
| `height` | all | int | `4` | Height cleared above the ground |
| `depth` | all | int | `9` | Size away from the street |
| `apron` | all | int | `2` | How far the ground may be off the street's level under the plot before it is refused or slid along its street: that many blocks of fill under it, or of cut into a rise above it, and no more than that between its highest and lowest corner. A wide plot in hills needs more. Set it high and the plot terraces straight into a slope, which in the wrong place eats a mountain |
| `ground` | all | block name | `minecraft:dirt` | What is packed underneath on a slope |
| `requires` | all | list of mod ids or pack namespaces | none | The plot is left out unless all are present |

Plots are what the pack's own cities build along their streets, and every template plot also joins the game's own villages as one of their houses, entered at the middle of its front. With no plot files at all, a city builds the game's own village houses for the village type of each district's biome. `weight` decides which of your plots is chosen once a street asks for one, and `villagePieces` in the `villages` settings names the plots a template keeps, as `mypack:smithy`, `smithy` or the structure the plot builds. How the streets themselves are laid, dressed, bridged, tunneled and railed is the `village*` settings under [What each group does](#what-each-group-does).

### Farms

*village plots*

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

| Key | Used by | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `crops` | farm | list of block names | wheat | Planted one per block, at a random growth stage |
| `edge` | farm | block name | `minecraft:oak_log` | The frame around the plot |
| `soil` | farm | block name | `minecraft:farmland` | What the rows are made of |
| `water` | farm | boolean | `true` | Put a water channel between the rows |
| `rowWidth` | farm | int | `2` | How wide each row of soil is |

### Built from templates

*village plots*

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
| `structure` | template | `namespace:name` | none | The template to place, or one of your structure maps, which then sets the plot's size |
| `integrity` | template | 1 to 100 | `100` | Percentage of the template's blocks that appear |
| `lootTable` | template | `namespace:path` | none | The loot table every chest inside the placed template is filled from the first time it is opened. A plot that names a structure map is left alone |
| `villagers` | all | int | `0` | How many people the plot spawns |
| `villagerEntity` | all | `namespace:name` | a villager | Who lives there, such as an entity variant of your own |
| `villagerX` | all | int | `1` | Where they appear, across the plot |
| `villagerY` | all | int | `1` | Where they appear, above the floor |
| `villagerZ` | all | int | `1` | Where they appear, into the plot |

## City layout maps

*generating the world*

A city map draws a city's street plan on a grid, one character to a cell, and the city is laid out from the drawing instead of rolling one. Streets, plazas and plots come out as the same pieces a rolled city uses, so every street option, bridge, tunnel, subway, sewer, lamp post and plaza centerpiece applies unchanged. The world template names the map in `villageLayout`.

`<namespace>/citymaps/*.json`

```json
{
  "cell": 48,
  "settings": { "villagePathCenterBlock": "minecraft:red_concrete" },
  "palette": {
    "#": "street",
    "+": "plaza",
    "a": "alley",
    "T": ["mypack:tower_blue=1", "mypack:tower_gray=1"],
    "B": "mypack:block",
    "s": ["mypack:shop_blue=2", "mypack:shop_gray=1"],
    "g": "grow",
    "J": "junction",
    "b": "bulb",
    "E": { "kind": "elevated", "height": 8 },
    "W": { "kind": "street", "settings": { "villagePathExtraWidth": 8, "villagePathSidewalkWidth": 3 } }
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
| `settings` | object | none | City settings for this map alone, under the names a world template uses, such as `villagePathCenterBlock`. They win over the template's, and a biome's own settings still win over them |

| Value | What it does |
| --- | --- |
| `"#": "street"` | A run of street cells along a row or column becomes one street at the pack's width. Where a row run crosses a column run the junction is painted like any other. A lone street cell with no run in either axis is laid as a short stub along the row |
| `"+": "plaza"` | A plaza with its centerpiece. Runs pass through plaza cells, so streets meet at the plaza, and a plaza on a crossing stands its `villageWellStructure` centerpiece in the middle of the crossroads like a roundabout. The first plaza in the file is the city's own center, which pins the map to where the city founds; a map without one is centered there |
| `"a": "alley"` | A narrow run. Buildings front it, but it connects nothing, the alley rule as usual |
| `"J": "junction"` | A street cell laid in both directions, so a crossing stands there even where the drawing runs only one way through it. The arm across it is one cell long |
| `"b": "bulb"` | A street cell that ends in a cul-de-sac. Once a map has a bulb cell, only street ends lying in bulb cells get one; a map without any gives three dead ends in four a cul-de-sac, rolled from the world seed. A cul-de-sac is seated only where no plot, other street, railway or plaza centerpiece stands within its reach: it shrinks to fit, down to a little wider than the street, and an end with no room at any size stays a plain end |
| `"E": { "kind": "elevated", "height": 8 }` | A street cell raised onto a deck `height` blocks, 2 to 64, above the highest ground under its stretch of joined elevated cells, with a ramp of one block a row at each end. A street crossing inside the stretch rises with it, and the plots along it stay on the ground. A stretch whose deck or ramps would reach a railway or the plaza centerpiece stays at grade, with a line in the log. Any value can be written as an object this way, `kind` naming the word |
| `"W": { "kind": "street", "settings": { "villagePathExtraWidth": 8 } }` | A street laid and paved with its own street keys, which win over the map's and the template's. Its width follows its own `villagePathExtraWidth`, `villagePathSidewalkWidth` and line, and its surface, lines and sidewalks follow its own block keys, so an avenue or a lane is drawn with a mark of its own. A run takes the keys of its first cell that sets any. However wide or narrow, a drawn street stays a street: it is never taken for an alley |
| `"T": "mypack:tower"` | A plot cell, laid from that plot definition, centered in the cell and facing the nearest street |
| `"T": ["mypack:a=3", "mypack:b=1"]` | The same, rolled by weight from the world seed and the cell's spot, so the same world always lays the same plot there |
| `"g": "grow"` | Left to the rolled layout, which fills such cells and spreads outward from the map |
| `.` or `open` | Open ground, nothing laid |

Every map rolls one of the four facings from the world seed and turns whole, so a plan reads the same from any side. Streets are laid first, so a plot that would overlap a street or another plot is left open with a line in the log, and a plot name no pack provides leaves its cell open the same way. The map does not change how the pieces dress: the street keys, `villageBlocks`, the lamps and the plaza centerpiece all read as they do for a rolled city.

## Retrogen

*generating the world*

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

## Pregeneration

*generating the world*

Making a world's land ahead of time, so nobody generates chunks while playing: no chunk lag, a known size on disk, and one wait up front instead of a stuttering first hour.

The first 12 chunks around the spawn are always taken in hand, whatever a pack or the config says, because the game makes exactly that much itself before anyone joins. `pregenOnNewWorld` sets how much further to reach, and the command runs one by hand.

`/rdplserver pregen <radius>` makes every chunk within that many chunks of where it is run. `status` says how far along it is and `stop` ends it. The land is asked of the game's own chunk system, `pregenChunksInFlight` chunks at a time, one region file of 32 by 32 chunks at a time, the regions taken in rings from the middle and the chunks of a whole region along a Hilbert curve, with each region finished before the next is begun, and comes back lit and finished, so there is no lighting pass to run afterward.

While a run is going everybody is held: made a spectator, kept in place, shown a pulsing line mid-screen with the progress on the action bar, the sky held still around them, every creature and machine in every dimension frozen, and the time and weather of the dimension being made held where they were. The mode each player arrived in is written onto the player as they are held, so a save taken mid-run, a crash, or a rejoin never strands anyone as a spectator; the run's finish gives back exactly the mode it took, or the pack's `worldGameMode` when one is set, survival for `hardcore`. A client with the mod sees the view fogged while held and the logo fade in afterward; a vanilla client sees the plain hold. How far each dimension was made is saved in the world, so a finished world never runs again. The finish is told to everybody before the world's backup is taken, and a run started from the console or a command block reports its counts back there.

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
    "resetClearsInventory": true,
    "resetClearsExperience": true,
    "spawnChunkRadius": 128,
    "spawnChunkRadii": ["minecraft:overworld=64"],
    "welcomeSays": ["Welcome to Ruby World!", "minecraft:the_nether=Welcome to the Nether!"],
    "saysCard": true,
    "saysIcon": "minecraft:compass",
    "saysColor": "1E2630",
    "saysImage": "rubyworld:textures/gui/card.png",
    "saysBackground": true,
    "saysFont": "rubyworld:runes",
    "toasts": ["advancements"]
  }
}
```

### What is made

*pregeneration*

| Key | What it does | Why you would set it |
| --- | --- | --- |
| `pregenOnNewWorld` | Radius in chunks made around the spawn before anybody plays. 12 is the floor and 0 means that floor rather than nothing, since the game makes 12 chunks around the spawn on its own anyway. Raise it to reach further than the game does | Sets how far a pack reaches past the ground the game already makes |
| `pregenDimensions` | Which dimensions are made, by id, in order, each around its own spawn | Add the nether, the end, or your own dimensions |
| `pregenAllDimensions` | Every dimension the server holds instead of a list, overworld first and the rest in id order | Packs with many dimensions. Every mod's dimensions count, so mind the size |
| `pregenDimensionsWhenEntered` | These are made the first time somebody sets foot in them, holding everyone again until done | Dimensions most players never visit; the ones who never go pay nothing |
| `pregenToBorder` | Fill each dimension out to its world border instead of a radius, centered on the border | Bounded worlds |
| `pregenBorderLimit` | How far a border may reach, in chunks either way, before the run is refused. Config only, never a pack key | A guard against a runaway run; raise it only knowing the time and disk it allows |

Run it yourself before shipping, at the radius being shipped, start to finish. Chunks grow with the square of the radius, 63 either way is sixteen thousand chunks, 500 is over a million, so your test world's region folder and wall clock are the honest numbers to put in front of players. Do not ship a radius that was never run.

### How a run behaves

*pregeneration*

| Key | What it does | Why you would set it |
| --- | --- | --- |
| `pregenResume` | A stopped or interrupted run picks up where it left off. The run's dimension, center and radius are written into the save when it starts, and the count so far every ten seconds, so a crash, a power cut or a quit mid-run all resume within about ten seconds of where they died on the next load. A run stopped on purpose, by command or by the stall watchdog, stays stopped | Long runs on servers; small runs restart cheaply without it |
| `pregenChunksInFlight` | How many chunks the run asks the game for at once. More keeps the generation threads busier and the server less responsive to whoever is held watching | Turn it up on an empty server, down on one people are playing on |

### What players are shown

*pregeneration*

| Key | What it does | Why you would set it |
| --- | --- | --- |
| `pregenRunningSays`, `pregenFinishedSays`, `pregenStoppedSays` | The messages for each stage. The first may hold `%d` for the percent and, after it, `%s` for the dimension's name, or `%1$d` and `%2$s` to put them in any order. Left at their defaults they speak each player's language | Reword them in your pack's voice, name the dimension when several are made, or silence them |
| `pregenSpectatingSays` | The mid-screen hold line while land is being made. Left at its default it speaks each player's language; empty shows nothing | Keep it under about thirty-five characters or small windows clip it |
| `pregenLogo` | Where the logo stands when pregeneration finishes: `left`, `center` or `right`, above the mid-screen text, shown for a few seconds and then fading out with the fog | It is always shown; an unknown word is read as `center` |
| `welcomeSays` | The green greeting, shown on every login and after pregeneration. A bare entry is the line for everywhere; a `dimension=message` entry overrides it for that dimension and also greets every arrival there, e.g. `"minecraft:the_nether=Welcome to the Nether!"`. The dimension may also be written as 1.12.2's `0`, `-1` or `1`, and nobody arriving while land is being made is greeted. An empty message after the `=` mutes that dimension; an empty list shows nothing. Left at its default it speaks each player's language | One bare line names your pack; add dimension lines to theme each world. Keep lines under about thirty-five characters |
| `saysCard` | Shows the lines this mod says, the welcome, the land-making note a player joining mid-run gets and the end of the run (the running progress stays on the action bar), and the threat lines, as a card in the lower right corner instead of in chat. The card slides in, stays eight seconds and fades, and shows over an open screen too | Turn it on when chat is busy or the lines should read as part of the world rather than as chatter |
| `saysIcon` | An item drawn on the card, e.g. `minecraft:compass`. Empty draws none | Give the card your pack's emblem |
| `saysColor` | The card's background color as hex, e.g. `1E2630`. Empty uses a dark slate | Match your pack's palette |
| `saysImage` | A PNG from the pack's client assets, e.g. `rubyworld:textures/gui/card.png`, stretched over the card as its background and drawn over the color. Empty draws none | Give the card a painted panel; keep the image wide and short, it is stretched to whatever the text needs |
| `saysBackground` | Draws the card's panel, its border and the color stripe, and the dark backdrop behind the welcome and the notes in the middle of the screen while a player is held. Off leaves only the text, which keeps its shadow, and `saysImage` if one is set | Let the lines float over the world, or let a painted `saysImage` stand on its own |
| `saysFont` | The font the card's text is drawn in, named as `namespace:name`, e.g. `rubyworld:runes`. Empty uses the RDPL font, `resourcedatapackloader:rdpl`. The file it names is described under Cards | Give the card your pack's own lettering |
| `toasts` | Which of the game's toasts, the pop-ups in the upper right corner, are shown. `true` shows them all and `false` none; a list shows only the kinds it names: `advancements`, `recipes` for unlocked recipes, `tutorial` for the how-to hints, `system` for the game's own notices, and `other` for every toast the rest do not cover, such as other mods'. The default shows none. A player's client takes the value as they join | Keep `["advancements"]` when your pack guides players with advancements and the rest get in the way |

### Backup and map reset

*pregeneration*

| Key | What it does | Why you would set it |
| --- | --- | --- |
| `pregenBackup` | Copy the world to a pristine backup once pregeneration finishes, while the players are still held. Generation is then paid once: a later reset, or a new world on the same pack and seed, restores the copy instead of generating again, which is far faster than pregenerating twice. The copy is kept outside the save, at `rdpl-pristine/<world>` beside it, so another mod's backups do not sweep it up and it does not appear in a folder they manage. A copy whose packs no longer match the ones loaded is thrown away and kept again from the world in hand, so a pack change never resets to someone else's map | `false` |
| `pregenBackupSays` | The mid-screen line players are shown while that copy is made, with the percentage after it. Empty shows nothing and the copy is made quietly | `Pack requested world backup` |
| `resetSays` | The mid-screen line players are shown while `/rdplserver reset` or a round's end puts the map back. Empty resets quietly | `Pack requested map reset` |
| `resetSendsTo` | Where players are put by a reset: `spawn`, a position as `x,y,z` or `x,z`, where the height is one above sea level, or either behind `dimension:` to send them into another world, the dimension by id or as 1.12.2's `0`, `-1` or `1`, which is how a reset drops everyone in a lobby rather than back in the arena | `spawn` |
| `resetRuns` | A function run after a reset has cleared the map, named `namespace:path`. This is what builds the arena again, since a pack that made its map from a function can simply run it a second time. Empty runs nothing | empty |
| `resetClearsEntities` | Remove every entity that is not a player. Mobs, dropped items and experience all go, which is what leaves the map as it started | `true` |
| `resetClearsScores` | Set every objective the pack keeps back to nothing, so a new match starts from zero. Teams themselves are kept | `true` |
| `resetClearsInventory` | Empty every player's inventory, armor and off hand included, so a round starts with what the map hands out and not what the last one left. A side's `gives` is handed out again right after | `false` |
| `resetClearsExperience` | Set every player's experience back to level zero | `false` |
| `spawnChunkRadius` | How far from the spawn point, in blocks, chunks are held loaded whether or not a player is there, rounded to whole chunks: `(blocks + 8) / 16` each way, so the default `128` holds 8. As a world starts, the overworld prepares a square 4 chunks wider each way than that before the server is ready. `0` prepares and holds none. RDPL holds them with its own chunk tickets, so on 1.21.1 the `spawnChunkRadius` game rule does nothing while this key is in effect | Hold a machine or a farm at spawn running, or turn the spawn chunks off with `0` |
| `spawnChunkRadii` | A radius for the overworld written as `dimension=blocks`, as in `minecraft:overworld=64`, which overrides `spawnChunkRadius`. Only the overworld has spawn chunks, so an entry for any other dimension changes nothing | Size the spawn area in a pack that sets its radii by dimension |

---

# Game modes

## World intro

*game modes*

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

### Pages

*world intro*

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `mode` | no | `scroll` or `static` | `scroll` | Text that moves, or text that sits still until the player moves on |
| `text` | no | path to a `.txt` file | none | The words. Leave it out for a page that is just pictures |
| `background` | no | texture path | the tiled dirt background | One background |
| `backgrounds` | no | list of texture paths | none | Several, cycled. Adds to `background` if you give both |
| `interval` | no | seconds | `5.0` | How long each background is held, when there is more than one |
| `time` | no | seconds | worked out from the text | How long a scrolling page takes, start to finish. On a still page, or on the last page of any kind, it is how long until the page moves on by itself, and without it they wait for the button |
| `direction` | no | `up` or `down` | `up` | Which way scrolling text travels |
| `textScale` | no | number | `1.0` | Multiplies the font size. A `static` page wraps its text to the width of the screen, less a margin either side, and when it would still run under the buttons its text is drawn smaller, down to half, until it fits |
| `settle` | no | boolean | `false` | Finish with the last line centered rather than running clear off the screen |

### Text and timing

*world intro*

Text files go in `assets/<namespace>/texts/*.txt`. Plain text, one paragraph to a line, and blank lines are kept as blank lines. A `.md` file is read the same way, and either kind takes the formatting below. `PLAYERNAME` is swapped for the player's name, the same substitution the vanilla end poem uses.

`time` sets how long the page lasts, so the same page takes the same time whether it holds one line or twenty. Tune the reading speed by how much you put on the page. Leave `time` out and the page runs at the same speed as the vanilla credits, where more text simply takes longer.

### Text formatting

*world intro*

Intro texts take Markdown. A file with no marks shows exactly as plain text does.

```markdown
# The Long Night
## Chapter one
Welcome, **PLAYERNAME**. The *old roads* are ~~open~~ closed; type `/spawn` to go back.
- Find the **lighthouse**
- Keep the fire lit; a long item wraps under its own text, not under the bullet
  - A nested item
1. Gather wood
2. Build the gate
> The keeper wrote this before the storm.
---
![The lighthouse](mypack:textures/gui/lighthouse.png)
See [the map](https://example.com/map) for the way, and \*this\* stays plain.
```

| Mark | Written as | Shows as |
| --- | --- | --- |
| Heading | `# `, `## `, `### ` at the start of a line | Bold and larger: twice, one and a half times and one and a quarter times the text size, aligned like the body text |
| Bold | `**text**` | The font's bold cut |
| Italic | `*text*` | The font's italic cut |
| Bold italic | `***text***` | The bold cut, slanted |
| Strikethrough | `~~text~~` | Struck through |
| Code | `` `text` `` | Tinted aqua |
| Link | `[text](url)` | The text alone, underlined; not clickable |
| Runic | `{runic}text{/runic}` | The text in the rune cipher, `resourcedatapackloader:rdpl_runic`, while the rest of the line keeps its font; bold and italic inside it take the cipher's bold and italic cuts. It works in headings, list items and quotes, and an unclosed `{runic}` shows as written |
| Bullet | `- ` or `* ` at the start of a line | A bullet, with wrapped lines indented under the text; two spaces before the mark nest it one level |
| Numbered | `1. ` at the start of a line | The number as written, indented the same way |
| Quote | `> ` at the start of a line | Indented and dimmed |
| Rule | `---` on a line of its own | A horizontal line across the text width |
| Image | `![alt](namespace:textures/....png)` on a line of its own | The picture, scaled down to the text width and keeping its shape; the alt text shows if it cannot be read |
| Escape | `\` before a mark, e.g. `\*` | The mark as a plain character |

Tables and fenced code blocks (between ``` lines) are drawn as plain text, marks and all. A scrolling page's worked-out time and a still page's shrink to fit both count the laid-out height, images included. Card titles and lines, Says messages and the welcome and hold notes take the inline marks from bold to runic, one line each.

### How it plays

*world intro*

A scrolling page moves to the next one when its time is up. The last page never advances on its own, it waits. Along the bottom are **Next Page** and **Skip All**, or a single **Continue to World** on the last page. Escape does the same as Skip All. Static pages center every line. Scrolling pages keep to a fixed column, the way the credits do.

In singleplayer the world pauses behind the intro, so nothing creeps up on the player while they read. The one exception is land still being made when the intro opens: then the making carries on behind the pages, and the player stays held as a spectator until they continue to the world, even if the run finishes first. On a server the world keeps running, and a vanilla client never sees the intro at all and joins as normal. The welcome greeting waits until the pages are closed, so it is not lost behind them.

`once` is remembered in the player's saved data and survives death. `/rdplserver intro` clears it for whoever runs it, so the intro plays again the next time they join. It does not replay on the spot, which keeps it from being a way back into the entry sequence in the middle of a game.

Backgrounds are stretched to fill the window, so a 16:9 image suits a 16:9 window and a square one looks squashed. Crop the picture to shape rather than relying on the fit. `music` takes any registered sound event, vanilla or one your own pack adds through `sounds`. It does not loop, so a short track finishes and leaves quiet behind it.

If more than one pack ships an intro, their pages run end to end in pack order rather than one winning. Gate them with `requires` if you only want one.

## Teams

*game modes*

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
  "entities": ["mypack:zombie_a", "mypack:sapper_a"],
  "picks": 0,
  "picksFrom": ["players"],
  "gives": ["minecraft:iron_pickaxe", { "item": "minecraft:bread", "count": 8 }],
  "standIn": { "entity": "mypack:herobrine", "at": "23,31,0" },
  "leadRuns": "mypack:lead_chosen"
}
```

### The side

*teams*

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `name` | text | the file name | The team's name on the scoreboard, 1 to 16 characters. This is what `/team` and the other files use |
| `displayName` | text | the name | What players are shown instead of the name |
| `color` | text | `white` | One of the sixteen text colors. It tints the nameplate and is what the per-team sidebar slots key off |
| `prefix` | text | empty | Put in front of a member's name, after the color |
| `suffix` | text | empty | Put after a member's name |
| `scoreboard` | boolean | `true` | Whether the side stands as a team on the game's scoreboard. Off fields no team at all: its mobs wear the side's color in their name instead, nothing keeps them from fighting each other, and no points land on it, since scoring goes by the team |

A side is only fielded where a pack asks for one: with no `teams` folder anywhere the mod adds no team, listens for nothing, and does not offer the command. A server operator who edits a file can run `/rdplserver reload` to field the change into the running world without restarting.

### Fighting and visibility

*teams*

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `friendlyFire` | boolean | `false` | Whether members can hurt each other. Also the default of `mobFriendlyFire` |
| `mobFriendlyFire` | boolean | `friendlyFire` | Whether a side's mobs can hurt their own side with explosions and thrown TNT, which the game alone never stops. Off spares the side; on leaves it as the game has it |
| `seeFriendlyInvisibles` | boolean | `true` | Whether members see each other while invisible |
| `nameTags` | text | `always` | `always`, `never`, `hideForOtherTeams` or `hideForOwnTeam`, in any letter case |
| `deathMessages` | text | `always` | The same four words, for who is told when a member dies |
| `collision` | text | `always` | `always`, `never`, `pushOtherTeams` or `pushOwnTeam`, in any letter case |

### Who joins

*teams*

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `entities` | list | empty | Entity ids whose every spawn joins this side, such as `minecraft:zombie` or one of your own |
| `players` | list | empty | Player names that join this side as they log in |
| `spawnBox` | list | none | Six whole numbers, x y z to x y z. Anything spawning inside joins, and the corners may be given either way round |
| `joinable` | boolean | `true` | Whether a player may join with `/rdplserver team join`. Set it false for a side that is only for mobs |
| `balance` | boolean | `false` | Whether `/rdplserver team join` with no name may put a player here. Among the sides that allow it, the one with the fewest players is chosen |
| `picks` | number | `0` | How many members this side draws at random. Each round open the side lets its last draw go back where they stood and draws afresh from everything `picksFrom` names; between draws a login or a spawn from that pool fills an empty seat at once. One player out of everyone, on a side of their own, is what it is for |
| `picksFrom` | list | empty | What the draw is made from: `players` for everyone online, and entity ids for every living mob of that kind |
| `standIn` | object | none | A mob that holds the side while no player is on it: `{ "entity": "mypack:herobrine", "at": "23,31,0" }` keeps one of that entity alive at that spot in the overworld, summoning it when it is missing, and removes it the moment a player joins the side, so a game plays against the AI until a player takes the part. Checked every five seconds; the spot must be in loaded ground. In a game with a lobby (`opens.by: leader`) a stand-in is only summoned while the lobby waits and as the round opens, so one that falls stays gone through the rest of the round and its end until everyone is back in the lobby; without a lobby, a fallen stand-in is not replaced while a round that ends on `ends.lastStanding` runs |

Three ways to join, and a side may use all of them. `entities` names entity ids, and anything of that type joins as it spawns, which is how a pack gives mobs sides without touching the mobs. `spawnBox` claims a corner of the world, and anything spawning inside joins, which suits an arena where both sides use the same mob. `players` names players outright. Beyond those, a player can join with `/rdplserver team join <name>` unless the side sets `joinable` to false, and leave with `/rdplserver team leave`.

### Starting kit and spawn

*teams*

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `gives` | list | empty | Items put in a player's inventory as they join the side, an item name for one or `{ "item", "count", "unbreakable" }` for more, or for one that never wears, into any free slot and dropped at their feet when none is. Handed out again after a reset that clears inventories (`resetClearsInventory`) |
| `spawn` | text | none | `x,y,z` in the overworld where the side's players are put as a round opens, so each side starts on its own ground; without it they stay where the reset or the lobby left them |

### The lead

*teams*

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `lead` | text | `none` | How the side's lead is chosen: `none`, `first` for whoever joined the side earliest among those online, so it passes down the order of joining while one is away and comes back with them; they are told as they arrive, past the intro and any hold, and again when it passes to them, `topScore` for whoever is highest on the objective `leadOn` names, `appointed` for the player `leadIs` names, `vote` for whoever the members vote for, or `claim` for whoever claims it first. A lead is a label and a color and nothing more: it grants no power, so a lead who logs out breaks nothing |
| `leadOn` | text | empty | With `topScore`, the objective the members are ranked by. It is worked out afresh every time it is read, so it follows the score |
| `leadIs` | text | empty | With `appointed`, the player who leads |
| `leadSays` | text | `You are the current round leader` | Told to a player as the lead comes to them: as they arrive on a side they lead, as they claim it, or as a `first` lead passes to them, when it carries who left. `{side}` is the side's display name; empty tells nothing |
| `leadRuns` | text | empty | A function, `namespace:path`, run once each time the lead passes to a player: the first lead, and every hand-on after. It runs as the lead, at their position, with the permission a function an advancement rewards has, so `@s` is the lead. Checked every second; a lead who is offline is run for when they are next on. A restart decides the lead afresh |

## Scoring

*game modes*

`<namespace>/scoring/*.json`

The file name is yours to choose, only the folder is read, and several files stack. Each file is one objective.

An objective is a real objective on the game's own scoreboard, so `/scoreboard players list` reads it and it keeps its scores through a save. `criterion` is what the game counts by itself: `dummy` for a score only this pack moves, or `deathCount`, `playerKillCount`, `totalKillCount`, `health`, `air`, `armor`, `food`, `level`, `xp`, `trigger`, or any statistic written the way `/scoreboard` takes it, such as `minecraft.custom:minecraft.jump`. A 1.12.2 statistic such as `stat.jump` is read as the one it became.

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
  "opens": { "by": "leader", "lobby": "0,64,0" },
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

### The objective

*scoring*

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

### Points

*scoring*

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `points.kill` | object | empty | Entity id to points, credited to the killer's side. `minecraft:player` scores a player kill |
| `points.death` | int | `0` | Points whenever a member dies, however it died. May be negative |
| `points.ownKill` | int | `0` | Points for a kill of the killer's own side, in place of the `kill` value. 0 scores nothing for it; a negative number is a penalty |

`points` is what this mod adds on top of what the game counts, fed into the same objective so `/scoreboard` still reads it. `kill` is worth so many points per entity id killed, credited to the killer's side; `death` is worth so many whenever a member of a side dies, and may be negative. With `teamTotals` the points land on a row named after the team, which is what lets the sidebar show four sides rather than a row for every mob. `individuals` adds a row per member as well, and is off by default because a row per mob UUID reads as noise.

### How a round ends

*scoring*

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `ends.atScore` | int | `0` | The match ends the moment a side reaches this. 0 never ends on score |
| `ends.afterMinutes` | int | `0` | The match ends after this many minutes. 0 never ends on time |
| `ends.afterRounds` | int | `0` | For an objective another one `awardsTo`: the match ends once this many rounds have been awarded in all, whoever took them. 0 never ends on rounds |
| `ends.lastStanding` | boolean | `false` | The round ends when only one side is left standing. The sides in play are those with a player or a living mob on them as the round opens, two at the least; a player who dies is out, back as a spectator until the round is over, and a side whose players are all out or gone and whose mobs are all dead has fallen. The side left standing takes the round, and `awardsTo` records it for that side whatever the score. With `resets` and `opens.by: leader` the game then goes back to the lobby. A side's `standIn` is not summoned again while such a round runs |
| `ends.outSays` | text | `You are out until the round ends` | What a knocked-out player is told. Empty says nothing |
| `ends.locksTeams` | boolean | `true` | Joining a side while a round is running waits until the round is over, so nobody drops into a scored round partway |

`ends` finishes the match, either the moment a side reaches `atScore` or once `afterMinutes` have passed. The standings are then shown, ranked by the game itself: as chat, or as a card if `results` asks for one. A player without this mod is told the same standings as chat lines, so nobody is left without a result. With `resets`, that end is a round's: the standings stand for `intermissionSeconds` while a cooldown counts down on the action bar, the map resets to the welcome, and the next round opens after a five-second count. `awardsTo` hands the round to the side that led, on an objective that `carries` across the reset. A carried objective can end on its own -- `atScore` for a best-of, `afterRounds` for a fixed count -- and its standing is cleared at the reset after, so a fresh match opens.

### Between rounds

*scoring*

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `ends.resets` | boolean | `false` | Ending the round resets the map, as `resetSays` and the other reset settings under [World templates](#world-templates) describe, then a new round opens |
| `ends.intermissionSeconds` | int | `10` | How long the standings stand between the end and the reset |
| `ends.intermissionSays` | text | `Round cooldown {seconds}` | Shown on the action bar every second of the intermission after a round ends, with `{seconds}` counting down to the reset. Empty shows nothing |
| `ends.startsSays` | text | `Round starting in {seconds}` | Shown on the action bar through the five-second count that opens the next round after the reset, with `{seconds}` counting down. Empty shows nothing |

### The lobby

*scoring*

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `opens.by` | text | `auto` | `auto` opens the next round on its own, five seconds after the reset. `leader` holds the game in a lobby instead: after the reset, and when the world first loads, nothing is scored and no clock runs, sides may be joined and left freely, and the round opens only when a side's lead, or an operator, runs `/rdplserver round start`, and not while anyone is still reading the world intro; then the five-second count runs, the draws are made, and each side is put at its `spawn`. While the world waits, until the five-second count ends, players stay where they stand and can break, place, use, hit or drop nothing and take no damage, being shown the waiting line when they try, and every other living thing stands still: no AI, no movement. Commands still work, so sides can be joined and the round started |
| `opens.says` | text | `Waiting for {leader} to start the round` | Flashed mid-screen, the way the welcome is, to each player who does not lead: as they arrive in the lobby past the intro, once the welcome has shown; as the lobby opens again after a round; whenever it changes, as a lead comes or goes; and when they try something the lobby refuses. `{leader}` is the leads of every side, or `a leader` while nobody leads. Empty shows nothing |
| `opens.leaderSays` | text | `Type /rdpl round start` | Flashed in the same way and at the same moments to a player who leads a side, in place of `opens.says`. Empty shows nothing |
| `opens.lobby` | text | none | `x,y,z` in the overworld, or `dimension:x,y,z` in another world, such as `minecraft:the_nether:0,64,0`, where everyone waits while the lobby holds: every player, and every living mob on a side, is stood on a ring around that spot, each facing its middle, so they stand staring at one another. Each is given an arc as wide as it is plus two blocks, so none overlaps another, and the ring grows as more arrive; it is laid out again whenever someone joins or leaves it. The height is the floor they stand on, found within three blocks either way. Players and mobs cross into that world and back directly, with no portal built. As the round opens players go to their side's `spawn`, and a mob that is still standing is put back where it was, in its own world |
| `opens.lobbyJoins` | boolean | `false` | Put a player who logs in mid-round in the lobby as a spectator until the round ends, instead of where they logged out. Needs `opens.lobby` |
| `opens.joinsSays` | text | `Round is in progress, you can join after it ends` | What they are told. Empty says nothing |

### Resetting a round

*scoring*

```json
{
  "name": "wall",
  "opens": { "by": "leader" },
  "ends": { "lastStanding": true, "resets": true },
  "reset": {
    "lead": "now",
    "players": "vote",
    "teams": ["miners", "raiders"],
    "passPercent": 51,
    "voteSeconds": 30,
    "cooldownSeconds": 60
  }
}
```

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `reset.lead` | text | `none` | What `/rdplserver round reset` does for a side's lead while a round runs. `now` ends the round at once and resets the map; `vote` calls a vote instead; `none` gives the lead no say of its own, so the lead calls a vote like any other player where `players` allows it. An operator always resets at once |
| `reset.players` | text | `none` | `vote` lets a player on any side call a vote with `/rdplserver round reset`. `none` leaves resetting to the lead |
| `reset.teams` | list | empty | The sides whose players may call a vote. Empty is every side |
| `reset.passPercent` | int | `51` | The share of voters, 1 to 100, who must vote yes for the round to be reset. `51` is more than half, `100` is everyone |
| `reset.voteSeconds` | int | `30` | How long a vote runs, five seconds at the least. It closes early the moment its outcome is certain |
| `reset.cooldownSeconds` | int | `60` | How long after a failed vote before another can be called. A lead with `now` is not held back by it |
| `reset.leadSays` | text | `{player} reset the round` | Told to everyone when the round is reset at once, `{player}` being who reset it. Empty says nothing |
| `reset.voteSays` | text | `{player} calls a vote to reset the round: /rdpl round vote yes or no, {seconds} seconds` | Told to everyone as a vote is called, `{player}` being who called it. Empty says nothing |
| `reset.tallySays` | text | `Reset the round? {yes} yes, {no} no, {seconds}` | Shown on the action bar every second of a vote, with `{seconds}` counting down. Empty shows nothing |
| `reset.passSays` | text | `The vote passed, so the round is reset` | Told to everyone when a vote passes. Empty says nothing |
| `reset.failSays` | text | `The vote failed, so the round goes on` | Told to everyone when a vote fails. Empty says nothing |

A reset cuts the round short where it stands. The standings are shown under `The round was reset`, nobody is awarded the round, the intermission counts down, and the map resets as though the round had ended with `ends.resets`, back to the lobby where `opens.by` is `leader`. It works whether or not the round would ever end on its own, but not in the lobby, through the count that opens a round, or once a round is over and its reset is on the way; a vote still running then is dropped.

Every online player on a side votes, whatever side they are on, with `/rdplserver round vote yes` or `no`, and may change their vote while it runs. Whoever calls the vote has voted yes, and a player who has not voted when time runs out counts as no. In a pack with sides, a player on none neither calls nor votes; in a pack without sides, every online player does. The first score file whose `reset` lets anyone reset is the one used.

### Results

*scoring*

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `results.card` | boolean | `false` | Show the standings as a card rather than as chat |
| `results.title` | text | the name and `results` | The card's heading |
| `results.icon` | text | empty | An item drawn on the card, e.g. `minecraft:tnt` |
| `results.image` | text | empty | An image drawn on the card instead of an item |
| `results.background` | text | a dark slate | The card's background color |
| `results.seconds` | int | `8` | How long the card stands, at least one second |

## Raids

*game modes*

`<namespace>/raids/*.json`

The file name is yours to choose, only the folder is read, and several files stack. Each file is one raid.

A raid is the kind the game has had since 1.14, fought over one of the game's own villages. It starts when a player carrying the `omen` effect is inside a village: the effect is taken away, and a boss bar comes up for every player within `reach` of the village center. After `waveDelay` ticks the first wave arrives on a ring around the village and walks in toward the center, attacking players, villagers and iron golems on the way. Raiders never hurt or target one another, so a stray arrow or blow between two of them does nothing. The bar shows the health the wave has left, and counts the raiders once two or fewer remain. Once a wave is gone, the next one waits `waveDelay` ticks. When the last wave is gone and nothing has come back for two seconds, the raid is won; when every villager is dead or the village itself is gone after a wave has come, it is lost. Either way the bar says so for thirty seconds, and the matching function runs as every player in reach.

A raid in progress is saved with the world, and its raiders take up their march again after a reload. It stops without an ending in peaceful, after `timeout` ticks, or when no spot around the village can take a wave. A village is where the game keeps its village points, the beds, job sites and bells villagers claim: its center is the middle of those points, it reaches at least 32 blocks around it, and its villagers are the ones within that reach and four blocks of the center's height. The game's own `minecraft:bad_omen` starts the game's own raid first, so a raid names an effect of its pack's own.

While a wave is on the village, its villagers run home and stay there, as they do when the game's bell rings. Raiders break down the wooden doors in their way to get at them, twelve seconds a door, on normal and hard difficulty while `mobGriefing` is on; iron doors hold. A block of type `bell` is a village bell wherever it stands, and rings as [Bells](#bells) describes; the raid's `bell` names any other block to ring as one. Every bell in the village rings when a wave arrives, and a named block also rings when a player uses it: villagers within 48 blocks hide for fifteen seconds and raiders within 48 blocks glow for three. `minecraft:bell` stands in the game's villages already and can be named, and it keeps ringing the game's way as well; a pack's own bell block is placed in the village through an NBT structure, as a plot or as the plaza centerpiece.

```json
{
  "omen": "mypack:bad_omen",
  "name": "Raid",
  "color": "red",
  "waveDelay": 300,
  "spawnDistance": 32,
  "sound": "mypack:raid_horn",
  "wins": "mypack:raid_won",
  "loses": "mypack:raid_lost",
  "bell": "minecraft:bell",
  "waves": [
    [ { "entity": "minecraft:vindicator", "count": 2 }, { "entity": "mypack:raider", "count": { "min": 1, "max": 3 } } ],
    [ { "entity": "minecraft:evoker" }, { "entity": "minecraft:vindicator", "count": 4 } ]
  ]
}
```

### The raid

*raids*

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `omen` | effect name | none, required | The effect that starts the raid when its holder is inside a village. Any registered effect will do, a pack's own potion included |
| `name` | text | `Raid` | The boss bar's title |
| `color` | text | `red` | The bar's color: `pink`, `blue`, `red`, `green`, `yellow`, `purple` or `white` |
| `waves` | list of waves | none, required | Each wave is a list of groups, and the waves come in order |
| `waveDelay` | int | `300` | Ticks before the first wave, and between the end of a wave and the next |
| `spawnDistance` | int | `32` | How far from the village center a wave arrives. The first tries are at twice this, then at this, then inside the village |
| `reach` | int | `96` | Players within this many blocks of the center see the bar, and the ending function runs as them. A raider that strays sixteen blocks past it leaves the raid |
| `timeout` | int | `48000` | Ticks after which an unfinished raid stops without an ending. `0` never stops it |
| `sound` | sound name | none | Played to every player in reach, from the side the wave comes from, as each wave arrives |
| `wins` | function | none | Runs as every player in reach when the raid is won |
| `loses` | function | none | Runs as every player in reach when the raid is lost |
| `bell` | block name or list | none | Other blocks that ring as a bell, when a player uses them and whenever a wave arrives. A block of type `bell` rings without being named |

### A group

*raids*

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `entity` | entity name | none, required | What comes. An entity variant keeps all of its own behavior and gains the march |
| `count` | int or `{ "min", "max" }` | `1` | How many come |

---

## Cards

*game modes*

`<namespace>/cards/*.json`

The file name is yours to choose, only the folder is read, and several files stack. Each file is one rule, and its id is `<namespace>:<file name>`. A rule waits for a trigger, checks its `when`, and shows a card to its audience; it can run a function as well. Nothing needs to be on the client: a player without the mod gets a corner card as chat lines and a center card as a title.

Every message this mod says itself is a built-in rule, listed below. A pack changes one by writing a file with that id, `rdpl/cards/<name>.json`, which needs no trigger: whatever it leaves out stays as it is today, and `{text}` stands for the message the mod would have said. A pack that writes none of them sees every message as before.

```json
{
  "trigger": "biome_enter",
  "biomes": ["minecraft:desert", "#minecraft:is_badlands"],
  "title": "The Dry Lands",
  "lines": ["Day {day}, {player}.", "Water is scarce from here on."],
  "style": "center",
  "image": "mypack:textures/gui/desert_card.png",
  "color": "3A2A10",
  "ticks": 120,
  "when": { "timeFrom": 0, "timeTo": 12000 },
  "repeat": "once_per_player"
}
```

```json
{
  "lines": ["{text}", "Speak to the gatekeeper for more."],
  "icon": "minecraft:ender_eye",
  "cooldown": 30
}
```

The second file, saved as `rdpl/cards/gate_blocked.json`, turns the red action-bar line a closed gate shows into a card with an icon and a second line, and shows it at most once every thirty seconds.

```json
{
  "trigger": "first_join",
  "title": "Ruby World",
  "lines": ["Welcome, {player}."],
  "style": "center",
  "background": false,
  "font": "mypack:runes"
}
```

The third greets a player on the first join with a center card that has no panel behind it, only its text and the text's shadow, drawn in the pack's own font.

### Triggers

*cards*

| Trigger | Needs | Fires when |
| --- | --- | --- |
| `command` | nothing | `/rdplserver card <rule> [players]` is run. The command skips `when`, `repeat` and `cooldown`, and still runs `runs`. Any rule can be shown this way, whatever its trigger |
| `first_join` | nothing | A player joins the world for the first time |
| `dimension_enter` | `dimension` | A player arrives in that dimension |
| `biome_enter` | `biomes` | A player walks into one of those biomes from somewhere else |
| `structure_enter` | `structures` | A player walks into one of those structures from outside it |
| `advancement` | `advancement` | A player earns that advancement |
| `time_of_day` | `time` | The day clock passes that tick, `0` to `23999`, while players are in the dimension. A clock set by a command or a bed does not count |
| `day` | nothing, or `day` | A new day begins in the dimension; with `day`, only that day |
| `craft` | `item` | A player crafts that item |
| `pickup` | `item` | A player picks that item up |
| `kill` | `entity` | A player kills that entity, or the `count`th one of it |
| `respawn` | nothing | A player respawns after dying |
| `death` | nothing | A player dies |
| `y_level` | `below` or `above` | A player goes below or above that height |
| `play_time` | `minutes` | A player's time in the world reaches that many minutes, counted from when the world intro closes, or from the join when no intro is shown to them |
| `score` | `objective` | A player's score in that objective reaches `score` |

Biome, structure, height, play time and score are checked once a second for each player, and fire on the change from outside to inside, never on the first check after a join. A `time_of_day` or `day` rule whose audience is not `player` fires once for the dimension instead of once for each player in it.

A card that fires while a player still has the world intro open waits and shows when the intro closes, whatever its trigger, the `command` one included. It is dropped if the player leaves before then.

### Trigger settings

*cards*

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `trigger` | text | none, required | One of the triggers above. A built-in rule takes none |
| `dimension` | text | none | A dimension id such as `minecraft:the_nether`; an id with no namespace is read as `minecraft:`. For `dimension_enter` it is the one entered; for every other trigger it limits the rule to players in that dimension |
| `biomes` | list | none | Biome ids such as `minecraft:desert`, or `#tag` for a biome tag such as `#minecraft:is_ocean` |
| `structures` | list | none | Structure ids such as `minecraft:village_plains`, or `#tag` for a structure tag such as `#minecraft:village`, which count while the player is inside a piece of one; or the name of a structure a pack places through `structures`, which counts within `radius` of where it was placed |
| `radius` | int | `32` | How close counts as inside a pack's own structure |
| `advancement` | text | none | The advancement id |
| `item` | text | none | The item, written as elsewhere in a pack, such as `minecraft:diamond_sword` |
| `entity` | text | none | The entity id, such as `minecraft:zombie` |
| `count` | int | `1` | For `kill`: how many kills it takes. The count starts again after the rule fires |
| `below`, `above` | int | none | For `y_level`: the height to go below or above |
| `time` | int | `0` | For `time_of_day`: the tick of the day |
| `day` | int | none | For `day`: the one day to fire on. Without it, every day |
| `minutes` | int | none | For `play_time` |
| `objective`, `score` | text, int | none, `1` | For `score`: the objective and the value to reach |
| `requires` | list of mod ids or pack namespaces | none | The file is skipped unless all are present |

### When

*cards*

`when` holds conditions that must all be true at the moment the trigger fires.

| Setting | Type | What it checks |
| --- | --- | --- |
| `biomes` | list | The player stands in one of these biomes, written as for the trigger |
| `timeFrom`, `timeTo` | int | The day clock is inside this window, which may run past midnight, such as `13000` to `1000` |
| `dayAtLeast` | int | The day number is at least this |
| `advancement` | text | The player has this advancement |
| `gameMode` | text | The player is in this game mode: `survival`, `creative`, `adventure` or `spectator` |
| `team` | text | The player is on this scoreboard team |
| `objective`, `scoreAtLeast` | text, int | The player's score in the objective is at least this |

### The card

*cards*

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `title` | text | none | The first line, drawn larger on a center card |
| `lines` | list | none | Up to sixteen lines. A rule needs a title or lines, apart from a built-in one. `{player}`, `{dim}`, `{biome}` and `{day}` are filled in; `{text}` is the built-in message, and on a line of its own gives every line of it |
| `style` | text | `corner` | `corner` is the card in the lower right that `saysCard` shows; `center` is a card in the middle of the screen; `chat` is chat lines; `bar` is the action bar |
| `icon` | text | `saysIcon` | An item drawn on a corner card. Empty draws none |
| `color` | text | `saysColor` | The card's background color as hex |
| `image` | text | `saysImage` | A PNG from the pack's client assets, stretched over the card as its background |
| `background` | boolean | `saysBackground` | `false` drops the panel, its border and the color stripe; the text keeps its shadow, and an `image` still draws |
| `font` | text | `saysFont` | The font the card's text is drawn in, as `namespace:name`. Empty uses the RDPL font |
| `ticks` | int | `160` | How long the card stays, fading included |
| `audience` | text | `player` | Who sees it: `player`, `everyone`, `dimension` (everyone in the player's dimension) or `team` (the player's scoreboard team) |
| `repeat` | text | `always` | `always`, `once_per_player`, `once_per_world` or `once_per_session` (again after the player logs back in) |
| `cooldown` | int | `0` | Seconds before the rule fires again for the same player |
| `runs` | text | none | A function run as the player when the rule fires |

A corner card goes to chat when `saysCard` is off. What a player has been shown is kept with the player, so it survives death and moving between dimensions; `once_per_world` is kept with the world.

The RDPL font, `resourcedatapackloader:rdpl`, is the default for all text: cards, Says messages, the welcome and hold notes, the world intro, and the game's own menus, chat, HUD and tooltips. Its bold and italic cuts are `resourcedatapackloader:rdpl_bold` and `resourcedatapackloader:rdpl_italic`. The enchanting table's lettering stays the game's.

RDPL ships these fonts and characters. A card, note or intro `font` can name an RDPL font by its short name, or by its full id:

| Name | What it draws |
| --- | --- |
| `rdpl` (or `resourcedatapackloader:rdpl`) | The RDPL font, with Cyrillic (U+0400 to U+04FF) and the runic alphabet (U+16A0 to U+16F8) |
| `rdpl_runic` (or `resourcedatapackloader:rdpl_runic`) | A rune cipher: the letters A to Z and a to z draw as runes, and every other character draws in the RDPL font. A bold run draws in `rdpl_runic_bold` and an italic run in `rdpl_runic_italic` |
| Runes, U+16A0 to U+16F8 | Written as the runic characters themselves (ᚠ ᚢ ᚦ ᚨ ᚱ ᚲ), in any text the RDPL font draws, chat included; bold and italic runs keep their cut |

A card font is a font definition at `assets/<namespace>/font/<name>.json`, in the same format as the game's own fonts, and the card is sized to that font's widths. A `bitmap` provider whose `file` is `<namespace>:font/<name>.png` and whose `chars` are the sixteen rows of the game's `ascii.png` reads the same PNG the 1.12.2 build uses at `assets/<namespace>/textures/font/<name>.png`, so one pack draws the same lettering on all three versions. `minecraft:default` names the game's font. A font no pack holds falls back to the game's font, with one warning in `rdpl.log`.

A pack changes the RDPL font by shipping its own `assets/resourcedatapackloader/font/rdpl.json`, or the PNGs under `assets/resourcedatapackloader/textures/font/` that it draws from; either replaces it everywhere, the game's text included. The game's text uses the RDPL font because the mod ships `assets/minecraft/font/default.json` with the RDPL font first and the game's own fonts after it for every other character. A pack's `assets/minecraft/font/default.json` is read before the mod's, so a copy of vanilla's gives the game's text back its own font; RDPL's own text keeps the RDPL font unless `saysFont` is `minecraft:default`.

Card titles and lines, Says messages and the welcome and hold notes take the inline marks in the table under World intro, Text formatting: bold, italic, bold italic, strikethrough, code, links and runic spans. A bold run draws in the font's `_bold` cut and an italic run in its `_italic` cut; for a font without that cut the run takes the game's bold or italic style, and the card is sized to the runs as drawn. Players without the mod get the same marks as chat formatting, and a runic span as its plain letters.

### Built-in rules

*cards*

| Id | The message | Its text comes from |
| --- | --- | --- |
| `rdpl:gate_unlocked` | A gate opens | `unlockedMessage` in [Gates](#gates) |
| `rdpl:gate_blocked` | A closed gate turns a player back, on the action bar | `blockedMessage` in [Gates](#gates) |
| `rdpl:team_joined` | A player joins a side | the side's `displayName` |
| `rdpl:team_lead` | The lead of a side comes to a player | `leadSays` in [Teams](#teams) |
| `rdpl:team_picked` | A player is picked for a side | the side's `displayName` |
| `rdpl:team_round_ended` | The round ended, so a player is moved to a side | the side's `displayName` |
| `rdpl:lobby_joins` | A player who logs in mid-round is sent to the lobby | `opens.joinsSays` in [The lobby](#the-lobby) |
| `rdpl:lobby_note` | The mid-screen lobby line | `opens.says`, `opens.leaderSays` in [The lobby](#the-lobby) |
| `rdpl:scoring_results` | The standings at the end of a round, to each player | `results.card`, `results.title`, `results.icon`, `results.image`, `results.background`, `results.seconds` in [Results](#results) |
| `rdpl:scoring_out` | A knocked-out player | `ends.outSays` in [How a round ends](#how-a-round-ends) |
| `rdpl:reset_lead` | The lead resets the round | `reset.leadSays` in [Resetting a round](#resetting-a-round) |
| `rdpl:reset_vote` | A reset vote is called | `reset.voteSays` |
| `rdpl:reset_pass` | The vote passes | `reset.passSays` |
| `rdpl:reset_fail` | The vote fails | `reset.failSays` |
| `rdpl:anvil_waits` | An anvil's work waits on an advancement | [Anvil work](#anvil-work) |
| `rdpl:threat` | A player's threat band changes | `threatSays` |
| `rdpl:prospect` | Each line a prospecting find reports | the find |
| `rdpl:prospect_none` | Prospecting found nothing | the language file |
| `rdpl:pregen_ended` | Pregeneration finishes or stops | `pregenFinishedSays`, `pregenStoppedSays` in [Pregeneration](#pregeneration) |
| `rdpl:pregen_running` | The progress line a player sees on joining during pregeneration | `pregenRunningSays` |

`welcomeSays` is not a rule and keeps its logo; a `first_join` or `dimension_enter` rule adds to it. The action-bar countdowns and tallies of a round stay as their settings make them.

---

# Control

## The control layer

*control*

Everything that stops or changes generation is grouped, and each group has one key in the config's `control` category with three values:

| Value | What it means |
| --- | --- |
| `default` | The pack decides. Config values are the fallback |
| `global` | The config wins. Pack sections are ignored |
| `off` | The group is disabled entirely and no pack can enable it |

The groups are `ores`, `biomes`, `structures`, `spawning`, `bedrock`, `voidWorld`, `recipes`, `terrain`, `replacements`, `villages`, `entities`, `chunks`, `blastPlaster`, `commands` and `server`.

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

*control*

Every setting below is read through its group, so the group's `control` key decides whether a pack or the config has the last word. A setting a pack may set appears in a world template's `settings` block under the same name; one marked **config only** is read from the config alone, and a pack writing it is warned and ignored. Defaults are the config's.

### Ores

*what each group does*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "blockOres": true,
    "logBlockedOres": true,
    "oreWhitelist": ["minecraft", "mypack"],
    "prospectItems": ["minecraft:compass=iron_vein|coal_seam", "mypack:dowsing_rod=*,12"],
    "prospectItemsAreBlacklist": false,
    "prospectDrops": false,
    "prospectSlow": 2,
    "prospectWear": 2,
    "oreTypes": ["COAL", "IRON"],
    "oreTypesAreBlacklist": true,
    "blockOreDimensions": ["minecraft:overworld", "minecraft:the_nether"],
    "blockOreDimensionsAreBlacklist": false
  }
}
```

`control.ores` decides this group. Blocking ore generation by mod and by ore type.

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `blockOres` | boolean | `false` | Stop every mod, and Minecraft itself, from generating ores. Only the mods in oreWhitelist still generate. An ore is a placed feature built on the game's ore or scattered ore feature, the ones 1.12.2 raised its ore event for, which covers Minecraft's and most mods' ores, dirt, gravel and the stone kinds. A pack's own worldgen entries are never blocked, by this or by oreTypes |
| `logBlockedOres` | boolean | `true` | Logs the first time each mod and ore type is turned away |
| `oreWhitelist` | list of mod ids | `["minecraft"]` | The mods still allowed to generate ore while `blockOres` is on |
| `prospectItems` | list | empty | Items that prospect for vein shaped worldgen entries when a sneaking player breaks a block with one, as item=entry\|entry[,radius in chunks] or item=*[,radius], e.g. minecraft:compass=iron_vein\|coal_seam or mypack:rod=*,12. The reading names the ore and a compass direction |
| `prospectItemsAreBlacklist` | boolean | `false` | On, each item's list is the entries it does not read |
| `prospectDrops` | boolean | `false` | On, a block broken in prospecting mode still drops and gives experience. Off, the sample is spent |
| `prospectSlow` | int, 1 to 100 | `2` | How many times longer a sneaking player with a tagged item takes to break a block. `1` is normal speed |
| `prospectWear` | int, 2 to 1000 | `2` | How many times the normal wear a prospecting break costs the tool. `2`, double, is the least allowed, and an item with no durability pays nothing |
| `oreTypes` | list | empty | Ore types this applies to, whoever generates them and whatever the whitelist says. Known types: COAL, IRON, COPPER, GOLD, REDSTONE, DIAMOND, LAPIS, EMERALD, QUARTZ, DIRT, GRAVEL, DIORITE, GRANITE, ANDESITE, TUFF, CLAY, SILVERFISH, CUSTOM for any other ore |
| `oreTypesAreBlacklist` | boolean | `true` | On, the types in `oreTypes` are the ones blocked. Off, only those types generate |
| `blockOreDimensions` | list | empty | The dimensions ore blocking applies to, empty meaning every one. A dimension outside the scope is not touched at all, so another mod's ores generate there while the overworld stays blocked |
| `blockOreDimensionsAreBlacklist` | boolean | `false` | On, the dimensions listed are the ones left alone |

### Biomes

*what each group does*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "logBlockedBiomes": true,
    "blockBiomes": true,
    "biomeWhitelist": ["minecraft", "mypack"],
    "biomeNames": ["minecraft:badlands", "minecraft:wooded_badlands"],
    "biomeNamesAreBlacklist": true,
    "blockBiomeDimensions": ["minecraft:overworld"],
    "blockBiomeDimensionsAreBlacklist": false
  }
}
```

`control.biomes` decides this group. Blocking biomes by mod and by name, and what replaces them.

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `logBlockedBiomes` | boolean | `true` | Logs a per-mod count of which biomes were blocked |
| `blockBiomes` | boolean | `false` | Stop every biome from generating except the mods in biomeWhitelist. Blocked biomes become the void biome, or what the world template's roles and fallback name. Blocking every biome while the world template is void, with no roles and a void default, makes the voidWorldDimensions a void world |
| `biomeWhitelist` | list of mod ids | `["minecraft"]` | The mods whose biomes still generate while `blockBiomes` is on. A pack biome uses the pack's namespace |
| `biomeNames` | list | empty | Biomes this applies to, by id such as minecraft:birch_forest or by the name the game shows, such as Birch Forest. As a blacklist they are blocked whoever owns them. As a whitelist, a listed biome still needs its mod in biomeWhitelist while blockBiomes is on |
| `biomeNamesAreBlacklist` | boolean | `true` | On, the names in `biomeNames` are blocked. Off, only those names generate |
| `blockBiomeDimensions` | list | `["minecraft:overworld"]` | The dimensions biome blocking applies to. Empty means every one |
| `blockBiomeDimensionsAreBlacklist` | boolean | `false` | On, blocking skips the dimensions listed. Off, it applies only to them |

### Generators

*what each group does*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "blockWorldGenerators": true,
    "generatorWhitelist": ["minecraft", "mypack"],
    "blockedGenerators": ["tconstruct"],
    "blockGeneratorDimensions": ["minecraft:overworld"],
    "blockGeneratorDimensionsAreBlacklist": false,
    "generatorTypes": ["ores", "lakes"],
    "generatorTypesAreBlacklist": true,
    "generatorTypeMap": ["mymod=ores", "sky_island=structures"],
    "logBlockedGenerators": true
  }
}
```

`control.generators` decides this group. Blocking other mods' world generation by mod and by what it makes. A generator is a placed feature, owned by its id's namespace; Minecraft's own features, this mod's and a pack's worldgen entries are never blocked.

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `blockWorldGenerators` | boolean | `false` | Stop every mod generating through its own placed features, which is how mods add slime islands, cave crystals and the like. Only the mods in generatorWhitelist still generate |
| `generatorWhitelist` | list of mod ids | `["minecraft"]` | The mods still allowed to generate while `blockWorldGenerators` is on |
| `blockedGenerators` | list of mod ids or id parts | empty | Individual generators blocked outright, whatever the whitelist says, by mod id or by part of a placed feature id |
| `blockGeneratorDimensions` | list | `["minecraft:overworld"]` | The dimensions generator blocking applies to. Empty means every one |
| `blockGeneratorDimensionsAreBlacklist` | boolean | `false` | On, blocking skips the dimensions listed. Off, it applies only to them |
| `generatorTypes` | list | empty | Types this applies to, whoever owns the generator and whatever the whitelist says: `ores`, `structures`, `flora`, `lakes`, `terrain`, or `unknown` for the ones nothing matched. The type comes from words in the feature's id, so `crystal_ore` is ores and `slime_island` is structures |
| `generatorTypesAreBlacklist` | boolean | `true` | On, the types in `generatorTypes` are the ones blocked. Off, only those types generate |
| `generatorTypeMap` | list of `pattern=type` | empty | Types for generators the id does not describe, the pattern being a mod id or part of a feature id, e.g. mymod=ores. Mapped entries are checked before the built-in words, so they also correct one the words read the wrong way |
| `logBlockedGenerators` | boolean | `true` | Logs each generator with the type it was given the first time it is blocked. `/rdplserver generators` shows the running totals by mod and type |

### Replacements

*what each group does*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "blockReplacements": ["minecraft:andesite=minecraft:stone", "minecraft:oak_log[axis=y]=minecraft:spruce_log[axis=y]"],
    "blockReplacementDimensions": ["minecraft:overworld"],
    "blockReplacementDimensionsAreBlacklist": false,
    "blockReplacementMinHeight": -64,
    "blockReplacementMaxHeight": 128,
    "blockReplacementKey": "cleanup_v1",
    "logBlockReplacements": true
  }
}
```

`control.replacements` decides this group. Block replacement in chunks that already exist.

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `blockReplacements` | list | empty | Blocks swapped out of chunks as they load, written as block=block with an optional state on either side, such as minecraft:andesite=minecraft:stone or minecraft:oak_log[axis=y]=minecraft:spruce_log[axis=y]. Every chunk is done once, new ones included |
| `blockReplacementDimensions` | list | empty | The dimensions this applies to. Empty means every one |
| `blockReplacementDimensionsAreBlacklist` | boolean | `false` | On, replacement skips the dimensions listed. Off, it applies only to them |
| `blockReplacementMinHeight` | int, -2032 to 2031 | `-64` | The lowest y it looks at |
| `blockReplacementMaxHeight` | int, -2032 to 2031 | `319` | The highest y it looks at |
| `blockReplacementKey` | string | `0000` | Change it and every chunk goes through replacement again |
| `logBlockReplacements` | boolean | `true` | Logs the first time each replacement is made, and a total when a world catches up |

### Villages and cities

*what each group does*

`control.villages` decides this group. The city and village streets a pack lays: their shape, dress, bridges, tunnels, rails, plots and plaza.

#### Village roads

*villages and cities*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "villagePathBlock": "minecraft:stone_bricks",
    "villagePathExtraWidth": 1,
    "villageBlockSizes": ["32=3", "64=1"],
    "villageCitySpacing": 4,
    "villagePathAlleyBlock": "minecraft:gravel",
    "villagePathAlleyChance": 25,
    "villagePathMinimumWidth": 0,
    "villagePathFlatRun": 6,
    "villagePlotsLeast": 12,
    "villagePlotsMost": 30,
    "villagePlotsBackRow": true,
    "villageTieStreets": true,
    "villageLayout": "mypack:downtown",
    "villagePathCenterBlock": "minecraft:quartz_block",
    "villagePathCenterDash": 2,
    "villagePathLineBlock": "minecraft:smooth_stone_slab",
    "villagePathSidewalkBlock": "minecraft:stone_bricks",
    "villagePathSidewalkWidth": 2,
    "villagePathLampBlock": "minecraft:iron_bars",
    "villagePathLampHeight": 3,
    "villagePathLampTopBlock": "minecraft:player_head",
    "villagePathLampSideBlock": "",
    "villagePathLampStructure": "",
    "villageWellStructure": ["mypack:plaza_spire=3", "mypack:fountain=1", "empty=1"],
    "villagePathDeadEnds": ["barrier", "sidewalk"],
    "villagePathIntersects": ["mypack:crosswalk"]
  }
}
```

**Mixing blocks.** Some block settings take a mix instead of one block: blocks separated by commas, each followed by a space and a weight, as in `"minecraft:stone_bricks 3, minecraft:cobblestone 1"`. A block with no weight counts once. Each block placed rolls the mix from the world seed and its spot, so the same world always builds the same pattern. The settings that take a mix are `villagePathVergeBlock`, `villagePathVergeWaterBlock`, `villagePathTunnelBlock`, `villagePathBridgeFrameBlock`, `villagePathBridgeFrameTopBlock`, `villageRailTunnelBlock`, `villageRailDeckBlock`, `villageRailSupportBlock`, `villageRailBarrierBlock`, `villageRailBridgeFrameBlock`, `villageRailBridgeFrameTopBlock`, `villageSubwayTunnelBlock`, `villageSubwayPlatformBlock`, `villageSubwayRailingBlock`, `villageSubwayBenchEndBlock` and `villageSewerMossBlock`. Every other block setting uses the first block of a mix throughout.

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `villagePathBlock` | text | empty | The road surface when terrainAdaptation lays city roads. Empty keeps the block the biome would use, sandstone over sand, terracotta over badlands, dirt path over earth |
| `villagePathExtraWidth` | int, 0 or more | `0` | Extra blocks of road width on each side beyond the usual 3, when terrainAdaptation lays the roads. Widens the streets themselves, so the blocks between them stand back from wide roads |
| `villageBlockSizes` | list of `size=weight` | empty | How deep the blocks between a city's parallel streets are, rolled once per district from its plaza position. Empty sizes every block to the largest plot the pack ships |
| `villageCitySpacing` | int, 0 to 256 | `16` | How far apart city districts are seeded, in districts sized from the plots (twice the largest plot, plus a plaza and a street each side, rounded up to 16 blocks, at least 96): one district in every square of this many carries a city, and at 1 every district is one, a plaza with the well at its center and streets out of it that join the next district's. 0 seeds none. When the pack does not set it, `structureSpacing` villages=chunks sets it, at least 9 chunks. A square founds its city on its flattest district, no more than 10 blocks from high to low, in a village biome (`structureBiomes` villages= chooses those), and not where a woodland mansion could start; `structureSeparation`, `structureMinDistanceFromSpawn` and `structureMost` villages= hold cities apart as they held villages, and `structureAt` villages=x,z founds cities only where it pins them. A city grows only onto districts within reach of its first well whose ground rises no more than 6 blocks and stays above the water line, and a city of two or fewer plots and wells is not built |
| `villagePathAlleyBlock` | block | empty | The surface of an alley, a road too narrow to carry lines and sidewalks. An alley runs between the sidewalks of the streets it meets and carries none of its own, and no crossing is painted where it meets a street. Empty lays alleys with the road block |
| `villagePathAlleyChance` | int, 0 or more | `0` | The percent chance a street is laid as an alley rather than at its full width. 0 lays no alleys |
| `villagePathMinimumWidth` | int, 0 or more | `0` | The narrowest street allowed. A street that would be laid narrower than this is not laid at all, and the district lays out around the gap. 0 never refuses |
| `villagePathFlatRun` | int, 0 or more | `6` | Streets hold each grade for at least this many blocks before stepping, anchored to world coordinates so segments agree across pieces. 0 or 1 lets a street step every block |
| `villagePlotsLeast` | int, 0 or more | `0` | How many plots a city grows to: districts are added ring by ring around its center until they hold at least this many, never more than villagePlotsMost. 0 lays the center district alone |
| `villagePlotsMost` | int, 0 or more | `0` | The most plots a city may hold: growth stops before the district that would pass it and no district seats more, and a district that reaches it leaves out the alleys no plot fronts. 0 sets no ceiling |
| `villagePlotsBackRow` | boolean | `true` | Once the village has grown, a second pass seats a plot directly behind every plot that fronts a street, turned to face it, with the same roll and the same room test, so the inside of a block between two streets is built rather than left bare |
| `villageTieStreets` | boolean | `true` | On, a district that cannot grow its streets to the standing village gets a straight tie street laid to the nearest street it lines up with. Off, such a district is taken back down |
| `villageLayout` | text | empty | A city map laid out instead of planning the district, named like mypack:downtown and read from that pack's citymaps folder. Empty plans the district as usual |
| `villagePathCenterBlock` | block | empty | A center line down the middle of the road. Empty draws none |
| `villagePathCenterDash` | int, 0 or more | `0` | Dashes that line: N blocks of line, then one of road. Anchored to world coordinates, so the dashes of one road piece continue into the next. `0` keeps it solid |
| `villagePathLineBlock` | block | empty | Edge lines between road and sidewalk. Empty draws none |
| `villagePathSidewalkBlock` | block | empty | Sidewalks, laid level with the road outside the edge lines. Empty lays none |
| `villagePathSidewalkWidth` | int, 0 or more | `2` | How wide each sidewalk is, once `villagePathSidewalkBlock` is set |
| `villagePathLampBlock` | text | `minecraft:oak_fence` | The block a lamp post along a street is built from, stacked villagePathLampHeight tall on the curb. A street or alley stands one at each end, one where another street meets it and one every 7 to 12 blocks between, on its lower side and on the other only where that has no room, and a cul-de-sac rings its rim with them. None stands on a bridge, in a tunnel or within two blocks of a door. Empty stands no lamp posts |
| `villagePathLampHeight` | int, 1 or more | `3` | How many blocks tall the post stands before its head |
| `villagePathLampTopBlock` | block | `minecraft:black_wool` | The head on top of the post. Empty leaves it bare |
| `villagePathLampSideBlock` | block | `minecraft:torch` | The light hung on each side of the head, facing outward. Empty hangs none |
| `villagePathLampStructure` | text | empty | A structure file placed as the whole lamp instead of stacking the three lamp blocks, named `mypack:street_lamp` and read from that pack's `structures` folder. It is centered on the lamp spot with its lowest layer on the curb, and the blocks it lays are held so nothing else overwrites them. Empty stacks the blocks |
| `villageWellStructure` | list | empty | Structure files placed as the centerpiece of every plaza, one weighted entry per line written name=weight like mypack:plaza_spire=3, rolled once per plaza. It is centered on a six block square cleared and floored with villagePathBlock, its lowest layer on that floor. Empty, the empty share, or a structure that cannot be loaded builds the game's own well there instead. An entry not written name=weight is left out |
| `villagePathDeadEnds` | list | empty | How a street that dead ends is closed off, one entry per line, rolled per end: sidewalk paves the end row with the sidewalk block and barrier stands villagePathBridgeBarrierBlock along it villagePathBridgeBarrierHeight tall; any other entry is ignored. They close only an end
 that grew no cul-de-sac, a style whose block is not set drops out of the roll, and an alley end takes only barrier. Empty leaves such ends open |
| `villagePathIntersects` | list | empty | Designs painted at junctions, named by registry key from a pack's `<namespace>/pathintersects/`. One entry paints every junction alike; several are picked per junction by weight |

#### Village bridges and piers

*villages and cities*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "villagePathSupportBlock": "minecraft:gravel",
    "villagePathBridgeBlock": "minecraft:oak_planks",
    "villagePathBridgeSidewalkBlock": "minecraft:oak_planks",
    "villagePathBridgeBarrierBlock": "minecraft:oak_fence",
    "villagePathBridgeBarrierHeight": 1,
    "villagePathBridgeDrop": 3,
    "villagePathVergeBlock": "",
    "villagePathVergeWaterBlock": "minecraft:oak_planks",
    "villagePathBridgeFrameBlock": "minecraft:stone_bricks",
    "villagePathBridgeFrameTopBlock": "minecraft:smooth_stone_slab",
    "villagePathBridgeFrameHeight": 4,
    "villagePathBridgeFrameRun": 24,
    "villagePathBridgeFrameLeast": 24,
    "villagePathPiers": ["railed", "pilings", "boardwalk"],
    "villagePathPierCargo": ["minecraft:chest=3", "mypack:crate=2,3", "empty=4"],
    "villagePathPierLoot": "resourcedatapackloader:chests/pier_cargo"
  }
}
```

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `villagePathSupportBlock` | text | empty | The surface itself where the ground is bare rock, and the piers and legs under a street over water. Empty keeps vanilla gravel, sandstone in desert cities |
| `villagePathBridgeBlock` | text | empty | The block a street or pier crosses water with. Empty decks it in planks of the village wood: acacia in a savanna village, spruce in a taiga village, oak elsewhere |
| `villagePathBridgeSidewalkBlock` | block | empty | Decks the sidewalk where a road crosses water. Empty carries the normal sidewalk block across |
| `villagePathBridgeBarrierBlock` | block | empty | Barriers stacked along both edges of a bridge deck. None stands where the deck rests on ground. Empty builds none |
| `villagePathBridgeBarrierHeight` | int, 1 or more | `1` | How many blocks tall those barriers stand |
| `villagePathBridgeDrop` | int, 0 or more | `0` | How far a road's grade must stand clear of the ground before the drop under it is bridged rather than filled solid. `0` keeps roads on the ground: they bridge water and nothing else. `3` is the rule a railway trestle follows. This moves the grade, not only the dress |
| `villagePathVergeBlock` | text | empty | The block the ground beside a street and under a plot is filled with where the city has to make land: the grooves between plots and the fill down to a street across a gap, which is decked in villagePathBridgeBlock instead where the street there is a bridge. Empty follows the ground it stands on, sand, terracotta, gravel or dirt with grass on top where it would be dirt |
| `villagePathVergeWaterBlock` | block | `minecraft:oak_planks` | What that fill becomes where it stands over water, so a verge carried out onto a lake is not a column of dirt. It dresses a stone doorstep left over water too |
| `villagePathBridgeFrameBlock` | block | empty | An overhead frame over a long bridge: a post up each side of the deck and a beam across the top. Each frame carries a pier down to the ground under the deck, and no lamp post is raised on the row it stands on. Empty builds none |
| `villagePathBridgeFrameTopBlock` | block | empty | The beam across the top of that frame. Empty uses `villagePathBridgeFrameBlock` |
| `villagePathBridgeFrameHeight` | int, 1 or more | `4` | How many blocks of clear headroom the frame leaves over the deck, the beam lying one block above that |
| `villagePathBridgeFrameRun` | int, 1 or more | `24` | How many rows apart the frames stand when a bridge is long enough for several. They are spread symmetrically about the middle of the bridged run |
| `villagePathBridgeFrameLeast` | int, 1 or more | `24` | The shortest bridged run that gets a frame at all. A shorter bridge is left plain |
| `villagePathPiers` | list | empty | Pier styles for a street that dead ends over water: the bridged tail becomes a pier instead of a bridge to nowhere. The styles are railed, pilings and boardwalk; several entries roll one per pier. Empty leaves such a tail a plain bridge |
| `villagePathPierCargo` | list | empty | Cargo stood along the inside of a pier's rails, as block=weight entries, block=weight,height to stack it, or empty=weight for the share left clear. A block may carry its state in brackets, and one with a facing turns toward the pier's middle. A height that is not 1 to 8 stands one block. Every other row rolls the list on each side. Empty leaves piers bare |
| `villagePathPierLoot` | text | `resourcedatapackloader:chests/pier_cargo` | The loot table cargo blocks with an inventory are filled from, rolled the first time one is opened. A pack may replace the built-in table by shipping its own loot table at that name. Empty leaves them empty |

#### Village tunnels

*villages and cities*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "villagePathTunnelBlock": "minecraft:stone_bricks",
    "villagePathTunnelDepth": 10,
    "villagePathTunnelLightBlock": "minecraft:sea_lantern",
    "villagePathTunnelLightRun": 8
  }
}
```

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `villagePathTunnelBlock` | text | empty | The block a street is lined with where it bores through a hill instead of cutting it open: the walls either side of the bore and the roof over it. Empty bores no tunnels and lets a street climb the hill |
| `villagePathTunnelDepth` | int, 1 or more | `10` | How much ground has to stand over the road surface before a stretch is bored rather than cut. A rise buried that deep for twelve rows or more is held level and bored through, its shallower approaches cut open; a shorter bump is cut as before. Only counts once `villagePathTunnelBlock` names a block |
| `villagePathTunnelLightBlock` | block | empty | A light set into the tunnel roof down its center line. Empty lights none |
| `villagePathTunnelLightRun` | int, 1 or more | `8` | How many blocks apart those lights sit. Anchored to world coordinates, so the lights of one road piece continue into the next; a tunnel too short to reach one of those spots is lit once, in its middle |

#### Village sewers

*villages and cities*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "villageSewerBlock": "minecraft:stone_bricks",
    "villageSewerDepth": 8,
    "villageSewerHeight": 3,
    "villageSewerWidth": 5,
    "villageSewerWaterBlock": "minecraft:water",
    "villageSewerWalkBlock": "minecraft:chiseled_stone_bricks",
    "villageSewerLightBlock": "minecraft:glowstone",
    "villageSewerLightRun": 8,
    "villageSewerLadderBlock": "minecraft:ladder",
    "villageSewerCoverBlock": "minecraft:iron_trapdoor",
    "villageSewerMossBlock": "minecraft:mossy_cobblestone",
    "villageSewerMossChance": 30,
    "villageSewerVineBlock": "minecraft:vine",
    "villageSewerVineChance": 20,
    "villageSewerWellEntrance": true
  }
}
```

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `villageSewerBlock` | block name | empty | The block a sewer is lined with under a village's streets and alleys: its floor, its two walls and its roof. Empty digs no sewers |
| `villageSewerDepth` | int, 4 or more | `8` | How far under a street's own surface the sewer floor sits. The sewer follows the street it runs under, so a climbing street carries a climbing sewer. Needs `villageSewerBlock` |
| `villageSewerHeight` | int, 2 or more | `3` | How many blocks of headroom stand over the walkway |
| `villageSewerWidth` | int, 3 or more | `5` | How wide the sewer runs, counted across including its two walls. An even number is rounded up so the channel keeps the middle |
| `villageSewerWaterBlock` | block name | `minecraft:water` | What fills the channel down the middle. Empty leaves the channel dry |
| `villageSewerWalkBlock` | block name | empty | What the walkways either side of the channel are surfaced with. Empty walks on the lining block |
| `villageSewerLightBlock` | block name | empty | The block set into the roof over the channel as a light. Empty lights none |
| `villageSewerLightRun` | int, 1 or more | `8` | How many blocks apart those lights sit. Anchored to world coordinates, so the lights of one road piece continue into the next |
| `villageSewerLadderBlock` | text | empty | The block a manhole shaft is climbed by, set down the shaft from the street to the sewer roof. Empty leaves the shaft open |
| `villageSewerCoverBlock` | block name | empty | The block covering a manhole, set flush in an east-west street wherever a street or alley meets it, and on the plaza where that street crosses the sewer loop. A wooden trapdoor is the usual choice: an iron one takes a redstone signal and no player can open it by hand, which shuts the sewer to them. Empty leaves the shaft mouth open |
| `villageSewerMossBlock` | block name | empty | A second block mixed into the lining here and there, mossy stone among plain for instance. Empty lines the sewer with one block throughout |
| `villageSewerMossChance` | 0 to 100 | `25` | What percentage of lining blocks come out as that second block. Rolled per block position from the world seed, so the same sewer always comes out the same |
| `villageSewerVineBlock` | block name | empty | A block hung on the inside of the sewer walls here and there, vines for instance. It is clung to whichever wall it stands against. Empty hangs nothing |
| `villageSewerVineChance` | 0 to 100 | `20` | What percentage of the cells beside a wall carry it. Rolled per block position from the world seed, so the same sewer always hangs the same |
| `villageSewerWellEntrance` | boolean | `true` | A loop of sewer under the plaza ring around the well, every street's sewer running through it, and a manhole on the plaza down onto the loop on each side where an east-west street crosses it, so the sewers are one connected system with a way in at the town center. Off, each street's sewer ends at the well and the plaza has no way down |

**Sewers.** Naming `villageSewerBlock` digs a sewer under every street and alley, `villageSewerDepth` blocks beneath that street's own surface. It is not a network of its own: it follows the streets, so wherever they go the sewer goes, it climbs where they climb, and two sewers meet under a crossroads because the streets above them meet; where a street or alley ends against another, its sewer runs on under that one to join it. A cul-de-sac and a stretch carried on a bridge carry none. The section is a lined floor, a channel down the middle filled with `villageSewerWaterBlock`, a walkway either side surfaced with `villageSewerWalkBlock`, `villageSewerHeight` blocks of headroom and a lined roof, `villageSewerWidth` wide across including its two walls, and `villageSewerLightBlock` sets a light into the roof over the channel every `villageSewerLightRun` blocks. Where a subway bore passes through the sewer's depth, under the street or beside it, the sewer is walled solid across it and any track there is left alone. A street's sewer stops at the loop around the well when `villageSewerWellEntrance` is on, and at the well itself when it is off. A sewer never rises far enough to disturb the street over it, and a stretch with no room between the street and the world floor is skipped rather than squeezed.

#### Village railways

*villages and cities*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "villageRailLines": 1,
    "villageRailSpacing": 48,
    "villageRailDirection": "ew",
    "villageRailWidth": 3,
    "villageRailBlock": "minecraft:rail",
    "villageRailTrackSeat": "auto",
    "villageRailBedBlock": "minecraft:gravel",
    "villageRailTieBlock": "minecraft:spruce_planks",
    "villageRailTieRun": 2,
    "villageRailTracks": 2,
    "villageRailTrackGap": 2,
    "villageRailShoulderBlock": "minecraft:gravel",
    "villageRailShoulderWidth": 1,
    "villageRailPowerBlock": "minecraft:powered_rail",
    "villageRailPowerBase": "minecraft:redstone_block",
    "villageRailPowerRun": 16,
    "villageRailClimb": 8,
    "villageRailTail": 48,
    "villageRailSupportBlock": "minecraft:oak_log",
    "villageRailDeckBlock": "minecraft:oak_planks",
    "villageRailBarrierBlock": "minecraft:oak_fence",
    "villageRailBridgeFrameBlock": "minecraft:stone_bricks",
    "villageRailBridgeFrameTopBlock": "minecraft:smooth_stone_slab",
    "villageRailBridgeFrameHeight": 4,
    "villageRailBridgeFrameRun": 24,
    "villageRailBridgeFrameLeast": 24,
    "villageRailTunnelBlock": "minecraft:stone_bricks",
    "villageRailTunnelDepth": 6,
    "villageRailTunnelLightBlock": "minecraft:glowstone",
    "villageRailTunnelLightRun": 8
  }
}
```

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `villageRailLines` | int, 0 or more | `0` | How many railway lines run through a city, laid before any street so the town grows around them, each running the length of the city. At villageCitySpacing 1 every district is a city and carries its own. 0 lays none |
| `villageRailSpacing` | int, 1 or more | `48` | The fewest blocks of clear ground between one railway line's bed and the next of the same city. 1 lays them a block apart, which is how a pack builds a yard of parallel lines |
| `villageRailDirection` | text | `any` | Which way the lines run: `ew` east to west, `ns` north to south, `any` rolls it per city. `e`, `w`, `n` and `s` are read the same way |
| `villageRailWidth` | int, 3 or more | `3` | The least the railbed is. `3` carries one track down the middle and `5` carries two; a bed asked for more tracks than that fits widens to hold them |
| `villageRailBlock` | block | empty | The track. Empty lays vanilla rails, which minecarts ride; any other block is laid as it stands |
| `villageRailTrackSeat` | `auto`, `on` or `in` | `auto` | Where the track sits. `auto` seats a rail block on the bed and sets any other block flush into the bed surface; `on` always lays it on the bed; `in` always sets it into the bed. A track set into the bed is how a pack lays a rail look out of iron blocks or slabs rather than minecart rails, and a level crossing then runs flush through the pavement |
| `villageRailBedBlock` | block | empty | The bed under the track. Empty lays gravel |
| `villageRailTieBlock` | text | empty | The sleeper laid across the bed every villageRailTieRun rows. Empty lays oak planks |
| `villageRailTieRun` | int, 1 or more | `2` | How many rows apart the sleepers lie |
| `villageRailTracks` | int, 0 or more | `0` | How many tracks the one bed carries, side by side and `villageRailTrackGap` apart. **The bed widens to hold them all**, so three tracks share one railbed rather than becoming three lines. `0` lays one track on a bed under five wide and two on a wider one |
| `villageRailTrackGap` | int, 2 or more | `2` | How many blocks apart the tracks on a bed sit, center to center. `2`, the least allowed, leaves one block of bed between them, which is what keeps them from curving into one another the way touching rails do |
| `villageRailShoulderBlock` | block | empty | Dresses the outermost columns of the bed, a maintenance path beside the track and the railway's answer to a road sidewalk. Empty lays none |
| `villageRailShoulderWidth` | int, 0 or more | `1` | How many columns wide that shoulder is on each side, added outside `villageRailWidth`. Needs `villageRailShoulderBlock` |
| `villageRailPowerBlock` | block | empty | The powered track set into the line every `villageRailPowerRun` rows. Empty uses a vanilla powered rail; a block that is not a rail is simply laid there |
| `villageRailPowerBase` | block | empty | What sits under a powered track to feed it. Empty uses a redstone block |
| `villageRailPowerRun` | int, 0 or more | `0` | Every so many rows a powered rail over a redstone block is set into a vanilla rail track, so a cart keeps rolling. `0` powers none, and any track but vanilla rails ignores it |
| `villageRailClimb` | int, 1 or more | `8` | How many rows the line runs level for every block it climbs or falls. `1` grades it as steep as a road |
| `villageRailTail` | int, 0 or more | `48` | How far a railway line runs on past the last district of the city at either end |
| `villageRailSupportBlock` | text | empty | The post block under a trestle, where the line runs over water or a drop. Empty uses oak logs |
| `villageRailDeckBlock` | text | empty | The deck a trestle carries the bed on. Empty uses oak planks |
| `villageRailBarrierBlock` | block | empty | Barriers along both edges of a trestle deck. Empty stands none |
| `villageRailBridgeFrameBlock` | block | empty | An overhead frame over a long trestle: a post up each side of the deck and a beam across the top. Every row that carries one also carries its support posts down to the bed. Empty builds none |
| `villageRailBridgeFrameTopBlock` | block | empty | The beam across the top of that frame. Empty uses `villageRailBridgeFrameBlock` |
| `villageRailBridgeFrameHeight` | int, 2 or more | `4` | How many blocks of clear headroom the frame leaves over the deck, the beam lying one block above that |
| `villageRailBridgeFrameRun` | int, 2 or more | `24` | How many rows apart the frames stand when a trestle is long enough for several |
| `villageRailBridgeFrameLeast` | int, 2 or more | `24` | The shortest trestle that gets a frame at all. A shorter trestle is left plain |
| `villageRailTunnelBlock` | text | empty | The block a railway line is lined with where it bores through a hill instead of climbing it. Empty bores no tunnels |
| `villageRailTunnelDepth` | int, 1 or more | `6` | How much ground must stand over the bed before a stretch is bored rather than cut. Needs `villageRailTunnelBlock` |
| `villageRailTunnelLightBlock` | block | empty | A light set into a railway tunnel's roof down its center line. Empty lights none |
| `villageRailTunnelLightRun` | int, 1 or more | `8` | How many blocks apart those tunnel lights sit, anchored to world coordinates so pieces agree |

**Where a line goes.** The lines run parallel, on the axis `villageRailDirection` names, and are spaced out from the city's first well in turn, first one side then the other, each keeping at least `villageRailSpacing` blocks of ground between its bed and the next line's. A railway starts clear of the plaza and the plots around it and slides aside wherever it would lie along a street; a subway starts on the well's row and slides onto the nearest street within `villageSubwaySpacing`, so it runs under a road. A line is laid before the plots, so no plot stands on open track, and it runs the length of the city and on `villageRailTail` past its last district at either end, stopping seven blocks short of any other city in its way. Every district it passes lays its own stretch, whether the city grew there or not.

**Grade.** A railway does not climb like a street. Its bed follows the ground smoothed out over a long run and changes level by one block at most every `villageRailClimb` rows; a subway follows the ground `villageSubwayDepth` below it, and a line that cannot hold that depth anywhere along it, over the six blocks of room its lining needs above the world floor, is not laid at all. Where the ground drops away by more than three blocks, or water covers it, the line runs on a trestle: a deck of `villageRailDeckBlock` with the track on it or in it and no sleepers or shoulder, on `villageRailSupportBlock` posts under both edges every four rows, each post going down to solid ground at most 24 blocks. Where the ground rises the line is cut open, or bored through with `villageRailTunnelBlock` once the ground over the bed stands `villageRailTunnelDepth` deep for twelve rows or more; the bore carries on for as long as a block of ground still roofs it. An open cut with water within three blocks of it is walled in the tunnel lining up to the water, and the ground beside the bed is made up where it falls away. Four blocks are kept clear over the bed along the whole line. A trestle lies at one height from end to end, and the bed either side of it ramps to meet that height; where holding a trestle level and the climb rate disagree, the level wins and the ramp beside it may step sooner than `villageRailClimb` says. A trestle of `villageRailBridgeFrameLeast` rows or more carries overhead frames once `villageRailBridgeFrameBlock` names a block, `villageRailBridgeFrameRun` rows apart and spread symmetrically about the middle of the trestle, and every row that carries one carries its support posts too. A row where a street crosses the line is left without a frame.

**Crossings.** A street crosses a line straight through. At a crossing the line is held level across the street and a row beyond it on either side, and the street is graded to the line, never the other way around, ramping to that level at its own slope. The pavement keeps the surface and the track runs across it one block up, or flush in it when `villageRailTrackSeat` sets the track in the bed, so a cart crosses the street and a villager crosses the track. A line bored under a street that stands six blocks or more over it is not crossed at all: the street keeps its own grade and passes over the tunnel. That height is read from the street's ground smoothed to climb at most a block a row, before any crossing, well or railway holds it.

**Doorsteps.** The step before every door of a plot is made up with ground where the ground falls away, and a stone step left standing over water is dressed in `villagePathVergeWaterBlock`.

**Track.** With `villageRailBlock` empty the track is vanilla rail turned along the line, and `villageRailPowerRun` sets a powered rail, switched on, over a redstone block every so many rows so a cart rides the whole line; a track set into the bed carries no powered rails. A pack that wants iron blocks, bars or anything else names them instead: a block with an axis, a log for instance, is turned along the line, and any other is laid as it stands. Every rail, subway, station and sewer block may carry its state in brackets, and Mixing blocks above names the settings a weighted mix is drawn from block by block.

#### Village subways

*villages and cities*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "villageSubwayLines": 0,
    "villageSubwayDepth": 24,
    "villageSubwaySpacing": 64,
    "villageSubwayDirection": "any",
    "villageSubwayWidth": 5,
    "villageSubwayBlock": "",
    "villageSubwayTrackSeat": "auto",
    "villageSubwayBedBlock": "minecraft:gravel",
    "villageSubwayTieBlock": "minecraft:spruce_planks",
    "villageSubwayTieRun": 2,
    "villageSubwayTracks": 2,
    "villageSubwayTrackGap": 2,
    "villageSubwayShoulderBlock": "",
    "villageSubwayShoulderWidth": 1,
    "villageSubwayPowerBlock": "",
    "villageSubwayPowerBase": "minecraft:redstone_block",
    "villageSubwayPowerRun": 16,
    "villageSubwayTunnelBlock": "minecraft:stone_bricks",
    "villageSubwayTunnelLightBlock": "minecraft:glowstone",
    "villageSubwayTunnelLightRun": 8,
    "villageSubwayClimb": 8,
    "villageSubwayTail": 48,
    "villageSubwaySurfaces": 25
  }
}
```

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `villageSubwayLines` | int, 0 or more | `0` | How many underground railway lines a city digs. 0 digs none and rolls nothing, so the city is laid exactly as it would be without them |
| `villageSubwayDepth` | int, 6 or more | `24` | How far under the surface the bed sits. The line is graded from the ground above it, so it follows the land at that depth rather than running level |
| `villageSubwaySpacing` | int, 1 or more | `64` | How far apart a city's subway lines are kept from one another |
| `villageSubwayDirection` | text | `any` | Which way subway lines run: ew for east to west, ns for north to south, or any to roll per city |
| `villageSubwayWidth` | int, 3 or more | `3` | How wide the bed is, before shoulders |
| `villageSubwayBlock` | block | empty | The track block. Empty lays vanilla rail |
| `villageSubwayTrackSeat` | string | `auto` | Whether the track sits on the bed, in it, or `auto` to let the block decide |
| `villageSubwayBedBlock` | block | empty | The block the bed is made of. Empty uses gravel |
| `villageSubwayTieBlock` | block | empty | The block laid across the bed as sleepers. Empty uses planks |
| `villageSubwayTieRun` | int, 1 or more | `2` | How many blocks apart the sleepers sit |
| `villageSubwayTracks` | int, 0 or more | `0` | How many parallel tracks the bed carries. 0 takes as many as the width allows |
| `villageSubwayTrackGap` | int, 2 or more | `2` | How far apart parallel tracks sit |
| `villageSubwayShoulderBlock` | block | empty | The block either side of the bed. Empty leaves no shoulder |
| `villageSubwayShoulderWidth` | int, 0 or more | `1` | How wide that shoulder is |
| `villageSubwayPowerBlock` | block | empty | The powered track block. Empty uses vanilla powered rail |
| `villageSubwayPowerBase` | block | empty | The block set under a powered track to drive it. Empty uses a redstone block |
| `villageSubwayPowerRun` | int, 0 or more | `0` | How many blocks apart the powered tracks sit. 0 lays none |
| `villageSubwayTunnelBlock` | text | empty | The block the bore is lined with: the walls either side and the roof over it. Empty digs the bore and its stations unlined |
| `villageSubwayTunnelLightBlock` | block | empty | The block set into the tunnel roof as a light. Empty lights none |
| `villageSubwayTunnelLightRun` | int, 1 or more | `8` | How many blocks apart those lights sit, anchored to world coordinates so pieces agree |
| `villageSubwayClimb` | int, 1 or more | `8` | How many blocks a line runs before it may step one block up or down |
| `villageSubwayTail` | int, 0 or more | `48` | How far past the city's own pieces a line runs before it stops |
| `villageSubwaySurfaces` | int, 0 to 100 | `25` | The chance in a hundred that a subway line climbs to the surface at one end and carries on from there as an ordinary railway, tunnel behind it and open track ahead. The climb takes villageSubwayClimb rows per block, so a deep line spends a long run coming up. 0 keeps every subway buried for its whole length |

**Climbing out.** `villageSubwaySurfaces` is the chance in a hundred that a line, instead of staying buried end to end, climbs to the surface at one end and carries on from there as an ordinary railway: tunnel behind it, open track ahead. The climb obeys `villageSubwayClimb`, one block per that many rows, so a line `villageSubwayDepth` deep spends depth times climb rows on the ramp alone and needs a good stretch beyond it to be worth the name; a line with no room for both simply stays underground. The climb starts no nearer than the far end of the streets the line runs under, so it comes up past the city's streets rather than through them, and a plot standing on the stretch that comes up makes way for it. Stations are claimed once the climb is decided and keep off the ramp. With `villageSubwayTunnelBlock` empty the bore, its stations and its stairs are dug unlined.

#### Subway stations

*villages and cities*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "villageSubwayStationLength": 16,
    "villageSubwayStationRun": 0,
    "villageSubwayPlatformWidth": 3,
    "villageSubwayPlatformBlock": "minecraft:mossy_stone_bricks",
    "villageSubwayRailingBlock": "minecraft:iron_bars",
    "villageSubwayBenchBlock": "minecraft:oak_stairs",
    "villageSubwayBenchEndBlock": "minecraft:oak_log",
    "villageSubwayBenchLength": 5,
    "villageSubwayStation": "mypack:subway_station",
    "villageSubwayStationFoot": 4,
    "villageSubwayStationRepeat": 12
  }
}
```

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `villageSubwayStationLength` | int, 0 or more | `0` | How many blocks long a station chamber is, centered on the row where the line passes nearest the well. 0 builds no stations at all |
| `villageSubwayStationRun` | int, 0 or more | `0` | How many blocks apart further stations sit along a line, past the one nearest the city's first well. 0 builds only the one at the well |
| `villageSubwayPlatformWidth` | int, 0 or more | `3` | How far the chamber is opened out either side of the bed to make a platform |
| `villageSubwayPlatformBlock` | block | empty | The block the platform is floored with. Empty floors it with the tunnel lining |
| `villageSubwayRailingBlock` | block | `minecraft:iron_bars` | The block railed around the head of a station's stairs where they open on the street, so nobody walks into the well. Empty leaves the head unrailed |
| `villageSubwayBenchBlock` | block | `minecraft:oak_stairs` | The seat of the benches set on a station's platform and beside its stair head. A stairs block is turned to face away from the line and reads as a bench; any block works. Empty leaves the benches out |
| `villageSubwayBenchEndBlock` | block | `minecraft:oak_log` | The arms at each end of a station bench. Empty leaves the seat bare at both ends |
| `villageSubwayBenchLength` | int, 0 to 32 | `5` | How long a station bench is, arms included. `0` leaves the benches out |
| `villageSubwayStation` | text | empty | A structure from a pack's structures folder used as the station: its shaft, its stairs and its way in from the street. Lift one out of a world built by hand with #scripts/rdpl-grab-template.py: its solid cells are laid and its air cells are carved, so the shape is the build and not a description of it. Empty builds no stations at all, and a name that cannot be loaded logs one error and builds none |
| `villageSubwayStationFoot` | int, 0 to 64 | `4` | How many layers at the foot of a station build are laid once, before the part that repeats. The floor and the doorway out to the platform live here |
| `villageSubwayStationRepeat` | int, 0 to 64 | `12` | How many layers of a station build repeat, so one build serves any depth: the shaft grows by whole copies of this band and the corridor absorbs what is left over. It must be a whole turn of the stairs or the flights will not join. `0` never grows the build |

**Stations.** A subway line gets stations only when `villageSubwayStation` names a build that loads: with it empty there is no chamber, no stairs and no way in, and a name that cannot be loaded logs one error and builds none. With a build named, a line gets a station at the row of the city's first well once `villageSubwayStationLength` and `villageSubwayPlatformWidth` are set, and further ones every `villageSubwayStationRun` blocks along it. Each slides up to 48 blocks either way to find a spot for its build beside a street that runs along the line for the whole length of that spot, off every street, well and plaza, and at least the station's length and seven more from a station already claimed; a plot standing on that spot makes way, and a station with no such spot is left out. A line that keeps no station opens no chamber, so it never carries one with no way into it. The chamber is held level along its length: the bed opened out `villageSubwayPlatformWidth` either side, floored with `villageSubwayPlatformBlock`, walled and roofed in the tunnel lining, lit from the tunnel's own `villageSubwayTunnelLightBlock` and `villageSubwayTunnelLightRun`, and walled across the bore at both ends. From the platform a corridor runs to the station build, which climbs to the street beside the road, never under it; the build comes up at the grade of the nearest street within eight blocks, the grade that street keeps on the ground rather than any deck or ramp raised over it, or at the height of the ground where there is no street, and is left out when the city is built where that is less than three blocks over the platform, where the build cannot make the climb even grown, or where its corridor to the platform would run past 32 blocks; the log says which. The ground between that street and the build is brought to the same height, filled in where it falls away and cleared overhead, so the station is walked into from the road. A bench of `villageSubwayBenchBlock` with `villageSubwayBenchEndBlock` arms, `villageSubwayBenchLength` long, stands on the platform.

**Building the station by hand.** `villageSubwayStation` names a structure file used as the station, which is how a pack ships a shape somebody built rather than one described in settings. Build it in a world, lift it out with #scripts/rdpl-grab-template.py and place it with the pack: its blocks are laid as built, sponge cells become the tunnel lining, its air cells are carved out, and anything standing in it, a minecart or an armor stand, comes with it. It is seated from the corner of the station's spot. One build serves any depth because the middle of it repeats: `villageSubwayStationFoot` layers are laid once at the bottom, carrying the floor and the doorway to the platform, then whole copies of the next `villageSubwayStationRepeat` layers stack up until the build reaches the street. That band must be a whole turn of the stairs or the flights will not meet where two copies join. A corridor two blocks high runs from the doorway down to the platform, turning back along the chamber where the drop is too long to go straight; the ground over the build's head is cleared eight blocks up, a railing of `villageSubwayRailingBlock` rings the opening at street level and a bench stands beside it. A build that cannot be loaded builds no station anywhere and logs one error; a station its build cannot bring up to the street is left out when the city is planned, with no chamber, and logs why. The build carries its own opening onto the street.

#### Railway links

*villages and cities*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "villageRailLines": 1,
    "villageRailLinks": true,
    "villageRailLinkLeast": 128,
    "villageRailLinkMost": 1024,
    "villageRailLinkBridgeMost": 96,
    "villageRailLinkTunnelMost": 192,
    "villageRailLinkStation": "both",
    "villageRailLinkStationLength": 16,
    "villageRailLinkPlatformWidth": 3,
    "villageRailLinkPlatformBlock": "minecraft:stone_bricks"
  }
}
```

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `villageRailLinks` | true/false | `false` | Links neighboring cities whose first lines face each other across a seam. Needs `villageRailLines`, or `villageSubwayLines` on a pack with no surface lines |
| `villageRailLinkLeast` | int, 0 or more | `128` | The shortest link laid, spur plus trunk plus spur, in blocks |
| `villageRailLinkMost` | int, 0 or more | `1024` | The longest link laid, spur plus trunk plus spur, in blocks |
| `villageRailLinkBridgeMost` | int, 0 or more | `96` | The longest bridge a link may need. A link over wider water or a deeper drop is not laid |
| `villageRailLinkTunnelMost` | int, 0 or more | `192` | The longest tunnel a link may need where `villageRailTunnelBlock` bores tunnels. A link that would bore further is not laid |
| `villageRailLinkStation` | text | `both` | The station on each spur just before the trunk: `both` lays a platform either side of the line, `one` a single platform on the left of a train arriving at the trunk, `none` builds none |
| `villageRailLinkStationLength` | int, 0 or more | `16` | How many rows long the station platforms are. `0` builds no stations |
| `villageRailLinkPlatformWidth` | int, 0 or more | `3` | How many blocks wide each platform is |
| `villageRailLinkPlatformBlock` | block | empty | The block the platforms are built of. Empty uses stone bricks |

**What a link is.** Railway links join neighboring cities into one network. Cities are founded one to a cell of the city grid (`villageCitySpacing`), and a link runs along the seam between two cells: each city's first line carries on past its tail as a spur, straight out to the seam, and meets a trunk laid along the seam at right angles. The trunk runs from one spur to the other and never past either. It needs `villageRailLines`, or `villageSubwayLines` on a pack with no surface lines, and it is off by default.

**Which cities link.** Two cities link only when they stand in neighboring cells, their first lines run on the axis that crosses the seam between them, and the whole link, measured from well to well along the track, is between `villageRailLinkLeast` and `villageRailLinkMost` blocks. Every part of the decision is worked out from the seed and the two city sites, so it comes out the same whichever city or chunk is made first. A link that cannot be built whole is not laid at all, never half built: one that would need a longer bridge or tunnel than the settings allow, reach past the world border, run into a woodland mansion, stand two cities closer than `structureSeparation` allows, or bring a join too close to a corner of the cells. A trunk is only laid toward a city that was actually founded: when a cap such as `structureMost` stops the neighbor, or it grows too small to keep, neither half of the trunk nor the spur past the city's own tail is built. Pinned cities link the same way, one per cell; a cell holding two pins links neither. Other cities keep clear of a link's spur and trunk as they grow, the way they keep clear of each other.

**Grade.** Spurs and trunks are railway lines and are graded, bridged, tunneled and crossed exactly like a city line, with the `villageRailClimb` and the trestle and tunnel settings above. Where a spur meets the trunk both lie level, and so does the station beside it.

**The junction.** A spur joins only the trunk's near track. That track is broken where the spur's middle meets it, the spur's left track curves left into it and its right track curves right, and the far track runs straight through. With two tracks, the trunk along the top and the spur coming up from below:

```
xxxxxxx
ooooooo
xxxxxxx
oooxooo
xxoxoxx
 xoxox
```

`x` is railbed and `o` is track. Where the two spurs would arrive within a few blocks of each other, the second city's first line moves over to line up with the first, and the two meet in a crossroad instead: each spur merges only into its own near track exactly as above, both trunk tracks are broken at the spur center, and no rail crosses another:

```
 xoxox
xxoxoxx
oooxooo
xxxxxxx
oooxooo
xxoxoxx
 xoxox
```

A single-track trunk has no second track to give the other spur, so a link whose spurs would meet head on over a single track is not laid. With a single track the spur's track curves into the trunk track toward the left, and the trunk track beyond that curve ends against it. The curves are set with their shapes fixed, so vanilla rail turns where the junction is drawn and nowhere else.

**Stations.** The last rows of a spur before the junction are a station: platforms of `villageRailLinkPlatformBlock` level with the rail, railed along the outer edge with `villageSubwayRailingBlock`, with a bench of `villageSubwayBenchBlock` halfway along each platform.

**Subways.** On a pack with subway lines only, the link carries a city's first subway line. The line climbs out of the ground toward the trunk, with the ramp `villageSubwayDepth` times `villageSubwayClimb` rows long, and reaches the station and the junction at the surface; a city of this kind links on one side only, the one with the shorter link, and the trunk is a surface railway built from the `villageRail` settings. Where a spur has no room for that ramp and its station, the trunk goes down to the subway instead: the whole link, spurs and trunk, stays underground at `villageSubwayDepth`, is built from the `villageSubway` settings, and meets in the same junction with no station.

#### Village decoration

*villages and cities*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "villageDecor": ["mypack:street_flowers=2", "mypack:street_tree=1", "empty=3"]
  }
}
```

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `villageDecor` | list | empty | Decoration scattered along city streets, as name=weight pairs naming worldgen from a pack, mypack:street_flowers=2. The name empty is the share of spots left bare, and an entry not written name=weight is left out. Every third block of verge on each side of a street rolls the list, on the ground there however high it stands, but not in a tunnel, under a plot, on a plaza or within two blocks of a door. Empty scatters nothing |

### Structures

*what each group does*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "structureSpacing": ["temples=24", "monuments=40", "mineshafts=200"],
    "structureSeparation": ["monuments=12"],
    "structureMost": ["villages=100"],
    "structureSpawners": ["dungeons=minecraft:zombie,minecraft:husk"],
    "structureMinDistanceFromSpawn": ["strongholds=1000"],
    "structureBiomes": ["temples=minecraft:desert,SANDY"],
    "structureBiomesAreBlacklist": ["temples=false"],
    "structureSpawns": ["temples=minecraft:witch:1:1:1", "monuments="],
    "structureAt": ["villages=1000,-500"],
    "structureAdaptation": ["villages=beard_thin", "mansions=bury", "monuments=none"],
    "terrainAdaptation": true,
    "villagePieces": ["mypack:smithy", "minecraft:village/plains/houses/plains_small_house_1"],
    "villagePiecesAreBlacklist": true,
    "villageBlocks": ["minecraft:cobblestone=mypack:ruby_brick", "minecraft:cobblestone=minecraft:mossy_cobblestone,20", "minecraft:oak_planks=minecraft:sandstone,100,under=minecraft:sand"]
  }
}
```

`control.structures` decides this group. Vanilla structures switched off, their spacing, separation, spawn distance, biomes, spawns, pins and terrain adaptation.

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `structureSpacing` | list | empty | How far apart vanilla structures are seeded, in chunks, as structure=chunks entries: the 1.12.2 names temples, monuments, mansions, mineshafts, strongholds, netherbridges, endcities and villages, or any structure set id such as pillager_outposts. For mineshafts the number is one chunk in that many; for strongholds it is the ring distance. Nether fortresses keep their own grid, which netherbridges spacing does not reach |
| `structureSeparation` | list | empty | The closest two of a structure may be, in chunks, as structure=chunks entries; for strongholds it is the ring spread. Temples, mineshafts and netherbridges keep their own separation, which this does not reach. For monuments, a separation as large as the spacing is brought down to one less than the spacing, and the log says so |
| `structureMost` | list | empty | The most villages a dimension may hold, as villages=count (for example villages=100); other structures are not capped: once that many have been founded no chunk founds another, chunks pinned with structureAt aside. 0 or an absent entry sets no ceiling |
| `structureSpawners` | list of `structure=entity` | empty | What the mob spawner inside a vanilla structure spawns, comma separated for a random pick per spawner. The four that place one are dungeons, mineshafts, nether fortresses and strongholds |
| `structureMinDistanceFromSpawn` | list | empty | How far from the world spawn a structure starts, in blocks, as structure=blocks entries. Measured from the world's spawn point; while a new world is still choosing one, from the pack's worldSpawn when one is set, else from the world origin |
| `structureBiomes` | list | empty | Where a structure may generate, as structure=biome,biome entries naming biome ids, the names the game shows such as Birch Forest, bare vanilla names such as desert, or biome types such as SANDY |
| `structureBiomesAreBlacklist` | list of `structure=true` or `structure=false` | empty | The direction of each structure's biome list |
| `structureSpawns` | list | empty | The mobs a structure spawns whatever the biome says, as structure=namespace:entity:weight:least:most entries, comma separated. The list replaces the structure's own list of mobs whole, whatever kind each mob is; an empty list after the = spawns nothing |
| `structureAt` | list of `structure=x,z` | empty | Pins a structure to an exact spot. See [Structures at exact places](#structures-at-exact-places) |
| `structureAdaptation` | list | mansions `beard_thin`; every other structure keeps its vanilla adaptation | How the terrain adapts to a structure, as structure=mode entries with the modes none, bury, beard_thin, beard_box and encapsulate |
| `terrainAdaptation` | boolean | `false` | Lay RDPL's own city streets, seated into the terrain instead of standing on stilts over every dip, and read the villagePath and villageRail options with them. Changes the terrain, so a world made with it on differs from one made without. Cities are seeded as villageCitySpacing sets out, and at 0 none are |
| `villagePieces` | list | empty | Village plots named here, one per line, by the full id of a villages file such as mypack:smithy, by its bare name, or by the name of the structure a template plot builds. While villagePiecesAreBlacklist is on, a structure named here is also left empty wherever the game loads it, the game's own village houses included, such as minecraft:village/plains/houses/plains_small_house_1 |
| `villagePiecesAreBlacklist` | boolean | `true` | On, the plots in villagePieces are blocked. Off, only those plots are built |
| `villageBlocks` | list | empty | Blocks village plots are built from, as original=replacement pairs, minecraft:cobblestone=mypack:ruby_brick. Either side may carry a state in brackets, which the original must then match exactly. A pair may add a chance out of 100, minecraft:cobblestone=minecraft:mossy_cobblestone,20, weighed from the world seed where the block is laid, at=block to rule only where that block stands, and under=block only over it. Pairs without a chance or a condition apply first, so a conditional pair can weather their result. It rules farms and the game's own village houses a city builds; streets, wells, lamps and the structures of your template plots are never ruled. Empty leaves every block as it is laid |

### Spawning

*what each group does*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "surfaceDayMonsterRate": 0.0,
    "surfaceNightMonsterRate": 1.0,
    "undergroundDayMonsterRate": 1.0,
    "undergroundNightMonsterRate": 1.0,
    "monsterCap": 40,
    "creatureCap": 10,
    "ambientCap": 15,
    "waterCreatureCap": 5,
    "monsterSpawnLight": 0,
    "threatItems": ["minecraft:diamond_sword=5,1", "minecraft:diamond=1,16,batch"],
    "threatLevels": [10, 25, 50],
    "threatMost": -1,
    "threatSpawnRate": 2.0,
    "threatNotice": 16.0,
    "threatSays": ["1=Something out there has taken notice of you.", "0=The world loses interest in you."]
  }
}
```

`control.spawning` decides this group. Mob spawn caps, hostile spawn rates and the light cap.

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `surfaceDayMonsterRate` | number, 0.0 to 4.0 | `1.0` | Multiplier on hostile spawning on the surface by day, `1.0` being vanilla, so daylight surface spawning can be turned off without touching the caves |
| `surfaceNightMonsterRate` | number, 0.0 to 4.0 | `1.0` | The same for the surface at night |
| `undergroundDayMonsterRate` | number, 0.0 to 4.0 | `1.0` | The same underground by day |
| `undergroundNightMonsterRate` | number, 0.0 to 4.0 | `1.0` | The same underground at night |
| `monsterCap` | int, -1 to 1000 | `-1` | How many hostiles may be loaded at once. Vanilla is 70, and `-1` leaves it alone |
| `creatureCap` | int, -1 to 1000 | `-1` | The same for passive animals. Vanilla is 10 |
| `ambientCap` | int, -1 to 1000 | `-1` | The same for bats and the like. Vanilla is 15 |
| `waterCreatureCap` | int, -1 to 1000 | `-1` | The same for squid. Vanilla is 5 |
| `monsterSpawnLight` | int, -1 to 15 | `-1` | The brightest block light a hostile mob may still spawn in, on top of the vanilla checks. -1 keeps the vanilla rule alone. Spawners are not affected |
| `threatItems` | list | empty | Items that raise a player's threat level, as item=level,count entries with an optional ,each or ,batch at the end, e.g. minecraft:diamond_sword=5,1 or minecraft:diamond=1,16,batch. Each, the default, adds the level for every one held, counting no more than count of them; batch adds the level once for every count held. A count above the item's stack size is cut to the stack size. Every loaded entity holding items is a carrier: a player's main inventory, armor and off hand, a dropped stack, anything with an item inventory such as a chest mule or a chest minecart, and the held items and armor of other mobs. Empty turns the threat level off. With `control.spawning` at `off` the config's own threat settings still apply |
| `threatLevels` | list | empty | The scores that enter each band, rising, so `10, 25, 50` makes three bands. Empty turns the threat level off |
| `threatMost` | int, -1 to 100000 | `-1` | Caps the score. `-1` leaves it uncapped |
| `threatSpawnRate` | number, 0.0 to 8.0 | `1.0` | Scales hostile spawning within 128 blocks of a carrier in the top band, on top of the other rates, with lower bands taking a proportional share |
| `threatNotice` | number, 0.0 to 64.0 | `0.0` | How many blocks farther hostile mobs, vanilla ones included, see a carrier in the top band, again shared out over the lower bands |
| `threatSays` | list of `band=message` | empty | The lines shown in yellow when a player's own band changes, band `0` being the line for dropping back below the first band |

### Bedrock

*what each group does*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "flatBedrock": true,
    "flatBedrockDimensions": ["minecraft:overworld", "minecraft:the_nether"],
    "flatBedrockDimensionsAreBlacklist": false,
    "bedrockLayers": 1,
    "flatBedrockBiomes": ["minecraft:plains"],
    "flatBedrockBiomesAreBlacklist": true,
    "flatBedrockRoof": true
  }
}
```

`control.bedrock` decides this group. Flat bedrock and its dimension and biome lists.

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `flatBedrock` | boolean | `false` | Replaces the jagged bedrock at the bottom of the world with flat layers. New chunks only, unless `flatBedrockRetrogen` is on |
| `flatBedrockDimensions` | list | `["minecraft:overworld"]` | The dimensions to flatten in. Empty means every one |
| `flatBedrockDimensionsAreBlacklist` | boolean | `false` | On, flattening skips the dimensions listed. Off, it applies only to them |
| `bedrockLayers` | int, 1 to 5 | `1` | How many layers of bedrock are left |
| `flatBedrockBiomes` | list of biome names | empty | The biomes to flatten in, by friendly or registry name. Empty means every biome |
| `flatBedrockBiomesAreBlacklist` | boolean | `false` | On, flattening skips the biomes listed. Off, it applies only to them |
| `flatBedrockRoof` | boolean | `false` | Flattens the bedrock ceiling too, where a dimension has one, such as the Nether roof |
| `flatBedrockFiller` | block | empty | What replaces the bedrock that is taken away. Empty picks per dimension: stone, netherrack, end stone |
| `flatBedrockFillers` | list of `dimension=block` | `["minecraft:the_nether=minecraft:netherrack", "minecraft:the_end=minecraft:end_stone"]` | A filler per dimension, which overrides `flatBedrockFiller` for the dimensions named |
| `flatBedrockBiomeTypes` | list | empty | Biome types to flatten bedrock in, alongside flatBedrockBiomes, by biome tag such as minecraft:is_ocean or by 1.12.2 type name such as OCEAN. flatBedrockBiomesAreBlacklist covers them too |
| `flatBedrockRetrogen` | boolean | `false` | Flattens the bedrock in chunks that already exist too. Each chunk is done once and remembers it, and it cannot be undone: the original pattern is not recorded anywhere |
| `flatBedrockRetrogenKey` | text | `0000` | Change it to make every chunk eligible for bedrock flattening again |

### Slow ticking far away

*what each group does*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "slowDistantEntities": true,
    "slowedKinds": ["items", "experience", "projectiles"],
    "slowDistance": 192,
    "slowRate": 4,
    "neverSlowed": ["minecraft:armor_stand"],
    "slowRecheck": 20
  }
}
```

`control.entities` decides this group. The slower pace of entities far from every player.

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `slowDistantEntities` | boolean | `true` | Tick entities far from every player less often. Nothing is ever left unticked, only ticked at a slower pace |
| `slowedKinds` | list | `["items", "experience"]` | Which kinds are given fewer ticks: items, experience, projectiles, the last being arrows, tridents, thrown snowballs, eggs, potions, bottles o' enchanting and ender pearls, and llama spit. Anything that thinks for itself is always slowed instead, without being named here: it chooses what to do next less often, and still moves every tick. Machines are never slowed |
| `slowDistance` | int, 64 to 4096 | `192` | How far from the nearest player, in blocks, before a chunk is slowed. The game stops telling a player about most entities beyond 64, so nothing below that |
| `slowRate` | int, 1 to 20 | `4` | One tick in this many is given to a slowed chunk. 1 is no slowing at all, 20 is once a second |
| `neverSlowed` | list | empty | Entities left alone however far away they are, as namespace:name |
| `slowRecheck` | int, 1 to 100 | `20` | How often, in ticks, the distance to the nearest player is worked out again. Every player counts for themselves, so someone alone far away still has their own quiet space around them |

### Land, holds and what the mod says

*what each group does*

`control.chunks` decides this group. The spawn chunk radius, pregeneration, retrogen and the reset, the welcome lines, the says card and the game's toasts.

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `retrogen` | boolean | `false` | Catch existing chunks up on worldgen entries with \"retrogen\": true. Off, chunks that already exist are left alone. Chunks are marked as they generate either way, so turning this on later only touches chunks older than the pack |
| `adoptExistingChunks` | boolean | `false` | Treat chunks that already exist as if this pack generated them, marking them instead of leaving them for retrogen. Turn this on when replacing a mod that already generated the same ore, so retrogen never doubles it. Worldgen entries added later still retrogen into them |
| `saysCard` | boolean | `false` | Show the lines this mod says, the welcome, the land-making note a player joining mid-run gets and the end of the run (the running progress stays on the action bar), and the threat lines, as a card in the lower right corner instead of in chat. The card slides in, stays eight seconds and fades, and shows over an open screen too |
| `saysIcon` | text | empty | An item drawn on the card, e.g. minecraft:compass. Empty draws none |
| `saysColor` | text | empty | The card's background color as hex, e.g. 1E2630. Empty uses a dark slate |
| `saysImage` | text | empty | A PNG from the pack's client assets stretched over the card as its background, e.g. rubyworld:textures/gui/card.png, drawn over the color. Empty draws none |
| `saysBackground` | boolean | `true` | Draw the card's panel, border and color stripe, and the dark backdrop behind the welcome and the hold notes. Off leaves only the text, which keeps its shadow, and saysImage if one is set |
| `saysFont` | text | empty | A font for the card's text, named as namespace:name, e.g. rubyworld:runes for the pack's assets/rubyworld/font/runes.json. Empty uses the RDPL font |
| `toasts` | boolean | `false` | Show the game's toasts, the pop-ups in the upper right corner for advancements, unlocked recipes, tutorial hints and system notices, other mods' toasts included. Off shows none. Takes effect on the next world or server joined. A world template can list the kinds to show instead |
| `pregenOnNewWorld` | int, 0 to 8192 | `0` | How far around the spawn, in chunks, a world has its land made before anybody plays it. The game makes 12 chunks around the spawn on its own, so 12 is the floor and 0 means that floor rather than nothing: the ground the game was going to make anyway is adopted and lit in one organized pass instead of trickling in. Raise it to reach further than the game does |
| `pregenToBorder` | boolean | `false` | Whether a new world has its land made out to its world border instead of a set number of chunks, centered on the border rather than the spawn. A world whose border was never moved in has no border to reach and is passed over |
| `pregenAllDimensions` | boolean | `false` | Make the land of every dimension the server holds, modded ones included, the overworld first and the rest in id order, instead of only those in pregenDimensions. Ones named in pregenDimensionsWhenEntered are still left for their first visitor |
| `pregenResume` | boolean | `false` | Whether a run that was stopped or cut short picks up where it left off next time the world is loaded, rather than starting again |
| `pregenChunksInFlight` | int, 1 to 512 | `32` | How many chunks a land-making run asks the game for at once. More keeps the generation threads busier and the server less responsive to whoever is held watching |
| `pregenBackup` | boolean | `false` | Copy the world to a pristine backup once pregeneration finishes, while the players are still held. The copy is what a reset would restore, and a copy whose packs no longer match is thrown away and kept afresh |
| `resetClearsEntities` | boolean | `true` | Remove every entity that is not a player when the map resets |
| `resetClearsScores` | boolean | `true` | Set every objective the pack keeps back to nothing when the map resets, so a new match starts from zero. Teams themselves are kept |
| `resetClearsInventory` | boolean | `false` | Empty every player's inventory, armor and off hand included, when the map is reset |
| `resetClearsExperience` | boolean | `false` | Set every player's experience back to level zero when the map is reset |
| `spawnChunkRadius` | int, 0 to 1024 | `128` | How far from the spawn point, in blocks, chunks are held loaded whether or not a player is there, rounded to whole chunks as (blocks + 8) / 16 each way, so 128 holds 8 chunks each way. As a world starts, the overworld prepares a square 4 chunks wider each way before the server is ready, 25 by 25 chunks at 128. 0 prepares and holds none, so the spawn area unloads like anywhere else. RDPL holds the chunks with its own tickets, so on 1.21.1 the spawnChunkRadius game rule does nothing while this key is in effect |
| `spawnChunkRadii` | list | empty | A radius for the overworld written as dimension=blocks, as in minecraft:overworld=64, which overrides spawnChunkRadius. Only the overworld has spawn chunks, so an entry for any other dimension changes nothing |
| `welcomeSays` | list | `[WELCOME]` | Welcome lines, shown in green on every login and after pregeneration. A bare entry is the line for everywhere; a dimension=message entry overrides it for that dimension and also greets every arrival there, e.g. minecraft:the_nether=Welcome to the Nether!. An empty message after the = mutes that dimension; an empty list shows nothing. Left at this default it speaks each player's language |
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

*what each group does*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "voidWorld": true,
    "voidWorldDimensions": ["minecraft:overworld"],
    "voidWorldDimensionsAreBlacklist": false,
    "voidPlatformBlock": "minecraft:stone",
    "voidPlatformHeight": 64,
    "voidPlatformSize": 5
  }
}
```

`control.voidWorld` decides this group. Void world generation and its platform.

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `voidWorld` | boolean | `false` | Generate the listed dimensions as empty space with a platform at the spawn point and nothing living, through the generated preset for the vanilla three and through a pack's own dimension files; any other dimension listed is emptied as its land is made. The choice is kept with the world when it is made, so turning it on or off later leaves an existing world as it was |
| `voidWorldDimensions` | list | `["minecraft:overworld"]` | Which dimensions are made void, by id. Empty means none, or every dimension when voidWorldDimensionsAreBlacklist is on |
| `voidWorldDimensionsAreBlacklist` | boolean | `false` | On, the dimensions listed are the ones left alone |
| `voidPlatformBlock` | block | `minecraft:stone` | What the platform is made of |
| `voidPlatformHeight` | int, -2032 to 2031 | `64` | The y the void world platform sits at |
| `voidPlatformSize` | int, 1 or more | `9` | How wide the platform is, rounded down to an odd number so it sits centered on spawn |
| `voidWorld` | text | `default` | Void world generation and its platform [default\|global\|off] |

### The dragon

*what each group does*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "dragonFight": true
  }
}
```

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `dragonFight` | boolean | `true` | Whether the whole thing happens at all: the dragon, its bar, the crystals, the fountain it stands on, and the respawn a player would start with end crystals. Belongs to the `structures` group |

`dragonFight` belongs to the `structures` group and decides whether the whole thing happens at all: the dragon, its bar, the crystals, the fountain it stands on, and the respawn a player would start with end crystals. An emptied end leaves it out unless a pack asks for it, and an ordinary end has it unless a pack says otherwise, so `dragonFight` is worth setting either way round.

### Terrain

*what each group does*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "worldSeed": "Hollow Ridge",
    "worldName": "Ruby World",
    "worldType": "largebiomes",
    "worldTypeExceptions": ["flat", "debug_all_block_states"],
    "generatorOptions": {"seaLevel": 63, "useMansions": false},
    "worldMinHeight": -128,
    "worldMaxHeight": 320,
    "deepStone": "minecraft:blackstone",
    "noiseCaves": "deep",
    "worldSpawn": "0,72,0",
    "worldBorder": 4096,
    "worldTime": 6000,
    "caveRegionPlainWeight": 4,
    "caveRegionCells": 128,
    "caveRegionCellsY": 64,
    "worldGravity": ["0.17", "minecraft:overworld=1.0"],
    "worldFallDamage": ["0.17"],
    "worldJumpStrength": ["1.0"],
    "worldTerminalVelocity": ["1.0"],
    "weatherCeiling": ["minecraft:overworld=128"],
    "cloudHeight": ["minecraft:overworld=384"],
    "worldBelow": ["minecraft:overworld=minecraft:the_nether"],
    "worldAbove": ["minecraft:the_nether=minecraft:overworld"],
    "worldSeamEntities": true,
    "worldSeamBedrock": false
  }
}
```

`control.terrain` decides this group. The world's name and seed at creation, generatorOptions, the cave regions, the cloud height and the seams between worlds.

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `worldSeed` | string | empty | The seed every new world is made with, written the way it would be typed: a number is used as it is, and anything else is turned into one the way the game does. A dedicated server makes its world with it too, and writes it into `server.properties` as `level-seed`. Empty leaves the choice alone |
| `worldName` | text | empty | What a new world is called when the screen for making one opens. Empty leaves it as the game names it |
| `worldType` | text | empty | The world type the shaped world is built on, one of default, largebiomes, amplified or flat, with the 1.12.2 names customized and default_1_1 read as default; flat is a superflat overworld built from the generatorOptions layers, with the pack's cities on it. The shape below (heights, deep stone, sea level, bedrock, void) is generated as a world preset of its own, listed under World Type on the world screen and chosen there whatever was picked. A dedicated server writes it into `server.properties` as `level-type`, naming that preset, or the game's own preset when nothing is shaped, unless `level-type` already names one of the worldTypeExceptions. Empty builds on default |
| `worldTypeExceptions` | list | `["flat", "debug_all_block_states"]` | World types a player picks that the generated preset leaves alone, such as flat or debug_all_block_states. Empty means every choice is replaced |
| `generatorOptions` | text | empty | The overworld's terrain settings as a JSON object, the keys the 1.12.2 customized world type wrote. Read here: seaLevel, useLavaOceans, fixedBiome, and useCaves, useRavines, useDungeons, useLavaLakes, useStrongholds, useVillages, useMineShafts, useTemples, useMonuments and useMansions set to false. With worldType flat it is the layers instead, bottom up, as the 1.12.2 superflat text 3;minecraft:bedrock,59*minecraft:stone,4*minecraft:dirt,minecraft:grass_block;1;village or a list of layers; the number after the layers is the biome, and village, biome_1, mineshaft, stronghold, oceanmonument, lava_lake and decoration after it turn those on. Only applied to a world as it is created. A dedicated server writes it into `server.properties` as `generator-settings`, the flat layers as the game's own flat JSON, unless `level-type` already names one of the worldTypeExceptions. Empty leaves the terrain as the world type makes it |
| `worldMinHeight` | int, -2032 to 2016 | `-64` | The lowest block of the overworld, a multiple of 16 down to -2032. The game's own bottom is -64; lower makes a deep world under the vanilla terrain, solid stone until the worldgen layer carves it or noiseCaves carries the game's caves down. Only applied through the generated preset |
| `worldMaxHeight` | int, -2016 to 2032 | `320` | The block above the overworld's top, a multiple of 16 up to 2032, at most 4064 above worldMinHeight. The game's own top is 320; higher leaves open sky above the vanilla terrain |
| `deepStone` | text | empty | The block the world below the vanilla terrain is made of when worldMinHeight goes under -64, such as a pack's own deepslate. It blends into deepslate across the eight layers under -64 the way deepslate blends into stone. Empty keeps stone |
| `noiseCaves` | text | `off` | Where the game's caves, tunnels, noodles and aquifers carry on when worldMinHeight goes under -64: off keeps the world under the vanilla terrain solid deep stone for the worldgen layer to carve, deep carries them down to the floor with the lava lakes moved to its bottom ten layers, world means the same on this version because the vanilla terrain has them already |
| `worldSpawn` | text | empty | Where every new world spawns, written as x,z or x,y,z. Without a y the world's average ground level is used, one above sea level, or the top of the layers on a flat world, and the game then finds safe footing there as it does for any spawn. Only applied to a world as it is created. Empty leaves the choice to the game |
| `worldBorder` | int, 0 to 60000000 | `0` | How far across, in blocks, the world border stands in every new world. Only applied to a world as it is created. 0 leaves the border where the game puts it |
| `worldTime` | int, -1 to 23999 | `-1` | Lock the overworld's time of day, in ticks, the same figure /time set takes, so 18000 is midnight. The clock stops and never moves: /time set cannot move it, the day still counts on underneath, and removing the setting gives that time back. -1 leaves time running |
| `caveRegionPlainWeight` | int, 0 or more | `4` | The weight of plain, region-less underground against the cave regions' own weights. Higher leaves more of the underground without any region |
| `caveRegionCells` | int, 16 or more | `128` | How wide a cave region cell is in blocks. Cave regions from packs are painted over the underground in cells about this size |
| `caveRegionCellsY` | int, 16 or more | `64` | How tall a cave region cell is in blocks |
| `worldGravity` | list | empty | Scale gravity, as a multiplier of vanilla where 1.0 is unchanged and 0.17 is moon-like. Covers players, mobs, dropped items, falling blocks, arrows, thrown things, TNT and experience orbs. A bare value covers every dimension, and an entry written as dimension=value covers that dimension alone and wins over the bare one. Empty leaves gravity alone |
| `worldFallDamage` | list | empty | Scale fall damage the same way, 0.5 halving it and 2.0 doubling it |
| `worldJumpStrength` | list | empty | Scale jump strength the same way, 1.5 jumping half again as high |
| `worldTerminalVelocity` | list | empty | Scale the fastest a mob or player falls the same way, 0.5 falling at half vanilla's top speed |
| `weatherCeiling` | list | empty | The highest y rain and snow reach, as dimension=y entries. A bare number covers every dimension. Above it rain does not fall, snow does not settle, cauldrons do not fill, lightning does not strike and no precipitation is drawn; below it weather is unchanged. Ice is temperature rather than precipitation, so it still forms above the line. Empty means no ceiling |
| `cloudHeight` | list | empty | The y clouds are drawn at, as dimension=y entries. A bare number covers every dimension. It wins over a pack dimension's own `cloudHeight`. Empty keeps the game's own cloud height, 192 in the overworld |
| `worldBelow` | list | empty | Stack another dimension under this one: falling out of the bottom of the world carries you into the named dimension, arriving under its ceiling at the same x and z, still falling. Entries are written as dimension=target, such as minecraft:overworld=minecraft:the_nether to hang the nether under the overworld; a bare id covers every dimension. Digging through needs the floor's bedrock left out, which worldSeamBedrock decides. Empty means the floor stays the floor |
| `worldAbove` | list | empty | The same for the ceiling: rising past the top of the world carries you into the named dimension, arriving above its floor. Written the same way as worldBelow |
| `worldSeamEntities` | boolean | `true` | Whether dropped items, mobs and other entities ride the world seams too, or only players. Riders and mounts cross one at a time |
| `worldSeamBedrock` | boolean | `false` | Keep the bedrock at a seam boundary anyway. Off, a dimension whose floor or ceiling carries a worldBelow or worldAbove seam generates no bedrock there, so the way through can be dug. Already generated chunks keep whatever they have |

### Server

*what each group does*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "worldGameMode": "creative",
    "worldDifficulty": ["normal", "minecraft:the_nether=hard"],
    "worldLanCommands": false,
    "worldForceGameMode": true,
    "worldPvp": false,
    "worldFlight": true,
    "worldSpawnProtection": 0,
    "worldNether": false,
    "worldCommandBlocks": true,
    "worldIdleTimeout": 30,
    "worldMotd": "Ruby World",
    "worldMaxSize": 10000,
    "worldStructures": true,
    "worldSpawnMonsters": true,
    "worldSpawnAnimals": true,
    "worldSpawnNpcs": false,
    "worldViewDistance": 12,
    "worldSimulationDistance": 8
  }
}
```

`control.server` decides this group: the lines of `server.properties` a pack may set, with the game mode, the difficulty and commands on a world opened to LAN. On a dedicated server every value a pack sets here is written into `server.properties` as the server starts, so the file names what is in force, and the ones the server has already read are set on it as well. A single player world takes what an integrated server has, as each row says. Empty, or `-1` for a number, leaves the server's own value, and with `control.server` at `off` every line stays as the server has it.

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `worldGameMode` | text | empty | Which way every new world is started, one of survival, hardcore, creative, adventure or spectator. Hardcore is survival where death ends the world, save wide, the same as the choice on the world screen. Empty leaves it as whoever made the world chose. The world screen offers only survival, hardcore and creative, so adventure and spectator are set as the world is made. A dedicated server sets every world to its server.properties mode at each start, so there the pack's mode is written into server.properties (gamemode and hardcore) before the world loads |
| `worldLanCommands` | boolean | `true` | Whether a player opening a single player world to LAN may turn commands on for everyone who joins. `false` grays out the Open to LAN screen's Allow Cheats button and holds it at off, and the world is opened without commands however it is asked for, `/publish` included |
| `worldDifficulty` | list | empty | Lock the difficulty, one of peaceful, easy, normal or hard. A bare difficulty covers every dimension, and an entry written as dimension=difficulty, such as minecraft:the_nether=hard, covers that dimension alone and wins over the bare one. The world's own setting is left as it was and comes back when the entry is removed. A dedicated server writes the overworld's difficulty into `server.properties` as `difficulty`. Empty leaves it as chosen |
| `worldForceGameMode` | boolean | empty | Whether a player who joins is put back in the server's game mode every time, the `force-gamemode` line. A world opened to LAN already does that, and `false` stops it there too |
| `worldPvp` | boolean | empty | Whether players can hurt each other, the `pvp` line. A single player world takes it too |
| `worldFlight` | boolean | empty | Whether a player flying in survival is left alone instead of kicked, the `allow-flight` line. A single player world takes it too |
| `worldSpawnProtection` | int, -1 or more | `-1` | How many blocks around the spawn point only operators may build in, the `spawn-protection` line, 0 for none. Only a dedicated server protects its spawn |
| `worldNether` | boolean | empty | Whether the Nether can be entered, the `allow-nether` line. `false` closes it in a single player world too |
| `worldCommandBlocks` | boolean | empty | Whether command blocks run, the `enable-command-block` line. A single player world runs them already, and `false` turns them off there too |
| `worldIdleTimeout` | int, -1 or more | `-1` | How many minutes a player may stand idle before being kicked, the `player-idle-timeout` line, 0 for never. A single player world takes it too |
| `worldMotd` | text | empty | The line shown under the server's name in the server list, the `motd` line. A single player world opened to LAN shows it in place of the owner and world name |
| `worldMaxSize` | int, -1 to 29999984 | `-1` | The farthest out, in blocks from the middle, a world border may ever reach, the `max-world-size` line. A single player world takes it too |
| `worldStructures` | boolean | empty | Whether a new world generates structures, the `generate-structures` line and the Generate Structures choice on the world screen. Only applied to a world as it is created |
| `worldSpawnMonsters` | boolean | empty | Whether hostile mobs spawn, the `spawn-monsters` line. `false` stops them in a single player world too |
| `worldSpawnAnimals` | boolean | empty | Whether animals spawn, the `spawn-animals` line. `false` stops them in a single player world too |
| `worldSpawnNpcs` | boolean | empty | Whether villagers spawn, the `spawn-npcs` line. `false` stops them in a single player world too |
| `worldViewDistance` | int, -1 to 32 | `-1` | How many chunks out a dedicated server sends the world to each player, the `view-distance` line. A single player world follows the render distance instead |
| `worldSimulationDistance` | int, -1 to 32 | `-1` | How many chunks out a dedicated server keeps the world ticking around each player, the `simulation-distance` line. A single player world follows its own setting instead |

### Recipes

*what each group does*

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "blockRecipes": true,
    "recipeWhitelist": ["minecraft", "mypack"],
    "blockedRecipeMods": ["tconstruct"],
    "recipeMatch": "recipe",
    "blockFurnaceRecipes": true,
    "furnaceWhitelist": ["minecraft", "mypack"],
    "blockedFurnaceMods": ["tconstruct"],
    "logBlockedRecipes": true
  }
}
```

`control.recipes` decides this group. Crafting and furnace recipe blocking and their whitelists. Furnace blocking takes blast furnace, smoker and campfire recipes with it, since 1.12.2 kept every cooking recipe in the one furnace list. Stonecutter and smithing recipes are never blocked, since 1.12.2 had none.

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `blockRecipes` | boolean | `false` | Removes every crafting recipe except those of the mods in `recipeWhitelist`. Nothing is exempt by default, so list your own pack's namespace to keep its recipes |
| `recipeWhitelist` | list of mod ids | `["minecraft"]` | The mods whose crafting recipes survive |
| `blockedRecipeMods` | list of mod ids | empty | Mods whose crafting recipes are removed outright, whatever the whitelist says |
| `recipeMatch` | `recipe`, `output` or `both` | `recipe` | Where the mod id is read from when crafting recipes are blocked: the recipe's own name, the item it makes, or either, which blocks when either matches and spares when either is whitelisted |
| `blockFurnaceRecipes` | boolean | `false` | The same for furnace recipes, the mod being read from the item produced |
| `furnaceWhitelist` | list of mod ids | `["minecraft"]` | The mods whose furnace recipes survive |
| `blockedFurnaceMods` | list of mod ids | empty | Mods whose furnace recipes are removed outright |
| `logBlockedRecipes` | boolean | `true` | Logs a per-mod count of what was blocked |
| `furnace` | boolean | `true` | Apply furnace/*.json files, which add and remove furnace smelting recipes **Config only.** |
| `removals` | boolean | `true` | Apply recipe_removals/*.json files, which delete crafting recipes by name, namespace or output **Config only.** |
| `skipMissingItems` | boolean | `true` | Skip recipes that use an item which is not registered, instead of letting them fail. The count is logged once **Config only.** |

### Commands

*what each group does*

`control.commands` decides this group. Who may run the mod's own commands: the goto permission levels.

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `gotoLevel` | int, 0 to 4 | `3` | The permission level needed for /rdplserver goto <name>, which carries the sender to the nearest one. 3 is an operator, the level every other part of the command sits at. 2 also lets a command block run it, so a pack can put the jump on a button or a pressure plate without handing anybody the rest of the command. 0 lets any player type it. The other parts of /rdplserver stay at 3 whatever this says |
| `gotoNextLevel` | int, 0 to 4 | `3` | The permission level for /rdplserver goto <name> next, which passes over the one it last carried the sender to and finds another. Same scale as gotoLevel |
| `gotoBackLevel` | int, 0 to 4 | `3` | The permission level for /rdplserver goto <name> back, which returns the sender to the one before. Same scale as gotoLevel |
| `gotoPlaceLevels` | list | empty | Permission levels for single places, as name=level entries, one per line, overriding the three settings above for that place alone and in all three of its forms. The name is what you would type after goto, so a vanilla one such as Village or Mansion, or a name a pack registered for its own structures with locateAs. Same scale: 3 an operator, 2 also a command block, 0 anybody. A pack can then open the way to its own ruins while every vanilla structure stays shut, or the other way about. A name nothing has registered is ignored with a note in the log |

### Worldgen, config only

*what each group does*

These `worldgen` keys belong to no group and are the config's alone.

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `worldgenDebug` | boolean | `false` | Write the debug lines other messages refer to into logs/rdpl.log, such as which pack served a file and what each command did. Very verbose |
| `worldTemplate` | text | `auto` | Which world template's settings apply. A pack adds one in worldtemplates/*.json and you name it here as namespace:name. 'auto' picks the template from the highest priority pack. Empty uses none |
| `tellWorldType` | boolean | `true` | Tell a player in chat, as they join a world made with the generated preset, which template shaped it. A pack cannot set this |
| `worldBorderLimit` | int, 1 to 60000000 | `60000000` | The widest border a pack is allowed to ask for through worldBorder. A pack asking for more is refused and the border is left where the game puts it. A pack cannot set this |
| `retrogenKey` | text | `0000` | Change this to make every chunk eligible for retrogen again, for every worldgen entry. New veins are added on top of what is already there |
| `retrogenChunksPerTick` | int, 1 or more | `2` | How many already generated chunks to catch up per tick. Higher is faster but stutters more |

### The `packs` category

*what each group does*

How pack folders are found and served. Config only.

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `rootDirectory` | text | `rdploader` | Folder packs are loaded from, relative to the .minecraft directory. An absolute path also works. Requires a restart |
| `overrideResourcePacks` | boolean | `true` | Insert the asset pack above the player's selected resource packs and the world's own data packs. A pack named RDPLO... always overrides, RDPLN... never does |
| `warnOnCaseMismatch` | boolean | `true` | Warn when a file only matches because the filesystem is case-insensitive. Such packs break on Linux |
| `logContents` | boolean | `false` | Log every pack found and how many files it provides |
| `traceUnresolvedVariables` | boolean | `false` | Log a stack trace the first time a file with a '#' in its name is requested, naming whatever asked for it |

### The `content` category

*what each group does*

Blocks, items, fluids and everything else packs define. Config only.

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `load` | boolean | `true` | Register the blocks, items, fluids, materials and creative tabs that packs define, and load their exposures. Requires a restart |
| `vanillaClients` | boolean | `false` | Serve plain vanilla clients: nothing from any pack is registered, no blocks, items, fluids or creative tabs, and no exposures load, so a client without the mod can join. Everything that lives on the server alone still applies. Requires a restart |
| `sounds` | boolean | `true` | Register the sound events named by sounds/*.json, so packs can ship their own audio |
| `fuels` | boolean | `true` | Apply fuels/*.json files, which give items a furnace burn time |
| `potions` | boolean | `true` | Register the potion effects and potion types described by potions/*.json and potion_types/*.json in packs. Requires a restart |
| `brewing` | boolean | `true` | Apply brewing/*.json files, which add brewing stand recipes |
| `villagers` | boolean | `true` | Register the villager professions described by villagers/*.json and apply the trades in trades/*.json. Requires a restart |
| `entities` | boolean | `true` | Register the entity variants described by entities/*.json in packs. Requires a restart |
| `overrides` | boolean | `true` | Apply overrides/<namespace>/<name>.json files, which change properties of blocks, items and potion types that already exist, vanilla or modded |
| `disabled` | boolean | `true` | Apply disabled/*.json files, which take blocks and items out of play: no creative tab, JEI entry, recipe, loot, trade, tag, placing, use or pick-up, and stacks of them are deleted |
| `hardness` | boolean | `true` | Apply hardness/*.json files, which give a group of blocks a mining time and blast resistance multiplier, rolled per block position |
| `shovelPaths` | boolean | `true` | Let a shovel turn blocks marked behavesAs path into a path, and revert a path while sneaking |
| `shovelPathBecomes` | text | empty | What a shovel turns those blocks into. Empty uses the dirt path |
| `shovelPathReverts` | text | empty | What sneaking with a shovel turns a path back into. Empty uses dirt |
| `hoeTilling` | boolean | `true` | Let a hoe till blocks marked behavesAs till |
| `hoeTillsInto` | text | empty | What a hoe turns those blocks into. Empty uses farmland |
| `caneMaxHeight` | int, 1 to 255 | `3` | How tall vanilla sugar cane grows. Vanilla is 3. Pack defined cane blocks use their own growth section and ignore this |
| `cactusMaxHeight` | int, 1 to 255 | `3` | The same for vanilla cactus |

### The `data` category

*what each group does*

Loot, functions and registry names. Config only.

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `lootInjections` | boolean | `true` | Apply loot_injections/*.json files, which add pools to loot tables that already exist instead of replacing the whole table |
| `playerLoot` | boolean | `true` | Apply player_loot/*.json files, which roll a loot table when a player dies and drop what it makes, on top of or instead of the inventory |
| `registryRemaps` | boolean | `true` | Apply registry_remap files, which rename a registry entry so worlds saved before the rename keep their blocks and items instead of losing them |
| `anvils` | boolean | `true` | Apply anvils/*.json files, which let an anvil put named enchantments on an item for a level cost, earn an advancement as it is taken, and hold an item back from use until then |
| `blockDrops` | boolean | `true` | Apply block_drops/*.json files, which add to or replace what a block drops whenever it breaks, experience included, for blocks a pack does not own |
| `functions` | boolean | `true` | Load .mcfunction files from packs, so they work in every world |

### The `tweaks` category

*what each group does*

Small changes to how vanilla behaves. Config only; see [Bonus: vanilla tweaks](#bonus-vanilla-tweaks).

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `promptLeafDecay` | boolean | `true` | Leaves that lose their tree decay within a second instead of waiting on random ticks |
| `lenientPaths` | boolean | `true` | Paths can be made under a block and stay there when one is placed above |
| `unbreakableSpawners` | boolean | `false` | Mob spawners cannot be mined or blown up. Creative mode still removes them. Requires a restart |
| `experimentalWarning` | boolean | `false` | Show the game's experimental settings warning when a world is made or opened. Off answers it as if you had clicked proceed |
| `privacy` | boolean | `true` | Turn off the game's telemetry and chat reporting: no telemetry event is sent or logged, the client signs no chat message, the server keeps no chat session and does not require one, so no message anybody sends can be reported, and the client shows no warning toast that a server does not enforce secure chat. A pack cannot set this. Takes effect on the next world or server joined |
| `darkSplash` | boolean | `true` | Draw the loading screen dark with the pack loader's logo in place of the game's: the logo is swapped as the screen is made, and the game's own Monochrome Logo option is turned on when it is still off, which takes effect at the next start. Off leaves the option as it is |

---

# Other mods

## Blast Plaster integration

*other mods*

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
  "blockConversions": ["minecraft:stone=minecraft:cobblestone@0.75", "#minecraft:logs=minecraft:stripped_oak_log@0.5"],
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
| `blockConversions` | list of rules | What a blasted block turns into instead of returning as it was, so a build wears down one step per blast |

`blockConversions` decides what a blasted block turns into instead of coming back as it was. A rule reads `<source>=<result>[@chance]`: the source is a block id, or a block tag with a leading `#`; the result is a block id, or `nothing` to leave the space empty; the chance runs 0.0 to 1.0 and defaults to 1.0. The first matching rule wins, so specific rules go above broad ones, and a block that is already some rule's result is never converted again — a wall gives up one step per blast rather than wearing away to nothing.

**Fully vanilla appearance:** `EJECT_DROPS` plus `healFullTrees`, `enableFakeTossedBlocks`, `enableExplosionFlash`, `enableExplosionSmoke`, `preventMobDrops` and `playerTNTAlwaysDrops` all off. Each key is per-dimension-capable.

**Vanilla clients** see nothing unusual. The flash is the only feature that places a block, so with `vanillaClients` set it is forced off; everything else is particles and items a plain client understands.

Not pack keys: Blast Plaster's debug logging and its log-to-leaves pairing (tree identification must be one answer game-wide). Both stay in Blast Plaster's own config.

---

# Reference

## Value lists

*reference*

### Accepted names

*value lists*

These are the names the parser accepts wherever the tables above say "one of the materials", and so on. Anything unrecognized is logged and replaced with the default.

**Block materials.** `air`, `grass`, `ground`, `wood`, `rock`, `iron`, `anvil`, `water`, `lava`, `leaves`, `plants`, `vine`, `sponge`, `cloth`, `fire`, `sand`, `circuits`, `carpet`, `glass`, `redstone_light`, `tnt`, `coral`, `ice`, `packed_ice`, `snow`, `crafted_snow`, `cactus`, `clay`, `gourd`, `dragon_egg`, `portal`, `cake`, `web`, `piston`, `barrier`, `structure_void`. The game itself no longer has materials; each name does what that material did on 1.12.2: it sets the map color, whether the block needs a tool to drop anything, how pistons treat it, whether lava sets it alight, whether flowing liquid washes it away and whether a placed block replaces it.

**Sound types.** `wood`, `ground`, `plant`, `stone`, `metal`, `glass`, `cloth`, `sand`, `snow`, `ladder`, `anvil`, `slime`.

**Map colors.** `air`, `grass`, `sand`, `cloth`, `tnt`, `ice`, `iron`, `foliage`, `snow`, `clay`, `dirt`, `stone`, `water`, `wood`, `quartz`, `adobe`, `magenta`, `light_blue`, `yellow`, `lime`, `pink`, `gray`, `silver`, `cyan`, `purple`, `blue`, `brown`, `green`, `red`, `black`, `gold`, `diamond`, `lapis`, `emerald`, `obsidian`, `netherrack`.

**Render layers.** `solid`, `cutout`, `cutout_mipped`, `translucent`. Left empty, the block picks one to suit its type.

**Rarities.** `common`, `uncommon`, `rare`, `epic`.

**Torch particles.** `none`, `flame`, `colored`. `colored` uses `particleColor`.

**Tool classes.** `pickaxe`, `axe`, `shovel`, `hoe`, `sword`.

**Armor slots.** `head` or `helmet`, `chest` or `chestplate`, `legs` or `leggings`, `feet` or `boots`.

**Tints.** `biome`, `none`, or a six digit hex color. Colors anywhere in a definition are hex, with or without a leading `#`.

**Behaviors** for `behavesAs`. `till`, `path`, `bush`, `animals`.

**Plant types** for `plantTypes`. `plains`, `desert`, `beach`, `cave`, `water`, `nether`, `crop`. 1.20.1 only; 1.21.1 reads the key and ignores it.

**Biome types**, the words that stand for a biome tag wherever a table says "list of biome types", in `biomeTypes`, a biome's `types`, a template's `roles` and a `biomes` section: `ocean`, `deepocean`, `beach`, `river`, `mountain`, `mesa`, `hills`, `coniferous`, `jungle`, `forest`, `savanna`, `overworld`, `nether`, `end`, `hot`, `cold`, `sparse`, `dense`, `wet`, `dry`, `spooky`, `dead`, `lush`, `mushroom`, `magical`, `rare`, `plateau`, `modified`, `water`, `desert`, `plains`, `swamp`, `sandy`, `snowy`, `wasteland`, `void`. The vanilla words map onto `minecraft:is_*` tags and the rest onto the convention tags, `forge:is_*` on 1.20.1 and `c:is_*` on 1.21.1. A tag written out, `minecraft:is_forest` or `#minecraft:is_forest`, is taken as it is. Case does not matter, so the 1.12.2 `FOREST` still reads.

**Roles** for a world template's `roles`. Any biome type word above: each names a biome that fills the biomes carrying that tag once blocking has removed them, so `"ocean": "mypack:ruby_ocean"` puts the ruby ocean wherever an ocean was blocked.

**Structures** for a world template's `structures` and for the `structures` group's own lists: the 1.12.2 names `villages`, `mineshafts`, `strongholds`, `temples`, `monuments`, `mansions`, `netherbridges` and `endcities`, or any structure set the game or a mod ships, such as `pillager_outposts`, `ancient_cities`, `trail_ruins`, `shipwrecks`, `ocean_ruins`, `ruined_portals`, `nether_fossils`, `buried_treasures`, `desert_pyramids`, `jungle_temples`, `igloos`, `swamp_huts`, `woodland_mansions`, `ocean_monuments`, `nether_complexes`, `end_cities`. A 1.12.2 name is read as the sets it stood for, so `temples` is the pyramids, the jungle temples, the igloos and the swamp huts together. The 1.12.2 populate names are read too, as the parts of this version's world they stand for: `caves` the cave carvers (the noise caves are `noiseCaves`), `ravines` the canyons, `dungeons` the monster rooms, `lavalakes` the lava lakes, `netherlava` the Nether's open lava springs, `fire` the Nether's fire patches, `glowstone` its glowstone, `ice` the frozen top layer and `animals` the animals placed as a chunk is made. `waterlakes` is accepted and does nothing, since this version has no water lakes.

**Creature types** for biome spawns and rates. `creature`, `monster`, `ambient`, `water`. An entity variant's spawn also takes this version's other lists by name, such as `water_ambient` or `underground_water_creature`.

**Ore types** for `oreTypes`. `COAL`, `IRON`, `COPPER`, `GOLD`, `REDSTONE`, `DIAMOND`, `LAPIS`, `EMERALD`, `QUARTZ`, `DIRT`, `GRAVEL`, `DIORITE`, `GRANITE`, `ANDESITE`, `TUFF`, `CLAY`, `SILVERFISH`, `CUSTOM` for any other ore.

**Damage types** for an entity variant's `immuneTo`, in any case and with or without underscores. The 1.12.2 names, each covering what it covered there: `inFire` (a campfire too), `onFire` (a fireball nobody shot too), `lava`, `hotFloor`, `inWall` (the world border too), `cramming`, `drown`, `starve`, `cactus`, `fall`, `flyIntoWall`, `outOfWorld` (`/kill` too), `generic`, `magic`, `indirectMagic`, `wither`, `anvil`, `fallingBlock`, `dragonBreath`, `fireworks`, `lightningBolt`, `thorns`, `arrow`, `fireball`, `thrown`, `mob` for a creature's blow, a llama's spit, a shulker's bullet or a wither's skull, `player` for a player's blow, `explosion` for a blast nobody set off, such as a bed, and `explosion.player` for a blast a creature or a player set off, a creeper or lit TNT. This version's own damage has names as well: `fire` for either kind of burning, `lightning`, `void`, `freeze`, `dryOut` and `sweetBerry`. Anything else is read as a damage type id, `minecraft:sonic_boom` or a mod's own.

**Sound names** for an entity variant's `sounds`, a lockbox's `openSound` and a portal's `sound`: any registered sound event, the game's, a mod's or one a pack adds through `sounds`. A 1.12.2 name is read as the name that sound has now, so `entity.endermen.scream` plays `entity.enderman.scream`, `block.cloth.step` plays `block.wool.step`, `entity.small_slime.squish` plays `entity.slime.squish_small` and `record.cat` plays `music_disc.cat`. The four parrot imitations this version dropped, of the enderman, polar bear, wolf and zombie pigman, play nothing.

## Folder list

*reference*

Every folder, with its full path and a link to the section that describes it, is in [Where files go](#where-files-go).

## Commands

*reference*

### Your own commands

*commands*

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
| `/rdpl biome`, `biome list [all]` | none | Every biome that can generate, and its id; `all` includes the ones nothing can generate |
| `/rdpl biome here` | none | The biome you are standing in: its name, id and number |
| `/rdpl locate`, `goto`, `vein`, `gate`, `pregen`, `intro`, `team`, `round`, `dimensions`, `oregen` | the server's | Linked. Passed word for word to `/rdplserver`, which decides, so see the table below |

**Which server subcommands are linked, and why the rest are not.** `locate`, `goto`, `vein`, `gate`, `pregen`, `intro`, `team`, `round`, `dimensions` and `oregen` can only ever mean the server's, since only the server knows the world, its players and its rounds, so `/rdpl` hands them over. In single player, tab completion after one of them offers what `/rdplserver` would; on a server, `goto` offers the vanilla structure names. The rest, `reload`, `list`, `which`, `unused`, `config`, `pixelmap` and `biome`, keep their own meaning of your packs and your client. The server's own permission check decides a linked command, so a client can neither cheat it nor be told a fabricated answer.

**Day-to-day editing:** F3+T reloads textures, models and language files, and `/reload` the server's data. Use `/rdpl reload` when you *add* or *delete* a file, since that changes what the folder contains.

### Server commands

*commands*

On a dedicated server, `/rdplserver` does the same for the server's own copy of the folder. The Level column is the permission level a sender needs: `3` is an operator, `2` also admits command blocks, `0` is any player, and `4` is above operator and reaches nobody.

#### Packs and files

*server commands*

| Command | Level | What it does |
| --- | --- | --- |
| `/rdplserver reload` | 3 | Rescan the server's folder and reload everything, then field the teams and objectives again |
| `/rdplserver list` | 3 | Every pack the server loaded, its priority, and what it contains |
| `/rdplserver which <namespace:path>` | 3 | Which pack provides a given file, and which packs it shadows |
| `/rdplserver unused` | 3 | Files in the server's packs that nothing has asked for |
| `/rdplserver config unused` | 3 | Option files in `rdploader/config` that no installed pack defines any more |
| `/rdplserver config prune` | 3 | Delete those files |
| `/rdplserver pixelmap <namespace:path>` | 3 | What a pixel map came out as |

#### World and generation

*server commands*

| Command | Level | What it does |
| --- | --- | --- |
| `/rdplserver oregen` | 3 | Running totals of ore generation that was blocked, per mod and type |
| `/rdplserver generators` | 3 | Running totals of world generators that were blocked, per mod and type |
| `/rdplserver biome list [all]` | 3 | Every biome that can generate on the server, with its number, id and name; `all` includes the ones nothing can generate |
| `/rdplserver biome` | 3 | The biome you are standing in and what the pack does with it: its id, number and name, whether `blockBiomes` is on and which world template is active, and the ground, the block under it and the stone at y 40 |
| `/rdplserver biome here [player]` | 3 | The biome you, or the player named, are standing in: its name, id and number. The console names a player |
| `/rdplserver dimensions` | 3 | Every dimension, including the ones packs added |
| `/rdplserver vein <entry> [radius]` | 3 | Where a `vein` shaped worldgen entry has its veins seeded within that many chunks (default 8) of where it is run, nearest first, whether or not those chunks exist yet. `/rdpl vein` forwards to it |

#### Gate commands

*server commands*

| Command | Level | What it does |
| --- | --- | --- |
| `/rdplserver gate`, `gate list` | 3 | Every gate, its dimension, its scope and whether it is open |
| `/rdplserver gate check <player>` | 3 | Which gates a player has passed |
| `/rdplserver gate grant <player> <gate>` | 3 | Open a gate for a player |
| `/rdplserver gate revoke <player> <gate>` | 3 | Close one again |

#### Pregeneration commands

*server commands*

| Command | Level | What it does |
| --- | --- | --- |
| `/rdplserver pregen <radius>` | 3 | Make every chunk within that many chunks of where it is run. See [Pregeneration](#pregeneration) |
| `/rdplserver pregen status` | 3 | How far along a run is |
| `/rdplserver pregen stop` | 3 | End it |

#### Players, teams and rounds

*server commands*

| Command | Level | What it does |
| --- | --- | --- |
| `/rdplserver intro` | 0 | Let the world intro play again on your next join. Any player may run it, and it only ever clears their own; it is refused when no pack has an intro |
| `/rdplserver team`, `team join [name]`, `team leave`, `team vote <player>`, `team claim` | 0 | The sides a pack has fielded and the ways onto and off them, offered only while a pack fields sides, see [Teams](#teams) |
| `/rdplserver round start`, `round reset`, `round vote yes`, `round vote no` | 0 | Start a round the pack holds in a lobby, reset a running round or call a vote to, and vote in one, as the pack's scoring allows, and only a player starts one; offered to players only while a pack keeps score, see [Scoring](#scoring) |
| `/rdplserver card <rule> [players]` | 2 | Show a [card rule](#cards) to the players named, or to yourself, by its id or file name. `when`, `repeat` and `cooldown` are skipped |
| `/rdplserver reset` | 3 | Put the map back the way a round's end does: everybody is held, the entities swept, the scores wiped, `resetRuns` run, the players put at `resetSendsTo` and released, and a round opens with the starting count, as the reset settings under [Pregeneration](#pregeneration) describe. Not passed through from `/rdpl` |

#### Going to places

*server commands*

| Command | Level | What it does |
| --- | --- | --- |
| `/rdplserver locate <name>` | 3 | The nearest structure a pack placed under that `locateAs` name |
| `/rdplserver goto <structure>` | `gotoLevel`, `3` | Take you to the nearest one nobody has been to yet, looking without generating the land on the way. A place a pack registered with `locateAs` is the nearest one placed, visited or not. `temple` means every scattered feature: desert and jungle temples, witch huts and igloos. Refused while land is being made |
| `/rdplserver goto <structure> next` | `gotoNextLevel`, `3` | Take you onward to the closest one you have not been taken to this session, whether or not it has been visited before. One within eight chunks of you is passed over; for a pack's place it is the nearest more than 128 blocks away |
| `/rdplserver goto <structure> back` | `gotoBackLevel`, `3` | Take you to the one before it, stepping back through where this session has sent you |

### Who may use goto

*commands*

**Opening `goto` up.** Every part of `/rdplserver` needs an operator, level 3, except `intro`, `team` and `round`, which any player may run, as 1.12.2 has them. The three `goto` forms are the one thing a pack decides: each carries a permission level of its own that a pack or the config may lower, separately from the other two and from the rest of the command. A pack that wants `reset` reachable by players puts it on a command block or in a function, which runs at level 3.

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

The value is the permission level a sender needs. `3` (operator) is the default. `2` also admits command blocks, so a pack can put a jump on a button or pressure plate without exposing the rest of `/rdplserver`. `0` opens it to any player. The three settings are independent: for example, `next` open to command blocks for a village tour while `back` stays operator-only. A value below 0 counts as 0 and one above 4 as 4, and an operator is always offered `goto` itself, whatever the settings say.

`gotoPlaceLevels` overrides the three settings for single places, as `name=level` entries, as in the example above. The name is whatever you would type after `goto`: a vanilla one such as `village` or `mansion`, or a name registered with `locateAs` on an imprint entry. Matching ignores case. A level of `4` is above operator and closes that place to everyone, the way to hide one place while the rest of `goto` is open.

An entry sets one level for all three forms of that place. An unlisted place falls back to the three settings above, and an unregistered name never matches. Tab completion follows the same rules, so after `goto` a sender is offered only the places they may actually be carried to.

These sit in the `commands` group, so `control.commands` in the config decides whether a pack may set them at all, and `off` there keeps everything at operator whatever a pack asks for.

## Good to know

*reference*

- KubeJS and CraftTweaker run after RDPL, so their changes still win.
- Recipes, loot tables, advancements and functions are the game's own data files here, so `/reload` picks up an edit and `/rdpl reload` a new file.
- A structure that has already generated stays loaded until you leave the world.
- Filename case matters. If your file's capitalization doesn't match what the game asked for, RDPL still loads it but warns you, because on Linux it wouldn't be found at all.
- Put a `pack.png` in `rdploader` to give the pack an icon. Without one it shows the RDPL icon.
- The folder can be moved or renamed with the `rootDirectory` option in `config/resourcedatapackloader-common.toml`. An absolute path works too, and it needs a restart.
- A model naming a finished vanilla model inherits vanilla's textures too. Parent models such as `cube_all` and `cross` take their textures from the model that names them and are fine.
- The game's telemetry and chat reporting are off while `privacy` in the `tweaks` category is on, which it is by default: nothing is sent, no chat message is signed, a server running the mod keeps no chat session for anyone, and joining a server that does not enforce secure chat shows no warning toast.
- A changed pack option is remembered by the world it changes; when the change leaves content the world holds unregistered, the world is backed up to the game's `backups` folder before it is opened again, and stays closed if that backup fails.

## When something doesn't work

*reference*

**Check `logs/rdpl.log` first.** Everything RDPL does goes there rather than the main log. Advancements, loot tables, recipes, functions, structures and every piece of content are logged with the pack they came from, and anything malformed is logged with the reason.

**Textures and other assets are different.** They're requested far too often to log individually, so instead `/rdpl unused` lists the files in your packs that nothing has asked for. Run it once the game has finished loading. A file with the right path is always requested, so anything listed is usually a typo, but bear in mind some files only load when they're needed, such as languages other than the one you play in.

**A zip without an `assets` or `data` directory inside it is skipped,** and so is any folder in `rdploader`, and the log says so. A zip whose top level is one folder wrapping them is skipped the same way.

**`/rdpl which minecraft:textures/block/stone.png`** tells you exactly which pack is serving a file and what it's shadowing.

**A pack written for 1.12.2 is read through the forward port.** See [Packs written for 1.12.2](#packs-written-for-1122); the log names every file it moved, left out or could not carry.

## Bonus: vanilla tweaks

*reference*

Small changes to how vanilla behaves, each switched in the `tweaks` config category.

| Option | Default | What it does |
| --- | --- | --- |
| `promptLeafDecay` | on | Leaves that lose their tree decay within a second instead of waiting on random ticks |
| `lenientPaths` | on | Paths can be made under a block and stay there when one is placed above |
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

*bonus: vanilla tweaks*

`unbreakableSpawners` gives the mob spawner block bedrock's numbers, an unbreakable hardness and an explosion resistance nothing survives. A player cannot mine one however good the pickaxe, and neither creepers, TNT, nor a pack entity that `explodes` will take one out. Creative mode still removes them, exactly as it still removes bedrock, so a pack author is never locked out of their own build. It requires a restart, since the block's numbers are set as it registers.

**It is the block, not the spawner.** There is no per-spawner switch. The option changes `minecraft:spawner` itself, so it reaches every spawner in the world at once: the vanilla structures that place one, any a mod places, and any your own packs place. A spawner inside one of your `.nbt` templates, placed by an `imprint` entry, is an ordinary spawner block carrying its own block entity, so it is covered the moment the option is on.

## Keys that did not carry forward

*reference*

What a 1.12.2 pack can write that this version does not read, and why. A pack that writes one still loads; the key does nothing. A row that says *not carried yet* was added to 1.12.2 after that part was ported and is still to come forward. Every other row cannot, or need not, come forward on this engine. The table is kept current as the lines move.

| Key | Where | Why |
| --- | --- | --- |
| `meta` | blocks, items, worldgen, block drops | Ids carry no metadata since the flattening. The port runs every `name:meta` through the game's data fixers and drops the key |
| `oreDict` | blocks, items, furnace, fuels | The ore dictionary is gone. The port turns it into `tags` on the convention tags, and a fuel's into `tag` |
| `oreDictionary` | settings | The ore dictionary is gone, so there are no ore dictionary files left to switch off. Tags do its job, and the port writes them from a pack's `oreDict` |
| `modelMeta` | blocks | Models are generated per variant, so there is no metadata to map them by |
| `disableOverrides`, `tolerateMissingInAdvancements` | settings | A data pack replaces a vanilla recipe or advancement by shipping one under the same name |
| `#CONSTANT` item names | recipes | Recipe constants lived in a 1.12.2 mod's `_constants.json`, which no pack carries and no mod of this version has. Name the item or tag the constant stood for |
| `harvestTool` other than `pickaxe`, `axe`, `shovel`, `hoe` and `sword` | property overrides | Tools mine by block tags here, and only those five have one. A tool class a 1.12.2 mod made up has no tag to write, so the block's tools are left alone and the log says so |
| `careers` | villagers | There have been no careers since 1.14, so each career becomes a villager file of its own |
| `career` | trades, entities | No careers; name the profession itself. A trade naming a vanilla 1.12.2 profession with its career goes to the profession that career became |
| `gameLoopFunction` | game rules | The game rule is gone; the `#minecraft:tick` function tag runs a function every tick, and the port writes one |
| `id` | biomes, dimensions | Biomes and dimensions are known by their resource location, never by a number |
| `suffix`, `keepLoaded` | dimensions | The save folder follows the dimension's name. Only the overworld has spawn chunks, so a dimension that must stay loaded takes a `forceload` |
| `baseHeight`, `heightVariation` | biomes | Terrain height belongs to the noise settings, not to the biome |
| `placement.villageSpawn` | biomes | Villagers come with the village structure itself |
| `rubicWorld`, `rubicWorldDimensions`, `rubicWorldDimensionsAreBlacklist`, `verticalCubeLoadDistance`, `cubeGCInterval`, `cubeGenMillisPerRound`, `cubesSentPerTick` | rubic worlds | The rubic world was the 1.12.2 way past a 256-block world. Here a dimension's `minHeight` and `maxHeight` set its size and the chunk system streams it |
| `rubicHeightLimit` | rubic worlds | The rubic world's height ceiling went with it; a dimension's `minHeight` and `maxHeight` set its size instead, with no separate ceiling to raise |
| `regionCacheLimit` | rubic worlds | Its cache of a rubic world's own open region files went with the rubic world; the chunk system here manages its own files |
| `skyStone`, `skyShape`, `skyIslands`, `skyThickness`, `skyHeights`, `skyAnimals` | the deep world, biomes, cave regions | Sky land lived above a rubic world's terrain window, and there is no rubic world to hold it |
| `deepRavines`, `oreVeins` | the deep world | The engine's own terrain already runs below y 0, with ravines and large ore veins of its own |
| `terrainOffset` | settings | There is no fixed vanilla window to shift; a dimension's `minHeight` and `maxHeight` set its floor and ceiling |
| `terrainWorldTypes`, `terrainWorldTypesAreBlacklist` | settings | World types are world presets here, and a world template names its own |
| `biomeSize`, `riverSize`, `dungeonChance`, `waterLakeChance`, `lavaLakeChance`, the ore size, count and height keys and the noise scale keys of a customized `generatorOptions` | settings, dimensions | Biome size and the shape of the land belong to the noise settings, and how often a feature or an ore is placed to its own placed feature, so no one number reaches them. A `fixedBiome` numbered above 39 names a biome this version cannot map |
| `useWaterLakes`, and `lake` and `dungeon` in a superflat text | settings, dimensions | The game has had no water lakes since 1.18. Dungeons on a flat world come with `decoration` and not on their own |
| `inherit` as the `biomes` source of a `flat` or `void` dimension | dimensions | A flat generator holds a single biome, so such a dimension takes the one its superflat text names, or plains |
| another mod's dimension in `flatBedrockDimensions` or `voidWorldDimensions` | settings | Bedrock and void are written into the generated world preset and a pack's own dimension files; a dimension another mod makes is built from files of its own |
| a mod's world type, or `debug_all_block_states`, as `worldType` | settings | World types are world presets now, and the shaped world is built on the game's own default, large biomes, amplified or flat noise. A debug world has no terrain to shape |
| `pregenKeepLoaded`, `pregenPauseAbove`, `pregenMillisPerRound`, `pregenRelightSays`, `hurryWritesAbove` | pregeneration | They tuned the 1.12.2 chunk writer and relight pass. The chunk system here lights land as it makes it and writes on its own schedule |
| `readCofhWorldFiles` | settings | CoFH World and its own file format do not exist on this engine. Translating those files into a pack, as 1.12.2 already recommended, is still the way across |
| `name:meta` in `villagePathLamp*` | villages | Ids carry no metadata, so a lamp block is written with its state in brackets, and block data in braces still reads |
| `harvestTool` naming `shears` or a modded tool class | blocks | A tool here reads block tags, not a class name, so nothing answers to one. Name the modded tool's own block tag under the variant's `tags` |
| another mod's tab label in `creativeTab` | blocks, items, fluids | A tab is known by its id now, so a bare label is read as a tab of the pack's own. Name the mod's tab by its id, such as `modid:main` |
| `/rdpl reload <group>` | commands | The game reloads every resource in one pass, so textures, models, languages, sounds and shaders cannot be reloaded on their own. `/rdpl reload` or F3+T reloads them all |
| `/rdplserver biome find <name>` and `/rdpl biome find <name>` | commands | Vanilla has had its own biome search since 1.19. `/locate biome <name>` finds the nearest one, so there is no need for a pack command that does the same job |
| `modernChestPlacement` | vanilla tweaks | The game has paired chests this way since 1.13: a chest joins a single chest beside it only when both face the same way, and sneaking keeps it single |
| `loadingScreenPercent` | settings | The game's own world loading screen already shows how much of the spawn area is ready |
| `disableOptimizations` | settings | It stood down the 1.12.2 pregeneration and generation optimizations, which were written for that engine and have no counterpart here |
| `fixTinkersModelErrors` | settings | It silenced the model errors the 1.12.2 releases of Tinkers' Construct and Construct's Armory logged for every tool, part and armor piece. The fix reached into those releases, so there is nothing for it to act on here |
| `achievement.` criteria | scoring, functions | Achievements became advancements, which keep no score to count. A `stat.` criterion is read as the statistic it became |
| `toggledownfall`, `stats` | functions | The commands are gone. `weather` names the weather to set, and `execute store` keeps a command's result |
| `gamerule gameLoopFunction`, and a game rule a pack made up | functions | The game decides which game rules exist. The port turns a game rules file's `gameLoopFunction` into the `#minecraft:tick` tag, but a command line setting it is kept for you to remove |
| a name with the data value `-1` or `*` that became several blocks or items | functions: `clear`, `testforblock`, `execute ... detect`, `fill ... replace`, `clone ... filtered` | One test names one block or item now. Name the one meant, or a tag such as `#minecraft:wool` |
| a block state written as `name=value` pairs that is not the block's whole 1.12.2 state, and a data value on another mod's block or item | functions | The data fixers know only whole 1.12.2 states, and another mod's metadata has no flattened name to go to |
| the `footstep` and `take` particles, `locate Temple`, `spreadplayers` with more than one target | functions | The particles are gone, a temple is four structures now, and `spreadplayers` takes one target argument |
