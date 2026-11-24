package mctmods.resourcedatapackloader.common.villages;

import mctmods.resourcedatapackloader.common.util.world.Coords;
import mctmods.resourcedatapackloader.common.util.world.TileData;
import mctmods.resourcedatapackloader.common.worldgen.structure.city.plot.RecurrentPlots;
import mctmods.resourcedatapackloader.core.util.math.Turns;

import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.template.TemplateManager;

public class RecurrentVillagePiece extends GroundedVillagePiece {
    private String structure = "";
    private String generation = "";
    private int courses;
    private int seat;
    private Rotation turned = Rotation.NONE;
    private final BlockPos.MutableBlockPos scratch = new BlockPos.MutableBlockPos();

    @SuppressWarnings("unused") public RecurrentVillagePiece() {}

    public RecurrentVillagePiece(StructureVillagePieces.Start start, int type, StructureBoundingBox box, EnumFacing facing, RecurrentPlots.Plot plot, Rotation turned) {
        super(start, type);
        setCoordBaseMode(facing);
        this.boundingBox = box;
        this.structure = plot.structure;
        this.generation = plot.generation;
        this.courses = plot.courses;
        this.seat = plot.seat;
        this.turned = turned;
    }

    @Override protected void writeStructureToNBT(@Nonnull NBTTagCompound tag) {
        super.writeStructureToNBT(tag);
        tag.setString("rdpl_rc_structure", structure);
        tag.setString("rdpl_rc_generation", generation);
        tag.setInteger("rdpl_rc_courses", courses);
        tag.setInteger("rdpl_rc_seat", seat);
        tag.setInteger("rdpl_rc_turn", turned.ordinal());
    }

    @Override protected void readStructureFromNBT(@Nonnull NBTTagCompound tag, @Nonnull TemplateManager templates) {
        super.readStructureFromNBT(tag, templates);
        structure = tag.getString("rdpl_rc_structure");
        generation = tag.getString("rdpl_rc_generation");
        courses = tag.getInteger("rdpl_rc_courses");
        seat = tag.getInteger("rdpl_rc_seat");
        turned = Turns.ALL[tag.getInteger("rdpl_rc_turn") & 3];
    }

    public String structureId() { return structure; }

    public int groundCourses() { return courses; }

    public int footingSink() { return -seat; }

    public static boolean tooShallow(StructureBoundingBox box) { return !canVillageGoDeeper(box); }

    @Override public boolean addComponentParts(@Nonnull World world, @Nonnull Random random, @Nonnull StructureBoundingBox clip) {
        if (structure.isEmpty()) { return true; }
        int[] size = RecurrentPlots.sizeOf(structure);
        if (size == null) { return true; }
        if (!settle(world, clip, seat)) { return true; }
        int wide = size[0];
        int tall = size[1];
        int deep = size[2];
        boolean quarter = Coords.quarter(turned);
        int localWide = quarter ? deep : wide;
        int localDeep = quarter ? wide : deep;
        for (int pass = 0; pass < 2; pass++) {
            for (int lx = 0; lx < localWide; lx++) {
                for (int lz = 0; lz < localDeep; lz++) {
                    int[] source = Coords.toSource(lx, lz, wide, deep, turned);
                    int sx = source[0];
                    int sz = source[1];
                    for (int ly = 0; ly < tall; ly++) {
                        int kind = RecurrentPlots.classify(structure, sx, ly, sz);
                        if (kind == RecurrentPlots.SKIPPED) { continue; }
                        if (kind == RecurrentPlots.OPEN) {
                            if (pass == 0) { setBlockState(world, Blocks.AIR.getDefaultState(), lx, ly, lz, clip); }
                            continue;
                        }
                        if (kind == RecurrentPlots.GROUND) {
                            if (pass == 0) { setBlockState(world, ground(world, lx, ly, lz, sx, sz, tall), lx, ly, lz, clip); }
                            continue;
                        }
                        IBlockState state = RecurrentPlots.stateAt(structure, sx, ly, sz);
                        if (state == null || state.isFullCube() != (pass == 0)) { continue; }
                        setBlockState(world, getBiomeSpecificBlockState(state.withRotation(turned).withMirror(Mirror.LEFT_RIGHT)), lx, ly, lz, clip);
                    }
                }
            }
        }
        for (NBTTagCompound tag : RecurrentPlots.tiles(structure)) {
            BlockPos source = Coords.readPos(tag);
            int sy = source.getY();
            int[] local = Coords.toLocal(source.getX(), source.getZ(), wide, deep, turned);
            int lx = local[0];
            int lz = local[1];
            BlockPos at = new BlockPos(getXWithOffset(lx, lz), getYWithOffset(sy), getZWithOffset(lx, lz));
            if (!clip.isVecInside(at)) { continue; }
            TileEntity tile = world.getTileEntity(at);
            if (tile == null) { continue; }
            tile.readFromNBT(TileData.at(tag, at));
            tile.markDirty();
        }
        return true;
    }

    private IBlockState ground(World world, int lx, int ly, int lz, int sx, int sz, int tall) {
        if (getYWithOffset(ly) < world.getSeaLevel() - 3) { return Blocks.STONE.getDefaultState(); }
        Biome biome = world.getBiome(scratch.setPos(getXWithOffset(lx, lz), 0, getZWithOffset(lx, lz)));
        boolean buried = ly + 1 < tall && RecurrentPlots.classify(structure, sx, ly + 1, sz) == RecurrentPlots.GROUND;
        IBlockState laid = buried ? biome.fillerBlock : biome.topBlock;
        return laid != null ? laid : Blocks.DIRT.getDefaultState();
    }
}
