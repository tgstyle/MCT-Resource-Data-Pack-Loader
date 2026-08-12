MCT Resource Data Pack Loader - pixel map tools
===============================================

Three scripts that turn a pack's PNG textures into the JSON pixel maps the
mod draws. A map sits where the PNG would have gone with .json on the end
of the whole name, so textures/blocks/panel.png.json answers every request
for textures/blocks/panel.png. Nothing that points at the texture changes.


A SESSION, START TO FINISH
--------------------------

Say the three scripts and your pack sit in the same folder.

    $ python3 convert_pack.py MCTBasemod MCTBasemod-maps
    mctbasemod                474 maps   tint 109 override 161 same 32 alone 141 skipped 0

    $ python3 verify_pack.py MCTBasemod MCTBasemod-maps
    == mctbasemod
    maps rendered and compared : 443
    kept as PNG                : 0
    mismatched                 : 0

Straight from a zip to a zip, ready to drop into rdploader:

    $ python3 convert_pack.py TiCh_Immersion_Assets.zip TiCh-maps.zip
    actuallyadditions           1 maps   tint 0 override 0 same 0 alone 1 skipped 0
    bigreactors                12 maps   tint 0 override 2 same 0 alone 9 skipped 0
    minecraft                1448 maps   tint 16 override 197 same 19 alone 1163 skipped 24
    ...
    24 texture(s) were kept as PNG rather than converted. This is expected,
    not a failure: a pack may hold maps and images side by side.

    $ python3 verify_pack.py TiCh_Immersion_Assets.zip TiCh-maps.zip

One texture on its own, written beside the image as coal_ore.png.json:

    $ python3 png_to_pixelmap.py MCTBasemod/assets/mctbasemod/textures/blocks/coal_ore.png
    MCTBasemod/assets/mctbasemod/textures/blocks/coal_ore.png.json is 16x16 with 7 color(s)

One texture as a child of a template. Against the ore template it keeps
only the speck colors:

    $ python3 png_to_pixelmap.py uranium_ore.png my_ore.json \
          --extends MCTBasemod-maps/assets/mctbasemod/textures/templates/ore.png.json
    my_ore.json is 16x16 with 4 palette entry(s)

    { "extends": "mctbasemod:textures/templates/ore.png",
      "palette": { "4": "#ABA363", "5": "#8D864C", "6": "#989051", "7": "#B3AC72" } }

Against the ingot template the same command finds a ramp instead:

    $ python3 png_to_pixelmap.py copper_ingot.png my_ingot.json \
          --extends MCTBasemod-maps/assets/mctbasemod/textures/templates/ingot.png.json
    my_ingot.json is 16x16 with a tint

    { "extends": "mctbasemod:textures/templates/ingot.png",
      "tint": { "from": "#462D17", "to": "#D67644" } }

And if you want no PNGs left at all, at the cost of much larger files:

    $ python3 convert_pack.py TiCh_Immersion_Assets.zip TiCh-all.zip --override-skips


WHICH SCRIPT IS WHICH
---------------------

Three files, easy to mix up after downloading, and the first line of each
says what it is:

    $ head -3 *.py | grep -A1 '==>'

Or ask them:

    $ python3 convert_pack.py --help      | head -3
    $ python3 png_to_pixelmap.py --help   | head -3
    $ python3 verify_pack.py --help       | head -3

convert_pack.py takes a pack, png_to_pixelmap.py takes one texture, and
verify_pack.py compares a converted pack against the original. If a run
complains that your pack zip "is not a PNG file", the file you ran is
png_to_pixelmap.py under another name.


WHAT YOU NEED
-------------

Python 3.7 or newer, and nothing else. The PNG decoder is written into
png_to_pixelmap.py, so there is no Pillow to install. It reads every PNG
the format allows: 8 and 16 bit, grayscale, indexed, with or without
alpha, interlaced or not.

Keep all three files in one folder. convert_pack.py and verify_pack.py
import png_to_pixelmap.py from beside them and say so plainly if it is
missing.


ONE TEXTURE
-----------

    python3 png_to_pixelmap.py <texture.png> [output.json]

Writes <texture.png>.json beside the image unless you name an output.

    python3 png_to_pixelmap.py assets/mypack/textures/blocks/panel.png

leaves panel.png.json next to panel.png. Delete or rename the PNG once
you are happy with the map, because a PNG always wins over a map of the
same name and the map would never be drawn.

