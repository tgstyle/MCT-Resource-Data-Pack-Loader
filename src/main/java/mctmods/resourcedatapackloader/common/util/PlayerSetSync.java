package mctmods.resourcedatapackloader.common.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public final class PlayerSetSync {
    private final Map<UUID, Set<String>> sent = new HashMap<>();
    private final Function<EntityPlayerMP, Set<String>> compute;
    private final BooleanSupplier watching;
    private final BiConsumer<EntityPlayerMP, Set<String>> send;

    public PlayerSetSync(Function<EntityPlayerMP, Set<String>> compute, BooleanSupplier watching, BiConsumer<EntityPlayerMP, Set<String>> send) {
        this.compute = compute;
        this.watching = watching;
        this.send = send;
    }

    public void push(EntityPlayerMP player) {
        if (!watching.getAsBoolean()) { return; }
        Set<String> open = compute.apply(player);
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
