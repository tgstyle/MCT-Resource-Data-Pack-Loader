package mctmods.resourcedatapackloader.common.scripts;

import mctmods.resourcedatapackloader.common.util.Nbts;
import mctmods.resourcedatapackloader.common.util.world.SavedTypes;

import javax.annotation.Nullable;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public final class ScriptData extends SavedData {
    private static final String NAME = "rdpl_script_data";
    private static final String WORLD = "world";
    private static final String PLAYERS = "players";
    private static final String TEAMS = "teams";
    private static final SavedDataType<ScriptData> TYPE = SavedTypes.of(NAME, ScriptData::new, ScriptData::read, ScriptData::write);
    private CompoundTag world = new CompoundTag();
    private CompoundTag players = new CompoundTag();
    private CompoundTag teams = new CompoundTag();

    public static ScriptData get(ServerLevel level) { return level.getServer().overworld().getDataStorage().computeIfAbsent(TYPE); }

    public View world() { return new View(this, world); }

    public View player(Player player) { return new View(this, Nbts.child(players, player.getUUID().toString())); }

    public View team(String name) { return new View(this, Nbts.child(teams, name)); }

    private static ScriptData read(CompoundTag tag) {
        ScriptData data = new ScriptData();
        data.world = tag.getCompoundOrEmpty(WORLD);
        data.players = tag.getCompoundOrEmpty(PLAYERS);
        data.teams = tag.getCompoundOrEmpty(TEAMS);
        return data;
    }

    private CompoundTag write() {
        CompoundTag tag = new CompoundTag();
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
            return switch (values.get(key)) {
                case StringTag(String text) -> text;
                case DoubleTag(double number) -> number;
                case ByteTag(byte flag) -> flag != 0;
                case null, default -> null;
            };
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
