package mctmods.resourcedatapackloader.mixin.bop;

import mctmods.resourcedatapackloader.content.worldgen.ContentTerrain;
import mctmods.resourcedatapackloader.util.Summary;

import biomesoplenty.common.world.BOPWorldSettings;
import com.google.gson.JsonObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BOPWorldSettings.class, remap = false)
public abstract class MixinBOPWorldSettings {
    @Shadow public int seaLevel;
    @Shadow public boolean useCaves;
    @Shadow public boolean useDungeons;
    @Shadow public int dungeonChance;
    @Shadow public boolean useStrongholds;
    @Shadow public boolean useVillages;
    @Shadow public boolean useMineShafts;
    @Shadow public boolean useTemples;
    @Shadow public boolean useMonuments;
    @Shadow public boolean useMansions;
    @Shadow public boolean useRavines;
    @Shadow public boolean useWaterLakes;
    @Shadow public int waterLakeChance;
    @Shadow public boolean useLavaLakes;
    @Shadow public int lavaLakeChance;
    @Shadow public boolean useLavaOceans;

    @Inject(method = "fromConfigObj", at = @At("TAIL"), remap = false)
    private void rdpl$packSettings(CallbackInfo ci) {
        JsonObject settings = ContentTerrain.settings("BIOMESOP");
        if (settings == null) { return; }

        seaLevel = rdpl$number(settings, "seaLevel", seaLevel);
        dungeonChance = rdpl$number(settings, "dungeonChance", dungeonChance);
        waterLakeChance = rdpl$number(settings, "waterLakeChance", waterLakeChance);
        lavaLakeChance = rdpl$number(settings, "lavaLakeChance", lavaLakeChance);
        useCaves = rdpl$flag(settings, "useCaves", useCaves);
        useDungeons = rdpl$flag(settings, "useDungeons", useDungeons);
        useStrongholds = rdpl$flag(settings, "useStrongholds", useStrongholds);
        useVillages = rdpl$flag(settings, "useVillages", useVillages);
        useMineShafts = rdpl$flag(settings, "useMineShafts", useMineShafts);
        useTemples = rdpl$flag(settings, "useTemples", useTemples);
        useMonuments = rdpl$flag(settings, "useMonuments", useMonuments);
        useMansions = rdpl$flag(settings, "useMansions", useMansions);
        useRavines = rdpl$flag(settings, "useRavines", useRavines);
        useWaterLakes = rdpl$flag(settings, "useWaterLakes", useWaterLakes);
        useLavaLakes = rdpl$flag(settings, "useLavaLakes", useLavaLakes);
        useLavaOceans = rdpl$flag(settings, "useLavaOceans", useLavaOceans);
        Summary.info("terrain.bop.handed", "Told Biomes O' Plenty the sea level, caves, lakes and structure switches a pack asked for, which it does not read from its own settings");
    }

    @Unique private static int rdpl$number(JsonObject settings, String key, int fallback) {
        try { return settings.has(key) ? settings.get(key).getAsInt() : fallback; }
        catch (RuntimeException unreadable) { return fallback; }
    }

    @Unique private static boolean rdpl$flag(JsonObject settings, String key, boolean fallback) {
        try { return settings.has(key) ? settings.get(key).getAsBoolean() : fallback; }
        catch (RuntimeException unreadable) { return fallback; }
    }
}
