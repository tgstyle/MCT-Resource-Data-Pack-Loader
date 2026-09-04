package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.ResourceDataPackLoader;

import net.neoforged.fml.loading.FMLPaths;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.GZIPOutputStream;
import javax.annotation.Nullable;

public final class ContentLog {
    public static final ContentLog LOGGER = new ContentLog();
    private static final String PATH = "logs/rdpl.log";
    private static final String DEBUG = "DEBUG";
    private static final int KEPT_AT_MOST = 1000;
    private final SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
    @Nullable private PrintWriter writer;
    private boolean failed;
    private boolean debug;

    private ContentLog() {}

    public void setDebug(boolean enabled) { debug = enabled; }

    public boolean debugEnabled() { return debug; }

    public void info(String message, Object... args) { write("INFO", message, args); }

    public void warn(String message, Object... args) { write("WARN", message, args); }

    public void error(String message, Object... args) { write("ERROR", message, args); }

    public void debug(String message, Object... args) { write(DEBUG, message, args); }

    public void fatal(String message, Object... args) { write("FATAL", message, args); }

    public void catching(Throwable thrown) { write("ERROR", "Caught {}", thrown.toString(), thrown); }

    private synchronized void write(String level, String message, Object... args) {
        if (DEBUG.equals(level) && !debug) { return; }
        PrintWriter out = open();
        if (out == null) {
            ResourceDataPackLoader.LOGGER.info(format(message, args, false));
            return;
        }
        Throwable thrown = args.length > 0 && args[args.length - 1] instanceof Throwable ? (Throwable) args[args.length - 1] : null;
        out.println("[" + time.format(new Date()) + "] [" + Thread.currentThread().getName() + "/" + level + "]: " + format(message, args, thrown != null));
        if (thrown != null) { thrown.printStackTrace(out); }
        out.flush();
    }

    @Nullable private PrintWriter open() {
        if (writer != null || failed) { return writer; }
        try {
            File file = FMLPaths.GAMEDIR.get().resolve(PATH).toFile();
            File parent = file.getParentFile();
            if (parent != null && !parent.isDirectory() && !parent.mkdirs()) { throw new IOException("could not create " + parent); }
            roll(file);
            writer = new PrintWriter(new FileWriter(file, false));
            writer.println("---- MCT Resource Data Pack Loader ----");
            writer.flush();
        }
        catch (IOException | RuntimeException ex) {
            failed = true;
            ResourceDataPackLoader.LOGGER.error("Could not open {}, so pack logging goes to the main log instead: {}", PATH, ex.getMessage());
        }
        return writer;
    }

    private static void roll(File file) {
        if (!file.isFile() || file.length() == 0L) { return; }
        String day = new SimpleDateFormat("yyyy-MM-dd").format(new Date(file.lastModified()));
        File kept = null;
        for (int index = 1; index <= KEPT_AT_MOST && kept == null; index++) {
            File candidate = new File(file.getParentFile(), "rdpl-" + day + "-" + index + ".log.gz");
            if (!candidate.exists()) { kept = candidate; }
        }
        if (kept == null) { return; }
        try (InputStream in = Files.newInputStream(file.toPath()); OutputStream out = new GZIPOutputStream(Files.newOutputStream(kept.toPath()))) {
            byte[] buffer = new byte[8192];
            for (int read = in.read(buffer); read > 0; read = in.read(buffer)) { out.write(buffer, 0, read); }
        }
        catch (IOException | RuntimeException ex) {
            ResourceDataPackLoader.LOGGER.error("Could not roll {} into {}, so the old one is lost: {}", PATH, kept.getName(), ex.getMessage());
        }
    }

    private static String format(String message, Object[] args, boolean skipLast) {
        int supplied = skipLast ? args.length - 1 : args.length;
        StringBuilder text = new StringBuilder(message.length() + 32);
        int used = 0;
        int from = 0;
        while (used < supplied) {
            int at = message.indexOf("{}", from);
            if (at < 0) { break; }
            text.append(message, from, at).append(args[used++]);
            from = at + 2;
        }
        return text.append(message, from, message.length()).toString();
    }
}
