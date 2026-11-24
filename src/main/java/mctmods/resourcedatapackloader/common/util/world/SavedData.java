package mctmods.resourcedatapackloader.common.util.world;

import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import java.util.function.Function;

public final class SavedData {
    private SavedData() {}

    @Nullable public static <T extends WorldSavedData> T of(@Nullable MapStorage storage, Class<T> type, String name, Function<String, T> make) {
        return storage == null ? null : get(storage, type, name, make);
    }

    @Nullable public static <T extends WorldSavedData> T overworld(@Nullable MinecraftServer server, Class<T> type, String name, Function<String, T> make) {
        return server == null ? null : of(server.getWorld(0).getMapStorage(), type, name, make);
    }

    @Nullable public static <T extends WorldSavedData> T overworld(World world, Class<T> type, String name, Function<String, T> make) {
        MinecraftServer server = world.getMinecraftServer();
        return of(server == null ? world.getMapStorage() : server.getWorld(0).getMapStorage(), type, name, make);
    }

    public static <T extends WorldSavedData> T get(MapStorage storage, Class<T> type, String name, Function<String, T> make) {
        T data = type.cast(storage.getOrLoadData(type, name));
        if (data == null) {
            data = make.apply(name);
            storage.setData(name, data);
        }
        return data;
    }
}
