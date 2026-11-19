package mctmods.resourcedatapackloader.common.util;

import mctmods.resourcedatapackloader.common.blocks.ContentStates;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockMatch;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public final class BlockMatches {
    private BlockMatches() {}

    @Nullable public static BlockMatch parse(String owner, String text, String part) {
        String name = text.trim();
        if (!name.startsWith("#")) {
            Block block = ContentStates.registered(name);
            if (block == null || block == Blocks.AIR) {
                ContentLog.LOGGER.error("Unknown block {} in {}, the entry is skipped", name, owner + " " + part);
                return null;
            }
            return BlockMatch.of(block);
        }
        ResourceLocation tag = name.length() > 1 ? ResourceLocation.tryParse(name.substring(1)) : null;
        if (tag != null) { return BlockMatch.group(tag); }
        ContentLog.LOGGER.error("{} gives '{}' with no block tag id in {}, ignoring it", owner, name, part);
        return null;
    }
}
