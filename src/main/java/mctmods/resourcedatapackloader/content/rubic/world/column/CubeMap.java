package mctmods.resourcedatapackloader.content.rubic.world.column;

import mctmods.resourcedatapackloader.content.rubic.world.cube.Cube;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CubeMap implements Iterable<Cube> {
    @Nonnull private final List<Cube> cubes = new ArrayList<>();

    @Nullable public Cube remove(int cubeY) {
        int index = binarySearch(cubeY);
        return index < cubes.size() && cubes.get(index).getY() == cubeY ? cubes.remove(index) : null;
    }

    public void put(Cube cube) {
        int searchIndex = binarySearch(cube.getY());
        if (this.contains(cube.getY(), searchIndex)) { throw new IllegalArgumentException("Cube at " + cube.getY() + " already exists!"); }
        cubes.add(searchIndex, cube);
    }

    public Iterable<Cube> cubes(int startY, int endY) {
        boolean reverse = false;
        if (startY > endY) {
            int i = startY;
            startY = endY;
            endY = i;
            reverse = true;
        }
        int bottom = binarySearch(startY);
        int top = binarySearch(endY + 1);
        if (bottom < cubes.size() && top <= cubes.size()) { return reverse ? Lists.reverse(cubes.subList(bottom, top)) : cubes.subList(bottom, top); }
        else { return Collections.emptyList(); }
    }

    private boolean contains(int cubeY, int searchIndex) { return searchIndex < cubes.size() && cubes.get(searchIndex).getY() == cubeY; }

    @Override @NotNull public Iterator<Cube> iterator() { return cubes.iterator(); }

    public Collection<Cube> all() { return cubes; }

    public boolean isEmpty() { return cubes.isEmpty(); }

    private int binarySearch(int cubeY) {
        int start = 0;
        int end = cubes.size() - 1;
        int mid;
        while (start <= end) {
            mid = start + end >>> 1;
            int at = cubes.get(mid).getY();
            if (at < cubeY) { start = mid + 1; }
            else if (at > cubeY) { end = mid - 1; }
            else { return mid; }
        }
        return start;
    }
}
