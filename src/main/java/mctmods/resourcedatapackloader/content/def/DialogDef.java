package mctmods.resourcedatapackloader.content.def;

import mctmods.resourcedatapackloader.content.card.CardWhen;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public record DialogDef(String key, String start, @Nullable String title, @Nullable String icon, @Nullable String color, @Nullable String image, @Nullable Boolean background, @Nullable String font, @Nullable String titleColor, @Nullable String textColor, @Nullable String replyColor, @Nullable String hoverColor, @Nullable String hoverBackground, @Nullable Boolean history, List<String> requires, Map<String, Node> nodes) {
    public record Node(@Nullable String title, List<String> lines, List<Reply> replies) {}

    public record Reply(String text, String target, String card, String runs, @Nullable CardWhen when, boolean close, boolean trade) {}
}
