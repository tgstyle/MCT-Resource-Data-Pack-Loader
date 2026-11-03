package mctmods.resourcedatapackloader.common.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class PlayerValueSync<T> {
    private final Map<UUID, T> sent = new HashMap<>();
    private final Function<ServerPlayer, T> compute;
    private final BooleanSupplier watching;
    private final BiConsumer<ServerPlayer, T> send;

    public PlayerValueSync(Function<ServerPlayer, T> compute, BooleanSupplier watching, BiConsumer<ServerPlayer, T> send) {
        this.compute = compute;
        this.watching = watching;
        this.send = send;
    }

    public void push(ServerPlayer player) {
        if (!watching.getAsBoolean()) { return; }
        T value = compute.apply(player);
        UUID id = player.getUUID();
        if (value.equals(sent.get(id))) { return; }
        sent.put(id, value);
        send.accept(player, value);
    }

    public void fresh(ServerPlayer player) {
        forget(player);
        push(player);
    }

    public void forget(Player player) { sent.remove(player.getUUID()); }

    public boolean told(ServerPlayer player) { return sent.containsKey(player.getUUID()); }

    public void pushAll(Collection<ServerPlayer> players) {
        for (ServerPlayer player : players) { push(player); }
    }
}
