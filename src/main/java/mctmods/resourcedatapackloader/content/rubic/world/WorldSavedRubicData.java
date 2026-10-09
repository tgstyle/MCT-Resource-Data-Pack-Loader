package mctmods.resourcedatapackloader.content.rubic.world;

import mctmods.resourcedatapackloader.content.rubic.world.storage.StorageFormatProviderBase;
import mctmods.resourcedatapackloader.content.rubic.worldgen.VanillaCompatibilityGeneratorProviderBase;
import mctmods.resourcedatapackloader.util.compat.CompatHandler;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.WorldSavedData;
import javax.annotation.Nonnull;

public class WorldSavedRubicData extends WorldSavedData {
    public static final String MIN_HEIGHT = "minHeight";
    public static final String MAX_HEIGHT = "maxHeight";
    public static final String IS_RUBIC_WORLD = "isRubicWorld";
    public static final String GENERATOR_TYPE = "compatibilityGeneratorType";
    public static final String STORAGE_FORMAT = "storageFormat";
    public boolean isRubicWorld = false;
    public int minHeight = 0, maxHeight = CompatHandler.VANILLA_HEIGHT;
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
            storageFormat = StorageFormatProviderBase.DEFAULT;
        }
    }

    @Override public void readFromNBT(NBTTagCompound nbt) {
        minHeight = nbt.getInteger(MIN_HEIGHT) & ~0xF;
        maxHeight = nbt.getInteger(MAX_HEIGHT) & ~0xF;
        isRubicWorld = !nbt.hasKey(IS_RUBIC_WORLD) || nbt.getBoolean(IS_RUBIC_WORLD);
        if (nbt.hasKey(GENERATOR_TYPE)) { compatibilityGeneratorType = new ResourceLocation(nbt.getString(GENERATOR_TYPE)); }
        else { compatibilityGeneratorType = VanillaCompatibilityGeneratorProviderBase.DEFAULT; }
        if (nbt.hasKey(STORAGE_FORMAT)) { storageFormat = new ResourceLocation(nbt.getString(STORAGE_FORMAT)); }
        else { storageFormat = StorageFormatProviderBase.DEFAULT; }
    }

    @Override @Nonnull public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger(MIN_HEIGHT, minHeight);
        compound.setInteger(MAX_HEIGHT, maxHeight);
        compound.setBoolean(IS_RUBIC_WORLD, isRubicWorld);
        compound.setString(GENERATOR_TYPE, compatibilityGeneratorType.toString());
        compound.setString(STORAGE_FORMAT, storageFormat.toString());
        return compound;
    }
}
