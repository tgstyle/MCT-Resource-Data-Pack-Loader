package mctmods.resourcedatapackloader.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Marks {
    public static final int BOLD = 1;
    public static final int ITALIC = 2;
    public static final int STRIKE = 4;
    public static final int CODE = 8;
    public static final int LINK = 16;
    public static final int RUNIC = 32;
    private static final String RUNIC_OPEN = "{runic}";
    private static final String RUNIC_CLOSE = "{/runic}";
    private static final String ESCAPED = "\\`*_~[]()!#>-+.|";

    private Marks() {}

    public static final class Run {
        public final String text;
        public final int marks;

        Run(String text, int marks) {
            this.text = text;
            this.marks = marks;
        }

        public boolean has(int mark) { return (marks & mark) != 0; }
    }

    public static List<Run> runs(String said) {
        if (plain(said)) { return Collections.singletonList(new Run(said, 0)); }
        List<Run> runs = new ArrayList<>();
        parse(said, 0, said.length(), 0, runs);
        return runs;
    }

    public static boolean plain(String said) {
        if (said.contains(RUNIC_OPEN)) { return false; }
        for (int i = 0; i < said.length(); i++) {
            if ("\\`*~[".indexOf(said.charAt(i)) >= 0) { return false; }
        }
        return true;
    }

    private static void parse(String s, int from, int to, int marks, List<Run> runs) {
        StringBuilder text = new StringBuilder();
        int i = from;
        while (i < to) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < to && ESCAPED.indexOf(s.charAt(i + 1)) >= 0) {
                text.append(s.charAt(i + 1));
                i += 2;
                continue;
            }
            if (c == '`') {
                int close = s.indexOf('`', i + 1);
                if (close > i + 1 && close < to) {
                    flush(text, marks, runs);
                    runs.add(new Run(s.substring(i + 1, close), marks | CODE));
                    i = close + 1;
                    continue;
                }
            }
            if (c == '[') {
                int middle = link(s, i + 1, to);
                int close = middle < 0 ? -1 : s.indexOf(')', middle + 2);
                if (middle > i + 1 && close > 0 && close < to) {
                    flush(text, marks, runs);
                    parse(s, i + 1, middle, marks | LINK, runs);
                    i = close + 1;
                    continue;
                }
            }
            if (c == '{' && s.startsWith(RUNIC_OPEN, i)) {
                int open = i + RUNIC_OPEN.length();
                int close = s.indexOf(RUNIC_CLOSE, open);
                if (close > open && close + RUNIC_CLOSE.length() <= to) {
                    flush(text, marks, runs);
                    parse(s, open, close, marks | RUNIC, runs);
                    i = close + RUNIC_CLOSE.length();
                    continue;
                }
            }
            if (c == '*' || c == '~') {
                int length = run(s, i, to, c);
                int mark = mark(c, length);
                int open = i + length;
                if (mark != 0 && open < to && !Character.isWhitespace(s.charAt(open))) {
                    int close = closer(s, open, to, c, length);
                    if (close > open) {
                        flush(text, marks, runs);
                        parse(s, open, close, marks | mark, runs);
                        i = close + length;
                        continue;
                    }
                }
                text.append(s, i, i + length);
                i += length;
                continue;
            }
            text.append(c);
            i++;
        }
        flush(text, marks, runs);
    }

    private static int mark(char c, int length) {
        if (c == '~') { return length == 2 ? STRIKE : 0; }
        if (length == 1) { return ITALIC; }
        if (length == 2) { return BOLD; }
        return length == 3 ? BOLD | ITALIC : 0;
    }

    private static int run(String s, int at, int to, char c) {
        int end = at;
        while (end < to && s.charAt(end) == c) { end++; }
        return end - at;
    }

    private static int closer(String s, int from, int to, char c, int length) {
        int i = from;
        while (i < to) {
            char at = s.charAt(i);
            if (at == '\\') {
                i += 2;
                continue;
            }
            if (at == '`') {
                int close = s.indexOf('`', i + 1);
                if (close > 0 && close < to) {
                    i = close + 1;
                    continue;
                }
            }
            if (at == c) {
                int found = run(s, i, to, c);
                if (found == length && !Character.isWhitespace(s.charAt(i - 1))) { return i; }
                i += found;
                continue;
            }
            i++;
        }
        return -1;
    }

    private static int link(String s, int from, int to) {
        int i = from;
        while (i < to) {
            if (s.charAt(i) == '\\') {
                i += 2;
                continue;
            }
            if (s.startsWith("](", i)) { return i; }
            i++;
        }
        return -1;
    }

    private static void flush(StringBuilder text, int marks, List<Run> runs) {
        if (text.isEmpty()) { return; }
        runs.add(new Run(text.toString(), marks));
        text.setLength(0);
    }
}
