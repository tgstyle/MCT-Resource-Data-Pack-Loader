package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.compat.LineCompat;
import mctmods.resourcedatapackloader.content.def.PickDef;
import mctmods.resourcedatapackloader.util.ContentLog;

import com.google.common.math.IntMath;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ContentListBiomes extends LineCompat.ListSourceBase {
    public static final String ID = "list";
    public static final int WEIGHT = 10;
    private static final Codec<PickDef> ENTRY = Codec.either(Codec.STRING, RecordCodecBuilder.<PickDef>create(entry -> entry.group(
            Codec.STRING.fieldOf("biome").forGetter(PickDef::name),
            Codec.INT.optionalFieldOf("weight", WEIGHT).forGetter(PickDef::weight)).apply(entry, PickDef::new))).xmap(either -> either.map(name -> new PickDef(name, WEIGHT), pick -> pick), Either::right);
    public static final MapCodec<ContentListBiomes> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RegistryOps.retrieveGetter(Registries.BIOME),
            ENTRY.listOf().fieldOf("biomes").forGetter(source -> source.entries),
            Codec.STRING.fieldOf("dimension").forGetter(source -> source.dimension)).apply(instance, instance.stable(ContentListBiomes::new)));
    private static final int ZOOMS = 6;
    private static final long LIST_SEED = 200L;
    private static final long ZOOM_SEED = 1000L;
    private static final long SMOOTH_SEED = 1000L;
    private static final long MULTIPLIER = 6364136223846793005L;
    private static final long INCREMENT = 1442695040888963407L;
    @Nullable private static volatile Long worldSeed;
    private final List<PickDef> entries;
    private final String dimension;
    private final List<Holder<Biome>> biomes;
    private final int[] weights;
    private final int total;
    @Nullable private volatile long[] cached;

    private ContentListBiomes(HolderGetter<Biome> lookup, List<PickDef> entries, String dimension) {
        this.entries = entries;
        this.dimension = dimension;
        List<Holder<Biome>> found = new ArrayList<>();
        List<Integer> weighed = new ArrayList<>();
        for (PickDef entry : entries) {
            Identifier id = Identifier.tryParse(entry.name());
            Optional<Holder.Reference<Biome>> biome = id == null ? Optional.empty() : lookup.get(ResourceKey.create(Registries.BIOME, id));
            if (biome.isEmpty()) { ContentLog.LOGGER.error("A dimension's biome list names '{}', which is not a registered biome, so it is left out", entry.name()); }
            else {
                found.add(biome.get());
                weighed.add(entry.weight());
            }
        }
        if (found.isEmpty()) {
            ContentLog.LOGGER.error("A dimension's biome list holds no registered biome, using minecraft:plains");
            found.add(lookup.getOrThrow(Biomes.PLAINS));
            weighed.add(WEIGHT);
        }
        this.biomes = List.copyOf(found);
        int common = 0;
        for (int weight : weighed) { common = IntMath.gcd(common, weight); }
        this.weights = new int[weighed.size()];
        int sum = 0;
        for (int i = 0; i < weights.length; i++) {
            weights[i] = weighed.get(i) / common;
            sum += weights[i];
        }
        this.total = sum;
    }

    public static void seed(long seed) { worldSeed = seed; }

    @Override @Nonnull protected MapCodec<? extends BiomeSource> codec() { return CODEC; }

    @Override @Nonnull protected Stream<Holder<Biome>> collectPossibleBiomes() { return biomes.stream().distinct(); }

    @Override protected Holder<Biome> biomeAt(int quartX, int quartZ) { return biomes.get(smooth(seeds(), quartX, quartZ)); }

    private long[] seeds() {
        Long world = worldSeed;
        if (world == null) { throw new IllegalStateException("Dimension " + dimension + " asked for a biome before the world seed was known"); }
        long[] known = cached;
        if (known == null || known[0] != world) {
            known = layers(world);
            cached = known;
        }
        return known;
    }

    private static long[] layers(long world) {
        long[] out = new long[ZOOMS + 3];
        out[0] = world;
        out[1] = worldGen(world, LIST_SEED);
        for (int zoom = 0; zoom < ZOOMS; zoom++) { out[2 + zoom] = worldGen(world, ZOOM_SEED + zoom); }
        out[ZOOMS + 2] = worldGen(world, SMOOTH_SEED);
        return out;
    }

    private static long step(long value, long add) { return value * (value * MULTIPLIER + INCREMENT) + add; }

    private static long worldGen(long seed, long layer) {
        long base = step(step(step(layer, layer), layer), layer);
        return step(step(step(seed, base), base), base);
    }

    private int smooth(long[] seeds, int x, int z) {
        int[] around = area(seeds, ZOOMS, x - 1, z - 1, 3, 3);
        int north = around[1];
        int west = around[3];
        int east = around[5];
        int south = around[7];
        if (west == east && north == south) {
            Rng rng = new Rng(seeds[ZOOMS + 2]);
            rng.at(x, z);
            return rng.next(2) == 0 ? west : north;
        }
        if (north == south) { return north; }
        return west == east ? west : around[4];
    }

    private int[] area(long[] seeds, int level, int x, int z, int width, int height) {
        if (level == 0) { return base(seeds[1], x, z, width, height); }
        int parentX = x >> 1;
        int parentZ = z >> 1;
        int parentWidth = (width >> 1) + 2;
        int parentHeight = (height >> 1) + 2;
        int[] parent = area(seeds, level - 1, parentX, parentZ, parentWidth, parentHeight);
        int span = (parentWidth - 1) << 1;
        int[] grown = new int[span * ((parentHeight - 1) << 1)];
        Rng rng = new Rng(seeds[level + 1]);
        for (int row = 0; row < parentHeight - 1; row++) {
            int at = (row << 1) * span;
            int topLeft = parent[row * parentWidth];
            int bottomLeft = parent[(row + 1) * parentWidth];
            for (int column = 0; column < parentWidth - 1; column++) {
                rng.at((column + parentX) << 1, (row + parentZ) << 1);
                int topRight = parent[column + 1 + row * parentWidth];
                int bottomRight = parent[column + 1 + (row + 1) * parentWidth];
                grown[at] = topLeft;
                grown[at++ + span] = rng.pick(topLeft, bottomLeft);
                grown[at] = rng.pick(topLeft, topRight);
                grown[at++ + span] = rng.modeOrPick(topLeft, topRight, bottomLeft, bottomRight);
                topLeft = topRight;
                bottomLeft = bottomRight;
            }
        }
        int[] out = new int[width * height];
        for (int row = 0; row < height; row++) { System.arraycopy(grown, (row + (z & 1)) * span + (x & 1), out, row * width, width); }
        return out;
    }

    private int[] base(long seed, int x, int z, int width, int height) {
        int[] out = new int[width * height];
        Rng rng = new Rng(seed);
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                rng.at(x + column, z + row);
                out[column + row * width] = weighed(rng.next(total));
            }
        }
        return out;
    }

    private int weighed(int roll) {
        int index = 0;
        for (int left = roll; left >= weights[index]; index++) { left -= weights[index]; }
        return index;
    }

    private static final class Rng {
        private final long world;
        private long state;

        private Rng(long world) { this.world = world; }

        private void at(int x, int z) { state = step(step(step(step(world, x), z), x), z); }

        private int next(int bound) {
            int out = (int) ((state >> 24) % bound);
            state = step(state, world);
            return out < 0 ? out + bound : out;
        }

        private int pick(int... values) { return values[next(values.length)]; }

        private int modeOrPick(int a, int b, int c, int d) {
            if (b == c && c == d) { return b; }
            else if (a == b && a == c) { return a; }
            else if (a == b && a == d) { return a; }
            else if (a == c && a == d) { return a; }
            else if (a == b && c != d) { return a; }
            else if (a == c && b != d) { return a; }
            else if (a == d && b != c) { return a; }
            else if (b == c && a != d) { return b; }
            else if (b == d && a != c) { return b; }
            else if (c == d && a != b) { return c; }
            else { return pick(a, b, c, d); }
        }
    }
}
