package mctmods.resourcedatapackloader.client;

import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;

final class CardFont {
    static final String RDPL = "resourcedatapackloader:rdpl";
    private static final String RUNIC = "resourcedatapackloader:rdpl_runic";
    private static final String OURS = "resourcedatapackloader:";
    private static final Set<String> SHORT = new HashSet<>(Arrays.asList("rdpl", "rdpl_bold", "rdpl_italic", "rdpl_runic", "rdpl_runic_bold", "rdpl_runic_italic"));
    private static final Set<String> WARNED = new HashSet<>();

    private CardFont() {}

    record Face(ResourceLocation plain, @Nullable ResourceLocation bold) {}

    static Face of(String name) {
        String asked = named(name);
        ResourceLocation plain = find(asked);
        if (plain != null) {
            ResourceLocation italic = find(asked + "_italic");
            if (italic != null) { GameFont.slants(plain, italic); }
            return new Face(plain, find(asked + "_bold"));
        }
        if (WARNED.add(asked)) { ContentLog.LOGGER.warn("Card font {} names no font/<name>.json in any pack, so the game's font is used", asked); }
        return new Face(Style.DEFAULT_FONT, null);
    }

    static Face runic() { return of(RUNIC); }

    private static String named(String name) {
        if (name.isEmpty()) { return RDPL; }
        return SHORT.contains(name) ? OURS + name : name;
    }

    @Nullable private static ResourceLocation find(String name) {
        ResourceLocation id = ContentParser.location(name);
        if (id != null && Minecraft.getInstance().getResourceManager().getResource(id.withPath(path -> "font/" + path + ".json")).isPresent()) { return id; }
        return null;
    }
}
