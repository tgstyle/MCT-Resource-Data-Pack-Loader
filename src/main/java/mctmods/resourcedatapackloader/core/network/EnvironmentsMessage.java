package mctmods.resourcedatapackloader.core.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record EnvironmentsMessage(Map<ResourceLocation, List<String>> active, boolean whole) {
    public static void write(EnvironmentsMessage message, FriendlyByteBuf buf) {
        buf.writeBoolean(message.whole);
        buf.writeVarInt(message.active.size());
        for (Map.Entry<ResourceLocation, List<String>> entry : message.active.entrySet()) {
            buf.writeResourceLocation(entry.getKey());
            buf.writeVarInt(entry.getValue().size());
            StringListCodec.writeStrings(buf, entry.getValue(), entry.getValue().size());
        }
    }

    public static EnvironmentsMessage read(FriendlyByteBuf buf) {
        boolean whole = buf.readBoolean();
        int count = buf.readVarInt();
        Map<ResourceLocation, List<String>> active = new LinkedHashMap<>();
        for (int i = 0; i < count; i++) {
            ResourceLocation dimension = buf.readResourceLocation();
            active.put(dimension, StringListCodec.readStrings(buf, buf.readVarInt()));
        }
        return new EnvironmentsMessage(active, whole);
    }
}
