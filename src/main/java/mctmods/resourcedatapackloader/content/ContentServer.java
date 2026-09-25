package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.worldgen.ContentTerrain;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.DimensionValues;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.Level;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public final class ContentServer {
    private static final Set<String> WARNED = ConcurrentHashMap.newKeySet();
    private static final DimensionValues<Difficulty> DIFFICULTY = new DimensionValues<>("worldDifficulty", named -> Difficulty.byName(named.toLowerCase(Locale.ROOT)), "which is not one of peaceful, easy, normal or hard");

    private ContentServer() {}

    private static String text(String key, String fallback) {
        if (ContentControl.off(ContentControl.SERVER)) { return ""; }
        return ContentControl.text(ContentControl.SERVER, key, fallback).trim();
    }

    private static int number(String key, int fallback) {
        if (ContentControl.off(ContentControl.SERVER)) { return -1; }
        return ContentControl.number(ContentControl.SERVER, key, fallback);
    }

    @Nullable private static Boolean choice(String key, String fallback) {
        String held = text(key, fallback);
        if (held.isEmpty()) { return null; }
        if ("true".equalsIgnoreCase(held)) { return Boolean.TRUE; }
        if ("false".equalsIgnoreCase(held)) { return Boolean.FALSE; }
        if (WARNED.add(key)) { ContentLog.LOGGER.error("{} is '{}', which is not true or false, so the server keeps its own", key, held); }
        return null;
    }

    public static String worldGameMode() { return text("worldGameMode", Config.worldgen.worldGameMode()); }

    public static boolean lanCommands() { return ContentControl.off(ContentControl.SERVER) || ContentControl.flag(ContentControl.SERVER, "worldLanCommands", Config.worldgen.worldLanCommands()); }

    @Nullable public static Difficulty difficultyFor(String dimension) {
        if (ContentControl.off(ContentControl.SERVER)) { return null; }
        return DIFFICULTY.at(dimension, ContentControl.lines(ContentControl.SERVER, "worldDifficulty", Config.worldgen.worldDifficulty()));
    }

    @Nullable public static Boolean forceGameMode() { return choice("worldForceGameMode", Config.worldgen.worldForceGameMode()); }

    @Nullable public static Boolean pvp() { return choice("worldPvp", Config.worldgen.worldPvp()); }

    @Nullable public static Boolean flight() { return choice("worldFlight", Config.worldgen.worldFlight()); }

    @Nullable public static Boolean nether() { return choice("worldNether", Config.worldgen.worldNether()); }

    @Nullable public static Boolean commandBlocks() { return choice("worldCommandBlocks", Config.worldgen.worldCommandBlocks()); }

    @Nullable public static Boolean structures() { return choice("worldStructures", Config.worldgen.worldStructures()); }

    @Nullable public static Boolean spawnMonsters() { return choice("worldSpawnMonsters", Config.worldgen.worldSpawnMonsters()); }

    @Nullable public static Boolean spawnAnimals() { return choice("worldSpawnAnimals", Config.worldgen.worldSpawnAnimals()); }

    @Nullable public static Boolean spawnNpcs() { return choice("worldSpawnNpcs", Config.worldgen.worldSpawnNpcs()); }

    public static String motd() { return text("worldMotd", Config.worldgen.worldMotd()); }

    public static int spawnProtection() { return number("worldSpawnProtection", Config.worldgen.worldSpawnProtection()); }

    public static int idleTimeout() { return number("worldIdleTimeout", Config.worldgen.worldIdleTimeout()); }

    public static int maxSize() { return number("worldMaxSize", Config.worldgen.worldMaxSize()); }

    public static int viewDistance() { return number("worldViewDistance", Config.worldgen.worldViewDistance()); }

    public static int simulationDistance() { return number("worldSimulationDistance", Config.worldgen.worldSimulationDistance()); }

    public static Map<String, String> properties(String levelType) {
        Map<String, String> asked = new LinkedHashMap<>();
        put(asked, "force-gamemode", forceGameMode());
        put(asked, "pvp", pvp());
        put(asked, "allow-flight", flight());
        put(asked, "spawn-protection", spawnProtection());
        put(asked, "allow-nether", nether());
        put(asked, "enable-command-block", commandBlocks());
        put(asked, "player-idle-timeout", idleTimeout());
        if (!motd().isEmpty()) { asked.put("motd", motd()); }
        put(asked, "max-world-size", maxSize());
        put(asked, "generate-structures", structures());
        put(asked, "spawn-monsters", spawnMonsters());
        put(asked, "spawn-animals", spawnAnimals());
        put(asked, "spawn-npcs", spawnNpcs());
        put(asked, "view-distance", viewDistance());
        put(asked, "simulation-distance", simulationDistance());
        Difficulty difficulty = difficultyFor(Level.OVERWORLD.location().toString());
        if (difficulty != null) { asked.put("difficulty", difficulty.getKey()); }
        ContentTerrain.properties(asked, levelType);
        return asked;
    }

    private static void put(Map<String, String> asked, String key, @Nullable Boolean value) {
        if (value != null) { asked.put(key, value.toString()); }
    }

    private static void put(Map<String, String> asked, String key, int value) {
        if (value >= 0) { asked.put(key, Integer.toString(value)); }
    }

    public static void applyTo(MinecraftServer server) {
        Boolean pvp = pvp();
        if (pvp != null) { server.setPvpAllowed(pvp); }
        Boolean flight = flight();
        if (flight != null) { server.setFlightAllowed(flight); }
        int idle = idleTimeout();
        if (idle >= 0) { server.setPlayerIdleTimeout(idle); }
        String motd = motd();
        if (!motd.isEmpty()) { server.setMotd(motd); }
        if (!server.isDedicatedServer()) { return; }
        int view = viewDistance();
        if (view >= 0) { server.getPlayerList().setViewDistance(view); }
        int simulation = simulationDistance();
        if (simulation >= 0) { server.getPlayerList().setSimulationDistance(simulation); }
    }
}
