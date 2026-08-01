package mctmods.resourcedatapackloader.content.village;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentParser;
import mctmods.resourcedatapackloader.content.def.VillageDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Names;
import mctmods.resourcedatapackloader.util.Summary;

import com.google.gson.JsonParseException;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentVillages {
    private static final String COMPONENT = "RDPL:Plot";
    private static final Map<String, VillageDef> DEFS = new LinkedHashMap<>();
    private static Set<String> named;
    private static boolean loaded;
    private static boolean registered;

    private ContentVillages() {}

    public static boolean load() {
        if (loaded) { return !DEFS.isEmpty(); }
        loaded = true;
        if (!Config.content.villages) { return false; }

        PackManager.get().forEach(PackManager.VILLAGES, PackManager.JSON, (namespace, path, contents) -> {
            ResourceLocation key = new ResourceLocation(namespace, path);
            try {
                VillageDef def = ContentParser.village(key, contents);
                if (def == null) { return; }
                if (!present(def)) {
                    ContentLog.LOGGER.info("Village plot {} needs {}, which is not here, so it is left out", key, def.requires);
                    return;
                }
                DEFS.put(key.toString(), def);
            }
            catch (IllegalArgumentException | JsonParseException ex) { ContentLog.LOGGER.error("Parsing error in village file {}, ignoring it", key, ex); }
        });

        if (!DEFS.isEmpty()) { Summary.info("villages", "Loaded " + DEFS.size() + " village plot(s) from packs"); }
        return !DEFS.isEmpty();
    }

    public static void register() {
        if (registered || DEFS.isEmpty()) { return; }
        registered = true;

        MapGenStructureIO.registerStructureComponent(ContentVillagePiece.class, COMPONENT);
        VillagerRegistry.instance().registerVillageCreationHandler(new Handler());
    }

    public static boolean filtering() {
        if (ContentControl.off(ContentControl.VILLAGES)) { return false; }

        return !names().isEmpty();
    }

    public static boolean blocked(Class<?> piece) {
        if (piece == null) { return false; }

        String name = piece.getSimpleName().toLowerCase(Locale.ROOT);
        return names().contains(name) == ContentControl.flag(ContentControl.VILLAGES, "villagePiecesAreBlacklist", Config.worldgen.villagePiecesAreBlacklist);
    }

    @Nullable public static VillageDef byName(String name) { return DEFS.get(name); }

    public static void reload() {
        DEFS.clear();
        named = null;
        loaded = false;
    }

    private static Set<String> names() {
        if (named == null) { named = Names.lower(ContentControl.list(ContentControl.VILLAGES, "villagePieces", Config.worldgen.villagePieces)); }

        return named;
    }

    private static boolean present(VillageDef def) {
        for (String name : def.requires) {
            if (!Loader.isModLoaded(name) && !PackManager.get().provides(name)) { return false; }
        }
        return true;
    }

    @Nullable private static VillageDef pick(Random random) {
        int total = 0;
        for (VillageDef def : DEFS.values()) { total += Math.max(1, def.weight); }
        if (total <= 0) { return null; }

        int roll = random.nextInt(total);
        for (VillageDef def : DEFS.values()) {
            roll -= Math.max(1, def.weight);
            if (roll < 0) { return def; }
        }
        return null;
    }

    public static final class Handler implements VillagerRegistry.IVillageCreationHandler {
        @Override public StructureVillagePieces.PieceWeight getVillagePieceWeight(Random random, int size) {
            int weight = 0;
            int least = Integer.MAX_VALUE;
            int most = 0;
            for (VillageDef def : DEFS.values()) {
                weight += Math.max(1, def.weight);
                least = Math.min(least, def.leastCount);
                most = Math.max(most, def.mostCount);
            }
            if (least == Integer.MAX_VALUE) { least = 0; }

            return new StructureVillagePieces.PieceWeight(ContentVillagePiece.class, weight, MathHelper.getInt(random, least + size, most + size));
        }

        @Override public Class<?> getComponentClass() { return ContentVillagePiece.class; }

        @Override public StructureVillagePieces.Village buildComponent(StructureVillagePieces.PieceWeight weight, StructureVillagePieces.Start start, List<StructureComponent> placed, Random random, int x, int y, int z, EnumFacing facing, int type) {
            VillageDef def = pick(random);
            if (def == null) { return null; }

            StructureBoundingBox box = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, def.width, def.height, def.depth, facing);
            for (StructureComponent piece : placed) {
                if (piece.getBoundingBox().intersectsWith(box)) { return null; }
            }

            return new ContentVillagePiece(start, type, box, facing, def);
        }
    }

    public static List<String> known() { return new ArrayList<>(DEFS.keySet()); }
}
