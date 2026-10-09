package mctmods.resourcedatapackloader.content.compat;

import mctmods.blastplaster.Config.ExplosionMode;
import mctmods.blastplaster.Config.View;
import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Enums;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.PackGeneration;
import mctmods.resourcedatapackloader.util.Summary;
import static mctmods.blastplaster.Config.provider;

import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.world.level.Level;

public final class ContentBlastPlaster {
    private static final Map<String, JsonElement> EVERYWHERE = new LinkedHashMap<>();
    private static final Map<String, Map<String, JsonElement>> PER_DIMENSION = new LinkedHashMap<>();
    private static final Map<String, View> RESOLVED = new ConcurrentHashMap<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    private ContentBlastPlaster() {}

    public static void install() {
        if (ContentControl.off(ContentControl.BLAST_PLASTER)) { return; }
        provider(ContentBlastPlaster::viewFor);
        ContentLog.LOGGER.info("Driving Blast Plaster through pack settings, EJECT_DROPS unless a pack says otherwise");
    }

    private static View viewFor(Level level) {
        load();
        return RESOLVED.computeIfAbsent(level.dimension().location().toString(), ContentBlastPlaster::resolve);
    }

    private static View resolve(String dimension) {
        Map<String, JsonElement> settings = new LinkedHashMap<>(EVERYWHERE);
        Map<String, JsonElement> dimensional = PER_DIMENSION.get(dimension);
        if (dimensional != null) { settings.putAll(dimensional); }
        return new PackView(settings, explosionMode(settings.get("explosionMode")));
    }

    private static ExplosionMode explosionMode(JsonElement held) {
        if (held != null && held.isJsonPrimitive() && held.getAsJsonPrimitive().isString()) {
            ExplosionMode found = Enums.byName(ExplosionMode.class, held.getAsString());
            if (found != null) { return found; }
            ContentLog.LOGGER.error("Blast Plaster explosionMode '{}' is not HEAL, EJECT_DROPS or VISUAL_TOSS, using EJECT_DROPS", held.getAsString());
        }
        return ExplosionMode.EJECT_DROPS;
    }

    private static void load() {
        if (!GENERATION.stale()) { return; }
        EVERYWHERE.clear();
        PER_DIMENSION.clear();
        RESOLVED.clear();
        if (ContentControl.packDecides(ContentControl.BLAST_PLASTER)) { read(); }
    }

    private static void read() {
        int[] read = {0};
        Json.eachObject(PackManager.BLASTPLASTER, "Blast Plaster settings file", (file, held) -> {
            for (Map.Entry<String, JsonElement> entry : held.entrySet()) {
                if (!"dimensions".equals(entry.getKey())) {
                    EVERYWHERE.put(entry.getKey(), entry.getValue());
                    continue;
                }
                for (Map.Entry<String, JsonElement> given : entry.getValue().getAsJsonObject().entrySet()) {
                    Map<String, JsonElement> section = PER_DIMENSION.computeIfAbsent(given.getKey().trim(), unused -> new LinkedHashMap<>());
                    for (Map.Entry<String, JsonElement> inner : given.getValue().getAsJsonObject().entrySet()) { section.put(inner.getKey(), inner.getValue()); }
                }
            }
            read[0]++;
        });
        if (read[0] > 0) { Summary.info("blastplaster", "Loaded " + read[0] + " Blast Plaster settings file(s) from packs"); }
    }

    private record PackView(Map<String, JsonElement> settings, ExplosionMode mode) implements View {
        private JsonElement given(String key, Predicate<JsonElement> fits) {
            JsonElement held = settings.get(key);
            return held != null && fits.test(held) ? held : null;
        }

        private Boolean flag(String key) {
            JsonElement held = given(key, found -> found.isJsonPrimitive() && found.getAsJsonPrimitive().isBoolean());
            return held == null ? null : held.getAsBoolean();
        }

        private Integer number(String key) {
            JsonElement held = given(key, found -> found.isJsonPrimitive() && found.getAsJsonPrimitive().isNumber());
            return held == null ? null : held.getAsInt();
        }

