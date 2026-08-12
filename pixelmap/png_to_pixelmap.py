#!/usr/bin/env python3

import argparse
import json
import math
import os
import struct
import sys
import unicodedata
import zlib

SIGNATURE = b"\x89PNG\r\n\x1a\n"
MAX_SIDE = 4096
HOLE = "."
SUFFIX = ".json"
ASCII = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!#$%&\'()*+,-/:;<=>?@[]^_`{|}~"
LATIN = [chr(c) for c in range(0xA1, 0x100) if c != 0xAD]
GREEK = [chr(c) for c in range(0x391, 0x3CA) if chr(c) not in "\u0391\u0392\u0395\u0396\u0397\u0399\u039A\u039C\u039D\u039F\u03A1\u03A4\u03A5\u03A7\u03BF\u03B9\u03BD\u03B1\u03C1\u03BA\u03C5\u03C7" and unicodedata.category(chr(c)).startswith("L")]
CYRILLIC = [chr(c) for c in range(0x400, 0x460) if chr(c) not in "\u0410\u0412\u0415\u041A\u041C\u041D\u041E\u0420\u0421\u0422\u0423\u0425\u0430\u0435\u043E\u0440\u0441\u0443\u0445\u0456\u0458\u0455\u0450\u0451" and unicodedata.category(chr(c)).startswith("L")]
SHAPES = [chr(c) for c in range(0x2500, 0x25A0)]
ALPHABET = ASCII + "".join(LATIN + GREEK + CYRILLIC + SHAPES)


def widened(wanted):
    if wanted <= len(ALPHABET):
        return ALPHABET

    held = list(ALPHABET)
    seen = set(held)
    for point in range(0x20, 0x10000):
        if len(held) >= wanted:
            break
        if 0xD800 <= point <= 0xDFFF:
            continue
        letter = chr(point)
        if letter in seen or letter in HOLE or letter in '"\\':
            continue
        if unicodedata.category(letter) in ("Cc", "Cf", "Cn", "Co", "Cs", "Mn", "Mc", "Me", "Zs", "Zl", "Zp"):
            continue
        if unicodedata.bidirectional(letter) in ("R", "AL", "AN"):
            continue
        held.append(letter)
        seen.add(letter)
    return "".join(held)
CHANNELS = {0: 1, 2: 3, 3: 1, 4: 2, 6: 4}
ADAM7 = [(0, 0, 8, 8), (4, 0, 8, 8), (0, 4, 4, 8), (2, 0, 4, 4), (0, 2, 2, 4), (1, 0, 2, 2), (0, 1, 1, 2)]


class PngError(Exception):
    pass


def chunks(data):
    if data[:8] != SIGNATURE:
        raise PngError("this is not a PNG file")
    at = 8
    while at + 8 <= len(data):
        length = struct.unpack(">I", data[at:at + 4])[0]
        kind = data[at + 4:at + 8]
        body = data[at + 8:at + 8 + length]
        if len(body) != length:
            raise PngError("the file ends inside a chunk")
        yield kind, body
        at += 12 + length


def unfilter(raw, width, height, bpp, stride):
    out = bytearray(height * stride)
    previous = bytearray(stride)
    at = 0
    for y in range(height):
        if at >= len(raw):
            raise PngError("the image data ends early")
        kind = raw[at]
        at += 1
        line = bytearray(raw[at:at + stride])
        if len(line) != stride:
            raise PngError("the image data ends early")
        at += stride
        if kind == 1:
            for i in range(bpp, stride):
                line[i] = (line[i] + line[i - bpp]) & 0xFF
        elif kind == 2:
            for i in range(stride):
                line[i] = (line[i] + previous[i]) & 0xFF
        elif kind == 3:
            for i in range(stride):
                left = line[i - bpp] if i >= bpp else 0
                line[i] = (line[i] + ((left + previous[i]) >> 1)) & 0xFF
        elif kind == 4:
            for i in range(stride):
                left = line[i - bpp] if i >= bpp else 0
                up = previous[i]
                corner = previous[i - bpp] if i >= bpp else 0
                guess = left + up - corner
                dl = abs(guess - left)
                du = abs(guess - up)
                dc = abs(guess - corner)
                if dl <= du and dl <= dc:
                    line[i] = (line[i] + left) & 0xFF
                elif du <= dc:
                    line[i] = (line[i] + up) & 0xFF
                else:
                    line[i] = (line[i] + corner) & 0xFF
        elif kind != 0:
            raise PngError("row " + str(y + 1) + " uses filter " + str(kind) + ", which is not a PNG filter")
        out[y * stride:(y + 1) * stride] = line
        previous = line
    return out, at


