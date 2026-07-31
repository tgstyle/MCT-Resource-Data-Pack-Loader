package mctmods.resourcedatapackloader.content.def;

import net.minecraft.util.ResourceLocation;
import java.util.List;

public final class BrewingDef {
    public final ResourceLocation key;
    public final String from;
    public final String to;
    public final String ingredient;
    public final String input;
    public final String output;
    public final List<String> requires;

    public BrewingDef(ResourceLocation key, String from, String to, String ingredient, String input, String output, List<String> requires) {
        this.key = key;
        this.from = from;
        this.to = to;
        this.ingredient = ingredient;
        this.input = input;
        this.output = output;
        this.requires = requires;
    }

    public boolean isMix() { return !from.isEmpty() && !to.isEmpty(); }
}
