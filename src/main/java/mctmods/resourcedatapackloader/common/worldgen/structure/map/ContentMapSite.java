package mctmods.resourcedatapackloader.common.worldgen.structure.map;

import mctmods.resourcedatapackloader.common.pack.definitions.worlds.StructureMapDef;
import mctmods.resourcedatapackloader.common.respawn.RespawnInstance;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public record ContentMapSite(int kind, String def, long seed, BlockPos origin, Rotation turn, BoundingBox box) {
    private static final String KIND = "SiteKind";
    private static final String DEF = "SiteDef";
    private static final String SEED = "SiteSeed";
    private static final String AT = "SiteAt";
    private static final String TURN = "SiteTurn";
    private static final String BOX = "SiteBox";

    public static ContentMapSite of(int kind, String def, StructureMapDef map, long seed, Rotation turn, int x, int base, int z) {
        boolean swapped = turn == Rotation.CLOCKWISE_90 || turn == Rotation.COUNTERCLOCKWISE_90;
        int wide = map.cellsWide() * map.cell();
        int deep = map.cellsDeep() * map.cell();
        BoundingBox box = new BoundingBox(x, base, z, x + (swapped ? deep : wide) - 1, base + map.layers().size() * map.cell() - 1, z + (swapped ? wide : deep) - 1);
        return new ContentMapSite(kind, def, seed, new BlockPos(x, base, z), turn, box);
    }

    @Nullable public static ContentMapSite read(CompoundTag tag) {
        int[] at = tag.getIntArray(AT).orElse(new int[0]);
        int[] area = tag.getIntArray(BOX).orElse(new int[0]);
        if (at.length != 3 || area.length != 6) { return null; }
        Rotation[] turns = Rotation.values();
        return new ContentMapSite(tag.getByteOr(KIND, (byte) 0), tag.getStringOr(DEF, ""), tag.getLongOr(SEED, 0L), new BlockPos(at[0], at[1], at[2]), turns[Mth.clamp(tag.getByteOr(TURN, (byte) 0), 0, turns.length - 1)], new BoundingBox(area[0], area[1], area[2], area[3], area[4], area[5]));
    }

    public void write(CompoundTag tag) {
        tag.putByte(KIND, (byte) kind);
        tag.putString(DEF, def);
        tag.putLong(SEED, seed);
        tag.putIntArray(AT, new int[] {origin.getX(), origin.getY(), origin.getZ()});
        tag.putByte(TURN, (byte) turn.ordinal());
        tag.putIntArray(BOX, new int[] {box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ()});
    }

    public RespawnInstance instance() { return new RespawnInstance(kind, def, "", origin, turn, Mirror.NONE, 100, seed, box); }
}
