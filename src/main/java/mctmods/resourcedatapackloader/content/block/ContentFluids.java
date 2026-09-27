package mctmods.resourcedatapackloader.content.block;

import mctmods.resourcedatapackloader.compat.LineCompat;
import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.def.FluidDef;
import mctmods.resourcedatapackloader.content.item.ContentBucketItem;
import mctmods.resourcedatapackloader.content.types.ContentTypes;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.fluid.BucketResourceHandler;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;

public final class ContentFluids {
    private static final Map<Identifier, Made> MADE = new LinkedHashMap<>();
    private static final int LEGACY_QUANTA = 8;
    private static final int MOST_QUANTA = 16;
    private static final int SOURCE_AMOUNT = 8;
    private static final int TICKS_PER_VISCOSITY = 200;
    private static boolean prepared;

    private ContentFluids() {}

    public static Collection<Made> made() { return Collections.unmodifiableCollection(MADE.values()); }

    public static void prepare() {
        if (prepared) { return; }
        prepared = true;
        for (FluidDef def : ContentRegistry.fluidDefs()) {
            if (!ContentRegistry.available(def.requires(), def.key())) { continue; }
            if (BuiltInRegistries.FLUID.containsKey(def.id())) {
                ContentLog.LOGGER.warn("A fluid named {} is already registered, {} will use the existing one", def.id(), def.key());
                continue;
            }
            MADE.put(def.id(), new Made(def));
        }
    }

    private static int dropOff(int quanta) {
        int reach = (quanta < 1 || quanta > MOST_QUANTA ? LEGACY_QUANTA : quanta) - 1;
        int best = 1;
        for (int drop = 2; drop <= SOURCE_AMOUNT; drop++) {
            if (Math.abs(reach(drop) - reach) < Math.abs(reach(best) - reach)) { best = drop; }
        }
        return best;
    }

    private static int reach(int drop) { return (SOURCE_AMOUNT - 1) / drop; }

    public static void registerTypes(BiConsumer<Identifier, ContentFluidType> out) {
        for (Made made : MADE.values()) { out.accept(made.def.id(), made.type); }
    }

    public static void registerFluids(BiConsumer<Identifier, BaseFlowingFluid> out) {
        for (Made made : MADE.values()) {
            out.accept(made.def.id(), made.still);
            out.accept(made.flowingId(), made.flowing);
        }
    }

    public static void registerBlocks(BiConsumer<Identifier, ContentLiquidBlock> out) {
        for (Made made : MADE.values()) {
            if (made.block != null) { out.accept(made.def.key(), made.block); }
        }
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        for (Made made : MADE.values()) {
            if (made.bucket != null) { event.registerItem(Capabilities.Fluid.ITEM, (ignored, access) -> new BucketResourceHandler(access), made.bucket); }
        }
    }

    public static void registerItems(BiConsumer<Identifier, Item> out) {
        for (Made made : MADE.values()) {
            if (made.bucket != null) { out.accept(made.bucketId(), made.bucket); }
        }
    }

    public static final class Made {
        public final FluidDef def;
        public final ContentFluidType type;
        public final BaseFlowingFluid still;
        public final BaseFlowingFluid flowing;
        @Nullable public final ContentLiquidBlock block;
        @Nullable public final BucketItem bucket;

        Made(FluidDef def) {
            this.def = def;
            this.type = new ContentFluidType(def);
            BaseFlowingFluid.Properties properties = new BaseFlowingFluid.Properties(() -> type, this::getStill, this::getFlowing).levelDecreasePerBlock(dropOff(def.quantaPerBlock())).tickRate(Math.max(1, def.viscosity() / TICKS_PER_VISCOSITY));
            if (def.createBlock()) { properties = properties.block(() -> Objects.requireNonNull(getBlock())); }
            if (def.bucket()) { properties = properties.bucket(() -> Objects.requireNonNull(getBucket())); }
            this.still = def.density() < 0 ? new ContentRisingFluid.Source(properties) : new BaseFlowingFluid.Source(properties);
            this.flowing = def.density() < 0 ? new ContentRisingFluid.Flowing(properties) : new BaseFlowingFluid.Flowing(properties);
            this.block = def.createBlock() ? new ContentLiquidBlock(def, still, blockProperties(def)) : null;
            this.bucket = def.bucket() ? new ContentBucketItem(still, type, new Item.Properties().setId(ResourceKey.create(Registries.ITEM, bucketId())).craftRemainder(Items.BUCKET).stacksTo(1)) : null;
        }

        public BaseFlowingFluid getStill() { return still; }

        public BaseFlowingFluid getFlowing() { return flowing; }

        @Nullable public ContentLiquidBlock getBlock() { return block; }

        @Nullable public BucketItem getBucket() { return bucket; }

        public Identifier flowingId() { return Identifier.fromNamespaceAndPath(def.id().getNamespace(), "flowing_" + def.name()); }

        public Identifier bucketId() { return Identifier.fromNamespaceAndPath(def.id().getNamespace(), def.name() + "_bucket"); }

        private static BlockBehaviour.Properties blockProperties(FluidDef def) {
            ContentTypes.Preset preset = ContentTypes.material(def.material(), def.key());
            MapColor color = def.lavaMaterial() ? MapColor.FIRE : preset.color() == MapColor.NONE ? MapColor.WATER : preset.color();
            BlockBehaviour.Properties properties = BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, def.key())).mapColor(color).replaceable().noCollision().strength(100.0F).pushReaction(LineCompat.PUSH_DESTROY).noLootTable().liquid().sound(SoundType.EMPTY);
            if (def.luminosity() > 0) { properties = properties.lightLevel(_ -> def.luminosity()); }
            return properties;
        }
    }
}
