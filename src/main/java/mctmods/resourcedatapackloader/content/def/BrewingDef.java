package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.Identifier;
import java.util.List;

public record BrewingDef(Identifier key, String from, String to, String ingredient, String input, String output, List<String> requires) {
    public boolean isMix() { return !from.isEmpty() && !to.isEmpty(); }
}
