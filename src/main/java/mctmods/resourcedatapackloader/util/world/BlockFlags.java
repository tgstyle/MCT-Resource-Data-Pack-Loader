package mctmods.resourcedatapackloader.util.world;

import net.minecraft.world.level.block.Block;

public final class BlockFlags {
    public static final int UPDATE_CLIENTS_KNOWN_SHAPE = Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE;

    private BlockFlags() {}
}
