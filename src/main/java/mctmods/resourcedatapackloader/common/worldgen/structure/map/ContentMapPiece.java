package mctmods.resourcedatapackloader.common.worldgen.structure.map;

import mctmods.resourcedatapackloader.common.respawn.RespawnRecorder;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentMapPiece extends TemplateStructurePiece {
    public static final StructurePieceType TYPE = (StructurePieceType.StructureTemplateType) ContentMapPiece::new;
    private static final String ROTATION = "Rot";
    private static final String MAP = "Map";
    private static final String SEED = "Seed";
    private final String map;
    private final long seed;
    @Nullable private final ContentMapSite site;

    public ContentMapPiece(StructureTemplateManager manager, ResourceLocation template, Rotation rotation, BlockPos corner, ResourceLocation map, long seed, @Nullable ContentMapSite site) {
        super(TYPE, 0, manager, template, template.toString(), settings(rotation, map.toString(), seed), corner);
        this.map = map.toString();
        this.seed = seed;
        this.site = site;
    }

    public ContentMapPiece(StructureTemplateManager manager, CompoundTag tag) {
        super(TYPE, tag, manager, held -> settings(Rotation.valueOf(tag.getString(ROTATION)), tag.getString(MAP), tag.getLong(SEED)));
        this.map = tag.getString(MAP);
        this.seed = tag.getLong(SEED);
        this.site = ContentMapSite.read(tag);
    }

    private static StructurePlaceSettings settings(Rotation rotation, String map, long seed) {
        ResourceLocation key = map.isEmpty() ? null : ResourceLocation.tryParse(map);
        return ContentMapCells.settings(key == null ? null : ContentStructureMaps.def(key), rotation, seed);
    }

    @Override protected void addAdditionalSaveData(@Nonnull StructurePieceSerializationContext context, @Nonnull CompoundTag tag) {
        super.addAdditionalSaveData(context, tag);
        tag.putString(ROTATION, placeSettings.getRotation().name());
        tag.putString(MAP, map);
        tag.putLong(SEED, seed);
        if (site != null) { site.write(tag); }
    }

    @Override public void postProcess(@Nonnull WorldGenLevel level, @Nonnull StructureManager manager, @Nonnull ChunkGenerator generator, @Nonnull RandomSource random, @Nonnull BoundingBox box, @Nonnull ChunkPos chunk, @Nonnull BlockPos pos) {
        super.postProcess(level, manager, generator, random, box, chunk, pos);
        if (site != null) { RespawnRecorder.record(level.getLevel(), RespawnRecorder.resolve(site.kind(), site.owner()), site.instance(), box, level); }
    }

    @Override protected void handleDataMarker(@Nonnull String name, @Nonnull BlockPos pos, @Nonnull ServerLevelAccessor level, @Nonnull RandomSource random, @Nonnull BoundingBox box) {}
}
