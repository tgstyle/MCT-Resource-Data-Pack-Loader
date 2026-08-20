package mctmods.resourcedatapackloader.content.rubic.world.column;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IColumn;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.ICube;
import mctmods.resourcedatapackloader.util.Coords;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.annotation.Nullable;

public class ColumnTileEntityMap implements Map<BlockPos, TileEntity> {
    private final IColumn column;

    public ColumnTileEntityMap(IColumn column) { this.column = column; }

    @Override public int size() {
        return column.getLoadedCubes().stream()
                .map(ICube::getTileEntityMap)
                .map(Map::size)
                .reduce(Integer::sum).orElse(0);
    }

    @Override public boolean isEmpty() {
        return  column.getLoadedCubes().stream()
                .map(ICube::getTileEntityMap)
                .allMatch(Map::isEmpty);
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

    @Override @NotNull public Set<BlockPos> keySet() {
        return new AbstractSet<BlockPos>() {
            @Override public int size() { return ColumnTileEntityMap.this.size(); }
            @Override public boolean isEmpty() { return ColumnTileEntityMap.this.isEmpty(); }
            @Override public boolean contains(Object o) { return ColumnTileEntityMap.this.containsKey(o); }
            @Override @NotNull public Iterator<BlockPos> iterator() {
                return new Iterator<BlockPos>() {
                    final Iterator<? extends ICube> cubes = column.getLoadedCubes().iterator();
                    Iterator<BlockPos> curIt = !cubes.hasNext() ? null : cubes.next().getTileEntityMap().keySet().iterator();
                    BlockPos nextVal;
                    @Override public boolean hasNext() {
                        if (nextVal != null) { return true; }
                        if (curIt == null) { return false; }
                        while (!curIt.hasNext() && cubes.hasNext()) { curIt = cubes.next().getTileEntityMap().keySet().iterator(); }
                        if (!curIt.hasNext()) { return false; }
                        nextVal = curIt.next();
                        return true;
                    }
                    @Override public BlockPos next() {
                        if (hasNext()) {
                            BlockPos next = nextVal;
                            nextVal = null;
                            return next;
                        }
                        throw new NoSuchElementException();
                    }
                };
            }
            @Override public boolean remove(Object o) { return ColumnTileEntityMap.this.remove(o) != null; }
            @Override public void clear() { throw new UnsupportedOperationException(); }
        };
    }

    @Override @NotNull public Collection<TileEntity> values() {
        return new AbstractCollection<TileEntity>() {
            @Override public int size() { return ColumnTileEntityMap.this.size(); }
            @Override public boolean isEmpty() { return ColumnTileEntityMap.this.isEmpty(); }
            @Override public boolean contains(Object o) { return ColumnTileEntityMap.this.containsValue(o); }
            @Override @NotNull public Iterator<TileEntity> iterator() {
                return new Iterator<TileEntity>() {
                    final Iterator<? extends ICube> cubes = column.getLoadedCubes().iterator();
                    Iterator<TileEntity> curIt = !cubes.hasNext() ? null : cubes.next().getTileEntityMap().values().iterator();
                    TileEntity nextVal;
                    @Override public boolean hasNext() {
                        if (nextVal != null) { return true; }
                        if (curIt == null) { return false; }
                        while (!curIt.hasNext() && cubes.hasNext()) { curIt = cubes.next().getTileEntityMap().values().iterator(); }
                        if (!curIt.hasNext()) { return false; }
                        nextVal = curIt.next();
                        return true;
                    }
                    @Override public TileEntity next() {
                        if (hasNext()) {
                            TileEntity next = nextVal;
                            nextVal = null;
                            return next;
                        }
                        throw new NoSuchElementException();
                    }
                };
            }
            @Override public boolean add(TileEntity tileEntity) { return ColumnTileEntityMap.this.put(tileEntity.getPos(), tileEntity) == null; }
            @Override public boolean remove(Object o) {
                if (!(o instanceof TileEntity)) { return false; }
                TileEntity te = (TileEntity) o;
                return ColumnTileEntityMap.this.remove(te.getPos(), te);
            }
            @Override public void clear() { throw new UnsupportedOperationException(); }
        };
    }

    @Override @NotNull public Set<Entry<BlockPos, TileEntity>> entrySet() {
        return new AbstractSet<Entry<BlockPos, TileEntity>>() {
            @Override public int size() { return ColumnTileEntityMap.this.size(); }
            @Override public boolean isEmpty() { return ColumnTileEntityMap.this.isEmpty(); }
            @Override public boolean contains(Object o) { return ColumnTileEntityMap.this.containsKey(o); }
            @Override @NotNull public Iterator<Entry<BlockPos, TileEntity>> iterator() {
                return new Iterator<Entry<BlockPos, TileEntity>>() {
                    final Iterator<? extends ICube> cubes = column.getLoadedCubes().iterator();
                    Iterator<Entry<BlockPos, TileEntity>> curIt = !cubes.hasNext() ? null : cubes.next().getTileEntityMap().entrySet().iterator();
                    Entry<BlockPos, TileEntity> nextVal;
                    @Override public boolean hasNext() {
                        if (nextVal != null) { return true; }
                        if (curIt == null) { return false; }
                        while (!curIt.hasNext() && cubes.hasNext()) { curIt = cubes.next().getTileEntityMap().entrySet().iterator(); }
                        if (!curIt.hasNext()) { return false; }
                        nextVal = curIt.next();
                        return true;
                    }
                    @Override public Entry<BlockPos, TileEntity> next() {
                        if (hasNext()) {
                            Entry<BlockPos, TileEntity> e = nextVal;
                            nextVal = null;
                            return e;
                        }
                        throw new NoSuchElementException();
                    }
                };
            }
            @Override public boolean remove(Object o) { return ColumnTileEntityMap.this.remove(o) != null; }
            @Override public void clear() { throw new UnsupportedOperationException(); }
        };
    }
}
