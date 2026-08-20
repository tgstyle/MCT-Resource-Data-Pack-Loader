package mctmods.resourcedatapackloader.content.rubic.world.interfaces;


public interface IHeightMap {
    void onOpacityChange(int localX, int blockY, int localZ, int opacity);

    int getTopBlockY(int localX, int localZ);

    @Deprecated int getTopBlockYBelow(int localX, int localZ, int blockY);

    final class HeightMap {
        private final int[] data;

        public HeightMap(int[] heightmap) { this.data = heightmap; }

        public int get(int index) { return data[index] - 1; }

        public void set(int index, int value) { data[index] = value + 1; }

        public void increment(int index) { data[index]++; }

        public void decrement(int index) { data[index]--; }
    }
}
