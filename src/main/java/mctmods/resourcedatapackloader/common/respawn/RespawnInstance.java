package mctmods.resourcedatapackloader.common.respawn;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.common.util.Constants;

public final class RespawnInstance {
    public static final int IMPRINT = 0;
    public static final int MAP = 1;
    public static final int CAVE = 2;
    public static final int PLOT = 3;
    public static final int DUNGEON = 4;
    private static final String[] KINDS = {"imprint", "map", "cave", "plot", "dungeon"};
    public final String id;
    public final int kind;
    public final String def;
    public final String tpl;
    public final BlockPos pos;
    public final Rotation rotation;
    public final Mirror mirror;
    public final int integrity;
    public final long seed;
    public final int[] box;
    public final Map<Long, String> loot = new LinkedHashMap<>();
    public final Map<Long, NBTTagCompound> spawn = new LinkedHashMap<>();
    public long last;
    public long due;

    public RespawnInstance(int kind, String def, String tpl, BlockPos pos, Rotation rotation, Mirror mirror, int integrity, long seed, StructureBoundingBox area) {
        this(kind, def, tpl, pos, rotation, mirror, integrity, seed, new int[] {area.minX, area.minY, area.minZ, area.maxX, area.maxY, area.maxZ});
    }

    private RespawnInstance(int kind, String def, String tpl, BlockPos pos, Rotation rotation, Mirror mirror, int integrity, long seed, int[] box) {
        this.kind = kind;
        this.def = def;
        this.tpl = tpl;
        this.pos = pos;
        this.rotation = rotation;
        this.mirror = mirror;
        this.integrity = MathHelper.clamp(integrity, 1, 100);
        this.seed = seed;
        this.box = box;
        this.id = KINDS[kind] + ":" + def + "@" + pos.toLong();
    }

    public StructureBoundingBox area() { return new StructureBoundingBox(box[0], box[1], box[2], box[3], box[4], box[5]); }

    public AxisAlignedBB bounds() { return new AxisAlignedBB(box[0], box[1], box[2], box[3] + 1, box[4] + 1, box[5] + 1); }

    public long centerChunk() { return ChunkPos.asLong(((box[0] + box[3]) >> 1) >> 4, ((box[2] + box[5]) >> 1) >> 4); }

    public void merge(RespawnInstance other) {
        loot.putAll(other.loot);
        spawn.putAll(other.spawn);
        last = Math.max(last, other.last);
    }

    public NBTTagCompound write() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("id", id);
        tag.setByte("kind", (byte) kind);
        tag.setString("def", def);
        tag.setString("tpl", tpl);
        tag.setIntArray("pos", new int[] {pos.getX(), pos.getY(), pos.getZ()});
        tag.setByte("rot", (byte) rotation.ordinal());
        tag.setByte("mir", (byte) mirror.ordinal());
        tag.setByte("integ", (byte) integrity);
        tag.setLong("seed", seed);
        tag.setIntArray("box", box.clone());
        tag.setLong("last", last);
        NBTTagList lootRows = new NBTTagList();
        for (Map.Entry<Long, String> row : loot.entrySet()) {
            NBTTagCompound entry = new NBTTagCompound();
            entry.setLong("pos", row.getKey());
            entry.setString("table", row.getValue());
            lootRows.appendTag(entry);
        }
        tag.setTag("loot", lootRows);
        NBTTagList spawnRows = new NBTTagList();
        for (Map.Entry<Long, NBTTagCompound> row : spawn.entrySet()) {
            NBTTagCompound entry = new NBTTagCompound();
            entry.setLong("pos", row.getKey());
            entry.setTag("nbt", row.getValue());
            spawnRows.appendTag(entry);
        }
        tag.setTag("spawn", spawnRows);
        return tag;
    }

    @Nullable public static RespawnInstance read(NBTTagCompound tag) {
        int kind = tag.getByte("kind");
        int[] at = tag.getIntArray("pos");
        int[] area = tag.getIntArray("box");
        if (kind < 0 || kind >= KINDS.length || at.length != 3 || area.length != 6) { return null; }
        Rotation[] turns = Rotation.values();
        Mirror[] flips = Mirror.values();
        Rotation rotation = turns[MathHelper.clamp(tag.getByte("rot"), 0, turns.length - 1)];
        Mirror mirror = flips[MathHelper.clamp(tag.getByte("mir"), 0, flips.length - 1)];
        RespawnInstance made = new RespawnInstance(kind, tag.getString("def"), tag.getString("tpl"), new BlockPos(at[0], at[1], at[2]), rotation, mirror, tag.getByte("integ"), tag.getLong("seed"), area);
        made.last = tag.getLong("last");
        NBTTagList lootRows = tag.getTagList("loot", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < lootRows.tagCount(); i++) { made.loot.put(lootRows.getCompoundTagAt(i).getLong("pos"), lootRows.getCompoundTagAt(i).getString("table")); }
        NBTTagList spawnRows = tag.getTagList("spawn", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < spawnRows.tagCount(); i++) { made.spawn.put(spawnRows.getCompoundTagAt(i).getLong("pos"), spawnRows.getCompoundTagAt(i).getCompoundTag("nbt")); }
        return made;
    }
}
