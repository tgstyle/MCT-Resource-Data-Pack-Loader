package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.SpreadDef;
import mctmods.resourcedatapackloader.content.def.WorldgenDef;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import java.util.Random;
import javax.annotation.Nullable;

public final class ContentSpread {
    private static final int SAFE_OFFSET = 8;

    private ContentSpread() {}

    public static Random regionRandom(World world, int chunkX, int chunkZ) {
        Random random = new Random(world.getSeed());
        long a = (random.nextLong() / 2L) * 2L + 1L;
        long b = (random.nextLong() / 2L) * 2L + 1L;
        random.setSeed(chunkX * a + chunkZ * b ^ world.getSeed());
        return random;
    }

    @Nullable public static BlockPos position(WorldgenDef def, World world, Random random, Random region, int baseX, int baseZ) {
        SpreadDef spread = def.spread;
        switch (spread.type) {
            case SpreadDef.SPRAWL: return sprawl(def, spread, region, baseX, baseZ);
            case SpreadDef.CENTERED: return centered(def, spread, random, baseX, baseZ);
            case SpreadDef.TERRAIN: return terrain(spread, world, random, baseX, baseZ);
            case SpreadDef.CAVERN: return cavern(def, spread, world, random, baseX, baseZ);
            case SpreadDef.SUBMERGED: return submerged(def, world, random, baseX, baseZ);
            default: return even(def, random, baseX, baseZ);
        }
    }

    private static BlockPos even(WorldgenDef def, Random random, int baseX, int baseZ) {
        int span = def.maxHeight - def.minHeight + 1;
        return new BlockPos(baseX + SAFE_OFFSET + random.nextInt(16), def.minHeight + random.nextInt(span), baseZ + SAFE_OFFSET + random.nextInt(16));
    }

    private static BlockPos centered(WorldgenDef def, SpreadDef spread, Random random, int baseX, int baseZ) {
        int y = spread.center;
        if (spread.range > 1) {
            int rolls = Math.max(1, spread.smoothness);
            for (int roll = 0; roll < rolls; roll++) { y += random.nextInt(spread.range); }
            y = Math.round(y - spread.range * (rolls * 0.5F));
        }
        return new BlockPos(baseX + SAFE_OFFSET + random.nextInt(16), MathHelper.clamp(y, def.minHeight, def.maxHeight), baseZ + SAFE_OFFSET + random.nextInt(16));
    }

    private static BlockPos sprawl(WorldgenDef def, SpreadDef spread, Random region, int baseX, int baseZ) {
        int x = baseX + SAFE_OFFSET + density(region, spread.veinDiameter, spread.horizontalDensity);
        int y = def.minHeight + density(region, spread.veinHeight, spread.verticalDensity);
        int z = baseZ + SAFE_OFFSET + density(region, spread.veinDiameter, spread.horizontalDensity);
        return new BlockPos(x, y, z);
    }

    @Nullable private static BlockPos terrain(SpreadDef spread, World world, Random random, int baseX, int baseZ) {
        int x = baseX + SAFE_OFFSET + random.nextInt(16);
        int z = baseZ + SAFE_OFFSET + random.nextInt(16);
        BlockPos top = world.getHeight(new BlockPos(x, 0, z));
        int span = Math.max(1, spread.offsetMax - spread.offsetMin + 1);
        int y = top.getY() + spread.offsetMin + random.nextInt(span);
        return y < ContentPlacer.floorY(world) ? null : new BlockPos(x, y, z);
    }

    @Nullable private static BlockPos cavern(WorldgenDef def, SpreadDef spread, World world, Random random, int baseX, int baseZ) {
        int x = baseX + SAFE_OFFSET + random.nextInt(16);
        int z = baseZ + SAFE_OFFSET + random.nextInt(16);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int y = def.maxHeight; y > def.minHeight; y--) {
            pos.setPos(x, y, z);
            if (!world.isAirBlock(pos)) { continue; }
            pos.setPos(x, spread.ceiling ? y + 1 : y - 1, z);
            if (world.isAirBlock(pos)) { continue; }
            return new BlockPos(x, spread.ceiling ? y + 1 : y - 1, z);
        }
        return null;
    }

    @Nullable private static BlockPos submerged(WorldgenDef def, World world, Random random, int baseX, int baseZ) {
        int x = baseX + SAFE_OFFSET + random.nextInt(16);
        int z = baseZ + SAFE_OFFSET + random.nextInt(16);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int y = def.maxHeight; y > def.minHeight; y--) {
            pos.setPos(x, y, z);
            if (!world.getBlockState(pos).getMaterial().isLiquid()) { continue; }
            pos.setPos(x, y - 1, z);
            if (world.getBlockState(pos).getMaterial().isLiquid() || world.isAirBlock(pos)) { continue; }
            return new BlockPos(x, y - 1, z);
        }
        return null;
    }

    private static int density(Random random, int distance, int percent) {
        float scaled = percent * 0.01F * (distance >> 1);
        int rolls = Math.max(1, (int) scaled);
        int bound = Math.max(1, distance / rolls);
        int total = 0;
        for (int roll = 0; roll < rolls; roll++) { total += random.nextInt(bound); }
        return total;
    }

}
