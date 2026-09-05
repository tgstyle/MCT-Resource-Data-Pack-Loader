package mctmods.resourcedatapackloader.content;

import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ContentStates {
    private ContentStates() {}

    public static List<BlockState> matching(Block block, Map<String, String> properties, Object context) {
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
}
