package mctmods.resourcedatapackloader.content.types;

import mctmods.resourcedatapackloader.content.ContentSetup;
import mctmods.resourcedatapackloader.content.block.ContentBlock;
import mctmods.resourcedatapackloader.content.block.ContentBlockBanner;
import mctmods.resourcedatapackloader.content.block.ContentBlockBannerWall;
import mctmods.resourcedatapackloader.content.block.ContentBlockCane;
import mctmods.resourcedatapackloader.content.block.ContentBlockCrop;
import mctmods.resourcedatapackloader.content.block.ContentBlockDoor;
import mctmods.resourcedatapackloader.content.block.ContentBlockFalling;
import mctmods.resourcedatapackloader.content.block.ContentBlockFence;
import mctmods.resourcedatapackloader.content.block.ContentBlockFenceGate;
import mctmods.resourcedatapackloader.content.block.ContentBlockFlower;
import mctmods.resourcedatapackloader.content.block.ContentBlockLadder;
import mctmods.resourcedatapackloader.content.block.ContentBlockLeaves;
import mctmods.resourcedatapackloader.content.block.ContentBlockLog;
import mctmods.resourcedatapackloader.content.block.ContentBlockPane;
import mctmods.resourcedatapackloader.content.block.ContentBlockPortal;
import mctmods.resourcedatapackloader.content.block.ContentBlockSapling;
import mctmods.resourcedatapackloader.content.block.ContentBlockSlab;
import mctmods.resourcedatapackloader.content.block.ContentBlockStairs;
import mctmods.resourcedatapackloader.content.block.ContentBlockTorch;
import mctmods.resourcedatapackloader.content.block.ContentBlockTrapDoor;
import mctmods.resourcedatapackloader.content.block.ContentBlockVine;
import mctmods.resourcedatapackloader.content.block.ContentBlockWall;
import mctmods.resourcedatapackloader.content.def.BlockDef;
import mctmods.resourcedatapackloader.content.def.GrowthDef;
import mctmods.resourcedatapackloader.content.interfaces.IBlockType;
import mctmods.resourcedatapackloader.util.ContentLog;

