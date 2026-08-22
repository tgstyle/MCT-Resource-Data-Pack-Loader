package mctmods.resourcedatapackloader.util.compat;

import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.util.ContentLog;

import drzhark.customspawner.CustomSpawner;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class CmsRubicSpawns {
    private CmsRubicSpawns() {}

    public static void register() {
        if (!Loader.isModLoaded("customspawner")) { return; }
        MinecraftForge.EVENT_BUS.register(new Handler());
        ContentLog.LOGGER.info("Custom Mob Spawner strips the biome spawn lists and its own spawner keeps within the vanilla heights, so on rubic worlds the mobs it remembered are handed back for everything above 255 or below 0");
    }

    public static final class Handler {
        @SubscribeEvent public void onPotentialSpawns(WorldEvent.PotentialSpawns event) {
            int y = event.getPos().getY();
            if (y >= 0 && y < 256) { return; }
            World world = event.getWorld();
            if (world.isRemote || !((IRubicWorld) world).rdpl$isRubicWorld() || !event.getList().isEmpty()) { return; }
            event.getList().addAll(rememberedFor(world.getBiome(event.getPos()), event.getType()));
        }

        private static final Map<Biome, EnumMap<EnumCreatureType, List<Biome.SpawnListEntry>>> REMEMBERED = new IdentityHashMap<>();

        private static List<Biome.SpawnListEntry> rememberedFor(Biome biome, EnumCreatureType type) {
            List<Biome.SpawnListEntry> found = REMEMBERED.computeIfAbsent(biome, Handler::gather).get(type);
            return found == null ? Collections.emptyList() : found;
        }

        private static EnumMap<EnumCreatureType, List<Biome.SpawnListEntry>> gather(Biome biome) {
            EnumMap<EnumCreatureType, List<Biome.SpawnListEntry>> out = new EnumMap<>(EnumCreatureType.class);
            for (Map.Entry<String, ArrayList<Biome>> remembered : CustomSpawner.entityDefaultSpawnBiomes.entrySet()) {
                if (!remembered.getValue().contains(biome)) { continue; }
                Biome.SpawnListEntry held = CustomSpawner.defaultSpawnListEntryMap.get(remembered.getKey());
                if (held == null) { continue; }
                for (EnumCreatureType type : EnumCreatureType.values()) {
                    if (type.getCreatureClass().isAssignableFrom(held.entityClass)) { out.computeIfAbsent(type, key -> new ArrayList<>()).add(held); }
                }
            }
            return out;
        }
    }
}
