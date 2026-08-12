import argparse
import collections
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

ALPHABET = tool.ALPHABET
LIMIT = len(ALPHABET)
OPAQUE = 0xFF000000
LOUD = sys.stderr.isatty()
GROWTH = 1.5
MAX_SIDE = 4096
SEGMENT = re.compile(r"^[a-z0-9_.-]+$")


def folder(value):
    if value.startswith("/") or value.startswith("\\") or ":" in value:
        raise argparse.ArgumentTypeError("the template folder must be relative to the namespace: '%s'" % value)
    cleaned = value.replace("\\", "/").strip("/")
    if not cleaned:
        raise argparse.ArgumentTypeError("the template folder may not be empty")
    for part in cleaned.split("/"):
        if part in (".", ".."):
            raise argparse.ArgumentTypeError("the template folder may not step outside the pack: '%s'" % value)
        if not SEGMENT.match(part):
            raise argparse.ArgumentTypeError("'%s' may hold only a-z, 0-9, underscore, dot, dash and slash" % value)
    return cleaned


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

    inside = sorted(n for n in os.listdir(source) if os.path.isdir(os.path.join(source, n, "assets")))
    if len(inside) == 1:
        return os.path.join(source, inside[0])
    if inside:
        raise SystemExit("%s holds several packs (%s), name the one you want"
                         % (source, ", ".join(inside)))
    raise SystemExit("no assets folder in %s, and none in the folders inside it" % source)


def discover(source):
    assets = os.path.join(source, "assets")
    spaces = []
    for name in sorted(os.listdir(assets)):
        if not os.path.isdir(os.path.join(assets, name, "textures")):
            continue
        if not SEGMENT.match(name):
            print("skipping '%s', a namespace may hold only a-z, 0-9, underscore, dot and dash" % name)
            continue
        spaces.append(name)
    return spaces


def note(text):
    if LOUD:
        sys.stderr.write("\r" + text.ljust(78)[:78])
        sys.stderr.flush()


def hush():
    if LOUD:
        sys.stderr.write("\r" + " " * 78 + "\r")
        sys.stderr.flush()


def zipped(folder, target):
    scratch = target + ".building"
    if os.path.exists(scratch):
        os.remove(scratch)
    with zipfile.ZipFile(scratch, "w", zipfile.ZIP_DEFLATED, compresslevel=9) as held:
        for base, _, names in os.walk(folder):
            for n in sorted(names):
                full = os.path.join(base, n)
                held.write(full, os.path.relpath(full, folder).replace(os.sep, "/"))
    shutil.move(scratch, target)


def argb(px):
    return ((px[3] & 0xFF) << 24) | ((px[0] & 0xFF) << 16) | ((px[1] & 0xFF) << 8) | (px[2] & 0xFF)


def written(c):
    a = (c >> 24) & 0xFF
    if a == 255:
        return "#%02X%02X%02X" % ((c >> 16) & 0xFF, (c >> 8) & 0xFF, c & 0xFF)
    return "#%02X%02X%02X%02X" % (a, (c >> 16) & 0xFF, (c >> 8) & 0xFF, c & 0xFF)


def ramp(level, frm, to, shift):
    s = (frm >> shift) & 0xFF
    e = (to >> shift) & 0xFF
    return max(0, min(255, int(math.floor(s + level * (e - s) / 255.0 + 0.5))))


def shade(c, frm, to):
    level = (((c >> 16) & 0xFF) + ((c >> 8) & 0xFF) + (c & 0xFF)) // 3
    return (c & OPAQUE) | (ramp(level, frm, to, 16) << 16) | (ramp(level, frm, to, 8) << 8) | ramp(level, frm, to, 0)


def pixels(path):
    w, h, grid = tool.decode(open(path, "rb").read())
    return w, h, [[argb(px) for px in row] for row in grid]


def partition(grid):
    w, h, g = grid
    order = {}
    spots = []
    sig = []
    for y in range(h):
        line = []
        for x in range(w):
            c = g[y][x]
            if c not in order:
                order[c] = len(order)
                spots.append((x, y))
            line.append(order[c])
        sig.append(tuple(line))
    return tuple(sig), spots


