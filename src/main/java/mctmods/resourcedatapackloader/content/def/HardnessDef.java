package mctmods.resourcedatapackloader.content.def;

import mctmods.resourcedatapackloader.content.worldgen.ContentField;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
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
    public final boolean keeps;
    public final boolean adventure;
    public final List<String> tools;
    public final List<String> teams;
    public final List<String> players;
    public final List<String> entities;
    public final String advancement;
    public final String becomesOn;
    public final String becomes;

    public HardnessDef(ResourceLocation registryName, List<BlockMatchDef> blocks, List<BlockMatchDef> except, float leastMining, float mostMining, float leastBlast, float mostBlast, int buckets, int minHeight, int maxHeight, List<String> requires, ContentField field, boolean keeps, boolean adventure, List<String> tools, List<String> teams, List<String> players, List<String> entities, String advancement, String becomesOn, String becomes) {
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
        this.keeps = keeps;
        this.adventure = adventure;
        this.tools = tools;
        this.teams = teams;
        this.players = players;
        this.entities = entities;
        this.advancement = advancement;
        this.becomesOn = becomesOn;
        this.becomes = becomes;
    }

    public boolean swaps() { return !becomesOn.isEmpty() && !becomes.isEmpty(); }

    public boolean rolls() { return buckets > 1 && (leastMining != mostMining || leastBlast != mostBlast); }

    public float mining(int bucket) { return at(leastMining, mostMining, bucket); }

    public float blast(int bucket) { return at(leastBlast, mostBlast, bucket); }

    private float at(float least, float most, int bucket) {
        if (buckets <= 1 || least == most) { return most; }
        float along = MathHelper.clamp(bucket, 0, buckets - 1) / (float) (buckets - 1);
        return most - along * (most - least);
    }
}
