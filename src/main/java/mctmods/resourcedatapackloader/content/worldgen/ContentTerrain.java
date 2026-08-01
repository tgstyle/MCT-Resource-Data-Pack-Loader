package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.util.Config;

public final class ContentTerrain {
    private ContentTerrain() {}

    public static String options() {
        if (ContentControl.off(ContentControl.TERRAIN)) { return ""; }

        return ContentControl.text(ContentControl.TERRAIN, "generatorOptions", Config.worldgen.generatorOptions).trim();
    }
}
