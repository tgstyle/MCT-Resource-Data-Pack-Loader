package mctmods.resourcedatapackloader.common.util;

import mctmods.resourcedatapackloader.common.blocks.ContentStates;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockMatch;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import javax.annotation.Nullable;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

public final class BlockMatches {
    private BlockMatches() {}

    @Nullable public static BlockMatch parse(String owner, String text, String part) {
        String name = text.trim();
        if (!name.startsWith("#")) {
            Block block = ContentStates.registered(name);
            if (block == null) { ContentLog.LOGGER.error("Unknown block {} in {} {}, the entry is skipped", name, owner, part); }
            return block == null ? null : BlockMatch.of(block);
        }
        Identifier tag = name.length() > 1 ? Identifier.tryParse(name.substring(1)) : null;
        if (tag == null) { ContentLog.LOGGER.error("{} gives '{}' in {}, which is not a block tag id, ignoring it", owner, name, part); }
        return tag == null ? null : BlockMatch.group(tag);
    }
}
