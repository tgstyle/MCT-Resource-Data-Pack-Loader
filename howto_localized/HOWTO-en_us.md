# Resource Data Pack Loader

**One folder that overrides anything Minecraft or a mod provides, defines new content from JSON, and controls what generates, in every world, on clients and servers, with nothing for players to switch on.**

Three working examples. Drop any of them straight into `rdploader` and look at how each file is written.

- [RDPLExamplePack.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExamplePack.zip) covers most features, blocks, items, biomes, a dimension, a world template and every worldgen shape.
- [RDPLExampleOrePackVoid.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleOrePackVoid.zip) makes the overworld an empty void with worldgen hanging in the air, one shape per height band, so each is easy to see on its own.
- [RDPLExampleDeepWorld.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleDeepWorld.zip) makes the overworld a rubic world with 256 blocks of generated world below the vanilla one and 128 above it: the deep stone blend, modern noise caves, ravines, banded ore veins, three cave regions to descend through, and floating islands overhead cut by the same noise.

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
- [Blockstates by type](#blockstates-by-type)
- [Making vanilla treat your block properly](#making-vanilla-treat-your-block-properly)
- [Items](#items)
- [Fluids](#fluids)
- [Materials, tabs, sounds, ore dictionary](#materials-tabs-sounds-ore-dictionary)
- [Furnace recipes and fuels](#furnace-recipes-and-fuels)
- [Potions, potion types and brewing](#potions-potion-types-and-brewing)
- [Exposures](#exposures)
- [Villagers and trades](#villagers-and-trades)
- [Entity variants](#entity-variants)
- [Village plots](#village-plots)
- [Biomes](#biomes)
- [Dimensions](#dimensions)
- [Portals and gates](#portals-and-gates)
- [World templates](#world-templates)
- [Rubic worlds](#rubic-worlds)
- [The deep world](#the-deep-world)
- [Cave regions](#cave-regions)
- [World intro](#world-intro)
- [Game rules](#game-rules)
- [Hardness groups](#hardness-groups)

**Generating it**
- [Worldgen entries](#worldgen-entries)
- [Shapes](#shapes)
- [Spreads](#spreads)
- [Retrogen](#retrogen)
- [Pregeneration](#pregeneration)

**Control**
- [The control layer](#the-control-layer)
- [What each group does](#what-each-group-does)
- [Universal Tweaks](#universal-tweaks)
- [Mo' Villages](#mo-villages)
- [CoFH World](#cofh-world)
- [Lost Cities](#lost-cities)
- [Blast Plaster integration](#blast-plaster-integration)
- [Grave mods](#grave-mods)

**Reference**
- [Value lists](#value-lists)
- [Folder list](#folder-list)
- [Commands](#commands)
- [Good to know](#good-to-know)
- [When something doesn't work](#when-something-doesnt-work)
- [Bonus: vanilla tweaks](#bonus-vanilla-tweaks)
- [Bonus: JEI plugin conflict fix](#bonus-jei-plugin-conflict-fix)
- [Bonus: fewer startup errors](#bonus-fewer-startup-errors)

---

# Getting started

## What it is

Resource Data Pack Loader (RDPL) reads a single folder, `rdploader`, and does three jobs:

- **Overrides.** A file in the folder replaces the one the game or a mod would have loaded. No toggle, no per-world setup, nothing for players to enable.
- **New content.** JSON definitions register blocks, items, fluids, biomes, dimensions, potions and villagers. No Java, no jar.
- **Control.** Block ore, biome, structure or recipe generation, flatten bedrock, set spawn rates, void the overworld, set world defaults.

## Where files go

Every path in this guide is written from `assets/` onward, so `<namespace>/blocks/*.json` is `assets/mypack/blocks/ruby_ore.json` on disk for a pack whose namespace is `mypack`. Each section repeats its own path under its header, with a note on what that path becomes.

| Path | What it holds |
| --- | --- |
| `<namespace>/blocks/*.json` | Block definitions. [Blocks](#blocks) |
| `<namespace>/items/*.json` | Item definitions. [Items](#items) |
| `<namespace>/fluids/*.json` | Fluids, with a block and a bucket. [Fluids](#fluids) |
| `<namespace>/materials/*.json` | Tool and armor materials. [Materials, tabs, sounds, ore dictionary](#materials-tabs-sounds-ore-dictionary) |
| `<namespace>/tabs/*.json` | Creative tabs. [Materials, tabs, sounds, ore dictionary](#materials-tabs-sounds-ore-dictionary) |
| `<namespace>/sounds/*.json` | Sound events. [Materials, tabs, sounds, ore dictionary](#materials-tabs-sounds-ore-dictionary) |
| `<namespace>/oredict/*.json` | Ore dictionary names. [Materials, tabs, sounds, ore dictionary](#materials-tabs-sounds-ore-dictionary) |
| `<namespace>/biomes/*.json` | Biome definitions. [Biomes](#biomes) |
| `<namespace>/worldgen/*.json` | What generates, and where. [Worldgen entries](#worldgen-entries) |
| `<namespace>/caveregions/*.json` | Named regions painted over the underground. [Cave regions](#cave-regions) |
| `<namespace>/dimensions/*.json` | Dimension definitions. [Dimensions](#dimensions) |
| `<namespace>/worldtemplates/*.json` | A whole world's settings in one file. [World templates](#world-templates) |
| `<namespace>/worldintro/*.json` | Pages shown when a player enters the world. [World intro](#world-intro) |
| `<namespace>/gates/*.json` | Conditions on portals and dimensions. [Portals and gates](#portals-and-gates) |
| `<namespace>/gamerules/*.json` | Game rules for new worlds. [Game rules](#game-rules) |
| `<namespace>/entities/*.json` | Entity variants built on entities that already exist. [Entity variants](#entity-variants) |
| `<namespace>/hardness/*.json` | Mining time and blast multipliers for groups of blocks. [Hardness groups](#hardness-groups) |
| `<namespace>/exposures/*.json` | Hazards that expose players near or carrying named blocks and items. [Exposures](#exposures) |
| `<namespace>/overrides/<target>/<name>.json` | Properties of existing blocks, items and potion types, changed in place. [Property overrides](#property-overrides) |
| `<namespace>/villages/*.json` | Plots villages can build. [Village plots](#village-plots) |
| `<namespace>/pathintersects/*.json` | Designs painted where village roads meet. [Village roads](#village-roads) |
| `<namespace>/portalframes/*.json` | Frames a player can build and light. [Portal frames](#portal-frames) |
| `<namespace>/blastplaster/*.json` | What Blast Plaster does after an explosion, per dimension. [Blast Plaster integration](#blast-plaster-integration) |
| `<namespace>/structures/*.nbt` | Templates, for saplings, `imprint` and mod overrides. [What you can override](#what-you-can-override) |
| `<namespace>/recipes/*.json` | Crafting recipes, added or replaced. [What you can override](#what-you-can-override) |
| `<namespace>/recipe_removals/*.json` | Recipes deleted by name, namespace or output. [What you can override](#what-you-can-override) |
| `<namespace>/furnace/*.json` | Furnace recipes added and removed. [Furnace recipes and fuels](#furnace-recipes-and-fuels) |
| `<namespace>/fuels/*.json` | Burn times. [Furnace recipes and fuels](#furnace-recipes-and-fuels) |
| `<namespace>/brewing/*.json` | Brewing stand recipes. [Potions, potion types and brewing](#potions-potion-types-and-brewing) |
| `<namespace>/potions/*.json` | Potion effects. [Potions, potion types and brewing](#potions-potion-types-and-brewing) |
| `<namespace>/potion_types/*.json` | Bottled potions built from those effects. [Potions, potion types and brewing](#potions-potion-types-and-brewing) |
| `<namespace>/villagers/*.json` | Villager professions. [Villagers and trades](#villagers-and-trades) |
| `<namespace>/trades/*.json` | What careers buy and sell. [Villagers and trades](#villagers-and-trades) |
| `<namespace>/loot_tables/*.json` | Loot tables, replaced. [What you can override](#what-you-can-override) |
| `<namespace>/loot_injections/*.json` | A pool added to a table that already exists. [What you can override](#what-you-can-override) |
| `<namespace>/player_loot/*.json` | A loot table rolled when a player dies. [Player loot](#player-loot) |
| `<namespace>/advancements/*.json` | Advancements. [What you can override](#what-you-can-override) |
| `<namespace>/functions/*.mcfunction` | Function files. [What you can override](#what-you-can-override) |
| `<namespace>/registry_remap/*.json` | Old names mapped to new ones. [Registry renames](#registry-renames) |
| `<namespace>/texts/*.txt` | Plain text files, used by the world intro. [World intro](#world-intro) |
| `<namespace>/models/`, `<namespace>/blockstates/`, `<namespace>/textures/`, `<namespace>/lang/` | The usual asset folders. [Models, blockstates and textures](#models-blockstates-and-textures) |

## Reading the tables

Every file is standard JSON. A representative worldgen entry:

```json
{
  "blocks": [
    { "block": "minecraft:wool", "weight": 80, "properties": { "color": "magenta" } },
    { "block": "mypack:ruby_ore", "weight": 20 }
  ],
  "size": { "min": 4, "max": 12 },
  "attempts": 12,
  "maxTemperature": 0.5,
  "sparse": true,
  "replace": ["minecraft:stone", "minecraft:andesite"],
  "dimensions": [0, -1]
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
| block name, item name | `"minecraft:stone"`, metadata as a third part: `"minecraft:stone:3"` |
| `namespace:name` | `"mypack:ruby_ore"` |
| biome name, sound name, tab name | the same quoted `namespace:name` form |
| hex color | six hex digits, `"A0C8FF"`, `#` optional |
| texture path | `"mypack:blocks/ruby_ore"` |
| list of ints | `[0, -1]` |
| list of block names | `["minecraft:stone", "minecraft:andesite"]` |
| list of biome names | `["minecraft:extreme_hills", "mypack:ruby_hills"]` |
| list of dictionary types | `["MOUNTAIN", "FOREST"]` |
| list of mod ids or pack namespaces | `["quark", "mypack"]` |
| list of objects | `[{ "potion": "minecraft:strength", "amplifier": 1 }]`, keys per that object's own table |
| object | `{ "type": "cluster" }`, keys per its own table |
| object of role to biome, of variant name to variant | keys are the first thing, values the second: `{ "ocean": "mypack:ruby_ocean" }` |

Most definitions also accept `requires`, a list of mod ids or pack namespaces that must be present or the file is skipped.

## The one rule

Open the jar, find the file you want to change, and copy its path from `assets` onwards:

```
assets/minecraft/textures/blocks/iron_ore.png        (in the Minecraft jar)
rdploader/assets/minecraft/textures/blocks/iron_ore.png    (your override)
```

The path after `assets` is always identical to the path inside the jar. Nothing is renamed or moved.

## Organizing packs

Loose files work. Grouping works too, as a folder or a zip, and the two behave identically:

```
rdploader/MyTextures/assets/...
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

## Resource packs: who wins

By default RDPL files sit above the resource packs a player selects, so a resource pack cannot override them. Add `O` or `N` after the `RDPL` prefix to decide per pack:

```
rdploader/RDPLO Branding        always wins; resource packs cannot touch it
rdploader/RDPLN BaseTextures    a resource pack can override it
rdploader/RDPL1O Seasonal       priority and override combined
```

Packs without a letter follow the `overrideResourcePacks` config option. `/rdpl list` marks the packs that override. The letter must end the prefix (followed by a space, dash, underscore, or nothing), so `RDPLOverhaul` is a pack named `Overhaul`, not an `O` flag.

## Mod API

A mod can ship RDPL content inside its own jar, so it needs no separate pack. Put a folder named `rdploader` at the root of the jar and lay it out exactly like a pack:

```
thatmod.jar
  mcmod.info
  rdploader/assets/thatmod/blocks/ruby_ore.json
```

What a mod ships is a default, not an override. It loads below every pack in the pack folder, so anything a pack author writes wins over it, and a mod may only supply files under a namespace it declares in its own `mcmod.info`. Files under any other namespace are ignored with a warning, and so is a nested `rdploader` folder inside a namespace, so a mod cannot quietly redefine another mod's content or a pack author's.

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

## Server-side packs

A pack can live on the server alone, with players on plain vanilla clients, under one constraint: **nothing in it may register anything**. Both mod ids accept any remote; the pack decides. A vanilla client plays with the registries it shipped with, so a pack that adds to them must be on both sides.

| Server alone is enough | Needs the pack on the client too |
| --- | --- |
| `worldgen`, `worldtemplates`, `gamerules`, `structures` | `blocks`, `items`, `fluids`, `materials` |
| `recipes`, `recipe_removals`, `furnace`, `fuels`, `brewing`, `oredict` | `potions`, `potion_types`, `sounds`, `tabs` |
| `loot_tables`, `loot_injections`, `player_loot`, `advancements`, `functions` | `biomes`, `dimensions` |
| `gates`, `trades`, `registry_remap` | `villagers` |
| the whole control layer, settings, and pregeneration | `models`, `blockstates`, `textures`, `lang` (client folders — with no client, leave them out) |

The right-hand column is a hard stop: a vanilla client sent to an unknown dimension disconnects, and unknown blocks cannot be described to it. The left-hand column works because everything there either runs entirely server-side or reaches the client through packets vanilla already speaks (server-filled crafting result slot, ordinary advancement packets, status-message gate refusals, and a pregeneration hold made of vanilla game mode/title/teleport packets).

`worldtemplates` is server-side with one exception: **`rubicWorld` cannot be used with `vanillaClients`**. A rubic world is made of cubes, and a client without the mod cannot be sent them, so it would be turned away at login or see nothing at all. With both set, new worlds are made plain rather than rubic and the log says why, instead of leaving a server that turns every player away. Switching `vanillaClients` on for a world that was *already* made as a rubic world is the one case that stops the game outright: loading such a save as a plain world would ruin it, so it is left untouched for you to decide.

Setup:

1. Enable `vanillaClients` in the config (`content` category, needs a restart). It enforces the right-hand column: those folders are skipped at load and each skipped file is named in the log, so a slipped block file becomes a log line instead of a refused connection.
2. Keep definitions out of the right-hand folders anyway; skipped files are dead weight. Where the pack references items (a gate's `hold`, `killedDrops`, recipe outputs, trades), name only items vanilla or the server's other both-sided mods provide.
3. Entity variants may stay: attributes, drops and spawns are server-applied, but looks are client-rendered, so vanilla clients see the stock creature with the new behavior. If the look is the point, the pack is not server-side.
4. Install on the server as usual. Nothing goes on players' machines; `/rdpl` will not exist for them.
5. Test with one clean vanilla client join of the same version. Failures are loud — the connection is refused at the door, not quietly broken later.
6. Two accepted cosmetic gaps: server-added recipes craft but do not appear in the recipe book, and behavior-only entity variants wear stock looks.

# Overriding

## What you can override

- **Anything in a mod's assets folder**, textures, models, blockstates, language files, sounds, fonts, splash texts, guide books, manuals
- **Advancements and loot tables**, server side, so they work on dedicated servers too
- **Recipes**, replace a mod's recipe or add your own
- **Structure templates**, the `.nbt` files mods use for generated buildings, under `<namespace>/structures/`
- **Functions**, the `.mcfunction` files under `<namespace>/functions/`
- **Registry renames**, keep old worlds working when a mod renames a block or item
- **Recipe removals**, delete a crafting recipe by name, namespace or output
- **Loot injections**, add a pool to a loot table instead of replacing the whole thing
- **Player loot**, roll a loot table when a player dies, on top of what they were carrying or instead of it
- **Properties of existing blocks, items and potions**, hardness, light, stack sizes, food on anything, a potion's effects, see [Property overrides](#property-overrides)
- **Ore dictionary names, furnace recipes, fuel burn times, creative tabs and sound events**

RDPL is good for replacing one or two recipes, and recipes for your own content should be added in the pack alongside it. For full recipe control across a modpack, CraftTweaker and GroovyScript are the better options, and a file here still replaces the original completely, so to change one ingredient or drop one loot entry, use those.

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

A pack can carry a `config` folder beside its `assets`, holding JSON files of true/false options with their defaults:

    PackA.zip/config/options.json
    { "enableTestingContent": true, "enableLoserBlocks": false }

A file with `"hide": true` at the top level keeps that pack's options out of the options screen and the generated file entirely, while the options still gate content at their defaults. Two things want that: content that is not ready to ship, and template packs, where the options are machinery holding the definitions together rather than a choice anyone should be making. Remove the key to publish them. The same works per option: `"hide": true` inside an option's object hides just that one, so a finished pack can carry a switch for unfinished content, or a template gate, without either showing up:

    { "enablePackB": { "default": false, "hide": true } }

Since a hidden option cannot be flipped, one hidden with its default true is effectively forced on, for content that must stay wired through the option machinery but is not a choice.

An option can also be an object carrying a description, shown under its name in the options screen:

    { "enableTestingContent": { "default": true, "description": "Registers the test blocks and items" } }

On launch the pack's option files become one real config file the user owns, named after the pack, `rdploader/config/PackA.json`, created with the pack's defaults and merged on pack updates so new options arrive without touching what the user already set. Changes apply on the next game start. Options belong to named packs only, a folder or a zip, since the generated file is named after the pack; loose files under `rdploader/assets` have no pack name and carry no options, so wrap loose content in a named folder if it needs a switch.

Any definition's `requires` list can then name an option with a `config:` entry: `"requires": ["config:enableTestingContent"]` registers that content only while the option is true, exactly as a missing mod would skip it. A bare name checks every pack's file and every pack defining it must agree; `"config:PackA:enableTestingContent"` names one pack. An option no pack defines counts as false and is warned about once.

A `file:` entry gates on a file or folder existing under the game folder, for coupling content to something outside RDPL's own packs, such as another mod's resource pack: `"requires": ["file:config/StarMaker/resources/0_jackspace2_celestialpack.zip"]` registers the content only while that exact file is installed. The path is relative to the game folder, always with forward slashes, and may not contain `..`.

### Inheriting definitions

A block or item definition can start from another in the same kind with `"inherits"`, naming any variant's registry name, then override whatever differs:

    { "inherits": "mypack:ruby_ore",
      "variants": { "sapphire_ore": { "meta": 0, "hardness": 4.0 } } }

The child copies every stat of the parent's file and the named variant, file order never matters, chains resolve parent-first, and a circle or a missing parent is logged and leaves the child as written. Fields the child writes replace the inherited value; nested variant properties override one by one, but lists such as `requires` replace whole, so write the full list wanted. Blocks inherit only from blocks and items only from items.

### Block and item templates

A parent can be a pure template that never enters the game, since inheritance reads the definition files themselves, not what registered. Gate the template behind a hidden option that is forced off, and it registers nothing while its stats stay inheritable:

`config/options.json`

```json
{
  "templates": { "default": false, "hide": true, "description": "Never on, parents only" }
}
```

`assets/jacksmod/blocks/ore_template.json`

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
    "ore_template": { "meta": 0, "hardness": 3.0, "resistance": 5.0 }
  }
}
```

`assets/jacksmod/blocks/jacks_ore.json`

```json
{
  "inherits": "jacksmod:ore_template",
  "requires": [],
  "variants": {
    "jacks_ore": { "meta": 0, "hardness": 4.0 }
  }
}
```

The template never registers, while `jacks_ore` registers with the template's material, sound, tool, tab, exp drops and resistance, overriding only hardness. The child must write its own `requires`, here cleared to an empty list, because it inherits the parent's otherwise and would vanish with it.

### Structures at exact places

Vanilla structures pin to exact spots with `structureAt` in the `terrain` settings, as `structure=x,z` entries, one per line: `"structureAt": ["villages=1000,-500"]`. **The x and z are block coordinates, not chunk coordinates**, and the structure generates in the chunk that holds that block. One entry per wanted instance. Its spacing, separation, minimum spawn distance and flat-ground checks all stand aside, so the spot is the pack's responsibility, and two pins closer than a chunk apart put two structures in the same chunk. The structure seats to the ground at its chunk by the usual rules once founded.

An `imprint` entry pins the same way with `"at": [x, z]` in its shape, placing exactly once at those coordinates on the surface when that chunk generates, instead of by chance. It composes with `locateAs`, so a pinned structure can also be found with /locate.

### Finding placed structures

An `imprint` entry with `"locateAs": "Crypt"` registers every structure it places under that name, and `/locate Crypt` then points at the nearest one, with the name offered in tab completion. Only structures that have already generated can be found, since pack structures are placed by chance as chunks are made rather than on a grid the game could predict. The names live in the world's save, so they survive restarts and work on servers. A name registered this way can also be given its own permission with `gotoPlaceLevels`, so a pack decides who may be carried to its own structures separately from the vanilla ones.

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
| `lightOpacity` | `0` to `255` | How much light the block stops |
| `soundType` | one of the sound types | Step, place and break sounds |
| `harvestTool` | tool class | What mines it; `harvestToolLevel`, default `0`, sets the tier |
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

At `overrides/minecraft/planks.json` that makes planks break about as fast as dirt and lets them be eaten. `food` takes `heal` (`1`), `saturation` (`0.6`), `alwaysEdible` (`false`; `true` allows eating on a full hunger bar) and `effects`, whose entries are written exactly like a potion type's. An item that is already food takes new `heal`, `saturation` and `alwaysEdible`; `effects` on one of those is not supported, and the log says so. When the edible item places a block, aim at the sky to eat, since aiming at a block places it: that is vanilla's use order, not a bug.

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

Overrides are live. The original values are remembered before the first change, so disabling the pack and running `/rdpl reload` snaps everything back to what it was, no restart needed; the same happens on every world entry. One file per target: when two packs override the same thing, the later pack's file replaces the earlier one whole, and the log says so.

Two limits worth knowing. A block or item whose own code computes a property ignores the field behind it, so the override applies but changes nothing; vanilla only does this for stairs' blast resistance, but mods are free to do it anywhere. And made-edible items only work on items with no right-click behavior of their own: an item that already does something when used keeps doing that.

Overrides need the pack on the client as well as the server, since mining speed, light and eating all happen on the player's screen, so they are not for server-side packs. `overrides` in the `content` config category turns the folder off entirely.

## Registry renames

`<namespace>/registry_remap/*.json`

The file name is yours to choose, only the folder is read, and several files stack.

When a mod renames one of its blocks or items, worlds saved before the rename lose them. Drop a file here to map the old name to the new one:

```json
{
  "registry": "minecraft:items",
  "mapping": { "oldmod:old_name": "newmod:new_name" }
}
```

The registry is the one the entry belongs to, usually `minecraft:items` or `minecraft:blocks`. Renames chain, so mapping A to B and later B to C sends A straight to C.

## Player loot

`<namespace>/player_loot/*.json`

The file name is yours to choose, only the folder is read, and several files stack.

Vanilla 1.12 gives players no loot table — death drops only the inventory, and there is no table name a pack could override. RDPL adds one, rolled when a player dies:

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

`add` drops the table's items alongside the inventory — use it for kill bounties. `replace` discards the inventory and drops only what the table rolls.

With `rollOnKeepInventory` off, deaths under `keepInventory` (and spectator deaths, which always keep the inventory) roll nothing. Turning it on keeps deaths costly on keep-inventory worlds.

Multiple files stack, each evaluated on its own terms. If any applicable entry is `replace`, the inventory is cleared once before rolling, so an `add` entry alongside it still lands.

The table is an ordinary loot table looked up by name: it can live in the pack at `loot_tables/entities/player.json`, be any vanilla or mod table, and be reached by `loot_injections`. Loot context: the dying player is the looted entity, the killer (if any) is the killing player, and the damage source is set — `killed_by_player`, `entity_properties`, `random_chance_with_looting`, `looting_enchant` and `quality` all behave normally.

One loot function is RDPL's own, usable in any table with a looted entity: `rdpl:killed_name` names the dropped item after the victim. `format` shapes the display name (`%s` is the victim, default just the name), and `tag` instead writes the plain name into an NBT string key for items that read it themselves.

```json
{ "item": "mypack:human_skull", "weight": 1,
  "functions": [ { "function": "rdpl:killed_name", "format": "%s's Skull" } ] }
```

**Grave mods.** Rolled items join the ordinary death drops before any grave mod reads them, so they end up in the grave with everything else (`replace` puts the table's contents in the grave instead of the inventory). Holds for Gravestone, GraveStone Mod, Corail Tombstone and anything else that works from the death's drop list. No setup required.

`dropLoose` bypasses the drop list entirely: the items are placed in the world directly, so grave mods never see them — the inventory goes in the grave, the table's items lie on the ground for the killer. Use it for spoils that belong to the killer rather than the victim's grave. Without a grave mod it changes little. Caveat: the items exist before anything downstream could cancel the drops, so entries that must not survive a canceled death should leave it off.

Set `playerLoot` in the `data` config category to `false` to turn the folder off entirely.

---

# Defining new content

## How definitions work

Alongside the folders that override files, there are folders that describe new things. The path is the identity: a file at `assets/mypack/blocks/ruby_ore.json` registers a block called `mypack:ruby_ore`.

Registration happens at the lowest priority Forge offers, so if a real mod registers the same name, the mod wins and your file is ignored. Nothing here can replace a mod.

**Where the line is.** Anything needing a tile entity, a GUI, an inventory or per-tick logic of its own needs a real mod. Everything short of that is fair game.

### Your namespace is your mod

The namespace you choose is, for every practical purpose, a mod id. Nothing is loaded as a mod and it never appears in the mod list, but everything that reads a mod id reads yours:

- Registry names are `mypack:ruby_ore`, exactly as a mod's would be, and they are written into every saved world that contains them.
- The ore, biome, generator and recipe whitelists in the config match it, so `oreWhitelist = mypack` keeps your ore and blocks everyone else's.
- `/rdpl which`, `/rdplserver oregen` and the reports all group by it.
- JEI, the ore dictionary and other mods' lookups see it the same way.

So pick one name at the start and never change it. Renaming a namespace orphans everything already placed in a world, the same as a mod changing its id, that is what `registry_remap` exists to repair.

This works both ways: `requires` accepts a pack namespace as readily as an installed mod id, so one pack can depend on another and be skipped when it isn't installed.

**A missing mod stops the game, the way a mod's own dependency does.** Every mod id named by a `requires` anywhere in your packs is handed to Forge as a dependency of this mod, before anything loads. If one isn't installed you get the standard Missing Mods screen naming what is needed, on a client or a dedicated server, and nothing generates or registers in the meantime.

A missing *pack* is different. Pack namespaces are not mods, so they never reach that check, the definition is skipped, one line goes to `logs/rdpl.log` naming what was missing, and the game carries on. If a block you expected is not in the creative tab, that log line is the first place to look.

`requires` takes bare ids only. There is no version range syntax, so it can say a mod must be present but not which version.

The mod's own two ids, `resourcedatapackloader` and `resourcedatapackloader_mixin`, are reserved. Defining content under them is ignored and logged, because it would claim ownership of things this mod registers. Overriding this mod's own assets is still fine, only registering content there is not.

Every table below follows the conventions in [Reading the tables](#reading-the-tables).

Most definitions also accept `requires`, a list of mod ids or pack namespaces that must be present or the file is skipped.

## Blocks

`<namespace>/blocks/*.json`

The file's path is the block's registry name, so `mypack/blocks/ruby_ore.json` registers `mypack:ruby_ore`. The keys inside `variants` name that one block's metadata values; they are not blocks of their own.

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
  "modelMeta": 0,
  "itemModel": "state",
  "tint": "biome",
  "plantTypes": ["Plains", "Crop"],
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
  "portal": { "dimension": 12 },
  "variants": {
    "ruby_ore": {
      "meta": 0,
      "hardness": 3.0,
      "resistance": 5.0,
      "light": 0,
      "harvestLevel": 2,
      "rarity": "rare",
      "maxSize": 64,
      "oreDict": ["oreRuby"],
      "drops": [
        { "block": "mypack:ruby", "amount": { "min": 1, "max": 2 }, "bonusChance": [1, 2] }
      ]
    },
    "deep_ruby_ore": {
      "meta": 1,
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
| `door` | Two blocks tall, opens by hand and answers to redstone. Uses one variant, since the rest of the metadata carries the hinge, the facing and whether it is open |
| `trapdoor` | A hinged flap on the top or bottom of a block, opened by hand or by redstone. One variant, the metadata carries the facing, the half and whether it is open |
| `fence_gate` | A gate in a fence line, opened by hand or by redstone, and lowered where it meets a wall. One variant |
| `banner` | A banner on a post or against a wall, sixteen standing rotations, carrying your own design. Registers a second block named `<name>_wall` for the hanging one |
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

### File keys

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `variants` | yes | object of variant name to variant |, | One entry per metadata value. The key names that value in the blockstate, the model path and the lang key. The registry name comes from the file's own path |
| `type` | no | one of the types above | `basic` | Which shape the block takes |
| `material` | no | one of the [block materials](#value-lists) | `rock` | Mining behavior, pistons, fire and liquids |
| `soundType` | no | one of the [sound types](#value-lists) | from the material | Footsteps, breaking and placing |
| `mapColor` | no | one of the [map colors](#value-lists) | from the material | How it looks on a map |
| `harvestTool` | no | `pickaxe`, `axe`, `shovel` | `pickaxe` | Which tool harvests it |
| `harvestToolLevel` | no | 0 to 3 | `0` | 0 wood, 1 stone, 2 iron, 3 diamond |
| `silkHarvest` | no | boolean | `true` | Whether silk touch returns the block itself |
| `opensWith` | no | item id | none | Makes the block a lockbox: breaking it drops the block itself, and right-clicking with the named item consumes one, plays the block's break sound, pays out the variant's `drops` list and removes the block. Any other click shows the action-bar line `tile.<pack>:<block>.<variant>.locked` from the lang files |
| `openSound` | no | sound name | the break sound | What a lockbox plays when opened instead of its break sound |
| `expDrop` | no | object with `min` and `max` | none | Experience dropped when broken without silk touch |
| `creativeTab` | no | tab name | none | The tab it appears in |
| `renderLayer` | no | `solid`, `cutout`, `cutout_mipped`, `translucent` | to suit the type | How it is drawn |
| `opaque` | no | boolean | `true` | Whether it blocks sight and light entirely |
| `fullCube` | no | boolean | same as `opaque` | Whether it fills its whole space |
| `lightOpacity` | no | 0 to 255 | `255` when opaque, else `0` | How much light it absorbs |
| `slipperiness` | no | float | `0.6` | Ice is `0.98` |
| `flammability` | no | int | `0` | How readily fire consumes it |
| `fireSpread` | no | int | `0` | How readily fire spreads from it |
| `explosionResistanceDivisor` | no | float | `1.0` | Divides each variant's `resistance` against explosions |
| `modelBlock` | no | block name | `minecraft:stone` | Block whose model is borrowed when yours has none |
| `modelMeta` | no | int | `0` | Which variant of that model |
| `itemModel` | no | `state`, `item` | `state` | `state` follows the blockstate, `item` looks for its own file |
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

### Variant keys

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `meta` | yes | 0 to 15 |, | The metadata value this variant claims |
| `hardness` | no | float | `1.0` | How long it takes to break. Obsidian is `50`, `-1` is unbreakable |
| `resistance` | no | float | `5.0` | Blast resistance |
| `light` | no | 0 to 15 | `0` | Light emitted |
| `harvestLevel` | no | 0 to 3 | the file's value | Overrides the tool tier for this variant |
| `rarity` | no | `common`, `uncommon`, `rare`, `epic` | `common` | Name color in the tooltip |
| `maxSize` | no | 1 to 64 | `64` | Stack size |
| `oreDict` | no | list of ore dictionary names | none | Ore dictionary names this variant is registered under |
| `drops` | no | list of drops | drops itself | What breaking it yields |

**Metadata is permanent.** The number a variant claims is written into every saved world that contains it. Renumbering or reordering variants later turns placed blocks into something else. Add new variants at the end and never reuse a number.

A `basic` block can hold sixteen variants; a `slab` eight; `log` and `leaves` four, because the axis and decay flags need bits of their own; the single-state types hold one.

### Drops

```json
{
  "drops": [
    { "block": "mypack:ruby", "meta": 0, "amount": { "min": 1, "max": 3 }, "chance": 100, "guaranteed": true, "bonusChance": [1, 2, 3] },
    { "block": "minecraft:coal", "amount": 1, "chance": 25 },
    { "block": "minecraft:diamond", "weight": 1 },
    { "block": "minecraft:emerald", "weight": 4 },
    { "entity": "minecraft:silverfish", "amount": { "min": 1, "max": 2 }, "chance": 15 }
  ]
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `block` | one of the two | block or item name |, | What is dropped |
| `entity` | one of the two | entity name |, | An entity let out when the block breaks, instead of an item |
| `meta` | no | int | `0` | Which variant of it |
| `amount` | no | int or range | `1` | How many |
| `chance` | no | 0 to 100 | `100`, or `0` when `guaranteed` is off | How often the drop happens at all |
| `weight` | no | int | `0` | Above zero, the entry joins a pool that yields exactly one drop. See below |
| `bonusChance` | no | list of ints | none | Extra drops per fortune level, one entry per level |
| `guaranteed` | no | boolean | `true` | Legacy shorthand for `chance`. On is `100`, off is `0` |

Every entry with no `weight` is decided on its own, so a block with three of them can drop all three, or none. Give entries a `weight` and they stop being independent: they form one pool, exactly one of which is chosen each time the block breaks, the odds in proportion to the weights. Above, diamond and emerald share a pool at one to four, so one of the two always comes out and it is emerald four times in five, while the ruby and the coal are decided separately and the silverfish is its own thing again. Items and entities pool separately, so a weighted item and a weighted entity do not compete.

An entry naming an `entity` lets one out where the block stood, facing a random way, and a mob is given its usual spawn treatment for the local difficulty, so it arrives with the equipment and the effects it would have had. `amount` decides how many, `chance` how often, `weight` puts it in the entity pool. It happens as the block breaks, however it broke, so an explosion or a piston sets them loose the same as a pickaxe does. `meta`, `bonusChance` and fortune mean nothing to an entity and are ignored.

A drop naming both a `block` and an `entity` uses the entity and says so in the log.

### Growth

For `crop`, `flower`, `cane` and `vine`.

```json
{
  "growth": {
    "stages": 8,
    "growth": 10,
    "spread": 1,
    "maxHeight": 3,
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
| `growth` | no | int |, | One in N chance per random tick to advance |
| `spread` | no | int | `0` | How far it spreads to neighboring blocks |
| `maxHeight` | no | int | `3` | Cane only. How tall the column grows |
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
    "soil": ["minecraft:grass", "minecraft:dirt"],
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

A `structure` replaces the generated tree with one of your templates, which is the way to build something a generator cannot, and nothing else in the block needs writing:

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
| `log` | no | block name | `minecraft:log` | Trunk block |
| `leaves` | no | block name | `minecraft:leaves` | Leaf block |
| `height` | no | int | `4` | Trunk height |
| `vines` | no | boolean | `false` | Hang vines from the leaves |
| `structure` | no | `namespace:name` | none | Grow into this template instead of a generated tree |

## Models, blockstates and textures

Defining a block or item registers it. What it *looks* like is still an ordinary set of asset files, in the same folders and the same format Minecraft already uses, under your own namespace.

```
assets/mypack/blockstates/ruby_ore.json
assets/mypack/models/block/ruby_ore.json
assets/mypack/models/item/ruby/ruby.json
assets/mypack/textures/blocks/ruby_ore.png
assets/mypack/lang/en_us.lang
```

### Naming the variants

Every block with more than one variant gets a property called `blocks`, and its values are the variant names from the definition. So a block file registering `ruby_ore` and `deep_ruby_ore` needs a blockstate with those two variants:

```json
{
  "variants": {
    "blocks=ruby_ore": { "model": "mypack:ruby_ore" },
    "blocks=deep_ruby_ore": { "model": "mypack:deep_ruby_ore" }
  }
}
```

A block with a single variant keeps the `blocks` property too, so its key is still `blocks=<name>`, but only on the types that have that property at all. Eleven types spend their whole metadata on their shape, hold one variant and carry no `blocks` property, so they key on their own properties alone. [Blockstates by type](#blockstates-by-type) says which is which.

Where the block has properties of its own, they are joined with commas in the order the state lists them, `blocks=ruby_log,axis=y`, `blocks=ruby_slab,half=bottom`, `blocks=ruby_wall,up=true,north=true`. A stairs block has no `blocks` property, so it is keyed by `facing=east,half=bottom,shape=straight` and nothing else. Two properties are left out on purpose: a wall's own variant property, and a leaf block's `check_decay` and `decayable`, so leaves need only `blocks=ruby_leaves`. A banner has no variant property at all, and is keyed by `rotation=0` through `15` standing or `facing=north` on a wall, which [Banners](#banners) covers.

### Blockstates by type

Two things decide what a blockstate file has to hold: whether the type carries the `blocks` property, and what properties it has of its own.

| Type | Registers | Blockstate properties | Variants |
| --- | --- | --- | --- |
| `basic`, `ore`, `falling` | one block | `blocks` | 16 |
| `flower` | one block | `blocks` | 16 |
| `portal` | one block | `blocks` | 16 |
| `fence`, `pane` | one block | `blocks`, `north`, `east`, `south`, `west` | 16 |
| `wall` | one block | `blocks`, `up`, `north`, `east`, `south`, `west` | 16 |
| `slab` | two, `<name>` and `<name>_double` | the half slab `blocks` and `half`; the double `blocks` alone | 8 |
| `log` | one block | `blocks`, `axis`, which is `x`, `y`, `z` or `none` | 4 |
| `leaves` | one block | `blocks` | 4 |
| `stairs` | one block | `facing`, `half`, `shape` | 1 |
| `door` | one block | `facing`, `half`, `hinge`, `open` | 1 |
| `trapdoor` | one block | `facing`, `half`, `open` | 1 |
| `fence_gate` | one block | `facing`, `in_wall`, `open` | 1 |
| `banner` | two, `<name>` and `<name>_wall` | standing `rotation`, `0` to `15`; the wall one `facing` | 1 |
| `ladder`, `torch` | one block | `facing`, and a torch adds `up` to the four walls | 1 |
| `crop` | one block | `age`, always `0` to `7`, whatever `maxAge` says | 1 |
| `cane` | one block | `age`, `0` to `15` | 1 |
| `sapling` | one block | `stage`, `0` to one less than `stages` | 1 |
| `vine` | one block | `up`, `north`, `east`, `south`, `west`, and multipart only | 1 |

Four properties are dropped for you, so write the keys without them: `powered` on doors and gates, `variant` on walls, and `check_decay` and `decayable` on leaves.

A crop keeps vanilla's eight `age` values whatever `maxAge` is, since `maxAge` only decides how far it grows, so its blockstate always writes `age=0` through `age=7`.

Two types register a second block. A slab's `<name>_double` needs a blockstate of its own, keyed on `blocks` with no `half`, and it never gets an item of its own. A banner's `<name>_wall` is covered under [Banners](#banners).

**Vine is the one type that cannot use the Forge format**, since `forge_marker` does not support multipart, so its blockstate is a plain vanilla `multipart` list with the texture baked into the model.

**The Forge format is shorter, and it is what the example pack uses.** A vanilla blockstate spells out every combination as its own key, which for stairs is forty of them. With `"forge_marker": 1` the file lists each property once and the game combines them, so the same forty states are eleven entries:

```json
{
  "forge_marker": 1,
  "defaults": {
    "model": "stairs",
    "textures": {
      "bottom": "mypack:blocks/ruby_brick",
      "top": "mypack:blocks/ruby_brick",
      "side": "mypack:blocks/ruby_brick"
    },
    "uvlock": true
  },
  "variants": {
    "inventory": [{}],
    "facing": { "east": { "y": 0 }, "south": { "y": 90 }, "west": { "y": 180 }, "north": { "y": 270 } },
    "half": { "bottom": {}, "top": { "x": 180 } },
    "shape": {
      "straight": {},
      "inner_left": { "model": "inner_stairs" },
      "inner_right": { "model": "inner_stairs" },
      "outer_left": { "model": "outer_stairs" },
      "outer_right": { "model": "outer_stairs" }
    }
  }
}
```

`defaults` is merged into every entry, a bare model name such as `stairs` means `minecraft:block/stairs`, and `inventory` is the model the item in your hand uses. The three stairs parents, `stairs`, `inner_stairs` and `outer_stairs`, take `bottom`, `top` and `side` textures.

**The connecting types add a submodel per side.** A fence, pane or wall has one boolean per direction, and a `true` glues another model onto the post rather than replacing it:

```json
{
  "forge_marker": 1,
  "defaults": {
    "model": "fence_post",
    "textures": { "texture": "mypack:blocks/ruby_planks" },
    "uvlock": true
  },
  "variants": {
    "blocks": {
      "oak": { "textures": { "texture": "mypack:blocks/ruby_planks" } },
      "birch": { "textures": { "texture": "mypack:blocks/pale_planks" } }
    },
    "north": { "true": { "submodel": { "north": { "model": "fence_side", "uvlock": true } } }, "false": {} },
    "east": { "true": { "submodel": { "east": { "model": "fence_side", "y": 90, "uvlock": true } } }, "false": {} },
    "south": { "true": { "submodel": { "south": { "model": "fence_side", "y": 180, "uvlock": true } } }, "false": {} },
    "west": { "true": { "submodel": { "west": { "model": "fence_side", "y": 270, "uvlock": true } } }, "false": {} }
  }
}
```

The parents are `fence_post` and `fence_side` with a `texture`; `wall_post` and `wall_side` with a `wall`, plus `block` for the no-post case; and `pane_post`, `pane_side`, `pane_side_alt`, `pane_noside` and `pane_noside_alt` with a `pane` and an `edge`. Every one of them wants `"uvlock": true`.

The rest take a single parent. `cube_all` takes an `all`, and is what a `basic`, `ore`, `falling` or `leaves` block wants. `cube_column` takes an `end` and a `side`, which is a `log`, turned by `axis`. `cross` takes a `cross` and is what a `flower`, `cane` or `sapling` wants; a `crop` uses its own per-stage models. A slab needs two models of its own, a bottom half and a top half, since it is drawn as a shape rather than a cube.

**The example pack is the worked reference.** [RDPLExamplePack.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExamplePack.zip) ships a definition, a blockstate and models for every type in the table above, in both the vanilla and the Forge format, so a shape that is not obvious is quicker to copy than to work out.

### Item models

By default the item uses whatever the blockstate gives that variant, so nothing more is needed. Setting `"itemModel": "item"` on the block makes it look for its own file instead, at `models/item/<block>/<variant>.json`.

Items are always that second way round, because every pack item has subtypes:

```
assets/mypack/models/item/ruby/ruby.json
assets/mypack/models/item/ruby/polished_ruby.json
```

The path is the item's registry name, then the variant name.

Fluids need no model at all, one is generated from the `still` and `flow` textures.

### Doors, trapdoors and fence gates

All three spend their whole metadata on the shape they take, so each is a single variant, and each has a few things worth knowing before you write the files.

**They carry no `blocks` property**, so their blockstates are keyed by the shape alone: `facing=east,half=lower,hinge=left,open=false` for a door, `facing=north,half=bottom,open=false` for a trapdoor, `facing=south,in_wall=false,open=false` for a gate. That is 32 keys, 16 and 16.

**`powered` is left out of doors and gates.** Both really have it, and both would otherwise double their blockstates for an axis that changes nothing you can see. It is dropped for you, exactly as the game drops it from its own doors and gates, so write the keys without it. Trapdoors never had it.

**Point the models at the parents that take textures**, not at the finished vanilla ones:

| Type | Parents |
| --- | --- |
| `door` | `block/door_bottom`, `block/door_bottom_rh`, `block/door_top`, `block/door_top_rh` |
| `trapdoor` | `block/trapdoor_bottom`, `block/trapdoor_top`, `block/trapdoor_open` |
| `fence_gate` | `block/fence_gate_closed`, `block/fence_gate_open`, `block/wall_gate_closed`, `block/wall_gate_open` |

A door takes two textures, `bottom` and `top`; the other two take one, `texture`. Both door top models reach for `bottom` to face their upper edge, so declare both in all four files even though the top ones seem to need only one. Gate variants want `"uvlock": true`, as the game's own do.

**Their textures use every pixel, and this is the one that catches people.** A door's wide faces are mapped `[0, 0, 16, 16]`, the whole image, and its narrow edges and its top and bottom come out of the same square: columns 0 to 3 for the sides, 13 to 16 for the top and bottom. A trapdoor is the same, its flat faces the whole image and its four rims taken from rows 13 to 16.

So leave no empty margin. Clear a few columns at one edge, thinking the shape is narrower than the file, and you cut a slit clean through the middle of the face and lose the top and bottom entirely. Draw the frame or the stiles into those edge pixels instead, and they read as trim on the block's own edges.

**Their items differ by type.** A door's is a flat sprite, `item/generated` over its own `textures/items/<name>.png`, since a door in the hand is drawn as a picture rather than a shape. A trapdoor's and a gate's parent a block model instead, the bottom half and the closed gate, which is what the game does with its own.

All three take whatever `material` you give them. A gate is built on a block that fixes itself to wood, so this mod sets the material back to yours as it registers, and a stone gate is mined with a pickaxe like the stone it says it is.

### Banners

A banner is the one type where the shape of the block and the shape of the model part ways, so it is worth setting out in full.

**It registers two blocks.** One definition gives you the standing banner under your own name and a second block named `<name>_wall` for the hanging one. Both need a blockstate; only the standing one gets an item, and that item decides which of the two it places, standing when you click the top of a block and wall when you click a side. You never place the wall block directly and it needs no item of its own.

**The standing one needs a Forge blockstate.** Its property is `rotation`, running `0` to `15`, because a banner turns in sixteenths rather than quarters. A vanilla blockstate cannot express that: its `y` goes through `ModelRotation`, which only accepts 0, 90, 180 and 270 and throws on anything else. Forge's format takes any angle, so the sixteen entries are written as a transform:

```json
{
  "forge_marker": 1,
  "defaults": { "model": "mypack:my_banner" },
  "variants": {
    "rotation": {
      "0": { "transform": { "rotation": { "y": 0 } } },
      "1": { "transform": { "rotation": { "y": -22.5 } } }
    }
  }
}
```

…and so on to `15`, each one `-22.5` degrees further round. The sign matches the game's own banners, which turn by minus the rotation. Build the model facing south, since that is where a banner placed by a player looking south ends up pointing. The wall block is an ordinary vanilla blockstate with the usual four `facing` entries at 0, 90, 180 and 270, since there is nothing fractional about it.

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

**There are no colors or patterns on it.** A pack banner has no tile entity, so nothing carries the layer list vanilla banners keep in theirs. The design is the texture, the same way a door's look is its texture, and one definition is one banner. Dyeing it and stacking patterns on it is not something a pack can reach.

**It takes the `material` you give it.** The block it is built on fixes itself to wood, so this mod sets the material back to yours as it registers, and a stone banner is mined with a pickaxe like the stone it says it is.

### Textures written as pixel maps

A texture can be a JSON file instead of a PNG. Put it where the PNG would have gone with `.json` on the end of the whole name, so `textures/blocks/panel.png.json` answers every request for `textures/blocks/panel.png`. Nothing else changes: models point at `mypack:blocks/panel` as they always did, and the atlas, mipmaps and an animation `.mcmeta` all work, because what the game receives is still a PNG.

```json
{
  "extends": "mypack:textures/blocks/panel_template",
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

**There is no name to declare.** The file's own path is its name, exactly as a PNG's is, so a map at `assets/mypack/textures/blocks/panel.png.json` is `mypack:blocks/panel` in a model and a map at `assets/mypack/textures/items/gem.png.json` is `mypack:items/gem` in an item model. Nothing points at a pixel map specially; a block or an item names its texture the way it always did and never learns which of the two it got. That also means the block and item folders stay apart, as they do for PNGs: `textures/blocks/gem.png.json` and `textures/items/gem.png.json` are two different textures and are cached as two different files.

**Any size you like**, up to 4096 a side, and the two sides need not match. `16x16` is an ordinary block face, `16x32` is the sort of tall strip a door half or an animation wants. The size is checked rather than guessed: give one row per line of pixels and one character per pixel across, or the map is refused and the log names the row and what it found. A character with no color in the palette is left clear, so `.` or a space is a hole.

**Templates are the point of it.** `extends` names another pixel map, as `namespace:path` or a bare path in the same pack, and the file that extends it inherits its `size`, its `rows` and its `palette`. Anything it names itself wins, and it need not name everything, so a whole variant can be a handful of colors:

```json
{
  "extends": "mypack:textures/blocks/panel.png",
  "palette": { "s": "#AA7EB1", "d": "#8B6292", "e": "#6B4A72", "p": "#C5A1CB" }
}
```

That is a complete second texture: the same shape in purpur, and if the shape is ever redrawn in the template every variant follows. A variant may instead give its own `rows` and keep the template's palette, which is the other way round, the same colors in a different pattern. Inheritance runs up to eight deep, a loop is caught and reported, and a map naming a template nothing provides is reported rather than drawn blank.

**Which of two textures is the template** is settled by which one holds more distinctions, not by which was drawn first. A variant gives each character one color, so every pixel the template calls the same character comes out the same color in the variant. An ore drawn on stone therefore cannot inherit the stone's `rows`: the stone calls the speck positions plain stone, and nothing a variant can write splits one character into two. Turn it round and it works. Let the ore be the template, so the stone tones and the ore tones each hold characters of their own, and a second ore is four colors:

```json
{
  "extends": "mypack:textures/blocks/ore_template",
  "palette": { "4": "#768291", "5": "#5E6977", "6": "#66717F", "7": "#848F9D" }
}
```

A variant that really does want a different pattern gives its own `rows`, as above, and then inherits only the palette. That is worth doing when the colors are the point and the shape is incidental; when the shape is the point, put the shape in the template and let the variants name colors.

**A template need not be a texture at all.** A map is only served to the game when its path ends in `.png`, so a template at `textures/blocks/ore_template.json` is invisible to the game and exists purely to be extended, while one at `textures/blocks/ore_template.png.json` would also answer requests for `ore_template.png`. Name a shared shape without the `.png` and nothing can ask for it by accident.

**A template can be a real image instead of a map.** Point `extends` at a PNG that any pack or the game itself provides and the palette changes meaning: keys become the colors already in that image, values the colors to put in their place. Nothing is traced and no `rows` are written, so a pack can recolor a vanilla or mod texture where it stands:

```json
{
  "extends": "minecraft:textures/blocks/coal_ore.png",
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
  "extends": "mypack:textures/items/materials/ingot.png",
  "tint": { "from": "#626669", "to": "#DBDFE2" }
}
```

`from` may be left out, in which case it is black and the tint becomes an ordinary multiply, the same shape as a `tintindex` at render time. The difference is that this one is drawn into the PNG once and cached, so it costs nothing per frame and reaches a texture nothing tints, but it also cannot follow a biome the way `grass` or `foliage` can.

The template stays an ordinary map: open it, look at it, and it draws as the gray it is. Both colors take `#RRGGBB`, `#AARRGGBB` or a leading `0x`, and a value that is neither leaves the map undrawn rather than drawing it in the wrong color. A tint is inherited like everything else and the first one down the chain wins, so a variant's own tint beats the one it extends. It works on an image template too, where it runs after the palette's color swaps.

**A tint is a ramp between two colors**, so it only suits a texture whose tones sit on one. A shape with two unrelated regions, an ore's stone against its specks, is not that, and wants its palette written out instead.

**Knowing what a template's characters mean** is the awkward part of extending one, which is what the `notes` block above is for: a character to a short line, inherited the same way the palette is and never drawn. Label a template's characters and whoever extends it knows which to override.

`/rdpl pixelmap <namespace:path>` then reports what a map actually came out as, which is the reliable way to write a variant without opening every file up the chain:

```
oretest:textures/blocks/ruby_ore.png is 16x16
  built from oretest:textures/blocks/ruby_ore.png.json
  built from oretest:textures/blocks/gem_ore.png.json
  rows come from oretest:textures/blocks/gem_ore.png.json
  1  #C4353F  17 pixel(s)  set by ruby_ore.png.json  ore body, most of every lump
  2  #8E2029   8 pixel(s)  set by ruby_ore.png.json  ore shadow, the darkest tone
  a  #747474  86 pixel(s)  set by gem_ore.png.json   stone, the commonest tone
```

Every character is listed with its color, how many pixels it covers, which file in the chain set it and what that file says it is for. The path may be given the short way, `mypack:blocks/panel`, or in full. A character showing 0 pixels is one the palette names and the rows never use, which is usually a typo in a row.

**Drawn images are kept on disk** in `rdploader/pixelmap-cache`, under a folder per namespace and named after the texture with a hash of its source on the end. The hash covers the whole chain, the map itself and every template above it, so editing a template changes the stamp of every variant that inherits from it and they are all redrawn. When a map is redrawn its older files are swept away.

The folder is also gone over every time the packs are scanned, and any image whose map no pack provides any more is deleted, along with any folder left empty. Rename a texture, drop a pack, delete a map, and its cached image goes with it rather than sitting there for good. Deleting the whole folder costs nothing but the time to draw them again, and it is skipped when packs are scanned, so it is never mistaken for a pack.

A PNG always wins. If both `panel.png` and `panel.png.json` exist, the PNG is served and the map is never drawn, so a generated texture can be replaced by a painted one later without changing anything that points at it.

**Nobody has to write these files by hand.** The repository ships scripts for the whole round trip in [`pixelmap/`](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/tree/1.12.2-1.0-Release/pixelmap): `png_to_pixelmap.py` turns one PNG into a map, `convert_pack.py` does it for every texture a pack holds, and `verify_pack.py` draws a converted pack's maps and compares them to the PNGs they came from, so a conversion can be trusted before the originals are put aside.

### Traps worth knowing

**A blockstate naming a bare vanilla model inherits vanilla's textures too.** `normal_torch`, `ladder`, `wooden_door_*` and `wheat_stage*` all carry their own textures, so a block pointing at one gets vanilla's look no matter what you put in the blockstate. Parent models such as `cube_all`, `cross` and `block/crop` take their textures from the blockstate and behave, as do the door, trapdoor and gate parents listed under [Doors, trapdoors and fence gates](#doors-trapdoors-and-fence-gates).

**`forge_marker: 1` does not support multipart.** A vine blockstate has to be plain vanilla multipart, with the textures baked into the model rather than passed in.

**Names come from the language file.** A block or item shows a raw key until `lang/en_us.lang` gives it one, in the usual `tile.mypack:ruby_ore.name=Ruby Ore` form.

**Single-variant types name themselves twice.** A block that can hold several variants is keyed by its registry name alone, as above. A block whose whole metadata goes on its shape adds the variant name after it, so a door defined in `blocks/my_door.json` with one variant called `my_door` is `tile.mypack:my_door.my_door.name=My Door`. That covers `door`, `trapdoor`, `fence_gate`, `banner`, `stairs`, `ladder`, `torch`, `crop`, `cane`, `sapling` and `vine`. Where such a type has an item of its own, as a door and a banner do, it wants the same key again under `item.` rather than `tile.`.

## Making vanilla treat your block properly

Vanilla checks for its own blocks by identity in a dozen places, so a pack block that should obviously work often doesn't. Two keys cover it.

```json
{
  "material": "ground",
  "plantTypes": ["Plains", "Crop"],
  "behavesAs": ["till", "path"],
  "variants": { "ruby_grass": { "meta": 0, "hardness": 0.6 } }
}
```

**`plantTypes`** lists the Forge plant types your block supports, so saplings, crops and flowers can be planted on it.

**`behavesAs`** makes vanilla treat your block like one of its own:

| Value | What it does |
| --- | --- |
| `till` | A hoe turns it into farmland |
| `path` | A shovel turns it into a grass path |

## Items

`<namespace>/items/*.json`

The file's path is the item's registry name, so `mypack/items/ruby.json` registers `mypack:ruby`. The keys inside `variants` name that one item's metadata values, and each one's model goes at `models/item/ruby/<key>.json`.

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
      "meta": 0,
      "maxSize": 64,
      "rarity": "rare",
      "healAmount": 6,
      "saturation": 0.8,
      "oreDict": ["foodRuby"],
      "potion": "minecraft:speed,600,1"
    },
    "dried_ruby_apple": { "meta": 1, "healAmount": 3, "saturation": 0.4 }
  }
}
```

| Type | What you get |
| --- | --- |
| `basic` | A plain item. Used when `type` is missing |
| `food` | Eaten, with hunger and saturation |
| `drink` | Drunk rather than eaten, returning an empty container |
| `tool` | Pickaxe, axe, shovel or sword from a material |
| `armor` | Helmet, chestplate, leggings or boots from a material |
| `seed` | Plants one of your crops |
| `potion` | Applies your potion effects when used |
| `potion_bottle` | Holds your potion types, and shows them in a creative tab |

A `potion_bottle` lists what it can hold with `potionTypes`, an array of potion type names such as `["mypack:ruby_tonic"]`. One with an empty list registers nothing, and the log says so.

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `variants` | yes | object of variant name to variant |, | One entry per metadata value. The key names that value in the blockstate, the model path and the lang key. The registry name comes from the file's own path |
| `type` | no | one of the types above | `basic` | Which type the item takes |
| `creativeTab` | no | tab name | none | The tab it appears in |
| `material` | tool, armor | material name | none | Which of your materials it is made from |
| `toolClass` | tool | `pickaxe`, `axe`, `shovel`, `sword` | none | Which tool it is |
| `slot` | armor | `head`, `chest`, `legs`, `feet` | none | Where it is worn. `helmet`, `chestplate`, `leggings` and `boots` also work |
| `eat` | food | boolean | `false` | Uses the eating animation |
| `alwaysEdible` | food | boolean | `false` | Can be eaten on a full hunger bar |
| `useDuration` | no | int, ticks | `32` | How long using it takes |
| `attackSpeed` | no | float | to suit the tool class | For `tool`, the attack speed attribute, the way a sword is `-2.4` |
| `cooldown` | no | int, ticks | `0` | For `food`, `drink` and `potion`, how long the item refuses re-use after being consumed |
| `container` | drink | item name | none | What is left behind, such as a bottle |
| `crop` | seed | block name | none | The crop it plants |
| `soil` | seed | block name | `minecraft:farmland` | What it can be planted on |
| `requires` | no | list of mod ids or pack namespaces | none | The file is skipped unless all are present |

Variant keys:

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `meta` | yes | 0 to 15 |, | The metadata value this variant claims |
| `maxSize` | no | 1 to 64 | `64` | Stack size |
| `rarity` | no | `common`, `uncommon`, `rare`, `epic` | `common` | Name color in the tooltip |
| `healAmount` | food | int, half drumsticks | `0` | Hunger restored |
| `saturation` | food | float | `0.0` | Saturation restored |
| `oreDict` | no | list of ore dictionary names | none | Ore dictionary names this variant is registered under |
| `potion` | food, drink | `potion,duration,amplifier` | none | An effect applied when the variant is eaten or drunk. A fourth part, `true`, makes it ambient. A beneficial effect is named in the tooltip |

## Fluids

`<namespace>/fluids/*.json`

The file's path is the fluid's registry name unless `name` overrides it.

```json
{
  "name": "molten_ruby",
  "still": "mypack:blocks/molten_ruby_still",
  "flow": "mypack:blocks/molten_ruby_flow",
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
| `block` | no | object |, | The fluid block. `material` (`water`), `flammability` (`0`), `fireSpread` (`0`), `quantaPerBlock` (`0`), `potions` (none, a list of effects given to whatever stands in it, each written `potion,duration,amplifier` with an optional fourth part `true` for an ambient one) |
| `requires` | no | list of mod ids or pack namespaces | none | The file is skipped unless all are present |

## Materials, tabs, sounds, ore dictionary

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
| `harvestLevel` | no | 0 to 3 | `1` | Tool tier. 0 wood, 1 stone, 2 iron, 3 diamond |
| `durability` | no | int | `250` | Uses before it breaks |
| `efficiency` | no | float | `6.0` | Mining speed. Diamond is 8 |
| `damage` | no | float | `2.0` | Attack damage bonus |
| `enchantability` | no | int | `14` | How good enchantments are. Gold is 22 |
| `repairItem` | no | item name | none | What repairs it in an anvil |
| `reduction` | no | list of four ints |, | Armor points, in the order feet, legs, chest, head |
| `toughness` | no | float | `0.0` | Armor toughness, as diamond has |
| `equipSound` | no | sound name | `item.armor.equip_iron` | Sound when armor is put on |
| `armorTexture` | no | texture prefix | the file name | The worn armor texture |

`<namespace>/tabs/*.json`

The file's path is the tab's name unless `label` overrides it, and blocks and items name it in `creativeTab`.

```json
{ "label": "rubypack", "icon": "mypack:ruby" }
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `label` | no | string | the file name | The tab's id: blocks and items name it in `creativeTab`, and the shown name comes from `itemGroup.<label>` in the lang files |
| `icon` | no | item name | none | The item shown on the tab |

`<namespace>/sounds/*.json`

The file name is yours to choose, only the folder is read, and several files stack.

The vanilla `sounds.json` format, so a pack can ship its own audio.

`<namespace>/oredict/*.json`

The file name is yours to choose, only the folder is read, and several files stack.

Adds ore dictionary names to items that already exist. Every key is an ore dictionary name and its value the items registered under it, so a file has no fixed keys of its own. A pack's own blocks and items name theirs in the variant's `oreDict` instead.

```json
{
  "_note": "ruby equivalents",
  "gemRuby": ["mypack:ruby", "mypack:polished_ruby:1"],
  "oreRuby": ["mypack:ruby_ore", "minecraft:redstone_ore"]
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| an ore dictionary name | yes | list of item names | | The items registered under it. Metadata as a third part, `"mypack:ruby:1"` |
| a name starting with `_` | no | anything | | Skipped, so a file can carry a note to itself |

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
    { "oreDict": "gemRuby", "burnTime": 800 }
  ]
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `item` | one of the two | item name | none | The item that burns |
| `oreDict` | one of the two | ore dictionary name | none | Everything under that name burns |
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
    { "attribute": "generic.movementSpeed", "uuid": "91AEAA56-376B-4498-935B-2F7F68070635", "amount": 0.2, "operation": 2 }
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
| `attributes` | no | list of objects | none | `attribute`, `uuid`, `amount` (`0.0`), `operation` (`0`) |

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
| `effects` | yes | list of objects |, | See below |

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

A pack-defined hazard: named blocks and items expose players standing near them or carrying them, in levels, each level applying effects and periodic damage. One file defines one hazard; several run side by side. The per-key defaults are the numbers Immersive World's radiation uses.

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
  "careers": ["gem_cutter", "appraiser"],
  "texture": "mypack:textures/entity/villager/jeweller.png",
  "zombieTexture": "mypack:textures/entity/zombie_villager/jeweller.png"
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `careers` | yes | list of names | none | The careers this profession offers. A profession with none is refused |
| `texture` | no | texture path | the vanilla villager | How the villager looks |
| `zombieTexture` | no | texture path | the vanilla zombie villager | How it looks once zombified |

`<namespace>/trades/*.json`

The file name is yours to choose, only the folder is read, and several files stack.

```json
{
  "trades": [
    {
      "profession": "mypack:jeweller",
      "career": "gem_cutter",
      "level": 1,
      "maxUses": 12,
      "buy": { "item": "minecraft:emerald", "min": 2, "max": 4 },
      "sell": { "item": "mypack:ruby", "min": 1 }
    }
  ]
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `profession` | yes | profession name |, | Whose trade this is |
| `career` | yes | career name |, | Which career within it |
| `level` | no | int | `1` | Which trade tier it appears at |
| `maxUses` | no | int | `12` | Times it can be used before locking |

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
  "career": 1,
  "baby": 0.05,
  "becomes": [
    { "variant": "mypack:angry_cow", "weight": 95 },
    { "variant": "mypack:little_angry_cow", "weight": 5 }
  ],
  "sounds": { "ambient": "entity.cow.ambient", "hurt": "entity.cow.hurt", "death": "entity.cow.death" },
  "soundVolume": 1.0,
  "soundPitch": 1.0,
  "immuneTo": ["fall", "drown", "explosion", "magic", "cactus", "lava", "wither", "starve", "anvil", "inWall"],
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
  "biomeTypes": ["PLAINS"],
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
| `career` | no | int | random | Which career within that profession, from 1 upwards |
| `baby` | no | boolean or 0.0 to 1.0 | `false` | How often one spawns young, and it stays that way. `true` is always, a number is that share of them |
| `becomes` | no | list | none | Other variants this one may turn into as it spawns, by weight. See below |
| `sounds` | no | object | the base's | `ambient`, `hurt` and `death`, each a registered sound event |
| `soundVolume` | no | number | `1.0` | How loud those sounds are |
| `soundPitch` | no | number | `1.0` | How high they play. Under 1 is deeper, over 1 is squeakier |
| `immuneTo` | no | list of damage types | none | Damage it shrugs off: `fall`, `drown`, `explosion`, `magic`, `cactus`, `lava`, `wither`, `starve`, `anvil`, `inWall` and the rest |
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
| `pathPriorities` | no | object | none | What it will walk through, as `WATER`, `LAVA`, `DANGER_FIRE`, `DOOR_WOOD_CLOSED` and the rest, each a number where a negative means never |
| `egg` | no | boolean or object | `true` | A spawn egg, colored like the egg of the entity it copies. `{ "primary": "AABBCC", "secondary": "112233" }` picks your own colors, `false` leaves the egg out |
| `attributes` | no | object | none | `maxHealth`, `movementSpeed`, `attackDamage`, `knockbackResistance`, `followRange`, `armor`. An attribute the entity does not normally have is given to it |
| `hostile` | no | boolean | `false` | Attacks what it can reach, and fights back when hurt |
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
| `equipment` | no | object | none | `mainhand`, `offhand`, `head`, `chest`, `legs`, `feet`, each an item name |
| `spawns` | no | list of objects | none | `creatureType`, `weight`, `min` and `max`, the same shape a biome uses |
| `biomes` | no | list of biome names | every biome | Where those spawns are added |
| `biomeTypes` | no | list of dictionary types | none | The same, by type |
| `trackingRange` | no | int | `80` | How far away the client is told about it |
| `trackVelocity` | no | boolean | `true` | Send its speed as well as its position. Off saves traffic on things that barely move |
| `trackingFrequency` | no | int | `3` | How often, in ticks |
| `requires` | no | list of mod ids or pack namespaces | none | The variant is left out unless all are present |

**A creature with a shelf life.** `despawnAfter` counts in seconds from the moment a creature first enters the world and takes it away quietly when the time is up: no death, no drops, no sound, exactly as if it had wandered off and been cleared. The clock is written into the creature itself, so it keeps running across a save and reload rather than starting over each time a chunk comes back.

It is its own thing, not a nudge to the rules `despawns` and `persistent` govern. Those two decide whether the game may clear a creature away for being far from anybody; this one is a promise that it goes at a set time regardless. A creature can be `persistent` and still have a shelf life, which is what you want for something summoned for a fight or an event that should not outlive it.

The clock runs on world time, so it pauses when nobody is playing and it does not count the minutes a chunk spent unloaded.

`scale` changes both the model and the hitbox on both sides, so what you see is what you can hit. A creature that changes its own size, an animal growing up or a zombie that is a child, is scaled around whatever size it has chosen, so the two do not fight. `angryScale` swells it while it has a target and returns it to `scale` when it loses one. Since the client is never told what a creature is hunting, the sprinting flag carries that news across, it is set on a variant that uses `angryScale` and on nothing else, so a mod reading sprinting on your variants will see it change. Growing inside a low ceiling is possible, the same way a slime growing is, so keep the difference modest.

A variant drops whatever the entity it copies drops, because the loot table is fixed in that entity's own code rather than looked up by name. `lootTable` points it at a table of your own, which you then supply at `loot_tables/entities/<name>.json` like any other.

A `texture` is bound in place of the one the entity would normally use, whatever renderer it inherits, so it works for modded entities as well as vanilla ones. It has to match the model it is drawn on, since the model is the base entity's, a skin, not a new shape. Layers keep their own textures, so armor still looks like armor on a reskinned zombie.

Armor is only ever drawn on an entity whose renderer has an armor layer, which in this version means the humanoid mobs and villagers. A variant of a cow or a spider can carry armor and gets its protection, but nothing draws it, so `armor` under `attributes` is usually the tidier way to make such a creature tough. `hideArmor` is for the other case: a humanoid that should keep the armor in its slots, for the protection or for a mod that reads them, without it being seen.

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

A file here adds a piece villages can build, alongside the vanilla ones. Two kinds, chosen with `type`.

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
  "crops": ["simplecorn:corn", "minecraft:wheat"],
  "edge": "minecraft:log",
  "soil": "minecraft:farmland",
  "water": true,
  "rowWidth": 2,
  "structure": "mypack:blacksmith_shed",
  "integrity": 100,
  "villagers": 2,
  "villagerEntity": "mypack:jeweller",
  "villagerX": 1,
  "villagerY": 1,
  "villagerZ": 1,
  "ground": "minecraft:dirt",
  "requires": ["mypack"]
}
```

A `farm` is vanilla's field, described rather than coded: a plot of the size you ask for, edged with a block, filled with rows of soil separated by water channels, planted with a crop picked per block from your list.

```json
{
  "type": "farm",
  "weight": 3,
  "width": 7,
  "depth": 9,
  "crops": ["simplecorn:corn"],
  "edge": "minecraft:log",
  "water": true,
  "rowWidth": 2
}
```

A `template` places one of your `.nbt` structures instead, turned to face the village path.

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

| Key | Used by | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `type` | all | `farm` or `template` | `farm` | Which kind of plot |
| `weight` | all | int | `3` | How often this plot is picked against the pack's others |
| `leastCount` | all | int | `1` | Fewest per village, before village size is added |
| `mostCount` | all | int | `4` | Most per village, before village size is added |
| `width` | all | int | `7` | Size across the path |
| `height` | all | int | `4` | Height cleared above the ground |
| `depth` | all | int | `9` | Size away from the path |
| `crops` | farm | list of block names | wheat | Planted one per block, at a random growth stage |
| `edge` | farm | block name | `minecraft:log` | The frame around the plot |
| `soil` | farm | block name | `minecraft:farmland` | What the rows are made of |
| `water` | farm | boolean | `true` | Put a water channel between the rows |
| `rowWidth` | farm | int | `2` | How wide each row of soil is |
| `structure` | template | `namespace:name` | none | The template to place |
| `integrity` | template | 1 to 100 | `100` | Percentage of the template's blocks that appear |
| `villagers` | all | int | `0` | How many people the plot spawns |
| `villagerEntity` | all | `namespace:name` | a villager | Who lives there, such as an entity variant of your own |
| `villagerX` | all | int | `1` | Where they appear, across the plot |
| `villagerY` | all | int | `1` | Where they appear, above the floor |
| `villagerZ` | all | int | `1` | Where they appear, into the plot |
| `ground` | all | block name | `minecraft:dirt` | What is packed underneath on a slope |
| `requires` | all | list of mod ids or pack namespaces | none | The plot is left out unless all are present |

Every pack plot is offered to villages as one entry, so `weight` decides which of your plots is chosen once a village asks for one. Which plot a placement used is written into the village's own data, so it rebuilds correctly on load.

## Biomes

`<namespace>/biomes/*.json`

The file's path is the biome's registry name, so `mypack/biomes/ruby_forest.json` registers `mypack:ruby_forest`. `name` is only what the player is shown.

Every key, shown at once. A real file writes only the ones it needs.

```json
{
  "name": "Ruby Forest",
  "id": 200,
  "types": ["FOREST", "DENSE", "WET"],
  "temperature": 0.7,
  "rainfall": 0.8,
  "rain": true,
  "snow": false,
  "baseHeight": 0.15,
  "heightVariation": 0.25,
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
    "villageSpawn": true,
    "strongholds": false,
    "playerSpawn": true
  },
  "villageType": "oak",
  "minHeight": 100,
  "maxHeight": 156,
  "replaces": ["minecraft:plains", "minecraft:forest"],
  "skyStone": "minecraft:end_stone",
  "skyIslands": 0.2,
  "skyThickness": 2.0,
  "requires": ["mypack"]
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `name` | no | string | the file name | Name shown to the player |
| `id` | no | int | assigned for you | Fixed biome id. Only set this if you need it stable |
| `temperature` | no | float | `0.5` | Below 0.15 snows, above 1.0 is desert-hot |
| `rainfall` | no | float, 0 to 1 | `0.5` | How wet it is |
| `rain` | no | boolean | `true` | Whether weather happens at all |
| `snow` | no | boolean | `false` | Whether rain falls as snow |
| `baseHeight` | no | float | `0.1` | Terrain height. Sea level is 0, plains 0.125 |
| `heightVariation` | no | float | `0.2` | How hilly it is |
| `topBlock` | no | block name | grass | The surface block |
| `fillerBlock` | no | block name | dirt | Just below the surface |
| `stoneBlock` | no | block name | stone | The bulk of the ground |
| `types` | no | list of dictionary types | none | Registers the biome under these, such as `FOREST`, `COLD`, `WET` or `NETHER`, so other mods find it |
| `waterColor` | no | hex color | `FFFFFF` | Water tint |
| `grassColor` | no | hex color | from the climate | Grass tint, in place of the color temperature and rainfall would give |
| `foliageColor` | no | hex color | from the climate | Leaf tint, the same way |
| `baseBiome` | no | biome name | none | An existing biome to copy settings from |
| `decoration` | no | object | vanilla counts | Per-chunk counts. The names it reads are `trees`, `flowers`, `grass`, `deadbush`, `mushrooms`, `bigmushrooms`, `reeds`, `cacti`, `sand`, `gravel`, `clay` and `waterlily`, plus `falls`, where above zero means lakes and springs generate, and `extratreechance`, a percentage chance of one tree more. Any other name is logged and ignored |
| `spawns` | no | list of objects | vanilla list | See below |
| `keepDefaultSpawns` | no | boolean | `false` | Keep vanilla's list alongside yours |
| `spawnChance` | no | float, below 1 | `0.1` | How likely another herd is placed as the land is first made. The game keeps rolling for as long as it succeeds, so 1 never stops and fills the world until it runs out of room. Anything at or above 0.99 is refused and 0.99 used |
| `spawnRates` | no | object of `surfaceDay`, `surfaceNight`, `undergroundDay`, `undergroundNight` to a multiplier | none | How often hostile mobs spawn here, in place of the global settings. See below |
| `placement` | no | object | none | Where it generates. See below |
| `villageType` | no | `oak`, `sandstone`, `acacia` or `spruce` | none | What a village standing here is built from. Empty builds with oak, as it would without the key |
| `minHeight` | no | int | none | Lowest y this biome takes over as a 3D biome. Setting either height turns the biome into a band: the column keeps its own biome outside it, and inside it every 4 by 4 by 4 cell of the world reports this one. Rubic worlds only, and applied as land is made, so existing land keeps what it had |
| `maxHeight` | no | int | none | Highest y of that band |
| `replaces` | no | list of biome names | every biome | Restricts the band to columns whose own biome is named here, so an alpine band can sit over mountains and nothing else |
| `skyStone` | no | block name | the world setting | The block sky islands are made of under their surface where this biome applies. On a band, `topBlock` and `fillerBlock` paint the island surface with it, so a band is how one stretch of sky gets islands of its own |
| `skyIslands` | no | float, `-1` to `1` | the world setting | The island threshold where this biome applies. Lower gathers more land |
| `skyThickness` | no | float, `0` or more | the world setting | How solid the islands are where this biome applies |
| `requires` | no | list of mod ids or pack namespaces | none | The file is skipped unless all are present |

A spawn entry takes `entity` (required), `type` (`creature`, one of the [creature types](#value-lists)), `weight` (`10`), `min` (`1`) and `max` (`min`).

`spawnRates` is about hostile mobs only, and nothing else. It takes four keys and no others: `surfaceDay` and `surfaceNight` for where the sky can be seen, `undergroundDay` and `undergroundNight` for where it cannot. Each is a multiplier on how often a hostile mob is allowed to appear, `1` is the ordinary rate, `0` stops them entirely, below 1 turns some attempts down, and above 1 lets through attempts the game would otherwise have refused, so `2` is twice as many. A key left out means the biome does not decide, and the global setting for that time and place is used instead. Anything else written here is not a key and is ignored, so a rate named after a creature type does nothing at all.

`placement`:

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `climate` | no | string | none | Which vanilla climate group it joins |
| `weight` | no | int | `10` | How often it is chosen against its neighbors |
| `villages` | no | boolean | `false` | Villages may generate |
| `villageSpawn` | no | boolean | `true` | Villagers may spawn in them |
| `strongholds` | no | boolean | `false` | Strongholds may generate |
| `playerSpawn` | no | boolean | `false` | The world spawn may be placed here |

**Temperature by height.** A biome cools as it rises, which is what puts snow on mountain tops and stops rain above a line. Three `terrain` keys move that curve, which matters on a rubic world where the ground can sit far above or below the height the game assumes. The defaults are what the game does, so a pack that leaves them alone changes nothing.

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "biomeTemperatureCenterY": 64,
    "biomeTemperatureHeightFactor": -0.001667,
    "biomeTemperatureScaleMaxY": 256
  }
}
```

| Key | Value | Default | What it does |
| --- | --- | --- | --- |
| `biomeTemperatureCenterY` | int | `64` | The height the curve is measured from. At or below it a biome reports its own `temperature` untouched |
| `biomeTemperatureHeightFactor` | float | `-0.001667` | How much the temperature moves per block above that height, the game's own 0.05 across 30 blocks. Negative cools with altitude, positive warms |
| `biomeTemperatureScaleMaxY` | int | `256` | The height the curve stops at, so a world taller than the game's own does not keep cooling all the way to its ceiling |

## Dimensions

`<namespace>/dimensions/*.json`

The file's path names the dimension for `suffix`, whose default is `DIM_<name>`. The dimension itself is found by its `id`, so that is the number everything else refers to.

Every key, shown at once. A real file writes only the ones it needs.

```json
{
  "id": 12,
  "suffix": "DIM_ruby",
  "keepLoaded": false,
  "requires": ["mypack"],
  "terrain": {
    "type": "overworld",
    "generatorOptions": "",
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
    "respawnDimension": 0,
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
| `id` | yes | int |, | The dimension id. Must not clash with another mod |
| `suffix` | no | string | `DIM_<name>` | The save folder |
| `keepLoaded` | no | boolean | `false` | Keep it loaded when nobody is in it |
| `gameRules` | no | object | none | Rules that apply only here |
| `requires` | no | list of mod ids or pack namespaces | none | The file is skipped unless all are present |

**`terrain`**

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `type` | no | `overworld`, `flat`, `void`, `nether`, `end` | `overworld` | Which generator builds it |
| `generatorOptions` | no | string | none | The generator string, as a superflat preset uses |
| `structures` | no | boolean | `true` | Whether vanilla structures generate |

**`biomes`**

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `source` | no | `inherit`, `single` | `inherit` | `inherit` uses the normal biome map, `single` uses one biome everywhere |
| `biome` | when `single` | biome name | `minecraft:plains` | Which biome that is |

**`sky`**

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `hasSkyLight` | no | boolean | `true` | Whether daylight reaches it |
| `surfaceWorld` | no | boolean | `true` | Whether maps and compasses behave as in the overworld |
| `respawn` | no | boolean | `true` | Whether players respawn here |
| `respawnDimension` | no | int | none | Where they respawn instead |
| `spawning` | no | boolean | `true` | Whether mobs spawn |
| `nether` | no | boolean | `false` | Treated as the nether for portals and ceilings |
| `beds` | no | boolean | `true` | Off, beds explode |
| `waterVaporizes` | no | boolean | `false` | Water evaporates |
| `cloudHeight` | no | int | `128` | Where clouds sit |
| `cloudColor` | no | hex color | none | Cloud tint |
| `groundLevel` | no | int | `63` | Sea level, used for the horizon and spawn searches |
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

`<namespace>/blocks/*.json`

A portal is an ordinary block definition, so the same path rule applies and the file's path is the block's registry name.

A `portal` block carries a `portal` section:

```json
{
  "type": "portal",
  "material": "portal",
  "portal": {
    "dimension": 12,
    "returnDimension": 0,
    "gate": "mypack:ruby_gate",
    "cooldown": 60,
    "platform": true,
    "platformBlock": "mypack:ruby_block",
    "sound": "block.portal.travel",
    "owned": true
  },
  "variants": { "ruby_portal": { "meta": 0, "hardness": -1, "light": 11 } }
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `dimension` | yes | int |, | Where it sends you |
| `returnDimension` | no | int | `0` | Where it sends you back to |
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
  "id": 12,
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

The block that stands in the hole is not written by the pack. A dimension with a `portal` section is given one of its own, named `<namespace>:portal_<dimension>`, drawn in the game's own portal texture under `color`, walked into rather than used by hand, and unbreakable. The color multiplies the texture, the way a `tintindex` does, so `#C77DFF` keeps the nether's violet and `#4CFFB0` turns it poisonous. For a portal that is not the vanilla texture at all, write an ordinary `portal` block of your own with its own model and a texture drawn as a [pixel map](#textures-written-as-pixel-maps), where `tint` can ramp between two colors.

`return` decides what happens on the other side. `built` puts up the same frame, at the size the player built, and lights it, which is the way vanilla behaves. `player` builds nothing but lets the same frame be lit over there, so the way home has to be found and made. `none` refuses to light the frame in that dimension at all, and the trip is one way.

**One frame, several dimensions.** The pair of a frame and the item that lights it is what picks the dimension, so the same `standing_gate` lit with flint and steel and lit with a pack's own igniter opens two different places, each with its own color. Two dimensions claiming the same frame *and* the same item is a mistake in the pack: the second one is refused and says so in the log rather than one of them quietly winning.

Breaking any block of the frame puts the portal out, as it does in vanilla.

`<namespace>/gates/*.json`

The file's path is the gate's registry name, which a portal then names in `gate`.

Every key, shown at once. A real file writes only the ones it needs.

```json
{
  "dimension": 12,
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
| `dimension` | yes | int |, | The dimension it guards |
| `name` | no | string | the file name | Shown to the player |
| `scope` | no | `player`, `global` | `player` | One player at a time, or the whole world at once |
| `open` | no | boolean | `false` | Whether it starts open |
| `unlock` | no | object |, | What opens it. See below |
| `unlockedMessage` | no | string | `%dim% is now open` | Shown when it opens |
| `blockedMessage` | no | string | `You need %item% to enter %dim%` | Shown when it refuses |
| `safeReturn` | no | boolean | `false` | A blocked return still lands somewhere safe rather than refusing |
| `requires` | no | list of mod ids or pack namespaces | none | The gate is skipped unless all are present |
| `portalBlocks` | no | list of block names | every portal | Limits the gate to these portal blocks, so one dimension can have a guarded door and an open one |

`unlock` takes `hold` (an item that must be held), `consume` with `consumeCount` (`1`), `craft` (an item that must have been crafted), `advancement`, and `killed` (an entity name, the gate opens for whoever slays one, so a boss can hold the key to a world) with `killedCount` (`1`) when one is not enough, tallied per player or for the whole world as the scope says. Adding `killedDrops` (an item name) makes the counted kills lay that item at the slayer's feet instead of opening the gate, and starts the counting over, so a key can be earned again and handed to somebody who never fought for it; gate on `hold` or `consume` of the same item to make it the key. `%item%`, `%mob%` and `%dim%` are filled in for you. A key a mob drops needs nothing special here: give the mob the drop and gate on `hold` or `consume`.

## World templates

`<namespace>/worldtemplates/*.json`

The file's path is the template's name, which the `worldTemplate` config option can name to pick it outright.

Gathers a world's shape into one file, so a pack ships a whole world at once rather than asking the player to set a dozen config options.

```json
{
  "name": "Ruby World",
  "default": "void",
  "dimensions": [0],
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
| `default` | no | biome name or `void` | `void` | What fills a biome that blocking removed |
| `roles` | no | object of role to biome | none | Biomes filling particular roles, such as ocean or river |
| `structures` | no | object of [structure name](#value-lists) to boolean | none | Vanilla structures switched on or off |
| `settings` | no | object | none | Config values the template sets |
| `dimensions` | no | list of ints | every dimension | Which dimensions it applies to |

`settings` uses the same key names as the config, so there is no translation table to learn.

Which template is active is decided by the `worldTemplate` config option. Left at `auto`, the highest priority pack that ships one wins, the same order everything else follows. Naming a template there picks it outright.

## Rubic worlds

`rubicWorld` in the `terrain` settings rebuilds a dimension's world out of 16×16×16 cubes instead of 256-block columns, so its floor and ceiling can sit wherever the pack puts them. Terrain generation itself is unchanged — vanilla's generator and other mods' worldgen run as usual and produce the same land; there is simply world above and below it.

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "rubicWorld": true,
    "worldMinHeight": -1024,
    "worldMaxHeight": 1024,
    "rubicWorldDimensions": [0, -1],
    "rubicWorldDimensionsAreBlacklist": false,
    "terrainOffset": 0
  }
}
```

All keys sit in the `terrain` group, in a world template's `settings` block like the rest:

| Key | Value | Default | What it does |
| --- | --- | --- | --- |
| `rubicWorld` | boolean | `false` | Turns rubic worlds on |
| `worldMinHeight` | int, multiple of 16 | `-64` | The world floor |
| `worldMaxHeight` | int, multiple of 16 | `320` | The world ceiling |
| `rubicWorldDimensions` | list of ints | empty | Which dimensions become rubic. Empty means every dimension |
| `rubicWorldDimensionsAreBlacklist` | boolean | `false` | Treat the list as the dimensions to leave alone instead |
| `terrainOffset` | int, non-negative multiple of 16 | `0` | Shifts the whole vanilla terrain window upward. For plain layered presets: a flat world with `272` puts its surface near y 275, above the vanilla ceiling. Decorations and structures a preset asks for still generate at their unshifted heights |

**Heights.** `worldMinHeight` must be below `worldMaxHeight`, both multiples of 16, and both inside the reach `rubicHeightLimit` in the config allows (`4096` blocks either way by default; config only, never a pack key). Anything else is refused with a log line and the world is made at `-64` to `320`. Height costs room: every 16 blocks is another cube in every column, so memory, disk and pregeneration time scale with it — the config comment on `rubicHeightLimit` carries the numbers.

**The terrain window.** The dimension's own generator keeps its own height, 256 blocks in the overworld, and that window is what `terrainOffset` slides. `worldMinHeight` and `worldMaxHeight` add room around the window, never inside it. Raising the ceiling does not raise the land, it adds sky; lowering the floor does not deepen the caves the generator cut, it adds deep world. Sea level sits inside the window as well, so it rides along with `terrainOffset` and comes from the world type rather than from any rubic key. To put the surface higher in the world, raise `terrainOffset`. To put more room above or below it, move the heights. Every cube outside the window is still generated and lit in every column, so a taller ceiling costs pregeneration time whether or not anything fills it, and costs memory and disk on top of that once `skyStone` does.

**What a shifted window does to other mods.** Population runs on the column, so every generator a mod registers still runs once per chunk, with no coordinates translated. What changes is where its own math lands. A generator that asks the world where the ground is, through the top solid block or the precipitation height, follows the shifted terrain: both are rubic aware, which covers trees, flowers and most decoration. A generator that computes an absolute height, the usual ore pattern of a random y under 64 among them, keeps writing at that height, which after a shift is the filler or the deep world far beneath the land. Sea level is not shifted either, so a generator that tests against it reads the unshifted number. Those writes also land outside the cubes population holds loaded, and pull in cubes of their own while a column populates. A large `terrainOffset` suits a pack that describes its own generation, not one stacked on top of another pack's worldgen. For room alone, depth is the cheaper direction: below the window sits a full generator with its own stone, caves, veins, aquifers and dungeons, and it leaves the surface at the heights every other generator assumes, where the space above the window is scenery a pack has to furnish itself.

**Decided per save, once.** Whether a dimension is rubic and what its heights are is written into its save the first time it loads, and stands from then on: a rubic world stays rubic even with the pack removed, and its heights cannot be changed afterward. Dimensions other than the overworld take the overworld's heights. Existing Anvil land is not converted — rubic keeps its land in `region2d`/`region3d` files of its own, so a dimension that already generated as Anvil starts its terrain over. Turn it on for new worlds.

**Excluding dimensions.** A dimension left out of `rubicWorldDimensions` keeps its ordinary Anvil world, in the same save — rubic and Anvil dimensions mix freely. That is the right call for dimensions whose generators write into chunk internals instead of going through the ordinary populate cycle. Independently of the list, a world whose server classes another mod replaced is skipped, with a log line saying so.

**Space outside the window.** The generator's own range keeps its usual shape, and the room a rubic world adds around it is filled with the block that range ends in: stone under the overworld, air over it. A dimension whose top is sealed with bedrock, the nether above all, counts as closed, so the room above it is left empty rather than packed with the netherrack under its roof. The roof itself is untouched. `deepStone` names the block for the room below the window, `skyStone` the block for the room above it.

**CubicChunks.** Running both is not supported. With CubicChunks installed and a pack asking for `rubicWorld`, loading stops with a message: remove CubicChunks, or take `rubicWorld` out of the pack and let CubicChunks make the worlds.

**Cube streaming.** Four `chunks` keys decide how cubes reach a player and when they are let go again. They only do anything on a rubic world, and the defaults are the numbers the subsystem was tuned at, so a pack that leaves them alone pays nothing.

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "verticalCubeLoadDistance": 8,
    "cubesSentPerTick": 649,
    "cubeGenMillisPerRound": 50,
    "cubeGCInterval": 200
  }
}
```

| Key | Value | Default | What it does |
| --- | --- | --- | --- |
| `verticalCubeLoadDistance` | int, cubes | `8` | How many cubes above and below a player a chunk loading ticket holds. The video settings slider of the same name is the client's own view distance, set by whoever is playing rather than by a pack |
| `cubesSentPerTick` | int, cubes | `649` | How many cubes a player may be sent in one tick. Raising it fills a view bubble faster and makes each tick's packets larger; a packet is still split at 1024 cubes or 512 KB, whichever comes first |
| `cubeGenMillisPerRound` | int, milliseconds | `50` | How long a tick may spend generating the cubes players are waiting on |
| `cubeGCInterval` | int, ticks | `200` | How often cubes nobody is watching are let go |

**Client.** Video settings gain a vertical render distance slider, the vertical analog of render distance (`verticalCubeLoadDistance` in the config, which belongs to whoever is playing). Everything else in the `terrain` group — pregeneration, world physics, spawn, border — applies to rubic worlds unchanged.

## The deep world

Nine more `terrain` keys fill the space a rubic world opens around the vanilla terrain window with modern-style generation. They only do anything on a rubic world:

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "rubicWorld": true,
    "worldMinHeight": -64,
    "worldMaxHeight": 1024,
    "deepStone": "mypack:slate",
    "skyStone": "minecraft:end_stone",
    "skyShape": "islands",
    "skyIslands": 0.05,
    "skyThickness": 3.0,
    "skyHeights": [400, 800],
    "noiseCaves": "world",
    "deepRavines": true,
    "oreVeins": ["minecraft:iron_ore,,mypack:slate@1,-56,20"]
  }
}
```

| Key | Value | Default | What it does |
| --- | --- | --- | --- |
| `deepStone` | `namespace:block`, meta as `@meta` | none | The block the world below the window is made of, such as a pack's own deepslate. It blends into the window's stone across the window's lowest eight layers, the way modern versions blend deepslate |
| `skyStone` | `namespace:block`, meta as `@meta` | none | The block the world above the window is made of under its surface, shaped into floating land by the same noise that carves the deep world below, so what is cave down there is island up here. Empty leaves the space above the window empty, as it has always been. The land carries the column's own surface, the top block and the three under it taken from the biome, so an overworld island reads as grass over dirt over this block. A biome or a cave region can name its own `skyStone`, `skyIslands` and `skyThickness`, so one band or one region carries islands of its own, resolved per column with the region winning over the band and the band over the biome. The islands are decorated in their own right: each cube above the window runs the biome's own features against the surface inside that cube, so trees, grass, flowers, mushrooms, reeds and patches land on the island rather than being scattered down the column the way vanilla places them, and a biome band up there decorates with its own counts. A biome's own additions run there too, not only the shared ones: desert wells, jungle melons, the dense canopy and mushrooms of a roofed forest, taiga boulders, ice spikes on the icy biomes, and the tall flowers and grasses each biome places. An island is never made of a block that falls: where a biome's surface would be sand or gravel the island uses sandstone, or its own `skyStone` for anything else, since nothing holds a falling block up in mid air. Herds are placed the same way, per cube, so animals start on the islands as the land is made. Surface depth varies from one to four filler blocks with the noise, so an island edge is not a uniform crust, and the crust is measured along the slope rather than straight down, so a steep face keeps its soil instead of thinning to nothing. The surface follows whatever biome the sky itself reports, the cave region first, then a height banded biome, then the column below, so naming `minecraft:mesa` on a sky region gives islands of banded clay at any height, the same bands the ground has, and naming a desert gives its sand, turned to sandstone because nothing holds a falling block up. Animals settle on it, which `skyAnimals` in the `spawning` group turns off. How much of the sky becomes land is `skyIslands`, and its default of 0.5 is an archipelago: on a generated world it leaves roughly seven cubes in eight above the window empty and its fullest layer sits near a third, so the sky is flown through rather than walked on. Lower it toward 0.2 and the band closes up into a rolling ceiling with hills standing on it, about four fifths solid through its middle, which is something to build on but no longer islands. Islands stop eight blocks short of `worldMaxHeight`, so a top is never cut flat against the ceiling and trees and plants have room above it; `caves` fills to the ceiling as before. Every rubic dimension has a window of its own, so this fills the space above each: in the nether, whose window is 128 tall, that is the room above the bedrock roof, and a seam that opens the ceiling clears the roof itself |
| `skyShape` | `islands` or `caves` | `islands` | What the world above the window is shaped into. `islands` is floating land. `caves` is solid rock with caves carved through it, the deep world's own treatment turned upward, which comes out about 86 percent solid, the same rock to cave ratio the deep world has. Nothing floods either way, since no aquifer is consulted above the window. Only read when `skyStone` names a block |
| `skyIslands` | number, `-1` to `1` | `0.5` | How readily the sky gathers into islands. Lower spreads island across more of the sky and deepens the shadow beneath it, higher leaves fewer and smaller pieces. The default leaves roughly seven cubes in eight empty and peaks near a third; around `0.2` the band closes into a ceiling with hills, about four fifths solid through its middle. Only read when `skyStone` names a block and `skyShape` is `islands` |
| `skyThickness` | number, `0` or more | `2.0` | How solid an island is. Higher fills islands out, lower hollows them and thins their edges away to nothing. Only read when `skyStone` names a block and `skyShape` is `islands` |
| `skyHeights` | two ints, lowest then highest | none | The lowest and highest block an island may reach, counted from the bottom of the window the way `oreVeins` heights are. Empty fills the whole world above the window, which on a tall world is a great deal of sky. Only read when `skyStone` names a block |
| `noiseCaves` | `off`, `deep`, `world` | `off` | Modern-style noise caves: cheese caverns, spaghetti tunnels, cave mouths near the surface and pillars in the big rooms. `deep` carves only below the window, `world` carves the whole world |
| `deepRavines` | boolean | `false` | Cut vanilla-style ravines through the world below the window, long steep canyons. A ravine takes the deep world's own fluids where it passes through them, filling with lava below the lava line and keeping an aquifer's water or its pressure wall above it, so it never drains what it cuts. Modern versions carve their canyons inside the window only, so the deep has none unless this is on |
| `oreVeins` | list of `ore,extra,filler,lowest,highest` | none | Large banded ore veins, mostly the `filler` block with the `ore` scattered through it and a rare chance of the `extra`, which may be left empty. Heights count from the bottom of the window, so negatives reach the deep world |

Water and lava behave down there. Bulk lava fills the lowest layers, and the caves above carry local aquifers — the same sample-point-and-pressure scheme modern versions use, ported from 26.1.2 — so pockets of still water sit at their own levels, with walls of the deep stone shaped by noise wherever two levels meet or water meets lava. Under oceans the caves flood toward sea level, the way modern versions tie their aquifers to the surface.

**Per dimension.** `deepStone`, `noiseCaves`, `skyStone`, `skyShape`, `deepRavines` and `oreVeins` each take either a single value or a list, and a list entry written as `dimension=value` applies to that dimension alone. Where any entry names a dimension, those entries decide it outright and the unnamed ones are ignored there, so `"1="` with nothing after it switches the key off for that dimension. A value with no dimension on it reaches every rubic dimension except the End, which stays void unless a pack names it: filling the End would be the end of the End, and a filled End also blinds the vanilla gateway search, which walks back from 1024 blocks for as long as it keeps meeting chunks with blocks in them. Name it and you get what you asked for.

With `noiseCaves` on, the deep also rolls modern-style monster rooms — about four tries per chunk column below the window, none closer than six blocks to the world floor — so dungeon spawners and their chest loot turn up in the deep caves the way they do in modern versions.

The `world` scope also retires two vanilla leftovers that would fight the reworked caves. The lava vanilla pours into its caves below y 10 is judged by the aquifer instead, so the old lava window is gone, and vanilla's buried water lakes — surface ponds included — stop generating, the way modern versions dropped them; the aquifer's own pools take their place.

The `deep` scope leaves the vanilla band as it is, lava window included, and only seals the seam where the two meet. Lava or water on the lowest layer of the window with a deep cave open directly beneath it becomes the deep stone, so the window cannot drain into the caves below.

## Cave regions

`<namespace>/caveregions/*.json`

The file's path is the region's name, which a worldgen entry then names in `caveRegions`. A bare name there takes that entry's own namespace.

Paints named regions over the underground, the pack counterpart of modern cave biomes. The underground is divided into rounded cells — `caveRegionCells` blocks wide and `caveRegionCellsY` tall, both `terrain` keys — and each cell rolls one region, or none, by weight. Everything a region does comes deterministically from the seed, so chunks agree with each other without ever writing across a border.

Every key, shown at once. A real file writes only the ones it needs.

```json
{
  "weight": 3,
  "minHeight": -56,
  "maxHeight": 16,
  "dimensions": [0],
  "biome": "minecraft:mushroom_island",
  "floorCover": "minecraft:mycelium",
  "floorChance": 0.8,
  "ceilingCover": "minecraft:brown_mushroom_block",
  "ceilingChance": 0.3,
  "coverReplace": ["minecraft:stone", "mypack:slate"],
  "waterLevel": -24,
  "keepDefaultSpawns": false,
  "spawns": [
    { "entity": "minecraft:mooshroom", "type": "creature", "weight": 12, "min": 2, "max": 4 }
  ],
  "structures": [
    { "structure": "mypack:cave_shrine", "weight": 3 },
    "mypack:cave_well"
  ],
  "structureChance": 0.5,
  "skyStone": "minecraft:sandstone",
  "skyIslands": 0.2,
  "skyThickness": 2.0
}
```

| Key | Value | Default | What it does |
| --- | --- | --- | --- |
| `weight` | int | `1` | Share of cells this region wins. `0` switches it off |
| `minHeight` | int | the world floor | Bottom of the band the region exists in |
| `maxHeight` | int | `48` | Top of that band. A cell whose center sits outside the band never picks the region |
| `dimensions` | list of ints | all | Which dimensions the region appears in |
| `floorCover` | block | none | Replaces the top block of cave floors inside the region |
| `floorChance` | 0.0 to 1.0 | `1.0` | How much of the floor gets covered |
| `ceilingCover` | block | none | Replaces cave ceiling blocks inside the region |
| `ceilingChance` | 0.0 to 1.0 | `1.0` | How much of the ceiling |
| `coverReplace` | list of blocks | anything stone-like | What the covers may replace |
| `waterLevel` | int | none | Pins the water level of every aquifer sample point inside the region, so its caves flood to this height. Walls where the region meets dry caves are shaped by the same pressure noise as modern aquifers, and water never touches the lava floor. Needs `noiseCaves` on |
| `spawns` | list | none | Mobs that spawn inside the region, the same entries a biome's `spawns` takes: `entity`, `type` (monster, creature, ambient or water), `weight`, `min` and `max` for the group size. Under the terrain window a spot that can see the sky is left to the biome, like the covers are; above the window, where the only land is the sky generation, the list applies in the open as well |
| `keepDefaultSpawns` | boolean | `false` | Keep the biome's own spawn list alongside the region's. Off, the region's list replaces it entirely inside the region |
| `structures` | list | none | A structure placed once per region cell, at the cell's heart, snapped to a cave floor — the way modern versions give a cave biome its landmark. Entries are `namespace:name` templates, or `{ "structure": "...", "weight": 3 }` to choose between several |
| `structureChance` | 0.0 to 1.0 | `1.0` | The chance each cell of the region actually gets its structure |
| `biome` | biome name | none | The biome the region reports inside its volume, written into the cube as a 3D biome. Gives the region its own foliage, grass and water colors, music and ambient sounds, and lets vanilla spawn weighting read it. The surface above is untouched, since only the cells the region occupies are written |
| `skyStone` | block | the world setting | The block sky islands are made of under their surface inside this region, so one region carries islands of its own |
| `skyIslands` | `-1` to `1` | the world setting | The island threshold inside the region. Lower gathers more land |
| `skyThickness` | `0` or more | the world setting | How solid the region's islands are |

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

How much of the underground stays plain is the `caveRegionPlainWeight` `terrain` key, default `4`: with a single region of weight 1, about a fifth of the cells get the region. Covers apply under a roof, so a region reaching above ground never shows on the surface; above the terrain window they also apply in the open, since everything up there is land the sky generation made. Covers work in every cave, whichever generator carved it; `waterLevel` is the one key that needs the noise caves, because the flood is placed while they are carved.

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

Text files go in `<namespace>/texts/*.txt`. Plain text, one paragraph to a line, and blank lines are kept as blank lines. `PLAYERNAME` is swapped for the player's name, the same substitution the vanilla end poem uses.

`time` sets how long the page lasts, so the same page takes the same time whether it holds one line or twenty. Tune the reading speed by how much you put on the page. Leave `time` out and the page runs at the same speed as the vanilla credits, where more text simply takes longer.

A scrolling page moves to the next one when its time is up. The last page never advances on its own, it waits. Along the bottom are **Next Page** and **Skip All**, or a single **Continue to World** on the last page. Escape does the same as Skip All. Static pages center every line. Scrolling pages keep to a fixed column, the way the credits do.

In singleplayer the world pauses behind the intro, so nothing creeps up on the player while they read. On a server the world keeps running, and a vanilla client never sees the intro at all and joins as normal.

`once` is remembered in the player's saved data and survives death. `/rdplserver intro` clears it for whoever runs it, so the intro plays again the next time they join. It does not replay on the spot, which keeps it from being a way back into the entry sequence in the middle of a game.

Backgrounds are stretched to fill the window, so a 16:9 image suits a 16:9 window and a square one looks squashed. Crop the picture to shape rather than relying on the fit. `music` takes any registered sound event, vanilla or one your own pack adds through `sounds`. It does not loop, so a short track finishes and leaves quiet behind it.

If more than one pack ships an intro, their pages run end to end in pack order rather than one winning. Gate them with `requires` if you only want one.

## Game rules

`<namespace>/gamerules/*.json`

The file name is yours to choose, only the folder is read, and several files stack.

```json
{
  "0": {
    "doFireTick": "false",
    "keepInventory": "true",
    "randomTickSpeed": "3"
  },
  "-1": {
    "doFireTick": "true"
  }
}
```

Each key is the id of the world the rules belong to, `0` for the overworld, `-1` for the nether, `1` for the end, and whatever a mod uses for its own. Values are strings, as they are in the `/gamerule` command, so `"false"` rather than `false`. These are applied to new worlds. A dimension file carries the same rules in a `gameRules` block instead, which only ever applies to that world.

## Hardness groups

`<namespace>/hardness/*.json`

The file's path names the group in the log and nothing else reads it, so several files stack.

Gives a group of blocks a mining time multiplier, rolled per block position. The block itself is never changed: nothing is registered, nothing is written into the world, and a world opened without the pack is ordinary vanilla.

```json
{
  "blocks": ["minecraft:stone:0"],
  "except": [{ "block": "minecraft:stone", "properties": { "variant": "andesite" } }],
  "miningTime": { "min": 1.0, "max": 20.0 },
  "blastResistance": { "min": 1.0, "max": 4.0 },
  "buckets": 10,
  "minHeight": 0,
  "maxHeight": 255,
  "field": { "type": "speckle", "spread": 0.15 },
  "requires": ["mypack"]
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `blocks` | yes | list of block names or objects |, | The group. Same three forms as a worldgen `replace` |
| `except` | no | list of block names or objects | none | Taken back out of the group, whatever `blocks` says |
| `miningTime` | no | number, or object with `min` and `max` | `1.0` | How many times longer the block takes to break |
| `blastResistance` | no | number, or object with `min` and `max` | `1.0` | Multiplies the block's blast resistance |
| `buckets` | no | 1 to 256 | `10` | How many steps the range is divided into |
| `minHeight` | no | int | `0` | Below this the roll is the hardest step |
| `maxHeight` | no | int | `255` | Above this the roll is the hardest step |
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
    "normal": [
      { "model": "mypack:stone_step0", "weight": 1 },
      { "model": "mypack:stone_step1", "weight": 1 }
    ]
  }
}
```

Minecraft already picks a variant from a block's position, and a hardness group hands it the bucket instead, so the texture and the multiplier always agree.

Three things have to be right, and none of them announce themselves when they are wrong.

**Exactly `buckets` entries, all weighing the same.** The bucket is used as a place in the list, so a list of a different length, or one where the weights differ, quietly points at the wrong texture.

**A model name without `block/` in front.** A blockstate adds `block/` itself, so `"model": "mypack:step_stone"` reads the file at `models/block/step_stone.json`. Writing `mypack:block/step_stone` looks for `models/block/block/step_stone.json`, which is not there, and the entry is dropped without a word.

**The same key the game asks for.** Not every block is keyed the way its properties read. Vanilla stone keys everything under `normal`, not `variant=stone`, so an override that only writes `variant=stone` is merged in and then never looked at. Writing both keys is safe, since the merge is per key and a pack outranks what came before it.

Turn on `worldgenDebug` and every hardness group is checked against its baked model when a world is entered, naming the blockstate, how many variants survived, what texture each one ended up with, and which packs the game merged to get there. That is the quickest way to find any of the three above, and it also warns when overriding a shared blockstate has changed a state the group never named.

### What it does not reach

Only a player's own mining is changed. Machines that break blocks read the block's hardness directly and are not affected. Blocks a player places are rolled the same as any other, since the roll belongs to the place rather than to the block, and a block carried elsewhere takes on whatever its new place says.

# Generating it

## Worldgen entries

`<namespace>/worldgen/*.json`

The file's path names the entry, and the `belt` and `field` shapes seed their noise from it, so renaming a file moves what it generates.

Describes something that generates. Every entry is a **shape** placed by a **spread**, filtered by where it is allowed.

```json
{
  "block": "mypack:ruby_ore",
  "meta": 0,
  "blocks": [
    { "block": "mypack:ruby_ore", "meta": 0, "weight": 80 },
    { "block": "minecraft:wool", "weight": 20, "properties": { "color": "magenta" } }
  ],
  "size": 8,
  "attempts": 12,
  "replace": ["minecraft:stone"],
  "adjacent": ["minecraft:air"],
  "minHeight": 8,
  "maxHeight": 48,
  "dimensions": [0],
  "dimensionsAreBlacklist": false,
  "biomes": ["minecraft:extreme_hills"],
  "biomeTypes": ["MOUNTAIN"],
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
  "requires": ["quark"],
  "shape": { "type": "cluster" },
  "spread": { "type": "even" }
}
```

Only `block` is required; everything else may be left out and takes its default. `blocks` replaces `block` when one is not enough and has its own example below.

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `block` | yes | block name |, | What is placed |
| `meta` | no | int | `0` | Which variant of that block |
| `blocks` | no | list of objects | none | A weighted list, used instead of one block. See below |
| `size` | no | int or range | `8` | How many blocks one attempt places, or how large a shape with a radius is |
| `attempts` | no | int or range | `1` | How many times per chunk it tries |
| `replace` | no | list of block names or objects | `["minecraft:stone"]` | What it may replace. See below |
| `adjacent` | no | list of block names or objects | none | Only place where one of these is among the 26 blocks touching the spot. Same three forms as `replace` |
| `minHeight` | no | int | `0` | Lowest y it will place at |
| `maxHeight` | no | int | `64` | Highest y it will place at |
| `dimensions` | no | list of ints | every dimension | Which dimensions it runs in |
| `dimensionsAreBlacklist` | no | boolean | `false` | Turn that list into the ones to avoid |
| `biomes` | no | list of biome names | every biome | Which biomes it runs in |
| `biomeTypes` | no | list of dictionary types | none | Biomes by type, such as `FOREST` or `NETHER` |
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

### Weighted blocks

`blocks` replaces `block` when one entry is not enough. Weights are relative, so 80 and 20 is four to one.

```json
{
  "blocks": [
    { "block": "minecraft:wool", "meta": 2, "weight": 80 },
    { "block": "minecraft:wool", "weight": 20, "properties": { "color": "lime" } }
  ]
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `block` | yes | block name |, | What is placed |
| `meta` | no | int | `0` | Which variant |
| `weight` | no | int | `1` | How often this one is chosen against the others |
| `properties` | no | object of property to value | none | Block state properties by name, for states with no metadata of their own |

`block` and `meta` are still required at the top level of the file even when `blocks` is used, the first entry is a good value to put there.

### Replace targets

`replace` is a list, and each entry takes one of three forms.

```json
{
  "replace": [
    "minecraft:stone",
    "minecraft:stone:3",
    { "block": "minecraft:stone", "properties": { "variant": "andesite" } },
    { "block": "minecraft:stone", "meta": 5 }
  ]
}
```

| Form | Example | What it matches |
| --- | --- | --- |
| Name | `"minecraft:stone"` | Every state of that block |
| Name and metadata | `"minecraft:stone:3"` | Only that metadata, here diorite |
| Object | `{ "block": "minecraft:stone", "properties": { "variant": "andesite" } }` | Only that state |

The object form also takes `meta` instead of `properties`, which is the same as the colon form. Use `"minecraft:air"` to generate in open space.

### Adjacent blocks

`adjacent` takes the same three forms as `replace` and adds a second condition on top of it: the spot is only used when at least one of the 26 blocks touching it, faces, edges and corners, matches the list. Left out, nothing is checked.

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
    "surface": ["minecraft:grass"],
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
    "turns": ["none", { "turn": "half", "weight": 2 }],
    "mirrors": ["none", { "mirror": "leftright", "weight": 2 }],
    "at": [1000, -500],
    "locateAs": "Crypt",
    "field": { "type": "speckle", "spread": 0.15 },
    "threshold": 0.5,
    "fade": 0,
    "rarity": 400,
    "rarityIsPerChunk": false
  }
}
```

```json
{
  "shape": { "type": "tree", "log": "mypack:ruby_log", "leaves": "mypack:ruby_leaves", "height": { "min": 4, "max": 7 }, "surface": ["minecraft:grass"] }
}
```

A `tree` with no `log` or `leaves` generates nothing, and says so in the log.

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
| `structure` | imprint | `namespace:name` | none | The template to place |
| `integrity` | imprint | 1 to 100 | `100` | Percentage of the template's blocks that actually appear |
| `structures` | imprint | list | none | Several templates to choose between, one placed each time. Each entry is `{ "structure": "namespace:name", "weight": 3 }`, or a bare name for equal odds. Overrides `structure` |
| `turns` | imprint | list | any | Which way round it may be placed: `none`, `quarter`, `half`, `threequarter`. Entries may carry a `weight`. Left out, all four are equally likely |
| `mirrors` | imprint | list | none | Flip it as well: `none`, `leftright`, `frontback`, with optional `weight`. An entry naming its own weight is written `{ "mirror": "leftright", "weight": 2 }`, and a `turns` entry the same with `turn` |
| `at` | imprint | two ints, x and z | none | Place exactly once at those block coordinates on the surface, when that chunk generates, instead of by chance. See [Structures at exact places](#structures-at-exact-places) |
| `locateAs` | imprint | string | none | Register every structure this entry places under that name, so `/locate <name>` finds the nearest. See [Finding placed structures](#finding-placed-structures) |
| `field` | field | object | `{ "type": "speckle" }` | How the field is worked out. Same keys as a hardness group's `field`, described under [The field](#the-field): `speckle` with `chances` and `spread`, or `seeded` with `cell`, `seeds`, `reach`, `arms` and `armReach` |
| `threshold` | field | 0.0 to 1.0 | `0.5` | How strong the field must be at a block before it is placed. Lower fills more |
| `fade` | field | int | `0` | Speckle out the top of the band instead of ending it flat: over the top this many blocks of the height range, each block's odds of placing thin out step by step, the same look the engine gives `deepStone` where it meets the world above |
| `rarity` | any | int | none (`400` for belt) | One placement per this many chunks. On a belt this spaces the belts out; on any other shape it gates the whole entry so only one chunk in this many rolls its `attempts` at all. `field` ignores it |
| `rarityIsPerChunk` | any | boolean | `false` | Turn `rarity` into how many placements each chunk gets instead |

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

An entry with `"retrogen": true` is generated into chunks that were saved before you added it. Each chunk records what it has had, so nothing is done twice.

The entry flag only marks an entry as eligible. Catching up is switched on by the `retrogen` setting, which a pack can set in its `settings` block or a player can set in the config, and it is off by default. Alongside it, `adoptExistingChunks` decides what happens the first time an old chunk is seen: on, the chunk is stamped as though this pack had already generated it and is never caught up; off, it is caught up like any other. Turning `retrogen` on while `adoptExistingChunks` is also on does nothing, because every old chunk is written off before it can be queued. To fill an existing world, set `retrogen` on and `adoptExistingChunks` off together.

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

The first 12 chunks around the spawn are always taken in hand, whatever a pack or the config says, because the game makes exactly that much itself before anyone joins. Left alone that ground arrives unlit and gets dressed a chunk at a time as the player walks it; adopted, it is finished in one pass and the player lands on ground that is already done. `pregenOnNewWorld` sets how much further to reach, and the command runs one by hand.

`/rdplserver pregen <radius>` makes every chunk within that many chunks of where it is run. `status` says how far along it is, `stop` ends it, and `<radius> relight` runs only the lighting pass over land that already exists, dressing the seams the run could not reach and leaving anything never made alone.

While a run is going everybody is held: made a spectator, kept in place, shown a pulsing line mid-screen, the world paused around them. The mode each player arrived in is written onto the player as they are held, so a save taken mid-run, a crash, or a rejoin never strands anyone as a spectator; the run's finish gives back exactly the mode it took, or the pack's `worldGameMode` when one is set. Progress is announced every tenth of the way, each run relights its own square when it finishes, and when everything is done players are released and greeted. How far each dimension was made is saved in the world, so a finished world never runs again, unless any of the files a dimension's land lives in go missing from the disk, which is noticed and makes that one over.

In a pack these go in a [world template's](#world-templates) `settings` block, like every other `chunks` key. Every one of them shown, with `pregenBorderLimit` the one absence since the config alone holds it:

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "pregenOnNewWorld": 63,
    "pregenDimensions": [0, -1],
    "pregenAllDimensions": false,
    "pregenDimensionsWhenEntered": [1],
    "pregenToBorder": false,
    "pregenResume": true,
    "pregenKeepLoaded": 2048,
    "pregenPauseAbove": 2000,
    "pregenMillisPerRound": 200,
    "pregenRunningSays": "Building your world, %d%% done",
    "pregenRelightSays": "Lighting your world, %d%% done",
    "pregenFinishedSays": "Your world is ready",
    "pregenStoppedSays": "World building stopped",
    "pregenSpectatingSays": "Spectating until the world is ready",
    "welcomeSays": ["Welcome to Ruby World!", "-1=Welcome to the Nether!"]
  }
}
```

| Key | What it does | Why you would set it |
| --- | --- | --- |
| `pregenOnNewWorld` | Radius in chunks made around the spawn before anybody plays. 12 is the floor and 0 means that floor rather than nothing, since the game makes 12 chunks around the spawn on its own anyway: the run adopts that ground and lights it in one pass instead of leaving it to trickle in behind the player. Raise it to reach further than the game does | Sets how far a pack reaches past the ground the game already makes |
| `pregenDimensions` | Which dimensions are made, in order, each around its own spawn | Add the nether, the end, or your own dimensions |
| `pregenAllDimensions` | Every registered dimension instead of a list, overworld first | Packs with many dimensions. Every mod's dimensions count, so mind the size |
| `pregenDimensionsWhenEntered` | These are made the first time somebody sets foot in them, holding everyone again until done | Dimensions most players never visit; the ones who never go pay nothing |
| `pregenToBorder` | Fill each dimension out to its world border instead of a radius | Bounded worlds |
| `pregenBorderLimit` | How far a border may reach before the run is refused. Config only, never a pack key | A guard against a runaway run; raise it only knowing the time and disk it allows |
| `pregenResume` | A stopped or interrupted run picks up where it left off. The run's dimension, center and radius are written into the save when it starts, so a crash, a power cut or a quit mid-run all resume within about ten seconds of where they died on the next load. A run stopped on purpose, by command or by the watchdog, stays stopped | Long runs on servers; small runs restart cheaply without it |
| `pregenKeepLoaded` | Chunks kept loaded behind the run so a chunk's neighbors are on hand when it is dressed and lit | Raise it if the relight reports many chunks left for later; costs memory |
| `pregenPauseAbove` | The run rests when this many chunks are waiting to be written | Lower it for a slow disk |
| `pregenMillisPerRound` | How long each tick may spend making land | Turn it up on an empty world, down on a server people are playing on |
| `pregenRunningSays`, `pregenRelightSays`, `pregenFinishedSays`, `pregenStoppedSays` | The chat messages for each stage. The first two may hold `%d` for the percent and, after it, `%s` for the dimension's name, or `%1$d` and `%2$s` to put them in any order, and always end with ` - ETA 00:00:00` for that pass, which is not a setting. Finished and stopped are said once, when everything asked for is done, ending with ` - Total time 00:00:00` for the whole of it, which is not a setting either | Reword them in your pack's voice, name the dimension when several are made, or silence them |
| `pregenSpectatingSays` | The mid-screen hold line while land is being made. Left at its default it speaks each player's language; empty shows nothing | Keep it under about thirty-five characters or small windows clip it |
| `welcomeSays` | The green greeting, shown on every login and after land-making. A bare entry is the line for everywhere; a `dimension=message` entry overrides it for that dimension and also greets every arrival there, e.g. `"-1=Welcome to the Nether!"`. An empty message after the `=` mutes that dimension; an empty list shows nothing. Left at its default it speaks each player's language | One bare line names your pack; add dimension lines to theme each world. Keep lines under about thirty-five characters |

Land making has its own fast path for lighting, and it stands aside when a light engine such as Alfheim or Phosphor is installed, letting that engine do the work instead. Either way you end up with finished, fully lit land.

Run it yourself before shipping, at the radius being shipped, start to finish. Chunks grow with the square of the radius, 63 either way is sixteen thousand chunks, 500 is over a million, at roughly ten kilobytes each, so your test world's region folder and wall clock are the honest numbers to put in front of players. Do not ship a radius that was never run.

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

# Control

## The control layer

Everything that stops or changes generation is grouped, and each group has one key in the config's `control` category with three values:

| Value | What it means |
| --- | --- |
| `default` | The pack decides. Config values are the fallback |
| `global` | The config wins. Pack sections are ignored |
| `off` | The group is disabled entirely and no pack can enable it |

The groups are `ores`, `biomes`, `generators`, `structures`, `spawning`, `bedrock`, `voidWorld`, `recipes`, `terrain`, `entities`, `chunks` and `commands`.

Settings resolve **biome → world template → config**. A world template's `settings` block uses the same key names as the config, so a pack sets them the same way you would:

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

With a group's control at `default` these win, at `global` they are ignored, and at `off` the whole group does nothing no matter what any pack says.

## What each group does

### Ores

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "blockOres": true,
    "oreWhitelist": ["minecraft", "mypack"],
    "oreTypes": ["COAL", "IRON"],
    "oreTypesAreBlacklist": true,
    "blockOreDimensions": [0, -1],
    "blockOreDimensionsAreBlacklist": false
  }
}
```

`blockOres` stops every mod and Minecraft generating ore except the mods in `oreWhitelist`. `oreTypes` names ore types this applies to, and `oreTypesAreBlacklist` decides the direction, on, the listed types are blocked; off, only the listed types generate. Only generation that goes through Forge's ore generation event can be reached, which is Minecraft and most mods but not all. `blockOreDimensions` limits ore blocking to certain dimensions, empty meaning every one, with `blockOreDimensionsAreBlacklist` turning that list into the dimensions to leave alone. A dimension outside the scope is not touched at all, so another mod's ores generate there untouched while the overworld stays blocked.

### Biomes

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "blockBiomes": true,
    "biomeWhitelist": ["minecraft", "mypack"],
    "biomeNames": ["minecraft:mesa", "minecraft:mesa_rock"],
    "biomeNamesAreBlacklist": true,
    "blockBiomeDimensions": [0],
    "blockBiomeDimensionsAreBlacklist": false
  }
}
```

`blockBiomes` and `biomeWhitelist` work by mod, and `biomeNames` with `biomeNamesAreBlacklist` by name. Blocked biomes are replaced on the finished biome map, which is the only way to reach oceans, mushroom islands, mesa variants, jungle, hills and shores, those are chosen outside the lists a mod can edit. Block every biome and the overworld becomes a void world by itself. `blockBiomeDimensions` limits all of it to certain dimensions, empty meaning every one, and `blockBiomeDimensionsAreBlacklist` turns that list into an exclusion.

### Generators

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "blockWorldGenerators": true,
    "generatorWhitelist": ["minecraft", "mypack"],
    "blockedGenerators": ["tconstruct"],
    "blockGeneratorDimensions": [0],
    "blockGeneratorDimensionsAreBlacklist": false,
    "generatorTypes": ["ores", "lakes"],
    "generatorTypesAreBlacklist": true,
    "generatorTypeMap": ["mrtjpcore=ores", "deworldgenhandler=structures"],
    "logBlockedGenerators": true
  }
}
```

`blockWorldGenerators` stops other mods generating through their own world generators, which is how mods add what Forge's events never see, slime islands, cave crystals and the like. `generatorWhitelist` keeps named mods, `blockedGenerators` names individual ones, and this mod's own pack generation is never blocked. `blockGeneratorDimensions` limits it to certain dimensions, with `blockGeneratorDimensionsAreBlacklist` to invert the list.

`generatorTypes` blocks by what a generator makes instead of by which mod owns it: `ores`, `structures`, `flora`, `lakes`, `terrain`, or `unknown` for the ones nothing matched. `generatorTypesAreBlacklist` decides the direction, on, the listed types are blocked; off, only the listed types generate. A type blocks whatever the whitelist says, the same way `oreTypes` does, so you can stop every mod adding ore while leaving its dungeons and trees alone.

The type comes from the generator's class name, matched against a built in list of words per type. That reads most mods correctly, `NetherOreGenerator` is ores, `SlimeIslandGenerator` is structures, but a generator named after nothing in particular, such as ProjectRed's `SimpleGenHandler` or Draconic Evolution's `DEWorldGenHandler`, comes out as `unknown`. `generatorTypeMap` fixes those by hand, one `pattern=type` per line, where the pattern is a mod id or part of a generator class name:

```
mrtjpcore=ores
deworldgenhandler=structures
```

Mapped entries are checked before the built in words, so they also correct a generator the words read the wrong way. Turn on `logBlockedGenerators` and each generator is logged with the type it was given the first time it is blocked, and `/rdplserver generators` shows the running totals by mod and type.

### Replacements

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "blockReplacements": [
      "bigreactors:oreyellorite=minecraft:stone",
      "mekanism:oreblock:0=minecraft:stone"
    ],
    "blockReplacementDimensions": [0],
    "blockReplacementDimensionsAreBlacklist": false,
    "blockReplacementMinHeight": 0,
    "blockReplacementMaxHeight": 255,
    "blockReplacementKey": "cleanup_v1"
  }
}
```

`blockReplacements` swaps blocks out of chunks that already exist, one `block=block` per line, with an optional meta on either side:

```
bigreactors:oreyellorite=minecraft:stone
mekanism:oreblock:0=minecraft:stone
tconstruct:ore:0=minecraft:netherrack
```

Each chunk is done once, as it loads from disk, and marked in the chunk's own data so it is never done twice. A chunk being generated for the first time is cleaned the next time it loads rather than straight away, because neighboring chunks are still writing into it while it generates. A chunk on the edge of explored land is cleaned but not marked, so it is cleaned again once the land around it exists. `blockReplacementDimensions` and `blockReplacementDimensionsAreBlacklist` choose where, `blockReplacementMinHeight` and `blockReplacementMaxHeight` choose the band of the world to look at, and `blockReplacementKey` is a string you change to make every chunk go through it again. It runs whether or not `retrogen` is on, since a world that needs cleaning up is usually one you do not want new veins added to. It only swaps blocks: something a mod generated as a structure cannot be taken back out this way, because the terrain it replaced was never recorded.

### Villages

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "villageBlocks": [
      "minecraft:cobblestone=mypack:ruby_brick",
      "minecraft:cobblestone=minecraft:mossy_cobblestone,20",
      "minecraft:planks=minecraft:sandstone,100,under=minecraft:sand"
    ],
    "villagePieces": ["field1", "field2"],
    "villagePiecesAreBlacklist": true
  }
}
```

Villages use the same `structure=value` lists as every other structure, under the name `villages`, so `structureSpacing`, `structureMinDistanceFromSpawn`, `structureBiomes` and `structureBiomesAreBlacklist` all reach them. A `structureBiomes` list that is not a blacklist also adds any named biome the structure's own list never held, so villages can be sent into the mountains, name them by registry name for that, since only registry names can add. Their spacing has a floor of 9, because vanilla subtracts 8 from it. `villagePieces` belongs to the same group, so one switch covers everything about where villages go and what they are built from, while the `villages` group covers only the plots a pack adds.

`villageBlocks` replaces the blocks a village is built from, as `original=replacement` pairs: `minecraft:cobblestone=mypack:ruby_brick`. It is applied after every other mod has had its say, so a pack always wins, even against mods that swap village materials per biome. Both sides accept a plain block name or a name with states. Roads are named separately by `villagePathBlock` and its siblings.

A pair may carry a chance and a condition after it, written as fields separated by commas, and then it is a rule rather than a plain swap. `minecraft:cobblestone=minecraft:mossy_cobblestone,20` weathers a fifth of the cobble a village lays; `minecraft:planks=minecraft:sandstone,100,under=minecraft:sand` changes the floor only where a house stands on sand. The fields after the pair may be given in any order, and an entry naming a field it cannot read is refused whole rather than half applied.

| Field | Value | Default | What it does |
| --- | --- | --- | --- |
| chance | int, 1 to 100 | `100` | How often the rule takes, out of a hundred |
| `at=` | block name | none | Only where this block already stands in the spot being built on |
| `under=` | block name | none | Only where this block lies directly beneath the spot |

A plain pair is answered where a piece asks the game what it should build from, so it changes every wall of that block at once. A rule is weighed where the block is actually laid, one spot at a time, which is what lets a chance and a condition mean anything, and it sees the block as it is about to be placed, after any plain pair has had its say. Which spots a chance falls on is worked out from the world seed and the spot itself, so the same world always weathers the same blocks, however many times it is generated.

Roads are never ruled, so the grades, bridges and junction designs still read the road they laid. A template plot lays its own `.nbt` file rather than building the game's way, so rules do not reach inside one; its blocks are the file's own. Plain pairs and rules both work whether or not `terrainAdaptation` is on.

`villagePieces` names vanilla village pieces, `house1`, `house2`, `house3`, `house4garden`, `church`, `woodhut`, `hall`, `field1` and `field2`, and `villagePiecesAreBlacklist` decides the direction, so you can drop vanilla's wheat fields and leave the houses, or list the only pieces you want. A pack plot is named by its own template: either the full name, `mypack:big_house`, or just `big_house`, or the plot's own name if you prefer. So a pack can ship ten plots and a world template can drop one of them without touching the other nine. So are pieces other mods add, Tektopia's houses or Recurrent Complex's plots among them: a whitelist only ever removes vanilla's own pieces, so listing the vanilla ones you want will not quietly delete somebody else's. To drop a modded piece, use a blacklist and name it, `tekhouse2` and the like.

#### Village roads

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "villagePathBlock": "minecraft:stonebrick",
    "villagePathSupportBlock": "minecraft:gravel",
    "villagePathBridgeBlock": "minecraft:planks",
    "villagePathBridgeBarrierBlock": "minecraft:oak_fence",
    "villagePathBridgeBarrierHeight": 1,
    "villagePathBridgeSidewalkBlock": "minecraft:planks",
    "villagePathCenterBlock": "minecraft:quartz_block",
    "villagePathCenterDash": 2,
    "villagePathLineBlock": "minecraft:stone_slab",
    "villagePathSidewalkBlock": "minecraft:stonebrick",
    "villagePathSidewalkWidth": 2,
    "villagePathExtraWidth": 1,
    "villagePathMinimumWidth": 0,
    "villagePathFlatRun": 6,
    "villagePathIntersects": ["mypack:crosswalk"]
  }
}
```

Everything below is experimental with the rest of the village work, and only does anything while `terrainAdaptation` is on. Every one of them is empty or zero by default, which leaves vanilla's roads exactly as they were.

| Setting | Type | Default | What it does |
| --- | --- | --- | --- |
| `villagePathBlock` | block | empty | The road surface. Empty keeps the block the biome would use, sandstone over sand, hardened clay over mesa, grass path over earth |
| `villagePathSupportBlock` | block | empty | The block under the surface, and the surface itself where the ground is bare rock. Empty keeps vanilla gravel |
| `villagePathBridgeBlock` | block | empty | What a road crosses water with. Empty keeps vanilla planks |
| `villagePathBridgeBarrierBlock` | block | empty | Barriers stacked along both edges of a bridge deck. Empty builds none |
| `villagePathBridgeBarrierHeight` | number | `1` | How many blocks tall those barriers stand |
| `villagePathBridgeSidewalkBlock` | block | empty | Decks the sidewalk where a road crosses water. Empty carries the normal sidewalk block across |
| `villagePathCenterBlock` | block | empty | A center line down the middle of the road. Empty draws none |
| `villagePathCenterDash` | number | `0` | Dashes that line: N blocks of line, then one of road. Anchored to world coordinates, so the dashes of one road piece continue into the next. `0` keeps it solid |
| `villagePathLineBlock` | block | empty | Edge lines between road and sidewalk. Empty draws none |
| `villagePathSidewalkBlock` | block | empty | Sidewalks, laid level with the road outside the edge lines. Empty lays none |
| `villagePathSidewalkWidth` | number | `2` | How wide each sidewalk is, once `villagePathSidewalkBlock` is set |
| `villagePathExtraWidth` | number | `0` | Extra blocks of road on each side beyond vanilla's 3. Widens the road pieces themselves, so houses stand back from a wide street |
| `villagePathMinimumWidth` | number | `0` | The narrowest road worth laying. A segment that cannot fit its full dress drops to a bare 3 wide alley; below this width it is not laid at all and the village lays out around it. `0` never refuses |
| `villagePathFlatRun` | number | `6` | How many blocks a road holds one height before it steps. Anchored to world coordinates so neighbouring pieces agree. `0` steps every block, as vanilla slopes do |
| `villagePathIntersects` | list | none | Designs painted at junctions, named by registry key from a pack's `<namespace>/pathintersects/`. One entry paints every junction alike; several are picked per junction by weight |

A road is dressed from the middle out: center line, then road, then edge lines, then sidewalks. Widths that do not fit fall back rather than overrun, so a narrow segment quietly loses its sidewalk before it loses its road.

`villagePathBlock` and its siblings win over `villageBlocks`. A named road block is used as it stands, while the map only touches what the road would otherwise have chosen for itself. Leave them empty and the map decides, which is how a pack keeps the biome accurate surfacing and still recolors it.

**Junction designs.** `villagePathIntersects` names files a pack ships, each one a small picture of what to paint where two roads meet, drawn as rows of single characters, one character to a block.

`<namespace>/pathintersects/*.json`

The file's path is the design's registry key, which `villagePathIntersects` then names.

```json
{
  "name": "Crosswalk",
  "weight": 3,
  "legend": { "w": "minecraft:quartz_block", "y": "minecraft:wool@4" },
  "mouth": ["wwww", "....", "wwww"],
  "corner": ["yy.", "y..", "..."]
}
```

| Key | Value | Default | What it does |
| --- | --- | --- | --- |
| `name` | string | the file name | The name used in the log |
| `weight` | int, 1 and up | `1` | Share of junctions this design wins when several are listed |
| `legend` | object of one character to a block | none | The characters the rows may use beyond the roles below. A character that is already a role is refused with a log line |
| `mouth` | list of strings | none | Rows painted on each approach, outside the crossing road. The first row is the one nearest the junction and the rest work outward. Characters run across the road and repeat where a row is shorter than the road is wide |
| `corner` | list of strings | none | Rows painted inside the junction itself. The first row is the one nearest the crossing road's edge, and within a row the first character is the one nearest the road's own edge, working inward. A cell the picture does not reach is left alone |

Five characters are roles rather than blocks, so they follow whatever the road is already dressed in: `r` is the road surface, `l` the edge line, `s` the sidewalk, `.` leaves the block exactly as it was, and `c` is reserved and paints the road surface. A role whose block the pack never set falls back to the road surface, and any other character is looked up in the `legend`, falling back to the road surface as well.

Which design a junction gets is worked out from the world seed and the junction's own position, so the same world always paints the same junctions.

#### Village decoration

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "villageDecor": ["mypack:street_flowers=2", "mypack:street_tree=1", "empty=3"]
  }
}
```

`villageDecor` scatters a pack's own worldgen along the verges of village roads, which is what stops a village reading as houses standing in bare grass. Each entry is `name=weight`: the name is a worldgen registry key, `mypack:street_flowers`, and the weight is that entry's share of the spots. The name `empty` is the share of spots left bare, and it is the one to get right, because a list without it fills every spot on every verge and the village comes out a nursery rather than a street.

Every third block along each side of a road is a spot, counted from world coordinates so the spacing carries from one road piece into the next. A spot is passed over where it falls inside any piece of the village, on the road itself, in front of a door, or where the ground is not open air standing on something solid. What grows at a spot is worked out from the world seed and the spot itself, so the same world always scatters the same way.

The name points at an ordinary worldgen entry from `<namespace>/worldgen/*.json`, so a `decoration`, a `tree` or an `imprint` all serve, and each keeps its own blocks, sizes and scatter. Only the shape of that entry is used here: its biomes, dimensions, heights and rarity are how it sows itself across the world at large, and the village does not consult them, so an entry meant for the verge is best written for nothing else. A verge is open air standing on ground, so such an entry wants `replace` set to `minecraft:air`; one that never names `replace` is given the usual default of `minecraft:stone` and quietly stands nothing here.

While `terrainAdaptation` is on, whatever a spot grows is held against the village's own tidying, so a tree standing on a verge is not felled again as the next chunk is dressed. With it off there is no tidying to hold it against, and the scatter is the same.

### Blast Plaster

What happens after an explosion, from `<namespace>/blastplaster/*.json`. `default` lets packs decide, `global` ignores pack files and leaves this mod's own defaults over Blast Plaster's config, and `off` hands Blast Plaster back to its own config entirely.

### Structures

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "structureSpacing": ["temples=24", "monuments=40", "mineshafts=200"],
    "structureSeparation": ["monuments=12"],
    "structureMinDistanceFromSpawn": ["strongholds=1000"],
    "structureBiomes": ["temples=minecraft:desert,SANDY"],
    "structureBiomesAreBlacklist": false,
    "structureSpawns": ["temples=minecraft:witch:1:1:1", "monuments="],
    "structureSpawners": ["dungeons=minecraft:zombie,minecraft:husk"],
    "structureAt": ["villages=1000,-500"]
  }
}
```

Vanilla structures switched off by name, per dimension. Placement is controlled with four lists written as `structure=value`, one per line: `structureSpacing` for how far apart they are seeded, `structureSeparation` for the closest two may be, `structureMinDistanceFromSpawn` for how far out they start, and `structureBiomes` with `structureBiomesAreBlacklist` for where they are allowed.

```
temples=24
monuments=40
mineshafts=200
```

```
temples=minecraft:desert,SANDY
monuments=minecraft:deep_ocean
```

Not every structure understands every setting. Spacing reaches temples, monuments, mansions, end cities and strongholds; for `mineshafts` the number means one chunk in that many rather than a grid, since that is how vanilla places them. Separation reaches monuments, mansions, end cities and strongholds. Biomes reach every structure except end cities, because the End is one biome in this version and there is nothing to choose between. End cities still pick their own spot within the grid: they only sit on an outer island whose surface reaches y60, so raising their spacing thins them out but cannot put one over the void. Nether fortresses sit on a fixed grid vanilla does not expose, so only the biome and spawn distance lists reach them. Villages keep their own `villageSpacing`, `villageBiomes` and the rest.

`structureSpawns` replaces the mobs a structure spawns whatever the biome around it says, written as `structure=namespace:entity:weight:least:most`, comma separated:

```
netherbridges=minecraft:blaze:10:2:3,minecraft:wither_skeleton:8:5:5
temples=minecraft:witch:1:1:1
monuments=
```

Only temples, monuments and nether fortresses keep such a list in this version; villages place their villagers from the pieces themselves, and mineshafts, strongholds and end cities use spawners and placed mobs instead. Leaving the line empty after the equals sign, as with monuments above, stops that structure spawning anything of its own.

`structureSpawners` says what the mob spawner inside a vanilla structure spawns, written as `structure=namespace:entity`, comma separated for a random pick per spawner:

```
dungeons=minecraft:zombie,minecraft:husk
mineshafts=minecraft:cave_spider
netherbridges=minecraft:wither_skeleton
strongholds=minecraft:silverfish
```

Four vanilla structures place a spawner: the dungeon room, the mineshaft corridor, the nether fortress throne and the stronghold portal room. Each is reached on its own, so spawners placed by other mods are never touched. Dungeons normally pick from the list mods add to through Forge, so naming them here takes that choice over as well.

Spacing decides where a structure is seeded, so changing it in a world that already exists leaves what is there and puts new ones on a different grid.

### Spawning

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
    "skyAnimals": false
  }
}
```

Mob spawn rates and caps, per biome. Hostile spawning is scaled by `surfaceDayMonsterRate`, `surfaceNightMonsterRate`, `undergroundDayMonsterRate` and `undergroundNightMonsterRate`, each a multiplier where `1.0` is vanilla, so daylight surface spawning can be turned off without touching the caves. The caps are `monsterCap`, `creatureCap` for passive animals, `ambientCap` for bats and the like, and `waterCreatureCap` for squid; vanilla's are 70, 10, 15 and 5, and `-1` leaves one alone. `monsterSpawnLight` caps the block light a hostile spawn tolerates on top of the vanilla checks: `0` is the modern rule, where a torch fully protects a cave, and `-1`, the default, keeps vanilla's dice. `skyAnimals` decides whether passive mobs settle on the land a rubic world generates above its terrain window, the sky islands above all: `true`, the default, leaves vanilla's herds wherever the top block is, and `false` keeps animals and bats on the ground below. Spawners ignore both.

### Seating structures

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "structureAdaptation": ["villages=beard_thin", "mansions=bury", "monuments=none"]
  }
}
```

`structureAdaptation` decides which structures the terrain adapts to and how, as `structure=mode` entries, `"mansions=bury"`, `"monuments=none"`, over villages, strongholds, mineshafts, monuments and mansions, with the five modes modern versions use: `none`, `bury`, `beard_thin`, `beard_box` and `encapsulate`. Villages are `beard_thin` unless overridden and everything else is `none` unless named, matching what modern versions choose for themselves. Temples cannot be named yet, because they place themselves only as they are built, so there is nothing for terrain to adapt to in time.

### Seating villages

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "terrainAdaptation": true
  }
}
```

**This one is experimental and still moving.** Use it at your own risk. It reshapes the terrain as the world is made, so whatever it lays down is permanent in that save, and a bug in it can leave you with a village that is half graded or a road standing on an embankment. Its behavior changes from build to build while it is being worked on, so two worlds made from the same seed on two different versions of the mod will not match, and a village laid down by an older build is never revisited or repaired by a newer one. If you care about a world, either leave this off or keep a backup, and expect the villages in it to be a snapshot of whatever the mod was doing the day those chunks generated.

`terrainAdaptation` reworks how villages choose their ground and sit on it, ported in spirit from how modern versions seat their structures, then taken further. A village only founds on a chunk whose ground varies by no more than ten blocks, and never within eight chunks of another village; regions offering no such chunk found nothing at all. The well seats to the lowest ground its own footprint touches, and the whole village shifts with it, so everything else levels from there.

Roads are graded as they are laid: the surface follows the lowest natural ground across the road's width, bumps are cut, dips are filled, the slope never exceeds one block per step, and short chasms are bridged with planks. The road surface follows the ground it crosses: grass paths on earth, sandstone on sand, hardened clay on mesa, gravel on stone and on gravel, planks over water, so a desert village gets sandstone streets rather than a dirt track and roads no longer vanish where the ground is not grass. Where two roads cross they meet at the lower of the two grades, since a level both can reach is the only one that leaves no step between them.

Each building seats one block above the road it fronts, read from the laid road or predicted from the ground the road will grade onto when the road has not been built yet, so its doorstep stairs rest on the road surface and its door sits behind them. A building whose footprint would need more than two blocks of made ground under any part of it is not built there: it slides up to twelve blocks along its road looking for the shallowest seat, and is dropped entirely if it finds none, so villages on broken ground come out sparser rather than perched. The ring around a building is banked up on the downhill side and cut back on the uphill side, one block shallower again a ring further out.

Farms keep vanilla's own ground level. Lamp posts stand at the grade of the road they light rather than the shoulder beside it, with ground filled under them where the road rides above the verge, and vanilla's own torch posts are left out of the layout since these replace them. Ground is filled beneath each building down to the nearest resting surface in the same material it rests on, walls and doorways are opened out of hillsides, dirt is lifted off roofs, and any tree standing in a structure is felled whole, its leaves going with its wood while every leaf a standing branch still owns is left alone. Mansions and the scattered features (temples, huts, igloos) are held to the same flat-ground standard before they may place.

It reshapes the terrain itself as it is made, so a world generated with it on differs from one generated without, the same warning modern versions carry, and it is off unless a pack or the config asks.

### Bedrock

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "flatBedrock": true,
    "flatBedrockRetrogen": false,
    "bedrockLayers": 1,
    "flatBedrockRoof": true,
    "flatBedrockFiller": "minecraft:stone",
    "flatBedrockFillers": ["-1=minecraft:netherrack", "1=minecraft:end_stone"],
    "flatBedrockDimensions": [0, -1],
    "flatBedrockDimensionsAreBlacklist": false,
    "flatBedrockBiomes": ["minecraft:plains"],
    "flatBedrockBiomeTypes": ["MOUNTAIN"],
    "flatBedrockBiomesAreBlacklist": true
  }
}
```

`flatBedrock` replaces the jagged layer with flat ones, per dimension and per biome, with a filler block you choose. `flatBedrockRetrogen` does it to chunks that already exist. It cannot be undone, the original pattern is not recorded anywhere. `bedrockLayers` sets how many layers are left, `flatBedrockRoof` does the ceiling too where a dimension has one, and `flatBedrockFiller` is what replaces the bedrock taken away, left empty to pick per dimension, with `flatBedrockFillers` naming one per dimension instead. Which dimensions and biomes it reaches is `flatBedrockDimensions`, `flatBedrockBiomes` and `flatBedrockBiomeTypes`, with `flatBedrockDimensionsAreBlacklist` and `flatBedrockBiomesAreBlacklist` turning those lists into exclusions.

### Slow ticking far away

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

Entities cost a server more than anything else, and most of them are nowhere near a player. `slowDistantEntities` gives a chunk with no player within `slowDistance` blocks one tick in `slowRate`, so what is in it still moves, floats, burns and despawns, only at a slower pace. Nothing is ever left unticked.

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `slowDistantEntities` | no | boolean | `true` | Whether anything is slowed at all |
| `slowedKinds` | no | list of `items`, `experience`, `projectiles` | `{items, experience}` | Which kinds are given fewer ticks. Anything that thinks for itself is always slowed instead, and is not named here. Machines are never slowed |
| `slowDistance` | no | int, 64 and up | `192` | How far from the nearest player before a chunk is slowed |
| `slowRate` | no | int, 1 to 20 | `4` | One tick in this many is given to a slowed chunk. `1` slows nothing |
| `neverSlowed` | no | list of entity names | none | Left alone however far away they are |
| `slowRecheck` | no | int, 1 to 100 | `20` | How often the distance to the nearest player is worked out again |

Anything that thinks for itself, every mob, animal, villager and golem, whatever mod it came from, is treated differently from the rest, and is not named in `slowedKinds` at all. It is never given fewer ticks, because a player can watch it walk. Instead it is left ticking every tick and made to think less often: the part of its mind that decides what to do next, which is also the expensive part of it, is asked one time in `slowRate` rather than every third tick. It keeps moving, falling, drowning, burning and pathing exactly as it would, and simply changes its mind less often while nobody is near it. There is nothing to see, no stepping and no catching up, and one a player walks up on is back to its ordinary self before it is in view. Because it cannot be noticed, it is not a choice: it happens wherever slowing is on at all.

What is given fewer ticks still ages at the ordinary pace. A dropped item and an experience orb each carry their own counter that decides when it disappears, and on a tick a slowed chunk does not take, that counter is moved on anyway. So an item still lies on the ground for five minutes rather than twenty. Only what it does each tick is reduced, never how long it lasts.

A chunk something is deliberately holding loaded is never slowed, however far away it is. Those are the chunks a chunk loader keeps, and the whole point of keeping one is that what is in it carries on running, so a farm left working while its owner is elsewhere works at the pace it was built for. The chunks around a world's spawn are not these, since nothing asked for them, so they are slowed like anywhere else.

A whole chunk is slowed or not slowed together, so what is inside it still behaves as it should: items land in the same pile, a mob still follows the one beside it. Every player counts for themselves, so someone off on their own still has quiet space around them wherever they are. Something ridden, named, tamed, leashed, glowing, kept from despawning, under an effect, or already chasing a player is left alone however far away it is, as are all machines. It applies to every world, including ones a mod adds.

### Watching chunk work

With `worldgenDebug` on, a line every hundred rounds says how the world is spending its chunk work: how many chunks were made fresh, how many had to be fetched back after being let go, how many of those came off the disk rather than out of the queue still waiting to be written, how many region files were opened and how often they were all closed at once, and the most chunks held and writes outstanding at any point. It is written for working out whether generating land is costing time in generation or in fetching the same ground back, so it is worth turning on before a large pregeneration and off afterward.

Three more lines follow it: one for writing chunks back to storage, one for lighting them, and one splitting the making of the land itself into the ground, the dressing the game puts on it, and the dressing each mod puts on it, worst five named. A slow world can then be read as four separate costs rather than one, and the mod responsible named rather than guessed at.

### Making land ahead of time

Large enough to have a section of its own, see [Pregeneration](#pregeneration).

### Blocks waiting their turn

Water spreading, lava cooling and crops growing are all blocks waiting a while before they do something, and the game keeps every one of them in a single heap. Each time a chunk is written it walks that whole heap from end to end looking for the few that belong to it, so the more of them a world has the slower every write becomes, whether or not the chunk being written has any at all. They are sorted by which chunk they sit in and the sorting is thrown away and done again the moment the heap changes or the round moves on, so writing a chunk looks only at the handful about it.

### Growing room for the blocks in a chunk

A chunk is kept in slices, and each slice holds a list of the kinds of block in it, starting with room for sixteen. Passing sixteen means making a bigger list and copying every one of the four thousand blocks in the slice across, and then again at thirty two, and again at sixty four. Ground with a few sorts of stone and ore in it passes all of those, so it is done four times over for the sake of a little room. It now goes straight to the largest of those sizes the first time it runs out, which is one copying instead of four and costs a few kilobytes a slice that is being used within moments anyway.

### Getting chunks ready to write

Before a chunk can be written it is turned into the form that goes on the disk, which walks every one of its blocks and looks each one up in a table by name. Ground comes in long runs of the same thing, so the same lookup is done thousands of times over for the same stone, and the answer to the last one is simply kept and used again when the next block is the same. It is not something that can be turned off, since there is nothing to weigh up: the answer is the same either way.

### Writing chunks out

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "hurryWritesAbove": 100
  }
}
```

The game writes finished chunks on a thread of its own, one at a time, resting a hundredth of a second after each. That holds it to about a hundred chunks a second no matter how quick the disk is, which is plenty while somebody plays and nowhere near enough while land is being made in bulk, so the unwritten chunks pile up in memory instead. `hurryWritesAbove` says how many may be waiting before it stops resting and simply writes as fast as it can. `100` is the default and matches the point at which the game itself starts holding generation back; `0` leaves it resting always, as the game does. Nothing changes while the number waiting is small, which is every ordinary moment of play.

Each time the tidying runs a line is written for it as it happens, naming which sweeper ran, how long it took, what was held before and after, and how much room the game had at the time. If that room changes it is said so, because the room growing is itself what causes the longest of these pauses: a game started with less room than it ends up needing will stop to grow it, repeatedly, at moments that have nothing to do with what it is doing. Starting it with as much room as it is allowed avoids that entirely.

A last line says how much working scrap was thrown away since the last look, how long the tidying up of it took and how many sweeps that was, and how much of the room it is allowed the game is currently holding. Making land throws away a great deal by its nature, since every chunk is turned into fresh arrays before it is written, and that tidying happens between rounds rather than during them, so it shows up as a hitch rather than as time in any of the counts above.

### Spawn chunks

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "spawnChunkRadius": 128,
    "spawnChunkRadii": ["0=64", "7=0"]
  }
}
```

The game holds the chunks around a world's spawn point loaded whether or not anyone is there, so mods have somewhere that always ticks. It is 128 blocks in every direction, about 289 chunks, and it is not adjustable in the game. `spawnChunkRadius` sets that distance. `128` is what the game does and is the default, a smaller number keeps a smaller anchor, and `0` holds none at all, so the spawn area unloads like anywhere else. `spawnChunkRadii` sets a radius for one dimension at a time, written as `dimension=blocks`, one per line, and overrides `spawnChunkRadius` for the dimensions named.

Only a dimension that was registered to hold its spawn keeps one, which in the game itself is the overworld alone, the nether and the end never held one, so setting this for them changes nothing. A dimension a mod adds holds one only if that mod asked for it, and a mod that did is often carrying a second 289 chunks a pack never wanted. Whether a world stays loaded at all is a separate thing that this does not touch: a dimension a mod marked as staying loaded still stays loaded at `0`, it simply stops holding chunks. Most mods that use spawn as an anchor want something there rather than 289 chunks of it, so a small number usually keeps them working while a `0` does not.

### Void world

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "voidWorld": true,
    "voidPlatformBlock": "minecraft:stone",
    "voidPlatformSize": 5,
    "voidPlatformHeight": 64,
    "voidWorldDimensions": [0],
    "voidWorldDimensionsAreBlacklist": false
  }
}
```

`voidWorld` generates an empty world with a platform at the spawn point, and stops mobs, animals, structures and everything a mod would otherwise generate there. The platform's block, size and height are `voidPlatformBlock`, `voidPlatformSize` and `voidPlatformHeight`; the size is rounded down to an odd number of blocks so the platform sits centered on spawn. `voidWorldDimensions` chooses which worlds are emptied, the overworld alone by default, and `voidWorldDimensionsAreBlacklist` turns that list into the ones to leave alone. The nether and the end are emptied the same way the overworld is, whether they are the ones this version builds or ones a mod has replaced them with. Only the overworld is given a platform, so a way into an emptied nether or end is something a pack provides itself. An emptied end has no dragon, no crystals and no bedrock fountain either, since the fight that builds them is left unstarted.

### The dragon

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "dragonFight": true
  }
}
```

`dragonFight` belongs to the `structures` group and decides whether the whole thing happens at all: the dragon, its bar, the crystals, the fountain it stands on, and the respawn a player would start with end crystals. An emptied end leaves it out unless a pack asks for it, and an ordinary end has it unless a pack says otherwise, so `dragonFight` is worth setting either way round.

### Terrain

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "worldType": "biomesop",
    "worldTypeExceptions": ["flat", "debug_all_block_states"],
    "worldSeed": "Hollow Ridge",
    "generatorOptions": "3;minecraft:bedrock,59*minecraft:stone,3*minecraft:dirt,minecraft:grass;1",
    "terrainWorldTypes": ["default", "customized"],
    "terrainWorldTypesAreBlacklist": false
  }
}
```

`worldType` decides what kind of world a new world is, whatever was chosen on the screen where it was made, `default`, `largebiomes`, `amplified`, `customized`, or one a mod adds such as `biomesop` or `realistic`. A pack that is built around one world type names it here and every new world is made that way. Empty, the default, leaves the choice to whoever is making the world. A world that already exists keeps the type it was made with, and a name nothing provides is logged and ignored. `worldTypeExceptions` names the choices that are left to stand, flat and the debug world to begin with, since a pack that wants one world type rarely means to take superflat away from someone testing, and whoever makes a world is told in chat, once they are in it, that the pack chose its type. That message is the config file's to decide with `tellWorldType`, not a pack's, so someone playing can turn it off for themselves and no pack can turn it back on. Settings the world was made with are dropped when the type is changed, since they were written for the type that was chosen.

`worldSeed` decides the seed every new world is made with, whatever was typed on the screen where it was made. It is written the same way it would be typed: a number is used as it is, and anything else is turned into a number the way the game turns a word into one, so `Hollow Ridge` and `-4172144997902289642` are both allowed and both always give the same world. Empty, the default, leaves the choice to whoever is making the world. A world that already exists keeps the seed it was made with, so this only ever decides what a new one gets. A pack built around one map names its seed here and every world made with that pack is that map.

`generatorOptions` shapes the overworld itself, sea level, lava oceans and every terrain noise, in the same format the customized world type writes. It is applied to a world as it is created and never afterward, so a world that already exists is left exactly as it was. A world that already carries options of its own keeps them, and the log names the string it used.

A world type that carries its own settings and never looks at the world's, as Quark's realistic one does, is given the pack's settings merged into its own, so the shape it was built for stays unless a pack asks for something else.

`terrainWorldTypes` names the world types the settings are given to at all, `default`, `customized`, `biomesop`, `realistic` and so on, and `terrainWorldTypesAreBlacklist` turns that into the list to leave alone. Empty, the default, means every world type. A pack that shapes the ordinary world but wants a mod's world type left exactly as that mod made it names it here and is done: nothing is merged, nothing is handed over, and the mod's own customize screen stays open. The names are matched against whatever world type a world was made with, so naming one that nothing here provides simply never matches and costs nothing.

Everything below about Biomes O' Plenty only happens when that mod is installed, since the work is done by compatibility that is only loaded when it is present. Without it there is no `biomesop` world type to pick, and a pack that names one is left with whatever world type the world was actually made with.

On a Biomes O' Plenty world the same settings are turned into the words that mod reads, so a pack does not need a second copy of them. `biomeSize` becomes one of its five sizes, the noise and scale settings pass through as they are, and anything it never reads is left out with a line in the log saying so. That mod reads far less than the customized world type does, and never reads sea level, caves, lakes or the structure switches from its settings at all, so those are handed to it directly instead, and a pack sets them the same way it would for any other world.

Two things it decides for itself. Rivers come out of its own layers and have no setting, so `riverSize` means nothing there. And where oceans, mountains and regions actually sit is its layers too, reachable only through `landScheme`, `tempScheme`, `rainScheme` and `biomeSize`, so a pack shapes that world in that mod's terms rather than the customized world type's. A world of a single biome is still a pack's to make: block every biome and name the one you want as the template's `default`, which works the same on its world type as on any other.

Everything else a pack does, blocking biomes and ores, replacing blocks, flat bedrock, structure placement, its own worldgen, never went through that string at all, and works the same on any world type.

### Logging

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "logBlockedOres": true,
    "logBlockedBiomes": true,
    "logBlockedGenerators": true,
    "logBlockedRecipes": true,
    "logBlockReplacements": true
  }
}
```

`logBlockedOres`, `logBlockedBiomes`, `logBlockedRecipes` and `logBlockReplacements` each log the first time something is turned away, so you can see what a blocking rule actually caught rather than guessing from what is missing. They are the first thing to turn on when a rule seems to be doing nothing, or too much.

### Recipes

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "blockRecipes": true,
    "recipeWhitelist": ["minecraft", "mypack"],
    "blockedRecipeMods": ["tconstruct"],
    "blockFurnaceRecipes": true,
    "furnaceWhitelist": ["minecraft", "mypack"],
    "blockedFurnaceMods": ["tconstruct"],
    "recipeMatch": "recipe"
  }
}
```

`blockRecipes` and `blockFurnaceRecipes` remove everything except the mods in their whitelists. Nothing is exempt by default, so list your own pack's namespace to keep its recipes. CraftTweaker and GroovyScript additions always survive, whatever the whitelist says. The whitelists are `recipeWhitelist` and `furnaceWhitelist`; `blockedRecipeMods` and `blockedFurnaceMods` go the other way and remove a named mod's recipes whatever the whitelist says. `recipeMatch` decides where the mod id is read from when crafting recipes are blocked: `recipe`, the default, uses the recipe's own name, `output` uses the item it makes, and `both` blocks when either matches and spares when either is whitelisted.

## Universal Tweaks

Universal Tweaks overlaps several of this mod's vanilla tweaks. Where they overlap, this mod stands down (logged each time, naming what was skipped) rather than have two mods edit the same method.

| What overlaps | When this mod steps aside |
| --- | --- |
| `promptLeafDecay` | Universal Tweaks has `Fast Leaf Decay` on |
| `lenientPaths` | Universal Tweaks has `Lenient Paths` on |
| `cactusMaxHeight` | Universal Tweaks is installed |
| `caneMaxHeight` | Universal Tweaks is installed |
| Nether portal return | Universal Tweaks is installed |

The first two read Universal Tweaks' own switches out of `config/Universal Tweaks - Tweaks.cfg`, so turning one off there hands that job back here. The height pair has no such switch to read, only `Cactus Size` and `Sugar Cane Size`, so this mod steps aside whenever Universal Tweaks is present at all and you set the height there instead.

**Nether portal return**: this mod records where you entered the nether and returns you there, instead of vanilla's nearest-portal search. Universal Tweaks has its own handling, so this is skipped entirely when it is installed.

**None of it touches a pack.** Everything above is about Minecraft's own cactus, cane, leaves, paths and portals. Blocks your pack defines carry their own behavior, and pack portals under `portals/*.json` are a separate system that Universal Tweaks never sees.

## Mo' Villages

Mo' Villages adds village biomes and swaps village materials — both things packs can also set. Unlike the Universal Tweaks overlaps, here the pack keeps the last word.

| What overlaps | What happens |
| --- | --- |
| `structureSpacing` for villages | Mo' Villages sets its own spacing from `villageDistance` after this mod has asked. If a pack named a spacing, this mod puts its number back and says so once in the log |
| `villageBlocks` | Mo' Villages swaps village materials per biome and marks the swap final. A pack's map is applied after that, so the pack wins |
| `structureBiomes` for villages | Mo' Villages adds its biomes to the game's own list. A pack whitelist still decides what survives |

Nothing here needs turning on. If a pack states no spacing and no block map, Mo' Villages is left alone to do as it likes.

Two things worth knowing when both are installed. Mo' Villages sets `minTownSeparation` as well, which does nothing at all in 1.12: the field is written once and never read, by the game or by this mod. And a village's blocks are decided per biome by Mo' Villages before `villageBlocks` runs, so mapping both the original block and the block Mo' Villages swapped it to catches a village either way, `minecraft:cobblestone=...` and `minecraft:brick_block=...` together.

## CoFH World

Mods that require CoFH World load without it, the requirement is removed automatically, except for mods that genuinely call its API and would crash.

Their own generation then does not happen, because CoFH World is what reads their `assets/<modid>/world/*.json`. A pack is expected to cover it.

Failing that, `readCofhWorldFiles` reads those files straight out of the mod jars and generates them through this mod. It is off by default, and it stands down when the real CoFH World is installed, which then generates as normal. Every CoFH generator and distribution that produces anything is converted, mapped onto the shapes and spreads above. The shapes are this mod's own geometry, so a lake or a spire will not look identical. Weighted structure lists, rotation and mirror tables, ignored-block lists and the taper on stalagmites all carry across. The taper is matched by shape rather than by formula, so a spire's outline is close but not identical.

Translating the files into a pack is the supported route, and the only way to change what they generate.

## Lost Cities

Lost Cities replaces the overworld generator with one of its own, so anything wired into the ordinary generator would stop working on its worlds. Compatibility that loads only when Lost Cities is installed carries three things across:

- `generatorOptions` shapes the land between and under the cities. Lost Cities reads only the noise settings, so ground level, water level, caves, lakes and the structure switches come out of its own profiles, and `seaLevel` does nothing on its worlds; the summary in the log says so. `terrainWorldTypes` gates it like any other type, matched as `lostcities`.
- A void world works, the one a fully blocked biome list brings included. Cities and land are both gone, and the platform and spawn behave the same as anywhere.
- A pack biome's `stoneBlock` replaces the stone under it, on every landscape type Lost Cities has, normal, floating, space and cavern.

The cities themselves are not this mod's to change. How big and how common they are, what the buildings are made of, ground and water level, all of it lives in Lost Cities' own profile files under `config/lostcities`, and its building JSON goes through its own `assets` setting in the same place. A pack that ships a Lost Cities world ships those files alongside it, the same way it ships any other mod's config.

`worldType` set to `lostcities` makes every new world a Lost Cities world, the same way it makes one `biomesop` or `realistic`. Forcing a type drops the settings the world would have carried, so the world lands on Lost Cities' default profile, and `defaultProfile` in `config/lostcities/general.cfg` names which one that is. The other way around, a pack that forces a different type takes Lost Cities away from a player who picked it, so a pack that means to leave that choice open adds `lostcities` to `worldTypeExceptions`.

Everything else never went through the generator to begin with and works the same as anywhere: pack worldgen, ore and biome blocking, structure spacing and spawners, flat bedrock, retrogen, pregeneration, and its two chest loot tables override and inject like any others.

## Blast Plaster integration

`<namespace>/blastplaster/*.json`

The file name is yours to choose, only the folder is read, and several files stack.

Blast Plaster (a dependency of this mod) handles post-explosion behavior: healing craters block by block, tree-aware felling, drop control. On its own it reads one global config. Driven from a pack it answers **per dimension**, and the pack ships the decision instead of asking players to edit a config. Village tree felling also reuses its tree geometry, which is why a tree over a new road comes down whole. Without pack files, Blast Plaster behaves exactly as if installed alone.

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
    "-1": { "explosionMode": "HEAL", "minimumTicksBeforeHeal": 200 },
    "1": { "enableExplosionSmoke": false }
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

## Grave mods

No setup needed. `player_loot` items join the ordinary death drops before any grave mod reads them, so they end up in the grave with the inventory — works with Gravestone, GraveStone Mod, Corail Tombstone and anything else that reads the death's drop list. Per entry, `dropLoose` bypasses the drop list so the items lie on the ground for the killer instead of going into the grave. Keys and the `dropLoose` caveat: [Player loot](#player-loot).

---

# Reference

## Value lists

These are the names the parser accepts wherever the tables above say "one of the materials", and so on. Anything unrecognized is logged and replaced with the default.

**Block materials.** `air`, `grass`, `ground`, `wood`, `rock`, `iron`, `anvil`, `water`, `lava`, `leaves`, `plants`, `vine`, `sponge`, `cloth`, `fire`, `sand`, `circuits`, `carpet`, `glass`, `redstone_light`, `tnt`, `coral`, `ice`, `packed_ice`, `snow`, `crafted_snow`, `cactus`, `clay`, `gourd`, `dragon_egg`, `portal`, `cake`, `web`, `piston`, `barrier`, `structure_void`.

**Sound types.** `wood`, `ground`, `plant`, `stone`, `metal`, `glass`, `cloth`, `sand`, `snow`, `ladder`, `anvil`, `slime`.

**Map colors.** `air`, `grass`, `sand`, `cloth`, `tnt`, `ice`, `iron`, `foliage`, `snow`, `clay`, `dirt`, `stone`, `water`, `wood`, `quartz`, `adobe`, `magenta`, `light_blue`, `yellow`, `lime`, `pink`, `gray`, `silver`, `cyan`, `purple`, `blue`, `brown`, `green`, `red`, `black`, `gold`, `diamond`, `lapis`, `emerald`, `obsidian`, `netherrack`.

**Render layers.** `solid`, `cutout`, `cutout_mipped`, `translucent`. Left empty, the block picks one to suit its type.

**Rarities.** `common`, `uncommon`, `rare`, `epic`.

**Torch particles.** `none`, `flame`, `colored`. `colored` uses `particleColor`.

**Tool classes.** `pickaxe`, `axe`, `shovel`, `sword`.

**Armor slots.** `head` or `helmet`, `chest` or `chestplate`, `legs` or `leggings`, `feet` or `boots`.

**Tints.** `biome`, `none`, or a six digit hex color. Colors anywhere in a definition are hex, with or without a leading `#`.

**Behaviors** for `behavesAs`. `till`, `path`.

The `terrain` keys below, together in a world template's `settings` block:

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "worldName": "Ruby World",
    "worldGameMode": "creative",
    "worldSpawn": "0,72,0",
    "worldBorder": 4096,
    "worldTime": 6000,
    "worldDifficulty": ["normal", "-1=hard"],
    "weatherCeiling": ["0=128"]
  }
}
```

**`worldName`** (`terrain` group) prefills the create-world screen's name box; the save folder follows from it as usual. It only fills the box while the box still holds the game's default, so a player-typed name is never overwritten, and unlike the seed and game mode it is not reapplied afterward — whatever is in the box at creation is the name.

**`worldGameMode`** (`terrain` group): `survival`, `hardcore`, `creative`, `adventure` or `spectator`. Applied at world creation only; existing worlds are untouched, and changing mode later is left alone. `hardcore` is survival plus the vanilla save-wide hardcore flag; `creative` also enables cheats, as the create screen's checkbox would. The create screen opens with the mode (and the pack's seed) pre-selected; a player may change it there, but the pack sets it back at creation. `adventure` and `spectator` are not offered on that screen and are applied as the world is made.

**`worldSpawn`** (`terrain` group): `x,z` or `x,y,z`. Applied at creation only. Without a y the surface at the world type's ground level is used. Non-integer entries are reported and ignored. Relevant on superflat in particular: vanilla's spawn search looks for grass at sea level, never finds it on a layer stack, and can wander hundreds of blocks — `worldSpawn` pins it.

**`worldBorder`** (`terrain` group): border diameter in blocks, the figure `/worldborder set` takes. Applied at creation; `0` (default) leaves the border alone; it can still be moved by command afterward. `worldBorderLimit` in the config caps what a pack may request — a pack asking for more is refused and logged, not clamped, so a pack cannot hand a server a border the operator did not agree to.

**`worldTime`** (`terrain` group): a tick value as `/time set` takes (`18000` midnight, `6000` noon). Locks the overworld clock; everything that reads the time of day (mob spawning, sleeping) sees the locked value. `-1` (default) leaves time running. The overworld analog of a custom dimension's `fixedTime`, and independent of `doDaylightCycle`.

**`worldDifficulty`** (`terrain` group): `peaceful`, `easy`, `normal` or `hard`. A bare value covers every dimension; `dimension=difficulty` lines (`-1=hard`) override per dimension. The lock holds against the pause menu. Empty (default) leaves difficulty to the player.

**`weatherCeiling`** (`terrain` group): the highest y rain and snow reach. A bare number covers every dimension; `dimension=y` lines (`0=128`) override per dimension. Above it rain does not fall, snow does not settle, cauldrons do not fill, lightning does not strike and no precipitation is drawn; below it weather is unchanged. Empty (default) means no ceiling. Ice is temperature rather than precipitation, so it still forms above the line.

**World physics** — four `terrain` keys, each a multiplier of vanilla (`1.0` = unchanged), each taking a bare value for all dimensions or `dimension=value` overrides:

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "worldGravity": ["0.17", "0=1.0"],
    "worldFallDamage": ["0.17"],
    "worldJumpStrength": ["1.0"],
    "worldTerminalVelocity": ["1.0"]
  }
}
```

| Setting | Scales | Notes |
| --- | --- | --- |
| `worldGravity` | Fall acceleration of players, mobs, dropped items, falling blocks, arrows, thrown entities, TNT and XP orbs | `0.17` is moon-like; jump arcs and projectile ranges follow automatically |
| `worldFallDamage` | Fall damage | A low-gravity dimension usually wants this matched |
| `worldJumpStrength` | Jump velocity | Applied on top of the gravity change |
| `worldTerminalVelocity` | Maximum fall speed, as a share of the vanilla cap | Elytra flight is untouched |

All four empty (default) keep vanilla physics. On Galacticraft dimensions the gravity key scales Galacticraft's own gravity.

**World seams** — stack dimensions vertically: leaving a world through its floor or ceiling delivers the entity into the dimension below or above, at the same x and z.

`<namespace>/worldtemplates/*.json`

```json
{
  "settings": {
    "worldBelow": ["0=-1"],
    "worldAbove": ["-1=0"],
    "worldSeamEntities": true,
    "worldSeamBedrock": false
  }
}
```

| Setting | Value | Default | What it does |
| --- | --- | --- | --- |
| `worldBelow` | `dimension=target` lines, or a bare id for every dimension | none | Dimension entered on falling past the world floor |
| `worldAbove` | the same | none | Dimension entered on rising past the generated top, meaning the nether's roof rather than its build limit |
| `worldSeamEntities` | boolean | `true` | Whether items, mobs and other entities cross, or players alone |
| `worldSeamBedrock` | boolean | `false` | Keep bedrock at a seam boundary. Off, the boundary generates none, so the way through can be dug |

Both lists empty (the default) keep every world closed. A world's outermost block layer is its doorway: entering the bottom layer carries you down, entering the top layer carries you back up. Arrivals land clear of it, three layers inside when travelling down and one when travelling up, so nothing bounces straight back. Coming down also cuts the layers above the arrival open all the way to the doorway, so the way in stays visible from below and serves as the way back.

Break a block in a doorway layer and the world on the other side shows through it: the sky of the dimension below appears under the floor, and the sky of the one above appears over the ceiling. That is drawn on the client alone, within render distance, and changes nothing about the world itself. Momentum carries across.

A player's crossings are remembered. Going down marks the hole, and coming back up near it lands you where that hole put you last time, so a shaft you use often always returns you to the same known spot instead of somewhere new. The first return works the landing out: that spot if it has ground under it, otherwise the nearest standing room working outward from the seam a height at a time, and otherwise a pocket cut into the rim right beside the hole, since a shaft dug straight down has no ledge of its own yet. Crossing up somewhere with no hole of yours nearby simply makes a new landing there. Arriving from below with nothing standable anywhere near falls back to the surface of that column. Feet and head are carved clear if the spot is inside rock, breaking those blocks properly so they drop, containers included.

Chains stack by giving each dimension its own lines, and riders and mounts cross separately.

Gates apply to players. A player who has not unlocked the target gets the gate's refusal message and is set back on the last ground they stood on, or a ledge near the seam; seams place no blocks, so a locked shaft cannot be farmed by falling down it. Items and mobs carry no gate of their own: with `worldSeamEntities` on they cross regardless of who lost them, and off they fall past an open floor and are lost as in any hole. `worldSeamBedrock` seals the floor instead, and a pack that keeps its bedrock supplies the passage itself, commonly a [property override](#property-overrides) giving `minecraft:bedrock` a positive `hardness`. Chunks generated before the seam keep the bedrock they already have.

**Rubic worlds** — `rubicWorld`, `worldMinHeight`, `worldMaxHeight`, `rubicWorldDimensions`, `rubicWorldDimensionsAreBlacklist` and `terrainOffset` are `terrain` keys too: see [Rubic worlds](#rubic-worlds).

**Structures** for a world template, and for the `structures` group's own lists. `villages`, `mineshafts`, `strongholds`, `temples`, `monuments`, `mansions`, `netherbridges`, `endcities`, `caves`, `ravines`, and `reccomplex`, which switches off everything Recurrent Complex generates on its own — its natural structures and its decoration stand-ins — leaving what already stands in the world untouched. Eight more name what the populate step places rather than a structure generator: `dungeons`, `waterlakes`, `lavalakes`, `netherlava`, `fire`, `glowstone`, `ice` and `animals`.

**Creature types** for biome spawns and rates. `creature`, `monster`, `ambient`, `water_creature`.

**Roles** for a world template's `roles`. `ocean`, `river`, `beach`, `mushroom`, `swamp`, `hills`, `mountain`, `jungle`, `forest`, `savanna`, `sandy`, `mesa`, `snowy`, `wasteland`, `plains`, `water`. Each names a biome that fills that role once blocking has removed the ones that would have.

**Ore types** for `oreTypes`. `COAL`, `IRON`, `GOLD`, `REDSTONE`, `DIAMOND`, `LAPIS`, `EMERALD`, `QUARTZ`, `DIRT`, `GRAVEL`, `DIORITE`, `GRANITE`, `ANDESITE`, `SILVERFISH`, `CUSTOM`.

## Folder list

Every folder, with its full path and a link to the section that describes it, is in [Where files go](#where-files-go).

## Commands

`/rdpl` runs on your own machine and needs no permissions, because everything it touches is yours. A reload rescans the folder you own, re-applies your [property overrides](#property-overrides) to your own copy of the blocks and items, and refreshes your own resources; it reaches no server, so the server's copy is reloaded with `/rdplserver reload` instead. In single player the two are one machine, so `/rdpl reload` also reloads the integrated server's loot tables, advancements and functions, the same as vanilla's own reload. It works on any server, whether or not the server has the mod.

| Command | Level | What it does |
| --- | --- | --- |
| `/rdpl list` | none | Every loaded pack, its priority, and what it contains. Click a pack to look up a file in it |
| `/rdpl which <namespace:path>` | none | Which pack provides a given file, and which packs it shadows |
| `/rdpl reload` | none | Rescan the folder and reload everything |
| `/rdpl reload <group>` | none | Reload just one kind, `textures`, `models`, `languages`, `sounds` or `shaders` |
| `/rdpl unused` | none | Files in your packs that nothing has asked for yet, usually a typo in a path |
| `/rdpl config unused` | none | Option files in `rdploader/config` that no installed pack defines any more |
| `/rdpl config prune` | none | Delete those files |
| `/rdpl pixelmap <namespace:path>` | none | What a [pixel map](#textures-written-as-pixel-maps) came out as, character by character |
| `/rdpl biome list` | none | Every biome that can generate, and its id |
| `/rdpl biome here` | none | The biome you are standing in |
| `/rdpl biome find <name>` | the server's | Linked. Passed to `/rdplserver biome find`, the only side that knows the seed |
| `/rdpl oregen`, `generators`, `gate`, `dimensions`, `pregen`, `intro`, `goto` | the server's | Linked. Passed word for word to `/rdplserver`, which decides, so see the table below |

**Which server subcommands are linked, and why the rest are not.** A server subcommand gets a passthrough exactly when the client has no meaning of its own for that name: `oregen`, `generators`, `gate`, `dimensions`, `pregen`, `intro` and `goto` can only ever mean the server's, so `/rdpl` hands them over. The six the client also has, `reload`, `list`, `which`, `unused`, `config` and `biome`, keep their own meaning of your packs and your client, and forwarding them would take that away. `biome find` is the one part of a shared name that belongs to the server anyway, since only the server knows the world seed, so that one form is passed on while `biome list` and `biome here` stay with you. That also settles the permission: the server's own operator check decides it, and a client can neither cheat it nor be told a fabricated answer.

On a dedicated server, `/rdplserver` does the same for the server's own copy of the folder. The Level column is the permission level a sender needs: `3` is an operator, `2` also admits command blocks, `0` is any player, and `4` is above operator and reaches nobody. Only `intro` and the three `goto` forms are open below operator, and `goto` is the one a pack can move.

| Command | Level | What it does |
| --- | --- | --- |
| `/rdplserver reload` | 3 | Rescan the server's folder and reload everything |
| `/rdplserver list` | 3 | Every pack the server loaded, its priority, and what it contains |
| `/rdplserver which <namespace:path>` | 3 | Which pack provides a given file, and which packs it shadows |
| `/rdplserver unused` | 3 | Files in the server's packs that nothing has asked for |
| `/rdplserver config unused` | 3 | Option files in `rdploader/config` that no installed pack defines any more |
| `/rdplserver config prune` | 3 | Delete those files |
| `/rdplserver oregen` | 3 | Running totals of ore generation that was blocked, per mod and type |
| `/rdplserver generators` | 3 | Running totals of world generators that were blocked, per mod and type |
| `/rdplserver biome` | 3 | Every biome that can generate on the server |
| `/rdplserver biome list [all]` | 3 | The same with each biome's id, and `all` includes the ones nothing can generate |
| `/rdplserver biome here` | 3 | The biome you are standing in. The console is standing nowhere, so from there it asks for a player instead |
| `/rdplserver biome here <player>` | 3 | The biome that player is standing in, which is the form the console and a script want |
| `/rdplserver biome find <name>` | 3 | The nearest place a biome generates, without generating chunks to look |
| `/rdplserver dimensions` | 3 | Every dimension, including the ones packs added |
| `/rdplserver gate list` | 3 | Every gate and whether it is open |
| `/rdplserver gate check <player>` | 3 | Which gates a player has passed |
| `/rdplserver gate grant <player> <gate>` | 3 | Open a gate for a player |
| `/rdplserver gate revoke <player> <gate>` | 3 | Close one again |
| `/rdplserver pregen <radius>` | 3 | Make every chunk within that many chunks of where it is run. See [Pregeneration](#pregeneration) |
| `/rdplserver pregen <radius> relight` | 3 | Run only the lighting pass over land that already exists |
| `/rdplserver pregen status` | 3 | How far along a run is |
| `/rdplserver pregen stop` | 3 | End it |
| `/rdplserver intro` | 0 | Let the world intro play again on your next join. Any player may run it, and it only ever clears their own |
| `/rdplserver goto <structure>` | `gotoLevel`, `3` | Take you to the nearest one nobody has been to yet, looking without generating the land on the way |
| `/rdplserver goto <structure> next` | `gotoNextLevel`, `3` | Take you onward to the closest one you have not been taken to this session, whether or not it has been visited before |
| `/rdplserver goto <structure> back` | `gotoBackLevel`, `3` | Take you to the one before it, stepping back through where this session has sent you |

**Opening `goto` up.** Every part of `/rdplserver` needs an operator, level 3, apart from `intro`, which is a player's own command and always level 0. The three `goto` forms are the one thing a pack decides: each carries a permission level of its own that a pack or the config may lower, separately from the other two and from the rest of the command.

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

The value is the permission level a sender needs. `3` (operator) is the default. `2` also admits command blocks, so a pack can put a jump on a button or pressure plate without exposing the rest of `/rdplserver`. `0` opens it to any player. The three settings are independent: for example, `next` open to command blocks for a village tour while `back` stays operator-only.

Because `intro` is open to everyone, any player reaches `/rdplserver` itself, so every other subcommand checks for operator on its own and refuses with a message. Tab completion matches: a non-operator is offered `intro`, and `goto` as well once a level lets them use it.

`gotoPlaceLevels` overrides the three settings for single places, as `name=level` entries, as in the example above. The name is whatever you would type after `goto`: a vanilla one such as `Village` or `Mansion`, or a name registered with `locateAs` on an imprint entry. Matching ignores case. A level of `4` is above operator and closes that place to everyone — the way to hide one place while the rest of `goto` is open.

An entry sets one level for all three forms of that place. An unlisted place falls back to the three settings above, and an unregistered name never matches.

Tab completion follows the same rules, so after `goto` a sender is offered only the places they may actually be carried to.

These sit in the `commands` group, so `control.commands` in the config decides whether a pack may set them at all, and `off` there keeps everything at operator whatever a pack asks for.

**`/rdpl` reaches the server command too.** Anything `/rdpl` does not handle itself, `oregen`, `generators`, `gate`, `dimensions`, `pregen`, `intro` and `goto`, is passed straight through to `/rdplserver` and offered in tab completion, so there is one command to type in single player. It is passed on word for word and the server decides as it always would, permissions and all, so nothing is opened up by typing the shorter name. The subcommands both have, `reload`, `list`, `which`, `unused`, `biome` and `config`, stay with `/rdpl` and mean the client's own packs. `biome find` is the one exception inside a shared name: only the server knows the world seed, so that form is passed on while `biome list` and `biome here` answer from your own client.

**Day-to-day editing:** `/rdpl reload textures` is much faster than F3+T in a large modpack. F3+T still works and reloads everything. Use plain `/rdpl reload` when you *add* or *delete* a file, since that changes what the folder contains.

## Good to know

- CraftTweaker and GroovyScript run after RDPL, so their changes still win.
- Recipes only load at startup, so recipe changes need a restart rather than a reload.
- Functions saved in a world's own data folder still beat a function from a pack, and so do that world's own advancements.
- A structure that has already generated stays loaded until you leave the world.
- Filename case matters. If your file's capitalization doesn't match what the game asked for, RDPL still loads it but warns you, because on Linux it wouldn't be found at all.
- Put a `pack.png` in `rdploader` to give the pack an icon.
- The folder can be moved or renamed with the `rootDirectory` option in `config/mct_resourcedatapackloader_mixin.cfg`. An absolute path works too, and it needs a restart.
- Blockstates naming a bare vanilla model inherit vanilla's textures too. Parent models such as `cube_all` and `cross` take their textures from the blockstate and are fine.
- `forge_marker: 1` does not support multipart, so vine blockstates have to be plain vanilla multipart with the textures baked into the model.

## When something doesn't work

**Check `logs/rdpl.log` first.** Everything RDPL does goes there rather than the main log. Advancements, loot tables, recipes, functions, structures and every piece of content are logged with the pack they came from, and anything malformed is logged with the reason.

**Textures and other assets are different.** They're requested far too often to log individually, so instead `/rdpl unused` lists the files in your packs that nothing has asked for. Run it once the game has finished loading. A file with the right path is always requested, so anything listed is usually a typo, but bear in mind some files only load when they're needed, such as languages other than the one you play in.

**A pack folder or zip without an `assets` directory inside it is skipped,** and the log says so.

**`/rdpl which minecraft:textures/blocks/stone.png`** tells you exactly which pack is serving a file and what it's shadowing.

## Bonus: vanilla tweaks

Small changes to how vanilla behaves, each switched in the `tweaks` config category.

| Option | Default | What it does |
| --- | --- | --- |
| `promptLeafDecay` | on | Leaves that lose their tree decay within a second instead of waiting on random ticks |
| `lenientPaths` | on | Grass paths can be made under a block and stay there when one is placed above |
| `unbreakableSpawners` | off | Mob spawners cannot be mined or blown up |

Three more sit in the `content` category rather than `tweaks`:

| Option | Default | What it does |
| --- | --- | --- |
| `cactusMaxHeight` | `3` | How tall vanilla cactus grows |
| `caneMaxHeight` | `3` | How tall vanilla sugar cane grows |
| `shovelPaths` | on | A shovel turns blocks marked `behavesAs` path into a path, and sneaking reverts one |

**These give way to Universal Tweaks**, which changes the same vanilla blocks. See [Universal Tweaks](#universal-tweaks) for exactly when.

**None of this reaches a pack.** These options only change Minecraft's own cactus, cane, leaves and paths. A block your pack defines with `"type": "cane"` carries its own `growth` section and grows to whatever height you gave it, whatever else is installed. `lenientPaths` also lifts the same restriction from pack blocks using `behavesAs`, which Universal Tweaks does not touch, so that half stays on either way.

### Unbreakable spawners

`unbreakableSpawners` gives the mob spawner block bedrock's numbers, an unbreakable hardness and an explosion resistance nothing survives. A player cannot mine one however good the pickaxe, and neither creepers, TNT, nor a pack entity that `explodes` will take one out. Creative mode still removes them, exactly as it still removes bedrock, so a pack author is never locked out of their own build. It requires a restart, since the values are set once as the game finishes loading.

**It is the block, not the spawner.** There is no per-spawner switch. The option changes `minecraft:mob_spawner` itself, so it reaches every spawner in the world at once: the four vanilla structures that place one, any a mod places, and any your own packs place.

That last one is the answer for a custom structure. A spawner inside one of your `.nbt` templates, placed by an `imprint` entry, is an ordinary mob spawner block carrying its own tile entity, so it is covered the moment the option is on. Build the structure with a spawner in it the usual way, set what it spawns in the template's tile entity data, turn `unbreakableSpawners` on, and the one in your dungeon is as unbreakable as the one in vanilla's. Nothing goes in the pack for this, and there is no way to protect only yours while leaving the rest of the world's breakable.

## Bonus: JEI plugin conflict fix

Some mods query JEI's recipe registry before the mods that provide it have finished initializing, which floods logs with hundreds of harmless-but-noisy errors and can silently break a mod's JEI integration. RDPL detects this automatically and corrects the notification order. It works with Just Enough Items and with Had Enough Items. If neither is installed, nothing happens.

## Bonus: fewer startup errors

- Recipes that reference an item no mod actually registered, usually content disabled in a mod's own config, are skipped instead of throwing a parse error. The count is logged once. (`skipMissingItems`)
- Advancements that reward a recipe a script has since removed still load, instead of failing. They just never unlock that recipe, and the whole set is summarized in one line. (`tolerateMissingInAdvancements`)