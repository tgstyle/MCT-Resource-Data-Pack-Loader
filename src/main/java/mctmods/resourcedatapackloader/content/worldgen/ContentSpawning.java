package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.DimensionDef;
import mctmods.resourcedatapackloader.content.interfaces.IContentBlock;
import mctmods.resourcedatapackloader.mixin.AccessorEnumCreatureType;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
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
            "animals", "path", "till", "bush")));
    private static final Map<String, Set<Block>> BY_BEHAVIOR = new HashMap<>();

    private ContentSpawning() {}

    public static void resolve() {
        BY_BEHAVIOR.clear();
        for (Map.Entry<ResourceLocation, Block> entry : ContentRegistry.registeredBlocks()) {
            Block block = entry.getValue();
            if (!(block instanceof IContentBlock)) { continue; }

            BlockDef def = ((IContentBlock) block).getDef();
            if (def == null) { continue; }

            if (def.spawnsAnimals) { add("animals", block); }
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

        AccessorEnumCreatureType access = (AccessorEnumCreatureType) (Object) type;
        int current = access.rdpl$getMaxNumberOfCreature();
        if (current == wanted) { return; }

        access.rdpl$setMaxNumberOfCreature(wanted);
        Summary.info("mobcaps", "Set the " + type.name().toLowerCase(Locale.ROOT) + " spawn cap to " + wanted + ", was " + current);
    }

    @SubscribeEvent public static void onCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
        World world = event.getWorld();
        DimensionDef dimension = ContentDimensions.byId(world.provider.getDimension());
        if (dimension != null && !dimension.spawning) {
            event.setResult(Event.Result.DENY);
            return;
        }

        if (!(event.getEntity() instanceof IMob)) { return; }

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

    public static boolean sustainsAnimals(Block block) { return does("animals", block); }

    public static boolean known(String behavior) { return BEHAVIORS.contains(behavior); }

    public static String describe() { return String.join(", ", BEHAVIORS); }

    public static String normalise(String behavior) { return behavior.trim().toLowerCase(Locale.ROOT); }

    private static void add(String behavior, Block block) { BY_BEHAVIOR.computeIfAbsent(behavior, key -> new HashSet<>()).add(block); }
}