def covers(base, other):
    bw, bh, bg = base
    ow, oh, og = other
    if (bw, bh) != (ow, oh):
        return False
    seen = {}
    for y in range(bh):
        for x in range(bw):
            was, becomes = bg[y][x], og[y][x]
            if was in seen:
                if seen[was] != becomes:
                    return False
            else:
                seen[was] = becomes
    return True


def families(grids, tally, seen=None):
    bysize = collections.defaultdict(list)
    for rel in grids:
        bysize[grids[rel][:2]].append(rel)
    groups = []
    done = 0
    for _, members in sorted(bysize.items()):
        members = sorted(members)
        reach = {}
        for a in members:
            reach[a] = {b for b in members if b != a and tally[a] <= tally[b] * GROWTH and covers(grids[a], grids[b])}
            done += 1
            if seen:
                seen("grouping", done)
        remaining = set(members)
        while remaining:
            base = max(sorted(remaining), key=lambda a: (len(reach[a] & remaining), -tally[a]))
            group = (reach[base] & remaining) | {base}
            groups.append((base, sorted(group)))
            remaining -= group
    return sorted(groups)


def keys_for(sig, count, alphabet=ALPHABET):
    tally = collections.Counter(i for row in sig for i in row)
    ranked = sorted(range(count), key=lambda i: (-tally[i], i))
    return {index: alphabet[slot] for slot, index in enumerate(ranked)}


def family(members, taken):
    parts = [os.path.basename(m)[:-len(".png")].split("_") for m in members]
    shared = collections.Counter()
    for p in parts:
        for i in range(len(p)):
            shared["_".join(p[i:])] += 1
    best = [(n, len(k.split("_")), k) for k, n in shared.items() if n > 1]
    name = max(best)[2] if best else "shape"
    if not SEGMENT.match(name):
        name = re.sub(r"[^a-z0-9_.-]", "_", name.lower()) or "shape"
    if name not in taken:
        taken.add(name)
        return name
    n = 2
    while "%s_%d" % (name, n) in taken:
        n += 1
    taken.add("%s_%d" % (name, n))
    return "%s_%d" % (name, n)


def gradient(palettes):
    count = len(palettes[0])
    lum = lambda c: 0.299 * ((c >> 16) & 0xFF) + 0.587 * ((c >> 8) & 0xFF) + 0.114 * (c & 0xFF)
    reference = palettes[0]
    a = min(range(count), key=lambda i: lum(reference[i]))
    b = max(range(count), key=lambda i: lum(reference[i]))
    grays = []
    for i in range(count):
        found = None
        base = reference[i] & OPAQUE
        for level in range(256):
            if all(shade(base | (level << 16) | (level << 8) | level, p[a], p[b]) == p[i] for p in palettes):
                found = level
                break
        if found is None:
            return None
        grays.append(found)
    return a, b, grays


def document(size=None, rows=None, palette=None, notes=None, extends=None, tint=None):
    out = ["{"]
    if extends:
        out.append('  "extends": "%s",' % extends)
    if tint:
        out.append('  "tint": { "from": "%s", "to": "%s" },' % tint)
    if size:
        out.append('  "size": "%dx%d",' % size)
    if palette:
        if len(palette) > 24:
            out.append('  "palette": {')
            for i, (k, v) in enumerate(palette):
                out.append('    "%s": "%s"%s' % (k, v, "" if i == len(palette) - 1 else ","))
            out.append("  },")
        else:
            out.append('  "palette": { ' + ", ".join('"%s": "%s"' % e for e in palette) + " },")
    if notes:
        out.append('  "notes": {')
        for i, (k, v) in enumerate(notes):
            out.append('    "%s": "%s"%s' % (k, v, "" if i == len(notes) - 1 else ","))
        out.append("  },")
    if rows:
        out.append('  "rows": [')
        for i, row in enumerate(rows):
            out.append('    "%s"%s' % (row, "" if i == len(rows) - 1 else ","))
        out.append("  ]")
    while out[-1].endswith(","):
        out[-1] = out[-1][:-1]
    out.append("}")
    return "\n".join(out) + "\n"


