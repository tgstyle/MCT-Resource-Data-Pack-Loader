package mctmods.resourcedatapackloader.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MarkLines {
    public static final int TEXT = 0;
    public static final int PLAIN = 1;
    public static final int HEADING = 2;
    public static final int ITEM = 3;
    public static final int QUOTE = 4;
    public static final int RULE = 5;
    public static final int IMAGE = 6;
    public static final String BULLET = "•";
    private static final Pattern HEADER = Pattern.compile("^(#{1,3}) +(.*)$");
    private static final Pattern BULLETED = Pattern.compile("^( *)[-*+] +(.*)$");
    private static final Pattern NUMBERED = Pattern.compile("^( *)(\\d{1,9}[.)]) +(.*)$");
    private static final Pattern QUOTED = Pattern.compile("^> ?(.*)$");
    private static final Pattern RULED = Pattern.compile("^ *(-{3,}|\\*{3,}|_{3,}) *$");
    private static final Pattern PICTURE = Pattern.compile("^ *!\\[([^]]*)]\\(([^)\\s]+)\\) *$");
    private static final String FENCE = "```";

    private MarkLines() {}

    public static final class Line {
        public final int kind;
        public final int level;
        public final String lead;
        public final String text;

        Line(int kind, int level, String lead, String text) {
            this.kind = kind;
            this.level = level;
            this.lead = lead;
            this.text = text;
        }
    }

    public static List<Line> parse(List<String> written) {
        List<Line> lines = new ArrayList<>();
        boolean fenced = false;
        for (String said : written) {
            boolean fence = said.trim().startsWith(FENCE);
            if (fenced || fence || said.startsWith("|")) {
                if (fence) { fenced = !fenced; }
                lines.add(new Line(PLAIN, 0, "", said));
                continue;
            }
            lines.add(line(said));
        }
        return lines;
    }

    private static Line line(String said) {
        Matcher match = PICTURE.matcher(said);
        if (match.matches()) { return new Line(IMAGE, 0, match.group(2), match.group(1)); }
        if (RULED.matcher(said).matches()) { return new Line(RULE, 0, "", ""); }
        match = HEADER.matcher(said);
        if (match.matches()) { return new Line(HEADING, match.group(1).length(), "", match.group(2)); }
        match = BULLETED.matcher(said);
        if (match.matches()) { return new Line(ITEM, match.group(1).length() / 2, BULLET, match.group(2)); }
        match = NUMBERED.matcher(said);
        if (match.matches()) { return new Line(ITEM, match.group(1).length() / 2, match.group(2), match.group(3)); }
        match = QUOTED.matcher(said);
        if (match.matches()) { return new Line(QUOTE, 0, "", match.group(1)); }
        return new Line(TEXT, 0, "", said);
    }
}
