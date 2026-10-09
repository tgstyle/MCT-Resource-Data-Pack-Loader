package mctmods.resourcedatapackloader.pack.port;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

final class FunctionLines {
    private FunctionLines() {}

    record Result(String text, int changed, int kept) {}

    static Result convert(String contents, String file, UnaryOperator<String> command, Consumer<String> note) {
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
                String converted = command.apply(line);
                out.append(converted);
                if (!converted.equals(line)) { changed++; }
            }
            catch (Commands.Kept why) {
                kept++;
                out.append("# ").append(line);
                note.accept("'" + file + "' line " + (i + 1) + " is turned into a comment, since " + why.getMessage() + ": " + line);
            }
        }
        return new Result(out.toString(), changed, kept);
    }
}
