package mctmods.resourcedatapackloader.common.util;

import mctmods.resourcedatapackloader.core.config.Settings;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public final class BlockSelection {
    private final ItemSelection selection;
    private boolean everything;

    public BlockSelection(String kind, String verb, List<String> raw, ResourceLocation key) {
        selection = new ItemSelection(kind, verb);
        List<String> names = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        for (String each : raw) {
            String trimmed = Settings.lower(each);
            if ("*".equals(trimmed)) { everything = true; }
            else if (trimmed.startsWith("#")) { tags.add(each); }
            else { names.add(each); }
        }
        selection.readNames(names, key);
        selection.readTags(tags, key);
        selection.unknown();
    }

    public boolean any() { return everything || !selection.unnamed(); }

    public boolean matches(Block block) {
        if (everything || selection.blocks().contains(ForgeRegistries.BLOCKS.getKey(block))) { return true; }
        for (ResourceLocation tag : selection.tags()) {
            if (block.defaultBlockState().is(TagKey.create(Registries.BLOCK, tag))) { return true; }
        }
        return false;
    }
}
