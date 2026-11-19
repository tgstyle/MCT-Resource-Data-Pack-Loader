package mctmods.resourcedatapackloader.common.syncdata;

public final class DataText {
    public static final String OPEN = "{data:";
    private static final char CLOSE = '}';
    private static final char SPLIT = '|';

    private DataText() {}

    public static int token(String raw, int from) { return raw.indexOf(OPEN, from); }

    public static String name(String body) {
        int split = body.indexOf(SPLIT);
        return (split < 0 ? body : body.substring(0, split)).trim();
    }

    public static String fallback(String body) {
        int split = body.indexOf(SPLIT);
        return split < 0 ? "" : body.substring(split + 1);
    }

    public static String value(DataReader reader, String name, String fallback) {
        SyncValue held = reader.known() ? reader.get(name) : null;
        return held == null ? fallback : held.display;
    }

    public static String fill(String line, DataReader reader) {
        int at = token(line, 0);
        if (at < 0) { return line; }
        StringBuilder out = new StringBuilder(line.length() + 16);
        int from = 0;
        while (at >= 0) {
            int close = line.indexOf(CLOSE, at);
            if (close < 0) { break; }
            String body = line.substring(at + OPEN.length(), close);
            out.append(line, from, at).append(value(reader, name(body), fallback(body)));
            from = close + 1;
            at = token(line, from);
        }
        return out.append(line, from, line.length()).toString();
    }
}
