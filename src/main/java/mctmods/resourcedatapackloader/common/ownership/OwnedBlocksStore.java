package mctmods.resourcedatapackloader.common.ownership;

import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.attachment.AttachmentType;

public final class OwnedBlocksStore {
    private static final AttachmentType<OwnedBlocks> TYPE = AttachmentType.builder(OwnedBlocks::new).serialize(OwnedBlocks.CODEC, blocks -> !blocks.entries().isEmpty()).build();

    private OwnedBlocksStore() {}

    public static AttachmentType<OwnedBlocks> type() { return TYPE; }

    public static OwnedBlocks of(LevelChunk chunk) { return chunk.getData(TYPE); }
}
