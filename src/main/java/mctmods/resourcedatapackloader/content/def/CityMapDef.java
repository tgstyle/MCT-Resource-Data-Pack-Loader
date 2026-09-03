package mctmods.resourcedatapackloader.content.def;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class CityMapDef {
    public static final int LIMIT = 64;

    public enum Kind { STREET, PLAZA, ALLEY, OPEN, GROW, PLOT }

    public static final class Cell {
        public final Kind kind;
        public final List<PickDef> picks;

        public Cell(Kind kind, List<PickDef> picks) {
            this.kind = kind;
            this.picks = Collections.unmodifiableList(picks);
        }
    }

    public final ResourceLocation key;
    public final String name;
    public final int cell;
    public final Map<Character, Cell> palette;
    public final String[] rows;
    public final int cellsWide;
    public final int cellsDeep;

    public CityMapDef(ResourceLocation key, String name, int cell, Map<Character, Cell> palette, String[] rows) {
        this.key = key;
        this.name = name;
        this.cell = cell;
        this.palette = Collections.unmodifiableMap(palette);
        this.rows = rows;
        int wide = 1;
        for (String row : rows) { wide = Math.max(wide, row.length()); }
        this.cellsWide = wide;
        this.cellsDeep = rows.length;
    }

    public Kind kindOf(char symbol) {
        if (symbol == '.') { return Kind.OPEN; }
        Cell held = palette.get(symbol);
        return held == null ? Kind.OPEN : held.kind;
    }

    public Kind[][] turned(Rotation turn) {
        Kind[][] grid = new Kind[cellsDeep][cellsWide];
        for (int z = 0; z < cellsDeep; z++) {
            for (int x = 0; x < cellsWide; x++) { grid[z][x] = x < rows[z].length() ? kindOf(rows[z].charAt(x)) : Kind.OPEN; }
        }
        int quarter = turn == Rotation.CLOCKWISE_90 ? 1 : turn == Rotation.CLOCKWISE_180 ? 2 : turn == Rotation.COUNTERCLOCKWISE_90 ? 3 : 0;
        for (int i = 0; i < quarter; i++) { grid = clockwise(grid); }
        return grid;
    }

    public char[][] turnedMarks(Rotation turn) {
        char[][] marks = new char[cellsDeep][cellsWide];
        for (int z = 0; z < cellsDeep; z++) {
            for (int x = 0; x < cellsWide; x++) { marks[z][x] = x < rows[z].length() ? rows[z].charAt(x) : '.'; }
        }
        int quarter = turn == Rotation.CLOCKWISE_90 ? 1 : turn == Rotation.CLOCKWISE_180 ? 2 : turn == Rotation.COUNTERCLOCKWISE_90 ? 3 : 0;
        for (int i = 0; i < quarter; i++) { marks = clockwise(marks); }
        return marks;
    }

    private static Kind[][] clockwise(Kind[][] grid) {
        int deep = grid.length;
        int wide = grid[0].length;
        Kind[][] turned = new Kind[wide][deep];
        for (int z = 0; z < wide; z++) {
            for (int x = 0; x < deep; x++) { turned[z][x] = grid[deep - 1 - x][z]; }
        }
        return turned;
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