Fully transparent black becomes a hole, written as . in the rows with no
palette entry. Transparent pixels that still carry color keep it, as
#00RRGGBB, so nothing is lost.


BUILDING ON A TEMPLATE
----------------------

    python3 png_to_pixelmap.py <texture.png> [output.json] --extends <template.json>

Point it at a template and it writes the child instead of a whole map:
the shape stays in the template and the new file carries only what is
its own.

    python3 png_to_pixelmap.py uranium_ore.png \
        --extends assets/mctbasemod/textures/templates/ore.png.json

    {
      "extends": "mctbasemod:textures/templates/ore.png",
      "palette": { "4": "#ABA363", "5": "#8D864C", "6": "#989051", "7": "#B3AC72" }
    }

Only the characters whose color differs from the template are listed, so
an ore that keeps vanilla's stone says nothing about it. Where the colors
sit on a single ramp against a grayscale template the child comes out as
a tint instead, two lines for the whole texture.

Give the template as the path to its .json file. The name written into
extends is read from that path, so it has to sit under assets/<namespace>
somewhere; the template's own extends chain is followed as far as it
goes.

WHICH WAY ROUND

The template has to hold every distinction its children need, so it is
the more detailed of the two textures, not the simpler one. This is the
mistake worth knowing about, because it looks so reasonable:

    $ python3 png_to_pixelmap.py stone.png stone_template.json
    stone_template.json is 16x16 with 4 color(s)

    $ python3 png_to_pixelmap.py ore.png ore_extends.json \
          --extends stone_template.json
    ore.png was not turned into a pixel map: the template is coarser than the
    image: 37 pixel(s) share a character but not a color, so no child of it
    can draw this image

37 is the ore's speck count. The stone template calls those pixels stone,
and a child can recolor a character but cannot split one, so stone can
never parent an ore. Turn it round and it works: make the ore the
template, and other ores become four colors each.

    $ python3 png_to_pixelmap.py ore.png  ore_template.json
    ore_template.json is 16x16 with 8 color(s)

    $ python3 png_to_pixelmap.py ore2.png ore_extends.json \
          --extends ore_template.json
    ore_extends.json is 16x16 with 4 palette entry(s)

You can write a child that keeps the stone's palette and brings its own
rows, and it draws correctly, but it saves 11 bytes over a plain map:
a template pays for itself by sharing rows, and once the child writes its
own there is nothing much left to inherit. The script never writes rows
into a child for that reason.

A template is only served to the game if its path ends in .png, so
a shared shape named ore_template.json is invisible to the game and
exists purely to be extended.


A WHOLE PACK
------------

    python3 convert_pack.py <source> <target> [--templates <folder>]

<source> may be a pack folder, a pack zip, or a folder holding one pack,
so you can point it straight at the zip you drop into rdploader without
unpacking it first.

<target> follows its own name. End it in .zip and you get a zip, ready to
drop back into rdploader; anything else is written as a folder. It is
replaced before anything is written, so never point it at the source; the
script refuses if the two paths are the same.

    python3 convert_pack.py MyPack.zip MyPack-maps.zip

The zip is written with deflate at level 9, which is what the game reads.
Minecraft opens a pack zip with java.util.zip.ZipFile and this mod opens
one with the JDK zip filesystem, and both accept stored and deflate and
nothing else. A zip built by 7-Zip or WinZip with bzip2, LZMA, PPMd or
Zstandard inside is refused outright, with "invalid CEN header (bad
compression method)" in the log. The deflate level itself makes no
difference to the reader, only to how small the file ends up.

Put assets/ at the top level of the zip. A zip whose top level is one
folder wrapping assets/ is skipped by the mod, and the log says so. The
zips this script writes are already built that way.

While it runs it keeps one line on screen showing where it is, per
namespace and overall. That line only appears on a terminal, so piping
the output to a file leaves nothing but the summary.

Every folder under assets/ holding a textures folder is converted. A
folder whose name is not a valid namespace, meaning anything outside
a-z, 0-9, underscore, dot and dash, is named and skipped rather than
turned into something the game cannot load.

--templates says where shared shapes are written, relative to each
namespace. The default is textures/templates. It has to be relative:
a leading slash, a drive letter or a .. step is refused, and backslashes
are accepted so a Windows shell works.

