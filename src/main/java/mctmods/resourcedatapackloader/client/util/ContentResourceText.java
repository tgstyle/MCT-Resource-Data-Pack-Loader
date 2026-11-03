package mctmods.resourcedatapackloader.client.util;

import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;

public final class ContentResourceText {
    private ContentResourceText() {}

    @Nullable public static JsonObject json(String namespace, String path) {
        Identifier location = Identifier.tryBuild(namespace, path);
        if (location == null) { return null; }
        Optional<Resource> held = Minecraft.getInstance().getResourceManager().getResource(location);
        if (held.isEmpty()) { return null; }
        try (InputStreamReader reader = new InputStreamReader(held.get().open(), StandardCharsets.UTF_8)) {
            return Json.GSON.fromJson(reader, JsonObject.class);
        }
        catch (IOException | JsonParseException ex) { return null; }
    }
}
