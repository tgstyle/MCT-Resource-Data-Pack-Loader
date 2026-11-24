package mctmods.resourcedatapackloader.core.util.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class TextLines {
    private TextLines() {}

    public static List<String> lines(String text) { return new ArrayList<>(Arrays.asList(text.split("\r\n|\r|\n", -1))); }
}
