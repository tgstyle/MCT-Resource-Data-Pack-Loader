package mctmods.resourcedatapackloader.content.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.attachment.AttachmentType;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class ContentChunkTokens {
    private static final Codec<Set<String>> CODEC = Codec.STRING.listOf().xmap(held -> Set.copyOf(new LinkedHashSet<>(held)), List::copyOf);
    private static final AttachmentType<Set<String>> TOKENS = AttachmentType.<Set<String>>builder(() -> Set.<String>of()).serialize(CODEC, held -> !held.isEmpty()).build();

    private ContentChunkTokens() {}

    public static AttachmentType<Set<String>> type() { return TOKENS; }

    public static Set<String> get(LevelChunk chunk) { return chunk.getData(TOKENS); }

    public static void put(LevelChunk chunk, Set<String> tokens) {
        chunk.setData(TOKENS, Set.copyOf(tokens));
        chunk.setUnsaved(true);
    }
}
