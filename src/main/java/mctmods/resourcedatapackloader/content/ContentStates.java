package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Registered;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.core.registries.BuiltInRegistries;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public final class ContentStates {
    private static final Map<String, Optional<BlockState>> KNOWN = new ConcurrentHashMap<>();

    private ContentStates() {}

    public record Spec(BlockState state, @Nullable CompoundTag tag, boolean exact) {}

    public static List<BlockState> matching(Block block, Map<String, String> properties, Object context) {
        Map<Property<?>, Comparable<?>> wanted = wanted(block, properties, context);
        List<BlockState> found = new ArrayList<>();
        for (BlockState state : block.getStateDefinition().getPossibleStates()) {
            boolean matches = true;
            for (Map.Entry<Property<?>, Comparable<?>> entry : wanted.entrySet()) {
                if (!state.getValue(entry.getKey()).equals(entry.getValue())) {
                    matches = false;
                    break;
                }
            }
            if (matches) { found.add(state); }
        }
        return found;
    }

    @Nullable public static BlockState parse(String written, Object context) {
        Spec spec = spec(written, context);
        return spec == null ? null : spec.state();
    }

    @Nullable public static BlockState known(String written, Object context) { return KNOWN.computeIfAbsent(written, key -> Optional.ofNullable(lookUp(key, context))).orElse(null); }

    public static void forget() { KNOWN.clear(); }

    @Nullable private static BlockState lookUp(String written, Object context) {
        BlockState found = parse(written, context);
        String name = written.split("[\\[{]", 2)[0].trim();
        if (found == null && !name.isEmpty()) { ContentLog.LOGGER.error("Unknown block {} in {}, the entry is skipped", name, context); }
        return found;
    }

    @Nullable public static Spec spec(String written, Object context) {
        String text = written.trim();
        CompoundTag tag = null;
        int brace = text.indexOf('{');
        if (brace >= 0) {
            try { tag = TagParser.parseTag(text.substring(brace)); }
            catch (CommandSyntaxException wrong) { ContentLog.LOGGER.error("Block '{}' in {} carries data that is not valid NBT, so it is placed without it", text, context); }
            text = text.substring(0, brace).trim();
        }
        Map<String, String> properties = new LinkedHashMap<>();
        int open = text.indexOf('[');
        String name = text;
        if (open >= 0) {
            name = text.substring(0, open).trim();
            String inside = text.substring(open + 1, text.endsWith("]") ? text.length() - 1 : text.length());
            for (String pair : inside.split(",")) {
                String[] halves = pair.split("=", 2);
                if (halves.length == 2) { properties.put(halves[0].trim(), halves[1].trim()); }
            }
        }
        if (name.isEmpty()) { return null; }
        Block block = Registered.find(BuiltInRegistries.BLOCK, ResourceLocation.tryParse(name.toLowerCase(Locale.ROOT)));
        if (block == null) { return null; }
        return new Spec(state(block, properties, context), tag, !properties.isEmpty());
    }

    public static BlockState state(Block block, Map<String, String> properties, Object context) {
        BlockState state = block.defaultBlockState();
        for (Map.Entry<Property<?>, Comparable<?>> entry : wanted(block, properties, context).entrySet()) { state = with(state, entry.getKey(), entry.getValue()); }
        return state;
    }

    @SuppressWarnings("unchecked") private static <T extends Comparable<T>> BlockState with(BlockState state, Property<T> property, Comparable<?> value) { return state.setValue(property, (T) value); }

    private static Map<Property<?>, Comparable<?>> wanted(Block block, Map<String, String> properties, Object context) {
        Map<Property<?>, Comparable<?>> wanted = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            Property<?> property = block.getStateDefinition().getProperty(entry.getKey());
            if (property == null) {
                ContentLog.LOGGER.error("Block {} in {} has no property '{}', ignoring it", block, context, entry.getKey());
                continue;
            }
            Optional<? extends Comparable<?>> value = property.getValue(entry.getValue());
            if (value.isEmpty()) {
                ContentLog.LOGGER.error("Property '{}' in {} has no value '{}', ignoring it", property.getName(), context, entry.getValue());
                continue;
            }
            wanted.put(property, value.get());
        }
        return wanted;
    }
}
