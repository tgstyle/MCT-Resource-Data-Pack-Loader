package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.ResourceLocation;
import java.util.List;

public record VillageDef(ResourceLocation key, String type, int weight, int leastCount, int mostCount, int width, int height, int depth, int apron,
                         List<String> crops, String edge, String soil, boolean water, int rowWidth, String structure, String ground, int integrity,
                         String lootTable, int villagers, String villagerEntity, int villagerX, int villagerY, int villagerZ, List<String> requires) {
    public static final String FARM = "farm";
    public static final String TEMPLATE = "template";

    public boolean template() { return TEMPLATE.equals(type); }
}
