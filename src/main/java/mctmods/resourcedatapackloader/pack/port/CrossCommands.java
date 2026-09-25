package mctmods.resourcedatapackloader.pack.port;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class CrossCommands {
    private static final Set<String> NEW_COMMANDS = Set.of("random", "tick", "transfer", "return");
    private static final Pattern NAMESPACED = Pattern.compile("(?<![a-z0-9_.#/-])((?:minecraft|forge|neoforge):[a-z0-9_./-]+)");
    private static final Pattern TAG = Pattern.compile("#((?:forge|c|neoforge|minecraft):[a-z0-9_./-]+)");
    private static final Pattern SELECTOR_NBT = Pattern.compile("nbt=!?\\{");
    private static final String RUN = " run ";
    private static final String TAGGED = "minecraft:stick";

    private CrossCommands() {}

    static String function(String contents, String file, Crossed pack) {
        String[] lines = contents.split("\r?\n", -1);
        StringBuilder out = new StringBuilder();
        int changed = 0;
        int kept = 0;
        for (int i = 0; i < lines.length; i++) {
            if (i > 0) { out.append('\n'); }
            String line = lines[i].trim();
            if (line.isEmpty() || line.startsWith("#")) {
                out.append(lines[i]);
                continue;
            }
            try {
                if (line.startsWith("$") && pack.to() == Port.Line.V1_20) { throw new Commands.Kept("function macros are new in 1.20.2"); }
                String converted = command(line, file, pack);
                out.append(converted);
                if (!converted.equals(line)) { changed++; }
            }
            catch (Commands.Kept why) {
                kept++;
                out.append(lines[i]);
                pack.note("'" + file + "' line " + (i + 1) + " is kept as written, since " + why.getMessage() + ": " + line);
            }
        }
        if (changed > 0 || kept > 0) { pack.note("'" + file + "' is a " + pack.origin() + " function: " + changed + " command(s) rewritten for " + pack.to().title() + ", " + kept + " kept as written"); }
        return changed == 0 ? contents : out.toString();
    }

    private static String command(String line, String file, Crossed pack) {
        boolean down = pack.to() == Port.Line.V1_20;
        String converted = selectors(line, pack);
        List<String> args = split(converted, ' ');
        String head = converted.split(" ", 2)[0];
        if (down && NEW_COMMANDS.contains(head)) { throw new Commands.Kept(head + " is new after 1.20.1"); }
        if ("execute".equals(head)) {
            int run = converted.indexOf(RUN);
            String chain = run < 0 ? converted : converted.substring(0, run);
            if (down && (chain.contains(" items ") || chain.contains(" if function ") || chain.contains(" unless function "))) { throw new Commands.Kept("its execute test is new after 1.20.1"); }
            return run < 0 ? ids(chain, file, pack) : ids(chain, file, pack) + RUN + command(converted.substring(run + RUN.length()), file, pack);
        }
        switch (head) {
            case "give", "clear" -> stackAt(args, 2, pack);
            case "item" -> stackAt(args, args.indexOf("with") + 1, pack);
            case "summon", "data" -> nbtTokens(args, pack);
            case "setblock" -> blockAt(args, 4, pack);
            case "fill" -> blockAt(args, 7, pack);
            case "particle" -> particle(args, pack);
            case "function" -> { if (down && args.size() > 2) { throw new Commands.Kept("function arguments are new after 1.20.1"); } }
            default -> { }
        }
        return ids(String.join(" ", args), file, pack);
    }

    static List<String> split(String text, char separator) {
        List<String> out = new ArrayList<>();
        int depth = 0;
        char quote = 0;
        int start = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (quote != 0) {
                if (c == '\\') { i++; }
                else if (c == quote) { quote = 0; }
                continue;
            }
            if (c == '"' || c == '\'') { quote = c; }
            else if (c == '[' || c == '{' || c == '(') { depth++; }
            else if (c == ']' || c == '}' || c == ')') { depth--; }
            else if (c == separator && depth == 0) {
                out.add(text.substring(start, i));
                start = i + 1;
            }
        }
        out.add(text.substring(start));
        return out;
    }

    private static int close(String text, int open) {
        int depth = 0;
        char quote = 0;
        for (int i = open; i < text.length(); i++) {
            char c = text.charAt(i);
            if (quote != 0) {
                if (c == '\\') { i++; }
                else if (c == quote) { quote = 0; }
                continue;
            }
            if (c == '"' || c == '\'') { quote = c; }
            else if (c == '{' || c == '[') { depth++; }
            else if ((c == '}' || c == ']') && --depth == 0) { return i; }
        }
        throw new Commands.Kept("a bracket in it is not closed");
    }

    private static String selectors(String line, Crossed pack) {
        Matcher matcher = SELECTOR_NBT.matcher(line);
        StringBuilder out = new StringBuilder();
        int from = 0;
        while (matcher.find(from)) {
            int open = matcher.end() - 1;
            int end = close(line, open);
            out.append(line, from, open).append(nbt(line.substring(open, end + 1), pack));
            from = end + 1;
        }
        return out.append(line.substring(from)).toString();
    }

    private static String nbt(String snbt, Crossed pack) {
        Port.Line source = pack.to() == Port.Line.V1_21 ? Port.Line.V1_20 : Port.Line.V1_21;
        CompoundTag parsed = CrossStacks.parse(snbt);
        if (!CrossStacks.stacks(parsed, source)) { return snbt; }
        List<String> lost = new ArrayList<>();
        Tag walked = CrossStacks.walk(parsed, pack.to(), lost);
        if (!lost.isEmpty()) { throw new Commands.Kept("its item component(s) " + String.join(", ", lost) + " have no 1.20.1 NBT form"); }
        return walked.toString();
    }

    private static void nbtTokens(List<String> args, Crossed pack) {
        for (int i = 1; i < args.size(); i++) {
            if (args.get(i).startsWith("{")) { args.set(i, nbt(args.get(i), pack)); }
        }
    }

    private static void stackAt(List<String> args, int index, Crossed pack) {
        if (index <= 0 || index >= args.size()) { return; }
        String token = args.get(index);
        int brace = token.indexOf(pack.to() == Port.Line.V1_21 ? '{' : '[');
        String id = brace < 0 ? token : token.substring(0, brace);
        String rest = brace < 0 ? null : token.substring(brace);
        if (id.startsWith("#")) {
            String tag = "#" + CrossIds.tag(CrossIds.ITEM_REGISTRY, namespaced(id.substring(1)), pack.to());
            args.set(index, rest == null ? tag : tag + stack(TAGGED, rest, pack).substring(TAGGED.length()));
            return;
        }
        String named = CrossIds.id(namespaced(id), pack.to());
        if (rest == null) {
            if (!named.equals(namespaced(id))) { args.set(index, named); }
            return;
        }
        args.set(index, stack(named, rest, pack));
    }

    private static String stack(String named, String rest, Crossed pack) {
        if (pack.to() == Port.Line.V1_21) { return CrossStacks.upCommand(named, rest); }
        List<String> lost = new ArrayList<>();
        String converted = CrossStacks.downCommand(named, rest, lost);
        if (!lost.isEmpty()) { throw new Commands.Kept("its item component(s) " + String.join(", ", lost) + " have no 1.20.1 NBT form"); }
        return converted;
    }

    private static void blockAt(List<String> args, int index, Crossed pack) {
        if (index >= args.size()) { return; }
        String token = args.get(index);
        int brace = token.indexOf('{');
        if (brace < 0) { return; }
        args.set(index, token.substring(0, brace) + nbt(token.substring(brace), pack));
    }

    private static String namespaced(String id) { return id.indexOf(':') < 0 ? CrossIds.MINECRAFT + id : id; }

    private static void particle(List<String> args, Crossed pack) {
        if (args.size() < 2) { return; }
        String token = args.get(1);
        if (pack.to() == Port.Line.V1_21) {
            String name = namespaced(token);
            String bare = name.substring(name.indexOf(':') + 1);
            switch (bare) {
                case "dust" -> options(args, name, 4, values -> "{color:[" + values[0] + "f," + values[1] + "f," + values[2] + "f],scale:" + values[3] + "f}");
                case "dust_color_transition" -> options(args, name, 7, values -> "{from_color:[" + values[0] + "f," + values[1] + "f," + values[2] + "f],scale:" + values[3] + "f,to_color:[" + values[4] + "f," + values[5] + "f," + values[6] + "f]}");
                case "item" -> options(args, name, 1, values -> "{item:\"" + namespaced(values[0].split("[{\\[]", 2)[0]) + "\"}");
                case "block", "falling_dust", "block_marker" -> options(args, name, 1, values -> "{block_state:" + blockState(values[0]) + "}");
                case "sculk_charge" -> options(args, name, 1, values -> "{roll:" + values[0] + "f}");
                case "shriek" -> options(args, name, 1, values -> "{delay:" + values[0] + "}");
                case "entity_effect" -> args.set(1, name + "{color:-1}");
                case "vibration" -> throw new Commands.Kept("its vibration particle names a target differently on 1.21.1");
                default -> { }
            }
            return;
        }
        int brace = token.indexOf('{');
        if (brace < 0) { return; }
        String name = token.substring(0, brace);
        CompoundTag options = CrossStacks.parse(token.substring(brace));
        String bare = name.substring(name.indexOf(':') + 1);
        List<String> out = switch (bare) {
            case "dust" -> join(floats(options.get("color")), List.of(number(options.get("scale"))));
            case "dust_color_transition" -> join(join(floats(options.get("from_color")), List.of(number(options.get("scale")))), floats(options.get("to_color")));
            case "item" -> List.of(options.get("item") instanceof CompoundTag stack ? stack.getString("id") : options.getString("item"));
            case "block", "falling_dust", "block_marker" -> List.of(blockString(options.get("block_state")));
            case "sculk_charge" -> List.of(number(options.get("roll")));
            case "shriek" -> List.of(number(options.get("delay")));
            case "entity_effect" -> List.of();
            default -> throw new Commands.Kept("its particle options have no 1.20.1 form");
        };
        args.set(1, name);
        args.addAll(2, out);
    }

    private interface Options { String of(String[] values); }

    private static void options(List<String> args, String name, int count, Options made) {
        if (args.size() < 2 + count) { throw new Commands.Kept("its particle has fewer options than it needs"); }
        String[] values = args.subList(2, 2 + count).toArray(new String[0]);
        args.subList(2, 2 + count).clear();
        args.set(1, name + made.of(values));
    }

    private static String blockState(String written) {
        int open = written.indexOf('[');
        String name = namespaced(open < 0 ? written : written.substring(0, open));
        if (open < 0) { return "{Name:\"" + name + "\"}"; }
        List<String> properties = new ArrayList<>();
        for (String pair : written.substring(open + 1, written.length() - 1).split(",")) {
            String[] parts = pair.split("=", 2);
            if (parts.length == 2) { properties.add(parts[0].trim() + ":\"" + parts[1].trim() + "\""); }
        }
        return "{Name:\"" + name + "\",Properties:{" + String.join(",", properties) + "}}";
    }

    private static String blockString(Tag state) {
        if (!(state instanceof CompoundTag held)) { return state == null ? "minecraft:air" : state.getAsString(); }
        CompoundTag properties = held.getCompound("Properties");
        List<String> pairs = new ArrayList<>();
        for (String key : properties.getAllKeys()) { pairs.add(key + "=" + properties.getString(key)); }
        return held.getString("Name") + (pairs.isEmpty() ? "" : "[" + String.join(",", pairs) + "]");
    }

    private static List<String> floats(Tag list) {
        List<String> out = new ArrayList<>();
        if (list instanceof ListTag held) { held.forEach(value -> out.add(number(value))); }
        return out;
    }

    private static String number(Tag value) { return value == null ? "0" : value.getAsString().replaceAll("[fFdDbBsSlL]$", ""); }

    private static List<String> join(List<String> first, List<String> second) {
        List<String> out = new ArrayList<>(first);
        out.addAll(second);
        return out;
    }

    private static String ids(String line, String file, Crossed pack) {
        Matcher matcher = NAMESPACED.matcher(line);
        StringBuilder out = new StringBuilder();
        while (matcher.find()) { matcher.appendReplacement(out, Matcher.quoteReplacement(CrossJson.id(matcher.group(1), file, pack))); }
        Matcher tags = TAG.matcher(matcher.appendTail(out).toString());
        StringBuilder tagged = new StringBuilder();
        while (tags.find()) { tags.appendReplacement(tagged, Matcher.quoteReplacement("#" + CrossJson.tag(tags.group(1), null, file, pack))); }
        return tags.appendTail(tagged).toString();
    }
}
