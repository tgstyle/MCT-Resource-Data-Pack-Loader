package mctmods.resourcedatapackloader.content.gate;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.card.CardIds;
import mctmods.resourcedatapackloader.content.def.GateDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.PackGeneration;
import mctmods.resourcedatapackloader.util.Says;
import mctmods.resourcedatapackloader.util.Standing;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonObject;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentGates {
    private static final Map<ResourceLocation, GateDef> DEFS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, List<GateDef>> BY_DIMENSION = new LinkedHashMap<>();
    private static final List<GateDef> USABLE = new ArrayList<>();
    private static final Map<String, ItemStack> STACKS = new HashMap<>();
    private static final ResourceLocation GATE = ResourceDataPackLoader.id("gate");
    private static final PackGeneration GENERATION = new PackGeneration();
    private static final List<String> TEAMLESS = List.of(GateDef.PLAYER, GateDef.REFUSE);
    private static final List<String> TALLIES = List.of(GateDef.PLAYER, GateDef.TEAM);
    private static final List<String> STANDINGS = List.of(GateDef.ALONE, GateDef.WITH);

    private ContentGates() {}

    public static void load() {
        if (!GENERATION.stale()) { return; }
        DEFS.clear();
        BY_DIMENSION.clear();
        USABLE.clear();
        if (Config.definitionsOff()) { return; }
        Json.eachFile(PackManager.GATES, "gate definition", (key, contents) -> {
            if (ContentRegistry.reserved(key)) { return; }
            GateDef def = parse(key, contents);
            if (def != null) { DEFS.put(key, def); }
        });
        for (GateDef def : DEFS.values()) {
            if (!ContentRegistry.available(def.requires(), def.key())) { continue; }
            USABLE.add(def);
            ResourceLocation dimension = def.dimension();
            if (dimension != null) { BY_DIMENSION.computeIfAbsent(dimension, id -> new ArrayList<>()).add(def); }
        }
        if (!USABLE.isEmpty()) { Summary.info("gates", "Guarding " + BY_DIMENSION.size() + " dimension(s) behind " + USABLE.size() + " gate(s)"); }
    }

    @Nullable private static GateDef parse(ResourceLocation key, String contents) {
        JsonObject json = Json.object(key, contents, "gate file");
        if (json == null) { return null; }
        String scope = Json.choice(json, "scope", GateDef.PLAYER, "Gate " + key, GateDef.SCOPES);
        String named = Json.getString(json, "dimension", "").trim();
        ResourceLocation dimension = named.isEmpty() ? null : ResourceLocation.tryParse(ContentFormats.dimensionId(named));
        if (!named.isEmpty() && dimension == null) {
            ContentLog.LOGGER.error("Gate {} names dimension '{}', which is not a dimension id, so it guards nothing", key, named);
            return null;
        }
        JsonObject unlock = Json.getJsonObject(json, "unlock", new JsonObject());
        String owner = "Gate " + key;
        return new GateDef(key, dimension, Json.getString(json, "name", key.getPath()), scope, Json.choice(json, "teamless", GateDef.PLAYER, owner, TEAMLESS), Json.getBoolean(json, "open", false),
                Json.getString(unlock, "craft", "").trim(), Json.getString(unlock, "consume", "").trim(), Math.max(1, Json.getInt(unlock, "consumeCount", 1)),
                Json.getString(unlock, "hold", "").trim(), Json.getString(unlock, "advancement", "").trim(), Json.getString(unlock, "killed", "").trim(),
                Math.max(1, Json.getInt(unlock, "killedCount", 1)), Json.getString(unlock, "killedDrops", "").trim(), Json.choice(unlock, "killedTally", GateDef.PLAYER, owner, TALLIES),
                Standing.parse(unlock), Json.choice(unlock, "teamAndScore", GateDef.ALONE, owner, STANDINGS), Json.strings(json, "portalBlocks"),
                Json.getString(json, "blockedMessage", "You need %item% to enter %dim%"), Json.getString(json, "unlockedMessage", "%dim% is now open"),
                Json.getBoolean(json, "safeReturn", false), Json.strings(json, "requires"));
    }

    public static boolean idle() { return USABLE.isEmpty(); }

    public static List<GateDef> forDimension(ResourceLocation dimension) { return BY_DIMENSION.getOrDefault(dimension, Collections.emptyList()); }

    public static Collection<GateDef> all() { return Collections.unmodifiableCollection(DEFS.values()); }

    @Nullable public static GateDef find(String name) {
        for (GateDef def : DEFS.values()) {
            if (def.id().equals(name) || def.key().getPath().equals(name)) { return def; }
        }
        return null;
    }

    public static boolean unlocked(ServerPlayer player, GateDef def) {
        if (turnedAway(player, def)) { return false; }
        return openFor(player, def) || def.standsAlone() && def.standing().earnedBy(player);
    }

    public static boolean bars(ServerPlayer player, GateDef def) {
        if (turnedAway(player, def)) { return true; }
        return def.standsAlone() ? !(openFor(player, def) || def.standing().earnedBy(player)) : !(openFor(player, def) && def.standing().met(player));
    }

    public static boolean admits(ServerPlayer player, GateDef def) {
        if (bars(player, def)) { return false; }
        if (def.standsAlone() && !openFor(player, def)) { unlock(player, def, true); }
        return true;
    }

    private static boolean openFor(ServerPlayer player, GateDef def) {
        if (def.open()) { return true; }
        if (!def.hold().isEmpty() && carrying(player, def.hold())) { return true; }
        if (!def.advancement().isEmpty() && earned(player, def.advancement())) { return true; }
        return GateStorage.unlocked(player, def);
    }

    private static boolean turnedAway(ServerPlayer player, GateDef def) { return def.refusesTeamless() && Standing.teamOf(player).isEmpty(); }

    public static void unlock(ServerPlayer player, GateDef def, boolean announce) {
        if (turnedAway(player, def)) { return; }
        GateStorage.unlock(player, def);
        String team = GateStorage.teamFor(player, def);
        ContentLog.LOGGER.debug("Gate {} opened for {}{}", def.key(), player.getName().getString(), def.global() ? " and everyone" : team.isEmpty() ? "" : " and team " + team);
        if (!announce || def.unlockedMessage().isEmpty()) { return; }
        String message = def.unlockedMessage().replace("%dim%", def.name()).replace("%player%", player.getName().getString());
        if (def.global() || !team.isEmpty()) {
            for (ServerPlayer online : player.server.getPlayerList().getPlayers()) {
                if (team.isEmpty() || team.equals(Standing.teamOf(online))) { Says.line(online, CardIds.GATE_UNLOCKED, ChatFormatting.GREEN, message); }
            }
        }
        else { Says.line(player, CardIds.GATE_UNLOCKED, ChatFormatting.GREEN, message); }
    }

    public static void lock(ServerPlayer player, GateDef def) { GateStorage.lock(player, def); }

    public static void refuse(ServerPlayer player, GateDef def) {
        if (def.blockedMessage().isEmpty()) { return; }
        String needed = def.consume().isEmpty() ? def.craft() : def.consume();
        if (needed.isEmpty()) { needed = def.hold(); }
        String[] parts = def.blockedMessage().replace("%dim%", def.name()).split("%item%", -1);
        MutableComponent message = Component.literal(parts[0]);
        for (int at = 1; at < parts.length; at++) { message.append(describe(needed)).append(parts[at]); }
        Says.bar(player, CardIds.GATE_BLOCKED, ChatFormatting.RED, message);
    }

    public static boolean carrying(ServerPlayer player, String item) {
        ItemStack wanted = stack(item);
        if (wanted.isEmpty()) { return false; }
        for (ItemStack held : player.getInventory().items) {
            if (ContentStacks.matches(held, wanted)) { return true; }
        }
        return ContentStacks.matches(player.getInventory().offhand.getFirst(), wanted);
    }

    private static boolean earned(ServerPlayer player, String name) {
        MinecraftServer server = player.getServer();
        ResourceLocation id = ResourceLocation.tryParse(name);
        if (server == null || id == null) { return false; }
        AdvancementHolder advancement = server.getAdvancements().get(id);
        return advancement != null && player.getAdvancements().getOrStartProgress(advancement).isDone();
    }

    private static Component describe(String item) {
        if (item.isEmpty()) { return Component.literal("something"); }
        ItemStack stack = stack(item);
        return stack.isEmpty() ? Component.literal(item) : stack.getHoverName();
    }

    public static ItemStack stack(String item) { return STACKS.computeIfAbsent(item, held -> ContentStacks.parse(GATE, held, 1)); }
}
