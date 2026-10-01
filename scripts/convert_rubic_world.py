"""Convert a Minecraft 1.12.2 world between a rubic world and a Cubic Chunks world.

usage: python3 convert_rubic_world.py <to-cubic|to-rubic> <world folder> [--dry-run]

  to-cubic   a rubic world (Resource Data Pack Loader) becomes a Cubic Chunks world
  to-rubic   a Cubic Chunks world becomes a rubic world
  --dry-run  print every change and make none

Close the game first and keep a backup of the world.

In every dimension of the world it renames the region files in region2d and
region3d and their .ext folders (.2rdr and .3rdr for rubic, .2dr and .3dr for
Cubic Chunks; a rubic world that still has the older .2dr and .3dr names is
accepted as it is), turns data/rdplRubicData.dat into data/cubicChunksData.dat
or back, storage format and compatibility generator names included, and last
swaps the isRubicWorld and isCubicWorld marker in level.dat and level.dat_old.

It refuses a world whose level.dat marker does not match the direction, a
storage format or compatibility generator the other side does not have, and a
rename whose target already exists. Nothing is changed when it refuses. A run
that was interrupted can simply be repeated.

Only what both mods understand survives: blocks and dimensions a pack defined
do not exist under plain Cubic Chunks.
"""
import gzip
import os
import re
import sys

SIZES = {1: 1, 2: 2, 3: 4, 4: 8, 5: 4, 6: 8}
ARRAYS = {7: 1, 11: 4, 12: 8}
RUBIC = {
    "label": "rubic",
    "marker": b"isRubicWorld",
    "columns": ".2rdr",
    "cubes": ".3rdr",
    "data": "rdplRubicData.dat",
    "flag": b"isRubicWorld",
    "storageFormat": b"resourcedatapackloader:rubic3d",
    "compatibilityGeneratorType": b"resourcedatapackloader:default",
}
CUBIC = {
    "label": "Cubic Chunks",
    "marker": b"isCubicWorld",
    "columns": ".2dr",
    "cubes": ".3dr",
    "data": "cubicChunksData.dat",
    "flag": b"isCubicChunks",
    "storageFormat": b"cubicchunks:anvil3d",
    "compatibilityGeneratorType": b"cubicchunks:default",
}
DIRECTIONS = {"to-cubic": (RUBIC, CUBIC), "to-rubic": (CUBIC, RUBIC)}
LEVEL_FILES = ("level.dat", "level.dat_old")


class Refused(Exception):
    pass


class Reader:
    def __init__(self, data):
        self.data = data
        self.at = 0

    def take(self, count):
        end = self.at + count
        if count < 0 or end > len(self.data):
            raise ValueError("the NBT data ends early")
        part = self.data[self.at:end]
        self.at = end
        return part

    def number(self, size):
        return int.from_bytes(self.take(size), "big", signed=True)

    def text(self):
        return self.take(int.from_bytes(self.take(2), "big"))

    def payload(self, kind):
        if kind in SIZES:
            return self.take(SIZES[kind])
        if kind in ARRAYS:
            return self.take(self.number(4) * ARRAYS[kind])
        if kind == 8:
            return self.text()
        if kind == 9:
            inner = self.take(1)[0]
            return inner, [self.payload(inner) for _ in range(self.number(4))]
        if kind == 10:
            held = {}
            while True:
                inner = self.take(1)[0]
                if inner == 0:
                    return held
                name = self.text()
                held[name] = (inner, self.payload(inner))
        raise ValueError("unknown NBT tag type %d" % kind)


