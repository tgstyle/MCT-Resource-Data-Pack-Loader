package mctmods.resourcedatapackloader.pack.port;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class CommandSelectors {
    private static final Pattern SELECTOR = Pattern.compile("^@([parse])(?:\\[(.*)])?$");
    static final Pattern RANGE = Pattern.compile("^(-?[0-9.]*)\\.\\.(-?[0-9.]*)$");
    private static final Map<String, String> GAME_MODES = new HashMap<>();

    static {
        GAME_MODES.put("survival", "0");
        GAME_MODES.put("creative", "1");
        GAME_MODES.put("adventure", "2");
        GAME_MODES.put("spectator", "3");
    }

    private CommandSelectors() {}

    static String target(String token, Ported pack) {
        Matcher m = SELECTOR.matcher(token);
        if (!m.matches()) { return token; }
        String kind = m.group(1);
        if (m.group(2) == null || m.group(2).isEmpty()) { return "@" + kind; }
        List<String> out = new ArrayList<>();
        String sort = null;
        String limit = null;
        for (String part : splitTop(m.group(2))) {
            int equals = part.indexOf('=');
            if (equals <= 0) { throw new Commands.Kept("the selector " + token + " holds '" + part + "', which is not an argument"); }
            String key = part.substring(0, equals).trim();
            String value = part.substring(equals + 1).trim();
            boolean not = value.startsWith("!");
            String bare = not ? value.substring(1) : value;
            switch (key) {
                case "type": out.add("type=" + (not ? "!" : "") + Ids.entity(bare)); break;
                case "name":
                case "team":
                case "tag": out.add(key + "=" + (not ? "!" : "") + unquote(bare)); break;
                case "distance": range(out, "rm", "r", value); break;
                case "level": range(out, "lm", "l", value); break;
                case "x_rotation": range(out, "rxm", "rx", value); break;
                case "y_rotation": range(out, "rym", "ry", value); break;
                case "gamemode":
                    if (!GAME_MODES.containsKey(bare)) { throw new Commands.Kept("the game mode " + bare + " is not one 1.12.2 knows"); }
                    out.add("m=" + (not ? "!" : "") + GAME_MODES.get(bare));
                    break;
                case "x":
                case "z":
                case "dx":
                case "dy":
                case "dz": out.add(key + "=" + whole(value)); break;
                case "y": out.add("y=" + Commands.height(String.valueOf(whole(value)), pack)); break;
                case "scores": scores(out, value); break;
                case "limit": limit = value; break;
                case "sort": sort = value; break;
                default: throw new Commands.Kept("the selector argument " + key + " has no twin on 1.12.2");
            }
        }
        if ("random".equals(sort)) { kind = "r"; }
        if (limit != null && !"s".equals(kind) && !("p".equals(kind) && "1".equals(limit))) { out.add("c=" + ("furthest".equals(sort) ? "-" : "") + limit); }
        return "@" + kind + (out.isEmpty() ? "" : "[" + String.join(",", out) + "]");
    }

    static List<String> splitTop(String text) {
        List<String> out = new ArrayList<>();
        int depth = 0;
        StringBuilder current = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (c == '{' || c == '[') { depth++; }
            if (c == '}' || c == ']') { depth--; }
            if (c == ',' && depth == 0) {
                out.add(current.toString());
                current.setLength(0);
                continue;
            }
            current.append(c);
        }
        if (current.length() > 0) { out.add(current.toString()); }
        return out;
    }

    private static String unquote(String text) { return text.length() > 1 && text.startsWith("\"") && text.endsWith("\"") ? text.substring(1, text.length() - 1) : text; }

    static int whole(String text) {
        try { return (int) Math.floor(Double.parseDouble(text)); }
        catch (NumberFormatException broken) { throw new Commands.Kept("'" + text + "' is not a number"); }
    }

    private static void range(List<String> out, String least, String most, String value) {
        Matcher m = RANGE.matcher(value);
        if (!m.matches()) {
            out.add(least + "=" + whole(value));
            out.add(most + "=" + whole(value));
            return;
        }
        if (!m.group(1).isEmpty()) { out.add(least + "=" + whole(m.group(1))); }
        if (!m.group(2).isEmpty()) { out.add(most + "=" + whole(m.group(2))); }
    }

    private static void scores(List<String> out, String value) {
        if (!value.startsWith("{") || !value.endsWith("}")) { throw new Commands.Kept("the scores argument " + value + " is not a set of scores"); }
        for (String part : splitTop(value.substring(1, value.length() - 1))) {
            int equals = part.indexOf('=');
            if (equals <= 0) { continue; }
            String objective = part.substring(0, equals).trim();
            String bounds = part.substring(equals + 1).trim();
            Matcher m = RANGE.matcher(bounds);
            if (!m.matches()) {
                out.add("score_" + objective + "_min=" + whole(bounds));
                out.add("score_" + objective + "=" + whole(bounds));
                continue;
            }
            if (!m.group(1).isEmpty()) { out.add("score_" + objective + "_min=" + whole(m.group(1))); }
            if (!m.group(2).isEmpty()) { out.add("score_" + objective + "=" + whole(m.group(2))); }
        }
    }
}
