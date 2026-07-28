# Links
- [Official Discord](https://discord.gg/ujY2mV9)<br/>

- [Resource Data Pack Loader on CurseForge](https://www.curseforge.com/minecraft/mc-mods/mct-resource-data-pack-loader)
- [Resource Data Pack Loader on Modrinth](https://modrinth.com/mod/mct-resource-data-pack-loader)

# MCT Resource Data Pack Loader
Loads resource and data overrides from one global folder, applied to every world.<br/>

Minecraft 1.12.2 has no data pack system, and Resource Loader only covers client assets. Advancements,
loot tables, recipes and functions cannot be overridden without repacking a mod jar or copying files
into every save. This mod covers both sides from a single folder, on the client and the server.<br/>

# Usage
Put loose files or a zip in the `rdploader` folder, alongside `mods` and `config`.<br/>

```
rdploader/assets/<namespace>/...
rdploader/<packname>.zip
```

Paths match the layout inside a mod jar, so files can be copied straight across.<br/>

See [HOWTO.md](HOWTO.md) for pack priority, resource pack precedence, registry renames and the
commands.<br/>

# What it covers
- Textures, models, blockstates, language files, sounds and anything else in a mod's `assets` folder
- Advancements and loot tables, on dedicated servers as well as singleplayer
- Recipes, replaced or added
- Structure templates (`.nbt`)
- Functions (`.mcfunction`), which vanilla otherwise only reads per world
- Registry renames, so worlds saved before a mod renamed a block or item keep it

A pack whose name starts with `RDPLO` always overrides the player's selected resource packs, one
starting with `RDPLN` never does, and anything else follows the `overrideResourcePacks` config.<br/>

`/rdpl unused` lists files in your packs that nothing has asked for, which is usually a typo in a
path.<br/>

# Requirements
Requires [MixinBooter](https://www.curseforge.com/minecraft/mc-mods/mixinbooter).<br/>

# Reporting issues
When you are reporting bugs, please attach the log, mod and forge version.<br/>

# Help translate the mod
Feel free to translate the mod and put it in a pull request.<br/>

# About Modpack and License
Resource Data Pack Loader is licensed under the GNU GENERAL PUBLIC LICENSE Version 3. You may use it
in modpacks, reviews or any other form as long as you abide by the terms.<br/>