import net.minecraft.block.Block;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public final class ContentBlockTypes {
    public static final String DEFAULT = "basic";
    private static final Map<String, IBlockType> TYPES = new LinkedHashMap<>();

    private ContentBlockTypes() {}

    static {
        IBlockType plain = def -> Collections.singletonList(ContentBlock.create(def));
        register(DEFAULT, plain);
        register("ore", plain);
        register("falling", def -> Collections.singletonList(ContentBlockFalling.create(def)));
        register("slab", new IBlockType() {
            @Override public List<Block> create(BlockDef def) {
                PropertyVariant property = new PropertyVariant(ContentSetup.names(def));
                ContentBlockSlab single = ContentBlockSlab.create(def, false, property);
                ContentBlockSlab twin = ContentBlockSlab.create(def, true, property);
                single.pair(twin);
                twin.pair(single);
                return Arrays.asList(single, twin);
            }

            @Override public int maxVariants() { return ContentBlockSlab.MAX_VARIANTS; }
        });
        register("fence", def -> Collections.singletonList(ContentBlockFence.create(def)));
        register("pane", def -> Collections.singletonList(ContentBlockPane.create(def)));
        register("wall", def -> Collections.singletonList(ContentBlockWall.create(def)));
        register("ladder", new IBlockType() {
            @Override public List<Block> create(BlockDef def) { return Collections.singletonList(new ContentBlockLadder(def)); }

            @Override public int maxVariants() { return ContentBlockLadder.MAX_VARIANTS; }
        });
        register("torch", new IBlockType() {
            @Override public List<Block> create(BlockDef def) { return Collections.singletonList(new ContentBlockTorch(def)); }

            @Override public int maxVariants() { return ContentBlockTorch.MAX_VARIANTS; }
        });
        register("portal", new IBlockType() {
            @Override public List<Block> create(BlockDef def) {
                ContentBlockPortal block = ContentBlockPortal.create(def);
                return block == null ? Collections.emptyList() : Collections.singletonList(block);
            }

            @Override public int maxVariants() { return ContentBlockPortal.MAX_VARIANTS; }
        });
        register("flower", new IBlockType() {
            @Override public List<Block> create(BlockDef def) {
                GrowthDef growth = def.growth == null ? GrowthDef.bush() : def.growth;
                return Collections.singletonList(ContentBlockFlower.create(def, growth));
            }

            @Override public int maxVariants() { return ContentBlockFlower.MAX_VARIANTS; }
        });
        register("vine", new IBlockType() {
            @Override public List<Block> create(BlockDef def) {
                GrowthDef growth = def.growth == null ? GrowthDef.bush() : def.growth;
                return Collections.singletonList(new ContentBlockVine(def, growth));
            }

            @Override public int maxVariants() { return ContentBlockVine.MAX_VARIANTS; }
        });
        register("cane", new IBlockType() {
            @Override public List<Block> create(BlockDef def) {
                if (def.growth == null) {
                    ContentLog.LOGGER.error("Block {} is a cane but has no 'growth' section, so it has no height to grow to", def.registryName);
                    return Collections.emptyList();
                }
                return Collections.singletonList(new ContentBlockCane(def, def.growth));
            }

            @Override public int maxVariants() { return ContentBlockCane.MAX_VARIANTS; }
        });
        register("crop", new IBlockType() {
            @Override public List<Block> create(BlockDef def) { return Collections.singletonList(new ContentBlockCrop(def)); }

            @Override public int maxVariants() { return ContentBlockCrop.MAX_VARIANTS; }
        });
        register("log", new IBlockType() {
            @Override public List<Block> create(BlockDef def) { return Collections.singletonList(ContentBlockLog.create(def)); }

            @Override public int maxVariants() { return ContentBlockLog.MAX_VARIANTS; }
        });
        register("leaves", new IBlockType() {
            @Override public List<Block> create(BlockDef def) { return Collections.singletonList(ContentBlockLeaves.create(def)); }

            @Override public int maxVariants() { return ContentBlockLeaves.MAX_VARIANTS; }
        });
        register("sapling", new IBlockType() {
            @Override public List<Block> create(BlockDef def) {
                if (def.sapling == null) {
                    ContentLog.LOGGER.error("Block {} is a sapling but has no 'sapling' section, so there is nothing for it to grow into", def.registryName);
                    return Collections.emptyList();
                }
                return Collections.singletonList(ContentBlockSapling.create(def, def.sapling));
            }

            @Override public int maxVariants() { return ContentBlockSapling.MAX_VARIANTS; }
        });
        register("door", new IBlockType() {
            @Override public List<Block> create(BlockDef def) { return Collections.singletonList(new ContentBlockDoor(def)); }

            @Override public int maxVariants() { return ContentBlockDoor.MAX_VARIANTS; }
        });
        register("banner", new IBlockType() {
            @Override public List<Block> create(BlockDef def) {
                ContentBlockBanner standing = new ContentBlockBanner(def);
                ContentBlockBannerWall wall = new ContentBlockBannerWall(def);
                standing.pair(wall);
                wall.pair(standing);
                return Arrays.asList(standing, wall);
            }

            @Override public int maxVariants() { return ContentBlockBanner.MAX_VARIANTS; }
        });
        register("trapdoor", new IBlockType() {
            @Override public List<Block> create(BlockDef def) { return Collections.singletonList(new ContentBlockTrapDoor(def)); }

            @Override public int maxVariants() { return ContentBlockTrapDoor.MAX_VARIANTS; }
        });
        register("fence_gate", new IBlockType() {
            @Override public List<Block> create(BlockDef def) { return Collections.singletonList(new ContentBlockFenceGate(def)); }

            @Override public int maxVariants() { return ContentBlockFenceGate.MAX_VARIANTS; }
        });
        register("stairs", new IBlockType() {
            @Override public List<Block> create(BlockDef def) { return Collections.singletonList(ContentBlockStairs.create(def)); }

            @Override public int maxVariants() { return ContentBlockStairs.MAX_VARIANTS; }
        });
    }

    public static void register(String name, IBlockType type) {
        IBlockType previous = TYPES.put(name.toLowerCase(Locale.ROOT), type);
        if (previous != null) { ContentLog.LOGGER.warn("Block type '{}' was registered twice, the later one wins", name); }
    }

    @Nullable public static IBlockType find(String name) { return TYPES.get(name == null ? "" : name.toLowerCase(Locale.ROOT)); }

    public static IBlockType get(String name, Object context) {
        IBlockType type = find(name);
        if (type != null) { return type; }

        ContentLog.LOGGER.error("Unknown block type '{}' in {}, treating it as '{}'. Known types are {}", name, context, DEFAULT, names());
        return TYPES.get(DEFAULT);
    }

    public static Set<String> names() { return Collections.unmodifiableSet(TYPES.keySet()); }
}
