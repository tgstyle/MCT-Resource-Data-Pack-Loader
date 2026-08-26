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
'assets' onwards.

To replace the iron ore texture, the file inside the Minecraft jar is:

    assets/minecraft/textures/blocks/iron_ore.png

so your version goes here:

    rdploader/assets/minecraft/textures/blocks/iron_ore.png

That is the whole rule. The path after 'assets' is always the same as the path
inside the jar, so nothing ever needs renaming or moving.


KEEPING THINGS TIDY
-------------------

You can group files into a named pack instead, as a folder or a zip:

    rdploader/MyTextures/assets/minecraft/textures/blocks/iron_ore.png
    rdploader/MyTextures.zip        (with 'assets' at the top level of the zip)

When zipping, select the contents and zip those, not the folder holding them.
A zip whose top level is a single folder wrapping 'assets' is skipped, and the
log says so.

Folders are easier to edit while you work, zips are easier to hand to someone
else. They behave the same.

If the same file exists in two places, a named pack wins over loose files. The
log names the pack every file came from, so you can always see which one won.


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
      mcmod.info
      rdploader/assets/thatmod/blocks/ruby_ore.json

Those are defaults, not overrides. A mod pack loads below every pack in this
folder, so anything you put here wins over it, and a mod may only supply files
under a namespace it declares in its own mcmod.info. Anything else is ignored
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

Packs without a prefix load before all numbered packs, in alphabetical order,
so a numbered pack always wins over an unnumbered one.

To turn a pack off without deleting it, add .disabled to the end of its name:

    rdploader/RDPL1 SeasonalTextures.zip.disabled

The pack is skipped and the log says so. Remove the suffix to turn it back on.


WHAT YOU CAN CHANGE
-------------------

Textures, models, blockstates, language files, sounds, fonts, splash texts, and
anything else a mod keeps in its assets folder, such as guide books or manuals.

Advancements and loot tables. These are server side, so they work on a dedicated
server as well.

Structure templates, the .nbt files under assets/<modid>/structures. A structure
saved in the world's own structures folder still wins over a file here, and a
structure that has already been placed stays loaded until you leave the world.

Recipes, including replacing a mod's recipe or adding one of your own. Recipes
only load when the game starts, so a change here needs a restart rather than a
reload.

Functions, the .mcfunction files under assets/<modid>/functions. Minecraft only
reads these from the world's own data folder, so putting them here makes them
work in every world. A function saved in the world still wins over a file here.

Registry renames, so a world saved before a mod renamed one of its blocks keeps
that block instead of losing it. Put a file in assets/<modid>/registry_remap:

    {
      "registry": "minecraft:items",
      "mapping": { "oldmod:old_name": "newmod:new_name" }
    }

The registry is the one the entry belongs to, usually minecraft:items or
minecraft:blocks. Renames chain, so mapping A to B and later B to C sends A to C.

Properties of things that already exist, without touching their files. A file
in assets/<yourpack>/overrides names its target by path, so
overrides/minecraft/stone.json changes minecraft:stone, vanilla or modded
alike. Blocks take hardness, blast resistance, light, light opacity,
slipperiness, sound, harvest tool and level, and flammability. Items take
stack size, durability and a container item, and any item can be made edible,
with food values and effects. A potion's effects can be rewritten. These are
live: disable the pack and run /rdpl reload, and every value snaps back to
what it was, no restart needed. Put the owning mod's id in "requires" so the
file is skipped quietly when that mod is not installed.

Player loot, a thing the game has no name for at all. Players drop their
inventory and nothing else, so a file in assets/<modid>/player_loot gives them
a loot table of their own:

    {
      "table": "mypack:entities/player",
      "mode": "add",
      "rollOnKeepInventory": false
    }

"add" drops what the table rolls alongside everything they were carrying,
"replace" drops it instead of their inventory, and rollOnKeepInventory decides
whether the table is rolled at all on a world where inventories are kept.

A banner is the odd one out. One definition registers two blocks, yours and
a second named <name>_wall, and only the standing one gets an item, which
picks between them as you place it. Its model reaches well past its own
block, to 29.33 of 16 standing and 13 below the block on a wall, and the
standing blockstate needs Forge's format to turn in sixteenths. The guides
have the full measurements.

A block's drops can be random, and need not be items. Entries in its drops list
are each decided on their own, unless you give them a weight, in which case
they share one pot and exactly one of them comes out. An entry naming an
entity instead of a block lets that entity loose where the block stood.

A texture can be written out as JSON instead of drawn. Name the file after the
PNG with .json on the end, textures/blocks/panel.png.json, and give it a size
such as 16x16 or 16x32, a palette of one character to one color, and rows of
those characters from the top down. Another such file can extend it and name
only the colors it wants different, so one shape can be recolored as many
times as you like without a single image file. What they draw is kept in
pixelmap-cache here and redrawn whenever a map or its template changes.

CraftTweaker and GroovyScript still work exactly as before. They run after this
mod, so anything your scripts remove or change wins over a file here.

