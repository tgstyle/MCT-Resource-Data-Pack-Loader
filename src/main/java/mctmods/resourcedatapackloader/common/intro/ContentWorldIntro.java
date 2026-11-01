package mctmods.resourcedatapackloader.common.intro;

import mctmods.resourcedatapackloader.common.pack.definitions.worlds.IntroPageDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.WorldIntroDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.common.pack.loading.parsers.ContentParserWorlds;
import mctmods.resourcedatapackloader.common.util.worldgen.PackGeneration;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.json.Json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public final class ContentWorldIntro {
    private static final Map<ResourceLocation, WorldIntroDef> DEFS = new LinkedHashMap<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    private ContentWorldIntro() {}

    public static void load() {
        if (!GENERATION.stale()) { return; }
        DEFS.clear();
        if (!Config.content.load) { return; }
        Json.eachFile(PackManager.WORLDINTRO, "world intro", (key, contents) -> {
            WorldIntroDef def = ContentParserWorlds.worldIntro(key, contents);
            if (def != null) { DEFS.put(key, def); }
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
