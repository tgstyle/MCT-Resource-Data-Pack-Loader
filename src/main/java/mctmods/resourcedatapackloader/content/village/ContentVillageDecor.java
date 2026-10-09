package mctmods.resourcedatapackloader.content.village;

import mctmods.blastplaster.util.BlastPlasterUtil;
import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.interfaces.IContentShape;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.content.worldgen.ContentBeardLamps;
import mctmods.resourcedatapackloader.content.worldgen.ContentPlacer;
import mctmods.resourcedatapackloader.content.worldgen.ContentStructureSearch;
import mctmods.resourcedatapackloader.content.worldgen.ContentWorldgen;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardKeep;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardPlots;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRoads;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardRoadsTunnels;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.WeightedPicks;
import mctmods.resourcedatapackloader.util.world.GroundLevel;
import mctmods.resourcedatapackloader.util.world.SeededRandom;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class ContentVillageDecor {
    private static final String NAME = "villageDecor";
    private static final int STEP = 3;
    private static final int SPREAD = 8;
    private static final int RISE = 16;
    private static final WeightedPicks CHOICES = new WeightedPicks(NAME);
    private static final String[] REASONS = { "outside the clip", "inside a piece or plaza", "without ground", "not open air on solid ground", "beside cubes not loaded", "before a door", "rolled bare", "without a shape", "planted", "against a keep-out", "refused by its shape" };

    private ContentVillageDecor() {}

    @SubscribeEvent(priority = EventPriority.LOWEST) public static void onDecorated(PopulateChunkEvent.Post event) {
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

    private static void alongRoad(World world, StructureComponent piece, StructureBoundingBox clip, int[] tally) {
        StructureBoundingBox box = piece.getBoundingBox();
        if (box.minX - 2 > clip.maxX || box.maxX + 2 < clip.minX || box.minZ - 2 > clip.maxZ || box.maxZ + 2 < clip.minZ) { return; }
        Collection<StructureStart> villages = ContentStructureSearch.villageStarts(world);
        boolean wanted = ContentBeard.wanted();
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        boolean alongX = BeardPlots.roadAlongX(box);
        int from = alongX ? box.minX : box.minZ;
        int to = alongX ? box.maxX : box.maxZ;
        int near = (alongX ? box.minZ : box.minX) - 1;
        int far = (alongX ? box.maxZ : box.maxX) + 1;
        BeardRoads.Grade grade = BeardRoads.layoutOf(piece);
        for (int row = from; row <= to; row++) {
            if (Math.floorMod(row, STEP) != 0 || BeardRoadsTunnels.tunnelAt(grade, row)) { continue; }
            tally[place(world, villages, wanted, clip, at, alongX ? row : near, alongX ? near : row)]++;
            tally[place(world, villages, wanted, clip, at, alongX ? row : far, alongX ? far : row)]++;
        }
    }

    private static int place(World world, Collection<StructureStart> villages, boolean wanted, StructureBoundingBox clip, BlockPos.MutableBlockPos at, int x, int z) {
        if (x < clip.minX || x > clip.maxX || z < clip.minZ || z > clip.maxZ) { return 0; }
        for (StructureStart village : villages) {
            if (BeardPlots.underAnother(village, null, x, z) || BeardPlots.overRoad(village, x, z)) { return 1; }
            if (wanted && BeardPlots.insidePlaza(village.getComponents(), x, z)) { return 1; }
        }
        BlockPos origin = GroundLevel.inWindow(world, new BlockPos(x, 0, z));
        int bed = origin.getY();
        if (bed <= 1 || BeardKeep.holds(x, bed, z)) { return 2; }
        if (!world.isAirBlock(origin) || !world.getBlockState(origin.down()).getMaterial().isSolid()) { return 3; }
        if (!world.isAreaLoaded(origin, SPREAD + 1)) { return 4; }
        if (ContentBeardLamps.beforeADoor(world, new StructureBoundingBox(x - 2, 0, z - 2, x + 2, 255, z + 2), at, x, bed, z)) { return 5; }
        Random random = SeededRandom.at(world, x, z);
        WeightedPicks.Pick chosen = CHOICES.pick(random);
        if (chosen == null || WeightedPicks.EMPTY.equals(chosen.name)) { return 6; }
        IContentShape figure = ContentWorldgen.shapeFor(chosen.name);
        if (figure == null) { return 7; }
        StructureBoundingBox area = new StructureBoundingBox(x - SPREAD, bed - 1, z - SPREAD, x + SPREAD, bed + RISE, z + SPREAD);
        ContentPlacer.keepOutOf(villages);
        try {
            if (!ContentBeard.wanted()) {
                return figure.generate(world, random, origin) ? 8 : 9;
            }
            BeardKeep.watchArea(world, area, NAME);
            boolean made = figure.generate(world, random, origin);
            BeardKeep.learn(world);
            return made ? 8 : ContentPlacer.keptOut() ? 9 : 10;
        }
        finally { ContentPlacer.releaseKeepOut(); }
    }

    public static boolean plantedAt(World world, BlockPos wood) {
        IContentShape figure = figureAt(world, wood);
        return figure != null && figure.places(world, world.getBlockState(wood), canopiesOver(world, wood));
    }

    public static boolean sustains(World world, BlockPos wood, IBlockState leaf) {
        IContentShape figure = figureAt(world, wood);
        if (figure == null) { return true; }
        IBlockState state = world.getBlockState(wood);
        return !figure.places(world, state, canopiesOver(world, wood)) || figure.places(world, state, Collections.singletonList(leaf));
    }

    private static IContentShape figureAt(World world, BlockPos wood) {
        if (CHOICES.stale()) { load(); }
        if (CHOICES.isEmpty()) { return null; }
        int x = wood.getX();
        int z = wood.getZ();
        boolean spot = false;
        for (StructureStart village : ContentStructureSearch.villageStarts(world)) {
            if (village != null && BeardPlots.vergeSpot(village, x, z, STEP)) {
                spot = true;
                break;
            }
        }
        if (!spot) { return null; }
        WeightedPicks.Pick chosen = CHOICES.pick(SeededRandom.at(world, x, z));
        if (chosen == null || WeightedPicks.EMPTY.equals(chosen.name)) { return null; }
        return ContentWorldgen.shapeFor(chosen.name);
    }

    private static List<IBlockState> canopiesOver(World world, BlockPos wood) {
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos(wood);
        for (int climbed = 0; climbed < 64 && BlastPlasterUtil.isTreeWood(world.getBlockState(at)); climbed++) { at.move(EnumFacing.UP); }
        List<IBlockState> crowns = new ArrayList<>();
        IBlockState over = world.getBlockState(at);
        if (over.getMaterial() == Material.LEAVES) { crowns.add(over); }
        at.move(EnumFacing.DOWN);
        BlockPos top = at.toImmutable();
        for (EnumFacing side : EnumFacing.HORIZONTALS) {
            IBlockState beside = world.getBlockState(top.offset(side));
            if (beside.getMaterial() == Material.LEAVES) { crowns.add(beside); }
        }
        return crowns;
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
