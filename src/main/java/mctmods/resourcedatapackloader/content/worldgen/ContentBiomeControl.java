package mctmods.resourcedatapackloader.content.worldgen;

import mctmods.resourcedatapackloader.mixin.AccessorBiomeName;
import mctmods.resourcedatapackloader.content.ContentControl;
import mctmods.resourcedatapackloader.content.def.WorldTemplateDef;
import mctmods.resourcedatapackloader.util.Blocked;
import mctmods.resourcedatapackloader.util.Config;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Names;
import mctmods.resourcedatapackloader.util.Summary;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentBiomeControl {
    private static final Blocked BLOCKED = new Blocked();
    private static final Set<Biome> SUBSTITUTED = new HashSet<>();
    private static final Map<BiomeProvider, Integer> DIMENSIONS = Collections.synchronizedMap(new IdentityHashMap<>());
    private static final Set<Integer> WARNED = Collections.synchronizedSet(new HashSet<>());
    @Nullable private static Biome replacement;
    private static boolean everythingBlocked;
    @Nullable private static Set<Integer> dimensions;

    private ContentBiomeControl() {}

    public static boolean enabled() {
        if (ContentControl.off(ContentControl.BIOMES)) { return false; }

        return ContentControl.flag(ContentControl.BIOMES, "blockBiomes", Config.worldgen.blockBiomes) || ContentControl.list(ContentControl.BIOMES, "biomeNames", Config.worldgen.biomeNames).length > 0;
    }

    public static void apply() {
        if (!enabled()) { return; }

        BLOCKED.clear();
        dimensions = new HashSet<>();
        for (int dimension : ContentControl.numbers(ContentControl.BIOMES, "blockBiomeDimensions", Config.worldgen.blockBiomeDimensions)) { dimensions.add(dimension); }

        Set<String> whitelist = Names.lower(ContentControl.list(ContentControl.BIOMES, "biomeWhitelist", Config.worldgen.biomeWhitelist));
        Set<String> names = Names.lower(ContentControl.list(ContentControl.BIOMES, "biomeNames", Config.worldgen.biomeNames));
        int removed = 0;

        for (BiomeManager.BiomeType type : BiomeManager.BiomeType.values()) {
            List<BiomeManager.BiomeEntry> entries = BiomeManager.getBiomes(type);
            if (entries == null || entries.isEmpty()) { continue; }

            List<BiomeManager.BiomeEntry> doomed = new ArrayList<>();
            for (BiomeManager.BiomeEntry entry : entries) {
                if (allowed(entry.biome, whitelist, names)) { continue; }
                doomed.add(entry);
            }

            if (doomed.isEmpty()) { continue; }
            if (doomed.size() == entries.size()) {
                ContentLog.LOGGER.info("Every biome in the {} group is blocked, so the group is left as it is rather than emptied. The replacement is applied to the finished biome map instead", type);
                continue;
            }

            for (BiomeManager.BiomeEntry entry : doomed) {
                BiomeManager.removeBiome(type, entry);
                BiomeManager.removeSpawnBiome(entry.biome);
                BiomeManager.removeVillageBiome(entry.biome);
                BiomeManager.removeStrongholdBiome(entry.biome);
                count(entry.biome);
                removed++;
            }
        }

        substitutions(whitelist, names);
        if (removed == 0) { return; }

        Summary.info("biomes.blocked", "Thinned " + removed + " biome entry/entries out of world generation");
        if (ContentControl.flag(ContentControl.BIOMES, "logBlockedBiomes", Config.worldgen.logBlockedBiomes)) { BLOCKED.report("biome(s)"); }
    }

    public static boolean everythingBlocked() { return everythingBlocked; }

    private static void substitutions(Set<String> whitelist, Set<String> names) {
        SUBSTITUTED.clear();
        replacement = null;
        everythingBlocked = false;

        Biome chosen = Biomes.VOID;

        int survivors = 0;
        for (Biome biome : ForgeRegistries.BIOMES) {
            if (biome == chosen) { continue; }
            if (allowed(biome, whitelist, names)) { survivors++; }
            else { SUBSTITUTED.add(biome); }
        }

        if (SUBSTITUTED.isEmpty()) { return; }

        ContentWorldTemplates.resolve(SUBSTITUTED);
        replacement = chosen;
        everythingBlocked = survivors == 0 && ContentWorldTemplates.isVoid();

        WorldTemplateDef template = ContentWorldTemplates.active();
        if (template == null) { ContentLog.LOGGER.info("Replacing {} blocked biome(s) with {} wherever they are generated, including oceans, mesas and hill variants that the biome lists do not reach. Set worldTemplate to fill them with real biomes instead", SUBSTITUTED.size(), chosen.getRegistryName()); }
        else { ContentLog.LOGGER.info("Replacing {} blocked biome(s) using world template {}, which maps them by role and falls back to {}", SUBSTITUTED.size(), template.getKey(), template.fallback.isEmpty() ? chosen.getRegistryName() : template.fallback); }

        if (everythingBlocked) { ContentLog.LOGGER.info("No biome survived blocking and the template is void, so the overworld is generated as a void world"); }
    }

    public static void substitute(BiomeProvider provider, @Nullable Biome[] biomes, int count) {
        Biome fallback = replacement;
        if (fallback == null || biomes == null || SUBSTITUTED.isEmpty()) { return; }
        if (outsideScope(provider)) { return; }

        Integer dimension = dimensionOf(provider);
        boolean templated = dimension == null || ContentWorldTemplates.appliesTo(dimension);

        int limit = Math.min(count, biomes.length);
        for (int i = 0; i < limit; i++) {
            if (!SUBSTITUTED.contains(biomes[i])) { continue; }

            Biome mapped = templated ? ContentWorldTemplates.replacement(biomes[i]) : null;
            biomes[i] = mapped == null ? fallback : mapped;
        }
    }

    public static boolean viable(BiomeProvider provider, Biome held, List<Biome> allowed) {
        Biome fallback = replacement;
        if (fallback == null || SUBSTITUTED.isEmpty() || !SUBSTITUTED.contains(held)) { return allowed.contains(held); }
        if (outsideScope(provider)) { return allowed.contains(held); }

        Integer dimension = dimensionOf(provider);
        boolean templated = dimension == null || ContentWorldTemplates.appliesTo(dimension);
        Biome mapped = templated ? ContentWorldTemplates.replacement(held) : null;
        return allowed.contains(mapped == null ? fallback : mapped);
    }

    private static boolean outsideScope(BiomeProvider provider) {
        Set<Integer> allowed = dimensions;
        if (allowed == null || allowed.isEmpty()) { return false; }

        Integer dimension = dimensionOf(provider);
        if (dimension == null) {
            if (WARNED.add(System.identityHashCode(provider))) {
                ContentLog.LOGGER.warn("A biome provider was asked for biomes before its world could be identified. Blocking is applied anyway, because skipping it would bake unblocked biomes into the chunks generated while a world is being created");
            }
            return false;
        }
        return allowed.contains(dimension) == ContentControl.flag(ContentControl.BIOMES, "blockBiomeDimensionsAreBlacklist", Config.worldgen.blockBiomeDimensionsAreBlacklist);
    }

    @Nullable private static Integer dimensionOf(BiomeProvider provider) {
        Integer known = DIMENSIONS.get(provider);
        if (known != null) { return known; }

        for (WorldServer world : DimensionManager.getWorlds()) {
            if (world == null || world.getBiomeProvider() != provider) { continue; }

            Integer dimension = world.provider.getDimension();
            DIMENSIONS.put(provider, dimension);
            return dimension;
        }
        return null;
    }

    @SubscribeEvent public static void onDecorate(DecorateBiomeEvent.Decorate event) {
        World world = event.getWorld();
        Biome biome = world.getBiome(event.getChunkPos().getBlock(8, 0, 8));

        if (biome instanceof ContentBiome && ((ContentBiome) biome).suppresses(event.getType())) {
            event.setResult(Event.Result.DENY);
            return;
        }

        if (SUBSTITUTED.isEmpty()) { return; }
        if (outsideScope(world.getBiomeProvider())) { return; }
        if (SUBSTITUTED.contains(biome)) { event.setResult(Event.Result.DENY); }
    }

    public static List<String> inspect(World world, BlockPos pos) {
        List<String> lines = new ArrayList<>();
        Biome biome = world.getBiome(pos);
        lines.add("Biome here: " + biome.getRegistryName() + " (id " + Biome.getIdForBiome(biome) + ", " + shownName(biome) + ")");
        lines.add("  its decorator: treesPerChunk=" + biome.decorator.treesPerChunk + " extraTreeChance=" + biome.decorator.extraTreeChance);
        lines.add("  blocked biome that should have been substituted: " + SUBSTITUTED.contains(biome));
        Integer dimension = DIMENSIONS.get(world.getBiomeProvider());
        lines.add("  provider dimension known: " + (dimension == null ? "NO, MixinWorldProvider has not fired for this world" : String.valueOf(dimension)));
        lines.add("  blockBiomes=" + ContentControl.flag(ContentControl.BIOMES, "blockBiomes", Config.worldgen.blockBiomes) + " blocked=" + SUBSTITUTED.size() + " template=" + (ContentWorldTemplates.active() == null ? "none" : ContentWorldTemplates.active().getKey()));
        lines.add("  biome topBlock=" + biome.topBlock.getBlock().getRegistryName() + " fillerBlock=" + biome.fillerBlock.getBlock().getRegistryName());

        BlockPos ground = world.getHeight(pos);
        lines.add("  ground at " + ground.getX() + "," + (ground.getY() - 1) + "," + ground.getZ() + " is " + world.getBlockState(ground.down()).getBlock().getRegistryName());
        lines.add("  one below that: " + world.getBlockState(ground.down(2)).getBlock().getRegistryName());
        lines.add("  deep stone at y=40: " + world.getBlockState(new BlockPos(pos.getX(), 40, pos.getZ())).getBlock().getRegistryName());
        return lines;
    }

    public static void remember(World world) {
        if (world.provider == null) { return; }

        DIMENSIONS.put(world.provider.getBiomeProvider(), world.provider.getDimension());
    }

    @SubscribeEvent public static void onWorldLoad(WorldEvent.Load event) { remember(event.getWorld()); }

    @SubscribeEvent public static void onWorldUnload(WorldEvent.Unload event) { DIMENSIONS.remove(event.getWorld().getBiomeProvider()); }

    private static boolean allowed(Biome biome, Set<String> whitelist, Set<String> names) {
        ResourceLocation name = biome.getRegistryName();
        if (!names.isEmpty() && named(biome, name, names) == ContentControl.flag(ContentControl.BIOMES, "biomeNamesAreBlacklist", Config.worldgen.biomeNamesAreBlacklist)) { return false; }
        if (name == null) { return true; }
        if (!ContentControl.flag(ContentControl.BIOMES, "blockBiomes", Config.worldgen.blockBiomes)) { return true; }

        return whitelist.contains(name.getNamespace().toLowerCase(Locale.ROOT));
    }

    public static String shownName(Biome biome) {
        String held = ((AccessorBiomeName) biome).rdpl$biomeName();
        if (held != null) { return held; }

        ResourceLocation name = biome.getRegistryName();
        return name == null ? "unknown" : name.getPath();
    }

    private static boolean named(Biome biome, @Nullable ResourceLocation name, Set<String> names) {
        if (names.contains(shownName(biome).toLowerCase(Locale.ROOT))) { return true; }
        return name != null && names.contains(name.toString().toLowerCase(Locale.ROOT));
    }

    private static void count(Biome biome) {
        ResourceLocation name = biome.getRegistryName();
        BLOCKED.count(name == null ? "unknown" : name.getNamespace());
    }
}
