package mctmods.resourcedatapackloader.common.types;

import mctmods.resourcedatapackloader.common.blocks.ContentBlock;
import mctmods.resourcedatapackloader.common.blocks.ContentFallingBlock;
import mctmods.resourcedatapackloader.common.blocks.banner.ContentBannerBlock;
import mctmods.resourcedatapackloader.common.blocks.banner.ContentWallBannerBlock;
import mctmods.resourcedatapackloader.common.blocks.bell.ContentBellBlock;
import mctmods.resourcedatapackloader.common.blocks.sensor.ContentSeasonSensorBlock;
import mctmods.resourcedatapackloader.common.blocks.container.ContentContainerBlock;
import mctmods.resourcedatapackloader.common.blocks.light.ContentTorchBlock;
import mctmods.resourcedatapackloader.common.blocks.plant.ContentCaneBlock;
import mctmods.resourcedatapackloader.common.blocks.plant.ContentCropBlock;
import mctmods.resourcedatapackloader.common.blocks.plant.ContentFlowerBlock;
import mctmods.resourcedatapackloader.common.blocks.plant.ContentPlantPartBlock;
import mctmods.resourcedatapackloader.common.blocks.plant.ContentSaplingBlock;
import mctmods.resourcedatapackloader.common.blocks.plant.ContentVineBlock;
import mctmods.resourcedatapackloader.common.blocks.portal.ContentPortalBlock;
import mctmods.resourcedatapackloader.common.blocks.portal.ContentPortwayBlock;
import mctmods.resourcedatapackloader.common.blocks.shape.ContentCarpetBlock;
import mctmods.resourcedatapackloader.common.blocks.shape.ContentDoorBlock;
import mctmods.resourcedatapackloader.common.blocks.shape.ContentFenceBlock;
import mctmods.resourcedatapackloader.common.blocks.shape.ContentFenceGateBlock;
import mctmods.resourcedatapackloader.common.blocks.shape.ContentLadderBlock;
import mctmods.resourcedatapackloader.common.blocks.shape.ContentPaneBlock;
import mctmods.resourcedatapackloader.common.blocks.shape.ContentSlabBlock;
import mctmods.resourcedatapackloader.common.blocks.shape.ContentStairsBlock;
import mctmods.resourcedatapackloader.common.blocks.shape.ContentTrapDoorBlock;
import mctmods.resourcedatapackloader.common.blocks.shape.ContentWallBlock;
import mctmods.resourcedatapackloader.common.blocks.wood.ContentLeavesBlock;
import mctmods.resourcedatapackloader.common.blocks.wood.ContentLogBlock;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.GrowthDef;
import mctmods.resourcedatapackloader.common.util.NamedRegistry;
import mctmods.resourcedatapackloader.core.util.ContentLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.util.BlockRenderLayer;

public final class ContentBlockTypes {
    public static final String DEFAULT = "basic";
    private static final String PILLAR = "pillar";
    private static final String LEAVES = "leaves";
    private static final String PORTAL = "portal";
    private static final NamedRegistry<IBlockType> TYPES = new NamedRegistry<>("Block", DEFAULT);

    private ContentBlockTypes() {}

