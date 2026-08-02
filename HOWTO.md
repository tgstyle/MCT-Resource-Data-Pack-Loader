# Resource Data Pack Loader

**One folder that overrides anything Minecraft or a mod provides, defines new content from JSON, and controls what generates — in every world, on clients and servers, with nothing for players to switch on.**

Two working examples. Drop either straight into `rdploader` and look at how each file is written.

- [RDPLExamplePack.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExamplePack.zip) covers most features — blocks, items, biomes, a dimension, a world template and every worldgen shape.
- [RDPLExampleOrePackVoid.zip](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/raw/refs/heads/1.12.2-1.0-Release/example/RDPLExampleOrePackVoid.zip) makes the overworld an empty void with worldgen hanging in the air, one shape per height band, so each is easy to see on its own.

---

## Contents

**Getting started**
- [What it is](#what-it-is)
- [The one rule](#the-one-rule)
- [Organizing packs](#organizing-packs)
- [Resource packs: who wins](#resource-packs-who-wins)

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
- [Biomes](#biomes)
- [Dimensions](#dimensions)
- [Portals and gates](#portals-and-gates)
- [World templates](#world-templates)
- [Game rules](#game-rules)

**Generating it**
- [Worldgen entries](#worldgen-entries)
- [Shapes](#shapes)
- [Spreads](#spreads)
- [Retrogen](#retrogen)

**Control**
- [The control layer](#the-control-layer)
- [What each group does](#what-each-group-does)
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

## What it is

Resource Data Pack Loader (RDPL) adds a single folder to your instance: `rdploader`. It does three jobs.

**Overrides.** Drop a file in, and it replaces the one the game or a mod would have used. There is no toggle, no per-world setup, and nothing for players to enable in a menu. If the file is in the folder, it is what the game loads.

**New content.** Add a JSON file describing a block, item, fluid, biome, dimension, potion or villager, and it is registered. No Java, no jar.

**Control.** Stop ore, biomes, structures or recipes generating, flatten bedrock, set spawn rates, or turn the overworld into a void.

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

Loose files work fine, but you can group them instead — as a folder or a zip:

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

---

## What you can override

- **Anything in a mod's assets folder** — textures, models, blockstates, language files, sounds, fonts, splash texts, guide books, manuals
- **Advancements and loot tables** — server side, so they work on dedicated servers too
- **Recipes** — replace a mod's recipe or add your own
- **Structure templates** — the `.nbt` files mods use for generated buildings, under `structures/`
- **Functions** — the `.mcfunction` files under `functions/`
- **Registry renames** — keep old worlds working when a mod renames a block or item
- **Recipe removals** — delete a crafting recipe by name, namespace or output
- **Loot injections** — add a pool to a loot table instead of replacing the whole thing
- **Ore dictionary names, furnace recipes, fuel burn times, creative tabs and sound events**

A file here replaces the original completely. To change one ingredient or drop one loot entry, CraftTweaker is the better tool.

## Entity variants

A file in `assets/<modid>/entities/` makes a new entity out of one that already exists. It is a real entity in its own right — its own registry name, its own name in the world, its own spawn egg, and a loot table of its own if you give it one — built on another entity's behavior rather than replacing it. Nothing about the entity it copies changes.

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
| `profession` | no | `namespace:name` | random | For a villager, the trade it practises |
| `career` | no | int | random | Which career within that profession, from 1 upwards |
| `baby` | no | boolean | `false` | Stays young, and stays that way |
| `sounds` | no | object | the base's | `ambient`, `hurt` and `death`, each a registered sound event |
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
| `equipment` | no | object | none | `mainhand`, `offhand`, `head`, `chest`, `legs`, `feet`, each an item name |
| `spawns` | no | list of objects | none | `creatureType`, `weight`, `min` and `max`, the same shape a biome uses |
| `biomes` | no | list of biome names | every biome | Where those spawns are added |
| `biomeTypes` | no | list of dictionary types | none | The same, by type |
| `trackingRange` | no | int | `80` | How far away the client is told about it |
| `trackingFrequency` | no | int | `3` | How often, in ticks |
| `requires` | no | list of mod ids or pack namespaces | none | The variant is left out unless all are present |

`scale` changes both the model and the hitbox on both sides, so what you see is what you can hit. A creature that changes its own size, an animal growing up or a zombie that is a child, is scaled around whatever size it has chosen, so the two do not fight. `angryScale` swells it while it has a target and returns it to `scale` when it loses one. Since the client is never told what a creature is hunting, the sprinting flag carries that news across — it is set on a variant that uses `angryScale` and on nothing else, so a mod reading sprinting on your variants will see it change. Growing inside a low ceiling is possible, the same way a slime growing is, so keep the difference modest.

A variant drops whatever the entity it copies drops, because the loot table is fixed in that entity's own code rather than looked up by name. `lootTable` points it at a table of your own, which you then supply at `loot_tables/entities/<name>.json` like any other.

A `texture` is bound in place of the one the entity would normally use, whatever renderer it inherits, so it works for modded entities as well as vanilla ones. It has to match the model it is drawn on, since the model is the base entity's — a skin, not a new shape. Layers keep their own textures, so armor still looks like armor on a reskinned zombie.

Armor is only ever drawn on an entity whose renderer has an armor layer, which in this version means the humanoid mobs and villagers. A variant of a cow or a spider can carry armor and gets its protection, but nothing draws it, so `armor` under `attributes` is usually the tidier way to make such a creature tough. `hideArmor` is for the other case: a humanoid that should keep the armor in its slots, for the protection or for a mod that reads them, without it being seen.

`hostile` also takes away the behavior that made the creature run: an animal that avoided players or panicked when hurt does neither once it is hostile, since otherwise it would flee the thing it is meant to be attacking. It needs an entity that walks the ground, since it uses the same attack behavior vanilla gives its own mobs. A flying or swimming base is logged and left alone. `passive` works more widely, but only reaches behavior built the way vanilla builds it — a mod whose hostility is written into its own tick or damage code is not something a pack can talk out of.

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

## Registry renames

When a mod renames one of its blocks or items, worlds saved before the rename lose them. Drop a file in `registry_remap/` to map the old name to the new one:

```json
{
  "registry": "minecraft:items",
  "mapping": { "oldmod:old_name": "newmod:new_name" }
}
```

The registry is the one the entry belongs to — usually `minecraft:items` or `minecraft:blocks`. Renames chain, so mapping A to B and later B to C sends A straight to C.

---

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

So pick one name at the start and never change it. Renaming a namespace orphans everything already placed in a world, the same as a mod changing its id — that is what `registry_remap` exists to repair.

This works both ways: `requires` accepts a pack namespace as readily as an installed mod id, so one pack can depend on another and be skipped when it isn't installed.

**A missing mod stops the game, the way a mod's own dependency does.** Every mod id named by a `requires` anywhere in your packs is handed to Forge as a dependency of this mod, before anything loads. If one isn't installed you get the standard Missing Mods screen naming what is needed, on a client or a dedicated server, and nothing generates or registers in the meantime.

A missing *pack* is different. Pack namespaces are not mods, so they never reach that check — the definition is skipped, one line goes to `logs/rdpl.log` naming what was missing, and the game carries on. If a block you expected is not in the creative tab, that log line is the first place to look.

`requires` takes bare ids only. There is no version range syntax, so it can say a mod must be present but not which version.

The mod's own two ids, `resourcedatapackloader` and `resourcedatapackloader_mixin`, are reserved. Defining content under them is ignored and logged, because it would claim ownership of things this mod registers. Overriding this mod's own assets is still fine — only registering content there is not.

**Reading the tables.** Every table says whether a key is required, what it may hold, and what happens if you leave it out. A value the parser doesn't recognize is logged and replaced with the default rather than crashing the game.

- Blocks and items are named `namespace:name`, and a metadata value can be added as a third part, `namespace:name:meta`.
- Colors are six hex digits, `A0C8FF`, with or without a leading `#`.
- Where a table says **int or range**, you may write either `8` or `{ "min": 4, "max": 12 }`.
- Most definitions accept `requires`, a list of mod ids or pack namespaces that must be present or the file is skipped.

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
| `variants` | yes | object of variant name to variant | — | One entry per metadata value. The key becomes the registry name |
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
| `meta` | yes | 0 to 15 | — | The metadata value this variant claims |
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
| `block` | yes | block or item name | — | What is dropped |
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
| `growth` | no | int | — | One in N chance per random tick to advance |
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

Where the block has properties of its own, they are joined with commas in the order the state lists them — `blocks=ruby_log,axis=y`, `blocks=ruby_slab,half=bottom`, `blocks=ruby_stairs,facing=east,half=bottom,shape=straight`. Two are left out on purpose: a wall's own variant property, and a leaf block's `check_decay` and `decayable`, so leaves need only `blocks=ruby_leaves`.

### Item models

By default the item uses whatever the blockstate gives that variant, so nothing more is needed. Setting `"itemModel": "item"` on the block makes it look for its own file instead, at `models/item/<block>/<variant>.json`.

Items are always that second way round, because every pack item has subtypes:

```
assets/mypack/models/item/ruby/ruby.json
assets/mypack/models/item/ruby/polished_ruby.json
```

The path is the item's registry name, then the variant name.

Fluids need no model at all — one is generated from the `still` and `flow` textures.

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

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `variants` | yes | object of variant name to variant | — | One entry per metadata value. The key becomes the registry name |
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
| `meta` | yes | 0 to 15 | — | The metadata value this variant claims |
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
| `block` | no | object | — | The fluid block. `material` (`water`), `flammability` (`0`), `fireSpread` (`0`), `quantaPerBlock` (`0`) |
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
| `reduction` | no | list of four ints | — | Armor points, in the order feet, legs, chest, head |
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
| `attributes` | no | list | none | `attribute`, `uuid`, `amount` (`0.0`), `operation` (`0`) |

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
| `effects` | yes | list | — | See below |

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
  "texture": "mypack:textures/entity/villager/jeweller.png",
  "zombieTexture": "mypack:textures/entity/zombie_villager/jeweller.png"
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
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
| `profession` | yes | profession name | — | Whose trade this is |
| `career` | yes | career name | — | Which career within it |
| `level` | no | int | `1` | Which trade tier it appears at |
| `maxUses` | no | int | `12` | Times it can be used before locking |

A stack is `item` with `min` (`1`) and `max` (`min`), so a fixed price is just `min`.

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
  "spawnRates": { "monster": 0.5 }
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
| `waterColor` | no | hex color | `FFFFFF` | Water tint |
| `baseBiome` | no | biome name | none | An existing biome to copy settings from |
| `decoration` | no | object | vanilla counts | Per-chunk counts for trees, grass, flowers, reeds, cacti, lakes, clay and the rest |
| `spawns` | no | list | vanilla list | See below |
| `keepDefaultSpawns` | no | boolean | `false` | Keep vanilla's list alongside yours |
| `spawnChance` | no | float | `0.1` | How likely a spawn attempt is |
| `spawnRates` | no | object of creature type to multiplier | none | Scales spawning per type |
| `placement` | no | object | — | Where it generates. See below |
| `requires` | no | list of mod ids or pack namespaces | none | The file is skipped unless all are present |

A spawn entry takes `entity` (required), `type` (`creature`, one of the [creature types](#value-lists)), `weight` (`10`), `min` (`1`) and `max` (`min`).

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
| `id` | yes | int | — | The dimension id. Must not clash with another mod |
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

Colors and the three render switches are all that is offered. Drawing something of your own up there — a painted dome, your own sun and moon — still needs Java.

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
| `dimension` | yes | int | — | Where it sends you |
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
  "unlock": { "consume": "mypack:ruby", "consumeCount": 4 },
  "unlockedMessage": "%dim% is now open",
  "blockedMessage": "You need %item% to enter %dim%",
  "safeReturn": true
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `dimension` | yes | int | — | The dimension it guards |
| `name` | no | string | the file name | Shown to the player |
| `scope` | no | `player`, `global` | `player` | One player at a time, or the whole world at once |
| `open` | no | boolean | `false` | Whether it starts open |
| `unlock` | no | object | — | What opens it. See below |
| `unlockedMessage` | no | string | `%dim% is now open` | Shown when it opens |
| `blockedMessage` | no | string | `You need %item% to enter %dim%` | Shown when it refuses |
| `safeReturn` | no | boolean | `false` | A blocked return still lands somewhere safe rather than refusing |

`unlock` takes `hold` (an item that must be held), `consume` with `consumeCount` (`1`), `craft` (an item that must have been crafted) and `advancement`. `%item%` and `%dim%` are filled in for you.

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

## Game rules

`gamerules/*.json`

```json
{
  "gameRules": {
    "doFireTick": "false",
    "keepInventory": "true",
    "randomTickSpeed": "3"
  }
}
```

Values are strings, as they are in the `/gamerule` command. These are applied to new worlds. A dimension file can carry the same block to apply rules only there.

## Worldgen entries

`worldgen/*.json` describes something that generates. Every entry is a **shape** placed by a **spread**, filtered by where it is allowed.

```json
{
  "block": "mypack:ruby_ore",
  "meta": 0,
  "size": 8,
  "attempts": 12,
  "minHeight": 8,
  "maxHeight": 48,
  "replace": ["minecraft:stone"],
  "dimensions": [0],
  "retrogen": false
}
```

| Key | Required | Value | Default | What it does |
| --- | --- | --- | --- | --- |
| `block` | yes | block name | — | What is placed |
| `meta` | no | int | `0` | Which variant of that block |
| `blocks` | no | list | none | A weighted list, used instead of one block. See below |
| `size` | no | int or range | `8` | How many blocks one attempt places, or how large a shape with a radius is |
| `attempts` | no | int or range | `1` | How many times per chunk it tries |
| `replace` | no | list | `["minecraft:stone"]` | What it may replace. See below |
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
| `block` | yes | block name | — | What is placed |
| `meta` | no | int | `0` | Which variant |
| `weight` | no | int | `1` | How often this one is chosen against the others |
| `properties` | no | object of property to value | none | Block state properties by name, for states with no metadata of their own |

`block` and `meta` are still required at the top level of the file even when `blocks` is used — the first entry is a good value to put there.

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
| `imprint` | One of your `.nbt` templates |
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

Changing `retrogenKey` in the config makes every chunk eligible again — which adds the new veins on top of the old ones, so density doubles. That is deliberate, and it is why the key is manual.

---

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

**Structures** for a world template. `villages`, `mineshafts`, `strongholds`, `temples`, `monuments`, `mansions`, `netherbridges`, `endcities`, `caves`, `ravines`.

**Creature types** for biome spawns and rates. `creature`, `monster`, `ambient`, `water_creature`.

## The control layer

Everything that stops or changes generation is grouped, and each group has one key in the config's `control` category with three values:

| Value | What it means |
| --- | --- |
| `default` | The pack decides. Config values are the fallback |
| `global` | The config wins. Pack sections are ignored |
| `off` | The group is disabled entirely and no pack can enable it |

The groups are `ores`, `biomes`, `generators`, `structures`, `spawning`, `bedrock`, `voidWorld`, `recipes` and `terrain`.

Settings resolve **biome → world template → config**. A world template's `settings` block uses the same key names as the config, so a pack sets them the same way you would.

## What each group does

**Ores.** `blockOres` stops every mod and Minecraft generating ore except the mods in `oreWhitelist`. `oreTypes` names ore types this applies to, and `oreTypesAreBlacklist` decides the direction — on, the listed types are blocked; off, only the listed types generate. Only generation that goes through Forge's ore generation event can be reached, which is Minecraft and most mods but not all.

**Biomes.** `blockBiomes` and `biomeWhitelist` work by mod, and `biomeNames` with `biomeNamesAreBlacklist` by name. Blocked biomes are replaced on the finished biome map, which is the only way to reach oceans, mushroom islands, mesa variants, jungle, hills and shores — those are chosen outside the lists a mod can edit. Block every biome and the overworld becomes a void world by itself.

**Generators.** `blockWorldGenerators` stops other mods generating through their own world generators, which is how mods add what Forge's events never see — slime islands, cave crystals and the like. `generatorWhitelist` keeps named mods, `blockedGenerators` names individual ones, and this mod's own pack generation is never blocked.

`generatorTypes` blocks by what a generator makes instead of by which mod owns it: `ores`, `structures`, `flora`, `lakes`, `terrain`, or `unknown` for the ones nothing matched. `generatorTypesAreBlacklist` decides the direction — on, the listed types are blocked; off, only the listed types generate. A type blocks whatever the whitelist says, the same way `oreTypes` does, so you can stop every mod adding ore while leaving its dungeons and trees alone.

The type comes from the generator's class name, matched against a built in list of words per type. That reads most mods correctly — `NetherOreGenerator` is ores, `SlimeIslandGenerator` is structures — but a generator named after nothing in particular, such as ProjectRed's `SimpleGenHandler` or Draconic Evolution's `DEWorldGenHandler`, comes out as `unknown`. `generatorTypeMap` fixes those by hand, one `pattern=type` per line, where the pattern is a mod id or part of a generator class name:

```
mrtjpcore=ores
deworldgenhandler=structures
```

Mapped entries are checked before the built in words, so they also correct a generator the words read the wrong way. Turn on `logBlockedGenerators` and each generator is logged with the type it was given the first time it is blocked, and `/rdplserver generators` shows the running totals by mod and type.

**Replacements.** `blockReplacements` swaps blocks out of chunks that already exist, one `block=block` per line, with an optional meta on either side:

```
bigreactors:oreyellorite=minecraft:stone
mekanism:oreblock:0=minecraft:stone
tconstruct:ore:0=minecraft:netherrack
```

Each chunk is done once, as it loads from disk, and marked in the chunk's own data so it is never done twice. A chunk being generated for the first time is cleaned the next time it loads rather than straight away, because neighboring chunks are still writing into it while it generates. A chunk on the edge of explored land is cleaned but not marked, so it is cleaned again once the land around it exists. `blockReplacementDimensions` and `blockReplacementDimensionsAreBlacklist` choose where, `blockReplacementMinHeight` and `blockReplacementMaxHeight` choose the band of the world to look at, and `blockReplacementKey` is a string you change to make every chunk go through it again. It runs whether or not `retrogen` is on, since a world that needs cleaning up is usually one you do not want new veins added to. It only swaps blocks: something a mod generated as a structure cannot be taken back out this way, because the terrain it replaced was never recorded.

**Villages.** Villages use the same `structure=value` lists as every other structure, under the name `villages`, so `structureSpacing`, `structureMinDistanceFromSpawn`, `structureBiomes` and `structureBiomesAreBlacklist` all reach them. Their spacing has a floor of 9, because vanilla subtracts 8 from it. `villagePieces` belongs to the same group, so one switch covers everything about where villages go and what they are built from, while the `villages` group covers only the plots a pack adds.

`villagePieces` names vanilla village pieces — `house1`, `house2`, `house3`, `house4garden`, `church`, `woodhut`, `hall`, `field1` and `field2` — and `villagePiecesAreBlacklist` decides the direction, so you can drop vanilla's wheat fields and leave the houses, or list the only pieces you want. Pack plots are unaffected by the list; they are added on top.

**Structures.** Vanilla structures switched off by name, per dimension. Placement is controlled with four lists written as `structure=value`, one per line: `structureSpacing` for how far apart they are seeded, `structureSeparation` for the closest two may be, `structureMinDistanceFromSpawn` for how far out they start, and `structureBiomes` with `structureBiomesAreBlacklist` for where they are allowed.

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

**Spawning.** Mob spawn rates and caps, per biome.

**Bedrock.** `flatBedrock` replaces the jagged layer with flat ones, per dimension and per biome, with a filler block you choose. `flatBedrockRetrogen` does it to chunks that already exist. It cannot be undone — the original pattern is not recorded anywhere.

**Void world.** `voidWorld` generates an empty world with a platform at the spawn point, and stops mobs, animals, structures and everything a mod would otherwise generate there. The platform's block, size and height are all options. `voidWorldDimensions` chooses which worlds are emptied, the overworld alone by default, and `voidWorldDimensionsAreBlacklist` turns that list into the ones to leave alone. The nether and the end are emptied the same way the overworld is, whether they are the ones this version builds or ones a mod has replaced them with. Only the overworld is given a platform, so a way into an emptied nether or end is something a pack provides itself. An emptied end has no dragon, no crystals and no bedrock fountain either, since the fight that builds them is left unstarted.

**The dragon.** `dragonFight` belongs to the `structures` group and decides whether the whole thing happens at all: the dragon, its bar, the crystals, the fountain it stands on, and the respawn a player would start with end crystals. An emptied end leaves it out unless a pack asks for it, and an ordinary end has it unless a pack says otherwise, so `dragonFight` is worth setting either way round.

**Terrain.** `generatorOptions` shapes the overworld itself — sea level, lava oceans and every terrain noise — in the same format the customized world type writes. It is applied to a world as it is created and never afterwards, so a world that already exists is left exactly as it was. A world that already carries options of its own keeps them, and the log names the string it used.

A world type that carries its own settings and never looks at the world's, as Quark's realistic one does, is given the pack's settings merged into its own, so the shape it was built for stays unless a pack asks for something else.

`terrainWorldTypes` names the world types the settings are given to at all — `default`, `customized`, `biomesop`, `realistic` and so on — and `terrainWorldTypesAreBlacklist` turns that into the list to leave alone. Empty, the default, means every world type. A pack that shapes the ordinary world but wants a mod's world type left exactly as that mod made it names it here and is done: nothing is merged, nothing is handed over, and the mod's own customize screen stays open. The names are matched against whatever world type a world was made with, so naming one that nothing here provides simply never matches and costs nothing.

Everything below about Biomes O' Plenty only happens when that mod is installed, since the work is done by compatibility that is only loaded when it is present. Without it there is no `biomesop` world type to pick, and a pack that names one is left with whatever world type the world was actually made with.

On a Biomes O' Plenty world the same settings are turned into the words that mod reads, so a pack does not need a second copy of them. `biomeSize` becomes one of its five sizes, the noise and scale settings pass through as they are, and anything it never reads is left out with a line in the log saying so. That mod reads far less than the customized world type does, and never reads sea level, caves, lakes or the structure switches from its settings at all — so those are handed to it directly instead, and a pack sets them the same way it would for any other world.

Two things it decides for itself. Rivers come out of its own layers and have no setting, so `riverSize` means nothing there. And where oceans, mountains and regions actually sit is its layers too, reachable only through `landScheme`, `tempScheme`, `rainScheme` and `biomeSize` — so a pack shapes that world in that mod's terms rather than the customized world type's. A world of a single biome is still a pack's to make: block every biome and name the one you want as the template's `default`, which works the same on its world type as on any other.

Everything else a pack does — blocking biomes and ores, replacing blocks, flat bedrock, structure placement, its own worldgen — never went through that string at all, and works the same on any world type.

**Recipes.** `blockRecipes` and `blockFurnaceRecipes` remove everything except the mods in their whitelists. Nothing is exempt by default, so list your own pack's namespace to keep its recipes. CraftTweaker and GroovyScript additions always survive, whatever the whitelist says.

## CoFH World

Mods that require CoFH World load without it — the requirement is removed automatically, except for mods that genuinely call its API and would crash.

Their own generation then does not happen, because CoFH World is what reads their `assets/<modid>/world/*.json`. A pack is expected to cover it.

Failing that, `readCofhWorldFiles` reads those files straight out of the mod jars and generates them through this mod. It is off by default, and it stands down when the real CoFH World is installed, which then generates as normal. Every CoFH generator and distribution that produces anything is converted, mapped onto the shapes and spreads above. The shapes are this mod's own geometry, so a lake or a spire will not look identical, and a few fine options do not carry — weighted structure lists take the first entry, and rotation tables, ignored-block lists and the curve shaping on stalagmites are dropped.

Translating the files into a pack is the supported route, and the only way to change what they generate.

---

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
| `gates` | Conditions on portals and dimensions |
| `gamerules` | Game rules for new worlds |
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
| `models`, `blockstates`, `textures`, `lang` | The usual asset folders |

## Commands

`/rdpl` runs on your own machine and needs no permissions, because it only reads files you already have. It works on any server, whether or not the server has the mod.

| Command | What it does |
| --- | --- |
| `/rdpl list` | Every loaded pack, its priority, and what it contains. Click a pack to look up a file in it |
| `/rdpl which <namespace:path>` | Which pack provides a given file, and which packs it shadows |
| `/rdpl reload` | Rescan the folder and reload everything |
| `/rdpl reload <group>` | Reload just one kind — `textures`, `models`, `languages`, `sounds` or `shaders` |
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

**Day-to-day editing:** `/rdpl reload textures` is much faster than F3+T in a large modpack. F3+T still works and reloads everything. Use plain `/rdpl reload` when you *add* or *delete* a file, since that changes what the folder contains.

## Good to know

- CraftTweaker and GroovyScript run after RDPL, so their changes still win.
- Recipes only load at startup, so recipe changes need a restart rather than a reload.
- Functions saved in a world's own data folder still beat a function from a pack, and so do that world's own advancements.
- A structure that has already generated stays loaded until you leave the world.
- Filename case matters. If your file's capitalization doesn't match what the game asked for, RDPL still loads it but warns you — because on Linux it wouldn't be found at all.
- Put a `pack.png` in `rdploader` to give the pack an icon.
- The folder can be moved or renamed with the `rootDirectory` option in `config/mct_resourcedatapackloader_mixin.cfg`. An absolute path works too, and it needs a restart.
- Blockstates naming a bare vanilla model inherit vanilla's textures too. Parent models such as `cube_all` and `cross` take their textures from the blockstate and are fine.
- `forge_marker: 1` does not support multipart, so vine blockstates have to be plain vanilla multipart with the textures baked into the model.

## When something doesn't work

**Check `logs/rdpl.log` first.** Everything RDPL does goes there rather than the main log. Advancements, loot tables, recipes, functions, structures and every piece of content are logged with the pack they came from, and anything malformed is logged with the reason.

**Textures and other assets are different.** They're requested far too often to log individually, so instead `/rdpl unused` lists the files in your packs that nothing has asked for. Run it once the game has finished loading. A file with the right path is always requested, so anything listed is usually a typo — but bear in mind some files only load when they're needed, such as languages other than the one you play in.

**A pack folder or zip without an `assets` directory inside it is skipped,** and the log says so.

**`/rdpl which minecraft:textures/blocks/stone.png`** tells you exactly which pack is serving a file and what it's shadowing.

## Bonus: vanilla tweaks

Two small changes to how vanilla behaves, both off by default, in the `tweaks` config category. Both switch themselves off when Universal Tweaks is installed, since it does the same thing.

| Option | What it does |
| --- | --- |
| `promptLeafDecay` | Leaves that lose their tree decay within a second instead of waiting on random ticks |
| `lenientPaths` | Grass paths can be made under a block and stay there when one is placed above |

`lenientPaths` also lifts the same restriction from pack blocks using `behavesAs`, which Universal Tweaks does not touch, so that half stays on either way.

## Bonus: JEI plugin conflict fix

Some mods query JEI's recipe registry before the mods that provide it have finished initializing, which floods logs with hundreds of harmless-but-noisy errors and can silently break a mod's JEI integration. RDPL detects this automatically and corrects the notification order. It works with Just Enough Items and with Had Enough Items. If neither is installed, nothing happens.

## Bonus: fewer startup errors

- Recipes that reference an item no mod actually registered — usually content disabled in a mod's own config — are skipped instead of throwing a parse error. The count is logged once. (`skipMissingItems`)
- Advancements that reward a recipe a script has since removed still load, instead of failing. They just never unlock that recipe, and the whole set is summarized in one line. (`tolerateMissingInAdvancements`)