package mctmods.resourcedatapackloader.common.ownership;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class OwnedBlocksProvider implements ICapabilitySerializable<CompoundTag> {
    public static final Capability<OwnedBlocks> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    private static final ResourceLocation KEY = ResourceDataPackLoader.id("owned_blocks");
    private final OwnedBlocks blocks = new OwnedBlocks();
    private final LazyOptional<OwnedBlocks> self = LazyOptional.of(() -> blocks);

    public static void register(RegisterCapabilitiesEvent event) { event.register(OwnedBlocks.class); }

    public static void onAttach(AttachCapabilitiesEvent<LevelChunk> event) { event.addCapability(KEY, new OwnedBlocksProvider()); }

    @Nullable public static OwnedBlocks of(LevelChunk chunk) { return chunk.getCapability(CAPABILITY).resolve().orElse(null); }

    @Override @Nonnull public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) { return CAPABILITY.orEmpty(capability, self); }

    @Override public CompoundTag serializeNBT() { return blocks.write(); }

    @Override public void deserializeNBT(CompoundTag nbt) { blocks.read(nbt); }
}
