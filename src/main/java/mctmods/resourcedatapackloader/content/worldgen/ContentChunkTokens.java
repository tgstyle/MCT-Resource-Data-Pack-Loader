package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentChunkTokens {
    public static final Capability<Held> TOKENS = CapabilityManager.get(new CapabilityToken<>() {});
    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(ResourceDataPackLoader.MOD_ID, ContentWorldgen.RETROGEN_TOKENS);
    private static final String TAG = "tokens";

    private ContentChunkTokens() {}

    public static void register(RegisterCapabilitiesEvent event) { event.register(Held.class); }

    public static void onAttach(AttachCapabilitiesEvent<LevelChunk> event) { event.addCapability(ID, new Held()); }

    public static Set<String> get(LevelChunk chunk) { return chunk.getCapability(TOKENS).map(Held::held).orElse(Set.of()); }

    public static void put(LevelChunk chunk, Set<String> tokens) {
        chunk.getCapability(TOKENS).ifPresent(held -> held.keep(tokens));
        chunk.setUnsaved(true);
    }

    public static final class Held implements ICapabilitySerializable<CompoundTag> {
        private final LazyOptional<Held> self = LazyOptional.of(() -> this);
        private Set<String> held = Set.of();

        public Set<String> held() { return held; }

        private void keep(Set<String> tokens) { held = Set.copyOf(tokens); }

        @Override @Nonnull public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) { return TOKENS.orEmpty(capability, self); }

        @Override public CompoundTag serializeNBT() {
            CompoundTag data = new CompoundTag();
            if (held.isEmpty()) { return data; }
            ListTag list = new ListTag();
            for (String token : held) { list.add(StringTag.valueOf(token)); }
            data.put(TAG, list);
            return data;
        }

        @Override public void deserializeNBT(CompoundTag data) {
            Set<String> found = new LinkedHashSet<>();
            for (Tag entry : data.getList(TAG, Tag.TAG_STRING)) { found.add(entry.getAsString()); }
            held = Set.copyOf(found);
        }
    }
}
