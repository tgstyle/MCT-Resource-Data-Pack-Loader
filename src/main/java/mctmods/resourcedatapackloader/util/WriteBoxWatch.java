package mctmods.resourcedatapackloader.util;

import mctmods.resourcedatapackloader.core.MCTMixin;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public final class WriteBoxWatch {
    private static final String PROPERTY = "rdpl.debug.writeBox";
    private static final int FRAMES_KEPT = 24;
    private static final int STACKS_KEPT = 128;
    @Nullable private static final Box BOX = parse(System.getProperty(PROPERTY));
    private static final Map<String, String> STACK_IDS = new HashMap<>();

    private WriteBoxWatch() {}

    public static boolean watching() { return BOX != null; }

    public static void write(int x, int y, int z, Object was, Object now) {
        if (BOX == null || !BOX.contains(x, y, z)) { return; }
        ContentLog.LOGGER.info("writeBox {},{},{} {} -> {} by {}", x, y, z, was, now, id());
    }

    @Nullable private static Box parse(@Nullable String value) {
        if (value == null) { return null; }
        int[] corners = numbers(value.split(","));
        if (corners == null) {
            MCTMixin.LOGGER.warn("-D{}={} is not six comma separated whole numbers, x y z to x y z, so no block write is watched", PROPERTY, value);
            return null;
        }
        MCTMixin.LOGGER.info("Watching every block write inside {}, with the caller of each one", value);
        return new Box(corners[0], corners[1], corners[2], corners[3], corners[4], corners[5]);
    }

    @Nullable private static int[] numbers(String[] parts) {
        if (parts.length != 6) { return null; }
        int[] corners = new int[6];
        for (int at = 0; at < 6; at++) {
            try { corners[at] = Integer.parseInt(parts[at].trim()); }
            catch (NumberFormatException notANumber) { return null; }
        }
        return corners;
    }

    private static synchronized String id() {
        String stack = stack();
        String known = STACK_IDS.get(stack);
        if (known != null) { return known; }
        if (STACK_IDS.size() >= STACKS_KEPT) { return "W?"; }
        String fresh = "W" + (STACK_IDS.size() + 1);
        STACK_IDS.put(stack, fresh);
        ContentLog.LOGGER.info("writeBox caller {} is{}", fresh, stack);
        return fresh;
    }

    private static String stack() {
        StringBuilder frames = new StringBuilder();
        int kept = 0;
        for (StackTraceElement frame : new Throwable().getStackTrace()) {
            if (kept == 0 && writeItself(frame)) { continue; }
            frames.append("\n\tat ").append(frame);
            if (++kept >= FRAMES_KEPT) { break; }
        }
        return frames.toString();
    }

    private static boolean writeItself(StackTraceElement frame) {
        String owner = frame.getClassName();
        if (owner.equals(WriteBoxWatch.class.getName())) { return true; }
        if (!owner.equals("net.minecraft.world.chunk.Chunk") && !owner.equals("net.minecraft.world.World")) { return false; }
        String method = frame.getMethodName();
        return method.equals("setBlockState") || method.contains("rdpl$watchWrite");
    }
}
