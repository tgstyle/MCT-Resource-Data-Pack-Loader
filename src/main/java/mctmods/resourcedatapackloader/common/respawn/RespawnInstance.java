package mctmods.resourcedatapackloader.common.respawn;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;

public final class RespawnInstance {
    public static final int IMPRINT = 0;
    public static final int MAP = 1;
    public static final int CAVE = 2;
    public static final int PLOT = 3;
    public static final int DUNGEON = 4;
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
    public final Map<Long, CompoundTag> spawn = new LinkedHashMap<>();
    public long last;
    public long due;

    public RespawnInstance(int kind, String def, String tpl, BlockPos pos, Rotation rotation, Mirror mirror, int integrity, long seed, BoundingBox area) { this(kind, def, tpl, pos, rotation, mirror, integrity, seed, new int[] {area.minX(), area.minY(), area.minZ(), area.maxX(), area.maxY(), area.maxZ()}); }

    private RespawnInstance(int kind, String def, String tpl, BlockPos pos, Rotation rotation, Mirror mirror, int integrity, long seed, int[] box) {
        this.kind = kind;
        this.def = def;
        this.tpl = tpl;
        this.pos = pos;
        this.rotation = rotation;
        this.mirror = mirror;
        this.integrity = Mth.clamp(integrity, 1, 100);
        this.seed = seed;
        this.box = box;
        this.id = KINDS[kind] + ":" + def + "@" + pos.asLong();
    }

    public BoundingBox area() { return new BoundingBox(box[0], box[1], box[2], box[3], box[4], box[5]); }

    public AABB bounds() { return new AABB(box[0], box[1], box[2], box[3] + 1, box[4] + 1, box[5] + 1); }

    public BlockPos center() { return new BlockPos((box[0] + box[3]) >> 1, (box[1] + box[4]) >> 1, (box[2] + box[5]) >> 1); }

    public long centerChunk() { return ChunkPos.asLong(((box[0] + box[3]) >> 1) >> 4, ((box[2] + box[5]) >> 1) >> 4); }

    public void merge(RespawnInstance other) {
        loot.putAll(other.loot);
        spawn.putAll(other.spawn);
        last = Math.max(last, other.last);
    }

    public CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        tag.putString(ID_KEY, id);
        tag.putByte(KIND_KEY, (byte) kind);
        tag.putString(DEF_KEY, def);
        tag.putString(TPL_KEY, tpl);
        tag.putIntArray(POS_KEY, new int[] {pos.getX(), pos.getY(), pos.getZ()});
        tag.putByte(ROT_KEY, (byte) rotation.ordinal());
        tag.putByte(MIR_KEY, (byte) mirror.ordinal());
        tag.putByte(INTEG_KEY, (byte) integrity);
        tag.putLong(SEED_KEY, seed);
        tag.putIntArray(BOX_KEY, box.clone());
        tag.putLong(LAST_KEY, last);
        ListTag lootRows = new ListTag();
        for (Map.Entry<Long, String> row : loot.entrySet()) {
            CompoundTag entry = new CompoundTag();
            entry.putLong(ROW_POS_KEY, row.getKey());
            entry.putString(TABLE_KEY, row.getValue());
            lootRows.add(entry);
        }
        tag.put(LOOT_KEY, lootRows);
        ListTag spawnRows = new ListTag();
        for (Map.Entry<Long, CompoundTag> row : spawn.entrySet()) {
            CompoundTag entry = new CompoundTag();
            entry.putLong(ROW_POS_KEY, row.getKey());
            entry.put(NBT_KEY, row.getValue());
            spawnRows.add(entry);
        }
        tag.put(SPAWN_KEY, spawnRows);
        return tag;
    }

    @Nullable public static RespawnInstance read(CompoundTag tag) {
        int kind = tag.getByte(KIND_KEY);
        int[] at = tag.getIntArray(POS_KEY);
        int[] area = tag.getIntArray(BOX_KEY);
        if (kind < 0 || kind >= KINDS.length || at.length != 3 || area.length != 6) { return null; }
        Rotation[] turns = Rotation.values();
        Mirror[] flips = Mirror.values();
        Rotation rotation = turns[Mth.clamp(tag.getByte(ROT_KEY), 0, turns.length - 1)];
        Mirror mirror = flips[Mth.clamp(tag.getByte(MIR_KEY), 0, flips.length - 1)];
        RespawnInstance made = new RespawnInstance(kind, tag.getString(DEF_KEY), tag.getString(TPL_KEY), new BlockPos(at[0], at[1], at[2]), rotation, mirror, tag.getByte(INTEG_KEY), tag.getLong(SEED_KEY), area);
        made.last = tag.getLong(LAST_KEY);
        ListTag lootRows = tag.getList(LOOT_KEY, Tag.TAG_COMPOUND);
        for (int i = 0; i < lootRows.size(); i++) { made.loot.put(lootRows.getCompound(i).getLong(ROW_POS_KEY), lootRows.getCompound(i).getString(TABLE_KEY)); }
        ListTag spawnRows = tag.getList(SPAWN_KEY, Tag.TAG_COMPOUND);
        for (int i = 0; i < spawnRows.size(); i++) { made.spawn.put(spawnRows.getCompound(i).getLong(ROW_POS_KEY), spawnRows.getCompound(i).getCompound(NBT_KEY)); }
        return made;
    }
}
