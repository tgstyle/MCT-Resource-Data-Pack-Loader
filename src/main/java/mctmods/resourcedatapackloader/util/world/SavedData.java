package mctmods.resourcedatapackloader.util.world;

import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import java.util.function.Function;

public final class SavedData {
    private SavedData() {}

    public static <T extends WorldSavedData> T get(MapStorage storage, Class<T> type, String name, Function<String, T> make) {
        T data = type.cast(storage.getOrLoadData(type, name));
        if (data == null) {
            data = make.apply(name);
            storage.setData(name, data);
        }
        return data;
    }
}
