package mctmods.resourcedatapackloader.content.rubic.server.chunkio;

import mctmods.resourcedatapackloader.content.rubic.regionlib.impl.RegionNames;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

final class OldRegionNames {
    private static final Pattern COLUMNS = Pattern.compile("(-?\\d+\\.-?\\d+)\\.2dr(\\.ext)?");
    private static final Pattern CUBES = Pattern.compile("(-?\\d+\\.-?\\d+\\.-?\\d+)\\.3dr(\\.ext)?");
    static final String[] LEVEL_FILES ={"level.dat", "level.dat_old"};

    private OldRegionNames() {}

    static void rename(Path world, Path dimension, Path columns, Path cubes) throws IOException {
        List<Path[]> moves = new ArrayList<>();
        columns(columns, moves);
        cubes(cubes, moves);
        if (moves.isEmpty()) { return; }
        if (cubicChunksWorld(world)) {
            ContentLog.LOGGER.warn("Left {} old region file(s) and folder(s) in {} alone, because this save is marked as a Cubic Chunks world", moves.size(), dimension.getFileName());
            return;
        }
        int renamed = 0;
        for (Path[] move : moves) {
            if (Files.exists(move[1])) {
                ContentLog.LOGGER.warn("Kept {} under its old name, because {} is already there", move[0], move[1].getFileName());
                continue;
            }
            Files.move(move[0], move[1]);
            renamed++;
        }
        ContentLog.LOGGER.info("Renamed {} rubic region file(s) and folder(s) in {} to the {} and {} names", renamed, dimension.getFileName(), RegionNames.COLUMNS, RegionNames.CUBES);
    }

    static void columns(Path folder, List<Path[]> moves) throws IOException { collect(folder, COLUMNS, RegionNames.COLUMNS, moves); }

    static void cubes(Path folder, List<Path[]> moves) throws IOException { collect(folder, CUBES, RegionNames.CUBES, moves); }

    private static void collect(Path folder, Pattern old, String extension, List<Path[]> moves) throws IOException {
        try (Stream<Path> held = Files.list(folder)) {
            for (Path file : (Iterable<Path>) held::iterator) {
                Matcher name = old.matcher(file.getFileName().toString());
                if (!name.matches()) { continue; }
                moves.add(new Path[] {file, folder.resolve(name.group(1) + extension + (name.group(2) == null ? "" : RegionNames.SPILL))});
            }
        }
    }

    static boolean cubicChunksWorld(Path world) {
        for (String name : LEVEL_FILES) {
            Path file = world.resolve(name);
            if (!Files.isRegularFile(file)) { continue; }
            try { return cubicChunksMarked(read(file).getCompoundTag("Data")); }
            catch (IOException unreadable) { ContentLog.LOGGER.warn("Could not read {} to tell whose region files these are", file); }
        }
        return false;
    }

    static boolean cubicChunksMarked(NBTTagCompound data) { return data.getBoolean("isCubicWorld") && !data.getBoolean("isRubicWorld"); }

    static NBTTagCompound read(Path file) throws IOException {
        try (InputStream in = Files.newInputStream(file)) { return CompressedStreamTools.readCompressed(in); }
    }
}
