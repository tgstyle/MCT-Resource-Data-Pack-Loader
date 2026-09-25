package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.content.worldgen.ContentTerrain;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.DimensionValues;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public final class ContentServer {
    private static final Set<String> WARNED = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private static final DimensionValues<EnumDifficulty> DIFFICULTY = new DimensionValues<>("worldDifficulty", ContentServer::difficultyFrom, "which is not one of peaceful, easy, normal or hard");

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

    public static String worldGameMode() { return text("worldGameMode", Config.worldgen.worldGameMode); }

    public static boolean hardcoreAsked() { return worldGameMode().equalsIgnoreCase("hardcore"); }

    public static GameType gameModeFrom(String written) {
        if (written.equalsIgnoreCase("hardcore")) { return GameType.SURVIVAL; }
        for (GameType type : GameType.values()) {
            if (type != GameType.NOT_SET && written.equalsIgnoreCase(type.getName())) { return type; }
        }
        return GameType.NOT_SET;
    }

    public static boolean lanCommands() { return ContentControl.off(ContentControl.SERVER) || ContentControl.flag(ContentControl.SERVER, "worldLanCommands", Config.worldgen.worldLanCommands); }

    @Nullable public static EnumDifficulty difficultyFor(int dimension) {
        if (ContentControl.off(ContentControl.SERVER)) { return null; }
        return DIFFICULTY.at(dimension, ContentControl.lines(ContentControl.SERVER, "worldDifficulty", Config.worldgen.worldDifficulty));
    }

    @Nullable private static EnumDifficulty difficultyFrom(String name) {
        for (EnumDifficulty difficulty : EnumDifficulty.values()) {
            if (difficulty.name().equalsIgnoreCase(name)) { return difficulty; }
        }
        return null;
    }

    @Nullable public static Boolean forceGameMode() { return choice("worldForceGameMode", Config.worldgen.worldForceGameMode); }

    @Nullable public static Boolean pvp() { return choice("worldPvp", Config.worldgen.worldPvp); }

    @Nullable public static Boolean flight() { return choice("worldFlight", Config.worldgen.worldFlight); }

    @Nullable public static Boolean nether() { return choice("worldNether", Config.worldgen.worldNether); }

    @Nullable public static Boolean commandBlocks() { return choice("worldCommandBlocks", Config.worldgen.worldCommandBlocks); }

    @Nullable public static Boolean structures() { return choice("worldStructures", Config.worldgen.worldStructures); }

    @Nullable public static Boolean spawnMonsters() { return choice("worldSpawnMonsters", Config.worldgen.worldSpawnMonsters); }

    @Nullable public static Boolean spawnAnimals() { return choice("worldSpawnAnimals", Config.worldgen.worldSpawnAnimals); }

    @Nullable public static Boolean spawnNpcs() { return choice("worldSpawnNpcs", Config.worldgen.worldSpawnNpcs); }

    public static String motd() { return text("worldMotd", Config.worldgen.worldMotd); }

    public static int spawnProtection() { return number("worldSpawnProtection", Config.worldgen.worldSpawnProtection); }

    public static int idleTimeout() { return number("worldIdleTimeout", Config.worldgen.worldIdleTimeout); }

    public static int maxSize() { return number("worldMaxSize", Config.worldgen.worldMaxSize); }

    public static int viewDistance() { return number("worldViewDistance", Config.worldgen.worldViewDistance); }

    public static int buildHeight() {
        int asked = number("worldBuildHeight", Config.worldgen.worldBuildHeight);
        return asked < 0 ? -1 : MathHelper.clamp((asked + 8) / 16 * 16, 64, 256);
    }

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
        put(asked, "max-build-height", buildHeight());
        EnumDifficulty difficulty = difficultyFor(0);
        if (difficulty != null) { asked.put("difficulty", Integer.toString(difficulty.getId())); }
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
        if (pvp != null) { server.setAllowPvp(pvp); }
        Boolean flight = flight();
        if (flight != null) { server.setAllowFlight(flight); }
        Boolean animals = spawnAnimals();
        if (animals != null) { server.setCanSpawnAnimals(animals); }
        Boolean npcs = spawnNpcs();
        if (npcs != null) { server.setCanSpawnNPCs(npcs); }
        Boolean forced = forceGameMode();
        if (forced != null) { server.setForceGamemode(forced); }
        int idle = idleTimeout();
        if (idle >= 0) { server.setPlayerIdleTimeout(idle); }
        String motd = motd();
        if (!motd.isEmpty()) { server.setMOTD(motd); }
        int height = buildHeight();
        if (height >= 0) { server.setBuildLimit(height); }
    }
}
