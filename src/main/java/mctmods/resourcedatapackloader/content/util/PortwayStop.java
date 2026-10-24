package mctmods.resourcedatapackloader.content.util;

import mctmods.resourcedatapackloader.content.block.ContentPortwayBlock;
import mctmods.resourcedatapackloader.content.def.PortwayDef;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import javax.annotation.Nullable;

public record PortwayStop(ResourceKey<Level> dimension, BlockPos pos, String name, String block, long found) {
    private static final String DIMENSION = "dim";
    private static final String POSITION = "pos";
    private static final String NAME = "name";
    private static final String BLOCK = "block";
    private static final String FOUND = "found";

    public PortwayStop(ResourceKey<Level> dimension, BlockPos pos, String name, String block) { this(dimension, pos, name, block, 0L); }

    PortwayStop fresh() { return new PortwayStop(dimension, pos, name, block, System.currentTimeMillis()); }

    PortwayStop keeping(PortwayStop old) { return new PortwayStop(dimension, pos, name, block, old.found); }

    boolean differs(PortwayStop other) { return !name.equals(other.name) || !block.equals(other.block); }

    public record Key(ResourceKey<Level> dimension, BlockPos pos) {}

    public Key key() { return new Key(dimension, pos); }

    public boolean at(ResourceKey<Level> where, BlockPos spot) { return dimension.equals(where) && pos.equals(spot); }

    public boolean same(PortwayStop other) { return at(other.dimension, other.pos); }

    @Nullable public PortwayDef def() {
        ResourceLocation id = ResourceLocation.tryParse(block);
        return id != null && ForgeRegistries.BLOCKS.getValue(id) instanceof ContentPortwayBlock portway ? portway.settings() : null;
    }

    public String describe() { return Portways.dimensionName(dimension) + " " + pos.getX() + " " + pos.getY() + " " + pos.getZ(); }

    public CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        tag.putString(DIMENSION, dimension.location().toString());
        tag.putLong(POSITION, pos.asLong());
        tag.putString(NAME, name);
        tag.putString(BLOCK, block);
        if (found > 0L) { tag.putLong(FOUND, found); }
        return tag;
    }

    @Nullable public static PortwayStop read(CompoundTag tag) {
        ResourceLocation where = ResourceLocation.tryParse(tag.getString(DIMENSION));
        if (where == null) { return null; }
        return new PortwayStop(ResourceKey.create(Registries.DIMENSION, where), BlockPos.of(tag.getLong(POSITION)), tag.getString(NAME), tag.getString(BLOCK), tag.getLong(FOUND));
    }
}
