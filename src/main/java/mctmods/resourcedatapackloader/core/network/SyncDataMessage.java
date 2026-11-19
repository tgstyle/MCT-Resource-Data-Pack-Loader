package mctmods.resourcedatapackloader.core.network;

import mctmods.resourcedatapackloader.common.syncdata.SyncValue;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class SyncDataMessage implements IMessage {
    public static final int MAX_ENTRIES = 128;
    private static final int MAX_NAME = 64;
    private static final int MAX_TEXT = 256;
    private static final int REMOVED = 0;
    private static final int PLAYER = 1;
    public boolean full;
    public List<Entry> entries = new ArrayList<>();

    public SyncDataMessage() {}

    public SyncDataMessage(boolean full, List<Entry> entries) {
        this.full = full;
        this.entries = entries;
    }

    @Override public void fromBytes(ByteBuf buf) {
        full = buf.readBoolean();
        int count = Math.min(buf.readShort(), MAX_ENTRIES);
        for (int at = 0; at < count; at++) {
            String name = ByteBufUtils.readUTF8String(buf);
            int scope = buf.readByte();
            int kind = buf.readByte();
            if (kind < REMOVED || kind > SyncValue.BOOL) { return; }
            SyncValue value = kind == REMOVED ? null : kind == SyncValue.TEXT ? SyncValue.text(ByteBufUtils.readUTF8String(buf)) : kind == SyncValue.NUMBER ? SyncValue.number(buf.readDouble()) : SyncValue.bool(buf.readBoolean());
            boolean sound = !name.isEmpty() && name.length() <= MAX_NAME && (scope == 0 || scope == PLAYER) && (value == null || value.kind != SyncValue.TEXT || value.text.length() <= MAX_TEXT);
            if (sound) { entries.add(new Entry(name, scope == PLAYER, value)); }
        }
    }

    @Override public void toBytes(ByteBuf buf) {
        buf.writeBoolean(full);
        buf.writeShort(entries.size());
        for (Entry entry : entries) {
            ByteBufUtils.writeUTF8String(buf, entry.name);
            buf.writeByte(entry.player ? PLAYER : 0);
            SyncValue value = entry.value;
            buf.writeByte(value == null ? REMOVED : value.kind);
            if (value == null) { continue; }
            if (value.kind == SyncValue.TEXT) { ByteBufUtils.writeUTF8String(buf, value.text); }
            else if (value.kind == SyncValue.NUMBER) { buf.writeDouble(value.number); }
            else { buf.writeBoolean(value.flag); }
        }
    }

    public static final class Entry {
        public final String name;
        public final boolean player;
        @Nullable public final SyncValue value;

        public Entry(String name, boolean player, @Nullable SyncValue value) {
            this.name = name;
            this.player = player;
            this.value = value;
        }
    }
}
