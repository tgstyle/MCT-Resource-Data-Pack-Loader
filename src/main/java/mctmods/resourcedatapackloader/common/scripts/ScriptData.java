package mctmods.resourcedatapackloader.common.scripts;

import mctmods.resourcedatapackloader.common.util.Nbts;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.StringTag;
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
    private static final Factory<ScriptData> FACTORY = new Factory<>(ScriptData::new, (tag, lookup) -> read(tag));
    private CompoundTag world = new CompoundTag();
    private CompoundTag players = new CompoundTag();
    private CompoundTag teams = new CompoundTag();

    @Nullable public static ScriptData get(Level level) {
        MinecraftServer server = level.getServer();
        return server == null ? null : server.overworld().getDataStorage().computeIfAbsent(FACTORY, NAME);
    }

    private static ScriptData read(CompoundTag tag) {
        ScriptData data = new ScriptData();
        data.world = tag.getCompound(WORLD);
        data.players = tag.getCompound(PLAYERS);
        data.teams = tag.getCompound(TEAMS);
        return data;
    }

    public View world() { return new View(this, world); }

    public View player(Player player) { return new View(this, Nbts.child(players, player.getUUID().toString())); }

    public View team(String name) { return new View(this, Nbts.child(teams, name)); }

    @Override @Nonnull public CompoundTag save(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider lookup) {
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
            if (tag instanceof StringTag text) { return text.getAsString(); }
            if (tag instanceof DoubleTag number) { return number.getAsDouble(); }
            if (tag != null && tag.getId() == Tag.TAG_BYTE) { return values.getBoolean(key); }
            return null;
        }

        public void set(String key, Object value) {
            switch (value) {
                case String text -> values.putString(key, text);
                case Number number -> values.putDouble(key, number.doubleValue());
                case Boolean flag -> values.putBoolean(key, flag);
                default -> throw new IllegalArgumentException("Script data holds text, numbers and true/false only");
            }
            owner.setDirty();
        }

        public void remove(String key) {
            values.remove(key);
            owner.setDirty();
        }

        public boolean has(String key) { return values.contains(key); }
    }
}
