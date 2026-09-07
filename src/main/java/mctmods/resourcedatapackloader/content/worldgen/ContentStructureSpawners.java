package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Registered;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;

public final class ContentStructureSpawners {
    public static final String DUNGEONS = "dungeons";
    public static final String MINESHAFTS = "mineshafts";
    public static final String STRONGHOLDS = "strongholds";
    public static final String FORTRESSES = "netherbridges";
    private static final String KEY = "structureSpawners";
    private static final List<String> KNOWN = List.of(DUNGEONS, MINESHAFTS, FORTRESSES, STRONGHOLDS);
    private static final Map<String, List<String>> RAW = new LinkedHashMap<>();
    @Nullable private static Map<String, List<EntityType<?>>> picks;

    private ContentStructureSpawners() {}

    static void load(List<String> entries, List<String> touched) {
        synchronized (RAW) {
            RAW.clear();
            picks = null;
            for (String entry : entries) {
                String[] parts = ContentStructureControl.split(entry, KEY);
                if (parts == null) { continue; }
                String key = parts[0].trim().toLowerCase(Locale.ROOT);
                if (!KNOWN.contains(key)) {
                    ContentLog.LOGGER.error("{} names '{}', and only {} build a spawner, so that entry does nothing", KEY, parts[0].trim(), KNOWN);
                    continue;
                }
                List<String> names = new ArrayList<>();
                for (String one : parts[1].split(",")) {
                    if (!one.trim().isEmpty()) { names.add(one.trim().toLowerCase(Locale.ROOT)); }
                }
                if (names.isEmpty()) { continue; }
                RAW.put(key, names);
                touched.add(key + " spawner " + String.join(",", names));
            }
        }
    }

    private static Map<String, List<EntityType<?>>> picks() {
        synchronized (RAW) {
            if (picks != null) { return picks; }
            Map<String, List<EntityType<?>>> made = new LinkedHashMap<>();
            for (Map.Entry<String, List<String>> entry : RAW.entrySet()) {
                List<EntityType<?>> types = new ArrayList<>();
                for (String name : entry.getValue()) {
                    ResourceLocation id = ResourceLocation.tryParse(name);
                    EntityType<?> type = Registered.find(ForgeRegistries.ENTITY_TYPES, id);
                    if (type == null) { ContentLog.LOGGER.error("{} entry for {} names {}, which nothing registers, ignoring that name", KEY, entry.getKey(), name); }
                    else { types.add(type); }
                }
                if (!types.isEmpty()) { made.put(entry.getKey(), types); }
            }
            picks = made;
            return made;
        }
    }

    public static EntityType<?> pick(String structure, EntityType<?> vanilla, RandomSource random) {
        if (RAW.isEmpty()) { return vanilla; }
        List<EntityType<?>> wanted = picks().get(structure);
        if (wanted == null || wanted.isEmpty()) { return vanilla; }
        EntityType<?> chosen = wanted.get(random.nextInt(wanted.size()));
        ContentLog.LOGGER.debug("Spawner in {} set to {} in place of {}", structure, ForgeRegistries.ENTITY_TYPES.getKey(chosen), ForgeRegistries.ENTITY_TYPES.getKey(vanilla));
        return chosen;
    }
}
