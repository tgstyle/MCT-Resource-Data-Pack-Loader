# Resource Data Pack Loader

**One folder that overrides anything Minecraft or a mod provides — in every world, on clients and servers, with nothing for players to switch on.**

## What it is

Resource Data Pack Loader (RDPL) adds a single folder to your instance: `rdploader`. Drop a file in it, and that file replaces the one the game or a mod would have used.

There is no toggle, no per-world setup, and nothing for players to enable in a menu. If the file is in the folder, it is what the game loads.

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

## What you can override

- **Anything in a mod's assets folder** — textures, models, blockstates, language files, sounds, fonts, splash texts, guide books, manuals
- **Advancements and loot tables** — server side, so they work on dedicated servers too
- **Recipes** — replace a mod's recipe or add your own
- **Structure templates** — the `.nbt` files mods use for generated buildings
- **Functions** — the `.mcfunction` files under `assets/<modid>/functions`
- **Registry renames** — keep old worlds working when a mod renames a block or item

## Why modpack developers want it

**No jar editing.** Editing files inside a mod jar breaks its signature, gets wiped by every update, and is legally murky to redistribute. RDPL leaves every jar untouched.

**No asking players to do anything.** A normal resource pack has to be enabled, can be turned off, and doesn't cover advancements, loot tables, or recipes. Data packs have to be installed per world. RDPL covers all of it, everywhere, automatically.

**Fix broken mods yourself.** Mods ship with typos in model files, missing textures, and paths that reference the wrong namespace. Those bugs may never be fixed upstream. With RDPL you drop in a corrected file and move on — no waiting on a mod author, no forking a mod.

**Reskin the pack.** Give an entire modpack a consistent look by overriding mod textures directly, instead of shipping a resource pack players might disable.

**Survives updates.** Because overrides are matched by path, they keep working across mod updates as long as the file paths stay the same.

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

Upper or lower case both work, a space, dash or underscore after the number is optional, and the prefix is hidden from the pack's display name.

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

## Registry renames

When a mod renames one of its blocks or items, worlds saved before the rename lose them. Drop a file in `assets/<modid>/registry_remap/` to map the old name to the new one:

```json
{
  "registry": "minecraft:items",
  "mapping": { "oldmod:old_name": "newmod:new_name" }
}
```

The registry is the one the entry belongs to — usually `minecraft:items` or `minecraft:blocks`. Renames chain, so mapping A to B and later B to C sends A straight to C.

## Commands

`/rdpl` runs on your own machine and needs no permissions, because it only reads files you already have. It works on any server, whether or not the server has the mod.

| Command | What it does |
| --- | --- |
| `/rdpl list` | Every loaded pack, its priority, and what it contains. Click a pack to look up a file in it |
| `/rdpl which <namespace:path>` | Which pack provides a given file, and which packs it shadows. This is what your packs offer, not proof the game asked for it |
| `/rdpl reload` | Rescan the folder and reload everything |
| `/rdpl reload <group>` | Reload just one kind of resource — `textures`, `models`, `languages`, `sounds` or `shaders` |
| `/rdpl unused` | Files in your packs that nothing has asked for yet, usually a typo in a path |

On a dedicated server, an operator uses `/rdplserver reload` to rescan the server's own copy of the folder. `/rdplserver list`, `/rdplserver which` and `/rdplserver unused` are also available.

**Day-to-day editing:** `/rdpl reload textures` is much faster than F3+T in a large modpack. F3+T still works and reloads everything. Use plain `/rdpl reload` when you *add* or *delete* a file, since that changes what the folder contains.

## Good to know

- A file here replaces the original completely. To change one ingredient or drop one loot entry, CraftTweaker is the better tool.
- CraftTweaker and GroovyScript run after RDPL, so their changes still win.
- Recipes only load at startup, so recipe changes need a restart rather than a reload.
- Functions saved in a world's own data folder still beat a function from a pack, and so do that world's own advancements.
- A structure that has already generated stays loaded until you leave the world.
- Filename case matters. If your file's capitalization doesn't match what the game asked for, RDPL still loads it but warns you — because on Linux it wouldn't be found at all.
- Put a `pack.png` in `rdploader` to give the pack an icon.
- The folder can be moved or renamed with the `rootDirectory` option in `config/mct_resourcedatapackloader_mixin.cfg`. An absolute path works too, and it needs a restart.

## When something doesn't work

**Check the log first.** Advancements, loot tables, recipes, functions and structures are logged with the pack they came from, and anything malformed is logged as a warning saying why.

**Textures and other assets are different.** They're requested far too often to log individually, so instead `/rdpl unused` lists the files in your packs that nothing has asked for. Run it once the game has finished loading. A file with the right path is always requested, so anything listed is usually a typo — but bear in mind some files only load when they're needed, such as languages other than the one you play in.

**A pack folder or zip without an `assets` directory inside it is skipped,** and the log says so.

**`/rdpl which minecraft:textures/blocks/stone.png`** tells you exactly which pack is serving a file and what it's shadowing.

## Bonus: JEI plugin conflict fix

Some mods query JEI's recipe registry before the mods that provide it have finished initializing, which floods logs with hundreds of harmless-but-noisy errors and can silently break a mod's JEI integration. RDPL detects this automatically and corrects the notification order. It works with Just Enough Items and with Had Enough Items. If neither is installed, nothing happens.

## Bonus: fewer startup errors

- Recipes that reference an item no mod actually registered — usually content disabled in a mod's own config — are skipped instead of throwing a parse error. The count is logged once. (`skipRecipesWithMissingItems`)
- Advancements that reward a recipe a script has since removed still load, instead of failing. They just never unlock that recipe, and the whole set is summarized in one line. (`tolerateMissingRecipes`)
- Tinkers' Construct and Construct's Armory can log a model error for every tool, part and armour piece, from a probe their model loaders answer but can't serve. Turn on `fixTinkersModelErrors` to silence just that failed probe. It does not change which model is used.