    static {
        IBlockType plain = def -> Collections.singletonList(ContentBlock.create(def));
        register(DEFAULT, plain);
        register("ore", plain);
        register("falling", def -> Collections.singletonList(ContentFallingBlock.create(def)));
        register("slab", new IBlockType() {
            @Override public List<Block> create(BlockDef def) {
                ContentSlabBlock single = ContentSlabBlock.create(def, false);
                ContentSlabBlock twin = ContentSlabBlock.create(def, true);
                single.pair(twin);
                twin.pair(single);
                return Arrays.asList(single, twin);
            }
            @Override public int maxVariants() { return ContentSlabBlock.MAX_VARIANTS; }
        });
        register("container", single(ContentContainerBlock::create, ContentContainerBlock.MAX_VARIANTS));
        register("fence", def -> Collections.singletonList(ContentFenceBlock.create(def)));
        register("pane", def -> Collections.singletonList(ContentPaneBlock.create(def)));
        register("wall", def -> Collections.singletonList(ContentWallBlock.create(def)));
        register("ladder", single(ContentLadderBlock::new, ContentLadderBlock.MAX_VARIANTS));
        register("torch", single(ContentTorchBlock::new, ContentTorchBlock.MAX_VARIANTS));
        register(PORTAL, new IBlockType() {
            @Override public List<Block> create(BlockDef def) {
                ContentPortalBlock block = ContentPortalBlock.create(def);
                return block == null ? Collections.emptyList() : Collections.singletonList(block);
            }
            @Override public int maxVariants() { return ContentPortalBlock.MAX_VARIANTS; }
        });
        register("flower", new IBlockType() {
            @Override public List<Block> create(BlockDef def) {
                return plantParts(def, ContentFlowerBlock.create(def, GrowthDef.orBush(def)), part -> ContentPlantPartBlock.flower(def, def.registryName, part));
            }
            @Override public int maxVariants() { return ContentFlowerBlock.MAX_VARIANTS; }
        });
        register("vine", single(def -> new ContentVineBlock(def, GrowthDef.orBush(def)), ContentVineBlock.MAX_VARIANTS));
        register("cane", new IBlockType() {
            @Override public List<Block> create(BlockDef def) {
                if (def.growth == null) {
                    ContentLog.LOGGER.error("Block {} is a cane but has no 'growth' section, so it has no height to grow to", def.registryName);
                    return Collections.emptyList();
                }
                return Collections.singletonList(new ContentCaneBlock(def, def.growth));
            }
            @Override public int maxVariants() { return ContentCaneBlock.MAX_VARIANTS; }
        });
        register("crop", new IBlockType() {
            @Override public List<Block> create(BlockDef def) {
                return plantParts(def, new ContentCropBlock(def), part -> new ContentPlantPartBlock(def, def.registryName, part));
            }
            @Override public int maxVariants() { return ContentCropBlock.MAX_VARIANTS; }
        });
        IBlockType log = single(ContentLogBlock::create, ContentLogBlock.MAX_VARIANTS);
        register("log", log);
        register(PILLAR, log);
        register("carpet", def -> Collections.singletonList(ContentCarpetBlock.create(def)));
        register(LEAVES, single(ContentLeavesBlock::create, ContentLeavesBlock.MAX_VARIANTS));
        register("sapling", new IBlockType() {
            @Override public List<Block> create(BlockDef def) {
                if (def.sapling == null) {
                    ContentLog.LOGGER.error("Block {} is a sapling but has no 'sapling' section, so there is nothing for it to grow into", def.registryName);
                    return Collections.emptyList();
                }
                return Collections.singletonList(ContentSaplingBlock.create(def, def.sapling));
            }
            @Override public int maxVariants() { return ContentSaplingBlock.MAX_VARIANTS; }
        });
        register("door", single(ContentDoorBlock::new, ContentDoorBlock.MAX_VARIANTS));
        register("banner", new IBlockType() {
            @Override public List<Block> create(BlockDef def) {
                ContentBannerBlock standing = new ContentBannerBlock(def);
                ContentWallBannerBlock wall = new ContentWallBannerBlock(def);
                standing.pair(wall);
                wall.pair(standing);
                return Arrays.asList(standing, wall);
            }
            @Override public int maxVariants() { return ContentBannerBlock.MAX_VARIANTS; }
        });
        register("trapdoor", single(ContentTrapDoorBlock::new, ContentTrapDoorBlock.MAX_VARIANTS));
        register("fence_gate", single(ContentFenceGateBlock::new, ContentFenceGateBlock.MAX_VARIANTS));
        register("bell", single(ContentBellBlock::new, ContentBellBlock.MAX_VARIANTS));
        register("seasonSensor", single(ContentSeasonSensorBlock::new, ContentSeasonSensorBlock.MAX_VARIANTS));
        register("portway", single(ContentPortwayBlock::create, ContentPortwayBlock.MAX_VARIANTS));
        register("stairs", single(ContentStairsBlock::create, ContentStairsBlock.MAX_VARIANTS));
    }

    private static List<Block> plantParts(BlockDef def, Block main, IntFunction<Block> part) {
        List<Block> blocks = new ArrayList<>();
        blocks.add(main);
        if (def.tall != null) {
            for (int index = 1; index < def.tall.parts; index++) { blocks.add(part.apply(index)); }
        }
        return blocks;
    }

    private static IBlockType single(Function<BlockDef, Block> make, int maxVariants) {
        return new IBlockType() {
            @Override public List<Block> create(BlockDef def) { return Collections.singletonList(make.apply(def)); }
            @Override public int maxVariants() { return maxVariants; }
        };
    }

    @Nullable public static String logTag(BlockDef def) { return def.tagged && !PILLAR.equals(def.type) ? "logWood" : null; }

    public static boolean plant(String type) {
        switch (type) {
            case "sapling": case "flower": case "crop": case "cane": case "vine": return true;
            default: return false;
        }
    }

    public static BlockRenderLayer defaultLayer(String type, boolean opaque) {
        switch (type) {
            case LEAVES: case "pane": return BlockRenderLayer.CUTOUT_MIPPED;
            case "sapling": case "flower": case "cane": case "vine": case "ladder": case "torch": case "door": case "trapdoor": case "bell": return BlockRenderLayer.CUTOUT;
            case PORTAL: return BlockRenderLayer.TRANSLUCENT;
            default: return opaque ? BlockRenderLayer.SOLID : BlockRenderLayer.CUTOUT;
        }
    }

    public static void register(String name, IBlockType type) { TYPES.register(name, type); }

    @Nullable public static IBlockType find(String name) { return TYPES.find(name); }

    public static IBlockType get(String name, Object context) { return TYPES.get(name, context); }

    public static Set<String> names() { return TYPES.names(); }
}
