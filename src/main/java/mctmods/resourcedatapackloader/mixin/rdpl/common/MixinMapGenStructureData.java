package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.util.compat.interfaces.IPackingStructureData;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.structure.MapGenStructureData;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

@Mixin(MapGenStructureData.class) public abstract class MixinMapGenStructureData implements IPackingStructureData {
    @Shadow private NBTTagCompound tagCompound;
    @Unique private final List<byte[]> rdpl$packed = new ArrayList<>();
    @Unique private final Map<Long, Integer> rdpl$packedAt = new HashMap<>();
    @Unique private final LongOpenHashSet rdpl$known = new LongOpenHashSet();

    @Inject(method = "readFromNBT", at = @At("TAIL")) private void rdpl$learnStarts(NBTTagCompound nbt, CallbackInfo ci) {
        for (String key : tagCompound.getKeySet()) {
            long at = rdpl$coords(key);
            if (at != Long.MIN_VALUE) { rdpl$known.add(at); }
        }
    }

    @Inject(method = "writeInstance", at = @At("TAIL")) private void rdpl$learnStart(NBTTagCompound tagCompoundIn, int chunkX, int chunkZ, CallbackInfo ci) { rdpl$known.add(ChunkPos.asLong(chunkX, chunkZ)); }

    @Unique private static long rdpl$coords(String key) {
        int comma = key.indexOf(',');
        if (key.length() < 5 || key.charAt(0) != '[' || comma < 0 || !key.endsWith("]")) { return Long.MIN_VALUE; }
        try { return ChunkPos.asLong(Integer.parseInt(key.substring(1, comma)), Integer.parseInt(key.substring(comma + 1, key.length() - 1))); }
        catch (NumberFormatException notCoords) { return Long.MIN_VALUE; }
    }

    @Override public int rdpl$startCount() { return rdpl$known.size(); }

    @Override public boolean rdpl$startWithin(int chunkX, int chunkZ, int chunks) {
        long reach = (long) chunks * chunks;
        for (long at : rdpl$known) {
            long dx = (int) at - chunkX;
            long dz = (int) (at >> 32) - chunkZ;
            if (dx * dx + dz * dz < reach) { return true; }
        }
        return false;
    }

    @Override public void rdpl$packFarStarts(int chunkX, int chunkZ, int keep) {
        NBTTagCompound far = null;
        for (String key : new ArrayList<>(tagCompound.getKeySet())) {
            int comma = key.indexOf(',');
            if (key.length() < 5 || key.charAt(0) != '[' || comma < 0 || !key.endsWith("]")) { continue; }
            int x;
            int z;
            try {
                x = Integer.parseInt(key.substring(1, comma));
                z = Integer.parseInt(key.substring(comma + 1, key.length() - 1));
            }
            catch (NumberFormatException notCoords) { continue; }
            if (Math.abs(x - chunkX) <= keep && Math.abs(z - chunkZ) <= keep) { continue; }
            if (far == null) { far = new NBTTagCompound(); }
            far.setTag(key, tagCompound.getTag(key));
            tagCompound.removeTag(key);
            rdpl$packedAt.put(ChunkPos.asLong(x, z), rdpl$packed.size());
        }
        if (far == null) { return; }
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            CompressedStreamTools.writeCompressed(far, out);
            rdpl$packed.add(out.toByteArray());
        }
        catch (IOException impossible) { throw new RuntimeException("Packing structure starts away failed", impossible); }
    }

    @Override @Nullable public NBTTagCompound rdpl$recall(int chunkX, int chunkZ) {
        Integer batch = rdpl$packedAt.get(ChunkPos.asLong(chunkX, chunkZ));
        if (batch == null) { return null; }
        try {
            NBTTagCompound entries = CompressedStreamTools.readCompressed(new ByteArrayInputStream(rdpl$packed.get(batch)));
            String key = "[" + chunkX + "," + chunkZ + "]";
            return entries.hasKey(key, 10) ? entries.getCompoundTag(key) : null;
        }
        catch (IOException impossible) { throw new RuntimeException("Unpacking a structure start that was packed away failed", impossible); }
    }

    @Inject(method = "writeToNBT", at = @At("HEAD"), cancellable = true)
    private void rdpl$writeThePackedToo(NBTTagCompound compound, CallbackInfoReturnable<NBTTagCompound> cir) {
        if (rdpl$packed.isEmpty()) { return; }
        NBTTagCompound whole = new NBTTagCompound();
        try {
            for (byte[] batch : rdpl$packed) {
                NBTTagCompound entries = CompressedStreamTools.readCompressed(new ByteArrayInputStream(batch));
                for (String key : entries.getKeySet()) { whole.setTag(key, entries.getTag(key)); }
            }
        }
        catch (IOException impossible) { throw new RuntimeException("Unpacking structure starts for the save failed", impossible); }
        for (String key : tagCompound.getKeySet()) { whole.setTag(key, tagCompound.getTag(key)); }
        compound.setTag("Features", whole);
        cir.setReturnValue(compound);
    }
}
