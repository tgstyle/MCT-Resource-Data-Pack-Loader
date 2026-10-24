package mctmods.resourcedatapackloader.content.def;

import mctmods.resourcedatapackloader.content.card.CardWhen;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public record DialogDef(String key, String start, @Nullable String title, @Nullable String icon, @Nullable String color, @Nullable String image, @Nullable Boolean background, @Nullable String font, List<String> requires, Map<String, Node> nodes) {
    public record Node(@Nullable String title, List<String> lines, List<Reply> replies) {}

    public record Reply(String text, String target, String card, String runs, @Nullable CardWhen when, boolean close) {}
}
