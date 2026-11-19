package mctmods.resourcedatapackloader.common.util;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockMatch;
import mctmods.resourcedatapackloader.common.worldgen.surface.ContentWorldShape;
import mctmods.resourcedatapackloader.core.config.Settings;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import javax.annotation.Nullable;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class BlockMatches {
    private BlockMatches() {}

    @Nullable public static BlockMatch parse(String owner, String text, String part) {
        String name = text.trim();
        if (!name.startsWith("#")) {
            Block block = ContentWorldShape.block(name, owner + " " + part);
            return block == null ? null : BlockMatch.of(block);
        }
        ResourceLocation tag = name.length() > 1 ? ResourceLocation.tryParse(Settings.lower(name.substring(1))) : null;
        if (tag != null) { return BlockMatch.group(TagKey.create(Registries.BLOCK, tag)); }
        ContentLog.LOGGER.error("{} gives '{}' with no valid block tag name in {}, ignoring it", owner, name, part);
        return null;
    }
}
