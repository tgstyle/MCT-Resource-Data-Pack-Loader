package mctmods.resourcedatapackloader.common.pack.definitions.blocks;

import javax.annotation.Nullable;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public record BlockMatch(@Nullable Block block, @Nullable TagKey<Block> tag) {
    public static BlockMatch of(Block block) { return new BlockMatch(block, null); }

    public static BlockMatch group(Identifier tag) { return new BlockMatch(null, TagKey.create(Registries.BLOCK, tag)); }

    public boolean group() { return tag != null; }

    public boolean matches(Block candidate) { return tag == null ? block == candidate : candidate.defaultBlockState().is(tag); }
}
