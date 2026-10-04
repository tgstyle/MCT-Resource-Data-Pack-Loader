# MCT Resource Data Pack Loader

**The API that should have been.**

Minecraft is data driven right up to the point where it gets interesting. A data
pack can change a recipe, but it cannot add a block, an item or a fluid, and it
lives in one world's save. Past that line you write a mod, once per version and
once per loader.

This mod moves the line. New content is a JSON file, one folder applies to every
world, and a pack written once loads on Minecraft 1.12.2, 1.20.1, 1.21.1 and 26.x.

- [HOWTO.md](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/1.12.2-1.0-Release/HOWTO.md), the full manual for each version (English, Deutsch, Русский, 简体中文, Español, Português (Brasil), Français)
- [Discord](https://discord.gg/ujY2mV9)
- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/mct-resource-data-pack-loader)
- [Modrinth](https://modrinth.com/mod/mct-resource-data-pack-loader)

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
- **Existing content can be changed in place.** Any block, item or potion, vanilla
  or modded, takes new properties from a pack, and a reload puts them back.
- **The pack decides what generates.** Other mods' ores, biomes, structures and
  recipes can be blocked or reshaped without touching their jars.

**1.12.2:** there are no data packs at all. Advancements, loot tables and functions
live inside each world's save, and recipes only load from mod jars. This mod is the
data pack system that version never had.

Anything below that applies to only some versions starts with those versions.

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

# What a pack can do

- **Replace files**: any texture, model, language file or sound, and any recipe,
  loot table, advancement, function or structure template, on dedicated servers
  too
- **Change what exists**: hardness, light, stack size, durability, food values and
  more on any block, item or potion, live with `/rdpl reload`
- **Add content**: blocks in every common shape, items, tools, armor, fluids,
  potions, villager trades, biomes, entity variants, portals and whole dimensions
- **Generate it**: ores, veins, geodes, trees and your own structure templates,
  filtered by height, biome, dimension and distance from spawn
- **Build cities**: streets, bridges, railways, subways, sewers and buildings
- **Control what generates**: block or reshape ores, biomes, structures, spawn
  rates and recipes from vanilla and other mods
- **Shape the world**: sea level, terrain, void worlds, gravity, stacked
  dimensions, and a world template that ships it all as one file
- **Run the game**: pregenerate a world as it is created, show an intro on entry,
  field teams and score rounds
- **1.12.2:** rubic worlds that reach far above and below the 256 block limit, and
  mods that require CoFH World load without it

Anything needing a block entity of its own, a screen, an inventory or per-tick
logic still needs a real mod. A machine is out of reach; an ore, a fence, a food, a
fluid or a crate is not.

[HOWTO.md](https://github.com/tgstyle/MCT-Resource-Data-Pack-Loader/blob/1.12.2-1.0-Release/HOWTO.md) covers each of these in full.

# Good to know

`/rdpl reload` applies pack changes without a restart; on a dedicated server,
`/rdplserver reload`. `/rdpl which` names the pack serving any file, and
`/rdpl unused` lists files nothing has asked for, which is usually a typo in a path.

A pack can stay on the server alone, with every player on a plain vanilla client,
as long as it registers nothing. The `vanillaClients` config switch enforces
exactly that.

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
