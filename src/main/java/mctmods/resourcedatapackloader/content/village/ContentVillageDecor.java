package mctmods.resourcedatapackloader.content.village;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.interfaces.IContentShape;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructureSearch;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldgen;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardKeep;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardPlots;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.WeightedPicks;
import mctmods.resourcedatapackloader.util.world.GroundLevel;
import mctmods.resourcedatapackloader.util.world.SeededRandom;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.Collection;
import java.util.Random;

public final class ContentVillageDecor {
    private static final String NAME = "villageDecor";
    private static final int STEP = 3;
    private static final int SPREAD = 8;
    private static final int RISE = 16;
    private static final WeightedPicks CHOICES = new WeightedPicks(NAME);

    private ContentVillageDecor() {}

    @SubscribeEvent public static void onDecorated(PopulateChunkEvent.Post event) {
        World world = event.getWorld();
        if (world.isRemote) { return; }
        if (CHOICES.stale()) { load(); }
        if (CHOICES.isEmpty()) { return; }
        Collection<StructureStart> starts = ContentStructureSearch.villageStarts(world);
        if (starts.isEmpty()) { return; }
        int blockX = (event.getChunkX() << 4) + 8;
        int blockZ = (event.getChunkZ() << 4) + 8;
        StructureBoundingBox clip = new StructureBoundingBox(blockX, 0, blockZ, blockX + 15, 255, blockZ + 15);
        int[] tally = new int[REASONS.length];
        for (StructureStart start : starts) {
            if (start == null || !start.isSizeableStructure() || !start.getBoundingBox().intersectsWith(clip)) { continue; }
            for (StructureComponent piece : start.getComponents()) {
                if (piece instanceof StructureVillagePieces.Path) { alongRoad(world, piece, clip, tally); }
            }
        }
        if (ContentLog.LOGGER.debugEnabled()) {
            int spots = 0;
            for (int count : tally) { spots += count; }
            if (spots > 0) {
                StringBuilder said = new StringBuilder();
                for (int i = 0; i < tally.length; i++) { if (tally[i] > 0) { said.append(said.length() > 0 ? ", " : "").append(tally[i]).append(' ').append(REASONS[i]); } }
                ContentLog.LOGGER.debug("Verge spots in the chunk at {}, {}: {} looked at, {}", blockX, blockZ, spots, said);
            }
        }
    }

    private static final String[] REASONS = { "outside the clip", "inside a piece or plaza", "without ground", "not open air on solid ground", "beside cubes not loaded", "before a door", "rolled bare", "without a shape", "planted" };

    private static void alongRoad(World world, StructureComponent piece, StructureBoundingBox clip, int[] tally) {
        StructureBoundingBox box = piece.getBoundingBox();
        if (box.minX - 2 > clip.maxX || box.maxX + 2 < clip.minX || box.minZ - 2 > clip.maxZ || box.maxZ + 2 < clip.minZ) { return; }
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        boolean alongX = BeardPlots.roadAlongX(box);
        int from = alongX ? box.minX : box.minZ;
        int to = alongX ? box.maxX : box.maxZ;
        int near = (alongX ? box.minZ : box.minX) - 1;
        int far = (alongX ? box.maxZ : box.maxX) + 1;
        for (int row = from; row <= to; row++) {
            if (Math.floorMod(row, STEP) != 0) { continue; }
            tally[place(world, clip, at, alongX ? row : near, alongX ? near : row)]++;
            tally[place(world, clip, at, alongX ? row : far, alongX ? far : row)]++;
        }
    }

    private static int place(World world, StructureBoundingBox clip, BlockPos.MutableBlockPos at, int x, int z) {
        if (x < clip.minX || x > clip.maxX || z < clip.minZ || z > clip.maxZ) { return 0; }
        for (StructureStart village : ContentStructureSearch.villageStarts(world)) {
            if (BeardPlots.underAnother(village, null, x, z) || BeardPlots.overRoad(village, x, z)) { return 1; }
            if (ContentBeard.wanted() && BeardPlots.insidePlaza(village.getComponents(), x, z)) { return 1; }
        }
        BlockPos origin = GroundLevel.inWindow(world, new BlockPos(x, 0, z));
        int bed = origin.getY();
        if (bed <= 1 || BeardKeep.holds(x, bed, z)) { return 2; }
        if (!world.isAirBlock(origin) || !world.getBlockState(origin.down()).getMaterial().isSolid()) { return 3; }
        if (!world.isAreaLoaded(origin, SPREAD + 1)) { return 4; }
        if (ContentBeard.beforeADoor(world, clip, at, x, bed, z)) { return 5; }
        Random random = SeededRandom.at(world, x, z);
        WeightedPicks.Pick chosen = CHOICES.pick(random);
        if (chosen == null || WeightedPicks.EMPTY.equals(chosen.name)) { return 6; }
        IContentShape figure = ContentWorldgen.shapeFor(chosen.name);
        if (figure == null) { return 7; }
        if (!ContentBeard.wanted()) {
            figure.generate(world, random, origin);
            return 8;
        }
        StructureBoundingBox area = new StructureBoundingBox(x - SPREAD, bed - 1, z - SPREAD, x + SPREAD, bed + RISE, z + SPREAD);
        BeardKeep.watchArea(world, area, NAME);
        figure.generate(world, random, origin);
        BeardKeep.learn(world);
        return 8;
    }

    private static void load() {
        CHOICES.load(ContentControl.list(ContentControl.VILLAGES, NAME, Config.worldgen.villageDecor));
        for (String name : CHOICES.names()) {
            if (WeightedPicks.EMPTY.equals(name) || ContentWorldgen.shapeFor(name) != null) { continue; }
            ContentLog.LOGGER.error("{} names {}, which no pack registers as worldgen, so its share of the verge is left bare", NAME, name);
        }
        if (!CHOICES.isEmpty()) { ContentLog.LOGGER.info("Villages scatter {} kind(s) of decoration along their roads", CHOICES.size()); }
    }
}