RDPL is good for replacing one or two recipes, and recipes for your own
content belong in the pack alongside it. For full recipe control across a
modpack, CraftTweaker and GroovyScript are better options. A file here
replaces the original completely, so to change one ingredient or drop one
loot entry, use those.


ADDING NEW CONTENT
------------------

A pack can also add blocks, items and fluids of its own, described as JSON. You
do not need to write or build a mod for this.

The file's path is its name. A block at

    rdploader/MyPack/assets/mypack/blocks/copper_ore.json

registers as mypack:copper_ore. There is no name field to fill in or get wrong.
If a real mod already registers that name, the mod wins and your file is skipped.

The simplest block is a few lines:

    {
      "type": "ore",
      "material": "rock",
      "harvestTool": "pickaxe",
      "variants": {
        "copper_ore": { "meta": 0, "hardness": 3.0, "harvestLevel": 1 }
      }
    }

You still supply the model, blockstate, texture and language entry the same way
as any other file in this folder.

Each of these is a folder under assets/<yourpack>:

    blocks           items            fluids           materials
    worldgen         furnace          fuels            oredict
    sounds           tabs             recipes          recipe_removals
    loot_tables      loot_injections  advancements     functions
    structures       registry_remap   potions          potion_types
    brewing          villagers        trades           biomes
    villages         entities         gates            dimensions
    gamerules        worldtemplates   worldintro       pathintersects
    hardness         blastplaster     player_loot      overrides

Blocks come in these shapes, set by the "type" field:

    basic   ore     falling   slab    stairs   fence    door
    pane    wall    ladder    torch   crop     flower   cane
    log     leaves  sapling   vine    portal   trapdoor fence_gate
    banner

and items in these:

    basic   food    drink     potion  tool     armor    seed
    potion_bottle

A potion type always appears on the vanilla potion, splash potion, lingering
potion and tipped arrow, which live in the Brewing and Combat tabs. The tab is
a property of the item, not of the potion type, so there is no way to move them
into your own tab. A potion_bottle item is your own container instead: it takes
a creativeTab like any other item, lists the potion types you name in
potionTypes, and the brewing stand accepts it wherever a glass bottle works.

