import argparse
import json
import math
import os
import re
import shutil
import sys
import tempfile
import zipfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
try:
    import png_to_pixelmap as tool
except ImportError:
    raise SystemExit("png_to_pixelmap.py must sit in the same folder as this script")

OPAQUE = 0xFF000000
MAX_SIDE = 4096
SEGMENT = re.compile(r"^[a-z0-9_.-]+$")


def unpacked(source, scratch):
    if os.path.isdir(source):
        return source
    if not os.path.isfile(source):
        raise SystemExit("there is nothing at %s" % source)
    if not zipfile.is_zipfile(source):
        raise SystemExit("%s is neither a folder nor a zip" % source)

    with zipfile.ZipFile(source) as held:
        for name in held.namelist():
            if name.startswith("/") or name.startswith("\\") or ".." in name.replace("\\", "/").split("/"):
                raise SystemExit("the zip holds an entry that would write outside the folder: %s" % name)
        held.extractall(scratch)
    return scratch


def rooted(source):
    if os.path.isdir(os.path.join(source, "assets")):
        return source
    if os.path.isdir(source):
        inside = sorted(n for n in os.listdir(source) if os.path.isdir(os.path.join(source, n, "assets")))
        if len(inside) == 1:
            return os.path.join(source, inside[0])
    raise SystemExit("no assets folder in %s" % source)


def discover(source):
    assets = os.path.join(source, "assets")
    return [n for n in sorted(os.listdir(assets))
            if os.path.isdir(os.path.join(assets, n, "textures")) and SEGMENT.match(n)]


def color(written):
    if written is None:
        return None
    t = written.strip()
    if t.startswith("#"):
        t = t[1:]
    if t[:2] in ("0x", "0X"):
        t = t[2:]
    if len(t) not in (6, 8):
        return None
    try:
        v = int(t, 16)
    except ValueError:
        return None
    return (v | OPAQUE) & 0xFFFFFFFF if len(t) == 6 else v


def ramp(level, frm, to, shift):
    s = (frm >> shift) & 0xFF
    e = (to >> shift) & 0xFF
    return max(0, min(255, int(math.floor(s + level * (e - s) / 255.0 + 0.5))))


def shade(c, frm, to):
    level = (((c >> 16) & 0xFF) + ((c >> 8) & 0xFF) + (c & 0xFF)) // 3
    return (c & OPAQUE) | (ramp(level, frm, to, 16) << 16) | (ramp(level, frm, to, 8) << 8) | ramp(level, frm, to, 0)


class Fail(Exception):
    pass


def resolve(root, namespace, path):
    palette = {}
    rows = None
    size = None
    tint = None
    seen = set()
    where, at = namespace, path + ".json"
    base = None
    while at is not None:
        token = where + ":" + at
        if token in seen:
            raise Fail("loop at " + token)
        seen.add(token)
        full = os.path.join(root, "assets", where, at.replace("/", os.sep))
        if not os.path.isfile(full):
            raise Fail("missing " + token)
        doc = json.load(open(full, encoding="utf-8"))

        for k, v in doc.get("palette", {}).items():
            if k not in palette:
                palette[k] = v
        if rows is None and "rows" in doc:
            rows = list(doc["rows"])
        if size is None and "size" in doc:
            wide, tall = doc["size"].split("x")
            size = (int(wide), int(tall))
        if tint is None and "tint" in doc:
            frm = color(doc["tint"].get("from", "#000000"))
            to = color(doc["tint"].get("to"))
            if frm is None or to is None:
                raise Fail("bad tint in " + token)
            tint = (frm, to)

        nxt = doc.get("extends", "")
        if not nxt:
            break
        colon = nxt.find(":")
        nw = where if colon < 0 else nxt[:colon]
        np_ = nxt if colon < 0 else nxt[colon + 1:]
        candidate = os.path.join(root, "assets", nw, (np_ + ".json").replace("/", os.sep))
        if os.path.isfile(candidate):
            where, at = nw, np_ + ".json"
            continue
        image = os.path.join(root, "assets", nw, np_.replace("/", os.sep))
        if not os.path.isfile(image):
            raise Fail("extends resolves to nothing: " + nxt)
        w, h, grid = tool.decode(open(image, "rb").read())
        if size is None:
            size = (w, h)
        base = [((p[3] & 0xFF) << 24) | ((p[0] & 0xFF) << 16) | ((p[1] & 0xFF) << 8) | p[2] for row in grid for p in row]
        at = None

    if size is None:
        raise Fail("no size")
    if base is None and not rows:
        raise Fail("no rows")
    if not (1 <= size[0] <= MAX_SIDE and 1 <= size[1] <= MAX_SIDE):
        raise Fail("bad size")
    if base is None:
        if len(rows) != size[1]:
            raise Fail("row count %d != height %d" % (len(rows), size[1]))
        for i, r in enumerate(rows):
            if len(r) != size[0]:
                raise Fail("row %d width %d != %d" % (i + 1, len(r), size[0]))
    return size, rows, palette, tint, base


