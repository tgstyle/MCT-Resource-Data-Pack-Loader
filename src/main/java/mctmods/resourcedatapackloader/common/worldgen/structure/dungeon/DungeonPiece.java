package mctmods.resourcedatapackloader.common.worldgen.structure.dungeon;

import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonDef;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlanner;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.PlanRecords;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.write.DungeonWriter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public final class DungeonPiece extends StructurePiece {
    public static final StructurePieceType TYPE = (StructurePieceType.ContextlessType) DungeonPiece::new;
    private static final String DUNGEON = "Dungeon";
    private static final String ORIGIN_X = "OriginX";
    private static final String ORIGIN_Z = "OriginZ";
    private static final String SEED = "SiteSeed";
    private static final String REGION_X = "RegionX";
    private static final String REGION_Z = "RegionZ";
    private static final String INDEX = "Index";
    private static final Map<DungeonPlan, List<BoundingBox>> ITEMS = Collections.synchronizedMap(new WeakHashMap<>());
    private final String dungeon;
    private final int originX;
    private final int originZ;
    private final long siteSeed;
    private final int regionX;
    private final int regionZ;
    private final int index;

    public DungeonPiece(BoundingBox box, String dungeon, int originX, int originZ, long siteSeed, int regionX, int regionZ, int index) {
        super(TYPE, 0, box);
        this.dungeon = dungeon;
        this.originX = originX;
        this.originZ = originZ;
        this.siteSeed = siteSeed;
        this.regionX = regionX;
        this.regionZ = regionZ;
        this.index = index;
    }

    public DungeonPiece(CompoundTag tag) {
        super(TYPE, tag);
        this.dungeon = tag.getStringOr(DUNGEON, "");
        this.originX = tag.getIntOr(ORIGIN_X, 0);
        this.originZ = tag.getIntOr(ORIGIN_Z, 0);
        this.siteSeed = tag.getLongOr(SEED, 0L);
        this.regionX = tag.getIntOr(REGION_X, 0);
        this.regionZ = tag.getIntOr(REGION_Z, 0);
        this.index = tag.getIntOr(INDEX, 0);
    }

    @Override protected void addAdditionalSaveData(@Nonnull StructurePieceSerializationContext context, @Nonnull CompoundTag tag) {
        tag.putString(DUNGEON, dungeon);
        tag.putInt(ORIGIN_X, originX);
        tag.putInt(ORIGIN_Z, originZ);
        tag.putLong(SEED, siteSeed);
        tag.putInt(REGION_X, regionX);
        tag.putInt(REGION_Z, regionZ);
        tag.putInt(INDEX, index);
    }

    @Override public void postProcess(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) {
        DungeonDef def = ContentDungeons.byName(dungeon);
        if (def == null) { return; }
        DungeonWorld world = ContentDungeons.world(level);
        DungeonSites.Site site = ContentDungeons.site(world, def, originX, originZ);
        if (site == null) { return; }
        DungeonPlan plan = DungeonPlanner.get(world, def, site);
        BoundingBox region = ContentDungeonStructure.region(regionX, regionZ);
        if (plan.site.siteSeed() != siteSeed || !writes(plan, region, box)) { return; }
        int floor = level.getMinY();
        int ceiling = level.getMaxY();
        BoundingBox window = new BoundingBox(Math.max(box.minX(), region.minX()), floor, Math.max(box.minZ(), region.minZ()), Math.min(box.maxX(), region.maxX()), ceiling, Math.min(box.maxZ(), region.maxZ()));
        DungeonWriter.write(level, site, window, floor, ceiling);
    }

    private boolean writes(DungeonPlan plan, BoundingBox region, BoundingBox box) {
        List<BoundingBox> items = items(plan);
        if (index >= items.size()) { return false; }
        for (int earlier = 0; earlier < index; earlier++) {
            BoundingBox clipped = clip(items.get(earlier), region);
            if (clipped != null && clipped.intersects(box)) { return false; }
        }
        return true;
    }

    @Nullable static BoundingBox clip(BoundingBox item, BoundingBox region) {
        if (!item.intersects(region.minX(), region.minZ(), region.maxX(), region.maxZ())) { return null; }
        return new BoundingBox(Math.max(item.minX(), region.minX()), item.minY(), Math.max(item.minZ(), region.minZ()), Math.min(item.maxX(), region.maxX()), item.maxY(), Math.min(item.maxZ(), region.maxZ()));
    }

    static List<BoundingBox> items(DungeonPlan plan) { return ITEMS.computeIfAbsent(plan, DungeonPiece::list); }

    private static List<BoundingBox> list(DungeonPlan plan) {
        List<BoundingBox> items = new ArrayList<>();
        for (DungeonPlan.Level level : plan.levels) {
            for (DungeonPlan.Room room : level.rooms) {
                items.add(new BoundingBox(room.x0 - 1, room.y0 - 1, room.z0 - 1, room.x1 + 1, room.y1 + 1, room.z1 + 1));
                for (PlanRecords.Feature feature : room.features) { items.add(new BoundingBox(feature.x0(), feature.y0(), feature.z0(), feature.x1(), feature.y1(), feature.z1())); }
            }
            for (DungeonPlan.Link link : level.links) {
                List<BoundingBox> parts = new ArrayList<>();
                for (DungeonPlan.Segment segment : link.segments) {
                    parts.add(new BoundingBox(Math.min(segment.x0(), segment.x1()) - link.width, Math.min(segment.floorYStart(), segment.floorYEnd()), Math.min(segment.z0(), segment.z1()) - link.width,
                            Math.max(segment.x0(), segment.x1()) + link.width, Math.max(segment.floorYStart(), segment.floorYEnd()) + link.height, Math.max(segment.z0(), segment.z1()) + link.width));
                }
                BoundingBox.encapsulatingBoxes(parts).ifPresent(items::add);
            }
            PlanRecords.Shaft shaft = level.shaft;
            if (shaft != null) { items.add(new BoundingBox(shaft.x() - 1, shaft.bottomY(), shaft.z() - 1, shaft.x() + 3, shaft.topY() + 4, shaft.z() + 3)); }
        }
        if (plan.entrance != null) {
            List<PlanRecords.Entrance> entrances = new ArrayList<>();
            entrances.add(plan.entrance);
            entrances.addAll(plan.entrance.extras());
            for (PlanRecords.Entrance entrance : entrances) {
                items.add(new BoundingBox(entrance.x() - PlanRecords.ENTRANCE_REACH, plan.site.surfaceY() - PlanRecords.ENTRANCE_DOWN, entrance.z() - PlanRecords.ENTRANCE_REACH,
                        entrance.x() + PlanRecords.ENTRANCE_REACH, plan.site.surfaceY() + PlanRecords.ENTRANCE_UP, entrance.z() + PlanRecords.ENTRANCE_REACH));
            }
        }
        for (PlanRecords.Spawner spawner : plan.spawners) { items.add(new BoundingBox(new BlockPos(spawner.x(), spawner.y(), spawner.z()))); }
        for (PlanRecords.Chest chest : plan.chests) { items.add(new BoundingBox(new BlockPos(chest.x(), chest.y(), chest.z()))); }
        return List.copyOf(items);
    }
}
