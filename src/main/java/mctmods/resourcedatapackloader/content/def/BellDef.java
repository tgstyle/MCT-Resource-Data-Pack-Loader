package mctmods.resourcedatapackloader.content.def;

public final class BellDef {
    public static final BellDef PLAIN = new BellDef(true, "minecraft:block.note.bell", "minecraft:block.note.chime");
    public final boolean swing;
    public final String sound;
    public final String resonateSound;

    public BellDef(boolean swing, String sound, String resonateSound) {
        this.swing = swing;
        this.sound = sound;
        this.resonateSound = resonateSound;
    }
}
