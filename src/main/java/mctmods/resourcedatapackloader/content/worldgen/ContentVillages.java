package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.def.StructureMapDef;
import mctmods.resourcedatapackloader.content.def.VillageDef;
import mctmods.resourcedatapackloader.pack.PackManager;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Json;
import mctmods.resourcedatapackloader.util.Settings;
import mctmods.resourcedatapackloader.util.Summary;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IStructureTemplatePool;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentVillages {
    private static final Gson GSON = new Gson();
    private static final int LARGEST_LEAST = 13;
    private static final int[][] VANILLA_LIMITS = {{2, 1, 4, 2}, {0, 1, 1, 1}, {0, 1, 2, 1}, {2, 1, 5, 3}, {0, 1, 2, 1}, {1, 1, 4, 1}, {2, 1, 4, 2}, {0, 0, 1, 1}, {0, 1, 3, 2}};
    private static final Map<ResourceLocation, VillageDef> DEFS = new LinkedHashMap<>();
    private static final String FIELD = "_farm_";
    private static final Map<ResourceLocation, VillageDef> VANILLA = new LinkedHashMap<>();
    private static final Map<ResourceLocation, Rotation> TURNS = new LinkedHashMap<>();
    private static final Set<String> GROWN = new LinkedHashSet<>();
    private static boolean loaded;

    private ContentVillages() {}

    public static void load() {
        if (loaded) { return; }
        loaded = true;
        if (Config.definitionsOff() || !Config.content.villages()) { return; }
        Json.eachFile(PackManager.VILLAGES, "village plot", (key, contents) -> {
            if (ContentRegistry.reserved(key)) { return; }
            VillageDef def = parse(key, contents);
            if (def == null) { return; }
            if (!ContentRegistry.available(def.requires(), key)) {
                ContentLog.LOGGER.debug("Village plot {} needs {}, which is not here, so it is left out", key, def.requires());
                return;
            }
            DEFS.put(key, def);
        });
        if (!DEFS.isEmpty()) { Summary.info("villages", "Loaded " + DEFS.size() + " village plot definition(s) from packs"); }
    }

    public static void joinVillages(RegistryAccess registries) {
        if (DEFS.isEmpty()) { return; }
        Registry<StructureTemplatePool> pools = registries.registryOrThrow(Registries.TEMPLATE_POOL);
        int joined = 0;
        for (String type : CityGround.VILLAGE_TYPES) {
            StructureTemplatePool pool = pools.get(ResourceLocation.fromNamespaceAndPath("minecraft", "village/" + type + "/houses"));
            if (pool == null) { continue; }
            ObjectArrayList<StructurePoolElement> held = ((IStructureTemplatePool) pool).rdpl$templates();
            if (held.stream().anyMatch(ContentPlotPoolElement.class::isInstance)) { continue; }
            for (VillageDef def : allowed()) {
                if (!def.template() || vanillaHouse(def) || ContentStructureMaps.def(ResourceLocation.tryParse(def.structure())) != null) { continue; }
                ContentPlotPoolElement element = ContentPlotPoolElement.of(def);
                for (int copy = 0; copy < Math.max(1, def.weight()); copy++) { held.add(element); }
                joined++;
            }
        }
        if (joined > 0) { Summary.info("villages.join", "Village plot definitions join the game's own villages: " + joined + " house entr(ies) across " + CityGround.VILLAGE_TYPES.size() + " village types"); }
    }

    public static void measure(@Nullable StructureTemplateManager manager) {
        for (Map.Entry<ResourceLocation, VillageDef> entry : DEFS.entrySet()) {
            VillageDef grown = grown(entry.getValue(), manager);
            if (grown != entry.getValue()) { entry.setValue(grown); }
        }
    }

    public record House(Vec3i size, Rotation turn) {}

    public static void vanilla(Map<ResourceLocation, House> houses) {
        VANILLA.clear();
        TURNS.clear();
        for (Map.Entry<ResourceLocation, House> house : houses.entrySet()) {
            Vec3i size = house.getValue().size();
            TURNS.put(house.getKey(), house.getValue().turn());
            VANILLA.put(house.getKey(), new VillageDef(house.getKey(), VillageDef.TEMPLATE, 3, 1, 4, Math.max(3, size.getX()), Math.max(1, size.getY()), Math.max(3, size.getZ()), 2,
                    List.of(), "minecraft:oak_log", "minecraft:farmland", true, 2, house.getKey().toString(), "minecraft:dirt", 100, "", 0, "", 1, 1, 1, List.of()));
        }
    }

    public static boolean vanillaHouse(VillageDef def) { return VANILLA.containsKey(def.key()); }

    public static boolean vanillaField(VillageDef def) { return vanillaHouse(def) && def.key().getPath().contains(FIELD); }

    public static Rotation turnOf(VillageDef def) { return TURNS.getOrDefault(def.key(), Rotation.NONE); }

    private static VillageDef grown(VillageDef def, @Nullable StructureTemplateManager manager) {
        if (!def.template()) { return def; }
        ResourceLocation named = ResourceLocation.tryParse(def.structure());
        StructureMapDef map = named == null ? null : ContentStructureMaps.def(named);
        if (map != null) { return sized(def, map.cellsWide() * map.cell(), (map.layers().size() - map.ground()) * map.cell(), map.cellsDeep() * map.cell()); }
        if (manager == null || named == null) { return def; }
        Optional<StructureTemplate> template = manager.get(named);
        if (template.isEmpty()) { return def; }
        Vec3i size = template.get().getSize(Rotation.NONE);
        if (size.getX() <= def.width() && size.getY() <= def.height() && size.getZ() <= def.depth()) { return def; }
        if (GROWN.add(def.key().toString())) { ContentLog.LOGGER.warn("Village plot {} declares {}x{}x{} but its template {} measures {}x{}x{}, so the plot is grown to fit rather than cutting the template short", def.key(), def.width(), def.height(), def.depth(), def.structure(), size.getX(), size.getY(), size.getZ()); }
        return sized(def, Math.max(def.width(), size.getX()), Math.max(def.height(), size.getY()), Math.max(def.depth(), size.getZ()));
    }

    private static VillageDef sized(VillageDef def, int width, int height, int depth) {
        if (width == def.width() && height == def.height() && depth == def.depth()) { return def; }
        return new VillageDef(def.key(), def.type(), def.weight(), def.leastCount(), def.mostCount(), width, height, depth, def.apron(), def.crops(), def.edge(), def.soil(), def.water(), def.rowWidth(),
                def.structure(), def.ground(), def.integrity(), def.lootTable(), def.villagers(), def.villagerEntity(), def.villagerX(), def.villagerY(), def.villagerZ(), def.requires());
    }

    @Nullable public static VillageDef byKey(String named) {
        ResourceLocation key = ResourceLocation.tryParse(named);
        if (key == null) { return null; }
        VillageDef def = DEFS.get(key);
        return def != null ? def : VANILLA.get(key);
    }

    private static Set<String> names() { return Settings.lower(ContentControl.list(ContentControl.STRUCTURES, "villagePieces", Config.worldgen.villagePieces())); }

    private static boolean blacklist() { return ContentControl.flag(ContentControl.STRUCTURES, "villagePiecesAreBlacklist", Config.worldgen.villagePiecesAreBlacklist()); }

    private static boolean named(Set<String> names, ResourceLocation key) { return names.contains(key.toString().toLowerCase(Locale.ROOT)) || names.contains(key.getPath().toLowerCase(Locale.ROOT)); }

    private static boolean listed(Set<String> names, VillageDef def) {
        if (def.template() && !def.structure().isEmpty()) {
            ResourceLocation template = ResourceLocation.tryParse(def.structure());
            if (template != null && named(names, template)) { return true; }
        }
        return named(names, def.key());
    }

    public static boolean emptied(ResourceLocation template) {
        if (ContentControl.off(ContentControl.STRUCTURES) || !blacklist()) { return false; }
        Set<String> names = names();
        return !names.isEmpty() && named(names, template);
    }

    public static List<VillageDef> allowed() { return allowed(ContentControl.off(ContentControl.STRUCTURES)); }

    private static List<VillageDef> allowed(boolean every) {
        Set<String> names = every ? Set.of() : names();
        boolean blacklist = blacklist();
        List<VillageDef> found = new ArrayList<>();
        for (Map<ResourceLocation, VillageDef> held : List.of(VANILLA, DEFS)) {
            for (VillageDef def : held.values()) {
                if (names.isEmpty() || listed(names, def) != blacklist) { found.add(def); }
            }
        }
        return found;
    }

    private static int chance(VillageDef def) { return Math.max(1, def.weight()) * Math.max(1, Math.max(def.width(), def.depth())); }

    public static int largestPlot() {
        int largest = LARGEST_LEAST;
        for (VillageDef def : allowed(false)) { largest = Math.max(largest, Math.max(def.width(), def.depth())); }
        return largest;
    }

    public static int plotsLeast() {
        int least = Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePlotsLeast", Config.worldgen.villagePlotsLeast()));
        int most = plotsMost();
        return most > 0 ? Math.min(least, most) : least;
    }

    public static int plotsMost() { return Math.max(0, ContentControl.number(ContentControl.VILLAGES, "villagePlotsMost", Config.worldgen.villagePlotsMost())); }

    public static int frontLimit(RandomSource random) {
        List<VillageDef> choices = allowed();
        if (choices.isEmpty()) { return 0; }
        int grown = plotsLeast() > 0 ? Math.max(16, plotsLeast() / 32) : 0;
        int limit = choices.stream().anyMatch(ContentVillages::vanillaHouse) ? vanillaLimit(random, grown) : 0;
        int least = Integer.MAX_VALUE;
        int most = 0;
        for (VillageDef def : choices) {
            if (vanillaHouse(def)) { continue; }
            least = Math.min(least, def.leastCount());
            most = Math.max(most, def.mostCount());
        }
        if (least == Integer.MAX_VALUE) { return limit; }
        return limit + Mth.randomBetweenInclusive(random, least + grown, Math.max(least, most) + grown);
    }

    private static int vanillaLimit(RandomSource random, int size) {
        int limit = 0;
        for (int[] piece : VANILLA_LIMITS) { limit += Mth.randomBetweenInclusive(random, piece[0] + piece[1] * size, piece[2] + piece[3] * size); }
        return limit;
    }

    @Nullable public static VillageDef pick(List<VillageDef> choices, RandomSource random, int widest, int deepest) {
        int total = 0;
        for (VillageDef def : choices) {
            if (def.width() <= widest && def.depth() <= deepest) { total += chance(def); }
        }
        if (total <= 0) { return null; }
        int roll = random.nextInt(total);
        for (VillageDef def : choices) {
            if (def.width() > widest || def.depth() > deepest) { continue; }
            roll -= chance(def);
            if (roll < 0) { return def; }
        }
        return null;
    }

    @Nullable private static VillageDef parse(ResourceLocation key, String contents) {
        JsonObject json = GSON.fromJson(contents, JsonObject.class);
        if (json == null) {
            ContentLog.LOGGER.error("Village plot {} is empty, so it is dropped", key);
            return null;
        }
        String type = GsonHelper.getAsString(json, "type", VillageDef.FARM).trim().toLowerCase(Locale.ROOT);
        if (!VillageDef.FARM.equals(type) && !VillageDef.TEMPLATE.equals(type)) {
            ContentLog.LOGGER.error("Village plot {} asks for type '{}', which is not {} or {}, so {} stands in", key, type, VillageDef.FARM, VillageDef.TEMPLATE, VillageDef.FARM);
            type = VillageDef.FARM;
        }
        String structure = GsonHelper.getAsString(json, "structure", "");
        if (VillageDef.TEMPLATE.equals(type) && structure.isEmpty()) {
            ContentLog.LOGGER.error("Village plot {} is a template but names no structure, so it is dropped", key);
            return null;
        }
        return grown(new VillageDef(key, type,
                Math.max(1, GsonHelper.getAsInt(json, "weight", 3)),
                Math.max(0, GsonHelper.getAsInt(json, "leastCount", 1)),
                Math.max(0, GsonHelper.getAsInt(json, "mostCount", 4)),
                Math.max(3, GsonHelper.getAsInt(json, "width", 7)),
                Math.max(1, GsonHelper.getAsInt(json, "height", 4)),
                Math.max(3, GsonHelper.getAsInt(json, "depth", 9)),
                Math.max(0, GsonHelper.getAsInt(json, "apron", 2)),
                Json.strings(json, "crops"),
                GsonHelper.getAsString(json, "edge", "minecraft:oak_log"),
                GsonHelper.getAsString(json, "soil", "minecraft:farmland"),
                GsonHelper.getAsBoolean(json, "water", true),
                Math.max(1, GsonHelper.getAsInt(json, "rowWidth", 2)),
                structure,
                GsonHelper.getAsString(json, "ground", "minecraft:dirt"),
                Mth.clamp(GsonHelper.getAsInt(json, "integrity", 100), 1, 100),
                GsonHelper.getAsString(json, "lootTable", ""),
                Math.max(0, GsonHelper.getAsInt(json, "villagers", 0)),
                GsonHelper.getAsString(json, "villagerEntity", ""),
                GsonHelper.getAsInt(json, "villagerX", 1),
                GsonHelper.getAsInt(json, "villagerY", 1),
                GsonHelper.getAsInt(json, "villagerZ", 1),
                Json.strings(json, "requires")), null);
    }
}
