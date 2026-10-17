package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.def.CraterDef;
import mctmods.resourcedatapackloader.content.village.RailPiece;
import mctmods.resourcedatapackloader.content.worldgen.beard.BeardLinks;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IMapGenBase;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IMapGenStructure;
import mctmods.resourcedatapackloader.util.MathUtil;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.ChunkGeneratorHell;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

public final class ContentCraters {
    private static final long SALT = 0x435241544552L;
    private static final int TOP = 255;
    private static final int MARGIN = 12;
    private static final IBlockState AIR = Objects.requireNonNull(Blocks.AIR).getDefaultState();
    private static final Field[] NO_FIELDS = new Field[0];
    private static final Map<Class<?>, Field[]> MOD_VILLAGES = new ConcurrentHashMap<>();
    private static int reach = -1;
    private static boolean asking;
    @Nullable private static ChunkPrimer carved;
    private static long carvedAt;

    private ContentCraters() {}

    private static final class Crater {
        final int x;
        final int z;
        final int radius;
        final int depth;

        Crater(int x, int z, int radius, int depth) {
            this.x = x;
            this.z = z;
            this.radius = radius;
            this.depth = depth;
        }

        boolean near(int minX, int minZ, int maxX, int maxZ, int keep) {
            long dx = Math.max(0, Math.max(minX - x, x - maxX));
            long dz = Math.max(0, Math.max(minZ - z, z - maxZ));
            return dx * dx + dz * dz < (long) keep * keep;
        }

        boolean near(StructureBoundingBox box, int keep) { return near(box.minX, box.minZ, box.maxX, box.maxZ, keep); }
    }

    public static void carve(@Nullable World world, @Nullable IChunkGenerator maker, int chunkX, int chunkZ, ChunkPrimer primer) {
        if (world == null || world.isRemote) { return; }
        int around = reach();
        if (around <= 0) { return; }
        long at = ChunkPos.asLong(chunkX, chunkZ);
        if (primer == carved && at == carvedAt) { return; }
        carved = primer;
        carvedAt = at;
        boolean roofed = maker instanceof ChunkGeneratorHell || world.provider.isNether();
        int blockX = chunkX << 4;
        int blockZ = chunkZ << 4;
        for (int craterX = chunkX - around; craterX <= chunkX + around; craterX++) {
            for (int craterZ = chunkZ - around; craterZ <= chunkZ + around; craterZ++) {
                Crater crater = at(world, craterX, craterZ);
                if (crater == null || !crater.near(blockX, blockZ, blockX + 15, blockZ + 15, crater.radius)) { continue; }
                if (claimed(world, maker, crater, craterX, craterZ)) { continue; }
                bowl(crater, blockX, blockZ, primer, roofed);
            }
        }
    }

    private static int reach() {
        if (reach < 0) {
            int widest = ContentBiomes.widestCrater();
            reach = widest <= 0 ? 0 : (widest + 15) >> 4;
        }
        return reach;
    }

    @Nullable private static Crater at(World world, int chunkX, int chunkZ) {
        Random random = new Random(MathUtil.mix(world.getSeed() ^ SALT, chunkX, 0, chunkZ));
        int x = (chunkX << 4) + random.nextInt(16);
        int z = (chunkZ << 4) + random.nextInt(16);
        Biome biome = world.getBiomeProvider().getBiome(new BlockPos(x, 0, z));
        if (!(biome instanceof ContentBiome)) { return null; }
        CraterDef def = ((ContentBiome) biome).getDef().craters;
        if (def == null || random.nextFloat() >= def.chance) { return null; }
        return new Crater(x, z, def.minRadius + random.nextInt(def.maxRadius - def.minRadius + 1), def.depth);
    }

    private static boolean claimed(World world, @Nullable IChunkGenerator maker, Crater crater, int chunkX, int chunkZ) {
        int keep = crater.radius + MARGIN;
        StructureBoundingBox area = new StructureBoundingBox(crater.x - keep, 0, crater.z - keep, crater.x + keep, 0, crater.z + keep);
        if (ContentStructureMaps.claims(world, area, box -> crater.near(box, keep))) { return true; }
        MapGenVillage shell = villages(world, maker);
        if (shell == null) { return false; }
        ContentCraterVerdicts verdicts = ContentCraterVerdicts.of(world);
        long key = ChunkPos.asLong(chunkX, chunkZ);
        Boolean known = verdicts.claimed(key);
        if (known != null) { return known; }
        int around = (keep >> 4) + 1;
        if (!asking) { found(world, maker, shell, chunkX, chunkZ, around); }
        boolean settled = settled(world, shell, crater, chunkX, chunkZ, keep, around);
        verdicts.put(key, settled);
        return settled;
    }

