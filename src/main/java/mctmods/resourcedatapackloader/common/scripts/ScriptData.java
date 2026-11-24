package mctmods.resourcedatapackloader.common.scripts;

import mctmods.resourcedatapackloader.common.util.Nbts;
import mctmods.resourcedatapackloader.common.util.world.SavedData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

public final class ScriptData extends WorldSavedData {
    private static final String NAME = "rdpl_script_data";
    private static final String WORLD = "world";
    private static final String PLAYERS = "players";
    private static final String TEAMS = "teams";
    private NBTTagCompound world = new NBTTagCompound();
    private NBTTagCompound players = new NBTTagCompound();
    private NBTTagCompound teams = new NBTTagCompound();

    public ScriptData(String name) { super(name); }

    @Nullable public static ScriptData get(World world) {
        return SavedData.overworld(world, ScriptData.class, NAME, ScriptData::new);
    }

    public View world() { return new View(this, world); }

    public View player(EntityPlayer player) { return new View(this, Nbts.child(players, player.getUniqueID().toString())); }

    public View team(String name) { return new View(this, Nbts.child(teams, name)); }

    @Override public void readFromNBT(@Nonnull NBTTagCompound tag) {
        world = tag.getCompoundTag(WORLD);
        players = tag.getCompoundTag(PLAYERS);
        teams = tag.getCompoundTag(TEAMS);
    }

    @Override @Nonnull public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound tag) {
        tag.setTag(WORLD, world);
        tag.setTag(PLAYERS, players);
        tag.setTag(TEAMS, teams);
        return tag;
    }

    public static final class View {
        private final ScriptData owner;
        private final NBTTagCompound values;

        private View(ScriptData owner, NBTTagCompound values) {
            this.owner = owner;
            this.values = values;
        }

        @Nullable public Object get(String key) {
            if (!values.hasKey(key)) { return null; }
            NBTBase tag = values.getTag(key);
            if (tag.getId() == Constants.NBT.TAG_STRING) { return ((NBTTagString) tag).getString(); }
            if (tag.getId() == Constants.NBT.TAG_DOUBLE) { return ((NBTTagDouble) tag).getDouble(); }
            if (tag.getId() == Constants.NBT.TAG_BYTE) { return ((NBTTagByte) tag).getByte() != 0; }
            return null;
        }

        public void set(String key, Object value) {
            if (value instanceof String) { values.setString(key, (String) value); }
            else if (value instanceof Number) { values.setDouble(key, ((Number) value).doubleValue()); }
            else if (value instanceof Boolean) { values.setBoolean(key, (Boolean) value); }
            else { throw new IllegalArgumentException("Script data holds text, numbers and true/false only"); }
            owner.markDirty();
        }

        public void remove(String key) {
            values.removeTag(key);
            owner.markDirty();
        }

        public boolean has(String key) { return values.hasKey(key); }
    }
}
