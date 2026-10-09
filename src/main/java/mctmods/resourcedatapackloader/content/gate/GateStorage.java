package mctmods.resourcedatapackloader.content.gate;

import mctmods.resourcedatapackloader.content.def.GateDef;
import mctmods.resourcedatapackloader.util.PlayerPersisted;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import javax.annotation.Nonnull;

public final class GateStorage extends SavedData {
    private static final String NAME = "rdpl_gates";
    private static final String OLD_OPEN = "Open";
    private static final String OLD_KILLS = "Kills";
    private static final String PERSISTED = "rdplGates";
    private static final String PERSISTED_KILLS = "rdplGateKills";
    private CompoundTag open = new CompoundTag();
    private CompoundTag kills = new CompoundTag();
    private static final Factory<GateStorage> FACTORY = new Factory<>(GateStorage::new, (tag, lookup) -> read(tag));

    private static GateStorage get(MinecraftServer server) { return server.overworld().getDataStorage().computeIfAbsent(FACTORY, NAME); }

    private static GateStorage read(CompoundTag tag) {
        GateStorage held = new GateStorage();
        held.open = tag.getCompound(tag.contains(PERSISTED) ? PERSISTED : OLD_OPEN).copy();
        held.kills = tag.getCompound(tag.contains(PERSISTED_KILLS) ? PERSISTED_KILLS : OLD_KILLS).copy();
        return held;
    }

    @Override @Nonnull public CompoundTag save(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider lookup) {
        tag.put(PERSISTED, open.copy());
        tag.put(PERSISTED_KILLS, kills.copy());
        return tag;
    }

    public static int tally(ServerPlayer player, GateDef def) { return def.global() ? tallyGlobally(player.server, def.id()) : tallyFor(player, def.id()); }

    public static void clearTally(ServerPlayer player, GateDef def) {
        if (def.global()) { clearTallyGlobally(player.server, def.id()); }
        else { clearTallyFor(player, def.id()); }
    }

    public static boolean unlocked(ServerPlayer player, GateDef def) { return def.global() ? unlockedGlobally(player.server, def.id()) : unlockedFor(player, def.id()); }

    public static void unlock(ServerPlayer player, GateDef def) {
        if (def.global()) { unlockGlobally(player.server, def.id()); }
        else { unlockFor(player, def.id()); }
    }

    public static void lock(ServerPlayer player, GateDef def) {
        if (def.global()) { lockGlobally(player.server, def.id()); }
        else { lockFor(player, def.id()); }
    }

    public static boolean unlockedFor(Player player, String key) { return PlayerPersisted.read(player, PERSISTED).getBoolean(key); }

    public static void unlockFor(Player player, String key) { PlayerPersisted.section(player, PERSISTED).putBoolean(key, true); }

    public static void lockFor(Player player, String key) { PlayerPersisted.section(player, PERSISTED).remove(key); }

    public static int tallyFor(Player player, String key) { return PlayerPersisted.tally(player, PERSISTED_KILLS, key); }

    public static void clearTallyFor(Player player, String key) { PlayerPersisted.clearTally(player, PERSISTED_KILLS, key); }

    public static int tallyGlobally(MinecraftServer server, String key) {
        GateStorage data = get(server);
        int now = data.kills.getInt(key) + 1;
        data.kills.putInt(key, now);
        data.setDirty();
        return now;
    }

    public static int countGlobally(MinecraftServer server, String key) { return get(server).kills.getInt(key); }

    public static void noteFor(Player player, String key, int value) { PlayerPersisted.section(player, PERSISTED_KILLS).putInt(key, value); }

    public static int notedFor(Player player, String key) { return PlayerPersisted.read(player, PERSISTED_KILLS).getInt(key); }

    public static void clearTallyGlobally(MinecraftServer server, String key) {
        GateStorage data = get(server);
        data.kills.remove(key);
        data.setDirty();
    }

    public static boolean unlockedGlobally(MinecraftServer server, String key) { return get(server).open.getBoolean(key); }

    public static void unlockGlobally(MinecraftServer server, String key) {
        GateStorage data = get(server);
        data.open.putBoolean(key, true);
        data.setDirty();
    }

    public static void lockGlobally(MinecraftServer server, String key) {
        GateStorage data = get(server);
        data.open.remove(key);
        data.setDirty();
    }
}
