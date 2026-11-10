package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import javax.annotation.Nullable;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class BlockMatch {
    @Nullable private final Block block;
    @Nullable private final TagKey<Block> tag;

    private BlockMatch(@Nullable Block block, @Nullable TagKey<Block> tag) {
        this.block = block;
        this.tag = tag;
    }

    public static BlockMatch of(Block block) { return new BlockMatch(block, null); }

    public static BlockMatch group(TagKey<Block> tag) { return new BlockMatch(null, tag); }

    public boolean group() { return this.tag != null; }

    public boolean matches(Block candidate) { return this.tag == null ? this.block == candidate : candidate.defaultBlockState().is(this.tag); }
}
