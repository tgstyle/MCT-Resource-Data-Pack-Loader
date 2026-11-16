package mctmods.resourcedatapackloader.common.worldgen.structure.dungeon;

import mctmods.resourcedatapackloader.common.pack.definitions.worlds.DungeonDef;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlan;
import mctmods.resourcedatapackloader.common.worldgen.structure.dungeon.plan.DungeonPlanner;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

public final class ContentDungeonStructure extends Structure {
    public static final MapCodec<ContentDungeonStructure> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(settingsCodec(instance),
            Identifier.CODEC.fieldOf("dungeon").forGetter(held -> held.dungeon)).apply(instance, ContentDungeonStructure::new));
    public static final StructureType<ContentDungeonStructure> TYPE = () -> CODEC;
    public static final int REGION_CHUNKS = 6;
    private final Identifier dungeon;

    public ContentDungeonStructure(StructureSettings settings, Identifier dungeon) {
        super(settings);
        this.dungeon = dungeon;
    }

    @Override @Nonnull protected Optional<GenerationStub> findGenerationPoint(@Nonnull GenerationContext context) {
        DungeonDef def = ContentDungeons.byName(dungeon.toString());
        DungeonWorld world = def == null ? null : ContentDungeons.world(context);
        if (world == null || !def.allowsDimension(world.dimension())) { return Optional.empty(); }
        int regionX = Math.floorDiv(context.chunkPos().x(), REGION_CHUNKS) * REGION_CHUNKS;
        int regionZ = Math.floorDiv(context.chunkPos().z(), REGION_CHUNKS) * REGION_CHUNKS;
        BoundingBox region = region(regionX, regionZ);
        List<DungeonPiece> pieces = new ArrayList<>();
        for (DungeonSites.Site site : DungeonSites.sitesTouching(world, region)) {
            if (!site.def.key().equals(def.key())) { continue; }
            DungeonPlan plan = DungeonPlanner.get(world, site.def, site);
            List<BoundingBox> items = DungeonPiece.items(plan);
            for (int index = 0; index < items.size(); index++) {
                BoundingBox clipped = DungeonPiece.clip(items.get(index), region);
                if (clipped != null) { pieces.add(new DungeonPiece(clipped, def.key().toString(), site.originX, site.originZ, plan.site.siteSeed(), regionX, regionZ, index)); }
            }
        }
        if (pieces.isEmpty()) { return Optional.empty(); }
        BlockPos origin = new BlockPos(region.minX(), context.chunkGenerator().getSeaLevel(), region.minZ());
        return Optional.of(new GenerationStub(origin, builder -> pieces.forEach(builder::addPiece)));
    }

    static BoundingBox region(int regionX, int regionZ) { return new BoundingBox(regionX << 4, 0, regionZ << 4, ((regionX + REGION_CHUNKS) << 4) - 1, 0, ((regionZ + REGION_CHUNKS) << 4) - 1); }

    @Override @Nonnull public StructureType<?> type() { return TYPE; }

    public Identifier dungeon() { return dungeon; }
}
