package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.def.BiomeDef;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IMobCategory;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.LightLayer;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import java.util.Locale;

public final class ContentSpawning {
    private ContentSpawning() {}

    public static void applyCaps() {
        if (ContentControl.off(ContentControl.SPAWNING)) { return; }
        cap(MobCategory.MONSTER, ContentControl.number(ContentControl.SPAWNING, "monsterCap", Config.worldgen.monsterCap()));
        cap(MobCategory.CREATURE, ContentControl.number(ContentControl.SPAWNING, "creatureCap", Config.worldgen.creatureCap()));
        cap(MobCategory.AMBIENT, ContentControl.number(ContentControl.SPAWNING, "ambientCap", Config.worldgen.ambientCap()));
        cap(MobCategory.WATER_CREATURE, ContentControl.number(ContentControl.SPAWNING, "waterCreatureCap", Config.worldgen.waterCreatureCap()));
    }

    private static void cap(MobCategory category, int wanted) {
        if (wanted < 0) { return; }
        int current = category.getMaxInstancesPerChunk();
        if (current == wanted) { return; }
        ((IMobCategory) (Object) category).rdpl$setMax(wanted);
        Summary.info("mobcaps." + category.getName(), "Set the " + category.getName().toLowerCase(Locale.ROOT) + " spawn cap to " + wanted + ", was " + current);
    }

    public static void onPositionCheck(MobSpawnEvent.PositionCheck event) {
        if (ContentControl.off(ContentControl.SPAWNING) || event.getSpawner() != null || event.getSpawnType() != MobSpawnType.NATURAL) { return; }
        Mob mob = event.getEntity();
        if (!(mob instanceof Enemy)) { return; }
        ServerLevel level = event.getLevel().getLevel();
        BlockPos pos = mob.blockPosition();
        int lightCap = ContentControl.number(ContentControl.SPAWNING, "monsterSpawnLight", Config.worldgen.monsterSpawnLight());
        if (lightCap >= 0 && level.getBrightness(LightLayer.BLOCK, pos) > lightCap) {
            event.setResult(Event.Result.DENY);
            return;
        }
        float rate = rateFor(level, pos);
        if (rate == 1.0F) { return; }
        if (rate <= 0.0F) {
            event.setResult(Event.Result.DENY);
            return;
        }
        if (rate < 1.0F) {
            if (level.random.nextFloat() >= rate) { event.setResult(Event.Result.DENY); }
            return;
        }
        if (level.random.nextFloat() < rate - 1.0F) { event.setResult(Event.Result.ALLOW); }
    }

    private static float rateFor(ServerLevel level, BlockPos pos) {
        boolean sky = level.canSeeSky(pos);
        boolean day = level.isDay();
        BiomeDef biome = level.getBiome(pos).unwrapKey().map(key -> ContentBiomes.def(key.location())).orElse(null);
        if (biome != null) {
            float wanted = biome.rate(sky, day);
            if (wanted >= 0.0F) { return wanted; }
        }
        if (sky) { return day ? ContentControl.decimal(ContentControl.SPAWNING, "surfaceDayMonsterRate", Config.worldgen.surfaceDayMonsterRate()) : ContentControl.decimal(ContentControl.SPAWNING, "surfaceNightMonsterRate", Config.worldgen.surfaceNightMonsterRate()); }
        return day ? ContentControl.decimal(ContentControl.SPAWNING, "undergroundDayMonsterRate", Config.worldgen.undergroundDayMonsterRate()) : ContentControl.decimal(ContentControl.SPAWNING, "undergroundNightMonsterRate", Config.worldgen.undergroundNightMonsterRate());
    }
}
