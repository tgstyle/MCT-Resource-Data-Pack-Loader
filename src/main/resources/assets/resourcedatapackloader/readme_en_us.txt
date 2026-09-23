Resource Data Pack Loader
=========================

Anything you put in this folder replaces what a mod or Minecraft itself provides.
It applies to every world, in singleplayer and on servers, and there is nothing
to switch on.


MORE THAN OVERRIDES
-------------------

Packs here can also define new blocks, items, biomes and whole dimensions from
JSON files, decide what generates and where, lock dimensions behind a key or a
mob that must be slain, and make a world's land ahead of time so nobody ever
waits on a chunk. HOWTO.md, shipped alongside the mod, covers all of it.


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

You can group files into a named pack instead, as a zip:

    rdploader/MyTextures.zip        (with 'assets' or 'data' at the top level of the zip)

When zipping, select the contents and zip those, not the folder holding them.
A zip whose top level is a single folder wrapping 'assets' or 'data' is
skipped, and the log says so.

A folder in rdploader is not a pack and is skipped, and the log says so. Keep
loose files under assets or data, and zip a pack up before you put it here.

If the same file exists in two places, a named pack wins over loose files, and
/rdpl which tells you which one won.


PACK PRIORITY
-------------

If two named packs contain the same file, control which one wins by prepending
RDPL and a number to the zip name. RDPL0 loads first, higher numbers
load later, and the pack loaded last wins:

    rdploader/RDPL0 BaseTextures.zip
    rdploader/RDPL1 SeasonalTextures.zip

Upper or lower case both work, and a space, dash or underscore after the number
is optional. The prefix is stripped from the pack's name in the log and in
/rdpl list, so RDPL1 SeasonalTextures shows up as SeasonalTextures.


MOD API
-------

A mod can carry RDPL content inside its own jar, in a folder named rdploader
laid out exactly like a pack:

    thatmod.jar
      META-INF/mods.toml
      rdploader/assets/thatmod/textures/block/ruby_ore.png

Those are defaults, not overrides. A mod pack loads below every pack in this
folder, so anything you put here wins over it, and a mod may only supply files
under a namespace it declares in its own mods.toml. Anything else is ignored
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


ADDING NEW CONTENT
------------------

A pack can also add blocks, items and fluids of its own, described as JSON. You
do not need to write or build a mod for this.

Definitions sit under data, one folder for each kind of thing. Each key inside
"variants" is a name, so a file at

    rdploader/data/mypack/blocks/ores.json

holding a variant called ruby_ore registers mypack:ruby_ore. The file's own
name only groups things. If a real mod already registers that name, the mod
wins and your variant is skipped.

The simplest block is a few lines:

    {
      "type": "ore",
      "material": "rock",
      "harvestTool": "pickaxe",
      "variants": {
        "ruby_ore": { "hardness": 3.0, "harvestLevel": 1 }
      }
    }

You still supply the model, blockstate, texture and language entry under
assets, the same way as any other file in this folder.

Each of these is a folder under data/<yourpack>:

    blocks           items            fluids           materials
    tabs             sounds           biomes           worldgen
    caveregions      dimensions       worldtemplates   worldintro
    gates            gamerules        teams            scoring
    raids            entities         hardness         anvils
    exposures        overrides        villages         pathintersects
    structuremaps    citymaps         portalframes     blastplaster
    structures       recipes          recipe_removals  furnace
    fuels            brewing          potions          potion_types
    villagers        trades           loot_tables      loot_injections
    block_drops      player_loot      advancements     functions
    tags             registry_remap

Blocks come in these shapes, set by the "type" field:

    basic   ore     falling   slab    stairs   fence    door
    pane    wall    ladder    torch   crop     flower   cane
    log     leaves  sapling   vine    portal   trapdoor fence_gate
    banner  bell    container

and items in these:

    basic   food    drink     potion  tool     armor    seed
    potion_bottle   container

A potion type is named by the lang key item.minecraft.potion.effect.<baseName>,
with splash_potion, lingering_potion or tipped_arrow in place of potion for the
other forms. A potion_bottle item is your own container for them: it takes a
creativeTab like any other item and holds the potion types you name in
potionTypes.

A villagers/<name>.json file defines a profession. A trades/*.json file adds
trades to any profession, whether yours or one of Minecraft's, naming the
profession and the level the trade appears at.

An entities/<name>.json file makes a new entity out of one that is already here.
It names the entity to build on, and what is different about it: its name, its
looks, how much health and damage it has, how it moves, how it fights and what
it drops. It is an entity of its own, with its own spawn egg and loot table,
and the one it was built from is left alone.

A villages/<name>.json file adds a plot a city or village can build from one of
your .nbt templates, and a raids/<name>.json file sends waves at a village when
a player brings an omen into it.

A worldintro/<name>.json file plays a run of pages when somebody enters the
world, before they take control. The words are plain .txt files under
assets/<yourpack>/texts. It can play once per player or on every join.

A teams/<name>.json file fields a side on the game's scoreboard and says what
joins it, and a scoring/<name>.json file is an objective that scores points to
those sides and decides how a match ends.


WHOLE WORLDS
------------

A pack is not limited to single things. dimensions/<name>.json registers a
dimension with its own terrain, biomes and sky. gates/<name>.json puts a
condition on reaching one, such as holding or spending an item. A block of
type portal sends whoever walks in to another dimension, and portalframes lets
a player build and light a frame of their own.

worldtemplates/<name>.json gathers a world's settings into one file, so a pack
can ship a whole world shape at once instead of asking for a dozen config
edits. It can shape the overworld itself too, such as its sea level and
whether its oceans are lava.

worldgen is more than ore. An entry places a shape, from a small blob of your
block to one of your own .nbt templates, and decides how often, how high and in
which biomes it appears.

A biomes/<name>.json file defines a biome: its climate and colors, the blocks
it is made of, what decorates it, what spawns in it, and where it generates.


WHERE THIS STOPS
----------------

This describes what a thing is, not what it does over time. Anything needing a
block entity, a screen or code running every tick still needs a real mod, with
one exception: a block of type container holds an inventory with a screen of
its own. A machine is out of reach; an ore, a fence, a food or a fluid is not.


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

Check your files sit inside an 'assets' or 'data' folder. A zip without either
is skipped, and the log says so.


ADVANCEMENTS AND RECIPES
------------------------

A recipe a script adds or replaces is known by the name the script gives it.
To have an advancement unlock it, drop an advancement file in here that names
that recipe, and the advancement works end to end again.

Give such a recipe a fixed name in the script. A name made up for you can
change the moment you edit the recipe, so it is not safe to point an
advancement at.


The rdploader folder itself can be moved or renamed with the rootDirectory option
in config/resourcedatapackloader-common.toml. An absolute path works too, and
a restart is required.

Put a pack.png next to this file to give the pack an icon.

This file is written by the mod and brought up to date whenever it changes,
so anything you type into it is replaced the next time the game starts.