def one(root, out_root, space, templates, everything, tick=None):
    found = []
    for base, _, names in os.walk(os.path.join(root, "textures")):
        for n in sorted(names):
            if n.endswith(".png"):
                found.append(os.path.relpath(os.path.join(base, n), root).replace(os.sep, "/"))

    def seen(stage, count):
        note("%s  %s %d/%d%s" % (space, stage, count, len(found), tick() if tick else ""))

    grids = {}
    for i, rel in enumerate(found, 1):
        grids[rel] = pixels(os.path.join(root, rel.replace("/", os.sep)))
        seen("reading", i)
    tally = {rel: len({p for row in grids[rel][2] for p in row}) for rel in found}

    biggest = max((tally[rel] for rel in found), default=0)
    alphabet = tool.widened(biggest) if everything else ALPHABET
    limit = len(alphabet)

    made = {}
    taken = {os.path.basename(rel)[:-len(".png")] for rel in found}
    plan = collections.defaultdict(list)

    for base, members in families(grids, tally, seen):
        w, h, _ = grids[base]
        sig, spots = partition(grids[base])
        count = len(spots)
        if count > limit or w > MAX_SIDE or h > MAX_SIDE:
            kind = "colors" if count > limit else "size"
            detail = "%d colors" % count if count > limit else "%dx%d" % (w, h)
            for rel in members:
                plan["skipped"].append((rel, kind, detail))
            continue

        keys = keys_for(sig, count, alphabet)
        rows = ["".join(keys[i] for i in row) for row in sig]
        rank = {letter: slot for slot, letter in enumerate(alphabet)}
        listed = sorted(range(count), key=lambda i: rank[keys[i]])
        shown = {rel: [grids[rel][2][y][x] for x, y in spots] for rel in members}

        distinct = []
        seen = {}
        for rel in sorted(members, key=lambda r: (0 if r == base else 1, len(os.path.basename(r)), r)):
            token = tuple(shown[rel])
            if token in seen:
                plan["same"].append((rel, seen[token]))
                made[rel + ".json"] = document(extends="%s:%s" % (space, seen[token]))
            else:
                seen[token] = rel
                distinct.append(rel)

        if len(distinct) == 1:
            rel = distinct[0]
            made[rel + ".json"] = document((w, h), rows, [(keys[i], written(shown[rel][i])) for i in listed])
            plan["alone"].append(rel)
            continue

        distinct.sort()
        template = "%s/%s.png" % (templates, family(members, taken))
        points_at = "%s:%s" % (space, template)
        picked = [shown[rel] for rel in distinct]
        fit = gradient(picked)

        if fit:
            a, b, grays = fit
            entries = [(keys[i], written((picked[0][i] & OPAQUE) | (grays[i] << 16) | (grays[i] << 8) | grays[i]))
                       for i in listed]
            made[template + ".json"] = document((w, h), rows, entries)
            for rel in distinct:
                pal = shown[rel]
                made[rel + ".json"] = document(extends=points_at,
                                               tint=(written(pal[a] | OPAQUE), written(pal[b] | OPAQUE)))
                plan["tint"].append(rel)
        else:
            reference = picked[0]
            varying = [i for i in range(count) if len({p[i] for p in picked}) > 1]
            made[template + ".json"] = document(
                (w, h), rows,
                [(keys[i], written(reference[i])) for i in listed],
                [(keys[i], "varies by variant") for i in listed if i in varying] or None)
            for rel in distinct:
                pal = shown[rel]
                over = [(keys[i], written(pal[i])) for i in listed if pal[i] != reference[i]]
                made[rel + ".json"] = document(extends=points_at, palette=over)
                plan["override"].append(rel)

    for rel, text in sorted(made.items()):
        path = os.path.join(out_root, rel.replace("/", os.sep))
        os.makedirs(os.path.dirname(path), exist_ok=True)
        open(path, "w", encoding="utf-8", newline="\n").write(text)

    kept = {rel for rel, _, _ in plan["skipped"]}
    for rel in found:
        if rel in kept:
            continue
        old = os.path.join(out_root, rel.replace("/", os.sep))
        if os.path.exists(old):
            os.remove(old)

    covered = set(plan["tint"]) | set(plan["override"]) | set(plan["alone"]) | {r for r, _ in plan["same"]} | kept
    missing = sorted(set(found) - covered)
    if missing:
        raise SystemExit("%s: these textures got no map and were not kept as PNG: %s" % (space, ", ".join(missing[:5])))
    return plan, made


