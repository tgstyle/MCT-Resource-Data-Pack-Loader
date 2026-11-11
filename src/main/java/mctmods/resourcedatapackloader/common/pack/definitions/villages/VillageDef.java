package mctmods.resourcedatapackloader.common.pack.definitions.villages;

import mctmods.resourcedatapackloader.common.pack.definitions.worlds.RespawnDef;

import net.minecraft.resources.Identifier;
import java.util.List;
import javax.annotation.Nullable;

public record VillageDef(Identifier key, String type, int weight, int leastCount, int mostCount, int width, int height, int depth, int apron,
                         List<String> crops, String edge, String soil, boolean water, int rowWidth, String structure, String ground, int integrity,
                         String lootTable, int villagers, String villagerEntity, int villagerX, int villagerY, int villagerZ, List<String> requires,
                         String orderSign, int orderX, int orderY, int orderZ, @Nullable RespawnDef respawn) {
    public static final String FARM = "farm";
    public static final String TEMPLATE = "template";

    public boolean template() { return TEMPLATE.equals(type); }
}
