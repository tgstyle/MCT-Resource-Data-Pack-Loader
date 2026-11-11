package mctmods.resourcedatapackloader.common.worldgen.structure.map;

import mctmods.resourcedatapackloader.common.pack.definitions.worlds.StructureMapDef;
import mctmods.resourcedatapackloader.common.respawn.RespawnInstance;
import mctmods.resourcedatapackloader.common.respawn.RespawnRecorder;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public record ContentMapSite(int kind, String owner, long seed, BlockPos origin, Rotation turn, BoundingBox area) {
    private static final String KIND = "SiteKind";
    private static final String OWNER = "SiteOwner";
    private static final String SEED = "SiteSeed";
    private static final String ORIGIN = "SiteAt";
    private static final String TURN = "SiteRot";
    private static final String AREA = "SiteBox";

    @Nullable public static ContentMapSite of(StructureMapDef def, @Nullable String village, long seed, Rotation turn, int x, int base, int z) {
        int kind = village == null ? RespawnInstance.MAP : RespawnInstance.PLOT;
        String owner = village == null ? def.key().toString() : village;
        if (RespawnRecorder.resolve(kind, owner) == null) { return null; }
        boolean swapped = turn == Rotation.CLOCKWISE_90 || turn == Rotation.COUNTERCLOCKWISE_90;
        int wide = def.cellsWide() * def.cell();
        int deep = def.cellsDeep() * def.cell();
        BoundingBox area = new BoundingBox(x, base, z, x + (swapped ? deep : wide) - 1, base + def.layers().size() * def.cell() - 1, z + (swapped ? wide : deep) - 1);
        return new ContentMapSite(kind, owner, seed, new BlockPos(x, base, z), turn, area);
    }

    @Nullable public static ContentMapSite read(CompoundTag tag) {
        int[] box = tag.getIntArray(AREA);
        int[] at = tag.getIntArray(ORIGIN);
        if (!tag.contains(KIND) || box.length != 6 || at.length != 3) { return null; }
        return new ContentMapSite(tag.getInt(KIND), tag.getString(OWNER), tag.getLong(SEED), new BlockPos(at[0], at[1], at[2]), Rotation.valueOf(tag.getString(TURN)), new BoundingBox(box[0], box[1], box[2], box[3], box[4], box[5]));
    }

    public void write(CompoundTag tag) {
        tag.putInt(KIND, kind);
        tag.putString(OWNER, owner);
        tag.putLong(SEED, seed);
        tag.putIntArray(ORIGIN, new int[] {origin.getX(), origin.getY(), origin.getZ()});
        tag.putString(TURN, turn.name());
        tag.putIntArray(AREA, new int[] {area.minX(), area.minY(), area.minZ(), area.maxX(), area.maxY(), area.maxZ()});
    }

    public RespawnInstance instance() { return new RespawnInstance(kind, owner, "", origin, turn, Mirror.NONE, 100, seed, area); }
}
