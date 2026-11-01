package mctmods.resourcedatapackloader.mixin.integration.reccomplex;

import mctmods.resourcedatapackloader.core.integration.reccomplex.ISheddingEntry;

import ivorius.reccomplex.world.gen.feature.WorldStructureGenerationData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.nbt.NBTBase;

@Mixin(value = WorldStructureGenerationData.StructureEntry.class, remap = false) public abstract class MixinStructureEntry implements ISheddingEntry {
    @Shadow(remap = false) protected NBTBase instanceData;

    @Override public boolean rdpl$carriesInstanceData() { return instanceData != null; }

    @Override public void rdpl$shedInstanceData() { instanceData = null; }
}
