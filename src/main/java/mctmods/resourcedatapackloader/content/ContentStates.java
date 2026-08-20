package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.common.base.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentStates {
    private ContentStates() {}

    @Nullable public static Block block(String name, Object context) {
        if (name == null || name.isEmpty()) {
            ContentLog.LOGGER.error("A block name is missing in {}, the entry is skipped", context);
            return null;
        }
        ResourceLocation location = new ResourceLocation(name);
        if (!ForgeRegistries.BLOCKS.containsKey(location)) {
            ContentLog.LOGGER.error("Unknown block {} in {}, the entry is skipped", location, context);
            return null;
        }
        return ForgeRegistries.BLOCKS.getValue(location);
    }

    @Nullable public static IBlockState parse(String name, Object context) {
        if (name.isEmpty()) { return null; }
        String[] parts = name.split(":");
        Block block = block(parts.length < 3 ? name : parts[0] + ":" + parts[1], context);
        if (block == null) { return null; }
        if (parts.length < 3) { return of(block, 0); }
        try { return of(block, Integer.parseInt(parts[2])); }
        catch (NumberFormatException ex) {
            ContentLog.LOGGER.error("Block metadata '{}' in {} is not a number, using 0", parts[2], context);
            return of(block, 0);
        }
    }

    public static IBlockState of(Block block, int meta, Map<String, String> properties, Object context) {
        if (properties.isEmpty()) { return of(block, meta); }
        IBlockState state = block.getDefaultState();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            IProperty<?> property = block.getBlockState().getProperty(entry.getKey());
            if (property == null) {
                ContentLog.LOGGER.error("Block {} in {} has no property '{}', ignoring it", block.getRegistryName(), context, entry.getKey());
                continue;
            }
            state = with(state, property, entry.getValue(), context);
        }
        return state;
    }

    private static <T extends Comparable<T>> IBlockState with(IBlockState state, IProperty<T> property, String value, Object context) {
        Optional<T> parsed = property.parseValue(value);
        if (!parsed.isPresent()) {
            ContentLog.LOGGER.error("Property '{}' in {} has no value '{}', leaving it at its default", property.getName(), context, value);
            return state;
        }
        return state.withProperty(property, parsed.get());
    }

    public static IBlockState of(Block block, int meta) {
        if (meta == 0) { return block.getDefaultState(); }
        for (IBlockState candidate : block.getBlockState().getValidStates()) {
            if (block.getMetaFromState(candidate) == meta) { return candidate; }
        }
        ContentLog.LOGGER.error("Block {} has no state with meta {}, using its default state", block.getRegistryName(), meta);
        return block.getDefaultState();
    }
}
