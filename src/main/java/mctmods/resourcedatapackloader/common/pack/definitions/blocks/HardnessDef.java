package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import mctmods.resourcedatapackloader.common.worldgen.feature.shape.ContentField;

import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public record HardnessDef(Identifier key, List<BlockMatchDef> blocks, List<BlockMatchDef> except, float leastMining, float mostMining, float leastBlast, float mostBlast, int buckets, int minHeight, int maxHeight, List<String> requires, ContentField field,
                          boolean keeps, boolean adventure, List<String> tools, List<String> teams, List<String> players, List<String> entities, String advancement, String becomesOn, String becomes) {
    public boolean swaps() { return !becomesOn.isEmpty() && !becomes.isEmpty(); }

    public boolean rolls() { return buckets > 1 && (leastMining != mostMining || leastBlast != mostBlast); }

    public float mining(int bucket) { return at(leastMining, mostMining, bucket); }

    public float blast(int bucket) { return at(leastBlast, mostBlast, bucket); }

    private float at(float least, float most, int bucket) {
        if (buckets <= 1 || least == most) { return most; }
        float along = Mth.clamp(bucket, 0, buckets - 1) / (float) (buckets - 1);
        return most - along * (most - least);
    }
}
