package mctmods.resourcedatapackloader.common.worldgen.structure.map;

import mctmods.resourcedatapackloader.common.pack.definitions.games.PickDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.StructureMapDef;
import mctmods.resourcedatapackloader.common.worldgen.feature.shape.ContentImprint;
import mctmods.resourcedatapackloader.core.util.math.Hashes;

import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public final class ContentMapCells {
    private ContentMapCells() {}

    public record Cell(ResourceLocation name, StructureTemplate template, BlockPos at, long seed) {}

    public interface Reach { boolean at(int cornerX, int cornerZ); }

    public static StructurePlaceSettings settings(StructureMapDef def, Rotation rotation, long seed) {
        StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(rotation).setMirror(Mirror.NONE);
        if (def != null && def.fills()) { settings.addProcessor(new ContentMapFillProcessor(def, seed)); }
        return settings;
    }

    public static int each(StructureMapDef def, StructureTemplateManager manager, long spotSeed, Rotation turn, int x, int base, int z, Reach reach, Consumer<Cell> take) {
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
                    int cornerX = x + cellX;
                    int cornerZ = z + cellZ;
                    if (!reach.at(cornerX, cornerZ)) { continue; }
                    long cellSeed = Hashes.mix(spotSeed, column, layer, row);
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
                    take.accept(new Cell(template, piece.get(), new BlockPos(cornerX + ContentImprint.backX(turn, span), layerY, cornerZ + ContentImprint.backZ(turn, span)), cellSeed));
                    placed++;
                }
            }
        }
        return placed;
    }
}
