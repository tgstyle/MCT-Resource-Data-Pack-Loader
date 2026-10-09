package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.pack.PackManager;

import net.minecraft.resources.ResourceLocation;

public final class WorldgenFiles {
    public static final String BIOME = "worldgen/biome";
    public static final String PLACED_FEATURE = "worldgen/placed_feature";
    public static final String STRUCTURE = "worldgen/structure";
    public static final String STRUCTURE_SET = "worldgen/structure_set";

    private WorldgenFiles() {}

    public static String file(String folder, String path) { return folder + "/" + path + PackManager.JSON_SUFFIX; }

    public static ResourceLocation id(String folder, ResourceLocation name) { return ResourceLocation.fromNamespaceAndPath(name.getNamespace(), file(folder, name.getPath())); }
}
