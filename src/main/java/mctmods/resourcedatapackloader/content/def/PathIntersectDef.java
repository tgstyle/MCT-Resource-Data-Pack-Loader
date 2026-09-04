package mctmods.resourcedatapackloader.content.def;

import net.minecraft.block.state.IBlockState;
import java.util.Map;

public class PathIntersectDef {
    public final int weight;
    public final Map<Character, IBlockState> legend;
    public final String[] mouth;
    public final String[] corner;

    public PathIntersectDef(int weight, Map<Character, IBlockState> legend, String[] mouth, String[] corner) {
        this.weight = weight;
        this.legend = legend;
        this.mouth = mouth;
        this.corner = corner;
    }
}
