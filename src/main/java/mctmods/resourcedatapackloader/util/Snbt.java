package mctmods.resourcedatapackloader.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public final class Snbt {
    public static final List<String> SIDES = List.of("back", "left", "right", "front");
    public static final Set<String> TRIM_PALETTES = Set.of("amethyst", "copper", "copper_darker", "diamond", "diamond_darker", "emerald", "gold", "gold_darker", "iron", "iron_darker", "lapis", "netherite", "netherite_darker", "quartz", "redstone", "resin");
    private static final List<String> POT_KEYS = List.of("pot_decorations=", "\"sherds\":", "sherds:");

    private Snbt() {}

    public static List<String> split(String text, char separator) {
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

    public static String pots(String line, Function<String, String> rewrite) {
        StringBuilder out = new StringBuilder();
        int at = 0;
        while (at < line.length()) {
            int found = -1;
            String key = "";
            for (String candidate : POT_KEYS) {
                int index = line.indexOf(candidate, at);
                while (index > 0 && Character.isLetterOrDigit(line.charAt(index - 1)) && !"pot_decorations=".equals(candidate)) { index = line.indexOf(candidate, index + 1); }
                if (index >= 0 && (found < 0 || index < found)) {
                    found = index;
                    key = candidate;
                }
            }
            if (found < 0) { break; }
            int start = found + key.length();
            while (start < line.length() && line.charAt(start) == ' ') { start++; }
            int end = end(line, start);
            out.append(line, at, start).append(rewrite.apply(line.substring(start, end)));
            at = end;
        }
        return at == 0 ? line : out.append(line.substring(at)).toString();
    }

    private static int end(String text, int from) {
        if (from >= text.length()) { return from; }
        char first = text.charAt(from);
        if (first == '"' || first == '\'') { return quoted(text, from) + 1; }
        if (first != '[' && first != '{') {
            int at = from;
            while (at < text.length() && ",]}: ".indexOf(text.charAt(at)) < 0) { at++; }
            return at;
        }
        int close = Brackets.close(text, from);
        return close < 0 ? text.length() : close + 1;
    }

    public static List<String> items(String inner) {
        List<String> out = new ArrayList<>();
        int at = 0;
        while (at < inner.length()) {
            while (at < inner.length() && (inner.charAt(at) == ' ' || inner.charAt(at) == ',')) { at++; }
            if (at >= inner.length()) { break; }
            int start = at;
            int end = end(inner, at);
            while (end < inner.length() && inner.charAt(end) == ':') { end = end(inner, end + 1 + skip(inner, end + 1)); }
            out.add(inner.substring(start, end).trim());
            at = end;
        }
        return out;
    }

    public static String unquoted(String token) {
        String trimmed = token.trim();
        if (trimmed.length() >= 2 && (trimmed.charAt(0) == '"' || trimmed.charAt(0) == '\'') && trimmed.charAt(trimmed.length() - 1) == trimmed.charAt(0)) { return trimmed.substring(1, trimmed.length() - 1); }
        return trimmed;
    }

    private static int skip(String text, int from) {
        int at = from;
        while (at < text.length() && text.charAt(at) == ' ') { at++; }
        return at - from;
    }

    private static int quoted(String text, int from) {
        char quote = text.charAt(from);
        for (int at = from + 1; at < text.length(); at++) {
            if (text.charAt(at) == '\\') { at++; }
            else if (text.charAt(at) == quote) { return at; }
        }
        return text.length() - 1;
    }
}
