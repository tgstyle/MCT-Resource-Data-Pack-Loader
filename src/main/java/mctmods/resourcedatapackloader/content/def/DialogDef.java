package mctmods.resourcedatapackloader.content.def;

import mctmods.resourcedatapackloader.content.card.CardWhen;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class DialogDef {
    public final String key;
    public final String start;
    @Nullable public final String title;
    @Nullable public final String icon;
    @Nullable public final String color;
    @Nullable public final String image;
    @Nullable public final Boolean background;
    @Nullable public final String font;
    public final List<String> requires;
    public final Map<String, Node> nodes;

    public DialogDef(String key, String start, @Nullable String title, @Nullable String icon, @Nullable String color, @Nullable String image, @Nullable Boolean background, @Nullable String font, List<String> requires, Map<String, Node> nodes) {
        this.key = key;
        this.start = start;
        this.title = title;
        this.icon = icon;
        this.color = color;
        this.image = image;
        this.background = background;
        this.font = font;
        this.requires = requires;
        this.nodes = nodes;
    }

    public static final class Node {
        @Nullable public final String title;
        public final List<String> lines;
        public final List<Reply> replies;

        public Node(@Nullable String title, List<String> lines, List<Reply> replies) {
            this.title = title;
            this.lines = lines;
            this.replies = replies;
        }
    }

    public static final class Reply {
        public final String text;
        public final String target;
        public final String card;
        public final String runs;
        @Nullable public final CardWhen when;
        public final boolean close;

        public Reply(String text, String target, String card, String runs, @Nullable CardWhen when, boolean close) {
            this.text = text;
            this.target = target;
            this.card = card;
            this.runs = runs;
            this.when = when;
            this.close = close;
        }
    }
}
