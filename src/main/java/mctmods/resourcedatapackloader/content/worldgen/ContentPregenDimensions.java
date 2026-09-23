package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.interfaces.IPregenMemory;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IWorldProviderEnd;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldServer;
import net.minecraft.world.end.DragonFightManager;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

public final class ContentPregenDimensions {
    static final Deque<Integer> PENDING = new ArrayDeque<>();
    public static final int VANILLA_SPAWN_REACH = 12;
    static int wantedRadius;
    static boolean chaining;
    private static final double NO_BORDER = 6.0E7D;

    public static int[] wantedDimensions() { return ContentControl.numbers(ContentControl.CHUNKS, "pregenDimensions", Config.chunks.pregenDimensions); }

    public static boolean makesEveryDimension() { return ContentControl.flag(ContentControl.CHUNKS, "pregenAllDimensions", Config.chunks.pregenAllDimensions); }

    private static int[] chosenDimensions() {
        if (!makesEveryDimension()) { return wantedDimensions(); }
        List<Integer> picked = new ArrayList<>();
        for (Integer dimension : DimensionManager.getStaticDimensionIDs()) {
            if (madeUpFront(dimension)) { picked.add(dimension); }
        }
        Collections.sort(picked);
        if (picked.remove(Integer.valueOf(0))) { picked.add(0, 0); }
        int[] chosen = new int[picked.size()];
        for (int at = 0; at < chosen.length; at++) { chosen[at] = picked.get(at); }
        return chosen;
    }

    public static int[] enteredDimensions() {
        return ContentControl.numbers(ContentControl.CHUNKS, "pregenDimensionsWhenEntered", Config.chunks.pregenDimensionsWhenEntered);
    }

    private static boolean madeUpFront(int dimension) {
        for (int later : enteredDimensions()) {
            if (later == dimension) { return false; }
        }
        return true;
    }

    static void startWhenEntered(int dimension) {
        int radius = wantedOnNewWorld();
        if ((radius <= 0 && !reachesTheBorder()) || madeUpFront(dimension)) { return; }
        if (ContentPregen.running != null && ContentPregen.running.dimension == dimension) { return; }
        if (PENDING.contains(dimension)) { return; }
        WorldServer entered = DimensionManager.getWorld(dimension);
        if (!reachesTheBorder() && entered != null) {
            BlockPos spawn = entered.getSpawnPoint();
            if (alreadyMade(memory(), dimension, radius, entered, spawn.getX() >> 4, spawn.getZ() >> 4)) { return; }
        }
        PENDING.addLast(dimension);
        wantedRadius = radius;
        chaining = true;
        if (!ContentPregen.busy()) { nextDimension(radius); }
    }

