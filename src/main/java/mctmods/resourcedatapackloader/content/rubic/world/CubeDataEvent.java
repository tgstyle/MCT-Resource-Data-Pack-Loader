package mctmods.resourcedatapackloader.content.rubic.world;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;

import net.minecraft.nbt.NBTTagCompound;

public class CubeDataEvent extends CubeEvent {
    private final NBTTagCompound data;

    public CubeDataEvent(ICube cube, NBTTagCompound data) {
        super(cube);
        this.data = data;
    }

    public NBTTagCompound getData() { return data; }

    public static class Load extends CubeDataEvent {
        public Load(ICube cube, NBTTagCompound data) { super(cube, data); }
    }

    public static class Save extends CubeDataEvent {
        public Save(ICube cube, NBTTagCompound data) { super(cube, data); }
    }
}
