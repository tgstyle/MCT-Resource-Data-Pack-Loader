package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.AmountDef;
import mctmods.resourcedatapackloader.content.def.ShapeDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentChunkShape;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Hashes;
import mctmods.resourcedatapackloader.util.Noise3D;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public final class ContentOreVein implements IContentChunkShape {
    public static final float RICH = 0.88F;
    public static final float NORMAL = 0.4F;
    public static final int REACH = 24;
    private static final int FULL = 16;
    private static final int WANING = 20;
    private static final float TUBE_WALL = 5.0F;
    private final ShapeDef shape;
    private final int size;
    private final AmountDef attempts;
    private final int minHeight;
    private final int maxHeight;
    private final ResourceLocation key;
    private final long salt;
    private final Noise3D noise;
    private final Noise3D warp;
    private final Noise3D wobble;
    @Nullable private final BlockState rich;
    @Nullable private final BlockState poor;

    public ContentOreVein(ShapeDef shape, AmountDef size, AmountDef attempts, int minHeight, int maxHeight, ResourceLocation key, @Nullable BlockState rich, @Nullable BlockState poor) {
        this.shape = shape;
        this.size = Math.max(1, size.most());
        this.attempts = attempts;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.key = key;
        this.salt = key.toString().hashCode() * 0x9E3779B97F4A7C15L;
        this.noise = new Noise3D(salt);
        this.warp = new Noise3D(salt - 1);
        this.wobble = new Noise3D(salt + 1000);
        this.rich = rich;
        this.poor = poor;
    }

    public record Vein(int x, int y, int z) {
        public BlockPos pos() { return new BlockPos(x, y, z); }
    }

    @Override public boolean generate(ContentPlacer placer, RandomSource random, BlockPos origin) { return false; }

    public List<Vein> veinsOf(long worldSeed, int floor, int ceiling, int chunkX, int chunkZ) {
        List<Vein> found = new ArrayList<>();
        int lowest = Math.max(floor, minHeight);
        int highest = Math.min(ceiling - 1, maxHeight);
        if (highest < lowest) { return found; }
        RandomSource roll = RandomSource.create(worldSeed ^ salt ^ (chunkX * 341873128712L + chunkZ * 132897987541L));
        if (shape.rarity() > 0 && !shape.perChunk() && roll.nextInt(shape.rarity()) != 0) { return found; }
        int tries = shape.rarity() > 0 && shape.perChunk() ? shape.rarity() : attempts.pick(roll);
        int band = highest - lowest + 1;
        for (int attempt = 0; attempt < tries; attempt++) {
            int x = chunkX * 16 + roll.nextInt(16);
            int z = chunkZ * 16 + roll.nextInt(16);
            int y = lowest + roll.nextInt(band);
            found.add(new Vein(x, y, z));
        }
        return found;
    }

    @Override public void generateChunk(ContentPlacer placer, ChunkPos chunk, Predicate<BlockPos> valid) {
        int floor = placer.floorY();
        int ceiling = placer.ceilingY();
        int lowest = Math.max(floor, minHeight);
        int highest = Math.min(ceiling - 1, maxHeight);
        if (highest < lowest) { return; }
        long seed = placer.level().getSeed() ^ salt;
        RandomSource random = RandomSource.create(seed ^ (chunk.x * 341873128712L + chunk.z * 132897987541L));
        int placed = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                for (Vein vein : veinsOf(placer.level().getSeed(), floor, ceiling, chunk.x + dx, chunk.z + dz)) {
                    if (!valid.test(vein.pos())) { continue; }
                    placed += write(placer, random, seed, vein, chunk, Math.max(lowest, vein.y() - REACH), Math.min(highest, vein.y() + REACH));
                }
            }
        }
        if (placed > 0) { ContentLog.LOGGER.debug("The {} vein(s) reaching chunk {} placed {} block(s) of {} there", shape.pattern(), chunk, placed, key); }
    }

    private int write(ContentPlacer placer, RandomSource random, long seed, Vein vein, ChunkPos chunk, int lowest, int highest) {
        int placed = 0;
        for (int x = chunk.getMinBlockX(); x <= chunk.getMaxBlockX(); x++) {
            int offX = x - vein.x();
            for (int z = chunk.getMinBlockZ(); z <= chunk.getMaxBlockZ(); z++) {
                int offZ = z - vein.z();
                if (offX * offX + offZ * offZ > REACH * REACH) { continue; }
                for (int y = lowest; y <= highest; y++) {
                    int offY = y - vein.y();
                    float away = Mth.sqrt(offX * offX + offY * offY + offZ * offZ);
                    float bound = boundary(away);
                    if (bound <= 0.0F) { continue; }
                    if (shape.density() < 1.0F && Hashes.unit(seed, x, y, z) >= shape.density()) { continue; }
                    float value = pattern(x, y, z) * bound;
                    if (value <= shape.threshold()) { continue; }
                    if (placer.occupied(x, y, z)) { continue; }
                    float tier = (value - shape.threshold()) / (1.0F - shape.threshold());
                    BlockState state = tier >= RICH && rich != null ? rich : tier >= NORMAL || poor == null ? placer.palette().choose(random) : poor;
                    if (placer.placeExactly(state, x, y, z)) { placed++; }
                }
            }
        }
        return placed;
    }

    private static float boundary(float away) {
        if (away <= FULL) { return 1.0F; }
        if (away <= WANING) { return 0.75F - 0.25F * (away - FULL) / (WANING - FULL); }
        if (away >= REACH) { return 0.0F; }
        return 0.5F * (1.0F - (away - WANING) / (REACH - WANING));
    }

    private float pattern(int x, int y, int z) {
        if (ShapeDef.BANDED.equals(shape.pattern())) { return banded(x, y, z); }
        if (ShapeDef.TUBE.equals(shape.pattern())) { return tube(x, y, z); }
        return plain(x, y, z);
    }

    private float plain(int x, int y, int z) {
        float bias = -0.2F + Math.min(0.5F, size / 100.0F);
        float warped = warp.octaves(x / 24.0F, y / 24.0F, z / 24.0F, 2, 1.0F) * 0.4F;
        return Mth.clamp(noise.octaves(x / 24.0F, y / 24.0F, z / 24.0F, 2, 1.0F) * 3.5F + bias + warped, -1.0F, 1.0F);
    }

    private float banded(int x, int y, int z) {
        float drift = warp.octaves(x / 80.0F, y / 50.0F, z / 80.0F, 2, 0.7F) - 0.2F;
        float base = Mth.clamp(noise.octaves(x / (float) size, y / 10.0F, z / (float) size, 3, 0.9F) * 3.0F + drift, -1.0F, 1.0F);
        float band = (float) Math.sin(y / (5.5F + drift * 0.2F) - drift * 0.15F);
        return Mth.clamp(-base * band * 1.6F, -1.0F, 1.0F);
    }

    private float tube(int x, int y, int z) {
        float drift = warp.octaves(x / 24.0F, y / 64.0F, z / 24.0F, 2, 0.5F) - 0.1F;
        float cell = Math.max(20.0F, 50.0F - size);
        float middle = 8.0F * (0.75F + 0.75F * drift);
        float inner = middle - TUBE_WALL * 0.5F;
        float outer = middle + TUBE_WALL * 0.5F;
        int cellX = (int) Math.floor(x / cell);
        int cellZ = (int) Math.floor(z / cell);
        float nearest = Float.MAX_VALUE;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int nx = cellX + i;
                int nz = cellZ + j;
                float centerX = nx * cell + Hashes.unit(salt, nx, 0, nz) * cell;
                float centerZ = nz * cell + Hashes.unit(salt + 1, nx, 0, nz) * cell;
                centerX += wobble.noise(centerX / 50.0F, y / 50.0F, centerZ / 50.0F) * 3.0F;
                centerZ += wobble.noise(centerX / 50.0F, y / 50.0F, centerZ / 50.0F + 100.0F) * 3.0F;
                float dx = x - centerX;
                float dz = z - centerZ;
                float away = Mth.sqrt(dx * dx + dz * dz);
                if (away < nearest) { nearest = away; }
            }
        }
        float wall = smooth(inner, inner + 1.0F, nearest) - smooth(outer - 1.0F, outer, nearest);
        float rise = (float) Math.sin(y / 16.0) * 0.2F + 1.0F + 0.5F * drift;
        float twist = warp.noise(x / 32.0F, y / 8.0F, z / 32.0F) + 1.0F;
        return Mth.clamp(wall * rise * twist * 1.4F - 0.5F, -1.0F, 1.0F);
    }

    private static float smooth(float from, float to, float at) {
        float t = Mth.clamp((at - from) / (to - from), 0.0F, 1.0F);
        return t * t * (3.0F - 2.0F * t);
    }
}