    @Nullable private static MapGenVillage villages(World world, @Nullable IChunkGenerator maker) {
        MapGenVillage shell = ContentStructureSearch.villageShell(maker);
        if (shell != null || maker == null) { return shell; }
        Field[] fields = MOD_VILLAGES.computeIfAbsent(maker.getClass(), ContentCraters::villageFields);
        if (fields.length == 0 || !world.getWorldInfo().isMapFeaturesEnabled()) { return null; }
        try {
            Object held = fields[0].get(maker);
            return fields[1].getBoolean(maker) && held instanceof MapGenVillage ? (MapGenVillage) held : null;
        }
        catch (IllegalAccessException refused) { return null; }
    }

    private static Field[] villageFields(Class<?> owner) {
        try {
            Field villages = owner.getDeclaredField("villageGenerator");
            Field features = owner.getDeclaredField("mapFeaturesEnabled");
            if (!MapGenVillage.class.isAssignableFrom(villages.getType()) || features.getType() != boolean.class) { return NO_FIELDS; }
            villages.setAccessible(true);
            features.setAccessible(true);
            return new Field[] { villages, features };
        }
        catch (NoSuchFieldException | RuntimeException absent) { return NO_FIELDS; }
    }

    private static void found(World world, @Nullable IChunkGenerator maker, MapGenVillage shell, int chunkX, int chunkZ, int around) {
        World sampling = ContentBeard.samplerWorld;
        ContentBeard.samplerWorld = maker instanceof ChunkGeneratorOverworld && ContentBeard.wanted() ? world : null;
        asking = true;
        try {
            shell.generate(world, chunkX, chunkZ, ContentBeardSeat.UNUSED);
            for (int offsetX = -around; offsetX <= around; offsetX += 2 * around) {
                for (int offsetZ = -around; offsetZ <= around; offsetZ += 2 * around) { shell.generate(world, chunkX + offsetX, chunkZ + offsetZ, ContentBeardSeat.UNUSED); }
            }
        }
        finally {
            asking = false;
            ContentBeard.samplerWorld = sampling;
        }
    }

    private static boolean settled(World world, MapGenVillage shell, Crater crater, int chunkX, int chunkZ, int keep, int around) {
        MapGenStructure real = ContentStructureSearch.theRealOne(shell);
        int reachable = ((IMapGenBase) real).rdpl$getRange() + around;
        boolean linked = BeardLinks.on();
        for (StructureStart start : ((IMapGenStructure) real).rdpl$getStructureMap().values()) {
            if (start == null || !start.isSizeableStructure()) { continue; }
            if (crater.near(start.getBoundingBox(), keep)) {
                for (StructureComponent piece : start.getComponents()) {
                    if (crater.near(piece.getBoundingBox(), keep)) { return true; }
                }
            }
            if (!linked || Math.abs(start.getChunkPosX() - chunkX) > reachable || Math.abs(start.getChunkPosZ() - chunkZ) > reachable) { continue; }
            for (RailPiece rail : BeardLinks.planned(world, start.getComponents())) {
                if (crater.near(rail.getBoundingBox(), keep)) { return true; }
            }
        }
        return false;
    }

    private static void bowl(Crater crater, int blockX, int blockZ, ChunkPrimer primer, boolean roofed) {
        long widest = (long) crater.radius * crater.radius;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                long dx = blockX + x - crater.x;
                long dz = blockZ + z - crater.z;
                long apart = dx * dx + dz * dz;
                if (apart >= widest) { continue; }
                double share = (double) apart / (double) widest;
                int deep = (int) Math.round(crater.depth * (1.0D - share * share));
                if (deep > 0) { dig(primer, x, z, deep, roofed); }
            }
        }
    }

    private static void dig(ChunkPrimer primer, int x, int z, int deep, boolean roofed) {
        int y = surface(primer, x, z, roofed);
        if (y < 1) { return; }
        IBlockState fill = y < TOP ? primer.getBlockState(x, y + 1, z) : AIR;
        if (!fill.getMaterial().isLiquid()) { fill = AIR; }
        for (int dug = 0; dug < deep && y > 1 && solid(primer, x, y - 1, z); dug++) {
            primer.setBlockState(x, y, z, fill);
            y--;
        }
    }

    private static int surface(ChunkPrimer primer, int x, int z, boolean roofed) {
        int y = TOP;
        while (y > 0 && !solid(primer, x, y, z)) { y--; }
        if (!roofed) { return y; }
        while (y > 0 && solid(primer, x, y, z)) { y--; }
        while (y > 0 && !solid(primer, x, y, z)) { y--; }
        return y;
    }

    private static boolean solid(ChunkPrimer primer, int x, int y, int z) { return primer.getBlockState(x, y, z).getMaterial().isSolid(); }
}
