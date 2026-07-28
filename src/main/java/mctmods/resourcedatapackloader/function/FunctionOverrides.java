package mctmods.resourcedatapackloader.function;

import mctmods.resourcedatapackloader.Config;
import mctmods.resourcedatapackloader.core.MCTMixin;
import mctmods.resourcedatapackloader.core.Summary;
import mctmods.resourcedatapackloader.pack.PackManager;

import net.minecraft.advancements.FunctionManager;
import net.minecraft.command.FunctionObject;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class FunctionOverrides {

    private FunctionOverrides() {}

    public static void apply(FunctionManager manager, Map<ResourceLocation, FunctionObject> functions) {
        if (!Config.settings.loadFunctions) { return; }
        PackManager packs = PackManager.get();
        if (packs.isEmpty()) { return; }
        int[] added = new int[1];
        packs.forEach(PackManager.FUNCTIONS, PackManager.MCFUNCTION, (namespace, path, contents) -> {
            ResourceLocation key = new ResourceLocation(namespace, path);
            if (functions.containsKey(key)) { return; }
            FunctionObject function = build(manager, key, contents);
            if (function == null) { return; }
            functions.put(key, function);
            added[0]++;
        });
        if (added[0] > 0) { Summary.info("functions", "Loaded " + added[0] + " function(s) from packs"); }
    }

    private static FunctionObject build(FunctionManager manager, ResourceLocation key, String contents) {
        try { return FunctionObject.create(manager, lines(contents)); }
        catch (RuntimeException ex) {
            MCTMixin.LOGGER.error("Parsing error in function {}, skipping it", key, ex);
            return null;
        }
    }

    private static List<String> lines(String contents) {
        return new ArrayList<>(Arrays.asList(contents.split("\r\n|\r|\n", -1)));
    }
}
