package mctmods.resourcedatapackloader.common.types;

import mctmods.resourcedatapackloader.common.blocks.ContentBlock;
import mctmods.resourcedatapackloader.common.blocks.ContentFallingBlock;
import mctmods.resourcedatapackloader.common.blocks.ContentStates;
import mctmods.resourcedatapackloader.common.blocks.banner.ContentBannerBlock;
import mctmods.resourcedatapackloader.common.blocks.banner.ContentWallBannerBlock;
import mctmods.resourcedatapackloader.common.blocks.bell.ContentBellBlock;
import mctmods.resourcedatapackloader.common.blocks.container.ContentContainerBlock;
import mctmods.resourcedatapackloader.common.blocks.light.ContentTorchBlock;
import mctmods.resourcedatapackloader.common.blocks.light.ContentWallTorchBlock;
import mctmods.resourcedatapackloader.common.blocks.plant.ContentBushBlock;
import mctmods.resourcedatapackloader.common.blocks.plant.ContentCaneBlock;
import mctmods.resourcedatapackloader.common.blocks.plant.ContentCropBlock;
import mctmods.resourcedatapackloader.common.blocks.plant.ContentSaplingBlock;
import mctmods.resourcedatapackloader.common.blocks.plant.ContentVineBlock;
import mctmods.resourcedatapackloader.common.blocks.portal.ContentPortalBlock;
import mctmods.resourcedatapackloader.common.blocks.portal.ContentPortwayBlock;
import mctmods.resourcedatapackloader.common.blocks.wood.ContentLeavesBlock;
import mctmods.resourcedatapackloader.common.blocks.wood.ContentLogBlock;
import mctmods.resourcedatapackloader.common.items.ContentBannerItem;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockVariant;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.GrowthDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.LineCompat;

