package mctmods.resourcedatapackloader.util.compat;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.util.ContentLog;

import farseek.util.ImplicitConversions$;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.fml.common.Loader;
import scala.Option;
import streams.block.FixedFlowBlock;
import streams.world.gen.structure.RiverGenerator;
import streams.world.gen.structure.RiverStructure;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import javax.annotation.Nonnull;

public final class StreamsRubicValleys {
    private static Handler handler;

    private StreamsRubicValleys() {}

    public static void register() {
        if (!Loader.isModLoaded("streams")) { return; }
        handler = new Handler();
        ContentLog.LOGGER.info("Streams carves its river valleys into chunks only as their terrain generates, an order rubic does not keep, so on rubic worlds the valley is carved over the whole populate window just before the liquid arrives");
    }

    public static void carveAhead(World world, int xChunk, int zChunk) {
        if (handler == null || world.isRemote || !((IRubicWorld) world).rdpl$isRubicWorld() || world.provider.getDimension() != 0) { return; }
        handler.carve((WorldServer) world, xChunk, zChunk);
    }

    static final class Handler {
        private static final Field GENERATING = generatingFlag();
        private static final Map<World, Set<Long>> CARVED = new WeakHashMap<>();

        void carve(WorldServer world, int xChunk, int zChunk) {
            RiverGenerator rivers = RiverGenerator.surfaceWaterGenerator();
            if (rivers == null || rivers.invalidWorldTypes().contains(world.getWorldType())) { return; }
            Set<Long> carved = CARVED.computeIfAbsent(world, unused -> new HashSet<>());
            for (int dx = 0; dx <= 1; dx++) {
                for (int dz = 0; dz <= 1; dz++) { carveChunk(world, rivers, carved, xChunk + dx, zChunk + dz); }
            }
        }

        private void carveChunk(WorldServer world, RiverGenerator rivers, Set<Long> carved, int x, int z) {
            long key = ChunkPos.asLong(x, z);
            if (carved.contains(key)) { return; }
            Option<Option<RiverStructure>> known = rivers.structures().get(rivers.riverKey(x, z));
            if (known.isDefined() && (known.get().isEmpty() || !touches(known.get().get(), x, z))) { return; }
            if (!hold(rivers)) { return; }
            try { rivers.generate(world, x, z, new ColumnPrimer(world.getChunk(x, z))); }
            finally { release(rivers); }
            known = rivers.structures().get(rivers.riverKey(x, z));
            if (known.isDefined() && known.get().isDefined() && touches(known.get().get(), x, z)) { carved.add(key); }
        }

        private static boolean touches(RiverStructure river, int x, int z) {
            StructureBoundingBox box = ImplicitConversions$.MODULE$.boundedBoundingBox(river);
            return box.intersectsWith(x << 4, z << 4, (x << 4) + 15, (z << 4) + 15);
        }

        private static Field generatingFlag() {
            try {
                Field field = RiverGenerator.class.getSuperclass().getDeclaredField("generating");
                field.setAccessible(true);
                return field;
            }
            catch (NoSuchFieldException missing) { return null; }
        }

        private static boolean hold(RiverGenerator rivers) {
            if (GENERATING == null) { return true; }
            try {
                if (GENERATING.getBoolean(rivers)) { return false; }
                GENERATING.setBoolean(rivers, true);
                return true;
            }
            catch (IllegalAccessException denied) { return true; }
        }

        private static void release(RiverGenerator rivers) {
            if (GENERATING == null) { return; }
            try { GENERATING.setBoolean(rivers, false); }
            catch (IllegalAccessException ignored) {}
        }
    }

    private static final class ColumnPrimer extends ChunkPrimer {
        private final Chunk column;
        private final World world;

        ColumnPrimer(Chunk column) {
            this.column = column;
            this.world = column.getWorld();
        }

        @Override @Nonnull public IBlockState getBlockState(int x, int y, int z) { return column.getBlockState(x, y, z); }

        @Override public void setBlockState(int x, int y, int z, @Nonnull IBlockState state) {
            if (column.getBlockState(x, y, z).getBlock() instanceof FixedFlowBlock) { return; }
            world.setBlockState(new BlockPos((column.x << 4) + x, y, (column.z << 4) + z), state, 2 | 16);
        }
    }
}
