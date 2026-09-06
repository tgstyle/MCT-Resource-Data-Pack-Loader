package mctmods.resourcedatapackloader.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModFileInfo;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.Nullable;

public final class GameData {
    private static final String MINECRAFT = "minecraft";
    private static final Gson GSON = new Gson();
    @Nullable private static VanillaPackResources vanilla;

    private GameData() {}

    @Nullable public static JsonObject json(ResourceLocation at) {
        JsonObject found = MINECRAFT.equals(at.getNamespace()) ? fromVanilla(at) : fromMod(at);
        if (found == null) { ContentLog.LOGGER.error("The game's or a mod's data file {} could not be read, so what a pack builds on it is left out", at); }
        return found;
    }

    public static void release() { vanilla = null; }

    @Nullable private static JsonObject fromVanilla(ResourceLocation at) {
        if (vanilla == null) { vanilla = ServerPacksSource.createVanillaPackSource(); }
        IoSupplier<InputStream> supplier = vanilla.getResource(PackType.SERVER_DATA, at);
        if (supplier == null) { return null; }
        try (InputStream in = supplier.get(); Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) { return GSON.fromJson(reader, JsonObject.class); }
        catch (IOException | JsonParseException failed) { return null; }
    }

    @Nullable private static JsonObject fromMod(ResourceLocation at) {
        IModFileInfo info = ModList.get().getModFileById(at.getNamespace());
        if (info == null) { return null; }
        Path path = info.getFile().findResource("data", at.getNamespace(), at.getPath());
        if (!Files.isRegularFile(path)) { return null; }
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) { return GSON.fromJson(reader, JsonObject.class); }
        catch (IOException | JsonParseException failed) { return null; }
    }
}
