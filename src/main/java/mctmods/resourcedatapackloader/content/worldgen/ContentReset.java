package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentScoring;
import mctmods.resourcedatapackloader.content.def.ScoreDef;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Scores;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public final class ContentReset {
    private ContentReset() {}

    public static int run(MinecraftServer server) {
        String said = ContentPregen.says("resetSays", Config.chunks.resetSays());
        ContentPregen.holdEveryone(server, false);
        if (!said.isEmpty()) { ContentPregen.tellBar(server, said); }
        int swept = 0;
        if (ContentControl.flag(ContentControl.CHUNKS, "resetClearsEntities", Config.chunks.resetClearsEntities())) { swept = sweep(server); }
        if (ContentControl.flag(ContentControl.CHUNKS, "resetClearsScores", Config.chunks.resetClearsScores())) { wipe(server); }
        String runs = ContentPregen.says("resetRuns", Config.chunks.resetRuns());
        if (!runs.isEmpty()) { call(server, runs); }
        place(server);
        ContentPregen.releaseEveryone(server, true);
        ContentScoring.starting(server);
        ContentLog.LOGGER.info("The map was reset: {} entity(s) swept", swept);
        return swept;
    }

    public static int sweep(MinecraftServer server) {
        int swept = 0;
        for (ServerLevel level : server.getAllLevels()) {
            List<Entity> doomed = new ArrayList<>();
            for (Entity held : level.getAllEntities()) {
                if (!(held instanceof Player)) { doomed.add(held); }
            }
            for (Entity held : doomed) {
                held.discard();
                swept++;
            }
        }
        return swept;
    }

    private static void wipe(MinecraftServer server) {
        Scoreboard board = Scores.board(server);
        for (ScoreDef def : ContentScoring.all().values()) {
            if (def.carries()) { continue; }
            Objective objective = Scores.objective(board, def.name());
            if (objective != null) { Scores.removeObjective(board, objective); }
        }
        ContentScoring.keep(server.overworld());
    }

    private static void call(MinecraftServer server, String named) {
        ResourceLocation id = ResourceLocation.tryParse(named);
        var held = id == null ? null : server.getFunctions().get(id).orElse(null);
        if (held == null) {
            ContentLog.LOGGER.error("The reset asks to run the function {}, which no pack provides, so nothing is run", named);
            return;
        }
        server.getFunctions().execute(held, server.createCommandSourceStack().withSuppressedOutput());
    }

    private static void place(MinecraftServer server) {
        String asked = ContentPregen.says("resetSendsTo", Config.chunks.resetSendsTo());
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            Landing landing = landingFor(server, asked, player);
            if (landing == null) { continue; }
            player.teleportTo(landing.level(), landing.x() + 0.5D, landing.y(), landing.z() + 0.5D, player.getYRot(), player.getXRot());
        }
    }

    @Nullable private static Landing landingFor(MinecraftServer server, String asked, ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        if (asked.isEmpty() || "spawn".equalsIgnoreCase(asked)) {
            BlockPos spawn = level.getSharedSpawnPos();
            return new Landing(level, spawn.getX(), spawn.getY(), spawn.getZ());
        }
        String where = asked;
        int comma = asked.indexOf(',');
        int colon = comma < 0 ? -1 : asked.lastIndexOf(':', comma);
        if (colon > 0) {
            ResourceLocation id = ResourceLocation.tryParse(asked.substring(0, colon).trim());
            ServerLevel named = id == null ? null : server.getLevel(ResourceKey.create(Registries.DIMENSION, id));
            if (named == null) {
                ContentLog.LOGGER.error("resetSendsTo names the dimension '{}', which is not loaded, so players stay where they are", asked.substring(0, colon));
                return null;
            }
            level = named;
            where = asked.substring(colon + 1);
        }
        String[] parts = where.split(",");
        if (parts.length != 3) {
            ContentLog.LOGGER.error("resetSendsTo is '{}', which is not spawn, x,y,z or dimension:x,y,z, so players stay where they are", asked);
            return null;
        }
        try { return new Landing(level, Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()), Integer.parseInt(parts[2].trim())); }
        catch (NumberFormatException notNumbers) {
            ContentLog.LOGGER.error("resetSendsTo is '{}', whose position is not three whole numbers, so players stay where they are", asked);
            return null;
        }
    }

    private record Landing(ServerLevel level, int x, int y, int z) {}
}
