package mctmods.resourcedatapackloader.mixin.rdpl.common;

import mctmods.resourcedatapackloader.util.compat.IPackingStructureData;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.gen.structure.MapGenStructureData;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Mixin(MapGenStructureData.class) public abstract class MixinMapGenStructureData implements IPackingStructureData {
    @Shadow private NBTTagCompound tagCompound;
    @Unique private final List<byte[]> rdpl$packed = new ArrayList<>();

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
        }
        if (far == null) { return; }
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            CompressedStreamTools.writeCompressed(far, out);
            rdpl$packed.add(out.toByteArray());
        }
        catch (IOException impossible) { throw new RuntimeException("Packing structure starts away failed", impossible); }
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
