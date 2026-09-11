package mctmods.resourcedatapackloader.pack;

import mctmods.resourcedatapackloader.mixin.rdpl.common.IMinecraftServer;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Lang;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public final class PackOptionsWorld {
    public static final String FILE = "rdpl-pack-options.json";

    private PackOptionsWorld() {}

    public static void beforeWorldsLoad(MinecraftServer server) {
        Path file = server.getWorldPath(LevelResource.ROOT).resolve(FILE);
        Map<String, Boolean> now = PackOptions.gatingValues();
        JsonObject was = read(file);
        List<String> changed = new ArrayList<>();
        for (Map.Entry<String, Boolean> option : now.entrySet()) {
            JsonElement held = was == null ? null : was.get(option.getKey());
            if (held == null || !held.isJsonPrimitive()) { continue; }
            boolean before = held.getAsBoolean();
            if (before == option.getValue()) { continue; }
            changed.add(option.getKey() + " (" + (before ? "on" : "off") + " to " + (option.getValue() ? "on" : "off") + ")");
        }
        PackOptions.worldChanged(changed);
        if (!changed.isEmpty()) {
            ContentLog.LOGGER.info("Pack options have changed since this world was last played: {}", changed);
            backup(server);
        }
        write(file, now);
    }

    @SuppressWarnings("resource") private static void backup(MinecraftServer server) {
        try {
            long size = ((IMinecraftServer) server).rdpl$storageSource().makeWorldBackup();
            ContentLog.LOGGER.info("The world was backed up to the game's backups folder before the changed pack options touch it, {} KB", size / 1024);
        }
        catch (IOException | RuntimeException failed) { ContentLog.LOGGER.error("The world could not be backed up before the changed pack options touch it: {}", failed.toString()); }
    }

    @Nullable private static JsonObject read(Path file) {
        if (!Files.isRegularFile(file)) { return null; }
        try {
            JsonElement held = JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8));
            return held.isJsonObject() ? held.getAsJsonObject() : null;
        }
        catch (IOException | RuntimeException failed) {
            ContentLog.LOGGER.error("The world's record of its pack options at {} could not be read, so the options are taken as unchanged: {}", file, failed.toString());
            return null;
        }
    }

    private static void write(Path file, Map<String, Boolean> now) {
        try {
            if (now.isEmpty()) {
                Files.deleteIfExists(file);
                return;
            }
            JsonObject out = new JsonObject();
            for (Map.Entry<String, Boolean> option : now.entrySet()) { out.addProperty(option.getKey(), option.getValue()); }
            Files.writeString(file, new GsonBuilder().setPrettyPrinting().create().toJson(out), StandardCharsets.UTF_8);
        }
        catch (IOException failed) { ContentLog.LOGGER.error("The world's record of its pack options at {} could not be written: {}", file, failed.toString()); }
    }

    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (PackOptions.worldChanged().isEmpty() || !(event.getEntity() instanceof ServerPlayer player) || player.serverLevel().dimension() != Level.OVERWORLD) { return; }
        player.sendSystemMessage(Component.literal(Lang.tr(player, "rdpl.world.packOptions", String.join(", ", PackOptions.worldChanged()))));
        PackOptions.worldTold();
    }
}
