package mctmods.resourcedatapackloader.common.scripts;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

public final class ScriptData extends SavedData {
    private static final String NAME = "rdpl_script_data";
    private static final String WORLD = "world";
    private static final String PLAYERS = "players";
    private static final String TEAMS = "teams";
    private CompoundTag world = new CompoundTag();
    private CompoundTag players = new CompoundTag();
    private CompoundTag teams = new CompoundTag();

    @Nullable public static ScriptData get(Level level) {
        MinecraftServer server = level.getServer();
        if (server == null) { return null; }
        return server.overworld().getDataStorage().computeIfAbsent(ScriptData::read, ScriptData::new, NAME);
    }

    private static ScriptData read(CompoundTag tag) {
        ScriptData data = new ScriptData();
        data.world = tag.getCompound(WORLD);
        data.players = tag.getCompound(PLAYERS);
        data.teams = tag.getCompound(TEAMS);
        return data;
    }

    public View world() { return new View(this, world); }

    public View player(Player player) { return new View(this, scope(players, player.getUUID().toString())); }

    public View team(String name) { return new View(this, scope(teams, name)); }

    private CompoundTag scope(CompoundTag parent, String key) {
        if (!parent.contains(key, Tag.TAG_COMPOUND)) { parent.put(key, new CompoundTag()); }
        return parent.getCompound(key);
    }

    @Override @Nonnull public CompoundTag save(@Nonnull CompoundTag tag) {
        tag.put(WORLD, world);
        tag.put(PLAYERS, players);
        tag.put(TEAMS, teams);
        return tag;
    }

    public static final class View {
        private final ScriptData owner;
        private final CompoundTag values;

        private View(ScriptData owner, CompoundTag values) {
            this.owner = owner;
            this.values = values;
        }

        @Nullable public Object get(String key) {
            if (!values.contains(key)) { return null; }
            Tag tag = values.get(key);
            if (tag == null) { return null; }
            if (tag.getId() == Tag.TAG_STRING) { return values.getString(key); }
            if (tag.getId() == Tag.TAG_DOUBLE) { return values.getDouble(key); }
            if (tag.getId() == Tag.TAG_BYTE) { return values.getByte(key) != 0; }
            return null;
        }

        public void set(String key, Object value) {
            if (value instanceof String text) { values.putString(key, text); }
            else if (value instanceof Number number) { values.putDouble(key, number.doubleValue()); }
            else if (value instanceof Boolean flag) { values.putBoolean(key, flag); }
            else { throw new IllegalArgumentException("Script data holds text, numbers and true/false only"); }
            owner.setDirty();
        }

        public void remove(String key) {
            values.remove(key);
            owner.setDirty();
        }

        public boolean has(String key) { return values.contains(key); }
    }
}
