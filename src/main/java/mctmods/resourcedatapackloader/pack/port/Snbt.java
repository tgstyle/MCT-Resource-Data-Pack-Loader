package mctmods.resourcedatapackloader.pack.port;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class Snbt {
    private final String text;
    private int at;

    private Snbt(String text) { this.text = text; }

    static Object parse(String text) {
        Snbt reader = new Snbt(text.trim());
        Object value = reader.value();
        reader.skip();
        if (reader.at != reader.text.length()) { throw new IllegalArgumentException("trailing text after nbt at " + reader.at); }
        return value;
    }

    static String write(Object value) {
        if (value instanceof Map) {
            List<String> parts = new ArrayList<>();
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) { parts.add(entry.getKey() + ":" + write(entry.getValue())); }
            return "{" + String.join(",", parts) + "}";
        }
        if (value instanceof List) {
            List<String> parts = new ArrayList<>();
            for (Object item : (List<?>) value) { parts.add(write(item)); }
            return "[" + String.join(",", parts) + "]";
        }
        return String.valueOf(value);
    }

    static String unquoted(Object value) {
        String raw = String.valueOf(value);
        return raw.length() > 1 && (raw.startsWith("\"") && raw.endsWith("\"") || raw.startsWith("'") && raw.endsWith("'")) ? raw.substring(1, raw.length() - 1) : raw;
    }

    @SuppressWarnings("unchecked") static void spawner(Object nbt) {
        if (!(nbt instanceof Map)) { return; }
        Map<String, Object> spawner = (Map<String, Object>) nbt;
        Object data = spawner.get("SpawnData");
        if (data instanceof Map && ((Map<String, Object>) data).get("entity") instanceof Map) { spawner.put("SpawnData", entity(((Map<String, Object>) data).get("entity"))); }
        Object potentials = spawner.get("SpawnPotentials");
        if (!(potentials instanceof List)) { return; }
        List<Object> converted = new ArrayList<>();
        for (Object entry : (List<Object>) potentials) {
            if (!(entry instanceof Map)) { continue; }
            Map<String, Object> held = (Map<String, Object>) entry;
            Object inner = held.get("data") instanceof Map ? ((Map<String, Object>) held.get("data")).get("entity") : held.get("Entity");
            Map<String, Object> out = new LinkedHashMap<>();
            out.put("Weight", held.containsKey("weight") ? held.get("weight") : held.getOrDefault("Weight", "1"));
            out.put("Entity", entity(inner));
            converted.add(out);
        }
        spawner.put("SpawnPotentials", converted);
    }

    @SuppressWarnings("unchecked") private static Object entity(Object held) {
        if (!(held instanceof Map)) { return held; }
        Map<String, Object> entity = (Map<String, Object>) held;
        Object id = entity.get("id");
        if (id != null && Ids.vanilla(unquoted(id))) { entity.put("id", "\"" + Ids.entity(unquoted(id)) + "\""); }
        return entity;
    }

    private void skip() {
        while (at < text.length() && Character.isWhitespace(text.charAt(at))) { at++; }
    }

    private Object value() {
        skip();
        if (at >= text.length()) { throw new IllegalArgumentException("nbt ends early"); }
        char c = text.charAt(at);
        if (c == '{') { return compound(); }
        if (c == '[') { return list(); }
        return primitive();
    }

    private Map<String, Object> compound() {
        Map<String, Object> out = new LinkedHashMap<>();
        at++;
        skip();
        if (peek() == '}') {
            at++;
            return out;
        }
        while (true) {
            skip();
            String key = String.valueOf(primitive());
            skip();
            colon();
            out.put(key, value());
            skip();
            char c = next();
            if (c == '}') { return out; }
            if (c != ',') { throw new IllegalArgumentException("expected , or } in nbt at " + at); }
        }
    }

    private List<Object> list() {
        List<Object> out = new ArrayList<>();
        at++;
        skip();
        if (at + 1 < text.length() && text.charAt(at + 1) == ';') {
            out.add(text.substring(at, at + 2));
            at += 2;
        }
        skip();
        if (peek() == ']') {
            at++;
            return out;
        }
        while (true) {
            out.add(value());
            skip();
            char c = next();
            if (c == ']') { return out; }
            if (c != ',') { throw new IllegalArgumentException("expected , or ] in nbt at " + at); }
        }
    }

    private Object primitive() {
        skip();
        char c = peek();
        int start = at;
        if (c == '"' || c == '\'') {
            at++;
            while (at < text.length() && text.charAt(at) != c) {
                if (text.charAt(at) == '\\') { at++; }
                at++;
            }
            at++;
            return text.substring(start, Math.min(at, text.length()));
        }
        while (at < text.length() && ",:}]".indexOf(text.charAt(at)) < 0) { at++; }
        return text.substring(start, at).trim();
    }

    private char peek() { return at < text.length() ? text.charAt(at) : '\0'; }

    private char next() {
        if (at >= text.length()) { throw new IllegalArgumentException("nbt ends early"); }
        return text.charAt(at++);
    }

    private void colon() {
        if (next() != ':') { throw new IllegalArgumentException("expected : in nbt at " + at); }
    }
}
