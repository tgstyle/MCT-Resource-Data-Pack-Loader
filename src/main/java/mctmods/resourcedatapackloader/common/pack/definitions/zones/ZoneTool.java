package mctmods.resourcedatapackloader.common.pack.definitions.zones;

import javax.annotation.Nullable;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

public enum ZoneTool {
    HOE_TILL(ToolActions.HOE_TILL),
    AXE_STRIP(ToolActions.AXE_STRIP),
    SHOVEL_FLATTEN(ToolActions.SHOVEL_FLATTEN);

    private final ToolAction action;

    ZoneTool(ToolAction action) { this.action = action; }

    @Nullable public static ZoneTool of(String name) {
        for (ZoneTool tool : values()) {
            if (tool.name().equalsIgnoreCase(name)) { return tool; }
        }
        return null;
    }

    @Nullable public static ZoneTool of(ToolAction action) {
        for (ZoneTool tool : values()) {
            if (tool.action == action) { return tool; }
        }
        return null;
    }
}
