package mctmods.resourcedatapackloader.content.rubic.server.chunkio;

import mctmods.resourcedatapackloader.content.rubic.RubicWorldControl;
import mctmods.resourcedatapackloader.content.rubic.world.storage.StorageFormatProviderBase;
import mctmods.resourcedatapackloader.content.rubic.worldgen.VanillaCompatibilityGeneratorProviderBase;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.StartupQuery;
import net.minecraftforge.fml.common.ZipperUtil;
import net.minecraftforge.fml.relauncher.Side;
import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CubicChunksWorld {
    public static final String QUERY = "A pack has requested a rubic world.\n\n"
            + "Convert to a rubic world \"Yes\"\n"
            + "To return to the menu \"No\"";
    private static final String THEIR_DATA = "cubicChunksData.dat";
    private static final String OUR_DATA = "rdplRubicData.dat";

    private CubicChunksWorld() {}

    public static void offer(File folder) {
        FMLCommonHandler fml = FMLCommonHandler.instance();
        if (fml.getEffectiveSide() != Side.SERVER) { return; }
        MinecraftServer server = fml.getMinecraftServerInstance();
        if (server == null || !folder.equals(new File(fml.getSavesDirectory(), server.getFolderName()))) { return; }
        Path world = folder.toPath();
        if (!OldRegionNames.cubicChunksWorld(world) || !RubicWorldControl.wanted()) { return; }
        if (!StartupQuery.confirm(QUERY)) { StartupQuery.abort(); }
        try { ZipperUtil.backupWorld(); }
        catch (IOException failed) {
            StartupQuery.notify("The world backup couldn't be created.\n\n" + failed);
            StartupQuery.abort();
        }
        try { convert(world); }
        catch (IOException failed) {
            ContentLog.LOGGER.error("Could not convert the Cubic Chunks world {} to a rubic world", world, failed);
            StartupQuery.notify("The world could not be converted to a rubic world.\n\n" + failed);
            StartupQuery.abort();
        }
    }

    private static void convert(Path world) throws IOException {
        List<Path[]> moves = new ArrayList<>();
        List<Path> dataFiles = new ArrayList<>();
        Files.walkFileTree(world, new SimpleFileVisitor<Path>() {
            @Override @Nonnull public FileVisitResult preVisitDirectory(@Nonnull Path folder, @Nonnull BasicFileAttributes attributes) throws IOException {
                String name = folder.getFileName().toString();
                if (name.equals("region2d")) {
                    OldRegionNames.columns(folder, moves);
                    return FileVisitResult.SKIP_SUBTREE;
                }
                if (name.equals("region3d")) {
                    OldRegionNames.cubes(folder, moves);
                    return FileVisitResult.SKIP_SUBTREE;
                }
                if (name.equals("data") && Files.isRegularFile(folder.resolve(THEIR_DATA))) { dataFiles.add(folder.resolve(THEIR_DATA)); }
                return FileVisitResult.CONTINUE;
            }
        });
        for (Path[] move : moves) {
            if (Files.exists(move[1])) { throw new IOException(world.relativize(move[1]) + " is already there, so " + move[0].getFileName() + " cannot take its name"); }
        }
        Map<Path, NBTTagCompound> written = new LinkedHashMap<>();
        for (Path file : dataFiles) { written.put(file, rubicData(world, file)); }
        Map<Path, NBTTagCompound> levels = new LinkedHashMap<>();
        for (int at = OldRegionNames.LEVEL_FILES.length - 1; at >= 0; at--) {
            Path file = world.resolve(OldRegionNames.LEVEL_FILES[at]);
            if (!Files.isRegularFile(file)) { continue; }
            NBTTagCompound root = OldRegionNames.read(file);
            NBTTagCompound data = root.getCompoundTag("Data");
            if (!OldRegionNames.cubicChunksMarked(data)) { continue; }
            data.removeTag("isCubicWorld");
            data.setBoolean("isRubicWorld", true);
            levels.put(file, root);
        }
        for (Path[] move : moves) { Files.move(move[0], move[1]); }
        for (Map.Entry<Path, NBTTagCompound> held : written.entrySet()) {
            write(held.getKey().resolveSibling(OUR_DATA), held.getValue());
            Files.delete(held.getKey());
        }
        for (Map.Entry<Path, NBTTagCompound> held : levels.entrySet()) { write(held.getKey(), held.getValue()); }
        ContentLog.LOGGER.info("Converted the Cubic Chunks world {} to a rubic world: {} region file(s) and folder(s) renamed, {} data file(s) converted, {} level file(s) marked",
                world.getFileName(), moves.size(), written.size(), levels.size());
    }

    private static NBTTagCompound rubicData(Path world, Path file) throws IOException {
        NBTTagCompound root = OldRegionNames.read(file);
        if (!root.hasKey("data", 10)) { throw new IOException(world.relativize(file) + " cannot be read: no data compound"); }
        NBTTagCompound data = root.getCompoundTag("data");
        if (data.hasKey("isCubicChunks")) {
            NBTBase flag = data.getTag("isCubicChunks");
            data.removeTag("isCubicChunks");
            data.setTag("isRubicWorld", flag);
        }
        rename(world, file, data, "storageFormat", "cubicchunks:anvil3d", StorageFormatProviderBase.DEFAULT.toString());
        rename(world, file, data, "compatibilityGeneratorType", "cubicchunks:default", VanillaCompatibilityGeneratorProviderBase.DEFAULT.toString());
        return root;
    }

    private static void rename(Path world, Path file, NBTTagCompound data, String key, String theirs, String ours) throws IOException {
        if (!data.hasKey(key)) { return; }
        if (!data.hasKey(key, 8) || !theirs.equals(data.getString(key))) { throw new IOException(world.relativize(file) + " has " + key + " " + data.getTag(key) + ", which a rubic world does not have"); }
        data.setString(key, ours);
    }

    private static void write(Path file, NBTTagCompound root) throws IOException {
        Path partial = file.resolveSibling(file.getFileName() + ".tmp");
        try (OutputStream out = Files.newOutputStream(partial)) { CompressedStreamTools.writeCompressed(root, out); }
        Files.move(partial, file, StandardCopyOption.REPLACE_EXISTING);
    }
}
