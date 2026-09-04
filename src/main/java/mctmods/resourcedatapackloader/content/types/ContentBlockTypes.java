package mctmods.resourcedatapackloader.content.types;

import mctmods.resourcedatapackloader.content.ContentRegistry;
import mctmods.resourcedatapackloader.content.block.ContentBlock;
import mctmods.resourcedatapackloader.content.block.ContentBushBlock;
import mctmods.resourcedatapackloader.content.block.ContentCaneBlock;
import mctmods.resourcedatapackloader.content.block.ContentCropBlock;
import mctmods.resourcedatapackloader.content.block.ContentFallingBlock;
import mctmods.resourcedatapackloader.content.block.ContentLeavesBlock;
import mctmods.resourcedatapackloader.content.block.ContentLogBlock;
import mctmods.resourcedatapackloader.content.block.ContentSaplingBlock;
import mctmods.resourcedatapackloader.content.block.ContentTorchBlock;
import mctmods.resourcedatapackloader.content.block.ContentWallTorchBlock;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.BlockVariant;
import mctmods.resourcedatapackloader.content.def.GrowthDef;
import mctmods.resourcedatapackloader.util.ContentLog;
import mctmods.resourcedatapackloader.util.Registered;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentBlockTypes {
    public static final String BASIC = "basic";
    public static final String ORE = "ore";
    public static final String FALLING = "falling";
    public static final String SLAB = "slab";
    public static final String STAIRS = "stairs";
    public static final String FENCE = "fence";
    public static final String PANE = "pane";
    public static final String WALL = "wall";
    public static final String DOOR = "door";
    public static final String TRAPDOOR = "trapdoor";
    public static final String FENCE_GATE = "fence_gate";
    public static final String LADDER = "ladder";
    public static final String TORCH = "torch";
    public static final String LOG = "log";
    public static final String LEAVES = "leaves";
    public static final String SAPLING = "sapling";
    public static final String CROP = "crop";
    public static final String FLOWER = "flower";
    public static final String CANE = "cane";
    public static final String VINE = "vine";
    public static final String BANNER = "banner";
    public static final String PORTAL = "portal";
    private static final Set<String> KNOWN = Set.of(BASIC, ORE, FALLING, SLAB, STAIRS, FENCE, PANE, WALL, DOOR, TRAPDOOR, FENCE_GATE, LADDER, TORCH, LOG, LEAVES, SAPLING, CROP, FLOWER, CANE, VINE);
    private static final Set<String> LATER = Set.of(BANNER, PORTAL);
    private static final Set<String> PLANTS = Set.of(SAPLING, CROP, FLOWER, CANE, VINE);

    private ContentBlockTypes() {}

    public static boolean plant(String type) { return PLANTS.contains(type); }

    @Nullable public static String renderType(BlockDef def) {
        return switch (def.renderLayer()) {
            case "solid" -> "minecraft:solid";
            case "cutout" -> "minecraft:cutout";
            case "cutout_mipped" -> "minecraft:cutout_mipped";
            case "translucent" -> "minecraft:translucent";
            default -> switch (def.type()) {
                case LEAVES -> "minecraft:cutout_mipped";
                case CROP, SAPLING, FLOWER, CANE, VINE, LADDER, TORCH, DOOR, TRAPDOOR, PANE -> "minecraft:cutout";
                default -> def.opaque() ? null : "minecraft:cutout";
            };
        };
    }

    public static List<Created> create(BlockDef def, BlockVariant variant) {
        String type = def.type();
        if (LATER.contains(type)) {
            ContentLog.LOGGER.error("Block {} is a '{}', which this line does not carry yet, skipping it", variant.id(), type);
            return List.of();
        }
        if (!KNOWN.contains(type)) {
            ContentLog.LOGGER.error("Unknown block type '{}' in {}, treating it as '{}'. Known types are {}", type, def.key(), BASIC, KNOWN);
            type = BASIC;
        }
        BlockBehaviour.Properties properties = ContentTypes.properties(def, variant, plant(type));
        ResourceLocation id = variant.id();
        return switch (type) {
            case FALLING -> List.of(new Created(id, new ContentFallingBlock(def, properties), ContentRegistry.MAIN));
            case SLAB -> List.of(new Created(id, new SlabBlock(properties), ContentRegistry.MAIN));
            case STAIRS -> List.of(new Created(id, new StairBlock(base(def), properties), ContentRegistry.MAIN));
            case FENCE -> List.of(new Created(id, new FenceBlock(properties), ContentRegistry.MAIN));
            case PANE -> List.of(new Created(id, new IronBarsBlock(properties.noOcclusion()), ContentRegistry.MAIN));
            case WALL -> List.of(new Created(id, new WallBlock(properties), ContentRegistry.MAIN));
            case DOOR -> List.of(new Created(id, new DoorBlock(setType(def), properties.noOcclusion()), ContentRegistry.MAIN));
            case TRAPDOOR -> List.of(new Created(id, new TrapDoorBlock(setType(def), properties.noOcclusion()), ContentRegistry.MAIN));
            case FENCE_GATE -> List.of(new Created(id, new FenceGateBlock(WoodType.OAK, properties), ContentRegistry.MAIN));
            case LADDER -> List.of(new Created(id, new LadderBlock(properties.noOcclusion().noCollission()), ContentRegistry.MAIN));
            case TORCH -> {
                ContentTorchBlock torch = new ContentTorchBlock(def, properties.noCollission().instabreak().lightLevel(state -> Math.max(variant.light(), 14)));
                ContentWallTorchBlock wall = new ContentWallTorchBlock(def, ContentTypes.properties(def, variant, false).noCollission().instabreak().lightLevel(state -> Math.max(variant.light(), 14)));
                yield List.of(new Created(id, torch, ContentRegistry.MAIN), new Created(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), id.getPath() + "_wall"), wall, ContentRegistry.WALL));
            }
            case LOG -> List.of(new Created(id, new ContentLogBlock(def, properties), ContentRegistry.MAIN));
            case LEAVES -> List.of(new Created(id, new ContentLeavesBlock(def, properties.noOcclusion().randomTicks().isSuffocating((state, level, pos) -> false).isViewBlocking((state, level, pos) -> false)), ContentRegistry.MAIN));
            case SAPLING -> {
                if (def.sapling() == null) {
                    ContentLog.LOGGER.error("Block {} is a sapling but has no 'sapling' section, so there is nothing for it to grow into", variant.id());
                    yield List.of();
                }
                yield List.of(new Created(id, new ContentSaplingBlock(def, variant.id(), properties), ContentRegistry.MAIN));
            }
            case CROP -> List.of(new Created(id, new ContentCropBlock(def, properties), ContentRegistry.MAIN));
            case FLOWER -> List.of(new Created(id, new ContentBushBlock(def, def.growth() == null ? GrowthDef.bush() : def.growth(), properties), ContentRegistry.MAIN));
            case CANE -> {
                if (def.growth() == null) {
                    ContentLog.LOGGER.error("Block {} is a cane but has no 'growth' section, so it has no height to grow to", variant.id());
                    yield List.of();
                }
                yield List.of(new Created(id, new ContentCaneBlock(def, def.growth(), properties), ContentRegistry.MAIN));
            }
            case VINE -> List.of(new Created(id, new VineBlock(properties.noOcclusion()), ContentRegistry.MAIN));
            default -> List.of(new Created(id, new ContentBlock(def, properties), ContentRegistry.MAIN));
        };
    }

    @Nullable public static Item item(ContentRegistry.BlockEntry entry) {
        BlockDef def = entry.def();
        BlockVariant variant = entry.variant();
        Item.Properties properties = new Item.Properties().stacksTo(variant.maxSize()).rarity(ContentTypes.rarity(variant.rarity(), variant.id()));
        return switch (def.type()) {
            case CROP -> null;
            case TORCH -> {
                if (!entry.isMain()) { yield null; }
                ContentRegistry.BlockEntry wall = ContentRegistry.block(ResourceLocation.fromNamespaceAndPath(entry.id().getNamespace(), entry.id().getPath() + "_wall"));
                yield wall == null ? new BlockItem(entry.block(), properties) : new StandingAndWallBlockItem(entry.block(), wall.block(), properties, Direction.DOWN);
            }
            case DOOR -> new DoubleHighBlockItem(entry.block(), properties);
            default -> new BlockItem(entry.block(), properties);
        };
    }

    private static BlockSetType setType(BlockDef def) {
        return switch (def.material()) {
            case "wood" -> BlockSetType.OAK;
            case "iron", "anvil" -> BlockSetType.IRON;
            default -> BlockSetType.STONE;
        };
    }

    private static BlockState base(BlockDef def) {
        ResourceLocation named = ResourceLocation.tryParse(def.modelBlock());
        ContentRegistry.BlockEntry made = named == null ? null : ContentRegistry.block(named);
        if (made != null) { return made.block().defaultBlockState(); }
        Block block = Registered.find(BuiltInRegistries.BLOCK, named);
        return block == null || block == Blocks.AIR ? Blocks.STONE.defaultBlockState() : block.defaultBlockState();
    }

    public record Created(ResourceLocation id, Block block, String role) {}
}
