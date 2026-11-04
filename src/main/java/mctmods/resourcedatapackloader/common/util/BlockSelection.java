package mctmods.resourcedatapackloader.common.util;

import mctmods.resourcedatapackloader.core.config.Settings;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

public final class BlockSelection {
    private final ItemSelection selection;
    private boolean everything;

    public BlockSelection(String group, String key, List<String> entries, Identifier owner) {
        selection = new ItemSelection(group + " " + key, "matches");
        List<String> names = new ArrayList<>();
        for (String each : entries) {
            if ("*".equals(Settings.lower(each))) { everything = true; }
            else { names.add(each); }
        }
        selection.readNames(names, owner);
        selection.unknown();
    }

    public boolean any() { return everything || !selection.unnamed(); }

    public boolean matches(Block block) { return everything || selection.matches(block); }
}
