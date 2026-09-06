package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.Config;

public final class ContentTerrain {
    public static final String HARDCORE = "hardcore";

    private ContentTerrain() {}

    private static String text(String key, String fallback) {
        if (ContentControl.off(ContentControl.TERRAIN)) { return ""; }
        return ContentControl.text(ContentControl.TERRAIN, key, fallback).trim();
    }

    public static String worldSeed() { return text("worldSeed", Config.worldgen.worldSeed()); }

    public static String worldName() { return text("worldName", Config.worldgen.worldName()); }

    public static String worldGameMode() { return text("worldGameMode", Config.worldgen.worldGameMode()); }
}
