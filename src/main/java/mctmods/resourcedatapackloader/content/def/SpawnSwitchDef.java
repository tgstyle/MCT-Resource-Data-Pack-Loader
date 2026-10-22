package mctmods.resourcedatapackloader.content.def;

import net.minecraft.resources.Identifier;
import java.util.LinkedHashMap;
import java.util.Map;

public final class SpawnSwitchDef {
    public static final SpawnSwitchDef NONE = new SpawnSwitchDef("");
    public final String owner;
    public final float[] rates = {-1.0F, -1.0F, -1.0F, -1.0F};
    public final int[] caps = {-1, -1, -1, -1};
    public final Map<Identifier, Float> weights = new LinkedHashMap<>();

    public SpawnSwitchDef(String owner) { this.owner = owner; }
}
