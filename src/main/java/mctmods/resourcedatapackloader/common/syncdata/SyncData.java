package mctmods.resourcedatapackloader.common.syncdata;

import mctmods.resourcedatapackloader.common.util.world.SavedTypes;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.SavedDataStorage;

public final class SyncData extends SavedData {
    public static final int MAX_VALUES = 64;
    public static final int MAX_TEXT = 256;
    static final String WORLD_KEY = "w:";
    static final String PLAYER_KEY = "p:";
    static final Map<UUID, Set<String>> QUEUE = new HashMap<>();
    private static final String NAME = "rdpl_synced_data";
    private static final String WORLD = "world";
    private static final String PLAYERS = "players";
    private static final String THE_WORLD = "the world";
    private static final Pattern VALID = Pattern.compile("[a-z0-9_.:/-]{1,64}");
    private static final SavedDataType<SyncData> TYPE = SavedTypes.of(NAME, SyncData::new, SyncData::read, SyncData::write);
    @Nullable private static SyncData loaded;
    private final Map<String, SyncValue> world = new LinkedHashMap<>();
    private final Map<UUID, Map<String, SyncValue>> players = new HashMap<>();

    public static boolean valid(String name) { return VALID.matcher(name).matches(); }

    @Nullable public static SyncData get(Level world) {
        SavedDataStorage storage = storage(world);
        loaded = storage == null ? null : storage.computeIfAbsent(TYPE);
        return loaded;
    }

    @Nullable public static SyncData peek(Level world) {
        if (loaded != null) { return loaded; }
        SavedDataStorage storage = storage(world);
        loaded = storage == null ? null : storage.get(TYPE);
        return loaded;
    }

    @Nullable private static SavedDataStorage storage(Level world) {
        MinecraftServer server = world.getServer();
        return server == null ? null : server.overworld().getDataStorage();
    }

    public static boolean set(Level world, @Nullable Player player, String name, Object value, String asking) {
        if (!valid(name)) {
            ContentLog.LOGGER.error("{} sets the synced value {}, which is not a valid name, so nothing is set", asking, name);
            return false;
        }
        SyncValue given = SyncValue.of(value);
        if (given == null) {
            ContentLog.LOGGER.error("{} sets the synced value {} to a {}; synced data holds text, numbers and true/false only, so nothing is set", asking, name, value.getClass().getSimpleName());
            return false;
        }
        SyncDataRules.Declaration declared = SyncDataRules.declared(name);
        if (declared != null && declared.kind != given.kind) {
            ContentLog.LOGGER.error("{} sets the synced value {} to a {}, but it is declared {}, so nothing is set", asking, name, SyncValue.kindName(given.kind), SyncValue.kindName(declared.kind));
            return false;
        }
        int max = SyncDataRules.max(name);
        if (given.kind == SyncValue.TEXT && given.text.length() > max) {
            ContentLog.LOGGER.error("{} sets the synced value {} to text longer than {} characters, so nothing is set", asking, name, max);
            return false;
        }
        SyncData data = get(world);
        return data != null && data.store(world.getServer(), player, name, given, asking);
    }

    public static boolean remove(Level world, @Nullable Player player, String name) {
        SyncData data = peek(world);
        Map<String, SyncValue> scope = data == null ? null : player == null ? data.world : data.players.get(player.getUUID());
        if (scope == null || scope.remove(name) == null) { return false; }
        data.setDirty();
        queue(world.getServer(), player == null ? null : player.getUUID(), name);
        return true;
    }

    @Nullable public static SyncValue read(Player player, String name) {
        SyncData data = peek(player.level());
        if (data != null) {
            SyncValue own = data.playerValue(player.getUUID(), name);
            if (own != null) { return own; }
            SyncValue shared = data.world.get(name);
            if (shared != null) { return shared; }
        }
        return SyncDataRules.defaultOf(name);
    }

    public static void revalidate(Level world) {
        SyncData data = peek(world);
        if (data == null) { return; }
        data.trim(data.world, THE_WORLD);
        for (Map.Entry<UUID, Map<String, SyncValue>> held : data.players.entrySet()) { data.trim(held.getValue(), held.getKey().toString()); }
    }

    public static void logout(UUID id) {
        QUEUE.remove(id);
        SyncData data = loaded;
        Map<String, SyncValue> own = data == null ? null : data.players.get(id);
        if (own == null) { return; }
        own.keySet().removeIf(name -> !SyncDataRules.persists(name));
        if (own.isEmpty()) { data.players.remove(id); }
    }

