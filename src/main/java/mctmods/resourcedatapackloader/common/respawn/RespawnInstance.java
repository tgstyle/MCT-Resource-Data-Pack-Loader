package mctmods.resourcedatapackloader.common.respawn;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
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
    private static final String[] KINDS = {"imprint", "map", "cave", "plot"};
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

    public RespawnInstance(int kind, String def, String tpl, BlockPos pos, Rotation rotation, Mirror mirror, int integrity, long seed, BoundingBox area) {
        this(kind, def, tpl, pos, rotation, mirror, integrity, seed, new int[] {area.minX(), area.minY(), area.minZ(), area.maxX(), area.maxY(), area.maxZ()});
    }

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

    public long centerChunk() { return ChunkPos.pack(((box[0] + box[3]) >> 1) >> 4, ((box[2] + box[5]) >> 1) >> 4); }

    public void merge(RespawnInstance other) {
        loot.putAll(other.loot);
        spawn.putAll(other.spawn);
        last = Math.max(last, other.last);
    }

    public CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", id);
        tag.putByte("kind", (byte) kind);
        tag.putString("def", def);
        tag.putString("tpl", tpl);
        tag.putIntArray("pos", new int[] {pos.getX(), pos.getY(), pos.getZ()});
        tag.putByte("rot", (byte) rotation.ordinal());
        tag.putByte("mir", (byte) mirror.ordinal());
        tag.putByte("integ", (byte) integrity);
        tag.putLong("seed", seed);
        tag.putIntArray("box", box.clone());
        tag.putLong("last", last);
        ListTag lootRows = new ListTag();
        for (Map.Entry<Long, String> row : loot.entrySet()) {
            CompoundTag entry = new CompoundTag();
            entry.putLong("pos", row.getKey());
            entry.putString("table", row.getValue());
            lootRows.add(entry);
        }
        tag.put("loot", lootRows);
        ListTag spawnRows = new ListTag();
        for (Map.Entry<Long, CompoundTag> row : spawn.entrySet()) {
            CompoundTag entry = new CompoundTag();
            entry.putLong("pos", row.getKey());
            entry.put("nbt", row.getValue().copy());
            spawnRows.add(entry);
        }
        tag.put("spawn", spawnRows);
        return tag;
    }

    @Nullable public static RespawnInstance read(CompoundTag tag) {
        int kind = tag.getByteOr("kind", (byte) 0);
        int[] at = tag.getIntArray("pos").orElse(new int[0]);
        int[] area = tag.getIntArray("box").orElse(new int[0]);
        if (kind < 0 || kind >= KINDS.length || at.length != 3 || area.length != 6) { return null; }
        Rotation[] turns = Rotation.values();
        Mirror[] flips = Mirror.values();
        Rotation rotation = turns[Mth.clamp(tag.getByteOr("rot", (byte) 0), 0, turns.length - 1)];
        Mirror mirror = flips[Mth.clamp(tag.getByteOr("mir", (byte) 0), 0, flips.length - 1)];
        RespawnInstance made = new RespawnInstance(kind, tag.getStringOr("def", ""), tag.getStringOr("tpl", ""), new BlockPos(at[0], at[1], at[2]), rotation, mirror, tag.getByteOr("integ", (byte) 0), tag.getLongOr("seed", 0L), area);
        made.last = tag.getLongOr("last", 0L);
        ListTag lootRows = tag.getListOrEmpty("loot");
        for (int i = 0; i < lootRows.size(); i++) { made.loot.put(lootRows.getCompoundOrEmpty(i).getLongOr("pos", 0L), lootRows.getCompoundOrEmpty(i).getStringOr("table", "")); }
        ListTag spawnRows = tag.getListOrEmpty("spawn");
        for (int i = 0; i < spawnRows.size(); i++) { made.spawn.put(spawnRows.getCompoundOrEmpty(i).getLongOr("pos", 0L), spawnRows.getCompoundOrEmpty(i).getCompoundOrEmpty("nbt")); }
        return made;
    }
}
