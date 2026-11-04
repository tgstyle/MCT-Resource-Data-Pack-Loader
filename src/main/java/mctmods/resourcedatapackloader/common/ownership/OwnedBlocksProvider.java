package mctmods.resourcedatapackloader.common.ownership;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.common.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.common.util.worldgen.GenHeights;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class OwnedBlocksProvider implements ICapabilitySerializable<NBTTagCompound> {
    @CapabilityInject(OwnedBlocks.class) public static Capability<OwnedBlocks> CAPABILITY = null;
    private static final ResourceLocation KEY = ResourceDataPackLoader.id("owned_blocks");
    private final OwnedBlocks blocks = new OwnedBlocks();

    private static final class Storage implements Capability.IStorage<OwnedBlocks> {
        @Override public NBTBase writeNBT(Capability<OwnedBlocks> capability, OwnedBlocks instance, EnumFacing side) { return instance.write(); }

        @Override public void readNBT(Capability<OwnedBlocks> capability, OwnedBlocks instance, EnumFacing side, NBTBase nbt) {
            if (nbt instanceof NBTTagCompound) { instance.read((NBTTagCompound) nbt); }
        }
    }

    public static void register() { CapabilityManager.INSTANCE.register(OwnedBlocks.class, new Storage(), OwnedBlocks::new); }

    @Nullable public static OwnedBlocks of(@Nullable ICapabilityProvider holder) { return holder == null ? null : holder.getCapability(CAPABILITY, null); }

    @SubscribeEvent public static void onAttachChunk(AttachCapabilitiesEvent<Chunk> event) {
        if (GenHeights.rubic(event.getObject().getWorld())) { return; }
        event.addCapability(KEY, new OwnedBlocksProvider());
    }

    @SubscribeEvent public static void onAttachCube(AttachCapabilitiesEvent<ICube> event) { event.addCapability(KEY, new OwnedBlocksProvider()); }

    @Override public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) { return capability == CAPABILITY; }

    @Override @Nullable public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == CAPABILITY ? CAPABILITY.cast(blocks) : null;
    }

    @Override public NBTTagCompound serializeNBT() { return blocks.write(); }

    @Override public void deserializeNBT(NBTTagCompound nbt) { blocks.read(nbt); }
}
