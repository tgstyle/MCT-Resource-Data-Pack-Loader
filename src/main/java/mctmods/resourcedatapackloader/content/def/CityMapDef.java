package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import java.util.List;
import java.util.Map;

public record CityMapDef(ResourceLocation key, int cell, Map<Character, Cell> palette, List<String> rows, int cellsWide, int cellsDeep) {
    public static final int LIMIT = 64;
    public static final char OPEN_MARK = '.';

    public enum Kind { STREET, PLAZA, ALLEY, OPEN, GROW, PLOT }

    public record Cell(Kind kind, List<PickDef> picks) {}

    public static CityMapDef of(ResourceLocation key, int cell, Map<Character, Cell> palette, List<String> rows) {
        int wide = 1;
        for (String row : rows) { wide = Math.max(wide, row.length()); }
        return new CityMapDef(key, cell, Map.copyOf(palette), List.copyOf(rows), wide, rows.size());
    }

    public int blocksWide() { return cellsWide * cell; }

    public int blocksDeep() { return cellsDeep * cell; }

    public Kind kindOf(char symbol) {
        if (symbol == OPEN_MARK) { return Kind.OPEN; }
        Cell held = palette.get(symbol);
        return held == null ? Kind.OPEN : held.kind();
    }

    public char[][] marks(Rotation turn) {
        char[][] grid = new char[cellsDeep][cellsWide];
        for (int z = 0; z < cellsDeep; z++) {
            String row = rows.get(z);
            for (int x = 0; x < cellsWide; x++) { grid[z][x] = x < row.length() ? row.charAt(x) : OPEN_MARK; }
        }
        for (int quarter = quarters(turn); quarter > 0; quarter--) { grid = clockwise(grid); }
        return grid;
    }

    public Kind[][] kinds(char[][] marks) {
        Kind[][] grid = new Kind[marks.length][marks[0].length];
        for (int z = 0; z < marks.length; z++) {
            for (int x = 0; x < marks[z].length; x++) { grid[z][x] = kindOf(marks[z][x]); }
        }
        return grid;
    }

    private static int quarters(Rotation turn) {
        if (turn == Rotation.CLOCKWISE_90) { return 1; }
        if (turn == Rotation.CLOCKWISE_180) { return 2; }
        if (turn == Rotation.COUNTERCLOCKWISE_90) { return 3; }
        return 0;
    }

    private static char[][] clockwise(char[][] grid) {
        int deep = grid.length;
        int wide = grid[0].length;
        char[][] turned = new char[wide][deep];
        for (int z = 0; z < wide; z++) {
            for (int x = 0; x < deep; x++) { turned[z][x] = grid[deep - 1 - x][z]; }
        }
        return turned;
    }
}
