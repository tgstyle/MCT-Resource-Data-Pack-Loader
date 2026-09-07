package mctmods.resourcedatapackloader.content.gate;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import javax.annotation.Nonnull;

public final class GateStorage extends SavedData {
    private static final String NAME = "rdpl_gates";
    private static final String OPEN = "Open";
    private static final String KILLS = "Kills";
    private static final String PERSISTED = "rdplGates";
    private static final String PERSISTED_KILLS = "rdplGateKills";
    private CompoundTag open = new CompoundTag();
    private CompoundTag kills = new CompoundTag();

    private static GateStorage of(MinecraftServer server) { return server.overworld().getDataStorage().computeIfAbsent(GateStorage::read, GateStorage::new, NAME); }

    private static GateStorage read(CompoundTag tag) {
        GateStorage held = new GateStorage();
        held.open = tag.getCompound(OPEN).copy();
        held.kills = tag.getCompound(KILLS).copy();
        return held;
    }

    @Override @Nonnull public CompoundTag save(@Nonnull CompoundTag tag) {
        tag.put(OPEN, open.copy());
        tag.put(KILLS, kills.copy());
        return tag;
    }

    public static boolean unlockedFor(Player player, String key) { return player.getPersistentData().getCompound(PERSISTED).getBoolean(key); }

    public static void unlockFor(Player player, String key) {
        CompoundTag gates = player.getPersistentData().getCompound(PERSISTED);
        gates.putBoolean(key, true);
        player.getPersistentData().put(PERSISTED, gates);
    }

    public static void lockFor(Player player, String key) {
        CompoundTag gates = player.getPersistentData().getCompound(PERSISTED);
        gates.remove(key);
        player.getPersistentData().put(PERSISTED, gates);
    }

    public static int tallyFor(Player player, String key) {
        CompoundTag tally = player.getPersistentData().getCompound(PERSISTED_KILLS);
        int now = tally.getInt(key) + 1;
        tally.putInt(key, now);
        player.getPersistentData().put(PERSISTED_KILLS, tally);
        return now;
    }

    public static void clearTallyFor(Player player, String key) {
        CompoundTag tally = player.getPersistentData().getCompound(PERSISTED_KILLS);
        tally.remove(key);
        player.getPersistentData().put(PERSISTED_KILLS, tally);
    }

    public static int tallyGlobally(MinecraftServer server, String key) {
        GateStorage data = of(server);
        int now = data.kills.getInt(key) + 1;
        data.kills.putInt(key, now);
        data.setDirty();
        return now;
    }

    public static void clearTallyGlobally(MinecraftServer server, String key) {
        GateStorage data = of(server);
        data.kills.remove(key);
        data.setDirty();
    }

    public static boolean unlockedGlobally(MinecraftServer server, String key) { return of(server).open.getBoolean(key); }

    public static void unlockGlobally(MinecraftServer server, String key) {
        GateStorage data = of(server);
        data.open.putBoolean(key, true);
        data.setDirty();
    }

    public static void lockGlobally(MinecraftServer server, String key) {
        GateStorage data = of(server);
        data.open.remove(key);
        data.setDirty();
    }
}
