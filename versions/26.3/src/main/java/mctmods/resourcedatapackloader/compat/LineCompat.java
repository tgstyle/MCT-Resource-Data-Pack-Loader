package mctmods.resourcedatapackloader.compat;

import mctmods.resourcedatapackloader.content.extra.ContentFuels;
import mctmods.resourcedatapackloader.content.types.ContentTypes;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IBiomeNoise;
import mctmods.resourcedatapackloader.mixin.rdpl.common.ISurfaceSystem;
import mctmods.resourcedatapackloader.util.ConfigCore;
import mctmods.resourcedatapackloader.util.GameData;
import mctmods.resourcedatapackloader.util.WorldgenUp;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.BlockTransformer;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackMetadataResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Prediction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextMap;
import net.minecraft.util.random.WeightedList;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.BlockTransformers;
import net.minecraft.world.item.component.CookingFuel;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.sounds.AmbientLeavesBlockSoundPlayer;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.levelgen.synth.Noise;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders;
import net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat;
import net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.extensions.IBlockExtension;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class LineCompat {
    public static final PushReaction PUSH_NORMAL = PushReaction.PUSH_PULL;
    public static final PushReaction PUSH_DESTROY = PushReaction.POPPED;
    public static final PushReaction PUSH_BLOCK = PushReaction.IMMOVEABLE;
    public static final String FEATURES = "worldgen/feature";
    public static final String DENSITY_GRADIENT = "minecraft:gradient";
    public static final String GRADIENT_FROM = "from_coordinate";
    public static final String GRADIENT_TO = "to_coordinate";
    private static final String NOISE_SETTINGS = "worldgen/noise_settings/";
    private static final String CARVERS = "worldgen/carver/";

    private LineCompat() {}

    public static void registerConfig(ModContainer container, IConfigSpec spec) { container.registerConfig(ModConfig.Type.LOCAL, spec, ConfigCore.FILE); }

    public static void listen(IEventBus modBus) { modBus.addListener(ModifyDefaultComponentsEvent.class, event -> event.modifyMatching((_, _) -> true, LineCompat::fuel)); }

    private static void fuel(DataComponentMap.Builder components, HolderLookup.Provider context, Item item) {
        HolderLookup.RegistryLookup<Item> items = context.lookupOrThrow(Registries.ITEM);
        int time = ContentFuels.burnTime(item, tag -> items.get(tag).map(set -> set.contains(BuiltInRegistries.ITEM.wrapAsHolder(item))).orElse(false));
        if (time <= 0) { return; }
        CookingFuel held = components.get(DataComponents.COOKING_FUEL);
        ResolvableFloat speed = held == null ? ResolvableFloat.fromKey(ContextFloatProviders.COOKING_DEFAULT_SPEED_MULTIPLIER) : held.speedMultiplier();
        components.set(DataComponents.COOKING_FUEL, new CookingFuel(new ResolvableInt.Constant(time), speed));
    }

    @Nullable public static <T> T param(LootContext context, ContextKey<T> key) { return context.getOptional(key); }

    public static RegistryOps<JsonElement> lootOps(LootTableLoadEvent event) {
        HolderGetter.Provider registries = event.getRegistries();
        return RegistryOps.create(JsonOps.INSTANCE, new RegistryOps.RegistryInfoLookup() {
            @Override @Nonnull public <T> Optional<HolderGetter<T>> lookup(@Nonnull ResourceKey<? extends Registry<? extends T>> registryKey) { return registries.<T>lookup(registryKey).map(getter -> getter); }
        });
    }

    public static ContextMap displayContext(HolderLookup.Provider registries) { return ContextMap.builder().set(SlotDisplayContext.REGISTRIES, registries instanceof RegistryAccess access ? access : null).buildAndValidate(SlotDisplayContext.CONTEXT); }

    public static void swing(LivingEntity mob) { mob.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT, false); }

    public static void markVelocity(Entity entity) { entity.syncVelocity = true; }

    public static void makeInvulnerable(Entity entity) { entity.setPermanentlyInvulnerable(true); }

    public static void drop(Player player, ItemStack stack) { player.drop(stack, false, Prediction.SERVER_ONLY); }

    @SuppressWarnings("deprecation") public static boolean passable(BlockState state) {
        Block block = state.getBlock();
        return block == Blocks.COBWEB || block == Blocks.BAMBOO_SAPLING || !state.isSolid();
    }

    public static void fluidTags(BlockState state, Consumer<String> tag) {
        if (ContentTypes.washedAway(state) && !(state.getBlock() instanceof DoorBlock)) { tag.accept(BlockTags.WASHED_AWAY_BY_FLUIDS.location().toString()); }
    }

    public static BlockBehaviour.Properties notViewBlocking(BlockBehaviour.Properties properties) { return properties.isViewBlocking((_, _, _, _) -> false); }

    public static Item axe(Item.Properties properties, ToolMaterial tool, float damage, float speed) { return new Item(properties.axe(tool, damage, speed)); }

    public static Item shovel(Item.Properties properties, ToolMaterial tool, float damage, float speed) { return new Item(properties.shovel(tool, damage, speed)); }

    public static TreeGrower treeGrower(Identifier id, Identifier tree) { return new TreeGrower(id.toString(), WeightedList.of(ResourceKey.create(Registries.FEATURE, tree)), WeightedList.of(), WeightedList.of(), null); }

    public static Component title(DisplayInfo shown) { return shown.title(); }

    @Nullable public static IoSupplier<InputStream> vanillaResource(VanillaPackResources vanilla, PackType type, Identifier at) { return vanilla.fullResources().getResource(type, at); }

    public static Pack.ResourcesSupplier packSupplier(Function<PackLocationInfo, PackResources> open) {
        return new Pack.ResourcesSupplier() {
            @Override @Nonnull public PackMetadataResources openMetadata(@Nonnull PackLocationInfo location) { return open.apply(location); }

            @Override @Nonnull public Stream<PackResources> openResources(@Nonnull PackLocationInfo location, @Nonnull Pack.Metadata metadata) { return Stream.of(open.apply(location)); }
        };
    }

    public static MobSpawnSettings.SpawnerData spawner(EntityType<?> type, int min, int max) { return new MobSpawnSettings.SpawnerData(type, UniformInt.of(min, max)); }

    @SuppressWarnings("deprecation") public static void addSpawn(MobSpawnSettings.Builder spawns, MobCategory category, EntityType<?> type, int weight, int min, int max) { spawns.addSpawn(type, category, weight, UniformInt.of(min, max)); }

    public static StructureTemplateManager templates(MinecraftServer server) { return server.getStructureTemplateManager(); }

    public static StructureStart pieceAt(StructureManager structures, BlockPos at, Structure structure) { return structures.getStructureWithPieceAt(at.getX(), at.getY(), at.getZ(), structure); }

    public static List<StructureStart> starts(StructureManager structures, ChunkPos chunk, Predicate<Structure> matcher) { return structures.startsForStructure(chunk.x(), chunk.z(), matcher); }

    public static INoiseBiomes noiseBiomes(BiomeSource source, RandomState random) { return source.createUncachedResolver(random)::getNoiseBiome; }

    public static BiomeManager withBiomes(BiomeManager manager, INoiseBiomes biomes) { return manager.withDifferentSource(biomes::at); }

    public static IBergNoise bergNoise(RandomState random) {
        Noise surface = random.getOrCreateNoise(Noises.ICEBERG_SURFACE);
        Noise pillar = random.getOrCreateNoise(Noises.ICEBERG_PILLAR);
        Noise roof = random.getOrCreateNoise(Noises.ICEBERG_PILLAR_ROOF);
        return new IBergNoise() {
            @Override public double rise(int x, int z) { return Math.min(Math.abs(surface.get(x, 0.0, z) * 8.25), pillar.get(x * 1.28, 0.0, z * 1.28) * 15.0F); }

            @Override public double roof(int x, int z) { return Math.abs(roof.get(x * 1.17, 0.0, z * 1.17) * 1.5); }
        };
    }

    public static double surfaceNoise(RandomState random, int x, int z) { return ((ISurfaceSystem) random.surfaceSystem()).rdpl$getSurfaceNoise().get(x, 0.0, z); }

    public static float temperatureNoise(BlockPos pos) { return IBiomeNoise.rdpl$temperatureNoise().get(pos.getX() / 8.0F, pos.getZ() / 8.0F) * 8.0F; }

    public static RandomState randomState(NoiseGeneratorSettings settings, RegistryAccess registries, long seed) { return RandomState.create(registries.lookupOrThrow(Registries.NOISE), seed, settings); }

    public static Registry<?> carvers(RegistryAccess registries) { return registries.lookupOrThrow(Registries.CARVER); }

    @Nullable public static JsonObject worldgenIn(String path) {
        JsonObject json = GameData.json(Identifier.fromNamespaceAndPath("minecraft", path));
        if (json == null || !path.startsWith(NOISE_SETTINGS)) { return json; }
        JsonElement rule = json.remove("material_rule");
        if (rule != null) { json.add("surface_rule", inlineRule(rule)); }
        JsonElement router = json.get("noise_router");
        if (router != null && router.isJsonObject() && router.getAsJsonObject().has("final_density")) { router.getAsJsonObject().add("final_density", inlined(router.getAsJsonObject().get("final_density"), "worldgen/density_function/")); }
        return json;
    }

    public static JsonObject worldgenOut(String path, JsonObject json) {
        if (path.startsWith(NOISE_SETTINGS)) { return veinsBeforeDeepStone(WorldgenUp.noiseSettings(json)); }
        if (path.startsWith(FEATURES + "/") || path.startsWith(CARVERS)) { return WorldgenUp.configured(json); }
        return json;
    }

    private static JsonObject veinsBeforeDeepStone(JsonObject settings) {
        JsonElement rule = settings.get("material_rule");
        if (rule == null || !rule.isJsonObject() || !rule.getAsJsonObject().has("sequence") || !rule.getAsJsonObject().get("sequence").isJsonArray()) { return settings; }
        List<JsonElement> steps = rule.getAsJsonObject().getAsJsonArray("sequence").asList();
        int lastVein = -1;
        for (int index = 0; index < steps.size(); index++) {
            if (typed(steps.get(index), "minecraft:ore_vein") != null) { lastVein = index; }
        }
        List<JsonElement> moved = new ArrayList<>();
        for (int index = lastVein - 1; index >= 0; index--) {
            if (!deepStone(steps.get(index))) { continue; }
            moved.addFirst(steps.remove(index));
            lastVein--;
        }
        steps.addAll(lastVein + 1, moved);
        return settings;
    }

    private static boolean deepStone(JsonElement step) {
        JsonObject condition = typed(step, "minecraft:condition");
        JsonObject test = condition == null ? null : typed(condition.get("if_true"), "minecraft:vertical_gradient");
        return test != null && test.has("random_name") && test.get("random_name").getAsString().endsWith(":deep_stone");
    }

    @Nullable private static JsonObject typed(@Nullable JsonElement element, String type) {
        if (element == null || !element.isJsonObject()) { return null; }
        JsonElement found = element.getAsJsonObject().get("type");
        return found != null && found.isJsonPrimitive() && type.equals(found.getAsString()) ? element.getAsJsonObject() : null;
    }

    private static JsonElement inlineRule(JsonElement element) {
        JsonElement rule = inlined(element, "worldgen/material_rule/");
        if (!rule.isJsonObject()) { return rule; }
        JsonObject object = rule.getAsJsonObject();
        if (object.has("sequence") && object.get("sequence").isJsonArray()) {
            JsonArray steps = object.getAsJsonArray("sequence");
            for (int index = 0; index < steps.size(); index++) { steps.set(index, inlineRule(steps.get(index))); }
        }
        if (object.has("if_true")) { object.add("if_true", inlineCondition(object.get("if_true"))); }
        if (object.has("then_run")) { object.add("then_run", inlineRule(object.get("then_run"))); }
        return object;
    }

    private static JsonElement inlineCondition(JsonElement element) {
        JsonElement condition = inlined(element, "worldgen/material_condition/");
        if (condition.isJsonObject() && condition.getAsJsonObject().has("invert")) { condition.getAsJsonObject().add("invert", inlineCondition(condition.getAsJsonObject().get("invert"))); }
        return condition;
    }

    private static JsonElement inlined(JsonElement element, String folder) {
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) { return element; }
        Identifier id = Identifier.tryParse(element.getAsString());
        if (id == null) { return element; }
        JsonObject found = GameData.json(Identifier.fromNamespaceAndPath(id.getNamespace(), folder + id.getPath() + ".json"));
        return found == null ? element : found;
    }

    public static StructureTemplate.JigsawBlockInfo jigsaw(StructureTemplate.StructureBlockInfo info) { return StructureTemplate.JigsawBlockInfo.parse(info); }

    public static boolean flattens(ItemStack held) { return transforms(held, BlockTransformers.SHOVEL); }

    public static boolean tills(ItemStack held) { return transforms(held, BlockTransformers.HOE); }

    private static boolean transforms(ItemStack held, ResourceKey<BlockTransformer> kind) {
        Holder<BlockTransformer> transformer = held.get(DataComponents.BLOCK_TRANSFORMER);
        return transformer != null && transformer.is(kind);
    }

    public static SoundEvent flattenSound() { return SoundEvents.SHOVEL_FLATTEN.value(); }

    public static SoundEvent tillSound() { return SoundEvents.HOE_TILL.value(); }

    public static boolean lake(WorldGenLevel level, ChunkGenerator generator, RandomSource random, Block fluid, Block barrier, BlockPos at) { return Compat.lake(fluid, barrier).place(level, generator, random, at); }

    public interface INoiseBiomes {
        Holder<Biome> at(int x, int y, int z);
    }

    public interface IBergNoise {
        double rise(int x, int z);

        double roof(int x, int z);
    }

    public abstract static class FlatBase extends NoiseBasedChunkGenerator {
        protected FlatBase(BiomeSource source, Holder<NoiseGeneratorSettings> noise) { super(source, noise); }

        protected abstract FlatLevelSource flat();

        protected abstract Stream<Holder<StructureSet>> structureSets(HolderLookup<StructureSet> lookup);

        @Override @Nonnull public ChunkGeneratorStructureState createState(@Nonnull HolderLookup<StructureSet> lookup, @Nonnull RandomState random, long seed) { return ChunkGeneratorStructureState.createForFlat(random, seed, flat().getOrigin(random), biomeSource, structureSets(lookup)); }

        @Override @Nonnull public CompletableFuture<ChunkAccess> buildTerrain(@Nonnull ChunkAccess chunk, @Nonnull Blender blender, @Nonnull RandomState random, @Nonnull StructureManager structures, @Nonnull BiomeManager biomes, @Nullable WorldGenRegion carverRegion, @Nonnull Set<Holder<Biome>> possibleBiomes) { return flat().buildTerrain(chunk, blender, random, structures, biomes, carverRegion, possibleBiomes); }
    }

    public interface BiomeEdit extends BiomeModifier {
        void edit(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder);

        @Override default void modify(@Nonnull RegistryAccess registries, @Nonnull Holder<Biome> biome, @Nonnull Phase phase, @Nonnull ModifiableBiomeInfo.BiomeInfo.Builder builder) { edit(biome, phase, builder); }
    }

    public interface TreeSoil extends IBlockExtension {
        boolean keepsUnderTree();

        @Override default boolean onTreeGrow(@Nonnull BlockState state, @Nonnull WorldGenLevel level, @Nonnull BiConsumer<BlockPos, BlockState> placeFunction, @Nonnull RandomSource randomSource, @Nonnull BlockPos pos, @Nonnull TreeFeature tree) { return keepsUnderTree(); }
    }

    public record Conditions(Optional<Holder<LootItemCondition>> condition) {
        private static final MapCodec<Conditions> CODEC = LootItemCondition.CODEC.optionalFieldOf("condition").xmap(Conditions::new, Conditions::condition);
    }

    public abstract static class LootFunction extends LootItemConditionalFunction {
        protected LootFunction(Conditions conditions) { super(conditions.condition()); }

        protected static <T extends LootFunction> Products.P1<RecordCodecBuilder.Mu<T>, Conditions> fields(RecordCodecBuilder.Instance<T> instance) { return instance.group(Conditions.CODEC.forGetter(LootFunction::conditions)); }

        private Conditions conditions() { return new Conditions(condition); }
    }

    public record ModifierStart(Optional<Holder<LootItemCondition>> condition, int priority) {
        private static final MapCodec<ModifierStart> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                LootItemCondition.CODEC.optionalFieldOf("condition").forGetter(ModifierStart::condition),
                Codec.INT.optionalFieldOf("priority", IGlobalLootModifier.DEFAULT_PRIORITY).forGetter(ModifierStart::priority))
                .apply(instance, ModifierStart::new));
    }

    public abstract static class Modifier extends LootModifier {
        protected Modifier(ModifierStart start) { super(start.condition(), start.priority()); }

        protected static <T extends Modifier> Products.P1<RecordCodecBuilder.Mu<T>, ModifierStart> start(RecordCodecBuilder.Instance<T> instance) { return instance.group(ModifierStart.CODEC.forGetter(Modifier::start)); }

        private ModifierStart start() { return new ModifierStart(condition, priority); }
    }

    public abstract static class FallingBase extends FallingBlock {
        protected FallingBase(Properties properties) { super(properties); }
    }

    public abstract static class ContainerBase extends BaseEntityBlock {
        protected ContainerBase(Properties properties) { super(properties); }
    }

    public abstract static class VegetationBase extends VegetationBlock {
        protected VegetationBase(Properties properties) { super(properties); }
    }

    public abstract static class SaplingBase extends VegetationBase implements BonemealableBlock {
        protected SaplingBase(Properties properties) { super(properties); }

        protected abstract void grow(ServerLevel level, BlockPos pos, BlockState state, RandomSource random);

        @Override public boolean isValidBonemealTarget(@Nonnull LevelReader level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull BonemealSource source) { return true; }

        @Override public boolean isBonemealSuccess(@Nonnull Level level, @Nonnull RandomSource random, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull BonemealSource source) { return true; }

        @Override public void performBonemeal(@Nonnull ServerLevel level, @Nonnull RandomSource random, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull BonemealSource source) { grow(level, pos, state, random); }
    }

    public abstract static class LeavesBase extends LeavesBlock {
        protected LeavesBase(Properties properties) { super(AmbientLeavesBlockSoundPlayer.noAmbientSound(), properties); }
    }

    public abstract static class BellBase extends BellBlock {
        protected BellBase(Properties properties) { super(properties); }
    }
}
