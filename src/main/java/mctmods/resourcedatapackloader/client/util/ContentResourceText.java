package mctmods.resourcedatapackloader.client.util;

import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT) public final class ContentResourceText {
    private ContentResourceText() {}

    @Nullable public static JsonObject json(String namespace, String path) {
        try (IResource held = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(namespace, path)); InputStreamReader reader = new InputStreamReader(held.getInputStream(), StandardCharsets.UTF_8)) {
            return Json.GSON.fromJson(reader, JsonObject.class);
        }
        catch (IOException | JsonParseException ex) { return null; }
    }
}