def encode(kind, value, out):
    if kind in SIZES:
        out += value
    elif kind in ARRAYS:
        out += (len(value) // ARRAYS[kind]).to_bytes(4, "big")
        out += value
    elif kind == 8:
        out += len(value).to_bytes(2, "big")
        out += value
    elif kind == 9:
        out.append(value[0])
        out += len(value[1]).to_bytes(4, "big")
        for item in value[1]:
            encode(value[0], item, out)
    else:
        for name, (inner, held) in value.items():
            out.append(inner)
            out += len(name).to_bytes(2, "big")
            out += name
            encode(inner, held, out)
        out.append(0)


def read_nbt(path):
    with gzip.open(path, "rb") as source:
        reader = Reader(source.read())
    if reader.take(1)[0] != 10:
        raise ValueError("the root tag is not a compound")
    return reader.text(), reader.payload(10)


def write_nbt(path, name, root):
    out = bytearray([10])
    out += len(name).to_bytes(2, "big")
    out += name
    encode(10, root, out)
    partial = path + ".tmp"
    with gzip.open(partial, "wb") as target:
        target.write(bytes(out))
    os.replace(partial, path)


def compound(parent, name):
    held = parent.get(name)
    if held is None or held[0] != 10:
        raise ValueError("no %s compound" % name.decode())
    return held[1]


def is_set(parent, name):
    held = parent.get(name)
    return held is not None and held[0] in SIZES and any(held[1])


def rename_key(parent, old, new, value=None):
    kept = [(new if name == old else name, value or held if name == old else held) for name, held in parent.items() if name != new]
    parent.clear()
    parent.update(kept)


def world_kind(data):
    if is_set(data, RUBIC["marker"]):
        return RUBIC["label"]
    if is_set(data, CUBIC["marker"]):
        return CUBIC["label"]
    return "neither"


def plan_renames(world, folder, old, new, renames, problems):
    pattern = re.compile(r"(-?\d+(?:\.-?\d+){1,2})" + re.escape(old) + r"(\.ext)?")
    for name in sorted(os.listdir(folder)):
        match = pattern.fullmatch(name)
        if not match:
            continue
        target = os.path.join(folder, match.group(1) + new + (match.group(2) or ""))
        if os.path.lexists(target):
            problems.append("%s is already there, so %s cannot take its name" % (os.path.relpath(target, world), name))
            continue
        renames.append((os.path.join(folder, name), target))


def plan_data(world, folder, source, target, conversions, problems):
    path = os.path.join(folder, source["data"])
    if not os.path.isfile(path):
        return
    shown = os.path.relpath(path, world)
    try:
        name, root = read_nbt(path)
        data = compound(root, b"data")
    except (OSError, EOFError, ValueError) as error:
        problems.append("%s cannot be read: %s" % (shown, error))
        return
    notes = []
    if source["flag"] in data:
        rename_key(data, source["flag"], target["flag"])
        notes.append("%s -> %s" % (source["flag"].decode(), target["flag"].decode()))
    for key in ("storageFormat", "compatibilityGeneratorType"):
        held = data.get(key.encode())
        if held is None:
            continue
        if held[0] != 8 or held[1] != source[key]:
            problems.append("%s has %s %s, which %s does not have" % (shown, key, held[1].decode(errors="replace") if held[0] == 8 else "of another type", target["label"]))
            continue
        data[key.encode()] = (8, target[key])
        notes.append("%s %s -> %s" % (key, source[key].decode(), target[key].decode()))
    conversions.append((path, os.path.join(folder, target["data"]), name, root, notes))


def plan_levels(world, source, target, problems):
    levels = []
    for file in LEVEL_FILES:
        path = os.path.join(world, file)
        if not os.path.isfile(path):
            if file == LEVEL_FILES[0]:
                raise Refused("%s has no level.dat, so it is not a world folder" % world)
            continue
        try:
            name, root = read_nbt(path)
            data = compound(root, b"Data")
        except (OSError, EOFError, ValueError) as error:
            if file == LEVEL_FILES[0]:
                raise Refused("level.dat cannot be read: %s" % error)
            problems.append("%s cannot be read: %s" % (file, error))
            continue
        kind = world_kind(data)
        if kind != source["label"]:
            if file == LEVEL_FILES[0]:
                raise Refused("level.dat marks this world as %s, not as a %s world" % (kind if kind == "neither" else "a %s world" % kind, source["label"]))
            continue
        rename_key(data, source["marker"], target["marker"], (1, b"\x01"))
        levels.append((path, name, root))
    return levels


def convert(direction, world, dry_run):
    source, target = DIRECTIONS[direction]
    problems = []
    levels = plan_levels(world, source, target, problems)
    renames = []
    conversions = []
    for folder, inside, _ in os.walk(world):
        for part, key in (("region2d", "columns"), ("region3d", "cubes")):
            if part in inside:
                inside.remove(part)
                plan_renames(world, os.path.join(folder, part), source[key], target[key], renames, problems)
        if os.path.basename(folder) == "data":
            plan_data(world, folder, source, target, conversions, problems)
    if problems:
        raise Refused("\n".join(problems))
    if dry_run:
        for old, new in renames:
            print("rename %s -> %s" % (os.path.relpath(old, world), os.path.basename(new)))
        for old, new, _, _, notes in conversions:
            print("convert %s -> %s (%s)" % (os.path.relpath(old, world), os.path.basename(new), ", ".join(notes) or "no key changes"))
        for path, _, _ in levels:
            print("mark %s: %s removed, %s set" % (os.path.relpath(path, world), source["marker"].decode(), target["marker"].decode()))
    else:
        for old, new in renames:
            os.rename(old, new)
        for old, new, name, root, _ in conversions:
            write_nbt(new, name, root)
            os.remove(old)
        for path, name, root in reversed(levels):
            write_nbt(path, name, root)
    print("%s%d region file(s) and folder(s) renamed, %d data file(s) converted, %d level file(s) marked as a %s world"
          % ("dry run: " if dry_run else "", len(renames), len(conversions), len(levels), target["label"]))


def main(arguments):
    dry_run = "--dry-run" in arguments
    rest = [argument for argument in arguments if argument != "--dry-run"]
    if len(rest) != 2 or rest[0] not in DIRECTIONS or not os.path.isdir(rest[1]):
        print(__doc__, file=sys.stderr)
        return 2
    try:
        convert(rest[0], os.path.abspath(rest[1]), dry_run)
    except Refused as refusal:
        print("refused, nothing was changed:\n%s" % refusal, file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
