package mctmods.resourcedatapackloader.common.util;

import mctmods.resourcedatapackloader.core.config.Settings;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

public final class BlockSelection {
    private final ItemSelection selection;
    private boolean everything;

    public BlockSelection(String kind, String verb, String[] raw, ResourceLocation key) {
        selection = new ItemSelection(kind, verb);
        List<String> names = new ArrayList<>();
        for (String each : raw) {
            if ("*".equals(Settings.lower(each))) { everything = true; }
            else { names.add(each); }
        }
        selection.readNames(names, key);
        selection.resolve();
    }

    public boolean any() { return everything || !selection.unnamed(); }

    public boolean matches(Block block) { return everything || selection.matches(block); }
}
