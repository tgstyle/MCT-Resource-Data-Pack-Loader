package mctmods.resourcedatapackloader.content.def;

public record BellDef(boolean swing, String sound, String resonateSound) {
    public static final BellDef PLAIN = new BellDef(true, "minecraft:block.note_block.bell", "minecraft:block.note_block.chime");
}
