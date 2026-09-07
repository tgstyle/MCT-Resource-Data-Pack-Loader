package mctmods.resourcedatapackloader.content.gate;

import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.ContentStacks;
import mctmods.resourcedatapackloader.content.def.GateDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Says;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentGates {
    private static final Gson GSON = new Gson();
    private static final Map<ResourceLocation, GateDef> DEFS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, List<GateDef>> BY_DIMENSION = new LinkedHashMap<>();
    private static final Map<String, ItemStack> STACKS = new HashMap<>();
    private static final ResourceLocation GATE = ResourceLocation.fromNamespaceAndPath("rdpl", "gate");
    private static boolean loaded;

    private ContentGates() {}

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        if (Config.contentOff()) { return; }
        Json.eachFile(PackManager.GATES, "gate definition", (key, contents) -> {
            if (ContentRegistry.reserved(key)) { return; }
            GateDef def = parse(key, contents);
            if (def != null) { DEFS.put(key, def); }
        });
        for (GateDef def : DEFS.values()) {
            if (!ContentRegistry.available(def.requires(), def.key())) { continue; }
            BY_DIMENSION.computeIfAbsent(def.dimension(), id -> new ArrayList<>()).add(def);
        }
        if (!BY_DIMENSION.isEmpty()) { Summary.info("gates", "Guarding " + BY_DIMENSION.size() + " dimension(s) behind " + DEFS.size() + " gate(s)"); }
    }

    @Nullable private static GateDef parse(ResourceLocation key, String contents) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null) { return null; }
        String scope = GsonHelper.getAsString(json, "scope", GateDef.PLAYER).trim().toLowerCase(Locale.ROOT);
        if (!GateDef.PLAYER.equals(scope) && !GateDef.GLOBAL.equals(scope)) {
            ContentLog.LOGGER.error("Gate {} asks for scope '{}', which is not {} or {}, using {}", key, scope, GateDef.PLAYER, GateDef.GLOBAL, GateDef.PLAYER);
            scope = GateDef.PLAYER;
        }
        String named = GsonHelper.getAsString(json, "dimension", "").trim();
        ResourceLocation dimension = named.isEmpty() ? null : ResourceLocation.tryParse(ContentFormats.dimensionId(named));
        if (dimension == null) {
            ContentLog.LOGGER.error("Gate {} names dimension '{}', which is not a dimension id, so it guards nothing", key, named);
            return null;
        }
        JsonObject unlock = GsonHelper.getAsJsonObject(json, "unlock", new JsonObject());
        return new GateDef(key, dimension, GsonHelper.getAsString(json, "name", key.getPath()), GateDef.GLOBAL.equals(scope), GsonHelper.getAsBoolean(json, "open", false),
                GsonHelper.getAsString(unlock, "craft", "").trim(), GsonHelper.getAsString(unlock, "consume", "").trim(), Math.max(1, GsonHelper.getAsInt(unlock, "consumeCount", 1)),
                GsonHelper.getAsString(unlock, "hold", "").trim(), GsonHelper.getAsString(unlock, "advancement", "").trim(), GsonHelper.getAsString(unlock, "killed", "").trim(),
                Math.max(1, GsonHelper.getAsInt(unlock, "killedCount", 1)), GsonHelper.getAsString(unlock, "killedDrops", "").trim(), Json.strings(json, "portalBlocks"),
                GsonHelper.getAsString(json, "blockedMessage", "You need %item% to enter %dim%"), GsonHelper.getAsString(json, "unlockedMessage", "%dim% is now open"),
                GsonHelper.getAsBoolean(json, "safeReturn", false), Json.strings(json, "requires"));
    }

    public static boolean enabled() { return !BY_DIMENSION.isEmpty(); }

    public static List<GateDef> forDimension(ResourceLocation dimension) { return BY_DIMENSION.getOrDefault(dimension, Collections.emptyList()); }

    public static Collection<GateDef> all() { return Collections.unmodifiableCollection(DEFS.values()); }

    @Nullable public static GateDef find(String name) {
        for (GateDef def : DEFS.values()) {
            if (def.id().equals(name) || def.key().getPath().equals(name)) { return def; }
        }
        return null;
    }

    public static boolean unlocked(ServerPlayer player, GateDef def) {
        if (def.open()) { return true; }
        if (!def.hold().isEmpty() && carrying(player, def.hold())) { return true; }
        if (!def.advancement().isEmpty() && earned(player, def.advancement())) { return true; }
        if (def.global()) { return GateStorage.unlockedGlobally(player.server, def.id()); }
        return GateStorage.unlockedFor(player, def.id());
    }

    public static void unlock(ServerPlayer player, GateDef def, boolean announce) {
        if (def.global()) { GateStorage.unlockGlobally(player.server, def.id()); }
        else { GateStorage.unlockFor(player, def.id()); }
        ContentLog.LOGGER.debug("Gate {} opened for {}{}", def.key(), player.getName().getString(), def.global() ? " and everyone" : "");
        if (!announce || def.unlockedMessage().isEmpty()) { return; }
        String message = def.unlockedMessage().replace("%dim%", def.name()).replace("%player%", player.getName().getString());
        if (def.global()) {
            for (ServerPlayer online : player.server.getPlayerList().getPlayers()) { Says.tell(online, message, ChatFormatting.GREEN); }
        }
        else { Says.tell(player, message, ChatFormatting.GREEN); }
    }

    public static void lock(ServerPlayer player, GateDef def) {
        if (def.global()) { GateStorage.lockGlobally(player.server, def.id()); }
        else { GateStorage.lockFor(player, def.id()); }
    }

    public static void refuse(ServerPlayer player, GateDef def) {
        if (def.blockedMessage().isEmpty()) { return; }
        String needed = def.consume().isEmpty() ? def.craft() : def.consume();
        if (needed.isEmpty()) { needed = def.hold(); }
        String message = def.blockedMessage().replace("%dim%", def.name()).replace("%item%", describe(needed));
        player.displayClientMessage(Component.literal(message).withStyle(ChatFormatting.RED), true);
    }

    public static boolean carrying(ServerPlayer player, String item) {
        ItemStack wanted = stack(item);
        if (wanted.isEmpty()) { return false; }
        for (ItemStack held : player.getInventory().items) {
            if (ContentStacks.matches(held, wanted)) { return true; }
        }
        return ContentStacks.matches(player.getInventory().offhand.get(0), wanted);
    }

    private static boolean earned(ServerPlayer player, String name) {
        MinecraftServer server = player.getServer();
        ResourceLocation id = ResourceLocation.tryParse(name);
        if (server == null || id == null) { return false; }
        Advancement advancement = server.getAdvancements().getAdvancement(id);
        return advancement != null && player.getAdvancements().getOrStartProgress(advancement).isDone();
    }

    private static String describe(String item) {
        if (item.isEmpty()) { return "something"; }
        ItemStack stack = stack(item);
        return stack.isEmpty() ? item : stack.getHoverName().getString();
    }

    public static ItemStack stack(String item) { return STACKS.computeIfAbsent(item, held -> ContentStacks.parse(GATE, held, 1)); }
}
