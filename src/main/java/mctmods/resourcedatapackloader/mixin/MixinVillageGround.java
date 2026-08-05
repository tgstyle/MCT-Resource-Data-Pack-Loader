package mctmods.resourcedatapackloader.mixin;

import mctmods.resourcedatapackloader.content.worldgen.ContentBeard;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Arrays;

@Mixin(StructureVillagePieces.Village.class)
public abstract class MixinVillageGround extends StructureComponent {
    @Unique private String rdpl$bedFrom = "";
    @Unique private int rdpl$bedGap = 0;

    @SuppressWarnings({"ConstantConditions"}) @Inject(method = "getAverageGroundLevel", at = @At("RETURN"), cancellable = true)
    private void rdpl$leanLow(World worldIn, StructureBoundingBox structurebb, CallbackInfoReturnable<Integer> cir) {
        if (!ContentBeard.wanted()) { return; }

        int found = cir.getReturnValueI();
        if (found < 0) { return; }

        StructureBoundingBox box = getBoundingBox();
        if ((Object) this instanceof StructureVillagePieces.Well || (Object) this instanceof StructureVillagePieces.Field1 || (Object) this instanceof StructureVillagePieces.Field2) { return; }

        int bed = rdpl$roadBedBeside(worldIn, box);
        if (bed != Integer.MIN_VALUE) {
            int seated = bed + 1;
            if (seated == found) { return; }

            cir.setReturnValue(seated);
            ContentLog.LOGGER.debug("{} at {}, {} set to y {}, one above the road {} block(s) away so its doorstep stairs sit on it, read from {}, instead of y {}", getClass().getSimpleName(), box.minX, box.minZ, seated, rdpl$bedGap, rdpl$bedFrom, found);
            return;
        }
        ContentLog.LOGGER.debug("{} at {}, {} found no road bed to seat against, so vanilla's y {} stands unless it leans", getClass().getSimpleName(), box.minX, box.minZ, found);
        int lowest = ContentBeard.lowestIn(worldIn, box.minX, box.minZ, box.maxX, box.maxZ, structurebb);
        if (lowest == Integer.MAX_VALUE || found <= lowest + 3) { return; }

        int leaned = rdpl$roadClamped(lowest + 3, box);
        if (found <= leaned) { return; }

        cir.setReturnValue(leaned);
        ContentLog.LOGGER.debug("{} at {}, {} leaned from y {} down to y {} over its low side", getClass().getSimpleName(), box.minX, box.minZ, found, leaned);
    }

    @Unique private int rdpl$roadBedBeside(World worldIn, StructureBoundingBox box) {
        StructureStart start = ContentBeard.current();
        if (start == null) { return Integer.MIN_VALUE; }

        StructureComponent self = this;
        BlockPos.MutableBlockPos at = new BlockPos.MutableBlockPos();
        int best = Integer.MIN_VALUE;
        int nearest = Integer.MAX_VALUE;
        for (StructureComponent other : start.getComponents()) {
            if (other == self || !(other instanceof StructureVillagePieces.Path)) { continue; }

            StructureBoundingBox road = other.getBoundingBox();
            int gap = Math.max(Math.max(road.minX - box.maxX, box.minX - road.maxX), Math.max(road.minZ - box.maxZ, box.minZ - road.maxZ));
            if (gap > 4 || gap > nearest) { continue; }

            int laid = rdpl$laidBed(worldIn, road, box, at);
            if (laid == Integer.MIN_VALUE) { continue; }
            if (gap == nearest && laid <= best) { continue; }

            best = laid;
            nearest = gap;
            rdpl$bedFrom = "the road itself";
            rdpl$bedGap = gap;
        }
        if (best != Integer.MIN_VALUE) { return best; }

        for (StructureComponent other : start.getComponents()) {
            if (other == self || !(other instanceof StructureVillagePieces.Path)) { continue; }

            StructureBoundingBox road = other.getBoundingBox();
            int gap = Math.max(Math.max(road.minX - box.maxX, box.minX - road.maxX), Math.max(road.minZ - box.maxZ, box.minZ - road.maxZ));
            if (gap > 4 || gap > nearest) { continue; }

            int graded = rdpl$gradedBed(worldIn, road, box, at);
            if (graded == Integer.MIN_VALUE) { continue; }
            if (gap == nearest && graded <= best) { continue; }

            best = graded;
            nearest = gap;
            rdpl$bedFrom = "the ground the road will grade onto";
            rdpl$bedGap = gap;
        }
        return best;
    }

