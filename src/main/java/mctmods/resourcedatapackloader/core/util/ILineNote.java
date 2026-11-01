package mctmods.resourcedatapackloader.core.util;

import com.google.gson.JsonElement;
import java.util.function.Consumer;
import javax.annotation.Nullable;

public interface ILineNote extends Consumer<String> {
    @Nullable default JsonElement carried(String folder, String id) { return null; }
}
