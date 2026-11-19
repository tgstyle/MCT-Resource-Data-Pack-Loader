package mctmods.resourcedatapackloader.common.pack.definitions.zones;

import javax.annotation.Nullable;

public enum ZoneTool {
    HOE_TILL,
    AXE_STRIP,
    SHOVEL_FLATTEN;

    @Nullable public static ZoneTool of(String name) {
        for (ZoneTool tool : values()) {
            if (tool.name().equalsIgnoreCase(name)) { return tool; }
        }
        return null;
    }
}
