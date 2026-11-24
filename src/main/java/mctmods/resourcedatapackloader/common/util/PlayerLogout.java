package mctmods.resourcedatapackloader.common.util;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import net.minecraftforge.event.entity.player.PlayerEvent;

public final class PlayerLogout {
    private static final List<Consumer<UUID>> FORGETTERS = new CopyOnWriteArrayList<>();

    private PlayerLogout() {}

    public static <V> Map<UUID, V> track(Map<UUID, V> map) {
        FORGETTERS.add(map::remove);
        return map;
    }

    public static Set<UUID> track(Set<UUID> set) {
        FORGETTERS.add(set::remove);
        return set;
    }

    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID id = event.getEntity().getUUID();
        for (Consumer<UUID> forgetter : FORGETTERS) { forgetter.accept(id); }
    }
}
