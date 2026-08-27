package mctmods.resourcedatapackloader.content.portal;

import mctmods.resourcedatapackloader.content.def.PortalFrameDef;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public final class PortalShapes {
    private PortalShapes() {}

    public static List<List<String>> spread(PortalFrameDef frame) {
        List<List<String>> shapes = new ArrayList<>();
        List<String> rows = frame.rows;
        int rowMarks = 0;
        for (String row : rows) {
            if (repeated(row)) { rowMarks++; }
        }
        int columnMarks = 0;
        for (String row : rows) {
            for (char held : row.toCharArray()) {
                if (held == PortalFrameDef.REPEAT) { columnMarks++; }
            }
        }
        if (rowMarks == 0 && columnMarks == 0) {
            if (roomy(rows, frame)) { shapes.add(rows); }
            return shapes;
        }
        int tallest = rowMarks > 0 ? frame.maxHeight : 1;
        int widest = columnMarks > 0 ? frame.maxWidth : 1;
        for (int tall = 0; tall <= tallest; tall++) {
            for (int wide = 0; wide <= widest; wide++) {
                List<String> grown = grow(rows, tall, wide);
                if (grown != null && roomy(grown, frame)) { shapes.add(grown); }
            }
        }
        return shapes;
    }

    public static boolean roomy(List<String> shape, PortalFrameDef frame) { return roomy(shape, frame, frame.leastTall()); }

    public static boolean roomy(List<String> shape, PortalFrameDef frame, boolean flat) {
        return roomy(shape, frame, flat ? PortalFrameDef.LEAST_WIDE : PortalFrameDef.LEAST_TALL);
    }

    private static boolean roomy(List<String> shape, PortalFrameDef frame, int leastTall) {
        int[] span = hole(shape);
        if (span == null) { return false; }
        return span[0] >= PortalFrameDef.LEAST_WIDE && span[1] >= leastTall
                && span[0] <= frame.maxWidth && span[1] <= frame.maxHeight;
    }

    @Nullable public static int[] hole(List<String> shape) {
        int leastRow = Integer.MAX_VALUE;
        int mostRow = Integer.MIN_VALUE;
        int leastColumn = Integer.MAX_VALUE;
        int mostColumn = Integer.MIN_VALUE;
        for (int row = 0; row < shape.size(); row++) {
            String line = shape.get(row);
            for (int column = 0; column < line.length(); column++) {
                if (line.charAt(column) != PortalFrameDef.HOLE) { continue; }
                leastRow = Math.min(leastRow, row);
                mostRow = Math.max(mostRow, row);
                leastColumn = Math.min(leastColumn, column);
                mostColumn = Math.max(mostColumn, column);
            }
        }
        if (mostRow < leastRow) { return null; }
        return new int[] { mostColumn - leastColumn + 1, mostRow - leastRow + 1 };
    }

    private static boolean repeated(String row) { return !row.isEmpty() && row.trim().chars().allMatch(held -> held == PortalFrameDef.REPEAT); }

    private static List<String> grow(List<String> rows, int tall, int wide) {
        List<String> out = new ArrayList<>();
        for (String row : rows) {
            if (repeated(row)) {
                if (out.isEmpty()) { return null; }
                String last = out.get(out.size() - 1);
                for (int again = 0; again < tall; again++) { out.add(last); }
                continue;
            }
            out.add(stretch(row, wide));
        }
        int width = out.isEmpty() ? 0 : out.get(0).length();
        for (String row : out) {
            if (row.length() != width) { return null; }
        }
        return out;
    }

    private static String stretch(String row, int wide) {
        StringBuilder built = new StringBuilder();
        for (int at = 0; at < row.length(); at++) {
            char held = row.charAt(at);
            if (held != PortalFrameDef.REPEAT) {
                built.append(held);
                continue;
            }
            char before = built.length() == 0 ? PortalFrameDef.SKIP : built.charAt(built.length() - 1);
            for (int again = 0; again < wide; again++) { built.append(before); }
        }
        return built.toString();
    }

    public static BlockPos at(BlockPos origin, int column, int row, int rows, boolean alongX, boolean flat, boolean mirrored) {
        int across = mirrored ? -column : column;
        if (flat) { return alongX ? origin.add(across, 0, row) : origin.add(row, 0, across); }
        int up = rows - 1 - row;
        return alongX ? origin.add(across, up, 0) : origin.add(0, up, across);
    }

    public static boolean matches(IBlockState found, IBlockState wanted) {
        if (found == wanted) { return true; }
        return wanted == wanted.getBlock().getDefaultState() && found.getBlock() == wanted.getBlock();
    }
}
