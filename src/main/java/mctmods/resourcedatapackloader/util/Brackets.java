package mctmods.resourcedatapackloader.util;

public final class Brackets {
    private Brackets() {}

    public static int close(String text, int open) {
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
        return -1;
    }
}
