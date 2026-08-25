package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.def.WorldTemplateDef;
import mctmods.resourcedatapackloader.pack.PackOptions;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Lang;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentTerrain {
    private static final String BOP = "BIOMESOP";
    private static final List<String> BOP_KEYS = Collections.unmodifiableList(Arrays.asList(
            "amplitude", "biomeSize", "coordinateScale", "heightScale", "lowerLimitScale", "upperLimitScale",
            "mainNoiseScaleX", "mainNoiseScaleY", "mainNoiseScaleZ", "landScheme", "tempScheme", "rainScheme",
            "generateBopOre", "generatePoisonIvy", "generateBerryBushes", "generateThorns", "generateQuicksand",
            "generateLiquidPoison", "generateHotSprings", "generateNetherHives", "generateEndFeatures"));
    private static final List<String> BOP_SIZES = Collections.unmodifiableList(Arrays.asList("tiny", "small", "medium", "large", "huge"));
    private static final List<String> BOP_HANDED_OVER = Collections.unmodifiableList(Arrays.asList(
            "seaLevel", "useCaves", "useDungeons", "dungeonChance", "useStrongholds", "useVillages", "useMineShafts",
            "useTemples", "useMonuments", "useMansions", "useRavines", "useWaterLakes", "waterLakeChance",
            "useLavaLakes", "lavaLakeChance", "useLavaOceans"));

    private ContentTerrain() {}

    public static String worldType() {
        if (ContentControl.off(ContentControl.TERRAIN)) { return ""; }
        return ContentControl.text(ContentControl.TERRAIN, "worldType", Config.worldgen.worldType).trim();
    }

    public static String worldSeed() {
        if (ContentControl.off(ContentControl.TERRAIN)) { return ""; }
        return ContentControl.text(ContentControl.TERRAIN, "worldSeed", Config.worldgen.worldSeed).trim();
    }

    public static String worldName() {
        if (ContentControl.off(ContentControl.TERRAIN)) { return ""; }
        return ContentControl.text(ContentControl.TERRAIN, "worldName", Config.worldgen.worldName).trim();
    }

    public static String worldGameMode() {
        if (ContentControl.off(ContentControl.TERRAIN)) { return ""; }
        return ContentControl.text(ContentControl.TERRAIN, "worldGameMode", Config.worldgen.worldGameMode).trim();
    }

    public static String worldSpawn() {
        if (ContentControl.off(ContentControl.TERRAIN)) { return ""; }
        return ContentControl.text(ContentControl.TERRAIN, "worldSpawn", Config.worldgen.worldSpawn).trim();
    }

    public static int worldTime() {
        if (ContentControl.off(ContentControl.TERRAIN)) { return -1; }
        return ContentControl.number(ContentControl.TERRAIN, "worldTime", Config.worldgen.worldTime);
    }

    @Nullable public static EnumDifficulty difficultyFor(int dimension) {
        if (ContentControl.off(ContentControl.TERRAIN)) { return null; }
        String[] asked = ContentControl.lines(ContentControl.TERRAIN, "worldDifficulty", Config.worldgen.worldDifficulty);
        if (asked.length == 0) { return null; }
        if (!Arrays.equals(asked, difficultyRaw)) {
            EnumDifficulty everywhere = null;
            Map<Integer, EnumDifficulty> scoped = new HashMap<>();
            for (String entry : asked) {
                String line = entry.trim();
                int split = line.indexOf('=');
                String name = split < 0 ? line : line.substring(split + 1).trim();
                EnumDifficulty found = difficultyFrom(name);
                if (found == null) {
                    ContentLog.LOGGER.error("worldDifficulty names '{}', which is not one of peaceful, easy, normal or hard, ignoring it", line);
                    continue;
                }
                if (split < 0) { everywhere = found; }
                else {
                    try { scoped.put(Integer.parseInt(line.substring(0, split).trim()), found); }
                    catch (NumberFormatException wrong) { ContentLog.LOGGER.error("worldDifficulty names '{}', whose dimension is not a whole number, ignoring it", line); }
                }
            }
            difficultyEverywhere = everywhere;
            difficultyByDimension = scoped;
            difficultyRaw = asked;
        }
        EnumDifficulty found = difficultyByDimension.get(dimension);
        return found != null ? found : difficultyEverywhere;
    }

    @Nullable private static EnumDifficulty difficultyFrom(String name) {
        for (EnumDifficulty difficulty : EnumDifficulty.values()) {
            if (difficulty.name().equalsIgnoreCase(name)) { return difficulty; }
        }
        return null;
    }

    @Nullable private static String[] difficultyRaw;
    @Nullable private static EnumDifficulty difficultyEverywhere;
    private static Map<Integer, EnumDifficulty> difficultyByDimension = Collections.emptyMap();

    public static int worldBorder() {
        if (ContentControl.off(ContentControl.TERRAIN)) { return 0; }
        return ContentControl.number(ContentControl.TERRAIN, "worldBorder", Config.worldgen.worldBorder);
    }

    @Nullable public static BlockPos spawnFrom(String written, int groundLevel) {
        String[] parts = written.split(",");
        if (parts.length != 2 && parts.length != 3) {
            ContentLog.LOGGER.error("worldSpawn is '{}', which is not written as x,z or x,y,z, so the world is spawned the way the game chooses", written);
            return null;
        }
        try {
            int x = Integer.parseInt(parts[0].trim());
            int y = parts.length == 3 ? Integer.parseInt(parts[1].trim()) : groundLevel;
            int z = Integer.parseInt(parts[parts.length - 1].trim());
            return new BlockPos(x, y, z);
        }
        catch (NumberFormatException notNumbers) {
            ContentLog.LOGGER.error("worldSpawn is '{}', which is not written as whole numbers, so the world is spawned the way the game chooses", written);
            return null;
        }
    }

    public static GameType gameModeFrom(String written) {
        if (written.equalsIgnoreCase("hardcore")) { return GameType.SURVIVAL; }
        for (GameType type : GameType.values()) {
            if (type != GameType.NOT_SET && written.equalsIgnoreCase(type.getName())) { return type; }
        }
        return GameType.NOT_SET;
    }

    public static boolean hardcoreAsked() { return worldGameMode().equalsIgnoreCase("hardcore"); }

    public static long seedFrom(String written) {
        try { return Long.parseLong(written); }
        catch (NumberFormatException notANumber) { return written.hashCode(); }
    }

    @SubscribeEvent public static void onLoginPackOptions(PlayerEvent.PlayerLoggedInEvent event) {
        if (PackOptions.worldChanged().isEmpty() || event.player.getEntityWorld().provider.getDimension() != 0) { return; }
        event.player.sendMessage(new TextComponentString(Lang.tr(event.player, "rdpl.world.packOptions", String.join(", ", PackOptions.worldChanged()))));
        PackOptions.worldTold();
    }

    @SubscribeEvent public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!Config.worldgen.tellWorldType) { return; }
        String wanted = worldType();
        World world = event.player.getEntityWorld();
        if (wanted.isEmpty() || world.provider.getDimension() != 0) { return; }
        WorldType made = world.getWorldInfo().getTerrainType();
        if (!wanted.equalsIgnoreCase(made.getName())) { return; }
        event.player.sendMessage(new TextComponentString(Lang.tr(event.player, "rdpl.world.type", Lang.vanilla(made.getTranslationKey()))));
    }

    public static boolean keeps(String worldType) {
        for (String name : ContentControl.list(ContentControl.TERRAIN, "worldTypeExceptions", Config.worldgen.worldTypeExceptions)) {
            if (name.equalsIgnoreCase(worldType)) { return true; }
        }
        return false;
    }

    public static boolean leaves(String worldType) {
        if (ContentControl.off(ContentControl.TERRAIN)) { return true; }
        String[] wanted = ContentControl.list(ContentControl.TERRAIN, "terrainWorldTypes", Config.worldgen.terrainWorldTypes);
        if (wanted.length == 0) { return false; }
        boolean listed = false;
        for (String name : wanted) {
            if (name.equalsIgnoreCase(worldType)) {
                listed = true;
                break;
            }
        }
        return listed == ContentControl.flag(ContentControl.TERRAIN, "terrainWorldTypesAreBlacklist", Config.worldgen.terrainWorldTypesAreBlacklist);
    }

    public static String merge(String existing, String worldType) {
        String options = options(worldType);
        if (options.isEmpty()) { return existing; }
        if (existing == null || existing.trim().isEmpty()) { return options; }
        JsonObject was;
        JsonObject wanted;
        try {
            was = new JsonParser().parse(existing).getAsJsonObject();
            wanted = new JsonParser().parse(options).getAsJsonObject();
        }
        catch (RuntimeException notJson) { return options; }
        for (Map.Entry<String, JsonElement> entry : wanted.entrySet()) { was.add(entry.getKey(), entry.getValue()); }
        return was.toString();
    }

    public static String options(String worldType) {
        if (leaves(worldType)) { return ""; }
        String options = ContentControl.text(ContentControl.TERRAIN, "generatorOptions", Config.worldgen.generatorOptions).trim();
        if (options.isEmpty() || !BOP.equalsIgnoreCase(worldType)) { return options; }
        return forBiomesOPlenty(options);
    }

    public static String owner(String worldType) {
        if (leaves(worldType)) { return ""; }
        String options = ContentControl.text(ContentControl.TERRAIN, "generatorOptions", Config.worldgen.generatorOptions).trim();
        if (options.isEmpty()) { return ""; }
        WorldTemplateDef template = ContentWorldTemplates.active();
        return template == null ? "the settings" : template.name;
    }

    @Nullable public static JsonObject settings(String worldType) {
        if (leaves(worldType)) { return null; }
        String options = ContentControl.text(ContentControl.TERRAIN, "generatorOptions", Config.worldgen.generatorOptions).trim();
        if (options.isEmpty()) { return null; }
        try { return new JsonParser().parse(options).getAsJsonObject(); }
        catch (RuntimeException notJson) { return null; }
    }

    private static String forBiomesOPlenty(String options) {
        JsonObject json;
        try { json = new JsonParser().parse(options).getAsJsonObject(); }
        catch (RuntimeException notJson) {
            ContentLog.LOGGER.error("The terrain settings are not something Biomes O' Plenty can read, so the world is shaped the way that mod shapes it: {}", options);
            return "";
        }
        JsonObject kept = new JsonObject();
        List<String> dropped = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (!BOP_KEYS.contains(entry.getKey())) {
                if (!BOP_HANDED_OVER.contains(entry.getKey())) { dropped.add(entry.getKey()); }
                continue;
            }
            if ("biomeSize".equals(entry.getKey())) { kept.addProperty("biomeSize", biomeSize(entry.getValue())); }
            else { kept.add(entry.getKey(), entry.getValue()); }
        }
        if (!dropped.isEmpty()) {
            Summary.info("terrain.bop.dropped", "Biomes O' Plenty shapes the land its own way and has no use for " + dropped
                    + ", so those settings do nothing on one of its worlds. It reads " + BOP_KEYS + ", and is told " + BOP_HANDED_OVER + " directly");
        }
        return kept.entrySet().isEmpty() ? "" : kept.toString();
    }

    private static String biomeSize(JsonElement value) {
        try {
            if (value.getAsJsonPrimitive().isString()) { return value.getAsString().toLowerCase(Locale.ROOT); }
            int size = Math.max(0, Math.min(BOP_SIZES.size() - 1, value.getAsInt() - 2));
            return BOP_SIZES.get(size);
        }
        catch (RuntimeException unreadable) { return "medium"; }
    }
}