import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;

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
    public static final String CONTAINER = "container";
    public static final String CROP = "crop";
    public static final String FLOWER = "flower";
    public static final String CANE = "cane";
    public static final String VINE = "vine";
    public static final String BANNER = "banner";
    public static final String PORTAL = "portal";
    public static final String BELL = "bell";
    public static final String PORTWAY = "portway";
    private static final Set<String> KNOWN = Set.of(BASIC, ORE, FALLING, SLAB, STAIRS, FENCE, PANE, WALL, DOOR, TRAPDOOR, FENCE_GATE, LADDER, TORCH, LOG, LEAVES, SAPLING, CROP, FLOWER, CANE, VINE, BANNER, PORTAL, CONTAINER, BELL, PORTWAY);
    private static final Set<String> PLANTS = Set.of(SAPLING, CROP, FLOWER, CANE, VINE);
    private static final Set<String> VARIANT_LEVELS = Set.of(BASIC, ORE, CONTAINER, PORTAL, FENCE, PANE, LOG, FALLING, SLAB, WALL, PORTWAY);

    private ContentBlockTypes() {}

    public static boolean plant(String type) { return PLANTS.contains(type); }

    public static boolean borrowsModel(String type) { return STAIRS.equals(type) || WALL.equals(type); }

    public static int harvestLevel(BlockDef def, BlockVariant variant) { return VARIANT_LEVELS.contains(KNOWN.contains(def.type()) ? def.type() : BASIC) ? variant.harvestLevel() : def.harvestToolLevel(); }

    public static boolean translucent(BlockDef def) { return "translucent".equals(def.renderLayer()); }

    public static List<Created> create(BlockDef def, BlockVariant variant) {
        String type = def.type();
        if (!KNOWN.contains(type)) {
            ContentLog.LOGGER.error("Unknown block type '{}' in {}, treating it as '{}'. Known types are {}", type, def.key(), BASIC, KNOWN);
            type = BASIC;
        }
        Identifier id = variant.id();
        Identifier wallId = ContentTypes.wallId(id);
        BlockBehaviour.Properties properties = ContentTypes.properties(def, variant, type).setId(ResourceKey.create(Registries.BLOCK, id));
        return switch (type) {
            case FALLING -> List.of(new Created(id, new ContentFallingBlock(def, properties), ContentRegistry.MAIN));
            case SLAB -> List.of(new Created(id, new SlabBlock(properties), ContentRegistry.MAIN));
            case STAIRS -> List.of(new Created(id, new StairBlock(base(def), properties), ContentRegistry.MAIN));
            case FENCE -> List.of(new Created(id, new FenceBlock(properties), ContentRegistry.MAIN));
            case PANE -> List.of(new Created(id, new IronBarsBlock(properties.noOcclusion()), ContentRegistry.MAIN));
            case WALL -> List.of(new Created(id, new WallBlock(properties), ContentRegistry.MAIN));
            case DOOR -> List.of(new Created(id, new DoorBlock(setType(def, type), properties.noOcclusion()), ContentRegistry.MAIN));
            case TRAPDOOR -> List.of(new Created(id, new TrapDoorBlock(setType(def, type), properties.noOcclusion()), ContentRegistry.MAIN));
            case FENCE_GATE -> List.of(new Created(id, new FenceGateBlock(properties, SoundEvents.FENCE_GATE_OPEN, SoundEvents.FENCE_GATE_CLOSE), ContentRegistry.MAIN));
            case LADDER -> List.of(new Created(id, new LadderBlock(properties.noOcclusion().noCollision()), ContentRegistry.MAIN));
            case BANNER -> {
                ContentBannerBlock standing = new ContentBannerBlock(id, properties.noCollision().forceSolidOn());
                ContentWallBannerBlock wall = new ContentWallBannerBlock(id, ContentTypes.properties(def, variant, type).setId(ResourceKey.create(Registries.BLOCK, wallId)).noCollision().forceSolidOn());
                yield List.of(new Created(id, standing, ContentRegistry.MAIN), new Created(wallId, wall, ContentRegistry.WALL));
            }
            case TORCH -> {
                ContentTorchBlock torch = new ContentTorchBlock(def, properties.noCollision());
                ContentWallTorchBlock wall = new ContentWallTorchBlock(def, ContentTypes.properties(def, variant, type).setId(ResourceKey.create(Registries.BLOCK, wallId)).noCollision());
                yield List.of(new Created(id, torch, ContentRegistry.MAIN), new Created(wallId, wall, ContentRegistry.WALL));
            }
            case CONTAINER -> List.of(new Created(id, new ContentContainerBlock(def, properties), ContentRegistry.MAIN));
            case BELL -> List.of(new Created(id, new ContentBellBlock(def, properties.noOcclusion()), ContentRegistry.MAIN));
            case PORTWAY -> List.of(new Created(id, ContentPortwayBlock.create(def, properties), ContentRegistry.MAIN));
            case LOG -> List.of(new Created(id, new ContentLogBlock(properties), ContentRegistry.MAIN));
            case LEAVES -> List.of(new Created(id, new ContentLeavesBlock(def, LineCompat.notViewBlocking(properties.randomTicks().isSuffocating((_, _, _) -> false))), ContentRegistry.MAIN));
            case SAPLING -> {
                if (def.sapling() == null) {
                    ContentLog.LOGGER.error("Block {} is a sapling but has no 'sapling' section, so there is nothing for it to grow into", variant.id());
                    yield List.of();
                }
                yield List.of(new Created(id, ContentSaplingBlock.create(def, variant.id(), properties), ContentRegistry.MAIN));
            }
            case CROP -> List.of(new Created(id, new ContentCropBlock(def, properties), ContentRegistry.MAIN));
            case FLOWER -> List.of(new Created(id, new ContentBushBlock(def, GrowthDef.orBush(def), properties), ContentRegistry.MAIN));
            case CANE -> {
                if (def.growth() == null) {
                    ContentLog.LOGGER.error("Block {} is a cane but has no 'growth' section, so it has no height to grow to", variant.id());
                    yield List.of();
                }
                yield List.of(new Created(id, new ContentCaneBlock(def, def.growth(), properties), ContentRegistry.MAIN));
            }
            case VINE -> List.of(new Created(id, new ContentVineBlock(GrowthDef.orBush(def), properties.noOcclusion()), ContentRegistry.MAIN));
            case PORTAL -> {
                if (def.portal() == null) {
                    ContentLog.LOGGER.error("Block {} is a portal but has no 'portal' section, so it has nowhere to lead", variant.id());
                    yield List.of();
                }
                yield List.of(new Created(id, new ContentPortalBlock(def, variant.portal() == null ? def.portal() : variant.portal(), properties), ContentRegistry.MAIN));
            }
            default -> List.of(new Created(id, new ContentBlock(def, properties), ContentRegistry.MAIN));
        };
    }

    @Nullable public static Item item(ContentRegistry.BlockEntry entry) {
        BlockDef def = entry.def();
        BlockVariant variant = entry.variant();
        Item.Properties properties = new Item.Properties().setId(ResourceKey.create(Registries.ITEM, entry.id())).useBlockDescriptionPrefix().stacksTo(variant.maxSize()).rarity(ContentTypes.rarity(variant.rarity(), variant.id()));
        return switch (def.type()) {
            case CROP -> null;
            case TORCH -> {
                if (!entry.isMain()) { yield null; }
                ContentRegistry.BlockEntry wall = ContentRegistry.block(ContentTypes.wallId(entry.id()));
                yield wall == null ? new BlockItem(entry.block(), properties) : new StandingAndWallBlockItem(entry.block(), wall.block(), Direction.DOWN, properties);
            }
            case BANNER -> {
                if (!entry.isMain()) { yield null; }
                ContentRegistry.BlockEntry wall = ContentRegistry.block(ContentTypes.wallId(entry.id()));
                yield wall == null ? new BlockItem(entry.block(), properties.stacksTo(16)) : new ContentBannerItem(entry.block(), wall.block(), properties.stacksTo(16));
            }
            case DOOR -> new DoubleHighBlockItem(entry.block(), properties);
            default -> new BlockItem(entry.block(), properties);
        };
    }

    private static BlockSetType setType(BlockDef def, String type) {
        BlockSetType sounds = "iron".equals(def.material()) ? BlockSetType.IRON : BlockSetType.OAK;
        return new BlockSetType(def.key().toString(), sounds.canOpenByHand(), sounds.canOpenByWindCharge(), sounds.canButtonBeActivatedByArrows(), sounds.pressurePlateSensitivity(), ContentTypes.sound(def, type), sounds.doorClose(), sounds.doorOpen(), sounds.trapdoorClose(), sounds.trapdoorOpen(), sounds.pressurePlateClickOff(), sounds.pressurePlateClickOn(), sounds.buttonClickOff(), sounds.buttonClickOn());
    }

    static BlockState base(BlockDef def) {
        Block block = ContentStates.registered(def.modelBlock());
        return block == null || block == Blocks.AIR ? Blocks.STONE.defaultBlockState() : block.defaultBlockState();
    }

    public record Created(Identifier id, Block block, String role) {}
}
