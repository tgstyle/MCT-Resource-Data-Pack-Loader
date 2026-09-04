package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;
import java.util.List;

public record BrewingDef(ResourceLocation key, String from, String to, String ingredient, String input, String output, List<String> requires) {
    public boolean isMix() { return !from.isEmpty() && !to.isEmpty(); }
}
