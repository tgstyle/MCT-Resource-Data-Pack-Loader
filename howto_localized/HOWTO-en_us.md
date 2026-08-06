# Resource Data Pack Loader

**One folder that overrides anything Minecraft or a mod provides, defines new content from JSON, and controls what generates, in every world, on clients and servers, with nothing for players to switch on.**

Two working examples. Drop either straight into `rdploader` and look at how each file is written.

- [RDPLExamplePack.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExamplePack.zip) covers most features, blocks, items, biomes, a dimension, a world template and every worldgen shape.
- [RDPLExampleOrePackVoid.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleOrePackVoid.zip) makes the overworld an empty void with worldgen hanging in the air, one shape per height band, so each is easy to see on its own.

---

## Contents

**Getting started**
- [What it is](#what-it-is)
- [Writing JSON](#writing-json)
- [The one rule](#the-one-rule)
- [Organizing packs](#organizing-packs)
- [Resource packs: who wins](#resource-packs-who-wins)
- [Server-side packs](#server-side-packs)

**Overriding**
- [What you can override](#what-you-can-override)
- [Registry renames](#registry-renames)

**Defining new content**
- [How definitions work](#how-definitions-work)
- [Blocks](#blocks)
- [Models, blockstates and textures](#models-blockstates-and-textures)
- [Making vanilla treat your block properly](#making-vanilla-treat-your-block-properly)
- [Items](#items)
- [Fluids](#fluids)
- [Materials, tabs, sounds, ore dictionary](#materials-tabs-sounds-ore-dictionary)
- [Furnace recipes and fuels](#furnace-recipes-and-fuels)
- [Potions, potion types and brewing](#potions-potion-types-and-brewing)
- [Villagers and trades](#villagers-and-trades)
- [Entity variants](#entity-variants)
- [Village plots](#village-plots)
- [Biomes](#biomes)
- [Dimensions](#dimensions)
- [Portals and gates](#portals-and-gates)
- [World templates](#world-templates)
- [World intro](#world-intro)
- [Game rules](#game-rules)

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
- [CoFH World](#cofh-world)

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

Resource Data Pack Loader (RDPL) adds a single folder to your instance: `rdploader`. It does three jobs.

**Overrides.** Drop a file in, and it replaces the one the game or a mod would have used. There is no toggle, no per-world setup, and nothing for players to enable in a menu. If the file is in the folder, it is what the game loads.

**New content.** Add a JSON file describing a block, item, fluid, biome, dimension, potion or villager, and it is registered. No Java, no jar.

**Control.** Stop ore, biomes, structures or recipes generating, flatten bedrock, set spawn rates, or turn the overworld into a void.

## Writing JSON

Every file here is JSON. This is one, a real worldgen entry, and it contains every shape JSON has:

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

Reading it line by line:

- The whole file is one **object**: it opens with `{` on the first line, closes with `}` on the last, and holds `"key": value` pairs with a comma after each pair except the final one.
- `"attempts": 12`, a **number**, written bare. `"maxTemperature": 0.5` is the same with a decimal.
- `"sparse": true`, a **boolean**, `true` or `false`, also bare.
- `"block": "minecraft:wool"`, **text**, always in double quotes.
- `"dimensions": [0, -1]`, a **list**, square brackets, commas between the entries. This one holds numbers.
- `"replace": ["minecraft:stone", "minecraft:andesite"]`, the same list shape holding text, so every entry is quoted.
- `"size": { "min": 4, "max": 12 }`, an **object as a value**: braces nest inside the file's own braces.
- `"blocks": [ { ... }, { ... } ]`, a **list of objects**: braces inside brackets, a comma between the two objects, and each object holds its own pairs. `"properties"` inside the first one is an object inside an object inside a list, and it nests as deep as a table asks.

The same five shapes, as a table:

| Shape | Written as | Example |
| --- | --- | --- |
| text (a string) | double quotes, always | `"minecraft:stone"` |
| number | bare, no quotes | `8`, `-1`, `0.5` |
| true or false (a boolean) | bare, no quotes | `true` |
| list (an array) | square brackets, entries separated by commas | `[0, -1]` |
| object | curly braces holding `"key": value` pairs separated by commas | `{ "min": 4, "max": 12 }` |

The rules that break files when missed:

- Keys are always in double quotes. Values are quoted only when they are text: `"8"` is text and `8` is a number, and a key expecting a number rejects the text form.
- Commas go between entries, never after the last one. A comma after the final entry is the most common broken file there is.
- A list holds entries of one kind, and the tables say which: a list of ints is `[0, -1]`, a list of block names is `["minecraft:stone", "minecraft:andesite"]`, and a list of one entry still needs its brackets, `[0]`.
- Objects nest inside other objects and inside lists, so a value can be as deep as `"shape": { "type": "cluster" }` or a list of objects like `[{ "block": "minecraft:wool", "weight": 80 }]`.

**Reading the tables.** Every table says whether a key is required, what it may hold, and what happens if you leave it out. A value the parser doesn't recognize is logged and replaced with the default rather than crashing the game. What the value words mean, each with exactly what you would type:

| When a table says | You write |
| --- | --- |
| int | `8` |
| int, ticks | `100`, twenty to a second |
| int or range | `8`, or `{ "min": 4, "max": 12 }` to roll between them |
| 0 to 15, 1 to 100 and such | an int inside those bounds |
| float | `0.5` |
| boolean | `true` or `false` |
| string | `"words in quotes"` |
| block name, item name | `"minecraft:stone"`, with metadata as a third part, `"minecraft:stone:3"` |
| `namespace:name` | `"mypack:ruby_ore"` |
| biome name, sound name, tab name | the same quoted `namespace:name` form |
| hex color | six hex digits, `"A0C8FF"`, with or without a leading `#` |
| texture path | `"mypack:blocks/ruby_ore"` |
| list of ints | `[0, -1]` |
| list of block names | `["minecraft:stone", "minecraft:andesite"]` |
| list of biome names | `["minecraft:extreme_hills", "mypack:ruby_hills"]` |
| list of dictionary types | `["MOUNTAIN", "FOREST"]` |
| list of mod ids or pack namespaces | `["quark", "mypack"]` |
| list of objects | `[{ "potion": "minecraft:strength", "amplifier": 1 }]`, each object's keys given by its own table |
| object | `{ "type": "cluster" }`, its keys given by its own table |
| object of role to biome, of variant name to variant | an object whose keys are the first thing and values the second, `{ "ocean": "mypack:ruby_ocean" }` |

Most definitions also accept `requires`, a list of mod ids or pack namespaces that must be present or the file is skipped.

## The one rule

Open the mod's jar, find the file you want to change, and copy its path from `assets` onwards.

The iron ore texture inside the Minecraft jar lives at:

```
assets/minecraft/textures/blocks/iron_ore.png
```

So your version goes at:

```
rdploader/assets/minecraft/textures/blocks/iron_ore.png
```

That's the whole system. The path after `assets` is always identical to the path inside the jar, so nothing ever needs renaming or moving.

## Organizing packs

Loose files work fine, but you can group them instead, as a folder or a zip:

```
rdploader/MyTextures/assets/...
rdploader/MyTextures.zip
```

Folders are easier while you're working. Zips are easier to hand to someone else. They behave identically.

**Control which pack wins.** If two packs contain the same file, prefix the name with `RDPL` and a number. Higher numbers load later and win:

```
rdploader/RDPL0 BaseTextures.zip
rdploader/RDPL1 SeasonalTextures.zip
rdploader/RDPL9 ModFixes.zip
```

Upper or lower case both work, a space, dash or underscore after the number is optional, and the prefix is hidden from the pack's display name. A pack with no prefix loads first, so it loses to any numbered pack.

Priority also decides the order worldgen entries generate in, which matters when one pack lays down blocks another pack replaces.

**Turn a pack off without deleting it** by adding `.disabled` to the end of its name.

## Resource packs: who wins

By default your files sit *above* the resource packs a player picks in the options screen, so a resource pack can't override them. That's right for a modpack logo and wrong for textures you'd like people to be able to reskin.

Add `O` or `N` after the `RDPL` prefix to decide per pack:

```
rdploader/RDPLO Branding        always wins, resource packs cannot touch it
rdploader/RDPLN BaseTextures    a resource pack can override it
rdploader/RDPL1O Seasonal       priority and override, both at once
```

Packs with no letter follow the `overrideResourcePacks` option in the config. `/rdpl list` marks the ones that override.

The letter has to be the end of the prefix, so it needs a space, dash or underscore after it, or nothing at all. That is what keeps a pack named `RDPLOverhaul` from having its `O` read as the letter and showing up as `Overhaul`.

---

## Server-side packs

A pack can live on the server alone, with every player on a plain vanilla client, as long as it stays on the right side of one line: **nothing in it may register anything**. The mod itself never demands to be on the client, both of its ids accept any remote, so what decides is the pack. A vanilla client plays with the block, item and sound lists it shipped with; a pack that adds to those lists needs to be on both sides, which means shipping a modpack, and this section stops applying.

What stays on the safe side, and what does not:

| Server alone is enough | Needs the pack on the client too |
| --- | --- |
| `worldgen`, `worldtemplates`, `gamerules`, `structures` | `blocks`, `items`, `fluids`, `materials` |
| `recipes`, `recipe_removals`, `furnace`, `fuels`, `brewing`, `oredict` | `potions`, `potion_types`, `sounds`, `tabs` |
| `loot_tables`, `loot_injections`, `advancements`, `functions` | `biomes`, `dimensions` |
| `gates`, `trades`, `registry_remap` | `villagers` |
| the whole control layer, settings, and pregeneration | `models`, `blockstates`, `textures`, `lang` (client folders, with no client, leave them out) |

The right-hand registry folders are hard stops, not preferences: a vanilla client sent into a dimension it has never heard of disconnects on the spot, and blocks it does not know cannot even be described to it. The left-hand column works because all of it either happens entirely on the server, generation, loot, functions, removals, the control layer, or reaches the client through packets vanilla already speaks. The crafting result slot is filled by the server in this version, advancements arrive by the ordinary advancement packets, gate refusals are plain status messages, and the pregeneration hold is nothing but vanilla game mode, title and teleport packets, so a vanilla client is held, warned and welcomed exactly like a modded one.

What to do, in order:

1. Turn on `vanillaClients` in the config, in the `content` category. It is the right-hand column as a rule rather than a discipline: everything there is skipped at loading, each pack's skipped files are named in the log, and nothing registers, so a slipped block file becomes a log line instead of a refused connection. It needs a restart, like everything else that decides what registers.
2. Keep every definition out of the right-hand folders anyway, the switch protects the door, but files that do nothing are dead weight in the pack. Where the pack reaches for an item, a gate's `hold`, a `killedDrops`, a recipe output, a trade, name only items vanilla or the server's other both-sided mods provide.
3. Entity variants may stay, with one eye open: their attributes, drops and spawns are applied by the server, but a variant's looks are painted by the client, so a vanilla client sees the ordinary creature wearing the new behavior. If the look is the point, the pack is not server-side.
4. Put the pack on the server the same way as always, in the server's pack folder. Nothing extra is installed on anybody else's machine, and `/rdpl` will not exist for them, it belongs to the mod, not the game.
5. Prove it before players do: join once with a clean vanilla client of the same version. Getting it wrong is loud, not subtle, the connection is refused or dropped at the door, not quietly broken later, so one clean join is a real test.
6. Expect the two cosmetic gaps and decide they are fine: server-added recipes craft normally but do not appear in the recipe book, and behavior-only entity variants wear stock looks. Everything else, the generated world, the rules, the loot, the locked dimensions, the pregeneration with its hold and its greeting, is the same experience the modded client gets.

# Overriding

## What you can override

- **Anything in a mod's assets folder**, textures, models, blockstates, language files, sounds, fonts, splash texts, guide books, manuals
- **Advancements and loot tables**, server side, so they work on dedicated servers too
- **Recipes**, replace a mod's recipe or add your own
- **Structure templates**, the `.nbt` files mods use for generated buildings, under `structures/`
- **Functions**, the `.mcfunction` files under `functions/`
- **Registry renames**, keep old worlds working when a mod renames a block or item
- **Recipe removals**, delete a crafting recipe by name, namespace or output
- **Loot injections**, add a pool to a loot table instead of replacing the whole thing
- **Ore dictionary names, furnace recipes, fuel burn times, creative tabs and sound events**

RDPL is good for replacing one or two recipes, and recipes for your own content should be added in the pack alongside it. For full recipe control across a modpack, CraftTweaker and GroovyScript are the better options, and a file here still replaces the original completely, so to change one ingredient or drop one loot entry, use those.

### Pack options

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

An `imprint` entry with `"locateAs": "Crypt"` registers every structure it places under that name, and `/locate Crypt` then points at the nearest one, with the name offered in tab completion. Only structures that have already generated can be found, since pack structures are placed by chance as chunks are made rather than on a grid the game could predict. The names live in the world's save, so they survive restarts and work on servers.

## Registry renames

When a mod renames one of its blocks or items, worlds saved before the rename lose them. Drop a file in `registry_remap/` to map the old name to the new one:

```json
{
  "registry": "minecraft:items",
  "mapping": { "oldmod:old_name": "newmod:new_name" }
}
```

The registry is the one the entry belongs to, usually `minecraft:items` or `minecraft:blocks`. Renames chain, so mapping A to B and later B to C sends A straight to C.

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

Every table below follows the conventions in [Writing JSON](#writing-json): whether a key is required, what it may hold, and what happens if you leave it out.

Most definitions also accept `requires`, a list of mod ids or pack namespaces that must be present or the file is skipped.

## Blocks

`blocks/*.json`

```json
{
  "type": "ore",
  "material": "rock",
  "soundType": "stone",
  "harvestTool": "pickaxe",
  "harvestToolLevel": 2,
  "creativeTab": "mypack:tab",
  "expDrop": { "min": 3, "max": 7 },
  "requires": ["mypack"],
  "variants": {
    "ruby_ore": {
      "meta": 0,
      "hardness": 3.0,
      "resistance": 5.0,
      "harvestLevel": 2,
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
| `variants` | yes | object of variant name to variant |, | One entry per metadata value. The key becomes the registry name |
| `type` | no | one of the types above | `basic` | Which shape the block takes |
| `material` | no | one of the [block materials](#value-lists) | `rock` | Mining behavior, pistons, fire and liquids |
| `soundType` | no | one of the [sound types](#value-lists) | from the material | Footsteps, breaking and placing |
| `mapColor` | no | one of the [map colors](#value-lists) | from the material | How it looks on a map |
| `harvestTool` | no | `pickaxe`, `axe`, `shovel` | `pickaxe` | Which tool harvests it |
| `harvestToolLevel` | no | 0 to 3 | `0` | 0 wood, 1 stone, 2 iron, 3 diamond |
| `silkHarvest` | no | boolean | `true` | Whether silk touch returns the block itself |
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
| `spawnsAnimals` | no | boolean | `false` | Animals may spawn on it |
| `plantTypes` | no | list of [plant types](#value-lists) | none | What can be planted on it |
| `behavesAs` | no | list of `animals`, `till`, `path`, `bush` | none | Vanilla behaviors to take on |
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
| `drops` | no | list of drops | drops itself | What breaking it yields |

**Metadata is permanent.** The number a variant claims is written into every saved world that contains it. Renumbering or reordering variants later turns placed blocks into something else. Add new variants at the end and never reuse a number.

A `basic` block can hold sixteen variants; a `slab` eight; `log` and `leaves` four, because the axis and decay flags need bits of their own; the single-state types hold one.

### Drops

```json
{
  "drops": [
    { "block": "mypack:ruby", "amount": { "min": 1, "max": 3 }, "bonusChance": [1, 2, 3] },
    { "block": "minecraft:coal", "amount": 1, "guaranteed": false }
  ]
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `block` | yes | block or item name |, | What is dropped |
| `meta` | no | int | `0` | Which variant of it |
| `amount` | no | int or range | `1` | How many |
| `bonusChance` | no | list of ints | none | Extra drops per fortune level, one entry per level |
| `guaranteed` | no | boolean | `true` | Off, the drop can fail |

### Growth

For `crop`, `flower`, `cane` and `vine`.

```json
{
  "growth": {
    "stages": 8,
    "growth": 10,
    "maxHeight": 3,
    "needsWater": true,
    "waterRange": 2,
    "drop": "mypack:reed",
    "dropCount": 1
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

Either a tree built from blocks:

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
    "vines": false
  }
}
```

…or one of your structure templates, which is the way to build something a generator cannot:

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

A block with a single variant and no other properties uses `normal` instead.

Where the block has properties of its own, they are joined with commas in the order the state lists them, `blocks=ruby_log,axis=y`, `blocks=ruby_slab,half=bottom`, `blocks=ruby_stairs,facing=east,half=bottom,shape=straight`. Two are left out on purpose: a wall's own variant property, and a leaf block's `check_decay` and `decayable`, so leaves need only `blocks=ruby_leaves`.

### Item models

By default the item uses whatever the blockstate gives that variant, so nothing more is needed. Setting `"itemModel": "item"` on the block makes it look for its own file instead, at `models/item/<block>/<variant>.json`.

Items are always that second way round, because every pack item has subtypes:

```
assets/mypack/models/item/ruby/ruby.json
assets/mypack/models/item/ruby/polished_ruby.json
```

The path is the item's registry name, then the variant name.

Fluids need no model at all, one is generated from the `still` and `flow` textures.

### Traps worth knowing

**A blockstate naming a bare vanilla model inherits vanilla's textures too.** `normal_torch`, `ladder`, `wooden_door_*` and `wheat_stage*` all carry their own textures, so a block pointing at one gets vanilla's look no matter what you put in the blockstate. Parent models such as `cube_all`, `cross` and `block/crop` take their textures from the blockstate and behave.

**`forge_marker: 1` does not support multipart.** A vine blockstate has to be plain vanilla multipart, with the textures baked into the model rather than passed in.

**Names come from the language file.** A block or item shows a raw key until `lang/en_us.lang` gives it one, in the usual `tile.mypack:ruby_ore.name=Ruby Ore` form.

## Making vanilla treat your block properly

Vanilla checks for its own blocks by identity in a dozen places, so a pack block that should obviously work often doesn't. Two keys cover it.

```json
{
  "material": "ground",
  "plantTypes": ["Plains", "Crop"],
  "behavesAs": ["animals", "till", "path"],
  "spawnsAnimals": true,
  "variants": { "ruby_grass": { "meta": 0, "hardness": 0.6 } }
}
```

**`plantTypes`** lists the Forge plant types your block supports, so saplings, crops and flowers can be planted on it.

**`behavesAs`** makes vanilla treat your block like one of its own:

| Value | What it does |
| --- | --- |
| `animals` | Animals spawn on it and pathfind toward it, as they do on grass |
| `till` | A hoe turns it into farmland |
| `path` | A shovel turns it into a grass path |
| `bush` | Counts as ground a bush or sapling will stay on |

## Items

`items/*.json`

```json
{
  "type": "food",
  "creativeTab": "mypack:tab",
  "eat": true,
  "useDuration": 32,
  "alwaysEdible": false,
  "variants": {
    "ruby_apple": { "meta": 0, "healAmount": 6, "saturation": 0.8, "rarity": "rare" },
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
| `variants` | yes | object of variant name to variant |, | One entry per metadata value. The key becomes the registry name |
| `type` | no | one of the types above | `basic` | Which type the item takes |
| `creativeTab` | no | tab name | none | The tab it appears in |
| `material` | tool, armor | material name | none | Which of your materials it is made from |
| `toolClass` | tool | `pickaxe`, `axe`, `shovel`, `sword` | none | Which tool it is |
| `slot` | armor | `head`, `chest`, `legs`, `feet` | none | Where it is worn. `helmet`, `chestplate`, `leggings` and `boots` also work |
| `eat` | food | boolean | `false` | Uses the eating animation |
| `alwaysEdible` | food | boolean | `false` | Can be eaten on a full hunger bar |
| `useDuration` | no | int, ticks | `32` | How long using it takes |
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

## Fluids

`fluids/*.json`

```json
{
  "name": "molten_ruby",
  "still": "mypack:blocks/molten_ruby_still",
  "flow": "mypack:blocks/molten_ruby_flow",
  "color": "C0304A",
  "luminosity": 12,
  "density": 2000,
  "temperature": 1500,
  "viscosity": 4000,
  "bucket": true,
  "creativeTab": "mypack:tab",
  "block": { "material": "lava", "quantaPerBlock": 8 }
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
| `block` | no | object |, | The fluid block. `material` (`water`), `flammability` (`0`), `fireSpread` (`0`), `quantaPerBlock` (`0`) |
| `requires` | no | list of mod ids or pack namespaces | none | The file is skipped unless all are present |

## Materials, tabs, sounds, ore dictionary

`materials/*.json`

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

`tabs/*.json`

```json
{ "label": "Ruby Pack", "icon": "mypack:ruby" }
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `label` | no | string | the file name | The tab's name |
| `icon` | no | item name | none | The item shown on the tab |

`sounds/*.json` is the vanilla `sounds.json` format, so a pack can ship its own audio. `oredict/*.json` adds ore dictionary names to items that already exist.

## Furnace recipes and fuels

`furnace/*.json` adds and removes smelting recipes.

```json
{
  "remove": [
    "minecraft:iron_ingot",
    { "input": "minecraft:gold_ore" }
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

`fuels/*.json`

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

`potions/*.json`

```json
{
  "name": "effect.mypack.ruby_sight",
  "color": "C0304A",
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

`potion_types/*.json`

```json
{
  "baseName": "ruby_sight",
  "effects": [
    { "potion": "mypack:ruby_sight", "duration": 3600, "amplifier": 0, "showParticles": true }
  ]
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `baseName` | no | string | the namespace and name | The name the bottle is built from |
| `effects` | yes | list of objects |, | See below |

Each effect takes `potion` (required), `duration` (`3600`), `amplifier` (`0`), `ambient` (`false`) and `showParticles` (`true`).

`brewing/*.json`

```json
{
  "brewing": [
    { "input": "minecraft:potion", "ingredient": "mypack:ruby", "output": "mypack:ruby_potion" }
  ]
}
```

Each entry is either `input`, `ingredient` and `output`, or `from`, `ingredient` and `to`.

## Villagers and trades

`villagers/*.json`

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

`trades/*.json`

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

A file in `assets/<modid>/entities/` makes a new entity out of one that already exists. It is a real entity in its own right, its own registry name, its own name in the world, its own spawn egg, and a loot table of its own if you give it one, built on another entity's behavior rather than replacing it. Nothing about the entity it copies changes.

```json
{
  "entity": "minecraft:cow",
  "name": "Angry Cow",
  "hostile": true,
  "targets": ["minecraft:player"],
  "attributes": {
    "maxHealth": 20,
    "movementSpeed": 0.32,
    "attackDamage": 4
  },
  "spawns": [
    { "creatureType": "creature", "weight": 4, "min": 1, "max": 2 }
  ],
  "biomeTypes": ["PLAINS"]
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
| `baby` | no | boolean | `false` | Stays young, and stays that way |
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
| `noAI` | no | boolean | `false` | Stands where it is put and does nothing |
| `leftHanded` | no | boolean | `false` | Holds its weapon in the other hand |
| `fireproof` | no | boolean | `false` | Never catches fire at all, so it is never hurt by fire or lava and never burns in daylight |
| `invulnerable` | no | boolean | `false` | Takes no damage from anything but the void and creative |
| `glowing` | no | boolean | `false` | Outlined through walls |
| `invisible` | no | boolean | `false` | Not drawn, though its gear still is |
| `dropChance` | no | 0 to 1 | `0` | How likely each piece of equipment is to drop |
| `scale` | no | float | `1.0` | How big it is drawn, and how big its hitbox is |
| `angryScale` | no | float | `scale` | The size it swells to while it has something to attack |
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

`scale` changes both the model and the hitbox on both sides, so what you see is what you can hit. A creature that changes its own size, an animal growing up or a zombie that is a child, is scaled around whatever size it has chosen, so the two do not fight. `angryScale` swells it while it has a target and returns it to `scale` when it loses one. Since the client is never told what a creature is hunting, the sprinting flag carries that news across, it is set on a variant that uses `angryScale` and on nothing else, so a mod reading sprinting on your variants will see it change. Growing inside a low ceiling is possible, the same way a slime growing is, so keep the difference modest.

A variant drops whatever the entity it copies drops, because the loot table is fixed in that entity's own code rather than looked up by name. `lootTable` points it at a table of your own, which you then supply at `loot_tables/entities/<name>.json` like any other.

A `texture` is bound in place of the one the entity would normally use, whatever renderer it inherits, so it works for modded entities as well as vanilla ones. It has to match the model it is drawn on, since the model is the base entity's, a skin, not a new shape. Layers keep their own textures, so armor still looks like armor on a reskinned zombie.

Armor is only ever drawn on an entity whose renderer has an armor layer, which in this version means the humanoid mobs and villagers. A variant of a cow or a spider can carry armor and gets its protection, but nothing draws it, so `armor` under `attributes` is usually the tidier way to make such a creature tough. `hideArmor` is for the other case: a humanoid that should keep the armor in its slots, for the protection or for a mod that reads them, without it being seen.

`hostile` also takes away the behavior that made the creature run: an animal that avoided players or panicked when hurt does neither once it is hostile, since otherwise it would flee the thing it is meant to be attacking. It needs an entity that walks the ground, since it uses the same attack behavior vanilla gives its own mobs. A flying or swimming base is logged and left alone. `passive` works more widely, but only reaches behavior built the way vanilla builds it, a mod whose hostility is written into its own tick or damage code is not something a pack can talk out of.

A variant is a class of its own, so a world that contains one depends on the pack that made it, the same way it depends on a mod. Take the file away and the creatures in that world go with it.

## Village plots

A file in `assets/<modid>/villages/` adds a piece villages can build, alongside the vanilla ones. Two kinds, chosen with `type`.

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

`biomes/*.json`

```json
{
  "name": "Ruby Forest",
  "type": ["FOREST", "DENSE"],
  "temperature": 0.7,
  "rainfall": 0.8,
  "baseHeight": 0.15,
  "heightVariation": 0.25,
  "topBlock": "mypack:ruby_grass",
  "fillerBlock": "minecraft:dirt",
  "waterColor": "8040A0",
  "placement": { "climate": "warm", "weight": 8, "villages": true },
  "spawns": [
    { "entity": "minecraft:sheep", "type": "creature", "weight": 12, "min": 2, "max": 4 }
  ],
  "spawnRates": { "surfaceNight": 0.5, "undergroundDay": 2.0 }
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `name` | no | string | the file name | Name shown to the player |
| `id` | no | int | assigned for you | Fixed biome id. Only set this if you need it stable |
| `type` | no | list of dictionary types | none | Such as `FOREST`, `COLD`, `NETHER` |
| `temperature` | no | float | `0.5` | Below 0.15 snows, above 1.0 is desert-hot |
| `rainfall` | no | float, 0 to 1 | `0.5` | How wet it is |
| `rain` | no | boolean | `true` | Whether weather happens at all |
| `snow` | no | boolean | `false` | Whether rain falls as snow |
| `baseHeight` | no | float | `0.1` | Terrain height. Sea level is 0, plains 0.125 |
| `heightVariation` | no | float | `0.2` | How hilly it is |
| `topBlock` | no | block name | grass | The surface block |
| `fillerBlock` | no | block name | dirt | Just below the surface |
| `stoneBlock` | no | block name | stone | The bulk of the ground |
| `types` | no | list of dictionary types | none | Registers the biome under these, such as `FOREST` or `WET`, so other mods find it |
| `waterColor` | no | hex color | `FFFFFF` | Water tint |
| `baseBiome` | no | biome name | none | An existing biome to copy settings from |
| `decoration` | no | object | vanilla counts | Per-chunk counts for trees, grass, flowers, reeds, cacti, lakes, clay and the rest |
| `spawns` | no | list of objects | vanilla list | See below |
| `keepDefaultSpawns` | no | boolean | `false` | Keep vanilla's list alongside yours |
| `spawnChance` | no | float, below 1 | `0.1` | How likely another herd is placed as the land is first made. The game keeps rolling for as long as it succeeds, so 1 never stops and fills the world until it runs out of room. Anything at or above 0.99 is refused and 0.99 used |
| `spawnRates` | no | object of `surfaceDay`, `surfaceNight`, `undergroundDay`, `undergroundNight` to a multiplier | none | How often hostile mobs spawn here, in place of the global settings. See below |
| `placement` | no | object |, | Where it generates. See below |
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

## Dimensions

`dimensions/*.json`

```json
{
  "id": 12,
  "suffix": "DIM_ruby",
  "keepLoaded": false,
  "terrain": { "type": "overworld", "structures": false },
  "biomes": { "source": "single", "biome": "mypack:ruby_forest" },
  "sky": {
    "skyColor": "3B1E4A",
    "fogColor": "20102A",
    "cloudHeight": 160,
    "hasSkyLight": true,
    "ambientLight": 0.1,
    "movementFactor": 4.0
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

`gates/*.json`

```json
{
  "name": "The Ruby Gate",
  "scope": "player",
  "dimension": 12,
  "open": false,
  "unlock": { "consume": "mypack:ruby", "consumeCount": 4, "killed": "minecraft:wither" },
  "unlockedMessage": "%dim% is now open",
  "blockedMessage": "You need %item% to enter %dim%",
  "safeReturn": true
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
| `portalBlocks` | no | list of block names | every portal | Limits the gate to these portal blocks, so one dimension can have a guarded door and an open one |

`unlock` takes `hold` (an item that must be held), `consume` with `consumeCount` (`1`), `craft` (an item that must have been crafted), `advancement`, and `killed` (an entity name, the gate opens for whoever slays one, so a boss can hold the key to a world) with `killedCount` (`1`) when one is not enough, tallied per player or for the whole world as the scope says. Adding `killedDrops` (an item name) makes the counted kills lay that item at the slayer's feet instead of opening the gate, and starts the counting over, so a key can be earned again and handed to somebody who never fought for it; gate on `hold` or `consume` of the same item to make it the key. `%item%`, `%mob%` and `%dim%` are filled in for you. A key a mob drops needs nothing special here: give the mob the drop and gate on `hold` or `consume`.

## World templates

`worldtemplates/*.json` gathers a world's shape into one file, so a pack ships a whole world at once rather than asking the player to set a dozen config options.

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

## World intro

`worldintro/*.json` shows a run of pages when a player enters the world, before they take control. Scrolling text over a picture, a title card, a slideshow, or all three in a row.

```json
{
  "once": true,
  "music": "minecraft:music.credits",
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

Text files go in `assets/<namespace>/texts/<name>.txt`. Plain text, one paragraph to a line, and blank lines are kept as blank lines. `PLAYERNAME` is swapped for the player's name, the same substitution the vanilla end poem uses.

`time` sets how long the page lasts, so the same page takes the same time whether it holds one line or twenty. Tune the reading speed by how much you put on the page. Leave `time` out and the page runs at the same speed as the vanilla credits, where more text simply takes longer.

A scrolling page moves to the next one when its time is up. The last page never advances on its own, it waits. Along the bottom are **Next Page** and **Skip All**, or a single **Continue to World** on the last page. Escape does the same as Skip All. Static pages center every line. Scrolling pages keep to a fixed column, the way the credits do.

In singleplayer the world pauses behind the intro, so nothing creeps up on the player while they read. On a server the world keeps running, and a vanilla client never sees the intro at all and joins as normal.

`once` is remembered in the player's saved data and survives death. `/rdplserver intro` clears it for whoever runs it, so the intro plays again the next time they join. It does not replay on the spot, which keeps it from being a way back into the entry sequence in the middle of a game.

Backgrounds are stretched to fill the window, so a 16:9 image suits a 16:9 window and a square one looks squashed. Crop the picture to shape rather than relying on the fit. `music` takes any registered sound event, vanilla or one your own pack adds through `sounds`. It does not loop, so a short track finishes and leaves quiet behind it.

If more than one pack ships an intro, their pages run end to end in pack order rather than one winning. Gate them with `requires` if you only want one.

## Game rules

`gamerules/*.json`

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

# Generating it

## Worldgen entries

`worldgen/*.json` describes something that generates. Every entry is a **shape** placed by a **spread**, filtered by where it is allowed.

```json
{
  "block": "mypack:ruby_ore",
  "meta": 0,
  "size": 8,
  "attempts": 12,
  "replace": ["minecraft:stone"],
  "dimensions": [0],
  "dimensionsAreBlacklist": false,
  "biomes": ["minecraft:extreme_hills"],
  "biomeTypes": ["MOUNTAIN"],
  "biomesAreBlacklist": false,
  "minTemperature": -100.0,
  "maxTemperature": 100.0,
  "minRainfall": -100.0,
  "maxRainfall": 100.0,
  "minHeight": 8,
  "maxHeight": 48,
  "minDistanceFromSpawn": 0,
  "sparse": false,
  "retrogen": false,
  "retrogenKey": "ruby_v1",
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

### Weighted blocks

`blocks` replaces `block` when one entry is not enough. Weights are relative, so 80 and 20 is four to one.

```json
{
  "blocks": [
    { "block": "minecraft:wool", "weight": 80, "properties": { "color": "magenta" } },
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

| Form | Example | What it matches |
| --- | --- | --- |
| Name | `"minecraft:stone"` | Every state of that block |
| Name and metadata | `"minecraft:stone:3"` | Only that metadata, here diorite |
| Object | `{ "block": "minecraft:stone", "properties": { "variant": "andesite" } }` | Only that state |

The object form also takes `meta` instead of `properties`, which is the same as the colon form. Use `"minecraft:air"` to generate in open space.

## Shapes

A `shape` block with a `type`. Keys not listed for a type are ignored by it.

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
| `mirrors` | imprint | list | none | Flip it as well: `none`, `leftright`, `frontback`, with optional `weight` |
| `rarity` | belt | int | `400` | One cluster per this many chunks |
| `rarityIsPerChunk` | belt | boolean | `false` | Turn `rarity` into how many clusters each chunk gets instead |

```json
{
  "shape": { "type": "geode", "radius": 6, "height": 8, "outline": "minecraft:obsidian", "fill": "minecraft:glowstone" }
}
```

```json
{
  "shape": { "type": "tree", "log": "mypack:ruby_log", "leaves": "mypack:ruby_leaves", "height": { "min": 4, "max": 7 }, "surface": ["minecraft:grass"] }
}
```

A `tree` with no `log` or `leaves` generates nothing, and says so in the log.

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

```json
{
  "spread": { "type": "centered", "center": 32, "range": 12, "smoothness": 3 }
}
```

## Retrogen

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

Making a world's land ahead of time, so nobody generates chunks while playing: no chunk lag, a known size on disk, and one wait up front instead of a stuttering first hour. Turned on by a pack with `pregenOnNewWorld`, or run by hand with the command.

`/rdplserver pregen <radius>` makes every chunk within that many chunks of where it is run. `status` says how far along it is, `stop` ends it, and `<radius> relight` runs only the lighting pass over land that already exists, dressing the seams the run could not reach and leaving anything never made alone.

While a run is going everybody is held: made a spectator, kept in place, shown a pulsing line mid-screen, the world paused around them. The mode each player arrived in is written onto the player as they are held, so a save taken mid-run, a crash, or a rejoin never strands anyone as a spectator; the run's finish gives back exactly the mode it took, or the pack's `worldGameMode` when one is set. Progress is announced every tenth of the way, each run relights its own square when it finishes, and when everything is done players are released and greeted. How far each dimension was made is saved in the world, so a finished world never runs again, unless any of the files a dimension's land lives in go missing from the disk, which is noticed and makes that one over.

| Key | What it does | Why you would set it |
| --- | --- | --- |
| `pregenOnNewWorld` | Radius in chunks made around the spawn the moment a world is created. 0 is off | The one key that turns pregeneration on for a pack |
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

In a pack these go in a [world template's](#world-templates) `settings` block, like every other `chunks` key. Every one of them shown, with `pregenBorderLimit` the one absence since the config alone holds it:

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
    "pregenWelcomeSays": "Welcome to Ruby World!"
  }
}
```

Run it yourself before shipping, at the radius being shipped, start to finish. Chunks grow with the square of the radius, 63 either way is sixteen thousand chunks, 500 is over a million, at roughly ten kilobytes each, so your test world's region folder and wall clock are the honest numbers to put in front of players. Do not ship a radius that was never run.

### How it stays fast

Everything below is how, not what: the engineering that makes a run quick, kept so it is not lost. A pack needs none of it to use the keys above.

Land making has its own fast path for lighting, and it stands aside when a light engine such as Alfheim or Phosphor is installed, letting that engine do the work instead. Either way you end up with finished, fully lit land.

The game refuses to light a chunk until all eight around it exist, and while land is being made the ones ahead have not been made yet, so lighting a chunk as it is made almost never works. Instead, as each chunk is made, the nine around that spot are looked at and any whose own ring is now complete and still held is lit then and there.

Each time the game looks at the six sides of a block while working out light, it asks for the list of the six directions, and each asking hands back a fresh copy of the same six. Over the making of a world that is tens of millions of copies of a list that never changes, all of them thrown away at once. The one list is used instead.

Where the ring of chunks is remembered matters as much as remembering it: looking it up once for every single reading, tens of millions of times, costs more than some of the readings. It is kept on the world itself, which is one lookup of a field.

Counting how long light takes is itself slow enough to matter, since asking the clock twice for every one of eighty million spreadings costs more than some of the work being counted. So every spreading is counted but only one in sixty four is timed, and the time is scaled up from those. The counts are exact and the times are close.

Almost all of the light the game works out is light it already knew. As a chunk is made it walks each column from the sky down, setting full daylight until it meets solid ground and nothing at all beneath, and then goes over every open block underneath asking what the light there should be. Underground and inside rock the answer is always none, and it was already none, so of every hundred of those questions barely two change anything. The only ones that matter are next to a cave mouth or under an overhang, where light comes in from the side.

So before asking, the answer is worked out directly: what the block lets through, and the most daylight any of the six around it holds. That is the whole of what the game would have worked out itself, and where it matches what the block already holds there is nothing to do and the question is skipped. Reaching that answer takes a handful of readings from chunks already to hand, against the far longer path the game takes to reach the same place. Lamps and fire are still asked about as before, and nothing ends up lit differently.

Spreading light is the slowest part of making land, and almost none of the cost is the light. Every time the game reads how bright a block is, or what it is, or whether it can see the sky, it looks the chunk up again from scratch, and it does that about sixteen times for every block it considers. Over one run that is well over a thousand million lookups of nine chunks that never change. Before the light is spread the game also asks twice whether the ground around the spot is all there, and asks again for every block.

So the ring of chunks about the one being lit is found once when the chunk is taken up, and every reading during that pass is served from it. The answers are the same ones the game would have arrived at, only without going and asking each time. Nothing about the light itself changes.

That also means the light is put in after the trees, ore and lakes rather than before them. Left alone the game lights the bare ground first and then has to work most of it out again as the dressing goes on, which is wasted twice over on land nobody is standing in yet. The first pass is held back and only the later one is done, whether land is being made in bulk or a player is simply walking into it: the game already tries again on any chunk that is not lit, every round, so a chunk held back is lit a round later having only been worked out once. Chunks the run is holding are also left out of the game's habit of retrying the light on every chunk every round, since the run knows when each of them is ready and the retry can only fail until then. A chunk whose turn never comes is left dark and lights itself when somebody walks up to it, and there are two quite different reasons for that. One is that it sits at the very edge of what was asked for, so the ring around it includes ground nobody asked to be made and never can be completed; that is the outer border of the square and no amount of holding will change it. The other is that it fell out of what is held before its neighbors were made, and holding more chunks does fix that. The two are counted apart so it is clear which is which.

A line says how often each of these shortcuts failed and the long way had to be taken: chunks looked up in earnest, blocks asked what they are made of, blocks named for the writing, and how many times Quark's stone generator was spared reading the world for ground too far from the middle of its cluster to be used. Only the failures are counted, since counting the successes would cost more than the successes save.

# Control

## The control layer

Everything that stops or changes generation is grouped, and each group has one key in the config's `control` category with three values:

| Value | What it means |
| --- | --- |
| `default` | The pack decides. Config values are the fallback |
| `global` | The config wins. Pack sections are ignored |
| `off` | The group is disabled entirely and no pack can enable it |

The groups are `ores`, `biomes`, `generators`, `structures`, `spawning`, `bedrock`, `voidWorld`, `recipes`, `terrain`, `entities` and `chunks`.

Settings resolve **biome → world template → config**. A world template's `settings` block uses the same key names as the config, so a pack sets them the same way you would:

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

`blockOres` stops every mod and Minecraft generating ore except the mods in `oreWhitelist`. `oreTypes` names ore types this applies to, and `oreTypesAreBlacklist` decides the direction, on, the listed types are blocked; off, only the listed types generate. Only generation that goes through Forge's ore generation event can be reached, which is Minecraft and most mods but not all. `blockOreDimensions` limits ore blocking to certain dimensions, empty meaning every one, with `blockOreDimensionsAreBlacklist` turning that list into the dimensions to leave alone. A dimension outside the scope is not touched at all, so another mod's ores generate there untouched while the overworld stays blocked.

### Biomes

`blockBiomes` and `biomeWhitelist` work by mod, and `biomeNames` with `biomeNamesAreBlacklist` by name. Blocked biomes are replaced on the finished biome map, which is the only way to reach oceans, mushroom islands, mesa variants, jungle, hills and shores, those are chosen outside the lists a mod can edit. Block every biome and the overworld becomes a void world by itself. `blockBiomeDimensions` limits all of it to certain dimensions, empty meaning every one, and `blockBiomeDimensionsAreBlacklist` turns that list into an exclusion.

### Generators

`blockWorldGenerators` stops other mods generating through their own world generators, which is how mods add what Forge's events never see, slime islands, cave crystals and the like. `generatorWhitelist` keeps named mods, `blockedGenerators` names individual ones, and this mod's own pack generation is never blocked. `blockGeneratorDimensions` limits it to certain dimensions, with `blockGeneratorDimensionsAreBlacklist` to invert the list.

`generatorTypes` blocks by what a generator makes instead of by which mod owns it: `ores`, `structures`, `flora`, `lakes`, `terrain`, or `unknown` for the ones nothing matched. `generatorTypesAreBlacklist` decides the direction, on, the listed types are blocked; off, only the listed types generate. A type blocks whatever the whitelist says, the same way `oreTypes` does, so you can stop every mod adding ore while leaving its dungeons and trees alone.

The type comes from the generator's class name, matched against a built in list of words per type. That reads most mods correctly, `NetherOreGenerator` is ores, `SlimeIslandGenerator` is structures, but a generator named after nothing in particular, such as ProjectRed's `SimpleGenHandler` or Draconic Evolution's `DEWorldGenHandler`, comes out as `unknown`. `generatorTypeMap` fixes those by hand, one `pattern=type` per line, where the pattern is a mod id or part of a generator class name:

```
mrtjpcore=ores
deworldgenhandler=structures
```

Mapped entries are checked before the built in words, so they also correct a generator the words read the wrong way. Turn on `logBlockedGenerators` and each generator is logged with the type it was given the first time it is blocked, and `/rdplserver generators` shows the running totals by mod and type.

### Replacements

`blockReplacements` swaps blocks out of chunks that already exist, one `block=block` per line, with an optional meta on either side:

```
bigreactors:oreyellorite=minecraft:stone
mekanism:oreblock:0=minecraft:stone
tconstruct:ore:0=minecraft:netherrack
```

Each chunk is done once, as it loads from disk, and marked in the chunk's own data so it is never done twice. A chunk being generated for the first time is cleaned the next time it loads rather than straight away, because neighboring chunks are still writing into it while it generates. A chunk on the edge of explored land is cleaned but not marked, so it is cleaned again once the land around it exists. `blockReplacementDimensions` and `blockReplacementDimensionsAreBlacklist` choose where, `blockReplacementMinHeight` and `blockReplacementMaxHeight` choose the band of the world to look at, and `blockReplacementKey` is a string you change to make every chunk go through it again. It runs whether or not `retrogen` is on, since a world that needs cleaning up is usually one you do not want new veins added to. It only swaps blocks: something a mod generated as a structure cannot be taken back out this way, because the terrain it replaced was never recorded.

### Villages

Villages use the same `structure=value` lists as every other structure, under the name `villages`, so `structureSpacing`, `structureMinDistanceFromSpawn`, `structureBiomes` and `structureBiomesAreBlacklist` all reach them. A `structureBiomes` list that is not a blacklist also adds any named biome the structure's own list never held, so villages can be sent into the mountains, name them by registry name for that, since only registry names can add. Their spacing has a floor of 9, because vanilla subtracts 8 from it. `villagePieces` belongs to the same group, so one switch covers everything about where villages go and what they are built from, while the `villages` group covers only the plots a pack adds.

`villagePieces` names vanilla village pieces, `house1`, `house2`, `house3`, `house4garden`, `church`, `woodhut`, `hall`, `field1` and `field2`, and `villagePiecesAreBlacklist` decides the direction, so you can drop vanilla's wheat fields and leave the houses, or list the only pieces you want. Pack plots are unaffected by the list; they are added on top.

### Structures

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

Mob spawn rates and caps, per biome. Hostile spawning is scaled by `surfaceDayMonsterRate`, `surfaceNightMonsterRate`, `undergroundDayMonsterRate` and `undergroundNightMonsterRate`, each a multiplier where `1.0` is vanilla, so daylight surface spawning can be turned off without touching the caves. The caps are `monsterCap`, `creatureCap` for passive animals, `ambientCap` for bats and the like, and `waterCreatureCap` for squid; vanilla's are 70, 10, 15 and 5, and `-1` leaves one alone.

### Seating structures

`structureAdaptation` decides which structures the terrain adapts to and how, as `structure=mode` entries, `"mansions=bury"`, `"monuments=none"`, over villages, strongholds, mineshafts, monuments and mansions, with the five modes modern versions use: `none`, `bury`, `beard_thin`, `beard_box` and `encapsulate`. Villages are `beard_thin` unless overridden and everything else is `none` unless named, matching what modern versions choose for themselves. Temples cannot be named yet, because they place themselves only as they are built, so there is nothing for terrain to adapt to in time.

### Seating villages

`terrainAdaptation` reworks how villages choose their ground and sit on it, ported in spirit from how modern versions seat their structures, then taken further. Villages only found on the flattest chunk their region offers, and never within eight chunks of another village; regions with no flat enough ground found nothing at all. The well seats to the lowest ground touching it with its rim flush with the surface, and the whole village levels from there. Roads are graded as they are laid: the surface follows the lowest natural ground across the road's width, bumps are cut, dips are filled, the slope never exceeds one block per step, and short chasms are bridged with planks. Grass paths go down on earth, gravel on stone and sand, planks over water, so roads no longer vanish where the ground is not grass. Each building seats one block above the road it fronts, read from the laid road or predicted from the ground the road will grade onto when the road has not been built yet, so its doorstep stairs rest on the road surface and its door sits behind them. Farms and lamp posts keep vanilla's own ground level. Pieces refuse ground that varies more than a few blocks under their own footprint, ground is filled beneath each building down to the nearest resting surface in the same material it rests on, walls and doorways are opened out of hillsides, dirt is lifted off roofs, and any tree standing in a structure is felled whole, its leaves going with its wood while every leaf a standing branch still owns is left alone. Mansions and the scattered features (temples, huts, igloos) are held to the same flat-ground standard before they may place. It reshapes the terrain itself as it is made, so a world generated with it on differs from one generated without, the same warning modern versions carry, and it is off unless a pack or the config asks.

### Bedrock

`flatBedrock` replaces the jagged layer with flat ones, per dimension and per biome, with a filler block you choose. `flatBedrockRetrogen` does it to chunks that already exist. It cannot be undone, the original pattern is not recorded anywhere. `bedrockLayers` sets how many layers are left, `flatBedrockRoof` does the ceiling too where a dimension has one, and `flatBedrockFiller` is what replaces the bedrock taken away, left empty to pick per dimension, with `flatBedrockFillers` naming one per dimension instead. Which dimensions and biomes it reaches is `flatBedrockDimensions`, `flatBedrockBiomes` and `flatBedrockBiomeTypes`, with `flatBedrockDimensionsAreBlacklist` and `flatBedrockBiomesAreBlacklist` turning those lists into exclusions.

### Slow ticking far away

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

The game writes finished chunks on a thread of its own, one at a time, resting a hundredth of a second after each. That holds it to about a hundred chunks a second no matter how quick the disk is, which is plenty while somebody plays and nowhere near enough while land is being made in bulk, so the unwritten chunks pile up in memory instead. `hurryWritesAbove` says how many may be waiting before it stops resting and simply writes as fast as it can. `100` is the default and matches the point at which the game itself starts holding generation back; `0` leaves it resting always, as the game does. Nothing changes while the number waiting is small, which is every ordinary moment of play.

Each time the tidying runs a line is written for it as it happens, naming which sweeper ran, how long it took, what was held before and after, and how much room the game had at the time. If that room changes it is said so, because the room growing is itself what causes the longest of these pauses: a game started with less room than it ends up needing will stop to grow it, repeatedly, at moments that have nothing to do with what it is doing. Starting it with as much room as it is allowed avoids that entirely.

A last line says how much working scrap was thrown away since the last look, how long the tidying up of it took and how many sweeps that was, and how much of the room it is allowed the game is currently holding. Making land throws away a great deal by its nature, since every chunk is turned into fresh arrays before it is written, and that tidying happens between rounds rather than during them, so it shows up as a hitch rather than as time in any of the counts above.

### Spawn chunks

The game holds the chunks around a world's spawn point loaded whether or not anyone is there, so mods have somewhere that always ticks. It is 128 blocks in every direction, about 289 chunks, and it is not adjustable in the game. `spawnChunkRadius` sets that distance. `128` is what the game does and is the default, a smaller number keeps a smaller anchor, and `0` holds none at all, so the spawn area unloads like anywhere else. `spawnChunkRadii` sets a radius for one dimension at a time, written as `dimension=blocks`, one per line, and overrides `spawnChunkRadius` for the dimensions named.

Only a dimension that was registered to hold its spawn keeps one, which in the game itself is the overworld alone, the nether and the end never held one, so setting this for them changes nothing. A dimension a mod adds holds one only if that mod asked for it, and a mod that did is often carrying a second 289 chunks a pack never wanted. Whether a world stays loaded at all is a separate thing that this does not touch: a dimension a mod marked as staying loaded still stays loaded at `0`, it simply stops holding chunks. Most mods that use spawn as an anchor want something there rather than 289 chunks of it, so a small number usually keeps them working while a `0` does not.

### Void world

`voidWorld` generates an empty world with a platform at the spawn point, and stops mobs, animals, structures and everything a mod would otherwise generate there. The platform's block, size and height are `voidPlatformBlock`, `voidPlatformSize` and `voidPlatformHeight`; the size is rounded down to an odd number of blocks so the platform sits centered on spawn. `voidWorldDimensions` chooses which worlds are emptied, the overworld alone by default, and `voidWorldDimensionsAreBlacklist` turns that list into the ones to leave alone. The nether and the end are emptied the same way the overworld is, whether they are the ones this version builds or ones a mod has replaced them with. Only the overworld is given a platform, so a way into an emptied nether or end is something a pack provides itself. An emptied end has no dragon, no crystals and no bedrock fountain either, since the fight that builds them is left unstarted.

### The dragon

`dragonFight` belongs to the `structures` group and decides whether the whole thing happens at all: the dragon, its bar, the crystals, the fountain it stands on, and the respawn a player would start with end crystals. An emptied end leaves it out unless a pack asks for it, and an ordinary end has it unless a pack says otherwise, so `dragonFight` is worth setting either way round.

### Terrain

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

`logBlockedOres`, `logBlockedBiomes`, `logBlockedRecipes` and `logBlockReplacements` each log the first time something is turned away, so you can see what a blocking rule actually caught rather than guessing from what is missing. They are the first thing to turn on when a rule seems to be doing nothing, or too much.

### Recipes

`blockRecipes` and `blockFurnaceRecipes` remove everything except the mods in their whitelists. Nothing is exempt by default, so list your own pack's namespace to keep its recipes. CraftTweaker and GroovyScript additions always survive, whatever the whitelist says. The whitelists are `recipeWhitelist` and `furnaceWhitelist`; `blockedRecipeMods` and `blockedFurnaceMods` go the other way and remove a named mod's recipes whatever the whitelist says. `recipeMatch` decides where the mod id is read from when crafting recipes are blocked, from the recipe's own name or from what it produces.

## Universal Tweaks

Universal Tweaks changes several of the same vanilla blocks and behaviors this mod does. Where they overlap, this mod stands down and lets Universal Tweaks have it, rather than both editing the same method and leaving the result to whichever loaded last. Every time that happens it says so in the log, naming what was left out.

| What overlaps | When this mod steps aside |
| --- | --- |
| `promptLeafDecay` | Universal Tweaks has `Fast Leaf Decay` on |
| `lenientPaths` | Universal Tweaks has `Lenient Paths` on |
| `cactusMaxHeight` | Universal Tweaks is installed |
| `caneMaxHeight` | Universal Tweaks is installed |
| Nether portal return | Universal Tweaks is installed |

The first two read Universal Tweaks' own switches out of `config/Universal Tweaks - Tweaks.cfg`, so turning one off there hands that job back here. The height pair has no such switch to read, only `Cactus Size` and `Sugar Cane Size`, so this mod steps aside whenever Universal Tweaks is present at all and you set the height there instead.

**Nether portal return** is the one with no option on this side. Without it, walking back through a nether portal drops you at whatever portal vanilla's search happens to find, which after enough travelling is often not the one you came from. This mod records where you entered the nether and puts you back there. Universal Tweaks has its own handling, so this is skipped entirely when it is installed.

**None of it touches a pack.** Everything above is about Minecraft's own cactus, cane, leaves, paths and portals. Blocks your pack defines carry their own behavior, and pack portals under `portals/*.json` are a separate system that Universal Tweaks never sees.

## CoFH World

Mods that require CoFH World load without it, the requirement is removed automatically, except for mods that genuinely call its API and would crash.

Their own generation then does not happen, because CoFH World is what reads their `assets/<modid>/world/*.json`. A pack is expected to cover it.

Failing that, `readCofhWorldFiles` reads those files straight out of the mod jars and generates them through this mod. It is off by default, and it stands down when the real CoFH World is installed, which then generates as normal. Every CoFH generator and distribution that produces anything is converted, mapped onto the shapes and spreads above. The shapes are this mod's own geometry, so a lake or a spire will not look identical. Weighted structure lists, rotation and mirror tables, ignored-block lists and the taper on stalagmites all carry across. The taper is matched by shape rather than by formula, so a spire's outline is close but not identical.

Translating the files into a pack is the supported route, and the only way to change what they generate.

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

**Behaviors** for `behavesAs`. `animals`, `till`, `path`, `bush`.

**The name of a new world** is set with `worldName` in the `terrain` group. The screen for making a world opens with that name already in the box, and the folder the world is saved in follows from it as it always does. It only fills the box while it still says what the game called it, so a name typed by the player is never taken away, and unlike the seed and the game mode it is not put back afterward: whatever is in the box when the world is made is what it is called.

**Game mode** is set with `worldGameMode` in the `terrain` group, one of `survival`, `creative`, `adventure` or `spectator`. Every world made while the pack is on starts that way, and creative also opens commands, the same as ticking the box when making the world by hand. It only decides how a world begins; changing mode in a world afterward is left alone. The screen for making a world starts with that mode already chosen, and with the seed a pack asks for already filled in, so what is shown there is what will happen, and a player is free to change it before making the world even though the pack will set it back. Adventure and spectator are not offered on that screen, so a pack asking for either leaves it showing whatever was chosen and sets the mode as the world is made.

**Where a new world spawns** is set with `worldSpawn` in the `terrain` group, written as `x,z` or `x,y,z`. Without a y the game's usual ground level for the world type is used, which is what vanilla stores anyway, and the player is put down on the surface there. It is applied as the world is made, so a world that already exists keeps the spawn it was born with, and an entry that is not whole numbers is reported and left to the game.

This is worth knowing on flat worlds in particular. The game picks a spawn by looking for grass at sea level, and on a superflat the block above the layer stack is always air, so that check never passes and it wanders up to a thousand steps looking. A flat world can therefore open hundreds of blocks from the origin, nowhere near where a pack expects. Naming `worldSpawn` settles it.

**The world border** is set with `worldBorder` in the `terrain` group, a whole number of blocks across, the same figure `/worldborder set` takes. It is applied as the world is made, so an existing world keeps the border it has, and `0`, the default, leaves the border where the game puts it. The border is centered wherever the game centers it, and can still be moved afterward by command in the usual way.

A pack cannot set a border of any size it likes. `worldBorderLimit` in the config is the widest a pack is allowed to ask for, and a pack asking for more is refused outright rather than quietly cut down: the reason is logged and the border is left alone. Only the person running the game can raise that limit, so a pack cannot hand a server a border it did not agree to.

**The time of day** is locked with `worldTime` in the `terrain` group, in ticks, the same figure `/time set` takes, so `18000` is midnight and `6000` is noon. The overworld clock stops there and never moves, and anything that reads whether it is day, mob spawning and sleeping among them, is told the locked time. `-1`, the default, leaves time running. This is the overworld's version of the `fixedTime` a dimension of your own can set, and unlike `doDaylightCycle` it does not matter what the clock said when the world was made.




**Structures** for a world template. `villages`, `mineshafts`, `strongholds`, `temples`, `monuments`, `mansions`, `netherbridges`, `endcities`, `caves`, `ravines`.

**Creature types** for biome spawns and rates. `creature`, `monster`, `ambient`, `water_creature`.

## Folder list

Under `assets/<namespace>/`:

| Folder | What it does |
| --- | --- |
| `blocks` | Block definitions |
| `items` | Item definitions |
| `fluids` | Fluids, with a block and a bucket |
| `materials` | Tool and armor materials |
| `biomes` | Biome definitions |
| `worldgen` | What generates, and where |
| `dimensions` | Dimension definitions |
| `worldtemplates` | A whole world's settings in one file |
| `worldintro` | Pages shown when a player enters the world |
| `gates` | Conditions on portals and dimensions |
| `gamerules` | Game rules for new worlds |
| `entities` | Entity variants built on entities that already exist |
| `villages` | Plots villages can build |
| `structures` | `.nbt` templates, for saplings, `imprint` and mod overrides |
| `recipes` | Crafting recipes, added or replaced |
| `recipe_removals` | Recipes deleted by name, namespace or output |
| `furnace` | Furnace recipes added and removed |
| `fuels` | Burn times |
| `brewing` | Brewing stand recipes |
| `potions` | Potion effects |
| `potion_types` | Bottled potions built from those effects |
| `villagers` | Villager professions |
| `trades` | What careers buy and sell |
| `sounds` | Sound events |
| `oredict` | Ore dictionary names |
| `loot_tables` | Loot tables, replaced |
| `loot_injections` | A pool added to a table that already exists |
| `advancements` | Advancements |
| `functions` | `.mcfunction` files |
| `registry_remap` | Old names mapped to new ones |
| `tabs` | Creative tabs |
| `texts` | Plain text files, used by the world intro |
| `models`, `blockstates`, `textures`, `lang` | The usual asset folders |

## Commands

`/rdpl` runs on your own machine and needs no permissions, because it only reads files you already have. It works on any server, whether or not the server has the mod.

| Command | What it does |
| --- | --- |
| `/rdpl list` | Every loaded pack, its priority, and what it contains. Click a pack to look up a file in it |
| `/rdpl which <namespace:path>` | Which pack provides a given file, and which packs it shadows |
| `/rdpl reload` | Rescan the folder and reload everything |
| `/rdpl reload <group>` | Reload just one kind, `textures`, `models`, `languages`, `sounds` or `shaders` |
| `/rdpl unused` | Files in your packs that nothing has asked for yet, usually a typo in a path |
| `/rdpl biome list` | Every biome that can generate, and its id |
| `/rdpl biome here` | The biome you are standing in |
| `/rdpl biome find <name>` | The nearest place a biome generates, without generating chunks to look |

On a dedicated server, `/rdplserver` does the same for the server's own copy of the folder, and needs operator permission.

| Command | What it does |
| --- | --- |
| `/rdplserver reload` | Rescan the server's folder and reload everything |
| `/rdplserver list` | Every pack the server loaded, its priority, and what it contains |
| `/rdplserver which <namespace:path>` | Which pack provides a given file, and which packs it shadows |
| `/rdplserver unused` | Files in the server's packs that nothing has asked for |
| `/rdplserver oregen` | Running totals of ore generation that was blocked, per mod and type |
| `/rdplserver biome` | Every biome that can generate on the server |
| `/rdplserver dimensions` | Every dimension, including the ones packs added |
| `/rdplserver gate list` | Every gate and whether it is open |
| `/rdplserver gate check <player>` | Which gates a player has passed |
| `/rdplserver gate grant <player> <gate>` | Open a gate for a player |
| `/rdplserver gate revoke <player> <gate>` | Close one again |
| `/rdplserver intro` | Let the world intro play again on your next join |

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