def paint(root, namespace, path):
    size, rows, palette, tint, base = resolve(root, namespace, path)
    if base is not None:
        swaps = {}
        for k, v in palette.items():
            was, becomes = color(k), color(v)
            if was is not None and becomes is not None:
                swaps[was] = becomes
        out = []
        for y in range(size[1]):
            line = []
            for x in range(size[0]):
                was = base[y * size[0] + x]
                drawn = swaps.get(was, was)
                line.append(shade(drawn, *tint) if tint else drawn)
            out.append(line)
        return size, out

    colors = {}
    for k, v in palette.items():
        if len(k) != 1:
            continue
        c = color(v)
        if c is None:
            continue
        colors[k] = shade(c, *tint) if tint else c
    out = []
    for y in range(size[1]):
        line = []
        for x in range(size[0]):
            line.append(colors.get(rows[y][x], 0))
        out.append(line)
    return size, out


def main(original, converted, space):
    root = os.path.join(original, "assets", space)
    if not os.path.isdir(os.path.join(root, "textures")):
        raise SystemExit("no assets/%s/textures folder in %s" % (space, original))
    checked = failed = kept = 0
    problems = []
    for base, _, names in os.walk(os.path.join(root, "textures")):
        for n in sorted(names):
            if not n.endswith(".png"):
                continue
            src = os.path.join(base, n)
            rel = os.path.relpath(src, root).replace(os.sep, "/")
            made = os.path.join(converted, "assets", space, (rel + ".json").replace("/", os.sep))
            if not os.path.isfile(made):
                if os.path.isfile(os.path.join(converted, "assets", space, rel.replace("/", os.sep))):
                    kept += 1
                    continue
                failed += 1
                problems.append((rel, "neither a map nor a PNG in the converted pack"))
                continue
            w, h, grid = tool.decode(open(src, "rb").read())
            want = [[((p[3] & 0xFF) << 24) | ((p[0] & 0xFF) << 16) | ((p[1] & 0xFF) << 8) | p[2] for p in row] for row in grid]
            try:
                size, got = paint(converted, space, rel)
            except Fail as ex:
                failed += 1
                problems.append((rel, str(ex)))
                continue
            checked += 1
            if size != (w, h):
                failed += 1
                problems.append((rel, "size %s != %s" % (size, (w, h))))
                continue
            wrong = sum(1 for y in range(h) for x in range(w) if got[y][x] != want[y][x])
            if wrong:
                failed += 1
                spot = next((y, x) for y in range(h) for x in range(w) if got[y][x] != want[y][x])
                problems.append((rel, "%d px differ, first at %s got %08X want %08X"
                                 % (wrong, spot, got[spot[0]][spot[1]], want[spot[0]][spot[1]])))
    print("maps rendered and compared : %d" % checked)
    print("kept as PNG                : %d" % kept)
    print("mismatched                 : %d" % failed)
    if checked == 0:
        print("NOTHING WAS CHECKED, the converted pack holds no maps for this namespace")
        return 1
    for rel, why in problems[:15]:
        print("   %-52s %s" % (rel, why))
    return failed


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Render a converted pack's pixel maps and compare them to the original PNGs.")
    parser.add_argument("original", help="the pack the textures came from")
    parser.add_argument("converted", help="the pack the maps were written into")
    args = parser.parse_args()
    scratch = tempfile.mkdtemp(prefix="pixelmap-")
    try:
        original = rooted(unpacked(args.original, os.path.join(scratch, "a")))
        converted = rooted(unpacked(args.converted, os.path.join(scratch, "b")))
        spaces = discover(original)
        if not spaces:
            raise SystemExit("no namespace under assets/ holds a textures folder in %s" % args.original)
        bad = 0
        for space in spaces:
            print("== %s" % space)
            bad += main(original, converted, space)
    finally:
        shutil.rmtree(scratch, ignore_errors=True)
    sys.exit(1 if bad else 0)
