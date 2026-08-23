package mctmods.resourcedatapackloader.mixin.reccomplex;

import mctmods.resourcedatapackloader.util.compat.interfaces.ISheddingData;
import mctmods.resourcedatapackloader.util.compat.interfaces.ISheddingEntry;

import ivorius.reccomplex.world.gen.feature.WorldStructureGenerationData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.storage.WorldSavedData;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Mixin(value = WorldStructureGenerationData.class, remap = false) public abstract class MixinWorldStructureGenerationData implements ISheddingData {
    @Shadow(remap = false) @Final protected Set<ChunkPos> checkedChunks;
    @Shadow(remap = false) @Final protected Map<UUID, WorldStructureGenerationData.Entry> entryMap;

    @Inject(method = "readFromNBT", at = @At("RETURN"), remap = true)
    private void rdpl$shedFinishedStructuresOnLoad(NBTTagCompound compound, CallbackInfo cbi) {
        for (WorldStructureGenerationData.Entry entry : entryMap.values()) { rdpl$shedIfComplete(entry); }
    }

    @Override public void rdpl$shedIfComplete(Object each) {
        if (!(each instanceof ISheddingEntry)) { return; }
        ISheddingEntry holder = (ISheddingEntry) each;
        if (!holder.rdpl$carriesInstanceData()) { return; }
        if (!checkedChunks.containsAll(((WorldStructureGenerationData.Entry) each).rasterize())) { return; }
        holder.rdpl$shedInstanceData();
        ((WorldSavedData) (Object) this).markDirty();
    }
}
