package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.core.MCTMixin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nullable;

public final class ContentLog {
    public static final ContentLog LOGGER = new ContentLog();
    private static final String PATH = "logs/rdpl.log";
    private static final String DEBUG = "DEBUG";
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

    private synchronized void write(String level, String message, Object... args) {
        if (DEBUG.equals(level) && !debug) { return; }

        PrintWriter out = open();
        if (out == null) {
            MCTMixin.LOGGER.info(format(message, args, false));
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
            File file = new File(PATH);
            File parent = file.getParentFile();
            if (parent != null && !parent.isDirectory() && !parent.mkdirs()) { throw new IOException("could not create " + parent); }

            writer = new PrintWriter(new FileWriter(file, false));
            writer.println("---- MCT Resource Data Pack Loader ----");
            writer.flush();
        }
        catch (IOException | RuntimeException ex) {
            failed = true;
            MCTMixin.LOGGER.error("Could not open {}, so pack logging goes to the main log instead: {}", PATH, ex.getMessage());
        }
        return writer;
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
