# Links
- [Official Discord](https://discord.gg/ujY2mV9)<br/>

- [Resource Data Pack Loader on CurseForge](https://www.curseforge.com/minecraft/mc-mods/resource-data-pack-loader)
- [Resource Data Pack Loader on Modrinth](https://modrinth.com/mod/resource-data-pack-loader)

# MCT Resource Data Pack Loader
Loads resource and data overrides from one global folder, applied to every world.<br/>

Minecraft 1.12.2 has no data pack system, and Resource Loader only covers client assets. Advancements,
loot tables and recipes cannot be overridden without repacking a mod jar or copying files into every
save. This mod covers both sides from a single folder, on the client and the server.<br/>

# Usage
Put loose files or a zip in the `rdploader` folder, alongside `mods` and `config`.<br/>

```
rdploader/assets/<namespace>/...
rdploader/<packname>.zip
```

Paths match the layout inside a mod jar, so files can be copied straight across.<br/>

# Reporting issues
When you are reporting bugs, please attach the log, mod and forge version.<br/>

# Help translate the mod
Feel free to translate the mod and put it in a pull request.<br/>

# About Modpack and License
Resource Data Pack Loader is licensed under the GNU GENERAL PUBLIC LICENSE Version 3. You may use it
in modpacks, reviews or any other form as long as you abide by the terms.<br/>
