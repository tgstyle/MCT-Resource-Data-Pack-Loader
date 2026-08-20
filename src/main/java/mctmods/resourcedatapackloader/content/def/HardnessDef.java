package mctmods.resourcedatapackloader.content.def;

import mctmods.resourcedatapackloader.content.worldgen.ContentField;

import net.minecraft.util.ResourceLocation;
import java.util.List;

public final class HardnessDef {
    public final ResourceLocation registryName;
    public final List<BlockMatchDef> blocks;
    public final List<BlockMatchDef> except;
    public final float leastMining;
    public final float mostMining;
    public final float leastBlast;
    public final float mostBlast;
    public final int buckets;
    public final int minHeight;
    public final int maxHeight;
    public final List<String> requires;
    public final ContentField field;

    public HardnessDef(ResourceLocation registryName, List<BlockMatchDef> blocks, List<BlockMatchDef> except, float leastMining, float mostMining, float leastBlast, float mostBlast, int buckets, int minHeight, int maxHeight, List<String> requires, ContentField field) {
        this.registryName = registryName;
        this.blocks = blocks;
        this.except = except;
        this.leastMining = leastMining;
        this.mostMining = mostMining;
        this.leastBlast = leastBlast;
        this.mostBlast = mostBlast;
        this.buckets = buckets;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.requires = requires;
        this.field = field;
    }

    public boolean rolls() { return buckets > 1 && (leastMining != mostMining || leastBlast != mostBlast); }

    public float mining(int bucket) { return at(leastMining, mostMining, bucket); }

    public float blast(int bucket) { return at(leastBlast, mostBlast, bucket); }

    private float at(float least, float most, int bucket) {
        if (buckets <= 1 || least == most) { return most; }
        float along = Math.max(0, Math.min(buckets - 1, bucket)) / (float) (buckets - 1);
        return most - along * (most - least);
    }
}
