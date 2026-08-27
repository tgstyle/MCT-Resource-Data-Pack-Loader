package mctmods.resourcedatapackloader.content.def;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import java.util.List;
import java.util.Map;

public final class PortalFrameDef {
    public static final char HOLE = '.';
    public static final char SKIP = ' ';
    public static final char REPEAT = '*';
    public static final String VERTICAL = "vertical";
    public static final String HORIZONTAL = "horizontal";
    public static final String BOTH = "both";
    public static final int LEAST_WIDE = 1;
    public static final int LEAST_TALL = 2;
    public final ResourceLocation registryName;
    public final String name;
    public final String axis;
    public final Map<Character, IBlockState> legend;
    public final List<String> rows;
    public final int maxWidth;
    public final int maxHeight;

    public PortalFrameDef(ResourceLocation registryName, String name, String axis, Map<Character, IBlockState> legend, List<String> rows, int maxWidth, int maxHeight) {
        this.registryName = registryName;
        this.name = name;
        this.axis = axis;
        this.legend = legend;
        this.rows = rows;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }

    public int leastTall() { return liesFlat() ? LEAST_WIDE : LEAST_TALL; }

    public boolean standsUp() { return !HORIZONTAL.equals(axis); }

    public boolean liesFlat() { return !VERTICAL.equals(axis); }

}
