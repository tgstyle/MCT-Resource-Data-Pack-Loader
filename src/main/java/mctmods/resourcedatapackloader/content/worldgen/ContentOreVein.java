package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentStates;
import mctmods.resourcedatapackloader.content.def.AmountDef;
import mctmods.resourcedatapackloader.content.def.ShapeDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentShape;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.MathUtil;
import mctmods.resourcedatapackloader.util.world.Noise3D;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public final class ContentOreVein implements IContentShape {
    public static final String DEFAULT = "default";
    public static final String BANDED = "banded";
    public static final String TUBE = "tube";
    public static final float RICH = 0.88F;
    public static final float NORMAL = 0.4F;
    private static final int OFFSET = 8;
    private static final int FULL = 16;
    private static final int WANING = 20;
    public static final int REACH = 24;
    private static final float TUBE_WALL = 5.0F;
    private final ContentPlacer placer;
    private final ShapeDef shape;
    private final int size;
    private final AmountDef attempts;
    private final int minHeight;
    private final int maxHeight;
    private final ResourceLocation registryName;
    private final long salt;
    private final Noise3D noise;
    private final Noise3D warp;
    private final Noise3D wobble;
    private boolean resolved;
    @Nullable private IBlockState rich;
    @Nullable private IBlockState poor;

    public ContentOreVein(ContentPlacer placer, ShapeDef shape, AmountDef size, AmountDef attempts, int minHeight, int maxHeight, ResourceLocation registryName) {
        this.placer = placer;
        this.shape = shape;
        this.size = Math.max(1, size.most);
        this.attempts = attempts;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.registryName = registryName;
        this.salt = registryName.toString().hashCode() * 0x9E3779B97F4A7C15L;
        this.noise = new Noise3D(salt);
        this.warp = new Noise3D(salt - 1);
        this.wobble = new Noise3D(salt + 1000);
    }

    @Override public boolean generate(World world, Random random, BlockPos origin) { return false; }

    public static final class Vein {
        public final int x;
        public final int y;
        public final int z;
        Vein(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public List<Vein> veinsOf(World world, int chunkX, int chunkZ) {
        List<Vein> found = new ArrayList<>();
        int lowest = Math.max(ContentPlacer.floorY(world), minHeight);
        int highest = Math.min(ContentPlacer.ceilingY(world) - 1, maxHeight);
        if (highest < lowest) { return found; }
        Random roll = new Random(world.getSeed() ^ salt ^ (chunkX * 341873128712L + chunkZ * 132897987541L));
        if (shape.rarity > 0 && !shape.perChunk && roll.nextInt(shape.rarity) != 0) { return found; }
        int tries = shape.rarity > 0 && shape.perChunk ? shape.rarity : attempts.pick(roll);
        int band = highest - lowest + 1;
        for (int attempt = 0; attempt < tries; attempt++) {
            int x = chunkX * 16 + OFFSET + roll.nextInt(16);
            int z = chunkZ * 16 + OFFSET + roll.nextInt(16);
            int y = lowest + roll.nextInt(band);
            found.add(new Vein(x, y, z));
        }
        return found;
    }

    public void generateChunk(World world, int chunkX, int chunkZ, Predicate<BlockPos> valid) {
        resolve();
        int baseX = chunkX * 16 + OFFSET;
        int baseZ = chunkZ * 16 + OFFSET;
        int lowest = Math.max(ContentPlacer.floorY(world), minHeight);
        int highest = Math.min(ContentPlacer.ceilingY(world) - 1, maxHeight);
        if (highest < lowest) { return; }
        long seed = world.getSeed() ^ salt;
        Random random = new Random(seed ^ (chunkX * 341873128712L + chunkZ * 132897987541L));
        int placed = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                for (Vein vein : veinsOf(world, chunkX + dx, chunkZ + dz)) {
                    if (!valid.test(new BlockPos(vein.x, vein.y, vein.z))) { continue; }
                    placed += write(world, random, seed, vein, baseX, baseZ, Math.max(lowest, vein.y - REACH), Math.min(highest, vein.y + REACH));
                }
            }
        }
        if (placed > 0 && ContentLog.LOGGER.debugEnabled()) { ContentLog.LOGGER.debug("The {} vein(s) reaching chunk {}, {} placed {} block(s) of {} there", shape.pattern, chunkX, chunkZ, placed, registryName); }
    }

    private int write(World world, Random random, long seed, Vein vein, int baseX, int baseZ, int lowest, int highest) {
        int placed = 0;
        for (int x = baseX; x < baseX + 16; x++) {
            int offX = x - vein.x;
            for (int z = baseZ; z < baseZ + 16; z++) {
                int offZ = z - vein.z;
                if (offX * offX + offZ * offZ > REACH * REACH) { continue; }
                for (int y = lowest; y <= highest; y++) {
                    int offY = y - vein.y;
                    float away = MathHelper.sqrt(offX * offX + offY * offY + offZ * offZ);
                    float bound = boundary(away);
                    if (bound <= 0.0F) { continue; }
                    if (shape.density < 1.0F && hash01(seed, x, y, z) >= shape.density) { continue; }
                    float value = pattern(x, y, z) * bound;
                    if (value <= shape.threshold) { continue; }
                    if (placer.occupied(world, x, y, z)) { continue; }
                    float tier = (value - shape.threshold) / (1.0F - shape.threshold);
                    IBlockState state = tier >= RICH && rich != null ? rich : tier >= NORMAL || poor == null ? placer.choose(random) : poor;
                    if (placer.placeExactly(world, state, x, y, z)) { placed++; }
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
        if (BANDED.equals(shape.pattern)) { return banded(x, y, z); }
        if (TUBE.equals(shape.pattern)) { return tube(x, y, z); }
        return plain(x, y, z);
    }

    private float plain(int x, int y, int z) {
        float bias = -0.2F + Math.min(0.5F, size / 100.0F);
        float warped = warp.octaves(x / 24.0F, y / 24.0F, z / 24.0F, 2, 1.0F) * 0.4F;
        return MathHelper.clamp(noise.octaves(x / 24.0F, y / 24.0F, z / 24.0F, 2, 1.0F) * 3.5F + bias + warped, -1.0F, 1.0F);
    }

    private float banded(int x, int y, int z) {
        float drift = warp.octaves(x / 80.0F, y / 50.0F, z / 80.0F, 2, 0.7F) - 0.2F;
        float base = MathHelper.clamp(noise.octaves(x / (float) size, y / 10.0F, z / (float) size, 3, 0.9F) * 3.0F + drift, -1.0F, 1.0F);
        float band = (float) Math.sin(y / (5.5F + drift * 0.2F) - drift * 0.15F);
        return MathHelper.clamp(-base * band * 1.6F, -1.0F, 1.0F);
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
                float centerX = nx * cell + hash01(salt, nx, 0, nz) * cell;
                float centerZ = nz * cell + hash01(salt + 1, nx, 0, nz) * cell;
                centerX += wobble.noise(centerX / 50.0F, y / 50.0F, centerZ / 50.0F) * 3.0F;
                centerZ += wobble.noise(centerX / 50.0F, y / 50.0F, centerZ / 50.0F + 100.0F) * 3.0F;
                float dx = x - centerX;
                float dz = z - centerZ;
                float away = MathHelper.sqrt(dx * dx + dz * dz);
                if (away < nearest) { nearest = away; }
            }
        }
        float wall = smooth(inner, inner + 1.0F, nearest) - smooth(outer - 1.0F, outer, nearest);
        float rise = (float) Math.sin(y / 16.0) * 0.2F + 1.0F + 0.5F * drift;
        float twist = warp.noise(x / 32.0F, y / 8.0F, z / 32.0F) + 1.0F;
        return MathHelper.clamp(wall * rise * twist * 1.4F - 0.5F, -1.0F, 1.0F);
    }

    private static float smooth(float from, float to, float at) {
        float t = MathHelper.clamp((at - from) / (to - from), 0.0F, 1.0F);
        return t * t * (3.0F - 2.0F * t);
    }

    private static float hash01(long seed, int x, int y, int z) { return (MathUtil.mix(seed, x, y, z) >>> 40) / (float) (1 << 24); }

    private void resolve() {
        if (resolved) { return; }
        resolved = true;
        rich = state(shape.rich, "rich");
        poor = state(shape.poor, "poor");
    }

    @Nullable private IBlockState state(String named, String key) {
        if (named.isEmpty()) { return null; }
        IBlockState state = ContentStates.parse(named, key);
        if (state == null) { ContentLog.LOGGER.error("Worldgen {} names {} as its {} block, which is not a registered block, so that tier places the normal block", registryName, named, key); }
        return state;
    }
}
