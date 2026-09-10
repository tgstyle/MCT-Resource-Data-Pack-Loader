package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentScoring;
import mctmods.resourcedatapackloader.content.def.ScoreDef;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public final class ContentReset {
    private ContentReset() {}

    public static int run(MinecraftServer server) {
        if (server == null) { return 0; }
        String said = ContentPregen.says("resetSays", Config.chunks.resetSays).trim();
        ContentPregen.holdEveryone(false);
        if (!said.isEmpty()) { ContentPregen.tellBar(server, said); }
        int swept = 0;
        if (ContentControl.flag(ContentControl.CHUNKS, "resetClearsEntities", Config.chunks.resetClearsEntities)) {
            swept = sweep(server);
        }
        if (ContentControl.flag(ContentControl.CHUNKS, "resetClearsScores", Config.chunks.resetClearsScores)) {
            wipe(server);
        }
        String runs = ContentPregen.says("resetRuns", Config.chunks.resetRuns).trim();
        if (!runs.isEmpty()) { call(server, runs); }
        place(server);
        ContentPregen.releaseEveryone(true);
        ContentScoring.starting(server);
        ContentLog.LOGGER.info("The map was reset: {} entity(s) swept", swept);
        return swept;
    }

    public static int sweep(MinecraftServer server) {
        int swept = 0;
        for (WorldServer world : server.worlds) {
            List<Entity> doomed = new ArrayList<>();
            for (Entity held : world.loadedEntityList) {
                if (!(held instanceof EntityPlayer)) { doomed.add(held); }
            }
            for (Entity held : doomed) { held.setDead(); swept++; }
        }
        return swept;
    }

    @SuppressWarnings({"ConstantConditions", "ConstantValue"}) private static void wipe(MinecraftServer server) {
        WorldServer world = server.getWorld(0);
        if (world == null) { return; }
        Scoreboard board = world.getScoreboard();
        for (ScoreDef def : ContentScoring.all().values()) {
            if (def.carries) { continue; }
            ScoreObjective objective = board.getObjective(def.name);
            if (objective != null) { board.removeObjective(objective); }
        }
        ContentScoring.keep(world);
    }

    private static void call(MinecraftServer server, String named) {
        net.minecraft.advancements.FunctionManager manager = server.getFunctionManager();
        net.minecraft.command.FunctionObject held = manager.getFunction(new ResourceLocation(named));
        if (held == null) {
            ContentLog.LOGGER.error("The reset asks to run the function {}, which no pack provides, so nothing is run", named);
            return;
        }
        manager.execute(held, server);
    }

    private static void place(MinecraftServer server) {
        String asked = ContentPregen.says("resetSendsTo", Config.chunks.resetSendsTo).trim();
        for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
            Landing landing = landingFor(server, asked, player);
            if (landing == null) { continue; }
            if (player.dimension != landing.dimension) { player.changeDimension(landing.dimension); }
            player.connection.setPlayerLocation(landing.x + 0.5D, landing.y, landing.z + 0.5D, player.rotationYaw, player.rotationPitch);
        }
    }

    @SuppressWarnings({"ConstantConditions", "ConstantValue"}) @Nullable private static Landing landingFor(MinecraftServer server, String asked, EntityPlayerMP player) {
        if (asked.isEmpty() || "spawn".equalsIgnoreCase(asked)) {
            WorldServer world = server.getWorld(player.dimension);
            if (world == null) { return null; }
            BlockPos spawn = world.getSpawnPoint();
            return new Landing(player.dimension, spawn.getX(), spawn.getY(), spawn.getZ());
        }
        int dimension = player.dimension;
        String where = asked;
        int colon = asked.indexOf(':');
        if (colon > 0) {
            try { dimension = Integer.parseInt(asked.substring(0, colon).trim()); }
            catch (NumberFormatException notADimension) { return null; }
            where = asked.substring(colon + 1);
        }
        WorldServer world = server.getWorld(dimension);
        BlockPos at = ContentTerrain.spawnFrom(where, world == null ? 64 : world.getSeaLevel() + 1);
        return at == null ? null : new Landing(dimension, at.getX(), at.getY(), at.getZ());
    }

    private static final class Landing {
        private final int dimension;
        private final int x;
        private final int y;
        private final int z;

        private Landing(int dimension, int x, int y, int z) {
            this.dimension = dimension;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
