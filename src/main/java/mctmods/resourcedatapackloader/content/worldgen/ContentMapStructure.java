package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.PickDef;
import mctmods.resourcedatapackloader.content.def.StructureMapDef;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nonnull;
import java.util.Optional;

public final class ContentMapStructure extends Structure {
    public static final MapCodec<ContentMapStructure> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(settingsCodec(instance),
            ResourceLocation.CODEC.fieldOf("map").forGetter(held -> held.map)).apply(instance, ContentMapStructure::new));
    public static final StructureType<ContentMapStructure> TYPE = () -> CODEC;
    private static final Rotation[] TURNS = Rotation.values();
    private final ResourceLocation map;

    public ContentMapStructure(StructureSettings settings, ResourceLocation map) {
        super(settings);
        this.map = map;
    }

    @Override @Nonnull protected Optional<GenerationStub> findGenerationPoint(@Nonnull GenerationContext context) {
        StructureMapDef def = ContentStructureMaps.def(map);
        if (def == null) { return Optional.empty(); }
        ChunkPos chunk = context.chunkPos();
        RandomSource random = context.random();
        Rotation turn = TURNS[random.nextInt(TURNS.length)];
        boolean swapped = turn == Rotation.CLOCKWISE_90 || turn == Rotation.COUNTERCLOCKWISE_90;
        int wide = def.cellsWide() * def.cell();
        int deep = def.cellsDeep() * def.cell();
        int spanX = swapped ? deep : wide;
        int spanZ = swapped ? wide : deep;
        int originX = chunk.getMinBlockX();
        int originZ = chunk.getMinBlockZ();
        int anchor = context.chunkGenerator().getFirstOccupiedHeight(originX + spanX / 2, originZ + spanZ / 2, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
        int floor = anchor + 1;
        int base = floor - def.ground() * def.cell();
        BlockPos origin = new BlockPos(originX, floor, originZ);
        return Optional.of(new GenerationStub(origin, builder -> pieces(context, def, random.nextLong(), turn, origin, base, wide, deep, builder)));
    }

    private void pieces(GenerationContext context, StructureMapDef def, long seed, Rotation turn, BlockPos origin, int base, int wide, int deep, StructurePiecesBuilder builder) {
        int placed = 0;
        int originX = origin.getX();
        int originZ = origin.getZ();
        for (int layer = 0; layer < def.layers().size(); layer++) {
            StructureMapDef.Layer held = def.layers().get(layer);
            int layerY = base + layer * def.cell();
            for (int row = 0; row < held.rows().size(); row++) {
                String cells = held.rows().get(row);
                for (int column = 0; column < cells.length(); column++) {
                    char mark = cells.charAt(column);
                    if (mark == '.') { continue; }
                    int flatX = column * def.cell();
                    int flatZ = row * def.cell();
                    int cellX;
                    int cellZ;
                    if (turn == Rotation.NONE) {
                        cellX = flatX;
                        cellZ = flatZ;
                    }
                    else if (turn == Rotation.CLOCKWISE_90) {
                        cellX = deep - def.cell() - flatZ;
                        cellZ = flatX;
                    }
                    else if (turn == Rotation.CLOCKWISE_180) {
                        cellX = wide - def.cell() - flatX;
                        cellZ = deep - def.cell() - flatZ;
                    }
                    else {
                        cellX = flatZ;
                        cellZ = wide - def.cell() - flatX;
                    }
                    RandomSource cellRandom = RandomSource.create(seed ^ (column * 31L + layer * 977L + row * 31337L));
                    String named = PickDef.pick(held.palette().get(mark), cellRandom);
                    ResourceLocation template = named == null ? null : ResourceLocation.tryParse(named);
                    if (template == null) { continue; }
                    Optional<StructureTemplate> piece = context.structureTemplateManager().get(template);
                    if (piece.isEmpty()) {
                        ContentStructureMaps.missing(def, named);
                        continue;
                    }
                    Vec3i span = piece.get().getSize(turn);
                    builder.addPiece(new ContentMapPiece(context.structureTemplateManager(), template, turn, new BlockPos(originX + cellX + ContentImprint.backX(turn, span), layerY, originZ + cellZ + ContentImprint.backZ(turn, span))));
                    placed++;
                }
            }
        }
        if (placed > 0) { ContentLog.LOGGER.debug("Structure map {} builds at {}, {}, {}, turned {}, its ground layer floored at y {}", def.key(), originX, origin.getY(), originZ, turn, origin.getY()); }
    }

    @Override @Nonnull public StructureType<?> type() { return TYPE; }
}