    @Unique private int rdpl$gradedBed(World worldIn, StructureBoundingBox road, StructureBoundingBox box, BlockPos.MutableBlockPos at) {
        boolean alongX = road.maxX - road.minX >= road.maxZ - road.minZ;
        int rowLeast = Math.max(alongX ? road.minX : road.minZ, (alongX ? box.minX : box.minZ) - 1);
        int rowMost = Math.min(alongX ? road.maxX : road.maxZ, (alongX ? box.maxX : box.maxZ) + 1);
        int acrossLeast = alongX ? road.minZ : road.minX;
        int acrossMost = alongX ? road.maxZ : road.maxX;
        int[] beds = new int[81];
        int count = 0;
        for (int row = rowLeast; row <= rowMost && count < beds.length; row++) {
            int lowest = Integer.MAX_VALUE;
            for (int across = acrossLeast; across <= acrossMost; across++) {
                int x = alongX ? row : across;
                int z = alongX ? across : row;
                if (!worldIn.isChunkGeneratedAt(x >> 4, z >> 4)) { continue; }

                at.setPos(x, 64, z);
                int top = worldIn.getTopSolidOrLiquidBlock(at).getY() - 1;
                if (top < worldIn.getSeaLevel() - 1) { continue; }
                if (top < lowest) { lowest = top; }
            }
            if (lowest != Integer.MAX_VALUE) { beds[count++] = lowest; }
        }
        if (count == 0) { return Integer.MIN_VALUE; }

        Arrays.sort(beds, 0, count);
        return beds[count / 2];
    }

    @Unique private int rdpl$laidBed(World worldIn, StructureBoundingBox road, StructureBoundingBox box, BlockPos.MutableBlockPos at) {
        int closest = Integer.MAX_VALUE;
        for (int x = Math.max(road.minX, box.minX - 4); x <= Math.min(road.maxX, box.maxX + 4); x++) {
            for (int z = Math.max(road.minZ, box.minZ - 4); z <= Math.min(road.maxZ, box.maxZ + 4); z++) {
                if (rdpl$bedAt(worldIn, road, at, x, z) == Integer.MIN_VALUE) { continue; }

                int reach = rdpl$reach(box, x, z);
                if (reach < closest) { closest = reach; }
            }
        }
        if (closest == Integer.MAX_VALUE) { return Integer.MIN_VALUE; }

        int[] beds = new int[81];
        int count = 0;
        for (int x = Math.max(road.minX, box.minX - 4); x <= Math.min(road.maxX, box.maxX + 4); x++) {
            for (int z = Math.max(road.minZ, box.minZ - 4); z <= Math.min(road.maxZ, box.maxZ + 4); z++) {
                if (rdpl$reach(box, x, z) > closest) { continue; }

                int bed = rdpl$bedAt(worldIn, road, at, x, z);
                if (bed == Integer.MIN_VALUE || count >= beds.length) { continue; }

                beds[count++] = bed;
            }
        }
        if (count == 0) { return Integer.MIN_VALUE; }

        Arrays.sort(beds, 0, count);
        return beds[count / 2];
    }

    @Unique private int rdpl$bedAt(World worldIn, StructureBoundingBox road, BlockPos.MutableBlockPos at, int x, int z) {
        if (!worldIn.isChunkGeneratedAt(x >> 4, z >> 4)) { return Integer.MIN_VALUE; }

        for (int y = road.maxY + 8; y >= road.minY - 4; y--) {
            at.setPos(x, y, z);
            Block held = worldIn.getBlockState(at).getBlock();
            if (held == Blocks.GRASS_PATH || held == Blocks.GRAVEL || held == Blocks.PLANKS) { return y; }
        }
        return Integer.MIN_VALUE;
    }

    @Unique private int rdpl$reach(StructureBoundingBox box, int x, int z) {
        int outX = Math.max(Math.max(box.minX - x, x - box.maxX), 0);
        int outZ = Math.max(Math.max(box.minZ - z, z - box.maxZ), 0);
        return outX + outZ;
    }

    @Unique private int rdpl$roadClamped(int leaned, StructureBoundingBox box) {
        StructureStart start = ContentBeard.current();
        if (start == null) { return leaned; }

        StructureComponent self = this;
        for (StructureComponent other : start.getComponents()) {
            if (other == self || !(other instanceof StructureVillagePieces.Path)) { continue; }

            StructureBoundingBox road = other.getBoundingBox();
            if (road.minX - 4 > box.maxX || box.minX - 4 > road.maxX || road.minZ - 4 > box.maxZ || box.minZ - 4 > road.maxZ) { continue; }
            if (road.minY > leaned) { leaned = road.minY; }
        }
        return leaned;
    }
}
