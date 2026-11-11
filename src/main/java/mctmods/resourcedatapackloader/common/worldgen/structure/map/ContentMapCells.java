package mctmods.resourcedatapackloader.common.worldgen.structure.map;

import mctmods.resourcedatapackloader.common.pack.definitions.games.PickDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.StructureMapDef;
import mctmods.resourcedatapackloader.common.worldgen.feature.shape.ContentImprint;
import mctmods.resourcedatapackloader.core.util.math.Hashes;

import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public final class ContentMapCells {
    private static final int FLAGS = 2;

    private ContentMapCells() {}

    public record Cell(ResourceLocation template, StructureTemplate structure, long seed, BlockPos at) {}

    public static BoundingBox box(StructureMapDef def, Rotation turn, int originX, int base, int originZ) {
        boolean swapped = turn == Rotation.CLOCKWISE_90 || turn == Rotation.COUNTERCLOCKWISE_90;
        int wide = def.cellsWide() * def.cell();
        int deep = def.cellsDeep() * def.cell();
        return new BoundingBox(originX, base, originZ, originX + (swapped ? deep : wide) - 1, base + def.layers().size() * def.cell() - 1, originZ + (swapped ? wide : deep) - 1);
    }

    public static int walk(StructureTemplateManager manager, StructureMapDef def, long seed, Rotation turn, int originX, int base, int originZ, @Nullable BoundingBox corners, Consumer<Cell> visitor) {
        int wide = def.cellsWide() * def.cell();
        int deep = def.cellsDeep() * def.cell();
        int placed = 0;
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
                    int cornerX = originX + cellX;
                    int cornerZ = originZ + cellZ;
                    if (corners != null && (cornerX < corners.minX() || cornerX > corners.maxX() || cornerZ < corners.minZ() || cornerZ > corners.maxZ())) { continue; }
                    long cellSeed = Hashes.mix(seed, column, layer, row);
                    String named = PickDef.pick(held.palette().get(mark), RandomSource.create(cellSeed));
                    ResourceLocation template = named == null ? null : ResourceLocation.tryParse(named);
                    if (template == null) { continue; }
                    Optional<StructureTemplate> piece = manager.get(template);
                    if (piece.isEmpty()) {
                        ContentStructureMaps.missing(def, named);
                        continue;
                    }
                    Vec3i span = piece.get().getSize(turn);
                    if (Math.max(span.getX(), span.getZ()) > StructureMapDef.CELL_MOST) { ContentStructureMaps.oversize(def, named, Math.max(span.getX(), span.getZ())); }
                    visitor.accept(new Cell(template, piece.get(), cellSeed, new BlockPos(cornerX + ContentImprint.backX(turn, span), layerY, cornerZ + ContentImprint.backZ(turn, span))));
                    placed++;
                }
            }
        }
        return placed;
    }

    public static void rebuild(ServerLevel level, StructureMapDef def, long seed, Rotation turn, BlockPos origin, BoundingBox area, @Nullable StructureProcessor filter) {
        walk(level.getStructureManager(), def, seed, turn, origin.getX(), origin.getY(), origin.getZ(), null, cell -> {
            StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(turn).setMirror(Mirror.NONE).setBoundingBox(area);
            if (def.fills()) { settings.addProcessor(new ContentMapFillProcessor(def, cell.seed())); }
            if (filter != null) { settings.addProcessor(filter); }
            cell.structure().placeInWorld(level, cell.at(), cell.at(), settings, RandomSource.create(cell.seed()), FLAGS);
        });
    }
}
