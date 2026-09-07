package mctmods.resourcedatapackloader.content.extra;

import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.def.IntroPageDef;
import mctmods.resourcedatapackloader.content.def.WorldIntroDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentWorldIntro {
    private static final Gson GSON = new Gson();
    private static final Map<ResourceLocation, WorldIntroDef> DEFS = new LinkedHashMap<>();
    private static boolean loaded;

    private ContentWorldIntro() {}

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        if (Config.contentOff()) { return; }
        Json.eachFile(PackManager.WORLDINTRO, "world intro", (key, contents) -> {
            if (ContentRegistry.reserved(key)) { return; }
            WorldIntroDef def = parse(key, contents);
            if (def != null) { DEFS.put(key, def); }
        });
        int pages = pages().size();
        if (pages > 0) { Summary.info("worldintro", "Showing an intro of " + pages + " page(s) when a player enters the world"); }
    }

    @Nullable private static WorldIntroDef parse(ResourceLocation key, String contents) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null) { return null; }
        List<IntroPageDef> pages = new ArrayList<>();
        for (JsonElement element : GsonHelper.getAsJsonArray(json, "pages", new com.google.gson.JsonArray())) {
            if (!element.isJsonObject()) { continue; }
            IntroPageDef page = page(key, element.getAsJsonObject());
            if (page != null) { pages.add(page); }
        }
        if (pages.isEmpty()) {
            ContentLog.LOGGER.error("World intro {} names no pages, ignoring it", key);
            return null;
        }
        String music = GsonHelper.getAsString(json, "music", "").trim();
        ResourceLocation track = music.isEmpty() ? null : ResourceLocation.tryParse(music);
        if (!music.isEmpty() && track == null) { ContentLog.LOGGER.error("World intro {} names music '{}', which is not a sound id, so it plays silently", key, music); }
        return new WorldIntroDef(key, GsonHelper.getAsBoolean(json, "once", false), track, List.copyOf(pages), Json.strings(json, "requires"));
    }

    @Nullable private static IntroPageDef page(ResourceLocation key, JsonObject json) {
        List<ResourceLocation> backgrounds = new ArrayList<>();
        String single = GsonHelper.getAsString(json, "background", "").trim();
        if (!single.isEmpty()) { location(key, single, backgrounds); }
        for (String name : Json.strings(json, "backgrounds")) { location(key, name, backgrounds); }
        String mode = GsonHelper.getAsString(json, "mode", IntroPageDef.SCROLL).trim().toLowerCase(Locale.ROOT);
        if (!IntroPageDef.SCROLL.equals(mode) && !IntroPageDef.STATIC.equals(mode)) {
            ContentLog.LOGGER.error("World intro {} has a page with mode '{}', which is neither '{}' nor '{}', ignoring the page", key, mode, IntroPageDef.SCROLL, IntroPageDef.STATIC);
            return null;
        }
        String direction = GsonHelper.getAsString(json, "direction", IntroPageDef.UP).trim().toLowerCase(Locale.ROOT);
        if (!IntroPageDef.UP.equals(direction) && !IntroPageDef.DOWN.equals(direction)) {
            ContentLog.LOGGER.error("World intro {} has a page with direction '{}', which is neither '{}' nor '{}', taking '{}'", key, direction, IntroPageDef.UP, IntroPageDef.DOWN, IntroPageDef.UP);
            direction = IntroPageDef.UP;
        }
        String text = GsonHelper.getAsString(json, "text", "").trim();
        ResourceLocation textAt = text.isEmpty() ? null : ResourceLocation.tryParse(text);
        if (!text.isEmpty() && textAt == null) { ContentLog.LOGGER.error("World intro {} names text '{}', which is not a resource id, so the page shows none", key, text); }
        return new IntroPageDef(List.copyOf(backgrounds), GsonHelper.getAsFloat(json, "interval", 5.0F), textAt, mode, GsonHelper.getAsFloat(json, "time", IntroPageDef.DERIVE), direction,
                Math.max(0.25F, GsonHelper.getAsFloat(json, "textScale", 1.0F)), GsonHelper.getAsBoolean(json, "settle", false));
    }

    private static void location(ResourceLocation key, String name, List<ResourceLocation> out) {
        ResourceLocation found = ResourceLocation.tryParse(name.trim());
        if (found == null) { ContentLog.LOGGER.error("World intro {} names background '{}', which is not a resource id, leaving it out", key, name); }
        else { out.add(found); }
    }

    private static List<WorldIntroDef> usable() {
        List<WorldIntroDef> usable = new ArrayList<>();
        for (WorldIntroDef def : DEFS.values()) {
            if (ContentRegistry.available(def.requires(), def.key())) { usable.add(def); }
        }
        return usable;
    }

    public static List<IntroPageDef> pages() {
        List<IntroPageDef> pages = new ArrayList<>();
        for (WorldIntroDef def : usable()) { pages.addAll(def.pages()); }
        return Collections.unmodifiableList(pages);
    }

    public static boolean once() {
        for (WorldIntroDef def : usable()) {
            if (def.once()) { return true; }
        }
        return false;
    }

    @Nullable public static ResourceLocation music() {
        for (WorldIntroDef def : usable()) {
            if (def.music() != null) { return def.music(); }
        }
        return null;
    }
}
