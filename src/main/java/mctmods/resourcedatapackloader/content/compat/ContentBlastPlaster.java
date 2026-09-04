package mctmods.resourcedatapackloader.content.compat;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.Enums;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.PackGeneration;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import net.minecraft.world.World;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ContentBlastPlaster {
    private static final Map<String, JsonElement> EVERYWHERE = new LinkedHashMap<>();
    private static final Map<Integer, Map<String, JsonElement>> PER_DIMENSION = new LinkedHashMap<>();
    private static final Map<Integer, mctmods.blastplaster.Config.View> RESOLVED = new ConcurrentHashMap<>();
    private static final PackGeneration GENERATION = new PackGeneration();

    private ContentBlastPlaster() {}

    public static void install() {
        if (ContentControl.off(ContentControl.BLAST_PLASTER)) { return; }
        mctmods.blastplaster.Config.provider(ContentBlastPlaster::viewFor);
        ContentLog.LOGGER.info("Driving Blast Plaster through pack settings, EJECT_DROPS unless a pack says otherwise");
    }

    private static mctmods.blastplaster.Config.View viewFor(World world) {
        load();
        return RESOLVED.computeIfAbsent(world.provider.getDimension(), ContentBlastPlaster::resolve);
    }

    private static mctmods.blastplaster.Config.View resolve(int dimension) {
        Map<String, JsonElement> settings = new LinkedHashMap<>(EVERYWHERE);
        Map<String, JsonElement> dimensional = PER_DIMENSION.get(dimension);
        if (dimensional != null) { settings.putAll(dimensional); }
        return new PackView(settings);
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
        PackManager.get().forEach(PackManager.BLASTPLASTER, PackManager.JSON, (namespace, path, contents) -> {
            try {
                JsonObject held = new JsonParser().parse(contents).getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : held.entrySet()) {
                    if (!"dimensions".equals(entry.getKey())) {
                        EVERYWHERE.put(entry.getKey(), entry.getValue());
                        continue;
                    }
                    for (Map.Entry<String, JsonElement> given : entry.getValue().getAsJsonObject().entrySet()) {
                        int dimension = Integer.parseInt(given.getKey().trim());
                        Map<String, JsonElement> section = PER_DIMENSION.computeIfAbsent(dimension, unused -> new LinkedHashMap<>());
                        for (Map.Entry<String, JsonElement> inner : given.getValue().getAsJsonObject().entrySet()) { section.put(inner.getKey(), inner.getValue()); }
                    }
                }
                read[0]++;
            }
            catch (JsonParseException | IllegalStateException | NumberFormatException ex) { ContentLog.LOGGER.error("Parsing error in Blast Plaster settings file {}:{}, ignoring it", namespace, path, ex); }
        });
        if (read[0] > 0) { Summary.info("blastplaster", "Loaded " + read[0] + " Blast Plaster settings file(s) from packs"); }
    }

    private static final class PackView implements mctmods.blastplaster.Config.View {
        private final Map<String, JsonElement> settings;

        private PackView(Map<String, JsonElement> settings) { this.settings = settings; }

        private Boolean flag(String key) {
            JsonElement held = settings.get(key);
            if (held == null || !held.isJsonPrimitive() || !held.getAsJsonPrimitive().isBoolean()) { return null; }
            return held.getAsBoolean();
        }

        private Integer number(String key) {
            JsonElement held = settings.get(key);
            if (held == null || !held.isJsonPrimitive() || !held.getAsJsonPrimitive().isNumber()) { return null; }
            return held.getAsInt();
        }

        @Override public mctmods.blastplaster.Config.ExplosionMode getExplosionMode() {
            JsonElement held = settings.get("explosionMode");
            if (held != null && held.isJsonPrimitive() && held.getAsJsonPrimitive().isString()) {
                mctmods.blastplaster.Config.ExplosionMode mode = Enums.byName(mctmods.blastplaster.Config.ExplosionMode.class, held.getAsString());
                if (mode != null) { return mode; }
                ContentLog.LOGGER.error("Blast Plaster explosionMode '{}' is not HEAL, EJECT_DROPS or VISUAL_TOSS, using EJECT_DROPS", held.getAsString());
            }
            return mctmods.blastplaster.Config.ExplosionMode.EJECT_DROPS;
        }

        @Override public boolean enableFakeTossedBlocks() {
            Boolean held = flag("enableFakeTossedBlocks");
            return held != null ? held : mctmods.blastplaster.Config.View.super.enableFakeTossedBlocks();
        }

        @Override public boolean enableExplosionFlash() {
            if (Config.content.vanillaClients) { return false; }
            Boolean held = flag("enableExplosionFlash");
            return held != null ? held : mctmods.blastplaster.Config.View.super.enableExplosionFlash();
        }

        @Override public int getExplosionFlashDuration() {
            Integer held = number("explosionFlashDuration");
            return held != null ? held : mctmods.blastplaster.Config.View.super.getExplosionFlashDuration();
        }

        @Override public int getExplosionFlashLightLevel() {
            Integer held = number("explosionFlashLightLevel");
            return held != null ? held : mctmods.blastplaster.Config.View.super.getExplosionFlashLightLevel();
        }

        @Override public int getExplosionFlashParticleCount() {
            Integer held = number("explosionFlashParticleCount");
            return held != null ? held : mctmods.blastplaster.Config.View.super.getExplosionFlashParticleCount();
        }

        @Override public int getExplosionFlashPulses() {
            Integer held = number("explosionFlashPulses");
            return held != null ? held : mctmods.blastplaster.Config.View.super.getExplosionFlashPulses();
        }

        @Override public boolean enableExplosionSmoke() {
            Boolean held = flag("enableExplosionSmoke");
            return held != null ? held : mctmods.blastplaster.Config.View.super.enableExplosionSmoke();
        }

        @Override public int getExplosionSmokeDuration() {
            Integer held = number("explosionSmokeDuration");
            return held != null ? held : mctmods.blastplaster.Config.View.super.getExplosionSmokeDuration();
        }

        @Override public int getExplosionSmokeParticleCount() {
            Integer held = number("explosionSmokeParticleCount");
            return held != null ? held : mctmods.blastplaster.Config.View.super.getExplosionSmokeParticleCount();
        }

        @Override public boolean playerTNTAlwaysDrops() {
            Boolean held = flag("playerTNTAlwaysDrops");
            return held != null ? held : mctmods.blastplaster.Config.View.super.playerTNTAlwaysDrops();
        }

        @Override public boolean playerTNTDropFullBlocks() {
            Boolean held = flag("playerTNTDropFullBlocks");
            return held != null ? held : mctmods.blastplaster.Config.View.super.playerTNTDropFullBlocks();
        }

        @Override public boolean healCreepers() {
            Boolean held = flag("healCreepers");
            return held != null ? held : mctmods.blastplaster.Config.View.super.healCreepers();
        }

        @Override public boolean healNonPlayerTNT() {
            Boolean held = flag("healNonPlayerTNT");
            return held != null ? held : mctmods.blastplaster.Config.View.super.healNonPlayerTNT();
        }

        @Override public boolean healWither() {
            Boolean held = flag("healWither");
            return held != null ? held : mctmods.blastplaster.Config.View.super.healWither();
        }

        @Override public boolean healAll() {
            Boolean held = flag("healAll");
            return held != null ? held : mctmods.blastplaster.Config.View.super.healAll();
        }

        @Override public boolean processPlayerIgnitedTNT() {
            Boolean held = flag("processPlayerIgnitedTNT");
            return held != null ? held : mctmods.blastplaster.Config.View.super.processPlayerIgnitedTNT();
        }

        @Override public List<String> getCustomEntitiesToHeal() {
            JsonElement held = settings.get("customEntitiesToHeal");
            if (held == null || !held.isJsonArray()) { return mctmods.blastplaster.Config.View.super.getCustomEntitiesToHeal(); }
            List<String> given = new ArrayList<>();
            for (JsonElement entry : held.getAsJsonArray()) {
                if (entry.isJsonPrimitive() && entry.getAsJsonPrimitive().isString()) { given.add(entry.getAsString()); }
            }
            return given;
        }

        @Override public int getMinimumTicksBeforeHeal() {
            Integer held = number("minimumTicksBeforeHeal");
            return held != null ? held : mctmods.blastplaster.Config.View.super.getMinimumTicksBeforeHeal();
        }

        @Override public int getRandomTickVar() {
            Integer held = number("randomTickVar");
            return held != null ? held : mctmods.blastplaster.Config.View.super.getRandomTickVar();
        }

        @Override public boolean isOverride() {
            Boolean held = flag("overrideBlocks");
            return held != null ? held : mctmods.blastplaster.Config.View.super.isOverride();
        }

        @Override public boolean healFullTrees() {
            Boolean held = flag("healFullTrees");
            return held != null ? held : mctmods.blastplaster.Config.View.super.healFullTrees();
        }

        @Override public boolean dtSpecialDrops() {
            Boolean held = flag("dtSpecialDrops");
            return held != null ? held : mctmods.blastplaster.Config.View.super.dtSpecialDrops();
        }

        @Override public int getMaxTreeSize() {
            Integer held = number("maxTreeSize");
            return held != null ? held : mctmods.blastplaster.Config.View.super.getMaxTreeSize();
        }

        @Override public boolean enableDropSuppression() {
            Boolean held = flag("enableDropSuppression");
            return held != null ? held : mctmods.blastplaster.Config.View.super.enableDropSuppression();
        }

        @Override public boolean preventMobDrops() {
            Boolean held = flag("preventMobDrops");
            return held != null ? held : mctmods.blastplaster.Config.View.super.preventMobDrops();
        }
    }
}
