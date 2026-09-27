package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.Identifier;
import java.util.List;
import java.util.Map;

public record ExposureDef(Identifier key, int scanInterval, int range, boolean skipsCreative, int sourcesForNextLevel, String immunity, Map<Identifier, Integer> blocks, Map<Identifier, Integer> items, Map<Identifier, Integer> dimensions, List<ExposureLevelDef> levels) {
    public String name() { return key.getPath(); }
}
