package mctmods.resourcedatapackloader.pack.port;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

final class CommandSelectors {
    private static final Pattern SELECTOR = Pattern.compile("^@([pares])(?:\\[([^ ]*)])?$");
    private static final Pattern PLAIN = Pattern.compile("^[0-9A-Za-z_\\-.+]+$");

    private CommandSelectors() {}

    static String target(String token, Ported pack) {
        Matcher m = SELECTOR.matcher(token);
        if (!m.matches()) { return token; }
        String kind = m.group(1);
        Map<String, String> given = m.group(2) != null && !m.group(2).isEmpty() ? Commands.pairs(m.group(2), part -> "the selector " + token + " holds '" + part + "', which is not an argument") : new LinkedHashMap<>();
        List<String> out = new ArrayList<>();
        String type = given.remove("type");
        if (type != null) { out.add("type=" + negated(type, id -> Commands.entity(id, pack))); }
        for (String key : new String[] {"name", "team", "tag"}) {
            String value = given.remove(key);
            if (value == null) { continue; }
            out.add(key + "=" + negated(value, bare -> "name".equals(key) ? quoted(bare) : bare));
        }
        range(out, "distance", given.remove("rm"), given.remove("r"));
        range(out, "level", given.remove("lm"), given.remove("l"));
        range(out, "x_rotation", given.remove("rxm"), given.remove("rx"));
        range(out, "y_rotation", given.remove("rym"), given.remove("ry"));
        String mode = given.remove("m");
        if (mode != null) { out.add("gamemode=" + negated(mode, name -> Commands.named(Commands.GAME_MODES, name, "game mode"))); }
        for (String key : new String[] {"x", "y", "z", "dx", "dy", "dz"}) {
            String value = given.remove(key);
            if (value != null) { out.add(key + "=" + ("y".equals(key) ? Commands.height(Commands.numberText(value), pack) : Commands.numberText(value))); }
        }
        Map<String, String[]> scores = new LinkedHashMap<>();
        for (String key : new ArrayList<>(given.keySet())) {
            if (!key.startsWith("score_") || key.length() == "score_".length()) { continue; }
            String objective = key.substring("score_".length());
            boolean least = objective.endsWith("_min") && objective.length() > "_min".length();
            if (least) { objective = objective.substring(0, objective.length() - "_min".length()); }
            scores.computeIfAbsent(objective, k -> new String[2])[least ? 0 : 1] = Commands.numberText(given.remove(key));
        }
        if (!scores.isEmpty()) {
            List<String> each = new ArrayList<>();
            scores.forEach((objective, bounds) -> each.add(objective + "=" + bounds(bounds[0], bounds[1])));
            out.add("scores={" + String.join(",", each) + "}");
        }
        String count = given.remove("c");
        if (!given.isEmpty()) { throw new Commands.Kept("the selector " + token + " holds " + given.keySet() + ", which this version has no argument for"); }
        kind = limited(kind, type != null, count == null ? null : Commands.number(count), out);
        return "@" + kind + (out.isEmpty() ? "" : "[" + String.join(",", out) + "]");
    }

    static String negated(String value, UnaryOperator<String> shape) { return value.startsWith("!") ? "!" + shape.apply(value.substring(1)) : shape.apply(value); }

    private static String limited(String kind, boolean typed, @Nullable Integer count, List<String> out) {
        if ("s".equals(kind)) { return kind; }
        boolean single = "p".equals(kind) || "r".equals(kind);
        boolean random = "r".equals(kind);
        int limit = count == null ? single ? 1 : 0 : count;
        boolean ownLimit = !typed && single && Math.abs(limit) == 1;
        boolean ownSort = !typed && single && (random ? limit != 0 : limit > 0);
        if (limit != 0 && !ownLimit) { out.add("limit=" + Math.abs(limit)); }
        if ((limit != 0 || single) && !ownSort) { out.add("sort=" + (random ? "random" : limit < 0 ? "furthest" : "nearest")); }
        if (typed) { return "e"; }
        return single && limit == 0 ? "a" : kind;
    }

    private static void range(List<String> out, String key, @Nullable String least, @Nullable String most) {
        if (least == null && most == null) { return; }
        out.add(key + "=" + bounds(least == null ? null : Commands.numberText(least), most == null ? null : Commands.numberText(most)));
    }

    static String bounds(@Nullable String least, @Nullable String most) {
        if (least != null && least.equals(most)) { return least; }
        return (least == null ? "" : least) + ".." + (most == null ? "" : most);
    }

    private static String quoted(String name) { return PLAIN.matcher(name).matches() ? name : Commands.quote(name); }

    static String narrowed(String target, String argument) {
        if (!target.startsWith("@")) { return "@a[name=" + quoted(target) + "," + argument + "]"; }
        if (target.endsWith("]")) { return target.substring(0, target.length() - 1) + "," + argument + "]"; }
        return target + "[" + argument + "]";
    }
}
