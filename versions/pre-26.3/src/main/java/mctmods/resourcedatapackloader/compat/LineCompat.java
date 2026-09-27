package mctmods.resourcedatapackloader.compat;

import mctmods.resourcedatapackloader.content.extra.ContentFuels;
import mctmods.resourcedatapackloader.content.extra.ContentPotions;
import mctmods.resourcedatapackloader.mixin.rdpl.common.IBiomeNoise;
import mctmods.resourcedatapackloader.mixin.rdpl.common.ISurfaceSystem;
import mctmods.resourcedatapackloader.util.GameData;

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
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
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
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.extensions.IBlockExtension;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class LineCompat {
    public static final PushReaction PUSH_NORMAL = PushReaction.NORMAL;
    public static final PushReaction PUSH_DESTROY = PushReaction.DESTROY;
    public static final PushReaction PUSH_BLOCK = PushReaction.BLOCK;
    public static final String FEATURES = "worldgen/configured_feature";
    public static final String DENSITY_GRADIENT = "minecraft:y_clamped_gradient";
    public static final String GRADIENT_FROM = "from_y";
    public static final String GRADIENT_TO = "to_y";

    private LineCompat() {}

    public static void listen(@SuppressWarnings("unused") IEventBus modBus) {
        NeoForge.EVENT_BUS.addListener(RegisterBrewingRecipesEvent.class, event -> ContentPotions.applyBrewing(new ContentPotions.Brewing() {
            @Override public void mix(Holder<Potion> from, Item ingredient, Holder<Potion> to) { event.getBuilder().addMix(from, ingredient, to); }

            @Override public void recipe(Item input, Item ingredient, ItemStack output) { event.getBuilder().addRecipe(Ingredient.of(input), Ingredient.of(ingredient), output); }

            @Override public void container(Item item) { event.getBuilder().addContainer(item); }
        }));
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, FurnaceFuelBurnTimeEvent.class, LineCompat::burnTime);
    }

    private static void burnTime(FurnaceFuelBurnTimeEvent event) {
        ItemStack fuel = event.getItemStack();
        if (fuel.isEmpty()) { return; }
        int time = ContentFuels.burnTime(fuel.getItem(), fuel::is);
        if (time > 0) { event.setBurnTime(time); }
    }

    @Nullable public static <T> T param(LootContext context, ContextKey<T> key) { return context.getOptionalParameter(key); }

    public static RegistryOps<JsonElement> lootOps(LootTableLoadEvent event) { return RegistryOps.create(JsonOps.INSTANCE, event.getRegistries()); }

    public static ContextMap displayContext(HolderLookup.Provider registries) { return new ContextMap.Builder().withParameter(SlotDisplayContext.REGISTRIES, registries).create(SlotDisplayContext.CONTEXT); }

    public static void swing(LivingEntity mob) { mob.swing(InteractionHand.MAIN_HAND); }

    public static void markVelocity(Entity entity) { entity.hurtMarked = true; }

    public static void makeInvulnerable(Entity entity) { entity.setInvulnerable(true); }

    public static void drop(Player player, ItemStack stack) { player.drop(stack, false); }

    @SuppressWarnings("deprecation") public static boolean passable(BlockState state) { return !state.blocksMotion(); }

    @SuppressWarnings("unused") public static void fluidTags(BlockState state, Consumer<String> tag) {}

    public static BlockBehaviour.Properties notViewBlocking(BlockBehaviour.Properties properties) { return properties.isViewBlocking((_, _, _) -> false); }

    public static Item axe(Item.Properties properties, ToolMaterial tool, float damage, float speed) { return new AxeItem(tool, damage, speed, properties); }

    public static Item shovel(Item.Properties properties, ToolMaterial tool, float damage, float speed) { return new ShovelItem(tool, damage, speed, properties); }

    public static TreeGrower treeGrower(Identifier id, Identifier tree) { return new TreeGrower(id.toString(), Optional.empty(), Optional.of(ResourceKey.create(Registries.CONFIGURED_FEATURE, tree)), Optional.empty()); }

    public static Component title(DisplayInfo shown) { return shown.getTitle(); }

    @Nullable public static IoSupplier<InputStream> vanillaResource(VanillaPackResources vanilla, PackType type, Identifier at) { return vanilla.getResource(type, at); }

    public static Pack.ResourcesSupplier packSupplier(Function<PackLocationInfo, PackResources> open) {
        return new Pack.ResourcesSupplier() {
            @Override @Nonnull public PackResources openPrimary(@Nonnull PackLocationInfo location) { return open.apply(location); }

            @Override @Nonnull public PackResources openFull(@Nonnull PackLocationInfo location, @Nonnull Pack.Metadata metadata) { return open.apply(location); }
        };
    }

    public static MobSpawnSettings.SpawnerData spawner(EntityType<?> type, int min, int max) { return new MobSpawnSettings.SpawnerData(type, min, max); }

    public static void addSpawn(MobSpawnSettings.Builder spawns, MobCategory category, EntityType<?> type, int weight, int min, int max) { spawns.addSpawn(category, weight, spawner(type, min, max)); }

    public static StructureTemplateManager templates(MinecraftServer server) { return server.getStructureManager(); }

    public static StructureStart pieceAt(StructureManager structures, BlockPos at, Structure structure) { return structures.getStructureWithPieceAt(at, structure); }

    public static List<StructureStart> starts(StructureManager structures, ChunkPos chunk, Predicate<Structure> matcher) { return structures.startsForStructure(chunk, matcher); }

    public static INoiseBiomes noiseBiomes(BiomeSource source, RandomState random) {
        Climate.Sampler sampler = random.sampler();
        return (x, y, z) -> source.getNoiseBiome(x, y, z, sampler);
    }

    public static BiomeManager withBiomes(BiomeManager manager, INoiseBiomes biomes) { return manager.withDifferentSource(biomes::at); }

    public static IBergNoise bergNoise(RandomState random) {
        NormalNoise surface = random.getOrCreateNoise(Noises.ICEBERG_SURFACE);
        NormalNoise pillar = random.getOrCreateNoise(Noises.ICEBERG_PILLAR);
        NormalNoise roof = random.getOrCreateNoise(Noises.ICEBERG_PILLAR_ROOF);
        return new IBergNoise() {
            @Override public double rise(int x, int z) { return Math.min(Math.abs(surface.getValue(x, 0.0, z) * 8.25), pillar.getValue(x * 1.28, 0.0, z * 1.28) * 15.0); }

            @Override public double roof(int x, int z) { return Math.abs(roof.getValue(x * 1.17, 0.0, z * 1.17) * 1.5); }
        };
    }

    public static double surfaceNoise(RandomState random, int x, int z) { return ((ISurfaceSystem) random.surfaceSystem()).rdpl$getSurfaceNoise().getValue(x, 0.0, z); }

    public static float temperatureNoise(BlockPos pos) { return (float) (IBiomeNoise.rdpl$temperatureNoise().getValue(pos.getX() / 8.0F, pos.getZ() / 8.0F, false) * 8.0D); }

    public static RandomState randomState(NoiseGeneratorSettings settings, RegistryAccess registries, long seed) { return RandomState.create(settings, registries.lookupOrThrow(Registries.NOISE), seed); }

    public static Registry<?> carvers(RegistryAccess registries) { return registries.lookupOrThrow(Registries.CONFIGURED_CARVER); }

    @Nullable public static JsonObject worldgenIn(String path) { return GameData.json(Identifier.fromNamespaceAndPath("minecraft", path)); }

    public static JsonObject worldgenOut(@SuppressWarnings("unused") String path, JsonObject json) { return json; }

    public static StructureTemplate.JigsawBlockInfo jigsaw(StructureTemplate.StructureBlockInfo info) { return StructureTemplate.JigsawBlockInfo.of(info); }

    public static boolean flattens(ItemStack held) { return held.canPerformAction(ItemAbilities.SHOVEL_FLATTEN); }

    public static boolean tills(ItemStack held) { return held.canPerformAction(ItemAbilities.HOE_TILL); }

    public static SoundEvent flattenSound() { return SoundEvents.SHOVEL_FLATTEN; }

    public static SoundEvent tillSound() { return SoundEvents.HOE_TILL; }

    public static boolean lake(WorldGenLevel level, ChunkGenerator generator, RandomSource random, Block fluid, Block barrier, BlockPos at) { return Feature.LAKE.place(Compat.lake(fluid, barrier), level, generator, random, at); }

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

        @Override @Nonnull public ChunkGeneratorStructureState createState(@Nonnull HolderLookup<StructureSet> lookup, @Nonnull RandomState random, long seed) { return ChunkGeneratorStructureState.createForFlat(random, seed, biomeSource, structureSets(lookup)); }

        @Override public void buildSurface(@Nonnull WorldGenRegion level, @Nonnull StructureManager structures, @Nonnull RandomState random, @Nonnull ChunkAccess chunk) {}

        @Override @Nonnull public CompletableFuture<ChunkAccess> fillFromNoise(@Nonnull Blender blender, @Nonnull RandomState random, @Nonnull StructureManager structures, @Nonnull ChunkAccess chunk) { return flat().fillFromNoise(blender, random, structures, chunk); }

        @Override public void applyCarvers(@Nonnull WorldGenRegion level, long seed, @Nonnull RandomState random, @Nonnull BiomeManager biomes, @Nonnull StructureManager structures, @Nonnull ChunkAccess chunk) {}
    }

    public interface BiomeEdit extends BiomeModifier {
        void edit(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder);

        @Override default void modify(@Nonnull Holder<Biome> biome, @Nonnull Phase phase, @Nonnull ModifiableBiomeInfo.BiomeInfo.Builder builder) { edit(biome, phase, builder); }
    }

    public interface TreeSoil extends IBlockExtension {
        boolean keepsUnderTree();

        @Override default boolean onTreeGrow(@Nonnull BlockState state, @Nonnull WorldGenLevel level, @Nonnull BiConsumer<BlockPos, BlockState> placeFunction, @Nonnull RandomSource randomSource, @Nonnull BlockPos pos, @Nonnull TreeConfiguration config) { return keepsUnderTree(); }
    }

    public record Conditions(List<LootItemCondition> list) {
        private static final MapCodec<Conditions> CODEC = LootItemCondition.DIRECT_CODEC.listOf().optionalFieldOf("conditions", List.of()).xmap(Conditions::new, Conditions::list);
    }

    public abstract static class LootFunction extends LootItemConditionalFunction {
        protected LootFunction(Conditions conditions) { super(conditions.list()); }

        protected static <T extends LootFunction> Products.P1<RecordCodecBuilder.Mu<T>, Conditions> fields(RecordCodecBuilder.Instance<T> instance) { return instance.group(Conditions.CODEC.forGetter(LootFunction::conditions)); }

        private Conditions conditions() { return new Conditions(predicates); }
    }

    public record ModifierStart(LootItemCondition[] conditions, int priority) {
        private static final MapCodec<ModifierStart> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                IGlobalLootModifier.LOOT_CONDITIONS_CODEC.fieldOf("conditions").forGetter(ModifierStart::conditions),
                Codec.INT.optionalFieldOf("priority", IGlobalLootModifier.DEFAULT_PRIORITY).forGetter(ModifierStart::priority))
                .apply(instance, ModifierStart::new));
    }

    public abstract static class Modifier extends LootModifier {
        protected Modifier(ModifierStart start) { super(start.conditions(), start.priority()); }

        protected static <T extends Modifier> Products.P1<RecordCodecBuilder.Mu<T>, ModifierStart> start(RecordCodecBuilder.Instance<T> instance) { return instance.group(ModifierStart.CODEC.forGetter(Modifier::start)); }

        private ModifierStart start() { return new ModifierStart(conditions, priority); }
    }

    public abstract static class FallingBase extends FallingBlock {
        protected FallingBase(Properties properties) { super(properties); }

        @Override @Nonnull protected MapCodec<? extends FallingBlock> codec() { return MapCodec.unit(this); }
    }

    public abstract static class ContainerBase extends BaseEntityBlock {
        protected ContainerBase(Properties properties) { super(properties); }

        @Override @Nonnull protected MapCodec<? extends BaseEntityBlock> codec() { return MapCodec.unit(this); }
    }

    public abstract static class VegetationBase extends VegetationBlock {
        protected VegetationBase(Properties properties) { super(properties); }

        @Override @Nonnull protected MapCodec<? extends VegetationBlock> codec() { return MapCodec.unit(this); }
    }

    public abstract static class SaplingBase extends VegetationBase implements BonemealableBlock {
        protected SaplingBase(Properties properties) { super(properties); }

        protected abstract void grow(ServerLevel level, BlockPos pos, BlockState state, RandomSource random);

        @Override public boolean isValidBonemealTarget(@Nonnull LevelReader level, @Nonnull BlockPos pos, @Nonnull BlockState state) { return true; }

        @Override public boolean isBonemealSuccess(@Nonnull Level level, @Nonnull RandomSource random, @Nonnull BlockPos pos, @Nonnull BlockState state) { return true; }

        @Override public void performBonemeal(@Nonnull ServerLevel level, @Nonnull RandomSource random, @Nonnull BlockPos pos, @Nonnull BlockState state) { grow(level, pos, state, random); }
    }

    public abstract static class LeavesBase extends LeavesBlock {
        protected LeavesBase(Properties properties) { super(0.0F, properties); }

        @Override @Nonnull public MapCodec<? extends LeavesBlock> codec() { return MapCodec.unit(this); }

        @Override protected void spawnFallingLeavesParticle(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull RandomSource random) {}
    }

    public abstract static class BellBase extends BellBlock {
        protected BellBase(Properties properties) { super(properties); }

        @Override @Nonnull public MapCodec<BellBlock> codec() { return MapCodec.unit(this); }
    }
}
