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
        this.h = tag.getIntOr("h", 0);
        this.kind = tag.getStringOr("kind", "");
        this.source = tag.getStringOr("source", "");
        this.key = tag.getStringOr("key", "");
        this.label = tag.getStringOr("label", "");
        this.state = tag.getStringOr("state", "").equals(STATE_DISSOLVED) ? STATE_DISSOLVED : STATE_LIVE;
        this.moved = tag.getIntOr("moved", 0);
        this.held = tag.getIntOr("held", 0);
        this.heldClaims = tag.getIntOr("heldClaims", 0);
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