def samples(line, width, count, depth):
    if depth == 8:
        return list(line[:width * count])
    if depth == 16:
        return [line[i * 2] for i in range(width * count)]
    per = 8 // depth
    mask = (1 << depth) - 1
    out = []
    wanted = width * count
    for i in range(wanted):
        byte = line[i // per]
        shift = 8 - depth * (i % per + 1)
        out.append((byte >> shift) & mask)
    return out


def pixels(line, width, color, depth, plte, trns):
    count = CHANNELS[color]
    values = samples(line, width, count, depth)
    out = []
    if color == 3:
        for i in range(width):
            index = values[i]
            if index * 3 + 2 >= len(plte):
                raise PngError("a pixel names palette entry " + str(index) + ", which the file does not hold")
            alpha = trns[index] if index < len(trns) else 255
            out.append((plte[index * 3], plte[index * 3 + 1], plte[index * 3 + 2], alpha))
        return out
    top = (1 << depth) - 1 if depth < 8 else 255
    for i in range(width):
        if color == 0:
            gray = values[i] * 255 // top if depth < 8 else values[i]
            alpha = 0 if trns is not None and values[i] == trns[0] else 255
            out.append((gray, gray, gray, alpha))
        elif color == 4:
            out.append((values[i * 2], values[i * 2], values[i * 2], values[i * 2 + 1]))
        elif color == 2:
            r, g, b = values[i * 3], values[i * 3 + 1], values[i * 3 + 2]
            alpha = 0 if trns is not None and (r, g, b) == (trns[0], trns[1], trns[2]) else 255
            out.append((r, g, b, alpha))
        else:
            out.append((values[i * 4], values[i * 4 + 1], values[i * 4 + 2], values[i * 4 + 3]))
    return out


def pass_rows(raw, at, width, height, color, depth, plte, trns):
    if width == 0 or height == 0:
        return [], at
    count = CHANNELS[color]
    bits = count * depth
    stride = (width * bits + 7) // 8
    bpp = max(1, bits // 8)
    lines, used = unfilter(raw[at:], width, height, bpp, stride)
    rows = [pixels(lines[y * stride:(y + 1) * stride], width, color, depth, plte, trns) for y in range(height)]
    return rows, at + used


def decode(data):
    header = None
    plte = b""
    trns = None
    body = bytearray()
    for kind, chunk in chunks(data):
        if kind == b"IHDR":
            header = struct.unpack(">IIBBBBB", chunk)
        elif kind == b"PLTE":
            plte = chunk
        elif kind == b"tRNS":
            trns = chunk
        elif kind == b"IDAT":
            body += chunk
        elif kind == b"IEND":
            break
    if header is None:
        raise PngError("the file holds no header")

    width, height, depth, color, compression, filtering, interlace = header
    if compression != 0 or filtering != 0:
        raise PngError("the file uses a compression or filter method this script does not know")
    if color not in CHANNELS:
        raise PngError("the file uses color type " + str(color) + ", which is not a PNG color type")
    if depth not in (1, 2, 4, 8, 16) or (color != 0 and color != 3 and depth < 8) or (color == 3 and depth == 16):
        raise PngError("the file uses " + str(depth) + " bits a sample with color type " + str(color) + ", which is not a PNG combination")
    if width < 1 or height < 1:
        raise PngError("the file says it is " + str(width) + " by " + str(height))

    if color == 3:
        alphas = list(trns) if trns is not None else []
        trns = alphas + [255] * (len(plte) // 3 - len(alphas))
    elif trns is not None:
        values = [struct.unpack(">H", trns[i * 2:i * 2 + 2])[0] for i in range(len(trns) // 2)]
        trns = [v >> 8 if depth == 16 else v for v in values]

    raw = zlib.decompress(bytes(body))
    if interlace == 0:
        rows, _ = pass_rows(raw, 0, width, height, color, depth, plte, trns)
        return width, height, rows
    if interlace != 1:
        raise PngError("the file uses interlace method " + str(interlace) + ", which is not a PNG method")

    grid = [[(0, 0, 0, 0)] * width for _ in range(height)]
    at = 0
    for startx, starty, stepx, stepy in ADAM7:
        wide = (width - startx + stepx - 1) // stepx
        tall = (height - starty + stepy - 1) // stepy
        rows, at = pass_rows(raw, at, wide, tall, color, depth, plte, trns)
        for y, row in enumerate(rows):
            for x, pixel in enumerate(row):
                grid[starty + y * stepy][startx + x * stepx] = pixel
    return width, height, grid


def written(color):
    r, g, b, a = color
    if a == 255:
        return "#%02X%02X%02X" % (r, g, b)
    return "#%02X%02X%02X%02X" % (a, r, g, b)


def mapped(width, height, grid):
    order = []
    counts = {}
    for row in grid:
        for pixel in row:
            if pixel == (0, 0, 0, 0):
                continue
            if pixel not in counts:
                counts[pixel] = 0
                order.append(pixel)
            counts[pixel] += 1
    seen = {pixel: i for i, pixel in enumerate(order)}
    order.sort(key=lambda pixel: (-counts[pixel], seen[pixel]))
    if len(order) > len(ALPHABET):
        raise PngError("the image holds " + str(len(order)) + " colors and a pixel map has only " + str(len(ALPHABET)) + " characters to give them")

    keys = {pixel: ALPHABET[i] for i, pixel in enumerate(order)}
    rows = ["".join(keys.get(pixel, HOLE) for pixel in row) for row in grid]
    palette = [(keys[pixel], written(pixel)) for pixel in order]
    return rows, palette


def document(width, height, rows, palette):
    out = ["{"]
    out.append('  "size": "%dx%d",' % (width, height))
    if len(palette) > 24:
        out.append('  "palette": {')
        for i, entry in enumerate(palette):
            out.append('    "%s": "%s"%s' % (entry[0], entry[1], "" if i == len(palette) - 1 else ","))
        out.append("  },")
    else:
        inside = ", ".join('"%s": "%s"' % entry for entry in palette)
        out.append('  "palette": ' + ("{ " + inside + " }" if inside else "{}") + ",")
    out.append('  "rows": [')
    for i, row in enumerate(rows):
        out.append('    "%s"%s' % (row, "" if i == len(rows) - 1 else ","))
    out.append("  ]")
    out.append("}")
    return "\n".join(out) + "\n"


def guard(source, data):
    if data[:2] == b"PK":
        raise PngError(os.path.basename(source) + " is a zip, not a texture. To convert a whole pack use "
                       "convert_pack.py, which takes a pack folder or zip and writes maps for every texture in it")


def named(path):
    parts = os.path.abspath(path).replace(os.sep, "/").split("/")
    if "assets" not in parts:
        raise PngError("the template must sit under an assets folder so its name can be worked out, and " + path + " does not")

    at = len(parts) - 1 - parts[::-1].index("assets")
    if at + 2 >= len(parts):
        raise PngError("there is no namespace folder under assets in " + path)

    space = parts[at + 1]
    inside = "/".join(parts[at + 2:])
    if not inside.endswith(SUFFIX):
        raise PngError("a template is a pixel map, so it should end in .json, and " + path + " does not")

    return "/".join(parts[:at]), space, inside[:-len(SUFFIX)]


def inherited(root, space, resource):
    palette = {}
    rows = None
    size = None
    seen = set()
    where, at = space, resource + SUFFIX
    while at is not None:
        if (where, at) in seen:
            raise PngError("the template chain reaches " + where + ":" + at + " twice")
        seen.add((where, at))
        full = os.path.join(root, "assets", where, at.replace("/", os.sep))
        if not os.path.isfile(full):
            raise PngError("the template needs " + where + ":" + at + ", which is not there")

        held = json.load(open(full, encoding="utf-8"))
        for key, value in held.get("palette", {}).items():
            palette.setdefault(key, value)
        if rows is None and "rows" in held:
            rows = list(held["rows"])
        if size is None and "size" in held:
            wide, tall = held["size"].split("x")
            size = (int(wide), int(tall))
        if "tint" in held:
            raise PngError("the template already carries a tint, so a child of it cannot be worked out")

        nxt = held.get("extends", "")
        if not nxt:
            break
        colon = nxt.find(":")
        where = where if colon < 0 else nxt[:colon]
        at = (nxt if colon < 0 else nxt[colon + 1:]) + SUFFIX

    if size is None or not rows:
        raise PngError("the template gives no size or no rows")
    return size, rows, palette


def ramp(level, frm, to, shift):
    a = (frm >> shift) & 0xFF
    b = (to >> shift) & 0xFF
    return max(0, min(255, int(math.floor(a + level * (b - a) / 255.0 + 0.5))))


def shade(color, frm, to):
    level = (((color >> 16) & 0xFF) + ((color >> 8) & 0xFF) + (color & 0xFF)) // 3
    return (color & 0xFF000000) | (ramp(level, frm, to, 16) << 16) | (ramp(level, frm, to, 8) << 8) | ramp(level, frm, to, 0)


def parsed(text):
    t = text.strip()
    if t.startswith("#"):
        t = t[1:]
    if t[:2] in ("0x", "0X"):
        t = t[2:]
    if len(t) not in (6, 8):
        return None
    value = int(t, 16)
    return (value | 0xFF000000) & 0xFFFFFFFF if len(t) == 6 else value


def packed(pixel):
    return ((pixel[3] & 0xFF) << 24) | ((pixel[0] & 0xFF) << 16) | ((pixel[1] & 0xFF) << 8) | pixel[2]


def child(source, target, template):
    root, space, resource = named(template)
    size, rows, base = inherited(root, space, resource)

    width, height, grid = decode(open(source, "rb").read())
    if (width, height) != size:
        raise PngError("the image is %dx%d and the template is %dx%d" % (width, height, size[0], size[1]))
    if len(rows) != height or any(len(r) != width for r in rows):
        raise PngError("the template's rows do not match its own size")

    wanted = {}
    clashes = 0
    for y in range(height):
        for x in range(width):
            key = rows[y][x]
            here = packed(grid[y][x])
            if key in wanted and wanted[key] != here:
                clashes += 1
            wanted.setdefault(key, here)
    if clashes:
        raise PngError("the template is coarser than the image: %d pixel(s) share a character but not a color, "
                       "so no child of it can draw this image" % clashes)

    keys = sorted(wanted, key=lambda k: ALPHABET.index(k) if k in ALPHABET else len(ALPHABET))
    lit = {k: parsed(v) for k, v in base.items() if parsed(v) is not None}
    lum = lambda k: 0.299 * ((lit[k] >> 16) & 0xFF) + 0.587 * ((lit[k] >> 8) & 0xFF) + 0.114 * (lit[k] & 0xFF)
    ramped = [k for k in keys if k in lit]

    tint = None
    if len(ramped) > 1:
        low = min(ramped, key=lum)
        high = max(ramped, key=lum)
        frm, to = wanted[low] | 0xFF000000, wanted[high] | 0xFF000000
        if all(shade(lit[k], frm, to) == wanted[k] for k in ramped):
            tint = (written(unpacked(frm)), written(unpacked(to)))

    out = ["{", '  "extends": "%s:%s",' % (space, resource)]
    if tint:
        out.append('  "tint": { "from": "%s", "to": "%s" }' % tint)
        kept = 0
    else:
        over = [(k, written(unpacked(wanted[k]))) for k in keys if k not in lit or lit[k] != wanted[k]]
        kept = len(over)
        if over:
            if len(over) > 24:
                out.append('  "palette": {')
                for i, (k, v) in enumerate(over):
                    out.append('    "%s": "%s"%s' % (k, v, "" if i == len(over) - 1 else ","))
                out.append("  }")
            else:
                out.append('  "palette": { ' + ", ".join('"%s": "%s"' % e for e in over) + " }")
    while out[-1].endswith(","):
        out[-1] = out[-1][:-1]
    out.append("}")

    folder = os.path.dirname(os.path.abspath(target))
    if folder:
        os.makedirs(folder, exist_ok=True)
    with open(target, "w", encoding="utf-8", newline="\n") as held:
        held.write("\n".join(out) + "\n")
    return width, height, ("a tint" if tint else "%d palette entry(s)" % kept)


def unpacked(value):
    return ((value >> 16) & 0xFF, (value >> 8) & 0xFF, value & 0xFF, (value >> 24) & 0xFF)


def convert(source, target):
    with open(source, "rb") as held:
        data = held.read()
    guard(source, data)
    width, height, grid = decode(data)
    if width > MAX_SIDE or height > MAX_SIDE:
        raise PngError("the image is " + str(width) + " by " + str(height) + " and a pixel map may be at most " + str(MAX_SIDE) + " a side")

    rows, palette = mapped(width, height, grid)
    text = document(width, height, rows, palette)
    folder = os.path.dirname(os.path.abspath(target))
    if folder:
        os.makedirs(folder, exist_ok=True)
    with open(target, "w", encoding="utf-8", newline="\n") as held:
        held.write(text)
    return width, height, len(palette)


def main(argv=None):
    parser = argparse.ArgumentParser(description="Turn one PNG into a pixel map.")
    parser.add_argument("source", help="the PNG to read")
    parser.add_argument("target", nargs="?", help="where to write the map, <source>.json by default")
    parser.add_argument("--extends", metavar="TEMPLATE",
                        help="a template pixel map to build on, given as the path to its .json file. "
                             "The map written is a child of it, carrying only a tint or the colors that differ")
    args = parser.parse_args(argv[1:] if argv else None)

    target = args.target or args.source + SUFFIX
    try:
        if args.extends:
            width, height, held = child(args.source, target, args.extends)
        else:
            width, height, colors = convert(args.source, target)
            held = "%d color(s)" % colors
    except (PngError, zlib.error, ValueError) as ex:
        print(os.path.basename(args.source) + " was not turned into a pixel map: " + str(ex), file=sys.stderr)
        return 1
    except OSError as ex:
        print(str(ex), file=sys.stderr)
        return 1

    print("%s is %dx%d with %s" % (target, width, height, held))
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv))
