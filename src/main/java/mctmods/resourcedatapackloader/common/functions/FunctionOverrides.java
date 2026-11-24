package mctmods.resourcedatapackloader.common.functions;

import mctmods.resourcedatapackloader.common.pack.loading.PackManager;
import mctmods.resourcedatapackloader.core.config.Config;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.Summary;
import mctmods.resourcedatapackloader.core.util.text.TextLines;

import java.util.Map;
import net.minecraft.advancements.FunctionManager;
import net.minecraft.command.FunctionObject;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;

public final class FunctionOverrides {
    private static boolean commandsReady;

    private FunctionOverrides() {}

    public static void serverStarted(MinecraftServer server) {
        commandsReady = true;
        if (Config.data.functions && !PackManager.get().isEmpty()) { server.getFunctionManager().reload(); }
    }

    public static void serverStopped() { commandsReady = false; }

    public static void apply(FunctionManager manager, Map<ResourceLocation, FunctionObject> functions) {
        if (!Config.data.functions || !commandsReady) { return; }
        PackManager packs = PackManager.get();
        if (packs.isEmpty()) { return; }
        int[] added = new int[1];
        packs.forEach(PackManager.FUNCTIONS, PackManager.MCFUNCTION, (namespace, path, contents) -> {
            ResourceLocation key = new ResourceLocation(namespace, path);
            ContentLog.LOGGER.debug("Function {} read from the pack as {} character(s), first line: {}", key, contents.length(), contents.split("\n", 2)[0]);
            if (functions.containsKey(key)) { return; }
            FunctionObject function = build(manager, key, contents);
            if (function == null) { return; }
            functions.put(key, function);
            added[0]++;
        });
        Summary.loaded("functions", added[0], "function(s) from packs");
    }

    private static FunctionObject build(FunctionManager manager, ResourceLocation key, String contents) {
        try { return FunctionObject.create(manager, TextLines.lines(contents)); }
        catch (RuntimeException ex) {
            ContentLog.LOGGER.error("Parsing error in function {}, skipping it", key, ex);
            return null;
        }
    }
}
