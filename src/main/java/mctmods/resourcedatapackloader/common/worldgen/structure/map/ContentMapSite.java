package mctmods.resourcedatapackloader.common.worldgen.structure.map;

import mctmods.resourcedatapackloader.common.respawn.RespawnInstance;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public record ContentMapSite(int kind, String key, long seed, BlockPos origin, BoundingBox area) {
    private static final String KIND = "SiteKind";
    private static final String KEY = "SiteKey";
    private static final String SEED = "SiteSeed";
    private static final String ORIGIN = "SiteOrigin";
    private static final String AREA = "SiteArea";

    public void write(CompoundTag tag) {
        tag.putByte(KIND, (byte) kind);
        tag.putString(KEY, key);
        tag.putLong(SEED, seed);
        tag.putIntArray(ORIGIN, new int[] {origin.getX(), origin.getY(), origin.getZ()});
        tag.putIntArray(AREA, new int[] {area.minX(), area.minY(), area.minZ(), area.maxX(), area.maxY(), area.maxZ()});
    }

    @Nullable public static ContentMapSite read(CompoundTag tag) {
        if (!tag.contains(KEY)) { return null; }
        int[] at = tag.getIntArray(ORIGIN);
        int[] box = tag.getIntArray(AREA);
        int kind = tag.getByte(KIND);
        if (at.length != 3 || box.length != 6 || kind != RespawnInstance.MAP && kind != RespawnInstance.PLOT) { return null; }
        return new ContentMapSite(kind, tag.getString(KEY), tag.getLong(SEED), new BlockPos(at[0], at[1], at[2]), new BoundingBox(box[0], box[1], box[2], box[3], box[4], box[5]));
    }

    public RespawnInstance instance(Rotation turn) { return new RespawnInstance(kind, key, "", origin, turn, Mirror.NONE, 100, seed, area); }
}
