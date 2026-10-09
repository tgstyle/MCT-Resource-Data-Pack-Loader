package mctmods.resourcedatapackloader.util;

import java.util.ArrayList;
import java.util.List;

public final class CommandWords {
    private CommandWords() {}

    public static List<String> split(String line) {
        List<String> out = new ArrayList<>();
        StringBuilder word = new StringBuilder();
        int depth = 0;
        char quote = 0;
        for (int i = 0; i < line.length(); i++) {
            char held = line.charAt(i);
            if (quote != 0) {
                if (held == '\\' && i + 1 < line.length()) { word.append(held).append(line.charAt(++i)); }
                else {
                    if (held == quote) { quote = 0; }
                    word.append(held);
                }
                continue;
            }
            if (held == ' ' && depth == 0) {
                if (word.length() > 0) { out.add(word.toString()); }
                word.setLength(0);
                continue;
            }
            if (held == '"' || held == '\'') { quote = held; }
            else if (held == '[' || held == '{') { depth++; }
            else if (held == ']' || held == '}') { depth = Math.max(0, depth - 1); }
            word.append(held);
        }
        if (word.length() > 0) { out.add(word.toString()); }
        if (out.isEmpty()) { out.add(""); }
        return out;
    }
}
