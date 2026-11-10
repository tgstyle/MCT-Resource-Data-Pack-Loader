package mctmods.resourcedatapackloader.common.pack.definitions.sky;

import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public final class SnowRuleDef {
    public static final int DEFAULT = 0;
    public static final int BIOME = 1;
    public static final int ANY = 2;
    public static final int NEVER = 3;
    public static final List<String> MODES = Collections.unmodifiableList(Arrays.asList("default", "biome", "any", "never"));
    public static final float MELT_CHANCE = 0.0625F;
    public static final Set<Block> MELT_BLOCKS = meltDefaults();
    public static final SnowRuleDef NONE = new SnowRuleDef(DEFAULT, DEFAULT, false, false, MELT_CHANCE, MELT_BLOCKS);
    public final int place;
    public final int freeze;
    public final boolean whenDry;
    public final boolean melt;
    public final float meltChance;
    public final Set<Block> meltBlocks;

    public SnowRuleDef(int place, int freeze, boolean whenDry, boolean melt, float meltChance, Set<Block> meltBlocks) {
        this.place = place;
        this.freeze = freeze;
        this.whenDry = whenDry;
        this.melt = melt;
        this.meltChance = meltChance;
        this.meltBlocks = meltBlocks;
    }

    private static Set<Block> meltDefaults() {
        Set<Block> blocks = Collections.newSetFromMap(new IdentityHashMap<>());
        blocks.add(Blocks.SNOW_LAYER);
        blocks.add(Blocks.ICE);
        return Collections.unmodifiableSet(blocks);
    }
}