A villagers/<name>.json file defines a profession and the careers it offers.
A trades/*.json file adds trades to any career, whether yours or one of
Minecraft's, naming the profession, the career and the level the trade appears
at. Name a career that does not exist and the log lists the ones that do.

An entities/<name>.json file makes a new entity out of one that is already here.
It names the entity to build on, and what is different about it: its name, its skin,
how much health and damage it has, how fast it moves and how high it jumps, how big
it is drawn, what it wears, what it hunts and what it ignores, and whether it
still obeys the spawn rules of the entity it was built from. It is an entity of
its own, with its own spawn egg and loot table, and the one it was built from is
left alone. A village plot can be told to house one instead of a villager.

A villages/<name>.json file adds a plot villages can build, either a farm you
describe or one of your .nbt templates. The same settings choose which vanilla
pieces still appear, how far apart villages are seeded, and which biomes they
are allowed in.

A worldintro/<name>.json file plays a run of pages when somebody enters the
world, before they take control: scrolling text over a picture, a title card, a
slideshow, with music behind it if you want. The words are plain .txt files
under assets/<yourpack>/texts. It can play once per player or on every join.

WHOLE WORLDS
------------

A pack is not limited to single things. dimensions/<name>.json registers a
dimension with its own terrain, biomes and sky. gates/<name>.json puts a
condition on reaching one, such as holding or spending an item. A block of
type portal sends whoever walks in, and remembers who built it.

A world template can also shape the overworld itself, setting sea level, lava
oceans and the terrain noise. That is applied as a world is created and never
afterwards, so a world that already exists is left as it was.

worldtemplates/<name>.json gathers a world's settings into one file, so a pack
can ship a whole world shape at once instead of asking for a dozen config
edits. Every group it can set also answers to the control category in the
config, which decides whether the pack decides, the config decides, or the
group is off entirely and no pack can turn it on.

worldgen is more than ore. An entry is a shape placed by a spread: blobs, long
veins, plates, geodes, bowls, spires, nodules, vents, surface decoration, whole
trees, vines, belts that span several chunks, or one of your own .nbt templates,
spread evenly, around a height, fractally, along the terrain, on cave floors or
ceilings, or under water.


A biomes/<name>.json file defines a biome: its climate and colors, the blocks
it is made of, what decorates it, what spawns in it, and where it generates.
Its number is chosen for you and written into each world the first time that
world loads, so it stays put afterwards no matter what else you install. Set
"id" only when a biome has to keep a number something else already used, such
as when a pack replaces a mod that is being retired. Renaming or deleting a
biome a world already contains loses it, the same as renumbering a block, so
use registry_remap for a rename instead.

A villager's displayed name is the lang key entity.Villager.<career>, using the
career name exactly as you wrote it and nothing else. That key space is shared
with Minecraft and every other pack, so put your namespace in the career name,
as in rdpltest.prospector. Only the name is affected: a villager stores its
career as a number, so renaming one changes what existing villagers are
called, and reordering the careers list changes which career they have.

A potion type is named for its file the same way, and its displayed name comes
from the lang key potion.effect.<namespace>.<name>, with splash_potion.effect.,
lingering_potion.effect. and tipped_arrow.effect. for the other three forms.


RUBIC WORLDS
------------

A world template can ask for a world built out of cubes instead of 256 block
columns, and the world then grows both ways: a floor far below zero, a ceiling
far above 255, with terrain, caves and ores through all of it. Playing in one is
ordinary. You dig, build, light and travel the same way, and the vanilla
generation window keeps its usual shape inside the taller world, so mods that
generate terrain land where they always did.

What a pack gets from it: a world height of its own, set once when the world is
made; a deep world under the vanilla window, with noise caves, aquifers and
banded ore veins in a stone you name; cave regions, the pack answer to cave
biomes, painted through the underground in three dimensions with their own
floors, ceilings, water level, mobs and structures; dimensions stacked on each
other, so falling out of the bottom of one world carries you into the next one
down and climbing out the top brings you back; and any dimension left out,
keeping its ordinary world in the same save.

Underneath, a world is stored as 16 by 16 by 16 cubes in their own region files
beside the vanilla ones, generated, loaded and saved on their own, with a light
engine written for that shape. Vanilla's assumption that a world is 256 blocks
tall is patched wherever it is load bearing, from build limits and kill planes
to pathing, portals, beacons, maps and the renderer. Other mods' generators
still see a normal looking 256 block window, which is why their terrain works.

HOWTO.md has the settings, the heights a world may take, and the mods this will
not run beside.


A WARNING ABOUT META
--------------------

Every variant has a meta number, and that number is what the world file stores.
Renumbering a variant people already have in a world turns their blocks into
something else. Add new variants on the end and never renumber an old one.

A block holds 16 variants, because that is what four bits of metadata allows.
Slabs get 8, since one bit says top or bottom, and stairs, ladders, torches and
crops get 1, because facing or age uses the rest. Items are not as tight and
can skip numbers.


WHERE THIS STOPS
----------------

This describes what a thing is, not what it does over time. Anything needing a
tile entity, a GUI, an inventory or code running every tick still needs a real
mod. A machine is out of reach; an ore, a fence, a food or a fluid is not.


SEEING YOUR CHANGES
-------------------

Press F3+T to reload textures, models, language files, advancements and loot
tables. On a server, type /reload for the same thing. Recipes are the exception,
as above: they only load at startup, so a recipe change needs a restart.

If you add a new file or delete one, use /rdpl reload instead. Editing a file
that was already there only needs F3+T.

/rdpl reload textures reloads only textures, which is much faster than F3+T in a
large pack. models, languages, sounds and shaders work the same way. Leave the
name off to rescan the folder and reload everything.

/rdpl list shows every pack loaded and what is in it. Click a pack to see it.

/rdpl which minecraft:textures/blocks/stone.png shows which pack serves a file
and which packs are shadowed underneath it.

These work without being an operator, because they only read files on your own
computer. On a dedicated server, /rdplserver reload rescans the server's copy.


IF SOMETHING DOES NOT WORK
--------------------------

Check the log first. Advancements, loot tables, recipes, functions and
structures are logged with the pack they came from, and anything wrong is logged
as a warning saying why.

For textures and other assets, /rdpl unused lists any file in your packs that
nothing has asked for yet, which usually means a typo in the path. Run it after
the game has finished loading, and bear in mind some files only load when they
are needed, such as languages other than the one you play in.

Capital letters matter. If your file is Stone.png and the game asked for
stone.png, it still loads, but a warning tells you to rename it. Do rename it,
because anywhere other than this mod the file will not be found at all.
Language files trip people up most often: they are en_us.lang, not en_US.lang.

Check your files sit inside an 'assets' folder. A pack folder or zip without one
is skipped, and the log says so.



ADVANCEMENTS AND RECIPES
------------------------

If your scripts remove a recipe, any advancement that unlocked it keeps working
instead of breaking. It just has no recipe left to give you, and the log names it
once.

If you replaced that recipe with a new one and you want the advancement to unlock
the new one, give the new recipe a name in your script:

    recipes.addShaped("rail", <minecraft:rail> * 16, [[...]]);

That registers it as crafttweaker:rail. Then drop an advancement file in here
pointing at that name, and the advancement works end to end again.

Without a name it gets called something like crafttweaker:ct_shaped-1834729103,
a hash of the recipe itself. That changes the moment you edit the recipe, and it
can shift if another recipe is added before it, so it is not safe to point an
advancement at.


The rdploader folder itself can be moved or renamed with the rootDirectory option
in config/mct_resourcedatapackloader_mixin.cfg. An absolute path works too, and
a restart is required.

Put a pack.png next to this file to give the pack an icon.

This file is written by the mod and brought up to date whenever it changes,
so anything you type into it is replaced the next time the game starts.
