package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.CaveRegionDef;
import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentBlock;
import mctmods.resourcedatapackloader.content.rubic.world.interfaces.IRubicWorld;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IEnumCreatureType;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;

public final class ContentSpawning {
    public static final Set<String> BEHAVIORS = Collections.unmodifiableSet(new LinkedHashSet<>(java.util.Arrays.asList(
            "path", "till")));
    private static final Map<String, Set<Block>> BY_BEHAVIOR = new HashMap<>();

    private ContentSpawning() {}

    public static void resolve() {
        BY_BEHAVIOR.clear();
        for (Map.Entry<ResourceLocation, Block> entry : ContentRegistry.registeredBlocks()) {
            Block block = entry.getValue();
            if (!(block instanceof IContentBlock)) { continue; }
            BlockDef def = ((IContentBlock) block).getDef();
            if (def == null) { continue; }
            for (String behavior : def.behavesAs) { add(behavior, block); }
        }
    }

    public static void applyCaps() {
        if (ContentControl.off(ContentControl.SPAWNING)) { return; }
        cap(EnumCreatureType.MONSTER, ContentControl.number(ContentControl.SPAWNING, "monsterCap", Config.worldgen.monsterCap));
        cap(EnumCreatureType.CREATURE, ContentControl.number(ContentControl.SPAWNING, "creatureCap", Config.worldgen.creatureCap));
        cap(EnumCreatureType.AMBIENT, ContentControl.number(ContentControl.SPAWNING, "ambientCap", Config.worldgen.ambientCap));
        cap(EnumCreatureType.WATER_CREATURE, ContentControl.number(ContentControl.SPAWNING, "waterCreatureCap", Config.worldgen.waterCreatureCap));
    }

    private static void cap(@Nonnull EnumCreatureType type, int wanted) {
        if (wanted < 0) { return; }
        IEnumCreatureType access = (IEnumCreatureType) (Object) type;
        int current = access.rdpl$getMaxNumberOfCreature();
        if (current == wanted) { return; }
        access.rdpl$setMaxNumberOfCreature(wanted);
        Summary.info("mobcaps", "Set the " + type.name().toLowerCase(Locale.ROOT) + " spawn cap to " + wanted + ", was " + current);
    }

    @SubscribeEvent public static void onPotentialSpawns(WorldEvent.PotentialSpawns event) {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        if (world.canSeeSky(pos) && pos.getY() < world.provider.getActualHeight()) { return; }
        CaveRegionDef region = ContentCaveRegions.regionAt(world, pos.getX(), pos.getY(), pos.getZ());
        if (region == null || !region.hasSpawns()) { return; }
        if (!region.keepDefaultSpawns) { event.getList().clear(); }
        event.getList().addAll(region.spawnsFor(event.getType()));
    }

    @SubscribeEvent public static void onCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
        World world = event.getWorld();
        DimensionDef dimension = ContentDimensions.byId(world.provider.getDimension());
        if (dimension != null && !dimension.spawning) {
            event.setResult(Event.Result.DENY);
            return;
        }
        if (!(event.getEntity() instanceof IMob)) {
            if (event.getSpawner() == null && deniedAboveWindow(world, event.getY())) { event.setResult(Event.Result.DENY); }
            return;
        }
        if (event.getSpawner() == null) {
            int lightCap = ContentControl.number(ContentControl.SPAWNING, "monsterSpawnLight", Config.worldgen.monsterSpawnLight);
            if (lightCap >= 0 && world.getLightFor(EnumSkyBlock.BLOCK, new BlockPos(event.getX(), event.getY(), event.getZ())) > lightCap) {
                event.setResult(Event.Result.DENY);
                return;
            }
        }
        float rate = rateFor(world, new BlockPos(event.getX(), event.getY(), event.getZ()));
        if (rate == 1.0F) { return; }
        if (rate <= 0.0F) {
            event.setResult(Event.Result.DENY);
            return;
        }
        if (rate < 1.0F) {
            if (world.rand.nextFloat() >= rate) { event.setResult(Event.Result.DENY); }
            return;
        }
        if (world.rand.nextFloat() < rate - 1.0F) { event.setResult(Event.Result.ALLOW); }
    }

    private static boolean deniedAboveWindow(World world, float y) {
        if (ContentControl.flag(ContentControl.SPAWNING, "skyAnimals", Config.worldgen.skyAnimals)) { return false; }
        return ((IRubicWorld) world).rdpl$isRubicWorld() && y > world.provider.getActualHeight();
    }

    private static float rateFor(World world, BlockPos pos) {
        boolean sky = world.canSeeSky(pos);
        boolean day = world.isDaytime();
        Biome biome = world.getBiome(pos);
        if (biome instanceof ContentBiome) {
            float wanted = ((ContentBiome) biome).monsterRate(sky, day);
            if (wanted >= 0.0F) { return wanted; }
        }
        if (sky) { return day ? ContentControl.decimal(ContentControl.SPAWNING, "surfaceDayMonsterRate", Config.worldgen.surfaceDayMonsterRate) : ContentControl.decimal(ContentControl.SPAWNING, "surfaceNightMonsterRate", Config.worldgen.surfaceNightMonsterRate); }
        return day ? ContentControl.decimal(ContentControl.SPAWNING, "undergroundDayMonsterRate", Config.worldgen.undergroundDayMonsterRate) : ContentControl.decimal(ContentControl.SPAWNING, "undergroundNightMonsterRate", Config.worldgen.undergroundNightMonsterRate);
    }

    public static boolean rateControlled() { return !ContentControl.off(ContentControl.SPAWNING); }

    public static boolean does(String behavior, Block block) {
        Set<Block> blocks = BY_BEHAVIOR.get(behavior);
        return blocks != null && blocks.contains(block);
    }

    public static boolean known(String behavior) { return BEHAVIORS.contains(behavior); }

    public static String describe() { return String.join(", ", BEHAVIORS); }

    public static String normalise(String behavior) { return behavior.trim().toLowerCase(Locale.ROOT); }

    private static void add(String behavior, Block block) { BY_BEHAVIOR.computeIfAbsent(behavior, key -> new HashSet<>()).add(block); }
}