    @SubscribeEvent public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        startWhenEntered(event.toDim);
        if (ContentPregen.running != null || !(event.player instanceof EntityPlayerMP)) { return; }
        if (ContentPregenHold.greetingFor(event.toDim, false) != null) { ContentPregenHold.welcome((EntityPlayerMP) event.player); }
    }

    public static boolean reachesTheBorder() { return ContentControl.flag(ContentControl.CHUNKS, "pregenToBorder", Config.chunks.pregenToBorder); }

    public static boolean picksUpAgain() { return ContentControl.flag(ContentControl.CHUNKS, "pregenResume", Config.chunks.pregenResume); }

    public static int wantedOnNewWorld() {
        int asked = Math.max(0, ContentControl.number(ContentControl.CHUNKS, "pregenOnNewWorld", Config.chunks.pregenOnNewWorld));
        return Math.max(asked, VANILLA_SPAWN_REACH);
    }

    @SubscribeEvent public static void onWorldLoad(WorldEvent.Load event) {
        World world = event.getWorld();
        if (world.isRemote || world.provider.getDimension() != 0 || ContentPregen.busy()) { return; }
        int radius = wantedOnNewWorld();
        IPregenMemory memory = (IPregenMemory) world.getWorldInfo();
        NBTTagCompound run = memory.rdpl$pregenRun();
        if (!run.isEmpty()) {
            int dimension = run.getInteger("dimension");
            if (!picksUpAgain()) {
                memory.rdpl$setPregenRun(null);
                memory.rdpl$setLandMadeAt(dimension, 0);
                if (run.getInteger("reach") > memory.rdpl$landMadeTo(dimension)) { memory.rdpl$setLandMadeTo(dimension, run.getInteger("reach")); }
                ContentLog.LOGGER.info("Land was still being made in dimension {} when the last session ended, and picking up again is off, so the world stands as far as it was made", dimension);
                return;
            }
            if (PENDING.isEmpty() && (radius > 0 || reachesTheBorder())) {
                for (int held : chosenDimensions()) {
                    if (held != dimension && (reachesTheBorder() || memory.rdpl$landMadeTo(held) < radius)) { PENDING.addLast(held); }
                }
                wantedRadius = radius;
                chaining = !PENDING.isEmpty();
            }
            ContentLog.LOGGER.info("Land was still being made in dimension {} when the last session ended, so it is picked up again", dimension);
            ContentPregen.start(null, dimension, run.getInteger("middleX"), run.getInteger("middleZ"), run.getInteger("reach"));
            return;
        }
        if ((radius <= 0 && !reachesTheBorder()) || !PENDING.isEmpty()) { return; }
        for (int dimension : chosenDimensions()) {
            if (reachesTheBorder() || memory.rdpl$landMadeTo(dimension) < radius) { PENDING.addLast(dimension); }
        }
        wantedRadius = radius;
        chaining = true;
        nextDimension(radius);
    }

    static NBTTagCompound runRecord(int dimension, int middleX, int middleZ, int reach) {
        NBTTagCompound run = new NBTTagCompound();
        run.setInteger("dimension", dimension);
        run.setInteger("middleX", middleX);
        run.setInteger("middleZ", middleZ);
        run.setInteger("reach", reach);
        return run;
    }

    static IPregenMemory memory() {
        WorldServer overworld = DimensionManager.getWorld(0);
        return overworld == null ? null : (IPregenMemory) overworld.getWorldInfo();
    }

    static void nextDimension(int radius) {
        while (!PENDING.isEmpty()) {
            int dimension = PENDING.removeFirst();
            if (!DimensionManager.isDimensionRegistered(dimension)) {
                ContentLog.LOGGER.error("A pack asks for land to be made in dimension {}, which nothing here provides, so it is passed over", dimension);
                continue;
            }
            if (DimensionManager.getWorld(dimension) == null) { DimensionManager.initDimension(dimension); }
            WorldServer world = DimensionManager.getWorld(dimension);
            if (world == null) {
                ContentLog.LOGGER.error("Dimension {} would not open, so no land is made in it", dimension);
                continue;
            }
            int centreX;
            int centreZ;
            int reach;
            if (reachesTheBorder()) {
                WorldBorder border = world.getWorldBorder();
                if (border.getDiameter() >= NO_BORDER) {
                    ContentLog.LOGGER.error("A pack asks for the land of dimension {} to be made out to its border, but no border has been set there, so there is nothing to reach and none is made", dimension);
                    continue;
                }
                reach = (int) Math.ceil(border.getDiameter() / 2.0D / 16.0D);
                if (reach > Config.chunks.pregenBorderLimit) {
                    ContentLog.LOGGER.error("The border of dimension {} stands {} block(s) across, which is {} chunk(s) either way and past the {} allowed by pregenBorderLimit, so no land is made out to it", dimension, (long) border.getDiameter(), reach, Config.chunks.pregenBorderLimit);
                    continue;
                }
                centreX = (int) Math.floor(border.getCenterX()) >> 4;
                centreZ = (int) Math.floor(border.getCenterZ()) >> 4;
            }
            else {
                BlockPos spawn = world.getSpawnPoint();
                reach = radius;
                centreX = spawn.getX() >> 4;
                centreZ = spawn.getZ() >> 4;
            }
            if (alreadyMade(memory(), dimension, reach, world, centreX, centreZ)) { continue; }
            long total = ContentPregen.start(null, dimension, centreX, centreZ, reach);
            ContentLog.LOGGER.info("Making {} chunk(s) of land in dimension {}, reaching {} chunk(s) either way from {}, {}, before anybody sets foot in it", total, dimension, reach, centreX, centreZ);
            return;
        }
    }

    private static boolean alreadyMade(IPregenMemory memory, int dimension, int reach, WorldServer world, int centerX, int centerZ) {
        if (memory == null || memory.rdpl$landMadeTo(dimension) < reach) { return false; }
        File region = regionFolder(world);
        if (region == null) { return true; }
        int expected = 0;
        int missing = 0;
        for (int rx = (centerX - reach) >> 5; rx <= (centerX + reach) >> 5; rx++) {
            for (int rz = (centerZ - reach) >> 5; rz <= (centerZ + reach) >> 5; rz++) {
                expected++;
                if (!new File(region, "r." + rx + "." + rz + ".mca").isFile()) { missing++; }
            }
        }
        if (missing == 0) { return true; }
        ContentLog.LOGGER.info("Dimension {} was made before, but {} of the {} region file(s) its land lives in are missing from the disk, so it is being made again", dimension, missing, expected);
        memory.rdpl$setLandMadeTo(dimension, 0);
        memory.rdpl$setLandMadeAt(dimension, 0);
        if (missing == expected) { freshDragon(world); }
        return false;
    }

    private static void freshDragon(WorldServer world) {
        if (!(world.provider instanceof WorldProviderEnd) || !ContentEndDragon.wanted(world)) { return; }
        NBTTagCompound data = world.getWorldInfo().getDimensionData(world.provider.getDimension());
        data.removeTag("DragonFight");
        world.getWorldInfo().setDimensionData(world.provider.getDimension(), data);
        ((IWorldProviderEnd) world.provider).rdpl$setDragonFightManager(new DragonFightManager(world, new NBTTagCompound()));
        ContentLog.LOGGER.info("The end's land is gone, so the dragon and its fight start over with it");
    }

    private static File regionFolder(WorldServer world) {
        if (!(world.getChunkProvider().chunkLoader instanceof AnvilChunkLoader)) { return null; }
        return new File(((AnvilChunkLoader) world.getChunkProvider().chunkLoader).chunkSaveLocation, "region");
    }
}
