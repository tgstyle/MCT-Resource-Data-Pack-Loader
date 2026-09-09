package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.PickDef;
import mctmods.resourcedatapackloader.content.def.StructureMapDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Hashes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ContentMapStructure extends Structure {
    public static final Codec<ContentMapStructure> CODEC = RecordCodecBuilder.create(instance -> instance.group(settingsCodec(instance),
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
        int windowX = context.chunkPos().getMinBlockX();
        int windowZ = context.chunkPos().getMinBlockZ();
        List<Spot> spots = spots(context, def, windowX, windowZ);
        if (spots.isEmpty()) { return Optional.empty(); }
        BlockPos origin = new BlockPos(windowX, context.chunkGenerator().getSeaLevel(), windowZ);
        return Optional.of(new GenerationStub(origin, builder -> {
            for (Spot spot : spots) { pieces(context, def, spot, windowX, windowZ, builder); }
        }));
    }

    private static List<Spot> spots(GenerationContext context, StructureMapDef def, int windowX, int windowZ) {
        int window = StructureMapDef.window();
        int widest = def.widest();
        long salted = context.seed() ^ def.key().hashCode();
        List<Spot> spots = new ArrayList<>();
        if (def.at() != null && reaches(def.at()[0], def.at()[1], windowX, windowZ, window, widest)) {
            spots.add(spot(context, def, Hashes.mix(salted, def.at()[0], 0, def.at()[1]), def.at()[0], def.at()[1]));
        }
        if (def.spacing() <= 0) { return spots; }
        int pitch = def.spacing() * 16;
        int give = Math.max(1, pitch - widest);
        for (int gridX = Math.floorDiv(windowX - widest, pitch); gridX <= Math.floorDiv(windowX + window - 1, pitch); gridX++) {
            for (int gridZ = Math.floorDiv(windowZ - widest, pitch); gridZ <= Math.floorDiv(windowZ + window - 1, pitch); gridZ++) {
                long seed = Hashes.mix(salted, gridX, 1, gridZ);
                if (Math.floorMod(seed >>> 16, 100) >= def.chance()) { continue; }
                int originX = gridX * pitch + Math.floorMod(seed, give);
                int originZ = gridZ * pitch + Math.floorMod(seed >>> 40, give);
                if (reaches(originX, originZ, windowX, windowZ, window, widest)) { spots.add(spot(context, def, seed, originX, originZ)); }
            }
        }
        return spots;
    }

    private static boolean reaches(int originX, int originZ, int windowX, int windowZ, int window, int widest) {
        return originX + widest > windowX && originX < windowX + window && originZ + widest > windowZ && originZ < windowZ + window;
    }

    private static Spot spot(GenerationContext context, StructureMapDef def, long seed, int originX, int originZ) {
        Rotation turn = TURNS[Math.floorMod(seed >>> 8, TURNS.length)];
        boolean swapped = turn == Rotation.CLOCKWISE_90 || turn == Rotation.COUNTERCLOCKWISE_90;
        int wide = def.cellsWide() * def.cell();
        int deep = def.cellsDeep() * def.cell();
        int spanX = swapped ? deep : wide;
        int spanZ = swapped ? wide : deep;
        int anchor = context.chunkGenerator().getFirstOccupiedHeight(originX + spanX / 2, originZ + spanZ / 2, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
        return new Spot(seed, originX, originZ, turn, anchor + 1 - def.ground() * def.cell());
    }

    private void pieces(GenerationContext context, StructureMapDef def, Spot spot, int windowX, int windowZ, StructurePiecesBuilder builder) {
        int window = StructureMapDef.window();
        int wide = def.cellsWide() * def.cell();
        int deep = def.cellsDeep() * def.cell();
        int placed = 0;
        for (int layer = 0; layer < def.layers().size(); layer++) {
            StructureMapDef.Layer held = def.layers().get(layer);
            int layerY = spot.base() + layer * def.cell();
            for (int row = 0; row < held.rows().size(); row++) {
                String cells = held.rows().get(row);
                for (int column = 0; column < cells.length(); column++) {
                    char mark = cells.charAt(column);
                    if (mark == '.') { continue; }
                    int flatX = column * def.cell();
                    int flatZ = row * def.cell();
                    int cellX;
                    int cellZ;
                    if (spot.turn() == Rotation.NONE) {
                        cellX = flatX;
                        cellZ = flatZ;
                    }
                    else if (spot.turn() == Rotation.CLOCKWISE_90) {
                        cellX = deep - def.cell() - flatZ;
                        cellZ = flatX;
                    }
                    else if (spot.turn() == Rotation.CLOCKWISE_180) {
                        cellX = wide - def.cell() - flatX;
                        cellZ = deep - def.cell() - flatZ;
                    }
                    else {
                        cellX = flatZ;
                        cellZ = wide - def.cell() - flatX;
                    }
                    int cornerX = spot.x() + cellX;
                    int cornerZ = spot.z() + cellZ;
                    if (cornerX < windowX || cornerX >= windowX + window || cornerZ < windowZ || cornerZ >= windowZ + window) { continue; }
                    long cellSeed = Hashes.mix(spot.seed(), column, layer, row);
                    String named = PickDef.pick(held.palette().get(mark), RandomSource.create(cellSeed));
                    ResourceLocation template = named == null ? null : ResourceLocation.tryParse(named);
                    if (template == null) { continue; }
                    Optional<StructureTemplate> piece = context.structureTemplateManager().get(template);
                    if (piece.isEmpty()) {
                        ContentStructureMaps.missing(def, named);
                        continue;
                    }
                    Vec3i span = piece.get().getSize(spot.turn());
                    if (Math.max(span.getX(), span.getZ()) > StructureMapDef.CELL_MOST) { ContentStructureMaps.oversize(def, named, Math.max(span.getX(), span.getZ())); }
                    builder.addPiece(new ContentMapPiece(context.structureTemplateManager(), template, spot.turn(), new BlockPos(cornerX + ContentImprint.backX(spot.turn(), span), layerY, cornerZ + ContentImprint.backZ(spot.turn(), span))));
                    placed++;
                }
            }
        }
        if (placed > 0 && spot.x() >= windowX && spot.x() < windowX + window && spot.z() >= windowZ && spot.z() < windowZ + window) {
            int floor = spot.base() + def.ground() * def.cell();
            ContentLog.LOGGER.debug("Structure map {} builds at {}, {}, {}, turned {}, its ground layer floored at y {}", def.key(), spot.x(), floor, spot.z(), spot.turn(), floor);
        }
    }

    @Override @Nonnull public StructureType<?> type() { return TYPE; }

    private record Spot(long seed, int x, int z, Rotation turn, int base) {}
}
