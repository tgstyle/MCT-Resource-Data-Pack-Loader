package mctmods.resourcedatapackloader.content.worldgen;

import net.minecraft.resources.Identifier;

public final class WorldgenFiles {
    public static final String BIOME = "worldgen/biome";
    public static final String PLACED_FEATURE = "worldgen/placed_feature";
    public static final String STRUCTURE = "worldgen/structure";
    public static final String STRUCTURE_SET = "worldgen/structure_set";

    private WorldgenFiles() {}

    public static String file(String folder, String path) { return folder + "/" + path + ".json"; }

    public static Identifier id(String folder, Identifier name) { return Identifier.fromNamespaceAndPath(name.getNamespace(), file(folder, name.getPath())); }
}
