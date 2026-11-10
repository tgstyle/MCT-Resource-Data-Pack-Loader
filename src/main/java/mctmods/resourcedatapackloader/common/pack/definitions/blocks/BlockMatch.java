package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import javax.annotation.Nullable;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public final class BlockMatch {
    @Nullable private final Block block;
    @Nullable private final TagKey<Block> tag;

    private BlockMatch(@Nullable Block block, @Nullable TagKey<Block> tag) {
        this.block = block;
        this.tag = tag;
    }

    public static BlockMatch of(Block block) { return new BlockMatch(block, null); }

    public static BlockMatch group(ResourceLocation tag) { return new BlockMatch(null, TagKey.create(Registries.BLOCK, tag)); }

    public boolean group() { return this.tag != null; }

    public boolean matches(Block candidate) { return this.tag == null ? this.block == candidate : candidate.defaultBlockState().is(this.tag); }

    public static Block blockOf(Item item) { return item instanceof BlockItem held ? held.getBlock() : Blocks.AIR; }
}