    public static void stopped() {
        QUEUE.clear();
        loaded = null;
    }

    public Map<String, SyncValue> worldValues() { return Collections.unmodifiableMap(world); }

    public Map<String, SyncValue> playerValues(UUID id) { return players.containsKey(id) ? Collections.unmodifiableMap(players.get(id)) : Collections.emptyMap(); }

    @Nullable SyncValue worldValue(String name) { return world.get(name); }

    @Nullable SyncValue playerValue(UUID id, String name) {
        Map<String, SyncValue> own = players.get(id);
        return own == null ? null : own.get(name);
    }

    private boolean store(@Nullable MinecraftServer server, @Nullable Player player, String name, SyncValue value, String asking) {
        Map<String, SyncValue> scope = player == null ? world : players.computeIfAbsent(player.getUUID(), _ -> new LinkedHashMap<>());
        SyncValue old = scope.get(name);
        if (old != null && old.matches(value.raw())) { return true; }
        if (old == null && scope.size() >= MAX_VALUES) {
            ContentLog.LOGGER.error("{} sets the synced value {} for {}, which already holds {}, so nothing is set", asking, name, player == null ? THE_WORLD : player.getName().getString(), MAX_VALUES);
            return false;
        }
        scope.put(name, value);
        setDirty();
        queue(server, player == null ? null : player.getUUID(), name);
        ContentLog.LOGGER.debug("Synced value {} set to {} for {}", name, value.display, player == null ? THE_WORLD : player.getName().getString());
        return true;
    }

    private static void queue(@Nullable MinecraftServer server, @Nullable UUID id, String name) {
        if (id != null) {
            QUEUE.computeIfAbsent(id, _ -> new HashSet<>()).add(PLAYER_KEY + name);
            return;
        }
        if (server == null) { return; }
        for (ServerPlayer online : server.getPlayerList().getPlayers()) { QUEUE.computeIfAbsent(online.getUUID(), _ -> new HashSet<>()).add(WORLD_KEY + name); }
    }

    private void trim(Map<String, SyncValue> scope, String label) {
        for (Map.Entry<String, SyncValue> held : new LinkedHashMap<>(scope).entrySet()) {
            SyncDataRules.Declaration declared = SyncDataRules.declared(held.getKey());
            SyncValue value = held.getValue();
            if (declared == null || declared.kind == value.kind && (value.kind != SyncValue.TEXT || value.text.length() <= declared.max)) { continue; }
            ContentLog.LOGGER.error("The synced value {} for {} no longer matches its declaration in {} after a reload, removing it", held.getKey(), label, declared.file);
            scope.remove(held.getKey());
            setDirty();
        }
    }

    private static SyncData read(CompoundTag tag) {
        SyncData held = new SyncData();
        load(tag.getCompoundOrEmpty(WORLD), held.world);
        CompoundTag own = tag.getCompoundOrEmpty(PLAYERS);
        for (String key : own.keySet()) {
            try { load(own.getCompoundOrEmpty(key), held.players.computeIfAbsent(UUID.fromString(key), _ -> new LinkedHashMap<>())); }
            catch (IllegalArgumentException ex) { ContentLog.LOGGER.error("The synced data store holds the player key {}, which is not an id, skipping it", key); }
        }
        return held;
    }

    private static void load(CompoundTag tag, Map<String, SyncValue> into) {
        for (String key : tag.keySet()) {
            SyncValue value = SyncValue.fromNbt(tag, key);
            if (value != null) { into.put(key, value); }
        }
    }

    private CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        tag.put(WORLD, save(world));
        CompoundTag held = new CompoundTag();
        for (Map.Entry<UUID, Map<String, SyncValue>> own : players.entrySet()) {
            CompoundTag values = save(own.getValue());
            if (!values.isEmpty()) { held.put(own.getKey().toString(), values); }
        }
        tag.put(PLAYERS, held);
        return tag;
    }

    private static CompoundTag save(Map<String, SyncValue> values) {
        CompoundTag tag = new CompoundTag();
        for (Map.Entry<String, SyncValue> entry : values.entrySet()) {
            if (SyncDataRules.persists(entry.getKey())) { entry.getValue().toNbt(tag, entry.getKey()); }
        }
        return tag;
    }
}
