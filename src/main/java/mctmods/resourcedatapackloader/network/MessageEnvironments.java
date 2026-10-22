package mctmods.resourcedatapackloader.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

public record MessageEnvironments(Map<Identifier, List<String>> active, boolean whole) implements CustomPacketPayload {
    public static final Type<MessageEnvironments> TYPE = new Type<>(ResourceDataPackLoader.id("environments"));
    public static final StreamCodec<FriendlyByteBuf, MessageEnvironments> CODEC = StreamCodec.of(MessageEnvironments::write, MessageEnvironments::read);

    private static void write(FriendlyByteBuf buf, MessageEnvironments message) {
        buf.writeBoolean(message.whole);
        buf.writeVarInt(message.active.size());
        for (Map.Entry<Identifier, List<String>> entry : message.active.entrySet()) {
            buf.writeIdentifier(entry.getKey());
            buf.writeVarInt(entry.getValue().size());
            for (String name : entry.getValue()) { buf.writeUtf(name); }
        }
    }

    private static MessageEnvironments read(FriendlyByteBuf buf) {
        boolean whole = buf.readBoolean();
        int count = buf.readVarInt();
        Map<Identifier, List<String>> active = new LinkedHashMap<>();
        for (int i = 0; i < count; i++) {
            Identifier dimension = buf.readIdentifier();
            int size = buf.readVarInt();
            List<String> names = new ArrayList<>();
            for (int name = 0; name < size; name++) { names.add(buf.readUtf()); }
            active.put(dimension, names);
        }
        return new MessageEnvironments(active, whole);
    }

    @Override @Nonnull public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
