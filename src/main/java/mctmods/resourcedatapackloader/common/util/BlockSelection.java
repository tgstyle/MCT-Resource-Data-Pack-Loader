package mctmods.resourcedatapackloader.common.util;

import mctmods.resourcedatapackloader.core.config.Settings;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class BlockSelection {
    private final ItemSelection selection;
    private final Set<Block> blocks = new HashSet<>();
    private final List<TagKey<Block>> tags = new ArrayList<>();
    private boolean everything;

    public BlockSelection(String kind, String verb, List<String> raw, ResourceLocation key) {
        selection = new ItemSelection(kind, verb);
        List<String> names = new ArrayList<>();
        for (String each : raw) {
            if ("*".equals(Settings.lower(each))) { everything = true; }
            else { names.add(each); }
        }
        selection.readNames(names, key);
        selection.unknown();
        for (ResourceLocation id : selection.blocks()) { blocks.add(BuiltInRegistries.BLOCK.get(id)); }
        for (ResourceLocation tag : selection.tags()) { tags.add(TagKey.create(Registries.BLOCK, tag)); }
    }

    public boolean any() { return everything || !selection.unnamed(); }

    public boolean matches(Block block) {
        if (everything || blocks.contains(block)) { return true; }
        for (TagKey<Block> tag : tags) {
            if (block.builtInRegistryHolder().is(tag)) { return true; }
        }
        return false;
    }
}
