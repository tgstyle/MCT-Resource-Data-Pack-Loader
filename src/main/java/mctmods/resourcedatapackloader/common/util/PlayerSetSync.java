package mctmods.resourcedatapackloader.common.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class PlayerSetSync {
    private final Map<UUID, Set<String>> sent = new HashMap<>();
    private final Function<ServerPlayer, Set<String>> compute;
    private final BooleanSupplier watching;
    private final BiConsumer<ServerPlayer, Set<String>> send;

    public PlayerSetSync(Function<ServerPlayer, Set<String>> compute, BooleanSupplier watching, BiConsumer<ServerPlayer, Set<String>> send) {
        this.compute = compute;
        this.watching = watching;
        this.send = send;
    }

    public void push(ServerPlayer player) {
        if (!watching.getAsBoolean()) { return; }
        Set<String> open = compute.apply(player);
        UUID id = player.getUUID();
        if (open.equals(sent.get(id))) { return; }
        sent.put(id, open);
        send.accept(player, open);
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
