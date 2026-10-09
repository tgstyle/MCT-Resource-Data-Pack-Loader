package mctmods.resourcedatapackloader.content.card;

import mctmods.resourcedatapackloader.util.SavedTypes;
import mctmods.resourcedatapackloader.util.PlayerPersisted;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public final class CardStorage extends SavedData {
    public static final String NAME = "rdpl_cards";
    private static final String OLD_FIRED = "Fired";
    private static final String PERSISTED = "rdplCards";
    private static final String PERSISTED_KILLS = "rdplCardKills";
    private CompoundTag fired = new CompoundTag();
    private static final SavedDataType<CardStorage> TYPE = SavedTypes.of(NAME, CardStorage::new, CardStorage::read, CardStorage::write);

    private static CardStorage get(MinecraftServer server) { return server.overworld().getDataStorage().computeIfAbsent(TYPE); }

    private static CardStorage read(CompoundTag tag) {
        CardStorage held = new CardStorage();
        held.fired = tag.getCompoundOrEmpty(tag.contains(PERSISTED) ? PERSISTED : OLD_FIRED).copy();
        return held;
    }

    private CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        tag.put(PERSISTED, fired.copy());
        return tag;
    }

    static boolean firedFor(Player player, String key) { return PlayerPersisted.read(player, PERSISTED).contains(key); }

    static long lastFor(Player player, String key) { return PlayerPersisted.read(player, PERSISTED).getLongOr(key, 0L); }

    static void stampFor(Player player, String key, long now) { PlayerPersisted.section(player, PERSISTED).putLong(key, now); }

    static int tallyFor(Player player, String key) { return PlayerPersisted.tally(player, PERSISTED_KILLS, key); }

    static void clearTallyFor(Player player, String key) { PlayerPersisted.clearTally(player, PERSISTED_KILLS, key); }

    static boolean firedInWorld(MinecraftServer server, String key) { return get(server).fired.contains(key); }

    static void stampWorld(MinecraftServer server, String key, long now) {
        CardStorage data = get(server);
        data.fired.putLong(key, now);
        data.setDirty();
    }
}
