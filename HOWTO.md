# Resource Data Pack Loader

**One folder that overrides anything Minecraft or a mod provides — in every world, on clients and servers, with nothing for players to switch on.**

## What it is

Resource Data Pack Loader (RDPL) adds a single folder to your instance: `rdloader`. Drop a file in it, and that file replaces the one the game or a mod would have used.

There is no toggle, no per-world setup, and nothing for players to enable in a menu. If the file is in the folder, it is what the game loads.

## The one rule

Open the mod's jar, find the file you want to change, and copy its path from `assets` onwards.

The iron ore texture inside the Minecraft jar lives at:

```
assets/minecraft/textures/blocks/iron_ore.png
```

So your version goes at:

```
rdloader/assets/minecraft/textures/blocks/iron_ore.png
```

That's the whole system. The path after `assets` is always identical to the path inside the jar, so nothing ever needs renaming or moving.

## What you can override

- **Anything in a mod's assets folder** — textures, models, blockstates, language files, sounds, fonts, splash texts, guide books, manuals
- **Advancements and loot tables** — server side, so they work on dedicated servers too
- **Recipes** — replace a mod's recipe or add your own
- **Structure templates** — the `.nbt` files mods use for generated buildings

## Why modpack developers want it

**No jar editing.** Editing files inside a mod jar breaks its signature, gets wiped by every update, and is legally murky to redistribute. RDPL leaves every jar untouched.

**No asking players to do anything.** A normal resource pack has to be enabled, can be turned off, and doesn't cover advancements, loot tables, or recipes. Data packs have to be installed per world. RDPL covers all of it, everywhere, automatically.

**Fix broken mods yourself.** Mods ship with typos in model files, missing textures, and paths that reference the wrong namespace. Those bugs may never be fixed upstream. With RDPL you drop in a corrected file and move on — no waiting on a mod author, no forking a mod.

**Reskin the pack.** Give an entire modpack a consistent look by overriding mod textures directly, instead of shipping a resource pack players might disable.

**Survives updates.** Because overrides are matched by path, they keep working across mod updates as long as the file paths stay the same.

## Organizing packs

Loose files work fine, but you can group them instead — as a folder or a zip:

```
rdloader/MyTextures/assets/...
rdloader/MyTextures.zip
```

Folders are easier while you're working. Zips are easier to hand to someone else. They behave identically.

**Control which pack wins.** If two packs contain the same file, prefix the name with `RDPL` and a number. Higher numbers load later and win:

```
rdloader/RDPL0 BaseTextures.zip
rdloader/RDPL1 SeasonalTextures.zip
rdloader/RDPL9 ModFixes.zip
```

Upper or lower case both work, and the prefix is hidden from the pack's display name.

**Turn a pack off without deleting it** by adding `.disabled` to the end of its name.

## Commands

| Command | What it does |
| --- | --- |
| `/rdpl list` | Every loaded pack, its priority, and what it contains |
| `/rdpl which <namespace:path>` | Which pack serves a given file, and which packs it shadows |
| `/rdpl reload` | Rescan the folder and reload advancements, loot tables and functions |

Press F3+T to reload textures, models and language files after editing a file. Use `/rdpl reload` when you add or remove one.

## Good to know

- A file here replaces the original completely. To change one ingredient or drop one loot entry, CraftTweaker is the better tool.
- CraftTweaker and GroovyScript run after RDPL, so their changes still win.
- Recipes only load at startup, so recipe changes need a restart rather than a reload.
- Every file served is logged with the pack it came from, so you can always see which pack won.
- Filename case matters. If your file's capitalization doesn't match what the game asked for, RDPL still loads it but warns you — because on Linux it wouldn't be found at all.

## Bonus: JEI plugin conflict fix

Some mods query JEI's recipe registry before the mods that provide it have finished initializing, which floods logs with hundreds of harmless-but-noisy errors and can silently break a mod's JEI integration. RDPL detects this automatically and corrects the notification order. If JEI isn't installed, nothing happens.
