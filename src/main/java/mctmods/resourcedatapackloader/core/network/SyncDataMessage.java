package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;
import mctmods.resourcedatapackloader.common.syncdata.SyncValue;

import io.netty.handler.codec.DecoderException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SyncDataMessage(boolean full, List<Entry> entries) implements CustomPacketPayload {
    public static final int MAX_ENTRIES = 128;
    private static final int MAX_NAME = 64;
    private static final int MAX_TEXT = 256;
    private static final int REMOVED = 0;
    private static final int PLAYER = 1;
    public static final Type<SyncDataMessage> TYPE = new Type<>(ResourceDataPackLoader.id("syncdata"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncDataMessage> CODEC = StreamCodec.of(SyncDataMessage::write, SyncDataMessage::read);

    @Override @Nonnull public Type<? extends CustomPacketPayload> type() { return TYPE; }

    private static SyncDataMessage read(RegistryFriendlyByteBuf buf) {
        boolean full = buf.readBoolean();
        int count = buf.readShort();
        if (count < 0 || count > MAX_ENTRIES) { throw new DecoderException("Synced data holds " + count + " entries, the most is " + MAX_ENTRIES); }
        List<Entry> entries = new ArrayList<>();
        for (int at = 0; at < count; at++) {
            String name = buf.readUtf();
            int scope = buf.readByte();
            int kind = buf.readByte();
            if (kind < REMOVED || kind > SyncValue.BOOL) { throw new DecoderException("Synced data holds the value kind " + kind); }
            SyncValue value = kind == REMOVED ? null : kind == SyncValue.TEXT ? SyncValue.text(buf.readUtf()) : kind == SyncValue.NUMBER ? SyncValue.number(buf.readDouble()) : SyncValue.bool(buf.readBoolean());
            boolean sound = !name.isEmpty() && name.length() <= MAX_NAME && (scope == 0 || scope == PLAYER) && (value == null || value.kind != SyncValue.TEXT || value.text.length() <= MAX_TEXT);
            if (sound) { entries.add(new Entry(name, scope == PLAYER, value)); }
        }
        return new SyncDataMessage(full, entries);
    }

    private static void write(RegistryFriendlyByteBuf buf, SyncDataMessage message) {
        buf.writeBoolean(message.full);
        buf.writeShort(message.entries.size());
        for (Entry entry : message.entries) {
            buf.writeUtf(entry.name());
            buf.writeByte(entry.player() ? PLAYER : 0);
            SyncValue value = entry.value();
            buf.writeByte(value == null ? REMOVED : value.kind);
            if (value == null) { continue; }
            if (value.kind == SyncValue.TEXT) { buf.writeUtf(value.text); }
            else if (value.kind == SyncValue.NUMBER) { buf.writeDouble(value.number); }
            else { buf.writeBoolean(value.flag); }
        }
    }

    public record Entry(String name, boolean player, @Nullable SyncValue value) {}
}
