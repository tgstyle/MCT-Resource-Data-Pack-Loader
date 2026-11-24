package mctmods.resourcedatapackloader.common.syncdata;

import mctmods.resourcedatapackloader.common.util.Nbts;
import mctmods.resourcedatapackloader.common.util.world.SavedData;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.common.FMLCommonHandler;

public final class SyncData extends WorldSavedData {
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
    @Nullable private static SyncData loaded;
    private final Map<String, SyncValue> world = new LinkedHashMap<>();
    private final Map<UUID, Map<String, SyncValue>> players = new HashMap<>();

    public SyncData(String name) { super(name); }

    public static boolean valid(String name) { return VALID.matcher(name).matches(); }

    @Nullable public static SyncData get(World world) {
        SyncData data = world.isRemote ? null : SavedData.overworld(world, SyncData.class, NAME, SyncData::new);
        if (data != null) { loaded = data; }
        return data;
    }

    @Nullable public static SyncData peek(World world) {
        if (loaded != null) { return loaded; }
        MapStorage storage = storage(world);
        loaded = storage == null ? null : (SyncData) storage.getOrLoadData(SyncData.class, NAME);
        return loaded;
    }

    @Nullable private static MapStorage storage(World world) {
        if (world.isRemote) { return null; }
        MinecraftServer server = world.getMinecraftServer();
        return server == null ? world.getMapStorage() : server.getWorld(0).getMapStorage();
    }

    public static boolean set(World world, @Nullable EntityPlayer player, String name, Object value, String asking) {
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
        return data != null && data.store(player, name, given, asking);
    }

    public static boolean remove(World world, @Nullable EntityPlayer player, String name) {
        SyncData data = peek(world);
        Map<String, SyncValue> scope = data == null ? null : player == null ? data.world : data.players.get(player.getUniqueID());
        if (scope == null || scope.remove(name) == null) { return false; }
        data.markDirty();
        queue(player == null ? null : player.getUniqueID(), name);
        return true;
    }

    @Nullable public static SyncValue read(EntityPlayer player, String name) {
        SyncData data = peek(player.world);
        if (data != null) {
            SyncValue own = data.playerValue(player.getUniqueID(), name);
            if (own != null) { return own; }
            SyncValue shared = data.world.get(name);
            if (shared != null) { return shared; }
        }
        return SyncDataRules.defaultOf(name);
    }

    public static void revalidate(World world) {
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

    private boolean store(@Nullable EntityPlayer player, String name, SyncValue value, String asking) {
        Map<String, SyncValue> scope = player == null ? world : players.computeIfAbsent(player.getUniqueID(), id -> new LinkedHashMap<>());
        SyncValue old = scope.get(name);
        if (old != null && old.matches(value.raw())) { return true; }
        if (old == null && scope.size() >= MAX_VALUES) {
            ContentLog.LOGGER.error("{} sets the synced value {} for {}, which already holds {}, so nothing is set", asking, name, player == null ? THE_WORLD : player.getName(), MAX_VALUES);
            return false;
        }
        scope.put(name, value);
        markDirty();
        queue(player == null ? null : player.getUniqueID(), name);
        ContentLog.LOGGER.debug("Synced value {} set to {} for {}", name, value.display, player == null ? THE_WORLD : player.getName());
        return true;
    }

    private static void queue(@Nullable UUID id, String name) {
        if (id != null) {
            QUEUE.computeIfAbsent(id, key -> new HashSet<>()).add(PLAYER_KEY + name);
            return;
        }
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) { return; }
        for (EntityPlayerMP online : server.getPlayerList().getPlayers()) { QUEUE.computeIfAbsent(online.getUniqueID(), key -> new HashSet<>()).add(WORLD_KEY + name); }
    }

    private void trim(Map<String, SyncValue> scope, String label) {
        for (Map.Entry<String, SyncValue> held : new LinkedHashMap<>(scope).entrySet()) {
            SyncDataRules.Declaration declared = SyncDataRules.declared(held.getKey());
            SyncValue value = held.getValue();
            if (declared == null || declared.kind == value.kind && (value.kind != SyncValue.TEXT || value.text.length() <= declared.max)) { continue; }
            ContentLog.LOGGER.error("The synced value {} for {} no longer matches its declaration in {} after a reload, removing it", held.getKey(), label, declared.file);
            scope.remove(held.getKey());
            markDirty();
        }
    }

    @Override public void readFromNBT(@Nonnull NBTTagCompound tag) {
        world.clear();
        players.clear();
        load(tag.getCompoundTag(WORLD), world);
        NBTTagCompound held = tag.getCompoundTag(PLAYERS);
        for (String key : held.getKeySet()) {
            UUID id = Nbts.uuid(key);
            if (id == null) {
                ContentLog.LOGGER.error("The synced data store holds the player key {}, which is not an id, skipping it", key);
                continue;
            }
            load(held.getCompoundTag(key), players.computeIfAbsent(id, made -> new LinkedHashMap<>()));
        }
    }

    private static void load(NBTTagCompound tag, Map<String, SyncValue> into) {
        for (String key : tag.getKeySet()) {
            SyncValue value = SyncValue.fromNbt(tag, key);
            if (value != null) { into.put(key, value); }
        }
    }

    @Override @Nonnull public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound tag) {
        tag.setTag(WORLD, save(world));
        NBTTagCompound held = new NBTTagCompound();
        for (Map.Entry<UUID, Map<String, SyncValue>> own : players.entrySet()) {
            NBTTagCompound values = save(own.getValue());
            if (!values.isEmpty()) { held.setTag(own.getKey().toString(), values); }
        }
        tag.setTag(PLAYERS, held);
        return tag;
    }

    private static NBTTagCompound save(Map<String, SyncValue> values) {
        NBTTagCompound tag = new NBTTagCompound();
        for (Map.Entry<String, SyncValue> entry : values.entrySet()) {
            if (SyncDataRules.persists(entry.getKey())) { entry.getValue().toNbt(tag, entry.getKey()); }
        }
        return tag;
    }
}