Converted PNGs are removed from the copy, since the map would otherwise
never be drawn. Textures the script could not convert stay as PNGs, and
.mcmeta files are left alone, so animations keep working either way.


WHAT THE CONVERTER WRITES
-------------------------

Textures that share a shape are collapsed onto one template, and the
summary counts each kind:

  tint      A variant of a grayscale template, carrying two colors. The
            template holds the shading, the variant says where its ramp
            starts and ends. Two lines for a whole texture.

  override  A variant of a template that is not a single ramp, naming
            only the colors that differ from it. The template's notes
            mark which characters vary.

  same      A texture identical to another, pixel for pixel. One line
            pointing at it.

  alone     A texture sharing its shape with nothing, written out in
            full.

  skipped   Left as a PNG. See below for why, which the run also prints.

A template only appears where it earns its place. Where every member of
a group is identical, the first is written in full and the rest point at
it, with no template in between.


WHY SOMETHING STAYS A PNG
-------------------------

Not every texture should be a map, and the run says which were left alone
and why. There are two reasons.

Too many colors. A pixel map names every color with a single character,
so it cannot hold more colors than the alphabet has characters, and the
alphabet holds 451. What lands here is photographs, gradients, colormaps
and entity skins rather than pixel art. Vanilla's own textures give the
shape of it: the main menu panoramas, the grass and foliage colormaps,
the horse and creeper skins, the world preset preview.

Converting those would make them worse, not better. A PNG compresses and
a map does not, so a map is always the larger of the two, usually by
a lot:

    grass.png       256x256    4,536 colors    6 KB  ->  ~149 KB
    foliage.png     256x256    8,837 colors   14 KB  ->  ~227 KB
    panorama_1.png  512x512    3,849 colors   97 KB  ->  ~336 KB
    isles.png       256x256   20,765 colors   92 KB  ->  ~441 KB

The rows would also be built from characters far past the readable end
of the alphabet, so nobody could edit them by hand either.

Too large. A map may be at most 4096 pixels a side, because the mod
refuses to draw one bigger, and converting it would leave you with
a texture the game will not load.

Neither is a failure. A pack may hold maps and images side by side, and
the mod serves whatever it finds, so a texture left as a PNG behaves
exactly as it did before.

If you want them converted anyway:

    python3 convert_pack.py <source> <target> --override-skips

This widens the alphabet as far as the pack needs, up to about 52,000
single characters, and converts everything except textures over 4096
a side, which stay PNGs because the mod will not draw a map that big
whatever you pass.

The maps are correct, and they are also much larger than the images they
replace. On vanilla's textures grass.png goes from 6 KB to 212 KB, nearly
36 times, and isles.png from 92 KB to 592 KB. Their rows are built from
whatever single characters the alphabet reached, well past anything you
would want to read. Use it if you need a pack with no PNGs left in it,
not because it is an improvement.


CHECKING THE RESULT
-------------------

    python3 verify_pack.py <source> <target>

Takes the same kinds of path the converter does, a folder or a zip.
Reads every original texture, renders the map that replaced it through
the mod's own inheritance and painting rules, and compares the two pixel
by pixel. Run it after every conversion. It exits 1 and names the file if

  - any rendered map differs from the texture it replaced,
  - a texture ended up as neither a map nor a PNG,
  - or it found nothing to compare at all.

That last one matters. A checker that passes when it checked nothing is
not a checker.


LIMITS
------

A palette key is one character, and the alphabet holds 451 of them:
printable ASCII first, then Latin-1, Greek, Cyrillic and box drawing,
with lookalikes such as Greek Alpha and Cyrillic Er left out so no two
keys can be confused. Small textures never leave ASCII. Anything needing
more than 451 colors, or measuring more than 4096 pixels a side, is kept
as a PNG; see WHY SOMETHING STAYS A PNG above.

Textures are only grouped within one namespace, so a mod texture never
comes to depend on a vanilla one.

Grouping compares every texture against every other of the same size, so
a large pack takes a while. Vanilla's 1419 textures take about 30 seconds.

A big texture makes a big file. A 512x512 image becomes a quarter of
a megabyte of JSON, which is valid and exact but not something anyone
will edit by hand.


TUNING
------

GROWTH near the top of convert_pack.py, set to 1.5, decides how much
larger a template's palette may be than a variant's own color count
before the two are left apart. Raising it means fewer templates and
longer variant palettes; lowering it means the reverse. It never affects
correctness, only how the pack is arranged.
