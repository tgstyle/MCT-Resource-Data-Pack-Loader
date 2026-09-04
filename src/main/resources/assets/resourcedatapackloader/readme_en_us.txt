Resource Data Pack Loader
=========================

Anything you put in this folder replaces what a mod or Minecraft itself provides.
It applies to every world, in singleplayer and on servers, and there is nothing
to switch on.


WHAT IS HERE SO FAR
-------------------

This version of the mod is being brought over from Minecraft 1.12.2 one piece
at a time. What is here now is the pack folder itself: overriding what
Minecraft and mods provide, in every world, on clients and servers. Defining
new content from JSON, deciding what generates, and the rest follow as they are
ported, and HOWTO.md, shipped alongside the mod, describes each one as it lands.


HOW TO ADD A FILE
-----------------

Open the mod's jar, find the file you want to change, and copy its path from
'assets' or 'data' onwards.

To replace the iron ore texture, the file inside the Minecraft jar is:

    assets/minecraft/textures/block/iron_ore.png

so your version goes here:

    rdploader/assets/minecraft/textures/block/iron_ore.png

A loot table lives under data instead, and goes the same way:

    data/minecraft/loot_tables/blocks/iron_ore.json
    rdploader/data/minecraft/loot_tables/blocks/iron_ore.json

That is the whole rule. The path after 'assets' or 'data' is always the same as
the path inside the jar, so nothing ever needs renaming or moving.


KEEPING THINGS TIDY
-------------------

You can group files into a named pack instead, as a folder or a zip:

    rdploader/MyTextures/assets/minecraft/textures/block/iron_ore.png
    rdploader/MyTextures.zip        (with 'assets' or 'data' at the top level of the zip)

When zipping, select the contents and zip those, not the folder holding them.
A zip whose top level is a single folder wrapping 'assets' or 'data' is
skipped, and the log says so.

Folders are easier to edit while you work, zips are easier to hand to someone
else. They behave the same.

If the same file exists in two places, a named pack wins over loose files, and
/rdpl which tells you which one won.


PACK PRIORITY
-------------

If two named packs contain the same file, control which one wins by prepending
RDPL and a number to the folder or zip name. RDPL0 loads first, higher numbers
load later, and the pack loaded last wins:

    rdploader/RDPL0 BaseTextures.zip
    rdploader/RDPL1 SeasonalTextures

Upper or lower case both work, and a space, dash or underscore after the number
is optional. The prefix is stripped from the pack's name in the log and in
/rdpl list, so RDPL1 SeasonalTextures shows up as SeasonalTextures.


MOD API
-------

A mod can carry RDPL content inside its own jar, in a folder named rdploader
laid out exactly like a pack:

    thatmod.jar
      META-INF/neoforge.mods.toml
      rdploader/assets/thatmod/textures/block/ruby_ore.png

Those are defaults, not overrides. A mod pack loads below every pack in this
folder, so anything you put here wins over it, and a mod may only supply files
under a namespace it declares in its own neoforge.mods.toml. Anything else is ignored
with a warning, so no mod can quietly redefine another one's content or yours.

Every mod that ships one is listed in config/mods.json the first time it is
seen:

    {
      "thatmod": {
        "enabled": true,
        "priority": -1
      }
    }

Set enabled to false to turn that mod's content off. Leave priority at -1 to
keep it underneath everything, or give it a number and it takes its place in
the ordering above, alongside the numbered packs. Packs are listed lowest
first in the log and a mod's own is marked there, so nothing loads that you
cannot see.


RESOURCE PACKS
--------------

By default the files here sit above the resource packs the player picks in the
options screen, so a resource pack cannot override them. That is right for
things like a modpack logo and wrong for textures you would like people to be
able to reskin.

Add O or N after the RDPL prefix to decide per pack:

    rdploader/RDPLO Branding          always wins, resource packs cannot touch it
    rdploader/RDPLN BaseTextures      a resource pack can override it
    rdploader/RDPL1O Seasonal         priority and always wins, both together

Packs with no letter follow the overrideResourcePacks option in the config, and
/rdpl list marks the ones that override.

The same rule covers data packs. A pack marked N sits below the data packs a
world carries in its own datapacks folder, and one marked O sits above them.

Packs without a prefix load before all numbered packs, in alphabetical order,
so a numbered pack always wins over an unnumbered one.

To turn a pack off without deleting it, add .disabled to the end of its name:

    rdploader/RDPL1 SeasonalTextures.zip.disabled

The pack is skipped and the log says so. Remove the suffix to turn it back on.


WHAT YOU CAN CHANGE
-------------------

Textures, models, blockstates, language files, sounds, fonts, splash texts, and
anything else a mod keeps in its assets folder, such as guide books or manuals.

Advancements, loot tables, recipes, tags, functions, structure templates and
anything else a mod keeps in its data folder. These are server side, so they
work on a dedicated server as well, and a change to them takes effect with
/reload.


SEEING YOUR CHANGES
-------------------

Press F3+T to reload textures, models, language files and everything else
under assets. On a server, or for anything under data, type /reload.

If you add a new file or delete one, use /rdpl reload instead. Editing a file
that was already there only needs F3+T or /reload.

/rdpl list shows every pack loaded and what is in it. Hover over a pack to see
it.

/rdpl which minecraft:textures/block/stone.png shows which pack serves a file
and which packs are shadowed underneath it.

These work without being an operator, because they only read files on your own
computer. On a dedicated server, /rdplserver reload rescans the server's copy,
and /rdplserver list, which and unused answer for it.


IF SOMETHING DOES NOT WORK
--------------------------

Check the log first. logs/rdpl.log lists every pack that was loaded and every
one that was skipped, with the reason, and anything wrong is logged as a
warning saying why.

/rdpl unused lists any file in your packs that nothing has asked for yet, which
usually means a typo in the path. Run it after the game has finished loading,
and bear in mind some files only load when they are needed, such as languages
other than the one you play in.

Capital letters matter. If your file is Stone.png and the game asked for
stone.png, it still loads, but a warning tells you to rename it. Do rename it,
because anywhere other than this mod the file will not be found at all.
Language files trip people up most often: they are en_us.json, not en_US.json.

Check your files sit inside an 'assets' or 'data' folder. A pack folder or zip
without either is skipped, and the log says so.


The rdploader folder itself can be moved or renamed with the rootDirectory option
in config/resourcedatapackloader-common.toml. An absolute path works too, and
a restart is required.

Put a pack.png next to this file to give the pack an icon.

This file is written by the mod and brought up to date whenever it changes,
so anything you type into it is replaced the next time the game starts.
