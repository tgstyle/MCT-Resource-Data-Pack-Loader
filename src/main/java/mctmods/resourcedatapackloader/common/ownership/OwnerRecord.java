package mctmods.resourcedatapackloader.common.ownership;

import net.minecraft.nbt.CompoundTag;

public final class OwnerRecord {
    public static final String KIND_TEAM = "team";
    public static final String KIND_PLAYER = "player";
    public static final String SOURCE_SCOREBOARD = "scoreboard";
    public static final String SOURCE_FTB = "ftb";
    public static final String STATE_LIVE = "live";
    public static final String STATE_DISSOLVED = "dissolved";
    final int h;
    final String kind;
    final String source;
    final String key;
    String label;
    String state;
    int moved;
    int held;
    int heldClaims;

    OwnerRecord(int h, String kind, String source, String key, String label) {
        this.h = h;
        this.kind = kind;
        this.source = source;
        this.key = key;
        this.label = label;
        this.state = STATE_LIVE;
    }

    private OwnerRecord(CompoundTag tag) {
        this.h = tag.getInt("h");
        this.kind = tag.getString("kind");
        this.source = tag.getString("source");
        this.key = tag.getString("key");
        this.label = tag.getString("label");
        this.state = tag.getString("state").equals(STATE_DISSOLVED) ? STATE_DISSOLVED : STATE_LIVE;
        this.moved = tag.getInt("moved");
        this.held = tag.getInt("held");
        this.heldClaims = tag.getInt("heldClaims");
    }

    static OwnerRecord read(CompoundTag tag) { return new OwnerRecord(tag); }

    CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("h", h);
        tag.putString("kind", kind);
        tag.putString("source", source);
        tag.putString("key", key);
        tag.putString("label", label);
        tag.putString("state", state);
        tag.putInt("moved", moved);
        tag.putInt("held", held);
        tag.putInt("heldClaims", heldClaims);
        return tag;
    }

    boolean isLive() { return state.equals(STATE_LIVE); }

    String lookupKey() { return lookupKey(kind, source, key); }

    static String lookupKey(String kind, String source, String key) { return kind + "|" + source + "|" + key; }
}
