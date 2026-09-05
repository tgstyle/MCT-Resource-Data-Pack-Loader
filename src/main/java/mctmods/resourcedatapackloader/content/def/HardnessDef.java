package mctmods.resourcedatapackloader.content.def;

import mctmods.resourcedatapackloader.content.worldgen.ContentField;

import net.minecraft.resources.ResourceLocation;
import java.util.List;

public record HardnessDef(ResourceLocation key, List<BlockMatchDef> blocks, List<BlockMatchDef> except, float leastMining, float mostMining, float leastBlast, float mostBlast, int buckets, int minHeight, int maxHeight, List<String> requires, ContentField field) {
    public boolean rolls() { return buckets > 1 && (leastMining != mostMining || leastBlast != mostBlast); }

    public float mining(int bucket) { return at(leastMining, mostMining, bucket); }

    public float blast(int bucket) { return at(leastBlast, mostBlast, bucket); }

    private float at(float least, float most, int bucket) {
        if (buckets <= 1 || least == most) { return most; }
        float along = Math.clamp(bucket, 0, buckets - 1) / (float) (buckets - 1);
        return most - along * (most - least);
    }
}
