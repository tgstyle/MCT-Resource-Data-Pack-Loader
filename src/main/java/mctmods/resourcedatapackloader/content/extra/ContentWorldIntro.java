package mctmods.resourcedatapackloader.content.extra;

import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.def.IntroPageDef;
import mctmods.resourcedatapackloader.content.def.WorldIntroDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonParseException;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public final class ContentWorldIntro {
    private static final Map<ResourceLocation, WorldIntroDef> DEFS = new LinkedHashMap<>();
    private static boolean loaded;

    private ContentWorldIntro() {}

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        if (!Config.content.load) { return; }

        PackManager.get().forEach(PackManager.WORLDINTRO, PackManager.JSON, (namespace, path, contents) -> {
            ResourceLocation key = new ResourceLocation(namespace, path);
            try {
                WorldIntroDef def = ContentParser.worldIntro(key, contents);
                if (def != null) { DEFS.put(key, def); }
            }
            catch (IllegalArgumentException | JsonParseException ex) { ContentLog.LOGGER.error("Parsing error in world intro {}, ignoring it: {}", key, ex.getMessage()); }
        });

        int pages = pages().size();
        if (pages > 0) { Summary.info("worldintro", "Showing an intro of " + pages + " page(s) when a player enters the world"); }
    }

    private static List<WorldIntroDef> usable() {
        List<WorldIntroDef> usable = new ArrayList<>();
        for (Map.Entry<ResourceLocation, WorldIntroDef> entry : DEFS.entrySet()) {
            if (ContentRegistry.available(entry.getValue().requires, entry.getKey())) { usable.add(entry.getValue()); }
        }
        return usable;
    }

    public static List<IntroPageDef> pages() {
        List<IntroPageDef> pages = new ArrayList<>();
        for (WorldIntroDef def : usable()) { pages.addAll(def.pages); }
        return Collections.unmodifiableList(pages);
    }

    public static boolean once() {
        for (WorldIntroDef def : usable()) {
            if (def.once) { return true; }
        }
        return false;
    }

    @Nullable public static ResourceLocation music() {
        for (WorldIntroDef def : usable()) {
            if (def.music != null) { return def.music; }
        }
        return null;
    }
}
