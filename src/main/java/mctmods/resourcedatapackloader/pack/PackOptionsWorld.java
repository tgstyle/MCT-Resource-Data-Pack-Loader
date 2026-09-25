package mctmods.resourcedatapackloader.pack;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IMinecraftServer;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Lang;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class PackOptionsWorld {
    public static final String FILE = "rdpl-pack-options.json";
    private static final String CONTENT = "content";
    private static final int NAMED = 10;
    private static boolean backedUp;

    private PackOptionsWorld() {}

    public static void beforeWorldsLoad(MinecraftServer server) {
        Map<String, Boolean> now = PackOptions.gatingValues();
        JsonObject was = read(file(server));
        List<String> changed = new ArrayList<>();
        for (Map.Entry<String, Boolean> option : now.entrySet()) {
            JsonElement held = was == null ? null : was.get(option.getKey());
            if (held == null || !held.isJsonPrimitive()) { continue; }
            boolean before = held.getAsBoolean();
            if (before == option.getValue()) { continue; }
            changed.add(option.getKey() + " (" + (before ? "on" : "off") + " to " + (option.getValue() ? "on" : "off") + ")");
        }
        PackOptions.worldChanged(changed);
        backedUp = false;
        if (changed.isEmpty()) { return; }
        ContentLog.LOGGER.info("Pack options have changed since this world was last played: {}", changed);
        List<String> missing = missing(server, was);
        if (missing.isEmpty()) {
            ContentLog.LOGGER.info("Nothing the world was last saved with has gone unregistered, so no backup is made");
            return;
        }
        ContentLog.LOGGER.info("The changed pack options leave {} id(s) the world was last saved with unregistered, among them {}", missing.size(), missing.subList(0, Math.min(NAMED, missing.size())));
        backup(server);
    }

    @SuppressWarnings("resource") private static void backup(MinecraftServer server) {
        try {
            long size = ((IMinecraftServer) server).rdpl$storageSource().makeWorldBackup();
            backedUp = true;
            ContentLog.LOGGER.info("The world was backed up to the game's backups folder before the changed pack options touch it, {} KB", size / 1024);
        }
        catch (IOException | RuntimeException failed) {
            ContentLog.LOGGER.error("The world could not be backed up before the changed pack options touch it, so it is not opened: {}", failed.toString());
            throw new IllegalStateException("The world was not opened: its pack options changed and left content it holds unregistered, and the backup made before loading it failed", failed);
        }
    }

    private static List<String> missing(MinecraftServer server, @Nullable JsonObject was) {
        List<String> missing = new ArrayList<>();
        JsonElement held = was == null ? null : was.get(CONTENT);
        if (held == null || !held.isJsonObject()) { return missing; }
        Map<ResourceLocation, Set<ResourceLocation>> now = registered(server);
        for (Map.Entry<String, JsonElement> registry : held.getAsJsonObject().entrySet()) {
            if (!registry.getValue().isJsonArray()) { continue; }
            ResourceLocation key = ResourceLocation.tryParse(registry.getKey());
            Set<ResourceLocation> present = key == null ? Set.of() : now.getOrDefault(key, Set.of());
            for (JsonElement id : registry.getValue().getAsJsonArray()) {
                ResourceLocation named = id.isJsonPrimitive() ? ResourceLocation.tryParse(id.getAsString()) : null;
                if (named != null && !present.contains(named)) { missing.add(registry.getKey() + " " + named); }
            }
        }
        return missing;
    }

    private static Map<ResourceLocation, Set<ResourceLocation>> registered(MinecraftServer server) {
        Map<ResourceLocation, Set<ResourceLocation>> out = new LinkedHashMap<>();
        server.registryAccess().registries().forEach(entry -> out.put(entry.key().location(), entry.value().keySet()));
        return out;
    }

    private static Set<String> packNamespaces() {
        Set<String> out = new HashSet<>();
        for (RDPLPack pack : PackManager.get().getPacks()) {
            for (PackType type : PackType.values()) { out.addAll(pack.getNamespaces(type)); }
        }
        out.add(ResourceDataPackLoader.MOD_ID);
        out.remove(ResourceLocation.DEFAULT_NAMESPACE);
        return out;
    }

    private static Path file(MinecraftServer server) { return server.getWorldPath(LevelResource.ROOT).resolve(FILE); }

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

    public static void onLevelSave(LevelEvent.Save event) {
        if (!(event.getLevel() instanceof ServerLevel level) || level.dimension() != Level.OVERWORLD) { return; }
        Path file = file(level.getServer());
        Map<String, Boolean> now = PackOptions.gatingValues();
        try {
            if (now.isEmpty()) {
                Files.deleteIfExists(file);
                return;
            }
            JsonObject out = new JsonObject();
            for (Map.Entry<String, Boolean> option : now.entrySet()) { out.addProperty(option.getKey(), option.getValue()); }
            Set<String> namespaces = packNamespaces();
            JsonObject content = new JsonObject();
            for (Map.Entry<ResourceLocation, Set<ResourceLocation>> registry : registered(level.getServer()).entrySet()) {
                JsonArray ids = new JsonArray();
                for (ResourceLocation id : registry.getValue()) {
                    if (namespaces.contains(id.getNamespace())) { ids.add(id.toString()); }
                }
                if (!ids.isEmpty()) { content.add(registry.getKey().toString(), ids); }
            }
            out.add(CONTENT, content);
            Files.writeString(file, ModPacks.GSON.toJson(out), StandardCharsets.UTF_8);
        }
        catch (IOException failed) { ContentLog.LOGGER.error("The world's record of its pack options at {} could not be written: {}", file, failed.toString()); }
    }

    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (PackOptions.worldChanged().isEmpty() || !(event.getEntity() instanceof ServerPlayer player) || player.serverLevel().dimension() != Level.OVERWORLD) { return; }
        player.sendSystemMessage(Component.literal(Lang.tr(player, backedUp ? "rdpl.world.packOptions" : "rdpl.world.packOptionsChanged", String.join(", ", PackOptions.worldChanged()))));
        PackOptions.worldTold();
    }
}
