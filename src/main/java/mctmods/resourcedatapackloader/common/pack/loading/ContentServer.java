package mctmods.resourcedatapackloader.common.pack.loading;

import mctmods.resourcedatapackloader.common.util.dimension.DimensionValues;
import mctmods.resourcedatapackloader.common.worldgen.surface.ContentTerrain;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

public final class ContentServer {
    private static final Set<String> WARNED = ConcurrentHashMap.newKeySet();
    private static final DimensionValues<Difficulty> DIFFICULTY = new DimensionValues<>("worldDifficulty", ContentServer::difficultyFrom, "which is not one of peaceful, easy, normal or hard");
    private static final List<Setting<?>> SETTINGS = List.of(
        new Setting<>("force-gamemode", ContentServer::forceGameMode, null),
        new Setting<>("pvp", ContentServer::pvp, null),
        new Setting<>("allow-flight", ContentServer::flight, null),
        new Setting<>("spawn-protection", () -> count(spawnProtection()), null),
        new Setting<>("player-idle-timeout", () -> count(idleTimeout()), MinecraftServer::setPlayerIdleTimeout),
        new Setting<>("motd", () -> words(motd()), MinecraftServer::setMotd),
        new Setting<>("max-world-size", () -> count(maxSize()), null),
        new Setting<>("generate-structures", ContentServer::structures, null),
        new Setting<>("view-distance", () -> count(viewDistance()), null),
        new Setting<>("simulation-distance", () -> count(simulationDistance()), null));

    private ContentServer() {}

    @Nullable public static Difficulty difficultyFrom(String name) { return Difficulty.byName(name.toLowerCase(Locale.ROOT)); }

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
        ContentLog.LOGGER.errorOnce(WARNED, key, "{} is '{}', which is not true or false, so the server keeps its own", key, held);
        return null;
    }

    public static boolean privacy() { return Config.tweaks.privacy(); }

    public static boolean chatUnsigned() { return !Config.tweaks.chatReporting(); }

    public static String worldGameMode() { return text("worldGameMode", Config.worldgen.worldGameMode()); }

    public static boolean lanCommands() { return ContentControl.off(ContentControl.SERVER) || ContentControl.flag(ContentControl.SERVER, "worldLanCommands", Config.worldgen.worldLanCommands()); }

    @Nullable public static Difficulty difficultyFor(String dimension) {
        if (ContentControl.off(ContentControl.SERVER)) { return null; }
        return DIFFICULTY.at(dimension, ContentControl.lines(ContentControl.SERVER, "worldDifficulty", Config.worldgen.worldDifficulty()));
    }

    @Nullable public static Boolean forceGameMode() { return choice("worldForceGameMode", Config.worldgen.worldForceGameMode()); }

    @Nullable public static Boolean pvp() { return choice("worldPvp", Config.worldgen.worldPvp()); }

    @Nullable public static Boolean flight() { return choice("worldFlight", Config.worldgen.worldFlight()); }

    public static boolean off(@Nullable Boolean choice) { return Boolean.FALSE.equals(choice); }

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
        for (Setting<?> setting : SETTINGS) { setting.into(asked); }
        Difficulty difficulty = difficultyFor(Level.OVERWORLD.identifier().toString());
        if (difficulty != null) { asked.put("difficulty", difficulty.getSerializedName()); }
        ContentTerrain.properties(asked, levelType);
        return asked;
    }

    @Nullable private static Integer count(int value) { return value >= 0 ? value : null; }

    @Nullable private static String words(String value) { return value.isEmpty() ? null : value; }

    public static void onAboutToStart(ServerAboutToStartEvent event) {
        if (!(event.getServer() instanceof DedicatedServer server)) { return; }
        writeGameMode(server);
        writeProperties(server);
        applyTo(server);
    }

    public static void onStarting(ServerStartingEvent event) { if (!event.getServer().isDedicatedServer()) { applyTo(event.getServer()); } }

    private static void writeGameMode(DedicatedServer server) {
        String mode = Settings.lower(worldGameMode());
        boolean hardcore = "hardcore".equals(mode);
        GameType asked = hardcore ? GameType.SURVIVAL : GameType.byName(mode, null);
        if (asked == null) { return; }
        DedicatedServerProperties held = server.settings.getProperties();
        if (held.gameMode.get() == asked && held.hardcore == hardcore) { return; }
        ContentLog.LOGGER.info("server.properties plays {} and a pack asks for {}, so the pack's mode is written to server.properties and every world plays it", held.hardcore ? "hardcore" : held.gameMode.get().getName(), hardcore ? "hardcore" : asked.getName());
        write(server, Map.of("gamemode", asked.getName(), "hardcore", Boolean.toString(hardcore)));
        server.getWorldData().setGameType(asked);
    }

    private static void writeProperties(DedicatedServer server) {
        Properties held = server.settings.getProperties().cloneProperties();
        Map<String, String> asked = properties(held.getProperty("level-type", ""));
        List<String> written = new ArrayList<>();
        for (Map.Entry<String, String> line : asked.entrySet()) {
            if (!line.getValue().equals(held.getProperty(line.getKey()))) { written.add(line.getKey() + "=" + line.getValue()); }
        }
        if (written.isEmpty()) { return; }
        write(server, asked);
        ContentLog.LOGGER.info("A pack sets {} line(s) of server.properties, so they are written there: {}", written.size(), String.join(", ", written));
    }

    private static void write(DedicatedServer server, Map<String, String> lines) {
        server.settings.update(current -> {
            Properties changed = current.cloneProperties();
            lines.forEach(changed::setProperty);
            return new DedicatedServerProperties(changed);
        });
    }

    public static void applyTo(MinecraftServer server) {
        for (Setting<?> setting : SETTINGS) { setting.apply(server); }
        if (!server.isDedicatedServer()) { return; }
        int view = viewDistance();
        if (view >= 0) { server.getPlayerList().setViewDistance(view); }
        int simulation = simulationDistance();
        if (simulation >= 0) { server.getPlayerList().setSimulationDistance(simulation); }
    }

    private record Setting<T>(String key, Supplier<T> read, @Nullable BiConsumer<MinecraftServer, T> live) {
        private void into(Map<String, String> asked) {
            T value = read.get();
            if (value != null) { asked.put(key, value.toString()); }
        }

        private void apply(MinecraftServer server) {
            if (live == null) { return; }
            T value = read.get();
            if (value != null) { live.accept(server, value); }
        }
    }
}
