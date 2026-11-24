package mctmods.resourcedatapackloader.common.portways;

import mctmods.resourcedatapackloader.common.blocks.portal.ContentPortwayBlock;
import mctmods.resourcedatapackloader.common.pack.definitions.portals.PortwayDef;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

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

    static List<PortwayStop> readAll(NBTTagList tags) {
        List<PortwayStop> stops = new ArrayList<>();
        for (int index = 0; index < tags.tagCount(); index++) { stops.add(read(tags.getCompoundTagAt(index))); }
        return stops;
    }

    static NBTTagList writeAll(List<PortwayStop> stops) {
        NBTTagList tags = new NBTTagList();
        for (PortwayStop stop : stops) { tags.appendTag(stop.write()); }
        return tags;
    }

    PortwayStop fresh() { return new PortwayStop(dimension, pos, name, block, System.currentTimeMillis()); }

    PortwayStop keeping(PortwayStop old) { return new PortwayStop(dimension, pos, name, block, old.found); }

    boolean refreshIn(List<PortwayStop> stops) {
        int at = stops.indexOf(this);
        if (at < 0 || stops.get(at).name.equals(name) && stops.get(at).block.equals(block)) { return false; }
        stops.set(at, keeping(stops.get(at)));
        return true;
    }

    @Nullable public PortwayDef def() {
        if (block.isEmpty()) { return null; }
        Block found = Block.REGISTRY.getObject(new ResourceLocation(block));
        return found instanceof ContentPortwayBlock ? ((ContentPortwayBlock) found).settings() : null;
    }

    @Override public boolean equals(Object other) { return other instanceof PortwayStop && ((PortwayStop) other).dimension == dimension && ((PortwayStop) other).pos.equals(pos); }

    @Override public int hashCode() { return 31 * dimension + pos.hashCode(); }
}
