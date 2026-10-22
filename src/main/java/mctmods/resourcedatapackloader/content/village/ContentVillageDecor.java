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

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public final class ContentVillageDecor {
    private static final String NAME = "villageDecor";
    private static final String SPACING = "villageDecorSpacing";
    private static final int SPREAD = 8;
    private static final int RISE = 16;
    private static final WeightedPicks CHOICES = new WeightedPicks(NAME);
    private static final Long2ObjectOpenHashMap<Replay> REPLAYS = new Long2ObjectOpenHashMap<>();
    private static final Scan SCAN = new Scan();
    private static WeakReference<World> replayed = new WeakReference<>(null);
    private static int widest;
    private static final String[] REASONS = { "outside the clip", "inside a piece or plaza", "without ground", "not open air on solid ground", "beside cubes not loaded", "before a door", "rolled bare", "without a shape", "planted", "against a keep-out", "refused by its shape" };

    private ContentVillageDecor() {}

    @SubscribeEvent(priority = EventPriority.LOWEST) public static void onDecorated(PopulateChunkEvent.Post event) {
        World world = event.getWorld();
        if (world.isRemote) { return; }
        REPLAYS.clear();
        if (CHOICES.stale()) { load(world); }
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
        int step = step();
        for (int row = from; row <= to; row++) {
            if (Math.floorMod(row, step) != 0 ||BeardRoadsTunnels.tunnelAt(grade, row)) { continue; }
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

    public static boolean plantedAt(World world, BlockPos wood) { return figureAt(world, wood) != null; }

    public static boolean sustains(World world, BlockPos wood, IBlockState leaf) {
        Replay replay = figureAt(world, wood);
        return replay == null || replay.places(world, world.getBlockState(wood), Collections.singletonList(leaf));
    }

    @Nullable private static Replay figureAt(World world, BlockPos wood) {
        if (CHOICES.stale()) { load(world); }
        if (CHOICES.isEmpty()) { return null; }
        int x = wood.getX();
        int z = wood.getZ();
        Collection<StructureStart> villages = ContentStructureSearch.villageStarts(world);
        IBlockState state = world.getBlockState(wood);
        List<IBlockState> crowns = null;
        int step = step();
        for (StructureStart village : villages) {
            if (village == null || !BeardPlots.vergeSpot(village, x, z, step)) { continue; }
            Replay here = replayFor(world, villages, x, z);
            if (here.planted()) {
                crowns = canopiesOver(world, wood);
                if (here.places(world, state, crowns)) { return here; }
            }
            break;
        }
        return widest > 0 ? SCAN.find(world, villages, wood, state, crowns, step) : null;
    }

    private static Replay replayFor(World world, Collection<StructureStart> villages, int x, int z) {
        if (replayed.get() != world) {
            REPLAYS.clear();
            replayed = new WeakReference<>(world);
        }
        long key = BeardKeep.packed(x, 0, z);
        Replay held = REPLAYS.get(key);
        if (held == null) {
            held = replay(world, villages, x, z);
            REPLAYS.put(key, held);
        }
        return held;
    }

    private static Replay replay(World world, Collection<StructureStart> villages, int x, int z) {
        boolean wanted = ContentBeard.wanted();
        for (StructureStart village : villages) {
            if (village == null) { continue; }
            if (BeardPlots.underAnother(village, null, x, z) || BeardPlots.overRoad(village, x, z)) { return Replay.NONE; }
            if (wanted && BeardPlots.insidePlaza(village.getComponents(), x, z)) { return Replay.NONE; }
        }
        Random random = SeededRandom.at(world, x, z);
        WeightedPicks.Pick chosen = CHOICES.pick(random);
        if (chosen == null || WeightedPicks.EMPTY.equals(chosen.name)) { return Replay.NONE; }
        IContentShape figure = ContentWorldgen.shapeFor(chosen.name);
        if (figure == null) { return Replay.NONE; }
        return new Replay(figure, figure.footprint(world, random, x, z, box -> underBuilding(villages, box)));
    }

    private static boolean underBuilding(Collection<StructureStart> villages, IContentShape.Footprint box) {
        for (int x = box.minX; x <= box.maxX; x++) {
            for (int z = box.minZ; z <= box.maxZ; z++) {
                for (StructureStart village : villages) {
                    if (village != null && BeardPlots.underBuilding(village, null, x, z)) { return true; }
                }
            }
        }
        return false;
    }

    private static int reachOf(World world) {
        int most = 0;
        for (String name : CHOICES.names()) {
            IContentShape figure = WeightedPicks.EMPTY.equals(name) ? null : ContentWorldgen.shapeFor(name);
            if (figure != null) { most = Math.max(most, figure.reach(world)); }
        }
        return most;
    }

    private static final class Replay {
        static final Replay NONE = new Replay(null, null);
        @Nullable private final IContentShape figure;
        @Nullable private final IContentShape.Footprint box;

        Replay(@Nullable IContentShape figure, @Nullable IContentShape.Footprint box) {
            this.figure = figure;
            this.box = box;
        }

        boolean planted() { return figure != null && box != null; }

        boolean covers(int x, int z) { return box != null && box.covers(x, z); }

        boolean places(World world, IBlockState wood, List<IBlockState> canopies) { return figure != null && figure.places(world, wood, canopies, box == null ? null : box.structure); }
    }

    private static final class Scan implements BeardPlots.ISpots {
        private World world;
        private Collection<StructureStart> villages;
        private BlockPos wood;
        private IBlockState state;
        private List<IBlockState> crowns;
        private Replay found;

        @Nullable Replay find(World world, Collection<StructureStart> villages, BlockPos wood, IBlockState state, @Nullable List<IBlockState> crowns, int step) {
            this.world = world;
            this.villages = villages;
            this.wood = wood;
            this.state = state;
            this.crowns = crowns;
            try {
                for (StructureStart village : villages) {
                    if (village != null && BeardPlots.vergeSpotsNear(village, wood.getX(), wood.getZ(), widest, step, this)) { return found; }
                }
                return null;
            }
            finally {
                this.world = null;
                this.villages = null;
                this.wood = null;
                this.state = null;
                this.crowns = null;
                found = null;
            }
        }

        @Override public boolean accept(int spotX, int spotZ) {
            Replay replay = replayFor(world, villages, spotX, spotZ);
            if (!replay.covers(wood.getX(), wood.getZ())) { return false; }
            if (crowns == null) { crowns = canopiesOver(world, wood); }
            if (!replay.places(world, state, crowns)) { return false; }
            found = replay;
            return true;
        }
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

    private static int step() {
        JsonObject was = ContentControl.roadKeys(null);
        try { return MathHelper.clamp(ContentControl.number(ContentControl.VILLAGES, SPACING, 3), 1, 16); }
        finally { ContentControl.roadKeys(was); }
    }

    private static void load(World world) {
        JsonObject was = ContentControl.roadKeys(null);
        try { CHOICES.load(ContentControl.list(ContentControl.VILLAGES, NAME, Config.worldgen.villageDecor)); }
        finally { ContentControl.roadKeys(was); }
        REPLAYS.clear();
        widest = reachOf(world);
        for (String name : CHOICES.names()) {
            if (WeightedPicks.EMPTY.equals(name) || ContentWorldgen.shapeFor(name) != null) { continue; }
            ContentLog.LOGGER.error("{} names {}, which no pack registers as worldgen, so its share of the verge is left bare", NAME, name);
        }
        if (!CHOICES.isEmpty()) { ContentLog.LOGGER.info("Villages scatter {} kind(s) of decoration along their roads", CHOICES.size()); }
    }
}
