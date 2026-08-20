package mctmods.resourcedatapackloader.content.rubic.world;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;

import net.minecraftforge.event.world.WorldEvent;

public class CubeEvent extends WorldEvent {
    private final ICube chunk;

    public CubeEvent(ICube cube) {
        super(cube.getWorld());
        this.chunk = cube;
    }

    public ICube getCube() { return chunk; }

    public static class Load extends CubeEvent {
        public Load(ICube cube) { super(cube); }
    }

    public static class Unload extends CubeEvent {
        public Unload(ICube cube) { super(cube); }
    }
}
