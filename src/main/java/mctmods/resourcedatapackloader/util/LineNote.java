package mctmods.resourcedatapackloader.util;

import com.google.gson.JsonElement;
import java.util.function.Consumer;
import javax.annotation.Nullable;

public interface LineNote extends Consumer<String> {
    @Nullable default JsonElement carried(String folder, String id) { return null; }

    default String namespace() { return "minecraft"; }
}
