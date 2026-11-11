package mctmods.resourcedatapackloader.common.worldgen.structure.map;

import mctmods.resourcedatapackloader.common.pack.definitions.games.PickDef;
import mctmods.resourcedatapackloader.common.pack.definitions.worlds.StructureMapDef;
import mctmods.resourcedatapackloader.core.util.math.Hashes;

import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;

public final class ContentMapCells {
    private ContentMapCells() {}

    public record Cell(int cornerX, int cornerZ, int y, long seed, String named, Identifier template) {}

    public interface Reach { boolean takes(int cornerX, int cornerZ); }

    public interface Visit { boolean place(Cell cell); }

    public static int each(StructureMapDef def, long seed, Rotation turn, int originX, int base, int originZ, Reach reach, Visit visit) {
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
                    if (!reach.takes(cornerX, cornerZ)) { continue; }
                    long cellSeed = Hashes.mix(seed, column, layer, row);
                    String named = PickDef.pick(held.palette().get(mark), RandomSource.create(cellSeed));
                    Identifier template = named == null ? null : Identifier.tryParse(named);
                    if (template == null) { continue; }
                    if (visit.place(new Cell(cornerX, cornerZ, layerY, cellSeed, named, template))) { placed++; }
                }
            }
        }
        return placed;
    }
}
