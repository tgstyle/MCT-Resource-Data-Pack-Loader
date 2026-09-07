package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;
import java.util.List;
import java.util.Map;

public record PortalFrameDef(ResourceLocation key, String name, String axis, Map<Character, BlockMatchDef> legend, List<String> rows, int maxWidth, int maxHeight) {
    public static final char HOLE = '.';
    public static final char SKIP = ' ';
    public static final char REPEAT = '*';
    public static final String VERTICAL = "vertical";
    public static final String HORIZONTAL = "horizontal";
    public static final String BOTH = "both";
    public static final int LEAST_WIDE = 1;
    public static final int LEAST_TALL = 2;

    public int leastTall() { return liesFlat() ? LEAST_WIDE : LEAST_TALL; }

    public boolean standsUp() { return !HORIZONTAL.equals(axis); }

    public boolean liesFlat() { return !VERTICAL.equals(axis); }
}
