package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.attachment.AttachmentType;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class ContentChunkTokens {
    private static final String KEY = ResourceDataPackLoader.MOD_ID + ":" + ContentWorldgen.RETROGEN_TOKENS;
    private static final String FIELD = "tokens";
    private static final Codec<Set<String>> CODEC = Codec.STRING.listOf().xmap(held -> Set.copyOf(new LinkedHashSet<>(held)), List::copyOf);
    private static final AttachmentType<Set<String>> TOKENS = AttachmentType.<Set<String>>builder(() -> Set.of()).serialize(CODEC.fieldOf(FIELD), held -> !held.isEmpty()).build();

    private ContentChunkTokens() {}

    public static AttachmentType<Set<String>> type() { return TOKENS; }

    public static Set<String> get(LevelChunk chunk) { return chunk.getData(TOKENS); }

    public static void put(LevelChunk chunk, Set<String> tokens) {
        chunk.setData(TOKENS, Set.copyOf(tokens));
        chunk.markUnsaved();
    }

    public static void upgrade(CompoundTag attachments) {
        if (attachments.get(KEY) instanceof ListTag old) {
            CompoundTag held = new CompoundTag();
            held.put(FIELD, old);
            attachments.put(KEY, held);
        }
    }
}
