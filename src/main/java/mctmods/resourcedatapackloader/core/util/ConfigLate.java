package mctmods.resourcedatapackloader.core.util;

import net.minecraftforge.common.config.Configuration;

public final class ConfigLate {
    public static final String FILE = "mct_resourcedatapackloader_mixin.cfg";
    public static final String PACKS = category("packs");
    public static final String COMPAT = category("compat");
    public static final String WORLDGEN = category("worldgen");

    private ConfigLate() {}

    private static String category(String name) { return Configuration.CATEGORY_GENERAL + Configuration.CATEGORY_SPLITTER + name; }
}
