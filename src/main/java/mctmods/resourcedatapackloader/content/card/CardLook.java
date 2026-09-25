package mctmods.resourcedatapackloader.content.card;

import mctmods.resourcedatapackloader.util.Says;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class CardLook {
    public static final String CORNER = "corner";
    public static final String CENTER = "center";
    public static final String CHAT = "chat";
    public static final String BAR = "bar";
    static final List<String> STYLES = Collections.unmodifiableList(Arrays.asList(CORNER, CENTER, CHAT, BAR));
    String title = "";
    List<String> lines;
    ItemStack icon;
    String image;
    boolean panel;
    String font;
    int background;
    int text;
    TextFormatting chat;
    int ticks;
    String style;

    private CardLook(List<String> lines, TextFormatting color, String style) {
        this.lines = lines;
        this.icon = Says.icon();
        this.image = Says.image();
        this.panel = Says.panel();
        this.font = Says.font();
        this.background = Says.background();
        this.text = Says.rgb(color);
        this.chat = color;
        this.ticks = Says.CARD_TICKS;
        this.style = style;
    }

    public static CardLook says(String said, TextFormatting color, String style) { return new CardLook(Collections.singletonList(said), color, style); }

    public static CardLook card(String title, List<String> lines, ItemStack icon, String image, int background, int ticks) {
        CardLook look = new CardLook(lines, TextFormatting.YELLOW, CORNER);
        look.title = title;
        look.icon = icon;
        look.image = image;
        look.background = background;
        look.text = 0xFFFFFF;
        look.ticks = ticks;
        return look;
    }

    static CardLook plain() { return new CardLook(Collections.emptyList(), TextFormatting.YELLOW, CORNER); }
}
