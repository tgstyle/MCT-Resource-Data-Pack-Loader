package mctmods.resourcedatapackloader.common.rubic.regionlib.impl;

import java.nio.file.Path;

public final class RegionNames {
    public static final String COLUMNS = ".2rdr";
    public static final String CUBES = ".3rdr";
    public static final String SPILL = ".ext";
    public static final String COLUMN_FOLDER = "region2d";
    public static final String CUBE_FOLDER = "region3d";
    public static final String CHUNKS = ".mca";
    public static final String CHUNK_PREFIX = "r.";

    public static final String CHUNK_FOLDER = "region";

    private RegionNames() {}

    public static Path chunkFolder(Path save) { return save.resolve(CHUNK_FOLDER); }

    public static String chunkRegion(int regionX, int regionZ) {
        return CHUNK_PREFIX + regionX + "." + regionZ + CHUNKS;
    }
}
