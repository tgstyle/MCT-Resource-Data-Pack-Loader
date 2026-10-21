package mctmods.resourcedatapackloader.content.rubic.server.chunkio;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IKey;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IKeyProvider;
import mctmods.resourcedatapackloader.content.rubic.regionlib.api.region.interfaces.IRegionProvider;
import mctmods.resourcedatapackloader.content.rubic.regionlib.impl.EntryLocation2D;
import mctmods.resourcedatapackloader.content.rubic.regionlib.impl.EntryLocation3D;
import mctmods.resourcedatapackloader.content.rubic.regionlib.impl.RegionNames;
import mctmods.resourcedatapackloader.content.rubic.regionlib.impl.SaveCubeColumns;
import mctmods.resourcedatapackloader.content.rubic.regionlib.impl.save.SaveSection2D;
import mctmods.resourcedatapackloader.content.rubic.regionlib.impl.save.SaveSection3D;
import mctmods.resourcedatapackloader.content.rubic.regionlib.lib.ExtRegion;
import mctmods.resourcedatapackloader.content.rubic.regionlib.lib.provider.SimpleRegionProvider;
import mctmods.resourcedatapackloader.content.rubic.regionlib.util.ICorruptedDataException;
import mctmods.resourcedatapackloader.content.rubic.regionlib.util.Utils;
import mctmods.resourcedatapackloader.content.rubic.server.chunkio.region.CachedRegionProvider;
import mctmods.resourcedatapackloader.content.rubic.server.chunkio.region.ShadowPagingRegion;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicStorage;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.CubePos;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.UnpooledByteBufAllocator;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class RegionCubeStorage implements IRubicStorage {
    private SaveCubeColumns save;

    public RegionCubeStorage(Path world, Path path) throws IOException { this.save = saveForPath(Objects.requireNonNull(world, "world"), Objects.requireNonNull(path, "path")); }

    private static SaveCubeColumns saveForPath(Path world, Path path) throws IOException {
        Utils.createDirectories(path);
        Path part2d = path.resolve("region2d");
        Utils.createDirectories(part2d);
        Path part3d = path.resolve("region3d");
        Utils.createDirectories(part3d);
        OldRegionNames.rename(world, path, part2d, part3d);
        return new SaveCubeColumns(new SaveSection2D(providers(EntryLocation2D.Provider::new, part2d)), new SaveSection3D(providers(EntryLocation3D.Provider::new, part3d)));
    }

    private static <K extends IKey> List<IRegionProvider<K>> providers(Supplier<IKeyProvider> keys, Path part) {
        return Arrays.asList(
                new CachedRegionProvider<>(new SimpleRegionProvider<>(keys.get(), part,
                        (keyProvider, regionKey) -> ShadowPagingRegion.<K>builder().setDirectory(part).setRegionKey(regionKey).setKeyProvider(keyProvider).setSectorSize(512).build(),
                        (dir, key) -> Files.exists(dir.resolve(key.getRegionKey().getName())))),
                new CachedRegionProvider<>(new SimpleRegionProvider<>(keys.get(), part,
                        (keyProvider, regionKey) -> new ExtRegion<>(part, Collections.emptyList(), keyProvider, regionKey),
                        (dir, key) -> Files.exists(dir.resolve(key.getRegionKey().getName() + RegionNames.SPILL)))));
    }

    @Override public boolean columnExists(@Nonnull ChunkPos pos) throws IOException { return this.save.getSaveSection2D().hasEntry(new EntryLocation2D(pos.x, pos.z)); }

    @Override public NBTTagCompound readColumn(@Nonnull ChunkPos pos) throws IOException {
        try { return parsed(this.save.load(new EntryLocation2D(pos.x, pos.z), true).orElse(null), "Column", pos); }
        catch (ICorruptedDataException corrupt) {
            regenerating("Column", pos, corrupt);
            return null;
        }
    }

    @Override public NBTTagCompound readCube(@Nonnull CubePos pos) throws IOException {
        try { return parsed(this.save.load(new EntryLocation3D(pos.getX(), pos.getY(), pos.getZ()), true).orElse(null), "Cube", pos); }
        catch (ICorruptedDataException corrupt) {
            regenerating("Cube", pos, corrupt);
            return null;
        }
    }

    @Nullable private static NBTTagCompound parsed(@Nullable ByteBuffer data, String what, Object pos) {
        if (data == null) { return null; }
        try { return CompressedStreamTools.readCompressed(new ByteArrayInputStream(data.array(), data.arrayOffset() + data.position(), data.remaining())); }
        catch (IOException | RuntimeException corrupt) {
            regenerating(what, pos, corrupt);
            return null;
        }
    }

    private static void regenerating(String what, Object pos, Exception corrupt) { ContentLog.LOGGER.error("{} is corrupted! The saved data of {} could not be read ({}). {} will be regenerated.", what, pos, corrupt.toString(), what); }

    @Override public void writeBatch(@Nonnull NBTBatch batch) throws IOException {
        Map<EntryLocation2D, ByteBuf> compressedColumns = Collections.emptyMap();
        Map<EntryLocation3D, ByteBuf> compressedCubes = Collections.emptyMap();
        try {
            compressedColumns = this.compressNBTForBatchWrite(batch.columns, pos -> new EntryLocation2D(pos.x, pos.z));
            compressedCubes = this.compressNBTForBatchWrite(batch.cubes, pos -> new EntryLocation3D(pos.getX(), pos.getY(), pos.getZ()));
            if (!compressedCubes.isEmpty()) {
                this.save.save3d(compressedCubes.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().nioBuffer())));
            }
            if (!compressedColumns.isEmpty()) {
                this.save.save2d(compressedColumns.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().nioBuffer())));
            }
            ContentLog.LOGGER.debug("Saved batch: {} columns and {} cubes", batch.columns.size(), batch.cubes.size());
        } finally {
            compressedColumns.values().forEach(ByteBuf::release);
            compressedCubes.values().forEach(ByteBuf::release);
        }
    }

    private <KI, KO> Map<KO, ByteBuf> compressNBTForBatchWrite(Map<KI, NBTTagCompound> nbt, Function<KI, KO> keyMappingFunction) throws IOException {
        if (nbt.isEmpty()) { return Collections.emptyMap(); }
        try {
            return nbt.entrySet().parallelStream().collect(Collectors.toMap(entry -> keyMappingFunction.apply(entry.getKey()), entry -> {
                ByteBuf compressedBuf = UnpooledByteBufAllocator.DEFAULT.ioBuffer();
                try {
                    CompressedStreamTools.writeCompressed(entry.getValue(), new ByteBufOutputStream(compressedBuf));
                    return compressedBuf.retain();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                } finally {
                    compressedBuf.release();
                }
            }));
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }

    @Override public void flush() throws IOException { this.save.flush(); }

    @Override public void close() throws IOException {
        this.save.close();
        this.save = null;
    }
}
