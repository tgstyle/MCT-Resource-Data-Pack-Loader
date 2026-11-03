package mctmods.resourcedatapackloader.common.blocks.compress;

import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockDef;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.BlockVariant;
import mctmods.resourcedatapackloader.common.pack.definitions.blocks.CompressableDef;
import mctmods.resourcedatapackloader.common.pack.loading.ContentRegistry;
import mctmods.resourcedatapackloader.core.util.ContentLog;
import mctmods.resourcedatapackloader.core.util.concurrent.Registered;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public final class CompressedSource {
    private static final Map<MapColor, String> MATERIALS = Map.ofEntries(Map.entry(MapColor.WOOD, "wood"), Map.entry(MapColor.GRASS, "grass"), Map.entry(MapColor.DIRT, "ground"), Map.entry(MapColor.STONE, "rock"), Map.entry(MapColor.METAL, "iron"), Map.entry(MapColor.SAND, "sand"), Map.entry(MapColor.SNOW, "snow"), Map.entry(MapColor.ICE, "ice"), Map.entry(MapColor.CLAY, "clay"), Map.entry(MapColor.PLANT, "plants"), Map.entry(MapColor.WOOL, "cloth"));
    private static final Map<SoundType, String> SOUNDS = Map.ofEntries(Map.entry(SoundType.WOOD, "wood"), Map.entry(SoundType.GRAVEL, "ground"), Map.entry(SoundType.GRASS, "plant"), Map.entry(SoundType.STONE, "stone"), Map.entry(SoundType.METAL, "metal"), Map.entry(SoundType.GLASS, "glass"), Map.entry(SoundType.WOOL, "cloth"), Map.entry(SoundType.SAND, "sand"), Map.entry(SoundType.SNOW, "snow"), Map.entry(SoundType.LADDER, "ladder"), Map.entry(SoundType.ANVIL, "anvil"), Map.entry(SoundType.SLIME_BLOCK, "slime"));
    public final Identifier baseBlock;
    public final String material;
    public final String mapColor;
    public final String sound;
    public final String tool;
    public final int toolLevel;
    public final int harvestLevel;
    public final float hardness;
    public final float resistance;
    public final List<String> requires;

    private CompressedSource(Identifier baseBlock, String material, String mapColor, String sound, String tool, int toolLevel, int harvestLevel, float hardness, float resistance, List<String> requires) {
        this.baseBlock = baseBlock;
        this.material = material;
        this.mapColor = mapColor;
        this.sound = sound;
        this.tool = tool;
        this.toolLevel = toolLevel;
        this.harvestLevel = harvestLevel;
        this.hardness = hardness;
        this.resistance = resistance;
        this.requires = requires;
    }

    @Nullable static CompressedSource of(CompressableDef def, Map<Identifier, BlockDef> defs) { return def.of() == null ? pack(def, defs) : foreign(def, def.of()); }

    @Nullable private static CompressedSource pack(CompressableDef def, Map<Identifier, BlockDef> defs) {
        BlockDef base = defs.get(def.key());
        if (base == null) {
            ContentLog.LOGGER.error("The compressable block {} did not load, so it gets no compressed layers", def.key());
            return null;
        }
        if (base.variants().isEmpty()) {
            ContentLog.LOGGER.error("The compressable block {} has no variants, so it gets no compressed layers", def.key());
            return null;
        }
        BlockVariant first = base.variants().getFirst();
        return new CompressedSource(first.id(), base.material(), base.mapColor(), base.soundType(), base.harvestTool(), base.harvestToolLevel(), first.harvestLevel(), first.hardness(), first.resistance() / base.explosionResistanceDivisor(), base.requires());
    }

    @SuppressWarnings("deprecation") @Nullable private static CompressedSource foreign(CompressableDef def, Identifier name) {
        String namespace = name.getNamespace();
        List<String> requires = "minecraft".equals(namespace) ? Collections.emptyList() : Collections.singletonList(namespace);
        if (!ContentRegistry.holds(requires, def.key())) { return null; }
        Block block = Registered.find(BuiltInRegistries.BLOCK, name);
        if (block == null) {
            ContentLog.LOGGER.error("The compressable file {} names block {}, which is not registered, so it gets no compressed layers", def.key(), name);
            return null;
        }
        BlockState state = block.defaultBlockState();
        int level = level(state);
        return new CompressedSource(name, MATERIALS.getOrDefault(state.getMapColor(EmptyBlockGetter.INSTANCE, BlockPos.ZERO), "rock"), "", SOUNDS.getOrDefault(state.getSoundType(), ""), tool(state), level, level, state.getDestroySpeed(EmptyBlockGetter.INSTANCE, BlockPos.ZERO), block.getExplosionResistance(), requires);
    }

    private static String tool(BlockState state) {
        if (state.is(BlockTags.MINEABLE_WITH_PICKAXE)) { return "pickaxe"; }
        if (state.is(BlockTags.MINEABLE_WITH_AXE)) { return "axe"; }
        if (state.is(BlockTags.MINEABLE_WITH_SHOVEL)) { return "shovel"; }
        if (state.is(BlockTags.MINEABLE_WITH_HOE)) { return "hoe"; }
        return "";
    }

    private static int level(BlockState state) {
        if (state.is(BlockTags.NEEDS_DIAMOND_TOOL)) { return 3; }
        if (state.is(BlockTags.NEEDS_IRON_TOOL)) { return 2; }
        if (state.is(BlockTags.NEEDS_STONE_TOOL)) { return 1; }
        return 0;
    }
}
