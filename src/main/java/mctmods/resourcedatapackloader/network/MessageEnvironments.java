package mctmods.resourcedatapackloader.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record MessageEnvironments(Map<ResourceLocation, List<String>> active, boolean whole) {
    public static void write(MessageEnvironments message, FriendlyByteBuf buf) {
        buf.writeBoolean(message.whole);
        buf.writeVarInt(message.active.size());
        for (Map.Entry<ResourceLocation, List<String>> entry : message.active.entrySet()) {
            buf.writeResourceLocation(entry.getKey());
            buf.writeVarInt(entry.getValue().size());
            for (String name : entry.getValue()) { buf.writeUtf(name); }
        }
    }

    public static MessageEnvironments read(FriendlyByteBuf buf) {
        boolean whole = buf.readBoolean();
        int count = buf.readVarInt();
        Map<ResourceLocation, List<String>> active = new LinkedHashMap<>();
        for (int i = 0; i < count; i++) {
            ResourceLocation dimension = buf.readResourceLocation();
            int size = buf.readVarInt();
            List<String> names = new ArrayList<>();
            for (int name = 0; name < size; name++) { names.add(buf.readUtf()); }
            active.put(dimension, names);
        }
        return new MessageEnvironments(active, whole);
    }
}