def convert(source, target, templates, everything):
    source = rooted(source)
    spaces = discover(source)
    if not spaces:
        raise SystemExit("no namespace under assets/ holds a textures folder in %s" % source)

    shutil.rmtree(target, ignore_errors=True)
    shutil.copytree(source, target)

    whole = 0
    for space in spaces:
        for _, _, names in os.walk(os.path.join(source, "assets", space, "textures")):
            whole += sum(1 for n in names if n.endswith(".png"))
    behind = [0]

    report = {}
    for space in spaces:
        tick = lambda: "   overall %d/%d" % (behind[0], whole)
        plan, made = one(os.path.join(source, "assets", space),
                         os.path.join(target, "assets", space), space, templates, everything, tick)
        behind[0] += sum(len(plan[k]) for k in ["tint", "override", "same", "alone", "skipped"])
        report[space] = (plan, made)
    hush()
    return report


WHY = {
    "colors": (
        "Too many colors for the alphabet, which holds %d characters." % LIMIT,
        "A pixel map names every color with a single character, so it cannot hold",
        "more colors than there are characters to give them. What lands here is",
        "photographs, gradients, colormaps and entity skins rather than pixel art.",
        "As maps they would be larger than the PNGs they replace, often several",
        "times over, since a PNG compresses and a map does not, and their rows",
        "would be unreadable. Leaving them as images is the right answer, and the",
        "mod goes on serving them exactly as it did before.",
    ),
    "size": (
        "Larger than %d pixels a side." % MAX_SIDE,
        "The mod refuses to draw a map bigger than that, so converting one would",
        "leave you with a texture the game will not load.",
    ),
}


def explain(report):
    skipped = [(space, rel, kind, detail)
               for space in sorted(report)
               for rel, kind, detail in report[space][0]["skipped"]]
    if not skipped:
        return

    print()
    print("%d texture(s) were kept as PNG rather than converted. This is expected," % len(skipped))
    print("not a failure: a pack may hold maps and images side by side.")
    for kind in ["colors", "size"]:
        listed = [(space, rel, detail) for space, rel, k, detail in skipped if k == kind]
        if not listed:
            continue
        print()
        for line in WHY[kind]:
            print("  " + line)
        print()
        for space, rel, detail in sorted(listed):
            print("    %-14s %-16s %s" % (detail, space, rel))


def main(argv=None):
    parser = argparse.ArgumentParser(description="Turn every namespace's PNG textures into pixel maps.")
    parser.add_argument("source", help="pack folder to read")
    parser.add_argument("target", help="folder to write the converted pack into, replaced if it exists")
    parser.add_argument("--templates", type=folder, default="textures/templates",
                        help="where shared templates are written, relative to each namespace")
    parser.add_argument("--override-skips", action="store_true", dest="everything",
                        help="convert textures that hold more colors than the alphabet, by widening it. "
                             "The maps come out larger than the PNGs they replace, sometimes several times over")
    args = parser.parse_args(argv)

    if os.path.abspath(args.source) == os.path.abspath(args.target):
        raise SystemExit("the source and the target must be different folders")

    if args.everything:
        print("--override-skips is on. Textures past the alphabet will be converted, and their")
        print("maps will be larger than the PNGs they replace, sometimes several times over.")
    scratch = tempfile.mkdtemp(prefix="pixelmap-")
    packing = args.target.lower().endswith(".zip")
    written = os.path.join(scratch, "out") if packing else args.target
    try:
        report = convert(unpacked(args.source, os.path.join(scratch, "in")), written,
                         args.templates, args.everything)
        if packing:
            note("writing %s" % os.path.basename(args.target))
            zipped(written, args.target)
            hush()
    finally:
        shutil.rmtree(scratch, ignore_errors=True)
    for space in sorted(report):
        plan, made = report[space]
        counts = " ".join("%s %d" % (k, len(plan[k])) for k in ["tint", "override", "same", "alone", "skipped"])
        print("%-24s %4d maps   %s" % (space, len(made), counts))
    explain(report)
    return 0


if __name__ == "__main__":
    sys.exit(main())
