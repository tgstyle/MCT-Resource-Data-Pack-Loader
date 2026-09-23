package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentFormats;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IServerLevel;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public final class ContentPregenDimensions {
    public static final int VANILLA_SPAWN_REACH = 12;
    private static final double NO_BORDER = 5.9E7D;
    private static final long REGION_HEADER = 8192L;
    private static final String OVERWORLD = "minecraft:overworld";
    static final Deque<ResourceLocation> PENDING = new ArrayDeque<>();
    static int wantedRadius;
    static boolean chaining;

    private ContentPregenDimensions() {}

    public static boolean reachesTheBorder() { return ContentControl.flag(ContentControl.CHUNKS, "pregenToBorder", Config.chunks.pregenToBorder()); }

    public static boolean picksUpAgain() { return ContentControl.flag(ContentControl.CHUNKS, "pregenResume", Config.chunks.pregenResume()); }

    public static int wantedOnNewWorld() {
        int asked = Math.max(0, ContentControl.number(ContentControl.CHUNKS, "pregenOnNewWorld", Config.chunks.pregenOnNewWorld()));
        return Math.max(asked, VANILLA_SPAWN_REACH);
    }

    private static List<ResourceLocation> ids(String key, List<String> fallback) {
        List<ResourceLocation> out = new ArrayList<>();
        for (String entry : ContentControl.list(ContentControl.CHUNKS, key, fallback)) {
            ResourceLocation id = ResourceLocation.tryParse(ContentFormats.dimensionId(entry));
            if (id == null) { ContentLog.LOGGER.error("{} names '{}', which is not a dimension id, ignoring it", key, entry); }
            else { out.add(id); }
        }
        return out;
    }

    private static List<ResourceLocation> chosenDimensions(MinecraftServer server) {
        if (!ContentControl.flag(ContentControl.CHUNKS, "pregenAllDimensions", Config.chunks.pregenAllDimensions())) { return ids("pregenDimensions", Config.chunks.pregenDimensions()); }
        List<ResourceLocation> picked = new ArrayList<>();
        for (ServerLevel level : server.getAllLevels()) {
            if (madeUpFront(level.dimension().location())) { picked.add(level.dimension().location()); }
        }
        picked.sort(null);
        ResourceLocation overworld = ResourceLocation.parse(OVERWORLD);
        if (picked.remove(overworld)) { picked.addFirst(overworld); }
        return picked;
    }

    private static boolean madeUpFront(ResourceLocation dimension) { return !ids("pregenDimensionsWhenEntered", Config.chunks.pregenDimensionsWhenEntered()).contains(dimension); }

    public static void onServerStarted(ServerStartedEvent event) {
        MinecraftServer server = event.getServer();
        if (ContentPregen.busy()) { return; }
        int radius = wantedOnNewWorld();
        PregenMemory memory = PregenMemory.of(server);
        CompoundTag run = memory.run();
        if (run != null) {
            ResourceLocation dimension = ResourceLocation.parse(run.getString("dimension"));
            if (!picksUpAgain()) {
                memory.setRun(null);
                memory.setMadeIn(dimension.toString(), null);
                if (run.getInt("reach") > memory.madeTo(dimension.toString())) { memory.setMadeTo(dimension.toString(), run.getInt("reach")); }
                ContentLog.LOGGER.info("Land was still being made in {} when the last session ended, and picking up again is off, so the world stands as far as it was made", dimension);
                return;
            }
            if (PENDING.isEmpty() && (radius > 0 || reachesTheBorder())) {
                for (ResourceLocation held : chosenDimensions(server)) {
                    if (!held.equals(dimension) && (reachesTheBorder() || memory.madeTo(held.toString()) < radius)) { PENDING.addLast(held); }
                }
                wantedRadius = radius;
                chaining = !PENDING.isEmpty();
            }
            ContentLog.LOGGER.info("Land was still being made in {} when the last session ended, so it is picked up again", dimension);
            ContentPregen.later(server, () -> ContentPregen.start(null, server, ResourceKey.create(Registries.DIMENSION, dimension), run.getInt("middleX"), run.getInt("middleZ"), run.getInt("reach")));
            return;
        }
        if ((radius <= 0 && !reachesTheBorder()) || !PENDING.isEmpty()) { return; }
        for (ResourceLocation dimension : chosenDimensions(server)) {
            if (reachesTheBorder() || memory.madeTo(dimension.toString()) < radius) { PENDING.addLast(dimension); }
        }
        if (PENDING.isEmpty()) { return; }
        wantedRadius = radius;
        chaining = true;
        ContentPregen.later(server, () -> nextDimension(server, radius));
    }

    static void startWhenEntered(MinecraftServer server, ResourceKey<Level> entered) {
        int radius = wantedOnNewWorld();
        ResourceLocation id = entered.location();
        if ((radius <= 0 && !reachesTheBorder()) || madeUpFront(id)) { return; }
        if (ContentPregen.running != null && ContentPregen.running.dimension.equals(entered)) { return; }
        if (PENDING.contains(id)) { return; }
        ServerLevel level = server.getLevel(entered);
        if (!reachesTheBorder() && level != null) {
            BlockPos spawn = level.getSharedSpawnPos();
            if (alreadyMade(PregenMemory.of(server), level, radius, spawn.getX() >> 4, spawn.getZ() >> 4)) { return; }
        }
        PENDING.addLast(id);
        wantedRadius = radius;
        chaining = true;
        if (!ContentPregen.busy()) { nextDimension(server, radius); }
    }

    static void nextDimension(MinecraftServer server, int radius) {
        while (!PENDING.isEmpty()) {
            ResourceLocation id = PENDING.removeFirst();
            ServerLevel level = server.getLevel(ResourceKey.create(Registries.DIMENSION, id));
            if (level == null) {
                ContentLog.LOGGER.error("A pack asks for land to be made in {}, which nothing here provides, so it is passed over", id);
                continue;
            }
            int middleX;
            int middleZ;
            int reach;
            if (reachesTheBorder()) {
                WorldBorder border = level.getWorldBorder();
                if (border.getSize() >= NO_BORDER) {
                    ContentLog.LOGGER.error("A pack asks for the land of {} to be made out to its border, but no border has been set there, so there is nothing to reach and none is made", id);
                    continue;
                }
                reach = (int) Math.ceil(border.getSize() / 2.0D / 16.0D);
                int limit = Config.chunks.pregenBorderLimit();
                if (reach > limit) {
                    ContentLog.LOGGER.error("The border of {} stands {} block(s) across, which is {} chunk(s) either way and past the {} allowed by pregenBorderLimit, so no land is made out to it", id, (long) border.getSize(), reach, limit);
                    continue;
                }
                middleX = (int) Math.floor(border.getCenterX()) >> 4;
                middleZ = (int) Math.floor(border.getCenterZ()) >> 4;
            }
            else {
                BlockPos spawn = level.getSharedSpawnPos();
                reach = radius;
                middleX = spawn.getX() >> 4;
                middleZ = spawn.getZ() >> 4;
            }
            if (alreadyMade(PregenMemory.of(server), level, reach, middleX, middleZ)) { continue; }
            long total = ContentPregen.start(null, server, level.dimension(), middleX, middleZ, reach);
            ContentLog.LOGGER.info("Making {} chunk(s) of land in {}, reaching {} chunk(s) either way from {}, {}, before anybody sets foot in it", total, id, reach, middleX, middleZ);
            return;
        }
    }

    private static boolean alreadyMade(PregenMemory memory, ServerLevel level, int reach, int middleX, int middleZ) {
        String dimension = level.dimension().location().toString();
        if (memory.madeTo(dimension) < reach) { return false; }
        File region = DimensionType.getStorageFolder(level.dimension(), level.getServer().getWorldPath(LevelResource.ROOT)).resolve("region").toFile();
        int expected = 0;
        int missing = 0;
        for (int rx = (middleX - reach) >> 5; rx <= (middleX + reach) >> 5; rx++) {
            for (int rz = (middleZ - reach) >> 5; rz <= (middleZ + reach) >> 5; rz++) {
                expected++;
                if (landless(new File(region, "r." + rx + "." + rz + ".mca"))) { missing++; }
            }
        }
        if (missing == 0) { return true; }
        ContentLog.LOGGER.info("{} was made before, but {} of the {} region file(s) its land lives in are missing from the disk, so it is being made again", dimension, missing, expected);
        memory.setMadeTo(dimension, 0);
        memory.setMadeIn(dimension, null);
        if (missing == expected) { freshDragon(level); }
        return false;
    }

    private static boolean landless(File file) { return !file.isFile() || file.length() <= REGION_HEADER; }

    private static void freshDragon(ServerLevel level) {
        if (level.getDragonFight() == null || ContentEndDragon.unwanted(level)) { return; }
        ((IServerLevel) level).rdpl$setDragonFight(new EndDragonFight(level, level.getSeed(), EndDragonFight.Data.DEFAULT));
        level.getServer().getWorldData().setEndDragonFightData(EndDragonFight.Data.DEFAULT);
        ContentLog.LOGGER.info("The end's land is gone, so the dragon and its fight start over with it");
    }

    static CompoundTag runRecord(ResourceKey<Level> dimension, int middleX, int middleZ, int reach) {
        CompoundTag run = new CompoundTag();
        run.putString("dimension", dimension.location().toString());
        run.putInt("middleX", middleX);
        run.putInt("middleZ", middleZ);
        run.putInt("reach", reach);
        return run;
    }

    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) { startWhenEntered(player.server, event.getTo()); }
    }
}
