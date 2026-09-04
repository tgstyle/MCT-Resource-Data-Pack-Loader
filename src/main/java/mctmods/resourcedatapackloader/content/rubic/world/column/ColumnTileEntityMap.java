package mctmods.resourcedatapackloader.content.rubic.world.column;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IColumn;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.util.Coords;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import javax.annotation.Nonnull;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;

public class ColumnTileEntityMap implements Map<BlockPos, TileEntity> {
    private final IColumn column;

    public ColumnTileEntityMap(IColumn column) { this.column = column; }

    @Override public int size() {
        int total = 0;
        for (ICube cube : column.getLoadedCubes()) { total += cube.getTileEntityMap().size(); }
        return total;
    }

    @Override public boolean isEmpty() {
        for (ICube cube : column.getLoadedCubes()) {
            if (!cube.getTileEntityMap().isEmpty()) { return false; }
        }
        return true;
    }

    private <T> Iterator<T> concat(Function<Map<BlockPos, TileEntity>, Iterator<T>> part) {
        return new Iterator<T>() {
            final Iterator<? extends ICube> cubes = column.getLoadedCubes().iterator();
            Iterator<T> curIt = !cubes.hasNext() ? null : part.apply(cubes.next().getTileEntityMap());
            T nextVal;
            @Override public boolean hasNext() {
                if (nextVal != null) { return true; }
                if (curIt == null) { return false; }
                while (!curIt.hasNext() && cubes.hasNext()) { curIt = part.apply(cubes.next().getTileEntityMap()); }
                if (!curIt.hasNext()) { return false; }
                nextVal = curIt.next();
                return true;
            }
            @Override public T next() {
                if (hasNext()) {
                    T next = nextVal;
                    nextVal = null;
                    return next;
                }
                throw new NoSuchElementException();
            }
        };
    }

    @Override public boolean containsKey(Object o) {
        if (!(o instanceof BlockPos)) { return false; }
        BlockPos pos = (BlockPos) o;
        int y = Coords.blockToCube(pos.getY());
        ICube cube = column.getCube(y);
        return cube.getTileEntityMap().containsKey(o);
    }

    @Override public boolean containsValue(Object o) {
        if (!(o instanceof TileEntity)) { return false; }
        BlockPos pos = ((TileEntity) o).getPos();
        int y = Coords.blockToCube(pos.getY());
        ICube cube = column.getLoadedCube(y);
        assert cube != null : "Cube is null but tile entity in it exists!";
        return cube.getTileEntityMap().containsValue(o);
    }

    @Nullable @Override public TileEntity get(Object o) {
        if (!(o instanceof BlockPos)) { return null; }
        BlockPos pos = (BlockPos) o;
        int y = Coords.blockToCube(pos.getY());
        ICube cube = column.getCube(y);
        return cube.getTileEntityMap().get(o);
    }

    @Override @Nullable public TileEntity put(BlockPos blockPos, TileEntity tileEntity) {
        int y = Coords.blockToCube(blockPos.getY());
        ICube cube = column.getCube(y);
        return cube.getTileEntityMap().put(blockPos, tileEntity);
    }

    @Nullable @Override public TileEntity remove(Object o) {
        if (!(o instanceof BlockPos)) { return null; }
        BlockPos pos = (BlockPos) o;
        int y = Coords.blockToCube(pos.getY());
        ICube cube = column.getLoadedCube(y);
        return cube == null ? null : cube.getTileEntityMap().remove(pos);
    }

    @Override public void putAll(Map<? extends BlockPos, ? extends TileEntity> map) { map.forEach(this::put); }

    @Override public void clear() { throw new UnsupportedOperationException(); }

    @Override @Nonnull public Set<BlockPos> keySet() {
        return new AbstractSet<BlockPos>() {
            @Override public int size() { return ColumnTileEntityMap.this.size(); }
            @Override public boolean isEmpty() { return ColumnTileEntityMap.this.isEmpty(); }
            @Override public boolean contains(Object o) { return ColumnTileEntityMap.this.containsKey(o); }
            @Override @Nonnull public Iterator<BlockPos> iterator() { return concat(map -> map.keySet().iterator()); }
            @Override public boolean remove(Object o) { return ColumnTileEntityMap.this.remove(o) != null; }
            @Override public void clear() { throw new UnsupportedOperationException(); }
        };
    }

    @Override @Nonnull public Collection<TileEntity> values() {
        return new AbstractCollection<TileEntity>() {
            @Override public int size() { return ColumnTileEntityMap.this.size(); }
            @Override public boolean isEmpty() { return ColumnTileEntityMap.this.isEmpty(); }
            @Override public boolean contains(Object o) { return ColumnTileEntityMap.this.containsValue(o); }
            @Override @Nonnull public Iterator<TileEntity> iterator() { return concat(map -> map.values().iterator()); }
            @Override public boolean add(TileEntity tileEntity) { return ColumnTileEntityMap.this.put(tileEntity.getPos(), tileEntity) == null; }
            @Override public boolean remove(Object o) {
                if (!(o instanceof TileEntity)) { return false; }
                TileEntity te = (TileEntity) o;
                return ColumnTileEntityMap.this.remove(te.getPos(), te);
            }
            @Override public void clear() { throw new UnsupportedOperationException(); }
        };
    }

    @Override @Nonnull public Set<Entry<BlockPos, TileEntity>> entrySet() {
        return new AbstractSet<Entry<BlockPos, TileEntity>>() {
            @Override public int size() { return ColumnTileEntityMap.this.size(); }
            @Override public boolean isEmpty() { return ColumnTileEntityMap.this.isEmpty(); }
            @Override public boolean contains(Object o) { return ColumnTileEntityMap.this.containsKey(o); }
            @Override @Nonnull public Iterator<Entry<BlockPos, TileEntity>> iterator() { return concat(map -> map.entrySet().iterator()); }
            @Override public boolean remove(Object o) { return ColumnTileEntityMap.this.remove(o) != null; }
            @Override public void clear() { throw new UnsupportedOperationException(); }
        };
    }
}
