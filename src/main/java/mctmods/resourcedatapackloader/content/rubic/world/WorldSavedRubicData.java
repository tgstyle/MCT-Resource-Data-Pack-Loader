package mctmods.resourcedatapackloader.content.rubic.world;

import mctmods.resourcedatapackloader.content.rubic.world.storage.StorageFormatProviderBase;
import mctmods.resourcedatapackloader.content.rubic.worldgen.VanillaCompatibilityGeneratorProviderBase;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.WorldSavedData;
import javax.annotation.Nonnull;

public class WorldSavedRubicData extends WorldSavedData {
    public boolean isRubicWorld = false;
    public int minHeight = 0, maxHeight = 256;
    public ResourceLocation compatibilityGeneratorType = VanillaCompatibilityGeneratorProviderBase.DEFAULT;
    public ResourceLocation storageFormat = StorageFormatProviderBase.DEFAULT;

    public WorldSavedRubicData(String name) { super(name); }

    public WorldSavedRubicData(String name, boolean isRubic, int minHeight, int maxHeight) {
        this(name);
        if (isRubic) {
            this.minHeight = minHeight;
            this.maxHeight = maxHeight;
            isRubicWorld = true;
            compatibilityGeneratorType = VanillaCompatibilityGeneratorProviderBase.DEFAULT;
            storageFormat = StorageFormatProviderBase.defaultStorageFormatProviderName("");
        }
    }

    @Override public void readFromNBT(NBTTagCompound nbt) {
        minHeight = nbt.getInteger("minHeight") & ~0xF;
        maxHeight = nbt.getInteger("maxHeight") & ~0xF;
        isRubicWorld = !nbt.hasKey("isRubicWorld") || nbt.getBoolean("isRubicWorld");
        if(nbt.hasKey("compatibilityGeneratorType"))
            compatibilityGeneratorType = new ResourceLocation(nbt.getString("compatibilityGeneratorType"));
        else
            compatibilityGeneratorType = VanillaCompatibilityGeneratorProviderBase.DEFAULT;
        if(nbt.hasKey("storageFormat"))
            storageFormat = new ResourceLocation(nbt.getString("storageFormat"));
        else
            storageFormat = StorageFormatProviderBase.DEFAULT;
    }

    @Override @Nonnull public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("minHeight", minHeight);
        compound.setInteger("maxHeight", maxHeight);
        compound.setBoolean("isRubicWorld", isRubicWorld);
        compound.setString("compatibilityGeneratorType", compatibilityGeneratorType.toString());
        compound.setString("storageFormat", storageFormat.toString());
        return compound;
    }
}
