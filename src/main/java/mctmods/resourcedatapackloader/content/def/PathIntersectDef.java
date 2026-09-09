package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;
import java.util.List;
import java.util.Map;

public record PathIntersectDef(ResourceLocation key, String name, int weight, Map<Character, String> legend, List<String> mouth, List<String> corner) {
    public static final char ROAD = 'r';
    public static final char LINE = 'l';
    public static final char WALK = 's';
    public static final char KEEP = '.';
    public static final char CORE = 'c';

    public static boolean role(char mark) { return mark == ROAD || mark == LINE || mark == WALK || mark == KEEP || mark == CORE; }
}
