package mctmods.resourcedatapackloader.common.respawn;

import mctmods.resourcedatapackloader.core.util.math.Turns;

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
    public static final int WHOLE = 100;
    private static final String[] KINDS = {"imprint", "map", "cave", "plot", "dungeon"};
    private static final String ID_KEY = "id";
    private static final String KIND_KEY = "kind";
    private static final String DEF_KEY = "def";
    private static final String TPL_KEY = "tpl";
    private static final String POS_KEY = "pos";
    private static final String ROW_POS_KEY = "pos";
    private static final String ROT_KEY = "rot";
    private static final String MIR_KEY = "mir";
    private static final String INTEG_KEY = "integ";
    private static final String SEED_KEY = "seed";
    private static final String BOX_KEY = "box";
    private static final String LAST_KEY = "last";
    private static final String LOOT_KEY = "loot";
    private static final String TABLE_KEY = "table";
    private static final String SPAWN_KEY = "spawn";
    private static final String NBT_KEY = "nbt";
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
        tag.setString(ID_KEY, id);
        tag.setByte(KIND_KEY, (byte) kind);
        tag.setString(DEF_KEY, def);
        tag.setString(TPL_KEY, tpl);
        tag.setIntArray(POS_KEY, new int[] {pos.getX(), pos.getY(), pos.getZ()});
        tag.setByte(ROT_KEY, (byte) rotation.ordinal());
        tag.setByte(MIR_KEY, (byte) mirror.ordinal());
        tag.setByte(INTEG_KEY, (byte) integrity);
        tag.setLong(SEED_KEY, seed);
        tag.setIntArray(BOX_KEY, box.clone());
        tag.setLong(LAST_KEY, last);
        NBTTagList lootRows = new NBTTagList();
        for (Map.Entry<Long, String> row : loot.entrySet()) {
            NBTTagCompound entry = new NBTTagCompound();
            entry.setLong(ROW_POS_KEY, row.getKey());
            entry.setString(TABLE_KEY, row.getValue());
            lootRows.appendTag(entry);
        }
        tag.setTag(LOOT_KEY, lootRows);
        NBTTagList spawnRows = new NBTTagList();
        for (Map.Entry<Long, NBTTagCompound> row : spawn.entrySet()) {
            NBTTagCompound entry = new NBTTagCompound();
            entry.setLong(ROW_POS_KEY, row.getKey());
            entry.setTag(NBT_KEY, row.getValue());
            spawnRows.appendTag(entry);
        }
        tag.setTag(SPAWN_KEY, spawnRows);
        return tag;
    }

    @Nullable public static RespawnInstance read(NBTTagCompound tag) {
        int kind = tag.getByte(KIND_KEY);
        int[] at = tag.getIntArray(POS_KEY);
        int[] area = tag.getIntArray(BOX_KEY);
        if (kind < 0 || kind >= KINDS.length || at.length != 3 || area.length != 6) { return null; }
        Rotation[] turns = Turns.ALL;
        Mirror[] flips = Mirror.values();
        Rotation rotation = turns[MathHelper.clamp(tag.getByte(ROT_KEY), 0, turns.length - 1)];
        Mirror mirror = flips[MathHelper.clamp(tag.getByte(MIR_KEY), 0, flips.length - 1)];
        RespawnInstance made = new RespawnInstance(kind, tag.getString(DEF_KEY), tag.getString(TPL_KEY), new BlockPos(at[0], at[1], at[2]), rotation, mirror, tag.getByte(INTEG_KEY), tag.getLong(SEED_KEY), area);
        made.last = tag.getLong(LAST_KEY);
        NBTTagList lootRows = tag.getTagList(LOOT_KEY, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < lootRows.tagCount(); i++) { made.loot.put(lootRows.getCompoundTagAt(i).getLong(ROW_POS_KEY), lootRows.getCompoundTagAt(i).getString(TABLE_KEY)); }
        NBTTagList spawnRows = tag.getTagList(SPAWN_KEY, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < spawnRows.tagCount(); i++) { made.spawn.put(spawnRows.getCompoundTagAt(i).getLong(ROW_POS_KEY), spawnRows.getCompoundTagAt(i).getCompoundTag(NBT_KEY)); }
        return made;
    }
}
