package mctmods.resourcedatapackloader.content.def;

import mctmods.resourcedatapackloader.content.rubic.worldgen.generator.DeepGeneration;
import mctmods.resourcedatapackloader.util.Json;

import com.google.gson.JsonObject;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import javax.annotation.Nullable;

public final class SkyStone {
    public final String stone;
    public final float islands;
    public final float thickness;
    private final ResourceLocation owner;
    private boolean resolved;
    @Nullable private IBlockState state;

    private SkyStone(String stone, float islands, float thickness, ResourceLocation owner) {
        this.stone = stone;
        this.islands = islands;
        this.thickness = thickness;
        this.owner = owner;
    }

    public static SkyStone read(JsonObject json, ResourceLocation key) {
        return new SkyStone(Json.getString(json, "skyStone", ""), Json.bounded(json, "skyIslands", -1.0F, 1.0F, key), Json.bounded(json, "skyThickness", 0.0F, 8.0F, key), key);
    }

    public boolean shapes() { return !stone.isEmpty() || !Float.isNaN(islands) || !Float.isNaN(thickness); }

    @Nullable public IBlockState state() {
        if (!resolved) {
            resolved = true;
            state = DeepGeneration.parseState(stone, owner + " skyStone");
        }
        return state;
    }
}
