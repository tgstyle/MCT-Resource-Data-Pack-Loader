package mctmods.resourcedatapackloader.content.interfaces;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public interface IContentShape {
    boolean generate(World world, Random random, BlockPos origin);

    default boolean places(World world, IBlockState wood, List<IBlockState> canopies) { return false; }

    default boolean places(World world, IBlockState wood, List<IBlockState> canopies, @Nullable String structure) { return places(world, wood, canopies); }

    default int reach(World world) { return 0; }

    @Nullable default Footprint footprint(World world, Random random, int x, int z, Predicate<Footprint> refused) {
        Footprint box = new Footprint(x, z, x, z, null);
        return refused.test(box) ? null : box;
    }

    final class Footprint {
        public final int minX;
        public final int minZ;
        public final int maxX;
        public final int maxZ;
        @Nullable public final String structure;

        public Footprint(int minX, int minZ, int maxX, int maxZ, @Nullable String structure) {
            this.minX = minX;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxZ = maxZ;
            this.structure = structure;
        }

        public boolean covers(int x, int z) { return x >= minX && z >= minZ && x <= maxX && z <= maxZ; }
    }
}
