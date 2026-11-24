package mctmods.resourcedatapackloader.common.util;

import mctmods.resourcedatapackloader.common.blocks.ContentStates;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockMatch;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.IdNames;

import javax.annotation.Nullable;
import net.minecraft.block.Block;

public final class BlockMatches {
    private BlockMatches() {}

    @Nullable public static BlockMatch parse(String owner, String text, String part) {
        String name = text.trim();
        if (!IdNames.isTag(name)) {
            Block block = ContentStates.block(name, owner + " " + part);
            return block == null ? null : BlockMatch.of(block);
        }
        if (name.length() > 1) { return BlockMatch.group(name.substring(1)); }
        ContentLog.LOGGER.error("{} gives a '#' with no ore dictionary name in {}, ignoring it", owner, part);
        return null;
    }
}
