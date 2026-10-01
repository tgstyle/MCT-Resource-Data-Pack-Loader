package mctmods.resourcedatapackloader.content.rubic.server.chunkio;

import mctmods.resourcedatapackloader.content.rubic.Rubic;
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
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RegionCubeStorage implements IRubicStorage {
    private static SaveCubeColumns saveForPath(Path world, Path path) throws IOException {
        Utils.createDirectories(path);

        Path part2d = path.resolve("region2d");
        Utils.createDirectories(part2d);

        Path part3d = path.resolve("region3d");
        Utils.createDirectories(part3d);

        OldRegionNames.rename(world, path, part2d, part3d);

        SaveSection2D section2d = new SaveSection2D(Arrays.asList(
                new CachedRegionProvider<>(
                        new SimpleRegionProvider<>(new EntryLocation2D.Provider(), part2d, (keyProv, r) ->
                                ShadowPagingRegion.<EntryLocation2D>builder()
                                        .setDirectory(part2d)
                                        .setRegionKey(r)
                                        .setKeyProvider(keyProv)
                                        .setSectorSize(512)
                                        .build(),
                                (dir, key) -> Files.exists(dir.resolve(key.getRegionKey().getName()))
                        )
                ),
                new CachedRegionProvider<>(
                        new SimpleRegionProvider<>(new EntryLocation2D.Provider(), part2d,
                                (keyProvider, regionKey) -> new ExtRegion<>(part2d, Collections.emptyList(), keyProvider, regionKey),
                                (dir, key) -> Files.exists(dir.resolve(key.getRegionKey().getName() + RegionNames.SPILL))
                        )
                )));
        SaveSection3D section3d = new SaveSection3D(Arrays.asList(
                new CachedRegionProvider<>(
                        new SimpleRegionProvider<>(new EntryLocation3D.Provider(), part3d, (keyProv, r) ->
                                ShadowPagingRegion.<EntryLocation3D>builder()
                                        .setDirectory(part3d)
                                        .setRegionKey(r)
                                        .setKeyProvider(keyProv)
                                        .setSectorSize(512)
                                        .build(),
                                (dir, key) -> Files.exists(dir.resolve(key.getRegionKey().getName()))
                        )
                ),
                new CachedRegionProvider<>(
                        new SimpleRegionProvider<>(new EntryLocation3D.Provider(), part3d,
                                (keyProvider, regionKey) -> new ExtRegion<>(part3d, Collections.emptyList(), keyProvider, regionKey),
                                (dir, key) -> Files.exists(dir.resolve(key.getRegionKey().getName() + RegionNames.SPILL))
                        )
                )));

        return new SaveCubeColumns(section2d, section3d);
    }

    private SaveCubeColumns save;

    public RegionCubeStorage(Path world, Path path) throws IOException { this.save = saveForPath(Objects.requireNonNull(world, "world"), Objects.requireNonNull(path, "path")); }

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
        try { return CompressedStreamTools.readCompressed(new ByteArrayInputStream(data.array())); }
        catch (IOException | RuntimeException corrupt) {
            regenerating(what, pos, corrupt);
            return null;
        }
    }

    private static void regenerating(String what, Object pos, Exception corrupt) { Rubic.LOGGER.error("{} is corrupted! The saved data of {} could not be read ({}). {} will be regenerated.", what, pos, corrupt.toString(), what); }

    @Override public void writeColumn(@Nonnull ChunkPos pos, @Nonnull NBTTagCompound nbt) throws IOException {
        ByteBuf compressedBuf = UnpooledByteBufAllocator.DEFAULT.ioBuffer();
        try {
            CompressedStreamTools.writeCompressed(nbt, new ByteBufOutputStream(compressedBuf));
            this.save.save2d(new EntryLocation2D(pos.x, pos.z), compressedBuf.nioBuffer());
        } finally {
            compressedBuf.release();
        }
    }

    @Override public void writeCube(@Nonnull CubePos pos, @Nonnull NBTTagCompound nbt) throws IOException {
        ByteBuf compressedBuf = UnpooledByteBufAllocator.DEFAULT.ioBuffer();
        try {
            CompressedStreamTools.writeCompressed(nbt, new ByteBufOutputStream(compressedBuf));
            this.save.save3d(new EntryLocation3D(pos.getX(), pos.getY(), pos.getZ()), compressedBuf.nioBuffer());
        } finally {
            compressedBuf.release();
        }
    }

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
