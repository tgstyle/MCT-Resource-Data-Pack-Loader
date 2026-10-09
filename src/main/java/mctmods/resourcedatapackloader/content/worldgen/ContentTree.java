package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.content.def.AmountDef;
import mctmods.resourcedatapackloader.content.def.ShapeDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentShape;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public final class ContentTree implements IContentShape {
    private final AmountDef count;
    private final AmountDef height;
    private final Set<Block> surface;
    private final int scatterX;
    private final int scatterZ;
    private final int drift;
    @Nullable private final IBlockState log;
    @Nullable private final IBlockState leaves;
    private final boolean templated;
    private final ContentImprint imprint;

    public ContentTree(AmountDef count, ShapeDef shape, Set<Block> surface, ResourceLocation key, ContentImprint imprint) {
        this.count = count;
        this.height = shape.height;
        this.surface = surface;
        this.scatterX = shape.scatterX;
        this.scatterZ = shape.scatterZ;
        this.drift = Math.max(1, shape.scatterY);
        this.templated = !shape.structures.isEmpty() || !shape.structure.isEmpty();
        this.imprint = imprint;
        this.log = ContentStates.parse(shape.log, key);
        this.leaves = ContentStates.parse(shape.leaves, key);
        if (!templated && (this.log == null || this.leaves == null)) { ContentLog.LOGGER.error("Worldgen {} grows a tree but its log '{}' or leaves '{}' are not registered, so nothing generates", key, shape.log, shape.leaves); }
    }

    @Override public boolean places(World world, IBlockState wood, List<IBlockState> canopies) { return places(world, wood, canopies, null); }

    @Override public boolean places(World world, IBlockState wood, List<IBlockState> canopies, @Nullable String structure) {
        if (templated) { return imprint.places(world, wood, canopies, structure); }
        if (log == null || leaves == null || !ContentStates.alike(log, wood)) { return false; }
        for (IBlockState canopy : canopies) {
            if (ContentStates.alike(leaves, canopy)) { return true; }
        }
        return canopies.isEmpty();
    }

    @Override public int reach(World world) { return Math.max(scatterX, scatterZ) + (templated ? imprint.reach(world) : 0); }

    @Override public Footprint footprint(World world, Random random, int x, int z, Predicate<Footprint> refused) {
        int span = templated ? imprint.span(world) : 0;
        int roamX = Math.max(0, scatterX - 1) + span;
        int roamZ = Math.max(0, scatterZ - 1) + span;
        return new Footprint(x - roamX, z - roamZ, x + roamX, z + roamZ, null);
    }

    @Override public boolean generate(World world, Random random, BlockPos origin) {
        if (!templated && (log == null || leaves == null)) { return false; }
        boolean placed = false;
        int attempts = count.pick(random);
        for (int attempt = 0; attempt < attempts; attempt++) {
            int x = origin.getX() + ContentPlacer.scatter(random, scatterX);
            int z = origin.getZ() + ContentPlacer.scatter(random, scatterZ);
            BlockPos top = world.getHeight(new BlockPos(x, 0, z));
            if (!world.isAreaLoaded(top, 3)) { continue; }
            if (Math.abs(top.getY() - origin.getY()) > drift) { continue; }
            if (!surface.isEmpty() && !surface.contains(world.getBlockState(top.down()).getBlock())) { continue; }
            if (!world.isAirBlock(top)) { continue; }
            if (templated) { placed |= imprint.generate(world, random, top); }
            else {
                ContentTreeGenerator tree = new ContentTreeGenerator(true, Math.max(1, height.pick(random)), log, leaves, surface);
                placed |= tree.generate(world, random, top);
            }
        }
        return placed;
    }
}