        private List<String> strings(String key) {
            JsonElement held = given(key, JsonElement::isJsonArray);
            if (held == null) { return null; }
            List<String> given = new ArrayList<>();
            for (JsonElement entry : held.getAsJsonArray()) {
                if (entry.isJsonPrimitive() && entry.getAsJsonPrimitive().isString()) { given.add(entry.getAsString()); }
            }
            return given;
        }

        @Override public ExplosionMode getExplosionMode() { return mode; }

        private static <T> T or(T held, Supplier<T> fallback) { return held != null ? held : fallback.get(); }

        @Override public boolean enableFakeTossedBlocks() { return or(flag("enableFakeTossedBlocks"), View.super::enableFakeTossedBlocks); }

        @Override public boolean enableExplosionFlash() {
            if (Config.content.vanillaClients()) { return false; }
            return or(flag("enableExplosionFlash"), View.super::enableExplosionFlash);
        }

        @Override public int getExplosionFlashDuration() { return or(number("explosionFlashDuration"), View.super::getExplosionFlashDuration); }

        @Override public int getExplosionFlashLightLevel() { return or(number("explosionFlashLightLevel"), View.super::getExplosionFlashLightLevel); }

        @Override public int getExplosionFlashParticleCount() { return or(number("explosionFlashParticleCount"), View.super::getExplosionFlashParticleCount); }

        @Override public int getExplosionFlashPulses() { return or(number("explosionFlashPulses"), View.super::getExplosionFlashPulses); }

        @Override public boolean enableExplosionSmoke() { return or(flag("enableExplosionSmoke"), View.super::enableExplosionSmoke); }

        @Override public int getExplosionSmokeDuration() { return or(number("explosionSmokeDuration"), View.super::getExplosionSmokeDuration); }

        @Override public int getExplosionSmokeParticleCount() { return or(number("explosionSmokeParticleCount"), View.super::getExplosionSmokeParticleCount); }

        @Override public boolean playerTNTAlwaysDrops() { return or(flag("playerTNTAlwaysDrops"), View.super::playerTNTAlwaysDrops); }

        @Override public boolean playerTNTDropFullBlocks() { return or(flag("playerTNTDropFullBlocks"), View.super::playerTNTDropFullBlocks); }

        @Override public boolean healCreepers() { return or(flag("healCreepers"), View.super::healCreepers); }

        @Override public boolean healNonPlayerTNT() { return or(flag("healNonPlayerTNT"), View.super::healNonPlayerTNT); }

        @Override public boolean healWither() { return or(flag("healWither"), View.super::healWither); }

        @Override public boolean healAll() { return or(flag("healAll"), View.super::healAll); }

        @Override public boolean processPlayerIgnitedTNT() { return or(flag("processPlayerIgnitedTNT"), View.super::processPlayerIgnitedTNT); }

        @Override public List<String> getCustomEntitiesToHeal() { return or(strings("customEntitiesToHeal"), View.super::getCustomEntitiesToHeal); }

        @Override public int getMinimumTicksBeforeHeal() { return or(number("minimumTicksBeforeHeal"), View.super::getMinimumTicksBeforeHeal); }

        @Override public int getRandomTickVar() { return or(number("randomTickVar"), View.super::getRandomTickVar); }

        @Override public boolean isOverride() { return or(flag("overrideBlocks"), View.super::isOverride); }

        @Override public boolean healFullTrees() { return or(flag("healFullTrees"), View.super::healFullTrees); }

        @Override public boolean dtSpecialDrops() { return or(flag("dtSpecialDrops"), View.super::dtSpecialDrops); }

        @Override public int getMaxTreeSize() { return or(number("maxTreeSize"), View.super::getMaxTreeSize); }

        @Override public List<String> getBlockConversions() { return or(strings("blockConversions"), View.super::getBlockConversions); }

        @Override public boolean enableDropSuppression() { return or(flag("enableDropSuppression"), View.super::enableDropSuppression); }

        @Override public boolean preventMobDrops() { return or(flag("preventMobDrops"), View.super::preventMobDrops); }
    }
}
