package mctmods.resourcedatapackloader.content.def;

import mctmods.resourcedatapackloader.util.Json;

import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;

public record SkyStoneDef(String stone, float islands, float thickness) {
    public static SkyStoneDef read(JsonObject json, Object owner) {
        return new SkyStoneDef(GsonHelper.getAsString(json, "skyStone", "").trim(), Json.ranged(json, "skyIslands", Float.NaN, -1.0F, 1.0F, owner), Json.ranged(json, "skyThickness", Float.NaN, 0.0F, 8.0F, owner));
    }

    public boolean shapes() { return !Float.isNaN(islands) || !Float.isNaN(thickness); }
}
