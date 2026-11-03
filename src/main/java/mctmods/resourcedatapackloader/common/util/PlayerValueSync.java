package mctmods.resourcedatapackloader.common.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public final class PlayerValueSync<T> {
    private final Map<UUID, T> sent = new HashMap<>();
    private final Function<EntityPlayerMP, T> compute;
    private final BooleanSupplier watching;
    private final BiConsumer<EntityPlayerMP, T> send;

    public PlayerValueSync(Function<EntityPlayerMP, T> compute, BooleanSupplier watching, BiConsumer<EntityPlayerMP, T> send) {
        this.compute = compute;
        this.watching = watching;
        this.send = send;
    }

    public void push(EntityPlayerMP player) {
        if (!watching.getAsBoolean()) { return; }
        T open = compute.apply(player);
        UUID id = player.getUniqueID();
        if (open.equals(sent.get(id))) { return; }
        sent.put(id, open);
        send.accept(player, open);
    }

    public void fresh(EntityPlayerMP player) {
        forget(player);
        push(player);
    }

    public void forget(EntityPlayer player) { sent.remove(player.getUniqueID()); }

    public boolean told(EntityPlayerMP player) { return sent.containsKey(player.getUniqueID()); }

    public void pushAll(Collection<EntityPlayerMP> players) {
        for (EntityPlayerMP player : players) { push(player); }
    }
}
