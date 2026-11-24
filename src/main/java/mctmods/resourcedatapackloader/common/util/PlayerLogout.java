package mctmods.resourcedatapackloader.common.util;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@Mod.EventBusSubscriber(modid = ResourceDataPackLoader.MOD_ID) public final class PlayerLogout {
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

    @SubscribeEvent public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID id = event.player.getUniqueID();
        for (Consumer<UUID> forgetter : FORGETTERS) { forgetter.accept(id); }
    }
}
