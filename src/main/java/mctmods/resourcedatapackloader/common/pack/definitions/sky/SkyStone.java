package mctmods.resourcedatapackloader.common.pack.definitions.sky;

import mctmods.resourcedatapackloader.common.rubic.worldgen.generator.DeepGeneration;
import mctmods.resourcedatapackloader.core.util.json.Json;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

public final class SkyStone {
    public static final String STONE_KEY = "skyStone";
    public static final String ISLANDS_KEY = "skyIslands";
    public static final String THICKNESS_KEY = "skyThickness";
    public static final float LEAST_ISLANDS = -1.0F;
    public static final float MOST_ISLANDS = 1.0F;
    public static final float LEAST_THICKNESS = 0.0F;
    public static final float MOST_THICKNESS = 8.0F;
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

    public static SkyStone read(JsonObject json, ResourceLocation key) { return new SkyStone(Json.getString(json, STONE_KEY, ""), Json.bounded(json, ISLANDS_KEY, LEAST_ISLANDS, MOST_ISLANDS, key), Json.bounded(json, THICKNESS_KEY, LEAST_THICKNESS, MOST_THICKNESS, key), key); }

    public static boolean islands(float value) { return value >= LEAST_ISLANDS && value <= MOST_ISLANDS; }

    public static boolean thickness(float value) { return value >= LEAST_THICKNESS && value <= MOST_THICKNESS; }

    public boolean shapes() { return !stone.isEmpty() || !Float.isNaN(islands) || !Float.isNaN(thickness); }

    @Nullable public IBlockState state() {
        if (!resolved) {
            resolved = true;
            state = DeepGeneration.parseState(stone, owner + " skyStone");
        }
        return state;
    }
}
