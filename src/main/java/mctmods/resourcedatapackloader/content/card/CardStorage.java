package mctmods.resourcedatapackloader.content.card;

import mctmods.resourcedatapackloader.util.PlayerPersisted;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import javax.annotation.Nonnull;

public final class CardStorage extends SavedData {
    private static final String NAME = "rdpl_cards";
    private static final String OLD_FIRED = "Fired";
    private static final String PERSISTED = "rdplCards";
    private static final String PERSISTED_KILLS = "rdplCardKills";
    private CompoundTag fired = new CompoundTag();

    private static CardStorage get(MinecraftServer server) { return server.overworld().getDataStorage().computeIfAbsent(CardStorage::read, CardStorage::new, NAME); }

    private static CardStorage read(CompoundTag tag) {
        CardStorage held = new CardStorage();
        held.fired = tag.getCompound(tag.contains(PERSISTED) ? PERSISTED : OLD_FIRED).copy();
        return held;
    }

    @Override @Nonnull public CompoundTag save(@Nonnull CompoundTag tag) {
        tag.put(PERSISTED, fired.copy());
        return tag;
    }

    static boolean firedFor(Player player, String key) { return PlayerPersisted.read(player, PERSISTED).contains(key); }

    static long lastFor(Player player, String key) { return PlayerPersisted.read(player, PERSISTED).getLong(key); }

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
