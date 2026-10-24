package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.content.block.ContentBlockPortway;
import mctmods.resourcedatapackloader.content.def.PortwayDef;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import javax.annotation.Nullable;

public final class PortwayStop {
    private static final String DIMENSION = "dim";
    private static final String POSITION = "pos";
    private static final String NAME = "name";
    private static final String BLOCK = "block";
    private static final String FOUND = "found";
    public final int dimension;
    public final BlockPos pos;
    public final String name;
    public final String block;
    public final long found;

    public PortwayStop(int dimension, BlockPos pos, String name, String block, long found) {
        this.dimension = dimension;
        this.pos = pos;
        this.name = name;
        this.block = block;
        this.found = found;
    }

    public PortwayStop(int dimension, BlockPos pos, String name, String block) { this(dimension, pos, name, block, 0L); }

    public PortwayStop(int dimension, BlockPos pos) { this(dimension, pos, "", ""); }

    static PortwayStop read(NBTTagCompound tag) { return new PortwayStop(tag.getInteger(DIMENSION), BlockPos.fromLong(tag.getLong(POSITION)), tag.getString(NAME), tag.getString(BLOCK), tag.getLong(FOUND)); }

    NBTTagCompound write() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger(DIMENSION, dimension);
        tag.setLong(POSITION, pos.toLong());
        tag.setString(NAME, name);
        tag.setString(BLOCK, block);
        if (found > 0L) { tag.setLong(FOUND, found); }
        return tag;
    }

    PortwayStop fresh() { return new PortwayStop(dimension, pos, name, block, System.currentTimeMillis()); }

    PortwayStop keeping(PortwayStop old) { return new PortwayStop(dimension, pos, name, block, old.found); }

    @Nullable public PortwayDef def() {
        if (block.isEmpty()) { return null; }
        Block found = Block.REGISTRY.getObject(new ResourceLocation(block));
        return found instanceof ContentBlockPortway ? ((ContentBlockPortway) found).settings() : null;
    }

    @Override public boolean equals(Object other) { return other instanceof PortwayStop && ((PortwayStop) other).dimension == dimension && ((PortwayStop) other).pos.equals(pos); }

    @Override public int hashCode() { return 31 * dimension + pos.hashCode(); }
}
